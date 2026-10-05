// Design-only lock examples. The active root follows the existing preview lease.
function projectTreeWithLocks({groups,graph,concepts,held,editorState,dirty,E,I}) {
  const examples={
    'root-move2':{state:'other',label:'Locked by editor Local',detail:'Another editing session holds this root. Read only until its lease is released or expires.'},
    'root-test':{state:'other',label:'Locked by reviewer Local',detail:'Another editing session holds this root. Read only until its lease is released or expires.'},
    'root-ubc':{state:'other-tab',label:'Locked by you in another tab',detail:'This tab does not own the lease. Continue editing in the owning tab, or release its lock first.'}
  };
  const lockFor=root=>{
    if(editorState==='error')return {state:'unknown',label:'Lock status unknown',detail:'Connection interrupted. Last known ownership cannot be confirmed. Reconnect before editing.'};
    if(root.id==='root-name'){
      if(editorState==='lost')return {state:'unknown',label:'Your lock expired',detail:'Your local draft is preserved. Refresh ownership and reacquire a lock before saving.'};
      if(held)return {state:'mine',label:'Locked by you',detail:'Owner: author Local · this tab. Your lease permits editing. Use Release lock to make this root available.'};
    }
    return examples[root.id]||{state:'available',label:'Unlocked',detail:'No active lease. Acquire this root’s lock before editing.'};
  };
  return groups.map(group=>'<div class="tree-group">'+I('down')+I('folder')+'<span title="'+E(group)+'">'+E(group)+'</span></div>'+
    Object.values(graph).filter(root=>root.type==='root'&&root.model===group).map(root=>{
      const lock=lockFor(root),selected=root.id==='root-name';
      const details=lock.label+'\n'+lock.detail+'\nDesign preview · illustrative lock state';
      const tooltip=root.name+' · '+(concepts[root.id]||'Root')+'\n'+details;
      const symbol=lock.state==='available'?'unlock':lock.state==='unknown'?'circle':'lock';
      return '<button class="'+(selected?'selected':'')+'" data-action="root" data-root-id="'+E(root.id)+'" title="'+E(tooltip)+'" aria-label="'+E(root.name+' · '+lock.label)+'">'+
        '<span class="tree-node-icon">'+I('file')+'</span><span class="label">'+E(root.name)+'</span>'+
        '<span class="tree-draft-slot">'+(selected&&dirty?'<span class="dot" title="Unsaved changes" aria-label="Unsaved changes"></span>':'')+'</span>'+
        '<span class="tree-lock '+lock.state+'" data-lock-state="'+lock.state+'" role="img" aria-label="'+E(lock.label)+'" title="'+E(details)+'">'+I(symbol)+'</span></button>';
    }).join('')).join('');
}
