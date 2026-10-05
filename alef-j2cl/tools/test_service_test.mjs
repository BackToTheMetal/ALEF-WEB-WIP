import {createRequire} from 'node:module';
import path from 'node:path';
import fs from 'node:fs';
import assert from 'node:assert/strict';
import crypto from 'node:crypto';

const root = path.resolve(import.meta.dirname, '..');
const require = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const engines = require('playwright');
const url = process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8812/';
const variant = process.env.ALEF_EXAMPLE_VARIANT || 'development';
assert.ok(['development', 'optimized'].includes(variant));
const directory = path.join(root, variant === 'optimized' ? 'out/service-test-optimized' : 'out/service-test');
const hash = crypto.createHash('sha256');
for await (const chunk of fs.createReadStream(path.join(root, 'bazel-bin/editor_example_browser' + (variant === 'optimized' ? '' : '_dev') + '.js'))) hash.update(chunk);
const sha256 = hash.digest('hex');
fs.mkdirSync(directory, {recursive: true});
for (const name of (process.env.ALEF_BROWSERS || 'chromium').split(',')) {
  const browser = await engines[name].launch({headless: true});
  const page = await browser.newPage({viewport: {width: 1600, height: 1150}});
  const errors = [], checks = [], messages = [];
  page.on('pageerror', error => errors.push(String(error)));
  page.on('console', message => {
    if (message.type() === 'error') errors.push(message.text());
    if (messages.length < 200) messages.push(message.type() + ': ' + message.text());
  });
  const report = {browser: name, variant, sha256, url, checks, errors, messages};
  const value = (i, v) => page.waitForFunction(([i, v]) => alefExample.value(i) === v, [i, v]);
  const open = async index => {
    const selected = await page.evaluate(index => alefExample.projectTree().models.flatMap(m => m.roots).find(r => r.concept === ['ObjectModel','Regelgroep','Service','ServiceTestSet'][index]), index);
    assert.ok(selected, 'Existing project root');
    await page.locator('mps-project-view').getByRole('treeitem', {name:selected.name,exact:true}).click();
  };
  const edit = async (index, text) => {
    await open(index);
    await page.evaluate(index => alefExample.focus(index), index);
    await page.waitForFunction(() => document.activeElement?.getAttribute('aria-label') === 'Editor text input');
    await page.keyboard.type(text);
  };
  const runTest = async expected => {
    await open(3);
    // Invoke the real generated Testspraak interpreter through the observation
    // bridge; the current IDE does not contain the former demo toolbar.
    await page.evaluate(() => alefExample.runTest());
    await page.waitForFunction(() => alefExample.testResult() !== 'Running…', null, {timeout: 60000});
    const result = await page.evaluate(() => alefExample.testResult());
    assert.equal(result, expected);
    assert.ok((await page.evaluate(() => alefExample.editorText(3))).includes(expected === 'PASS' ? '1 geslaagd' : '1 gefaald'), 'Generated Testspraak result summary');
    if (expected === 'FAIL') {
      const details = await page.evaluate(() => alefExample.testFailure());
      assert.ok(details.includes('<resultaatcode>=1'), 'A failed expectation must still execute the service successfully: ' + details);
    }
    if(process.env.ALEF_EXPORT_NATIVE_PARITY==='1'){
      const fixture=await page.evaluate(()=>({models:alefExample.projectTree().models.map(model=>({...model,roots:model.roots.map(root=>alefExample.rootSnapshot(root.id))})),diagnostics:[0,1,2,3].map(i=>alefExample.diagnostics(i)),types:[0,1,2,3].map(i=>alefExample.type(i))}));
      report.nativeCases??=[];const index=report.nativeCases.length;report.nativeCases.push({index,expected});
      fs.writeFileSync(path.join(directory,`${name}-native-case-${index}.json`),JSON.stringify({kind:'EXISTING_BROWSER_ALLOWANCE_EXAMPLE',expected,...fixture},null,2));
    }
  };
  try {
    await page.goto(url, {waitUntil: 'load', timeout: 90000});
    await page.waitForFunction(() => alefExample.status() === 'READY' || alefExample.status().startsWith('FAIL:'), null, {timeout: 60000});
    assert.equal(await page.evaluate(() => alefExample.status()), 'READY');
    await open(2);
    await value(2, 'BerekenToeslag');
    await value(3, '21');
    report.outlines = await page.evaluate(() => [alefExample.outline(2), alefExample.outline(3)]);
    for (const text of ['Service name=Toeslagservice', 'Entrypoint name=BerekenToeslag', 'set->Regelgroep', 'object->ObjectType', 'attr->Attribuut']) assert.ok(report.outlines[0].includes(text), text);
    for (const text of ['ServiceTestSet', 'entrypoint->Entrypoint', 'ServiceTest name=Volwassene ontvangt 125', 'waarde=21', 'waarde=125', 'veld->DirectInvoerAttribuut', 'veld->DirectUitvoerAttribuut']) assert.ok(report.outlines[1].includes(text), text);
    report.texts = await page.evaluate(() => [alefExample.editorText(2), alefExample.editorText(3)]);
    assert.ok(report.texts[0].includes('BerekenToeslag'));
    assert.ok(report.texts[1].includes('21') && report.texts[1].includes('125'));
    assert.deepEqual(await page.evaluate(() => [alefExample.type(2), alefExample.type(3)]), ['NumeriekType', 'NumeriekType']);
    report.diagnostics = await page.evaluate(() => [alefExample.diagnostics(2), alefExample.diagnostics(3)]);
    assert.ok(report.diagnostics.every(text => !text.includes('ERROR:')), JSON.stringify(report.diagnostics));
    checks.push('generated service and test editors with resolved cross-language references');
    await page.screenshot({path: path.join(directory, `${name}-editors.png`), fullPage: true});
    await runTest('PASS');
    checks.push('age 21 produces allowance 125 through the actual service/test interpreters');
    await edit(2, 'BerekenHulp');
    await value(2, 'BerekenHulp');
    assert.ok((await page.evaluate(() => alefExample.editorText(3))).includes('BerekenHulp'));
    await page.locator('#undo').click();
    await value(2, 'BerekenToeslag');
    checks.push('service rename updates the test reference and can be undone');
    await edit(3, '17');
    await value(3, '17');
    await runTest('FAIL');
    checks.push('age below the rule threshold fails the expected allowance');
    await edit(3, '21');
    await value(3, '21');
    await runTest('PASS');
    checks.push('rerunning after input correction reevaluates the model');
    await edit(1, '200');
    await value(1, '200');
    await runTest('FAIL');
    checks.push('editing Regelspraak changes the service result');
    await page.locator('#undo').click();
    await value(1, '100');
    await runTest('PASS');
    checks.push('undoing a rule edit restores the service result');
    await page.screenshot({path: path.join(directory, `${name}-passed.png`), fullPage: true});
    assert.deepEqual(errors, []);
    report.passed = true;
  } catch (error) {
    report.passed = false;
    report.failure = String(error);
    report.status = await page.evaluate(() => globalThis.alefExample?.status()).catch(() => null);
    report.testFailure = await page.evaluate(() => globalThis.alefExample?.testFailure()).catch(() => null);
    report.values = await page.evaluate(() => [0, 1, 2, 3].map(i => alefExample.value(i))).catch(() => null);
    report.diagnostics = await page.evaluate(() => [alefExample.diagnostics(2), alefExample.diagnostics(3)]).catch(() => null);
    await page.screenshot({path: path.join(directory, `${name}-failure.png`), fullPage: true}).catch(() => {});
    process.exitCode = 1;
  } finally {
    fs.writeFileSync(path.join(directory, `${name}.json`), JSON.stringify(report, null, 2));
    console.log(JSON.stringify(report, null, 2));
    await browser.close();
  }
}
