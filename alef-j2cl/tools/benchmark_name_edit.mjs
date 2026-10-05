import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';

const root = path.resolve(import.meta.dirname, '..');
const require = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const {chromium} = require('playwright');
const label = process.argv[2] || 'current';
const directory = path.join(root, 'out/name-edit-performance');
fs.mkdirSync(directory, {recursive: true});
const browser = await chromium.launch({headless: true});
const page = await browser.newPage({viewport: {width: 1500, height: 1150}});
const errors = [];
page.on('pageerror', error => errors.push(String(error)));
if (process.env.ALEF_DISABLE_MEASUREMENT_CACHE === '1') await page.addInitScript(() => {
  // An unused custom font exercises the provider's uncached path without
  // changing any font used by the editors; useful for same-build comparisons.
  document.fonts.add(new FontFace('AlefCacheBenchmark', 'local("AlefUnusedBenchmarkFont")'));
});
if (process.env.ALEF_PROFILE === '1') await page.addInitScript(() => {
  const add = EventTarget.prototype.addEventListener;
  const remove = EventTarget.prototype.removeEventListener;
  const wrapped = new WeakMap();
  window.keyListeners = [];
  EventTarget.prototype.addEventListener = function(type, listener, options) {
    if ((type === 'keydown' || type === 'keyup') && listener) {
      const original = listener;
      if (!wrapped.has(listener)) wrapped.set(listener, function(event) {
        const start = performance.now();
        try { return typeof original === 'function' ? original.call(this, event) : original.handleEvent(event); }
        finally {
          if (window.recordKeys) window.keyListeners.push({type: event.type, key: event.key, target: event.target.id || event.target.tagName,
            listener: this.id || this.tagName, prevented: event.defaultPrevented, ms: performance.now() - start});
        }
      });
      listener = wrapped.get(listener);
    }
    return add.call(this, type, listener, options);
  };
  EventTarget.prototype.removeEventListener = function(type, listener, options) {
    return remove.call(this, type, (type === 'keydown' || type === 'keyup') && wrapped.has(listener) ? wrapped.get(listener) : listener, options);
  };
});
try {
  await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8810/', {timeout: 60000});
  await page.waitForFunction(() => window.alefExample?.status() === 'READY' || window.alefExample?.status().startsWith('FAIL:'), null, {timeout: 60000});
  assert.equal(await page.evaluate(() => window.alefExample.status()), 'READY', errors.join('\n'));
  await page.waitForFunction(() => document.getElementById('object-type').textContent !== 'Type pending');
  await page.getByRole('button', {name: 'Edit person name', exact: true}).click();
  await page.waitForFunction(() => document.activeElement?.getAttribute('aria-label') === 'Editor text input');
  await page.keyboard.type('Persoon');
  await page.waitForTimeout(700);
  const cdp = await page.context().newCDPSession(page);
  if (process.env.ALEF_PROFILE === '1') {
    await cdp.send('Profiler.enable');
    await cdp.send('Profiler.setSamplingInterval', {interval: 100});
    await cdp.send('Profiler.start');
  }
  const samples = [];
  for (let i = 0; i < 24; i++) {
    const sample = await page.evaluate(index => {
      window.recordKeys = true;
      const input = document.activeElement;
      const key = index % 2 === 0 ? 'x' : 'Backspace';
      const start = performance.now();
      input.dispatchEvent(new KeyboardEvent('keydown', {key, code: key === 'x' ? 'KeyX' : key, bubbles: true, cancelable: true}));
      const down = performance.now();
      input.dispatchEvent(new KeyboardEvent('keyup', {key, code: key === 'x' ? 'KeyX' : key, bubbles: true, cancelable: true}));
      const up = performance.now();
      window.recordKeys = false;
      return {key, keydownMs: down - start, keyupMs: up - down, value: window.alefExample.value(0),
        referencesUpdated: window.alefExample.editorText(1).includes(window.alefExample.value(0))};
    }, i);
    assert.equal(sample.value, i % 2 === 0 ? 'Persoonx' : 'Persoon', 'One key must make exactly one edit');
    assert.ok(sample.referencesUpdated, 'Reference rendering follows the rename');
    samples.push(sample);
    await page.waitForTimeout(40);
  }
  if (process.env.ALEF_PROFILE === '1') {
    const {profile} = await cdp.send('Profiler.stop');
    fs.writeFileSync(path.join(directory, label + '.cpuprofile'), JSON.stringify(profile));
    fs.writeFileSync(path.join(directory, label + '-listeners.json'), JSON.stringify(await page.evaluate(() => window.keyListeners), null, 2));
  }
  const percentile = (key, fraction) => {
    const values = samples.slice(4).filter(s => s.key === key).map(s => s.keydownMs).sort((a, b) => a - b);
    return values[Math.ceil(values.length * fraction) - 1];
  };
  const report = {label, url: process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8810/',
    profiled: process.env.ALEF_PROFILE === '1', cacheDisabled: process.env.ALEF_DISABLE_MEASUREMENT_CACHE === '1',
    typedMedianMs: percentile('x', .5), typedP95Ms: percentile('x', .95),
    backspaceMedianMs: percentile('Backspace', .5), samples, errors};
  assert.deepEqual(errors, []);
  fs.writeFileSync(path.join(directory, label + '.json'), JSON.stringify(report, null, 2) + '\n');
  console.log(JSON.stringify({...report, samples: samples.length}));
} finally { await browser.close(); }
