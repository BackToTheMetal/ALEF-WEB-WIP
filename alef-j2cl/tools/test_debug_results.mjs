import {createRequire} from 'node:module';
import {createHash,randomUUID} from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
// Real generated editors and project tree; every API request uses isolated fixtures.
const root=path.resolve(import.meta.dirname,'..');
const engines=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const graph=JSON.parse(fs.readFileSync(path.join(root,'docs/examples/sportclub/workspace.json')));
for(const m of graph.models)for(const r of m.roots){r.rootId=randomUUID();r.version=randomUUID();r.editable=true;}
// The real backend includes these five accessory models. Omitting them masked
// a collision with locally installed standard models during remote startup.
graph.models.push(...JSON.parse(fs.readFileSync(path.join(root,'tools/fixtures/alef-standard-models.json'))).models);
const walk=n=>{if(n.id==='900029000000000025')for(const p in n.properties)n.properties[p]='80'; for(const c of n.children)walk(c.node);};graph.models.forEach(m=>m.roots.forEach(walk));
let text=JSON.stringify(graph);
const roots=graph.models.filter(m=>!m.readOnly).flatMap(m=>m.roots);
const engine=process.env.ALEF_TEST_BROWSER||'chromium';
const output=path.join(root,'out/test-debug',engine);fs.mkdirSync(output,{recursive:true});
const browser=await engines[engine].launch({headless:true});
const page=await browser.newPage({viewport:{width:1440,height:1360}}),errors=[];
page.on('pageerror',e=>errors.push(String(e)));
await page.addInitScript(()=>{
 window.testPaints=[];
 const draw=CanvasRenderingContext2D.prototype.fillText;
 CanvasRenderingContext2D.prototype.fillText=function(text,x,y,...args){
  const m=this.getTransform();window.testPaints.push({text,canvas:this.canvas,x:m.a*x+m.e,y:m.d*y+m.f,time:performance.now()});
  if(window.testPaints.length>10000)window.testPaints.splice(0,5000);
  return draw.call(this,text,x,y,...args);
 };
 // Retain the latest complete test-editor paint, not superseded frames painted
 // between mouse-down and the queued update. Inspectors use separate canvases.
 window.currentTestPaints=()=>{
  const starts=new Map();
  window.testPaints.forEach((p,i)=>{if(p.text==='Servicetestset')starts.set(p.canvas,i);});
  return window.testPaints.filter((p,i)=>p.canvas.isConnected&&i>=(starts.get(p.canvas)||0));
 };
 window.EventSource=class extends EventTarget{static CLOSED=2;readyState=1;close(){this.readyState=2;}};
});
if(process.env.ALEF_TEST_BUNDLE)await page.route('**/app.js',r=>r.fulfill({path:path.resolve(process.env.ALEF_TEST_BUNDLE)}));
await page.route('**/backend-mode.js',r=>r.fulfill({contentType:'text/javascript',body:'globalThis.alefBackendMode=true;'}));
await page.route('**/api/**',async route=>{
 const req=route.request(),pathname=new URL(req.url()).pathname;
 const reply=(data,status=200)=>route.fulfill({status,contentType:'application/json',body:JSON.stringify(data)});
 if(pathname==='/api/csrf')return reply({headerName:'X-CSRF',token:'fixture'});
 if(pathname==='/api/session')return reply({name:'Test runner',workspacePermissions:['EDIT','READ']});
 if(pathname==='/api/editing-clients')return reply({id:'test-client'});
 if(pathname==='/api/editing-clients/close')return reply({});
 if(pathname==='/api/v1/leases')return reply({leases:req.postDataJSON().scopes.map(scope=>({token:{scope,leaseId:randomUUID(),generation:1},expiresAt:new Date(Date.now()+120000).toISOString()}))});
 if(pathname==='/api/v1/leases/renew')return reply({token:req.postDataJSON().token,expiresAt:new Date(Date.now()+120000).toISOString()});
 if(pathname==='/api/v1/workspace')return reply({ready:true});
 if(pathname==='/api/v1/packages/editor-status')return reply({roots:{}});
 if(pathname==='/api/v1/snapshots')return reply({snapshotId:'fixture',revision:'revision-1',policyEpoch:1,languageFingerprint:'fixture',eventCursor:'cursor',counts:{roots:roots.length,nodes:0},chunks:[{url:'/api/v1/snapshots/fixture/chunks/graph',bytes:Buffer.byteLength(text),sha256:createHash('sha256').update(text).digest('hex')}]});
 if(pathname.includes('/chunks/'))return route.fulfill({contentType:'application/json',body:text});
 throw new Error('Unexpected request: '+pathname);
});


// Click actual generated MPS run controls, not the standalone runTest bridge.
async function runControl(symbol, occurrence=0){
 const p=await page.evaluate(({symbol,occurrence})=>{
  const candidates=window.currentTestPaints().filter(p=>p.text===symbol);
  const unique=[...new Map(candidates.map(p=>[p.x+':'+p.y,p])).values()].sort((a,b)=>a.y-b.y);
  const p=unique[occurrence];if(!p)throw new Error('Missing run control: '+symbol);
  const b=p.canvas.getBoundingClientRect();return {x:b.x+p.x*b.width/p.canvas.width+4,y:b.y+p.y*b.height/p.canvas.height-4};
 },{symbol,occurrence});
 await page.evaluate(()=>window.testPaints=[]);
 await page.mouse.click(p.x,p.y);
 await page.evaluate(()=>new Promise(r=>requestAnimationFrame(()=>requestAnimationFrame(r))));
 return p;
}
async function painted(){return page.evaluate(()=>window.currentTestPaints().map(p=>p.text));}
async function currentPaint(){
 // Observe only. A resize or explicit repaint masks missed update notifications.
 // Each action clears the paint log before it runs.
 await page.waitForTimeout(200);
 return (await painted()).join(' ');
}
async function assertFailedValueIndicator(expected,actual,label){
 // Inspect the actual pixels between expected and actual, before any click in
 // the output. Text draw logs alone can pass while stale/clipped pixels remain.
 const result=await page.evaluate(({expected,actual})=>{
  const paints=window.currentTestPaints();
  const value=paints.findLast(p=>p.text===expected);
  if(!value)return {error:'Expected value was not repainted after scrolling'};
  const result=paints.findLast(p=>p.canvas===value.canvas&&p.text===actual&&p.x>value.x&&Math.abs(p.y-value.y)<3);
  if(!result)return {error:'Actual value was not repainted after scrolling'};
  const cross=paints.findLast(p=>p.canvas===value.canvas&&p.text==='✘'&&p.x>value.x&&p.x<result.x&&Math.abs(p.y-value.y)<4);
  if(!cross)return {error:'Failure indicator was not repainted after scrolling'};
  const canvas=value.canvas,bounds=canvas.getBoundingClientRect();
  const x=Math.floor(cross.x),y=Math.floor(cross.y-22),width=Math.ceil(result.x-x),height=26;
  const box={left:bounds.x+x*bounds.width/canvas.width,top:bounds.y+y*bounds.height/canvas.height,
   right:bounds.x+(x+width)*bounds.width/canvas.width,bottom:bounds.y+(y+height)*bounds.height/canvas.height};
  for(let ancestor=canvas.parentElement;ancestor;ancestor=ancestor.parentElement){
   const style=getComputedStyle(ancestor),rect=ancestor.getBoundingClientRect();
   if(style.overflowY!=='visible'&&(box.top<rect.top-1||box.bottom>rect.bottom+1))return {error:'Indicator is vertically clipped'};
   if(style.overflowX!=='visible'&&(box.left<rect.left-1||box.right>rect.right+1))return {error:'Indicator is horizontally clipped'};
  }
  const data=canvas.getContext('2d').getImageData(x,y,width,height).data;
  let redPixels=0;
  for(let i=0;i<data.length;i+=4)if(data[i]>130&&data[i+1]<90&&data[i+2]<100&&data[i+3]>100)redPixels++;
  return {redPixels,box};
 },{expected,actual});
 fs.writeFileSync(path.join(output,label+'.json'),JSON.stringify(result,null,2));
 await page.screenshot({path:path.join(output,label+'.png')});
 assert.ok(result.redPixels>10,`Visible red cross without clicking the result: ${JSON.stringify(result)}`);
}
const findNode=(node,id)=>node.id===id?node:node.children.map(c=>findNode(c.node,id)).find(Boolean);
const testRoot=roots.find(r=>r.conceptName.endsWith('.ServiceTestSet'));
async function editValue(oldValue,value,id){
 const point=await runControl(oldValue);
 await page.mouse.dblclick(point.x,point.y);await page.keyboard.type(value);await page.keyboard.press('Tab');
 await page.waitForFunction(({rootId,id,value})=>{
  const find=n=>n.id===id?n:n.children.map(c=>find(c.node)).find(Boolean);
  return Object.values(find(window.alefExample.rootSnapshot(rootId)).properties).includes(value);
 },{rootId:testRoot.rootId,id,value},{timeout:5000});
}
async function snapshots(){return page.evaluate(ids=>ids.map(id=>window.alefExample.rootSnapshot(id)),roots.map(r=>r.rootId));}
async function ready(){
 await page.waitForFunction(()=>document.documentElement.dataset.workspaceState==='ready'||window.alefExample?.status().startsWith('FAIL:'),null,{timeout:120000});
 assert.equal(await page.evaluate(()=>window.alefExample.status()),'READY');
 await page.locator('mps-project-view').getByRole('treeitem',{name:'Contributie controleren',exact:true}).click();
 await page.waitForFunction(()=>window.testPaints.some(p=>p.canvas.isConnected&&p.text==='Servicetestset'));
}
try{
 await page.goto((process.env.ALEF_EXAMPLE_URL||'http://localhost:8080/')+'#model');
 await ready();
 // Refresh reuses the browser runtime and must rebuild the authorized models
 // without registering a second standard module or retaining old nodes.
 await page.evaluate(text=>window.alefExample.refreshRemote(text),text);
 await page.waitForFunction(()=>window.alefExample.status()==='READY'||window.alefExample.status().startsWith('FAIL:'),null,{timeout:120000});
 await ready();
 await currentPaint();
 const before=await snapshots();
 await runControl('▶');
 const individual=await painted();fs.writeFileSync(path.join(output,'individual.json'),JSON.stringify(individual,null,2));
 await page.screenshot({path:path.join(output,'individual.png')});
 assert.ok(individual.some(t=>t.includes('1 gefaald')),'Individual failure must run');
 assert.match(individual.join(' '),/80\s+\u2718\s+75(?:\.0+)?\b/,'The actual computed value must be visible next to expected 80');
 assert.ok(!individual.includes('references:'),'Debug nodes must use the generated trace editor, not the fallback editor');
 assert.ok(individual.includes('Jeugdcontributie'),'The executed rule must be visible in the native debug trace');
 assert.match(individual.join(' '),/Deelnemer\s+\.\s+jaarcontributie\s+:=\s+75\b/,'The trace must show the value assigned by the rule');
 assert.deepEqual(await snapshots(),before,'Debugging must leave workspace nodes unchanged');
 assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),[],'Debugging must not dirty the workspace');
 await runControl(' ? '); // Follow the failed value to the service output mapping.
 await page.waitForFunction(()=>document.querySelector('mps-project-view')?.shadowRoot
   ?.querySelector('[role="treeitem"][aria-selected="true"]')?.textContent.includes('Contributieservice'),null,{timeout:5000});
 assert.deepEqual(errors,[],'Generated debug navigation must use the browser editor');
 await page.locator('mps-project-view').getByRole('treeitem',{name:'Contributie controleren',exact:true}).click();
 await currentPaint();
 await runControl('⏩'); // Clear individual results.
 await runControl('⏩'); // Run all and open first failure.
 const batch=await painted();fs.writeFileSync(path.join(output,'batch.json'),JSON.stringify(batch,null,2));
 await page.screenshot({path:path.join(output,'batch.png')});
 assert.ok(batch.some(t=>t.includes('4 geslaagd, 1 gefaald')),'Batch must keep every test result');
 assert.match(batch.join(' '),/80\s+\u2718\s+75(?:\.0+)?\b/,'Batch failure must include actual values');
 assert.ok(!batch.includes('references:'),'Batch trace must render with its generated editor');
 assert.ok(batch.includes('Jeugdcontributie'),'Batch must open the native rule trace for its first failure');
 assert.deepEqual(await snapshots(),before,'Batch debugging must leave workspace nodes unchanged');
 assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),[]);
 // Correct the open test and rerun without a page/workspace reload. A held
 // lease exposes accidental writes previously hidden by the read-only guard.
 await page.getByRole('button',{name:'Lock root',exact:true}).click();
 await page.getByRole('button',{name:'Release lock',exact:true}).waitFor();
 await currentPaint();
 // Edit with the failed result and its debug nodes still on screen, matching
 // the reported workflow. No artificial resize or repaint may help the rerun.
 await editValue('80','75','900029000000000025');
 const editedFailure=await snapshots();
 await runControl('✘'); // The batch result replaces its run icon.
 assert.doesNotMatch(await currentPaint(),/Jeugdcontributie|Uitvoerbericht mappings/,'Clearing tests removes the old trace');
 assert.deepEqual(await snapshots(),editedFailure,'Clearing debug state must not change workspace nodes');
 assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),[testRoot.rootId]);
 await runControl('▶');
 assert.match(await currentPaint(),/1 geslaagd, 0 gefaald/,'Corrected test passes without reloading');
 // Change the input so another rule executes. Old rule/value nodes must vanish.
 await editValue('10','35','900029000000000021');
 await runControl('✔');await runControl('▶');
 const changedRule=await currentPaint();
 assert.match(changedRule,/Volwassenencontributie/);
 assert.doesNotMatch(changedRule,/Jeugdcontributie/,'Do not retain the previous rule trace');
 assert.match(changedRule,/75\s+✘\s+125/,'The rerun displays the new actual value');
 await editValue('75','125','900029000000000025');
 await runControl('✘');await runControl('▶');
 const corrected=await currentPaint();
 assert.match(corrected,/1 geslaagd, 0 gefaald/);
 assert.doesNotMatch(corrected,/Jeugdcontributie|✘/,'The previous failure cells are removed');
 await runControl('⏩');await runControl('⏩');
 const cleanBatch=await currentPaint();
 assert.match(cleanBatch,/5 geslaagd, 0 gefaald/);
 assert.doesNotMatch(cleanBatch,/Jeugdcontributie|Volwassenencontributie|Uitvoerbericht mappings|✘/,'A passing batch retains no previous failure trace');
 const expectedDraft=structuredClone(before);
 for(const [id,value] of [['900029000000000021','35'],['900029000000000025','125']]){
  const node=expectedDraft.map(r=>findNode(r,id)).find(Boolean);
  for(const key in node.properties)node.properties[key]=value;
 }
 assert.deepEqual(await snapshots(),expectedDraft,'Only the explicit input/expectation edits change workspace nodes');
 assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),[testRoot.rootId]);
 await page.screenshot({path:path.join(output,'corrected-rerun.png')});
 const correctExpected=n=>{if(n.id==='900029000000000025')for(const p in n.properties)n.properties[p]='75';for(const c of n.children)correctExpected(c.node);};
 roots.forEach(correctExpected);text=JSON.stringify(graph);
 page.once('dialog',dialog=>dialog.accept()); // Discard this isolated fixture's unsaved edits.
 await page.reload();await ready();
 const passingBefore=await snapshots();
 await runControl('⏩');
 assert.ok((await painted()).some(t=>t.includes('5 geslaagd, 0 gefaald')),'Valid tests must pass using the real standard runtime models');
 assert.deepEqual(await snapshots(),passingBefore);
 assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),[]);
 // A runtime exception must render its actual message, not an editor-definition
 // node such as CellModel_Constant. Reload keeps the scenarios independent.
 const badInput=n=>{if(n.id==='900029000000000021')for(const p in n.properties)n.properties[p]='not-a-number';for(const c of n.children)badInput(c.node);};
 roots.forEach(badInput);text=JSON.stringify(graph);
 await page.reload();await ready();
 const errorBefore=await snapshots();
 await runControl('▶');
 const runtimeError=await painted();
 assert.ok(runtimeError.some(t=>t.includes('For input string:')),'Runtime error details must be readable');
 assert.ok(!runtimeError.includes('CellModel_Constant'),'Do not show editor-definition nodes as error messages');
 await page.screenshot({path:path.join(output,'runtime-error.png')});
 assert.deepEqual(await snapshots(),errorBefore);
 assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),[]);
 await page.getByRole('button',{name:'Lock root',exact:true}).click();
 await page.getByRole('button',{name:'Release lock',exact:true}).waitFor();
 await currentPaint();
 await editValue('not-a-number','10','900029000000000021');
 await runControl('✘');
 assert.doesNotMatch(await currentPaint(),/For input string:/,'Clearing removes the old runtime error immediately');
 await runControl('▶');
 const recovered=await currentPaint();
 assert.match(recovered,/1 geslaagd, 0 gefaald/);
 assert.doesNotMatch(recovered,/For input string:|CellModel_Constant|✘/,'Correcting and rerunning removes the old runtime-error nodes');
 const recoveredDraft=structuredClone(errorBefore);
 const recoveredInput=recoveredDraft.map(r=>findNode(r,'900029000000000021')).find(Boolean);
 for(const key in recoveredInput.properties)recoveredInput.properties[key]='10';
 assert.deepEqual(await snapshots(),recoveredDraft);
 assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),[testRoot.rootId]);
 // Start with a short editor: the output is below the viewport when the test
 // finishes. Native wheel scrolling must repaint it, not reveal the old frame.
 const failedExpected=findNode(testRoot,'900029000000000025');
 for(const key in failedExpected.properties)failedExpected.properties[key]='175';
 const validInput=findNode(testRoot,'900029000000000021');
 for(const key in validInput.properties)validInput.properties[key]='10';
 text=JSON.stringify(graph);
 page.once('dialog',dialog=>dialog.accept()); // Discard only this isolated fixture.
 await page.setViewportSize({width:1168,height:764});
 await page.reload();await ready();await currentPaint();
 const beforeScrollTest=await snapshots();
 await runControl('▶');await currentPaint();
 const viewport=await page.evaluate(()=>{
  const p=window.currentTestPaints().find(p=>p.text==='Servicetestset');
  for(let e=p.canvas.parentElement;e;e=e.parentElement)if(getComputedStyle(e).overflowY==='auto'){
   e.setAttribute('data-test-result-viewport','');const b=e.getBoundingClientRect();
   return {x:b.x+b.width*.7,y:b.y+b.height*.8};
  }
  throw new Error('Editor viewport missing');
 });
 await page.mouse.move(viewport.x,viewport.y);
 await page.mouse.wheel(0,440);
 await page.waitForFunction(()=>document.querySelector('[data-test-result-viewport]').scrollTop>=400);
 await currentPaint();
 await assertFailedValueIndicator('175','75','failure-after-wheel-no-click');
 // The native scrollbar uses JViewport.setViewPosition rather than the wheel
 // callback. Verify that path after another run with the output off screen.
 await page.mouse.wheel(0,-1000);
 await page.waitForFunction(()=>document.querySelector('[data-test-result-viewport]').scrollTop===0);
 await currentPaint();await runControl('✘');await runControl('▶');await currentPaint();
 const bar=page.locator('[data-test-result-viewport]').locator('..')
  .locator('[role=scrollbar][aria-orientation=vertical]');
 const track=await bar.boundingBox();
 await page.mouse.move(track.x+track.width/2,track.y+30);await page.mouse.down();
 // Move the real scrollbar thumb; stop when this result is in the viewport.
 for(let offset=5;offset<=180;offset+=5){
  await page.mouse.move(track.x+track.width/2,track.y+30+offset);
  if(Number(await bar.getAttribute('aria-valuenow'))>=350)break;
 }
 await page.mouse.up();
 await page.waitForFunction(()=>document.querySelector('[data-test-result-viewport]').scrollTop>=350,null,{timeout:5000});
 await currentPaint();
 await assertFailedValueIndicator('175','75','failure-after-scrollbar-no-click');
 assert.deepEqual(await snapshots(),beforeScrollTest,'Scrolling and test execution do not modify workspace nodes');
 assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),[]);
 assert.deepEqual(errors,[]);
 console.log('PASS: debug details, corrected reruns, fresh traces, passing-batch cleanup, red failure pixels after scrolling without a click, and no unintended workspace writes');
}catch(error){
 await page.screenshot({path:path.join(output,'failure.png')});
 console.error(JSON.stringify({status:await page.evaluate(()=>window.alefExample?.status()),errors}));
 throw error;
}finally{await browser.close();}
