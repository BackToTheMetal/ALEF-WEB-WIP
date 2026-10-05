import {createRequire} from 'node:module';
import fs from 'node:fs';
import http from 'node:http';
import path from 'node:path';
import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..');
const engines=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const output=path.join(root,'out/editor-refresh');
const server=http.createServer((req,res)=>{
  if(req.url==='/app.js'){
    res.setHeader('Content-Type','text/javascript');
    fs.createReadStream(process.env.ALEF_TEST_BUNDLE||path.join(output,'contracts.js')).pipe(res);
  }else{
    res.setHeader('Content-Type','text/html');
    res.end('<!doctype html><html><body><div id="editors"></div><script src="/app.js"></script></body></html>');
  }
});
await new Promise(r=>server.listen(0,'127.0.0.1',r));
const results=[];
try{
 for(const engine of (process.env.ALEF_BROWSERS||'chromium,firefox,webkit').split(',')){
  const browser=await engines[engine].launch({headless:true});
  const page=await browser.newPage({viewport:{width:1280,height:800}}),errors=[],logs=[];
  page.on('pageerror',e=>errors.push(String(e)));
  page.on('console',m=>{if(m.type()==='error')logs.push(m.text());});
  try{
   await page.addInitScript(()=>{
    window.paints=[];
    const original=CanvasRenderingContext2D.prototype.fillText;
    CanvasRenderingContext2D.prototype.fillText=function(text,x,y,...args){
     const t=this.getTransform();
     window.paints.push({text,canvas:this.canvas,x:t.a*x+t.e,y:t.d*y+t.f,width:this.measureText(text).width*t.a});
     if(window.paints.length>5000)window.paints.splice(0,2500);
     return original.call(this,text,x,y,...args);
    };
   });
   await page.goto(`http://127.0.0.1:${server.address().port}/`);
   await page.waitForFunction(()=>window.editorUpdates?.status()==='READY'||window.editorUpdates?.status().startsWith('FAIL'),null,{timeout:120000});
   assert.equal(await page.evaluate(()=>editorUpdates.status()),'READY');
   async function visible(value){
    await page.waitForFunction(value=>[0,1].every(i=>editorUpdates.text(i).includes(value))&&
     new Set(paints.filter(p=>p.canvas.isConnected&&p.text===value).map(p=>p.canvas)).size===2,value,{timeout:5000});
   }
   await visible('Initial model');
   const directText='Fresh direct result with a long removable tail';
   const modes=['write','command','direct'];
   for(const mode of modes){
    const value=mode==='direct'?directText:`Fresh ${mode} result`;
    await page.evaluate(({mode,value})=>{window.paints=[];editorUpdates.change(mode,value);},{mode,value});
    await visible(value);
   }
   // Removal must erase pixels as well as removing cells. Retain the last label
   // rectangle and inspect the canvas, without causing a repaint in the test.
   // Inspect the tail beyond the new "node is not inside a model" placeholder.
   await page.evaluate(value=>window.oldLabels=[...new Map(paints.filter(p=>p.canvas.isConnected&&p.text===value).map(p=>[p.canvas,p])).values()],directText);
   assert.ok(await page.evaluate(()=>oldLabels.length===2&&oldLabels.every(p=>{
    const data=p.canvas.getContext('2d').getImageData(Math.ceil(p.x+p.width)-40,Math.max(0,Math.floor(p.y-12)),40,12).data;
    for(let i=0;i<data.length;i+=4)if(data[i+3]>0&&data[i]<180&&data[i+1]<180&&data[i+2]<180)return true;
    return false;
   })),'The removed-label pixel region must initially contain text');
   await page.evaluate(()=>{window.paints=[];editorUpdates.change('remove','');});
   await page.waitForFunction(()=>[0,1].every(i=>!editorUpdates.text(i).includes('Fresh direct result')));
   await page.waitForFunction(()=>oldLabels.every(p=>{
    const x=Math.ceil(p.x+p.width)-40,y=Math.max(0,Math.floor(p.y-12));
    const data=p.canvas.getContext('2d').getImageData(x,y,40,12).data;
    for(let i=0;i<data.length;i+=4)if(data[i+3]>0&&data[i]<180&&data[i+1]<180&&data[i+2]<180)return false;
    return true;
   }),null,{timeout:5000});
   await page.evaluate(()=>{window.paints=[];editorUpdates.change('add','Restored model');});
   await visible('Restored model');
   assert.deepEqual(errors,[]);
   await page.screenshot({path:path.join(output,`${engine}-updates.png`)});
   results.push({browser:engine,result:'PASS',checks:['write action','command','direct rebuild','removed node pixels','restored node','two dependent editors'],errors});
  }catch(e){await page.screenshot({path:path.join(output,`${engine}-failure.png`)});console.error({errors,logs});throw e;}
  finally{await browser.close();}
 }
 fs.writeFileSync(path.join(output,'contracts-results.json'),JSON.stringify(results,null,2));
 console.log(JSON.stringify(results));
}finally{server.close();}
