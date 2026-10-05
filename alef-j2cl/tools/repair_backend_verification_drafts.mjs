// Repair only values introduced by this task's mutation tests, through the real
// authenticated lease/save boundary. Never replace a model with baseline bytes.
import {createRequire} from 'node:module';import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';import {randomUUID} from 'node:crypto';
const root=path.resolve(import.meta.dirname,'..'),backend='C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const {chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const users=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,'')).users;
const browser=await chromium.launch();try{
 const context=await browser.newContext(),page=await context.newPage(),user=users.find(u=>u.username==='author');await page.goto('http://localhost:8080/oauth2/authorization/alef');await page.locator('#username').waitFor();await page.waitForLoadState('networkidle');await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);await page.goto('about:blank');
 const csrf=await(await context.request.get('http://localhost:8080/api/csrf')).json();const call=async(method,url,data,headers={})=>{const r=await context.request.fetch('http://localhost:8080'+url,{method,data,headers:{[csrf.headerName]:csrf.token,...headers},timeout:120000});assert.ok(r.ok(),`${r.status()} ${await r.text()}`);return r.json();};
 for(let i=0;i<90;i++){if((await call('GET','/api/v1/workspace')).ready)break;await new Promise(r=>setTimeout(r,1000));}
 const manifest=await call('POST','/api/v1/snapshots'),graph=await call('GET',manifest.chunks[0].url),client=await call('POST','/api/editing-clients');
 const roots=[];for(const model of graph.models.filter(m=>!m.readOnly))for(const original of model.roots){const proposed=structuredClone(original);let changed=false;
  for(const[key,value]of Object.entries(proposed.properties))if(typeof value==='string'&&/\s(?:ACL |A\d{10}|B\d{10}|Browser |Remote |Local |UI |COLLAB|RESUME|collab|browser)/.test(value)){
    // Only constrained service/XML names need repair. Other human-readable names
    // remain exactly as entered by their browser test.
    if(proposed.conceptName.startsWith('servicespraak.')||model.name.endsWith('.Berichten')){proposed.properties[key]=value.replace(/\s+/g,'_');changed=true;console.log('Repairing test-created identifier',proposed.conceptName,key);}
  }
  if(changed)roots.push({rootId:original.rootId,expectedVersion:original.version,expectedModelContext:model.contextDigest,root:proposed});
 }
 if(roots.length){const acquired=await call('POST','/api/v1/leases',{editingClientId:client.id,scopes:roots.map(r=>({kind:'ROOT',key:r.rootId}))});
  const result=await call('POST','/api/v1/changes',{editingClientId:client.id,baseSnapshotId:manifest.snapshotId,languageFingerprint:manifest.languageFingerprint,reason:'Correct constrained identifiers introduced by integration test suffixes',leases:acquired.leases.map(l=>l.token),rootUpdates:roots,structuralOperations:[]},{'Idempotency-Key':randomUUID()});assert.equal(result.state,'ACTIVE');for(const lease of acquired.leases)await call('POST','/api/v1/leases/release',{editingClientId:client.id,token:lease.token});
 }
 await call('POST','/api/editing-clients/close',{editingClientId:client.id});console.log(`Repaired ${roots.length} test-created draft roots through governed publication.`);
}finally{await browser.close();}
