import {el,button,loadingState} from './interface-ui.js';
import {modelChanges,comparisonGraph} from './model-diff.js';

// Own two model snapshots, not another runtime. Retain them while browsing roots.
class MpsComparisonSession extends HTMLElement {
  connectedCallback(){
    this.id='comparison-'+crypto.randomUUID();
    this.ready=this.prepare();
    this.ready.catch(()=>{}); // An empty filter may have no editor awaiting it.
  }
  async prepare(){
    const {graphs,dependencies}=this.comparison;
    // Direct links can render the comparison before workspace startup finishes.
    while(this.isConnected&&window.alefExample?.status()!=='READY'){
      if(window.alefExample?.status().startsWith('FAIL:'))throw new Error(window.alefExample.status());
      await new Promise(resolve=>setTimeout(resolve,30));
    }
    if(!this.isConnected)throw new Error('Comparison closed');
    const models=typeof dependencies==='function'?dependencies():dependencies;
    window.alefExample.prepareComparison(this.id,
      JSON.stringify(comparisonGraph(graphs.before,models)),
      JSON.stringify(comparisonGraph(graphs.after,models)));
    while(this.isConnected){
      const status=window.alefExample.comparisonStatus(this.id);
      if(status==='READY')return;
      if(status!=='LOADING')throw new Error(status);
      await new Promise(resolve=>setTimeout(resolve,30));
    }
    throw new Error('Comparison closed');
  }
  disconnectedCallback(){window.alefExample?.closeComparison(this.id);}
}
customElements.define('mps-comparison-session',MpsComparisonSession);
export function mpsComparisonSession(graphs,dependencies=[]){
  const session=document.createElement('mps-comparison-session');
  session.comparison={graphs,dependencies};return session;
}

class MpsModelDiff extends HTMLElement {
  connectedCallback(){if(!this.comparison||this.started)return;this.started=true;this.render();}
  disconnectedCallback(){
    this.started=false;clearInterval(this.poll);this.resize?.disconnect();
    for(const side of Object.keys(this.views||{}))window.alefExample?.closeComparisonEditor(this.comparison.session.id,side);
    this.replaceChildren();
  }
  async render(){
    const generation=this.generation=Symbol(),{root,session,onComment,canComment=()=>false}=this.comparison;
    this.delta=modelChanges(root.before,root.after);this.views={};this.states={};
    if(root.beforeReference&&root.afterReference&&root.beforeReference!==root.afterReference&&!this.delta.changes.some(c=>c.id===root.nodeId))this.delta.changes.unshift({id:root.nodeId,kind:'moved'});
    const legend=el('div',undefined,'mps-diff-legend');for(const[kind,label]of [['removed','Removed text'],['added','Added text']])legend.append(el('span',label,kind));
    this.columns=el('div',undefined,'mps-diff-columns');this.append(legend,this.columns);this.dataset.state='loading';
    for(const side of ['before','after']){
      const pane=el('section',undefined,'mps-diff-pane'),header=el('div',undefined,'mps-diff-pane-heading');header.append(el('strong',side==='before'?'Before':'After'));pane.append(header);this.columns.append(pane);
      if(!root[side]||root[side].absent){pane.append(el('p',side==='before'?'Not present in the baseline':'Removed from this version','interface-empty'));continue;}
      const status=loadingState('Opening comparison…','Loading saved models for this view.',{compact:true});status.classList.add('mps-diff-status');
      const surface=el('div',undefined,'mps-diff-surface');surface.id='mps-surface-'+crypto.randomUUID();surface.setAttribute('aria-label',(side==='before'?'Before':'After')+' · '+(root.name||root.nodeId)+' · read-only MPS editor');
      surface.setAttribute('aria-busy','true');const view={surface,status};this.views[side]=view;
      if(onComment&&canComment(side)){view.comment=button('Comment on selected node',()=>{const id=this.states[side]?.selectedNodeId,anchor=this.delta[side].get(id);if(anchor)onComment(side,anchor.line);});view.comment.disabled=true;header.append(view.comment);}
      pane.append(status,surface);
    }
    const active=()=>this.isConnected&&this.generation===generation;
    const fail=error=>{if(!active())return;clearInterval(this.poll);this.dataset.state='error';for(const view of Object.values(this.views)){view.surface.setAttribute('aria-busy','false');view.status.textContent=error.message;view.status.className='mps-diff-status interface-error';view.status.role='alert';view.status.hidden=false;}};
    try{
      await session.ready;if(!active())return;
      for(const [side,view] of Object.entries(this.views)){
        const reference=root[side+'Reference'];if(!reference)throw new Error('The model reference for this version is unavailable.');
        window.alefExample.openComparison(session.id,side,reference,JSON.stringify(this.delta.changes.filter(c=>this.delta[side].has(c.id))),view.surface.id);
        view.status.hidden=true;view.surface.setAttribute('aria-busy','false');
      }
      const readState=()=>{for(const [side,view] of Object.entries(this.views)){const state=window.alefExample.comparisonState(session.id,side);if(!state)throw new Error('Comparison closed. Reopen this view to refresh its models.');this.states[side]=state;if(view.comment)view.comment.disabled=!state.selectedNodeId||!this.delta[side].has(state.selectedNodeId);}};
      readState();this.dataset.state='ready';
      this.poll=setInterval(()=>{try{readState();}catch(error){fail(error);}},150);
      this.resize=new ResizeObserver(()=>{for(const [side,view] of Object.entries(this.views))window.alefExample.layoutComparison(session.id,side,view.surface.clientWidth,view.surface.clientHeight);});
      for(const view of Object.values(this.views))this.resize.observe(view.surface);
    }catch(error){fail(error);}
  }
  focusNode(side,id){if(this.views[side]&&this.dataset.state==='ready')window.alefExample.focusComparison(this.comparison.session.id,side,id);}
  focusAnchor(side,line){const entry=[...this.delta[side]].find(([,value])=>value.line===line);if(entry)this.focusNode(side,entry[0]);}
}
customElements.define('mps-model-diff',MpsModelDiff);
export function mpsDiff(comparison){const view=document.createElement('mps-model-diff');view.comparison=comparison;return view;}
