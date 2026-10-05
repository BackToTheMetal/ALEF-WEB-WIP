import {createRequire} from 'node:module';
import {saveWorkspace} from './backend_save_test.mjs';
import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..');
const engines=createRequire(process.env.ALEF_PLAYWRIGHT_PACKAGE||path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const backend=process.env.ALEF_BACKEND_HOME||'C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const user=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,'')).users.find(u=>u.username==='author');
fs.mkdirSync(path.join(root,'out/backend-structure'),{recursive:true});
for(const name of(process.env.ALEF_BROWSERS||'chromium').split(',')){
 const browser=await engines[name].launch({headless:true}),context=await browser.newContext({viewport:{width:1650,height:1100}}),page=await context.newPage();
 const errors=[];page.on('pageerror',error=>errors.push(error.stack||String(error)));
 try{
  await page.goto('http://localhost:8080/');await page.getByRole('button',{name:'Log in',exact:true}).click();
  await page.locator('#username').waitFor();await page.waitForLoadState('networkidle');
  await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);
  await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);
  const ready=async()=>{await page.waitForFunction(()=>window.alefExample?.status()==='READY',null,{timeout:120000});await page.locator('mps-project-view [data-kind=root]').first().waitFor();};
  await ready();console.log(`${name}: loaded complete graph`);const initial=await page.evaluate(()=>window.alefExample.projectTree().models.flatMap(m=>m.roots).length);
  const save=()=>saveWorkspace(page);
  for(const concept of(process.env.ALEF_ROOT_TYPES||'ObjectModel,Regelgroep,Service,ServiceTestSet').split(',')){
   console.log(`${name}: creating ${concept}`);
   const model=await page.evaluate(()=>window.alefExample.projectTree().models.find(m=>m.roots.some(r=>r.concept==='ObjectModel')));
   const types=await page.evaluate(id=>window.alefExample.rootTypes(id),model.id),type=types.find(t=>t.name===concept);assert.ok(type,concept);
   await page.locator('mps-project-view').getByRole('treeitem',{name:model.name,exact:true}).click();
   await page.getByRole('button',{name:'+ New root',exact:true}).click();const form=page.getByRole('form',{name:'Create root node'});
   await form.getByLabel('Root type',{exact:true}).selectOption(type.id);const label=`Backend ${concept} ${Date.now()}`;
   await form.getByLabel('Name',{exact:true}).fill(label);await form.getByRole('button',{name:'Create',exact:true}).click();await form.waitFor({state:'detached',timeout:20000});
   const created=await page.evaluate(label=>window.alefExample.projectTree().models.flatMap(m=>m.roots).find(r=>r.name===label),label);assert.ok(created?.rootId,'Reserved identity appears in the real project tree');
   await page.getByRole('button',{name:'Undo',exact:true}).click();
   await page.waitForFunction(id=>!window.alefExample.projectTree().models.flatMap(m=>m.roots).some(r=>r.rootId===id),created.rootId);
   await page.getByRole('button',{name:'Redo',exact:true}).click();
   await page.waitForFunction(id=>window.alefExample.projectTree().models.flatMap(m=>m.roots).some(r=>r.rootId===id),created.rootId);
   await page.locator('mps-project-view').getByRole('treeitem',{name:label,exact:true}).click();
   if(concept==='ServiceTestSet'){
     const bounds=await page.locator('#alef-editor').boundingBox();await page.mouse.click(bounds.x+170,bounds.y+83);await page.keyboard.press('Control+Space');
     await page.locator('.ws-popup-menu [role=option]').first().waitFor({timeout:15000});
     console.log('Entrypoint choices:',await page.locator('.ws-popup-menu [role=option]').allTextContents());
     await page.keyboard.press('ArrowDown');await page.keyboard.press('Enter');
     await page.waitForFunction(id=>window.alefExample.rootSnapshot(id).references.length>0,created.rootId);
     await page.screenshot({path:path.join(root,`out/backend-structure/${name}-testset-before-reference.png`)});
     if(process.env.ALEF_CAPTURE_TESTSET==='1'){console.log('Captured the generated Testspraak editor before selecting its required entrypoint');continue;}
   }
   const expected=await page.evaluate(id=>window.alefExample.rootSnapshot(id),created.rootId);
   fs.writeFileSync(path.join(root,`out/backend-structure/${name}-${concept}.json`),JSON.stringify({model:model.id,root:expected},null,2));await save();
   assert.deepEqual(await page.evaluate(id=>window.alefExample.rootSnapshot(id),created.rootId),expected,'Factory-created root roundtrips through native MPS');
   await page.locator('mps-project-view').getByRole('treeitem',{name:label,exact:true}).click();
   page.once('dialog',dialog=>dialog.accept());await page.getByRole('button',{name:'Delete root',exact:true}).click();
   await page.waitForFunction(id=>!window.alefExample.projectTree().models.flatMap(m=>m.roots).some(r=>r.rootId===id),created.rootId);await save();
   assert.equal(await page.evaluate(()=>window.alefExample.projectTree().models.flatMap(m=>m.roots).length),initial);
   console.log(`${name}: PASS ${concept}: real node factory, reserved identity, lease-aware undo/redo, governed create/save/delete/save without reloads`);
  }
  assert.deepEqual(errors,[]);
 }catch(error){await page.screenshot({path:path.join(root,`out/backend-structure/${name}-failure.png`),mask:[page.locator('input')]}).catch(()=>{});console.error(await page.evaluate(()=>({status:window.alefExample?.status(),messages:[...document.querySelectorAll('.actions [role=status]')].map(e=>e.textContent),error:document.querySelector('#error')?.textContent})).catch(()=>null));console.error(errors);throw new Error(String(error.message).split('Call log:')[0]);}
 finally{await page.evaluate(()=>window.alefBackend?.closeClient()).catch(()=>{});await context.close();await browser.close();}
}
