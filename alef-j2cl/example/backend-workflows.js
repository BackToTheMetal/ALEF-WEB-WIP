// Release and deployment views reuse the existing editor shell. Every displayed
// value is text, and every decision is checked again by the authenticated API.
import {installInterface} from './interface.js';
import {el as uiEl,modal,field,option,button as uiButton,badge} from './interface-ui.js';
export function installWorkflowControls({api,identity,container,editor}) {
  const element=(tag,text,className)=>{const node=document.createElement(tag);if(text!==undefined)node.textContent=text;if(className)node.className=className;return node;};
  const request=async(method,url,body,id)=>{const response=await api(url,{method,...(body?{body:JSON.stringify(body)}:{}),...(id?{headers:{'Idempotency-Key':id}}:{})});return response.json();};
  function button(parent,text,action){const node=element('button',text);node.type='button';node.onclick=async()=>{node.disabled=true;try{await action();}catch(error){const message=parent.closest('dialog')?.querySelector('[data-workflow-error]');if(message)message.textContent=error.message;else alert(error.message);}finally{node.disabled=false;}};parent.append(node);return node;}
  function dialog(title){const node=element('dialog',undefined,'backend-dialog workflow-dialog');node.append(element('h2',title));const error=element('p');error.dataset.workflowError='';error.setAttribute('role','alert');node.append(error);const body=element('section');node.append(body);button(node,'Close',()=>node.close());node.addEventListener('close',()=>node.remove());document.body.append(node);node.showModal();return{node,body,error};}
  function details(parent,title,value){const d=element('details');d.append(element('summary',title),element('pre',typeof value==='string'?value:JSON.stringify(value,null,2)));parent.append(d);return d;}
  const permission=name=>identity.workspacePermissions?.includes(name);
  installInterface({api,identity,editor,deployment:{deploy:deployRelease,inspect:inspectDeployment,settings:openPlatforms,legacy:inspectRelease}});
  globalThis.alefDeploymentDialog=deployRelease;
  async function openPlatforms(){
    const panel=dialog('Environment settings');
    panel.body.append(element('p','These durable stub platforms verify and retain delivery state. They do not execute ALEF services or deploy to external production infrastructure.'));
    if(permission('MANAGE'))button(panel.body,'Add environment',async()=>{const platforms=await request('GET','/api/v1/platforms'),add=dialog('Add environment'),nameLabel=element('label','Environment name'),name=element('input'),positionLabel=element('label','Position in deployment path'),position=element('select');nameLabel.append(name);for(let i=0;i<=platforms.length;i++){const option=element('option',i<platforms.length?'Before '+platforms[i].name:'After '+(platforms.at(-1)?.name||'start'));option.value=i+1;position.append(option);}positionLabel.append(position);add.body.append(nameLabel,positionLabel);button(add.body,'Add environment',async()=>{if(!name.value.trim())throw new Error('Enter an environment name.');await request('POST','/api/v1/platforms',{name:name.value.trim(),position:Number(position.value)},crypto.randomUUID());add.node.close();await refresh();});});
    const targets=element('section');panel.body.append(targets);let closed=false;panel.node.addEventListener('close',()=>closed=true);
    async function refresh(){const platforms=await request('GET','/api/v1/platforms');if(closed)return;targets.replaceChildren();
      for(const target of platforms){const card=element('article',undefined,'workflow-card');card.append(element('h3',`${target.name} · SIMULATED`),element('p',`Configuration ${target.version} · ${target.config.enabled?'enabled':'disabled'}`),element('code',target.installed||'No simulated package installed'));
        if(permission('MANAGE'))button(card,'Configure '+target.name,()=>configurePlatform(target,refresh));targets.append(card);}
    }
    await refresh();const timer=setInterval(()=>refresh().catch(error=>{targets.replaceChildren();panel.error.textContent=error.message;}),3000);panel.node.addEventListener('close',()=>clearInterval(timer));
  }
  async function configurePlatform(target,onSaved){
    const panel=dialog(`Configure ${target.name} — simulation`);panel.body.append(element('p','Changes create a new configuration version and invalidate queued work or environment approvals bound to the previous version.'));
    const check=(title,value)=>{const label=element('label',title),field=element('input');field.type='checkbox';field.checked=value;label.append(field);panel.body.append(label);return field;};
    const enabled=check('Platform enabled',target.config.enabled),test=check('Require successful delivery of this package to test',target.config.requireTestSuccess),approval=check('Require independent environment approval',target.config.requireEnvironmentApproval);
    const delayLabel=element('label','Simulated duration in milliseconds'),delay=element('input');delay.type='number';delay.min='1000';delay.max='60000';delay.value=target.config.delayMillis;delayLabel.append(delay);panel.body.append(delayLabel);
    const faultLabel=element('label','Simulation outcome'),fault=element('select');fault.setAttribute('aria-label','Simulation outcome');for(const value of ['NONE','FAIL_BEFORE_ACCEPTANCE','FAIL_AFTER_ACCEPTANCE','TIMEOUT_AFTER_ACCEPTANCE']){const option=element('option',value.replaceAll('_',' '));option.value=value;fault.append(option);}fault.value=target.config.fault;faultLabel.append(fault);panel.body.append(faultLabel);
    button(panel.body,'Save platform configuration',async()=>{await request('PUT',`/api/v1/platforms/${target.id}`,{expectedVersion:target.version,enabled:enabled.checked,requireTestSuccess:test.checked,requireEnvironmentApproval:approval.checked,delayMillis:Number(delay.value),fault:fault.value},crypto.randomUUID());panel.node.close();await onSaved();});
  }
  async function deployRelease(input,preferredTarget){
    const platforms=await request('GET','/api/v1/platforms'),packages=Array.isArray(input)?input.filter(p=>p.finalized&&p.canDeploy):null;
    const fixed=packages?null:input,initial=packages?.[0],digest=fixed?.packageDigest||initial?.packageDigest;
    const target=preferredTarget?platforms.find(p=>p.id===preferredTarget):platforms.find(p=>p.canDeploy&&p.config.enabled&&!p.active&&p.installed!==digest&&(!p.previousId||p.previousInstalledDigest===digest&&!p.previousActive));
    const panel=modal(target?.previousId?'Promote package':'Deploy package');
    panel.body.append(uiEl('p','Only approved, finalized packages can be deployed.','interface-muted'));
    let select;if(packages){select=field(panel.body,'Package','select');for(const p of packages)option(select,p.id,(p.version?'v'+p.version+' · ':'')+p.title);if(!packages.length)panel.body.append(uiEl('p','No approved packages are available.'));}
    else panel.body.append(uiEl('strong',fixed?.title||'Approved package'));
    const destination=uiEl('div',undefined,'deployment-destination');destination.append(uiEl('small',target?.previousId?'Promotion':'First environment'),uiEl('p',target?target.previousName?target.previousName+' → '+target.name:target.name:'No environment is ready.'));panel.body.append(destination);
    panel.body.append(uiEl('p',platforms.map(p=>p.name).join(' → '),'interface-muted'));
    if(target?.config.requireEnvironmentApproval)panel.body.append(uiEl('p','An independent environment approval is required before delivery begins.'));
    panel.body.append(uiEl('p','SIMULATED · Records delivery state without running ALEF services.','interface-muted'));
    let pending;const submit=uiButton(target?.previousId?'Promote package':'Deploy package',async()=>{
      const chosen=packages?.find(p=>p.id===select.value);let release=fixed;
      if(packages){if(!chosen)throw new Error('Choose an approved package.');const value=await request('GET',chosen.legacy?'/api/v1/releases/'+chosen.candidateId:'/api/v1/packages/'+chosen.id);if(!chosen.legacy&&!value.canDeploy)throw new Error('This package is no longer approved for deployment.');release=chosen.legacy?value:value.release;}
      if(!target?.canDeploy||!release?.packageDigest||release.candidate.state!=='APPROVED')throw new Error('An approved package and Deploy permission are required.');
      const body={platformId:target.id,candidateId:release.candidate.id,packageDigest:release.packageDigest,configurationVersion:target.version,expectedInstalledDigest:target.installed,purpose:target.previousId?'PROMOTE':'DEPLOY'};
      if(!pending||JSON.stringify(pending.body)!==JSON.stringify(body))pending={id:crypto.randomUUID(),body};
      const delivery=await request('POST','/api/v1/deployments',body,pending.id);panel.node.close();await inspectDeployment(delivery.id);
    },'interface-button primary');
    submit.disabled=!target?.canDeploy||!target.config.enabled||!!target.active||!!target.previousActive||!!target.previousId&&target.previousInstalledDigest!==digest||(packages?!packages.length:fixed?.candidate?.state!=='APPROVED');panel.footer.append(submit);
  }
  async function inspectDeployment(id){
    const panel=modal('Deployment progress');panel.cancel.textContent='Close';let closed=false,last;panel.node.addEventListener('close',()=>closed=true);
    async function refresh(){const delivery=await request('GET',`/api/v1/deployments/${id}`);if(closed||JSON.stringify(delivery)===last)return;last=JSON.stringify(delivery);panel.body.replaceChildren(panel.error);panel.footer.replaceChildren(panel.cancel);
      const uncertain=['FAILED','CANCELLED'].includes(delivery.jobState)&&!['DEPLOYED','FAILED','CANCELLED'].includes(delivery.observedState);
      const state=delivery.stage==='WAITING_FOR_ENVIRONMENT_APPROVAL'?'Awaiting environment approval':uncertain?'Outcome uncertain':delivery.observedState==='DEPLOYED'?'Succeeded':delivery.jobState==='FAILED'?'Failed':delivery.jobState==='CANCELLED'?'Cancelled':delivery.jobState==='QUEUED'?'Queued':'In progress';
      const context=uiEl('div',undefined,'deployment-context');context.append(uiEl('h3',delivery.platform),badge(state));panel.body.append(context,uiEl('p','SIMULATED · Records delivery state without running ALEF services.','interface-muted'));
      if(delivery.error)panel.body.append(uiEl('p',delivery.error,'interface-error'));
      if(['QUEUED','RUNNING'].includes(delivery.jobState)){const progress=uiEl('progress');progress.setAttribute('aria-label','Deployment progress');const latest=[...delivery.events].reverse().find(e=>Number.isFinite(e.details.progress));if(latest){progress.max=100;progress.value=latest.details.progress;}panel.body.append(progress);}
      if(delivery.canCancel)panel.footer.append(uiButton('Cancel deployment',async()=>{await request('POST',`/api/v1/deployments/${id}/cancel`);await refresh();}));
      if(delivery.stage==='WAITING_FOR_ENVIRONMENT_APPROVAL'&&delivery.canApproveEnvironment){const reason=field(panel.body,'Approval comment','textarea');panel.footer.append(uiButton('Approve environment delivery',async()=>{if(!reason.value.trim())throw new Error('Add an approval comment.');await request('POST',`/api/v1/deployments/${id}/environment-approval`,{packageDigest:delivery.packageDigest,configurationVersion:delivery.configurationVersion,reason:reason.value.trim()},crypto.randomUUID());await refresh();},'interface-button primary'));}
      if(delivery.canReconcile)panel.footer.append(uiButton('Reconcile deployment',async()=>{await request('POST',`/api/v1/deployments/${id}/reconcile`,undefined,crypto.randomUUID());await refresh();}));
      const history=uiEl('details'),events=uiEl('ol',undefined,'deployment-events');history.append(uiEl('summary','Delivery history'));for(const e of delivery.events||[])events.append(uiEl('li',new Date(e.at).toLocaleString()+' · '+e.type.toLowerCase().replaceAll('_',' ')));history.append(events);panel.body.append(history);
    }
    await refresh();const timer=setInterval(()=>refresh().catch(error=>{panel.error.textContent=error.message;panel.error.hidden=false;}),1500);panel.node.addEventListener('close',()=>clearInterval(timer));
  }
  async function openReleases(){
    const panel=dialog('Workspace releases');let closed=false;panel.node.addEventListener('close',()=>closed=true);
    const actions=element('div',undefined,'workflow-actions');panel.body.append(actions);
    if(permission('SUBMIT'))button(actions,'Submit release',()=>submitRelease(refresh));
    button(actions,'Refresh',refresh);const list=element('div');panel.body.append(list);
    async function refresh(){try{const releases=await request('GET','/api/v1/releases');list.replaceChildren();for(const release of releases){
      const row=element('article',undefined,'workflow-card');row.append(element('h3',release.title),element('p',`${release.candidate.state} · ${release.jobState}${release.stage?' · '+release.stage:''}`));
      row.append(element('code',release.candidate.sourceRevision));button(row,'Inspect release',()=>inspectRelease(release.candidate.id));list.append(row);
    }if(!releases.length)list.append(element('p','No releases are visible to this account.'));}catch(error){list.replaceChildren();throw error;}}
    await refresh();const timer=setInterval(()=>{if(!closed)refresh().catch(error=>panel.error.textContent=error.message);},3000);panel.node.addEventListener('close',()=>clearInterval(timer));
  }
  async function submitRelease(onSubmitted){
    const options=await request('GET','/api/v1/releases/options'),panel=dialog('Submit a committed workspace revision');
    panel.body.append(element('p','The package includes every committed model and its pinned dependencies. Unsaved editor changes are not included. Editing can continue while this fixed revision is validated.'));
    const label=element('label','Release title'),title=element('input');title.maxLength=200;title.required=true;label.append(title);panel.body.append(label);
    panel.body.append(element('p',`Source: ${options.sourceRevision}`),element('p',options.baselineRevision?`Baseline: approved revision ${options.baselineRevision}`:'Baseline: empty; all imported content must be reviewed.'));
    panel.body.append(element('p','Approval requires one independent human who is neither the submitter nor any contributor.'));
    let pending=null;
    button(panel.body,'Prepare and submit',async()=>{
      if(!title.value.trim())throw new Error('Enter a release title.');
      const body={sourceRevision:options.sourceRevision,baselineCandidateId:options.baselineCandidateId||'EMPTY',title:title.value.trim()};
      if(!pending||JSON.stringify(pending.body)!==JSON.stringify(body))pending={id:crypto.randomUUID(),body};
      const release=await request('POST','/api/v1/releases',pending.body,pending.id);panel.node.close();await onSubmitted();await inspectRelease(release.candidate.id);
    });
  }
  function describeNode(node,depth=0){if(!node)return '(absent)';if(!node.conceptName)return JSON.stringify(node,null,2);
    const name=node.conceptName.split('.').at(-1);const props=Object.entries(node.properties||{}).map(([key,value])=>`${key}: ${value}`).join(', ');
    const refs=(node.references||[]).map(ref=>`${ref.role} → ${ref.model||'(same model)'}/${ref.nodeId||ref.resolveInfo||'(unresolved)'}`).join('\n'+'  '.repeat(depth+1));
    return `${'  '.repeat(depth)}${name} [${node.id}]${props?' · '+props:''}${refs?'\n'+'  '.repeat(depth+1)+refs:''}`+(node.children||[]).map(child=>'\n'+describeNode(child.node,depth+1)).join('');
  }
  async function inspectRelease(id){
    const panel=dialog('Release review');let closed=false,lastSignature;panel.node.addEventListener('close',()=>closed=true);
    async function refresh(){
      const release=await request('GET',`/api/v1/releases/${id}`);if(closed)return;
      const signature=JSON.stringify(release);if(signature===lastSignature)return;lastSignature=signature;panel.body.replaceChildren();
      panel.body.append(element('h3',release.title),element('p',`${release.candidate.state} · ${release.jobState}`));
      const running=['QUEUED','RUNNING'].includes(release.jobState);
      if(running){const progress=element('progress');progress.setAttribute('aria-label','Release preparation');panel.body.append(progress,element('p',(release.stage||'Queued').replaceAll('_',' ')));if(permission('SUBMIT'))button(panel.body,'Cancel preparation',async()=>{await request('POST',`/api/v1/releases/${id}/cancel`);await refresh();});}
      if(release.error)panel.body.append(element('p',release.error,'workflow-error'));
      if(release.packageDigest){panel.body.append(element('p','Exact package SHA-256'),element('code',release.packageDigest));const link=element('a','Download reviewed source package');link.href=`/api/v1/releases/${id}/package`;link.download='';panel.body.append(link);}
      if(release.validation){
        panel.body.append(element('h3','Native validation and Testspraak'));
        panel.body.append(element('p',`${release.validation.passed?'Passed':'Failed'} · ${release.validation.issueCount} diagnostics · ${release.validation.tests?.length||0} tests · persisted source ${release.validation.sourceUnchanged?'unchanged':'CHANGED'}`));
        for(const diagnostic of release.validation.diagnostics||[])panel.body.append(element('p',`${diagnostic.severity}: ${diagnostic.message}`));
        for(const test of release.validation.tests||[])panel.body.append(element('p',`${test.status} · ${test.test}${test.error?' · '+test.error:''}`));
      }
      const modelChanges=element('section');modelChanges.append(element('h3','Model changes'));for(const change of release.semanticChanges||[]){const d=element('details');d.append(element('summary',`${change.kind} · ${change.identity}`));const columns=element('div',undefined,'workflow-comparison');for(const [label,value] of [['Before',change.before],['After',change.after]]){const column=element('section');column.append(element('h4',label),element('pre',describeNode(value)));columns.append(column);}d.append(columns);modelChanges.append(d);}panel.body.append(modelChanges);
      const resourceChanges=element('section');resourceChanges.append(element('h3','Files and logical resources'));for(const change of release.resourceChanges||[]){const d=details(resourceChanges,`${change.kind} · ${change.afterPath||change.beforePath}`,change);button(d,'Inspect resource diff',async()=>{const comparison=element('div',undefined,'workflow-comparison');for(const side of ['before','after']){const path=change[side+'Path'];const column=element('section');column.append(element('h4',side));if(path){const response=await api(`/api/v1/releases/${id}/resource?side=${side}&path=${encodeURIComponent(path)}`);column.append(element('pre',await response.text()));}else column.append(element('pre','(absent)'));comparison.append(column);}d.append(comparison);});}panel.body.append(resourceChanges);
      if(release.manifest)details(panel.body,'Package manifest and pinned dependencies',release.manifest);
      if(release.provenance)details(panel.body,'Contributor and source provenance',release.provenance);
      details(panel.body,'Decision history',release.decisions||[]);
      const decision=async value=>{const reason=prompt(`${value.replaceAll('_',' ')} — reason:`);if(!reason?.trim())return;await request('POST',`/api/v1/releases/${id}/decisions`,{decision:value,expectedVersion:release.candidate.version,candidateDigest:release.candidate.candidateDigest,packageDigest:release.packageDigest,reason},crypto.randomUUID());await refresh();};
      const actions=element('div',undefined,'workflow-actions');panel.body.append(actions);
      if(release.candidate.state==='SUBMITTED'&&release.canApprove){
        const independent=release.submittedBy!==identity.principalId&&!release.provenance?.contributors.includes(identity.principalId);
        const approve=button(actions,'Approve exact package',()=>decision('APPROVE'));approve.disabled=!independent;
        if(!independent)panel.body.append(element('p','An independent reviewer must approve this package; you submitted or contributed to it.'));
        button(actions,'Request changes',()=>decision('CHANGES_REQUESTED'));button(actions,'Reject',()=>decision('REJECT'));
      }
      if(['SEALED','SUBMITTED','APPROVED'].includes(release.candidate.state)&&release.canRevoke)button(actions,'Revoke approval / eligibility',()=>decision('REVOKE'));
      if(release.candidate.state==='APPROVED'&&permission('DEPLOY'))button(actions,'Simulated deployment',()=>globalThis.alefDeploymentDialog?.(release));
    }
    await refresh();const timer=setInterval(()=>refresh().catch(error=>{panel.body.replaceChildren();panel.error.textContent=error.message;}),2500);panel.node.addEventListener('close',()=>clearInterval(timer));
  }
}
