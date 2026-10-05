import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
import {connect,origin,root} from './workspace-example-api.mjs';

const output=path.join(root,'out/diff-whitespace');fs.mkdirSync(output,{recursive:true});
const api=await connect('author'),page=api.page,baseline=process.argv.includes('--baseline'),errors=[],writes=[];
page.setDefaultTimeout(60000);
if(process.env.ALEF_TEST_BUNDLE)await page.route('**/app.js',route=>route.fulfill({contentType:'application/javascript',path:process.env.ALEF_TEST_BUNDLE}));
page.on('pageerror',error=>errors.push(String(error)));
page.on('request',request=>{if(request.method()!=='GET'&&/\/api\/v1\/(changes|packages|leases)/.test(request.url()))writes.push(request.url());});
const visible=states=>{
 for(const state of Object.values(states)){
  assert.equal(state.readOnly,true);
  for(const span of state.highlights){assert.ok(span.ranges.length);for(const [start,end] of span.ranges){assert.ok(end>start);assert.doesNotMatch(span.text.slice(start,end),/\s/u,'Spaces must stay uncoloured: '+JSON.stringify(span));}}
 }
};
try{
 const packages=await api.request('GET','/api/v1/packages'),pkg=packages.find(p=>p.version==='1.8.1');assert.ok(pkg);
 const data=await api.request('GET','/api/v1/packages/'+pkg.id+'/contents');
 await page.goto(origin+'/#release/'+pkg.id);await page.locator('mps-model-diff[data-state=ready]').waitFor();
 await page.locator('.comparison-tree [data-kind=root]').filter({hasText:'Contributieregels'}).click();await page.locator('mps-model-diff[data-state=ready]').waitFor();
 let states=await page.locator('mps-model-diff').evaluate(el=>el.states);
 const blank=Object.values(states).flatMap(s=>s.highlights).filter(s=>!s.text.trim());
 console.log('Live rule comparison:',JSON.stringify({highlightedCells:Object.values(states).reduce((n,s)=>n+s.highlights.length,0),blankCells:blank.length}));
 await page.screenshot({path:path.join(output,baseline?'rules-before.png':'rules-after.png'),fullPage:true});
 if(!baseline){
  visible(states);assert.equal(blank.length,0);
  // Exercise both colours using copies of saved data; never save these fixtures.
  const fixture=structuredClone(data),item=structuredClone(data.roots.find(r=>r.after&&r.name==='Sportclub'));assert.ok(item);
  fixture.roots=[item];fixture.beforeGraph=structuredClone(fixture.afterGraph);item.before=structuredClone(item.after);item.beforeReference=item.afterReference;item.changed=true;item.kind='Modified';
  const property=Object.keys(item.after.properties).find(key=>item.after.properties[key]===item.name);assert.ok(property);
  const sync=()=>{for(const side of ['before','after']){const model=fixture[side+'Graph'].models.find(m=>item[side+'Reference']===m.reference+'/'+item[side].id);assert.ok(model);model.roots[model.roots.findIndex(n=>n.id===item[side].id)]=structuredClone(item[side]);}};
  const refresh=async()=>{await page.locator('.primary-nav').getByRole('button',{name:'Packaging',exact:true}).click();await page.locator('.primary-nav').getByRole('button',{name:'Compare',exact:true}).click();await page.locator('mps-model-diff[data-state=ready]').waitFor();return page.locator('mps-model-diff').evaluate(el=>el.states);};
  await page.route('**/api/v1/packages/compare',route=>route.fulfill({contentType:'application/json',body:JSON.stringify(fixture)}));
  item.before.properties[property]='Shared OLD name';item.after.properties[property]='Shared NEW name';sync();states=await refresh();visible(states);
  for(const [side,word] of [['before','OLD'],['after','NEW']]){const span=states[side].highlights.find(h=>h.text==='Shared '+word+' name');assert.ok(span);assert.deepEqual(span.ranges,[[7,10]]);}
  await page.waitForFunction(()=>[...document.querySelectorAll('.mps-diff-surface')].every((surface,side)=>[...surface.querySelectorAll('canvas')].some(canvas=>{if(!canvas.width||!canvas.height)return false;const p=canvas.getContext('2d')?.getImageData(0,0,canvas.width,canvas.height).data||[],rgb=side?[213,242,221]:[253,221,216];for(let i=0;i<p.length;i+=4)if(p[i]===rgb[0]&&p[i+1]===rgb[1]&&p[i+2]===rgb[2])return true;return false;})));
  // The inserted/deleted space itself is never painted.
  for(const added of [true,false]){item.before.properties[property]=added?'Model':'Model fix';item.after.properties[property]=added?'Model fix':'Model';sync();states=await refresh();visible(states);assert.deepEqual(states[added?'before':'after'].highlights,[]);assert.deepEqual(states[added?'after':'before'].highlights.find(h=>h.text==='Model fix').ranges,[[6,9]]);}
  item.before.properties[property]='Shared name';item.after.properties[property]='Shared   name';sync();states=await refresh();visible(states);for(const s of Object.values(states))assert.deepEqual(s.highlights,[]);
  for(const index of [0,1,2,3]){await page.goto(origin+'/#release/'+pkg.id);await page.locator('mps-model-diff[data-state=ready]').waitFor();await page.locator('.comparison-tree [data-kind=root]').nth(index).click();await page.locator('mps-model-diff[data-state=ready]').waitFor();visible(await page.locator('mps-model-diff').evaluate(el=>el.states));}
  assert.deepEqual(await page.evaluate(()=>alefExample.dirtyRoots()),[]);assert.deepEqual(writes,[]);assert.deepEqual(errors,[]);
  console.log('PASS: blank and whitespace-only cells are uncoloured in all four package roots; modified text remains red/green; insertion/deletion spaces and whitespace-only changes stay uncoloured. No live model or package writes.');
 }
}catch(error){await page.screenshot({path:path.join(output,'failure.png')});throw error;}finally{await api.browser.close();}
