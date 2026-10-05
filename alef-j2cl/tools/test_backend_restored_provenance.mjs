import {createRequire} from 'node:module';import fs from 'node:fs';import path from 'node:path';import {createHash} from 'node:crypto';import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..'),backend=process.env.ALEF_BACKEND_HOME||'C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const {chromium}=createRequire(process.env.ALEF_PLAYWRIGHT_PACKAGE||path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const user=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,'')).users.find(u=>u.username==='reviewer');
const expected=JSON.parse(fs.readFileSync(path.join(root,'out/backend-approved-release.json'),'utf8'));
const browser=await chromium.launch({headless:true}),context=await browser.newContext(),page=await context.newPage();
try{
 await page.goto('http://localhost:8080/oauth2/authorization/alef');await page.locator('#username').waitFor();await page.waitForLoadState('networkidle');await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);
 await page.waitForFunction(()=>window.alefExample?.status()==='READY',null,{timeout:120000});
 const get=async url=>{const response=await context.request.get('http://localhost:8080'+url);assert.equal(response.status(),200,url);return response;};
 const release=await(await get('/api/v1/releases/'+expected.id)).json();assert.equal(release.packageDigest,expected.packageDigest);assert.equal(release.provenance.sourceRevision,expected.sourceRevision);assert.equal(release.candidate.state,'APPROVED');
 const archive=await(await get('/api/v1/releases/'+expected.id+'/package')).body();assert.equal(createHash('sha256').update(archive).digest('hex'),expected.packageDigest);
 const deployments=await(await get('/api/v1/deployments')).json();assert.ok(deployments.some(d=>d.candidateId===expected.id&&d.observedState==='DEPLOYED'),'Retained successful simulated deployment history is visible');
 await page.getByRole('button',{name:'Releases',exact:true}).click();await page.getByText(expected.title,{exact:true}).first().waitFor();
 console.log('PASS restored browser review, original approved package bytes, source revision and independent deployment history');
 if(process.env.ALEF_VERIFY_DEPENDENCIES==='1'){
  for(const dependency of release.manifest.dependencies){
   const descriptor=await(await get(`/api/v1/releases/${expected.id}/dependencies/${dependency.sha256}`)).json(),hash=createHash('sha256');let bytes=0;
   for(let ordinal=0;ordinal<descriptor.chunks;ordinal++){
    const response=await get(descriptor.chunkUrlTemplate.replace('{ordinal}',ordinal)),chunk=await response.body();assert.equal(createHash('sha256').update(chunk).digest('hex'),response.headers()['x-chunk-sha256']);hash.update(chunk);bytes+=chunk.length;await response.dispose();
   }
   assert.equal(bytes,descriptor.bytes);assert.equal(hash.digest('hex'),dependency.sha256);console.log(`PASS restored retained dependency: ${bytes} bytes, ${descriptor.chunks} individually authorized chunks, exact package-bound SHA-256`);
  }
 }
}finally{await page.evaluate(()=>window.alefBackend?.closeClient()).catch(()=>{});await browser.close();}
