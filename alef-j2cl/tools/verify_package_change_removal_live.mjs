import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import {connect,origin,root} from './workspace-example-api.mjs';

// Inspect the served controls and cancel the dialog; never submit a live removal.
const api=await connect(),output=path.join(root,'out/package-change-removal');
fs.mkdirSync(output,{recursive:true});
const writes=[],errors=[];
api.page.on('request',r=>{if(r.method()!=='GET'&&/\/api\/v1\/packages\//.test(r.url()))writes.push(r.url());});
api.page.on('pageerror',e=>errors.push(String(e)));
try{
  const packages=await api.request('GET','/api/v1/packages'),draft=packages.find(p=>p.canEdit&&!p.finalized),finalized=packages.find(p=>p.finalized);
  assert.ok(draft,'An editable package is needed for read-only verification');
  const contents=await api.request('GET',`/api/v1/packages/${draft.id}/contents`),included=contents.roots.filter(r=>r.includedChange);
  await api.page.goto(`${origin}/#release/${draft.id}`);
  const remove=api.page.getByRole('button',{name:'Remove changes…',exact:true});await remove.waitFor({timeout:90000});
  for(const width of [1242,760]){
    await api.page.setViewportSize({width,height:900});
    await api.page.screenshot({path:path.join(output,`package-actions-${width}.png`)});
    assert.ok(await api.page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth));
  }
  await api.page.setViewportSize({width:1242,height:900});await remove.click();
  await api.page.locator('.package-picker label').first().waitFor();
  assert.equal(await api.page.getByRole('dialog').getByRole('checkbox').count(),included.length);
  for(const item of included)assert.equal(await api.page.getByRole('dialog').getByText(item.name,{exact:true}).count(),1);
  assert.equal(await api.page.getByRole('button',{name:'Remove selected changes',exact:true}).isDisabled(),true);
  await api.page.getByRole('dialog').getByRole('checkbox').first().check();
  assert.equal(await api.page.getByRole('button',{name:'Remove selected changes',exact:true}).isEnabled(),true);
  await api.page.screenshot({path:path.join(output,'live-remove-changes.png')});
  await api.page.getByRole('button',{name:'Cancel',exact:true}).click();
  if(finalized){
    await api.page.goto(`${origin}/#release/${finalized.id}`);await api.page.getByRole('tab',{name:'Contents',exact:true}).waitFor({timeout:90000});
    assert.equal(await api.page.getByRole('button',{name:/^Remove changes?…?$/}).count(),0);
  }
  assert.deepEqual(writes,[]);assert.deepEqual(errors,[]);
  const after=await api.request('GET',`/api/v1/packages/${draft.id}`);assert.equal(after.revision,draft.revision);
  console.log(JSON.stringify({result:'PASS',package:draft.version,removableChanges:included.length,finalizedReadOnly:!!finalized,writes:writes.length}));
}finally{await api.browser.close();}
