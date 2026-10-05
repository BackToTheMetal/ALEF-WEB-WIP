import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';
const root = path.resolve(import.meta.dirname, '..');
const engines = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const directory = path.join(root, 'out/project-inspector');
fs.mkdirSync(directory, {recursive: true});
for (const name of (process.env.ALEF_BROWSERS || 'chromium').split(',')) {
  const browser = await engines[name].launch({headless: true});
  const page = await browser.newPage({viewport: {width: 1500, height: 1250}});
  const errors = [];
  page.on('pageerror', error => errors.push(String(error)));
  try {
    await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8812/');
    await page.waitForFunction(() => window.alefExample?.status() === 'READY' || window.alefExample?.status().startsWith('FAIL:'));
    assert.equal(await page.evaluate(() => window.alefExample.status()), 'READY');
    const view = page.locator('mps-project-view');
    const folder = view.getByRole('treeitem', {name: 'allowance', exact: true});
    const menu = view.getByRole('menu', {name: 'Project actions'});
    await folder.click({button: 'right'});
    await menu.waitFor();
    await menu.screenshot({path: path.join(directory, `${name}-menu.png`)});
    await page.keyboard.press('Escape');
    await menu.waitFor({state: 'detached'});
    await page.waitForFunction(() => {
      const shadow = document.querySelector('mps-project-view').shadowRoot;
      return shadow.activeElement?.getAttribute('aria-label') === 'allowance';
    }, null, {timeout: 3000});
    await page.keyboard.press('Shift+F10');
    await menu.waitFor();
    await menu.getByRole('menuitem', {name: 'New root…', exact: true}).click();
    const form = view.getByRole('form', {name: 'Create root node'});
    assert.equal(await form.getByLabel('Virtual package', {exact: true}).inputValue(), 'rules.allowance');
    await form.getByLabel('Name', {exact: true}).fill('Context regelgroep');
    await form.getByRole('button', {name: 'Create', exact: true}).click();
    await form.waitFor({state: 'detached'});
    await page.waitForFunction(() => window.alefExample.inspectorState().name === 'Context regelgroep');
    const created = await page.evaluate(() => window.alefExample.inspectorState());
    assert.equal(created.concept, 'Regelgroep');
    await page.getByRole('button', {name: 'Undo', exact: true}).click();
    await view.getByRole('treeitem', {name: 'Context regelgroep', exact: true}).waitFor({state: 'detached'});
    await view.getByRole('treeitem', {name: 'Huishouden', exact: true}).click();
    await page.evaluate(() => window.alefExample.focus(0));
    await page.waitForFunction(() => window.alefExample.inspectorState().concept === 'ObjectType');
    const personInspector = await page.evaluate(() => ({state: window.alefExample.inspectorState(), text: window.alefExample.inspectorText()}));
    assert.ok(personInspector.state.name.startsWith('Persoon'));
    assert.match(personInspector.text, /metatags/, 'The generated ALEF inspector is used');
    await page.waitForFunction(() => document.getElementById('inspector-selection').textContent.includes('Persoon'));
    await page.screenshot({path: path.join(directory, `${name}-inspector.png`), fullPage: true});
    // History shortcuts from the inspector act on the same MPS model history.
    await page.keyboard.type('PersoonX');
    await page.waitForFunction(() => window.alefExample.value(0) === 'PersoonX');
    await page.locator('.inspector-heading').scrollIntoViewIfNeeded();
    const bounds = await page.locator('#alef-editor').boundingBox();
    const editorHeight = await page.locator('#alef-editor').evaluate(el => parseInt(el.style.getPropertyValue('--editor-height')));
    await page.mouse.click(bounds.x + 115, bounds.y + editorHeight + 73);
    await page.keyboard.press('Control+Z');
    await page.waitForFunction(() => window.alefExample.value(0) === 'Persoon');
    await page.keyboard.press('Control+Shift+Z');
    await page.waitForFunction(() => window.alefExample.value(0) === 'PersoonX');
    await page.keyboard.press('Control+Z');
    await page.waitForFunction(() => window.alefExample.value(0) === 'Persoon');
    await page.getByRole('button', {name: 'Hide inspector', exact: true}).click();
    assert.equal(await page.evaluate(() => window.alefExample.inspectorState().visible), false);
    await page.getByRole('button', {name: 'Show inspector', exact: true}).click();
    assert.equal(await page.evaluate(() => window.alefExample.inspectorState().visible), true);
    await view.getByRole('treeitem', {name: 'Bereken toeslag', exact: true}).click();
    await page.evaluate(() => window.alefExample.focus(1));
    await page.waitForFunction(() => window.alefExample.inspectorState().concept === 'NumeriekeLiteral');
    await view.getByRole('treeitem', {name: 'Huishouden', exact: true}).click({button: 'right'});
    await menu.getByRole('menuitem', {name: 'Open', exact: true}).click();
    await page.waitForFunction(() => window.alefExample.inspectorState().concept === 'ObjectType');
    await view.getByRole('treeitem', {name: 'Huishouden', exact: true}).click({button: 'right'});
    await page.locator('.brand').click();
    await menu.waitFor({state: 'detached'});
    assert.equal(await page.locator('#error').isVisible(), false, await page.locator('#error').textContent());
    await page.evaluate(() => window.alefExample.close());
    await page.waitForFunction(() => window.alefExample.status() === 'CLOSED');
    assert.deepEqual(errors, []);
    fs.writeFileSync(path.join(directory, `${name}.json`), JSON.stringify({personInspector, created, errors}, null, 2));
    console.log(JSON.stringify({browser: name, personInspector, checks: 'context menu mouse and keyboard, create in package, open, dismiss, inspector follows selection, shared undo/redo, hide/show, disposal', errors}));
  } catch (failure) {
    await page.screenshot({path: path.join(directory, `${name}-failure.png`), fullPage: true});
    console.error(await page.locator('#error').textContent(), errors, await page.evaluate(() => ({state: window.alefExample.inspectorState(), text: window.alefExample.inspectorText(), active: document.activeElement?.outerHTML.slice(0, 200)})));
    throw failure;
  } finally { await browser.close(); }
}

