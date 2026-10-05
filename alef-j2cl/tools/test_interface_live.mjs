import {createRequire} from 'node:module';import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..'),{chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const credentials=JSON.parse(fs.readFileSync('C:/Users/niekh/Documents/ChatGPT/ALEFBackend/.local/development-identities.json','utf8').replace(/^\uFEFF/,''));
const account=credentials.users.find(x=>x.username===(process.env.ALEF_TEST_USER||'author'));const browser=await chromium.launch({headless:true});const page=await browser.newPage({viewport:{width:1440,height:950}});const errors=[];page.on('pageerror',e=>errors.push(String(e)));const output=path.join(root,'out/interface-tests');
try {
 const ready=()=>page.waitForFunction(()=>window.alefExample?.status()==='READY'&&document.documentElement.dataset.workspaceState==='ready',{},{timeout:120000});
 await page.goto('http://localhost:8080/#releases');await page.getByRole('heading',{name:'Sign in to ALEF',exact:true}).waitFor();
 assert.equal(await page.locator('.project-workspace').isVisible(),false);assert.equal(await page.locator('#workspace-startup .loading-ring').isVisible(),false);assert.equal(await page.locator('#status-text').textContent(),'Sign in to ALEF');
 await page.screenshot({path:path.join(output,'live-sign-in.png')});
 const login=page.getByRole('button',{name:'Log in',exact:true});await login.click();await page.locator('#username').fill(account.username);await page.locator('#password').fill(account.password);await page.locator('#kc-login').click();await page.waitForURL('http://localhost:8080/**');await ready();
 assert.equal(new URL(page.url()).hash,'#releases');await page.getByRole('heading',{name:'Packaging',exact:true}).waitFor();
 await page.reload();await ready();await page.getByRole('heading',{name:'Packaging',exact:true}).waitFor();
 // A failed transfer has an explicit recovery path, independent of auth.
 const unavailable=route=>route.fulfill({status:503,contentType:'application/problem+json',body:JSON.stringify({title:'Workspace temporarily unavailable'})});
 await page.route('**/api/v1/workspace',unavailable);await page.reload();await page.getByRole('heading',{name:'Could not open the workspace',exact:true}).waitFor();assert.match(await page.locator('#workspace-startup').innerText(),/Workspace temporarily unavailable/);assert.equal(await page.locator('#workspace-startup .loading-ring').isVisible(),false);
 await page.unroute('**/api/v1/workspace',unavailable);await page.getByRole('button',{name:'Try again',exact:true}).click();await ready();
 await page.getByRole('button',{name:'Packaging',exact:true}).waitFor({timeout:20000});
 const state=await page.evaluate(async()=>{const response=await fetch('/api/v1/packages/editor-status');const data=await response.json();return {status:response.status,count:Object.keys(data.roots||{}).length,rootRefs:(window.alefExample.projectTree().models||[]).flatMap(m=>m.roots).map(r=>({id:r.id,rootId:r.rootId}))};});assert.equal(state.status,200);assert.ok(state.count>0);console.log('Editor status: '+state.count+' authorized saved roots.');
 await page.getByRole('button',{name:'Editor',exact:true}).click();const row=page.locator('mps-project-view').locator('[data-kind=root]').first();await row.click();await page.screenshot({path:path.join(output,'live-editor.png')});
 await page.getByRole('button',{name:'Packaging',exact:true}).click();await page.getByRole('heading',{name:'Packaging',exact:true}).waitFor();assert.equal(await page.locator('#workflow-error').isVisible(),false,await page.locator('#workflow-error').textContent());
 await page.getByRole('button',{name:'+ New package',exact:true}).click();await page.locator('.package-picker label').first().waitFor({timeout:60000});assert.equal(await page.locator('dialog [role=alert]').isVisible(),false);
 // Never create a real package, even if client validation regresses.
 let invalidCreates=0;const preventCreate=route=>{if(route.request().method()==='POST'){invalidCreates++;return route.abort('blockedbyclient');}return route.continue();};
 await page.route('**/api/v1/packages',preventCreate);
 const picker=page.getByRole('dialog'),version=picker.getByRole('textbox',{name:'Package version',exact:true});
 await version.fill('1.41');await picker.getByRole('textbox',{name:'Name',exact:true}).fill('Validation check');
 await picker.getByRole('button',{name:'Select filtered',exact:true}).click();await picker.getByRole('button',{name:'Create package for review',exact:true}).click();
 await page.waitForFunction(()=>document.querySelector('dialog [role=alert]')?.textContent.includes('three numbers'));
 assert.equal(invalidCreates,0);assert.equal(await version.inputValue(),'1.41');assert.ok(await picker.locator('.package-picker input:checked').count()>0);
 await page.screenshot({path:path.join(output,'live-package-picker.png')});await picker.getByRole('button',{name:'Cancel',exact:true}).click();await page.unroute('**/api/v1/packages',preventCreate);
 console.log('PASS: served package form rejects 1.41 with an actionable message and preserves the selection without sending a creation request.');
 await page.getByRole('button',{name:'Compare',exact:true}).click();await page.locator('.comparison-body').waitFor({timeout:120000});await page.screenshot({path:path.join(output,'live-compare.png')});
 if(await page.getByRole('button',{name:'Deployment',exact:true}).count()){await page.getByRole('button',{name:'Deployment',exact:true}).click();await page.getByRole('heading',{name:'Deployment',exact:true}).waitFor();assert.equal(await page.locator('#workflow-error').isVisible(),false);}
 // Exercise an expired session response without signing out other live tabs.
 await page.getByRole('button',{name:'Editor',exact:true}).click();const expired=route=>route.fulfill({status:401,contentType:'application/problem+json',body:'{"code":"SESSION_REQUIRED"}'});
 await page.route('**/api/v1/packages',expired);await page.getByRole('button',{name:'Packaging',exact:true}).click();await page.getByRole('heading',{name:'Your session has expired',exact:true}).waitFor();assert.equal(await page.locator('#workspace-startup .loading-ring').isVisible(),false);assert.equal(await page.getByRole('button',{name:'Log in',exact:true}).isVisible(),true);
 await page.unroute('**/api/v1/packages',expired);await page.reload();await ready();await page.getByRole('heading',{name:'Packaging',exact:true}).waitFor();await page.screenshot({path:path.join(output,'live-packaging.png')});
 assert.deepEqual(errors,[]);console.log('PASS: real sign-in, direct Packaging entry/reload, generated editor, package options, retained comparison and available deployment view; injected unavailable/expired responses recover without a stuck spinner. No model, tag, package or deployment writes performed.');
} catch(error) {await page.screenshot({path:path.join(output,'live-failure.png')});console.error((await page.locator('body').innerText()).slice(0,1800));throw error;} finally {await browser.close();}
