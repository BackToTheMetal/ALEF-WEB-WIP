# MPS project view

`project-view.js` registers a native `<mps-project-view>` custom element using
Vue's `defineCustomElement`. It owns a shadow root and scoped styles, so it can
be embedded in a plain HTML page or an application using a different framework.
It uses the locally served, pinned Vue runtime (MIT license), without a template
compiler or CDN. Install it with `npm ci --ignore-scripts` before starting the
example server. Serve the two component modules and the Vue runtime at the
relative paths used by `project-view.js` (see `tools/serve_example.py`).

```html
<script type="module" src="/project-view.js"></script>
<mps-project-view id="project"></mps-project-view>
```

Pass a snapshot through the element's **property**, not an HTML attribute:

```js
await customElements.whenDefined('mps-project-view');
const view = document.querySelector('mps-project-view');
view.project = {
  name: 'My solution',
  models: [{
    id: 'serialized-model-reference',
    name: 'my.solution.model',
    roots: [{
      id: 'serialized-MPS-node-reference',
      name: 'Example root',
      concept: 'ObjectModel',
      virtualPackage: 'domain.people'
    }]
  }]
};
view.selectedRoot = 'serialized-MPS-node-reference';
view.addEventListener('root-open', event => {
  const [root] = event.detail; // Vue custom-element events use an argument array.
  // Resolve root.id in the MPS repository and open its generated editor.
});
```

`root-open` contains `{id, name, concept, virtualPackage}`. The embedding page
owns selection: set `selectedRoot` after successfully opening the reference.
Replace `project` when the model changes; stable IDs preserve expanded packages
and keyboard focus. Names are rendered as text, including names containing HTML.
Packages are scoped by model identity and split at dots. Roots with no virtual
package appear directly under their model. Filtering includes root names,
concepts, and package paths, expands matching ancestors, and preserves the
unfiltered expansion state. Arrow keys, Home/End, Enter, and Space navigate or
activate tree entries. An empty project or filter produces an explicit empty state.

## Example's MPS adapter

### Creating roots

The **+ New root** button opens an inline form for the model, root type, name
(when supported), and virtual package. Focusing a package or root before opening
the form prefills that destination. Cancel/Escape leaves the model untouched.
Creation errors stay in the form. Successful creation clears the tree filter,
reveals the new root, and selects its generated editor.
Right-click a model, virtual package, or root to open **Project actions → New
root…** with that location as the destination. Root menus also include **Open**.
Shift+F10/the Context Menu key opens the same menu from the keyboard; arrow keys
move through actions and Escape dismisses it and restores tree focus. Clicking
outside, scrolling, or resizing also dismisses the menu.

To enable creation in another host, supply these callback properties:

```js
view.getRootTypes = modelId => app.rootTypes(modelId);
// May return a Promise. Return the created root record with its stable id;
// update view.project and view.selectedRoot, and open the editor in the host.
view.createRoot = ({modelId, concept, name, virtualPackage}) => {
  const id = app.createRoot(modelId, concept, name, virtualPackage);
  view.project = app.projectTree();
  view.selectedRoot = id;
  app.openRoot(id);
  return view.project.models.flatMap(model => model.roots).find(root => root.id === id);
};
```

`rootTypes(modelId)` returns `{id, name, language, named}` records derived from
the loaded ALEF rootable, concrete concepts and MPS `canBeRoot` constraints.
`createRoot(modelId, conceptId, name, virtualPackage)` validates the writable
project model and concept again, then uses MPS `NodeFactoryManager` to initialize
the root and required children. It sets the name through MPS property access
(`simpleName` for test sets), assigns `BaseConcept.virtualPackage`, imports the
language, and adds the root in one undoable command. Creation returns the stable
MPS node reference. Names are required where the concept supports them; package
paths must not contain empty or padded segments. Empty package means model level.
New roots are normal incomplete language models: their generated editors can
show missing-field diagnostics until the user fills them in.

Undo removes the new root and activates another available root tab; redo
restores the same reference and the root can be reopened and edited. As with
other edits in this example, creation is in memory and is reset by reloading.
Automatic MPS quick fixes join the preceding history entry, so they do not
consume a separate Undo or discard the user's redo history.

### Reading and opening roots

`AlefEditorApp.projectTree()` enumerates `projectSolution.getModels()` and
`SModel.getRootNodes()` inside an MPS read action. Names use
`SNode.getPresentation()`, package paths use the actual
`SNodeUtil.property_BaseConcept_virtualPackage`, and IDs use
`SNode.getReference().toString()`. No language names, root labels, or root counts
are hardcoded into the component. The sample model has an ungrouped root and
real `rules.allowance`, `services`, and `services.tests` packages.

`openRoot(reference)` resolves the serialized node reference against the live
repository, rejects missing or non-root nodes, and reuses or creates a
`NodeEditorComponent`. `page.js` passes snapshots to the component on its existing
200ms status tick and replaces the Vue property only when the snapshot changes.
The bridge reads only roots, not their entire subtrees. Standard/library modules
are intentionally outside this example solution's project view. For a larger
workspace, the embedding adapter can supply snapshots on model-change events
and include additional project modules. This example remains an in-memory session.

Ctrl-clicking a resolved reference in an editor opens the root containing its target,
reuses that root's tab, and reveals the referenced node. The inspector follows
the target. Plain clicks select a reference for editing. Holding Ctrl over a
resolved reference shows MPS's link color, underline, and a hand cursor, even
when the editor does not have focus. Releasing Ctrl or leaving the reference
clears the highlight. Ctrl/Cmd+B follows the selected reference from the keyboard.
Unresolved references stay
in the current editor. `BrowserReferenceNavigation` reads MPS reference roles
and explicit navigation targets from generated cells, rather than matching
their displayed text. References into loaded library models can open a tab
without adding those models to the project tree; MPS retains their read-only state.

## IDE workspace

The host page fills the viewport with the project tree, editor tabs, breadcrumbs,
and an inspector. The first root opens automatically. Opening additional roots
keeps their tabs available; closing the last tab shows an empty workspace.
Names and tabs follow model changes and undo. Project and inspector dividers
support pointer capture during dragging and arrow-key resizing.
`AlefEditorApp.layout(width, editorHeight, inspectorHeight)` resizes the native
Swing editor windows to the measured web workspace. The page itself does not
scroll; each editor and the project tree own their scrolling.
`tools/test_ide.mjs` covers layout, native edits, history, root tabs, resize,
keyboard navigation, and closing/reopening across Chromium, Firefox, and WebKit.

## Inspector

The pane below the main editors is an actual MPS `InspectorEditorComponent`,
registered through `InspectorTool.InspectorAccessor` by `BrowserInspector.java`.
MPS editor selection listeners send it the selected node, editor hints, and
read-only state. It renders the language's generated inspector cells (for example,
ALEF metatags), not a copy of the main editor or a JavaScript property list.
Nodes without inspector content use MPS's empty inspector presentation.

The header identifies the inspected node and concept; **Hide inspector** and
**Show inspector** collapse or reveal the pane. Inspector editing uses the same
model command history, including Ctrl/Cmd+Z, Ctrl/Cmd+Shift+Z, and Ctrl/Cmd+Y.
Generated read-only cells remain read-only. A first opening places the caret in
the root's name or a suitable leaf cell without selecting the whole root.
Returning to an open editor preserves its selection and caret; the inspector
follows that selected cell's node. Undoing
creation clears the removed node and activates another available root.
Inspector registration, its editor and window, and its data provider are disposed
with the example runtime.

## Validation

`tools/test_project_view.mjs` checks package isolation across models, nested and
default packages, filtering, keyboard traversal, selecting all four real roots,
native MPS root-name editing, live updates and undo, and narrow-screen layout.
It runs in Chromium, Firefox, and WebKit with `ALEF_BROWSERS`.
`tools/test_create_root.mjs` additionally creates roots for all four example
languages, checks inherited and new package paths, cancellation, invalid package
and concept rejection, undo/redo, and editing a newly created root after redo.
`tools/test_project_inspector.mjs` checks mouse/keyboard context menus, creation
in the clicked package, generated inspector content and selection, undo/redo
shortcuts with inspector focus, hide/show, and runtime disposal in all three engines.
`tools/test_reference_navigation.mjs` exercises reference clicks, descendant
selection, tab reuse, keyboard navigation, and ordinary name editing.

