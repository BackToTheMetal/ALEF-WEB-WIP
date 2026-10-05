// Package composition, independent reviews, finalization and tag lifecycle.
// Runs active prototype scripts in a Node VM; no browser or backend is used.
const assert=require('node:assert/strict'),fs=require('node:fs'),path=require('node:path'),vm=require('node:vm');
const html=fs.readFileSync(path.join(__dirname,'iteration-20.html'),'utf8');
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
    if(asset==='design-20.js')code=code.replace(/\}\)\(\);\s*$/, 'globalThis.testState={releases,reviewThreads,changeCatalog,rootSessions,linkedPackages,packageStatus,getActive:()=>activeRoot,isDirty:()=>dirty,getSaved:()=>savedGraph,getLive:()=>liveGraph,getDraftTags:()=>draftTags,hasTagDraft:()=>tagsDirty};})();');
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
  const as=person=>event('change',{id:'review-viewer',value:person});
  const reviewers=(...names)=>{click('add-reviewers');names.forEach(person=>event('change',{dataset:{reviewer:person},checked:true}));click('confirm-add-reviewers');};
  const approve=person=>{as(person);click('review-decisions');click('approve-package');};
  const create=(keys=['name'],version='1.3.2')=>{click('create-package');fields(version);keys.forEach(key=>select(key));click('confirm-create-package');return state.releases[0];};
  return {context,state,element,click,event,input,versions,tags,latest,select,filter,fields,applyTags,as,reviewers,approve,create,popup:()=>element('#modal-host').innerHTML,page:()=>element('#screen').innerHTML};
}
let checks=0;
function check(name,run){run(boot());checks++;console.log('PASS '+name);}
const root=(t,key)=>{t.context.go('model');t.click('root',{rootId:t.state.getLive()[key].id});};
const edit=(t,key)=>{root(t,key);t.click('version-lock');t.click('preview-edit');};
const choose=(t,...ids)=>{ids.forEach(id=>t.event('change',{dataset:{editPackage:String(id)},checked:true}));t.click('confirm-edit-destinations');};
const current=r=>r.revisions.at(-1);
check('Package icons follow lock icons and distinguish linked, inherited and finalized contents',t=>{
  t.context.go('model');const html=t.page(),row=id=>html.match(new RegExp('<button[^>]*data-root-id="'+id+'"[\\s\\S]*?</button>'))[0];
  assert.match(row('root-name'),/tree-package linked/);assert.match(row('root-ibm'),/tree-package included/);
  assert(row('root-test').indexOf('tree-lock other')<row('root-test').indexOf('tree-package linked'));
  assert.match(row('root-ibm'),/choose this package when editing/);assert.match(row('root-name'),/saving edits also updates this package/);
  assert.match(row('root-ibm'),/tree-lock available/);assert.equal(t.state.packageStatus('ibm').state,'included');
});
check('Selecting ibm displays its own root, version and inspector',t=>{
  root(t,'ibm');assert.equal(t.state.getActive(),'ibm');assert.match(t.page(),/<span class="name">ibm<\/span>/);assert.match(t.page(),/Invoerberichtmapping ibm/);
  assert.doesNotMatch(t.page(),/class="native-surface"|class="inspector-capture"/);assert.match(t.page(),/root-preview-inspector/);
});
check('An unchanged root prompts before editing and cancellation leaves all contents untouched',t=>{
  const before=json(t.state.releases),workspace=json(t.state.getSaved());edit(t,'ibm');
  assert.match(t.popup(),/Where should this change go/);assert.match(t.popup(),/Currently unchanged in this package/);assert.equal(t.state.isDirty(),false);
  t.click('cancel-edit-destinations');assert.equal(t.state.isDirty(),false);assert.deepEqual(json(t.state.releases),before);assert.deepEqual(json(t.state.getSaved()),workspace);
});
check('Workspace-only choice retains every package and offers the new version in New package',t=>{
  const before=json(t.state.releases),previous=json(t.state.getSaved().ibm);edit(t,'ibm');choose(t);assert.equal(t.state.isDirty(),true);t.applyTags('ibm-fix');t.click('version-save');
  assert.deepEqual(json(t.state.releases),before);assert.notEqual(t.state.getSaved().ibm.snapshot,previous.snapshot);assert.equal(t.state.getSaved().ibm.version,previous.version+1);
  assert.deepEqual(t.tags(t.latest('ibm').id),['ibm-fix']);t.click('create-package');t.filter('tag:ibm-fix');assert.match(t.element('.package-picker-list').innerHTML,/data-package-change="ibm"/);
});
check('Choosing a package links the formerly unchanged root and preserves its baseline and other roots',t=>{
  const r=t.state.releases.find(r=>r.id===18),first=current(r),baseline=r.baseline,others=json(t.state.releases.filter(x=>x!==r));
  edit(t,'ibm');choose(t,18);t.applyTags('ibm-fix');t.click('version-save');
  assert.equal(r.baseline,baseline);assert.equal(r.revisions.length,3);const expected=json(first.graph);expected.ibm=json(t.state.getSaved().ibm);assert.deepEqual(json(current(r).graph),expected);assert.equal(r.revisions[1],first);
  assert.deepEqual(json(t.state.releases.filter(x=>x!==r)),others);assert.deepEqual(t.tags(t.latest('ibm').id),[]);assert.equal(t.state.packageStatus('ibm').state,'linked');
  t.click('preview-edit');assert.equal(t.popup(),'');t.click('version-save');assert.equal(r.revisions.length,4);assert.deepEqual(json(current(r).graph.ibm),json(t.state.getSaved().ibm));
});
check('Linked edits update on save and invalidate approvals while keeping review history',t=>{
  const r=t.state.releases.find(r=>r.id===18);t.click('open-release',{release:18});t.approve('reviewer Local');assert.equal(r.state,'Approved');const old=current(r),reviews=json(r.reviews),threads=json(t.state.reviewThreads);
  t.as('author Local');edit(t,'name');assert.equal(t.popup(),'');assert.equal(r.revisions.length,2);t.applyTags('linked-fix');t.click('version-save');
  assert.equal(r.revisions.length,3);assert.equal(r.state,'In review');assert.equal(r.approval,null);assert.equal(r.revisions[1],old);assert.deepEqual(json(r.reviews),reviews);assert.deepEqual(json(t.state.reviewThreads),threads);
  assert.deepEqual(json(current(r).graph.name),json(t.state.getSaved().name));assert.deepEqual(t.tags(t.latest('name').id),[]);assert.deepEqual(json(t.state.getDraftTags()),[]);
});
check('The same selected saved version in multiple open packages updates them together',t=>{
  const r=t.create(['name']),existing=t.state.releases.find(r=>r.id===18),old=current(r),oldExisting=current(existing);edit(t,'name');assert.match(t.page(),/Workspace \+ 2 packages/);t.click('version-save');
  assert.equal(r.revisions.length,2);assert.equal(existing.revisions.length,3);assert.equal(r.revisions[0],old);assert.equal(existing.revisions[1],oldExisting);
  assert.deepEqual(json(current(r).graph.name),json(current(existing).graph.name));assert.equal(r.state,'In review');assert.equal(existing.state,'In review');
});
check('An inherited root can explicitly update several packages, leaving unselected packages untouched',t=>{
  const r=t.create(['name']),other=t.create(['service'],'1.3.3'),oldOther=json(other);edit(t,'ibm');choose(t,18,r.id);t.click('version-save');
  assert.deepEqual(json(current(r).graph.ibm),json(t.state.getSaved().ibm));assert.deepEqual(json(current(t.state.releases.find(x=>x.id===18)).graph.ibm),json(t.state.getSaved().ibm));assert.deepEqual(json(other),oldOther);
});
check('Retained older selected versions do not silently follow newer workspace saves',t=>{
  const old=json(t.latest('name'));edit(t,'name');t.click('version-save');
  t.click('create-package');t.fields();t.select('name',old.id);t.click('confirm-create-package');const pinned=t.state.releases[0],before=json(pinned);
  t.context.go('model');t.click('preview-edit');t.click('version-save');assert.deepEqual(json(pinned),before);assert.notEqual(current(pinned).graph.name.snapshot,t.state.getSaved().name.snapshot);
});
check('Saving one root does not save a second root draft or change its package selection',t=>{
  const r=t.state.releases.find(r=>r.id===18),savedName=json(t.state.getSaved().name),packagedName=json(current(r).graph.name);edit(t,'name');const draft=json(t.state.getLive().name);
  edit(t,'ibm');choose(t);t.click('version-save');assert.deepEqual(json(t.state.getSaved().name),savedName);assert.deepEqual(json(current(r).graph.name),packagedName);assert.deepEqual(json(t.state.getLive().name),draft);
  root(t,'name');assert.equal(t.state.isDirty(),true);t.click('version-save');assert.deepEqual(json(t.state.getSaved().name),draft);assert.deepEqual(json(current(r).graph.name),draft);
});
check('Removing a linked change stops subsequent saves from updating that package',t=>{
  const r=t.create(['name']);edit(t,'name');t.click('open-release',{release:r.id});t.click('remove-package-change',{key:'name',revision:current(r).id});const before=json(r);
  t.context.go('model');t.click('version-save');assert.deepEqual(json(r),before);assert.notEqual(t.state.getSaved().name.snapshot,current(r).graph.name.snapshot);
});
check('Adding a workspace-only saved change makes its following edits linked',t=>{
  edit(t,'ibm');choose(t);t.click('version-save');const r=t.state.releases.find(r=>r.id===18);
  t.click('open-release',{release:18});t.click('add-package-changes');t.select('ibm');t.click('confirm-add-package-changes');assert.equal(t.state.packageStatus('ibm').state,'linked');
  t.context.go('model');t.click('preview-edit');t.click('version-save');assert.deepEqual(json(current(r).graph.ibm),json(t.state.getSaved().ibm));
});
check('Tag-only saves do not create package revisions or invalidate approvals',t=>{
  const r=t.state.releases.find(r=>r.id===18);t.click('open-release',{release:18});t.approve('reviewer Local');t.as('author Local');const before=json(r);
  root(t,'name');t.click('version-lock');t.applyTags('next-fix');t.click('version-save');assert.deepEqual(json(r),before);assert.deepEqual(t.tags(t.latest('name').id),['next-fix']);
});
check('Foreign locks and missing locks prevent edits and saves',t=>{
  const saved=json(t.state.getSaved()),packages=json(t.state.releases);root(t,'ibm');t.click('preview-edit');t.click('version-save');assert.deepEqual(json(t.state.getSaved()),saved);
  ['test','move2','ubc'].forEach(key=>{edit(t,key);t.click('version-save');assert.equal(t.state.isDirty(),false);assert.match(t.page(),/Locked elsewhere/);});assert.deepEqual(json(t.state.releases),packages);assert.deepEqual(json(t.state.getSaved()),saved);
});
check('Another preview identity cannot change packages owned by the author through Editor save',t=>{
  const before=json(t.state.releases);t.as('editor Local');edit(t,'name');t.click('version-save');assert.deepEqual(json(t.state.releases),before);assert.notEqual(t.state.getSaved().name.snapshot,current(t.state.releases[0]).graph.name.snapshot);
});
check('A stale destination popup refreshes choices without applying edits',t=>{
  edit(t,'ibm');t.event('change',{dataset:{editPackage:'18'},checked:true});const saved=json(t.state.getSaved());
  t.click('open-release',{release:18});t.click('remove-package-change',{key:'name',revision:current(t.state.releases[0]).id});t.context.go('model');t.click('confirm-edit-destinations');
  assert.equal(t.state.isDirty(),false);assert.deepEqual(json(t.state.getSaved()),saved);assert.match(t.popup(),/Where should this change go/);
});
check('Finalization during an unsaved edit preserves the artifact and saves a new workspace version',t=>{
  const r=t.state.releases.find(r=>r.id===18);edit(t,'name');const draft=json(t.state.getLive().name);
  t.click('open-release',{release:18});t.approve('reviewer Local');t.as('author Local');t.click('release-final');t.click('confirm-release-final');assert.equal(r.state,'Packaged');const frozen=json(r);
  root(t,'name');t.click('version-lock');t.click('version-save');assert.deepEqual(json(r),frozen);assert.equal(t.state.getSaved().name.version,draft.version+1);assert.deepEqual(json(t.state.getSaved().name.content),draft.content);
  assert.match(t.page(),/Workspace only/);assert.equal(t.state.packageStatus('ibm').state,'finalized');
});
check('A destination finalized before save requires a new choice and cannot receive the draft',t=>{
  const r=t.state.releases.find(r=>r.id===18);edit(t,'ibm');choose(t,18);const saved=json(t.state.getSaved());
  t.click('open-release',{release:18});t.approve('reviewer Local');t.as('author Local');t.click('release-final');t.click('confirm-release-final');const frozen=json(r);
  root(t,'ibm');t.click('version-lock');t.click('version-save');assert.match(t.popup(),/Where should this change go/);assert.deepEqual(json(t.state.getSaved()),saved);
  choose(t);assert.deepEqual(json(r),frozen);assert.notEqual(t.state.getSaved().ibm.snapshot,saved.ibm.snapshot);
});
check('A package can still be independently reviewed and finalized after a linked save',t=>{
  const r=t.create(['name']);t.reviewers('reviewer Local','editor Local');edit(t,'name');t.click('version-save');const savedRevision=current(r);
  t.click('open-release',{release:r.id});t.approve('reviewer Local');assert.equal(r.state,'In review');t.approve('editor Local');assert.equal(r.state,'Approved');
  t.as('author Local');t.click('release-final');t.click('confirm-release-final');assert.equal(r.state,'Packaged');assert.equal(r.artifact.revisionId,savedRevision.id);assert.equal(r.artifact.digest,savedRevision.digest);assert.equal(r.deployed,'Not deployed');
});
check('Diff comments remain on the previous revision after Editor automatically updates the package',t=>{
  const r=t.state.releases.find(r=>r.id===18),p=current(r),anchor={package:p.id,source:p.id,root:'name',side:'b',line:'4'};
  t.click('open-release',{release:18});t.as('reviewer Local');t.click('comment-start',anchor);t.element('#inline-comment-text').value='Check the linked save';t.click('comment-post',anchor);const thread=json(t.state.reviewThreads.at(-1));
  t.as('author Local');edit(t,'name');t.click('version-save');t.click('open-release',{release:18});assert.doesNotMatch(t.page(),/Check the linked save/);
  t.click('inspect-package',{index:1});assert.match(t.page(),/Check the linked save/);assert.deepEqual(json(t.state.reviewThreads.at(-1)),thread);
});
console.log(`${checks} editor-package workflow checks passed. Node VM only; browser layout and backend are not tested.`);
