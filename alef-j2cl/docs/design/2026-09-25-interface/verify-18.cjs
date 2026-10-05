// Lifecycle checks for tag consumption. No browser or backend is used.
const assert=require('node:assert/strict'),fs=require('node:fs'),path=require('node:path'),vm=require('node:vm');
const html=fs.readFileSync(path.join(__dirname,'iteration-18.html'),'utf8');
const json=value=>JSON.parse(JSON.stringify(value));
function boot(){
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
  const context=vm.createContext({document:{querySelector:s=>['#project','#workbench'].includes(s)?null:element(s),querySelectorAll:()=>[],createElement:()=>element('new-button'),addEventListener:(name,fn)=>{if(!listeners.has(name))listeners.set(name,[]);listeners.get(name).push(fn)}},window:{addEventListener(){}},location:{hash:'#releases'},innerWidth:2122,setTimeout:()=>0,clearTimeout(){},console});
  for(const [,asset] of html.matchAll(/(?:src|href)="([^"?#]+\.(?:js|css))"/g))assert(fs.existsSync(path.join(__dirname,asset)),asset);
  for(const [,asset] of html.matchAll(/<script src="([^"]+)"/g)){
    let code=fs.readFileSync(path.join(__dirname,asset),'utf8');
    if(asset==='design-18.js')code=code.replace(/\}\)\(\);\s*$/, 'globalThis.testState={releases,changeCatalog,getSaved:()=>savedGraph,getLive:()=>liveGraph,getDraftTags:()=>draftTags,hasTagDraft:()=>tagsDirty};})();');
    vm.runInContext(code,context,{filename:asset});
  }
  const state=context.testState;
  const click=(action,dataset={})=>context.handle(action,{dataset});
  const event=(type,props)=>{const target={id:'',dataset:{},matches:()=>false,setAttribute(){},removeAttribute(){},...props};for(const listener of listeners.get(type)||[])listener({target});};
  const input=(id,value)=>event('input',{id,value});
  const versions=()=>state.changeCatalog.versions();
  const tags=id=>json(versions().find(v=>v.id===id).tags);
  const latest=key=>versions().slice().reverse().find(v=>v.key===key&&JSON.stringify(v.node)===JSON.stringify(state.getSaved()[key]||null));
  const select=(key,id=latest(key).id)=>event('change',{dataset:{packageChange:key,packageVersion:id},checked:true});
  const filter=tag=>event('change',{id:'package-tag-filter',value:tag});
  const fields=(version='1.3.2',title='Selected fix')=>{input('candidate-version',version);input('candidate-title',title);};
  const applyTags=text=>{click('change-tags');element('#change-tags-input').value=text;click('apply-change-tags');};
  return {context,state,element,click,event,input,versions,tags,latest,select,filter,fields,applyTags,popup:()=>element('#modal-host').innerHTML,page:()=>element('#screen').innerHTML};
}
let checks=0;
function check(name,run){run(boot());checks++;console.log('PASS '+name);}
check('Creating a package clears every selected version tag and preserves its exact graph',t=>{
  const original=json(t.state.releases),saved=json(t.state.getSaved()),catalog=json(t.versions());
  t.click('create-package');t.filter('tag:name-update');t.click('package-select-all');t.fields();t.click('confirm-create-package');
  const r=t.state.releases[0],chosen=['name','service','test','added'];assert.equal(r.state,'In review');
  chosen.forEach(key=>assert.deepEqual(t.tags(t.latest(key).id),[]));
  catalog.filter(v=>!chosen.includes(v.key)).forEach(v=>assert.deepEqual(t.tags(v.id),v.tags));
  const expected=json(r.baseline.graph);chosen.forEach(key=>{expected[key]=saved[key];});assert.deepEqual(json(r.revisions[0].graph),expected);
  assert.deepEqual(json(t.state.getSaved()),saved);assert.deepEqual(json(t.state.releases.slice(1)),original);
  assert(r.selectedVersions.every(v=>!('tags' in v)));t.click('create-package');assert.doesNotMatch(t.popup(),/value="tag:name-update"/);
});
check('A shared tag is retained on unselected files and removed from the included file only',t=>{
  t.click('create-package');t.fields();t.select('name');t.click('confirm-create-package');
  assert.deepEqual(t.tags(t.latest('name').id),[]);assert.deepEqual(t.tags(t.latest('service').id),['name-update']);
  t.click('create-package');t.filter('tag:name-update');
  const picker=t.element('.package-picker-list').innerHTML;assert.match(picker,/data-package-change="service"/);assert.doesNotMatch(picker,/data-package-change="name"/);
});
check('Selecting, cancelling, empty selections and validation failures retain tags',t=>{
  const original=json(t.versions());t.click('create-package');t.select('name');assert.deepEqual(json(t.versions()),original);t.click('cancel-package');
  t.click('create-package');t.fields();t.click('confirm-create-package');assert.equal(t.state.releases.length,5);
  t.select('name');t.input('candidate-version','1.4.0');t.click('confirm-create-package');assert.equal(t.state.releases.length,5);
  t.input('candidate-version','invalid');t.click('confirm-create-package');assert.equal(t.state.releases.length,5);
  t.fields('1.3.2','');t.click('confirm-create-package');assert.equal(t.state.releases.length,5);assert.deepEqual(json(t.versions()),original);
});
check('Tagged deletions consume their own tags without changing other versions',t=>{
  const id=t.latest('leeg').id,other=json(t.latest('name'));
  t.click('create-package');t.fields();t.select('leeg');t.click('confirm-create-package');
  assert.deepEqual(t.tags(id),[]);assert(!t.state.releases[0].revisions[0].graph.leeg);assert.deepEqual(json(t.latest('name')),other);
  t.click('create-package');assert.doesNotMatch(t.popup(),/value="tag:cleanup"/);
});
check('Consuming an older version leaves a newer version of the same root tagged',t=>{
  const old=json(t.latest('name'));t.context.go('model');t.click('version-lock');t.click('preview-edit');t.applyTags('name-update');t.click('version-save');
  const newer=json(t.latest('name'));assert.notEqual(old.id,newer.id);
  t.click('create-package');t.fields();t.select('name',old.id);t.click('confirm-create-package');
  assert.deepEqual(t.tags(old.id),[]);assert.deepEqual(t.tags(newer.id),['name-update']);assert.deepEqual(json(t.state.getDraftTags()),['name-update']);
  assert.deepEqual(json(t.state.releases[0].revisions[0].graph.name),old.node);
  t.click('create-package');t.filter('tag:name-update');const picker=t.element('.package-picker-list').innerHTML;
  assert(picker.includes(newer.id));assert(!picker.includes(old.id));
});
check('Editor chips are cleared and subsequent saves cannot resurrect consumed tags',t=>{
  const originalId=t.latest('name').id;t.click('create-package');t.fields();t.select('name');t.click('confirm-create-package');
  assert.deepEqual(json(t.state.getDraftTags()),[]);assert.equal(t.state.hasTagDraft(),false);
  t.context.go('model');assert.doesNotMatch(t.page(),/>Tags · 1<\/button>/);
  t.click('version-lock');t.click('preview-edit');t.click('version-save');assert.deepEqual(t.tags(originalId),[]);assert.deepEqual(t.tags(t.latest('name').id),[]);
});
check('New pending tags on the same content survive while consumed tags are removed',t=>{
  t.context.go('model');t.click('version-lock');t.applyTags('name-update, next-fix');
  t.click('create-package');t.fields();t.select('name');t.click('confirm-create-package');
  assert.deepEqual(t.tags(t.latest('name').id),[]);assert.deepEqual(json(t.state.getDraftTags()),['next-fix']);assert.equal(t.state.hasTagDraft(),true);
  t.context.go('model');t.click('version-save');assert.deepEqual(t.tags(t.latest('name').id),['next-fix']);
});
check('An unsaved new content version keeps its own pending tags',t=>{
  const savedId=t.latest('name').id;t.context.go('model');t.click('version-lock');t.click('preview-edit');t.applyTags('name-update, next-fix');
  const draft=json(t.state.getLive().name);t.click('create-package');t.fields();t.select('name',savedId);t.click('confirm-create-package');
  assert.deepEqual(t.tags(savedId),[]);assert.deepEqual(json(t.state.getLive().name),draft);assert.deepEqual(json(t.state.getDraftTags()),['name-update','next-fix']);
  t.context.go('model');t.click('version-save');assert.notEqual(t.latest('name').id,savedId);assert.deepEqual(t.tags(t.latest('name').id),['name-update','next-fix']);
});
check('Removing a packaged selection does not recreate the consumed tags',t=>{
  const id=t.latest('name').id;t.click('create-package');t.fields();t.select('name');t.click('confirm-create-package');
  const r=t.state.releases[0],revision=r.revisions[0];t.click('remove-package-change',{key:'name',revision:revision.id});
  assert.deepEqual(t.tags(id),[]);assert.deepEqual(json(r.revisions.at(-1).graph),json(r.baseline.graph));assert.equal(r.revisions[0],revision);
});
check('Approval and final packaging do not consume additional tags',t=>{
  t.click('create-package');t.fields();t.select('name');t.click('confirm-create-package');const catalog=json(t.versions());
  t.click('review-decisions');t.click('approve-package');t.click('release-final');t.click('confirm-release-final');
  assert.equal(t.state.releases[0].state,'Packaged');assert.deepEqual(json(t.versions()),catalog);
});
console.log(`${checks} tag lifecycle checks passed. Node VM only; browser layout and backend are not tested.`);
