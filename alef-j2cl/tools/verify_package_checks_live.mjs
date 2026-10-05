import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';import {connect,origin,root} from './workspace-example-api.mjs';
const id='1569f740-79af-45b1-bf19-5efc666385b4',out=path.join(root,'out/package-checks');fs.mkdirSync(out,{recursive:true});
const api=await connect();
try{
 const before=await api.request('GET','/api/v1/packages/'+id);fs.writeFileSync(path.join(out,'live-before.json'),JSON.stringify(before,null,2));
 const errors=[];api.page.on('pageerror',e=>errors.push(String(e)));
 await api.page.goto(origin+'/#release/'+id);await api.page.getByRole('tab',{name:'Checks',exact:true}).waitFor({timeout:90000});
 await api.page.getByRole('tab',{name:'Checks',exact:true}).click();
 const comparison=await api.page.locator('mps-comparison-session').elementHandle();
 if(process.argv.includes('--run')){
   assert.equal(before.finalized,false);assert.ok(!['RUNNING','QUEUED'].includes(before.release.jobState));
   await api.page.getByRole('button',{name:'Run checks again',exact:true}).click();
   await api.page.getByRole('button',{name:'Running checks…',exact:true}).waitFor();
   assert.equal(await comparison.evaluate(el=>el.isConnected),true,'Retry keeps native editors mounted');
 }
 let p;
 for(let i=0;i<240;i++){
   p=await api.request('GET','/api/v1/packages/'+id);
   if(!['RUNNING','QUEUED'].includes(p.release.jobState))break;
   if(i%15===0)console.log('Checks: '+p.release.stage);
   await new Promise(r=>setTimeout(r,1000));
 }
 fs.writeFileSync(path.join(out,'live-after.json'),JSON.stringify(p,null,2));
 assert.ok(p.release.validation,'Checks produced a validation report: '+p.release.error);
 assert.deepEqual(p.release.provenance.composition,before.release.provenance.composition,'Selected package contents retained');
 assert.deepEqual(p.reviewers.map(r=>r.id),before.reviewers.map(r=>r.id),'Assigned reviewers retained');
 assert.equal(p.approvalCount,0);assert.equal(p.finalized,false);
 await api.page.locator('.test-result').first().waitFor({timeout:15000});assert.equal(await api.page.locator('.test-result').count(),p.release.validation.tests.length);
 assert.equal(await api.page.getByRole('tab',{name:'Checks',exact:true}).getAttribute('aria-selected'),'true');assert.equal(await comparison.evaluate(el=>el.isConnected),true);
 assert.deepEqual(errors,[]);await api.page.screenshot({path:path.join(out,'live-report.png'),fullPage:true});
 console.log(JSON.stringify({revision:p.revision,state:p.state,passed:p.release.validation.passed,tests:p.release.validation.tests.map(t=>({name:t.name,status:t.status})),sourceUnchanged:p.release.validation.sourceUnchanged,editorPreserved:true},null,2));
}finally{await api.browser.close();}
