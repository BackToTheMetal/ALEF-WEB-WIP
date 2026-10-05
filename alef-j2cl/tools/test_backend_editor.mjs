import {createRequire} from 'node:module';
import {saveWorkspace} from './backend_save_test.mjs';
import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..');
const engines=createRequire(process.env.ALEF_PLAYWRIGHT_PACKAGE||path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const backend=process.env.ALEF_BACKEND_HOME||'C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const user=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,'')).users.find(u=>u.username==='author');
fs.mkdirSync(path.join(root,'out/backend-editor'),{recursive:true});
for(const name of(process.env.ALEF_BROWSERS||'chromium').split(',')){
 const browser=await engines[name].launch({headless:true});const context=await browser.newContext({viewport:{width:1650,height:1100}});const page=await context.newPage();const errors=[];
 page.setDefaultNavigationTimeout(120000);
 page.on('pageerror',error=>errors.push(String(error)));
 try{
  await page.goto('http://localhost:8080/');await page.getByRole('button',{name:'Log in',exact:true}).click();
  await page.locator('#username').waitFor();await page.waitForLoadState('networkidle');
  await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);
  assert.ok((await page.locator('#username').inputValue())===user.username,'Identity-provider username field was not filled correctly');
  assert.ok((await page.locator('#password').inputValue())===user.password,'Identity-provider password field was not filled correctly');
  await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);
  await page.waitForFunction(()=>window.alefExample?.status()==='READY'||window.alefExample?.status().startsWith('FAIL:'),null,{timeout:120000});
  assert.equal(await page.evaluate(()=>window.alefExample.status()),'READY');
  await page.locator('mps-project-view [data-kind=root]').first().waitFor();
  const roots=await page.evaluate(()=>window.alefExample.projectTree().models.flatMap(m=>m.roots));
  const csrf=await(await context.request.get('http://localhost:8080/api/csrf')).json();
  const manifest=await(await context.request.post('http://localhost:8080/api/v1/snapshots',{headers:{[csrf.headerName]:csrf.token}})).json();
  const expectedRoots=[];for(const chunk of manifest.chunks){const graph=await(await context.request.get('http://localhost:8080'+chunk.url)).json();expectedRoots.push(...graph.models.filter(m=>!m.readOnly).flatMap(m=>m.roots));}
  assert.deepEqual(roots.map(r=>r.rootId).sort(),expectedRoots.map(r=>r.rootId).sort(),'Every authorized root is loaded before editing');
  for(const expected of expectedRoots){const {rootId,version,editable,...node}=expected;assert.deepEqual(await page.evaluate(id=>window.alefExample.rootSnapshot(id),rootId),node,'Every authorized descendant and reference is initialized');}
  if(process.env.ALEF_VERIFY_RESTART==='1'){
    const expected=JSON.parse(fs.readFileSync(path.join(root,'out/backend-editor/latest-saved-root.json'),'utf8'));
    assert.deepEqual(await page.evaluate(id=>window.alefExample.rootSnapshot(id),expected.rootId),expected.snapshot);
    console.log(`${name}: PASS persisted browser edit survives backend and native worker restart`);
    continue;
  }
  const selected=roots.find(r=>r.concept==='ObjectModel');assert.ok(selected);
  await page.locator('mps-project-view').getByRole('treeitem',{name:selected.name,exact:true}).click();
  await page.screenshot({path:path.join(root,`out/backend-editor/${name}-loaded.png`)});
  assert.ok(await page.getByRole('button',{name:'Lock root',exact:true}).isEnabled());
  await page.getByRole('button',{name:'Lock root',exact:true}).click();
  await page.getByText('Root locked for this tab',{exact:true}).waitFor();
  const editor=await page.locator('#alef-editor').boundingBox();
  await page.mouse.click(editor.x+230,editor.y+55);await page.keyboard.press('End');await page.keyboard.type('Z');
  await page.waitForFunction(id=>window.alefExample.dirtyRoots().includes(id),selected.rootId,{timeout:15000});
  const changed=await page.evaluate(id=>window.alefExample.rootSnapshot(id),selected.rootId);
  assert.ok(JSON.stringify(changed).includes('Z'));
  const publication=page.waitForRequest(request=>request.url().endsWith('/api/v1/changes')&&request.method()==='POST');
  const completed=saveWorkspace(page);
  // Focus flushing and legitimate automatic quick fixes can run between a
  // keystroke and Save. Compare with the actual submitted graph, not an earlier
  // intermediate editor frame captured before the button was clicked.
  const submitted=(await publication).postDataJSON().rootUpdates.find(r=>r.rootId===selected.rootId).root;
  await completed;
  await page.waitForFunction(()=>window.alefExample?.status()==='READY',null,{timeout:120000});
  const loaded=await page.evaluate(id=>window.alefExample.rootSnapshot(id),selected.rootId);
  assert.deepEqual(loaded,submitted,'Every node actually submitted by Save remains in the open editor');
  fs.writeFileSync(path.join(root,`out/backend-editor/${name}-saved-root.json`),JSON.stringify({rootId:selected.rootId,snapshot:loaded},null,2));
  fs.writeFileSync(path.join(root,'out/backend-editor/latest-saved-root.json'),JSON.stringify({rootId:selected.rootId,snapshot:loaded},null,2));
  await page.screenshot({path:path.join(root,`out/backend-editor/${name}-saved.png`)});
  assert.deepEqual(errors,[]);
  console.log(`${name}: PASS served generated editor, full authorized graph, root lease, actual keyboard edit and governed save without reloading`);
 }catch(error){await page.screenshot({path:path.join(root,`out/backend-editor/${name}-failure.png`),mask:[page.locator('input')]}).catch(()=>{});console.error('Browser state:',await page.evaluate(()=>({status:window.alefExample?.status(),error:document.querySelector('#error')?.textContent,messages:[...document.querySelectorAll('.actions [role=status]')].map(e=>e.textContent)})).catch(()=>null));console.error(errors);throw new Error(String(error.message).split('Call log:')[0].split('===')[0]);}
 finally{await context.close();await browser.close();}
}
