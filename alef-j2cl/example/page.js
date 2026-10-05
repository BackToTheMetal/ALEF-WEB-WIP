'use strict';
import './project-view.js';
const element = id => document.getElementById(id);
const projectView = element('project-view');
const workspace = document.querySelector('.workspace');
const surface = element('alef-editor');
let initialized = false, error = '', selectedRoot = '', projectSnapshot = '';
let roots = [], inspectorVisible = true, inspectorSize = 190, layoutKey = '', frame = 0;
const externalRoots = new Map();

function showError(message) {
  error = String(message);
  element('error').textContent = error;
  element('error').hidden = false;
  element('status').dataset.state = 'error';
  element('status-text').textContent = 'Workspace needs attention';
}
function run(action) { try { return action(); } catch (failure) { showError(failure); } }
window.addEventListener('error', event => showError(event.message || 'Could not load the workspace'));
window.addEventListener('unhandledrejection', event => showError(event.reason));

function rootHeading(root) {
  element('editor-path').textContent = root ? `${root.virtualPackage ? root.virtualPackage.replaceAll('.', ' / ') + ' / ' : ''}${root.name || root.concept}` : 'Select a root in the project view';
  element('editor-concept').textContent = root?.concept || '';
  document.title = root ? `${root.name || root.concept} — ALEF` : 'ALEF IDE';
}
function openProjectRoot(root, alreadyOpened = false) {
  root = roots.find(item => item.id === root.id) || root;
  if (!alreadyOpened) window.alefExample.openRoot(root.id);
  selectedRoot = root.id; projectView.selectedRoot = root.id;
  document.dispatchEvent(new CustomEvent('alef-root-selected', {detail:root}));
  workspace.classList.remove('empty'); element('empty-editor').hidden = true;
  rootHeading(root);
  scheduleLayout();
}
function clearSelection() {
  selectedRoot = ''; projectView.selectedRoot = '';
  document.dispatchEvent(new CustomEvent('alef-root-selected', {detail:null}));
  workspace.classList.add('empty'); element('empty-editor').hidden = false; rootHeading(null);
}
function updateProject() {
  const project = window.alefExample.projectTree();
  const snapshot = JSON.stringify(project);
  if (snapshot === projectSnapshot) return;
  projectSnapshot = snapshot; projectView.project = project;
  const projectRoots = project.models.flatMap(model => model.roots);
  roots = [...projectRoots, ...externalRoots.values()];
  element('root-count').textContent = `${projectRoots.length} roots`;
  if (selectedRoot && !roots.some(root => root.id === selectedRoot)) {
    if (roots[0]) openProjectRoot(roots[0]); else clearSelection();
  } else rootHeading(roots.find(root => root.id === selectedRoot));
}
projectView.addEventListener('root-open', event => run(() => openProjectRoot(event.detail[0])));
document.addEventListener('alef-workspace-refreshed',()=>run(()=>{
  projectSnapshot='';layoutKey='';error='';element('error').hidden=true;externalRoots.clear();
  updateProject();const root=roots.find(r=>r.id===selectedRoot)||roots[0];if(root)openProjectRoot(root);updateInspector();
}));
document.addEventListener('alef-reference-navigation', () => run(() => {
  const root = window.alefExample.takeNavigation();
  if (!root) return;
  if (root.external) externalRoots.set(root.id, root);
  if (!roots.some(item => item.id === root.id)) roots.push(root);
  openProjectRoot(root, true);
  updateInspector();
}));
projectView.getRootTypes = modelId => window.alefExample.rootTypes(modelId);
projectView.createRoot = async request => {
  const id = window.alefBackendMode ? await window.alefBackend.createRoot(request)
    : window.alefExample.createRoot(request.modelId, request.concept, request.name, request.virtualPackage);
  updateProject();
  const root = roots.find(root => root.id === id); openProjectRoot(root); return root;
};
element('undo').onclick = () => run(() => { if (!window.alefBackend?.saving()) window.alefExample.undo(); });
element('redo').onclick = () => run(() => { if (!window.alefBackend?.saving()) window.alefExample.redo(); });
element('toggle-inspector').onclick = () => run(() => {
  window.alefExample.showInspector(!inspectorVisible); updateInspector();
});
function updateInspector() {
  const state = window.alefExample.inspectorState();
  if (!state) return;
  element('inspector-selection').textContent = state.id ? `${state.name} · ${state.concept}` : 'Select a node to inspect';
  inspectorVisible = state.visible;
  surface.classList.toggle('inspector-hidden', !state.visible);
  const toggle = element('toggle-inspector');
  toggle.textContent = state.visible ? 'Hide inspector' : 'Show inspector';
  toggle.title = state.visible ? 'Collapse inspector' : 'Expand inspector';
  toggle.setAttribute('aria-expanded', String(state.visible));
  scheduleLayout();
}
function scheduleLayout() {
  if (!frame) frame = requestAnimationFrame(() => { frame = 0; run(layout); });
}
function layout() {
  if (!initialized) return;
  const width = Math.floor(surface.clientWidth), height = Math.floor(surface.clientHeight);
  // Other workspace screens keep editor state mounted, without laying out a
  // hidden generated canvas at zero width.
  if (!width || !height) return;
  const inspectorHeight = inspectorVisible ? Math.max(60, Math.min(inspectorSize, Math.floor(height * .5))) : 0;
  const editorHeight = Math.max(60, height - inspectorHeight - 32);
  const key = `${width}:${editorHeight}:${inspectorHeight}`;
  if (layoutKey === key) return;
  layoutKey = key; surface.style.setProperty('--editor-height', `${editorHeight}px`);
  element('inspector-resizer').setAttribute('aria-valuenow', String(inspectorHeight));
  element('inspector-resizer').setAttribute('aria-valuemin', '60');
  element('inspector-resizer').setAttribute('aria-valuemax', String(Math.floor(height * .5)));
  element('project-resizer').setAttribute('aria-valuenow', String(Math.round(projectView.clientWidth)));
  element('project-resizer').setAttribute('aria-valuemin', '150');
  element('project-resizer').setAttribute('aria-valuemax', String(Math.max(150, window.innerWidth - 260)));
  window.alefExample.layout(width, editorHeight, inspectorHeight);
}
new ResizeObserver(scheduleLayout).observe(surface);
function splitter(id, axis, change) {
  const handle = element(id);
  let start, initial;
  handle.onpointerdown = event => {
    if (event.button !== 0) return;
    event.preventDefault(); start = axis === 'x' ? event.clientX : event.clientY;
    initial = axis === 'x' ? projectView.clientWidth : Math.min(inspectorSize, surface.clientHeight * .5);
    handle.setPointerCapture(event.pointerId);
  };
  handle.onpointermove = event => {
    if (!handle.hasPointerCapture(event.pointerId)) return;
    change(initial + (axis === 'x' ? event.clientX - start : start - event.clientY));
  };
  handle.onpointerup = event => { if (handle.hasPointerCapture(event.pointerId)) handle.releasePointerCapture(event.pointerId); };
  handle.onkeydown = event => {
    const delta = axis === 'x' ? {ArrowLeft:-20, ArrowRight:20}[event.key] : {ArrowDown:-20, ArrowUp:20}[event.key];
    if (!delta) return;
    event.preventDefault(); change((axis === 'x' ? projectView.clientWidth : Math.min(inspectorSize, surface.clientHeight * .5)) + delta);
  };
}
splitter('project-resizer', 'x', size => {
  document.documentElement.style.setProperty('--project-width', `${Math.max(150, Math.min(size, window.innerWidth - 260))}px`);
});
splitter('inspector-resizer', 'y', size => { inspectorSize = Math.max(60, Math.min(size, surface.clientHeight * .5)); scheduleLayout(); });
window.addEventListener('resize', () => {
  if (projectView.clientWidth > window.innerWidth - 260) document.documentElement.style.removeProperty('--project-width');
});
setInterval(() => {
  if (!window.alefExample) return;
  // The backend owns sign-in, transfer and failure states. The idle language
  // runtime must not overwrite them with a perpetual loading message.
  if (window.alefBackendMode && document.documentElement.dataset.workspaceState !== 'ready') return;
  const app = window.alefExample, status = app.status(), ready = status === 'READY';
  if (status.startsWith('FAIL:')) { if (!error) showError(status); return; }
  element('undo').disabled = !ready || !!window.alefBackend?.saving() || !app.canUndo();
  element('redo').disabled = !ready || !!window.alefBackend?.saving() || !app.canRedo();
  element('toggle-inspector').disabled = !ready;
  if (!error) {
    element('status').dataset.state = ready ? 'ready' : 'loading';
    element('status-text').textContent = ready ? 'Ready' : status;
  }
  if (!ready) return;
  run(() => {
    updateProject();
    if (!initialized) {
      initialized = true; element('loading')?.remove();
      if (roots[0]) openProjectRoot(roots[0]); else { workspace.classList.add('empty'); element('empty-editor').hidden = false; }
    }
    updateInspector();
  });
}, 200);
