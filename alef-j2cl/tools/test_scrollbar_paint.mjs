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
    fs.createReadStream(path.join(root, 'bazel-bin/scrollbar_paint_browser_dev.js')).pipe(response);
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
      const page = await browser.newPage({deviceScaleFactor: 2});
      const errors = [];
      page.on('pageerror', error => errors.push(String(error)));
      await page.goto(`http://127.0.0.1:${server.address().port}/`, {timeout: 60000});
      await page.waitForFunction(() => window.scrollbarPaintResult?.(), null, {timeout: 60000});
      const result = await page.evaluate(() => window.scrollbarPaintResult());
      assert.match(result, /^PASS:/);
      assert.deepEqual(errors, []);
      results.push({browser: name, result, errors});
    } finally { await browser.close(); }
  }
  fs.mkdirSync(path.join(root, 'out/gutter-errors'), {recursive: true});
  fs.writeFileSync(path.join(root, 'out/gutter-errors/paint-contracts.json'), JSON.stringify(results, null, 2) + '\n');
  console.log(JSON.stringify(results));
} finally { server.close(); }

