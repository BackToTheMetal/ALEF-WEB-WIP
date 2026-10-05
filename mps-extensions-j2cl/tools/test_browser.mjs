import fs from 'node:fs';
import path from 'node:path';
import http from 'node:http';
import crypto from 'node:crypto';
import {createRequire} from 'node:module';

const root = path.resolve(import.meta.dirname, '..');
const require = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const {chromium, firefox, webkit} = require('playwright');
const engines = {chromium, firefox, webkit};
const selected = process.argv[2];
if (selected && !engines[selected]) throw new Error('Unknown browser: ' + selected);
const builds = process.argv[3] ? [process.argv[3]] : ['contracts_browser_dev', 'contracts_browser', 'contracts_stripped'];
const bundles = new Map(builds.map(build => {
  const filename = path.join(root, 'bazel-bin', build + '.js');
  return ['/' + build + '.js', fs.readFileSync(filename)];
}));
const server = http.createServer((req, res) => {
  const bundle = bundles.get(req.url);
  res.setHeader('Content-Type', bundle ? 'application/javascript' : 'text/html');
  res.end(bundle ?? '<!doctype html><meta charset="utf-8"><title>Extensions runtime contracts</title>');
});
await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
const results = [];
try {
  for (const [engine, type] of Object.entries(engines)) {
    if (selected && engine !== selected) continue;
    const browser = await type.launch({headless: true});
    try {
      for (const build of builds) {
        const page = await browser.newPage();
        const errors = [], consoleErrors = [];
        page.on('pageerror', e => errors.push(String(e)));
        page.on('console', message => { if (message.type() === 'error') consoleErrors.push(message.text()); });
        let output = null;
        try {
          await page.goto(`http://127.0.0.1:${server.address().port}/`);
          await page.addScriptTag({url: '/' + build + '.js'});
          await page.waitForFunction(() => globalThis.extensionResult?.() != null, null, {timeout: 30000});
          output = await page.evaluate(() => globalThis.extensionResult());
        } catch (e) { errors.push(String(e)); }
        const passed = output?.startsWith('PASS: ') === true && errors.length === 0 && consoleErrors.length === 0;
        results.push({engine, build, passed, output, errors, consoleErrors,
          sha256: crypto.createHash('sha256').update(bundles.get('/' + build + '.js')).digest('hex')});
        console.log(`${engine}/${build}: ${output ?? errors.join('\n')}`);
        if (!passed) console.error(consoleErrors.join('\n'));
        await page.close();
      }
    } finally { await browser.close(); }
  }
} finally {
  server.close();
  fs.mkdirSync(path.join(root, 'out'), {recursive: true});
  fs.writeFileSync(path.join(root, 'out/browser-results.json'), JSON.stringify(results, null, 2) + '\n');
}
if (!results.length || results.some(r => !r.passed)) process.exitCode = 1;
