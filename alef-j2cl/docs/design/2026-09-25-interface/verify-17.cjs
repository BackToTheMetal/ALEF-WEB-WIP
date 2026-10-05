// Node-only rendering and composition regression checks. No browser or backend.
const assert=require('node:assert/strict');
const fs=require('node:fs');
const path=require('node:path');
const vm=require('node:vm');
const html=fs.readFileSync(path.join(__dirname,'iteration-17.html'),'utf8');
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
  if(asset==='design-17.js')code=code.replace(/\}\)\(\);\s*$/, 'globalThis.testState={releases,getSaved:()=>savedGraph,getLive:()=>liveGraph,openPackages,changeCatalog};})();');
  vm.runInContext(code,context,{filename:asset});
}
const state=context.testState;
const json=value=>JSON.parse(JSON.stringify(value));
const page=()=>element('#screen').innerHTML;
const popup=()=>element('#modal-host').innerHTML;
const click=(action,dataset={})=>context.handle(action,{dataset});
const event=(type,props)=>{const target={id:'',dataset:{},matches:()=>false,setAttribute(){},removeAttribute(){},...props};for(const listener of listeners.get(type)||[])listener({target});};
const input=(id,value)=>event('input',{id,value});
const select=(key,checked=true,id)=>{const node=state.getSaved()[key]||null;const candidate=state.changeCatalog.versions().slice().reverse().find(v=>v.key===key&&JSON.stringify(v.node)===JSON.stringify(node));event('change',{dataset:{packageChange:key,packageVersion:id||candidate.id},checked});};
const filter=tag=>event('change',{id:'package-tag-filter',value:tag});
const picker=()=>element('.package-picker-list').innerHTML||popup();
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
  assert.match(popup(),/All contents from <strong>v1.3.1/);assert.match(popup(),/Also selected in v1.4.0/);assert.doesNotMatch(popup(),/View change|package-picker-diff|<pre>/);assert.match(popup(),/Filter by tag/);
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
let taggedName,latestName;
check('Tags normalize consistently and only become selectable after saving',()=>{
  assert.deepEqual(json(context.normalizeChangeTags(' Hotfix-42, hotfix-42, Ticket-7, ')),['hotfix-42','ticket-7']);
  context.go('model');assert.match(page(),/data-action="change-tags"/);
  click('change-tags');element('#change-tags-input').value='Hotfix-42, hotfix-42, Ticket-7';click('apply-change-tags');
  assert(!state.changeCatalog.versions().some(v=>v.tags.includes('hotfix-42')));
  click('version-save');taggedName=json(state.getSaved().name);
  const tagged=state.changeCatalog.versions().find(v=>v.node?.snapshot===taggedName.snapshot&&v.key==='name');
  assert.deepEqual(json(tagged.tags),['hotfix-42','ticket-7']);assert.deepEqual(json(release(18)),original);
});
check('A later untagged save retains the tagged version and its exact content',()=>{
  click('preview-edit');click('version-save');latestName=json(state.getSaved().name);assert.notDeepEqual(latestName,taggedName);
  const versions=state.changeCatalog.versions().filter(v=>v.key==='name');
  assert.deepEqual(json(versions.find(v=>v.node.snapshot===latestName.snapshot).tags),[]);
  assert.deepEqual(json(versions.find(v=>v.tags.includes('hotfix-42')).node),taggedName);
  click('create-package');event('change',{id:'package-baseline',value:'REL-17-R1'});filter('tag:hotfix-42');
  assert.match(picker(),/hotfix-42/);assert.match(picker(),/retained/);assert.doesNotMatch(picker(),/data-package-change="config"/);
  fields('1.5.1','Tagged fix');click('package-select-all');click('confirm-create-package');
  assert.deepEqual(json(current(release(22)).graph.name),taggedName);assert.notDeepEqual(json(current(release(22)).graph.name),latestName);
  assert.deepEqual(json(release(22).selectedVersions[0].tags),['hotfix-42','ticket-7']);
});
check('Tag filtering, hidden selections, Clear shown and empty results are scoped',()=>{
  click('create-package');event('change',{id:'package-baseline',value:'REL-17-R1'});filter('tag:name-update');click('package-select-all');
  assert.match(element('#package-selection-summary').innerHTML,/4 changes selected/);
  filter('tag:production-fix');assert.match(picker(),/execution.properties/);assert.doesNotMatch(picker(),/data-package-change="name"/);
  assert.match(element('#package-selection-summary').innerHTML,/4 selected outside/);click('package-select-all');assert.match(element('#package-selection-summary').innerHTML,/5 changes selected/);
  click('package-clear');assert.match(element('#package-selection-summary').innerHTML,/4 changes selected/);
  filter('tag:missing');assert.match(picker(),/No saved changes match/);click('package-select-all');assert.match(element('#package-selection-summary').innerHTML,/4 changes selected/);
  filter('selected');assert.equal([...picker().matchAll(/data-package-change=/g)].length,4);
});
check('Only one saved version per root can be selected, with no model diff in the popup',()=>{
  filter('all');assert.match(picker(),/data-version-choice="name"/);assert.doesNotMatch(picker(),/View change|<pre>/);
  const version=state.changeCatalog.versions().find(v=>v.key==='name'&&v.node.snapshot===latestName.snapshot);
  event('change',{dataset:{versionChoice:'name'},value:version.id});assert.match(element('#package-selection-summary').innerHTML,/4 changes selected/);
  fields('1.5.2','Selected versions');click('confirm-create-package');assert.deepEqual(json(current(release(23)).graph.name),latestName);
  assert.equal(release(23).selectedVersions.filter(v=>v.key==='name').length,1);
});
check('Removing an added change resets approval and preserves the earlier revision',()=>{
  const r=release(23),first=json(current(r)),other=json(release(22));
  click('review-decisions');click('approve-package');assert.equal(r.state,'Approved');
  click('compare-root',{key:'added',inline:'true'});assert.match(page(),/data-action="remove-package-change"/);
  click('remove-package-change',{key:'added',revision:first.id});
  assert.equal(r.state,'In review');assert.equal(r.approval,null);assert.equal(r.revisions.length,2);assert(!current(r).graph.added);
  assert.deepEqual(json(r.revisions[0]),first);assert.deepEqual(json(release(22)),other);assert.deepEqual(json(release(18)),original);
});
check('Removing renamed and modified changes restores the exact baseline content',()=>{
  const r=release(23);
  for(const key of ['service','test','name']){click('remove-package-change',{key,revision:current(r).id});assert.deepEqual(json(current(r).graph[key]),json(r.baseline.graph[key]));}
  assert.deepEqual(json(current(r).graph),json(r.baseline.graph));assert.match(page(),/No changes remain/);
  click('review-decisions');click('approve-package');click('release-final');click('confirm-release-final');assert.equal(r.state,'In review');assert.equal(r.approval,null);
});
check('Removing a deletion restores its baseline root without altering other changes',()=>{
  click('open-release',{release:'20'});const r=release(20),added=json(current(r).graph.added);
  assert(!current(r).graph.leeg);click('remove-package-change',{key:'leeg',revision:current(r).id});
  assert.deepEqual(json(current(r).graph.leeg),json(r.baseline.graph.leeg));assert.deepEqual(json(current(r).graph.added),added);
});
check('Historical, packaged, stale and baseline-only roots cannot be removed',()=>{
  const r=release(20),count=r.revisions.length;
  click('remove-package-change',{key:'added',revision:r.revisions[0].id});assert.equal(r.revisions.length,count);
  click('remove-package-change',{key:'ibm',revision:current(r).id});assert.equal(r.revisions.length,count);
  click('inspect-package',{index:'0'});assert.doesNotMatch(page(),/data-action="remove-package-change"/);
  click('remove-package-change',{key:'added',revision:r.revisions[0].id});assert.equal(r.revisions.length,count);
  click('open-release',{release:'19'});assert.doesNotMatch(page(),/data-action="remove-package-change"/);const packaged=json(release(19));
  click('remove-package-change',{key:'config',revision:current(release(19)).id});assert.deepEqual(json(release(19)),packaged);
  click('open-release',{release:'23'});event('change',{id:'release-against-source',value:'REL-15-R1'});assert.doesNotMatch(page(),/data-action="remove-package-change"/);
});
check('Changing baseline retains a visible tag filter even with no matching changes',()=>{
  click('create-package');event('change',{id:'package-baseline',value:'REL-17-R1'});filter('tag:production-fix');event('change',{id:'package-baseline',value:'REL-19-R1'});
  assert.match(popup(),/value="tag:production-fix" selected/);assert.match(popup(),/No saved changes match this filter/);click('cancel-package');
});
console.log(`${checks} checks passed. Node VM only; layout and real integrations are not tested.`);
