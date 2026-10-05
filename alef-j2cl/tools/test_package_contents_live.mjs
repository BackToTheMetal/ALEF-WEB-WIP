import {createRequire} from 'node:module';
import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..'),{chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const account=JSON.parse(fs.readFileSync('C:/Users/niekh/Documents/ChatGPT/ALEFBackend/.local/development-identities.json','utf8').replace(/^\uFEFF/,'')).users.find(u=>u.username==='author');
const packageId=process.env.ALEF_TEST_PACKAGE_ID||'92da4f11-0094-42a9-bccf-b93fd9232640';
const browser=await chromium.launch({headless:true}),page=await browser.newPage({viewport:{width:1565,height:1244}}),errors=[],writes=[];
const output=path.join(root,'out/package-contents-fix');fs.mkdirSync(output,{recursive:true});page.setDefaultTimeout(30000);
if(process.env.ALEF_TEST_SOURCE)for(const [file,type] of [['interface.js','application/javascript'],['interface-ui.js','application/javascript'],['interface.css','text/css'],['project-view.js','application/javascript'],['mps-diff.js','application/javascript'],['model-diff.js','application/javascript']])await page.route('**/'+file,route=>route.fulfill({status:200,contentType:type,body:fs.readFileSync(path.join(root,'example',file))}));
page.on('pageerror',e=>errors.push(String(e)));
await page.route('**/api/v1/**',async route=>{const r=route.request();if(r.method()!=='GET'&&/\/api\/v1\/(changes|packages|leases)/.test(r.url())){writes.push(r.url());await route.abort();}else await route.fallback();});
try {
  await page.goto('http://localhost:8080/#release/'+packageId);await page.getByRole('button',{name:'Log in',exact:true}).click();
  await page.locator('#username').fill(account.username);await page.locator('#password').fill(account.password);await page.locator('#kc-login').click();await page.waitForURL('http://localhost:8080/**');
  console.log('Loading the actual failing package and its retained MPS contents');
  await page.waitForFunction(()=>document.documentElement.dataset.workspaceState==='ready',null,{timeout:120000});
  await page.locator('mps-model-diff[data-state=ready]').waitFor({timeout:180000});
  const detail=await (await page.request.get('http://localhost:8080/api/v1/packages/'+packageId)).json();
  const response=await page.request.get('http://localhost:8080/api/v1/packages/'+packageId+'/contents');assert.equal(response.status(),200);const data=await response.json();
  assert.equal(data.revision,detail.revision);assert.equal(detail.release.jobState,'FAILED');assert.equal(detail.readyToFinalize,false);assert.equal(detail.release.manifest,undefined);
  const selected=detail.release.provenance.composition.roots.filter(r=>!r.deleted);
  assert.equal(data.roots.filter(r=>!r.deleted).length,selected.length);
  assert.equal(data.afterGraph.models.reduce((n,m)=>n+m.roots.length,0),selected.length);
  assert.ok(data.roots.every(r=>r.deleted||r.after));assert.ok(data.roots.some(r=>r.changed));
  await page.getByRole('button',{name:'All roots',exact:true}).click();assert.equal(await page.locator('.comparison-tree [data-kind=root]').count(),selected.length);
  const initialTree=await page.evaluate(()=>window.alefExample.projectTree());
  const unchanged=data.roots.find(r=>!r.changed&&!r.deleted);
  let states;if(unchanged){await page.locator('.comparison-tree [data-kind=root]').filter({hasText:unchanged.name}).first().click();await page.locator('mps-model-diff[data-state=ready]').waitFor();
  states=await page.locator('mps-model-diff').evaluate(el=>el.states);assert.equal(Object.keys(states).length,2);for(const s of Object.values(states)){assert.equal(s.readOnly,true);assert.ok(s.text.trim());}}
  await page.getByRole('button',{name:'Changes only',exact:true}).click();assert.equal(await page.locator('.comparison-tree [data-kind=root]').count(),data.roots.filter(r=>r.changed).length);
  await page.locator('mps-model-diff[data-state=ready]').waitFor();states=await page.locator('mps-model-diff').evaluate(el=>el.states);for(const s of Object.values(states)){assert.equal(s.readOnly,true);assert.ok(s.text.trim());}
  assert.equal(await page.getByRole('button',{name:'Finalize',exact:true}).isDisabled(),true);
  await page.screenshot({path:path.join(output,'contents-working.png')});
  assert.deepEqual(await page.locator('#workflow-page [role=tab]').allTextContents(),['Contents','Checks']);
  const comparisonSession=await page.locator('mps-comparison-session').getAttribute('id');
  await page.getByRole('tab',{name:'Checks',exact:true}).click();
  await page.getByRole('tabpanel',{name:'Checks',exact:true}).getByText(detail.release.error,{exact:true}).waitFor();
  assert.equal(await page.getByRole('tabpanel',{name:'Contents',exact:true}).isVisible(),false);
  await page.getByRole('tab',{name:'Contents',exact:true}).click();
  await page.locator('mps-model-diff[data-state=ready]').waitFor();
  assert.equal(await page.locator('mps-comparison-session').getAttribute('id'),comparisonSession,'Switching tabs keeps the contents editor mounted');
  await page.locator('.interface-actions').getByRole('button',{name:/^(Add|Manage) reviewers$/}).click();await page.locator('dialog h2').getByText('Package reviewers',{exact:true}).waitFor();await page.getByRole('button',{name:'Cancel',exact:true}).click();
  assert.equal(await page.locator('mps-comparison-session').getAttribute('id'),comparisonSession,'Reviewer popup keeps the contents editor mounted');
  assert.equal(await page.getByText('Package details',{exact:true}).count(),0);
  for(const width of [1242,700]){await page.setViewportSize({width,height:764});await page.locator('#workflow-page').evaluate(el=>el.scrollTop=0);assert.ok(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth));await page.screenshot({path:path.join(output,'package-actions-'+width+'.png')});}
  await page.setViewportSize({width:1565,height:1244});
  // A pending package has no release manifest either. Viewing it must use the
  // same retained contents endpoint, without permitting review/finalization.
  const pending=structuredClone(detail);pending.id='pending-contents-fixture';pending.reviewers=[];pending.state='In review';pending.release.jobState='QUEUED';pending.release.error=null;
  let pendingReads=0,pendingContentsReads=0;
  await page.route('**/api/v1/packages/pending-contents-fixture',route=>{pendingReads++;return route.fulfill({status:200,contentType:'application/json',body:JSON.stringify(pending)});});
  await page.route('**/api/v1/packages/pending-contents-fixture/contents',route=>{pendingContentsReads++;return route.fulfill({status:200,contentType:'application/json',body:JSON.stringify({...data,revision:pending.revision})});});
  await Promise.all([page.waitForResponse(r=>r.url().endsWith('/pending-contents-fixture/contents')),page.evaluate(()=>location.hash='release/pending-contents-fixture')]);
  await page.locator('mps-model-diff[data-state=ready]').waitFor();
  const pendingSession=await page.locator('mps-comparison-session').getAttribute('id');
  const deadline=Date.now()+10000;while(pendingReads<2&&Date.now()<deadline)await new Promise(resolve=>setTimeout(resolve,100));
  assert.ok(pendingReads>=2,'Pending preparation is polled');assert.equal(await page.locator('mps-comparison-session').getAttribute('id'),pendingSession,'Status polling must not reload the editors');
  assert.equal(await page.getByRole('button',{name:'Finalize',exact:true}).isDisabled(),true);
  // Saving reviewers is intercepted in this browser; the live package stays unchanged.
  const reviewerOptions=[...(pending.reviewers||[]).map(({id,name})=>({id,name})),{id:'fixture-reviewer',name:'Fixture Reviewer'}],reviewerSaves=[];
  await page.route('**/api/v1/packages/pending-contents-fixture/reviewers',route=>{
    if(route.request().method()==='GET')return route.fulfill({status:200,contentType:'application/json',body:JSON.stringify(reviewerOptions)});
    const body=route.request().postDataJSON();reviewerSaves.push(body);assert.equal(body.expectedRevision,pending.revision);
    pending.revision++;pending.reviewers=body.reviewers.map(id=>reviewerOptions.find(r=>r.id===id));pending.release.jobState='QUEUED';pending.release.stage='Preparation queued';pending.state='In review';pending.readyToFinalize=false;
    return route.fulfill({status:200,contentType:'application/json',body:JSON.stringify(pending)});
  });
  const nativeView=await page.locator('mps-model-diff').elementHandle(),nativeSurface=await page.locator('.mps-diff-surface').first().elementHandle(),selectedRoot=await page.locator('.comparison-tree [aria-selected=true]').getAttribute('data-id');
  const currentRoot=await page.locator('.comparison-tree [aria-selected=true]').getAttribute('aria-label');
  await page.locator('.comparison-tree').getByRole('searchbox',{name:'Find a root'}).fill(currentRoot);
  const originalRevision=pending.revision,originalContentsReads=pendingContentsReads;
  await page.setViewportSize({width:1242,height:764});
  await page.locator('.interface-actions').getByRole('button',{name:/^(Add|Manage) reviewers$/}).click();await page.getByRole('checkbox',{name:'Fixture Reviewer',exact:true}).check();
  const scroll=await page.locator('#workflow-page').evaluate(el=>{el.scrollTop=80;return el.scrollTop;});assert.ok(scroll>0,'Exercise preservation of an actual scroll offset');
  await page.getByRole('button',{name:'Save reviewers',exact:true}).click();await page.locator('dialog').waitFor({state:'detached'});
  assert.equal(reviewerSaves.length,1);assert.equal(reviewerSaves[0].expectedRevision,originalRevision);
  assert.equal(await page.getByRole('button',{name:'Fixture Reviewer · Awaiting review',exact:true}).isVisible(),true);
  assert.equal(await nativeView.evaluate(el=>el.isConnected),true);assert.equal(await nativeSurface.evaluate(el=>el.isConnected),true);
  assert.equal(await page.locator('mps-comparison-session').getAttribute('id'),pendingSession);assert.equal(await page.locator('.comparison-tree [aria-selected=true]').getAttribute('data-id'),selectedRoot);
  assert.equal(await page.locator('.comparison-tree').getByRole('searchbox').inputValue(),currentRoot);assert.equal(await page.locator('#workflow-page').evaluate(el=>el.scrollTop),scroll);
  assert.ok((await page.locator('.fixed-source').innerText()).endsWith('revision '+pending.revision));assert.equal(pendingContentsReads,originalContentsReads);
  await page.getByRole('tab',{name:'Checks',exact:true}).click();pending.release.jobState='SUCCEEDED';pending.release.stage='Complete';pending.release.validation={passed:true,diagnostics:[],tests:[]};
  await page.getByRole('tabpanel',{name:'Checks',exact:true}).getByText('Passed',{exact:true}).waitFor();
  assert.equal(await nativeSurface.evaluate(el=>el.isConnected),true);assert.equal(await page.getByRole('tab',{name:'Checks',exact:true}).getAttribute('aria-selected'),'true');assert.equal(pendingContentsReads,originalContentsReads);
  await page.getByRole('tab',{name:'Contents',exact:true}).click();assert.equal(await page.locator('.comparison-tree').getByRole('searchbox').inputValue(),currentRoot);
  await page.locator('.comparison-tree').getByRole('button',{name:'Clear root filter',exact:true}).click();
  await page.getByRole('button',{name:'Editor',exact:true}).click();assert.deepEqual(await page.evaluate(()=>window.alefExample.projectTree()),initialTree);assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),[]);
  // The comparison sidebar shares the editor's tree component and keeps the native session.
  const editorMetrics=await page.locator('#project-view [data-kind=root]').first().evaluate(el=>{const s=getComputedStyle(el);return {height:s.height,font:s.font,gap:s.gap};});
  await page.getByRole('button',{name:'Compare',exact:true}).click();await page.locator('mps-model-diff[data-state=ready]').waitFor();
  const compareTree=page.locator('.comparison-tree mps-project-view'),roots=compareTree.locator('[data-kind=root]');
  assert.deepEqual(await roots.first().evaluate(el=>{const s=getComputedStyle(el);return {height:s.height,font:s.font,gap:s.gap};}),editorMetrics);
  assert.equal(await compareTree.getByRole('button',{name:'+ New root',exact:true}).count(),0);
  const rootNames=await roots.evaluateAll(rows=>rows.map(r=>r.getAttribute('aria-label'))),session=await page.locator('mps-comparison-session').getAttribute('id');
  await compareTree.getByRole('searchbox',{name:'Find a root',exact:true}).fill(rootNames.at(-1));
  assert.equal(await roots.count(),1);await roots.first().click();await page.locator('mps-model-diff[data-state=ready]').waitFor();
  assert.equal(await roots.first().getAttribute('aria-selected'),'true');
  assert.equal(await compareTree.getByRole('searchbox').inputValue(),rootNames.at(-1),'Root selection retains the search');
  await compareTree.getByRole('button',{name:'Clear root filter',exact:true}).click();assert.equal(await roots.count(),rootNames.length);
  const model=compareTree.locator('[data-kind=model]').first();await model.focus();await page.keyboard.press('ArrowLeft');
  assert.equal(await model.getAttribute('aria-expanded'),'false');assert.ok(await roots.count()<rootNames.length);
  await page.keyboard.press('ArrowRight');assert.equal(await model.getAttribute('aria-expanded'),'true');assert.equal(await roots.count(),rootNames.length);
  for(const width of [1242,700]){
    await page.setViewportSize({width,height:764});await page.locator('#workflow-page').evaluate(el=>el.scrollTop=0);
    assert.equal(await page.locator('.mps-diff-toolbar').count(),0);assert.equal(await page.getByRole('button',{name:/^(↑ Previous|↓ Next|Side by side|Stacked)$/}).count(),0);
    const panes=await page.locator('.mps-diff-pane').evaluateAll(nodes=>nodes.map(el=>({x:el.getBoundingClientRect().x,y:el.getBoundingClientRect().y,width:el.getBoundingClientRect().width})));
    assert.equal(panes.length,2);assert.equal(panes[0].y,panes[1].y);assert.ok(panes[1].x>panes[0].x+panes[0].width-1);
    assert.ok(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth));
    await page.screenshot({path:path.join(output,'compare-project-'+width+'.png')});
  }
  assert.equal(await page.locator('mps-comparison-session').getAttribute('id'),session);
  assert.deepEqual(await page.evaluate(()=>window.alefExample.projectTree()),initialTree);assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),[]);
  // Compare the actual package with a second retained snapshot in response copies.
  // No packages or model nodes are created in the backend.
  const source=page.getByRole('combobox',{name:'Compare',exact:true}),against=page.getByRole('combobox',{name:'Against',exact:true});
  const packages=await (await page.request.get('http://localhost:8080/api/v1/packages')).json();
  const other={...packages.find(p=>p.id===packageId),id:'comparison-other',version:'0.9.0',title:'Comparison fixture',revision:1};
  const otherContents=structuredClone(data),otherRoot=otherContents.roots.find(r=>r.after?.conceptName?.endsWith('.ObjectModel'))||otherContents.roots.find(r=>r.after);
  const nameKey=Object.keys(otherRoot.after.properties).find(k=>otherRoot.after.properties[k]===otherRoot.name);assert.ok(nameKey);
  const originalName=otherRoot.name,fixtureName=originalName+' previous package';otherRoot.name=otherRoot.after.properties[nameKey]=fixtureName;
  for(const model of otherContents.afterGraph.models){const index=model.roots.findIndex(n=>model.reference+'/'+n.id===otherRoot.afterReference);if(index>=0)model.roots[index]=structuredClone(otherRoot.after);}
  await page.route('**/api/v1/packages',route=>route.fulfill({status:200,contentType:'application/json',body:JSON.stringify([...packages,other])}));
  await page.route('**/api/v1/packages/comparison-other/contents',route=>route.fulfill({status:200,contentType:'application/json',body:JSON.stringify(otherContents)}));
  await page.getByRole('button',{name:'Editor',exact:true}).click();await page.getByRole('button',{name:'Compare',exact:true}).click();await source.waitFor();
  await source.selectOption('package:'+packageId);
  await page.waitForFunction(id=>document.querySelector('[aria-label=Compare]').value==='package:'+id&&!document.querySelector('.interface-card[aria-busy]'),packageId);
  await against.selectOption('package:comparison-other');
  await page.waitForFunction(name=>{const view=document.querySelector('mps-model-diff');return view?.dataset.state==='ready'&&view.states.before?.text.includes(name);},fixtureName);
  let packageStates=await page.locator('mps-model-diff').evaluate(el=>el.states);
  assert.ok(packageStates.after.text.includes(originalName));assert.ok(!packageStates.after.text.includes(fixtureName));
  assert.ok(packageStates.before.highlights.length);assert.equal(packageStates.after.readOnly,true);assert.equal(packageStates.before.readOnly,true);
  assert.equal(await page.getByRole('button',{name:'Remove change',exact:true}).count(),0);assert.equal(await page.getByRole('button',{name:'Comment on selected node',exact:true}).count(),0);
  await page.setViewportSize({width:1242,height:764});await page.locator('#workflow-page').evaluate(el=>el.scrollTop=0);await page.screenshot({path:path.join(output,'compare-two-packages.png')});
  await against.selectOption('package:'+packageId);await page.getByText('No roots to show.',{exact:true}).waitFor();
  await page.getByRole('button',{name:'All roots',exact:true}).click();await page.locator('mps-model-diff[data-state=ready]').waitFor();
  packageStates=await page.locator('mps-model-diff').evaluate(el=>el.states);assert.equal(packageStates.before.text,packageStates.after.text);assert.deepEqual(packageStates.before.highlights,[]);assert.deepEqual(packageStates.after.highlights,[]);
  await source.selectOption('package:comparison-other');
  await page.waitForFunction(name=>{const view=document.querySelector('mps-model-diff');return view?.dataset.state==='ready'&&view.states.after?.text.includes(name);},fixtureName);
  packageStates=await page.locator('mps-model-diff').evaluate(el=>el.states);assert.ok(!packageStates.before.text.includes(fixtureName));
  await source.selectOption('workspace');
  await page.waitForFunction(()=>document.querySelector('[aria-label=Compare]').value==='workspace'&&!document.querySelector('.interface-card[aria-busy]')&&document.querySelector('mps-model-diff')?.dataset.state==='ready');
  assert.equal(await against.inputValue(),'package:'+packageId);assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),[]);assert.deepEqual(await page.evaluate(()=>window.alefExample.projectTree()),initialTree);
  assert.deepEqual(errors,[]);assert.deepEqual(writes,[]);
  console.log('PASS: actual failed package displays exact retained roots and native read-only MPS views; All roots/Changes only, reviewer saves and validation updates without native editor reloads, separate checks, shared project tree, search and keyboard folders, fixed side-by-side layout, package-to-package and workspace selectors, disabled Finalize, pending package, and workspace isolation. No model/package writes.');
}catch(error){await page.screenshot({path:path.join(output,'failure.png')});console.error(errors);console.error((await page.locator('body').innerText()).slice(-1500));throw error;}finally{await browser.close();}
