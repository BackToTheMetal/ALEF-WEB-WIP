// Reconcile a confirmed save's bookkeeping without modifying any MPS models.
// Root contents exclude transport metadata; property/reference order is immaterial.
const canonical=value=>JSON.stringify(normalize(value));
function normalize(value){
  if(Array.isArray(value))return value.map(normalize);
  if(value&&typeof value==='object')return Object.fromEntries(Object.keys(value).sort().map(key=>[key,normalize(value[key])]));
  return value;
}
function content(node){
  if(!node)return null;
  return {id:node.id,concept:node.concept,conceptName:node.conceptName,properties:node.properties,
    references:[...node.references].sort((a,b)=>a.role.localeCompare(b.role)),
    children:node.children.map(child=>({role:child.role,node:content(child.node)}))};
}
const index=graph=>new Map(graph.models.flatMap(model=>model.readOnly?[]:model.roots.map(root=>[root.rootId,{model,root}])));

export function savedWorkspace(current,fresh,receipt,snapshots){
  const next=structuredClone(current),before=index(next),after=index(fresh),contexts=new Set();
  let needsRefresh=false;
  for(const {rootId,version} of receipt.roots){
    const previous=before.get(rootId),saved=after.get(rootId),local=snapshots[rootId];
    // Relocations, concurrent saves or native side effects need an explicit
    // refresh. Do not silently replace the user's editor to apply them.
    if((saved&&saved.root.version!==version)||(saved&&(!previous||saved.model.reference!==previous.model.reference))||
      canonical(content(local))!==canonical(content(saved?.root||null))){needsRefresh=true;continue;}
    if(previous){
      contexts.add(previous.model.reference);
      const i=previous.model.roots.indexOf(previous.root);
      if(saved)previous.model.roots[i]=structuredClone(saved.root);
      else previous.model.roots.splice(i,1); // Metadata only; the local deletion already happened.
    }
  }
  for(const model of next.models)if(contexts.has(model.reference))model.contextDigest=fresh.models.find(m=>m.reference===model.reference).contextDigest;
  return {graph:next,needsRefresh};
}
