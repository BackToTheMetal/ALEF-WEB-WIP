import fs from 'node:fs';
import path from 'node:path';
import http from 'node:http';
import crypto from 'node:crypto';
import {createRequire} from 'node:module';

const root = path.resolve(import.meta.dirname, '..');
const require = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const {chromium, firefox, webkit} = require('playwright');
const engineFilter = process.argv[2];
const buildFilter = process.argv[3];
const builds = ['editor_live_browser_dev', 'editor_live_browser', 'editor_live_stripped'].filter(name => !buildFilter || name === buildFilter);
const assets = new Map(builds.map(name => ['/' + name + '.js', fs.readFileSync(path.join(root, 'bazel-bin', name + '.js'))]));
const results = [];
const output = path.join(root, 'out/editor-live');
fs.mkdirSync(output, {recursive: true});
// Inspect the actual editor canvas: Java message objects alone do not prove that
// a parent repaint reaches the native canvas or draws/removes the diagnostic.
function diagnosticPixels() {
  let count = 0;
  for (const canvas of document.querySelectorAll('canvas[data-webswing-paint-surface]')) {
    if (canvas.width < 100 || canvas.height < 50) continue;
    const pixels = canvas.getContext('2d').getImageData(0, 0, canvas.width, canvas.height).data;
    for (let i = 0; i < pixels.length; i += 4) {
      if (pixels[i] > 140 && pixels[i + 1] < 110 && pixels[i + 2] < 110 && pixels[i + 3] > 0) count++;
    }
  }
  return count;
}
async function blurEditor(page) {
  await page.evaluate(() => {
    let button = document.getElementById('outside-editor-focus');
    if (!button) {
      button = document.createElement('button'); button.id = 'outside-editor-focus';
      button.textContent = 'Outside editor'; document.body.appendChild(button);
    }
    button.focus();
  });
}
async function prepareIme(page) {
  await page.evaluate(() => globalThis.prepareEditorIme());
  await page.waitForFunction(() => globalThis.editorKeyboardState() === 'IME_READY'
      && document.activeElement?.matches('textarea[data-webswing-input-method]'), null, {timeout: 10000});
}
async function mouseAndHistory(page) {
  await blurEditor(page);
  const before = await page.evaluate(() => globalThis.editorModelValue());
  const bounds = await page.evaluate(() => globalThis.editorPropertyBounds());
  const [x, y, width, height] = bounds.split(',').map(Number);
  if (![x, y, width, height].every(Number.isFinite) || width <= 0 || height <= 0) throw new Error('Invalid property-cell screen bounds: ' + bounds);
  // Hit the actual painted property cell, without selecting it through a Java helper.
  await page.mouse.click(x + width - 1, y + height / 2);
  await page.waitForFunction(() => document.activeElement?.matches('textarea[data-webswing-input-method]'), null, {timeout: 10000});
  await page.keyboard.type('9');
  const edited = before + '9';
  const expectValue = value => page.waitForFunction(expected => globalThis.editorModelValue() === expected, value, {timeout: 10000});
  await expectValue(edited);
  for (const [shortcut, expected] of [
    ['Control+z', before], ['Control+Shift+z', edited],
    ['Control+z', before], ['Control+Shift+z', edited],
    ['Meta+z', before], ['Meta+Shift+z', edited],
    ['Meta+z', before], ['Meta+y', edited], ['Control+z', before],
  ]) {
    await page.keyboard.press(shortcut);
    await expectValue(expected);
  }
  await blurEditor(page);
  // The known native edit is now on redo; an incorrectly global shortcut would
  // visibly replay it. Do not rely on older history having changed this property.
  for (const shortcut of ['Control+Shift+z', 'Meta+Shift+z']) {
    await page.keyboard.press(shortcut);
    await page.waitForTimeout(50);
    if (await page.evaluate(() => globalThis.editorModelValue()) !== before) throw new Error('An unfocused editor consumed redo: ' + shortcut);
  }
  await page.mouse.click(x + width / 2, y + height / 2);
  await page.waitForFunction(() => document.activeElement?.matches('textarea[data-webswing-input-method]'), null, {timeout: 10000});
  await page.keyboard.press('Control+Shift+z');
  await expectValue(edited);
  await blurEditor(page);
  for (const shortcut of ['Control+z', 'Meta+z']) {
    await page.keyboard.press(shortcut);
    await page.waitForTimeout(50);
    if (await page.evaluate(() => globalThis.editorModelValue()) !== edited) throw new Error('An unfocused editor consumed undo: ' + shortcut);
  }
  await page.mouse.click(x + width / 2, y + height / 2);
  await page.waitForFunction(() => document.activeElement?.matches('textarea[data-webswing-input-method]'), null, {timeout: 10000});
  await page.keyboard.press('Control+z');
  await expectValue(before);
  await blurEditor(page);
  return {mouseTyping: true, keyboardUndoRedo: true, shortcutsScopedToEditorFocus: true};
}
const server = http.createServer((request, response) => {
  const asset = assets.get(request.url);
  response.setHeader('Content-Type', asset ? 'application/javascript' : 'text/html');
  response.end(asset ?? '<!doctype html><meta charset="utf-8"><title>MPS live editor acceptance</title><style>body{margin:0;background:#edf0f4}</style>');
});
await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
try {
  for (const [engine, browserType] of Object.entries({chromium, firefox, webkit})) {
    if (engineFilter && engine !== engineFilter) continue;
    const browser = await browserType.launch({headless: true});
    try {
      for (const build of builds) {
        const page = await browser.newPage({viewport: {width: 900, height: 450}});
        const pageErrors = [], consoleErrors = [];
        const started = Date.now();
        const record = {engine, build, passed: false, sha256: crypto.createHash('sha256').update(assets.get('/' + build + '.js')).digest('hex')};
        page.on('pageerror', error => pageErrors.push(String(error.stack ?? error)));
        page.on('console', message => { if (message.type() === 'error') consoleErrors.push(message.text()); });
        try {
          await page.goto(`http://127.0.0.1:${server.address().port}/`);
          await page.addScriptTag({url: '/' + build + '.js'});
          await page.waitForFunction(() => globalThis.editorLiveResult !== undefined, null, {timeout: 30000});
          record.contracts = await page.evaluate(() => globalThis.editorLiveResult);
          if (record.contracts !== 'PASS') throw new Error(record.contracts);
          record.historyInput = await mouseAndHistory(page);
          await page.evaluate(() => globalThis.showEditorDiagnostic());
          await page.waitForFunction(() => globalThis.editorKeyboardState() !== 'null', null, {timeout: 10000});
          const diagnostic = await page.evaluate(() => globalThis.editorKeyboardState());
          if (diagnostic !== 'DIAGNOSTIC_PENDING') throw new Error(diagnostic);
          await page.waitForFunction(() => globalThis.editorAutomaticErrors() === 1, null, {timeout: 15000});
          record.automaticDiagnosticCount = await page.evaluate(() => globalThis.editorAutomaticErrors());
          record.diagnosticValue = await page.evaluate(() => globalThis.editorModelValue());
          if (record.diagnosticValue !== 'invalid') throw new Error('Invalid diagnostic model value was not retained');
          await page.waitForFunction(diagnosticPixels, null, {timeout: 10000});
          record.diagnosticPixels = await page.evaluate(diagnosticPixels);
          // Screenshot awaits a rendering frame; Java checks the actual property-cell message targeting.
          record.diagnosticScreenshot = path.relative(root, path.join(output, `${engine}-${build}-diagnostic.png`));
          await page.screenshot({path: path.join(root, record.diagnosticScreenshot)});
          const queriesBeforeFocus = await page.evaluate(() => globalThis.editorIntentionQueries());
          record.intentionQueriesBeforeFocus = queriesBeforeFocus;
          record.focusBeforeKeyboard = await page.evaluate(() => document.activeElement?.outerHTML.slice(0, 300));
          await page.evaluate(() => globalThis.prepareEditorKeyboard());
          await page.waitForFunction(() => globalThis.editorKeyboardState() !== 'DIAGNOSTIC_PENDING', null, {timeout: 10000});
          const keyboard = await page.evaluate(() => globalThis.editorKeyboardState());
          if (keyboard !== 'READY') throw new Error(keyboard);
          // Native Playwright typing goes through the installed WebAWT DOM key bridge.
          await page.keyboard.type('7');
          await page.waitForFunction(() => globalThis.editorModelValue() === '7', null, {timeout: 10000});
          await page.waitForFunction(() => globalThis.editorAutomaticErrors() === 0, null, {timeout: 15000});
          record.automaticDiagnosticCountAfterTyping = await page.evaluate(() => globalThis.editorAutomaticErrors());
          record.keyboardValue = await page.evaluate(() => globalThis.editorModelValue());
          // Allow the queued repaint to follow the checker result.
          await page.evaluate(() => new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(() => resolve()))));
          record.diagnosticPixelsAfterTyping = await page.evaluate(diagnosticPixels);
          if (record.diagnosticPixelsAfterTyping !== 0) throw new Error('Resolved diagnostic remains painted');
          record.editedScreenshot = path.relative(root, path.join(output, `${engine}-${build}-edited.png`));
          await page.screenshot({path: path.join(root, record.editedScreenshot)});
          await page.waitForFunction(before => globalThis.editorIntentionQueries() > before
              && globalThis.editorIntentionBulbs() === 1, queriesBeforeFocus, {timeout: 10000});
          record.intentionBulbs = await page.evaluate(() => globalThis.editorIntentionBulbs());
          const staleTooltip = await page.evaluate(() => [...document.querySelectorAll('.ws-popup-menu,[role="tooltip"]')]
              .some(element => element.textContent.includes('Expected an integer literal')
                  && element.getBoundingClientRect().width > 0 && element.getBoundingClientRect().height > 0));
          if (staleTooltip) throw new Error('A delayed tooltip still shows the resolved type error');
          record.staleErrorTooltipAbsent = true;
          // Queue a fresh delayed request, then lose focus before its 1000ms deadline.
          await blurEditor(page);
          await prepareIme(page);
          await blurEditor(page);
          const queriesAtBlur = await page.evaluate(() => globalThis.editorIntentionQueries());
          await page.waitForTimeout(1200);
          if (await page.evaluate(() => globalThis.editorIntentionQueries()) !== queriesAtBlur
              || await page.evaluate(() => globalThis.editorIntentionBulbs()) !== 0) throw new Error('Blur did not cancel pending intention lookup');
          record.intentionBlurCancellation = true;
          await prepareIme(page);
          if (engine === 'chromium') {
            // CDP sends native browser IME input to the real focused textarea.
            const session = await page.context().newCDPSession(page);
            try {
              await session.send('Input.imeSetComposition', {text: '8', selectionStart: 1, selectionEnd: 1});
              await page.waitForFunction(() => globalThis.editorModelValue() === '78');
              await session.send('Input.insertText', {text: '8'});
              await page.waitForFunction(() => globalThis.editorModelValue() === '78');
              await session.send('Input.imeSetComposition', {text: '9', selectionStart: 1, selectionEnd: 1});
              await page.waitForFunction(() => globalThis.editorModelValue() === '789');
              await session.send('Input.imeSetComposition', {text: '', selectionStart: 0, selectionEnd: 0});
              await page.waitForFunction(() => globalThis.editorModelValue() === '78');
              record.imeCoverage = 'native Chromium composition commit and cancellation';
            } finally { await session.detach(); }
          } else {
            // Playwright has no native IME driver for these engines. Verify the
            // DOM bridge contract, without describing synthetic events as OS input.
            const compose = (type, data) => page.evaluate(event => {
              document.querySelector('textarea[data-webswing-input-method]').dispatchEvent(
                  new CompositionEvent(event.type, {data: event.data, bubbles: true}));
            }, {type, data});
            await compose('compositionstart', '');
            await compose('compositionupdate', '8');
            await page.waitForFunction(() => globalThis.editorModelValue() === '78');
            await compose('compositionend', '8');
            await compose('compositionstart', '');
            await compose('compositionupdate', '9');
            await page.waitForFunction(() => globalThis.editorModelValue() === '789');
            await compose('compositionend', '');
            await page.waitForFunction(() => globalThis.editorModelValue() === '78');
            record.imeCoverage = 'DOM composition-event commit and cancellation contract';
          }
          record.imeValue = await page.evaluate(() => globalThis.editorModelValue());
          await page.waitForFunction(() => globalThis.editorAutomaticErrors() === 0);
          // Dispose with another delayed intention request outstanding.
          await blurEditor(page);
          await prepareIme(page);
          const queriesAtDisposal = await page.evaluate(() => globalThis.editorIntentionQueries());
          await page.evaluate(() => globalThis.disposeEditorLive());
          await page.waitForFunction(() => globalThis.editorKeyboardState() !== 'IME_READY', null, {timeout: 10000});
          record.disposal = await page.evaluate(() => globalThis.editorKeyboardState());
          if (record.disposal !== 'DISPOSED') throw new Error(record.disposal);
          if (await page.locator('textarea[data-webswing-input-method]').count()) throw new Error('Disposed editor retained an IME proxy');
          await page.waitForTimeout(1200);
          if (await page.evaluate(() => globalThis.editorIntentionQueries()) !== queriesAtDisposal) throw new Error('Disposed editor performed intention lookup');
          record.intentionDisposalCancellation = true;
          record.passed = pageErrors.length === 0 && consoleErrors.length === 0;
          if (!record.passed) throw new Error('Unexpected browser or Java error output: ' + [...pageErrors, ...consoleErrors].join('\n'));
        } catch (error) {
          record.error = String(error.stack ?? error);
          record.progress = await page.evaluate(() => globalThis.editorProgress?.() ?? 'entry did not initialize').catch(() => 'page unavailable');
          record.failureState = await page.evaluate(() => ({value: globalThis.editorModelValue?.(),
            keyboard: globalThis.editorKeyboardState?.(), queries: globalThis.editorIntentionQueries?.(),
            bulbs: globalThis.editorIntentionBulbs?.(), focus: document.activeElement?.outerHTML.slice(0, 500)})).catch(() => null);
          record.failureScreenshot = path.relative(root, path.join(output, `${engine}-${build}-failure.png`));
          await page.screenshot({path: path.join(root, record.failureScreenshot)}).catch(() => {});
        } finally {
          record.pageErrors = pageErrors;
          record.consoleErrors = consoleErrors;
          record.elapsedMs = Date.now() - started;
          results.push(record);
          console.log(`${engine}/${build}: ${record.passed ? 'PASS' : 'FAIL'}`);
          if (!record.passed) console.error(record.error ?? pageErrors.join('\n'));
          await page.close();
        }
      }
    } finally { await browser.close(); }
  }
} finally {
  server.close();
  fs.writeFileSync(path.join(root, 'out/editor-live-browser-results.json'), JSON.stringify(results, null, 2) + '\n');
}
if (!results.length || results.some(result => !result.passed)) throw new Error('Live editor acceptance failed; see out/editor-live-browser-results.json');
