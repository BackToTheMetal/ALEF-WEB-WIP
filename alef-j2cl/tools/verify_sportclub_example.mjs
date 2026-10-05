import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';
import {connect,output,origin} from './workspace-example-api.mjs';

const api=await connect(),errors=[],writes=[];
try{
  api.page.on('pageerror',error=>errors.push(String(error)));
  await api.page.route('**/api/v1/**',async route=>{const r=route.request();if(r.method()!=='GET'&&/\/api\/v1\/(changes|leases|packages)/.test(r.url())){writes.push(r.url());await route.abort();}else await route.fallback();});
  await api.page.goto(origin+'/#model');
  await api.page.waitForFunction(()=>window.alefExample?.status()==='READY',null,{timeout:120000});
  const tree=await api.page.evaluate(()=>window.alefExample.projectTree());
  const roots=tree.models.flatMap(m=>m.roots);assert.equal(roots.length,6);
  const expected=['Sportclub','Contributieregels','Contributieservice','Deelnemergegevens','Contributieberekening','Contributie controleren'];
  const serviceModel=tree.models.find(m=>m.roots.some(r=>r.name==='Contributieservice'));
  for(const name of ['Deelnemergegevens','Contributieberekening'])assert.ok(serviceModel.roots.some(r=>r.name===name),'Mapping is in the service folder: '+name);
  for(const name of expected){
    assert.ok(roots.some(r=>r.name===name),name);
    await api.page.locator('mps-project-view').getByRole('treeitem',{name,exact:true}).click();
    await api.page.waitForTimeout(700);
    await api.page.screenshot({path:path.join(output,'live-'+name.replaceAll(' ','-')+'.png')});
  }
  assert.deepEqual(await api.page.evaluate(()=>window.alefExample.dirtyRoots()),[]);
  assert.deepEqual(errors,[]);assert.deepEqual(writes,[]);
  console.log('PASS: saved workspace displays all six connected example roots, including separate input/output mappings beside the service, without editor errors or writes.');
}finally{await api.page.evaluate(()=>window.alefBackend?.closeClient()).catch(()=>{});await api.browser.close();}
