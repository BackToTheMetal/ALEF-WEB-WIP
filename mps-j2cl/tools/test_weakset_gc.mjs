import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import {createRequire} from 'node:module';

const root = path.resolve(import.meta.dirname, '..');
const siblingRequire = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const {chromium} = siblingRequire('playwright');
const builds = process.argv.slice(2);
if (!builds.length) builds.push('util_browser_dev', 'util_browser');
const results = [];
const browser = await chromium.launch({headless: true});
try {
  for (const build of builds) {
    const source = fs.readFileSync(path.join(root, 'bazel-bin', build + '.js'), 'utf8');
    const page = await browser.newPage();
    const errors = [];
    page.on('pageerror', error => errors.push(String(error)));
    await page.addScriptTag({content: source});
    const result = await page.evaluate(() => globalThis.utilResult);
    let before, after;
    if (result === 'PASS') {
      // The constructor pins the referent until this JavaScript job ends.
      before = await page.evaluate(() => {
        globalThis.prepareWeakProbe();
        return globalThis.weakProbeSize();
      });
      const session = await page.context().newCDPSession(page);
      await session.send('HeapProfiler.enable');
      await session.send('HeapProfiler.collectGarbage');
      after = await page.evaluate(() => globalThis.weakProbeSize());
    }
    const passed = result === 'PASS' && before === 1 && after === 0 && errors.length === 0;
    results.push({build, passed, before, after, errors,
      sha256: crypto.createHash('sha256').update(source).digest('hex')});
    console.log(build + '/WeakRef GC: ' + (passed ? 'PASS' : 'FAIL'));
    await page.close();
  }
} finally {
  await browser.close();
  fs.mkdirSync(path.join(root, 'out'), {recursive: true});
  fs.writeFileSync(path.join(root, 'out/weakset-gc-results.json'), JSON.stringify(results, null, 2) + '\n');
}
if (results.some(result => !result.passed)) throw new Error('WeakSet GC verification failed');
