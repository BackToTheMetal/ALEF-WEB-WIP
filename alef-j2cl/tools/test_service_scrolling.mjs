import {createRequire} from 'node:module';
import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
const root = path.resolve(import.meta.dirname, '..');
const engines = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const directory = path.join(root, 'out/service-scrolling');
fs.mkdirSync(directory, {recursive: true});
for (const name of (process.env.ALEF_BROWSERS || 'chromium').split(',')) {
  const browser = await engines[name].launch({headless: true});
  const page = await browser.newPage({viewport: {width: 1600, height: 1150}, deviceScaleFactor: 2});
  const report = {browser: name, url: process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8810/', deviceScaleFactor: 2, samples: [], errors: []};
  page.on('pageerror', error => report.errors.push(String(error)));
  try {
    await page.goto(report.url);
    await page.waitForFunction(() => globalThis.alefExample?.status() === 'READY');
    await page.locator('#show-services').click();
    const bar = page.locator('[role=scrollbar][aria-orientation=vertical]:visible').first();
    const state = () => bar.evaluate(bar => {
      const viewport = Array.from(bar.parentElement.children).find(e => e.style?.scrollbarWidth === 'none');
      const view = viewport.firstElementChild;
      return {value: +bar.getAttribute('aria-valuenow'), max: +bar.getAttribute('aria-valuemax'),
        scrollTop: viewport.scrollTop, scrollHeight: viewport.scrollHeight, clientHeight: viewport.clientHeight,
        viewTop: view.style.top, viewHeight: view.style.height, viewport: viewport.getBoundingClientRect().toJSON()};
    });
    await page.waitForTimeout(500);
    const initial = await state();
    report.samples.push(initial);
    await page.mouse.move(initial.viewport.x + initial.viewport.width / 2, initial.viewport.y + 180);
    for (let i = 0; i < 12; i++) {
      await page.mouse.wheel(0, 300);
      await page.waitForTimeout(100);
      report.samples.push(await state());
    }
    await page.screenshot({path: path.join(directory, `${name}-bottom.png`), fullPage: true});
    const last = report.samples.at(-1);
    assert.ok(report.samples.every((s, i) => i === 0 || s.value >= report.samples[i - 1].value), 'Downward wheel movement never jumps backward');
    assert.ok(initial.max > initial.clientHeight, 'Service content extends beyond two screens');
    assert.equal(last.value, initial.max, 'Wheel reaches the complete service, including its mappings');
    assert.equal(last.scrollHeight, initial.scrollHeight, 'Scrolling preserves the DOM content extent');
    for (let i = 0; i < 12; i++) { await page.mouse.wheel(0, -300); await page.waitForTimeout(80); }
    assert.equal((await state()).value, 0, 'Wheel returns to the top');
    await bar.focus();
    await page.keyboard.press('End');
    assert.equal((await state()).value, initial.max, 'Keyboard End reaches the same bottom');
    await page.keyboard.press('Home');
    assert.equal((await state()).value, 0, 'Keyboard Home restores the top');
    await page.setViewportSize({width: 1000, height: 900});
    await page.waitForTimeout(300);
    const narrow = await state();
    const horizontal = bar.locator('..').locator('[role=scrollbar][aria-orientation=horizontal]');
    const horizontalState = () => horizontal.evaluate(bar => {
      const viewport = Array.from(bar.parentElement.children).find(e => e.style?.scrollbarWidth === 'none');
      return {value: +bar.getAttribute('aria-valuenow'), max: +bar.getAttribute('aria-valuemax'),
        scrollLeft: viewport.scrollLeft, scrollWidth: viewport.scrollWidth};
    });
    const horizontalStart = await horizontalState();
    assert.ok(horizontalStart.max > 0, 'Narrow service editor has horizontal overflow');
    await page.mouse.move(narrow.viewport.x + narrow.viewport.width / 2, narrow.viewport.y + 160);
    for (let i = 0; i < 12; i++) { await page.mouse.wheel(300, 0); await page.waitForTimeout(80); }
    const horizontalEnd = await horizontalState();
    assert.equal(horizontalEnd.value, horizontalStart.max, 'Horizontal wheel reaches the right edge');
    assert.equal(horizontalEnd.scrollLeft, horizontalEnd.value, 'DOM and Swing agree horizontally');
    assert.equal(horizontalEnd.scrollWidth, horizontalStart.scrollWidth);
    for (let i = 0; i < 12; i++) { await page.mouse.wheel(-300, 0); await page.waitForTimeout(80); }
    assert.equal((await horizontalState()).value, 0, 'Horizontal wheel returns to the left');
    report.horizontal = {start: horizontalStart, end: horizontalEnd};
    await page.screenshot({path: path.join(directory, `${name}-narrow.png`), fullPage: true});
    assert.deepEqual(report.errors, []);
    report.passed = true;
  } catch (error) { report.passed = false; report.failure = String(error); process.exitCode = 1; }
  finally {
    fs.writeFileSync(path.join(directory, `${name}.json`), JSON.stringify(report, null, 2));
    console.log(JSON.stringify(report));
    await browser.close();
  }
}
