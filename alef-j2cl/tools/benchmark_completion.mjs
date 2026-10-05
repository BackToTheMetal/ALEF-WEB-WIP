import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';

const root = path.resolve(import.meta.dirname, '..');
const require = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const {chromium} = require('playwright');
const browser = await chromium.launch({headless: true});
const page = await browser.newPage({viewport: {width: 1500, height: 1150}});
const errors = [];
page.on('pageerror', error => errors.push(String(error)));
try {
  await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8810/', {timeout: 60000});
  await page.waitForFunction(() => window.alefExample?.status() === 'READY', null, {timeout: 60000});
  await page.waitForFunction(() => document.getElementById('object-type').textContent !== 'Type pending');
  const originalText = await page.evaluate(() => window.alefExample.editorText(1));
  await page.getByRole('button', {name: 'Edit first number', exact: true}).click();
  await page.waitForFunction(() => document.activeElement?.getAttribute('aria-label') === 'Editor text input');
  // Select the generated attribute reference in the fixed benchmark viewport.
  const editorBounds = await page.locator('#alef-editor').boundingBox();
  await page.mouse.click(editorBounds.x + editorBounds.width / 2 + 150, editorBounds.y + 165);
  await page.waitForTimeout(250);
  await page.keyboard.press('Control+Space');
  await page.locator('.ws-popup-menu [role=listbox]').waitFor({state: 'visible'});
  for (let i = 0; i < 7; i++) await page.keyboard.press('ArrowRight');
  for (let i = 0; i < 7; i++) await page.keyboard.press('Backspace');
  await page.waitForFunction(() => document.querySelectorAll('.ws-popup-menu [role=option]').length > 1);
  await page.waitForTimeout(700);
  const samples = [];
  for (let i = 0; i < 32; i++) {
    const sample = await page.evaluate(key => {
      const list = document.querySelector('.ws-popup-menu [role=listbox]');
      const rows = [...list.querySelectorAll('[role=option]')];
      const canvases = rows.map(row => row.querySelector('canvas'));
      const before = rows.findIndex(row => row.getAttribute('aria-selected') === 'true');
      const target = document.activeElement;
      const start = performance.now();
      target.dispatchEvent(new KeyboardEvent('keydown', {key, code: key, keyCode: key === 'ArrowDown' ? 40 : 38, bubbles: true, cancelable: true}));
      const down = performance.now();
      target.dispatchEvent(new KeyboardEvent('keyup', {key, code: key, keyCode: key === 'ArrowDown' ? 40 : 38, bubbles: true, cancelable: true}));
      const end = performance.now();
      const afterRows = [...list.querySelectorAll('[role=option]')];
      return {keydownMs: down - start, keyupMs: end - down, rowCount: rows.length, before,
        after: afterRows.findIndex(row => row.getAttribute('aria-selected') === 'true'),
        retainedRows: rows.filter((row, index) => row === afterRows[index]).length,
        changedCanvases: canvases.filter((canvas, index) => canvas !== afterRows[index]?.querySelector('canvas')).length,
        activeDescendant: list.getAttribute('aria-activedescendant')};
    }, i % 2 === 0 ? 'ArrowDown' : 'ArrowUp');
    assert.equal(sample.after, sample.before + (i % 2 === 0 ? 1 : -1), 'Arrow must move selection one row: ' + JSON.stringify(sample));
    if (process.env.ALEF_ASSERT_INCREMENTAL === '1') {
      assert.equal(sample.retainedRows, sample.rowCount, 'Selection must retain row DOM');
      assert.equal(sample.changedCanvases, 2, 'Only old and new selections need painting');
    }
    samples.push(sample);
    await page.waitForTimeout(25);
  }
  assert.deepEqual(errors, []);
  const selectedIndex = () => page.evaluate(() => [...document.querySelectorAll('.ws-popup-menu [role=option]')].findIndex(row => row.getAttribute('aria-selected') === 'true'));
  await page.keyboard.press('End');
  assert.equal(await selectedIndex(), samples[0].rowCount - 1);
  await page.keyboard.press('ArrowDown');
  assert.equal(await selectedIndex(), 0, 'ArrowDown wraps at the last item');
  await page.keyboard.press('ArrowUp');
  assert.equal(await selectedIndex(), samples[0].rowCount - 1, 'ArrowUp wraps at the first item');
  await page.keyboard.press('Home');
  assert.equal(await selectedIndex(), 0);
  const percentile = (field, fraction) => samples.slice(4).map(s => s[field]).sort((a, b) => a - b)[Math.floor(27 * fraction)];
  const report = {label: process.argv[2] || 'current', keydownMedianMs: percentile('keydownMs', .5), keydownP95Ms: percentile('keydownMs', .95),
    keyupMedianMs: percentile('keyupMs', .5), samples, errors};
  const directory = path.join(root, 'out/completion-performance');
  fs.mkdirSync(directory, {recursive: true});
  await page.screenshot({path: path.join(directory, report.label + '.png'), fullPage: true});
  await page.keyboard.press('Escape');
  await page.locator('.ws-popup-menu').waitFor({state: 'detached'});
  assert.equal(await page.evaluate(() => window.alefExample.editorText(1)), originalText, 'Escape preserves the original reference');
  // Reopen and accept a different reference, then verify normal editor undo.
  await page.keyboard.press('Control+Space');
  await page.locator('.ws-popup-menu [role=listbox]').waitFor({state: 'visible'});
  for (let i = 0; i < 7; i++) await page.keyboard.press('ArrowRight');
  for (let i = 0; i < 7; i++) await page.keyboard.press('Backspace');
  await page.waitForFunction(() => document.querySelectorAll('.ws-popup-menu [role=option]').length > 1);
  await page.keyboard.press('Home');
  const chosenLabel = await page.locator('.ws-popup-menu [role=option][aria-selected=true]').getAttribute('aria-label');
  await page.keyboard.press('Enter');
  await page.locator('.ws-popup-menu').waitFor({state: 'detached'});
  await page.waitForFunction(before => window.alefExample.editorText(1) !== before, originalText);
  assert.ok((await page.evaluate(() => window.alefExample.editorText(1))).includes(chosenLabel.split(' ')[0]), 'Enter applies the highlighted reference');
  await page.getByRole('button', {name: 'Undo', exact: true}).click();
  await page.waitForFunction(before => window.alefExample.editorText(1) === before, originalText);
  assert.deepEqual(errors, []);
  fs.writeFileSync(path.join(directory, report.label + '.json'), JSON.stringify(report, null, 2) + '\n');
  console.log(JSON.stringify({...report, samples: {count: samples.length, first: samples[0]}}));
} finally { await browser.close(); }
