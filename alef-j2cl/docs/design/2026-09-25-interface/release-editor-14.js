// Local design simulation. Release drafts never write to the workspace graph.
function createReleaseEditor({E,I,B,clone,differences,render,toast,save,feedback,discussion}) {
  const sessions=new Map();
  const editable=(r,p)=>p.id===r.revisions.at(-1).id&&['In review','Changes requested','Approved'].includes(r.state);
  function session(r,p) {
    const id=r.id+'/'+p.id;
    if(!sessions.has(id))sessions.set(id,{graph:clone(p.graph),selected:'name',tabs:['name'],locked:new Set(),filter:'all',query:''});
    const s=sessions.get(id),keys=Object.keys(s.graph);
    s.tabs=s.tabs.filter(key=>s.graph[key]);
    if(!s.graph[s.selected])s.selected=keys[0];
    if(s.selected&&!s.tabs.includes(s.selected))s.tabs.push(s.selected);
    return s;
  }
  const pending=(r,p)=>differences(p.graph,session(r,p).graph);
  const hasDraft=r=>pending(r,r.revisions.at(-1)).length>0;
  function tree(r,p,s) {
    const changed=new Map(differences(r.baseline.graph,s.graph).map(c=>[c.key,c]));
    const entries=Object.entries(s.graph),changedCount=entries.filter(([key])=>changed.has(key)).length;
    const visible=entries.filter(([key,n])=>(s.filter==='all'||(s.filter==='changed')===changed.has(key))&&n.name.toLowerCase().includes(s.query.toLowerCase()));
    const groups=[...new Set(visible.map(([,n])=>n.model))];
    return `<div class="release-root-filters" aria-label="Included root filters">${[['all','All',entries.length],['changed','Changed',changedCount],['unchanged','Unchanged',entries.length-changedCount]].map(([value,label,count])=>B(label+' <span>'+count+'</span>','release-root-filter',s.filter===value?'active':'','',`data-filter="${value}" aria-pressed="${s.filter===value}"`)).join('')}</div><label class="release-root-search">${I('search')}<input id="release-root-search" aria-label="Find an included root" placeholder="Find a root…" value="${E(s.query)}"></label><div class="change-tree release-root-tree">${groups.map(group=>`<div class="group-label" title="${E(group)}">${I('down')}${I(group==='Managed resources'?'file':'folder')}<span>${E(group)}</span></div>${visible.filter(([,n])=>n.model===group).map(([key,n])=>B(`<span class="tree-node-icon">${I('file')}</span><span class="change-name">${E(n.name)}</span>${changed.has(key)?`<span class="change-kind ${changed.get(key).kind}" title="Changed in this release">${changed.get(key).kind==='V'?'↗':changed.get(key).kind}</span>`:'<span class="release-root-unchanged" title="Unchanged" aria-hidden="true"></span>'}`,'release-editor-root',key===s.selected?'active':'','',`data-key="${E(key)}" title="${E(n.name)} · ${changed.has(key)?'Changed':'Unchanged'}"`)).join('')}`).join('')||'<p class="release-root-empty">No matching roots.</p>'}</div>`;
  }
  function body(r,p) {
    const s=session(r,p),n=s.graph[s.selected],canEdit=editable(r,p),locked=canEdit&&s.locked.has(s.selected),changes=pending(r,p);
    const change=differences(r.baseline.graph,s.graph).find(c=>c.key===s.selected);
    const reason=p.id!==r.revisions.at(-1).id?'Earlier revision · read only':r.state==='Packaged'?'Packaged · read only':r.state==='Rejected'?'Rejected · read only':'Release v'+r.version;
    return `<section class="release-editor" aria-label="Release editor"><aside class="change-project release-editor-project"><div class="change-project-head">${I('tree')}Included files<span class="grow"></span><span class="count">${Object.keys(s.graph).length}</span></div><div id="release-root-browser">${tree(r,p,s)}</div><div class="release-tree-foot">Only files in this release</div></aside><div class="release-editor-main"><div class="release-file-tabs" role="tablist" aria-label="Release files">${s.tabs.map(key=>`<div class="release-file-tab ${key===s.selected?'active':''}">${B(I('file')+'<span>'+E(s.graph[key].name)+'</span>','release-editor-root','','',`data-key="${E(key)}" role="tab" aria-selected="${key===s.selected}" title="${E(s.graph[key].name)}"`)}${s.tabs.length>1?B('×','release-editor-close','release-tab-close','',`data-key="${E(key)}" aria-label="Close ${E(s.graph[key].name)}"`):''}</div>`).join('')}</div><div class="file-toolbar" aria-label="Release file controls"><div class="file-version">${n.type==='root'?'<span class="root-ver">v'+n.version+'</span>':''}<span class="file-version-state">${change?'Changed':'Unchanged'}</span></div><span class="grow"></span><span class="release-editor-scope">${E(reason)}</span><span class="file-save-state" id="release-save-state" role="status">${changes.length?'Unsaved':'Saved'}</span>${canEdit?B(locked?'Locked':'Lock to edit','release-editor-lock','file-lock '+(locked?'held':''),'lock',`aria-label="${locked?'Release file lock':'Lock release file'}" title="${locked?'Locked by you in this release':'Acquire this release file’s lock'}"`)+B('Save','release-editor-save','primary file-save','check',changes.length?'title="Save all pending edits in this release"':'disabled'):I('lock')}</div><div class="release-editor-canvas">${feedback(r,s.selected)}${discussion(p.id,s.selected,'b',0,p.id)}${locked?`<textarea id="release-file-content" aria-label="Release file content" spellcheck="false">${E(n.content.join('\n'))}</textarea>`:`<div class="release-source" aria-label="Release file content">${n.content.map((line,i)=>`<div class="release-source-line">${B('+','comment-start','line-comment','',`data-package="${E(p.id)}" data-source="${E(p.id)}" data-root="${E(s.selected)}" data-side="b" data-line="${i+1}" aria-label="Comment on release line ${i+1}"`)}<span class="ln">${i+1}</span><code>${E(line)||' '}</code></div>${discussion(p.id,s.selected,'b',i+1,p.id)}`).join('')}</div>`}</div><div class="release-editor-foot"><span>Design preview · model representation</span><span class="grow"></span>${canEdit?B('Discard edits','release-editor-discard','small ghost','',changes.length?'':'disabled'):''}${B('Add comment','comment-start','small ghost','',`data-package="${E(p.id)}" data-source="${E(p.id)}" data-root="${E(s.selected)}" data-side="b" data-line="0"`)}</div></div></section>`;
  }
  function input(event,r,p) {
    const s=session(r,p);
    if(event.target.id==='release-root-search'){
      s.query=event.target.value;
      // Keep typing focus while filtering the tree only.
      const temporary=document.createElement('div');temporary.innerHTML=tree(r,p,s);
      document.querySelector('.release-root-tree').replaceWith(temporary.querySelector('.release-root-tree'));
    }
    if(event.target.id!=='release-file-content'||!editable(r,p)||!s.locked.has(s.selected))return;
    s.graph[s.selected].content=event.target.value.split('\n');
    const dirty=pending(r,p).length>0;
    document.querySelector('#release-save-state').textContent=dirty?'Unsaved':'Saved';
    document.querySelector('[data-action="release-editor-save"]').disabled=!dirty;
    document.querySelector('[data-action="release-editor-discard"]').disabled=!dirty;
    // Review/finalize require the saved revision, never an unsaved buffer.
    document.querySelectorAll('[data-action="review-decisions"],[data-action="release-final"]').forEach(b=>b.disabled=dirty);
  }
  function handle(action,button,r,p) {
    if(!action.startsWith('release-editor-')&&action!=='release-root-filter')return false;
    const s=session(r,p);
    if(action==='release-editor-root'&&s.graph[button.dataset.key]){s.selected=button.dataset.key;if(!s.tabs.includes(s.selected))s.tabs.push(s.selected);}
    if(action==='release-editor-close'){const key=button.dataset.key;s.tabs=s.tabs.filter(x=>x!==key);if(s.selected===key)s.selected=s.tabs.at(-1);}
    if(action==='release-root-filter')s.filter=button.dataset.filter;
    if(action==='release-editor-lock'&&editable(r,p)){if(s.locked.has(s.selected))s.locked.delete(s.selected);else s.locked.add(s.selected);}
    if(action==='release-editor-discard'&&editable(r,p))s.graph=clone(p.graph);
    if(action==='release-editor-save'&&editable(r,p)){
      const changes=pending(r,p);if(!changes.length)return true;
      const next=save(r,p,s.graph,changes.map(c=>c.key));
      if(next){sessions.delete(r.id+'/'+p.id);sessions.set(r.id+'/'+next.id,{...s,graph:clone(next.graph)});toast('Saved in release v'+r.version+' · In review. Latest workspace unchanged.');}
    }
    render();
    if(action==='release-editor-lock')document.querySelector('#release-file-content')?.focus();
    return true;
  }
  function preview(r,p) {
    const s=session(r,p);if(!editable(r,p)||!s.locked.has(s.selected))return false;
    s.graph[s.selected].content.push('  // release edit '+(s.graph[s.selected].content.length+1));render();return true;
  }
  return {body,handle,input,hasDraft,editable,preview,canPreview:(r,p)=>editable(r,p)&&session(r,p).locked.has(session(r,p).selected)};
}
