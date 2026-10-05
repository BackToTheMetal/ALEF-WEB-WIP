import {createRequire} from 'node:module';
import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';import {randomUUID,createHash} from 'node:crypto';
const root=path.resolve(import.meta.dirname,'..'),backend=process.env.ALEF_BACKEND_HOME||'C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const {chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const users=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,'')).users;
const browser=await chromium.launch({headless:true});let restore;
async function login(name,ui=false){const context=await browser.newContext(),page=await context.newPage(),user=users.find(u=>u.username===name);
 await page.goto('http://localhost:8080/oauth2/authorization/alef');await page.locator('#username').waitFor();await page.waitForLoadState('networkidle');await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);
 if(ui)await page.waitForFunction(()=>globalThis.alefExample?.status()==='READY',{},{timeout:120000});else await page.goto('about:blank');
 const csrf=await(await context.request.get('http://localhost:8080/api/csrf')).json();
 const call=(method,url,data,headers={})=>context.request.fetch('http://localhost:8080'+url,{method,data,headers:{[csrf.headerName]:csrf.token,...headers},timeout:180000});
 const json=async(method,url,data,headers={})=>{const r=await call(method,url,data,headers);assert.ok(r.ok(),`${method} ${url}: ${r.status()} ${await r.text()}`);return r.json();};return{context,page,call,json,identity:await json('GET','/api/session')};
}
try{
 const author=await login('author',true),title='Browser release '+Date.now();
 await author.page.getByRole('button',{name:'Releases',exact:true}).click();await author.page.getByRole('button',{name:'Submit release',exact:true}).click();
 await author.page.getByLabel('Release title').fill(title);const submittedResponse=author.page.waitForResponse(r=>r.url().endsWith('/api/v1/releases')&&r.request().method()==='POST');await author.page.getByRole('button',{name:'Prepare and submit',exact:true}).click();
 const submitted=await submittedResponse;assert.ok(submitted.ok(),`Browser submission: ${submitted.status()} ${await submitted.text()}`);
 let release;for(let i=0;i<240;i++){const list=await author.json('GET','/api/v1/releases');release=list.find(r=>r.title===title);if(release){console.log('Release stage',release.jobState,release.stage);if(['FAILED','CANCELLED','SUCCEEDED'].includes(release.jobState))break;}await new Promise(r=>setTimeout(r,2000));}
 assert.ok(release,'Browser submitted a durable release');assert.equal(release.jobState,'SUCCEEDED',JSON.stringify(release));assert.equal(release.candidate.state,'SUBMITTED');
 const id=release.candidate.id,detail=await author.json('GET',`/api/v1/releases/${id}`);assert.equal(detail.validation.passed,true);assert.ok(detail.validation.tests.length>0);assert.ok(detail.semanticChanges.length>0);assert.ok(detail.resourceChanges.length>0);
 const first=await author.call('GET',`/api/v1/releases/${id}/package`),bytes=await first.body();assert.equal(createHash('sha256').update(bytes).digest('hex'),detail.packageDigest);
 const admin=await login('administrator'),reviewer=await login('reviewer',true);
 const decision={decision:'APPROVE',expectedVersion:detail.candidate.version,candidateDigest:detail.candidate.candidateDigest,packageDigest:detail.packageDigest,reason:'Review integration evidence'};
 const self=await admin.call('POST',`/api/v1/releases/${id}/decisions`,decision,{'Idempotency-Key':randomUUID()});assert.equal(self.status(),403);assert.equal((await self.json()).code,'INDEPENDENT_APPROVER_REQUIRED');
 const altered=await reviewer.call('POST',`/api/v1/releases/${id}/decisions`,{...decision,packageDigest:'0'.repeat(64)},{'Idempotency-Key':randomUUID()});assert.equal(altered.status(),409);
 const snapshot=await author.json('POST','/api/v1/snapshots'),graph=await author.json('GET',snapshot.chunks[0].url),hidden=graph.models.flatMap(m=>m.roots).find(r=>r.rootId);
 const rule={principalId:reviewer.identity.principalId,scope:{kind:'ROOT',key:hidden.rootId},permission:'READ'};const prior=(await admin.json('GET','/api/v1/admin/access')).rules.find(r=>r.principalId===rule.principalId&&r.scope.key===hidden.rootId&&r.permission==='READ');restore=()=>admin.json('PUT','/api/v1/admin/access',{...rule,effect:prior?.effect||'INHERIT'});
 await admin.json('PUT','/api/v1/admin/access',{...rule,effect:'DENY'});assert.equal((await reviewer.call('GET',`/api/v1/releases/${id}`)).status(),403);assert.equal((await reviewer.call('GET',`/api/v1/releases/${id}/package`)).status(),403);assert.ok(!(await reviewer.json('GET','/api/v1/releases')).some(r=>r.candidate.id===id));await restore();restore=null;
 await reviewer.page.reload();await reviewer.page.waitForFunction(()=>globalThis.alefExample?.status()==='READY',{},{timeout:120000});
 await reviewer.page.getByRole('button',{name:'Releases',exact:true}).click();const row=reviewer.page.locator('.workflow-card').filter({has:reviewer.page.getByRole('heading',{name:title,exact:true})});await row.getByRole('button',{name:'Inspect release',exact:true}).click();
 const panel=reviewer.page.locator('dialog').filter({has:reviewer.page.getByRole('heading',{name:'Release review',exact:true})});await panel.getByText('Native validation and Testspraak',{exact:true}).waitFor();
 await panel.getByText('Package manifest and pinned dependencies',{exact:true}).click();assert.ok((await panel.textContent()).includes(detail.packageDigest));
 reviewer.page.once('dialog',dialog=>dialog.accept('Reviewed native model and resource diffs, pinned source, dependency closure and passing Testspraak evidence.'));
 await panel.getByRole('button',{name:'Approve exact package',exact:true}).click();
 let approved;for(let i=0;i<90;i++){approved=await reviewer.json('GET',`/api/v1/releases/${id}`);if(approved.candidate.state==='APPROVED')break;await new Promise(r=>setTimeout(r,1000));}
 assert.equal(approved.candidate.state,'APPROVED');assert.equal(approved.packageDigest,detail.packageDigest);assert.equal(approved.candidate.approvalCount,1);
 const retained=await author.call('GET',`/api/v1/releases/${id}/package`);assert.deepEqual(await retained.body(),bytes);
 fs.writeFileSync(path.join(root,'out/backend-approved-release.json'),JSON.stringify({id,packageDigest:detail.packageDigest,sourceRevision:detail.candidate.sourceRevision,title},null,2));
 console.log('PASS: actual browser submission, isolated native validation/Testspraak, immutable full package, dependency retention, whole-package ACL, contributor exclusion, digest-bound independent browser approval and byte-identical download.');
}finally{if(restore)await restore();await browser.close();}
