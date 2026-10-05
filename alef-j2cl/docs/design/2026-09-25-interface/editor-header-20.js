// Compact controls for the isolated design prototype, not the generated editor.
function compactFileToolbar({B,I,E,root,state,active,held,dirty,editorState,changeTags=[],destinationLabel='Workspace only',destinationDetail='',lockBlocked=''}) {
  const unavailable=['lost','error'].includes(editorState)||!!lockBlocked;
  const saveText=dirty?'Unsaved':'Saved';
  const lockText=lockBlocked?'Locked elsewhere':editorState==='lost'?'Lease expired':editorState==='error'?'Disconnected':held?'Locked':'Lock to edit';
  const lockTitle=lockBlocked||(held?'Locked by you in this tab. Release lock.':'Read only. Acquire this root’s lock to edit.');
  const releaseLabel=destinationLabel;
  const releasePanel='<div class="file-menu-panel"><div class="file-menu-heading"><strong>'+E(releaseLabel)+'</strong>'+
    '<span class="muted">'+E(destinationDetail)+'</span></div>'+
    '<p class="file-menu-note">Linked changes update their open packages on Save. Finalized packages stay unchanged.</p>'+B('Choose packages…','edit-package-destinations','file-menu-action','',(!held||unavailable?'disabled':''))+B('New package','create-package','file-menu-action')+
    active.map(r=>B('View v'+E(r.version),'open-release','file-menu-action','',`data-release="${r.id}"`)).join('')+B('All packages','all-releases','file-menu-action')+'</div>';
  const morePanel='<div class="file-menu-panel file-secondary-menu">'+
    B('Version history','version-history','file-menu-action')+
    B('Undo','undo','file-menu-action','undo')+B('Redo','redo','file-menu-action','redo')+
    B('Root actions…','more','file-menu-action')+'</div>';
  return '<div class="file-toolbar" aria-label="File controls"><div class="file-version"><span class="root-ver">v'+root.version+'</span><span class="file-version-state">'+E(state)+'</span></div>'+
    '<span class="grow"></span><span class="file-save-state '+(dirty?'pending':'')+'" role="status">'+saveText+'</span>'+
    B('Tags'+(changeTags.length?' · '+changeTags.length:''),'change-tags','ghost file-tags','',(!held||unavailable?'disabled ':'')+'title="'+E(changeTags.length?changeTags.join(', '):'Tag this change before saving')+'"')+
    '<details class="file-menu" id="file-release-menu"><summary aria-label="Save destination" title="'+E(destinationDetail)+'"><span>'+E(destinationLabel)+'</span>'+I('down')+'</summary>'+releasePanel+'</details>'+
    B(lockText,'version-lock','file-lock '+(held?'held':''),'lock',(unavailable?'disabled ':'')+'title="'+E(lockTitle)+'" aria-label="'+E(unavailable?lockText:held?'Release lock':'Lock root')+'"')+
    B('Save','version-save','primary file-save','check',!dirty||!held||unavailable?'disabled':'')+
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
