import {createRequire} from 'node:module';
import {createHash,randomUUID} from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
// Real generated editors and project tree; every API request uses isolated fixtures.
const root=path.resolve(import.meta.dirname,'..');
const {chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const property='ceab5195-25ea-4f22-9b92-103b95ca8c0c/1169194658468/1169194664001';
const model={reference:'r:0984d67e-6c32-4580-8540-15a45f6929c8(locks.fixture)',moduleId:'40c0c5ad-8f73-405e-b474-b1ba540eeb48',moduleName:'Lock fixture',modelId:'0984d67e-6c32-4580-8540-15a45f6929c8',name:'locks.fixture',contextDigest:'context-1',roots:[1,2].map(i=>({rootId:randomUUID(),version:randomUUID(),editable:true,id:String(i),concept:'471364db-8078-4933-b2ef-88232bfa34fc/653687101152179938',conceptName:'gegevensspraak.structure.ObjectModel',properties:{[property]:'LockFixture'+i},children:[],references:[]}))};
// Include the editor's initial whitespace node so opening the root is clean.
for(const [i,r] of model.roots.entries())r.children=[{role:'471364db-8078-4933-b2ef-88232bfa34fc/653687101152179938/653687101152179939',node:{id:String(10+i),concept:'09c04f52-88c5-4bd6-a481-cabab9f61ff5/4485080112264973245',conceptName:'contexts.structure.Witruimte',properties:{},children:[],references:[]}}];
for(const [i,r] of model.roots.entries())r.children.unshift({role:'471364db-8078-4933-b2ef-88232bfa34fc/653687101152179938/653687101152179939',node:{id:String(20+i),concept:'471364db-8078-4933-b2ef-88232bfa34fc/653687101152157008',conceptName:'gegevensspraak.structure.ObjectType',properties:{[property]:'LockType'+(i+1)},children:[],references:[]}});
const graph={models:[model]},text=JSON.stringify(graph),held=new Map(),pending=[];
let pauseStatus=false,rejectRelease=false,acquires=0,releases=0,leaseDelay=180;
const browser=await chromium.launch({headless:true});
const page=await browser.newPage({viewport:{width:1440,height:950}}),errors=[];
if(process.env.ALEF_TEST_BUNDLE)await page.route('**/app.js',r=>r.fulfill({path:path.resolve(process.env.ALEF_TEST_BUNDLE)}));
page.on('pageerror',e=>errors.push(String(e)));
await page.addInitScript(()=>{
 window.lockPaints=[];
 const draw=CanvasRenderingContext2D.prototype.fillText;
 CanvasRenderingContext2D.prototype.fillText=function(text,x,y,...args){
  const m=this.getTransform();window.lockPaints.push({text,canvas:this.canvas,x:m.a*x+m.e,y:m.d*y+m.f,time:performance.now()});
  if(window.lockPaints.length>10000)window.lockPaints.splice(0,5000);
  return draw.call(this,text,x,y,...args);
 };
 window.EventSource=class extends EventTarget{static CLOSED=2;readyState=1;close(){this.readyState=2;}};
 const original=window.fetch;window.fetch=async(...args)=>{const response=await original(...args);if(String(args[0]).endsWith('/leases/release')&&response.ok)window.releaseReceived=performance.now();return response;};
});
if(!process.env.ALEF_TEST_SERVED)for(const file of ['backend.js','interface.js','interface-ui.js','interface.css'])await page.route('**/'+file,r=>r.fulfill({path:path.join(root,'example',file)}));
await page.route('**/backend-mode.js',r=>r.fulfill({contentType:'text/javascript',body:'globalThis.alefBackendMode=true;'}));
await page.route('**/api/**',async route=>{
 const req=route.request(),pathname=new URL(req.url()).pathname,body=req.postDataJSON();
 const reply=(data,status=200)=>route.fulfill({status,contentType:'application/json',body:JSON.stringify(data)});
 if(pathname==='/api/csrf')return reply({headerName:'X-CSRF',token:'fixture'});
 if(pathname==='/api/session')return reply({name:'Lock test',workspacePermissions:['EDIT','READ']});
 if(pathname==='/api/editing-clients')return reply({id:'lock-client'});
 if(pathname==='/api/editing-clients/close')return reply({});
 if(pathname==='/api/v1/workspace')return reply({ready:true});
 if(pathname==='/api/v1/packages/editor-status'){
  const roots=Object.fromEntries(model.roots.map(r=>[r.rootId,{version:r.version,tags:[],packages:[{id:'open-package',version:'1.4.0',title:'Open package',canEdit:true,linked:false}],...(held.has(r.rootId)||held.has(model.moduleId+'/'+model.modelId)?{lock:{label:'Locked by Lock test'}}:{})}]));
  if(pauseStatus)return new Promise(resolve=>pending.push(async()=>{await reply({roots});resolve();}));
  return reply({roots});
 }
 if(pathname==='/api/v1/snapshots')return reply({snapshotId:'fixture',revision:'revision-1',policyEpoch:1,languageFingerprint:'fixture',eventCursor:'cursor',counts:{roots:2,nodes:4},chunks:[{url:'/api/v1/snapshots/fixture/chunks/graph',bytes:Buffer.byteLength(text),sha256:createHash('sha256').update(text).digest('hex')}]});
 if(pathname.includes('/chunks/'))return route.fulfill({contentType:'application/json',body:text});
 if(pathname==='/api/v1/leases'){
  acquires++;await new Promise(r=>setTimeout(r,leaseDelay));
  const leases=body.scopes.map(scope=>({token:{scope,leaseId:randomUUID(),generation:1},expiresAt:new Date(Date.now()+120000).toISOString()}));leases.forEach(l=>held.set(l.token.scope.key,l));return reply({leases});
 }
 if(pathname==='/api/v1/leases/release'){
  releases++;await new Promise(r=>setTimeout(r,180));
  if(rejectRelease)return reply({title:'Release temporarily unavailable'},503);
  held.delete(body.token.scope.key);return reply({});
 }
 if(pathname==='/api/v1/leases/renew')return reply({token:body.token,expiresAt:new Date(Date.now()+120000).toISOString()});
 throw new Error('Unexpected request: '+pathname);
});
const tree=page.locator('mps-project-view'),control=page.locator('.lock-button');
const count=async value=>{await page.waitForFunction(value=>document.querySelector('mps-project-view').shadowRoot.querySelectorAll('.tree-lock').length===value,value,{timeout:2000});};
const waitPending=async n=>{for(let i=0;i<100&&pending.length<n;i++)await new Promise(r=>setTimeout(r,10));assert.ok(pending.length>=n);};
const assertReadOnlyInput=async(label,inspector=false,targetName='LockFixture1')=>{
 const before=await page.evaluate(id=>({snapshot:window.alefExample.rootSnapshot(id),dirty:window.alefExample.dirtyRoots(),inspector:window.alefExample.inspectorText()}),model.roots[0].rootId);
 const box=await page.locator('#alef-editor').boundingBox(),heading=inspector?await page.locator('.inspector-heading').boundingBox():null;
 const point=inspector?{x:box.x+95,y:heading.y+heading.height+48}:await page.evaluate(targetName=>{
  const p=[...window.lockPaints].reverse().find(p=>p.text.startsWith(targetName)&&p.canvas.isConnected&&p.canvas.getBoundingClientRect().height>0);
  if(!p)throw new Error('Root name was not painted');
  const b=p.canvas.getBoundingClientRect();return{x:b.x+p.x*b.width/p.canvas.width+12,y:b.y+p.y*b.height/p.canvas.height-5};
 },targetName);
 await page.mouse.click(point.x,point.y);
 assert.ok((await page.evaluate(()=>window.alefExample.inspectorState())).id,label+': mouse selection remains available');
 await page.keyboard.press('End');await page.evaluate(()=>window.lockInputStart=performance.now());await page.keyboard.type('MUST_NOT_EDIT',{delay:10});await page.keyboard.insertText('BLOCKED_INPUT');
 await page.evaluate(()=>new Promise(resolve=>requestAnimationFrame(()=>requestAnimationFrame(resolve))));
 const blockedPaints=await page.evaluate(()=>window.lockPaints.filter(p=>p.time>=window.lockInputStart&&(p.text.includes('MUST_NOT_EDIT')||p.text.includes('BLOCKED_INPUT'))).map(p=>({text:p.text,connected:p.canvas.isConnected,rect:JSON.stringify(p.canvas.getBoundingClientRect())})));
 if(blockedPaints.length){fs.mkdirSync(path.join(root,'out/lock-regression'),{recursive:true});await page.screenshot({path:path.join(root,'out/lock-regression/readonly-failure.png')});}
 assert.deepEqual(blockedPaints,[],label+': blocked input must not appear in a temporary native text cell');
 for(const key of ['Backspace','Delete','Enter','Control+Space','Escape','Control+z','Control+Shift+z','Control+y','Control+d','Control+Shift+ArrowDown','Control+/'])await page.keyboard.press(key);
 await page.evaluate(()=>new Promise(resolve=>requestAnimationFrame(()=>requestAnimationFrame(resolve))));
 const after=await page.evaluate(id=>({snapshot:window.alefExample.rootSnapshot(id),dirty:window.alefExample.dirtyRoots(),inspector:window.alefExample.inspectorText()}),model.roots[0].rootId);
 assert.deepEqual(after,before,label+': view-only input must not change nodes, drafts or inspector text');
 assert.deepEqual(errors,[],label+': rejected typing must not reach the model guard');
};
try{
 await page.goto((process.env.ALEF_EXAMPLE_URL||'http://localhost:8080/')+'#model');
 await page.waitForFunction(()=>document.documentElement.dataset.workspaceState==='ready',null,{timeout:120000});
 await tree.getByRole('treeitem',{name:'LockFixture1',exact:true}).click();
 await page.evaluate(()=>window.originalLockButton=document.querySelector('.lock-button'));
 assert.equal(await control.getAttribute('data-button-icon'),'unlock');
 await assertReadOnlyInput('No lock');
 await assertReadOnlyInput('Unlocked object type',false,'LockType1');
 await assertReadOnlyInput('Unlocked inspector',true);
 fs.mkdirSync(path.join(root,'out/lock-regression'),{recursive:true});await page.screenshot({path:path.join(root,'out/lock-regression/readonly.png')});
 pauseStatus=true;await page.evaluate(()=>document.dispatchEvent(new Event('alef-workspace-saved')));await waitPending(1);
 leaseDelay=1200;
 await control.click();await page.waitForFunction(()=>document.querySelector('.lock-button').getAttribute('aria-busy')==='true');assert.equal(await control.isDisabled(),true);
 await assertReadOnlyInput('Lock request pending');leaseDelay=180;
 await count(1);await waitPending(2);assert.equal(await control.getAttribute('aria-label'),'Release lock');assert.equal(await control.getAttribute('data-button-icon'),'lock');
 assert.equal(await page.getByRole('dialog').count(),0,'Locking an inherited package root does not ask where to put its changes');
 await control.click();await count(0);await waitPending(3);
 const releasePaintMs=await page.evaluate(()=>performance.now()-window.releaseReceived);assert.ok(releasePaintMs<1000,`Lock tree took ${releasePaintMs}ms after release`);
 assert.equal(await control.getAttribute('aria-label'),'Lock root');assert.equal(acquires,1);assert.equal(releases,1);
 await assertReadOnlyInput('Released lock');
 // Older locked and unlocked polls must not overwrite the latest confirmed release.
 await pending.pop()();for(const respond of pending.splice(0).reverse())await respond();pauseStatus=false;
 await page.evaluate(()=>new Promise(resolve=>requestAnimationFrame(()=>requestAnimationFrame(resolve))));await count(0);
 assert.equal(await page.evaluate(()=>window.originalLockButton===document.querySelector('.lock-button')),true);
 assert.equal(await page.locator('.editor-overflow,#project-name').count(),0);
 for(const name of ['Move root','Move content'])assert.equal(await page.getByRole('button',{name,exact:true}).count(),0);
 assert.equal(await control.evaluate(b=>b.nextElementSibling?.getAttribute('aria-label')),'Delete root');
 assert.equal(await page.getByRole('button',{name:'Settings',exact:true}).evaluate(b=>b.nextElementSibling?.getAttribute('aria-label')),'Log out');
 await control.click();await count(1);rejectRelease=true;await control.click();await page.getByText('Release temporarily unavailable',{exact:true}).waitFor();await count(1);
 assert.equal(await control.getAttribute('aria-label'),'Release lock');assert.equal(await control.isEnabled(),true);
 rejectRelease=false;await control.click();await count(0);
 await page.getByRole('button',{name:'Settings',exact:true}).click();await page.getByRole('button',{name:'Lock model',exact:true}).click();await count(2);
 await page.getByRole('button',{name:'Editor',exact:true}).click();await tree.getByRole('treeitem',{name:'LockFixture2',exact:true}).click();
 await control.click();await tree.getByRole('treeitem',{name:'LockFixture1',exact:true}).click();await count(0);assert.equal(await control.getAttribute('aria-label'),'Lock root');
 await control.click();await count(1);
 await tree.getByRole('treeitem',{name:'LockFixture2',exact:true}).click();
 const secondBefore=await page.evaluate(id=>window.alefExample.rootSnapshot(id),model.roots[1].rootId),secondBox=await page.locator('#alef-editor').boundingBox();
 await page.mouse.click(secondBox.x+230,secondBox.y+55);await page.keyboard.type('NO_LOCK');
 assert.deepEqual(await page.evaluate(id=>window.alefExample.rootSnapshot(id),model.roots[1].rootId),secondBefore,'Another root lock does not permit editing this root');
 await tree.getByRole('treeitem',{name:'LockFixture1',exact:true}).click();
 const box=await page.locator('#alef-editor').boundingBox();await page.mouse.click(box.x+230,box.y+55);await page.keyboard.press('End');await page.keyboard.insertText('Draft');
 await page.waitForFunction(()=>window.alefExample.dirtyRoots().length>0&&document.querySelector('.lock-button').disabled);
 assert.match(await control.getAttribute('title'),/Save your changes/);
 assert.equal(await page.getByRole('button',{name:'Save',exact:true}).locator('svg').count(),1);assert.equal(await page.getByRole('button',{name:'Tags',exact:true}).locator('svg').count(),1);
 fs.mkdirSync(path.join(root,'out/lock-regression'),{recursive:true});await page.screenshot({path:path.join(root,'out/lock-regression/editor.png')});
 await page.setViewportSize({width:878,height:791});
 for(const b of [control,page.getByRole('button',{name:'Save',exact:true}),page.getByRole('button',{name:'Tags',exact:true}),page.getByRole('button',{name:'Delete root',exact:true}),page.getByRole('button',{name:'Log out',exact:true})]){const rect=await b.boundingBox();assert.ok(rect.x>=0&&rect.x+rect.width<=878,'Editor action must fit the narrower viewport');}
 await page.screenshot({path:path.join(root,'out/lock-regression/editor-narrow.png')});
 await page.setViewportSize({width:1440,height:950});
 await page.evaluate(id=>{window.testLockUntil=Date.now()+80;window.alefExample.setRootLease(id,window.testLockUntil);},model.roots[0].rootId);
 await page.waitForFunction(()=>Date.now()>window.testLockUntil&&!window.alefExample.canUndo());
 await assertReadOnlyInput('Expired lock with draft');
 await page.evaluate(id=>window.alefExample.setRootLease(id,Date.now()+120000),model.roots[0].rootId);
 assert.equal(await page.evaluate(()=>window.alefExample.canUndo()),true,'Reacquiring enables retained undo');
 await page.getByRole('button',{name:'Undo',exact:true}).click();
 await page.waitForFunction(id=>window.alefExample.rootSnapshot(id).properties["ceab5195-25ea-4f22-9b92-103b95ca8c0c/1169194658468/1169194664001"]==='LockFixture1',model.roots[0].rootId);
 await page.evaluate(()=>window.alefExample.freezeEditing());
 await assertReadOnlyInput('Frozen editor');await assertReadOnlyInput('Frozen inspector',true);
 assert.deepEqual(errors,[]);
 console.log(JSON.stringify({status:'PASS',releasePaintMs:Math.round(releasePaintMs),checks:['unlocked and pending roots are selectable and read-only','inspector is read-only without a lease','released, expired and frozen leases block input','other root lock grants no editing','reacquiring retains undo','one icon lock/release control','immediate root and model lock updates','out-of-order status responses','failed release retains lock','selection change during release','unsaved draft protection'],liveWorkspaceWrites:false}));
}catch(error){console.error(JSON.stringify({acquires,releases,held:[...held.keys()],errors,body:await page.locator('body').innerText(),statuses:await page.evaluate(()=>document.querySelector('mps-project-view').statuses),dirty:await page.evaluate(()=>window.alefExample.dirtyRoots()),node:await page.evaluate(id=>window.alefExample.rootSnapshot(id),model.roots[0].rootId)}));throw error;}
finally{for(const respond of pending.splice(0))await respond().catch(()=>{});await browser.close();}
