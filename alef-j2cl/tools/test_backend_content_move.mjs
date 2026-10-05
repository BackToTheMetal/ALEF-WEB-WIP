import {createRequire} from 'node:module';
import {saveWorkspace} from './backend_save_test.mjs';
import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..'),backend=process.env.ALEF_BACKEND_HOME||'C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const engines=createRequire(process.env.ALEF_PLAYWRIGHT_PACKAGE||path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const user=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,'')).users.find(u=>u.username==='author');
for(const name of(process.env.ALEF_BROWSERS||'chromium').split(',')){
 const browser=await engines[name].launch({headless:true}),page=await browser.newPage({viewport:{width:1700,height:1100}}),errors=[];page.on('pageerror',e=>errors.push(e.stack||String(e)));
 const ready=()=>page.waitForFunction(()=>window.alefExample?.status()==='READY',null,{timeout:120000});
 const tree=()=>page.evaluate(()=>window.alefExample.projectTree());
 const snapshot=id=>page.evaluate(id=>window.alefExample.rootSnapshot(id),id);
 const select=async id=>{const value=(await tree()).models.flatMap(m=>m.roots).find(r=>r.rootId===id);assert.ok(value);await page.locator('mps-project-view').getByRole('treeitem',{name:value.name,exact:true}).click();return value;};
 const save=()=>saveWorkspace(page);
 try{
  await page.goto('http://localhost:8080/oauth2/authorization/alef');await page.locator('#username').waitFor();await page.waitForLoadState('networkidle');await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);await ready();
  const initial=await tree();let source,model,before;
  for(const m of initial.models)for(const r of m.roots)if(!source&&r.concept==='ObjectModel'){const value=await snapshot(r.rootId);if(value.children.length){source=r;model=m;before=value;}}
  assert.ok(source,'Real persisted ObjectModel with contained data exists');const child=before.children.find(c=>c.node.conceptName.endsWith('.ObjectType')).node,anchor=before.children[before.children.findIndex(c=>c.node.id===child.id)+1]?.node;
  await page.locator('mps-project-view').getByRole('treeitem',{name:model.name,exact:true}).click();await page.getByRole('button',{name:'+ New root',exact:true}).click();
  const form=page.getByRole('form',{name:'Create root node'}),type=(await page.evaluate(id=>window.alefExample.rootTypes(id),model.id)).find(t=>t.name==='ObjectModel'),label='MoveTarget'+Date.now();
  await form.getByLabel('Root type',{exact:true}).selectOption(type.id);await form.getByLabel('Name',{exact:true}).fill(label);await form.getByRole('button',{name:'Create',exact:true}).click();await form.waitFor({state:'detached'});
  const target=(await tree()).models.flatMap(m=>m.roots).find(r=>r.name===label);assert.ok(target);await save();const targetBefore=await snapshot(target.rootId);
  const move=async(from,to)=>{await select(from.rootId);await page.getByRole('button',{name:'Move content',exact:true}).click();await page.getByLabel('Source content',{exact:true}).selectOption(child.id);
   const candidates=await page.evaluate(ref=>window.alefExample.containmentTargets(ref),model.id+'/'+child.id),index=candidates.findIndex(c=>c.reference===to.id);assert.ok(index>=0,'Real concept metadata exposes compatible target containment');await page.getByLabel('Target containment',{exact:true}).selectOption(String(index));if(to.rootId===source.rootId&&anchor)await page.getByLabel('Insert before',{exact:true}).selectOption(model.id+'/'+anchor.id);await page.getByRole('button',{name:'Move locally',exact:true}).click();await page.getByText('Content moved locally under both root locks. Save publishes both roots together.',{exact:true}).waitFor();};
  await move(source,target);assert.equal((await snapshot(source.rootId)).children.some(c=>c.node.id===child.id),false);assert.deepEqual((await snapshot(target.rootId)).children.find(c=>c.node.id===child.id).node,child);
  await page.getByRole('button',{name:'Undo',exact:true}).click();await page.waitForFunction(({id,child})=>window.alefExample.rootSnapshot(id).children.some(c=>c.node.id===child),{id:source.rootId,child:child.id});assert.deepEqual(await snapshot(source.rootId),before);assert.deepEqual(await snapshot(target.rootId),targetBefore);
  await page.getByRole('button',{name:'Redo',exact:true}).click();await page.waitForFunction(({id,child})=>window.alefExample.rootSnapshot(id).children.some(c=>c.node.id===child),{id:target.rootId,child:child.id});assert.deepEqual((await snapshot(target.rootId)).children.find(c=>c.node.id===child.id).node,child);await save();assert.deepEqual((await snapshot(target.rootId)).children.find(c=>c.node.id===child.id).node,child);
  await move(target,source);await save();assert.deepEqual(await snapshot(source.rootId),before,'Original graph and child order restored through native persistence');
  await select(target.rootId);page.once('dialog',d=>d.accept());await page.getByRole('button',{name:'Delete root',exact:true}).click();await save();assert.equal((await tree()).models.flatMap(m=>m.roots).length,initial.models.flatMap(m=>m.roots).length);
  assert.deepEqual(errors,[]);console.log(`${name}: PASS real contained-node move between leased roots, undo/redo, atomic native Save without reload, identity/reference preservation and governed cleanup`);
 }catch(error){console.error(errors);console.error(await page.evaluate(()=>({status:window.alefExample?.status(),messages:[...document.querySelectorAll('.actions [role=status]')].map(e=>e.textContent)})).catch(()=>null));throw error;}
 finally{await page.evaluate(()=>window.alefBackend?.closeClient()).catch(()=>{});await browser.close();}
}
