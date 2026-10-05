import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';
import {projectTree} from '../example/project-tree.js';
const root = path.resolve(import.meta.dirname, '..');
const engines = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const directory = path.join(root, 'out/project-view');
fs.mkdirSync(directory, {recursive: true});

// Same package names in different models remain distinct; an ungrouped root stays at model level.
const sample = {models: ['one', 'two'].map(id => ({id, name: id, roots: [
  {id: id + '-a', name: 'A <node>', concept: 'Thing', virtualPackage: 'a.b'},
  {id: id + '-z', name: 'Z', concept: 'Thing', virtualPackage: ''}
]}))};
const tree = projectTree(sample);
assert.notEqual(tree[0].children[0].id, tree[1].children[0].id);
assert.equal(tree[0].children[0].children[0].children[0].name, 'A <node>');
assert.equal(tree[0].children[1].id, 'one-z');
assert.equal(projectTree(sample, 'nothing').length, 0);
assert.equal(projectTree(sample, 'a.b')[0].children.length, 1);
assert.deepEqual(projectTree({models: [{id: 'empty', name: 'Empty', roots: []}]}), [], 'Empty model folders are hidden');
sample.models[0].roots[0].virtualPackage = 'moved';
assert.equal(projectTree(sample)[0].children[0].name, 'moved', 'Moved roots do not leave empty packages behind');
assert.equal(projectTree(sample)[1].children[0].name, 'a', 'Other models retain their packages');

for (const name of (process.env.ALEF_BROWSERS || 'chromium').split(',')) {
  const browser = await engines[name].launch({headless: true});
  const page = await browser.newPage({viewport: {width: 1500, height: 1150}, deviceScaleFactor: 1});
  const errors = [];
  page.on('pageerror', e => errors.push(String(e)));
  try {
    await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8812/');
    await page.waitForFunction(() => window.alefExample?.status() === 'READY');
    const view = page.locator('mps-project-view');
    await view.locator('[data-kind=root]').first().waitFor();
    const project = await page.evaluate(() => window.alefExample.projectTree());
    const roots = project.models.flatMap(model => model.roots);
    fs.writeFileSync(path.join(directory, `${name}-model.json`), JSON.stringify(project, null, 2));
    assert.equal(roots.length, 4);
    assert.equal(await view.locator('[data-kind=root]').count(), roots.length);
    for (const node of roots) assert.equal(await view.locator('[data-kind=root]').filter({hasText: node.name}).count(), 1);
    const model = view.locator('[data-kind=model]').first();
    await model.focus();
    await page.keyboard.press('ArrowLeft');
    assert.equal(await view.locator('[data-kind=root]').count(), 0);
    await page.keyboard.press('ArrowRight');
    assert.equal(await view.locator('[data-kind=root]').count(), 4);
    await page.keyboard.press('ArrowRight');
    assert.equal(await view.evaluate(el => el.shadowRoot.activeElement.getAttribute('aria-level')), '2');
    await page.screenshot({path: path.join(directory, `${name}-project.png`), fullPage: true});
    const search = view.getByRole('searchbox', {name: 'Find a root'});
    await search.fill('rules.allowance');
    assert.equal(await view.locator('[data-kind=root]').count(), 1);
    await view.getByRole('treeitem', {name: 'allowance', exact: true}).focus();
    await page.keyboard.press('ArrowLeft');
    assert.equal(await view.locator('[data-kind=root]').count(), 0);
    await page.keyboard.press('ArrowRight');
    assert.equal(await view.locator('[data-kind=root]').count(), 1);
    await search.fill('no such root');
    await view.getByText('No matching roots', {exact: true}).waitFor();
    await view.getByRole('button', {name: 'Clear root filter'}).click();
    await page.waitForFunction(() => document.querySelector('mps-project-view').shadowRoot.querySelectorAll('[data-kind=root]').length === 4);
    for (const node of roots) {
      const row = view.getByRole('treeitem', {name: node.name, exact: true});
      await row.focus();
      await page.keyboard.press('Enter');
      await page.waitForFunction(id => document.querySelector('mps-project-view').selectedRoot === id, node.id);
      assert.equal(await page.title(), `${node.name} — ALEF`);
      assert.equal(await page.getByRole('region', {name: 'Node editor', exact: true}).count(), 1);
      assert.equal(await row.getAttribute('aria-selected'), 'true');
      await page.waitForTimeout(100);
    }
    await page.screenshot({path: path.join(directory, `${name}-root.png`), fullPage: true});
    const dataRoot = roots.find(node => node.concept === 'ObjectModel');
    await view.getByRole('treeitem', {name: dataRoot.name, exact: true}).click();
    await page.waitForTimeout(150);
    await page.screenshot({path: path.join(directory, `${name}-data-root.png`), fullPage: true});
    // Edit the actual generated root-name cell, not a frontend fixture or copied label.
    const editor = await page.locator('#alef-editor').boundingBox();
    await page.mouse.click(editor.x + 230, editor.y + 55);
    await page.keyboard.press('End');
    await page.keyboard.type('X');
    await page.waitForFunction(id => window.alefExample.projectTree().models[0].roots.find(root => root.id === id).name === 'HuishoudenX', dataRoot.id);
    await view.getByRole('treeitem', {name: 'HuishoudenX', exact: true}).waitFor();
    assert.equal(await page.title(), 'HuishoudenX — ALEF');
    await page.getByRole('button', {name: 'Undo', exact: true}).click();
    await view.getByRole('treeitem', {name: 'Huishouden', exact: true}).waitFor();
    assert.equal(await view.getByRole('treeitem', {name: 'Huishouden', exact: true}).getAttribute('aria-selected'), 'true');
    assert.equal(await page.getByRole('tab').count(), 0);
    assert.equal(await page.locator('#empty-editor').isVisible(), false);
    assert.equal(await view.locator('[aria-selected=true]').count(), 1);
    assert.equal(await page.locator('#error').isVisible(), false, await page.locator('#error').textContent());
    assert.deepEqual(errors, []);
    await page.setViewportSize({width: 800, height: 1000});
    assert.ok(await view.isVisible(), 'Project view remains available on narrow screens');
    assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth), true);
    console.log(JSON.stringify({browser: name, roots: roots.map(node => ({name: node.name, package: node.virtualPackage})), checks: 'real roots, scoped packages, keyboard, filtering, single editor root switching, live rename/undo, no tabs, narrow layout', errors}));
  } finally { await browser.close(); }
}

