/** Pure projection of an MPS snapshot. Packages are scoped to their owning model. */
export function projectTree(project, query = '') {
  const filter = query.trim().toLocaleLowerCase();
  return (project?.models || []).map(model => {
    const branch = {id: JSON.stringify(['model', model.id]), modelId: model.id, kind: 'model', name: model.name, children: []};
    const packages = new Map();
    for (const root of model.roots) {
      if (filter && !`${root.name} ${root.concept} ${root.virtualPackage}`.toLocaleLowerCase().includes(filter)) continue;
      let parent = branch;
      const path = [];
      for (const segment of (root.virtualPackage || '').split('.').filter(Boolean)) {
        path.push(segment);
        const id = JSON.stringify(['package', model.id, ...path]);
        if (!packages.has(id)) {
          const folder = {id, modelId: model.id, kind: 'package', name: segment, path: path.join('.'), children: []};
          packages.set(id, folder); parent.children.push(folder);
        }
        parent = packages.get(id);
      }
      parent.children.push({...root, modelId: model.id, name: root.name || `<${root.concept}>`, kind: 'root'});
    }
    const sort = node => {
      node.children?.sort((a, b) => Number(a.kind === 'root') - Number(b.kind === 'root')
        || a.name.localeCompare(b.name) || a.id.localeCompare(b.id));
      node.children?.forEach(sort);
    };
    sort(branch);
    return branch;
  }).filter(model => model.children.length > 0);
}
