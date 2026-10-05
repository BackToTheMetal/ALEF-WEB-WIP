import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';
const root = path.resolve(import.meta.dirname, '..');
const engines = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const directory = path.join(root, 'out/completion-style');
fs.mkdirSync(directory, {recursive: true});
for (const name of (process.env.ALEF_BROWSERS || 'chromium').split(',')) {
  const browser = await engines[name].launch({headless: true});
  const page = await browser.newPage({viewport: {width: 1500, height: 1150}, deviceScaleFactor: 2});
  const errors = [];
  page.on('pageerror', error => errors.push(String(error)));
  try {
    await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8810/');
    await page.waitForFunction(() => window.alefExample?.status() === 'READY');
    await page.waitForFunction(() => document.getElementById('object-type').textContent !== 'Type pending');
    await page.evaluate(() => {
      window.completionPaint = [];
      const draw = CanvasRenderingContext2D.prototype.fillText;
      CanvasRenderingContext2D.prototype.fillText = function(text, ...args) {
        if (this.canvas.closest('.ws-popup-menu')) window.completionPaint.push({text, font: this.font, color: this.fillStyle});
        return draw.call(this, text, ...args);
      };
    });
    // Open the actual root so the reference is visible regardless of sidebar width.
    await page.locator('mps-project-view').getByRole('treeitem', {name: 'Bereken toeslag', exact: true}).click();
    await page.evaluate(() => window.alefExample.focus(1));
    await page.waitForFunction(() => document.activeElement?.getAttribute('aria-label') === 'Editor text input');
    const bounds = await page.locator('#alef-editor').boundingBox();
    await page.mouse.click(bounds.x + 150, bounds.y + 165);
    await page.waitForTimeout(250);
    await page.keyboard.press('Control+Space');
    await page.locator('.ws-popup-menu [role=listbox]').waitFor({state: 'visible'});
    for (let i = 0; i < 7; i++) await page.keyboard.press('ArrowRight');
    for (let i = 0; i < 7; i++) await page.keyboard.press('Backspace');
    await page.waitForFunction(() => document.querySelectorAll('.ws-popup-menu [role=option]').length > 1);
    await page.waitForTimeout(300);
    const report = await page.evaluate(() => {
      const menu = document.querySelector('.ws-popup-menu');
      const list = menu.querySelector('[role=listbox]');
      const row = list.querySelector('[role=option]');
      return {rows: [...list.children].map(e => e.getAttribute('aria-label')), rowHeight: row.getBoundingClientRect().height,
        viewportHeight: list.parentElement.clientHeight, menu: menu.getBoundingClientRect().toJSON(),
        paint: [...new Map(window.completionPaint.map(p => [JSON.stringify(p), p])).values()]};
    });
    await page.screenshot({path: path.join(directory, `${name}.png`), fullPage: true});
    await page.locator('.ws-popup-menu').screenshot({path: path.join(directory, `${name}-popup.png`)});
    fs.writeFileSync(path.join(directory, `${name}.json`), JSON.stringify(report, null, 2));
    if (process.env.ALEF_CAPTURE_ONLY !== '1') {
      const description = report.paint.filter(p => p.text.includes('uit objectmodel'));
      const names = report.paint.filter(p => ['naam', 'leeftijd', 'geboortedatum'].includes(p.text));
      assert.ok(description.length && names.length, 'Real reference names and package descriptions are painted');
      const size = p => Number(/([\d.]+)px/.exec(p.font)[1]);
      assert.ok(description.every(p => size(p) < Math.min(...names.map(size))), 'Package descriptions use smaller letters');
      assert.ok(description.some(p => p.color === '#707580'), 'Unselected descriptions use a subdued color');
      assert.ok(report.rowHeight >= 24, 'Rows retain comfortable vertical spacing');
      assert.ok(report.viewportHeight >= report.rowHeight * Math.min(6, report.rows.length), 'Popup shows complete result rows');
      assert.ok(report.menu.left >= 0 && report.menu.right <= 1500, 'Popup fits the browser');
      assert.deepEqual(errors, []);
    }
    console.log(JSON.stringify({browser: name, rows: report.rows, rowHeight: report.rowHeight, viewportHeight: report.viewportHeight, errors}));
  } finally { await browser.close(); }
}
