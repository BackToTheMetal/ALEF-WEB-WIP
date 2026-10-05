import {createRequire} from 'node:module';
import fs from 'node:fs';import path from 'node:path';import assert from 'node:assert/strict';
const root=path.resolve(import.meta.dirname,'..'),{chromium}=createRequire(path.join(root,'../org-jdom-j2cl/package.json'))('playwright');
const account=JSON.parse(fs.readFileSync('C:/Users/niekh/Documents/ChatGPT/ALEFBackend/.local/development-identities.json','utf8').replace(/^\uFEFF/, '')).users.find(u=>u.username==='author');
const browser=await chromium.launch({headless:true}),page=await browser.newPage({viewport:{width:1640,height:1060}}),errors=[],writes=[];
page.setDefaultTimeout(30000);
await page.addInitScript(()=>{let runtime;window.runtimeLoads=0;window.comparisonLoads=0;Object.defineProperty(window,'alefExample',{configurable:true,get:()=>runtime,set:api=>{runtime=api;const start=api.startRemote,prepare=api.prepareComparison;api.startRemote=(...args)=>{window.runtimeLoads++;return start(...args);};api.prepareComparison=(...args)=>{window.comparisonLoads++;return prepare(...args);};}});});
const output=path.join(root,'out/mps-diff');fs.mkdirSync(output,{recursive:true});
if(process.env.ALEF_TEST_PREVIEW_URL)for(const asset of ['app.js','interface.js','interface-ui.js','interface.css','model-diff.js','mps-diff.js'])await page.route('**/'+asset,async route=>{const response=await fetch(new URL(asset,process.env.ALEF_TEST_PREVIEW_URL));await route.fulfill({status:response.status,contentType:response.headers.get('content-type'),body:Buffer.from(await response.arrayBuffer())});});
page.on('pageerror',e=>errors.push(String(e)));page.on('request',r=>{if(r.method()!=='GET'&&/\/api\/v1\/(changes|packages|leases)/.test(r.url()))writes.push(r.url());});
try{
  console.log('Checking sign-in and workspace initialization');
  await page.goto('http://localhost:8080/#changes');await page.getByRole('button',{name:'Log in',exact:true}).click();await page.locator('#username').fill(account.username);await page.locator('#password').fill(account.password);await page.locator('#kc-login').click();await page.waitForURL('http://localhost:8080/**');
  await page.waitForFunction(()=>document.documentElement.dataset.workspaceState==='ready',{},{timeout:120000});
  console.log('Checking real retained comparison');
  await page.locator('.comparison-body').waitFor({timeout:120000});await page.getByRole('button',{name:'All roots',exact:true}).click();
  await page.locator('mps-model-diff[data-state=ready]').waitFor({timeout:120000});assert.equal(await page.locator('.diff-line').count(),0);
  const badges=await page.locator('.comparison-tree .change-badge').evaluateAll(nodes=>nodes.map(n=>({text:n.textContent,title:n.title,label:n.getAttribute('aria-label')})));
  assert.ok(badges.length);assert.ok(badges.every(b=>b.text.length===1&&b.title===b.label&&b.title.length>1));
  assert.deepEqual(await page.locator('.mps-diff-legend span').allTextContents(),['Removed text','Added text']);
  const realStates=await page.locator('mps-model-diff').evaluate(el=>el.states);assert.ok(Object.keys(realStates).length);for(const state of Object.values(realStates)){assert.equal(state.readOnly,true);assert.ok(state.text.trim());}
  await page.screenshot({path:path.join(output,'live-comparison.png')});
  const initialDirty=await page.evaluate(()=>window.alefExample.dirtyRoots());assert.deepEqual(initialDirty,[]);
  assert.equal(await page.evaluate(()=>window.alefExample.canUndo()),false,'Comparison loading must not create workspace edits');
  const sessionId=await page.locator('mps-comparison-session').getAttribute('id'),initialTree=await page.evaluate(()=>window.alefExample.projectTree());
  const loads=await page.evaluate(()=>window.comparisonLoads),bundleLoads=await page.evaluate(()=>performance.getEntriesByType('resource').filter(r=>r.name.endsWith('/app.js')).length);
  for(const i of [1,0,2,0]){await page.locator('.comparison-tree [data-kind=root]').nth(i).click();await page.locator('mps-model-diff[data-state=ready]').waitFor();}
  assert.equal(await page.locator('mps-comparison-session').getAttribute('id'),sessionId);
  assert.equal(await page.evaluate(()=>window.comparisonLoads),loads,'Switching roots retains both model snapshots');
  assert.equal(await page.evaluate(()=>window.runtimeLoads),1,'The existing MPS runtime is reused');
  assert.equal(await page.evaluate(()=>performance.getEntriesByType('resource').filter(r=>r.name.endsWith('/app.js')).length),bundleLoads);
  assert.equal(await page.locator('iframe').count(),0);
  assert.deepEqual(await page.evaluate(()=>window.alefExample.projectTree()),initialTree,'Comparison models do not enter the workspace tree');
  // Use copies of actual native graph data to guarantee a before/after change,
  // additions and deletion without saving a model or creating a real package.
  const data=await (await page.request.get('http://localhost:8080/api/v1/packages/compare')).json();
  const source=data.roots.find(r=>r.after&&/Gegevensmodel/.test(r.name))||data.roots.find(r=>r.after);assert.ok(source);
  const fixture=structuredClone(data);fixture.roots=[structuredClone(source)];const item=fixture.roots[0];item.before=structuredClone(item.after);item.beforeReference=item.afterReference;fixture.beforeGraph=structuredClone(fixture.afterGraph);
  const property=Object.keys(item.after.properties).find(k=>item.after.properties[k]===item.name)||Object.keys(item.after.properties)[0];assert.ok(property);
  item.before.properties[property]='Shared OLD name';item.after.properties[property]='Shared NEW name';item.changed=true;item.kind='Modified';item.includedChange=true;
  const syncGraphs=value=>{const root=value.roots[0];for(const side of ['before','after'])for(const model of value[side+'Graph'].models){if(root[side+'Reference']!==model.reference+'/'+root[side].id)continue;const i=model.roots.findIndex(r=>r.id===root[side].id);if(i>=0){model.roots[i]=structuredClone(root[side]);break;}}};syncGraphs(fixture);
  const compare=route=>route.fulfill({status:200,contentType:'application/json',body:JSON.stringify(fixture)});await page.route('**/api/v1/packages/compare',compare);
  await page.getByRole('button',{name:'Editor',exact:true}).click();await page.getByRole('button',{name:'Compare',exact:true}).click();await page.locator('mps-model-diff[data-state=ready]').waitFor({timeout:120000});
  let states=await page.locator('mps-model-diff').evaluate(el=>el.states);assert.equal(Object.keys(states).length,2);assert.ok(states.before.text.includes('Shared OLD name'));assert.ok(states.after.text.includes('Shared NEW name'));assert.equal(states.before.highlightedNodes,1);assert.equal(states.after.highlightedNodes,1);
  for(const side of ['before','after']){assert.equal(states[side].highlights.length,1);const span=states[side].highlights[0];assert.equal(span.whole,false);assert.deepEqual(span.ranges,[[7,10]],'Only OLD/NEW is highlighted; Shared, name and Objectmodel are unchanged');}
  assert.notEqual(states.before.modelReference,states.after.modelReference,'Each version has its own model identity');
  assert.notEqual(states.after.modelReference,item.afterReference.slice(0,item.afterReference.lastIndexOf('/')));
  await page.waitForFunction(()=>Array.from(document.querySelectorAll('.mps-diff-surface')).every((surface,side)=>Array.from(surface.querySelectorAll('canvas')).some(canvas=>{if(!canvas.width||!canvas.height)return false;const pixels=canvas.getContext('2d')?.getImageData(0,0,canvas.width,canvas.height).data||[],rgb=side?[213,242,221]:[253,221,216];for(let i=0;i<pixels.length;i+=4)if(pixels[i]===rgb[0]&&pixels[i+1]===rgb[1]&&pixels[i+2]===rgb[2])return true;return false;})));
  const yellow=await page.locator('.mps-diff-surface canvas').evaluateAll(nodes=>nodes.some(canvas=>{if(!canvas.width||!canvas.height)return false;const data=canvas.getContext('2d').getImageData(0,0,canvas.width,canvas.height).data;for(let i=0;i<data.length;i+=4)if(data[i]===255&&data[i+1]===235&&data[i+2]===187)return true;return false;}));assert.equal(yellow,false,'There is no yellow modified-node highlighting');
  await page.waitForFunction(()=>!Array.from(document.querySelectorAll('.mps-diff-surface canvas')).some(canvas=>{if(!canvas.width||!canvas.height)return false;const data=canvas.getContext('2d').getImageData(0,0,canvas.width,canvas.height).data;for(let i=0;i<data.length;i+=4)if((data[i]===80&&data[i+1]===140&&data[i+2]===36)||(data[i]===124&&data[i+1]===0&&data[i+2]===124)||(data[i]===0&&data[i+1]===0&&data[i+2]===255))return true;return false;}),null,{timeout:10000});
  await page.screenshot({path:path.join(output,'highlighted-mps-comparison.png')});
  await page.locator('mps-model-diff').evaluate(el=>{for(const side of ['before','after'])el.focusAnchor(side,1);});await page.waitForFunction(()=>Object.values(document.querySelector('mps-model-diff').states).every(s=>s.selectedNodeId));
  for(const [i,side] of ['before','after'].entries()){const original=states[side].text;await page.locator('.mps-diff-surface').nth(i).locator('[data-swing-viewport-view]:visible').first().click({position:{x:120,y:45}});await page.keyboard.type('MUST_NOT_EDIT');await page.keyboard.insertText('MUST_NOT_PASTE');await page.keyboard.press('Delete');assert.equal(await page.evaluate(side=>window.alefExample.comparisonState(document.querySelector('mps-comparison-session').id,side).text,side),original);}
  assert.deepEqual(await page.evaluate(()=>window.alefExample.dirtyRoots()),initialDirty);
  // Pure insertions leave the old text uncoloured, and only the suffix is green.
  const replacementFixture=structuredClone(fixture);
  item.after.properties[property]=item.before.properties[property]+' fix';syncGraphs(fixture);
  await page.getByRole('button',{name:'Editor',exact:true}).click();await page.getByRole('button',{name:'Compare',exact:true}).click();await page.locator('mps-model-diff[data-state=ready]').waitFor();
  const inserted=await page.locator('mps-model-diff').evaluate(el=>el.states);
  assert.deepEqual(inserted.before.highlights,[]);assert.equal(inserted.after.highlights.length,1);assert.deepEqual(inserted.after.highlights[0].ranges,[[16,19]]);
  // Equal-looking roots with different identities are whole-node changes.
  item.after.properties[property]=item.before.properties[property];
  const oldId=item.after.id;item.after.id='77777777777777777';
  const targetModel=fixture.afterGraph.models.find(m=>item.afterReference===m.reference+'/'+oldId);
  targetModel.roots[targetModel.roots.findIndex(n=>n.id===oldId)]=structuredClone(item.after);
  item.afterReference=targetModel.reference+'/'+item.after.id;
  await page.getByRole('button',{name:'Editor',exact:true}).click();await page.getByRole('button',{name:'Compare',exact:true}).click();await page.locator('mps-model-diff[data-state=ready]').waitFor();
  const replaced=await page.locator('mps-model-diff').evaluate(el=>el.states);
  for(const side of ['before','after']){assert.ok(replaced[side].highlights.some(h=>h.text==='Objectmodel'&&h.whole));assert.ok(replaced[side].highlights.every(h=>h.whole));}
  Object.assign(fixture,replacementFixture);
  // A generated reference cell must resolve in its own version, never the live
  // workspace or the other pane. Rename a cross-model target in response copies.
  const referenceFixture=structuredClone(data),referenceRoot=referenceFixture.roots.find(r=>r.after?.references?.some(ref=>ref.model&&ref.model!==r.afterReference.slice(0,r.afterReference.lastIndexOf('/'))&&ref.resolveInfo==='Persoon'));
  assert.ok(referenceRoot,'Retained cross-model mapping is available');
  const edge=referenceRoot.after.references.find(ref=>ref.resolveInfo==='Persoon');
  referenceRoot.before=structuredClone(referenceRoot.after);referenceRoot.beforeReference=referenceRoot.afterReference;referenceRoot.changed=true;
  referenceFixture.roots=[referenceRoot];referenceFixture.beforeGraph=structuredClone(referenceFixture.afterGraph);
  const targetIn=graph=>{const find=n=>n.id===edge.nodeId?n:(n.children||[]).map(c=>find(c.node)).find(Boolean);return graph.models.find(m=>m.reference===edge.model).roots.map(find).find(Boolean);};
  for(const side of ['before','after']){const target=targetIn(referenceFixture[side+'Graph']),property=Object.keys(target.properties).find(k=>target.properties[k]==='Persoon');target.properties[property]='Person_'+side+'_snapshot';}
  await page.unroute('**/api/v1/packages/compare',compare);
  await page.route('**/api/v1/packages/compare',route=>route.fulfill({status:200,contentType:'application/json',body:JSON.stringify(referenceFixture)}));
  await page.getByRole('button',{name:'Editor',exact:true}).click();await page.getByRole('button',{name:'Compare',exact:true}).click();await page.locator('mps-model-diff[data-state=ready]').waitFor();
  const referenced=await page.locator('mps-model-diff').evaluate(el=>el.states);
  for(const side of ['before','after'])assert.ok(referenced[side].text.includes('Person_'+side+'_snapshot'),side+' resolves its own cross-model target: '+referenced[side].text);
  // Retarget a reference to another node with the SAME display name. Its own
  // presentation is highlighted even though a textual diff would be empty.
  const oldTarget=targetIn(referenceFixture.afterGraph),newTarget=structuredClone(oldTarget);
  newTarget.id='77777777777777778';newTarget.children=[];
  const rename=node=>{for(const key of Object.keys(node.properties||{}))if(node.properties[key]?.startsWith?.('Person_'))node.properties[key]='Identical name';};
  rename(targetIn(referenceFixture.beforeGraph));rename(oldTarget);rename(newTarget);
  referenceFixture.afterGraph.models.find(m=>m.reference===edge.model).roots.push(newTarget);
  edge.nodeId=newTarget.id;edge.resolveInfo='Identical name';
  for(const model of referenceFixture.afterGraph.models){const found=model.roots.findIndex(n=>n.id===referenceRoot.after.id);if(found>=0)model.roots[found]=structuredClone(referenceRoot.after);}
  await page.getByRole('button',{name:'Editor',exact:true}).click();await page.getByRole('button',{name:'Compare',exact:true}).click();await page.locator('mps-model-diff[data-state=ready]').waitFor();
  const retargeted=await page.locator('mps-model-diff').evaluate(el=>el.states);
  for(const side of ['before','after'])assert.ok(retargeted[side].highlights.some(h=>h.text==='Identical name'&&h.whole),side+' flags a changed reference ID with unchanged text');
  assert.deepEqual(await page.evaluate(()=>window.alefExample.projectTree()),initialTree);
  assert.equal(await page.locator('.mps-diff-toolbar').count(),0);assert.equal(await page.getByRole('button',{name:/^(↑ Previous|↓ Next|Side by side|Stacked)$/}).count(),0);assert.equal(await page.locator('.mps-diff-columns > section').count(),2);await page.setViewportSize({width:878,height:850});assert.ok(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth));
  // Package Contents uses the same native component, including comment anchors.
  const pkg={id:'diff-test',version:'9.9.9',title:'Isolated comparison fixture',revision:1,authorName:'Test',state:'In review',canEdit:false,canFinalize:false,finalized:false,reviewers:[],comments:[],activity:[],release:{jobState:'SUCCEEDED',candidate:{id:'fixture'},manifest:{contents:fixture.afterGraph}}};
  await page.route('**/api/v1/packages/diff-test',route=>route.fulfill({status:200,contentType:'application/json',body:JSON.stringify(pkg)}));
  await page.route('**/api/v1/packages/diff-test/contents',route=>route.fulfill({status:200,contentType:'application/json',body:JSON.stringify(fixture)}));
  await page.evaluate(()=>location.hash='release/diff-test');await page.locator('mps-model-diff[data-state=ready]').waitFor({timeout:120000});await page.locator('mps-model-diff').evaluate(el=>{for(const side of ['before','after'])el.focusAnchor(side,1);});const comment=page.getByRole('button',{name:'Comment on selected node',exact:true}).last();await comment.waitFor();await page.waitForFunction(()=>!Array.from(document.querySelectorAll('button')).filter(b=>b.textContent==='Comment on selected node').at(-1)?.disabled);await comment.click();await page.getByRole('dialog').waitFor();await page.getByRole('button',{name:'Cancel',exact:true}).click();
  const finalSession=await page.locator('mps-comparison-session').getAttribute('id');await page.getByRole('button',{name:'Editor',exact:true}).click();assert.equal(await page.locator('.mps-diff-surface').count(),0);assert.equal(await page.evaluate(id=>window.alefExample.comparisonStatus(id),finalSession),'CLOSED');assert.equal(await page.evaluate(()=>window.alefExample.status()),'READY');assert.deepEqual(errors,[]);assert.deepEqual(writes,[]);
  console.log('PASS: compact badges and hover labels, red/green changed text ranges, neutral unchanged text, suffix-only insertions, whole-node ID replacements, same-name reference retargeting, retained native models, read-only typing/delete, package comments, permanent side-by-side layout and workspace isolation. No live model/package writes.');
}catch(error){await page.screenshot({path:path.join(output,'failure.png')});console.error(errors);console.error((await page.locator('body').innerText()).slice(-2500));throw error;}finally{await browser.close();}
