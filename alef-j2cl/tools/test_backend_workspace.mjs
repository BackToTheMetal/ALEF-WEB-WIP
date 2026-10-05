import {createRequire} from 'node:module';
import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
import {randomUUID} from 'node:crypto';
const root=path.resolve(import.meta.dirname,'..');
const {chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const backend=process.env.ALEF_BACKEND_HOME||'C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const users=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,'')).users;
const browser=await chromium.launch({headless:true});
async function login(name){
 const context=await browser.newContext(),page=await context.newPage(),user=users.find(u=>u.username===name);
 await page.goto('http://localhost:8080/oauth2/authorization/alef');
 await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);
 await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);
 // API integration owns its own client; keep the editor from opening a second
 // snapshot/event stream in this context while preserving the authenticated cookie.
 await page.goto('about:blank');
 const csrf=await(await context.request.get('http://localhost:8080/api/csrf')).json();
 const headers={[csrf.headerName]:csrf.token};
 const post=async(url,body={},extra={})=>context.request.post('http://localhost:8080'+url,{headers:{...headers,...extra},data:body});
 const get=async url=>context.request.get('http://localhost:8080'+url);
 const client=await(await post('/api/editing-clients')).json();
 return {context,page,post,get,client};
}
try{
 const a=await login('author'),b=await login('editor');
 let status;for(let i=0;i<90;i++){status=await(await a.get('/api/v1/workspace')).json();if(status.ready)break;await new Promise(r=>setTimeout(r,1000));}
 assert.ok(status.ready,JSON.stringify(status));
 async function snapshot(user){const response=await user.post('/api/v1/snapshots');assert.equal(response.status(),200,await response.text());const manifest=await response.json();const chunk=await user.get(manifest.chunks[0].url);assert.equal(chunk.status(),200,await chunk.text());const graph=await chunk.json();return{manifest,graph};}
 const first=await snapshot(a);assert.equal(first.graph.rootCount,13);assert.equal(first.graph.models.filter(m=>m.readOnly).length,5);
 const stream=async(user,cursor)=>{const response=await user.get('/api/v1/events?cursor='+encodeURIComponent(cursor));assert.equal(response.status(),200,await response.text());const text=await response.text();return{text,data:JSON.parse(text.split('\n').find(line=>line.startsWith('data: ')).slice(6))};};
 assert.equal((await stream(a,first.manifest.eventCursor)).data.events.length,0);
 assert.ok((await stream(b,first.manifest.eventCursor)).text.includes('workspace.resnapshot'),'Cross-session event cursor rejected');
 assert.ok(!JSON.stringify(first.graph).includes('nativeXml'));
 assert.equal((await b.get(first.manifest.chunks[0].url)).status(),404,'Snapshot belongs to authenticated session');
 const named=root=>Object.keys(root.properties).some(p=>p.endsWith('/1169194664001'));
 const model=first.graph.models.find(m=>!m.readOnly&&m.roots.filter(named).length>=2);
 assert.ok(model,'Real fixture requires two named roots sharing a persistence file');
 const [ar,br]=model.roots.filter(named).slice(0,2);
 async function lease(user,id){const response=await user.post('/api/v1/leases',{editingClientId:user.client.id,scopes:[{kind:'ROOT',key:id}]});assert.equal(response.status(),200,await response.text());return(await response.json()).leases[0];}
 const al=await lease(a,ar.rootId),bl=await lease(b,br.rootId);
 assert.equal((await b.post('/api/v1/leases',{editingClientId:b.client.id,scopes:[{kind:'ROOT',key:ar.rootId}]})).status(),423);
 const second=await snapshot(b);
 const props=[Object.keys(ar.properties).find(p=>p.endsWith('/1169194664001')),Object.keys(br.properties).find(p=>p.endsWith('/1169194664001'))];
 assert.ok(props.every(Boolean),JSON.stringify([ar.properties,br.properties]));
 async function save(user,snapshot,root,property,lease,suffix){
   const edited=structuredClone(root);edited.properties[property]+=' '+suffix;
   const model=snapshot.graph.models.find(m=>m.roots.some(r=>r.rootId===root.rootId));
   const data={editingClientId:user.client.id,baseSnapshotId:snapshot.manifest.snapshotId,languageFingerprint:snapshot.manifest.languageFingerprint,
     reason:'Integration test '+suffix,leases:[lease.token],rootUpdates:[{rootId:root.rootId,expectedVersion:root.version,expectedModelContext:model.contextDigest,root:edited}],structuralOperations:[]};
   const op=randomUUID(),response=await user.post('/api/v1/changes',data,{'Idempotency-Key':op});
   assert.equal(response.status(),200,await response.text());assert.equal((await response.json()).state,'ACTIVE');
   const retry=await user.post('/api/v1/changes',data,{'Idempotency-Key':op});assert.equal(retry.status(),200,await retry.text());assert.deepEqual(await retry.json(),await response.json());
   const altered=structuredClone(data);altered.reason+=' changed';assert.equal((await user.post('/api/v1/changes',altered,{'Idempotency-Key':op})).status(),409);
   return edited;
 }
 const suffix=Date.now().toString();
 const ea=await save(a,first,ar,props[0],al,'A'+suffix);
 const br2=second.graph.models.flatMap(m=>m.roots).find(r=>r.rootId===br.rootId);
 const eb=await save(b,second,br2,props[1],bl,'B'+suffix);
 const final=await snapshot(a);const roots=final.graph.models.flatMap(m=>m.roots);
 const replay=await stream(a,first.manifest.eventCursor);assert.equal(replay.data.events.length,2);
 assert.deepEqual(replay.data.events.flatMap(e=>e.roots.map(r=>r.rootId)).sort(),[ar.rootId,br.rootId].sort());
 assert.deepEqual((await stream(a,first.manifest.eventCursor)).data.events,replay.data.events,'Reconnect replays the same accepted changes');
 assert.equal((await stream(a,replay.data.cursor)).data.events.length,0,'Acknowledged cursor does not repeat changes');
 assert.equal(roots.find(r=>r.rootId===ar.rootId).properties[props[0]],ea.properties[props[0]]);
 assert.equal(roots.find(r=>r.rootId===br.rootId).properties[props[1]],eb.properties[props[1]]);
 for(const [user,lock]of[[a,al],[b,bl]])assert.equal((await user.post('/api/v1/leases/release',{editingClientId:user.client.id,token:lock.token})).status(),200);
 console.log('PASS: real OIDC, complete authorized native graph, standard models, session-owned snapshots, lease exclusion, two stale-base edits in one physical file without lost updates, active save and idempotent retry, durable SSE replay, cursor ownership and event deduplication.');
 console.log('Saved revision: '+final.graph.revision);
}finally{await browser.close();}
