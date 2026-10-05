// Isolated package composition preview. No model writes or backend requests.
function composePackageGraph(baseline,workspace,selectedKeys) {
  const graph=JSON.parse(JSON.stringify(baseline));
  for(const key of selectedKeys){
    if(Object.prototype.hasOwnProperty.call(workspace,key))graph[key]=JSON.parse(JSON.stringify(workspace[key]));
    else delete graph[key];
  }
  return graph;
}

function createPackageBuilder({B,I,E,modal,closeModal,differences,getBaselines,getProduction,getWorkspace,getReleases,onCreate,getTarget,onAdd}) {
  let draft=null;
  function candidates(){
    if(!draft)return [];
    const versions=draft.source.versions||[],base=draft.base.pkg.graph;
    const latest=differences(base,draft.source.graph).map(c=>{
      const saved=versions.slice().reverse().find(v=>v.key===c.key&&JSON.stringify(v.node)===JSON.stringify(c.b||null));
      return {...c,id:saved?.id||c.key+'@latest',tags:saved?.tags||[],source:saved?.source||draft.source.id,latest:true};
    });
    const ids=new Set(latest.map(c=>c.id));
    const retained=versions.filter(v=>v.tags.length&&!ids.has(v.id)).flatMap(v=>{
      const change=differences(base[v.key]?{[v.key]:base[v.key]}:{},v.node?{[v.key]:v.node}:{});
      return change.map(c=>({...c,id:v.id,tags:v.tags,source:v.source,latest:false}));
    });
    return [...latest,...retained];
  }
  function visibleGroups(){
    const grouped=new Map();
    candidates().filter(c=>draft.filter==='all'||draft.filter==='selected'&&draft.selected.get(c.key)===c.id||draft.filter.startsWith('tag:')&&c.tags.includes(draft.filter.slice(4))).forEach(c=>{
      if(!grouped.has(c.key))grouped.set(c.key,[]);grouped.get(c.key).push(c);
    });
    return [...grouped.entries()];
  }
  function shownVersion(key,options){
    return options.find(c=>c.id===draft.shown.get(key))||options.find(c=>c.id===draft.selected.get(key))||options[0];
  }
  function selectedCandidates(){return candidates().filter(c=>draft.selected.get(c.key)===c.id);}
  function selectedGraph(){
    const chosen=selectedCandidates(),source={};
    chosen.forEach(c=>{if(c.b)source[c.key]=c.b;});
    return composePackageGraph(draft.base.pkg.graph,source,chosen.map(c=>c.key));
  }
  const versionLabel=c=>(c.b?(c.b.type==='root'?'v'+c.b.version:c.b.snapshot):'Deleted')+' · '+c.source+(c.latest?' · latest':' · retained')+(c.tags.length?' · '+c.tags.join(', '):'');
  const productionLabels=base=>getProduction().filter(p=>p.revisionId===base.pkg.id).map(p=>p.name);
  const baseLabel=base=>'v'+base.version+' · '+base.title+(productionLabels(base).length?' · '+productionLabels(base).join(', '):'');
  function summary(){
    const graph=selectedGraph();
    const nodes=Object.values(graph),roots=nodes.filter(n=>n.type==='root').length,resources=nodes.length-roots;
    const selected=draft.selected.size,retained=Object.keys(draft.base.pkg.graph).filter(key=>!draft.selected.has(key)).length;
    const visible=new Set(visibleGroups().flatMap(([,items])=>items.map(c=>c.id)));
    const hidden=[...draft.selected.values()].filter(id=>!visible.has(id)).length;
    return `<strong>${selected} change${selected===1?'':'s'} selected</strong><span>${retained} ${draft.target?'existing':'baseline'} item${retained===1?'':'s'} retained</span><span class="grow"></span><span>Package: ${roots} roots${resources?' · '+resources+' resource'+(resources===1?'':'s'):''}</span>${hidden?`<span class="hidden-selections">${hidden} selected outside this filter. Use Selected changes to see everything included.</span>`:''}`;
  }
  function renderChanges(){
    const groups=visibleGroups(),labels={A:'Added',D:'Deleted',R:'Renamed',V:'Moved',M:'Modified'};
    if(!groups.length)return '<p class="package-picker-empty">No saved changes match this filter and baseline.</p>';
    return groups.map(([key,options])=>{
      const c=shownVersion(key,options);
      const shared=getReleases().filter(r=>['In review','Changes requested','Approved'].includes(r.state))
        .filter(r=>differences(r.baseline.graph,r.revisions.at(-1).graph).some(other=>other.key===c.key&&JSON.stringify(other.b)===JSON.stringify(c.b)))
        .map(r=>'v'+r.version);
      return `<div class="package-picker-item"><label class="package-picker-choice"><input type="checkbox" data-package-change="${E(c.key)}" data-package-version="${E(c.id)}" ${draft.selected.get(c.key)===c.id?'checked':''}><span class="package-picker-icon">${I('file')}</span><span class="package-picker-name"><strong>${E(c.name)}</strong><small>${E(c.model)}</small><span class="package-version-note">${E(versionLabel(c))}</span>${shared.length?`<small>Also selected in ${E(shared.join(', '))}</small>`:''}</span><span class="change-kind ${c.kind}">${labels[c.kind]}</span></label>${options.length>1?`<div class="package-version-choice"><label for="package-version-${E(key)}">Saved version</label><select id="package-version-${E(key)}" data-version-choice="${E(key)}">${options.map(v=>`<option value="${E(v.id)}" ${v.id===c.id?'selected':''}>${E(versionLabel(v))}</option>`).join('')}</select></div>`:''}</div>`;
    }).join('');
  }
  function updateSelection(){
    const summaryNode=document.querySelector('#package-selection-summary');
    if(summaryNode)summaryNode.innerHTML=summary();
    const submit=document.querySelector('[data-action="'+(draft.target?'confirm-add-package-changes':'confirm-create-package')+'"]');
    if(submit)submit.disabled=draft.selected.size===0;
    const error=document.querySelector('#package-selection-error');if(error)error.hidden=true;
  }
  function refreshPicker(){
    const list=document.querySelector('.package-picker-list'),top=list?.scrollTop||0;
    if(list){list.innerHTML=renderChanges();list.scrollTop=top;}
    updateSelection();
  }
  function render(){
    const bases=getBaselines(),production=getProduction().filter(p=>bases.some(b=>b.pkg.id===p.revisionId));
    modal(draft.target?'Add changes to v'+draft.base.version:'New package',`${draft.target?`<p class="package-create-intro">Choose saved versions to add or update in <strong>v${E(draft.base.version)} · ${E(draft.base.title)}</strong>. Existing contents are retained. Every reviewer will need to approve the new revision.</p>`:`<p class="package-create-intro">Start from a packaged version and apply only the changes you select. Other open packages keep their own contents and review.</p><div class="package-create-fields"><div><label for="candidate-version">Package version</label><input id="candidate-version" placeholder="e.g. 1.3.2" value="${E(draft.version)}" aria-describedby="release-version-error" required><p id="release-version-error" class="release-version-error" role="alert" hidden></p></div><div><label for="candidate-title">Name</label><input id="candidate-title" placeholder="e.g. Production name fix" value="${E(draft.title)}" required></div></div><label for="package-baseline">Start from packaged version</label><select id="package-baseline">${bases.map(base=>`<option value="${E(base.pkg.id)}" ${base.pkg.id===draft.base.pkg.id?'selected':''}>${E(baseLabel(base))}</option>`).join('')}</select>${production.length?`<div class="package-production-shortcuts">${production.map(p=>B('Use '+E(p.name)+' · v'+E(p.version),'package-use-production','small ghost','',`data-baseline="${E(p.revisionId)}"`)).join('')}</div>`:''}<p class="package-baseline-note">All contents from <strong>v${E(draft.base.version)}</strong> are included. Unselected changes keep their baseline version; new items are added only when selected.</p>`}<div class="package-picker-heading"><h2>Choose changes</h2><span>${E(draft.source.label)}</span><span class="grow"></span>${B('Select shown','package-select-all','small ghost')}${B('Clear shown','package-clear','small ghost')}</div><div class="package-tag-filter"><label for="package-tag-filter">Filter by tag</label><select id="package-tag-filter"><option value="all" ${draft.filter==='all'?'selected':''}>All tags</option><option value="selected" ${draft.filter==='selected'?'selected':''}>Selected changes</option>${[...new Set((draft.source.versions||[]).flatMap(v=>v.tags))].sort().map(tag=>`<option value="tag:${E(tag)}" ${draft.filter==='tag:'+tag?'selected':''}>${E(tag)}</option>`).join('')}</select><span>Tags identify saved versions · one version per file</span></div><div class="package-picker-list">${renderChanges()}</div><div id="package-selection-summary" class="package-selection-summary" role="status">${summary()}</div><p id="package-selection-error" role="alert" hidden></p><p class="package-review-note">${draft.target?'Adding changes':'Creating the package'} clears tags from the selected versions. Review and remove changes in Contents before finalizing.</p>`,draft.target?'Add selected changes':'Create package for review',draft.target?'confirm-add-package-changes':'confirm-create-package');
    const card=document.querySelector('#modal-host .card');card?.classList.add('package-create-dialog');
    const footer=document.querySelector('#modal-host .card-body > .flex:last-child');
    if(footer){footer.classList.add('package-create-footer');footer.insertAdjacentHTML('afterbegin',B('Cancel','cancel-package','ghost'));}
    updateSelection();
  }
  function open(adding=false){
    const target=adding?getTarget():null;
    if(adding&&!target)return;
    const base=target||getBaselines()[0];if(!base)return;
    const source=getWorkspace();
    draft={base,target,source:JSON.parse(JSON.stringify(source)),selected:new Map(),shown:new Map(),filter:'all',version:'',title:''};
    render();document.querySelector(adding?'#package-tag-filter':'#candidate-version')?.focus();
  }
  function changeBaseline(id){
    if(draft.target)return;
    const base=getBaselines().find(b=>b.pkg.id===id);if(!base||base.pkg.id===draft.base.pkg.id)return;
    draft.base=base;draft.selected.clear();draft.shown.clear();render();
    document.querySelector('#package-baseline')?.focus();
  }
  function confirm(){
    if(!draft)return;
    const version=draft.version.trim(),title=draft.title.trim();
    const valid=/^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)$/.test(version);
    const message=!valid?'Enter a version such as 1.3.2.':getReleases().some(r=>r.version===version)?'This version already exists. Choose a new version.':'';
    if(!draft.target&&message){const error=document.querySelector('#release-version-error');error.textContent=message;error.hidden=false;const input=document.querySelector('#candidate-version');input.setAttribute('aria-invalid','true');input.focus();return;}
    if(!draft.target&&!title){const input=document.querySelector('#candidate-title');input.setAttribute('aria-invalid','true');input.focus();return;}
    const chosen=selectedCandidates(),selected=chosen.map(c=>c.key);
    if(!selected.length){const error=document.querySelector('#package-selection-error');error.textContent='Select at least one change for this package.';error.hidden=false;return;}
    const created={title,version,baseline:draft.base.pkg,source:draft.source,selected,selectedVersions:chosen.map(c=>({key:c.key,id:c.id,tags:c.tags,source:c.source})),graph:selectedGraph()};
    if(draft.target){
      if(!onAdd({...created,target:draft.target})){
        const error=document.querySelector('#package-selection-error');error.textContent='This package or viewer changed. Close this dialog and reopen Add changes on the current package.';error.hidden=false;return;
      }
      draft=null;closeModal();
    }else{draft=null;closeModal();onCreate(created);}
  }
  function handle(action,button){
    if(action==='create-package'){open();return true;}
    if(action==='add-package-changes'){open(true);return true;}
    if(!draft)return false;
    if(action==='confirm-create-package'||action==='confirm-add-package-changes'){confirm();return true;}
    if(action==='cancel-package'||action==='close-modal'){draft=null;closeModal();return true;}
    if(action==='package-use-production'){changeBaseline(button.dataset.baseline);return true;}
    if(action==='package-select-all'||action==='package-clear'){
      visibleGroups().forEach(([key,options])=>{
        if(action==='package-select-all')draft.selected.set(key,shownVersion(key,options).id);
        else if(options.some(c=>c.id===draft.selected.get(key)))draft.selected.delete(key);
      });
      refreshPicker();return true;
    }
    return false;
  }
  function change(event){
    if(!draft)return;
    const input=event.target;
    if(input.id==='package-baseline')changeBaseline(input.value);
    if(input.id==='package-tag-filter'){draft.filter=input.value;draft.shown.clear();refreshPicker();}
    if(input.dataset.packageChange){
      const c=candidates().find(c=>c.id===input.dataset.packageVersion&&c.key===input.dataset.packageChange);
      if(!c)return;
      if(input.checked)draft.selected.set(c.key,c.id);else if(draft.selected.get(c.key)===c.id)draft.selected.delete(c.key);
      if(draft.filter==='selected')refreshPicker();else updateSelection();
    }
    if(input.dataset.versionChoice){
      const key=input.dataset.versionChoice;
      if(!candidates().some(c=>c.key===key&&c.id===input.value))return;
      draft.shown.set(key,input.value);if(draft.selected.has(key))draft.selected.set(key,input.value);refreshPicker();
      document.querySelector('#package-version-'+key)?.focus();
    }
  }
  function input(event){
    if(!draft)return;
    const node=event.target;
    if(node.id==='candidate-version'){draft.version=node.value;node.removeAttribute('aria-invalid');document.querySelector('#release-version-error').hidden=true;}
    if(node.id==='candidate-title'){draft.title=node.value;node.removeAttribute('aria-invalid');}
  }
  return {handle,change,input,cancel:()=>{draft=null;}};
}
