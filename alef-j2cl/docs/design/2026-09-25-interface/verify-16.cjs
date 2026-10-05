// Node-only rendering and composition regression checks. No browser or backend.
const assert=require('node:assert/strict');
const fs=require('node:fs');
const path=require('node:path');
const vm=require('node:vm');
const html=fs.readFileSync(path.join(__dirname,'iteration-16.html'),'utf8');
const elements=new Map(),listeners=new Map();
function element(selector){
  if(!elements.has(selector))elements.set(selector,{
    innerHTML:'',textContent:'',value:'',dataset:{},style:{},hidden:false,
    classList:{add(){},remove(){},toggle(){},contains(){return false}},
    setAttribute(){},removeAttribute(){},addEventListener(){},insertBefore(){},insertAdjacentHTML(){},
    querySelectorAll(){return []},focus(){},previousElementSibling:{}
  });
  return elements.get(selector);
}
const context=vm.createContext({
  document:{querySelector:s=>['#project','#workbench'].includes(s)?null:element(s),querySelectorAll:()=>[],createElement:()=>element('new-button'),addEventListener:(name,fn)=>{if(!listeners.has(name))listeners.set(name,[]);listeners.get(name).push(fn)}},
  window:{addEventListener(){}},location:{hash:'#releases'},innerWidth:2122,setTimeout:()=>0,clearTimeout(){},console
});
for(const [,asset] of html.matchAll(/(?:src|href)="([^"?#]+\.(?:js|css))"/g))assert(fs.existsSync(path.join(__dirname,asset)),asset);
for(const [,asset] of html.matchAll(/<script src="([^"]+)"/g)){
  let code=fs.readFileSync(path.join(__dirname,asset),'utf8');
  // Expose immutable fixtures in the test VM only; production source is untouched.
  if(asset==='design-16.js')code=code.replace(/\}\)\(\);\s*$/, 'globalThis.testState={releases,getSaved:()=>savedGraph,getLive:()=>liveGraph,openPackages};})();');
  vm.runInContext(code,context,{filename:asset});
}
const state=context.testState;
const json=value=>JSON.parse(JSON.stringify(value));
const page=()=>element('#screen').innerHTML;
const popup=()=>element('#modal-host').innerHTML;
const click=(action,dataset={})=>context.handle(action,{dataset});
const event=(type,props)=>{const target={id:'',dataset:{},matches:()=>false,setAttribute(){},removeAttribute(){},...props};for(const listener of listeners.get(type)||[])listener({target});};
const input=(id,value)=>event('input',{id,value});
const select=(key,checked=true)=>event('change',{dataset:{packageChange:key},checked});
const release=id=>state.releases.find(r=>r.id===id);
const current=r=>r.revisions.at(-1);
const keys=()=>[...page().matchAll(/data-action="compare-root"[^>]+data-key="([^"]+)"/g)].map(m=>m[1]);
function fields(version,title){input('candidate-version',version);input('candidate-title',title);}
let checks=0;
function check(name,fn){fn();checks++;console.log('PASS '+name);}
const original=json(release(18)),workspace=json(state.getSaved());
check('Package list always offers creation while a package is open',()=>{
  assert.match(page(),/data-action="create-package"[^>]*>[^<]*|New package/);
  assert.match(page(),/1 open · 5 packages/);assert.doesNotMatch(page(),/One open release|Only one/);
});
check('Popup starts empty, offers packaged baselines and the deployed production version',()=>{
  click('create-package');assert.match(popup(),/New package/);assert.match(popup(),/Use Production EU · v1.2.0/);
  const baseline=popup().match(/<select id="package-baseline">([\s\S]*?)<\/select>/)[1];
  assert.doesNotMatch(baseline,/REL-18/);assert.match(baseline,/REL-17-R1/);
  assert.equal(element('[data-action="confirm-create-package"]').disabled,true);
  assert.match(popup(),/All contents from <strong>v1.3.1/);assert.match(popup(),/Also selected in v1.4.0/);assert.match(popup(),/View change/);
});
check('Changing baseline clears selection and preserves entered version and name',()=>{
  fields('1.2.1','Production config fix');select('name');assert.equal(element('[data-action="confirm-create-package"]').disabled,false);
  click('package-use-production',{baseline:'REL-15-R1'});assert.match(popup(),/value="1.2.1"/);assert.match(popup(),/value="Production config fix"/);
  assert.equal(element('[data-action="confirm-create-package"]').disabled,true);assert.match(popup(),/All contents from <strong>v1.2.0/);
});
check('Empty selections and duplicate versions cannot create a package',()=>{
  click('confirm-create-package');assert.equal(state.releases.length,5);assert.match(element('#package-selection-error').textContent,/Select at least one/);
  select('config');input('candidate-version','1.4.0');click('confirm-create-package');assert.equal(state.releases.length,5);assert.match(element('#release-version-error').textContent,/already exists/);
  input('candidate-version','bad');click('confirm-create-package');assert.equal(state.releases.length,5);assert.match(element('#release-version-error').textContent,/Enter a version/);
});
check('Production fix preserves the entire production baseline except its selected change',()=>{
  input('candidate-version','1.2.1');click('confirm-create-package');assert.equal(state.openPackages().length,2);
  const hotfix=release(19),graph=json(current(hotfix).graph),base=json(release(15).revisions[0].graph);
  const expected={...base,config:workspace.config};assert.deepEqual(graph,expected);assert.equal(hotfix.baseline.id,'REL-15-R1');
  assert.equal(graph.name.version,11);assert(graph.leeg);assert(!graph.added);
  assert.match(page(),/aria-current="page">v1.2.1/);assert.deepEqual(keys(),['config']);
  assert.deepEqual(json(release(18)),original);assert.deepEqual(json(state.getSaved()),workspace);
});
check('All roots reveals inherited content; only selected changes appear in Changes only',()=>{
  click('contents-filter',{filter:'all'});assert(keys().includes('ibm'));assert(keys().includes('leeg'));assert(!keys().includes('added'));assert.equal(keys().length,16);
  click('compare-root',{key:'name',inline:'true'});assert.match(page(),/change-kind U">Unchanged/);assert.match(page(),/de voornaam Tekst/);
  click('contents-filter',{filter:'changes'});assert.deepEqual(keys(),['config']);
});
check('Select all, Clear and cancellation do not alter any existing package',()=>{
  click('all-releases');assert.match(page(),/2 open · 6 packages/);click('create-package');
  click('package-select-all');assert.equal(element('[data-action="confirm-create-package"]').disabled,false);
  click('package-clear');assert.equal(element('[data-action="confirm-create-package"]').disabled,true);
  click('cancel-package');assert.equal(state.releases.length,6);assert.deepEqual(json(release(18)),original);
});
check('A third package can independently select only an addition and deletion',()=>{
  click('create-package');fields('1.3.2','Root maintenance');select('leeg');select('added');click('confirm-create-package');
  assert.equal(state.openPackages().length,3);const third=release(20),expected=json(release(17).revisions[0].graph);
  delete expected.leeg;expected.added=workspace.added;assert.deepEqual(json(current(third).graph),expected);
  assert.deepEqual(keys().sort(),['added','leeg']);assert.deepEqual(json(release(18)),original);assert.equal(release(19).state,'In review');
});
check('Approving and packaging one fix leaves the other packages open',()=>{
  click('open-release',{release:'19'});click('review-decisions');click('approve-package');assert.equal(release(19).state,'Approved');assert.equal(state.openPackages().length,3);
  click('release-final');click('confirm-release-final');assert.equal(release(19).state,'Packaged');assert.equal(state.openPackages().length,2);
  assert.deepEqual(json(release(18)),original);assert.equal(release(20).state,'In review');click('all-releases');assert.match(page(),/2 open · 7 packages/);
});
check('Creation uses saved workspace content even while the editor has unsaved changes',()=>{
  context.go('model');assert.match(page(),/View v1.4.0/);assert.match(page(),/View v1.3.2/);assert.doesNotMatch(page(),/Include in the next release/);
  click('version-lock');click('preview-edit');assert.notDeepEqual(json(state.getLive().name),workspace.name);
  click('create-package');fields('1.5.0','Saved name change');select('name');click('confirm-create-package');
  assert.deepEqual(json(current(release(21)).graph.name),workspace.name);assert.deepEqual(json(release(18)),original);
});
check('Standalone Compare and fixed package source remain available',()=>{
  assert.match(page(),/id="release-compare-source"/);assert.doesNotMatch(page(),/<select[^>]+id="release-compare-source"|data-section="editor"/);
  context.go('changes');assert.match(page(),/id="compare-source"/);assert.match(page(),/id="against-source"/);
});
check('Composition clones both sources and retains baseline-only items',()=>{
  const base={kept:{content:['old']},removed:{content:['remove']}},source={kept:{content:['new']},added:{content:['add']}};
  const graph=context.composePackageGraph(base,source,['added']);assert.deepEqual(json(graph),{...base,added:source.added});
  graph.kept.content.push('local');graph.added.content.push('local');assert.equal(base.kept.content.length,1);assert.equal(source.added.content.length,1);
});
console.log(`${checks} checks passed. Node VM only; layout and real integrations are not tested.`);
