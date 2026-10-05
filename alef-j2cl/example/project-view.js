import {defineCustomElement, ref, computed, watch, nextTick, onMounted, onUnmounted, h} from './vendor/vue.runtime.esm-browser.prod.js';
import {projectTree} from './project-tree.js';

const styles = `
  :host { display:block; min-width:0; color:#374356; font:12px/1.45 "Segoe UI",system-ui,sans-serif; background:#f5f6f8; }
  section { display:flex; flex-direction:column; height:100%; overflow:auto; }
  * { box-sizing:border-box; } button,input { font:inherit; }
  .heading { display:flex; justify-content:space-between; align-items:center; gap:6px; min-height:38px; padding:6px 12px; border-bottom:1px solid #dce2ea; flex-shrink:0; }
  h2 { font-size:13px; margin:0; font-weight:650; } .count { color:#6b7782; font-size:11px; }
  .project { font-size:12px; font-weight:600; padding:14px 15px 8px; overflow-wrap:anywhere; }
  .search { display:flex; margin:0 12px 12px; position:relative; }
  input { width:100%; min-width:0; border:1px solid #d5dce2; border-radius:5px; background:white; padding:7px 28px 7px 9px; color:inherit; }
  input:focus { outline:2px solid #91a7cd; outline-offset:1px; }
  .clear { position:absolute; right:2px; top:2px; border:0; background:none; padding:4px 7px; color:#637381; cursor:pointer; }
  .tree { overflow:auto; padding:3px 5px 14px; min-height:100px; flex:1; }
  .row { display:flex; align-items:center; gap:6px; height:30px; border-radius:3px; padding-right:8px;
    white-space:nowrap; min-width:max-content; cursor:default; outline:none; }
  .row:hover { background:#eaf0f7; } .row:focus-visible { box-shadow:inset 0 0 0 1px #526da5; }
  .row[aria-selected=true] { background:#526da5; color:white; }
  .disclosure { display:grid; place-items:center; width:16px; height:24px; border:0; padding:0; background:none; color:inherit; flex-shrink:0; }
  .icon { width:16px; height:16px; flex-shrink:0; } .folder { color:#b28a43; } .model { color:#59829b; } .root { color:#537dac; }
  .row[aria-selected=true] .icon { color:#f4e4bb; }
  .concept { font-size:10px; color:#798590; margin-left:4px; } .row[aria-selected=true] .concept { color:#e2e9f6; }
  .empty { color:#798590; padding:16px; margin:0; font-size:12px; }
  .new-root,.form-actions button { border:1px solid #ced8e3; border-radius:4px; background:white; color:#344f70; padding:5px 9px; cursor:pointer; font:inherit; }
  button:disabled { opacity:.5; cursor:default; } button:focus-visible { outline:2px solid #526da5; outline-offset:2px; }
  .create { margin:12px; padding:12px; background:white; border:1px solid #cbd7e5; border-radius:5px; }
  .create h3 { font-size:13px; margin:0 0 10px; } .create label { display:block; margin:8px 0; font-size:12px; }
  .create input,.create select { display:block; width:100%; margin-top:4px; padding:6px; font:inherit; border:1px solid #cbd5df; border-radius:4px; background:white; color:inherit; }
  .form-actions { display:flex; gap:7px; margin-top:12px; } .form-actions button[type=submit] { background:#526da5; color:white; border-color:#526da5; }
  .form-error { font-size:12px; color:#a23030; overflow-wrap:anywhere; } .hint { font-size:11px; color:#74818d; }
  .context-menu { position:fixed; z-index:10000; width:190px; padding:5px; border:1px solid #b8c4d0; border-radius:5px; background:white; box-shadow:0 4px 16px #17243226; }
  .context-menu button { display:block; width:100%; border:0; background:white; color:#273745; padding:7px 12px; text-align:left; border-radius:2px; font:inherit; cursor:pointer; }
  .context-menu button:hover,.context-menu button:focus { outline:none; background:#526da5; color:white; }
  :host { background:#fffaf6; color:#544031; }
  .heading { border-color:#e7d8ce; }.project { display:none; }.search { margin-top:12px; }
  .row { min-width:0; height:34px; gap:7px; border-left:2px solid transparent; }
  .row:hover { background:#fff0e2; }.row[aria-selected=true] { background:#ffe7d4; color:#b63700; border-left-color:#ff7109; }
  .row .icon,.row[aria-selected=true] .icon { color:inherit; width:16px; height:16px; }
  .root-name { min-width:0; overflow:hidden; text-overflow:ellipsis; flex:1; }
  .tree-lock,.tree-package { display:grid; place-items:center; width:16px; height:20px; flex-shrink:0; background:transparent; }
  .tree-lock { color:#a96924; }.tree-package { color:#c43c00; }
  .change-badge { flex:0 0 16px; text-align:center; font-size:11px; font-weight:600; }
  .collapse-panel { display:grid; place-items:center; background:none; border:0; color:inherit; cursor:pointer; width:26px; height:26px; }
  .row:focus-visible,input:focus-visible,button:focus-visible { outline:2px solid #ff7109; outline-offset:-2px; }
  .context-menu button:hover,.context-menu button:focus,.form-actions button[type=submit] { background:#c43c00; color:white; }
  .heading .count { display:none; }
  .heading .new-root { padding:3px 6px; font-size:11px; background:transparent; }
  .project { padding-top:12px; font-size:11px; color:#778599; }
`;

function statusIcon(kind) {
  const d={panel:'M2 2h12v12H2zM6 2v12',lock:'M4 7V5a4 4 0 0 1 8 0v2M3 7h10v8H3zM8 10v2',package:'m8 1 6 3.5v7L8 15l-6-3.5v-7L8 1zm0 7 6-3.5M8 8 2 4.5M8 8v7M5 2.75l6 3.5'}[kind];
  return h('svg',{viewBox:'0 0 16 16',width:16,height:16,fill:'none',stroke:'currentColor','stroke-width':1.2,'stroke-linejoin':'round','aria-hidden':'true'},[h('path',{d})]);
}
function icon(kind) {
  const paths = kind === 'package' || kind === 'model'
    ? [h('path', {d: 'M1.5 4h5l1.5 2h6.5v7h-13z'})]
    : kind === 'model'
      ? [h('rect', {x: 2, y: 2, width: 12, height: 12, rx: 1}), h('path', {d: 'M2 6h12M6 6v8'})]
      : [h('path', {d: 'M4 1.5h5l3 3v10H4zM9 1.5v3h3M6 8h4M6 11h4'})];
  return h('svg', {class: ['icon', kind === 'package' ? 'folder' : kind], viewBox: '0 0 16 16', fill: 'none', stroke: 'currentColor', 'stroke-width': 1.15, 'stroke-linejoin': 'round', 'aria-hidden': 'true'}, paths);
}

/** <mps-project-view>: framework-independent snapshot property + root-open event. */
export const MpsProjectView = defineCustomElement({
  props: {project: Object, statuses: {type:Object,default:()=>({})}, readOnly: Boolean, collapsed: Boolean, selectedRoot: {type: String, default: ''}, getRootTypes: Function, createRoot: Function},
  emits: ['root-open','toggle-panel'],
  styles: [styles],
  setup(props, {emit}) {
    const query = ref('');
    const collapsed = ref(new Set());
    const searchCollapsed = ref(new Set());
    watch(query, () => { searchCollapsed.value = new Set(); });
    const focused = ref('');
    const treeElement = ref(null);
    const creating = ref(false), submitting = ref(false), loadingTypes = ref(false), creationError = ref('');
    const modelId = ref(''), concept = ref(''), rootName = ref(''), packageName = ref(''), types = ref([]);
    const newButton = ref(null), typeSelect = ref(null);
    const contextMenu = ref(null), menuElement = ref(null);
    const dismissContext = () => { contextMenu.value = null; };
    const outsidePointer = event => { if (!event.composedPath().includes(menuElement.value)) dismissContext(); };
    onMounted(() => {
      document.addEventListener('pointerdown', outsidePointer);
      window.addEventListener('blur', dismissContext);
      window.addEventListener('resize', dismissContext);
      window.addEventListener('scroll', dismissContext, true);
    });
    onUnmounted(() => {
      document.removeEventListener('pointerdown', outsidePointer);
      window.removeEventListener('blur', dismissContext);
      window.removeEventListener('resize', dismissContext);
      window.removeEventListener('scroll', dismissContext, true);
    });
    let typeRequest = 0;
    const selectedType = computed(() => types.value.find(type => type.id === concept.value));
    const loadTypes = async () => {
      const request = ++typeRequest;
      loadingTypes.value = true; creationError.value = ''; types.value = [];
      try {
        const result = await props.getRootTypes(modelId.value);
        if (request !== typeRequest) return;
        types.value = [...result].sort((a, b) => a.name.localeCompare(b.name));
        if (!types.value.some(type => type.id === concept.value)) concept.value = types.value[0]?.id || '';
      } catch (failure) { if (request === typeRequest) creationError.value = String(failure); }
      finally { if (request === typeRequest) loadingTypes.value = false; }
    };
    const rows = computed(() => {
      const result = [];
      const visit = (nodes, level, parent = '') => nodes.forEach((node, index) => {
        const closed = query.value.trim() ? searchCollapsed.value : collapsed.value;
        const expanded = !!node.children && !closed.has(node.id);
        result.push({...node, level, parent, expanded, position: index + 1, siblings: nodes.length});
        if (expanded) visit(node.children, level + 1, node.id);
      });
      visit(projectTree(props.project, query.value), 1);
      return result;
    });
    const focus = id => {
      focused.value = id;
      nextTick(() => [...(treeElement.value?.querySelectorAll('[role=treeitem]') || [])].find(row => row.dataset.id === id)?.focus());
    };
    watch(rows, value => {
      if (!value.some(row => row.id === focused.value)) focused.value = value[0]?.id || '';
    }, {immediate: true});
    const toggle = row => {
      const state = query.value.trim() ? searchCollapsed : collapsed;
      const next = new Set(state.value);
      if (next.has(row.id)) next.delete(row.id); else next.add(row.id);
      state.value = next;
    };
    const beginCreate = async requested => {
      const context = requested?.kind ? requested : rows.value.find(row => row.id === focused.value);
      modelId.value = context?.modelId || props.project.models[0].id;
      packageName.value = context?.kind === 'package' ? context.path : context?.virtualPackage || '';
      rootName.value = ''; creating.value = true;
      await loadTypes();
      const model = props.project.models.find(model => model.id === modelId.value);
      const preferred = context?.concept || model?.roots.find(root => root.virtualPackage === packageName.value)?.concept;
      const matching = types.value.find(type => type.name === preferred);
      if (matching) concept.value = matching.id;
      if (creating.value) nextTick(() => typeSelect.value?.focus());
    };
    const cancelCreate = () => {
      if (submitting.value) return;
      typeRequest++; creating.value = false;
      nextTick(() => newButton.value?.focus());
    };
    const submitCreate = async event => {
      event.preventDefault();
      if (submitting.value || loadingTypes.value || !selectedType.value) return;
      submitting.value = true; creationError.value = '';
      try {
        const root = await props.createRoot({modelId: modelId.value, concept: concept.value,
          name: rootName.value.trim(), virtualPackage: packageName.value.trim()});
        query.value = ''; collapsed.value = new Set();
        creating.value = false;
        focus(root.id);
      } catch (failure) { creationError.value = String(failure); }
      finally { submitting.value = false; }
    };
    const open = row => {
      if (row.kind === 'root') emit('root-open', {id: row.id, name: row.name, concept: row.concept, virtualPackage: row.virtualPackage});
      else toggle(row);
    };
    const showContext = (event, row, keyboard = false) => {
      if (!row || props.readOnly) return;
      event.preventDefault(); event.stopPropagation();
      focus(row.id);
      const rect = event.currentTarget.getBoundingClientRect();
      contextMenu.value = {row, x: Math.max(6, Math.min(keyboard ? rect.left + 25 : event.clientX, window.innerWidth - 200)),
        y: Math.max(6, Math.min(keyboard ? rect.bottom : event.clientY, window.innerHeight - 90))};
      nextTick(() => menuElement.value?.querySelector('button')?.focus());
    };
    const menuKeydown = event => {
      const buttons = [...menuElement.value.querySelectorAll('button')];
      const index = buttons.indexOf(menuElement.value.getRootNode().activeElement);
      if (event.key === 'Escape') { const id = contextMenu.value.row.id; dismissContext(); focus(id); }
      else if (event.key === 'Tab') { dismissContext(); return; }
      else if (event.key === 'ArrowDown') buttons[(index + 1) % buttons.length].focus();
      else if (event.key === 'ArrowUp') buttons[(index + buttons.length - 1) % buttons.length].focus();
      else if (event.key === 'Home') buttons[0].focus();
      else if (event.key === 'End') buttons.at(-1).focus();
      else return;
      event.preventDefault(); event.stopPropagation();
    };
    const keydown = (event, row) => {
      if (event.altKey || event.ctrlKey || event.metaKey) return;
      const index = rows.value.findIndex(item => item.id === row.id);
      switch (event.key) {
        case 'ContextMenu': showContext(event, row, true); return;
        case 'F10': if (event.shiftKey) { showContext(event, row, true); return; } return;
        case 'ArrowDown': focus(rows.value[Math.min(index + 1, rows.value.length - 1)].id); break;
        case 'ArrowUp': focus(rows.value[Math.max(0, index - 1)].id); break;
        case 'Home': focus(rows.value[0].id); break;
        case 'End': focus(rows.value.at(-1).id); break;
        case 'ArrowRight':
          if (row.children?.length) { if (!row.expanded) toggle(row); else focus(rows.value[index + 1].id); } break;
        case 'ArrowLeft':
          if (row.children && row.expanded) toggle(row); else if (row.parent) focus(row.parent); break;
        case 'Enter': case ' ': open(row); break;
        default: return;
      }
      event.preventDefault(); event.stopPropagation();
    };
    return () => h('section', {'aria-label': 'MPS project'}, [
      h('div', {class: 'heading'}, [h('h2', 'Project'), h('span', {class: 'count'}, `${props.project?.models.reduce((n, model) => n + model.roots.length, 0) || 0} roots`),
        !props.readOnly && h('button', {class: 'new-root', ref: newButton, disabled: !props.project?.models.length || !props.createRoot || !props.getRootTypes || creating.value,
          'aria-expanded': creating.value, 'aria-controls': 'create-root', onClick: beginCreate}, '+ New root'), !props.readOnly && h('button', {class:'collapse-panel',title:'Hide project panel','aria-label':'Hide project panel',onClick:()=>emit('toggle-panel')}, [statusIcon('panel')])]),
      h('div', {class: 'project'}, props.project?.name || 'Loading project…'),
      creating.value && h('form', {id: 'create-root', class: 'create', 'aria-label': 'Create root node', onSubmit: submitCreate,
        onKeydown: event => { if (event.key === 'Escape') { event.preventDefault(); event.stopPropagation(); cancelCreate(); } }}, [
        h('h3', 'New root node'),
        h('label', ['Model', h('select', {'aria-label': 'Model', value: modelId.value, disabled: submitting.value,
          onChange: event => { modelId.value = event.target.value; loadTypes(); }}, props.project.models.map(model => h('option', {value: model.id}, model.name)))]),
        h('label', ['Root type', h('select', {'aria-label': 'Root type', ref: typeSelect, value: concept.value, required: true, disabled: submitting.value || loadingTypes.value,
          onChange: event => concept.value = event.target.value}, types.value.map(type => h('option', {value: type.id}, `${type.name} · ${type.language}`)))]),
        loadingTypes.value && h('p', {class: 'hint', role: 'status'}, 'Loading root types…'),
        !loadingTypes.value && !types.value.length && h('p', {class: 'hint'}, 'No root types are available in this model.'),
        selectedType.value?.named && h('label', ['Name', h('input', {value: rootName.value, required: true, disabled: submitting.value,
          onInput: event => rootName.value = event.target.value})]),
        h('label', ['Virtual package', h('input', {value: packageName.value, placeholder: 'e.g. services.tests', disabled: submitting.value,
          onInput: event => packageName.value = event.target.value})]),
        h('span', {class: 'hint'}, 'Leave the package empty to create at model level.'),
        creationError.value && h('p', {class: 'form-error', role: 'alert'}, creationError.value),
        h('div', {class: 'form-actions'}, [h('button', {type: 'submit', disabled: submitting.value || loadingTypes.value || !selectedType.value}, submitting.value ? 'Creating…' : 'Create'),
          h('button', {type: 'button', disabled: submitting.value, onClick: cancelCreate}, 'Cancel')])
      ]),
      h('div', {class: 'search'}, [
        h('input', {type: 'search', placeholder: 'Find a root…', 'aria-label': 'Find a root', value: query.value, onInput: event => query.value = event.target.value}),
        query.value && h('button', {class: 'clear', 'aria-label': 'Clear root filter', onClick: () => query.value = ''}, '×')
      ]),
      h('div', {class: 'tree', role: 'tree', 'aria-label': 'Project roots', ref: treeElement,
        onContextmenu: event => showContext(event, rows.value.find(row => row.kind === 'model'))}, rows.value.map(row => h('div', {
        key: row.id, class: 'row', role: 'treeitem', 'data-id': row.id, 'data-kind': row.kind,
        'aria-label': row.name, 'aria-level': row.level, 'aria-posinset': row.position, 'aria-setsize': row.siblings,
        'aria-expanded': row.children ? row.expanded : undefined,
        'aria-selected': row.kind === 'root' ? props.selectedRoot === row.id : undefined,
        tabindex: focused.value === row.id ? 0 : -1, style: {paddingLeft: `${Math.max(0,row.level - (row.kind === 'root' ? 2 : 1)) * 16 + 3}px`},
        title: row.kind === 'root' ? `${row.name} · ${row.concept}\n${row.virtualPackage || '(default package)'}` : row.path || row.name,
        onFocus: () => focused.value = row.id,
        onClick: () => { focus(row.id); open(row); }, onKeydown: event => keydown(event, row),
        onContextmenu: event => showContext(event, row)
      }, [
        h('span', {class: 'disclosure', 'aria-hidden': 'true'}, row.children && h('svg', {viewBox: '0 0 12 12', width: 12, height: 12}, [h('path', {d: row.expanded ? 'm3 4 3 3 3-3' : 'm4 3 3 3-3 3', fill: 'none', stroke: 'currentColor', 'stroke-width': 1.3})])),
        icon(row.kind), h('span', {class:'root-name'}, row.name),
        row.kind === 'root' && props.statuses[row.id]?.change && h('span', {class:'change-badge',title:props.statuses[row.id].change,'aria-label':props.statuses[row.id].change}, props.statuses[row.id].change === 'Removed' ? 'D' : props.statuses[row.id].change.slice(0,1)),
        row.kind === 'root' && props.statuses[row.id]?.lock && h('span', {class:'tree-lock',role:'img','aria-label':props.statuses[row.id].lock.label,title:props.statuses[row.id].lock.label}, [statusIcon('lock')]),
        row.kind === 'root' && props.statuses[row.id]?.packages?.some(p=>p.linked) && h('span', {class:'tree-package',role:'img','aria-label':'Packaged change',title:props.statuses[row.id].packages.filter(p=>p.linked).map(p=>'v'+p.version+' · '+p.title).join('\n')}, [statusIcon('package')])
      ]))),
      !rows.value.length && h('p', {class: 'empty', role: 'status'}, query.value ? 'No matching roots' : props.project ? 'No roots in this project' : 'Waiting for the model…'),
      contextMenu.value && h('div', {ref: menuElement, class: 'context-menu', role: 'menu', 'aria-label': 'Project actions',
        style: {left: `${contextMenu.value.x}px`, top: `${contextMenu.value.y}px`}, onKeydown: menuKeydown}, [
        contextMenu.value.row.kind === 'root' && h('button', {role: 'menuitem', onClick: () => { const row = contextMenu.value.row; dismissContext(); open(row); }}, 'Open'),
        h('button', {role: 'menuitem', disabled: !props.createRoot || !props.getRootTypes || creating.value,
          onClick: () => { const row = contextMenu.value.row; dismissContext(); beginCreate(row); }}, 'New root…')
      ])
    ]);
  }
});
if (!customElements.get('mps-project-view')) customElements.define('mps-project-view', MpsProjectView);
