import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';

const root = path.resolve(import.meta.dirname, '..');
const browsers = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const directory = path.join(root, 'out/scrollbars');
fs.mkdirSync(directory, {recursive: true});
const reports = [];
for (const name of ['chromium', 'firefox', 'webkit']) {
  const browser = await browsers[name].launch({headless: true});
  try {
    const page = await browser.newPage({viewport: {width: 1500, height: 1150}});
    const errors = [];
    page.on('pageerror', error => errors.push(String(error)));
    await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8810/', {timeout: 60000});
    await page.waitForFunction(() => window.alefExample?.status() === 'READY' || window.alefExample?.status().startsWith('FAIL:'), null, {timeout: 60000});
    assert.equal(await page.evaluate(() => window.alefExample.status()), 'READY', errors.join('\n'));
    await page.waitForTimeout(600);
    const geometry = async () => {
      const controls = await page.locator('[role=scrollbar]:visible').evaluateAll(bars => bars.flatMap(bar =>
        Array.from(bar.querySelectorAll('button')).map(button => ({
          bar: bar.getBoundingClientRect().toJSON(), button: button.getBoundingClientRect().toJSON(),
          width: parseFloat(button.style.width), height: parseFloat(button.style.height), tabIndex: button.tabIndex,
          icons: Array.from(button.querySelectorAll('[data-swing-icon]')).map(icon => icon.getBoundingClientRect().toJSON())
        }))));
      assert.ok(controls.length >= 4, 'Both editor gutters must be present');
      for (const c of controls) {
        assert.equal(c.button.width, c.width, `${name}: scrollbar button must respect its Java width`);
        assert.equal(c.button.height, c.height, `${name}: zero-height controls must remain empty`);
        assert.ok(c.button.left >= c.bar.left - .5 && c.button.right <= c.bar.right + .5 && c.button.bottom <= c.bar.bottom + .5,
          `${name}: scrollbar controls stay inside their track`);
        assert.equal(c.tabIndex, -1, 'Scrollbar buttons must not introduce stray keyboard focus stops');
        for (const icon of c.icons) assert.ok(icon.left >= c.button.left - .5 && icon.right <= c.button.right + .5
          && icon.top >= c.button.top - .5 && icon.bottom <= c.button.bottom + .5, `${name}: status icon fits its button`);
      }
      return controls.length;
    };
    const wideControls = await geometry();
    await page.screenshot({path: path.join(directory, name + '-wide.png'), fullPage: true});
    await page.setViewportSize({width: 900, height: 900});
    await page.waitForTimeout(300);
    const narrowControls = await geometry();
    const bar = page.locator('[role=scrollbar][aria-orientation=horizontal]:visible').first();
    assert.ok(await bar.count(), 'Narrow editor has horizontal overflow');
    const state = () => bar.evaluate(element => ({
      value: Number(element.getAttribute('aria-valuenow')), max: Number(element.getAttribute('aria-valuemax')),
      scrollLeft: Array.from(element.parentElement.children).find(child => child.style?.scrollbarWidth === 'none')?.scrollLeft
    }));
    await bar.focus();
    await page.keyboard.press('End');
    let position = await state();
    assert.ok(position.max > 0);
    assert.equal(position.value, position.max, 'End reaches the end of the scrollbar');
    assert.equal(position.scrollLeft, position.value, 'Scrollbar and viewport stay synchronized');
    await page.keyboard.press('Home');
    assert.equal((await state()).value, 0);
    await bar.locator('button').last().click();
    position = await state();
    assert.ok(position.value > 0, 'Arrow button scrolls the viewport');
    assert.equal(position.scrollLeft, position.value);
    await page.screenshot({path: path.join(directory, name + '-narrow.png'), fullPage: true});
    assert.deepEqual(errors, []);
    reports.push({browser: name, passed: true, wideControls, narrowControls, errors});
  } finally { await browser.close(); }
}
fs.writeFileSync(path.join(directory, 'report.json'), JSON.stringify(reports, null, 2) + '\n');
console.log(JSON.stringify(reports));
