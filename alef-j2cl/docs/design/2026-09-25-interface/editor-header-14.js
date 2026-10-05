// Compact controls for the isolated design prototype, not the generated editor.
function compactFileToolbar({B,I,E,root,state,active,held,dirty,selectionDirty,editorState,excluded}) {
  const unavailable=['lost','error'].includes(editorState);
  const saveText=dirty?'Unsaved':selectionDirty?'Inclusion pending':'Saved';
  const lockText=editorState==='lost'?'Lease expired':editorState==='error'?'Disconnected':held?'Locked':'Lock to edit';
  const lockTitle=held?'Locked by you in this tab. Release lock.':'Read only. Acquire this root’s lock to edit.';
  const releaseLabel='Latest workspace';
  const disabled=!held||unavailable?'disabled':'';
  const releasePanel='<div class="file-menu-panel"><div class="file-menu-heading"><strong>'+E(releaseLabel)+'</strong>'+
    '<span class="muted">Save updates the latest workspace</span></div>'+
    (active?'<p class="file-menu-note">Release v'+E(active.version)+' has its own editor.</p>'+B('Open release editor','open-release-editor','file-menu-action')+B('Compare with release','open-correction','file-menu-action'):
    '<label class="file-inclusion"><input id="include-root-change" type="checkbox" '+(excluded?'':'checked')+' '+disabled+'>Include in the next release</label>'+B('Choose changes…','choose-release-changes','file-menu-action','',disabled))+'</div>';
  const morePanel='<div class="file-menu-panel file-secondary-menu">'+
    B('Version history','version-history','file-menu-action')+
    B('Undo','undo','file-menu-action','undo')+B('Redo','redo','file-menu-action','redo')+
    B('Root actions…','more','file-menu-action')+'</div>';
  return '<div class="file-toolbar" aria-label="File controls"><div class="file-version"><span class="root-ver">v'+root.version+'</span><span class="file-version-state">'+E(state)+'</span></div>'+
    '<span class="grow"></span><span class="file-save-state '+(dirty||selectionDirty?'pending':'')+'" role="status">'+saveText+'</span>'+
    '<details class="file-menu" id="file-release-menu"><summary aria-label="Workspace options" title="Latest workspace"><span>Latest workspace</span>'+I('down')+'</summary>'+releasePanel+'</details>'+
    B(lockText,'version-lock','file-lock '+(held?'held':''),'lock',(unavailable?'disabled ':'')+'title="'+E(lockTitle)+'" aria-label="'+E(unavailable?lockText:held?'Release lock':'Lock root')+'"')+
    B('Save','version-save','primary file-save','check',(!dirty&&!selectionDirty)||!held||unavailable?'disabled':'')+
    '<details class="file-menu file-overflow" id="file-more-menu"><summary aria-label="More file actions" title="More file actions">'+I('more')+'</summary>'+morePanel+'</details></div>';
}
document.addEventListener('click',event=>{
  document.querySelectorAll('.file-menu[open]').forEach(menu=>{
    if(!menu.contains(event.target))menu.open=false;
  });
});
document.addEventListener('keydown',event=>{
  if(event.key!=='Escape')return;
  const menu=document.querySelector('.file-menu[open]');
  if(menu){menu.open=false;menu.querySelector('summary')?.focus();}
});
