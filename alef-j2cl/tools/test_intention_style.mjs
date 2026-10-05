import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';
const root = path.resolve(import.meta.dirname, '..');
const engines = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const directory = path.join(root, 'out/intention-style');
fs.mkdirSync(directory, {recursive: true});
for (const name of (process.env.ALEF_BROWSERS || 'chromium').split(',')) {
  const browser = await engines[name].launch({headless: true});
  const page = await browser.newPage({viewport: {width: 1500, height: 1150}, deviceScaleFactor: 2});
  const errors = [];
  page.on('pageerror', e => errors.push(String(e)));
  try {
    await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8810/');
    await page.waitForFunction(() => window.alefExample?.status() === 'READY');
    const original = await page.evaluate(() => window.alefExample.outline(0));
    await page.getByRole('button', {name: 'Edit person name', exact: true}).click();
    await page.waitForFunction(() => document.activeElement?.getAttribute('aria-label') === 'Editor text input');
    await page.keyboard.press('Alt+Enter');
    const menu = page.locator('.ws-popup-menu').first();
    await menu.waitFor({state: 'visible'});
    await page.waitForTimeout(200);
    const report = await menu.evaluate(menu => ({
      menu: menu.getBoundingClientRect().toJSON(), shadow: getComputedStyle(menu).boxShadow,
      rows: [...menu.querySelectorAll('[role=menuitem]')].map(row => {
        const s = getComputedStyle(row);
        const bounds = row.getBoundingClientRect();
        const canvas = row.querySelector('canvas[data-swing-icon]');
        const pixels = canvas?.getContext('2d').getImageData(0, 0, canvas.width, canvas.height).data || [];
        let amberPixels = 0;
        for (let i = 0; i < pixels.length; i += 4) {
          if (pixels[i] > 230 && pixels[i + 1] > 140 && pixels[i + 1] < 200 && pixels[i + 2] < 50 && pixels[i + 3] > 200) amberPixels++;
        }
        const range = document.createRange(); range.selectNodeContents(row);
        return {text: row.textContent, font: s.font, size: s.fontSize, weight: s.fontWeight,
          background: s.backgroundColor, color: s.color, image: s.backgroundImage,
          border: s.border, shadow: s.boxShadow, align: s.textAlign, height: bounds.height,
          selected: row === document.activeElement, amberPixels,
          rightPadding: bounds.right - range.getBoundingClientRect().right};
      })
    }));
    await menu.screenshot({path: path.join(directory, `${name}-popup.png`)});
    fs.writeFileSync(path.join(directory, `${name}.json`), JSON.stringify(report, null, 2));
    assert.equal(report.rows.length, 8, 'All real node intentions are available');
    assert.ok(report.rows.every(row => row.size === '14px' && row.weight === '400'), 'Regular IDE menu typography');
    assert.ok(report.rows.every(row => row.height >= 26 && row.rightPadding >= 12), 'Comfortable rows with unclipped labels');
    assert.ok(report.rows.every(row => row.amberPixels > 20), 'Actual bulb artwork replaces missing-icon placeholders');
    assert.ok(report.rows[0].selected && report.rows[0].background === 'rgb(82, 109, 165)' && report.rows[0].color === 'rgb(255, 255, 255)', 'First intention is selected');
    assert.ok(report.rows.slice(1).every(row => row.background === 'rgb(255, 255, 255)'), 'Unselected rows have a flat background');
    const selectedIndex = () => menu.evaluate(m => [...m.querySelectorAll('[role=menuitem]')].indexOf(document.activeElement));
    await page.keyboard.press('ArrowDown'); assert.equal(await selectedIndex(), 1);
    await page.keyboard.press('End'); assert.equal(await selectedIndex(), 7);
    await page.keyboard.press('Home'); assert.equal(await selectedIndex(), 0);
    await page.keyboard.press('Escape');
    await menu.waitFor({state: 'detached'});
    assert.equal(await page.evaluate(() => window.alefExample.outline(0)), original, 'Cancel leaves the model intact');
    await page.getByRole('button', {name: 'Edit person name', exact: true}).click();
    await page.waitForFunction(() => document.activeElement?.getAttribute('aria-label') === 'Editor text input');
    await page.keyboard.press('Alt+Enter');
    await menu.waitFor({state: 'visible'});
    await page.keyboard.press('Enter');
    await menu.waitFor({state: 'detached'});
    await page.waitForFunction(before => window.alefExample.outline(0) !== before, original);
    await page.getByRole('button', {name: 'Undo', exact: true}).click();
    await page.waitForFunction(before => window.alefExample.outline(0) === before, original);
    assert.deepEqual(errors, []);
    console.log(JSON.stringify({browser: name, rows: report.rows.length, height: report.rows[0].height, checks: 'style, bulbs, full labels, keyboard selection, cancel, execute and undo', errors}));
  } finally { await browser.close(); }
}
