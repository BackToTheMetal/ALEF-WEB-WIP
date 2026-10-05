import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
import {connect,origin,root} from './workspace-example-api.mjs';

// Read the real authorized graph. Never acquire a lease or save workspace data.
const api=await connect(),errors=[],writes=[];
try {
 const {graph}=await api.snapshot();
 const expected=graph.models.filter(m=>!m.readOnly).flatMap(m=>m.roots);
 assert.equal(graph.models.filter(m=>m.readOnly).length,5,'Exercise all real standard models');
 if(process.env.ALEF_TEST_BUNDLE)await api.page.route('**/app.js',r=>r.fulfill({path:path.resolve(process.env.ALEF_TEST_BUNDLE)}));
 api.page.on('pageerror',e=>errors.push(String(e)));
 api.page.on('request',r=>{if(r.method()!=='GET'&&/\/api\/v1\/(changes|leases)(?:\/|$)/.test(new URL(r.url()).pathname))writes.push(r.url());});
 const ready=async()=>{
  await api.page.waitForFunction(()=>window.alefExample?.status()==='READY'||window.alefExample?.status().startsWith('FAIL:')||document.documentElement.dataset.workspaceState==='error',null,{timeout:120000});
  assert.equal(await api.page.evaluate(()=>window.alefExample.status()),'READY');
 };
 const verify=async()=>{
  const tree=await api.page.evaluate(()=>window.alefExample.projectTree());
  assert.deepEqual(tree.models.flatMap(m=>m.roots).map(r=>r.rootId).sort(),expected.map(r=>r.rootId).sort());
  for(const r of expected){
   const {rootId,version,editable,...node}=r;
   assert.deepEqual(await api.page.evaluate(id=>window.alefExample.rootSnapshot(id),rootId),node);
  }
  assert.deepEqual(await api.page.evaluate(()=>window.alefExample.dirtyRoots()),[]);
 };
 await api.page.goto(origin+'/#model');await ready();await verify();
 await api.page.locator('mps-project-view').getByRole('treeitem',{name:'Contributie controleren',exact:true}).click();
 fs.mkdirSync(path.join(root,'out/workspace-startup'),{recursive:true});
 await api.page.screenshot({path:path.join(root,'out/workspace-startup/loaded.png')});
 for(let i=0;i<2;i++){
  await api.page.evaluate(graph=>window.alefExample.refreshRemote(JSON.stringify(graph)),graph);
  await ready();await verify();
 }
 assert.deepEqual(errors,[]);assert.deepEqual(writes,[]);
 console.log(`PASS: actual authorized workspace opens and refreshes twice with five standard models and ${expected.length} editable roots; no leases, saves, or dirty nodes`);
} finally {
 await api.page.evaluate(()=>window.alefBackend?.closeClient()).catch(()=>{});
 await api.browser.close();
}
