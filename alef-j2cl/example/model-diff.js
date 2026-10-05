// Compare stable MPS nodes and features, not a flattened textual presentation.
export function indexNodes(root) {
  const result=new Map();let line=0;
  function walk(node,parent=null,role=null){if(!node||node.absent)return;const anchor=++line;line+=(node.references||[]).length;result.set(node.id,{node,parent,role,line:anchor});for(const c of node.children||[])walk(c.node,node.id,c.role);}
  walk(root);return result;
}
const canonical=value=>Array.isArray(value)?value.map(canonical):value&&typeof value==='object'?Object.fromEntries(Object.keys(value).sort().map(k=>[k,canonical(value[k])])):value;
const equal=(a,b)=>JSON.stringify(canonical(a))===JSON.stringify(canonical(b));
export function modelChanges(before,after) {
  const left=indexNodes(before),right=indexNodes(after),changes=new Map();
  const mark=(id,kind)=>{if(!changes.has(id)||kind!=='moved')changes.set(id,{...changes.get(id),id,kind,before:left.get(id)?.line,after:right.get(id)?.line});if(kind==='moved')changes.get(id).whole=true;};
  for(const[id,{node:a,parent,role}]of left){const b=right.get(id);if(!b){mark(id,'removed');continue;}
    if(a.concept!==b.node.concept||!equal(a.properties||{},b.node.properties||{})||!equal([...(a.references||[])].sort((x,y)=>x.role.localeCompare(y.role)),[...(b.node.references||[])].sort((x,y)=>x.role.localeCompare(y.role))))mark(id,'modified');
    if(a.concept!==b.node.concept)changes.get(id).whole=true;
    // Reference identity can change while its displayed name stays identical.
    const oldRefs=new Map((a.references||[]).map(r=>[r.role,r])),newRefs=new Map((b.node.references||[]).map(r=>[r.role,r]));
    const references=[...new Set([...oldRefs.keys(),...newRefs.keys()])].filter(role=>{
      const x=oldRefs.get(role),y=newRefs.get(role);return x?.nodeId!==y?.nodeId||x?.model!==y?.model||x?.kind!==y?.kind;
    });
    if(references.length)changes.get(id).references=references;
    if(parent!==b.parent||role!==b.role)mark(id,'moved');
    // Keep inserted/deleted siblings from being mistaken for moved nodes.
    const oldChildren=(a.children||[]).map(c=>c.node.id).filter(id=>right.has(id)&&right.get(id).parent===a.id);
    const newChildren=(b.node.children||[]).map(c=>c.node.id).filter(id=>left.has(id)&&left.get(id).parent===a.id);
    if(!equal(oldChildren,newChildren))for(const id of newChildren)if(oldChildren.indexOf(id)!==newChildren.indexOf(id))mark(id,'moved');
  }
  for(const id of right.keys())if(!left.has(id))mark(id,'added');
  return {before:left,after:right,changes:[...changes.values()]};
}

export function comparisonGraph(graph,dependencies=[]) {
  if(!graph?.models)throw new Error('The retained model graph is unavailable. Refresh the comparison.');
  const models=structuredClone(graph.models),references=new Set(models.map(m=>m.reference));
  // Retained native graphs have node IDs but no live workspace root IDs. The
  // bridge needs a local identity even though comparison never takes a lease.
  for(const model of models)for(const root of model.roots||[])root.rootId??=model.reference+'/'+root.id;
  for(const model of dependencies)if(model.readOnly&&!references.has(model.reference))models.push(structuredClone(model));
  return {...graph,models};
}

// Join two authorized contents responses by stable root identity. Each side
// keeps its retained graph, so neither package is substituted with workspace data.
export function compareContents(beforeContents,afterContents,beforeSide='after',afterSide='after') {
  function snapshot(contents,side){
    const graph=contents[side+'Graph'];
    if(!graph?.models)throw new Error('The selected package contents are unavailable.');
    const modelNames=new Map(graph.models.map(model=>[model.reference,model.name]));
    const roots=new Map();
    for(const root of contents.roots||[]){
      const node=root[side],reference=root[side+'Reference'];if(!node||node.absent)continue;
      roots.set(root.rootId,{node,reference,
        name:Object.values(node.properties||{}).find(value=>typeof value==='string'&&value.trim())||node.conceptName||root.name,
        modelName:modelNames.get(reference?.slice(0,reference.lastIndexOf('/')))||root.modelName});
    }
    return {graph,roots};
  }
  const before=snapshot(beforeContents,beforeSide),after=snapshot(afterContents,afterSide);
  const roots=[...new Set([...after.roots.keys(),...before.roots.keys()])].map(rootId=>{
    const left=before.roots.get(rootId),right=after.roots.get(rootId),shown=right||left;
    const changed=!left||!right||left.reference!==right.reference||modelChanges(left.node,right.node).changes.length>0;
    return {rootId,nodeId:shown.node.id,name:shown.name,modelName:shown.modelName,
      before:left?.node||null,after:right?.node||null,beforeReference:left?.reference,afterReference:right?.reference,
      changed,deleted:!right,kind:!left?'Added':!right?'Deleted':changed?'Modified':'Unchanged'};
  });
  return {beforeGraph:before.graph,afterGraph:after.graph,roots};
}
