import {el,icon,button,buttonContent,field,option,modal,badge,segments,heading,loadingState} from './interface-ui.js';
import {mpsDiff,mpsComparisonSession} from './mps-diff.js';
import {compareContents} from './model-diff.js';
import {paintPackageChecks} from './package-checks.js';
import './project-view.js';

/** Real application views. All identities, permissions, contents and decisions
 * come from authenticated APIs; the saved design study is never loaded here. */
export function installInterface({api,identity,editor,deployment}) {
  const retryKeys=new Map();
  const request=async(method,url,body)=>{const key=method+' '+url+' '+JSON.stringify(body);if(method!=='GET'&&!retryKeys.has(key))retryKeys.set(key,crypto.randomUUID());try{const response=await api(url,{method,...(body===undefined?{}:{body:JSON.stringify(body)}),...(method==='GET'?{}:{headers:{'Idempotency-Key':retryKeys.get(key)}})});const result=await response.json();retryKeys.delete(key);return result;}catch(error){if(error.status&&error.status<500)retryKeys.delete(key);throw error;}};
  const permit=p=>identity.workspacePermissions?.includes(p);
  const workspace=document.querySelector('.project-workspace'),topbar=document.querySelector('.topbar'),actions=document.querySelector('.actions');
  const page=el('section',undefined,'interface-page');page.id='workflow-page';page.hidden=true;workspace.after(page);
  const nav=el('nav',undefined,'primary-nav');nav.setAttribute('aria-label','Workspace');topbar.querySelector('.brand').after(nav);
  const toolbar=el('div',undefined,'editor-toolbar'),state=el('span','Latest workspace','editor-state');toolbar.append(state,actions);workspace.querySelector('.workspace').prepend(toolbar);
  const account=el('div',undefined,'identity-actions'),session=document.querySelector('.session'),settingsButton=button('',()=>navigate('settings'));
  settingsButton.setAttribute('aria-label','Settings');settingsButton.title='Settings';settingsButton.append(icon('settings'));
  editor.logout.classList.add('icon-action');editor.logout.setAttribute('aria-label','Log out');editor.logout.title='Log out';
  account.append(session,settingsButton,editor.logout);topbar.append(account);
  editor.remove.classList.add('icon-action');editor.remove.setAttribute('aria-label','Delete root');editor.remove.title='Delete selected root';editor.lock.after(editor.remove);editor.save.classList.add('save-button');
  const rail=el('div',undefined,'project-rail'),reopen=button('',()=>collapse(false));reopen.append(icon('panel'));reopen.setAttribute('aria-label','Show project panel');rail.append(reopen);workspace.prepend(rail);
  function collapse(value){workspace.classList.toggle('project-collapsed',value);document.getElementById('project-view').collapsed=value;dispatchEvent(new Event('resize'));}
  document.getElementById('project-view').addEventListener('toggle-panel',()=>collapse(true));
  const navItems=[['model','Editor',true],['changes','Compare',permit('REVIEW')],['releases','Packaging',permit('REVIEW')],['deployments','Deployment',permit('READ')||permit('DEPLOY')||permit('MANAGE')]];
  for(const[id,label,visible]of navItems){if(!visible)continue;const b=button(label,()=>navigate(id),'');b.dataset.route=id;nav.append(b);}
  const tagButton=button('Tags',editTags);editor.lock.parentNode.insertBefore(tagButton,editor.lock);let rootState={},routeEpoch=0,currentPackage=null,releaseTab='contents',contentFilter='changes',selectedContent=null;
  let comparisonSelection={source:'workspace',against:'latest'};
  let statusTimer,routeTimer,statusRequest=0,lockRefreshQueued=false;
  const route=()=>location.hash.slice(1)||'model';
  function navigate(value){if(route()===value)return render();location.hash=value;}
  function errorHost(){const p=el('p',undefined,'interface-error');p.id='workflow-error';p.role='alert';p.hidden=true;return p;}
  document.addEventListener('alef-interface-error',event=>{editor.message.textContent=event.detail.message;editor.message.dataset.error='true';});
  async function refreshStatus(force=false){
    if(document.hidden&&!force)return;
    const current=++statusRequest;
    const data=await request('GET','/api/v1/packages/editor-status');
    if(current!==statusRequest)return;
    rootState=data.roots||{};paintStatus();
  }
  function paintStatus(){
    const statuses={};for(const model of editor.graph()?.models||[])for(const root of model.roots||[]){const info=rootState[root.rootId];if(info)statuses[model.reference+'/'+root.id]=info;}
    document.getElementById('project-view').statuses=statuses;updateEditorState();
  }
  function updateEditorState(){const selected=editor.selected(),info=rootState[selected?.rootId];tagButton.disabled=!selected?.rootId;buttonContent(tagButton,info?.tags?.length?'Tags · '+info.tags.length:'Tags','tag');const packages=info?.packages?.filter(p=>p.linked)||[];state.textContent=packages.length?'Workspace · '+packages.map(p=>'v'+p.version).join(', '):'Latest workspace';state.title=packages.length?'Saving this changed root updates its linked open packages and requires review again.':'Editing the latest saved workspace';}
  async function editTags(){const root=editor.selected();if(!root)return;const info=rootState[root.rootId]||{},panel=modal('Tags for this change'),tags=field(panel.body,'Tags, separated by commas','text',(info.tags||[]).join(', '));panel.body.append(el('p','Tags identify a saved version. They are cleared when that version is added to a package.'));panel.footer.append(button('Save tags',async()=>{const values=[...new Set(tags.value.split(',').map(t=>t.trim()).filter(Boolean))];if(editor.dirty().includes(root.rootId)){editor.setTags(root.rootId,values);panel.node.close();editor.message.textContent='Tags will be stored with this change when you save.';}else{await request('PUT','/api/v1/packages/tags',{rootId:root.rootId,version:info.version,tags:values});panel.node.close();await refreshStatus();}},'interface-button primary'));}
  document.addEventListener('alef-locks-changed',event=>{
    // The lease response is authoritative already. Paint it without waiting for
    // package status, and prevent a slower pre-action poll undoing this update.
    ++statusRequest;
    for(const change of event.detail?.updates||[])rootState[change.rootId]={...rootState[change.rootId],lock:change.lock};
    paintStatus();
    if(!lockRefreshQueued){lockRefreshQueued=true;queueMicrotask(()=>{lockRefreshQueued=false;refreshStatus(true).catch(()=>{});});}
  });document.addEventListener('alef-root-selected',updateEditorState);document.addEventListener('alef-workspace-refreshed',()=>refreshStatus().catch(()=>{}));document.addEventListener('alef-workspace-saved',()=>refreshStatus().catch(()=>{}));
  async function render(){
    const epoch=++routeEpoch;clearInterval(routeTimer);const parts=route().split('/'),view=parts[0],isEditor=view==='model';workspace.hidden=!isEditor;page.hidden=isEditor;
    nav.querySelectorAll('button').forEach(b=>b.setAttribute('aria-current',(view==='release'?b.dataset.route==='releases':b.dataset.route===view)?'page':'false'));
    if(isEditor){page.replaceChildren();dispatchEvent(new Event('resize'));return;}
    const loading=loadingState(view==='release'?'Loading package…':view==='changes'?'Loading comparison…':'Loading '+({releases:'packages',deployments:'deployments',settings:'settings'}[view]||'workspace')+'…',view==='release'?'Retrieving package details.':'Retrieving saved information.');
    page.replaceChildren(errorHost(),loading);
    try{let content;if(view==='releases')content=await packaging();else if(view==='release')content=await packageDetail(parts[1],p=>loading.update(packageTitle(p),'Loading saved package contents.'));else if(view==='changes')content=await comparison();else if(view==='deployments')content=await deployments();else if(view==='settings')content=settings();else{navigate('model');return;}
      if(epoch!==routeEpoch)return;page.replaceChildren(errorHost(),content);
      if(view==='release')watchPackage(currentPackage,content.updatePackage,epoch);
    }catch(error){if(epoch!==routeEpoch)return;page.replaceChildren(errorHost());page.firstChild.textContent=error.message;page.firstChild.hidden=false;page.append(button('Retry',render));}
  }
  function watchPackage(p,update,epoch=routeEpoch){
    clearInterval(routeTimer);if(!['QUEUED','RUNNING'].includes(p.release?.jobState))return;
    let polling=false;const timer=routeTimer=setInterval(async()=>{
      if(polling||document.querySelector('dialog')||document.hidden)return;polling=true;
      try{const next=await request('GET','/api/v1/packages/'+encodeURIComponent(p.id));if(epoch===routeEpoch&&timer===routeTimer)await update(next);}
      catch{/* Retain the visible package on a transient polling failure. */}finally{polling=false;}
    },4000);
  }
  function packageContentsKey(p){
    const composition=p.release?.provenance?.composition;if(!Array.isArray(composition?.roots))return null;
    const roots=composition.roots.map(r=>[r.rootId,r.version,r.modelKey,r.nodeId,!!r.deleted]).sort((a,b)=>a[0].localeCompare(b[0]));
    return JSON.stringify([p.baselineCandidateId,composition.baselineDigest,roots]);
  }
  function table(headers){const card=el('div',undefined,'interface-card'),t=el('table',undefined,'interface-table'),thead=el('thead'),row=el('tr'),body=el('tbody');for(const label of headers)row.append(el('th',label));thead.append(row);t.append(thead,body);card.append(t);return{card,body};}
  function openPackage(p){if(p.legacy)return deployment.legacy(p.candidateId);navigate('release/'+p.id);}
  async function deliverPackage(p,target){const value=await request('GET',p.legacy?'/api/v1/releases/'+p.candidateId:'/api/v1/packages/'+p.id);return deployment.deploy(p.legacy?value:value.release,target);}
  function packageTitle(p){return p.version?'v'+p.version+' · '+p.title:p.title;}
  function packageLabel(p){return p.version?'v'+p.version:p.title;}
  async function packaging(){const items=await request('GET','/api/v1/packages'),container=el('div');container.append(heading('Packaging','Prepare releases and production fixes in parallel.',permit('SUBMIT')?[button('+ New package',()=>packagePicker(),'interface-button primary')]:[]));
    const filter=el('div',undefined,'interface-filter'),search=el('input');search.type='search';search.placeholder='Search packages…';search.setAttribute('aria-label','Search packages');const {card,body}=table(['Version','Package','Status','Review','']);let selected='all';
    const refresh=()=>{body.replaceChildren();for(const p of items.filter(p=>(selected==='all'||(selected==='open'?!p.finalized:p.finalized))&&(p.title+' '+p.version).toLowerCase().includes(search.value.toLowerCase()))){const row=el('tr'),version=el('td'),title=el('td'),status=el('td'),review=el('td'),action=el('td');version.append(button(packageLabel(p),()=>openPackage(p),'link'));if(p.legacy)version.replaceChildren(document.createTextNode('—'));title.append(el('strong',p.title),el('small',p.legacy?'Retained release':'Revision '+p.revision+(p.baselineVersion?' · From v'+p.baselineVersion:'')));status.append(badge(p.state));review.textContent=p.legacy?'Retained release':p.reviewers?.length?`${p.approvalCount||0} / ${p.reviewers.length} approved`:'No reviewers assigned';action.append(button('Open',()=>openPackage(p)));const remove=deletePackageButton(p);if(remove)action.append(remove);row.append(version,title,status,review,action);body.append(row);}if(!body.children.length){const row=el('tr'),cell=el('td','No packages match this view.','interface-empty');cell.colSpan=5;row.append(cell);body.append(row);}};
    const tabs=el('span');function select(value){selected=value;tabs.replaceChildren(segments([['all','All'],['open','Open'],['packaged','Packaged']],value,select));refresh();}search.oninput=refresh;filter.append(search,tabs);container.append(filter,card);select('all');return container;
  }
  function deletePackageButton(p){
    if(!p.canDelete&&!p.deleteBlockedReason)return null;
    const remove=button('Delete package',()=>deletePackage(p),'interface-button package-delete');buttonContent(remove,'Delete package','trash');
    remove.disabled=!p.canDelete;remove.title=p.deleteBlockedReason||'Delete '+packageTitle(p);if(p.deleteBlockedReason)remove.setAttribute('aria-description',p.deleteBlockedReason);return remove;
  }
  async function deletePackage(p){
    const panel=modal('Delete '+packageTitle(p)+'?');panel.body.append(el('p','Remove this package from Packaging? Saved workspace content and audit history are retained. This version number stays reserved.'));
    const confirm=button('Delete package',async()=>{
      await request('DELETE','/api/v1/packages/'+encodeURIComponent(p.id),{expectedRevision:p.revision});
      panel.node.close();currentPackage=null;releaseTab='contents';
      if(location.hash==='#releases')await render();else navigate('releases');await refreshStatus();
    },'interface-button package-delete confirm');buttonContent(confirm,'Delete package','trash');panel.footer.append(confirm);
  }
  async function packagePicker(existing){const panel=modal(existing?'Add changes to '+packageLabel(existing):'New package');panel.body.append(el('p',existing?'Choose saved versions to add or replace. Other package contents stay as they are.':'Start from a packaged version and select the changes for this package. Unchanged baseline content is retained.'));
    const fields=el('div',undefined,'field-row');panel.body.append(fields);const version=existing?null:field(fields,'Package version'),title=existing?null:field(fields,'Name');
    if(version){
      version.placeholder='e.g. 1.4.1';
      const hint=el('small','Use major.minor.patch, for example 1.4.1.');hint.id='package-version-format';
      version.setAttribute('aria-describedby',hint.id);version.parentElement.append(hint);
    }
    const baseline=existing?null:field(panel.body,'Start from packaged version','select');const filters=el('div',undefined,'interface-filter'),search=el('input'),tag=el('select');search.type='search';search.placeholder='Filter files…';search.setAttribute('aria-label','Filter package changes');tag.setAttribute('aria-label','Filter by tag');filters.append(search,tag);panel.body.append(filters);
    const list=el('div',undefined,'package-picker'),count=el('p',undefined,'interface-muted');panel.body.append(list,count);let data,selected=new Map(),generation=0;
    const visible=()=>(data?.changes||[]).filter(item=>(!tag.value||item.tags.includes(tag.value))&&((item.name||'')+' '+item.modelName+' '+item.rootId).toLowerCase().includes(search.value.toLowerCase()));
    function refresh(){list.replaceChildren();const items=visible();for(const item of items){const label=el('label'),check=el('input'),text=el('span',undefined,'item-label');check.type='checkbox';check.checked=selected.get(item.rootId)===item.version;check.onchange=()=>{if(check.checked)selected.set(item.rootId,item.version);else selected.delete(item.rootId);refresh();};text.append(el('span',item.name||editor.rootName(item.rootId)||item.nodeId),el('small',(item.modelName||item.modelKey)+' · '+(item.current?'Latest saved version':'Retained version')+(item.tags.length?' · '+item.tags.join(', '):'')));label.append(check,icon('file'),text,badge(item.kind));list.append(label);}if(!list.children.length)list.append(el('p',existing&&!data.changes.length?'All available saved changes are already in this package.':'No saved changes match this filter.','interface-empty'));count.textContent=selected.size+' changes selected · '+data.baselineCount+' baseline roots retained';submit.disabled=!selected.size;all.disabled=!items.length;clear.disabled=!selected.size;}
    async function load(id){const n=++generation;submit.disabled=all.disabled=clear.disabled=true;data=null;selected.clear();list.replaceChildren(el('p','Loading saved changes…','interface-muted'));const query=new URLSearchParams();if(existing)query.set('packageId',existing.id);else if(id)query.set('baseline',id);const next=await request('GET','/api/v1/packages/options'+(query.size?'?'+query:''));if(n!==generation||!panel.node.open)return;data=next;if(existing)existing={...existing,revision:next.packageRevision};data.changes=data.changes.map(c=>({...c,name:c.name||editor.rootName(c.rootId)||c.nodeId,modelName:c.modelName||editor.modelName?.(c.modelKey)||c.modelKey}));selected=new Map();tag.replaceChildren();option(tag,'','All tags');for(const t of [...new Set(data.changes.flatMap(c=>c.tags))].sort())option(tag,t,t);refresh();}
    search.oninput=()=>{if(data)refresh();};tag.onchange=()=>{if(data)refresh();};const all=button('Select filtered',()=>{for(const item of visible())if(!selected.has(item.rootId))selected.set(item.rootId,item.version);refresh();}),clear=button('Clear',()=>{selected.clear();refresh();});filters.append(all,clear);
    const submit=button(existing?'Add selected changes':'Create package for review',async()=>{
      panel.error.hidden=true;
      if(!selected.size)throw new Error('Select at least one change.');
      if(existing){
        await request('POST',`/api/v1/packages/${existing.id}/changes`,{expectedRevision:existing.revision,versions:[...selected.values()],removeRootIds:[]});panel.node.close();await render();
      }else{
        const packageVersion=version.value.trim().replace(/^v/,''),packageName=title.value.trim();
        // Match PackageService's accepted format before sending a create request.
        if(packageVersion.length>2000||!/^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(-[0-9A-Za-z.-]+)?$/.test(packageVersion)){
          version.focus();throw new Error('Enter a package version with three numbers, for example 1.4.1 (optionally followed by -rc.1).');
        }
        if(!packageName||packageName.length>200){title.focus();throw new Error('Enter a package name of 1 to 200 characters.');}
        const p=await request('POST','/api/v1/packages',{version:packageVersion,title:packageName,baselineCandidateId:baseline.value||null,sourceRevision:data.sourceRevision,versions:[...selected.values()]});panel.node.close();navigate('release/'+p.id);
      }
      await refreshStatus();
    },'interface-button primary');submit.disabled=all.disabled=clear.disabled=true;panel.footer.append(submit);
    try{if(baseline){const initial=await request('GET','/api/v1/packages/options');option(baseline,'','Empty package · select initial contents');for(const p of initial.baselines)option(baseline,p.candidateId,packageTitle(p));baseline.value=initial.baselineCandidateId||initial.baselines[0]?.candidateId||'';baseline.onchange=()=>load(baseline.value).catch(e=>{panel.error.textContent=e.message;panel.error.hidden=false;});}await load(baseline?.value);}catch(error){panel.error.textContent=error.message;panel.error.hidden=false;submit.disabled=true;}
  }
  async function removePackageChanges(p,rootId){
    if(!p.canEdit||p.finalized)return;
    const panel=modal('Remove changes from '+packageLabel(p));
    panel.body.append(el('p','Choose changes to remove from this package. Existing files revert to the package baseline; newly added files leave the package. Your saved workspace changes are kept and can be added again.'));
    const filters=el('div',undefined,'interface-filter'),search=el('input'),list=el('div',undefined,'package-picker'),count=el('p',undefined,'interface-muted');
    search.type='search';search.placeholder='Filter files…';search.setAttribute('aria-label','Filter included changes');count.setAttribute('aria-live','polite');
    const selected=new Set();let changes=[],revision,loading=true,saving=false;
    const visible=()=>changes.filter(item=>(item.name+' '+item.modelName).toLowerCase().includes(search.value.toLowerCase()));
    function updateControls(){
      count.textContent=selected.size+' of '+changes.length+' changes selected';
      submit.disabled=loading||saving||!selected.size;all.disabled=loading||saving||!visible().length;clear.disabled=loading||saving||!selected.size;search.disabled=loading||saving;
      for(const check of list.querySelectorAll('input'))check.disabled=saving;
      panel.cancel.disabled=saving;panel.node.querySelector('header button').disabled=saving;
    }
    function refresh(){
      list.replaceChildren();
      for(const item of visible()){
        const label=el('label'),check=el('input'),text=el('span',undefined,'item-label');check.type='checkbox';check.checked=selected.has(item.rootId);
        check.onchange=()=>{if(check.checked)selected.add(item.rootId);else selected.delete(item.rootId);updateControls();};
        text.append(el('span',item.name),el('small',item.modelName));label.append(check,icon('file'),text,badge(item.kind));list.append(label);
      }
      if(!list.children.length)list.append(el('p',changes.length?'No included changes match this filter.':'No changes have been added to this package.','interface-empty'));
      updateControls();
    }
    const all=el('button','Select filtered','interface-button'),clear=el('button','Clear','interface-button');all.type=clear.type='button';
    all.onclick=()=>{for(const item of visible())selected.add(item.rootId);refresh();};clear.onclick=()=>{selected.clear();refresh();};
    filters.append(search,all,clear);search.oninput=refresh;panel.body.append(filters,list,count,el('p','Changing package contents requires new checks and approvals.','interface-muted'));
    panel.node.addEventListener('cancel',event=>{if(saving)event.preventDefault();});
    const submit=button('Remove selected changes',async()=>{
      if(!selected.size||loading||saving)return;
      saving=true;updateControls();panel.error.hidden=true;
      try{
        await request('POST',`/api/v1/packages/${p.id}/changes`,{expectedRevision:revision,versions:[],removeRootIds:[...selected]});
        panel.node.close();await render();await refreshStatus();
      }finally{saving=false;updateControls();}
    },'interface-button primary');panel.footer.append(submit);updateControls();
    list.append(loadingState('Loading included changes…','Retrieving the current package selection.',{compact:true}));
    try{
      // Always use the package's own baseline, independent of the comparison selector.
      const data=await request('GET',`/api/v1/packages/${p.id}/contents`);if(!panel.node.open)return;
      revision=data.revision;changes=(data.roots||[]).filter(item=>item.includedChange).map(item=>({...item,name:item.name||editor.rootName(item.rootId)||item.nodeId,modelName:item.modelName||''})).sort((a,b)=>a.modelName.localeCompare(b.modelName)||a.name.localeCompare(b.name));
      if(changes.some(item=>item.rootId===rootId))selected.add(rootId);loading=false;refresh();
    }catch(error){if(panel.node.open){list.replaceChildren();panel.error.textContent=error.message;panel.error.hidden=false;}}
  }
  async function packageDetail(id,onDetails){if(!id){navigate('releases');return el('div');}const p=await request('GET','/api/v1/packages/'+encodeURIComponent(id));onDetails?.(p);if(currentPackage?.id!==p.id)releaseTab='contents';currentPackage=p;const container=el('div'),crumb=el('div',undefined,'interface-breadcrumb');crumb.append(button('Packaging',()=>navigate('releases'),'link'),document.createTextNode(' › '+packageLabel(p)));container.append(crumb);
    const summary=heading(packageTitle(p),'Revision '+p.revision+' · '+p.authorName),linked=el('p','A linked workspace change still needs to be added. Restore the package author’s submission access, then use Add changes before review or finalization.','interface-error');
    let status=badge(p.state);container.append(summary,status,linked);
    function paintSummary(){
      const actions=[],editable=p.canEdit&&!p.finalized;
      if(p.reviewers?.length){const reviewers=el('div',undefined,'package-reviewers');reviewers.role='group';reviewers.setAttribute('aria-label','Reviewers');for(const reviewer of p.reviewers){const name=reviewer.name?.trim()||'Reviewer',status=reviewer.decision==='APPROVE'?'Approved':reviewer.decision==='CHANGES_REQUESTED'?'Changes requested':'Awaiting review',initial=Array.from(name)[0].toLocaleUpperCase(),avatar=editable?button(initial,()=>assignReviewers(p,updatePackage),'reviewer-avatar'):el('span',initial,'reviewer-avatar');avatar.title=name+' · '+status;avatar.setAttribute('aria-label',avatar.title);avatar.dataset.decision=reviewer.decision||'PENDING';if(!editable){avatar.role='img';avatar.tabIndex=0;}reviewers.append(avatar);}actions.push(reviewers);}
      if(editable)actions.push(button(p.reviewers?.length?'Manage reviewers':'Add reviewers',()=>assignReviewers(p,updatePackage)),button('Add changes…',()=>packagePicker(p)),button('Remove changes…',()=>removePackageChanges(p)));if(p.canFinalize&&!p.finalized){const b=button('Finalize',()=>finalize(p),'interface-button primary');b.disabled=!p.readyToFinalize;b.title=p.readyToFinalize?'Finalize this exact approved package':'All assigned reviewers must approve this revision, and validation must pass.';actions.push(b);}else if(p.canReview&&!p.finalized)actions.push(button('Review…',()=>review(p),'interface-button primary'));
      if(p.canDeploy)actions.push(button('Deploy package',()=>deployment.deploy(p.release),'interface-button primary'));
      const remove=deletePackageButton(p);if(remove)actions.push(remove);
      summary.querySelector('h1').textContent=packageTitle(p);summary.querySelector('p').textContent='Revision '+p.revision+' · '+p.authorName;
      summary.querySelector('.interface-actions').replaceChildren(...actions);const next=badge(p.state);status.replaceWith(next);status=next;linked.hidden=!p.linkedUpdatePending;
    }
    paintSummary();
    const tabs=el('div',undefined,'interface-tabs'),panels=new Map(),tabButtons=new Map();tabs.role='tablist';tabs.setAttribute('aria-label','Package');
    function selectTab(value){releaseTab=value;for(const [id,b]of tabButtons){b.setAttribute('aria-selected',String(id===value));b.tabIndex=id===value?0:-1;panels.get(id).hidden=id!==value;}if(value==='contents')dispatchEvent(new Event('resize'));}
    for(const [id,label]of [['contents','Contents'],['checks','Checks']]){const panel=el('section'),b=button(label,()=>selectTab(id),'');b.role='tab';b.id='package-tab-'+id;b.setAttribute('aria-controls','package-panel-'+id);panel.id='package-panel-'+id;panel.role='tabpanel';panel.setAttribute('aria-labelledby',b.id);panel.tabIndex=0;panels.set(id,panel);tabButtons.set(id,b);tabs.append(b);b.addEventListener('keydown',event=>{if(!['ArrowLeft','ArrowRight','Home','End'].includes(event.key))return;event.preventDefault();const next=event.key==='Home'?'contents':event.key==='End'?'checks':id==='contents'?'checks':'contents';selectTab(next);tabButtons.get(next).focus();});}
    const checks=el('div',undefined,'interface-card padded package-checks');
    function paintChecks(){paintPackageChecks(checks,p,async()=>{
      const next=await request('POST',`/api/v1/packages/${p.id}/checks`,{expectedRevision:p.revision});
      await updatePackage(next);
    });}
    const checkKey=value=>JSON.stringify([value.canEdit,value.release?.validation,value.release?.jobState,value.release?.error,value.release?.stage]);
    async function updatePackage(next){
      if(!container.isConnected||next.id!==p.id||next.revision<p.revision)return;
      const oldContents=packageContentsKey(p),newContents=packageContentsKey(next);
      // Reviewer revisions reset approvals but retain the same root versions.
      const contentsChanged=oldContents!==null&&newContents!==null?oldContents!==newContents:next.revision!==p.revision;
      if(p.finalized!==next.finalized||contentsChanged){await render();return;}
      const refreshChecks=checkKey(p)!==checkKey(next),refreshContents=p.revision!==next.revision||JSON.stringify(p.comments)!==JSON.stringify(next.comments);
      const summaryKey=value=>JSON.stringify([value.revision,value.state,value.reviewers,value.readyToFinalize,value.canFinalize,value.canEdit,value.canReview,value.canDeploy,value.canDelete,value.deleteBlockedReason,value.linkedUpdatePending]);
      const refreshSummary=summaryKey(p)!==summaryKey(next);Object.assign(p,next);
      if(refreshSummary)paintSummary();if(refreshChecks)paintChecks();
      if(refreshContents)panels.get('contents').firstElementChild?.refreshPackageMetadata?.();
      watchPackage(p,updatePackage);
    }
    container.updatePackage=updatePackage;paintChecks();
    panels.get('contents').append(await contents(p));panels.get('checks').append(checks);container.append(tabs,...panels.values());selectTab(releaseTab);return container;
  }
  async function review(p){const panel=modal('Review '+packageLabel(p));panel.body.append(el('p','Your decision applies to revision '+p.revision+' and its exact package contents.'));const reason=field(panel.body,'Review comment','textarea');for(const[value,label]of [['CHANGES_REQUESTED','Request changes'],['APPROVE','Approve']])panel.footer.append(button(label,async()=>{if(!reason.value.trim())throw new Error('Add a review comment.');await request('POST',`/api/v1/packages/${p.id}/decisions`,{expectedRevision:p.revision,packageDigest:p.release.packageDigest,decision:value,reason:reason.value.trim()});panel.node.close();render();},value==='APPROVE'?'interface-button primary':'interface-button'));}
  async function finalize(p){const panel=modal('Finalize '+packageLabel(p));panel.body.append(el('p','Finalize revision '+p.revision+' with the current approvals. Its contents will be immutable and ready for deployment.'));panel.footer.append(button('Finalize package',async()=>{await request('POST',`/api/v1/packages/${p.id}/finalize`,{expectedRevision:p.revision,packageDigest:p.release.packageDigest});panel.node.close();await render();await refreshStatus();},'interface-button primary'));}
  async function assignReviewers(p,onSaved){const data=await request('GET',`/api/v1/packages/${p.id}/reviewers`),panel=modal('Package reviewers'),chosen=new Set((p.reviewers||[]).map(r=>r.id));for(const person of data){const label=el('label'),input=el('input');input.type='checkbox';input.checked=chosen.has(person.id);input.onchange=()=>input.checked?chosen.add(person.id):chosen.delete(person.id);label.append(input,document.createTextNode(' '+person.name));panel.body.append(label);}panel.body.append(el('p','Changing reviewers requires new approvals. Existing test results are kept.'));panel.footer.append(button('Save reviewers',async()=>{const next=await request('POST',`/api/v1/packages/${p.id}/reviewers`,{expectedRevision:p.revision,reviewers:[...chosen]});panel.node.close();await onSaved(next);},'interface-button primary'));}
  async function loadComparison({source,against}){
    const pending=new Map(),get=url=>{if(!pending.has(url))pending.set(url,request('GET',url));return pending.get(url);};
    if(source==='workspace'&&against==='latest')return get('/api/v1/packages/compare');
    async function selected(value){
      if(value==='workspace'||value==='latest')return {data:await get('/api/v1/packages/compare'),side:value==='latest'?'before':'after'};
      if(value.startsWith('candidate:'))return {data:await get('/api/v1/packages/compare?against='+encodeURIComponent(value.slice(10))),side:'before'};
      return {data:await get('/api/v1/packages/'+encodeURIComponent(value.slice(8))+'/contents'),side:'after'};
    }
    const [before,after]=await Promise.all([selected(against),selected(source)]);
    return compareContents(before.data,after.data,before.side,after.side);
  }
  async function contents(p,compareSource,comparisonOptions){
    const data=comparisonOptions?await loadComparison(comparisonOptions):await request('GET',`/api/v1/packages/${p.id}/contents${compareSource?'?against='+encodeURIComponent(compareSource):''}`);
    const card=el('div',undefined,'interface-card'),tools=el('div',undefined,'comparison-toolbar'),fixed=el('label',p?'Current package':'Compare'),againstLabel=el('label','Against'),against=el('select');against.setAttribute('aria-label','Against');
    if(comparisonOptions){
      const source=el('select');source.setAttribute('aria-label','Compare');option(source,'workspace','Saved workspace');option(against,'latest','Latest packaged version');option(against,'workspace','Saved workspace');
      for(const item of comparisonOptions.packages){
        const value=(item.legacy?'candidate:':'package:')+item.id,label=packageTitle(item)+' · revision '+item.revision;
        option(source,value,label);option(against,value,label);
      }
      source.value=comparisonOptions.source;against.value=comparisonOptions.against;fixed.append(source);
      const failure=el('p',undefined,'interface-error');failure.role='alert';failure.hidden=true;
      let generation=0;
      const change=async()=>{
        const current=++generation,selection={source:source.value,against:against.value};failure.hidden=true;card.setAttribute('aria-busy','true');
        try{const next=await contents(null,undefined,{...comparisonOptions,...selection});if(current!==generation||!card.isConnected)return;comparisonSelection=selection;card.replaceWith(next);}
        catch(error){if(current!==generation||!card.isConnected)return;source.value=comparisonOptions.source;against.value=comparisonOptions.against;failure.textContent=error.message;failure.hidden=false;}
        finally{if(current===generation)card.removeAttribute('aria-busy');}
      };
      source.onchange=against.onchange=change;card.append(failure);
    }else{
      fixed.append(el('span',packageLabel(p)+' · revision '+p.revision,'fixed-source'));option(against,'','Package baseline');for(const b of data.baselines||[])option(against,b.candidateId,packageTitle(b));against.value=compareSource||'';
      against.onchange=async()=>{const next=await contents(p,against.value);card.replaceWith(next);};
    }
    againstLabel.append(against);tools.append(fixed,againstLabel);card.prepend(tools);
    const session=mpsComparisonSession({before:data.beforeGraph,after:data.afterGraph},()=>editor.graph()?.models||[]),filter=el('div',undefined,'diff-heading'),body=el('div',undefined,'comparison-body'),tree=el('aside',undefined,'comparison-tree'),diff=el('section',undefined,'comparison-diff');body.append(tree,diff);session.append(filter,body);card.append(session,el('div','Read-only comparison · edit the latest workspace in Editor','comparison-footer'));
    const rootModels=new Map((editor.graph()?.models||[]).flatMap(m=>(m.roots||[]).map(r=>[r.rootId,m.name]))),modelName=r=>r.modelName||rootModels.get(r.rootId)||'Package contents';
    const project=el('mps-project-view');project.readOnly=true;tree.append(project);
    // MPS BaseConcept.virtualPackage: use the retained version, including deleted roots.
    const virtualPackageProperty=`ceab5195-25ea-4f22-9b92-103b95ca8c0c/${0x10802efe25a}/${0x115eca8579f}`;
    let visibleRoots=[];
    project.addEventListener('root-open',event=>{
      const root=visibleRoots.find(r=>r.rootId===event.detail[0].id);
      if(!root||selectedContent===root.rootId)return;
      selectedContent=root.rootId;project.selectedRoot=selectedContent;drawDiff(root);
    });
    function selectFilter(value){
      contentFilter=value;filter.replaceChildren(segments([['all','All roots'],['changes','Changes only']],value,selectFilter));
      visibleRoots=(data.roots||[]).filter(r=>value==='all'?!r.deleted:r.changed).sort((a,b)=>modelName(a).localeCompare(modelName(b))||(a.name||'').localeCompare(b.name||''));
      if(!visibleRoots.some(r=>r.rootId===selectedContent))selectedContent=visibleRoots[0]?.rootId;
      const models=new Map(),statuses={};
      for(const root of visibleRoots){
        const node=root.after&&!root.after.absent?root.after:root.before,reference=root.after&&!root.after.absent?root.afterReference:root.beforeReference;
        const modelId=reference?.slice(0,reference.lastIndexOf('/'))||modelName(root);
        if(!models.has(modelId))models.set(modelId,{id:modelId,name:modelName(root),roots:[]});
        models.get(modelId).roots.push({id:root.rootId,name:root.name||editor.rootName(root.rootId)||root.nodeId,concept:node?.conceptName?.split('.').at(-1)||'Root',virtualPackage:node?.properties?.[virtualPackageProperty]||''});
        if(root.changed)statuses[root.rootId]={change:root.kind};
      }
      project.project={models:[...models.values()]};project.statuses=statuses;project.selectedRoot=selectedContent||'';
      const selected=visibleRoots.find(r=>r.rootId===selectedContent);
      if(selected)drawDiff(selected);else diff.replaceChildren(el('p','No roots to show.','interface-empty'));
    }
    let activeRoot,activeDiff;
    function paintDiffDetails(root,modelDiff){const top=el('div',undefined,'diff-heading');top.append(el('strong',root.name||editor.rootName(root.rootId)||root.nodeId),badge(root.kind||'Unchanged'));if(p?.canEdit&&!p.finalized&&root.includedChange)top.append(button('Remove change',()=>removePackageChanges(p,root.rootId)));
      diff.querySelector(':scope > .diff-heading')?.remove();diff.querySelectorAll(':scope > .discussion').forEach(node=>node.remove());diff.prepend(top);for(const c of (p?.comments||[]).filter(c=>c.rootId===root.rootId)){const thread=el('div',undefined,'discussion');thread.append(el('strong',c.authorName+' · '+c.side+' · anchor '+c.line),el('small',' · revision '+c.revision),el('p',c.body));if(c.candidateId?c.candidateId===p.candidateId:c.revision===p.revision)thread.append(button('Show in model',()=>modelDiff.focusAnchor(c.side,c.line)));for(const reply of c.replies||[])thread.append(el('p',reply.authorName+': '+reply.body));if(!p.finalized){thread.append(button('Reply',()=>addComment(p,root,c.side,c.line,c.id)));if(!c.resolved)thread.append(button('Resolve',async()=>{await request('POST',`/api/v1/packages/${p.id}/comments/${c.id}/resolve`,{expectedRevision:p.revision});render();}));else thread.append(badge('Resolved'));}diff.append(thread);}}
    function drawDiff(root){
      activeRoot=root;activeDiff=mpsDiff({root,session,canComment:side=>p&&!p.finalized&&(side==='after'||!compareSource||compareSource===p.baselineCandidateId),onComment:(side,line)=>addComment(p,root,side,line)});
      diff.replaceChildren(activeDiff);paintDiffDetails(root,activeDiff);
    }
    card.refreshPackageMetadata=()=>{
      if(!p)return;fixed.querySelector('.fixed-source').textContent=packageLabel(p)+' · revision '+p.revision;
      if(activeDiff?.isConnected)paintDiffDetails(activeRoot,activeDiff);
    };
    selectFilter(contentFilter);return card;
  }
  async function addComment(p,root,side,line,parentId){const panel=modal(parentId?'Reply to discussion':'Comment on selected model node · '+(root.name||root.nodeId)+' · '+side),body=field(panel.body,'Comment','textarea');panel.footer.append(button('Post comment',async()=>{if(!body.value.trim())throw new Error('Enter a comment.');await request('POST',`/api/v1/packages/${p.id}/comments`,{expectedRevision:p.revision,rootId:root.rootId,side,line,body:body.value.trim(),parentId:parentId||null});panel.node.close();render();},'interface-button primary'));}
  async function comparison(){
    const packages=(await request('GET','/api/v1/packages')).filter(p=>!p.legacy||p.finalized),values=new Set(['workspace',...packages.map(p=>(p.legacy?'candidate:':'package:')+p.id)]);
    if(!values.has(comparisonSelection.source))comparisonSelection.source='workspace';
    if(comparisonSelection.against!=='latest'&&!values.has(comparisonSelection.against))comparisonSelection.against='latest';
    const container=el('div');container.append(heading('Compare','Compare saved workspace and package contents.'),await contents(null,undefined,{packages,...comparisonSelection}));return container;
  }
  async function deployments(){
    const container=el('div',undefined,'deployment-page');let last,polling=false;
    const uncertain=d=>['FAILED','CANCELLED'].includes(d.jobState)&&!['DEPLOYED','FAILED','CANCELLED'].includes(d.observedState);
    const status=d=>d.stage==='WAITING_FOR_ENVIRONMENT_APPROVAL'?'Awaiting approval':uncertain(d)?'Outcome uncertain':d.observedState==='DEPLOYED'?'Succeeded':d.jobState==='FAILED'?'Failed':d.jobState==='CANCELLED'?'Cancelled':d.jobState==='QUEUED'?'Queued':'In progress';
    async function refresh(){
      if(polling)return;polling=true;
      try{
        const [targets,all,history]=await Promise.all([request('GET','/api/v1/platforms'),request('GET','/api/v1/packages'),request('GET','/api/v1/deployments')]),signature=JSON.stringify([targets,all,history]);if(signature===last)return;last=signature;
        const packages=all.filter(p=>p.finalized&&p.canDeploy),first=targets.find(t=>!t.previousId),actions=[];
        const pending=t=>history.find(d=>d.platformId===t.id&&(['QUEUED','RUNNING'].includes(d.jobState)||uncertain(d)));
        const start=()=>deployment.deploy(packages,first?.id);
        const unavailable=t=>{
          if(!permit('DEPLOY'))return 'Deploy permission is required.';
          if(!t)return 'No deployment environment is available.';
          if(!t.canDeploy)return 'Deploy permission is required for this environment.';
          if(!t.config.enabled)return 'This environment is disabled.';
          if(t.active||pending(t))return 'Wait for the current deployment to finish.';
          if(!t.previousId)return packages.length?'':'Approve and finalize a package in Packaging first.';
          if(t.previousActive||targets.some(previous=>previous.id===t.previousId&&pending(previous)))return 'Wait for deployment to '+t.previousName+' to finish.';
          if(!t.previousInstalledDigest)return 'Deploy a package to '+t.previousName+' first.';
          if(t.previousInstalledDigest===t.installed)return 'This package is already installed.';
          if(!packages.some(p=>p.packageDigest===t.previousInstalledDigest))return 'The package in '+t.previousName+' must be approved and available to deploy.';
          return '';
        };
        const deploymentButton=(label,onClick,reason,primary=false)=>{
          const control=button(label,onClick,'interface-button'+(primary?' primary':''));buttonContent(control,label,'package');control.disabled=!!reason;control.title=reason;if(reason)control.setAttribute('aria-description',reason);return control;
        };
        actions.push(deploymentButton('Deploy package',start,unavailable(first),true));
        const reload=button('Refresh',refresh);buttonContent(reload,'Refresh','refresh');actions.push(reload);
        container.replaceChildren(heading('Deployment','Deploy an approved package, then promote it through the environments in order.',actions));
        container.append(el('p','SIMULATED · These environments record delivery state; they do not run ALEF services.','interface-muted'));
        if(!permit('DEPLOY'))container.append(el('p','View only · Deploy permission is required to deploy or promote packages.','interface-muted'));
        const path=el('ol',undefined,'deployment-path');path.setAttribute('aria-label','Promotion path');for(const t of targets){const step=el('li');step.append(el('span',t.position,'path-number'),el('span',t.name));path.append(step);}container.append(path);
        const {card,body}=table(['#','Environment','Installed package','Status','Actions']);card.classList.add('deployment-environments');
        for(const t of targets){
          const row=el('tr'),installed=all.find(p=>p.packageDigest===t.installed),cell=el('td'),environment=el('td'),label=el('span',undefined,'environment-name'),state=el('td'),action=el('td'),active=pending(t),latest=history.find(d=>d.platformId===t.id);
          row.dataset.environment=t.id;label.append(icon('server'),el('strong',t.name));environment.append(label);if(t.config.requireEnvironmentApproval)environment.append(el('small','Environment approval required'));
          if(installed){cell.append(button(packageLabel(installed),()=>openPackage(installed),'link'));if(installed.version)cell.append(el('small',installed.title));}else cell.textContent=t.installed?'Retained package':'Not deployed';cell.title=t.installed||'';
          const next=packages.find(p=>p.packageDigest===t.previousInstalledDigest);
          let text=active?status(active):!t.config.enabled?'Disabled':latest&&['FAILED','CANCELLED'].includes(latest.jobState)?status(latest):t.previousId&&t.previousInstalledDigest!==t.installed?'Awaiting promotion':t.installed?'Installed':'Not started';
          state.append(badge(text));if(active){const progress=[...(active.events||[])].reverse().find(e=>Number.isFinite(e.details?.progress));if(progress){const bar=el('progress');bar.max=100;bar.value=progress.details.progress;bar.setAttribute('aria-label',t.name+' deployment progress');state.append(bar);}}
          if(active||t.active)action.append(button('View progress',()=>deployment.inspect(active?.id||t.active)));
          else{
            const reason=unavailable(t),control=deploymentButton(t.previousId?'Promote to '+t.name:'Deploy package',t.previousId?()=>deliverPackage(next,t.id):start,reason);
            action.append(control);if(reason){const hint=el('small',reason,'deployment-action-hint');hint.id='deployment-action-'+t.id;control.setAttribute('aria-describedby',hint.id);action.append(hint);}
          }
          row.append(el('td',t.position,'path-step'),environment,cell,state,action);body.append(row);
        }
        if(!targets.length){const row=el('tr'),cell=el('td','No deployment environments are available to this account.','interface-empty');cell.colSpan=5;row.append(cell);body.append(row);}container.append(card);
        if(permit('DEPLOY')&&!packages.length)container.append(el('p','No approved, finalized packages are available to deploy. Complete package review in Packaging first.','interface-muted'));
        if(history.length){container.append(el('h2','Deployment history'));const {card,body}=table(['Package','Environment','Action','Status','Started','']);card.classList.add('deployment-history');for(const d of history){const row=el('tr'),p=all.find(p=>p.packageDigest===d.packageDigest),state=el('td'),action=el('td'),at=d.events?.find(e=>e.type==='REQUESTED')?.at;state.append(badge(status(d)));action.append(button('Details',()=>deployment.inspect(d.id)));row.append(el('td',p?packageTitle(p):'Retained package'),el('td',d.platform),el('td',d.purpose==='PROMOTE'?'Promote':d.purpose==='ROLLBACK'?'Rollback':'Deploy'),state,el('td',at?new Date(at).toLocaleString():'—'),action);body.append(row);}container.append(card);}
      }finally{polling=false;}
    }
    await refresh();routeTimer=setInterval(()=>{if(container.isConnected&&!document.hidden&&!document.querySelector('dialog'))refresh().catch(()=>{});},3000);return container;
  }
  function settings(){const container=el('div');container.append(heading('Settings','Manage workspace access and deployment environments.'));const workspaceActions=el('div',undefined,'interface-actions');workspaceActions.append(button('Reload workspace',()=>editor.reload.click()),editor.lockModel);container.append(workspaceActions);if(permit('MANAGE'))container.append(button('Access and editing locks',()=>editor.administration.click()),button('Environment settings',deployment.settings));return container;}
  addEventListener('hashchange',render);render();refreshStatus().catch(error=>{editor.message.textContent=error.message;});statusTimer=setInterval(()=>refreshStatus().catch(()=>{}),10000);
  document.addEventListener('visibilitychange',()=>{if(!document.hidden)refreshStatus().catch(()=>{});});
  return{refreshStatus,navigate};
}
