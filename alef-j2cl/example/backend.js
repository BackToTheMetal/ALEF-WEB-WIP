// Focused backend controls around the existing generated editors and project view.
import {installWorkflowControls} from './backend-workflows.js';
import {savedWorkspace} from './saved-workspace.js';
import {buttonContent} from './interface-ui.js';
if (globalThis.alefBackendMode) {
  document.documentElement.classList.add('backend-enabled');
  const toolbar=document.querySelector('.actions');
  const session=document.querySelector('.session');
  const controls=document.createElement('span');controls.className='backend-controls';
  const button=(label,action,iconName)=>{const b=document.createElement('button');b.type='button';b.className='backend-button';if(iconName)buttonContent(b,label,iconName);else b.textContent=label;b.onclick=()=>{if(!b.disabled){delete message.dataset.error;Promise.resolve().then(action).catch(fail);}};controls.append(b);return b;};
  const message=document.createElement('span');message.setAttribute('role','status');
  const progress=document.createElement('progress');progress.setAttribute('aria-label','Workspace loading');progress.hidden=true;
  toolbar.append(controls,message,progress);session.title='Shared backend workspace';session.textContent='Connecting…';
  let csrf,client,manifest,graph,selected,loading,identity;
  const leases=new Map();let frozen=false,pendingSave=null,saving=false,lockBusy=false,events=null,policyLost=false,remotePending=false,refreshing=false,allowNavigation=false;
  const lostScopes=new Map();
  const created=new Map();
  const moves=new Map();
  const changeTags=new Map();
  const dirtyRoots=()=>[...new Set([...(globalThis.alefExample?.dirtyRoots()||[]),...moves.keys()])];
  const seenEvents=new Set();
  const login=button('Log in',()=>{
    sessionStorage.setItem('alef.return-route',location.hash);
    location.assign('/oauth2/authorization/alef');
  });
  const lock=button('Lock root',toggleSelectedLock,'unlock'),save=button('Save',saveChanges,'save');lock.classList.add('lock-button');lock.setAttribute('aria-label','Lock root');
  const lockModel=button('Lock model',lockSelectedModel,'lock');lockModel.disabled=true;
  const remove=button('Delete root',deleteSelected,'trash');remove.disabled=true;
  const move=button('Move root',moveSelected,'move');move.disabled=true;
  const moveContent=button('Move content',moveSelectedContent,'move');moveContent.disabled=true;
  let interfaceInstalled=false;
  const retry=button('Reload',()=>interfaceInstalled?load():location.reload(),'refresh'),cancel=button('Cancel loading',()=>loading?.abort(),'close');
  const resolveSave=button('Resolve save',resolvePendingSave,'refresh');resolveSave.hidden=true;
  const recoverDraft=button('Resume draft',()=>refreshFromServer(true),'refresh');recoverDraft.disabled=true;
  const administration=button('Access & locks',adminDialog,'settings');administration.hidden=true;
  const logout=button('Log out',async()=>{
    if(dirtyRoots().length&&!confirm('Discard unsaved edits and log out?'))return;
    allowNavigation=true;
    globalThis.alefExample?.freezeEditing();
    const form=document.createElement('form');form.method='POST';form.action='/logout';
    const field=document.createElement('input');field.type='hidden';field.name='_csrf';field.value=csrf.token;form.append(field);document.body.append(form);form.submit();
  },'logout');
  lock.disabled=save.disabled=true;login.hidden=true;cancel.hidden=true;logout.hidden=true;
  const startup=document.createElement('section');startup.id='workspace-startup';startup.setAttribute('aria-labelledby','workspace-startup-title');
  const startupCard=document.createElement('div'),startupRing=document.createElement('span'),startupTitle=document.createElement('h1'),startupDetail=document.createElement('p'),startupAction=document.createElement('button');
  startupCard.className='workspace-startup-card';startupRing.className='loading-ring';startupRing.setAttribute('aria-hidden','true');startupTitle.id='workspace-startup-title';startupDetail.setAttribute('role','status');startupAction.className='interface-button primary';
  startupCard.append(startupRing,startupTitle,startupDetail,startupAction);startup.append(startupCard);document.querySelector('.topbar').after(startup);
  function workspaceState(state,title,detail){
    document.documentElement.dataset.workspaceState=state;
    document.documentElement.classList.toggle('workspace-blocked',state!=='ready');startup.hidden=state==='ready';
    startupTitle.textContent=title;startupDetail.textContent=detail||'';startupRing.hidden=state!=='loading';
    startupAction.hidden=state==='ready'||(state==='loading'&&!loading);startupAction.textContent=state==='sign-in'?'Log in':state==='loading'?'Cancel loading':'Try again';
    startupAction.onclick=()=>state==='sign-in'?login.click():state==='loading'?cancel.click():retry.click();
    document.getElementById('status').dataset.state=state==='error'?'error':state==='ready'?'ready':state;
    document.getElementById('status-text').textContent=state==='ready'?'Ready':title;
  }
  workspaceState('loading','Connecting to your workspace','Checking your session…');
  new MutationObserver(()=>{
    if(document.documentElement.dataset.workspaceState==='loading')startupDetail.textContent=message.textContent;
  }).observe(message,{childList:true,characterData:true,subtree:true});
  function fail(error){
    message.textContent=error.message||String(error);message.dataset.error='true';progress.hidden=true;cancel.hidden=true;retry.disabled=false;
    if(document.documentElement.dataset.workspaceState==='loading')workspaceState('error','Could not open the workspace',message.textContent);
  }
  async function adminDialog(){
    const data=await(await api('/api/v1/admin/access')).json(),active=await(await api('/api/v1/admin/leases')).json();
    const dialog=document.createElement('dialog');dialog.className='backend-dialog';
    const title=document.createElement('h2');title.textContent='Access and editing locks';dialog.append(title);
    const explanation=document.createElement('p');explanation.textContent='Permissions inherit from the workspace. An explicit denial overrides inherited grants. Changes take effect on every open session.';dialog.append(explanation);
    const form=document.createElement('form');form.method='dialog';dialog.append(form);
    const select=(label,entries)=>{const field=document.createElement('label');field.textContent=label;const input=document.createElement('select');for(const [value,name]of entries){const option=document.createElement('option');option.value=value;option.textContent=name;input.append(option);}field.append(input);form.append(field);return input;};
    const person=select('User',data.principals.filter(p=>p.type==='HUMAN').map(p=>[p.id,p.name]));
    const scope=select('Scope',[['workspace','Entire workspace'],...graph.models.filter(m=>!m.readOnly).flatMap(m=>m.roots.map(r=>[r.rootId,`${m.name}: ${Object.values(r.properties)[0]||r.conceptName}`]))]);
    const permission=select('Permission',['READ','EDIT','CREATE_ROOT','DELETE_ROOT','REVIEW','SUBMIT','APPROVE','DEPLOY','MANAGE'].map(p=>[p,p]));
    const effect=select('Rule',[['ALLOW','Allow'],['DENY','Deny'],['INHERIT','Use inherited rule']]);
    const status=document.createElement('p');status.setAttribute('role','status');form.append(status);
    const apply=document.createElement('button');apply.type='button';apply.textContent='Apply permission';apply.onclick=async()=>{
      apply.disabled=true;try{await api('/api/v1/admin/access',{method:'PUT',body:JSON.stringify({principalId:person.value,scope:{kind:scope.value==='workspace'?'WORKSPACE':'ROOT',key:scope.value},permission:permission.value,effect:effect.value})});status.textContent='Permission updated and recorded in the audit history.';}catch(error){status.textContent=error.message;}finally{apply.disabled=false;}
    };form.append(apply);
    const details=document.createElement('details'),summary=document.createElement('summary');summary.textContent='Current rules';details.append(summary);const table=document.createElement('table');
    for(const rule of data.rules){const row=document.createElement('tr');for(const text of[data.principals.find(p=>p.id===rule.principalId)?.name||rule.principalId,rule.scope.kind,rule.scope.key,rule.permission,rule.effect]){const cell=document.createElement('td');cell.textContent=text;row.append(cell);}table.append(row);}details.append(table);dialog.append(details);
    const heading=document.createElement('h3');heading.textContent='Active leases';dialog.append(heading);
    const accounts=document.createElement('details'),accountTitle=document.createElement('summary');accountTitle.textContent='Account access';accounts.append(accountTitle);
    for(const principal of data.principals.filter(p=>p.type==='HUMAN')){const row=document.createElement('p');row.textContent=principal.name+' · '+(principal.enabled?'enabled':'disabled')+' ';const change=document.createElement('button');change.type='button';change.textContent=principal.enabled?'Disable account':'Enable account';change.onclick=async()=>{const reason=prompt('Reason for changing local application access:');if(!reason?.trim())return;try{await api(`/api/v1/admin/principals/${principal.id}/state`,{method:'POST',body:JSON.stringify({enabled:!principal.enabled,reason})});row.textContent='Account access updated. Existing editing clients cannot use a disabled account.';}catch(error){status.textContent=error.message;}};row.append(change);accounts.append(row);}dialog.append(accounts);
    for(const lease of active){const row=document.createElement('p');row.textContent=`${lease.token.scope.kind} ${lease.token.scope.key} · ${data.principals.find(p=>p.id===lease.ownerId)?.name||'User'} · expires ${new Date(lease.expiresAt).toLocaleTimeString()} `;
      const revoke=document.createElement('button');revoke.textContent='Force release';revoke.onclick=async()=>{const reason=prompt('Reason for administratively releasing this lock:');if(!reason?.trim())return;try{await api('/api/v1/admin/leases/force-release',{method:'POST',body:JSON.stringify({token:lease.token,reason})});row.textContent='Lock released; the previous holder can no longer save.';}catch(error){status.textContent=error.message;}};row.append(revoke);dialog.append(row);
    }
    const close=document.createElement('button');close.textContent='Close';close.type='button';close.onclick=()=>dialog.close();dialog.append(close);dialog.onclose=()=>dialog.remove();document.body.append(dialog);dialog.showModal();
  }
  async function api(url,options={}){
    const response=await fetch(url,{...options,headers:{...(options.body?{'Content-Type':'application/json'}:{}),...(csrf?{[csrf.headerName]:csrf.token}:{}),...options.headers},cache:'no-store'});
    if(!response.ok){let data;try{data=await response.json();}catch{}if(response.status===401){
      freeze(identity?'Session expired; log in again':'Log in to open workspace');login.hidden=false;
      workspaceState('sign-in',identity?'Your session has expired':'Sign in to ALEF',dirtyRoots().length?'Your unsaved edits are still in this tab. Signing in reloads the page; keep this tab open until you are ready.':'Log in to open the shared workspace and your packages.');
    }
      const error=new Error(data?.title||data?.code||`Request failed (${response.status})`);error.code=data?.code;error.status=response.status;throw error;}
    return response;
  }
  function freeze(reason){frozen=true;globalThis.alefExample?.freezeEditing();message.textContent=reason;lock.disabled=true;save.disabled=true;}
  async function waitApp(){for(let i=0;i<600;i++){if(globalThis.alefExample)return;await new Promise(r=>setTimeout(r,100));}throw new Error('Language runtime did not load');}
  async function load(){
    if(dirtyRoots().length&&!confirm('Discard unsaved edits and reload the current authorized workspace?'))return;
    if(globalThis.alefExample?.status()==='READY'){allowNavigation=true;location.reload();return;}
    loading?.abort();loading=new AbortController();const signal=loading.signal;
    workspaceState('loading','Opening your workspace','Preparing workspace…');delete message.dataset.error;
    retry.disabled=true;cancel.hidden=false;progress.hidden=false;progress.removeAttribute('value');message.textContent='Preparing workspace…';
    try{
      for(let i=0;i<120;i++){
        const status=await(await api('/api/v1/workspace',{signal})).json();if(status.ready)break;
        if(status.stage==='CAPACITY_LIMIT')throw new Error(`Workspace exceeds the configured capacity of ${status.configuredNodeLimit} nodes. Ask the operator to review its size and memory settings.`);
        if(i===119)throw new Error('Workspace is not ready; inspect backend recovery status');
        message.textContent=`Loading model runtime (${status.stage})…`;await new Promise(r=>setTimeout(r,1000));if(signal.aborted)throw new DOMException('Cancelled','AbortError');
      }
      manifest=await(await api('/api/v1/snapshots',{method:'POST',signal})).json();
      let transferred=0;const total=manifest.chunks.reduce((sum,c)=>sum+c.bytes,0);const models=[];
      progress.max=total;progress.value=0;
      for(const chunk of manifest.chunks){
        const response=await api(chunk.url,{signal});const reader=response.body.getReader(),parts=[];let bytes=0;
        for(;;){const {done,value}=await reader.read();if(done)break;parts.push(value);bytes+=value.length;transferred+=value.length;progress.value=transferred;message.textContent=`Loading models: ${Math.round(transferred/total*100)}%`;}
        const data=new Uint8Array(bytes);let offset=0;for(const part of parts){data.set(part,offset);offset+=part.length;}
        const digest=[...new Uint8Array(await crypto.subtle.digest('SHA-256',data))].map(x=>x.toString(16).padStart(2,'0')).join('');
        if(digest!==chunk.sha256)throw new Error('Snapshot integrity check failed');
        const decoded=JSON.parse(new TextDecoder().decode(data));models.push(...decoded.models);graph={...decoded,models};
      }
      await waitApp();if(signal.aborted)throw new DOMException('Cancelled','AbortError');
      progress.removeAttribute('value');message.textContent=`Initializing ${manifest.counts.nodes} model nodes…`;
      await new Promise(resolve=>setTimeout(resolve,0));globalThis.alefExample.startRemote(JSON.stringify(graph));
      for(let i=0;i<600;i++){
        if(signal.aborted){globalThis.alefExample.close();throw new DOMException('Cancelled','AbortError');}
        const units=globalThis.alefExample.initializationProgress();if(units.total){progress.max=units.total;progress.value=units.completed;message.textContent=`Initializing models: ${units.completed} / ${units.total} nodes`;}
        const status=globalThis.alefExample.status();if(status==='READY')break;if(status.startsWith('FAIL:'))throw new Error(status);
        if(i===599)throw new Error('Model initialization timed out');await new Promise(r=>setTimeout(r,100));
      }
      if(signal.aborted){globalThis.alefExample.freezeEditing();throw new DOMException('Cancelled','AbortError');}
      frozen=false;message.textContent='';session.textContent=identity.name;progress.hidden=true;cancel.hidden=true;retry.disabled=false;workspaceState('ready');update();subscribe();dispatchEvent(new Event('resize'));
    }catch(error){
      if(signal.aborted){if(manifest)await api(`/api/v1/snapshots/${manifest.snapshotId}`,{method:'DELETE'}).catch(()=>{});message.textContent='Loading cancelled';if(document.documentElement.dataset.workspaceState!=='sign-in')workspaceState('error','Workspace loading cancelled','Choose Try again to open your workspace.');}
      else{if(manifest)await api(`/api/v1/snapshots/${manifest.snapshotId}`,{method:'DELETE'}).catch(()=>{});globalThis.alefExample?.close();fail(error);}
      progress.hidden=true;cancel.hidden=true;retry.disabled=false;
    }
  }
  function subscribe(){
    events?.close();events=new EventSource(`/api/v1/events?cursor=${encodeURIComponent(manifest.eventCursor)}`);
    events.addEventListener('workspace.resnapshot',event=>{
      events.close();const reason=JSON.parse(event.data).reason;
      if(reason==='POLICY_CHANGED'){
        // A revoked view is removed immediately, including still-visible drafts.
        policyLost=true;globalThis.alefExample?.close();graph=null;manifest=null;leases.clear();location.reload();
      }else{freeze('The backend restarted or event history expired. Reload to reconnect. Unsaved drafts remain in this tab.');}
    });
    events.addEventListener('workspace.changes',async event=>{
      const batch=JSON.parse(event.data);let changed=false;
      for(const update of batch.events){
        if(seenEvents.has(update.operationId))continue;seenEvents.add(update.operationId);
        if(update.operationId===pendingSave?.id)continue;
        changed=true;
      }
      if(!changed)return;remotePending=true;
      if(pendingSave||frozen){message.textContent='Another user saved changes. Your draft is preserved; resolve the current editing state to compare it.';return;}
      await refreshFromServer(false).catch(fail);
    });
    events.onerror=()=>{if(events.readyState===EventSource.CLOSED)freeze('Change connection ended. Reload to reauthenticate.');};
  }
  async function freshSnapshot(){
    const next=await(await api('/api/v1/snapshots',{method:'POST'})).json(),models=[];let decoded;
    for(const chunk of next.chunks){
      const data=new Uint8Array(await(await api(chunk.url)).arrayBuffer());
      const digest=[...new Uint8Array(await crypto.subtle.digest('SHA-256',data))].map(x=>x.toString(16).padStart(2,'0')).join('');
      if(digest!==chunk.sha256)throw new Error('Snapshot integrity check failed');
      decoded=JSON.parse(new TextDecoder().decode(data));models.push(...decoded.models);
    }
    return{manifest:next,graph:{...decoded,models}};
  }
  const walk=node=>node?[node,...node.children.flatMap(c=>walk(c.node))]:[];
  function refreshedDraft(draft,before,current){
    if(!draft)return null;draft=structuredClone(draft);
    const oldNodes=new Map(walk(before).map(n=>[n.id,n])),newNodes=new Map(walk(current).map(n=>[n.id,n]));
    for(const node of walk(draft))node.references=node.references.map(edge=>{
      const old=oldNodes.get(node.id)?.references.find(r=>r.role===edge.role),next=newNodes.get(node.id)?.references.find(r=>r.role===edge.role);
      if(edge.kind==='RESTRICTED'){
        if(!next)throw new Error('A protected reference changed. Discard this root draft or resolve it from the current model.');
        return structuredClone(next);
      }
      if(next?.kind==='RESTRICTED'&&JSON.stringify(old)===JSON.stringify(edge))return structuredClone(next);
      return edge;
    });return draft;
  }
  function compareDrafts(conflicts){return new Promise(resolve=>{
    const dialog=document.createElement('dialog');dialog.className='backend-dialog';const heading=document.createElement('h2');heading.textContent='Compare unsaved and accepted changes';dialog.append(heading);
    const explanation=document.createElement('p');explanation.textContent='These roots changed after your editing base. Review the original, your draft, and the current accepted content. Replacing a root is explicit and still requires Save. The previous undo history will be cleared.';dialog.append(explanation);
    const readable=root=>root?walk(root).map(n=>({node:n.id,concept:n.conceptName,properties:n.properties,children:n.children.map(c=>({role:c.role,node:c.node.id})),references:n.references.map(r=>r.kind==='RESTRICTED'?{role:r.role,target:'Restricted reference'}:r)})):'Root deleted';
    for(const conflict of conflicts){const details=document.createElement('details');details.open=true;const title=document.createElement('summary');title.textContent=Object.values(conflict.before?.properties||{})[0]||'Changed root';details.append(title);
      for(const [label,value]of[['Original',conflict.before],['Your draft',conflict.draft],['Current accepted content',conflict.current]]){const sub=document.createElement('h3');sub.textContent=label;const pre=document.createElement('pre');pre.textContent=JSON.stringify(readable(value),null,2);details.append(sub,pre);}dialog.append(details);
    }
    const choose=(label,value)=>{const button=document.createElement('button');button.textContent=label;button.onclick=()=>{dialog.close();resolve(value);};dialog.append(button);};
    choose('Resume my drafts against the current revision','DRAFT');choose('Discard drafts and use current content','CURRENT');choose('Keep drafts read-only','CANCEL');
    dialog.oncancel=()=>resolve('CANCEL');dialog.onclose=()=>dialog.remove();document.body.append(dialog);dialog.showModal();
  });}
  async function refreshFromServer(reacquire){
    if(refreshing||pendingSave||!graph)return;refreshing=true;events?.close();
    const ids=dirtyRoots();const drafts=new Map(ids.map(id=>[id,globalThis.alefExample.rootSnapshot(id)]));const original=graph;
    freeze(reacquire?'Reacquiring editing scopes and comparing revisions…':'Refreshing accepted changes; preserving your drafts…');
    try{
      if(reacquire){
        const scopes=new Map();
        for(const id of ids){const entry=modelRoot(id);if(!entry)throw new Error('The draft base is unavailable');
          const previous=[...leases.values()].map(l=>l.token.scope).concat([...lostScopes.values()]);
          const structural=created.has(id)||drafts.get(id)===null||previous.some(s=>s.kind==='MODEL'&&s.key===modelKey(entry.model));
          const scope=structural?{kind:'MODEL',key:modelKey(entry.model)}:{kind:'ROOT',key:id};scopes.set(scope.kind+scope.key,scope);
        }
        for(const lease of leases.values())await api('/api/v1/leases/release',{method:'POST',body:JSON.stringify({editingClientId:client.id,token:lease.token})}).catch(()=>{});
        leases.clear();
        if(scopes.size){const result=await(await api('/api/v1/leases',{method:'POST',body:JSON.stringify({editingClientId:client.id,scopes:[...scopes.values()]})})).json();result.leases.forEach(installLease);}
      }
      const fresh=await freshSnapshot();
      if(fresh.manifest.policyEpoch!==manifest.policyEpoch){policyLost=true;globalThis.alefExample.close();graph=null;location.reload();return;}
      const current=new Map(fresh.graph.models.flatMap(m=>m.roots.map(root=>[root.rootId,{model:m,root}]))),conflicts=[];
      for(const id of ids){const old=modelRoot(id),now=current.get(id);
        if(!created.has(id)&&(!now||old.root.version!==now.root.version||old.model.contextDigest!==now.model.contextDigest))conflicts.push({before:old.root,draft:drafts.get(id),current:now?.root});
      }
      let choice='DRAFT';if(conflicts.length){
        if(!reacquire){message.textContent='An edited root changed. Your draft is preserved. Use Resume draft to compare all three versions.';return;}
        choice=await compareDrafts(conflicts);if(choice==='CANCEL')return;
      }
      // Human comparison may outlast a lease. Reconfirm every proof before the
      // refreshed editor becomes writable; old generations never resume locally.
      for(const [id,lease]of leases){
        try{installLease(await(await api('/api/v1/leases/renew',{method:'POST',body:JSON.stringify({editingClientId:client.id,token:lease.token})})).json());}
        catch(error){lostScopes.set(id,lease.token.scope);leases.delete(id);throw error;}
      }
      const display=structuredClone(fresh.graph);display.deletedDraftRoots=[];display.dirtyRootIds=choice==='DRAFT'?ids:[];
      if(choice==='DRAFT')for(const id of ids){
        const old=modelRoot(id),now=current.get(id);if(!now&&!created.has(id))throw new Error('An edited root was deleted. Discard the draft to use the current workspace.');
        const destination=display.models.find(m=>m.reference===(now?.model.reference||old.model.reference));
        if(!destination)throw new Error('The draft model is unavailable');
        const draft=refreshedDraft(drafts.get(id),old.root,now?.root);destination.roots=destination.roots.filter(r=>r.rootId!==id);
        if(draft)destination.roots.push({...draft,rootId:id,version:now?.root.version||null,editable:true});
        else display.deletedDraftRoots.push({model:destination.reference,nodeId:old.root.id,rootId:id});
      }
      globalThis.alefExample.refreshRemote(JSON.stringify(display));
      for(let i=0;i<600;i++){const status=globalThis.alefExample.status();if(status==='READY')break;if(status.startsWith('FAIL:')||i===599)throw new Error(status);await new Promise(r=>setTimeout(r,50));}
      graph=fresh.graph;manifest=fresh.manifest;
      if(choice==='DRAFT')for(const [id]of created){const old=original.models.find(m=>m.roots.some(r=>r.rootId===id));if(old&&!graph.models.some(m=>m.roots.some(r=>r.rootId===id)))graph.models.find(m=>m.reference===old.reference)?.roots.push(old.roots.find(r=>r.rootId===id));}
      else {created.clear();moves.clear();}
      leases.forEach(installLease);lostScopes.clear();frozen=false;remotePending=false;
      document.dispatchEvent(new CustomEvent('alef-workspace-refreshed'));update();subscribe();
      message.textContent=ids.length&&choice==='DRAFT'?'Current changes loaded; your drafts are preserved. Previous undo history cleared.':'Current changes loaded';
    }finally{refreshing=false;update();}
  }
  function modelRoot(id){for(const model of graph?.models||[])for(const root of model.roots)if(root.rootId===id)return{model,root};return null;}
  const modelKey=model=>model.moduleId+'/'+model.modelId;
  function rootLease(id){const entry=modelRoot(id);return leases.get(id)||(entry&&leases.get(modelKey(entry.model)));}
  function installLease(value){
    leases.set(value.token.scope.key,value);
    notifyLease(value);
    if(saving||pendingSave)return;
    activateLease(value);
  }
  function notifyLease(value,released=false){
    const scope=value.token.scope,updates=[];
    for(const model of graph?.models||[])for(const root of model.roots){
      if(scope.kind==='ROOT'?root.rootId===scope.key:modelKey(model)===scope.key)
        updates.push({rootId:root.rootId,lock:released?null:{label:`Locked by ${identity.name} · this tab · until ${new Date(value.expiresAt).toLocaleTimeString()}`}});
    }
    document.dispatchEvent(new CustomEvent('alef-locks-changed',{detail:{updates}}));
  }
  function activateLease(value){
    // Fail closed slightly before server expiry. Later server responses are still
    // authoritative, and every backend write rechecks the lease in PostgreSQL.
    if(value.token.scope.kind==='MODEL'){
      const model=graph.models.find(m=>modelKey(m)===value.token.scope.key);if(model)globalThis.alefExample.setModelLease(model.reference,Date.parse(value.expiresAt)-3000);
    }else globalThis.alefExample.setRootLease(value.token.scope.key,Date.parse(value.expiresAt)-3000);
  }
  globalThis.alefBackend={saving:()=>saving||!!pendingSave,createRoot:async request=>{
    if(frozen||pendingSave)throw new Error('Resolve the current editing state first');
    const model=graph.models.find(m=>m.reference===request.modelId&&!m.readOnly);if(!model)throw new Error('Choose an editable model');
    const reservation=await(await api('/api/v1/root-reservations',{method:'POST',headers:{'Idempotency-Key':crypto.randomUUID()},body:JSON.stringify({editingClientId:client.id,modelKey:modelKey(model)})})).json();
    installLease(reservation.lease);globalThis.alefExample.reserveRoot(model.reference,reservation.rootId,reservation.nodeId,Date.parse(reservation.lease.expiresAt)-3000);
    const reference=globalThis.alefExample.createRoot(request.modelId,request.concept,request.name,request.virtualPackage);
    const root={...globalThis.alefExample.rootSnapshot(reservation.rootId),rootId:reservation.rootId,version:null,editable:true};model.roots.push(root);created.set(reservation.rootId,reservation);
    message.textContent='Root created locally under a model lease. Save publishes it.';update();return reference;
  },closeClient:async()=>{
    if(!client||!csrf)return;events?.close();globalThis.alefExample?.freezeEditing();
    await api('/api/editing-clients/close',{method:'POST',keepalive:true,body:JSON.stringify({editingClientId:client.id})});
  }};
  async function deleteSelected(){
    if(!selected?.rootId)return;const {model}=modelRoot(selected.rootId);
    if(!confirm('Delete this root on the next Save? The backend rejects deletion while incoming references remain.'))return;
    moves.delete(selected.rootId);
    if(!leases.has(modelKey(model))){const acquired=await(await api('/api/v1/leases',{method:'POST',body:JSON.stringify({editingClientId:client.id,scopes:[{kind:'MODEL',key:modelKey(model)}]})})).json();acquired.leases.forEach(installLease);}
    globalThis.alefExample.deleteRoot(selected.id);message.textContent='Root deleted locally. Save publishes the deletion; Undo restores it.';update();
  }
  async function moveSelected(){
    if(!selected?.rootId)return;const id=selected.rootId,entry=modelRoot(id);
    if(created.has(id))throw new Error('Save the new root before moving it');
    if(moves.has(id)){moves.delete(id);message.textContent='Pending root move cancelled';update();return;}
    const destination=await new Promise(resolve=>{
      const dialog=document.createElement('dialog'),title=document.createElement('h2'),select=document.createElement('select');title.textContent='Move root to another model';select.setAttribute('aria-label','Destination model');
      for(const model of graph.models.filter(m=>!m.readOnly&&m.reference!==entry.model.reference)){const option=document.createElement('option');option.value=modelKey(model);option.textContent=model.name;select.append(option);}
      const apply=document.createElement('button'),cancel=document.createElement('button');apply.textContent='Stage move';apply.disabled=!select.options.length;cancel.textContent='Cancel';apply.onclick=()=>{resolve(select.value);dialog.close();};cancel.onclick=()=>{resolve(null);dialog.close();};dialog.oncancel=()=>resolve(null);dialog.onclose=()=>dialog.remove();dialog.append(title,select,apply,cancel);document.body.append(dialog);dialog.showModal();
    });if(!destination)return;
    const plan=await(await api('/api/v1/root-move-plans',{method:'POST',body:JSON.stringify({editingClientId:client.id,rootId:id,destinationModelKey:destination})})).json();
    const acquired=await(await api('/api/v1/leases',{method:'POST',body:JSON.stringify({editingClientId:client.id,scopes:plan.scopes})})).json();acquired.leases.forEach(installLease);
    moves.set(id,{type:'MOVE_ROOT',rootId:id,destinationModelKey:destination,expectedDestinationContext:plan.expectedDestinationContext});message.textContent='Root move staged. Save moves it and repairs incoming references atomically.';update();
  }
  async function moveSelectedContent(){
    if(!selected?.rootId)return;const entry=modelRoot(selected.rootId),snapshot=globalThis.alefExample.rootSnapshot(selected.rootId);
    const nodes=[];function visit(node,depth){if(depth)nodes.push({node,depth});for(const child of node.children)visit(child.node,depth+1);}visit(snapshot,0);
    const choice=await new Promise(resolve=>{
      const dialog=document.createElement('dialog'),title=document.createElement('h2'),source=document.createElement('select'),target=document.createElement('select'),position=document.createElement('select');title.textContent='Move contained content';source.setAttribute('aria-label','Source content');target.setAttribute('aria-label','Target containment');position.setAttribute('aria-label','Insert before');
      for(const {node,depth}of nodes){const option=document.createElement('option');option.value=node.id;option.textContent='· '.repeat(depth)+(Object.values(node.properties).find(Boolean)||node.conceptName.split('.').pop());source.append(option);}
      let targets=[];const apply=document.createElement('button'),cancel=document.createElement('button');apply.textContent='Move locally';cancel.textContent='Cancel';
      const positions=()=>{position.replaceChildren();const end=document.createElement('option');end.value='';end.textContent='At end';position.append(end);const chosen=targets[Number(target.value)];if(!chosen)return;
        const id=chosen.reference.slice(chosen.reference.lastIndexOf('/')+1);function find(node){if(node.id===id)return node;for(const child of node.children){const found=find(child.node);if(found)return found;}}
        const parent=find(globalThis.alefExample.rootSnapshot(chosen.rootId));for(const child of parent?.children||[])if(child.node.id!==source.value){const option=document.createElement('option');option.value=entry.model.reference+'/'+child.node.id;option.textContent=Object.values(child.node.properties).find(Boolean)||child.node.conceptName.split('.').pop();position.append(option);}
      };
      const refresh=()=>{target.replaceChildren();targets=source.value?globalThis.alefExample.containmentTargets(entry.model.reference+'/'+source.value):[];targets.forEach((item,index)=>{const option=document.createElement('option');option.value=index;option.textContent=item.label;target.append(option);});apply.disabled=!targets.length;positions();};source.onchange=refresh;target.onchange=positions;refresh();
      apply.onclick=()=>{resolve({source:entry.model.reference+'/'+source.value,target:targets[Number(target.value)],before:position.value});dialog.close();};cancel.onclick=()=>{resolve(null);dialog.close();};dialog.oncancel=()=>resolve(null);dialog.onclose=()=>dialog.remove();dialog.append(title,source,target,position,apply,cancel);document.body.append(dialog);dialog.showModal();
    });if(!choice)return;
    const scopes=[...new Set([selected.rootId,choice.target.rootId])].map(key=>({kind:'ROOT',key}));const acquired=await(await api('/api/v1/leases',{method:'POST',body:JSON.stringify({editingClientId:client.id,scopes})})).json();acquired.leases.forEach(installLease);
    globalThis.alefExample.moveContent(choice.source,choice.target.reference,choice.target.role,choice.before);document.dispatchEvent(new CustomEvent('alef-workspace-refreshed'));message.textContent='Content moved locally under both root locks. Save publishes both roots together.';update();
  }
  async function toggleSelectedLock(){
    if(lockBusy)return;
    lockBusy=true;update();
    try{if(rootLease(selected?.rootId))await releaseSelected();else await lockSelected();}
    finally{lockBusy=false;update();}
  }
  async function lockSelected(){
    const rootId=selected?.rootId;if(!rootId)return;
    const result=await(await api('/api/v1/leases',{method:'POST',body:JSON.stringify({editingClientId:client.id,scopes:[{kind:'ROOT',key:rootId}]})})).json();
    result.leases.forEach(installLease);message.textContent='';update();
  }
  async function lockSelectedModel(){
    const entry=modelRoot(selected?.rootId);if(!entry)return;
    const result=await(await api('/api/v1/leases',{method:'POST',body:JSON.stringify({editingClientId:client.id,scopes:[{kind:'MODEL',key:modelKey(entry.model)}]})})).json();
    result.leases.forEach(installLease);message.textContent='';update();
  }
  async function releaseSelected(){
    const entry=modelRoot(selected?.rootId),lease=rootLease(entry?.root.rootId);if(!lease)return;
    if(dirtyRoots().some(id=>rootLease(id)===lease))throw new Error('Save or reload the local draft before releasing this lock');
    await api('/api/v1/leases/release',{method:'POST',body:JSON.stringify({editingClientId:client.id,token:lease.token})});
    if(lease.token.scope.kind==='MODEL')globalThis.alefExample.setModelLease(entry.model.reference,0);else globalThis.alefExample.setRootLease(entry.root.rootId,0);
    leases.delete(lease.token.scope.key);notifyLease(lease,true);message.textContent='';update();
  }
  async function saveChanges(){
    if(saving||refreshing)return;
    if(pendingSave){await resolvePendingSave();return;}
    if(frozen)throw new Error('Resolve the current editing state before saving');
    const ids=dirtyRoots().filter(id=>!created.has(id)||globalThis.alefExample.rootSnapshot(id)!==null);if(!ids.length)return;
    if(ids.some(id=>!modelRoot(id)))throw new Error('A restored root has already been deleted on the server. Create a new root to publish restored content.');
    if(ids.some(id=>!rootLease(id)||Date.now()>=Date.parse(rootLease(id).expiresAt)-3000))throw new Error('An editing lock was lost. Preserve the draft and reacquire before comparing changes.');
    save.disabled=true;message.textContent='Saving and activating durable revision…';
    if(!pendingSave){
      const structuralOperations=[...moves.values()];
      for(const lease of leases.values())if(lease.token.scope.kind==='MODEL'){
        const model=graph.models.find(m=>modelKey(m)===lease.token.scope.key);
        structuralOperations.push({type:'UPDATE_MODEL_CONTEXT',modelKey:lease.token.scope.key,expectedModelContext:model.contextDigest});
      }
      const rootUpdates=ids.map(id=>{const {model,root}=modelRoot(id),snapshot=globalThis.alefExample.rootSnapshot(id);
        if(created.has(id))structuralOperations.push({type:'CREATE_ROOT',rootId:id});else if(snapshot===null)structuralOperations.push({type:'DELETE_ROOT',rootId:id});
        return{rootId:id,expectedVersion:root.version,expectedModelContext:model.contextDigest,root:snapshot};});
      pendingSave={id:crypto.randomUUID(),body:{editingClientId:client.id,baseSnapshotId:manifest.snapshotId,languageFingerprint:manifest.languageFingerprint,reason:'Explicit browser save',changeTags:Object.fromEntries([...changeTags].filter(([id])=>ids.includes(id))),leases:[...leases.values()].map(lease=>lease.token),rootUpdates,structuralOperations}};
      sessionStorage.setItem('alef.pending-operation',pendingSave.id);
    }
    saving=true;globalThis.alefExample.freezeEditing();update();
    try{
      let result=await(await api('/api/v1/changes',{method:'POST',headers:{'Idempotency-Key':pendingSave.id},body:JSON.stringify(pendingSave.body)})).json();
      for(let i=0;result.state!=='ACTIVE'&&i<120;i++){await new Promise(r=>setTimeout(r,1000));result=await(await api(`/api/v1/operations/${pendingSave.id}`)).json();}
      if(result.state!=='ACTIVE')throw new Error(`Save ${pendingSave.id} is still recovering; retry uses this same operation`);
      pendingSave.accepted=true;
      await completeSave(result,pendingSave.id);
    }catch(error){
      if(error.status===422&&!pendingSave?.accepted){
        // An explicit validation refusal cannot have an accepted publication.
        // Keep the draft editable so the user can fix a missing required feature.
        pendingSave=null;sessionStorage.removeItem('alef.pending-operation');frozen=false;
        for(const lease of leases.values())if(Date.now()<Date.parse(lease.expiresAt)-3000)activateLease(lease);
        update();
      }else if(!pendingSave?.accepted&&(error.status===423||error.code==='REVISION_CONFLICT')){
        pendingSave=null;sessionStorage.removeItem('alef.pending-operation');freeze('The draft was not accepted. Use Resume draft to reacquire editing scopes and compare with the current content.');
      }else{resolveSave.hidden=false;freeze(`Save needs attention: ${error.message}. Operation ${pendingSave.id}`);}
      throw error;
    }finally{saving=false;update();}
  }
  async function completeSave(result,id){
    if(result.state!=='ACTIVE'||result.operationId!==id)throw new Error('Save confirmation does not match the pending operation');
    const fresh=await freshSnapshot();
    if(policyLost||fresh.manifest.policyEpoch!==manifest.policyEpoch)throw new Error('Permissions changed. Reload to open the current authorized workspace.');
    const snapshots={},acknowledged={};
    for(const change of result.roots){
      try{snapshots[change.rootId]=globalThis.alefExample.rootSnapshot(change.rootId);}
      catch(error){if(modelRoot(change.rootId))throw error;snapshots[change.rootId]=null;}
    }
    for(const update of pendingSave?.body.rootUpdates||[]){
      if(!result.roots.some(change=>change.rootId===update.rootId))throw new Error('Save confirmation is missing a submitted root');
      acknowledged[update.rootId]=JSON.stringify(update.root);
    }
    const next=savedWorkspace(graph,fresh.graph,result,snapshots);
    // This checks every submitted root before clearing any dirty marker. It
    // never rebuilds an editor, changes a model, or clears the undo history.
    globalThis.alefExample.acknowledgeSave(JSON.stringify(acknowledged));
    for(const update of pendingSave?.body.rootUpdates||[]){created.delete(update.rootId);moves.delete(update.rootId);changeTags.delete(update.rootId);}
    seenEvents.add(id);pendingSave=null;sessionStorage.removeItem('alef.pending-operation');resolveSave.hidden=true;
    if(next.needsRefresh){
      freeze('Saved. The server also changed model content or location. Your current editor is preserved; use Reload when ready to show those changes.');
    }else{
      graph=next.graph;manifest=fresh.manifest;frozen=false;
      for(const lease of leases.values())if(Date.now()<Date.parse(lease.expiresAt)-3000)activateLease(lease);
      delete message.dataset.error;message.textContent=remotePending?'Saved. Other workspace changes are available; reload when ready.':'';
    }
    document.dispatchEvent(new CustomEvent('alef-workspace-saved',{detail:{operationId:id,revision:result.revision}}));update();
  }
  async function resolvePendingSave(){
    if(saving)return;
    const id=pendingSave?.id||sessionStorage.getItem('alef.pending-operation');if(!id)return;
    saving=true;resolveSave.disabled=true;globalThis.alefExample.freezeEditing();update();
    try{
      let response=await api(`/api/v1/operations/${id}`),result=await response.json();
      if(result.state==='NOT_ACCEPTED'&&pendingSave){
        // Retain the exact original body. Do not manufacture a second save key.
        response=await api('/api/v1/changes',{method:'POST',headers:{'Idempotency-Key':id},body:JSON.stringify(pendingSave.body)});result=await response.json();
      }
      if(result.state==='ACTIVE'){await completeSave(result,id);return;}
      message.textContent=result.state==='NOT_ACCEPTED'?'The operation was not accepted. The previous tab draft was not persisted.':`Operation ${id} is recovering; resolve it again shortly.`;
    }finally{saving=false;resolveSave.disabled=false;update();}
  }
  function update(){
    const entry=modelRoot(selected?.rootId),held=!!rootLease(selected?.rootId);
    const blocked=frozen||saving||lockBusy||!!pendingSave;
    const hasDraft=held&&dirtyRoots().some(id=>rootLease(id)===rootLease(selected?.rootId));
    lock.disabled=blocked||(!held&&!entry?.root.editable)||hasDraft;
    buttonContent(lock,held?'Release lock':'Lock root',held?'lock':'unlock');lock.setAttribute('aria-label',held?'Release lock':'Lock root');lock.dataset.locked=String(held);lock.setAttribute('aria-busy',String(lockBusy));
    lock.title=hasDraft?'Save your changes before releasing this lock':held?(rootLease(selected?.rootId).token.scope.kind==='MODEL'?'Release the lock on this model':'Release your lock on this root'):'Lock this root to edit';
    save.disabled=blocked||!dirtyRoots().length;
    remove.disabled=blocked||!entry?.root.editable;
    move.disabled=blocked||!entry?.root.editable;buttonContent(move,moves.has(selected?.rootId)?'Cancel root move':'Move root','move');
    moveContent.disabled=blocked||!entry?.root.editable;
    lockModel.disabled=blocked||!entry?.root.editable||leases.has(entry?modelKey(entry.model):'');
    recoverDraft.disabled=refreshing||!!pendingSave||!frozen||!dirtyRoots().length;
    recoverDraft.hidden=!frozen||!!pendingSave||!dirtyRoots().length;
    retry.hidden=(!frozen&&!remotePending&&message.dataset.error!=='true')||!!pendingSave||saving;
  }
  document.addEventListener('alef-root-selected',event=>{selected=event.detail;update();});
  document.addEventListener('alef-command-rollback',async()=>{
    freeze('The command could not edit every required root. Restoring the complete pre-command draft…');
    try{for(let i=0;i<600;i++){const status=globalThis.alefExample.status();if(status==='READY')break;if(status.startsWith('FAIL:')||i===599)throw new Error(status);await new Promise(r=>setTimeout(r,50));}
      leases.forEach(installLease);frozen=false;message.textContent='The entire rejected command was rolled back. Earlier drafts are preserved; undo history was cleared.';document.dispatchEvent(new CustomEvent('alef-workspace-refreshed'));update();
    }catch(error){fail(error);}
  });
  setInterval(update,500);
  setInterval(async()=>{
    if(frozen||saving||pendingSave)return;
    for(const [id,lease]of leases)try{const next=await(await api('/api/v1/leases/renew',{method:'POST',body:JSON.stringify({editingClientId:client.id,token:lease.token})})).json();installLease(next);}
    catch(error){lostScopes.set(id,lease.token.scope);leases.delete(id);freeze('Editing lock lost. Unsaved local changes are preserved for comparison.');}
  },30000);
  addEventListener('beforeunload',event=>{if(!allowNavigation&&!policyLost&&dirtyRoots().length&&!pendingSave){event.preventDefault();event.returnValue='';}});
  addEventListener('pagehide',()=>globalThis.alefBackend.closeClient().catch(()=>{}));
  (async()=>{
    csrf=await(await api('/api/csrf')).json();
    try{identity=await(await api('/api/session')).json();}catch(error){if(error.status===401){login.hidden=false;retry.hidden=true;session.textContent='Log in to open workspace';return;}throw error;}
    const returnRoute=sessionStorage.getItem('alef.return-route');sessionStorage.removeItem('alef.return-route');
    if(!location.hash&&/^#(?:model|changes|releases|deployments|settings|release\/[a-zA-Z0-9-]+)$/.test(returnRoute||''))history.replaceState(null,'',returnRoute);
    logout.hidden=false;administration.hidden=!identity.workspacePermissions?.includes('MANAGE');client=await(await api('/api/editing-clients',{method:'POST'})).json();
    for(const control of [lockModel,move,moveContent,administration])control.remove();
    const editorBridge={graph:()=>graph,selected:()=>selected,dirty:dirtyRoots,lock,save,remove,lockModel,reload:retry,administration,logout,message,
      modelName:key=>graph?.models?.find(model=>modelKey(model)===key)?.name||'',
      rootName:id=>{const root=modelRoot(id)?.root;return root?Object.values(root.properties||{}).find(Boolean)||root.conceptName?.split('.').at(-1):'';},
      setTags:(id,tags)=>changeTags.set(id,tags)};
    installWorkflowControls({api,identity,container:controls,editor:editorBridge});
    interfaceInstalled=true;
    resolveSave.hidden=!sessionStorage.getItem('alef.pending-operation');await load();
  })().catch(fail);
}
