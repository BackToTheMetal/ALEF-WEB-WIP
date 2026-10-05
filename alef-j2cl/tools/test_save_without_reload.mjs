import {createRequire} from 'node:module';
import assert from 'node:assert/strict';
import {createHash,randomUUID} from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import {savedWorkspace} from '../example/saved-workspace.js';

// Real generated MPS editors, isolated persistence fixtures. No live workspace
// writes, leases or authentication: every /api request is intercepted.
const root=path.resolve(import.meta.dirname,'..');
const {chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const url=process.env.ALEF_EXAMPLE_URL||'http://127.0.0.1:8814/';
const property='ceab5195-25ea-4f22-9b92-103b95ca8c0c/1169194658468/1169194664001';
const graph={models:[{reference:'r:0984d67e-6c32-4580-8540-15a45f6929c8(save.fixture)',moduleId:'40c0c5ad-8f73-405e-b474-b1ba540eeb48',moduleName:'Save fixture',modelId:'0984d67e-6c32-4580-8540-15a45f6929c8',name:'save.fixture',contextDigest:'context-1',roots:[1,2].map(i=>({rootId:randomUUID(),version:randomUUID(),editable:true,id:String(i),concept:'471364db-8078-4933-b2ef-88232bfa34fc/653687101152179938',conceptName:'gegevensspraak.structure.ObjectModel',properties:{[property]:'SaveFixture'+i},children:[],references:[]}))}]};
let snapshotNo=0,mode='accept',lastReceipt,requests=[],validationError,failNextSnapshot=false;
const snapshots=new Map(),operations=new Map();
const browser=await chromium.launch({headless:true});
const page=await browser.newPage({viewport:{width:1600,height:1000}}),errors=[];
page.on('pageerror',e=>errors.push(String(e)));
page.setDefaultTimeout(30000);
await page.addInitScript(()=>{
  let runtime;window.saveEvents=[];window.starts=0;window.refreshes=0;window.streams=[];
  Object.defineProperty(window,'alefExample',{configurable:true,get:()=>runtime,set(api){runtime=api;const start=api.startRemote,refresh=api.refreshRemote;api.startRemote=(...args)=>{window.starts++;return start(...args);};api.refreshRemote=(...args)=>{window.refreshes++;return refresh(...args);};}});
  window.EventSource=class extends EventTarget{static CLOSED=2;readyState=1;constructor(){super();window.streams.push(this);}close(){this.readyState=2;}};
  document.addEventListener('alef-workspace-saved',e=>window.saveEvents.push(e.detail));
});
await page.route('**/backend-mode.js',r=>r.fulfill({contentType:'text/javascript',body:'globalThis.alefBackendMode=true;'}));
await page.route('**/api/**',async route=>{
  const req=route.request(),pathname=new URL(req.url()).pathname,body=req.postDataJSON();
  const reply=(data,status=200)=>route.fulfill({status,contentType:'application/json',body:JSON.stringify(data)});
  if(pathname==='/api/csrf')return reply({headerName:'X-CSRF',token:'fixture'});
  if(pathname==='/api/session')return reply({name:'Save test',workspacePermissions:['EDIT','READ']});
  if(pathname==='/api/editing-clients')return reply({id:'fixture-client'});
  if(pathname==='/api/editing-clients/close')return reply({});
  if(pathname==='/api/v1/workspace')return reply({ready:true});
  if(pathname==='/api/v1/packages/editor-status')return reply({roots:{}});
  if(pathname==='/api/v1/snapshots'){
    if(failNextSnapshot){failNextSnapshot=false;return reply({title:'Fixture snapshot temporarily unavailable'},503);}
    const id='snapshot-'+(++snapshotNo),text=JSON.stringify(graph);snapshots.set(id,text);
    return reply({snapshotId:id,revision:'revision-'+snapshotNo,policyEpoch:1,languageFingerprint:'fixture',eventCursor:'cursor-'+snapshotNo,counts:{roots:2,nodes:2},chunks:[{url:'/api/v1/snapshots/'+id+'/chunks/graph',bytes:Buffer.byteLength(text),sha256:createHash('sha256').update(text).digest('hex')}]});
  }
  if(pathname.includes('/chunks/'))return route.fulfill({contentType:'application/json',body:snapshots.get(pathname.split('/')[4])});
  if(pathname==='/api/v1/leases')return reply({leases:body.scopes.map(scope=>({token:{scope,leaseId:randomUUID(),generation:1},expiresAt:new Date(Date.now()+120000).toISOString()}))});
  if(pathname==='/api/v1/leases/renew')return reply({token:body.token,expiresAt:new Date(Date.now()+120000).toISOString()});
  if(pathname==='/api/v1/changes'){
    const key=req.headers()['idempotency-key'];requests.push({key,body});
    await new Promise(r=>setTimeout(r,450));
    if(mode==='reject')return reply({code:'INVALID_MODEL_CHANGE',title:'Fixture validation refused this edit'},422);
    try{
      assert.equal(body.baseSnapshotId,'snapshot-'+snapshotNo,'Second save advances the snapshot base');
      for(const update of body.rootUpdates){
        const model=graph.models.find(m=>m.roots.some(r=>r.rootId===update.rootId)),i=model.roots.findIndex(r=>r.rootId===update.rootId),old=model.roots[i];
        assert.equal(update.expectedVersion,old.version,'Second save uses the newly committed root version');
        assert.equal(update.expectedModelContext,model.contextDigest);
        model.roots[i]={...structuredClone(update.root),rootId:old.rootId,editable:true,version:randomUUID()};
      }
    }catch(error){validationError=error;return reply({title:error.message},409);}
    lastReceipt={operationId:key,state:'ACTIVE',revision:'revision-'+requests.length,roots:body.rootUpdates.map(u=>({rootId:u.rootId,version:graph.models.flatMap(m=>m.roots).find(r=>r.rootId===u.rootId).version}))};
    operations.set(key,lastReceipt);
    if(mode==='snapshot-failure'){mode='accept';failNextSnapshot=true;}
    if(mode==='server-effect')graph.models[0].roots[0].properties[property]+='ServerEffect';
    if(mode==='lost-response'){mode='accept';return route.abort('failed');}
    return reply(lastReceipt);
  }
  if(pathname.startsWith('/api/v1/operations/'))return reply(operations.get(pathname.split('/').at(-1)));
  throw new Error('Unexpected API request: '+req.method()+' '+pathname);
});
const saved=n=>page.waitForFunction(n=>window.saveEvents.length===n,n);
const dirty=id=>page.waitForFunction(id=>window.alefExample.dirtyRoots().includes(id),id);
const select=async item=>{await page.locator('mps-project-view').getByRole('treeitem',{name:item.name,exact:true}).click();};
async function type(text){const box=await page.locator('#alef-editor').boundingBox();await page.mouse.click(box.x+230,box.y+55);await page.keyboard.press('End');await page.keyboard.type(text);}
async function checkPreserved(editable=true){
  assert.equal(await page.evaluate(()=>window.alefExample===window.savedRuntime),true,'Save keeps the runtime object');
  assert.equal(await page.evaluate(()=>window.savedSurface.isConnected),true,'Save keeps the native editor surface');
  assert.equal(await page.evaluate(()=>window.starts),1);
  assert.equal(await page.evaluate(()=>window.refreshes),0,'Save does not reconstruct models');
  assert.equal(await page.locator('#editor-tabs,[role=tablist]').count(),0,'The editor has no tab strip');
  assert.equal(await page.getByRole('region',{name:'Node editor',exact:true}).count(),1,'Only one editor view is mounted');
  assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),[]);
  assert.equal(await page.evaluate(()=>window.alefExample.canUndo()),editable,'Saved history is available only while editing is allowed');
  assert.equal(await page.getByRole('button',{name:'Save',exact:true}).isDisabled(),true);
}
try{
  await page.goto(url+'#model');await page.waitForFunction(()=>document.documentElement.dataset.workspaceState==='ready',null,{timeout:120000});
  const roots=await page.evaluate(()=>window.alefExample.projectTree().models.flatMap(m=>m.roots));
  await select(roots[1]);await select(roots[0]);await page.getByRole('button',{name:'Lock root',exact:true}).click();
  await page.getByRole('button',{name:'Release lock',exact:true}).waitFor();
  await type('One');await dirty(roots[0].rootId);
  const unsaved=await page.evaluate(id=>window.alefExample.rootSnapshot(id),roots[0].rootId);
  await select(roots[1]);
  assert.deepEqual(await page.evaluate(id=>window.alefExample.rootSnapshot(id),roots[0].rootId),unsaved,'Switching roots preserves the previous draft');
  await select(await page.evaluate(id=>window.alefExample.projectTree().models.flatMap(m=>m.roots).find(r=>r.rootId===id),roots[0].rootId));
  await dirty(roots[0].rootId);
  assert.equal(await page.evaluate(()=>window.alefExample.canUndo()),true,'Switching roots retains undo history');
  const refused=await page.evaluate(ids=>{
    const snapshots=Object.fromEntries(ids.map(id=>[id,JSON.stringify(window.alefExample.rootSnapshot(id))]));snapshots[ids[1]]='null';
    try{window.alefExample.acknowledgeSave(JSON.stringify(snapshots));return false;}catch{return true;}
  },roots.map(r=>r.rootId));
  assert.equal(refused,true,'Stale acknowledgements are rejected atomically');await dirty(roots[0].rootId);
  await page.evaluate(()=>{window.savedRuntime=window.alefExample;window.savedSurface=document.querySelector('#alef-editor [data-swing-viewport-view]')||document.querySelector('#alef-editor canvas');});
  const navigation=[];page.on('framenavigated',f=>{if(f===page.mainFrame())navigation.push(f.url());});
  await page.getByRole('button',{name:'Save',exact:true}).click();
  assert.equal(await page.getByRole('button',{name:'Save',exact:true}).isDisabled(),true);
  await saved(1);await checkPreserved();
  assert.equal(await page.locator('.editor-toolbar [role=status]').textContent(),'','A successful save needs no confirmation text');
  const first=lastReceipt;
  await page.evaluate(receipt=>window.streams.at(-1).dispatchEvent(new MessageEvent('workspace.changes',{data:JSON.stringify({events:[receipt]})})),first);
  await type('Two');await dirty(roots[0].rootId);await page.getByRole('button',{name:'Save',exact:true}).click();await saved(2);await checkPreserved();
  assert.equal(requests[1].body.rootUpdates[0].expectedVersion,first.roots[0].version);
  // Undo after saving is still a local edit, rather than disappearing with a reload.
  await page.evaluate(()=>window.alefExample.undo());await dirty(roots[0].rootId);await page.evaluate(()=>window.alefExample.redo());
  await page.getByRole('button',{name:'Save',exact:true}).click();await saved(3);await checkPreserved();
  mode='reject';await type('Rejected');await dirty(roots[0].rootId);await page.getByRole('button',{name:'Save',exact:true}).click();
  await page.getByText('Fixture validation refused this edit',{exact:true}).waitFor();
  assert.ok((await page.evaluate(()=>window.alefExample.dirtyRoots())).includes(roots[0].rootId));
  assert.equal(await page.getByRole('button',{name:'Save',exact:true}).isEnabled(),true);
  mode='lost-response';await page.getByRole('button',{name:'Save',exact:true}).click();
  await page.getByRole('button',{name:'Resolve save',exact:true}).waitFor();
  const writes=requests.length;await page.getByRole('button',{name:'Resolve save',exact:true}).click();await saved(4);await checkPreserved();
  assert.equal(requests.length,writes,'Resolving an accepted save never submits another write');
  assert.equal(await page.evaluate(()=>sessionStorage.getItem('alef.pending-operation')),null);
  mode='snapshot-failure';await type('Recovery');await dirty(roots[0].rootId);await page.getByRole('button',{name:'Save',exact:true}).click();
  await page.getByText('Fixture snapshot temporarily unavailable',{exact:true}).waitFor();
  assert.ok(await page.evaluate(()=>sessionStorage.getItem('alef.pending-operation')),'Committed save remains resolvable if metadata download fails');
  await page.getByRole('button',{name:'Resolve save',exact:true}).click();await saved(5);await checkPreserved();
  mode='server-effect';await type('Preserved');await dirty(roots[0].rootId);const draft=await page.evaluate(id=>window.alefExample.rootSnapshot(id),roots[0].rootId);
  await page.getByRole('button',{name:'Save',exact:true}).click();await saved(6);await checkPreserved(false);
  await page.evaluate(id=>window.alefExample.setRootLease(id,Date.now()+120000),roots[0].rootId);
  assert.equal(await page.evaluate(()=>window.alefExample.canUndo()),true,'Freezing suspends retained history without clearing it');
  await page.evaluate(()=>window.alefExample.freezeEditing());
  assert.deepEqual(await page.evaluate(id=>window.alefExample.rootSnapshot(id),roots[0].rootId),draft,'A server-side difference does not overwrite the open editor');
  await page.getByText('Saved. The server also changed model content or location. Your current editor is preserved; use Reload when ready to show those changes.',{exact:true}).waitFor();
  assert.deepEqual(navigation,[]);assert.deepEqual(errors,[]);if(validationError)throw validationError;
  fs.mkdirSync(path.join(root,'out/save-regression'),{recursive:true});await page.screenshot({path:path.join(root,'out/save-regression/saved.png')});
  console.log('PASS: actual MPS edits; single editor without tabs; root switches preserve drafts and undo; repeated saves advance versions/base; clean Save disabled with no confirmation text; no navigation or MPS reload; undo/redo; own SSE ignored; validation failure keeps draft; stale acknowledgement rejected; lost-response and metadata-failure recovery; unexpected server changes preserve the editor. Persistence is mocked; no live workspace writes.');
  // A receipt must not silently install server-side relocation or a newer save.
  const current=structuredClone(graph),fresh=structuredClone(graph),id=fresh.models[0].roots[0].rootId,receipt={roots:[{rootId:id,version:fresh.models[0].roots[0].version}]};
  fresh.models[0].roots[0].properties[property]='Concurrent change';
  assert.equal(savedWorkspace(current,fresh,receipt,{[id]:current.models[0].roots[0]}).needsRefresh,true);
  assert.equal(current.models[0].roots[0].properties[property],graph.models[0].roots[0].properties[property]);
}catch(error){console.error(await page.locator('body').innerText());console.error(errors);throw error;}
finally{await browser.close();}
