// Real identity, native model API, accepted saves and served editor. API load
// clients represent distinct tabs under one authenticated user, not 20 rendered
// desktop browsers. Browser memory/loading is measured separately in Chromium.
import fs from 'node:fs';import path from 'node:path';import {createRequire} from 'node:module';import {randomUUID} from 'node:crypto';import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..'),backend=process.env.ALEF_BACKEND_HOME;
const {chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const user=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8')).users.find(u=>u.username==='author');
const count=Number(process.env.ALEF_CAPACITY_NODES),result={requestedNodes:count,profile:JSON.parse(fs.readFileSync(path.join(root,'out/backend-browser-artifacts.json'),'utf8')).profile,clients:[],browser:{}};
const browser=await chromium.launch({headless:true,args:['--enable-precise-memory-info']}),context=await browser.newContext(),page=await context.newPage();page.setDefaultNavigationTimeout(120000);
const output=()=>fs.writeFileSync(path.join(root,`out/backend-capacity-browser-${count}.json`),JSON.stringify(result,null,2));
const p95=values=>[...values].sort((a,b)=>a-b)[Math.ceil(values.length*.95)-1];
try{
 await page.goto('http://localhost:8080/oauth2/authorization/alef');await page.locator('#username').waitFor();await page.waitForLoadState('networkidle');await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);
 await Promise.all([page.waitForURL('http://localhost:8080/',{waitUntil:'commit'}),page.locator('#kc-login').click()]);await page.goto('about:blank');
 const csrf=await(await context.request.get('http://localhost:8080/api/csrf')).json();
 async function call(method,url,data,operation){const start=performance.now(),response=await context.request.fetch('http://localhost:8080'+url,{method,data,headers:{[csrf.headerName]:csrf.token,...(operation?{'Idempotency-Key':operation}:{})},timeout:180000});const body=await response.body(),status=response.status();await response.dispose();return{status,value:body.length?JSON.parse(body):null,bytes:body.length,millis:performance.now()-start};}
 async function json(method,url,data,operation){const response=await call(method,url,data,operation);assert.ok(response.status<300,JSON.stringify(response));return response.value;}
 for(let attempt=0;attempt<180;attempt++){if((await json('GET','/api/v1/workspace')).ready)break;if(attempt===179)throw new Error('Native workspace did not become ready within 180 seconds');await new Promise(resolve=>setTimeout(resolve,1000));}
 async function snapshot(){const start=performance.now(),manifest=await call('POST','/api/v1/snapshots');if(manifest.status!==200)return{status:manifest.status,error:manifest.value,millis:performance.now()-start};
  const graph=await call('GET',manifest.value.chunks[0].url);return{manifest:manifest.value,graph:graph.value,status:graph.status,millis:performance.now()-start,bytes:graph.bytes};}
 const first=await snapshot();assert.equal(first.status,200);assert.ok(first.manifest.counts.nodes>=count);result.actualCounts=first.manifest.counts;result.snapshotBytes=first.bytes;
 const model=first.graph.models.find(m=>m.name.startsWith('capacity.synthetic.')),selected=model.roots[0];
 function nodes(n){return 1+n.children.reduce((a,c)=>a+nodes(c.node),0);}assert.equal(nodes(selected),1000);
 await json('DELETE',`/api/v1/snapshots/${first.manifest.snapshotId}`);
 for(const concurrency of [1,10,20]){
  const clients=await Promise.all(Array.from({length:concurrency},()=>json('POST','/api/editing-clients'))),measurement={concurrency,loadMillis:[],saveMillis:[],rejections:[]};result.clients.push(measurement);
  try{
   const loads=await Promise.all(clients.map(()=>snapshot()));
   for(const load of loads){if(load.status===200){measurement.loadMillis.push(load.millis);await json('DELETE',`/api/v1/snapshots/${load.manifest.snapshotId}`);}else measurement.rejections.push({status:load.status,code:load.error?.code});}
   measurement.loadP95Millis=p95(measurement.loadMillis);
   const writer=clients[0];const lease=(await json('POST','/api/v1/leases',{editingClientId:writer.id,scopes:[{kind:'ROOT',key:selected.rootId}]})).leases[0];
   try{for(let sample=0;sample<5;sample++){
    const base=await snapshot();assert.equal(base.status,200);const currentModel=base.graph.models.find(m=>m.modelId===model.modelId),current=currentModel.roots.find(r=>r.rootId===selected.rootId),{rootId,version,editable,...edited}=current;
    const name=Object.keys(edited.properties)[0];edited.properties[name]='Capacity '+String.fromCharCode(97+sample)+' '+String.fromCharCode(97+concurrency);
    const readers=clients.slice(1).map(async()=>{const value=await snapshot();if(value.manifest)await json('DELETE',`/api/v1/snapshots/${value.manifest.snapshotId}`);return value.status;});
    const response=await call('POST','/api/v1/changes',{editingClientId:writer.id,baseSnapshotId:base.manifest.snapshotId,languageFingerprint:base.manifest.languageFingerprint,reason:'Synthetic capacity measurement',leases:[lease.token],rootUpdates:[{rootId,expectedVersion:version,expectedModelContext:currentModel.contextDigest,root:edited}],structuralOperations:[]},randomUUID());
    assert.equal(response.status,200,JSON.stringify(response.value));assert.equal(response.value.state,'ACTIVE');measurement.saveMillis.push(response.millis);await Promise.all(readers);await json('DELETE',`/api/v1/snapshots/${base.manifest.snapshotId}`);
    await json('POST','/api/v1/leases/renew',{editingClientId:writer.id,token:lease.token});
   }}finally{await json('POST','/api/v1/leases/release',{editingClientId:writer.id,token:lease.token});}
   measurement.saveP95Millis=p95(measurement.saveMillis);output();
  }finally{for(const client of clients)await json('POST','/api/editing-clients/close',{editingClientId:client.id});}
 }
 if(process.env.ALEF_CAPACITY_JOB_INTERFERENCE==='1'){
  const options=await json('GET','/api/v1/releases/options'),submission=await json('POST','/api/v1/releases',{sourceRevision:options.sourceRevision,baselineCandidateId:options.baselineCandidateId||'EMPTY',title:'Synthetic capacity background validation '+Date.now()},randomUUID());
  const candidateId=submission.candidate.id,client=await json('POST','/api/editing-clients'),lease=(await json('POST','/api/v1/leases',{editingClientId:client.id,scopes:[{kind:'ROOT',key:selected.rootId}]})).leases[0];
  const measurement=result.jobInterference={candidateId,saveMillis:[],observations:[],purpose:'Real isolated native validation while saving; synthetic model may fail semantic rules'};output();
  try{
   for(let sample=0;sample<5;sample++){
    const observation=await json('GET',`/api/v1/releases/${candidateId}`);measurement.observations.push({jobState:observation.jobState,stage:observation.stage});
    const base=await snapshot();assert.equal(base.status,200);const m=base.graph.models.find(m=>m.modelId===model.modelId),{rootId,version,editable,...edited}=m.roots.find(r=>r.rootId===selected.rootId);edited.properties[Object.keys(edited.properties)[0]]='Capacity background '+String.fromCharCode(97+sample);
    const response=await call('POST','/api/v1/changes',{editingClientId:client.id,baseSnapshotId:base.manifest.snapshotId,languageFingerprint:base.manifest.languageFingerprint,reason:'Synthetic capacity with real background validation',leases:[lease.token],rootUpdates:[{rootId,expectedVersion:version,expectedModelContext:m.contextDigest,root:edited}],structuralOperations:[]},randomUUID());
    measurement.saveMillis.push(response.millis);assert.equal(response.status,200,JSON.stringify(response.value));assert.equal(response.value.state,'ACTIVE');await json('DELETE',`/api/v1/snapshots/${base.manifest.snapshotId}`);await json('POST','/api/v1/leases/renew',{editingClientId:client.id,token:lease.token});output();
   }
   measurement.saveP95Millis=p95(measurement.saveMillis);
   for(let attempt=0;attempt<300;attempt++){const state=await json('GET',`/api/v1/releases/${candidateId}`);measurement.finalJobState=state.jobState;measurement.validationPassed=state.validation?.passed;if(['SUCCEEDED','FAILED','CANCELLED'].includes(state.jobState))break;await new Promise(resolve=>setTimeout(resolve,1000));}
   assert.ok(['SUCCEEDED','FAILED','CANCELLED'].includes(measurement.finalJobState),'Bounded release job finished');
   measurement.overlapObserved=measurement.observations.some(o=>o.jobState==='RUNNING');assert.ok(measurement.overlapObserved,'At least one measured Save overlaps the background job');
  }finally{await json('POST','/api/v1/leases/release',{editingClientId:client.id,token:lease.token});await json('POST','/api/editing-clients/close',{editingClientId:client.id});output();}
 }
 // A cold navigation exercises the actual served bundle. The 100 Mbit/s limit
 // applies to the graph and assets; report its effect rather than omitting assets.
 const cdp=await context.newCDPSession(page);await cdp.send('Network.enable');await cdp.send('Network.emulateNetworkConditions',{offline:false,latency:10,downloadThroughput:100_000_000/8,uploadThroughput:100_000_000/8});await cdp.send('Performance.enable');
 await page.addInitScript(()=>{window.capacityProgress=[];new MutationObserver(()=>{const p=[...document.querySelectorAll('progress')].filter(e=>!e.hidden).map(e=>({value:e.value,max:e.max,text:e.getAttribute('aria-label')}));if(p.length)window.capacityProgress.push({at:performance.now(),progress:p});}).observe(document,{subtree:true,attributes:true,childList:true});});
 const started=performance.now();await page.goto('http://localhost:8080/');await page.waitForFunction(()=>globalThis.alefExample?.status()==='READY'||globalThis.alefExample?.status().startsWith('FAIL:'),null,{timeout:180000});assert.equal(await page.evaluate(()=>globalThis.alefExample.status()),'READY');result.browser.loadMillis=performance.now()-started;
 result.browser.progress=await page.evaluate(()=>capacityProgress);result.browser.memory=(await cdp.send('Performance.getMetrics')).metrics.filter(m=>['JSHeapUsedSize','JSHeapTotalSize','Nodes','Documents','LayoutDuration','ScriptDuration','TaskDuration'].includes(m.name));
 result.status=result.clients.some(c=>c.rejections.length)?'PASS_WITH_CAPACITY_REJECTIONS':'PASS';console.log(JSON.stringify({...result,browser:{...result.browser,progressSamples:result.browser.progress.length,progress:undefined}}));
}catch(error){result.status='CAPACITY_FAILURE';result.error=String(error);throw error;}
finally{output();await context.close();await browser.close();}
