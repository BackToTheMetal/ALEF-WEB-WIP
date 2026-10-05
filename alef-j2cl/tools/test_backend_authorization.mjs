import {createRequire} from 'node:module';
import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';import {randomUUID} from 'node:crypto';
const root=path.resolve(import.meta.dirname,'..'),backend=process.env.ALEF_BACKEND_HOME||'C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const {chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const users=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,'')).users;
const browser=await chromium.launch({headless:true});let admin,restore;
async function login(name){
 const context=await browser.newContext(),page=await context.newPage(),user=users.find(u=>u.username===name);
 await page.goto('http://localhost:8080/oauth2/authorization/alef');await page.locator('#username').waitFor();await page.waitForLoadState('networkidle');
 await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);
 await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);await page.goto('about:blank');
 const csrf=await(await context.request.get('http://localhost:8080/api/csrf')).json();
 const call=(method,url,data,headers={})=>context.request.fetch('http://localhost:8080'+url,{method,data,headers:{[csrf.headerName]:csrf.token,...headers}});
 const json=async(method,url,data,headers={})=>{const r=await call(method,url,data,headers);assert.ok(r.ok(),`${method} ${url}: ${r.status()} ${await r.text()}`);return r.json();};
 const identity=await json('GET','/api/session'),client=await json('POST','/api/editing-clients');
 return{context,call,json,identity,client};
}
const nodes=root=>[root,...root.children.flatMap(c=>nodes(c.node))];
async function snapshot(user){const manifest=await user.json('POST','/api/v1/snapshots');return{manifest,graph:await user.json('GET',manifest.chunks[0].url)};}
try{
 admin=await login('administrator');const editor=await login('editor'),author=await login('author');
 assert.equal((await editor.call('GET','/api/v1/admin/access')).status(),403);
 const original=await snapshot(author),locations=new Map();
 for(const model of original.graph.models.filter(m=>!m.readOnly))for(const root of model.roots)for(const node of nodes(root))locations.set(model.reference+'\n'+node.id,{model,root,node});
 let chosen;for(const source of locations.values())for(const edge of source.node.references){const target=locations.get((edge.model||source.model.reference)+'\n'+edge.nodeId);if(target&&target.root.rootId!==source.root.rootId&&Object.keys(source.root.properties).length){chosen={source,target,edge};break;}if(chosen)break;}
 assert.ok(chosen,'Real persisted corpus provides a cross-root reference');const{source,target}=chosen;
 const prior=(await admin.json('GET','/api/v1/admin/access')).rules.find(r=>r.principalId===editor.identity.principalId&&r.scope.kind==='ROOT'&&r.scope.key===target.root.rootId&&r.permission==='READ');
 const rule={principalId:editor.identity.principalId,scope:{kind:'ROOT',key:target.root.rootId},permission:'READ'};
 restore=()=>admin.json('PUT','/api/v1/admin/access',{...rule,effect:prior?.effect||'INHERIT'});
 const before=await snapshot(editor);await admin.json('PUT','/api/v1/admin/access',{...rule,effect:'DENY'});
 assert.equal((await editor.call('GET',before.manifest.chunks[0].url)).status(),409,'Revocation invalidates existing download handles');
 const oldEvents=await editor.call('GET','/api/v1/events?cursor='+encodeURIComponent(before.manifest.eventCursor));assert.match(await oldEvents.text(),/POLICY_CHANGED/);
 const hidden=await snapshot(editor),encoded=JSON.stringify(hidden.graph);
 assert.ok(!encoded.includes(target.root.rootId));assert.ok(!encoded.includes(target.node.id));assert.ok(!encoded.includes('nativeXml'));
 const visible=hidden.graph.models.flatMap(m=>m.roots).find(r=>r.rootId===source.root.rootId);assert.ok(visible);
 const protectedEdge=nodes(visible).flatMap(n=>n.references).find(r=>r.kind==='RESTRICTED');assert.ok(protectedEdge);
 assert.equal((await editor.call('POST','/api/v1/leases',{editingClientId:editor.client.id,scopes:[{kind:'ROOT',key:target.root.rootId}]})).status(),403);
 const lease=(await editor.json('POST','/api/v1/leases',{editingClientId:editor.client.id,scopes:[{kind:'ROOT',key:source.root.rootId}]})).leases[0];
 const proposal=structuredClone(visible);proposal.properties[Object.keys(proposal.properties)[0]]+=' ACL '+Date.now();
 const model=hidden.graph.models.find(m=>m.roots.some(r=>r.rootId===source.root.rootId));
 const body={editingClientId:editor.client.id,baseSnapshotId:hidden.manifest.snapshotId,languageFingerprint:hidden.manifest.languageFingerprint,reason:'Protected reference preservation integration',leases:[lease.token],rootUpdates:[{rootId:source.root.rootId,expectedVersion:visible.version,expectedModelContext:model.contextDigest,root:proposal}],structuralOperations:[]};
 const forged=structuredClone(body);nodes(forged.rootUpdates[0].root).flatMap(n=>n.references).find(r=>r.kind==='RESTRICTED').preservationHandle='forged';
 assert.equal((await editor.call('POST','/api/v1/changes',forged,{'Idempotency-Key':randomUUID()})).status(),403,'Forged protected edge cannot be published');
 assert.equal((await editor.json('POST','/api/v1/changes',body,{'Idempotency-Key':randomUUID()})).state,'ACTIVE');
 const after=await snapshot(author),saved=after.graph.models.flatMap(m=>m.roots).find(r=>r.rootId===source.root.rootId);
 assert.deepEqual(nodes(saved).find(n=>n.id===source.node.id).references,source.node.references,'Native target identity and resolveInfo are retained exactly');
 await admin.json('POST','/api/v1/admin/leases/force-release',{token:lease.token,reason:'Exercise administrative recovery in integration test'});
 assert.equal((await editor.call('POST','/api/v1/leases/renew',{editingClientId:editor.client.id,token:lease.token})).status(),423,'Superseded holder cannot renew');
 const tab2=await editor.json('POST','/api/editing-clients');
 const next=(await editor.json('POST','/api/v1/leases',{editingClientId:tab2.id,scopes:[{kind:'ROOT',key:source.root.rootId}]})).leases[0];
 assert.ok(next.token.generation>lease.token.generation);
 const current=await snapshot(editor),fresh=current.graph.models.flatMap(m=>m.roots).find(r=>r.rootId===source.root.rootId);
 const stale={...body,baseSnapshotId:current.manifest.snapshotId,rootUpdates:[{...body.rootUpdates[0],expectedVersion:fresh.version,root:fresh}]};
 assert.equal((await editor.call('POST','/api/v1/changes',stale,{'Idempotency-Key':randomUUID()})).status(),423,'Original tab cannot save with a superseded generation');
 await editor.json('POST','/api/v1/leases/release',{editingClientId:tab2.id,token:next.token});
 console.log('PASS: real OIDC identity, backend administration scope, root denial, revoked snapshot/cursor, hidden ID exclusion, opaque edge preservation, forged edge rejection, administrative recovery, stale lease generation and separate-tab ownership.');
}finally{if(restore)await restore();await browser.close();}
