import fs from 'node:fs';
import {connect,origin} from './workspace-example-api.mjs';

// Read-only profiling: separate browser, existing package, no model mutations.
const api=await connect(),{page}=api,output='out/package-loading-2026-09-29';
fs.mkdirSync(output,{recursive:true});
const report={runs:[],errors:[]},requests=[];
page.on('pageerror',e=>report.errors.push(String(e)));
page.on('requestfinished',r=>{if(r.url().includes('/api/'))requests.push({url:new URL(r.url()).pathname,timing:r.timing()});});
await page.addInitScript(()=>{
  window.packageProfile=[];
  let facade;
  Object.defineProperty(window,'alefExample',{configurable:true,get:()=>facade,set(value){
    facade=value;
    for(const name of ['prepareComparison','openComparison']){
      const original=value[name];
      value[name]=function(...args){const start=performance.now();try{return original.apply(this,args);}finally{window.packageProfile.push({name,start,duration:performance.now()-start});}};
    }
  }});
});
try{
  const start=Date.now();await page.goto(origin+'/#releases');
  await page.waitForFunction(()=>document.documentElement.dataset.workspaceState==='ready',null,{timeout:120000});
  await page.getByRole('table').getByRole('button',{name:'Open',exact:true}).first().waitFor();
  report.applicationStartupMs=Date.now()-start;
  const packages=await api.request('GET','/api/v1/packages');
  report.packages=packages.map(p=>({id:p.id,version:p.version,revision:p.revision,state:p.state}));
  for(let i=0;i<3;i++){
    if(i){await page.locator('.primary-nav').getByRole('button',{name:'Packaging',exact:true}).click();await page.getByRole('table').waitFor();}
    const offset=requests.length,start=Date.now();
    await page.getByRole('table').getByRole('button',{name:'Open',exact:true}).first().click();
    await page.locator('mps-model-diff[data-state=ready]').waitFor({timeout:180000});
    // Wait for painting after synchronous native editor creation.
    await page.evaluate(()=>new Promise(resolve=>requestAnimationFrame(()=>requestAnimationFrame(resolve))));
    report.runs.push({elapsedMs:Date.now()-start,requests:requests.slice(offset),native:await page.evaluate(()=>window.packageProfile.splice(0))});
    console.log(JSON.stringify(report.runs.at(-1)));
  }
  const id=report.packages[0].id,detail=await api.request('GET','/api/v1/packages/'+id),contents=await api.request('GET','/api/v1/packages/'+id+'/contents');
  report.package={jobState:detail.release?.jobState,hasManifest:!!detail.release?.manifest,roots:contents.roots.length,bytes:Buffer.byteLength(JSON.stringify(contents)),storageRevisions:[...new Set((detail.release?.provenance?.composition?.roots||[]).map(r=>r.storageRevision))]};
  fs.writeFileSync(output+'/'+(process.env.ALEF_PROFILE_LABEL||'before')+'.json',JSON.stringify(report,null,2));
  console.log(JSON.stringify({startupMs:report.applicationStartupMs,package:report.package,errors:report.errors}));
}finally{await api.browser.close();}
