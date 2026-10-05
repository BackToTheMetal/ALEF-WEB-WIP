// Tags identify exact saved content. They are metadata, not model changes.
function normalizeChangeTags(text){
  return [...new Set(String(text).split(',').map(tag=>tag.trim().toLowerCase()).filter(Boolean))];
}
function createChangeCatalog(){
  const entries=new Map();
  const copy=value=>JSON.parse(JSON.stringify(value));
  const idFor=(key,node,source)=>key+'@'+(node?.snapshot||'deleted-'+source);
  return {
    remember(key,node,tags,source){
      const id=idFor(key,node,source);
      entries.set(id,{id,key,node:node?copy(node):null,tags:[...tags],source});
      return id;
    },
    tagsFor(key,node){return [...(entries.get(idFor(key,node))?.tags||[])];},
    clearTags(versionIds){
      const cleared=[];
      for(const id of new Set(versionIds)){
        const entry=entries.get(id);
        if(!entry?.tags.length)continue;
        cleared.push(copy(entry));entry.tags=[];
      }
      return cleared;
    },
    versions(){return copy([...entries.values()]);}
  };
}
