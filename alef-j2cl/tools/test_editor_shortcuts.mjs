// Native browser keyboard regressions against disposable generated ALEF examples.
import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import {spawn} from 'node:child_process';
import {createRequire} from 'node:module';
import {createHash} from 'node:crypto';

const root = path.resolve(import.meta.dirname, '..');
const {chromium} = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const optimized = process.argv.includes('--optimized');
const target = 'editor_example_browser' + (optimized ? '' : '_dev');
const directory = path.join(root, 'out/editor-shortcuts');
fs.mkdirSync(directory, {recursive: true});
const server = spawn('python3', [path.join(root, 'tools/serve_example.py'), '--port', '0', ...(optimized ? ['--optimized'] : [])]);
const url = await new Promise((resolve, reject) => {
  let output = '';
  const timeout = setTimeout(() => { server.kill(); reject(new Error('Example server startup timed out')); }, 15000);
  server.stdout.on('data', data => {
    output += data;
    const match = output.match(/http:\/\/localhost:(\d+)\//);
    if (match) { clearTimeout(timeout); resolve(`http://127.0.0.1:${match[1]}/`); }
  });
  server.once('error', error => { clearTimeout(timeout); reject(error); });
  server.once('exit', code => { clearTimeout(timeout); reject(new Error(`Example server exited: ${code}`)); });
});
let browser, page;
const checks = [], errors = [];
const report = {target, checks, errors};
try {
  const bundle = fs.readFileSync(path.join(root, `bazel-bin/${target}.js`));
  assert.ok(bundle.equals(Buffer.from(await (await fetch(new URL('app.js', url))).arrayBuffer())));
  report.sha256 = createHash('sha256').update(bundle).digest('hex');
  browser = await chromium.launch({headless: true});
  page = await browser.newPage({viewport: {width: 1500, height: 1100}});
  page.on('pageerror', error => errors.push(String(error)));
  page.on('console', message => { if (message.type() === 'error') errors.push(message.text()); });
  await page.goto(url);
  await page.waitForFunction(() => globalThis.alefExample?.status() === 'READY', null, {timeout: 90000});
  const focus = async index => {
    await page.locator('mps-project-view').getByRole('treeitem', {name: index === 0 ? 'Huishouden' : 'Bereken toeslag', exact: true}).click();
    await page.evaluate(i => globalThis.alefExample.focus(i), index);
    await page.waitForFunction(() => document.activeElement?.getAttribute('aria-label') === 'Editor text input');
  };
  const outline = index => page.evaluate(i => globalThis.alefExample.outline(i), index);
  const settle = () => page.evaluate(() => new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve))));
  const restored = async (index, original) => {
    await page.waitForFunction(([i, text]) => globalThis.alefExample.outline(i) === text, [index, original]);
    await settle();
  };
  await focus(0);
  const original = await outline(0);
  report.original = original;
  await page.keyboard.press('Control+d');
  await page.waitForFunction(() => (globalThis.alefExample.outline(0).match(/ObjectType name=Persoon/g) || []).length === 2);
  const duplicate = await outline(0);
  report.duplicate = duplicate;
  // Delete the explicitly selected copy. Browser history restores model edits,
  // and does not promise to reselect recreated nodes after an undo/redo cycle.
  await settle();
  report.step = 'delete selected duplicate';
  await page.keyboard.press('Control+y');
  await restored(0, original);
  report.step = 'undo duplicate deletion';
  await page.keyboard.press('Control+z');
  await restored(0, duplicate);
  report.step = 'redo duplicate deletion';
  await page.keyboard.press('Control+Shift+z');
  await restored(0, original);
  checks.push('Ctrl+Y deletes the duplicate; undo and redo preserve the model');
  await page.keyboard.press('Control+z');
  await restored(0, duplicate);
  await page.keyboard.press('Control+z');
  await restored(0, original);
  await page.keyboard.press('Control+Shift+z');
  await restored(0, duplicate);
  checks.push('native duplicate, undo and redo preserve the complete model');
  await page.keyboard.press('Control+z');
  await restored(0, original);
  report.step = 'move selected node';

  await focus(0);
  await page.keyboard.press('Control+Shift+ArrowDown');
  await page.waitForFunction(before => globalThis.alefExample.outline(0) !== before, original);
  await page.keyboard.press('Control+Shift+ArrowUp');
  await restored(0, original);
  checks.push('native move down/up restores order, descendants and references');

  report.step = 'surround selected expression';
  await focus(1);
  const expression = await outline(1);
  await page.keyboard.press('Control+Alt+t');
  const surround = page.getByRole('menuitem', {name: /Zet Expressie Tussen Haakjes/i});
  await surround.waitFor({timeout: 10000});
  await surround.click();
  await page.waitForFunction(before => globalThis.alefExample.outline(1) !== before, expression);
  await page.keyboard.press('Control+z');
  await restored(1, expression);
  checks.push('original Surround With menu executes a generated intention and undo');

  await focus(1);
  await page.keyboard.press('Control+Space');
  await page.locator('.ws-popup-menu [role=listbox]').waitFor();
  await page.keyboard.press('Escape');
  await page.locator('.ws-popup-menu').waitFor({state: 'detached'});
  assert.equal(await outline(1), expression);
  checks.push('completion retains priority and Escape dismisses it without editing');

  const search = page.locator('mps-project-view').getByRole('searchbox', {name: 'Find a root'});
  await search.focus();
  await page.keyboard.press('Control+d');
  assert.equal(await outline(0), original);
  assert.equal(await outline(1), expression);
  checks.push('shortcuts do not edit an unfocused editor');
  assert.deepEqual(errors, []);
  report.passed = true;
  delete report.original;
  delete report.duplicate;
  delete report.step;
} catch (error) {
  report.passed = false;
  report.failure = String(error);
  if (page) {
    report.diagnostic = await page.evaluate(() => ({outline: globalThis.alefExample?.outline(0),
      inspector: globalThis.alefExample?.inspectorText(), active: document.activeElement?.outerHTML})).catch(() => null);
    await page.screenshot({path: path.join(directory, `native-${optimized ? 'optimized' : 'development'}-failure.png`)}).catch(() => {});
  }
  process.exitCode = 1;
} finally {
  await browser?.close();
  server.kill();
  fs.writeFileSync(path.join(directory, `native-${optimized ? 'optimized' : 'development'}.json`), JSON.stringify(report, null, 2) + '\n');
  console.log(JSON.stringify(report));
}
