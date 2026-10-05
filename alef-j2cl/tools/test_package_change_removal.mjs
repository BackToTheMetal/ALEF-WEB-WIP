import assert from 'node:assert/strict';
import path from 'node:path';

// Isolated package state: exercise the real interface without changing live packages.
export async function testPackageChangeRemoval(page,output){
  await page.evaluate(()=>{
    const p=fixtureData['/api/v1/packages/p1'];
    window.removalBackup={package:structuredClone(p),contents:structuredClone(fixtureData['/api/v1/packages/p1/contents'])};
    p.revision=7;p.canEdit=true;p.finalized=false;
    const data=fixtureData['/api/v1/packages/p1/contents'];
    data.roots[0].modelName='Model';data.roots[1].modelName='Model';
    data.roots.push(
      {rootId:'r3',name:'Deleted file',modelName:'Model',kind:'Deleted',changed:true,includedChange:true,deleted:true,before:{id:'n3',properties:{name:'Deleted file'},children:[]},after:null,beforeReference:'m/n3'},
      {rootId:'r4',name:'Added file',modelName:'Model',kind:'Added',changed:true,includedChange:true,before:null,after:{id:'n4',properties:{name:'Added file'},children:[]},afterReference:'m/n4'}
    );
    window.fixtureApi=async(url,options)=>{
      if(url.startsWith('/api/v1/packages/p1/contents')){
        if(window.removalLoadError)throw Error('Package temporarily unavailable');
        const result=structuredClone(data);result.revision=p.revision;
        // Against another package, an unchanged baseline root can look changed.
        if(url.includes('?against=')){result.roots[1].changed=true;result.roots[1].kind='Modified';}
        return result;
      }
      if(url==='/api/v1/packages/p1/changes'&&options.method==='POST'){
        const body=JSON.parse(options.body);
        if(body.expectedRevision!==p.revision)throw Object.assign(Error('Package changed; refresh before removing changes'),{status:409});
        for(const id of body.removeRootIds){
          const root=data.roots.find(r=>r.rootId===id);if(!root?.includedChange)throw Error('Not an included change');
          if(root.before){root.after=structuredClone(root.before);root.afterReference=root.beforeReference;root.includedChange=false;root.changed=false;root.deleted=false;root.kind='Unchanged';}
          else data.roots=data.roots.filter(r=>r!==root);
        }
        p.revision++;return p;
      }
      if(url.startsWith('/api/v1/packages/options?packageId=p1'))return {packageRevision:p.revision,baselineCount:3,changes:removalBackup.contents.roots.filter(r=>r.includedChange&&!data.roots.find(n=>n.rootId===r.rootId)?.includedChange).map(r=>({...r,version:'v1',tags:[],current:true}))};
    };
  });
  const reopen=async()=>{
    await page.locator('.primary-nav').getByRole('button',{name:'Packaging',exact:true}).click();
    await page.getByRole('table').getByRole('button',{name:'Open',exact:true}).first().click();
    await page.getByRole('tab',{name:'Contents',exact:true}).waitFor();
  };
  const remove=()=>page.getByRole('button',{name:'Remove changes…',exact:true});
  const submit=()=>page.getByRole('button',{name:'Remove selected changes',exact:true});
  const mutations=()=>page.evaluate(()=>calls.filter(c=>c[0].endsWith('/changes')&&c[1]==='POST'));
  try{
    await reopen();await page.getByRole('combobox',{name:'Against',exact:true}).selectOption('base1');
    await page.locator('.comparison-tree [data-kind=root]').filter({hasText:'Name model'}).click();
    await page.getByRole('button',{name:'Remove change',exact:true}).waitFor();
    const retained=await page.locator('mps-comparison-session').elementHandle(),before=(await mutations()).length;
    await remove().click();await page.locator('.package-picker label').first().waitFor();
    assert.deepEqual(await page.locator('.package-picker .item-label > span').allTextContents(),['Added file','Deleted file','Name model']);
    assert.equal(await submit().isDisabled(),true);assert.equal(await page.getByRole('button',{name:'Clear',exact:true}).isDisabled(),true);
    await page.getByRole('searchbox',{name:'Filter included changes'}).fill('Deleted');
    await page.getByRole('button',{name:'Select filtered',exact:true}).click();
    assert.equal(await submit().isEnabled(),true);await page.getByRole('button',{name:'Clear',exact:true}).click();assert.equal(await submit().isDisabled(),true);
    await page.getByRole('button',{name:'Cancel',exact:true}).click();
    assert.equal((await mutations()).length,before);assert.equal(await retained.evaluate(el=>el.isConnected),true);
    // Per-root removal preselects only that file; it uses the same explicit confirmation.
    await page.getByRole('button',{name:'Remove change',exact:true}).click();
    assert.equal(await page.getByRole('checkbox',{name:/Name model/}).isChecked(),true);
    assert.equal(await page.locator('.package-picker input:checked').count(),1);
    await page.getByRole('button',{name:'Cancel',exact:true}).click();
    // Load failures cannot enable a submission or remove anything.
    await page.evaluate(()=>window.removalLoadError=true);await remove().click();
    await page.getByRole('dialog').getByRole('alert').getByText('Package temporarily unavailable',{exact:true}).waitFor();assert.equal(await submit().isDisabled(),true);
    await page.getByRole('button',{name:'Cancel',exact:true}).click();await page.evaluate(()=>window.removalLoadError=false);
    // Fetch the latest revision when opening; keep the selected revision fixed during confirmation.
    await page.evaluate(()=>fixtureData['/api/v1/packages/p1'].revision=8);
    await remove().click();await page.locator('.package-picker label').first().waitFor();
    await page.getByRole('button',{name:'Select filtered',exact:true}).click();
    await page.evaluate(()=>fixtureData['/api/v1/packages/p1'].revision=9);
    await submit().click();await page.getByRole('dialog').getByRole('alert').getByText('Package changed; refresh before removing changes',{exact:true}).waitFor();
    assert.equal(await page.locator('.package-picker input:checked').count(),3);assert.equal(await retained.evaluate(el=>el.isConnected),true);
    assert.equal(await page.evaluate(()=>fixtureData['/api/v1/packages/p1/contents'].roots.filter(r=>r.includedChange).length),3);
    await page.getByRole('button',{name:'Cancel',exact:true}).click();
    await remove().click();await page.locator('.package-picker label').first().waitFor();
    await page.getByRole('searchbox',{name:'Filter included changes'}).fill('file');await page.getByRole('button',{name:'Select filtered',exact:true}).click();
    await page.getByRole('searchbox',{name:'Filter included changes'}).fill('Name');await page.getByRole('checkbox',{name:/Name model/}).check();
    await page.getByRole('searchbox',{name:'Filter included changes'}).fill('');
    await page.screenshot({path:path.join(output,'remove-package-changes.png')});
    await submit().click();await page.getByRole('dialog').waitFor({state:'detached'});await remove().waitFor();
    const writes=(await mutations()).slice(before);assert.equal(writes.length,2);
    assert.deepEqual(writes.map(c=>JSON.parse(c[2]).expectedRevision),[8,9]);
    assert.deepEqual(JSON.parse(writes[1][2]),{expectedRevision:9,versions:[],removeRootIds:['r4','r3','r1']});
    assert.ok(writes.every(c=>c[3]['Idempotency-Key']));assert.notEqual(writes[0][3]['Idempotency-Key'],writes[1][3]['Idempotency-Key']);
    const rows=await page.evaluate(()=>fixtureData['/api/v1/packages/p1/contents'].roots);
    assert.equal(rows.find(r=>r.rootId==='r1').after.properties.name,'Name');assert.equal(rows.find(r=>r.rootId==='r3').deleted,false);assert.equal(rows.some(r=>r.rootId==='r4'),false);
    assert.equal(await page.getByRole('button',{name:'Remove change',exact:true}).count(),0);
    await remove().click();await page.getByText('No changes have been added to this package.',{exact:true}).waitFor();assert.equal(await submit().isDisabled(),true);
    await page.getByRole('button',{name:'Cancel',exact:true}).click();
    await page.getByRole('button',{name:'Add changes…',exact:true}).click();await page.getByRole('checkbox',{name:/Name model/}).waitFor();await page.getByRole('button',{name:'Cancel',exact:true}).click();
    for(const state of [{canEdit:false,finalized:false},{canEdit:true,finalized:true}]){
      await page.evaluate(state=>Object.assign(fixtureData['/api/v1/packages/p1'],state),state);await reopen();assert.equal(await remove().count(),0);assert.equal(await page.getByRole('button',{name:'Remove change',exact:true}).count(),0);
    }
  }finally{
    await page.evaluate(()=>{Object.assign(fixtureData['/api/v1/packages/p1'],removalBackup.package);fixtureData['/api/v1/packages/p1/contents']=removalBackup.contents;delete window.fixtureApi;delete window.removalLoadError;delete window.removalBackup;});
    await reopen();
  }
  console.log('PASS: bulk removal, default-baseline selection, packaged deletion, per-file confirmation, cancel, failed load, stale revision, re-add, and read-only packages.');
}
