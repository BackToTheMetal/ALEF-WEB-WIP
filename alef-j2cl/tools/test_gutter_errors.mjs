import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';

const root = path.resolve(import.meta.dirname, '..');
const {chromium} = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const directory = path.join(root, 'out/gutter-errors');
fs.mkdirSync(directory, {recursive: true});
const browser = await chromium.launch({headless: true});
const page = await browser.newPage({viewport: {width: 1500, height: 1150}});
const errors = [];
page.on('pageerror', error => errors.push(String(error)));
try {
  await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8810/', {timeout: 60000});
  await page.waitForFunction(() => window.alefExample?.status() === 'READY', null, {timeout: 60000});
  await page.waitForFunction(() => document.getElementById('object-type').textContent !== 'Type pending');
  const original = await page.evaluate(() => window.alefExample.editorText(1));
  const gutter = page.locator('[role=scrollbar][aria-orientation=vertical]:visible').last().locator(':scope > canvas');
  const redPixels = () => gutter.evaluate(canvas => {
    const data = canvas.getContext('2d').getImageData(0, 0, canvas.width, canvas.height).data;
    let red = 0;
    for (let i = 0; i < data.length; i += 4) if (data[i] > 120 && data[i + 1] < 80 && data[i + 2] < 80 && data[i + 3] > 0) red++;
    return red;
  });
  const waitForMarkers = async present => {
    // Validation and gutter updates have separate debounce windows.
    const deadline = Date.now() + 10000;
    let pixels, clearSince = 0;
    do {
      pixels = await redPixels();
      if (present && pixels > 0) return pixels;
      if (!present && pixels === 0) {
        if (!clearSince) clearSince = Date.now();
        if (Date.now() - clearSince >= 700) return pixels;
      } else clearSince = 0;
      await page.waitForTimeout(100);
    } while (Date.now() < deadline);
    return pixels;
  };
  await page.waitForTimeout(1200);
  assert.equal(await redPixels(), 0, 'Clean model starts without error marks');
  await page.getByRole('button', {name: 'Edit first number', exact: true}).click();
  await page.waitForFunction(() => document.activeElement?.getAttribute('aria-label') === 'Editor text input');
  const editorBounds = await page.locator('#alef-editor').boundingBox();
  await page.mouse.click(editorBounds.x + editorBounds.width / 2 + 150, editorBounds.y + 165);
  await page.waitForTimeout(250);
  await page.keyboard.press('Control+Space');
  await page.locator('.ws-popup-menu [role=listbox]').waitFor({state: 'visible'});
  for (let i = 0; i < 7; i++) await page.keyboard.press('ArrowRight');
  for (let i = 0; i < 7; i++) await page.keyboard.press('Backspace');
  await page.waitForFunction(() => document.querySelectorAll('.ws-popup-menu [role=option]').length > 1);
  // A text-valued attribute cannot receive the numeric assignment.
  const options = page.locator('.ws-popup-menu [role=option]');
  const labels = await options.evaluateAll(rows => rows.map(row => row.getAttribute('aria-label')));
  const index = labels.findIndex(label => /^naam\b/.test(label));
  assert.ok(index >= 0, 'Text-valued reference is available: ' + labels);
  await page.keyboard.press('Home');
  for (let i = 0; i < index; i++) await page.keyboard.press('ArrowDown');
  await page.keyboard.press('Enter');
  await page.locator('.ws-popup-menu').waitFor({state: 'detached'});
  const errorPixels = await waitForMarkers(true);
  await page.screenshot({path: path.join(directory, 'error.png'), fullPage: true});
  assert.ok(errorPixels > 0, 'Invalid assignment must paint an error marker');
  await page.getByRole('button', {name: 'Undo', exact: true}).click();
  await page.waitForFunction(text => window.alefExample.editorText(1) === text, original);
  const resolvedPixels = await waitForMarkers(false);
  await page.screenshot({path: path.join(directory, 'resolved.png'), fullPage: true});
  const report = {errorPixels, resolvedPixels, errors};
  fs.writeFileSync(path.join(directory, 'report.json'), JSON.stringify(report, null, 2) + '\n');
  assert.equal(resolvedPixels, 0, 'Resolving the error must erase its gutter marker without a resize');
  await page.getByRole('button', {name: 'Redo', exact: true}).click();
  await page.waitForFunction(text => window.alefExample.editorText(1) !== text, original);
  report.redoPixels = await waitForMarkers(true);
  assert.ok(report.redoPixels > 0, 'A returning error must still paint its marker');
  await page.getByRole('button', {name: 'Undo', exact: true}).click();
  await page.waitForFunction(text => window.alefExample.editorText(1) === text, original);
  report.resolvedAgainPixels = await waitForMarkers(false);
  assert.equal(report.resolvedAgainPixels, 0, 'Repeated resolution clears the stripe again');
  assert.deepEqual(errors, []);
  fs.writeFileSync(path.join(directory, 'report.json'), JSON.stringify(report, null, 2) + '\n');
  console.log(JSON.stringify(report));
} catch (failure) {
  await page.screenshot({path: path.join(directory, 'failure.png'), fullPage: true});
  console.error(await page.locator('.ws-popup-menu').allTextContents(), errors);
  throw failure;
} finally { await browser.close(); }
