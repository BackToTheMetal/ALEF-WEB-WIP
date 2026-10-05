// Isolated package composition preview. No model writes or backend requests.
function composePackageGraph(baseline,workspace,selectedKeys) {
  const graph=JSON.parse(JSON.stringify(baseline));
  for(const key of selectedKeys){
    if(Object.prototype.hasOwnProperty.call(workspace,key))graph[key]=JSON.parse(JSON.stringify(workspace[key]));
    else delete graph[key];
  }
  return graph;
}

function createPackageBuilder({B,I,E,modal,closeModal,differences,getBaselines,getProduction,getWorkspace,getReleases,onCreate}) {
  let draft=null;
  const changes=()=>draft?differences(draft.base.pkg.graph,draft.source.graph):[];
  const productionLabels=base=>getProduction().filter(p=>p.revisionId===base.pkg.id).map(p=>p.name);
  const baseLabel=base=>'v'+base.version+' · '+base.title+(productionLabels(base).length?' · '+productionLabels(base).join(', '):'');
  function summary(){
    const graph=composePackageGraph(draft.base.pkg.graph,draft.source.graph,draft.selected);
    const nodes=Object.values(graph),roots=nodes.filter(n=>n.type==='root').length,resources=nodes.length-roots;
    const selected=draft.selected.size,retained=Object.keys(draft.base.pkg.graph).filter(key=>!draft.selected.has(key)).length;
    return `<strong>${selected} change${selected===1?'':'s'} selected</strong><span>${retained} baseline item${retained===1?'':'s'} retained</span><span class="grow"></span><span>Package: ${roots} roots${resources?' · '+resources+' resource'+(resources===1?'':'s'):''}</span>`;
  }
  function renderChanges(){
    const items=changes(),labels={A:'Added',D:'Deleted',R:'Renamed',V:'Moved',M:'Modified'};
    if(!items.length)return '<p class="package-picker-empty">No saved changes against this version. Choose another baseline or save changes in the Editor.</p>';
    return items.map(c=>{
      const shared=getReleases().filter(r=>['In review','Changes requested','Approved'].includes(r.state))
        .filter(r=>differences(r.baseline.graph,r.revisions.at(-1).graph).some(other=>other.key===c.key&&JSON.stringify(other.b)===JSON.stringify(c.b)))
        .map(r=>'v'+r.version);
      return `<div class="package-picker-item"><label class="package-picker-choice"><input type="checkbox" data-package-change="${E(c.key)}" ${draft.selected.has(c.key)?'checked':''}><span class="package-picker-icon">${I('file')}</span><span class="package-picker-name"><strong>${E(c.name)}</strong><small>${E(c.model)}</small>${shared.length?`<small>Also selected in ${E(shared.join(', '))}</small>`:''}</span><span class="change-kind ${c.kind}">${labels[c.kind]}</span></label><details class="package-picker-diff"><summary>View change</summary><div class="package-picker-versions"><div><strong>Baseline · v${E(draft.base.version)}</strong><pre>${E(c.a?.content.join('\n')??'Not present')}</pre></div><div><strong>${E(draft.source.label)}</strong><pre>${E(c.b?.content.join('\n')??'Deleted')}</pre></div></div></details></div>`;
    }).join('');
  }
  function updateSelection(){
    const summaryNode=document.querySelector('#package-selection-summary');
    if(summaryNode)summaryNode.innerHTML=summary();
    const submit=document.querySelector('[data-action="confirm-create-package"]');
    if(submit)submit.disabled=draft.selected.size===0;
    const error=document.querySelector('#package-selection-error');if(error)error.hidden=true;
  }
  function render(){
    const bases=getBaselines(),production=getProduction().filter(p=>bases.some(b=>b.pkg.id===p.revisionId));
    modal('New package',`<p class="package-create-intro">Start from a packaged version and apply only the changes you select. Other open packages keep their own contents and review.</p><div class="package-create-fields"><div><label for="candidate-version">Package version</label><input id="candidate-version" placeholder="e.g. 1.3.2" value="${E(draft.version)}" aria-describedby="release-version-error" required><p id="release-version-error" class="release-version-error" role="alert" hidden></p></div><div><label for="candidate-title">Name</label><input id="candidate-title" placeholder="e.g. Production name fix" value="${E(draft.title)}" required></div></div><label for="package-baseline">Start from packaged version</label><select id="package-baseline">${bases.map(base=>`<option value="${E(base.pkg.id)}" ${base.pkg.id===draft.base.pkg.id?'selected':''}>${E(baseLabel(base))}</option>`).join('')}</select>${production.length?`<div class="package-production-shortcuts">${production.map(p=>B('Use '+E(p.name)+' · v'+E(p.version),'package-use-production','small ghost','',`data-baseline="${E(p.revisionId)}"`)).join('')}</div>`:''}<p class="package-baseline-note">All contents from <strong>v${E(draft.base.version)}</strong> are included. Unselected changes keep their baseline version; new items are added only when selected.</p><div class="package-picker-heading"><h2>Choose changes</h2><span>${E(draft.source.label)}</span><span class="grow"></span>${B('Select all','package-select-all','small ghost')}${B('Clear','package-clear','small ghost')}</div><div class="package-picker-list">${renderChanges()}</div><div id="package-selection-summary" class="package-selection-summary" role="status">${summary()}</div><p id="package-selection-error" role="alert" hidden></p><p class="package-review-note">The new package opens for review. Approval and final packaging happen next.</p>`,'Create package for review','confirm-create-package');
    const card=document.querySelector('#modal-host .card');card?.classList.add('package-create-dialog');
    const footer=document.querySelector('#modal-host .card-body > .flex:last-child');
    if(footer){footer.classList.add('package-create-footer');footer.insertAdjacentHTML('afterbegin',B('Cancel','cancel-package','ghost'));}
    updateSelection();
  }
  function open(){
    const base=getBaselines()[0];if(!base)return;
    const source=getWorkspace();
    draft={base,source:{...source,graph:JSON.parse(JSON.stringify(source.graph))},selected:new Set(),version:'',title:''};
    render();document.querySelector('#candidate-version')?.focus();
  }
  function changeBaseline(id){
    const base=getBaselines().find(b=>b.pkg.id===id);if(!base||base.pkg.id===draft.base.pkg.id)return;
    draft.base=base;draft.selected.clear();render();
    document.querySelector('#package-baseline')?.focus();
  }
  function confirm(){
    if(!draft)return;
    const version=draft.version.trim(),title=draft.title.trim();
    const valid=/^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)$/.test(version);
    const message=!valid?'Enter a version such as 1.3.2.':getReleases().some(r=>r.version===version)?'This version already exists. Choose a new version.':'';
    if(message){const error=document.querySelector('#release-version-error');error.textContent=message;error.hidden=false;const input=document.querySelector('#candidate-version');input.setAttribute('aria-invalid','true');input.focus();return;}
    if(!title){const input=document.querySelector('#candidate-title');input.setAttribute('aria-invalid','true');input.focus();return;}
    const available=new Set(changes().map(c=>c.key));
    const selected=[...draft.selected].filter(key=>available.has(key));
    if(!selected.length){const error=document.querySelector('#package-selection-error');error.textContent='Select at least one change for this package.';error.hidden=false;return;}
    const created={title,version,baseline:draft.base.pkg,source:draft.source,selected,graph:composePackageGraph(draft.base.pkg.graph,draft.source.graph,selected)};
    draft=null;closeModal();onCreate(created);
  }
  function handle(action,button){
    if(action==='create-package'){open();return true;}
    if(!draft)return false;
    if(action==='confirm-create-package'){confirm();return true;}
    if(action==='cancel-package'||action==='close-modal'){draft=null;closeModal();return true;}
    if(action==='package-use-production'){changeBaseline(button.dataset.baseline);return true;}
    if(action==='package-select-all'||action==='package-clear'){
      draft.selected=action==='package-select-all'?new Set(changes().map(c=>c.key)):new Set();
      document.querySelectorAll('[data-package-change]').forEach(input=>{input.checked=draft.selected.has(input.dataset.packageChange);});
      updateSelection();return true;
    }
    return false;
  }
  function change(event){
    if(!draft)return;
    const input=event.target;
    if(input.id==='package-baseline')changeBaseline(input.value);
    if(input.dataset.packageChange){if(input.checked)draft.selected.add(input.dataset.packageChange);else draft.selected.delete(input.dataset.packageChange);updateSelection();}
  }
  function input(event){
    if(!draft)return;
    const node=event.target;
    if(node.id==='candidate-version'){draft.version=node.value;node.removeAttribute('aria-invalid');document.querySelector('#release-version-error').hidden=true;}
    if(node.id==='candidate-title'){draft.title=node.value;node.removeAttribute('aria-invalid');}
  }
  return {handle,change,input};
}
