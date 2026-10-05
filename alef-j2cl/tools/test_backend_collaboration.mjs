import {createRequire} from 'node:module';import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';
import {saveWorkspace} from './backend_save_test.mjs';
const root=path.resolve(import.meta.dirname,'..'),backend=process.env.ALEF_BACKEND_HOME||'C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const engines=createRequire(process.env.ALEF_PLAYWRIGHT_PACKAGE||path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const users=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,'')).users;
fs.mkdirSync(path.join(root,'out/backend-collaboration'),{recursive:true});
for(const engine of(process.env.ALEF_BROWSERS||'chromium').split(',')){
 const browser=await engines[engine].launch({headless:true}),pages=[],errors=[];
 async function session(name,ui=true){
  const context=await browser.newContext({viewport:{width:1650,height:1100}}),page=await context.newPage();pages.push(page);page.on('pageerror',e=>errors.push(e.stack||String(e)));
  await page.goto('http://localhost:8080/oauth2/authorization/alef');await page.locator('#username').waitFor();await page.waitForLoadState('networkidle');
  const user=users.find(u=>u.username===name);await page.locator('#username').fill(name);await page.locator('#password').fill(user.password);
  await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);
  if(ui)await page.waitForFunction(()=>window.alefExample?.status()==='READY',null,{timeout:120000});else await page.goto('about:blank');
  const csrf=await(await context.request.get('http://localhost:8080/api/csrf')).json();
  const call=(method,url,data)=>context.request.fetch('http://localhost:8080'+url,{method,data,headers:{[csrf.headerName]:csrf.token}});
  return{context,page,call};
 }
 async function edit(user,node,suffix){
  const{page}=user;await page.locator('mps-project-view').getByRole('treeitem',{name:node.name,exact:true}).click();const lock=page.getByRole('button',{name:'Lock root',exact:true});if(await lock.isEnabled()){await lock.click();await page.getByText('Root locked for this tab',{exact:true}).waitFor();}
  const bounds=await page.locator('#alef-editor').boundingBox();await page.mouse.click(bounds.x+230,bounds.y+55);await page.keyboard.press('End');await page.keyboard.type(suffix);
  await page.waitForFunction(id=>window.alefExample.dirtyRoots().includes(id),node.rootId);return page.evaluate(id=>window.alefExample.rootSnapshot(id),node.rootId);
 }
 async function save(user){await saveWorkspace(user.page);}
 try{
  const a=await session('author'),b=await session('editor'),admin=await session('administrator',false);
  const roots=await a.page.evaluate(()=>window.alefExample.projectTree().models.flatMap(m=>m.roots));const [left,right]=roots.filter(r=>r.concept==='ObjectModel');assert.ok(left&&right);
  const draft=await edit(a,left,' Local'),remote=await edit(b,right,' Remote');await save(b);
  await a.page.waitForFunction(({id,value})=>window.alefExample?.status()==='READY'&&JSON.stringify(window.alefExample.rootSnapshot(id))===JSON.stringify(value),{id:right.rootId,value:remote},{timeout:60000});
  assert.deepEqual(await a.page.evaluate(id=>window.alefExample.rootSnapshot(id),left.rootId),draft,'Remote accepted edits do not overwrite the dirty root');
  assert.ok((await a.page.evaluate(()=>window.alefExample.dirtyRoots())).includes(left.rootId));
  assert.equal(await a.page.evaluate(()=>window.alefExample.canUndo()),false,'Remote batch does not enter local undo history');
  await save(a);console.log(`${engine}: PASS remote event applies current graph once while preserving an independent unsaved draft, then draft Save succeeds`);
  const fresh=await a.page.evaluate(id=>window.alefExample.projectTree().models.flatMap(m=>m.roots).find(r=>r.rootId===id),left.rootId);
  const interrupted=await edit(a,fresh,' Recover');
  const active=await(await admin.call('GET','/api/v1/admin/leases')).json(),lease=active.find(l=>l.token.scope.key===left.rootId);assert.ok(lease);
  assert.equal((await admin.call('POST','/api/v1/admin/leases/force-release',{token:lease.token,reason:'Browser draft recovery acceptance test'})).status(),200);
  await a.page.getByRole('button',{name:'Save',exact:true}).click();await a.page.getByRole('button',{name:'Resume draft',exact:true}).waitFor();
  await a.page.waitForFunction(()=>!Array.from(document.querySelectorAll('button')).find(b=>b.textContent==='Resume draft').disabled);
  await a.page.getByRole('button',{name:'Resume draft',exact:true}).click();
  await a.page.getByText('Current changes loaded; your drafts are preserved. Previous undo history cleared.',{exact:true}).waitFor({timeout:60000});
  assert.deepEqual(await a.page.evaluate(id=>window.alefExample.rootSnapshot(id),left.rootId),interrupted);
  await save(a);console.log(`${engine}: PASS forced lease loss rejects Save, preserves the draft, reacquires and compares against the current revision, then saves once`);
  assert.deepEqual(errors,[]);await a.page.screenshot({path:path.join(root,`out/backend-collaboration/${engine}-complete.png`)});
 }catch(error){for(let i=0;i<pages.length;i++)await pages[i].screenshot({path:path.join(root,`out/backend-collaboration/${engine}-${i}-failure.png`),mask:[pages[i].locator('input')]}).catch(()=>{});console.error(errors);for(const page of pages)console.error(await page.evaluate(()=>({status:window.alefExample?.status(),messages:[...document.querySelectorAll('.actions [role=status]')].map(e=>e.textContent),error:document.querySelector('#error')?.textContent})).catch(()=>null));throw new Error(String(error.message).split('Call log:')[0]);}
 finally{for(const page of pages)await page.evaluate(()=>window.alefBackend?.closeClient()).catch(()=>{});await browser.close();}
}
