import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import {createRequire} from 'node:module';

const root = path.resolve(import.meta.dirname, '..');
const siblingRequire = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const {chromium, firefox, webkit} = siblingRequire('playwright');
const builds = process.argv.slice(2);
if (!builds.length) builds.push('dynamic_components_browser_dev', 'dynamic_components_browser');
const results = [];
try {
  for (const [engine, type] of Object.entries({chromium, firefox, webkit})) {
    const browser = await type.launch({headless: true});
    try {
      for (const build of builds) {
        const source = fs.readFileSync(path.join(root, 'bazel-bin', build + '.js'), 'utf8');
        const page = await browser.newPage();
        const errors = [];
        page.on('pageerror', error => errors.push(String(error)));
        const result = {engine, build, passed: false,
          sha256: crypto.createHash('sha256').update(source).digest('hex'), errors};
        try {
          await page.addScriptTag({content: source});
          Object.assign(result, await page.evaluate(() => ({
            lifecycle: globalThis.dynamicComponentsResult,
            boundaries: globalThis.dynamicComponentsBoundaryResult,
          })));
          if (result.lifecycle !== 'PASS' || result.boundaries !== 'PASS') {
            throw new Error('Dynamic component lifecycle or shutdown boundary failed');
          }
          if (engine === 'chromium') {
            // Each evaluate/CDP call is a separate job. WeakRef targets remain
            // alive through the end of the job that constructs or derefs them.
            await page.evaluate(() => globalThis.prepareDynamicGc());
            const session = await page.context().newCDPSession(page);
            await session.send('HeapProfiler.enable');
            result.gcAttempts = 0;
            result.collection = 'PENDING';
            while (result.collection === 'PENDING' && result.gcAttempts < 10) {
              await session.send('HeapProfiler.collectGarbage');
              result.gcAttempts++;
              result.collection = await page.evaluate(() => globalThis.pollDynamicGc());
            }
            if (result.collection !== 'COLLECTED') throw new Error('Abandoned token/value cycles remained reachable');
            result.afterCollection = await page.evaluate(() => globalThis.finishDynamicGc());
            if (result.afterCollection !== 'PASS') throw new Error('Factory/component ownership changed after collection');
            await session.detach();
          } else {
            result.collection = 'Not forced: lifecycle checked; GC instrumentation uses Chromium CDP';
          }
          result.passed = errors.length === 0;
        } catch (error) {
          errors.push(String(error.stack ?? error));
        } finally {
          results.push(result);
          console.log(engine + '/' + build + ': ' + (result.passed ? 'PASS' : 'FAIL'));
          if (!result.passed) console.error(errors.join('\n'));
          await page.close();
        }
      }
    } finally {
      await browser.close();
    }
  }
} finally {
  fs.mkdirSync(path.join(root, 'out'), {recursive: true});
  fs.writeFileSync(path.join(root, 'out/dynamic-components-results.json'), JSON.stringify(results, null, 2) + '\n');
}
if (results.some(result => !result.passed)) throw new Error('Dynamic component browser verification failed');
