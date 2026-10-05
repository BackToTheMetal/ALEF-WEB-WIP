import {createRequire} from 'node:module';import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..'),backend=process.env.ALEF_BACKEND_HOME||'C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const engines=createRequire(process.env.ALEF_PLAYWRIGHT_PACKAGE||path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const user=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,'')).users.find(u=>u.username==='author');
for(const name of(process.env.ALEF_BROWSERS||'chromium,webkit').split(',')){
 const browser=await engines[name].launch({headless:true}),page=await browser.newPage(),errors=[];page.on('pageerror',e=>errors.push(e.stack||String(e)));
 try{
  if(process.env.ALEF_TEST_BUNDLE)await page.route('**/app.js',r=>r.fulfill({path:path.resolve(process.env.ALEF_TEST_BUNDLE)}));
  // Cancel the real editor immediately after initialization is scheduled. The
  // downloaded graph, identity provider and model runtime are not substituted.
  await page.addInitScript(()=>{let api;Object.defineProperty(globalThis,'alefExample',{configurable:true,get:()=>api,set(value){api=value;const start=value.startRemote;let once=true;value.startRemote=(graph)=>{const result=start(graph);if(once){once=false;setTimeout(()=>[...document.querySelectorAll('button')].find(b=>b.textContent==='Cancel loading')?.click(),0);}return result;};}});});
  await page.goto('http://localhost:8080/oauth2/authorization/alef');await page.locator('#username').waitFor();await page.waitForLoadState('networkidle');await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);
  await page.getByRole('heading',{name:'Workspace loading cancelled',exact:true}).waitFor({timeout:120000});await page.waitForFunction(()=>window.alefExample?.status()==='CLOSED');
  assert.equal(await page.getByRole('progressbar',{name:'Workspace loading'}).isVisible(),false);
  await page.getByRole('button',{name:'Try again',exact:true}).click();await page.waitForFunction(()=>window.alefExample?.status()==='READY'&&document.documentElement.dataset.workspaceState==='ready',null,{timeout:120000});
  const progress=await page.evaluate(()=>window.alefExample.initializationProgress());assert.ok(progress.total>0);assert.equal(progress.completed,progress.total);
  const tree=await page.evaluate(()=>window.alefExample.projectTree());assert.ok(tree.models.flatMap(m=>m.roots).length>0);
  assert.deepEqual(errors,[]);console.log(`${name}: PASS cancellation disposes the partial real runtime, retry reconstructs the complete authorized graph (${progress.total} initialized nodes)`);
 }catch(error){console.error(errors);console.error(await page.evaluate(()=>({status:window.alefExample?.status(),message:[...document.querySelectorAll('.actions [role=status]')].map(e=>e.textContent)})).catch(()=>null));throw error;}
 finally{await page.evaluate(()=>window.alefBackend?.closeClient()).catch(()=>{});await browser.close();}
}
