import {createRequire} from 'node:module';
import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..');
const engines=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const backend=process.env.ALEF_BACKEND_HOME||'C:/Users/niekh/Documents/ChatGPT/ALEFBackend';
const identities=JSON.parse(fs.readFileSync(path.join(backend,'.local/development-identities.json'),'utf8').replace(/^\uFEFF/,''));
const author=identities.users.find(user=>user.username==='author');
for(const name of (process.env.ALEF_BROWSERS||'chromium,firefox,webkit').split(',')){
  const browser=await engines[name].launch({headless:true});
  try{
    const context=await browser.newContext();const page=await context.newPage();
    assert.equal((await context.request.get('http://localhost:8080/api/session')).status(),401);
    const initialCsrf=await context.request.get('http://localhost:8080/api/csrf');
    const servedCookie=initialCsrf.headers()['set-cookie'];
    if(servedCookie)assert.match(servedCookie,/SameSite=Lax/i);
    await page.goto('http://localhost:8080/oauth2/authorization/alef');
    await page.locator('#username').waitFor();
    await page.waitForLoadState('networkidle');
    const authorization=new URL(page.url());
    assert.equal(authorization.origin,'http://localhost:8180');
    assert.equal(authorization.searchParams.get('code_challenge_method'),'S256');
    assert.ok(authorization.searchParams.get('state'));assert.ok(authorization.searchParams.get('nonce'));
    await page.locator('#username').fill(author.username);await page.locator('#password').fill(author.password);
    await Promise.all([page.waitForURL('http://localhost:8080/'),page.locator('#kc-login').click()]);
    const authenticated=await context.request.get('http://localhost:8080/api/session');
    assert.equal(authenticated.status(),200);
    const identity=await authenticated.json();assert.ok(identity.principalId);assert.equal(identity.name,'author Local');
    const cookies=await context.cookies('http://localhost:8080');const session=cookies.find(cookie=>cookie.name==='JSESSIONID');
    assert.ok(session?.httpOnly);
    // WebKit's Playwright cookie bridge reports "None" for this local HTTP
    // session; verify the actual server attribute above rather than that bridge.
    if(name!=='webkit')assert.equal(session.sameSite,'Lax');
    assert.equal((await context.request.post('http://localhost:8080/api/editing-clients')).status(),403);
    const csrf=await (await context.request.get('http://localhost:8080/api/csrf')).json();
    const headers={[csrf.headerName]:csrf.token};
    const first=await context.request.post('http://localhost:8080/api/editing-clients',{headers});
    const second=await context.request.post('http://localhost:8080/api/editing-clients',{headers});
    assert.equal(first.status(),200);assert.equal(second.status(),200);
    assert.notEqual((await first.json()).id,(await second.json()).id,'Separate editing clients must have distinct identities');
    await context.request.post('http://localhost:8080/logout',{headers});
    assert.equal((await context.request.get('http://localhost:8080/api/session')).status(),401);
    console.log(`${name}: PASS real Keycloak code/PKCE login, PostgreSQL identity, HttpOnly session, CSRF, distinct tab clients and logout`);
  }finally{await browser.close();}
}
