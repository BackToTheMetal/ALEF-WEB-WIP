import fs from 'node:fs';
import path from 'node:path';
import http from 'node:http';
import crypto from 'node:crypto';
import {createRequire} from 'node:module';

const root = path.resolve(import.meta.dirname, '..');
const require = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const {chromium} = require('playwright');
const languages = process.argv.includes('--languages');
const optimized = process.argv.includes('--optimized');
const target = (languages ? 'language_contracts_browser' : 'dependency_contracts_browser') + (optimized ? '' : '_dev');
const getter = languages ? 'languageResult' : 'dependencyResult';
const bundle = fs.readFileSync(path.join(root, `bazel-bin/${target}.js`));
const server = http.createServer((req, res) => {
  res.setHeader('Content-Type', req.url === '/bundle.js' ? 'application/javascript' : 'text/html');
  res.end(req.url === '/bundle.js' ? bundle : '<!doctype html><meta charset="utf-8"><title>ALEF dependencies</title>');
});
await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
const browser = await chromium.launch({headless: true});
const errors = [];
const consoleErrors = [];
const messages = [];
let result;
try {
  const page = await browser.newPage();
  page.on('pageerror', error => errors.push(String(error)));
  page.on('console', message => {
    if (message.type() === 'error') consoleErrors.push(message.text().slice(0, 4000));
    if (messages.length < 100) messages.push(message.type() + ': ' + message.text().slice(0, 4000));
  });
  await page.goto(`http://127.0.0.1:${server.address().port}/`);
  await page.addScriptTag({url: '/bundle.js'});
  await page.waitForFunction(key => globalThis[key]?.() != null, getter, {timeout: 60000});
  result = await page.evaluate(key => ({jre: globalThis.jreResult, contracts: globalThis[key]()}), getter);
} catch (error) {
  errors.push(String(error));
} finally {
  await browser.close();
  server.close();
}
const editorFailures = messages.filter(message => message.includes('Failed to create cell for node:'));
const passed = (languages || result?.jre === 'PASS: composed JRE') && result?.contracts?.startsWith('PASS: ') && !result?.contracts?.includes('failed:') && errors.length === 0 && consoleErrors.length === 0 && editorFailures.length === 0;
const report = {passed: !!passed, target, result, errors, consoleErrors, messages, sha256: crypto.createHash('sha256').update(bundle).digest('hex')};
fs.writeFileSync(path.join(root, `out/${languages ? 'language' : 'dependency'}-browser${optimized ? '-optimized' : ''}.json`), JSON.stringify(report, null, 2) + '\n');
console.log(JSON.stringify(report));
if (!passed) process.exitCode = 1;
