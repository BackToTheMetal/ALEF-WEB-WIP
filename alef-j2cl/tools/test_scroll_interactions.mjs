import {createRequire} from 'node:module';
import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
const root = path.resolve(import.meta.dirname, '..');
const engines = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const directory = path.join(root, 'out/scroll-interactions');
fs.mkdirSync(directory, {recursive: true});
for (const name of (process.env.ALEF_BROWSERS || 'chromium').split(',')) {
  const browser = await engines[name].launch({headless: true});
  const page = await browser.newPage({viewport: {width: 1600, height: 1150}, deviceScaleFactor: 2});
  const report = {browser: name, deviceScaleFactor: 2, checks: [], errors: [], failures: []};
  page.on('pageerror', error => report.errors.push(String(error)));
  const check = async (label, action) => {
    try { await action(); report.checks.push(label); }
    catch (e) { report.failures.push(`${label}: ${e}`); }
  };
  try {
    await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8810/');
    await page.waitForFunction(() => globalThis.alefExample?.status() === 'READY');
    await page.locator('#show-services').click();
    await page.waitForTimeout(500);
    const bar = page.locator('[role=scrollbar][aria-orientation=vertical]:visible').first();
    const value = () => bar.evaluate(e => +e.getAttribute('aria-valuenow'));
    await bar.scrollIntoViewIfNeeded();
    await bar.focus(); await page.keyboard.press('Home');
    await check('vertical drag continues outside track and stops on release', async () => {
      const box = await bar.boundingBox();
      await page.mouse.move(box.x + box.width / 2, box.y + 40);
      await page.mouse.down();
      await page.mouse.move(box.x + box.width / 2, box.y + 90, {steps: 4});
      const inside = await value();
      await page.mouse.move(box.x - 120, box.y + 210, {steps: 6});
      const outside = await value();
      await page.mouse.up();
      await page.mouse.move(box.x + box.width / 2, box.y + 310, {steps: 4});
      report.drag = {inside, outside, released: await value()};
      assert.ok(inside > 0, 'Dragging begins on the thumb');
      assert.ok(outside > inside + 100, 'Dragging still follows the pointer outside the track');
      assert.equal(await value(), outside, 'Release outside the track ends dragging');
    });
    await page.mouse.up();
    await page.getByRole('button', {name: 'Edit entry point name', exact: true}).click();
    await page.waitForFunction(() => document.activeElement?.getAttribute('aria-label') === 'Editor text input');
    // Record intermediate geometry, not just the final frame: a transient resize
    // clears the canvas even if its original dimensions are restored in the task.
    const startTrace = () => bar.evaluate(bar => {
      const viewport = Array.from(bar.parentElement.children).find(e => e.style?.scrollbarWidth === 'none');
      const trace = window.scrollTrace = {heights: [], canvasResizes: [], frames: [], viewport};
      const observer = new MutationObserver(records => {
        for (const r of records) {
          if (r.target === viewport && r.attributeName === 'style') {
            trace.heights.push(/(?:^|;)\s*height:\s*([^;]+)/.exec(r.oldValue || '')?.[1]);
          }
          if (r.target.tagName === 'CANVAS' && ['width', 'height'].includes(r.attributeName)) {
            trace.canvasResizes.push({dimension: r.attributeName, old: r.oldValue, now: r.target.getAttribute(r.attributeName)});
          }
        }
      });
      observer.observe(bar.parentElement, {subtree: true, attributes: true, attributeOldValue: true, attributeFilter: ['style', 'width', 'height']});
      trace.observer = observer;
      const sample = document.createElement('canvas'); sample.width = sample.height = 64;
      const context = sample.getContext('2d', {willReadFrequently: true});
      const surface = viewport.querySelector('[data-webswing-paint-surface]');
      const frame = () => {
        context.clearRect(0, 0, 64, 64); context.drawImage(surface, 0, 0, 64, 64);
        const pixels = context.getImageData(0, 0, 64, 64).data;
        let ink = 0;
        for (let i = 0; i < pixels.length; i += 4) if (pixels[i + 3] > 0 && Math.min(pixels[i], pixels[i + 1], pixels[i + 2]) < 220) ink++;
        trace.frames.push({height: viewport.clientHeight, x: viewport.scrollLeft, ink}); trace.raf = requestAnimationFrame(frame);
      };
      trace.raf = requestAnimationFrame(frame);
    });
    const stopTrace = () => page.evaluate(() => {
      const t = window.scrollTrace; t.observer.disconnect(); cancelAnimationFrame(t.raf);
      return {heights: [...new Set(t.heights.filter(Boolean))], canvasResizes: t.canvasResizes,
        frames: t.frames, finalHeight: t.viewport.clientHeight, scrollLeft: t.viewport.scrollLeft,
        value: window.alefExample.value(2)};
    });
    await startTrace();
    await page.keyboard.type('W'.repeat(38), {delay: 30});
    await page.waitForTimeout(400);
    report.appearance = await stopTrace();
    await check('scrollbar appears without blank frames or size oscillation', async () => {
      const frames = report.appearance.frames;
      assert.ok(frames.length > 0 && frames.every(f => f.ink > 0));
      assert.ok(frames.every((f, i) => i === 0 || f.height <= frames[i - 1].height));
      assert.ok(frames.at(-1).height < frames[0].height, 'Horizontal bar appears as text grows');
    });
    await startTrace();
    await page.keyboard.type('W'.repeat(12), {delay: 50});
    await page.waitForTimeout(400);
    report.typing = await stopTrace();
    await check('typing into overflow keeps viewport height stable', async () => {
      assert.equal(report.typing.value, 'W'.repeat(50));
      assert.ok(report.typing.scrollLeft > 0, 'Caret scrolls into view');
      assert.ok(report.typing.heights.every(h => h === `${report.typing.finalHeight}px`), 'No temporary expansion to the height without a scrollbar');
      assert.ok(report.typing.frames.length > 0 && report.typing.frames.every(f => f.height === report.typing.finalHeight), 'Rendered viewport height remains stable');
      assert.ok(report.typing.canvasResizes.every(r => r.old !== r.now), 'Unchanged canvas dimensions do not clear the drawing surface');
      assert.ok(report.typing.frames.every(f => f.ink > 0), 'Editor content never disappears for a rendered frame');
    });
    await check('horizontal drag continues outside track and ends on capture loss', async () => {
      const horizontal = bar.locator('..').locator('[role=scrollbar][aria-orientation=horizontal]');
      await horizontal.scrollIntoViewIfNeeded();
      await horizontal.focus(); await page.keyboard.press('Home');
      await horizontal.evaluate(el => el.addEventListener('pointerdown', e => { window.dragPointer = e.pointerId; }, {once: true}));
      const box = await horizontal.boundingBox();
      const position = () => horizontal.evaluate(e => +e.getAttribute('aria-valuenow'));
      await page.mouse.move(box.x + 40, box.y + box.height / 2);
      await page.mouse.down();
      await page.mouse.move(box.x + 100, box.y + box.height / 2, {steps: 4});
      const inside = await position();
      await page.mouse.move(box.x + 240, box.y - 100, {steps: 6});
      const outside = await position();
      assert.ok(inside > 0 && outside > inside, 'Horizontal drag follows movement outside the track');
      await horizontal.evaluate(el => el.releasePointerCapture(window.dragPointer));
      await page.mouse.move(box.x + 300, box.y + box.height / 2, {steps: 4});
      assert.equal(await position(), outside, 'Losing capture ends the active drag');
      await page.mouse.up();
      report.horizontalDrag = {inside, outside};
    });
    await page.mouse.up();
    await page.getByRole('button', {name: 'Edit entry point name', exact: true}).click();
    await page.waitForFunction(() => document.activeElement?.getAttribute('aria-label') === 'Editor text input');
    await startTrace();
    await page.keyboard.type('X');
    await page.waitForTimeout(500);
    report.shrink = await stopTrace();
    await check('scrollbar disappears when text fits again without blank frames', async () => {
      const horizontal = bar.locator('..').locator('[role=scrollbar][aria-orientation=horizontal]');
      assert.equal(await horizontal.isVisible(), false);
      assert.equal(report.shrink.value, 'X');
      assert.ok(report.shrink.frames.length > 0 && report.shrink.frames.every(f => f.ink > 0));
      assert.ok(report.shrink.finalHeight > report.typing.finalHeight);
    });
    await page.screenshot({path: path.join(directory, `${name}.png`), fullPage: true});
    assert.deepEqual(report.errors, []);
  } catch (e) { report.failures.push(String(e)); }
  finally {
    report.passed = report.failures.length === 0;
    fs.writeFileSync(path.join(directory, `${name}.json`), JSON.stringify(report, null, 2));
    console.log(JSON.stringify({browser: name, passed: report.passed, drag: report.drag, heights: report.typing?.heights,
      canvasResizes: report.typing?.canvasResizes.length, checks: report.checks, failures: report.failures}));
    if (!report.passed) process.exitCode = 1;
    await browser.close();
  }
}
