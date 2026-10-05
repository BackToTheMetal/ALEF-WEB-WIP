import {createRequire} from 'node:module';
import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';

export const root=path.resolve(import.meta.dirname,'..');
export const output=path.join(root,'out/workspace-sportclub-2026-09-29');
export const origin='http://localhost:8080';
export const {chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
export async function connect(username='author'){
  fs.mkdirSync(output,{recursive:true});
  const users=JSON.parse(fs.readFileSync('C:/Users/niekh/Documents/ChatGPT/ALEFBackend/.local/development-identities.json','utf8').replace(/^\uFEFF/,''));
  const user=users.users.find(u=>u.username===username);
  const browser=await chromium.launch({headless:true}),context=await browser.newContext({viewport:{width:1565,height:1100}}),page=await context.newPage();
  await page.goto(origin+'/oauth2/authorization/alef');
  await page.locator('#username').fill(user.username);await page.locator('#password').fill(user.password);
  await Promise.all([page.waitForURL(origin+'/**'),page.locator('#kc-login').click()]);
  await page.goto('about:blank');
  const csrf=await(await context.request.get(origin+'/api/csrf')).json();
  const request=async(method,url,data,headers={})=>{
    const response=await context.request.fetch(origin+url,{method,data,headers:{[csrf.headerName]:csrf.token,...headers},timeout:180000});
    assert.ok(response.ok(),`${method} ${url}: ${response.status()} ${await response.text()}`);
    return response.status()===204?null:response.json();
  };
  const snapshot=async()=>{const manifest=await request('POST','/api/v1/snapshots');assert.equal(manifest.chunks.length,1);const graph=await request('GET',manifest.chunks[0].url);return{manifest,graph};};
  return {browser,context,page,request,snapshot};
}
if(process.argv[2]==='inspect'){
  const api=await connect();
  try{
    const current=await api.snapshot();
    const file=path.join(output,'before.json');
    if(!fs.existsSync(file))fs.writeFileSync(file,JSON.stringify(current,null,2));
    const packages=await api.request('GET','/api/v1/packages');
    if(!fs.existsSync(path.join(output,'packages-before.json')))fs.writeFileSync(path.join(output,'packages-before.json'),JSON.stringify(packages,null,2));
    console.log(JSON.stringify({revision:current.graph.revision,models:current.graph.models.map(m=>({name:m.name,reference:m.reference,moduleId:m.moduleId,modelId:m.modelId,readOnly:m.readOnly,roots:m.roots.map(r=>({rootId:r.rootId,id:r.id,concept:r.conceptName,properties:r.properties,editable:r.editable}))})),packages:packages.map(p=>({id:p.id,version:p.version,title:p.title,revision:p.revision,finalized:p.finalized}))},null,2));
  }finally{await api.browser.close();}
}
