import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';
const root = path.resolve(import.meta.dirname, '..');
const engines = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const directory = path.join(root, 'out/create-root');
fs.mkdirSync(directory, {recursive: true});
for (const name of (process.env.ALEF_BROWSERS || 'chromium').split(',')) {
  const browser = await engines[name].launch({headless: true});
  const page = await browser.newPage({viewport: {width: 1500, height: 1150}});
  const errors = [];
  page.on('pageerror', error => errors.push(String(error)));
  try {
    await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8812/');
    await page.waitForFunction(() => window.alefExample?.status() === 'READY');
    const view = page.locator('mps-project-view');
    const form = view.getByRole('form', {name: 'Create root node'});
    const initial = await page.evaluate(() => window.alefExample.projectTree());
    const model = initial.models[0];
    const types = await page.evaluate(id => window.alefExample.rootTypes(id), model.id);
    fs.writeFileSync(path.join(directory, `${name}-types.json`), JSON.stringify(types, null, 2));
    assert.ok(types.length >= 4);
    assert.ok(!types.some(type => type.name === 'NumeriekeLiteral'), 'Non-root concepts are excluded');
    await view.getByRole('treeitem', {name: 'allowance', exact: true}).focus();
    await view.getByRole('button', {name: '+ New root', exact: true}).click();
    assert.equal(await form.getByLabel('Virtual package', {exact: true}).inputValue(), 'rules.allowance');
    await form.getByRole('button', {name: 'Cancel', exact: true}).click();
    assert.equal(await page.evaluate(() => window.alefExample.projectTree().models[0].roots.length), 4);
    const created = [];
    for (const concept of ['ObjectModel', 'Regelgroep', 'Service', 'ServiceTestSet']) {
      const type = types.find(type => type.name === concept);
      assert.ok(type, `MPS offers ${concept}`);
      await view.getByRole('button', {name: '+ New root', exact: true}).click();
      await form.getByLabel('Root type', {exact: true}).selectOption(type.id);
      await form.getByLabel('Name', {exact: true}).fill(`Nieuw ${concept}`);
      await form.getByLabel('Virtual package', {exact: true}).fill(concept === 'ObjectModel' ? '' : 'nieuw.voorbeeld');
      if (concept === 'ObjectModel') {
        await form.getByLabel('Virtual package', {exact: true}).fill('bad..package');
        await form.getByRole('button', {name: 'Create', exact: true}).click();
        await form.getByRole('alert').waitFor();
        assert.match(await form.getByRole('alert').textContent(), /dot-separated package names/);
        assert.equal(await page.evaluate(() => window.alefExample.projectTree().models[0].roots.length), 4);
        await form.getByLabel('Virtual package', {exact: true}).fill('');
      }
      await page.screenshot({path: path.join(directory, `${name}-form.png`), fullPage: true});
      await form.getByRole('button', {name: 'Create', exact: true}).click();
      await form.waitFor({state: 'detached', timeout: 10000});
      const nodes = await page.evaluate(() => window.alefExample.projectTree().models[0].roots);
      const node = nodes.find(node => node.name === `Nieuw ${concept}`);
      assert.ok(node, `${concept} is a real root in the MPS model`);
      assert.equal(node.concept, concept);
      assert.equal(node.virtualPackage, concept === 'ObjectModel' ? '' : 'nieuw.voorbeeld');
      assert.equal(await view.getByRole('treeitem', {name: node.name, exact: true}).getAttribute('aria-selected'), 'true');
      assert.equal(await page.locator('[role=tab][aria-selected=true] .tab-name').textContent(), node.name);
      await page.getByRole('button', {name: 'Undo', exact: true}).click();
      await page.waitForFunction(id => !window.alefExample.projectTree().models[0].roots.some(root => root.id === id), node.id);
      await page.waitForFunction(id => document.querySelector('mps-project-view').selectedRoot !== id, node.id);
      await page.getByRole('button', {name: 'Redo', exact: true}).click();
      await view.getByRole('treeitem', {name: node.name, exact: true}).click();
      await page.waitForTimeout(200);
      assert.ok(await page.evaluate(id => window.alefExample.projectTree().models[0].roots.some(root => root.id === id), node.id), 'Redo restores the same MPS identity');
      if (concept === 'ObjectModel') {
        const bounds = await page.locator('#alef-editor').boundingBox();
        await page.mouse.click(bounds.x + 230, bounds.y + 55);
        await page.keyboard.press('End');
        await page.keyboard.type('X');
        await view.getByRole('treeitem', {name: 'Nieuw ObjectModelX', exact: true}).waitFor();
        await page.getByRole('button', {name: 'Undo', exact: true}).click();
        await view.getByRole('treeitem', {name: node.name, exact: true}).waitFor();
      }
      await page.screenshot({path: path.join(directory, `${name}-${concept}.png`), fullPage: true});
      created.push(node);
    }
    assert.equal(await page.evaluate(() => window.alefExample.projectTree().models[0].roots.length), 8);
    const invalid = await page.evaluate(modelId => {
      try { window.alefExample.createRoot(modelId, 'gegevensspraak.structure.NumeriekeLiteral', 'Invalid', ''); return false; }
      catch { return true; }
    }, model.id);
    assert.equal(invalid, true, 'Backend rejects a non-root concept');
    assert.equal(await page.locator('#error').isVisible(), false, await page.locator('#error').textContent());
    assert.deepEqual(errors, []);
    console.log(JSON.stringify({browser: name, availableTypes: types.map(type => type.name), created, errors}));
  } catch (failure) {
    await page.screenshot({path: path.join(directory, `${name}-failure.png`), fullPage: true});
    console.error(await page.locator('mps-project-view').textContent(), await page.locator('mps-project-view [role=alert]').allTextContents(), errors);
    throw failure;
  } finally { await browser.close(); }
}

