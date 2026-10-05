// DESIGN ONLY. Reuses the previous editor capture and environment controls.
// All model versions, identities, hashes, users and workflow actions are fixtures.
// No API, authentication, model editing, real validation or deployment is performed.
(() => {
  const capturedModel = model, oldHandle = handle;
  const E = escapeText;
  const freeze = value => {
    if (value && typeof value === 'object' && !Object.isFrozen(value)) {
      Object.values(value).forEach(freeze); Object.freeze(value);
    }
    return value;
  };
  const clone = value => JSON.parse(JSON.stringify(value));
  const record = (id, name, modelName, version, content, extra = {}) => freeze({
    id, name, model: modelName, version, snapshot: `s${version}a`,
    type: 'root', content: content.split('\n'), ...extra
  });
  const choice = 'servicetest.choice', service = 'serviceEnBerichtTypesInAparteModellen.Service';
  const baseline = freeze({
    name: record('root-name', rootName, choice, 12, 'Objecttype de naam\n  de bijnaam   Tekst;\n  de initialen Tekst;\n  de voornaam  Tekst;'),
    service: record('root-service', 'serviceapartmodel', service, 8, 'Service serviceapartmodel\n  uitvoer naam\n  verwijst naar de voornaam'),
    test: record('root-test', 'ServiceTest', service, 12, 'ServiceTest\n  invoer persoon\n  verwacht voornaam = "Jan"'),
    leeg: record('root-leeg', 'leeg', choice, 2, 'Regelgroep leeg'),
    ubc: record('root-ubc', 'ubc', choice, 5, 'Uitvoerberichtmapping ubc\n  uitvoer de naam'),
    ibm: record('root-ibm', 'ibm', choice, 4, 'Invoerberichtmapping ibm'),
    keuze: record('root-keuze', 'keuzetest_A1790279010802', choice, 7, 'Service keuzetest_A1790279010802'),
    move1: record('root-move1', 'MoveTarget1790287481145', choice, 1, 'ObjectModel MoveTarget1790287481145'),
    move2: record('root-move2', 'MoveTarget1790287585892Z', choice, 3, 'ObjectModel MoveTarget1790287585892Z'),
    bericht: record('root-bericht', 'Berichtuit_B1790279324981', 'serviceEnBerichtTypesInAparteModellen.Berichten', 5, 'Uitvoerberichtmapping Berichtuit'),
    invoer: record('root-invoer', 'Invoer_spApart_spModel_A1790279324981', 'serviceEnBerichtTypesInAparteModellen.Berichten', 4, 'Invoerberichtmapping Invoer'),
    uitvoer: record('root-uitvoer', 'Uitvoer_spApart_spModel', 'serviceEnBerichtTypesInAparteModellen.Berichten', 3, 'Uitvoerberichtmapping Uitvoer'),
    regels: record('root-regels', 'Regels apart model', 'serviceEnBerichtTypesInAparteModellen.Regels', 8, 'Regelgroep Regels apart model'),
    regeltest: record('root-regeltest', 'Regeltest', service, 5, 'TestSet Regeltest'),
    remote: record('root-remote', 'Gegevens apart model Remote', 'serviceEnBerichtTypesInAparteModellen.Gegevens', 6, 'ObjectModel Gegevens apart model Remote'),
    config: record('resource-config', 'execution.properties', 'Managed resources', null, 'encoding=UTF-8\nvalidation=strict', {type:'resource',snapshot:'res-8'})
  });
  const savedSeed = clone(baseline);
  savedSeed.name = record('root-name', rootName, choice, 13, 'Objecttype de naam\n  de bijnaam   Tekst;\n  de initialen Tekst;\n  de roepnaam  Tekst;', {snapshot:'draft-13.2'});
  savedSeed.service = record('root-service', 'serviceapartmodelNaam', service, 9, 'Service serviceapartmodelNaam\n  uitvoer naam\n  verwijst naar de roepnaam', {snapshot:'draft-9.1'});
  savedSeed.test = record('root-test', 'ServiceTest', service, 13, 'ServiceTest\n  invoer persoon\n  verwacht roepnaam = "Jan"', {snapshot:'draft-13.1'});
  savedSeed.ubc = record('root-ubc', 'ubc', 'serviceEnBerichtTypesInAparteModellen.Berichten', 6, 'Uitvoerberichtmapping ubc\n  uitvoer de naam', {snapshot:'draft-6.1'});
  delete savedSeed.leeg;
  savedSeed.added = record('root-added', 'Naamcontrole', service, 1, 'Service Naamcontrole\n  controleer de roepnaam', {snapshot:'draft-1.1'});
  savedSeed.config = record('resource-config', 'execution.properties', 'Managed resources', null, 'encoding=UTF-8\nvalidation=strict\nrelease.notes=Naamcontrole', {type:'resource',snapshot:'res-9'});
  let savedGraph = freeze(savedSeed), liveGraph = clone(savedGraph), workspaceSequence = 43;
  let dirty = false, held = false, mainState = 'In review', saveCounter = 2;
  let selectionDirty=false;
  const excludedChanges=new Set();
  editorState='saved';
  const previousGraph = clone(baseline);
  previousGraph.name = record('root-name', rootName, choice, 11, 'Objecttype de naam\n  de bijnaam Tekst;\n  de voornaam Tekst;');
  previousGraph.service = record('root-service', 'serviceapartmodel', service, 7, 'Service serviceapartmodel\n  uitvoer naam');
  const submitted = clone(savedGraph);
  submitted.name = record('root-name', rootName, choice, 13, 'Objecttype de naam\n  de bijnaam   Tekst;\n  de initialen Tekst;\n  de roepnaam  Numeriek;', {snapshot:'draft-13.1'});
  const revisionOf = (id, graph, source, digest) => freeze({id, graph:clone(graph), source, digest});
  const pOld = revisionOf('REL-15-R1', previousGraph, 'w38', '72a38fc1…a097');
  const pBaseline = revisionOf('REL-16-R1', baseline, 'w40', 'f0b39be8…079b');
  const approvedGraph=clone(baseline);
  approvedGraph.config=record('resource-config','execution.properties','Managed resources',null,'encoding=UTF-8\nvalidation=strict\nvalidation.report=enabled',{type:'resource',snapshot:'res-8.1'});
  const pApproved = revisionOf('REL-17-R1', approvedGraph, 'w41', 'ad89076b…229c');
  const pRequested = revisionOf('REL-18-R1', submitted, 'w42', '77ac9821…401b');
  const pReview = revisionOf('REL-18-R2', savedGraph, 'w43', '40e218a2…519b');
  const releases = [
    {id:18,title:'Gegevensmodel update',version:'1.4.0',state:'In review',revisions:[pRequested,pReview],baseline:pApproved,author:'author Local',reviewer:'reviewer Local',updated:'10:42',deployed:'Not deployed',activity:['Revision 2 saved from Model. Same package and root versions; ready for review.','Changes requested on revision 1. Feedback remains with that revision.','One release opened for selected workspace changes.']},
    {id:17,title:'Mapping maintenance',version:'1.3.1',state:'Packaged',revisions:[pApproved],baseline:pBaseline,author:'editor Local',reviewer:'reviewer Local',updated:'Yesterday',deployed:'Not deployed',activity:['Package created from approved revision. Deployment was not requested.']},
    {id:16,title:'Name model baseline',version:'1.3.0',state:'Packaged',revisions:[pBaseline],baseline:pOld,author:'author Local',reviewer:'reviewer Local',updated:'23 Sep',deployed:'2 environments · SIMULATED',activity:['Packaged after approval. The same immutable artifact was deployed.']},
    {id:15,title:'Initial name model',version:'1.2.0',state:'Packaged',revisions:[pOld],baseline:pOld,author:'author Local',reviewer:'reviewer Local',updated:'20 Sep',deployed:'1 environment · SIMULATED',activity:['Retained release baseline.']}
  ];
  const rejectedRevision=revisionOf('REL-14-R1',previousGraph,'w37','rejected-preview-14');
  releases.push({id:14,title:'Earlier mapping proposal',version:'1.1.0',state:'Rejected',revisions:[rejectedRevision],baseline:pOld,author:'author Local',reviewer:'reviewer Local',updated:'18 Sep',deployed:'Not eligible',activity:['Proposal rejected and closed. No deployment package was created.']});
  const finalized = new Map(Object.values(baseline).filter(r=>r.type==='root').map(r=>[r.id+'@'+r.version,r.snapshot]));
  const eligible = new Map(Object.values(savedGraph).filter(r=>r.type==='root').map(r=>[r.id+'@'+r.version,r.snapshot]));
  let activeRelease = 18, packageIndex = 1, section = 'changes', releaseFilter = 'all', releaseQuery = '';
  let reviewDecision=null;
  let selectedChange = 'name', inlineChange = 'name', diffMode = 'side', releaseDetail = false;
  // Review discussions are separate from immutable saved revision fixtures.
  const reviewThreads=[{id:1,packageId:pRequested.id,sourceId:pRequested.id,root:'name',side:'b',line:4,kind:'Changes requested',resolved:false,messages:[{author:'reviewer Local',text:'The roepnaam property should be Tekst, not Numeriek.',when:'10:42'}]}];
  const releaseAgainstByRelease=new Map();
  const commentDrafts=new Map();
  let commentComposer=null;
  let compareId = 'workspace-43', againstId = pBaseline.id, currentReviewId = 18;
  const comparisonSources = new Map();
  const register = (id,label,pkg) => comparisonSources.set(id,{id,label,pkg});
  releases.forEach(r=>r.revisions.forEach((p,i)=>register(p.id,`Release ${r.version} · revision ${i+1} · ${r.state}`,p)));
  function captureWorkspace() {
    const id = 'workspace-'+workspaceSequence;
    register(id, `Saved workspace · w${workspaceSequence}`, revisionOf(id,savedGraph,'w'+workspaceSequence,'workspace-snapshot'));
    return id;
  }
  captureWorkspace();
  const rel = () => releases.find(r=>r.id===activeRelease);
  const pkg = () => rel().revisions[packageIndex];
  const currentRevision = r => r.revisions[r.revisions.length-1];
  const openPackage = () => releases.find(r=>['In review','Changes requested','Approved'].includes(r.state));
  const latestPackaged = () => releases.find(r=>r.state==='Packaged');
  releases.filter(r=>r.state==='Packaged').forEach(r=>{
    r.approval=freeze({packageId:'REL-'+r.id,revisionId:currentRevision(r).id,digest:currentRevision(r).digest});
    r.artifact=freeze({revisionId:currentRevision(r).id,digest:currentRevision(r).digest});
    Object.values(currentRevision(r).graph).filter(n=>n.type==='root').forEach(n=>finalized.set(n.id+'@'+n.version,n.snapshot));
  });
  function decisionIsCurrent() {
    const r=rel(),p=pkg();
    return reviewDecision?.releaseId===r.id&&reviewDecision.revisionId===p.id&&p.id===currentRevision(r).id&&r.state==='In review'&&!dirty&&!selectionDirty&&!blocked(p).length;
  }
  function selectedGraph(base) {
  const graph=clone(savedGraph);
  excludedChanges.forEach(key=>{if(base[key])graph[key]=clone(base[key]);else delete graph[key];});
  return graph;
}
  function appendWorkspaceRevision(r) {
  const graph=selectedGraph(r.baseline.graph);
  if(r.revisions.length&&JSON.stringify(currentRevision(r).graph)===JSON.stringify(graph))return false;
  const n=r.revisions.length+1,wasApproved=r.state==='Approved';
  const snapshot=revisionOf('REL-'+r.id+'-R'+n,graph,'w'+workspaceSequence,'preview-'+r.id+'-'+n+'-w'+workspaceSequence);
  r.revisions.push(snapshot);r.state='In review';r.updated='Just now';r.approval=null;
  r.activity.unshift('Revision '+n+' saved from Model. Selected changes stay in this release.'+(wasApproved?' Previous approval invalidated; new review required.':''));
  register(snapshot.id,'Release '+r.version+' · revision '+n+' · In review',snapshot);
  currentReviewId=r.id;
  return snapshot;
}
  const tag = state => `<span class="pill ${['Draft','Changes requested'].includes(state)?'warn':['Rejected','Revoked'].includes(state)?'danger':['In review','Not deployed'].includes(state)?'neutral':''}">${E(state)}</span>`;
  function blocked(p) {
  return Object.values(p.graph).filter(r=>r.type==='root').filter(r=>{
    const key=r.id+'@'+r.version;
    return finalized.has(key)&&finalized.get(key)!==r.snapshot;
  });
}
  function rootState(r) {
  if(!r||r.type!=='root')return '';
  if(finalized.get(r.id+'@'+r.version)===r.snapshot)return 'Packaged';
  const active=openPackage(),p=active&&currentRevision(active);
  if(p&&Object.values(p.graph).some(n=>n.id===r.id&&n.snapshot===r.snapshot))
    return active.state==='Changes requested'?'Draft':active.state;
  return 'Draft';
}
  model = () => {
    if(editorState==='loading') return loading();
    const r=liveGraph.name;
    const active=openPackage();
    mainState=rootState(r);
    let html=capturedModel();
    const groups=[...new Set(Object.values(liveGraph).filter(n=>n.type==='root').map(n=>n.model))];
    const concepts={'root-name':'ObjectModel','root-service':'Service','root-test':'ServiceTestSet','root-ubc':'Uitvoerberichtmapping','root-ibm':'Invoerberichtmapping','root-keuze':'Service','root-move1':'ObjectModel','root-move2':'ObjectModel','root-bericht':'Uitvoerberichtmapping','root-invoer':'Invoerberichtmapping','root-uitvoer':'Uitvoerberichtmapping','root-regels':'Regelgroep','root-regeltest':'TestSet','root-remote':'ObjectModel','root-added':'Service'};
    const project=groups.map(group=>treeGroup(group,Object.values(liveGraph).filter(n=>n.type==='root'&&n.model===group).map(n=>[n.name,concepts[n.id]||'Root',n.id==='root-name']))).join('');
    html=html.replace(/<div class="tree">[\s\S]*?<div class="project-footer">/,'<div class="tree">'+project+'</div><div class="project-footer">');
    const toolbar=compactFileToolbar({B,I,E,root:r,state:mainState,active,held,dirty,selectionDirty,editorState,excluded:excludedChanges.has('name')});
    html=html.replace(/<div class="editor-actions">[\s\S]*?(?=<div class="editing-center">)/,toolbar);
    if(active){
      const release=active;
      const feedback=reviewThreads.filter(t=>t.kind&&!t.resolved&&t.root==='name'&&release.revisions.some(p=>p.id===t.packageId));
      if(feedback.length)html=html.replace('<div class="canvas-area">',`<div class="canvas-area"><div class="editor-review-feedback">${feedback.map(t=>feedbackNote(t).replace('class="context-feedback" open','class="context-feedback"')).join('')}</div>`);
    }
    return html;
  };
  function differences(before,after) {
    return [...new Set([...Object.keys(before),...Object.keys(after)])].map(key=>{
      const a=before[key],b=after[key];
      if(a&&b&&JSON.stringify(a)===JSON.stringify(b))return null;
      let kind=!a?'A':!b?'D':a.name!==b.name?'R':a.model!==b.model?'V':'M';
      return {key,a,b,kind,name:(b||a).name,model:(b||a).model,type:(b||a).type};
    }).filter(Boolean);
  }
  function tree(changes,selected,inline=false) {
    const groups=[...new Set(changes.map(c=>c.model))];
    return `<aside class="change-project"><div class="change-project-head">${I('tree')}Changed project<span class="grow"></span><span class="count">${changes.length}</span></div><div class="change-tree">${groups.map(group=>`<div class="group-label" title="${E(group)}">${I('down')}${I(group==='Managed resources'?'file':'folder')}<span>${E(group)}</span></div>${changes.filter(c=>c.model===group).map(c=>B(`<span class="tree-node-icon">${I('file')}</span><span class="change-name">${E(c.name)}</span><span class="change-kind ${c.kind}" title="${({A:'Added',D:'Deleted',R:'Renamed',V:'Moved',M:'Modified'})[c.kind]}">${c.kind==='V'?'↗':c.kind}</span>`,'compare-root',c.key===selected?'active':'','',`data-key="${c.key}" data-inline="${inline}"`)).join('')}`).join('')}</div></aside>`;
  }
  function lineDiff(a,b) {
    const rows=Array.from({length:a.length+1},()=>Array(b.length+1).fill(0));
    for(let i=a.length-1;i>=0;i--)for(let j=b.length-1;j>=0;j--)rows[i][j]=a[i]===b[j]?1+rows[i+1][j+1]:Math.max(rows[i+1][j],rows[i][j+1]);
    const out=[];let i=0,j=0;
    while(i<a.length||j<b.length){if(i<a.length&&j<b.length&&a[i]===b[j])out.push({op:'same',line:a[i],a:++i,b:++j});else if(j<b.length&&(i===a.length||rows[i][j+1]>rows[i+1][j]))out.push({op:'add',line:b[j],b:++j});else out.push({op:'remove',line:a[i],a:++i});}
    return out;
  }
  const commentAnchor=(packageId,root,side,line,sourceId=packageId)=>`${packageId}/${root}/${sourceId}/${side}/${line}`;
  function feedbackNote(thread) {
    const owner=releases.find(r=>r.revisions.some(p=>p.id===thread.packageId));
    const revision=owner.revisions.findIndex(p=>p.id===thread.packageId)+1;
    return `<details class="context-feedback" open><summary>${E(thread.kind)}<span>v${E(owner.version)} · revision ${revision}</span></summary><p>${E(thread.messages[0].text)}</p>${B('Open discussion','open-feedback-package','small ghost','',`data-thread="${thread.id}"`)}</details>`;
  }
  function renderDiscussion() {
    const positions=['.review-shell','.change-detail'].map(selector=>({selector,top:document.querySelector(selector)?.scrollTop||0}));
    render();positions.forEach(({selector,top})=>{const element=document.querySelector(selector);if(element)element.scrollTop=top;});
  }
  function lineDiscussion(packageId,root,side,line,sourceId=packageId) {
    const anchor=commentAnchor(packageId,root,side,line,sourceId);
    const threads=reviewThreads.filter(t=>commentAnchor(t.packageId,t.root,t.side,t.line,t.sourceId)===anchor);
    const discussion=threads.map(t=>`<details class="inline-thread ${t.resolved?'resolved':''}" ${t.resolved?'':'open'}><summary>${t.resolved?'Resolved':E(t.kind||'Discussion')} · ${t.messages.length} comment${t.messages.length===1?'':'s'}</summary><div class="thread-body">${t.messages.map(m=>`<div class="thread-message"><div><strong>${E(m.author)}</strong><span>${E(m.when)}</span></div><p>${E(m.text)}</p></div>`).join('')}<div class="thread-actions">${B(t.resolved?'Reopen':'Resolve','comment-resolve','small ghost','',`data-thread="${t.id}"`)}</div>${!t.resolved?`<label class="sr-only" for="reply-${t.id}">Reply to thread ${t.id}</label><textarea id="reply-${t.id}" class="inline-comment-input" data-comment-draft="reply-${t.id}" placeholder="Reply…" rows="2">${E(commentDrafts.get('reply-'+t.id)||'')}</textarea><div class="thread-actions">${B('Reply','comment-reply','small','',`data-thread="${t.id}"`)}</div>`:''}</div></details>`).join('');
    const composer=commentComposer===anchor?`<div class="inline-thread comment-composer"><label for="inline-comment-text">New comment · ${side==='a'?'Before':'After'} line ${line}</label><textarea id="inline-comment-text" class="inline-comment-input" data-comment-draft="${E(anchor)}" placeholder="Write a comment…" rows="3">${E(commentDrafts.get(anchor)||'')}</textarea><div class="thread-actions">${B('Cancel','comment-cancel','small ghost')}${B('Post comment','comment-post','small primary','',`data-package="${E(packageId)}" data-source="${E(sourceId)}" data-root="${E(root)}" data-side="${side}" data-line="${line}"`)}</div></div>`:'';
    return discussion+composer;
  }
  function diffPane(c,beforeLabel,afterLabel,reviewPackage=null,beforeSourceId=null,contextFeedback='') {
    if(!c)return `<div class="empty-compare"><h2>No changes</h2><p>The selected snapshots contain the same authorized model and resources.</p></div>`;
    const ops=lineDiff(c.a?.content||[],c.b?.content||[]);
    const row=(o,side)=>{
      const anchorSide=side==='both'?(o.op==='remove'?'a':'b'):side,line=anchorSide==='a'?o.a:o.b;
      const sourceId=anchorSide==='a'?beforeSourceId:reviewPackage;
      const count=reviewPackage?reviewThreads.filter(t=>t.packageId===reviewPackage&&t.root===c.key&&t.side===anchorSide&&t.line===line&&(t.sourceId||t.packageId)===sourceId).length:0;
      const comment=reviewPackage?B(count||'+','comment-start','line-comment'+(count?' has-thread':''),'',`data-package="${E(reviewPackage)}" data-source="${E(sourceId)}" data-root="${E(c.key)}" data-side="${anchorSide}" data-line="${line}" aria-label="Comment on ${anchorSide==='a'?'Before':'After'} line ${line}" title="Add a comment"`):'';
      return `<div class="number-line ${o.op==='same'?'':o.op}">${comment}<span class="ln">${line||''}</span><code>${o.op==='add'?'+ ':o.op==='remove'?'− ':'  '}${E(o.line)}</code></div>${reviewPackage?lineDiscussion(reviewPackage,c.key,anchorSide,line,sourceId)+(side==='both'&&o.op==='same'?lineDiscussion(reviewPackage,c.key,'a',o.a,beforeSourceId):''):''}`;
    };
    const code=diffMode==='unified'?`<div>${ops.map(o=>row(o,'both')).join('')}</div>`:`<div>${ops.filter(o=>o.op!=='add').map(o=>row(o,'a')).join('')||'<div class="empty-compare">Not present</div>'}</div><div>${ops.filter(o=>o.op!=='remove').map(o=>row(o,'b')).join('')||'<div class="empty-compare">Deleted</div>'}</div>`;
    const endpoints=[[c.a,'Before',beforeLabel],[c.b,'After',afterLabel]].map(([root,side,source])=>{
      const version=root?root.type==='resource'?'Resource':`v${root.version}`:'Not present';
      const detail=`${side} · ${source} · ${version}${root?' · '+root.snapshot:''}`;
      return `<div title="${E(detail)}" aria-label="${E(detail)}" tabindex="0"><span class="diff-side">${side}</span><strong>${version}</strong><span class="diff-source">${E(source)}</span></div>`;
    }).join('');
    return `<div class="change-detail"><div class="change-detail-head">${I(c.type==='resource'?'file':'folder')}<h2>${E(c.name)}</h2><span class="change-kind ${c.kind}">${({A:'Added',D:'Deleted',R:'Renamed',V:'Moved',M:'Modified'})[c.kind]}</span><span class="grow"></span><div class="diff-mode">${B('Side by side','diff-side',diffMode==='side'?'active':'')}${B('Unified','diff-unified',diffMode==='unified'?'active':'')}</div></div>${['R','V'].includes(c.kind)?`<div class="change-summary">${E(c.a.model+' / '+c.a.name)} → ${E(c.b.model+' / '+c.b.name)}</div>`:''}<div class="diff-version-row">${endpoints}</div>${(reviewPackage?lineDiscussion(reviewPackage,c.key,'b',0)+lineDiscussion(reviewPackage,c.key,'a',0,beforeSourceId):contextFeedback)?`<div class="diff-review-feedback">${reviewPackage?lineDiscussion(reviewPackage,c.key,'b',0)+lineDiscussion(reviewPackage,c.key,'a',0,beforeSourceId):contextFeedback}</div>`:''}<div class="diff-content ${diffMode==='unified'?'unified':''}">${code}</div><div class="diff-caption">${I('lock')} Read-only comparison · ${E((c.b||c.a).id)} · illustrative model representation</div></div>`;
  }
  function comparison(before,after,inline=false) {
    const list=differences(before.pkg.graph,after.pkg.graph);
    let key=inline?inlineChange:selectedChange;
    if(!list.some(c=>c.key===key))key=list[0]?.key;
    if(inline)inlineChange=key;else selectedChange=key;
    const feedback=inline?'':reviewThreads.filter(t=>t.kind&&t.root===key&&[before.pkg.id,after.pkg.id].includes(t.packageId)).map(t=>feedbackNote(t)).join('');
    return `<div class="compare-layout">${tree(list,key,inline)}${diffPane(list.find(c=>c.key===key),before.label,after.label,inline?after.pkg.id:null,before.pkg.id,feedback)}</div>`;
  }
  changes = () => {
    const before=comparisonSources.get(againstId),after=comparisonSources.get(compareId);
    return `<section class="compare-shell"><div class="compare-controls"><div class="compare-control-line"><label>Compare<select id="compare-source" aria-label="Compare">${sourceOptions(compareId)}</select></label><label>Against<select id="against-source" aria-label="Against">${sourceOptions(againstId)}</select></label><div class="comparison-actions">${B('⇄','swap-comparison','swap','', 'aria-label="Swap comparison"')}<details class="comparison-options"><summary aria-label="Comparison options" title="Comparison options">${I('more')}</summary><div class="comparison-options-list">${B('Refresh saved workspace','refresh-comparison','small ghost')}${B('Reset comparison','reset-comparison','small ghost')}</div></details></div></div></div>${comparison(before,after)}</section>`;
  };
  const sourceOptions = (selected,include=()=>true) => [...comparisonSources.values()].filter(include).map(s=>`<option value="${s.id}" ${s.id===selected?'selected':''}>${E(s.label)}</option>`).join('');
  function releaseList() {
    const active=openPackage();
    return `<section class="review-shell"><div class="compact-heading"><div class="grow"><h1>Releases</h1><p>One open release. Package it after approval.</p></div>${active?B('Open current release','open-active-package','primary'):B('Start release','create-package','primary','plus')}</div><div class="release-tools"><label class="release-search">${I('search')}<input id="release-search" type="search" placeholder="Search releases…" aria-label="Search releases" value="${E(releaseQuery)}"></label><div class="segmented">${[['all','All'],['review','Needs review'],['approved','Approved'],['packaged','Packaged'],['rejected','Rejected']].map(([value,label])=>B(label,'release-filter',releaseFilter===value?'active':'','',`data-filter="${value}"`)).join('')}</div><span class="grow"></span><span class="release-count">${releases.length} releases</span></div><div class="release-table-wrap"><table class="release-table"><thead><tr><th>Version</th><th>Release</th><th>Status</th><th>Author</th><th>Reviewer</th><th>Updated</th><th>Deployment</th></tr></thead><tbody>${releaseRows()}</tbody></table></div><p class="hint" style="margin-top:14px">Included changes reset approval. Creating the package freezes its contents; deployment is optional.</p></section>`;
  }
  function releaseRows() {
    const shown=releases.filter(r=>`${r.title} ${r.version} ${r.id}`.toLowerCase().includes(releaseQuery.toLowerCase())).filter(r=>releaseFilter==='all'||releaseFilter==='review'&&['In review','Changes requested'].includes(r.state)||releaseFilter==='approved'&&r.state==='Approved'||releaseFilter==='packaged'&&r.state==='Packaged'||releaseFilter==='rejected'&&r.state==='Rejected');
    return shown.map(r=>`<tr><td>${B('v'+E(r.version),'open-release','release-version','',`data-release="${r.id}" aria-label="Open release v${E(r.version)}"`)}</td><td>${B(E(r.title),'open-release','title-link','',`data-release="${r.id}"`)}<small>${openPackage()?.id===r.id?'Open release':r.state==='Rejected'?'Closed release':'Immutable package'} · revision ${r.revisions.length}</small></td><td>${tag(r.state)}</td><td class="release-person">${r.author}</td><td class="release-person">${r.reviewer}</td><td class="release-person">${r.updated}</td><td><span style="font-size:11px;color:var(--muted)">${r.deployed}</span></td></tr>`).join('')||'<tr><td colspan="7">No releases match these filters.</td></tr>';
  }
  function releaseBody(r,p) {
    if(section==='changes'){
      const before=comparisonSources.get(releaseAgainstByRelease.get(r.id)||r.baseline.id),after=comparisonSources.get(p.id);
      return `<div class="inline-comparison"><div class="compare-controls release-compare-controls"><div class="compare-control-line"><label>Compare<select id="release-compare-source" aria-label="Compare release package">${sourceOptions(p.id,s=>r.revisions.some(x=>x.id===s.id))}</select></label><label>Against<select id="release-against-source" aria-label="Against release package">${sourceOptions(before.pkg.id,s=>!s.id.startsWith('workspace-'))}</select></label></div></div>${comparison(before,after,true)}</div>`;
    }
    if(section==='roots')return `<div class="manifest-box"><table class="manifest-table"><thead><tr><th>Root / model</th><th>Version</th><th>Snapshot</th><th>State</th></tr></thead><tbody>${Object.values(p.graph).filter(n=>n.type==='root').map(n=>`<tr><td><strong>${E(n.name)}</strong><small>${E(n.model)}</small></td><td><span class="root-ver">v${n.version}</span></td><td><code>${E(n.snapshot)}</code><small>${n.id}</small></td><td>${finalized.get(n.id+'@'+n.version)===n.snapshot?'Packaged':r.state==='Changes requested'?'Correction requested':r.state==='Rejected'?'Rejected':blocked(p).some(x=>x.id===n.id)?'Conflicting version':r.state==='Approved'?'Approved':'Candidate'}</td></tr>`).join('')}</tbody></table><div class="manifest-foot">Complete snapshot: selected workspace changes plus baseline versions for excluded changes. Root versions become final when packaged.</div></div>`;
    if(section==='checks')return `<article class="review-article"><h2>Checks for revision ${packageIndex+1}</h2>${['Native MPS validation','Testspraak verification','Reference and dependency completeness','Package retention and checksum','Reviewer scope and independence'].map(x=>`<div class="check-line">${I('check')}${x}<span class="grow"></span><span class="hint">Passed</span></div>`).join('')}<div class="check-line">${I(blocked(p).length?'circle':'check')}Root version eligibility<span class="grow"></span>${tag(blocked(p).length?'Conflict':'Current')}</div><p>Illustrative evidence, attached to <code>${p.id}</code>. This prototype runs no native validation.</p></article>`;
    if(section==='activity')return `<article class="review-article"><h2>Activity & revision history</h2>${r.revisions.slice().reverse().map((x,i)=>`<div class="package-history-row"><span class="platform-icon">${I('lock')}</span><div class="grow"><strong>Revision ${r.revisions.length-i}</strong><p>${x.id} · ${x.source}</p><code>${x.digest}</code></div>${B('Inspect revision '+(r.revisions.length-i),'inspect-package','small','',`data-index="${r.revisions.length-i-1}"`)}</div>`).join('')}<div class="timeline" style="margin-top:22px">${r.activity.map(x=>`<div class="timeline-row"><strong>${E(x)}</strong><small>Illustrative recorded activity · saved revision retained</small></div>`).join('')}</div></article>`;
    return `<article class="review-article"><h2>${E(r.title)}</h2><p>Update the name model and corresponding service references, tests and resources. Review the fixed workspace snapshot before approval.</p><div class="facts"><div><span class="muted">Reviewer</span><strong>${E(r.reviewer)} · 1 independent approval</strong></div><div><span class="muted">Scope</span><strong>Complete graph · selected changes</strong></div><div><span class="muted">Source</span><code>${p.source}</code></div><div><span class="muted">Package / revision</span><code>REL-${r.id} / ${packageIndex+1}</code></div><div><span class="muted">Digest</span><code>${p.digest}</code></div><div><span class="muted">Deployment</span><strong>${r.deployed}</strong></div></div><p>${I('lock')} Save includes the selected workspace changes. Changes to an approved revision return it to In review. Create package freezes the approved revision; deployment is separate.</p>${B('How root versions work','version-history','small ghost')}</article>`;
  }
  function releaseView() {
    const r=rel(),p=pkg(),historical=packageIndex!==r.revisions.length-1,conflicts=blocked(p);
    const canApprove=!historical&&r.state==='In review'&&!conflicts.length&&!dirty&&!selectionDirty;
    const reviewControls=`${!historical&&r.state==='In review'?B('Review…','review-decisions','primary','',canApprove?'':'disabled title="Save pending changes and review the latest valid revision"'):''}<details class="comparison-options release-options"><summary aria-label="Release actions" title="Release actions">${I('more')}</summary><div class="comparison-options-list">${B('Compare with…','compare-package','small ghost')}${conflicts.length?B('Inspect version conflict','version-conflict','small ghost'):''}${!historical&&['Approved','Packaged'].includes(r.state)?B('Revoke approval','revoke-candidate','small ghost'):''}${B('Version rules','version-history','small ghost')}</div></details>`;

    return `<section class="review-shell"><div class="breadcrumbs">${B('Releases','all-releases','ghost')}${I('chevron')}#${r.id}</div><div class="compact-heading"><div class="grow"><div class="flex" style="flex-wrap:wrap"><span class="release-version release-version-heading" aria-label="Release version v${E(r.version)}">v${E(r.version)}</span><h1>${E(r.title)}</h1>${tag(historical?'Earlier revision':r.state)}</div><div class="slim-meta"><span>Revision ${packageIndex+1}</span><span>·</span><span>${r.author}</span><span>·</span><code>REL-${r.id}</code><span>·</span><code>${p.digest}</code></div></div><div class="right-actions">${openPackage()?.id===r.id&&!historical?B('Edit in Model','edit-package','small','file'):''}${r.state==='Approved'&&!historical?B('Create package','release-final','primary','lock',(dirty||selectionDirty)?'disabled title="Save pending changes first"':''):''}${r.state==='Packaged'&&!r.revoked&&!historical?B('Deploy package','deploy-approved','primary','arrow'):''}${reviewControls}</div></div>${r.state==='Approved'&&!historical?`<div class="approved-note">${I('check')}<strong>Approved · not packaged</strong><span>Included changes return this release to review.</span></div>`:''}<div class="review-tabs" role="tablist" aria-label="Release sections">${[['overview','Overview'],['changes','Changes'],['roots','Included roots'],['checks','Checks'],['activity','Activity / history']].map(([value,label])=>B(label,'release-section',section===value?'active':'','',`data-section="${value}" role="tab" aria-selected="${section===value}"`)).join('')}<span class="grow"></span>${section!=='changes'?`<select aria-label="Revision history" id="package-history">${r.revisions.map((x,i)=>`<option value="${i}" ${i===packageIndex?'selected':''}>Revision ${i+1}${i===r.revisions.length-1?' · current':' · earlier'}</option>`).join('')}</select>`:''}</div><div class="compact-review"><div>${releaseBody(r,p)}</div></div></section>`;
  }
  go = next => {
    releaseDetail=['release','pulls'].includes(next);view=releaseDetail?'releases':next;
    location.hash=releaseDetail?'release':view;render();
  };
  render = () => {
    releases.forEach(r=>r.revisions.forEach((p,i)=>{const source=comparisonSources.get(p.id);if(source)source.label=`Release ${r.version} · revision ${i+1} · ${i===r.revisions.length-1?r.state:'Earlier revision'}`;}));
    document.querySelectorAll('nav [data-page]').forEach(b=>b.classList.toggle('active',b.dataset.page===view));
    document.querySelector('#screen').innerHTML=view==='model'?model():view==='changes'?changes():view==='releases'?(releaseDetail?releaseView():releaseList()):view==='settings'?environmentDesign.settingsPage():environmentPage();
    const reviewer=view==='releases'&&releaseDetail;
    document.querySelector('#identity-label').textContent=reviewer?'reviewer Local':['deployments','settings'].includes(view)?'administrator Local':'author Local';
    document.querySelector('#avatar').textContent=reviewer?'RL':['deployments','settings'].includes(view)?'AD':'AL';
    document.querySelector('#state-preview').disabled=view!=='model';
    document.querySelector('#state-preview').value=editorState;
    document.querySelector('#footer-state').textContent='Design preview · no application changes';
    document.querySelector('#preview-edit').disabled=view!=='model'||!held||['lost','error'].includes(editorState);
    document.querySelector('.app-settings')?.classList.toggle('active',view==='settings');
    iconLabels();
    document.querySelector('.app-settings')?.setAttribute('aria-label','Settings');
  };
  const environmentDesign=createEnvironmentDesign({
    B,I,E,modal,closeModal,toast,go:next=>go(next),render:()=>render(),
    getPackages:()=>releases.filter(r=>r.state==='Packaged'&&!r.revoked&&r.artifact&&r.approval).map(r=>({id:r.id,title:r.title,version:r.version,revisionId:r.artifact.revisionId,digest:r.artifact.digest})),
    recordDeployment:(p,environment)=>{const r=releases.find(x=>x.id===p.id);if(!r)return;r.deploymentTargets=r.deploymentTargets||new Set();r.deploymentTargets.add(environment.id);r.deployed=r.deploymentTargets.size+(r.deploymentTargets.size===1?' environment':' environments')+' · SIMULATED';r.activity.unshift('SIMULATED: '+p.revisionId+' / '+p.digest+' succeeded on '+environment.name+'.');}
  });
  environmentPage=()=>environmentDesign.environmentsPage();
  function versionDialog() {
  modal('Root versions and packaging','<span class="proposal-label">Revised design semantics</span><div class="lifecycle"><div><span class="root-ver">v12</span><span>Packaged<small>Final contents cannot change</small></span></div><div class="current"><span class="root-ver">v13</span><span>Draft → In review → Approved<small>Included edits return approval to In review. The root stays v13.</small></span></div><div><span class="root-ver">v13</span><span>Packaged<small>Create package freezes the approved revision.</small></span></div><div><span class="root-ver">v14</span><span>First edit after packaging<small>No deployment prerequisite</small></span></div></div><p>Only one release is open, including while Approved or Changes requested. Rejected closes that release. Packaged creates the immutable artifact and closes the release.</p><p>Choose changes in Model, then Save. Excluded changes stay in the workspace; their previous packaged versions remain in the complete package. Each review revision and its comments remain in history.</p><p class="hint">Prototype only. Actual packaging must validate references and dependencies before freezing the selected graph.</p>');
}
  function publishPreviewPackage() {
  if(openPackage()){handle('open-active-package');return;}
  if(dirty||selectionDirty){modal('Save before starting a release','<p>Save your workspace edits and release inclusion choices in Model first.</p>','Go to Model','go-model');return;}
  const base=latestPackaged()?currentRevision(latestPackaged()).graph:baseline;
  if(!differences(base,selectedGraph(base)).length){toast('Select and save changes in Model before starting a release.');return;}
  modal('Start release','<label for="candidate-version">Release version</label><input id="candidate-version" placeholder="e.g. 1.5.0" aria-describedby="release-version-help" required><p id="release-version-help" class="hint">A unique major.minor.patch version. Only one release may be open.</p><p id="release-version-error" class="release-version-error" role="alert" hidden></p><label for="candidate-title">Title</label><input id="candidate-title" value="Workspace update"><p>Includes selected saved changes. Review and approval come first; Create package later freezes the approved contents.</p><p class="hint">Design preview only.</p>','Start review','confirm-create-package');
}
  handle = (action,button) => {
    if(environmentDesign.handle(action,button,activeRelease))return;
    if(action==='comment-start'){
      const {package:packageId,source:sourceId,root,side,line}=button.dataset;
      commentComposer=commentAnchor(packageId,root,side,Number(line),sourceId);renderDiscussion();
      document.querySelector('#inline-comment-text')?.focus();return;
    }
    if(action==='comment-cancel'){commentDrafts.delete(commentComposer);commentComposer=null;renderDiscussion();return;}
    if(action==='comment-post'){
      const text=document.querySelector('#inline-comment-text')?.value.trim();
      if(!text){document.querySelector('#inline-comment-text')?.focus();return;}
      const {package:packageId,source:sourceId,root,side,line}=button.dataset;
      reviewThreads.push({id:Math.max(0,...reviewThreads.map(t=>t.id))+1,packageId,sourceId,root,side,line:Number(line),resolved:false,messages:[{author:'reviewer Local',text,when:'Just now'}]});
      rel().activity.unshift(`Comment added to ${packageId}, ${side==='a'?'Before':'After'} line ${line}.`);
      commentDrafts.delete(commentComposer);commentComposer=null;renderDiscussion();return;
    }
    if(action==='comment-reply'){
      const thread=reviewThreads.find(t=>t.id===Number(button.dataset.thread));
      const text=document.querySelector('#reply-'+thread?.id)?.value.trim();
      if(!thread||!text)return;
      thread.messages.push({author:'reviewer Local',text,when:'Just now'});commentDrafts.delete('reply-'+thread.id);renderDiscussion();return;
    }
    if(action==='comment-resolve'){
      const thread=reviewThreads.find(t=>t.id===Number(button.dataset.thread));
      if(thread){thread.resolved=!thread.resolved;rel().activity.unshift(`Discussion ${thread.id} ${thread.resolved?'resolved':'reopened'} on ${thread.packageId}.`);renderDiscussion();}return;
    }
    if(action==='open-feedback-package'){
      const thread=reviewThreads.find(t=>t.id===Number(button.dataset.thread));
      const owner=releases.find(r=>r.revisions.some(p=>p.id===thread?.packageId));
      if(!owner)return;
      activeRelease=owner.id;packageIndex=owner.revisions.findIndex(p=>p.id===thread.packageId);inlineChange=thread.root;section='changes';commentComposer=null;
      releaseAgainstByRelease.set(owner.id,thread.side==='a'?thread.sourceId:owner.baseline.id);go('release');return;
    }
    if(action==='review-decisions'){
      if(rel().state!=='In review'||packageIndex!==rel().revisions.length-1||blocked(pkg()).length||dirty||selectionDirty)return;
      reviewDecision={releaseId:rel().id,revisionId:pkg().id};
      modal(`Review release v${E(rel().version)}`,`<p>Revision ${packageIndex+1} · ${E(rel().reviewer)} · one independent approval required.</p><label for="review-feedback">Review summary</label><textarea id="review-feedback" class="inline-comment-input" rows="3" placeholder="Optional feedback…"></textarea><div class="review-dialog-actions">${B('Approve revision','approve-package','primary','check')}${B('Request changes','request-correction')}${B('Reject','reject-candidate','small ghost')}</div>`,'Cancel','close-modal');return;
    }
    if(action==='version-history'){versionDialog();return;}
    if(action==='preview-edit'){
      if(!held||['lost','error'].includes(editorState)){toast('Lock the root before editing.');return;}
      if(finalized.get(liveGraph.name.id+'@'+liveGraph.name.version)===liveGraph.name.snapshot){
        liveGraph=clone(savedGraph);liveGraph.name={...liveGraph.name,version:liveGraph.name.version+1,snapshot:'draft-'+(liveGraph.name.version+1)+'.1'};
        saveCounter=1;
      }else liveGraph.name={...liveGraph.name,snapshot:`draft-${liveGraph.name.version}.${++saveCounter}`};
      liveGraph.name.content=[...liveGraph.name.content,`  // review edit ${saveCounter}`];
      mainState='Draft';
      dirty=true;editorState='draft';render();toast(openPackage()?'Preview edit · '+(excludedChanges.has('name')?'workspace only':'included in this release')+' · root v'+liveGraph.name.version+'.':'Preview edit · root v'+liveGraph.name.version+' draft for the next release.');return;
    }
    
    if(action==='version-save'){
      if((!dirty&&!selectionDirty)||!held||['lost','error'].includes(editorState))return;
      if(dirty){savedGraph=freeze(clone(liveGraph));workspaceSequence++;}
      dirty=false;selectionDirty=false;editorState='saved';
      Object.values(savedGraph).filter(n=>n.type==='root').forEach(n=>eligible.set(n.id+'@'+n.version,n.snapshot));
      const active=openPackage(),updated=active&&appendWorkspaceRevision(active);
      render();toast(updated?'Saved selected changes to v'+active.version+' · revision '+active.revisions.length+' · In review.':active?'Workspace saved. Release contents and approval unchanged.':'Workspace saved. Start the next release in Releases.');return;
    }
    if(action==='choose-release-changes'){
      if(!held||['lost','error'].includes(editorState))return;
      const active=openPackage(),base=active?active.baseline.graph:latestPackaged()?currentRevision(latestPackaged()).graph:baseline;
      const changed=differences(base,liveGraph);
      modal('Include changes in release','<p>Each choice covers the complete change to a root or resource. Unchecked changes stay in the workspace.</p><div class="selection-box">'+changed.map(c=>'<label class="release-choice"><input type="checkbox" data-release-key="'+E(c.key)+'" '+(excludedChanges.has(c.key)?'':'checked')+'><span>'+E(c.name)+'<small>'+E(c.model)+'</small></span><span class="change-kind '+c.kind+'">'+c.kind+'</span></label>').join('')+'</div><p class="hint">Save in Model applies these choices. Packaged baseline versions remain for excluded changes.</p>','Apply choices','apply-release-choices');return;
    }
    if(action==='apply-release-choices'){
      document.querySelectorAll('[data-release-key]').forEach(input=>{if(input.checked)excludedChanges.delete(input.dataset.releaseKey);else excludedChanges.add(input.dataset.releaseKey);});
      selectionDirty=true;closeModal();render();return;
    }

  if(action==='version-lock'){held=!held;render();return;}
    if(action==='open-correction'){compareId=captureWorkspace();againstId=currentRevision(openPackage()||releases.find(x=>x.id===currentReviewId)).id;go('changes');return;}
    if(action==='compare-root'){if(button.dataset.inline==='true')inlineChange=button.dataset.key;else selectedChange=button.dataset.key;render();return;}
    if(action==='diff-side'||action==='diff-unified'){diffMode=action==='diff-side'?'side':'unified';render();return;}
    if(action==='swap-comparison'){[compareId,againstId]=[againstId,compareId];render();return;}
    if(action==='reset-comparison'){compareId=captureWorkspace();againstId=pBaseline.id;selectedChange='name';render();return;}
    if(action==='refresh-comparison'){compareId=captureWorkspace();render();toast('Comparison refreshed to saved workspace w'+workspaceSequence+'. Unsaved edits remain excluded.');return;}
    if(action==='create-package'){publishPreviewPackage();return;}
    if(action==='open-active-package'){
      const active=openPackage();if(!active)return;
      closeModal();activeRelease=active.id;currentReviewId=active.id;packageIndex=active.revisions.length-1;section='changes';go('release');return;
    }
    if(action==='go-model'){closeModal();go('model');return;}
    if(action==='open-current-review'){closeModal();activeRelease=currentReviewId;packageIndex=rel().revisions.length-1;go('release');return;}
    if(action==='confirm-create-package'){
      if(openPackage()){closeModal();handle('open-active-package');return;}
      if(dirty||selectionDirty){closeModal();go('model');toast('Save the workspace and inclusion choices first.');return;}
      const title=document.querySelector('#candidate-title').value.trim()||'Model update';
      const versionInput=document.querySelector('#candidate-version');
      const version=versionInput.value.trim();
      {
        const valid=/^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)$/.test(version);
        const message=!valid?'Enter a release version such as 1.5.0.':releases.some(r=>r.version===version)?'This release version already exists. Choose a new version.':'';
        if(message){const error=document.querySelector('#release-version-error');error.textContent=message;error.hidden=false;versionInput.setAttribute('aria-invalid','true');versionInput.focus();return;}
      }
      const id=Math.max(...releases.map(x=>x.id))+1;
      const r={id,title,version,state:'In review',revisions:[],baseline:latestPackaged()?currentRevision(latestPackaged()):pBaseline,author:'author Local',reviewer:'reviewer Local',updated:'Just now',deployed:'Not deployed',activity:[]};
      releases.unshift(r);appendWorkspaceRevision(r);
      currentReviewId=r.id;activeRelease=r.id;packageIndex=0;section='changes';closeModal();go('release');return;
    }
    if(action==='open-release'){activeRelease=Number(button.dataset.release);packageIndex=rel().revisions.length-1;section='changes';go('release');return;}
    if(action==='all-releases'){go('releases');return;}
    if(action==='release-filter'){releaseFilter=button.dataset.filter;render();return;}
    if(action==='release-section'){section=button.dataset.section;render();return;}
    if(action==='inspect-package'){packageIndex=Number(button.dataset.index);section='roots';render();return;}
    if(action==='compare-package'){compareId=pkg().id;againstId=rel().baseline.id;go('changes');return;}
    if(action==='edit-package'||action==='correct-version'){
      const active=openPackage();if(!active||active.id!==rel().id){toast('This release is closed. Workspace edits belong to the current or next open release.');return;}
      currentReviewId=active.id;
      mainState=active.state==='In review'?'In review':'Draft';editorState=dirty?'draft':'saved';go('model');return;
    }
    if(action==='request-correction'){
      if(!decisionIsCurrent()){closeModal();toast('Open Review for the latest saved revision.');return;}
      const before=comparisonSources.get(releaseAgainstByRelease.get(rel().id)||rel().baseline.id).pkg;
      const changes=differences(before.graph,pkg().graph);
      if(!changes.length){closeModal();toast('Choose a comparison with changed roots before requesting changes.');return;}
      const roots=Object.fromEntries(changes.map(c=>[c.key,c.b||c.a]));
      modal('Request changes',`<label for="correction-root">Root or resource</label><select id="correction-root">${Object.entries(roots).map(([key,root])=>`<option value="${E(key)}" ${key===inlineChange?'selected':''}>${E(root.name)}</option>`).join('')}</select><label for="correction-feedback">Feedback</label><textarea id="correction-feedback" class="comment-input" rows="3">${E(document.querySelector('#review-feedback')?.value||'Please verify the name mapping before approval.')}</textarea><p>This feedback appears with the selected root in the diff and correction editor. This review revision remains in history. Save in Model updates the same package.</p>`,'Request changes · preview','confirm-correction');return;
    }
    if(action==='confirm-correction'){
      if(!decisionIsCurrent()){closeModal();toast('The package changed. Review its latest revision.');return;}
      const text=document.querySelector('#correction-feedback').value.trim();
      if(!text){document.querySelector('#correction-feedback').focus();return;}
      const root=document.querySelector('#correction-root').value,side=pkg().graph[root]?'b':'a';
      const sourceId=side==='b'?pkg().id:releaseAgainstByRelease.get(rel().id)||rel().baseline.id;
      reviewThreads.push({id:Math.max(0,...reviewThreads.map(t=>t.id))+1,packageId:pkg().id,sourceId,root,side,line:0,kind:'Changes requested',resolved:false,messages:[{author:rel().reviewer,text,when:'Just now'}]});
      inlineChange=root;rel().feedback=text;rel().state='Changes requested';rel().activity.unshift('Changes requested on '+root+'; version number retained.');mainState='Draft';closeModal();render();return;
    }
    
    if(action==='approve-package'){
      const r=rel(),p=pkg();
      if(!decisionIsCurrent()){closeModal();toast('The release changed. Review its latest saved revision.');return;}
      r.state='Approved';r.approval=freeze({packageId:'REL-'+r.id,revisionId:p.id,digest:p.digest});
      r.activity.unshift('Revision '+(packageIndex+1)+' approved. Included changes invalidate approval; Create package will freeze it.');
      held=false;closeModal();render();toast('Approved · still open. Create package to freeze this revision.');return;
    }
    if(action==='reject-candidate'){
      if(!decisionIsCurrent()){closeModal();toast('Review the latest saved revision first.');return;}
      rel().state='Rejected';rel().approval=null;rel().deployed='Not eligible';rel().updated='Just now';rel().activity.unshift('Release rejected and closed. Workspace edits retained; no artifact created.');
      held=false;closeModal();render();return;
    }

  if(action==='version-conflict'){modal('Conflicting root version',`<div class="selection-box">${blocked(pkg()).map(n=>`<strong>${E(n.name)} · v${n.version}</strong><br>Package snapshot: ${n.snapshot}<br>Eligible/finalized snapshot: ${finalized.get(n.id+'@'+n.version)||eligible.get(n.id+'@'+n.version)}`).join('<hr>')}</div><p>Packaging cannot bind the same root/version to different contents. Only the latest valid revision of the active package can be approved. Historical revisions remain read-only; they are not separate active packages.</p>`);return;}
    
    if(action==='release-final'){
      const r=rel(),p=pkg();
      if(r.state!=='Approved'||p.id!==currentRevision(r).id||r.approval?.revisionId!==p.id||dirty||selectionDirty||blocked(p).length)return;
      reviewDecision={releaseId:r.id,revisionId:p.id};
      modal('Create immutable package','<p>Package release v'+E(r.version)+', revision '+(packageIndex+1)+'.</p><div class="selection-box"><code>'+E(p.id)+' · '+E(p.digest)+'</code></div><p>This freezes the approved contents and root versions. Further edits belong to the next release. Deployment remains optional.</p>','Create package · preview','confirm-release-final');return;
    }
    if(action==='confirm-release-final'){
      const r=rel(),p=pkg();
      if(r.state!=='Approved'||reviewDecision?.releaseId!==r.id||reviewDecision.revisionId!==currentRevision(r).id||r.approval?.revisionId!==p.id||dirty||selectionDirty||blocked(p).length){closeModal();toast('The release changed. Review the latest revision before packaging.');return;}
      r.state='Packaged';r.updated='Just now';r.artifact=freeze({revisionId:p.id,digest:p.digest});
      Object.values(p.graph).filter(n=>n.type==='root').forEach(n=>finalized.set(n.id+'@'+n.version,n.snapshot));
      r.activity.unshift('Immutable package created from approved revision '+(packageIndex+1)+'. No deployment requested.');
      held=false;closeModal();render();toast('Packaged · immutable · not deployed.');return;
    }
    if(action==='revoke-candidate'){
      modal('Revoke approval','<p>Before packaging this returns the release to review. A packaged artifact stays immutable; revocation only blocks future deployments.</p>','Revoke · preview','confirm-revoke');return;
    }
    if(action==='confirm-revoke'){
      const r=rel();r.approval=null;
      if(r.state==='Approved')r.state='In review';else r.revoked=true;
      r.activity.unshift('Approval revoked. Packaged contents, if any, remain unchanged.');closeModal();render();return;
    }
  const keep=['toggle-tree','toggle-dock','dock-inspector','dock-diagnostics','cancel-load','env-filter','add-environment','confirm-environment','configure-env','gates','delivery','reconcile','rollback','close-modal','new-root','root','undo','redo','menu-action'];
    if(action==='more'){modal('Root actions','<p>Move root, move content, delete root and model metadata actions retain their existing authorization and lease rules. They follow the same version-cycle gate as ordinary edits.</p>');return;}
    if(keep.includes(action)){
      oldHandle(action,button);
      if(action==='rollback'){const host=document.querySelector('#modal-host');host.innerHTML=host.innerHTML.replaceAll('v1.3.0','v1.2.0').replaceAll('f0b39be8…079b','72a38fc1…a097');}
      if(['gates','delivery','reconcile'].includes(action)){
        const host=document.querySelector('#modal-host');host.innerHTML=host.innerHTML.replaceAll('v1.4.0','v1.3.0');
      }
    }
  };
  document.addEventListener('change',e=>{
    if(e.target.id==='include-root-change'){if(e.target.checked)excludedChanges.delete('name');else excludedChanges.add('name');selectionDirty=true;render();const menu=document.querySelector('#file-release-menu');if(menu)menu.open=true;document.querySelector('#include-root-change')?.focus();}
    if(e.target.id==='release-compare-source'){packageIndex=rel().revisions.findIndex(p=>p.id===e.target.value);commentComposer=null;render();}
    if(e.target.id==='release-against-source'){releaseAgainstByRelease.set(rel().id,e.target.value);commentComposer=null;render();}
    if(e.target.id==='compare-source'){compareId=e.target.value;render();}
    if(e.target.id==='against-source'){againstId=e.target.value;render();}
    if(e.target.id==='package-history'){packageIndex=Number(e.target.value);render();}
  });
  document.addEventListener('input',e=>{
    if(e.target.dataset.commentDraft)commentDrafts.set(e.target.dataset.commentDraft,e.target.value);
    if(e.target.id==='release-search'){releaseQuery=e.target.value;document.querySelector('.release-table tbody').innerHTML=releaseRows();}
  });
  const preview=document.createElement('button');preview.id='preview-edit';preview.dataset.action='preview-edit';preview.className='demo-edit';preview.textContent='Preview edit';preview.title='Design simulation: first edit starts a version; later edits retain it';
  document.querySelector('.study').insertBefore(preview,document.querySelector('#state-preview').previousElementSibling);
  const route=location.hash.slice(1);releaseDetail=['release','pulls'].includes(route);if(releaseDetail)view='releases';if(route==='settings')view='settings';
  render();
})();
