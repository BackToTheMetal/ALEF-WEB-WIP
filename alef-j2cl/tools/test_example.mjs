import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import crypto from 'node:crypto';
import assert from 'node:assert/strict';

const root = path.resolve(import.meta.dirname, '..');
const require = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const {chromium} = require('playwright');
const url = process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8810/';
const variant = process.env.ALEF_EXAMPLE_VARIANT || 'development';
assert.ok(['development', 'optimized'].includes(variant), 'Unknown ALEF_EXAMPLE_VARIANT');
const target = variant === 'optimized' ? 'editor_example_browser' : 'editor_example_browser_dev';
const directory = path.join(root, variant === 'optimized' ? 'out/example-optimized' : 'out/example');
fs.mkdirSync(directory, {recursive: true});
const browser = await chromium.launch({headless: true});
const page = await browser.newPage({viewport: {width: 1500, height: 1150}});
const errors = [];
const warnings = [];
const browserAdvisories = [];
const checks = [];
page.on('pageerror', error => errors.push(String(error)));
page.on('console', message => {
  if (message.type() === 'error') errors.push(message.text());
  if (message.type() === 'warning') {
    const text = message.text();
    // Chromium's canvas performance advice is not a generated-editor diagnostic.
    if (text.startsWith('Canvas2D: Multiple readback operations using getImageData are faster with the willReadFrequently attribute set to true.')) browserAdvisories.push(text);
    else warnings.push(text);
  }
});
const ready = async () => {
  await page.waitForFunction(() => window.alefExample?.status() === 'READY' || window.alefExample?.status().startsWith('FAIL:'), null, {timeout: 60000});
  assert.equal(await page.evaluate(() => window.alefExample.status()), 'READY');
  await page.getByRole('tab', {name: 'Huishouden', exact: true}).waitFor();
  assert.equal(await page.locator('#error').isVisible(), false, await page.locator('#error').textContent());
};
const expectValue = (index, value) => page.waitForFunction(([i, v]) => window.alefExample.value(i) === v, [index, value], {timeout: 5000});
const focusEditor = async index => {
  await page.locator('mps-project-view').getByRole('treeitem', {name: index === 0 ? 'Huishouden' : 'Bereken toeslag', exact: true}).click();
  await page.evaluate(i => window.alefExample.focus(i), index);
  await page.waitForFunction(() => document.activeElement?.getAttribute('aria-label') === 'Editor text input');
};
const openCompletion = async () => {
  await focusEditor(1);
  await page.waitForFunction(() => document.activeElement?.getAttribute('aria-label') === 'Editor text input');
  await page.keyboard.press('Control+Space');
  await page.locator('.ws-popup-menu [role=listbox]').waitFor({state: 'visible'});
  await page.waitForFunction(() => {
    const menu = document.querySelector('.ws-popup-menu');
    const input = document.activeElement;
    return menu && Math.abs(menu.getBoundingClientRect().top - input.getBoundingClientRect().bottom) < 3;
  });
};
const completionGeometry = () => page.evaluate(() => {
  const menu = document.querySelector('.ws-popup-menu');
  const row = menu.querySelector('[role=option]');
  const canvas = row.querySelector('canvas');
  return {menu: menu.getBoundingClientRect().toJSON(), input: document.activeElement.getBoundingClientRect().toJSON(),
    shadow: getComputedStyle(menu).boxShadow, rowHeight: row.getBoundingClientRect().height,
    background: getComputedStyle(row).backgroundColor, canvasHeight: canvas?.height,
    viewportWidth: document.documentElement.clientWidth};
});
try {
  const bundle = fs.readFileSync(path.join(root, `bazel-bin/${target}.js`));
  const actual = Buffer.from(await (await fetch(new URL('app.js', url))).arrayBuffer());
  assert.ok(bundle.equals(actual), 'The server must serve the current build');
  checks.push('current J2CL build served');
  await page.goto(url, {waitUntil: 'load', timeout: 60000});
  await ready();
  assert.equal(await page.evaluate(() => window.alefExample.type(0)), 'DatumTijdType');
  const expressionType = await page.evaluate(() => window.alefExample.type(1));
  assert.equal(expressionType, 'NumeriekType');
  await expectValue(0, 'Persoon');
  await expectValue(1, '100');
  checks.push('both generated editor trees and MPS inferred types');
  const objectOutline = await page.evaluate(() => window.alefExample.outline(0));
  const ruleOutline = await page.evaluate(() => window.alefExample.outline(1));
  assert.match(objectOutline, /^ObjectModel name=Huishouden/m);
  for (const name of ['Persoon', 'Woning', 'Bewoning', 'geboortedatum', 'toeslag', 'leeftijdsgrens']) {
    assert.ok(objectOutline.includes(`name=${name}`), `Missing object-model child ${name}`);
  }
  assert.equal((objectOutline.match(/Attribuut /g) || []).length, 6);
  assert.equal((objectOutline.match(/objectType->ObjectType/g) || []).length, 2);
  assert.match(ruleOutline, /^Regelgroep name=Bereken toeslag/m);
  for (const concept of ['Regel name=Basistoeslag', 'RegelVersie', 'Geldigheidsperiode', 'ActieIndienVoorwaarde', 'Gelijkstelling', 'EnkeleVoorwaarde', 'Vergelijking', 'PlusExpressie']) {
    assert.ok(ruleOutline.includes(concept), `Missing rule child ${concept}`);
  }
  assert.equal((ruleOutline.match(/base->Attribuut/g) || []).length, 2);
  const texts = await page.evaluate(() => [window.alefExample.editorText(0), window.alefExample.editorText(1)]);
  for (const name of ['Huishouden', 'Persoon', 'Woning', 'geboortedatum', 'leeftijdsgrens']) assert.ok(texts[0].includes(name), `Object editor does not display ${name}`);
  for (const name of ['Bereken toeslag', 'Basistoeslag', 'toeslag', 'leeftijd', '100', '25', '18']) assert.ok(texts[1].includes(name), `Rule editor does not display ${name}`);
  checks.push('ObjectModel and Regelgroep descendants, resolved references, and generated child cells');
  await page.screenshot({path: path.join(directory, 'editors.png'), fullPage: true});

  await openCompletion();
  const popupBeforeScroll = await completionGeometry();
  assert.ok(popupBeforeScroll.menu.right <= popupBeforeScroll.viewportWidth + 1, 'Completion must fit the viewport');
  assert.ok(popupBeforeScroll.menu.left <= popupBeforeScroll.input.left, 'Completion must stay beside the selected cell');
  assert.match(popupBeforeScroll.shadow, /inset/);
  assert.ok(!popupBeforeScroll.shadow.includes('2px 2px'), 'Component popup must not have the Basic menu bevel');
  assert.ok(popupBeforeScroll.rowHeight >= 24, 'Completion text needs vertical padding');
  assert.equal(popupBeforeScroll.canvasHeight, popupBeforeScroll.rowHeight);
  assert.equal(popupBeforeScroll.background, 'rgb(82, 109, 165)', 'Preserve the MPS selection palette');
  await page.evaluate(() => window.scrollBy(0, 100));
  await page.waitForFunction(before => {
    const menu = document.querySelector('.ws-popup-menu').getBoundingClientRect();
    const input = document.activeElement.getBoundingClientRect();
    return Math.abs(menu.top - input.bottom) < 3 && window.scrollY === 0;
  }, popupBeforeScroll.menu.top);
  await page.screenshot({path: path.join(directory, 'completion-scrolled.png')});
  await page.keyboard.press('Escape');
  await page.locator('.ws-popup-menu').waitFor({state: 'detached'});
  await page.evaluate(() => window.scrollTo(0, 0));
  await expectValue(1, '100');
  checks.push('completion placement, viewport bounds, fixed workspace, renderer styling, Escape, and focus');

  await focusEditor(0);
  await page.waitForTimeout(100);
  await page.keyboard.type('Bewoner');
  await expectValue(0, 'Bewoner');
  await page.waitForFunction(() => window.alefExample.editorText(1).includes('Bewoner'));
  await page.keyboard.press('Control+z');
  await expectValue(0, 'Persoon');
  await page.waitForFunction(() => window.alefExample.editorText(1).includes('Persoon'));
  checks.push('native object-name editing updates rule references and supports keyboard undo');

  await focusEditor(1);
  await page.waitForTimeout(100);
  await page.keyboard.type('250');
  await expectValue(1, '250');
  await page.getByRole('button', {name: 'Undo', exact: true}).click();
  await expectValue(1, '100');
  await page.getByRole('button', {name: 'Redo', exact: true}).click();
  await expectValue(1, '250');
  await page.getByRole('button', {name: 'Undo', exact: true}).click();
  await expectValue(1, '100');
  await expectValue(0, 'Persoon');
  checks.push('native expression editing, toolbar undo/redo, independent editors');

  await page.evaluate(() => window.alefExample.sortObjectModel());
  await page.waitForFunction(before => window.alefExample.outline(0) !== before, objectOutline);
  const sorted = await page.evaluate(() => window.alefExample.outline(0));
  assert.ok(sorted.indexOf('name=geboortedatum') < sorted.indexOf('name=leeftijd'));
  assert.ok(sorted.indexOf('name=leeftijd') < sorted.indexOf('name=naam'));
  await page.getByRole('button', {name: 'Undo', exact: true}).click();
  await page.waitForFunction(before => window.alefExample.outline(0) === before, objectOutline);
  checks.push('generated ObjectModel sorting intention and undo preserve children and references');

  assert.equal(await page.evaluate(() => window.alefExample.type(0)), 'DatumTijdType');
  assert.equal(await page.evaluate(() => window.alefExample.type(1)), expressionType);
  checks.push('type inference after edits');

  await page.locator('mps-project-view').getByRole('searchbox', {name: 'Find a root'}).focus();
  await page.keyboard.press('Backspace');
  await expectValue(0, 'Persoon');
  checks.push('keyboard actions stay inside editor focus');
  await page.setViewportSize({width: 900, height: 900});
  await page.waitForTimeout(250);
  await page.screenshot({path: path.join(directory, 'editors-narrow.png'), fullPage: true});
  await page.reload();
  await ready();
  await expectValue(0, 'Persoon');
  await expectValue(1, '100');
  checks.push('resize and reset restore the example');
  await openCompletion();
  const narrowPopup = await completionGeometry();
  assert.ok(narrowPopup.menu.left >= 0 && narrowPopup.menu.right <= narrowPopup.viewportWidth + 1);
  await page.keyboard.press('Escape');
  await page.locator('.ws-popup-menu').waitFor({state: 'detached'});
  checks.push('completion remains anchored and fits after resize');
  // Let MPS's debounced selection highlighting finish before testing normal teardown.
  await page.waitForTimeout(600);
  await page.evaluate(() => window.alefExample.close());
  await page.waitForFunction(() => window.alefExample.status() === 'CLOSED');
  checks.push('editor and language runtime disposal');
  assert.deepEqual(errors, []);
  assert.deepEqual(warnings, [], 'Generated editors must not fall back or use missing concept descriptors');
  const report = {passed: true, variant, url, expressionType, checks, errors, warnings, browserAdvisories, sha256: crypto.createHash('sha256').update(bundle).digest('hex')};
  fs.writeFileSync(path.join(directory, 'smoke.json'), JSON.stringify(report, null, 2) + '\n');
  console.log(JSON.stringify(report));
} catch (failure) {
  await page.screenshot({path: path.join(directory, 'failure.png'), fullPage: true}).catch(() => {});
  fs.writeFileSync(path.join(directory, 'smoke.json'), JSON.stringify({passed: false, variant, error: String(failure), checks, errors, warnings, browserAdvisories}, null, 2) + '\n');
  console.error(await page.locator('body').innerText());
  throw failure;
} finally {
  await browser.close();
}

