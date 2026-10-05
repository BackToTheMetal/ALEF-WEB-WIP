// Package composition, independent reviews, finalization and tag lifecycle.
// Runs active prototype scripts in a Node VM; no browser or backend is used.
const assert=require('node:assert/strict'),fs=require('node:fs'),path=require('node:path'),vm=require('node:vm');
const html=fs.readFileSync(path.join(__dirname,'iteration-19.html'),'utf8');
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
    if(asset==='design-19.js')code=code.replace(/\}\)\(\);\s*$/, 'globalThis.testState={releases,reviewThreads,changeCatalog,getSaved:()=>savedGraph,getLive:()=>liveGraph,getDraftTags:()=>draftTags,hasTagDraft:()=>tagsDirty};})();');
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
  t.reviewers('reviewer Local');t.approve('reviewer Local');t.as('author Local');t.click('release-final');t.click('confirm-release-final');
  assert.equal(t.state.releases[0].state,'Packaged');assert.deepEqual(json(t.versions()),catalog);
});
check('Author sees Finalize, assigned reviewer sees Review, unassigned viewer gets neither',t=>{
  t.click('open-release',{release:18});assert.match(t.page(),/data-action="release-final" disabled/);assert.doesNotMatch(t.page(),/data-action="review-decisions"/);
  assert.match(t.page(),/data-action="add-package-changes"/);assert.match(t.page(),/data-action="add-reviewers"/);
  t.as('reviewer Local');assert.match(t.page(),/data-action="review-decisions"/);assert.doesNotMatch(t.page(),/data-action="release-final"|data-action="add-package-changes"|data-action="remove-package-change"/);
  t.as('tester Local');assert.doesNotMatch(t.page(),/data-action="review-decisions"|data-action="release-final"/);
});
check('New package author is its creator and cannot self-approve or finalize without reviewers',t=>{
  t.as('editor Local');const r=t.create();assert.equal(r.author,'editor Local');assert.deepEqual(json(r.reviewers),[]);
  t.click('review-decisions');t.click('approve-package');t.click('release-final');t.click('confirm-release-final');assert.equal(r.state,'In review');assert(!r.artifact);
  t.reviewers('editor Local','reviewer Local','reviewer Local');assert.deepEqual(json(r.reviewers),['reviewer Local']);
});
check('Every assigned reviewer must approve, and only the author can finalize',t=>{
  const r=t.create();t.reviewers('reviewer Local','editor Local');const snapshot=json(r.revisions[0]);
  t.approve('reviewer Local');assert.equal(r.state,'In review');assert.equal(r.reviews.length,1);assert(!r.approval);
  t.as('author Local');t.click('release-final');t.click('confirm-release-final');assert(!r.artifact);
  t.approve('editor Local');assert.equal(r.state,'Approved');assert.equal(r.approval.revisionId,snapshot.id);
  t.click('release-final');t.click('confirm-release-final');assert(!r.artifact);
  t.as('author Local');t.click('release-final');assert.match(t.popup(),/All 2 reviewers approved/);t.click('confirm-release-final');
  assert.equal(r.state,'Packaged');assert.equal(r.artifact.digest,snapshot.digest);assert.deepEqual(json(r.revisions[0]),snapshot);assert.equal(r.deployed,'Not deployed');
});
check('Add reviewers retains existing approvals but requires the new approval too',t=>{
  const r=t.create();t.reviewers('reviewer Local');t.approve('reviewer Local');assert.equal(r.state,'Approved');
  t.as('author Local');t.reviewers('editor Local');assert.equal(r.state,'In review');assert.equal(r.reviews.length,1);assert.equal(r.revisions.length,1);
  t.approve('editor Local');assert.equal(r.state,'Approved');assert.equal(r.reviews.length,2);
});
check('Reviewers cannot add reviewers or change package contents even via direct handlers',t=>{
  const r=t.create();t.reviewers('reviewer Local');t.as('reviewer Local');const before=json(r),catalog=json(t.versions());
  t.click('add-reviewers');t.event('change',{dataset:{reviewer:'tester Local'},checked:true});t.click('confirm-add-reviewers');
  t.click('add-package-changes');t.select('test');t.click('confirm-add-package-changes');t.click('remove-package-change',{key:'name',revision:r.revisions[0].id});
  assert.deepEqual(json(r),before);assert.deepEqual(json(t.versions()),catalog);
});
check('Add changes retains exact existing contents and baseline, creating an independent revision',t=>{
  const r=t.create(['name']);const first=r.revisions[0],baseline=r.baseline,other=json(t.state.releases.slice(1)),saved=json(t.state.getSaved());
  t.click('add-package-changes');assert.match(t.popup(),/Add changes to v1.3.2/);assert.doesNotMatch(t.popup(),/id="candidate-version"|id="package-baseline"|View change/);
  assert.doesNotMatch(t.popup(),/data-package-change="name"/);t.select('test');t.select('leeg');t.click('confirm-add-package-changes');
  assert.equal(r.revisions.length,2);assert.equal(r.baseline,baseline);assert.equal(r.revisions[0],first);
  const expected=json(first.graph);expected.test=saved.test;delete expected.leeg;assert.deepEqual(json(r.revisions[1].graph),expected);
  assert.deepEqual(json(t.state.releases.slice(1)),other);assert.deepEqual(t.tags(t.latest('test').id),[]);assert.deepEqual(t.tags(t.latest('leeg').id),[]);assert.deepEqual(t.tags(t.latest('service').id),['name-update']);
});
check('Adding changes after approval invalidates all old approvals while preserving history and comments',t=>{
  const r=t.create();t.reviewers('reviewer Local','editor Local');t.approve('reviewer Local');t.approve('editor Local');
  const reviews=json(r.reviews),comments=json(t.state.reviewThreads);t.as('author Local');t.click('add-package-changes');t.select('test');t.click('confirm-add-package-changes');
  assert.equal(r.state,'In review');assert.equal(r.approval,null);assert.deepEqual(json(r.reviews),reviews);assert.deepEqual(json(t.state.reviewThreads),comments);assert.match(t.page(),/Reviewers · 0\/2 approved/);
  t.approve('reviewer Local');assert.equal(r.state,'In review');t.approve('editor Local');assert.equal(r.state,'Approved');assert.equal(r.approval.revisionId,r.revisions[1].id);
});
check('Cancelled, empty and stale Add changes operations preserve tags and package contents',t=>{
  const r=t.create();const original=json(r),tags=json(t.versions());
  t.click('add-package-changes');t.select('test');t.click('cancel-package');assert.deepEqual(json(r),original);assert.deepEqual(json(t.versions()),tags);
  t.click('add-package-changes');t.click('confirm-add-package-changes');assert.deepEqual(json(r),original);
  t.select('test');t.click('remove-package-change',{key:'name',revision:r.revisions[0].id});const changed=json(r);
  t.click('confirm-add-package-changes');assert.deepEqual(json(r),changed);assert.deepEqual(json(t.versions()),tags);assert.match(t.element('#package-selection-error').textContent,/package or viewer changed/);
});
check('Add changes can select a retained tagged version without losing other selected contents',t=>{
  const r=t.create(['config']),old=json(t.latest('name'));
  t.context.go('model');t.click('version-lock');t.click('preview-edit');t.applyTags('next-fix');t.click('version-save');const newer=json(t.latest('name'));
  t.click('open-release',{release:r.id});t.click('add-package-changes');t.filter('tag:name-update');t.select('name',old.id);t.click('confirm-add-package-changes');
  assert.deepEqual(json(r.revisions.at(-1).graph.name),old.node);assert.deepEqual(json(r.revisions.at(-1).graph.config),json(r.revisions[0].graph.config));assert.deepEqual(t.tags(old.id),[]);assert.deepEqual(t.tags(newer.id),['next-fix']);
});
check('Newer saved versions can explicitly replace an included change and synchronize Editor tags',t=>{
  const r=t.create(),first=json(r.revisions[0]);
  t.context.go('model');t.click('version-lock');t.click('preview-edit');t.applyTags('revision-fix');t.click('version-save');const newer=json(t.latest('name'));
  t.click('open-release',{release:r.id});t.click('add-package-changes');t.select('name',newer.id);t.click('confirm-add-package-changes');
  assert.deepEqual(json(r.revisions[0]),first);assert.deepEqual(json(r.revisions[1].graph.name),newer.node);assert.deepEqual(t.tags(newer.id),[]);assert.deepEqual(json(t.state.getDraftTags()),[]);
});
check('Removing an approved change restores baseline and requires fresh approval',t=>{
  const r=t.create(['name','test']);t.reviewers('reviewer Local');t.approve('reviewer Local');t.as('author Local');
  t.click('remove-package-change',{key:'test',revision:r.revisions[0].id});assert.equal(r.state,'In review');assert.equal(r.approval,null);assert.equal(r.reviews.length,1);
  assert.deepEqual(json(r.revisions.at(-1).graph.test),json(r.baseline.graph.test));assert.match(t.page(),/Reviewers · 0\/1 approved/);
});
check('Change requests block finalization and other approvals cannot override them',t=>{
  const r=t.create();t.reviewers('reviewer Local','editor Local');t.as('reviewer Local');t.click('review-decisions');t.click('request-correction');
  t.element('#correction-root').value='name';t.element('#correction-feedback').value='Please check this mapping.';t.click('confirm-correction');assert.equal(r.state,'Changes requested');
  const thread=t.state.reviewThreads.at(-1);assert.equal(thread.messages[0].author,'reviewer Local');assert.equal(thread.packageId,r.revisions[0].id);
  t.approve('editor Local');assert.equal(r.state,'Changes requested');t.as('author Local');t.click('comment-resolve',{thread:thread.id});t.click('release-final');t.click('confirm-release-final');assert(!r.artifact);
  t.approve('reviewer Local');assert.equal(r.state,'Approved');assert.equal(r.reviews.length,3);
});
check('Diff comments and replies record the current person and retain revision and line anchors',t=>{
  const r=t.create(),p=r.revisions[0],anchor={package:p.id,source:p.id,root:'name',side:'b',line:'4'};
  t.reviewers('reviewer Local');t.as('reviewer Local');assert.match(t.page(),/aria-label="Comment on After line 4"/);
  t.click('comment-start',anchor);t.element('#inline-comment-text').value='<Please check> the name';t.click('comment-post',anchor);
  const thread=t.state.reviewThreads.at(-1);assert.equal(thread.messages[0].author,'reviewer Local');assert.equal(thread.line,4);assert.equal(thread.packageId,p.id);
  t.as('author Local');t.element('#reply-'+thread.id).value='I will update it.';t.click('comment-reply',{thread:thread.id});assert.equal(thread.messages[1].author,'author Local');
  t.click('diff-unified');assert.match(t.page(),/&lt;Please check&gt;/);const before=json(thread);
  t.click('add-package-changes');t.select('test');t.click('confirm-add-package-changes');assert.deepEqual(json(thread),before);assert.doesNotMatch(t.page(),/&lt;Please check&gt;/);
  t.click('inspect-package',{index:0});assert.match(t.page(),/&lt;Please check&gt;/);
});
check('Role switching and closed dialogs invalidate pending review and finalization decisions',t=>{
  const r=t.create();t.reviewers('reviewer Local');t.as('reviewer Local');t.click('review-decisions');t.as('author Local');t.click('approve-package');assert.equal(r.reviews.length,0);
  t.as('reviewer Local');t.click('review-decisions');t.click('close-modal');t.click('approve-package');assert.equal(r.reviews.length,0);
  t.approve('reviewer Local');t.as('author Local');t.click('release-final');t.as('reviewer Local');t.click('confirm-release-final');assert.equal(r.state,'Approved');assert(!r.artifact);
});
check('Finalized and historical packages cannot be edited, assigned or approved',t=>{
  const r=t.create();t.reviewers('reviewer Local');t.approve('reviewer Local');t.as('author Local');t.click('release-final');t.click('confirm-release-final');const original=json(r);
  t.click('add-package-changes');t.select('test');t.click('confirm-add-package-changes');t.reviewers('tester Local');t.click('remove-package-change',{key:'name',revision:r.revisions[0].id});t.approve('reviewer Local');assert.deepEqual(json(r),original);
  t.as('author Local');t.click('open-release',{release:18});const historical=json(t.state.releases.find(r=>r.id===18));t.click('inspect-package',{index:0});assert.doesNotMatch(t.page(),/data-action="release-final"|data-action="add-package-changes"|data-action="add-reviewers"/);
  t.reviewers('tester Local');t.as('reviewer Local');t.click('review-decisions');t.click('approve-package');assert.deepEqual(json(t.state.releases.find(r=>r.id===18)),historical);
});
check('Opening another package prevents a stale Add changes dialog from mutating either package',t=>{
  const r=t.create(),before=json(t.state.releases),tags=json(t.versions());t.click('add-package-changes');t.select('test');t.click('open-release',{release:18});t.click('confirm-add-package-changes');
  assert.deepEqual(json(t.state.releases),before);assert.deepEqual(json(t.versions()),tags);assert.equal(r.revisions.length,1);
});
console.log(`${checks} workflow checks passed. Node VM only; browser layout and backend are not tested.`);
