import {el,button,badge,loadingState} from './interface-ui.js';

const labels={PASSED:'Passed',FAILED:'Failed',ERROR:'Error',SKIPPED:'Not run'};
const marks={PASSED:'✓',FAILED:'×',ERROR:'!',SKIPPED:'–'};
export function paintPackageChecks(host,p,onRun){
  host.replaceChildren();
  const release=p.release||{},v=release.validation,running=['QUEUED','RUNNING'].includes(release.jobState);
  const heading=el('div',undefined,'package-section-heading');heading.append(el('h2','Test report'));
  if(v&&!running)heading.append(badge(v.passed?'Passed':'Failed'));
  if(p.canEdit&&!p.finalized){const run=button(running?'Running checks…':'Run checks again',onRun);run.disabled=running;run.title='Run checks on these package contents. Previous approvals must be renewed.';heading.append(run);}
  host.append(heading);
  if(running){
    const stages={RETAINING_DEPENDENCIES:'Preparing the test runtime',LOADING_FIXED_REVISION:'Loading package models',VALIDATING_NATIVE_MODELS_AND_TESTS:'Validating models and running tests',ASSEMBLING_IMMUTABLE_PACKAGE:'Saving the test report'};
    host.append(loadingState(release.jobState==='QUEUED'?'Checks queued':'Checks in progress',stages[release.stage]||'Preparing the package checks',{compact:true}));return;
  }
  if(!v){host.append(el('p','Tests have not run.','check-not-run'),el('p',release.error||'No test report is available yet.','interface-error'));return;}
  const tests=v.tests||[],counts=Object.fromEntries(Object.keys(labels).map(s=>[s,tests.filter(t=>t.status===s).length]));
  const totals=el('div',undefined,'test-totals');totals.setAttribute('aria-label','Test totals');
  for(const [status,label]of Object.entries(labels)){const item=el('span',counts[status]+' '+label.toLowerCase(),'test-total');item.dataset.status=status;totals.append(item);}
  host.append(totals);
  const outcome=el('p',v.passed?'All package checks passed.':'Package checks failed. Resolve the issues before finalizing.','check-outcome');outcome.dataset.status=v.passed?'PASSED':'FAILED';host.append(outcome);
  if(!tests.length)host.append(el('p','No test cases found in this package.','interface-muted'));
  else{
    const list=el('div',undefined,'test-results');list.role='list';list.setAttribute('aria-label','Test results');
    for(const test of tests){
      const row=el('div',undefined,'test-result');row.role='listitem';row.dataset.status=test.status;
      const mark=el('span',marks[test.status]||'?','test-result-mark');mark.setAttribute('aria-hidden','true');
      const text=el('div',undefined,'test-result-copy');text.append(el('strong',test.name||test.test||'Unnamed test'));
      if(test.suite||test.model)text.append(el('small',[test.suite,test.model].filter(Boolean).join(' · ')));
      if(test.error||test.message)text.append(el('p',test.error||test.message,'test-result-message'));
      const state=el('span',labels[test.status]||'Unknown','test-result-status');
      row.append(mark,text,state);list.append(row);
    }
    host.append(list);
  }
  const diagnostics=v.diagnostics||[];
  if(diagnostics.length||v.engineFailures||v.sourceUnchanged===false){
    const section=el('section',undefined,'check-diagnostics');section.append(el('h3','Model validation'));
    if(v.engineFailures)section.append(el('p','The model checker encountered '+v.engineFailures+' internal error(s).','interface-error'));
    if(v.sourceUnchanged===false)section.append(el('p','The test run changed persisted model data. This package cannot be finalized.','interface-error'));
    for(const d of diagnostics){const row=el('div',undefined,'check-diagnostic');row.append(badge(d.severity),el('span',d.message));section.append(row);}
    host.append(section);
  }
}
