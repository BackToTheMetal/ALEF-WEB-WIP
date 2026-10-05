import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import http from 'node:http';
import assert from 'node:assert/strict';

const root = path.resolve(import.meta.dirname, '..');
const require = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const playwright = require('playwright');
const server = http.createServer((request, response) => {
  if (request.url === '/app.js') {
    response.setHeader('Content-Type', 'application/javascript');
    fs.createReadStream(path.join(root, 'bazel-bin/font_measurement_browser_dev.js')).pipe(response);
  } else {
    response.setHeader('Content-Type', 'text/html');
    response.end('<!doctype html><html><body><script src="/app.js"></script></body></html>');
  }
});
await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
const results = [];
try {
  for (const name of ['chromium', 'firefox', 'webkit']) {
    const browser = await playwright[name].launch({headless: true});
    try {
      const page = await browser.newPage();
      const errors = [];
      page.on('pageerror', error => errors.push(String(error)));
      await page.goto(`http://127.0.0.1:${server.address().port}/`, {timeout: 60000});
      const checks = await page.evaluate(async () => {
        const passed = [];
        const check = (condition, message) => { if (!condition) throw new Error(message); passed.push(message); };
        const native = CanvasRenderingContext2D.prototype.measureText;
        let calls = 0;
        CanvasRenderingContext2D.prototype.measureText = function(text) { calls++; return native.call(this, text); };
        const context = document.createElement('canvas').getContext('2d');
        try {
          for (const font of ['18px Arial', 'bold 18px Arial', 'italic 14px serif', '24px monospace', 'invalid font']) {
            for (const text of ['', 'Persoon', 'Woning', 'AV fi', 'é漢😀']) {
              context.font = '10px sans-serif'; context.font = font;
              const expected = native.call(context, text);
              const actual = window.fontMeasurement(font, text);
              check(Math.abs(actual[0] - expected.width) < .0001, 'native width: ' + font + '/' + text);
              const sample = native.call(context, 'Hg');
              const nativeBounds = Number.isFinite(expected.fontBoundingBoxAscent) && Number.isFinite(expected.fontBoundingBoxDescent);
              const ascent = nativeBounds ? expected.fontBoundingBoxAscent : sample.actualBoundingBoxAscent;
              const descent = nativeBounds ? expected.fontBoundingBoxDescent : sample.actualBoundingBoxDescent;
              check(actual.slice(0, 3).every(Number.isFinite) && actual[1] === ascent && actual[2] === descent,
                'native line metrics: ' + font + '/' + text);
              const before = calls;
              actual[0] = -100;
              const again = window.fontMeasurement(font, text);
              check(calls === before && again[0] >= 0, 'cache hit and defensive result: ' + font + '/' + text);
            }
          }
          const face = new FontFace('CacheTest', 'local("Arial")');
          document.fonts.add(face);
          const beforeCustom = calls;
          window.fontMeasurement('18px Arial', 'Persoon');
          const once = calls;
          window.fontMeasurement('18px Arial', 'Persoon');
          check(once > beforeCustom && calls > once, 'custom font sets bypass caching');
          document.fonts.delete(face);
          window.fontMeasurement('18px Arial', 'Task boundary');
          await new Promise(resolve => setTimeout(resolve, 20));
          const beforeNextTask = calls;
          window.fontMeasurement('18px Arial', 'Task boundary');
          check(calls > beforeNextTask, 'measurements expire between browser tasks');
          window.fontMeasurement('18px Arial', 'Evicted');
          for (let i = 0; i < 2050; i++) window.fontMeasurement('18px Arial', 'bounded-' + i);
          const beforeEvicted = calls;
          window.fontMeasurement('18px Arial', 'Evicted');
          check(calls > beforeEvicted, 'cache size is bounded');
          return passed;
        } finally { CanvasRenderingContext2D.prototype.measureText = native; }
      });
      assert.deepEqual(errors, []);
      results.push({browser: name, passed: true, checks: checks.length, errors});
    } finally { await browser.close(); }
  }
  fs.mkdirSync(path.join(root, 'out/name-edit-performance'), {recursive: true});
  fs.writeFileSync(path.join(root, 'out/name-edit-performance/font-contracts.json'), JSON.stringify(results, null, 2) + '\n');
  console.log(JSON.stringify(results));
} finally { server.close(); }
