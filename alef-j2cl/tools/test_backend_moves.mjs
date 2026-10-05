import {createRequire} from 'node:module';import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';
import {saveWorkspace} from './backend_save_test.mjs';
const root=path.resolve(import.meta.dirname,'..'),backend=process.env.ALEF_BACKEND_HOME||'C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const engines=createRequire(process.env.ALEF_PLAYWRIGHT_PACKAGE||path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const user=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,'')).users.find(u=>u.username==='author');
for(const name of (process.env.ALEF_BROWSERS||'chromium').split(',')){
 const browser=await engines[name].launch({headless:true}),context=await browser.newContext({viewport:{width:1700,height:1100}}),page=await context.newPage();
 const errors=[];page.on('pageerror',e=>errors.push(String(e)));
 const ready=async()=>{await page.waitForFunction(()=>window.alefExample?.status()==='READY',null,{timeout:120000});await page.locator('mps-project-view [data-kind=root]').first().waitFor();};
 const tree=()=>page.evaluate(()=>window.alefExample.projectTree());
 const select=async id=>{const current=await tree(),entry=current.models.flatMap(m=>m.roots).find(r=>r.rootId===id);assert.ok(entry);await page.locator('mps-project-view').getByRole('treeitem',{name:entry.name,exact:true}).click();return entry;};
 // Server-side relocations need an explicit refresh to inspect reference repairs.
 const save=async()=>{await saveWorkspace(page);await page.reload();await ready();};
 try{
  await page.goto('http://localhost:8080/oauth2/authorization/alef');await page.locator('#username').waitFor();await page.waitForLoadState('networkidle');await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);await ready();
  const original=await tree(),source=original.models.find(m=>m.name.endsWith('servicetest.choice')),destination=original.models.find(m=>m.name.endsWith('.Flow'));assert.ok(source);assert.ok(destination);
  const selected=original.models.flatMap(m=>m.roots).find(r=>r.rootId==='51a0751f-b793-4bfe-894c-0e3c067d0790')||source.roots.find(r=>r.concept==='ObjectModel');assert.ok(selected);
  const relocate=async target=>{await select(selected.rootId);await page.getByRole('button',{name:'Move root',exact:true}).click();await page.getByLabel('Destination model').selectOption({label:target.name});await page.getByRole('button',{name:'Stage move',exact:true}).click();await page.getByText('Root move staged. Save moves it and repairs incoming references atomically.',{exact:true}).waitFor();await save();};
  if(!source.roots.some(r=>r.rootId===selected.rootId)){await relocate(source);console.log(`${name}: restored earlier interrupted test move through the governed save pipeline`);}
  const nativeId=selected.id.slice(selected.id.lastIndexOf('/')+1),before=await page.evaluate(id=>window.alefExample.rootSnapshot(id),selected.rootId);
  for(const target of [destination,source]){
   await relocate(target);
   const moved=(await tree()).models.find(m=>m.id===target.id).roots.find(r=>r.rootId===selected.rootId);assert.ok(moved,'Stable application identity moves to the chosen model');assert.ok(moved.id.endsWith('/'+nativeId),'Native node ID is retained');assert.deepEqual(await page.evaluate(id=>window.alefExample.rootSnapshot(id),selected.rootId),before,'Root graph survives model relocation');
  }
  console.log(`${name}: PASS browser stages cross-model move, atomically repairs incoming references, saves/reloads and moves back with stable identities`);
  if(process.env.ALEF_SKIP_INTERNAL_COMMAND_TEST!=='1'){
   await select(selected.rootId);await page.getByRole('button',{name:'Lock root',exact:true}).click();await page.getByText('Root locked for this tab',{exact:true}).waitFor();
   const current=await tree(),other=current.models.flatMap(m=>m.roots).find(r=>r.rootId!==selected.rootId&&r.concept==='Service');assert.ok(other);
   const snapshots=await page.evaluate(ids=>ids.map(id=>window.alefExample.rootSnapshot(id)),[selected.rootId,other.rootId]);
   const result=await page.evaluate(refs=>{
    const App=goog.module.get('alefj2cl.example.AlefEditorApp'),Facade=goog.module.get('org.jetbrains.mps.openapi.persistence.PersistenceFacade'),Runnable=goog.module.get('java.lang.Runnable');
    function method(object,name,...args){let type=object;while(type){const key=Object.getOwnPropertyNames(type).find(k=>k.startsWith('m_'+name+'__')&&typeof object[k]==='function'&&object[k].length===args.length);if(key)return object[key](...args);type=Object.getPrototypeOf(type);}throw new Error('Missing actual runtime method '+name);}
    const host=App.f_host__alefj2cl_example_AlefEditorApp_,project=method(host,'getProject'),repository=method(project,'getRepository'),access=method(project,'getModelAccess'),facade=method(Facade,'getInstance');
    let rejected=false;try{method(access,'executeCommand',Runnable.$adapt(()=>{for(const reference of refs){const node=method(method(facade,'createNodeReference',reference),'resolve',repository),iterator=method(method(node,'getProperties'),'iterator');let property;while(method(iterator,'hasNext')){const candidate=method(iterator,'next');if(method(candidate,'getName')==='name'){property=candidate;break;}}if(!property)throw new Error('Representative root lacks a name property');method(node,'setProperty',property,method(node,'getProperty',property)+'_atomic');}}));}catch(error){rejected=String(error).includes('editing lock');}return{rejected};
   },[current.models.flatMap(m=>m.roots).find(r=>r.rootId===selected.rootId).id,other.id]);assert.equal(result.rejected,true);
   await page.getByText('The entire rejected command was rolled back. Earlier drafts are preserved; undo history was cleared.',{exact:true}).waitFor({timeout:30000});await ready();
   assert.deepEqual(await page.evaluate(ids=>ids.map(id=>window.alefExample.rootSnapshot(id)),[selected.rootId,other.rootId]),snapshots,'Failure in a second root rolls back the first mutation');
   console.log(`${name}: PASS actual multi-root MPS command cannot leave a partial local mutation after a missing lease`);
  }
  assert.deepEqual(errors,[]);
 }catch(error){fs.mkdirSync(path.join(root,'out/backend-moves'),{recursive:true});await page.screenshot({path:path.join(root,`out/backend-moves/${name}-failure.png`),mask:[page.locator('input')]}).catch(()=>{});console.error(await page.evaluate(()=>({status:window.alefExample?.status(),messages:[...document.querySelectorAll('.actions [role=status]')].map(e=>e.textContent)})).catch(()=>null));console.error(errors);throw new Error(String(error.message).split('Call log:')[0]);}
 finally{await page.evaluate(()=>window.alefBackend?.closeClient()).catch(()=>{});await browser.close();}
}
