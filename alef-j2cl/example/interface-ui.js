export const el=(tag,text,className)=>{const node=document.createElement(tag);if(text!==undefined&&text!==null)node.textContent=String(text);if(className)node.className=className;return node;};
export function loadingState(title,detail,{compact=false}={}){
  const node=el('div',undefined,'interface-loading'+(compact?' compact':'')),message=el('div',undefined,'loading-message'),mark=el('span',undefined,'loading-mark'),copy=el('div');
  node.role='status';node.setAttribute('aria-live','polite');node.setAttribute('aria-atomic','true');
  mark.setAttribute('aria-hidden','true');mark.append(el('span',undefined,'loading-ring'));
  const label=el('strong',title),description=el('p',detail);copy.append(label,description);message.append(mark,copy);
  const skeleton=el('div',undefined,'loading-skeleton');skeleton.setAttribute('aria-hidden','true');
  for(let i=0;i<(compact?1:3);i++){const column=el('div');for(let j=0;j<5;j++)column.append(el('span'));skeleton.append(column);}
  node.append(message,skeleton);node.update=(title,detail)=>{label.textContent=title;description.textContent=detail;};return node;
}
const actionIcons={unlock:'M5 7V4.5a3 3 0 0 1 6 0M3 7h10v8H3zM8 10v2',save:'M2 1.5h9l3 3v10H2zM5 1.5v4h6v-4M5 14.5v-6h6v6',tag:'M2 2h6l6 6-6 6-6-6zM5 5h.01',trash:'M2 4h12M6 4V2h4v2M4 4l1 10h6l1-10M7 7v4M9 7v4',move:'M1 8h14M4 5 1 8l3 3M12 5l3 3-3 3M8 1v14M5 4l3-3 3 3M5 12l3 3 3-3',refresh:'M13 5A5.5 5.5 0 1 0 13.5 10M13 1v4H9',close:'M4 4l8 8M12 4l-8 8',logout:'M6 2H2v12h4M6 8h8M11 5l3 3-3 3'};
export function buttonContent(node,label,name){
  if(node.dataset.buttonLabel===label&&node.dataset.buttonIcon===name)return;
  const svg=icon(name);if(actionIcons[name])svg.firstChild.setAttribute('d',actionIcons[name]);
  node.replaceChildren(svg,el('span',label));node.dataset.buttonLabel=label;node.dataset.buttonIcon=name;
}
export function icon(name){const paths={server:'M2 2h12v5H2zM2 9h12v5H2zM4 4.5h.01M4 11.5h.01M8 4.5h4M8 11.5h4',file:'M4 1.5h5l3 3v10H4zM9 1.5v3h3M6 8h4M6 11h4',folder:'M1.5 4h5l1.5 2h6.5v7h-13z',lock:'M4 7V5a4 4 0 0 1 8 0v2M3 7h10v8H3zM8 10v2',package:'m8 1 6 3.5v7L8 15l-6-3.5v-7L8 1zm0 7 6-3.5M8 8 2 4.5M8 8v7M5 2.75l6 3.5',panel:'M2 2h12v12H2zM6 2v12',settings:'m6 1 4 0 .5 2 1.7 1 2 .1 1 3.8-1.6 1.3-.5 2-1.8 2.8-2-.5-1.3 1.6-3.8-1-.1-2L2 10.5 1 9V6l2-1 1-2zM10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0'};const svg=document.createElementNS('http://www.w3.org/2000/svg','svg');svg.setAttribute('viewBox','0 0 16 16');svg.setAttribute('aria-hidden','true');const p=document.createElementNS(svg.namespaceURI,'path');p.setAttribute('d',paths[name]||paths.file);svg.append(p);return svg;}
export function button(text,action,className='interface-button'){const node=el('button',text,className);node.type='button';node.addEventListener('click',async()=>{if(node.disabled)return;node.disabled=true;try{await action();}catch(error){const host=node.closest('dialog')?.querySelector('[role=alert]')||document.getElementById('workflow-error');if(host){host.textContent=error.message;host.hidden=false;}else document.dispatchEvent(new CustomEvent('alef-interface-error',{detail:error}));}finally{if(node.isConnected)node.disabled=false;}});return node;}
export function field(parent,label,type='text',value=''){const wrap=el('label',undefined,'field'),input=el(type==='textarea'?'textarea':type==='select'?'select':'input');if(input.tagName==='INPUT')input.type=type;input.value=value;input.setAttribute('aria-label',label);wrap.append(el('span',label),input);parent.append(wrap);return input;}
export function option(select,value,label){const o=el('option',label);o.value=value;select.append(o);return o;}
export function modal(title){const node=el('dialog',undefined,'interface-dialog'),header=el('header'),body=el('section'),footer=el('footer'),error=el('p',undefined,'interface-error');error.role='alert';error.hidden=true;const close=button('×',()=>node.close());close.setAttribute('aria-label','Close');header.append(el('h2',title),close);body.append(error);const cancel=button('Cancel',()=>node.close());footer.append(cancel);node.append(header,body,footer);const previous=document.activeElement;node.addEventListener('close',()=>{node.remove();previous?.focus();});document.body.append(node);node.showModal();return{node,body,footer,error,cancel};}
export function badge(text){return el('span',text,'interface-badge '+(/Packaged|Approved|Passed|Succeeded/.test(text)?'good':/review/i.test(text)?'review':/Failed|Rejected|Changes requested/.test(text)?'error':''));}
export function changeBadge(kind){const letters={Added:'A',Deleted:'D',Removed:'D',Modified:'M',Moved:'M',Renamed:'R',Unchanged:'U'},node=el('span',letters[kind]||kind.slice(0,1),'interface-badge change-badge');node.title=kind;node.setAttribute('aria-label',kind);return node;}
export function segments(values,current,onChange){const node=el('div',undefined,'interface-segments');for(const[value,label]of values){const b=button(label,()=>onChange(value),'');b.setAttribute('aria-pressed',String(current===value));node.append(b);}return node;}
export function heading(title,subtitle,actions=[]){const node=el('div',undefined,'interface-heading'),text=el('div'),right=el('div',undefined,'interface-actions');text.append(el('h1',title));if(subtitle)text.append(el('p',subtitle));right.append(...actions);node.append(text,right);return node;}
export function describeNode(node,depth=0){if(!node||node.absent)return[];const line='  '.repeat(depth)+(node.conceptName?.split('.').at(-1)||'Root')+(node.properties?' · '+Object.values(node.properties).filter(Boolean).join(' · '):'');return[line,...(node.references||[]).map(r=>'  '.repeat(depth+1)+r.role+' → '+(r.resolveInfo||r.nodeId||'unresolved')),...(node.children||[]).flatMap(c=>describeNode(c.node,depth+1))];}

/** Line alignment keeps comment anchors tied to original before/after lines. */
export function diffLines(before,after){
  const n=before.length,m=after.length,result=[];
  if(n*m>1000000){before.forEach((text,i)=>result.push({text,type:'removed',before:i+1}));after.forEach((text,i)=>result.push({text,type:'added',after:i+1}));return result;}
  const lengths=Array.from({length:n+1},()=>new Uint32Array(m+1));
  for(let i=n-1;i>=0;i--)for(let j=m-1;j>=0;j--)lengths[i][j]=before[i]===after[j]?lengths[i+1][j+1]+1:Math.max(lengths[i+1][j],lengths[i][j+1]);
  let i=0,j=0;while(i<n||j<m){if(i<n&&j<m&&before[i]===after[j]){result.push({text:before[i],type:'context',before:++i,after:++j});}else if(i<n&&(j===m||lengths[i+1][j]>=lengths[i][j+1])){result.push({text:before[i],type:'removed',before:++i});}else{result.push({text:after[j],type:'added',after:++j});}}
  return result;
}
