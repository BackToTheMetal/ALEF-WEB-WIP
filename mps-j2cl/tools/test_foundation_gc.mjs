import fs from 'node:fs';
import crypto from 'node:crypto';
import path from 'node:path';
import {createRequire} from 'node:module';
const root = path.resolve(import.meta.dirname, '..');
const require = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const {chromium} = require('playwright');
const results = [];
const browser = await chromium.launch({headless: true});
try {
  for (const build of ['foundation_jre_browser_dev', 'foundation_jre_browser']) {
    const source = fs.readFileSync(path.join(root, 'bazel-bin', build + '.js'), 'utf8');
    const sha256 = crypto.createHash('sha256').update(source).digest('hex');
    const page = await browser.newPage();
    const errors = [];
    page.on('pageerror', error => errors.push(String(error)));
    try {
      await page.addScriptTag({content: source});
      const initial = await page.evaluate(() => globalThis.foundationJreResult);
      if (initial !== 'PASS') throw new Error('Initial contracts: ' + await page.evaluate(() => globalThis.foundationJreError));
      await page.evaluate(() => globalThis.prepareFoundationGc());
      const session = await page.context().newCDPSession(page);
      await session.send('HeapProfiler.enable');
      let actual = 'PENDING', attempts = 0;
      while (actual === 'PENDING' && attempts++ < 10) {
        // Each evaluate/CDP command is a separate JS job: deref's job keepalive has ended.
        await session.send('HeapProfiler.collectGarbage');
        actual = await page.evaluate(() => globalThis.pollFoundationGc());
      }
      if (actual !== 'COLLECTED') throw new Error('Weak references/map were not collected and queued');
      await page.evaluate(() => globalThis.prepareIteratorGc());
      await session.send('HeapProfiler.collectGarbage');
      const retained = await page.evaluate(() => globalThis.advanceIteratorGc());
      if (retained !== 'RETAINED') throw new Error('Iterator lost selected key before next');
      await session.send('HeapProfiler.collectGarbage');
      const removed = await page.evaluate(() => globalThis.removeIteratorGc());
      if (removed !== 'REMOVED') throw new Error('Iterator lost current key before remove');
      let iteratorActual = 'PENDING', iteratorAttempts = 0;
      while (iteratorActual === 'PENDING' && iteratorAttempts++ < 10) {
        await session.send('HeapProfiler.collectGarbage');
        iteratorActual = await page.evaluate(() => globalThis.pollIteratorGc());
      }
      await session.detach();
      if (iteratorActual !== 'COLLECTED') throw new Error('Iterator retained the key after removal');
      results.push({build, sha256, passed: errors.length === 0, actual, attempts, iteratorActual, iteratorAttempts, errors});
    } catch (error) {
      results.push({build, sha256, passed: false, errors: [...errors, String(error.stack ?? error)]});
    } finally { await page.close(); }
  }
} finally {
  await browser.close();
  fs.mkdirSync(path.join(root, 'out'), {recursive: true});
  fs.writeFileSync(path.join(root, 'out/foundation-gc-results.json'), JSON.stringify(results, null, 2) + '\n');
}
console.log(JSON.stringify(results, null, 2));
if (results.some(result => !result.passed)) throw new Error('Foundation GC tests failed');
