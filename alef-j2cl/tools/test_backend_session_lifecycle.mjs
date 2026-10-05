import {createRequire} from 'node:module';
import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';import {execFileSync} from 'node:child_process';
const root=path.resolve(import.meta.dirname,'..'),backend=process.env.ALEF_BACKEND_HOME||'C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const {chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const identities=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,''));
const browser=await chromium.launch({headless:true});let realm,providerUser,admin,disabled;
const pause=ms=>new Promise(r=>setTimeout(r,ms));
async function provider(method,url,data){const token=await fetch('http://localhost:8180/realms/master/protocol/openid-connect/token',{method:'POST',body:new URLSearchParams({grant_type:'password',client_id:'admin-cli',username:identities.bootstrapAdmin,password:identities.bootstrapPassword})});assert.ok(token.ok);admin=(await token.json()).access_token;const response=await fetch('http://localhost:8180'+url,{method,headers:{Authorization:'Bearer '+admin,'Content-Type':'application/json'},body:data===undefined?undefined:JSON.stringify(data)});assert.ok(response.ok,`Provider admin ${method}: ${response.status}`);return response.status===204?undefined:response.json();}
async function login(name){const context=await browser.newContext(),page=await context.newPage(),user=identities.users.find(u=>u.username===name);await page.goto('http://localhost:8080/oauth2/authorization/alef');await page.locator('#username').waitFor();await page.waitForLoadState('networkidle');await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);await page.goto('about:blank');const csrf=await(await context.request.get('http://localhost:8080/api/csrf')).json();const call=(method,url,data)=>context.request.fetch('http://localhost:8080'+url,{method,data,headers:{[csrf.headerName]:csrf.token},timeout:120000});const json=async(method,url,data)=>{const response=await call(method,url,data);assert.ok(response.ok(),`${method} ${url}: ${response.status()} ${await response.text()}`);return response.json();};return{context,call,json,identity:await json('GET','/api/session')};}
try{
 const token=await fetch('http://localhost:8180/realms/master/protocol/openid-connect/token',{method:'POST',body:new URLSearchParams({grant_type:'password',client_id:'admin-cli',username:identities.bootstrapAdmin,password:identities.bootstrapPassword})});assert.ok(token.ok);admin=(await token.json()).access_token;
 realm=await provider('GET','/admin/realms/alef');if(realm.accessTokenLifespan===75)realm.accessTokenLifespan=300;
 // Short-lived *real* tokens exercise refresh. Restore the provider immediately
 // afterward; this does not change production clocks or bypass validation.
 await provider('PUT','/admin/realms/alef',{...realm,accessTokenLifespan:75,revokeRefreshToken:true,refreshTokenMaxReuse:0});
 const author=await login('author'),administrator=await login('administrator');
 const client=await author.json('POST','/api/editing-clients');
 const manifest=await author.json('POST','/api/v1/snapshots'),graph=await author.json('GET',manifest.chunks[0].url);
 const selected=graph.models.filter(m=>!m.readOnly).flatMap(m=>m.roots)[0];
 const lease=(await author.json('POST','/api/v1/leases',{editingClientId:client.id,scopes:[{kind:'ROOT',key:selected.rootId}]})).leases[0];
 const restartLog=fs.openSync(path.join(root,'out/backend-session-restart-helper.log'),'w');try{execFileSync('C:/Users/niekh/.cache/codex-runtimes/codex-primary-runtime/dependencies/native/powershell/pwsh.exe',['-NoProfile','-File',path.join(backend,'tools/start-application.ps1'),'-Restart'],{cwd:backend,stdio:['ignore',restartLog,restartLog],timeout:120000,windowsHide:true});}finally{fs.closeSync(restartLog);}
 assert.equal((await author.json('GET','/api/session')).principalId,author.identity.principalId,'Encrypted JDBC login survives API restart');
 await author.json('POST','/api/v1/leases/renew',{editingClientId:client.id,token:lease.token});
 await author.json('POST','/api/v1/leases/release',{editingClientId:client.id,token:lease.token});
 console.log('PASS encrypted PostgreSQL session, CSRF and same-tab lease survive owned application restart');
 await pause(65000);
 const responses=await Promise.all(Array.from({length:12},()=>author.call('GET','/api/session')));
 assert.ok(responses.every(r=>r.status()===200),'Concurrent requests refresh real expiring provider token');
 await pause(65000);assert.equal((await author.call('GET','/api/session')).status(),200,'Rotated token remains usable beyond original token lifetime');
 console.log('PASS real short-lived access token refresh and concurrent requests');
 await administrator.json('POST',`/api/v1/admin/principals/${author.identity.principalId}/state`,{enabled:false,reason:'Session lifecycle integration'});disabled=author.identity.principalId;
 assert.equal((await author.call('GET','/api/session')).status(),401,'Local disable takes effect on next request');
 await administrator.json('POST',`/api/v1/admin/principals/${disabled}/state`,{enabled:true,reason:'Restore session lifecycle fixture'});disabled=undefined;
 console.log('PASS local account revocation invalidates an existing login');
 const editor=await login('editor');providerUser=(await provider('GET','/admin/realms/alef/users?username=editor&exact=true'))[0];assert.ok(providerUser?.id);
 await provider('PUT',`/admin/realms/alef/users/${providerUser.id}`,{...providerUser,enabled:false});
 await pause(61000);assert.equal((await editor.call('GET','/api/session')).status(),401,'Provider disable stops old browser session within freshness bound');
 console.log('PASS actual Keycloak account disable invalidates existing login within 60-second freshness interval');
}finally{
 if(providerUser)await provider('PUT',`/admin/realms/alef/users/${providerUser.id}`,providerUser);
 if(realm)await provider('PUT','/admin/realms/alef',realm);
 if(disabled)throw new Error('Local test account remains disabled; restore using administrator account');
 await browser.close();
}

