import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import crypto from 'node:crypto';
import assert from 'node:assert/strict';

const root = path.resolve(import.meta.dirname, '..');
const require = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const {chromium} = require('playwright');
const browser = await chromium.launch({headless: true});
const page = await browser.newPage({viewport: {width: 1200, height: 900}});
const errors = [];
page.on('pageerror', error => errors.push(String(error)));
page.on('console', message => { if (message.type() === 'error') errors.push(message.text()); });
const directory = path.join(root, 'out/financial');
fs.mkdirSync(directory, {recursive: true});
try {
  const bundle = Buffer.from(await (await fetch('http://127.0.0.1:8800/financial.js')).arrayBuffer());
  if (!bundle.equals(fs.readFileSync(path.join(root, 'bazel-bin/financial_browser_dev.js')))) throw new Error('Restart server: served bundle differs from the build');
  await page.goto('http://127.0.0.1:8800/');
  await page.waitForFunction(() => window.financialStatus?.() === 'READY' || window.financialStatus?.().startsWith('FAIL:'), null, {timeout: 60000});
  const status = await page.evaluate(() => window.financialStatus());
  if (status !== 'READY') throw new Error(status);
  const interestType = await page.evaluate(() => window.financialInterestType());
  if (interestType !== 'DoubleType') throw new Error('Expected numeric interest type, got ' + interestType);
  await page.waitForTimeout(1500);
  await page.screenshot({path: path.join(directory, 'calculator.png')});
  await page.getByRole('button', {name: 'Rename calculator', exact: true}).click();
  await page.waitForTimeout(150);
  await page.keyboard.type('SavingsSmoke');
  await page.waitForFunction(() => window.financialName() === 'SavingsSmoke');
  await page.keyboard.press('Control+z');
  await page.waitForFunction(() => window.financialName() !== 'SavingsSmoke');
  await page.reload();
  await page.waitForFunction(() => window.financialStatus?.() === 'READY', null, {timeout: 30000});
  // Real browser key events, with observations of the MPS model and selected cell.
  const selection = () => page.evaluate(() => {
    const [text, caret, y] = window.financialSelectionState().split('|');
    return {text, caret: Number(caret), y: Number(y)};
  });
  const expectName = name => page.waitForFunction(value => window.financialName() === value, name, {timeout: 3000});
  await page.getByRole('button', {name: 'Rename calculator', exact: true}).click();
  await page.waitForTimeout(100);
  // Use the loaded name, so typing and deletion are not coalesced by MPS's
  // normal same-cell undo grouping before testing the deletion's undo.
  await page.keyboard.press('End');
  await page.keyboard.press('Backspace');
  await expectName('Saving');
  await page.keyboard.press('Control+z');
  await expectName('Savings');
  await page.keyboard.press('End');
  await page.keyboard.press('ArrowLeft');
  assert.equal((await selection()).caret, 6, 'Left moves the model cell caret');
  await page.keyboard.press('ArrowRight');
  assert.equal((await selection()).caret, 7, 'Right moves the model cell caret');
  await page.keyboard.press('ArrowLeft');
  await page.keyboard.press('Delete');
  await expectName('Saving');
  await page.keyboard.press('Control+z');
  await expectName('Savings');
  await page.keyboard.press('End');
  await page.keyboard.press('Shift+ArrowLeft');
  await page.keyboard.press('Shift+ArrowLeft');
  await page.keyboard.press('Backspace');
  await expectName('Savin');
  await page.keyboard.press('Control+z');
  await expectName('Savings');
  await page.keyboard.press('Home');
  assert.equal((await selection()).text, 'Simulation', 'Home selects the first cell on the line');
  await page.keyboard.press('End');
  const firstLine = await selection();
  assert.equal(firstLine.text, 'Savings');
  await page.keyboard.press('ArrowDown');
  const secondLine = await selection();
  assert.ok(secondLine.y > firstLine.y, 'Down moves to the next editor line');
  await page.keyboard.press('ArrowUp');
  assert.equal((await selection()).y, firstLine.y, 'Up returns to the previous editor line');
  await page.keyboard.press('Control+End');
  assert.ok((await selection()).y > secondLine.y, 'Ctrl+End moves to the last editor line');
  await page.keyboard.press('Control+Home');
  assert.equal((await selection()).y, firstLine.y, 'Ctrl+Home moves to the first editor line');
  await expectName('Savings');
  // Keys outside the editor must not dispatch model actions.
  await page.getByRole('button', {name: 'Rename calculator', exact: true}).focus();
  await page.keyboard.press('Backspace');
  await expectName('Savings');
  await page.reload();
  await page.waitForFunction(() => window.financialStatus?.() === 'READY', null, {timeout: 30000});
  if (errors.length) throw new Error(errors.join('\n'));
  const report = {passed: true, url: page.url(), sha256: crypto.createHash('sha256').update(bundle).digest('hex'),
    checks: ['current development bundle', 'generated Calculator editor', 'real type engine infers DoubleType for interest', 'native rename updates model', 'keyboard undo', 'Backspace and Delete with undo', 'Left and Right move cell caret', 'Shift selection and deletion', 'Up and Down change editor line', 'Home/End and Ctrl+Home/End navigation', 'keyboard actions scoped to editor focus', 'reload restores sample'], errors};
  fs.writeFileSync(path.join(directory, 'smoke.json'), JSON.stringify(report, null, 2) + '\n');
  console.log(JSON.stringify(report));
} catch (error) {
  await page.screenshot({path: path.join(directory, 'failure.png')}).catch(() => {});
  fs.writeFileSync(path.join(directory, 'smoke.json'), JSON.stringify({passed: false, error: String(error), errors}, null, 2) + '\n');
  console.error(await page.locator('body').innerText());
  throw error;
} finally { await browser.close(); }
