import fs from 'node:fs';
import assert from 'node:assert/strict';
import {connect,origin} from './workspace-example-api.mjs';

const output='out/package-contents-cache',baseline=JSON.parse(fs.readFileSync(output+'/live-before.json','utf8'));
const label=process.env.ALEF_CACHE_VERIFY_LABEL||'backfill';
const workerLog='C:/Users/niekh/Documents/ChatGPT/ALEFBackend/.local/contents-worker.log';
const size=()=>fs.statSync(workerLog).size,api=await connect(),errors=[];
api.page.on('pageerror',e=>errors.push(String(e)));
try{
  await api.page.goto(origin+'/#releases');
  await api.page.waitForFunction(()=>document.documentElement.dataset.workspaceState==='ready',null,{timeout:120000});
  const workerBytesBefore=size(),reads=[];
  for(let i=0;i<3;i++){
    const start=performance.now();const contents=await api.request('GET','/api/v1/packages/'+baseline.id+'/contents');
    reads.push(Math.round(performance.now()-start));
    assert.equal(contents.revision,baseline.contents.revision,'No package edits occurred during validation');
    assert.deepEqual(contents.afterGraph,baseline.contents.afterGraph,'Exact retained package graph is unchanged');
    assert.deepEqual(contents.roots,baseline.contents.roots,'Root versions, differences and selection are unchanged');
  }
  const started=performance.now();
  await api.page.getByRole('table').getByRole('button',{name:'Open',exact:true}).first().click();
  await api.page.locator('mps-model-diff[data-state=ready]').waitFor({timeout:180000});
  const editorMs=Math.round(performance.now()-started);
  const workerBytesAfter=size();
  if(label==='after-restart')assert.equal(workerBytesAfter,workerBytesBefore,'Persisted contents must not start another native worker after restart');
  assert.deepEqual(errors,[]);
  const result={id:baseline.id,revision:baseline.contents.revision,readsMs:reads,clickToNativeEditorMs:editorMs,workerLogGrew:workerBytesAfter!==workerBytesBefore,unchangedContents:true,errors};
  fs.writeFileSync(output+'/live-'+label+'.json',JSON.stringify(result,null,2));
  await api.page.screenshot({path:output+'/package-'+label+'.png'});
  console.log(JSON.stringify(result));
}finally{await api.browser.close();}
