// Isolated UI design simulation. No network, durable jobs or execution platform.
function createEnvironmentDesign(api) {
  const {B,I,E,modal,closeModal,toast,go,render,getPackages,recordDeployment}=api;
  let pathVersion=1,nextEnvironmentId=5,nextOperation=1;
  const path=[
    {id:1,name:'Integration',type:'test',approval:false},
    {id:2,name:'Regression',type:'test',approval:false},
    {id:3,name:'Acceptance',type:'test',approval:false},
    {id:4,name:'Production EU',type:'production',approval:true}
  ];
  const baseline=getPackages().find(p=>p.version==='1.3.0');
  const previous=getPackages().find(p=>p.version==='1.2.0');
  const installed=new Map(path.map((e,i)=>[e.id,i===0?baseline:previous]));
  const history=[];
  let rollout=null,pending=null,selectedStep=0;
  const active=()=>rollout&&!['Completed','Cancelled'].includes(rollout.state);
  const currentPath=()=>active()?rollout.path:path;
  const eligible=p=>getPackages().some(x=>x.id===p.id&&x.revisionId===p.revisionId&&x.digest===p.digest);
  const fixedPath=()=>path.map(e=>Object.freeze({...e}));
  const button=(text,action,extra='',kind='small')=>B(text,action,kind,'',extra);
  const simulation='<span class="pill warn">SIMULATED</span>';
  function packageText(p) {
    return p?'<strong>v'+E(p.version)+'</strong><small><code>'+E(p.digest)+'</code></small>':'<span class="muted">None</span>';
  }
  function rowAction(step,i,route) {
    if(!rollout)return i===0?button('Deploy package','deploy-env'):'<span class="muted">—</span>';
    if(rollout.state==='Cancelled')return i===0?button('Deploy package','deploy-env'):'<span class="muted">—</span>';
    const state=rollout.steps[i]?.state;
    if(state==='Running')return button('View progress','path-progress','data-step="'+i+'"');
    if(state==='Failed')return button('Retry this step','path-retry','data-step="'+i+'"');
    if(state==='Uncertain')return button('Reconcile','path-progress','data-step="'+i+'"');
    if(state==='Waiting'&&i>0&&rollout.steps[i-1].state==='Succeeded')
      return button('Promote to '+E(step.name),'path-promote','data-step="'+i+'"');
    if(state==='Waiting')return '<span class="muted">After '+E(route[i-1]?.name||route[0].name)+'</span>';
    return '<span class="muted">'+(i===route.length-1?'Path complete':'—')+'</span>';
  }
  function environmentsPage() {
    const route=rollout&&rollout.state==='Completed'?rollout.path:currentPath();
    const rows=route.map((e,i)=>{
      const state=rollout&&rollout.state!=='Cancelled'?rollout.steps[i]?.state:'Installed';
      const label=state==='Waiting'?'Awaiting promotion':state;
      const warning=['Failed','Uncertain'].includes(state)?'warn':state==='Succeeded'?'':'neutral';
      return '<tr><td class="path-step">'+(i+1)+'</td><td><span class="env-name">'+I('server')+E(e.name)+'</span><small>'+E(e.type==='test'?'Test':'Production')+'</small></td><td>'+packageText(installed.get(e.id))+'</td><td><span class="pill '+warning+'">'+E(label||'Not started')+'</span>'+(state==='Running'?'<div class="progress-track"><span style="width:65%"></span></div>':'')+'</td><td class="path-actions">'+rowAction(e,i,route)+'</td></tr>';
    }).join('');
    return '<section class="page path-page"><div class="page-heading"><div class="grow"><h1>Deployment</h1></div>'+simulation+
      (rollout?button('Deploy package','deploy-env',active()?'disabled title="Finish or cancel the current promotion path first"':'','primary'):'')+
      '</div><ol class="promotion-path" aria-label="Promotion path">'+route.map((e,i)=>'<li><span class="path-number">'+(i+1)+'</span>'+E(e.name)+(i<route.length-1?I('chevron'):'')+'</li>').join('')+'</ol>'+
      (rollout?'<div class="rollout-context"><strong>Package v'+E(rollout.package.version)+'</strong><code>'+E(rollout.package.digest)+'</code><span class="grow"></span><span>'+E(rollout.state)+'</span>'+(active()?button('Cancel path','path-cancel','','small ghost'):'')+'</div>':'')+
      '<div class="environment-table path-table"><table><thead><tr><th aria-label="Step">#</th><th>Environment</th><th>Installed package</th><th>Status</th><th></th></tr></thead><tbody>'+rows+'</tbody></table></div></section>';
  }
  function settingsPage() {
    return '<section class="page environment-settings"><div class="page-heading"><div class="grow"><h1>Settings</h1><p>Environments and promotion order</p></div>'+button('Add environment','add-environment','','primary')+'</div>'+
      '<p class="hint">Packages follow this path in order. Changes apply to the next deployment.</p>'+
      '<div class="environment-table path-table"><table><thead><tr><th>#</th><th>Environment</th><th>Type</th><th>Approval</th><th>Order</th><th></th></tr></thead><tbody>'+
      path.map((e,i)=>'<tr><td>'+(i+1)+'</td><td><strong>'+E(e.name)+'</strong></td><td>'+E(e.type==='test'?'Test':'Production')+'</td><td>'+(e.approval?'Required':'—')+'</td><td class="order-buttons">'+button('↑','path-up','data-environment="'+e.id+'" aria-label="Move '+E(e.name)+' earlier" '+(i===0||path[i-1].type!==e.type?'disabled':''))+button('↓','path-down','data-environment="'+e.id+'" aria-label="Move '+E(e.name)+' later" '+(i===path.length-1||path[i+1].type!==e.type?'disabled':''))+'</td><td>'+button('Edit','configure-env','data-environment="'+e.id+'" aria-label="Edit '+E(e.name)+'"')+'</td></tr>').join('')+
      '</tbody></table></div><p class="hint">'+simulation+' No real platform configuration is changed.</p></section>';
  }
  function environmentDialog(id) {
    const e=path.find(x=>x.id===id);
    pending={kind:'environment',id:e?.id};
    modal(e?'Edit environment':'Add environment','<label for="path-environment-name">Name</label><input id="path-environment-name" value="'+E(e?.name||'')+'" placeholder="For example: Performance test"><label for="path-environment-type">Type</label><select id="path-environment-type"><option value="test" '+(e?.type==='production'?'':'selected')+'>Test</option><option value="production" '+(e?.type==='production'?'selected':'')+'>Production</option></select><div class="selection-box"><label><input id="path-environment-approval" type="checkbox" '+(e?.approval?'checked':'')+'>Require approval before promotion</label></div><p class="hint">New test environments are inserted before production. The path can be ordered in Settings.</p><p id="path-form-error" class="release-version-error" role="alert"></p>'+simulation,e?'Save settings':'Add environment','path-save-environment');
  }
  function startDialog(selectedRelease) {
    if(active()){go('deployments');toast('Continue the current promotion path before starting another.');return;}
    const list=getPackages();
    if(!list.length){toast('Create an approved package before deploying.');return;}
    pending={kind:'start',pathVersion};
    modal('Deploy package',simulation+'<label for="path-package">Package</label><select id="path-package">'+list.map(p=>'<option value="'+p.id+'" '+(p.id===selectedRelease?'selected':'')+'>v'+E(p.version)+' · '+E(p.title)+'</option>').join('')+'</select><label>First environment</label><div class="selection-box">'+E(path[0].name)+'</div>'+(path[0].approval?'<div class="selection-box"><label><input id="path-start-approved" type="checkbox">Environment approval granted · preview</label></div>':'')+'<p class="hint">'+path.map(e=>E(e.name)).join(' → ')+'</p><p>No real execution. This starts the interactive deployment simulation.</p>','Deploy package · simulated','path-start');
  }
  function progressDialog(i) {
    const step=rollout?.steps[i];
    if(!step||!['Running','Uncertain'].includes(step.state))return;
    selectedStep=i;
    modal(step.state==='Uncertain'?'Reconcile deployment':'Deployment progress',simulation+'<p><strong>v'+E(rollout.package.version)+' → '+E(rollout.path[i].name)+'</strong></p><code>'+E(step.operation)+' · '+E(rollout.package.digest)+'</code>'+
      (step.state==='Running'?'<div class="progress-track" style="margin-top:18px"><span style="width:65%"></span></div><p>Verifying package · 65%</p>':'<p>Look up the existing operation. Promotion is blocked until its outcome is known.</p>')+
      '<div class="simulation-controls">'+button(step.state==='Uncertain'?'Confirm success · simulated':'Complete simulation','path-success','','primary')+button('Simulate failure','path-failure')+(step.state==='Running'?button('Simulate uncertain outcome','path-uncertain'):'')+'</div><p class="hint">These controls change the design preview only.</p>');
  }
  function canAdvance(i) {
    return active()&&eligible(rollout.package)&&i>0&&i<rollout.path.length&&
      rollout.steps[i].state==='Waiting'&&rollout.steps.slice(0,i).every(s=>s.state==='Succeeded');
  }
  function beginStep(i) {
    const step=rollout.steps[i];
    step.state='Running';step.attempts++;
    step.operation=step.operation||'sim-'+nextOperation++;
    rollout.state='In progress';closeModal();go('deployments');progressDialog(i);
  }
  function handle(action,button,selectedRelease) {
    if(action==='environment-settings'){go('settings');return true;}
    if(action==='add-environment'){environmentDialog();return true;}
    if(action==='configure-env'){environmentDialog(Number(button.dataset.environment));return true;}
    if(action==='path-save-environment'){
      if(pending?.kind!=='environment')return true;
      const name=document.querySelector('#path-environment-name').value.trim();
      const type=document.querySelector('#path-environment-type').value;
      const error=!name?'Enter an environment name.':path.some(e=>e.id!==pending.id&&e.name.toLowerCase()===name.toLowerCase())?'Environment names must be unique.':'';
      if(error){document.querySelector('#path-form-error').textContent=error;return true;}
      const old=path.find(e=>e.id===pending.id);
      if(old&&old.type==='test'&&type==='production'&&path.filter(e=>e.type==='test').length===1){document.querySelector('#path-form-error').textContent='Keep a test environment at the start of the path.';return true;}
      const entry={id:old?.id||nextEnvironmentId++,name,type,approval:document.querySelector('#path-environment-approval').checked||type==='production'};
      if(old&&old.type===type)path.splice(path.indexOf(old),1,entry);
      else {if(old)path.splice(path.indexOf(old),1);const pos=type==='test'?path.findIndex(e=>e.type==='production'):-1;path.splice(pos<0?path.length:pos,0,entry);}
      pathVersion++;pending=null;closeModal();render();return true;
    }
    if(action==='path-up'||action==='path-down'){
      const i=path.findIndex(e=>e.id===Number(button.dataset.environment)),j=i+(action==='path-up'?-1:1);
      if(i<0||j<0||j>=path.length||path[i].type!==path[j].type)return true;
      [path[i],path[j]]=[path[j],path[i]];pathVersion++;render();return true;
    }
    if(action==='deploy-approved'||action==='deploy-env'){startDialog(action==='deploy-approved'?selectedRelease:undefined);return true;}
    if(action==='path-start'){
      const p=getPackages().find(x=>x.id===Number(document.querySelector('#path-package')?.value));
      if(active()||pending?.kind!=='start'||pending.pathVersion!==pathVersion||!p){closeModal();toast('Reopen deployment for the current package and path.');return true;}
      if(path[0].approval&&!document.querySelector('#path-start-approved')?.checked){toast('Environment approval is required before this step.');return true;}
      if(rollout)history.push(rollout);
      rollout={package:Object.freeze({...p}),path:fixedPath(),pathVersion,state:'In progress',steps:path.map(()=>({state:'Waiting',operation:null,attempts:0}))};
      pending=null;beginStep(0);return true;
    }
    if(action==='path-promote'){
      const i=Number(button.dataset.step);
      if(!canAdvance(i)){toast('The preceding step must succeed with this exact package.');return true;}
      pending={kind:'promote',index:i,rollout};
      modal('Promote package',simulation+'<p><strong>v'+E(rollout.package.version)+'</strong></p><div class="promotion-destination">'+E(rollout.path[i-1].name)+' '+I('arrow')+' <strong>'+E(rollout.path[i].name)+'</strong></div><code>'+E(rollout.package.digest)+'</code>'+
        (rollout.path[i].approval?'<div class="selection-box"><label><input id="path-gate-approved" type="checkbox">Environment approval granted · preview</label></div>':'')+
        '<p>The same package moves to the next step. Its contents do not change.</p><p id="path-gate-error" class="release-version-error" role="alert"></p>','Promote · simulated','path-confirm-promote');return true;
    }
    if(action==='path-confirm-promote'){
      if(pending?.kind!=='promote'||pending.rollout!==rollout||!canAdvance(pending.index)){closeModal();toast('Promotion is no longer eligible.');return true;}
      const i=pending.index;
      if(rollout.path[i].approval&&!document.querySelector('#path-gate-approved')?.checked){document.querySelector('#path-gate-error').textContent='Environment approval is required before this step.';return true;}
      pending=null;beginStep(i);return true;
    }
    if(action==='path-progress'){progressDialog(Number(button.dataset.step));return true;}
    if(action==='path-retry'){
      const i=Number(button.dataset.step);
      if(!active()||!eligible(rollout.package)||rollout.steps[i]?.state!=='Failed'||!rollout.steps.slice(0,i).every(s=>s.state==='Succeeded'))return true;
      beginStep(i);return true;
    }
    if(['path-success','path-failure','path-uncertain'].includes(action)){
      const step=rollout?.steps[selectedStep];
      if(!active()||!step||!['Running','Uncertain'].includes(step.state))return true;
      if(!eligible(rollout.package)){closeModal();toast('The package is no longer approved for deployment.');return true;}
      step.state=action==='path-success'?'Succeeded':action==='path-failure'?'Failed':'Uncertain';
      if(step.state==='Succeeded'){installed.set(rollout.path[selectedStep].id,rollout.package);recordDeployment(rollout.package,rollout.path[selectedStep]);}
      if(rollout.steps.every(s=>s.state==='Succeeded'))rollout.state='Completed';
      closeModal();render();return true;
    }
    if(action==='path-cancel'){
      if(!active())return true;
      if(rollout.steps.some(s=>s.state==='Uncertain')){toast('Reconcile the uncertain operation before cancelling this path.');return true;}
      rollout.state='Cancelled';rollout.steps.forEach(s=>{if(['Running','Waiting'].includes(s.state))s.state='Cancelled';});
      closeModal();render();toast('Simulation cancelled. Confirmed installations are retained.');return true;
    }
    return false;
  }
  const productionPackages=()=>path.filter(e=>e.type==='production').map(e=>({name:e.name,...installed.get(e.id)})).filter(p=>p.revisionId);
  return {environmentsPage,settingsPage,handle,productionPackages};
}
