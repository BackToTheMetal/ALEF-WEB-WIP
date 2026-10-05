import fs from 'node:fs';
import path from 'node:path';
import http from 'node:http';
import crypto from 'node:crypto';
import {createRequire} from 'node:module';

const root = path.resolve(import.meta.dirname, '..');
const siblingRequire = createRequire(path.join(root, '../org-jdom-j2cl/package.json'));
const {chromium, firefox, webkit} = siblingRequire('playwright');
const mode = process.argv[2] ?? 'class';
const selectedEngine = process.argv[3];
const definitions = {
  keystroke: {
    builds: ['keystroke_browser_dev', 'keystroke_browser'],
    result: 'keyStrokeResult', error: 'keyStrokeError',
    expected: fs.readFileSync(path.join(root, '../javax-swing-j2cl/src/test/runtime/keystroke/jvm-expected.txt'), 'utf8').trim(),
  },
  swing_editor_controls: {
    builds: ['swing_editor_controls_browser_dev', 'swing_editor_controls_browser'],
    result: 'swingEditorControlsResult', error: 'swingEditorControlsError',
    expected: fs.readFileSync(path.join(root, '../javax-swing-j2cl/src/test/runtime/swing_editor/controls-jvm-expected.txt'), 'utf8').trim(),
  },
  intellij_contracts: {
    builds: ['intellij_contracts_browser_dev', 'intellij_contracts_browser'],
    result: 'getIntellijContractsResult', resultFunction: true, expected: 'PASS:', expectedPrefix: true,
    body: '<div id="intellij-contracts-root"></div>',
  },
  swing_editor_native: {
    builds: ['swing_editor_native_browser_dev', 'swing_editor_native_browser'],
    result: 'swingEditorNativeResult', error: 'swingEditorNativeError', expected: 'PASS',
  },
  swing_editor: {
    builds: ['swing_editor_browser_dev', 'swing_editor_browser'],
    result: 'swingEditorResult', error: 'swingEditorError',
    expected: fs.readFileSync(path.join(root, '../javax-swing-j2cl/src/test/runtime/swing_editor/jvm-expected.txt'), 'utf8').trim(),
    additional: {swingEditorWindowResult: 'PASS'},
  },
  completion_matching: {
    builds: ['completion_matching_browser_dev', 'completion_matching_browser'],
    result: 'completionMatchingResult', error: 'completionMatchingError', expected: 'PASS',
  },
  swing_utilities: {
    builds: ['swing_utilities_browser_dev', 'swing_utilities_browser'],
    result: 'swingUtilitiesResult', error: 'swingUtilitiesError',
    expected: fs.readFileSync(path.join(root, '../javax-swing-j2cl/src/test/runtime/swing_utilities/jvm-expected.txt'), 'utf8').trim(),
    additional: {swingUtilitiesWindowResult: 'PASS'},
  },
  editor_runtime_contracts: {
    builds: ['editor_runtime_contracts_browser_dev', 'editor_runtime_contracts_browser'],
    result: 'editorRuntimeContractsResult', error: 'editorRuntimeContractsError', expected: 'PASS',
  },
  foundation_jre: {
    builds: ['foundation_jre_browser_dev', 'foundation_jre_browser'],
    result: 'foundationJreResult', error: 'foundationJreError', expected: 'PASS',
  },
  typechecking_foundation: {
    builds: ['typechecking_foundation_browser_dev', 'typechecking_foundation_browser', 'typechecking_foundation_stripped'],
    result: 'typecheckingFoundationResult', error: 'typecheckingFoundationError', expected: 'PASS',
  },
  browser_platform: {
    builds: ['browser_platform_browser_dev', 'browser_platform_browser', 'browser_platform_stripped'],
    result: 'browserPlatformResult', error: 'browserPlatformError', expected: 'PASS',
  },
  editor_utilities: {
    builds: ['editor_utilities_browser_dev', 'editor_utilities_browser'],
    result: 'editorUtilitiesResult', error: 'editorUtilitiesError',
    expected: fs.readFileSync(path.join(root, 'tests/editor_utilities/jvm-expected.txt'), 'utf8').trim(),
  },
  kernel_utilities: {
    builds: ['kernel_utilities_browser_dev', 'kernel_utilities_browser'],
    result: 'kernelUtilitiesResult', error: 'kernelUtilitiesError',
    expected: fs.readFileSync(path.join(root, 'tests/kernel_utilities/jvm-expected.txt'), 'utf8').trim(),
  },
  modifiers: {
    builds: ['modifier_browser_dev', 'modifier_browser'],
    result: 'modifierResult', error: 'modifierError',
    expected: 'Modifier: all 65536 flag combinations, high/negative bits, rendering order and declaration masks match the JVM',
  },
  regex: {
    builds: ['regex_browser_dev', 'regex_browser'],
    result: 'regexResult', error: 'regexError',
    expected: fs.readFileSync(path.join(root, 'tests/regex/jvm-expected.txt'), 'utf8').trim(),
    additional: {regexUnsupportedResult: 'PASS'},
  },
  class: {
    builds: ['class_support_browser_dev', 'class_support_browser', 'class_support_stripped'],
    result: 'classSupportResult', error: 'classSupportError',
    expected: fs.readFileSync(path.join(root, 'tests/class_support/jvm-expected.txt'), 'utf8').trim(),
  },
  core: {
    builds: ['core_browser_dev', 'core_browser'],
    result: 'coreResult', error: 'coreError', expected: 'PASS',
  },
  jdom: {
    builds: ['jdom_browser_dev', 'jdom_browser'],
    result: 'jdomResult', error: 'jdomError', expected: 'PASS',
  },
  openapi: {
    builds: ['openapi_browser_dev', 'openapi_browser'],
    result: 'openapiResult', error: 'openapiError',
    expected: fs.readFileSync(path.join(root, 'tests/openapi/jvm-expected.txt'), 'utf8').trim(),
  },
  classloader: {
    builds: ['classloader_browser_dev', 'classloader_browser'],
    result: 'classloaderResult', error: 'classloaderError', expected: 'PASS',
  },
  smodel: {
    builds: ['smodel_browser_dev', 'smodel_browser'],
    result: 'smodelResult', error: 'smodelError', expected: 'PASS',
  },
  util: {
    builds: ['util_browser_dev', 'util_browser'],
    result: 'utilResult', error: 'utilError', expected: 'PASS',
  },
  api_basics: {
    builds: ['api_basics_browser_dev', 'api_basics_browser'],
    result: 'apiBasicsResult', error: 'apiBasicsError',
    expected: fs.readFileSync(path.join(root, 'tests/api_basics/jvm-expected.txt'), 'utf8').trim(),
  },
  vfs: {
    builds: ['vfs_browser_dev', 'vfs_browser'],
    result: 'vfsResult', error: 'vfsError',
    expected: fs.readFileSync(path.join(root, 'tests/vfs/jvm-expected.txt'), 'utf8').trim(),
  },
  runtime: {
    builds: ['runtime_browser_dev', 'runtime_browser'],
    result: 'runtimeResult', error: 'runtimeError', expected: 'PASS',
  },
  runtime_support: {
    builds: ['runtime_support_browser_dev', 'runtime_support_browser', 'runtime_support_stripped'],
    result: 'runtimeResult', error: 'runtimeError',
    expected: fs.readFileSync(path.join(root, 'tests/runtime_support/jvm-expected.txt'), 'utf8').trim(),
  },
  formatting: {
    builds: ['formatting_browser_dev', 'formatting_browser'],
    result: 'formattingResult', error: 'formattingError',
    expected: 'Formatting: 58 JVM golden cases passed through helper, String.format and formatted; unsupported features fail explicitly',
  },
};
if (mode === '--list-modes') {
  console.log(JSON.stringify(Object.keys(definitions)));
  process.exit(0);
}
if (!(mode in definitions)) throw new Error('Unknown test mode: ' + mode);
const definition = definitions[mode];
const assets = new Map(definition.builds.map(name => {
  const file = path.join(root, 'bazel-bin', name + '.js');
  if (!fs.existsSync(file)) throw new Error('Missing built JavaScript: ' + file);
  return ['/' + name + '.js', fs.readFileSync(file)];
}));
const server = http.createServer((request, response) => {
  const asset = assets.get(request.url);
  response.setHeader('Content-Type', asset ? 'application/javascript' : 'text/html');
  response.end(asset ?? '<!doctype html><meta charset="utf-8"><title>MPS J2CL tests</title>' + (definition.body ?? ''));
});
await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
const results = [];
async function verifyNativeEditor(page) {
  await page.waitForFunction(() => {
    const canvas = document.querySelector('#native-editor canvas[data-webswing-paint-surface]');
    return canvas && canvas.width >= 800 && canvas.height >= 1000 && canvas.getContext('2d').getImageData(10, 10, 1, 1).data[3] === 255;
  }, null, {timeout: 5000});
  const painted = await page.evaluate(() => {
    const host = document.querySelector('#native-editor');
    const canvas = host.querySelector('canvas[data-webswing-paint-surface]');
    return {pixel: Array.from(canvas.getContext('2d').getImageData(10, 10, 1, 1).data),
      first: host.firstElementChild === canvas, pointerEvents: getComputedStyle(canvas).pointerEvents};
  });
  if (painted.pixel.join(',') !== '230,70,40,255' || !painted.first || painted.pointerEvents !== 'none') {
    throw new Error('Paint surface contract: ' + JSON.stringify(painted));
  }
  // Let mount/layout damage settle, then dirty only the external scroll pane.
  await page.evaluate(() => new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve))));
  await page.evaluate(() => globalThis.swingEditorNativeRepaintParent(false));
  await page.waitForFunction(() => {
    const canvas = document.querySelector('#native-editor canvas[data-webswing-paint-surface]');
    return Array.from(canvas.getContext('2d').getImageData(10, 10, 1, 1).data).join(',') === '40,200,80,255';
  });
  if (await page.evaluate(() => globalThis.swingEditorNativePaintCount()) !== 1) throw new Error('External repaint painted its delegated canvas more than once');
  await page.evaluate(() => globalThis.swingEditorNativeRepaintParent(true));
  await page.waitForFunction(() => {
    const canvas = document.querySelector('#native-editor canvas[data-webswing-paint-surface]');
    return Array.from(canvas.getContext('2d').getImageData(10, 10, 1, 1).data).join(',') === '40,90,220,255';
  });
  const repaintEvidence = await page.evaluate(() => {
    const context = document.querySelector('#native-editor canvas[data-webswing-paint-surface]').getContext('2d');
    return {inside: Array.from(context.getImageData(10, 10, 1, 1).data), outside: Array.from(context.getImageData(25, 10, 1, 1).data), count: globalThis.swingEditorNativePaintCount()};
  });
  if (repaintEvidence.outside.join(',') !== '40,200,80,255' || repaintEvidence.count !== 1) throw new Error('External repaint ignored damage clipping or duplicated painting: ' + JSON.stringify(repaintEvidence));
  await page.evaluate(() => globalThis.swingEditorNativeRepaintDetached());
  await page.waitForFunction(() => {
    const context = document.querySelector('#native-editor canvas[data-webswing-paint-surface]').getContext('2d');
    return Array.from(context.getImageData(10, 10, 1, 1).data).join(',') === '175,110,210,255';
  });
  await page.evaluate(() => globalThis.swingEditorNativeRepaintFailure());
  await page.waitForFunction(() => {
    const context = document.querySelector('#native-editor canvas[data-webswing-paint-surface]').getContext('2d');
    return Array.from(context.getImageData(10, 10, 1, 1).data).join(',') === '150,70,110,255';
  });
  repaintEvidence.detachedValidationSkipped = true;
  repaintEvidence.validationFailurePreservedWorkAndRescheduled = true;
  const rendererEvidence = await page.locator('#native-completions [role=option]').evaluateAll(options => options.map(option => {
    const canvas = option.querySelector('canvas[data-webswing-renderer]');
    return {label: option.getAttribute('aria-label'), pixel: canvas ? Array.from(canvas.getContext('2d').getImageData(2, 2, 1, 1).data) : null};
  }));
  if (rendererEvidence.length !== 3 || rendererEvidence[0].label !== 'alpha details alpha'
      || rendererEvidence[1].label !== 'beta details beta' || rendererEvidence[0].pixel?.join(',') !== '200,40,30,255'
      || rendererEvidence[1].pixel?.join(',') !== '30,180,50,255') throw new Error('Reusable completion renderer snapshot: ' + JSON.stringify(rendererEvidence));
  await page.locator('#native-completions [role=option]').nth(1).click();
  if (await page.evaluate(() => globalThis.swingEditorNativeCompletionIndex()) !== 1) throw new Error('Renderer canvas blocked native selection');
  await page.locator('#native-viewport').evaluate(element => { element.scrollLeft = 35; element.scrollTop = 75; });
  await page.waitForFunction(() => globalThis.swingEditorNativePosition() === '35,75,35,75,75', null, {timeout: 5000});
  const geometry = await page.evaluate(() => {
    const viewport = document.querySelector('#native-viewport');
    const view = document.querySelector('#native-editor').getBoundingClientRect();
    const bounds = viewport.getBoundingClientRect();
    return {x: Math.round(view.left - bounds.left), y: Math.round(view.top - bounds.top)};
  });
  if (geometry.x !== -35 || geometry.y !== -75) throw new Error('Viewport displaced its view twice: ' + JSON.stringify(geometry));
  const viewportBox = await page.locator('#native-viewport').boundingBox();
  await page.mouse.move(viewportBox.x + viewportBox.width / 2, viewportBox.y + viewportBox.height / 2);
  await page.mouse.wheel(0, 120);
  await page.waitForFunction(() => Number(globalThis.swingEditorNativePosition().split(',')[1]) > 75, null, {timeout: 5000});
  const wheelPosition = await page.evaluate(() => globalThis.swingEditorNativePosition());
  const wheel = wheelPosition.split(',').map(Number);
  if (wheel[1] !== wheel[3] || wheel[1] !== wheel[4]) throw new Error('Wheel/model/header synchronization: ' + wheelPosition);
  await page.locator('#native-viewport').evaluate(element => { element.scrollLeft = 0; element.scrollTop = 0; });
  await page.waitForFunction(() => globalThis.swingEditorNativePosition() === '0,0,0,0,0', null, {timeout: 5000});
  await page.locator('#native-editor-input').fill('native edit');
  if (await page.evaluate(() => globalThis.swingEditorNativeInput()) !== 'native edit') throw new Error('Embedded text input did not update its Java document');
  await page.evaluate(() => globalThis.swingEditorNativeReplace());
  await page.locator('#native-editor-input').fill('rebuilt edit');
  if (await page.evaluate(() => globalThis.swingEditorNativeInput()) !== 'rebuilt edit') throw new Error('Cell rebuild lost native input');
  if (await page.locator('#native-editor canvas[data-webswing-paint-surface]').count() !== 1) throw new Error('removeAll changed canvas ownership');
  await page.evaluate(() => globalThis.swingEditorNativeRemount());
  await page.locator('#native-editor-input').fill('remounted edit');
  if (await page.evaluate(() => globalThis.swingEditorNativeInput()) !== 'remounted edit') throw new Error('Remount lost native input');
  await page.locator('#native-vertical-scrollbar').focus();
  await page.locator('#native-vertical-scrollbar').press('PageDown');
  await page.waitForFunction(() => Number(globalThis.swingEditorNativePosition().split(',')[1]) > 0, null, {timeout: 5000});
  const keyboardPosition = await page.evaluate(() => globalThis.swingEditorNativePosition());
  await page.evaluate(() => globalThis.swingEditorNativeDispose());
  if (await page.locator('[data-webswing-paint-surface], #native-editor-input').count() !== 0) throw new Error('Disposed editor retained DOM content');
  return {painted, repaintEvidence, rendererEvidence, geometry, wheelPosition, keyboardPosition, nativeInputRebuildAndRemount: true, disposed: true};
}
try {
  for (const [engine, type] of Object.entries({chromium, firefox, webkit})) {
    if (selectedEngine && selectedEngine !== engine) continue;
    const browser = await type.launch({headless: true});
    try {
      for (const build of definition.builds) {
        const page = await browser.newPage();
        const errors = [];
        let reportRuntimeError;
        const runtimeError = new Promise(resolve => { reportRuntimeError = resolve; });
        page.on('pageerror', error => {
          errors.push(String(error));
          reportRuntimeError();
        });
        await page.goto(`http://127.0.0.1:${server.address().port}/`);
        const start = Date.now();
        try {
          await page.addScriptTag({url: '/' + build + '.js'});
          await Promise.race([
            page.waitForFunction(({result, error, resultFunction}) => {
              const value = resultFunction ? globalThis[result]?.() : globalThis[result];
              return (value !== undefined && value !== 'RUNNING') || globalThis[error] !== undefined;
            }, definition, {timeout: 15000}),
            runtimeError,
          ]);
        } catch (error) {
          errors.push(String(error));
        }
        const actual = await page.evaluate(({result, error, resultFunction}) => ({
          result: resultFunction ? globalThis[result]?.() : globalThis[result], error: globalThis[error],
        }), definition);
        for (const [name, expectedValue] of Object.entries(definition.additional ?? {})) {
          const extra = await page.evaluate(name => globalThis[name], name);
          if (extra !== expectedValue) errors.push(name + ': ' + extra);
        }
        let nativeEvidence;
        if (mode === 'swing_editor_native' && actual.result === 'PASS' && !errors.length) {
          try { nativeEvidence = await verifyNativeEditor(page); }
          catch (error) { errors.push(String(error)); }
        }
        const normalized = String(actual.result ?? '').replaceAll('\r\n', '\n').trim();
        const expected = definition.expected.replaceAll('\r\n', '\n');
        const passed = (definition.expectedPrefix ? normalized.startsWith(expected) : normalized === expected) && !actual.error && errors.length === 0;
        const result = {
          engine, build, passed, elapsedMs: Date.now() - start,
          sha256: crypto.createHash('sha256').update(assets.get('/' + build + '.js')).digest('hex'),
          output: normalized, error: actual.error, pageErrors: errors,
          ...(nativeEvidence ? {nativeEvidence} : {}),
        };
        results.push(result);
        console.log(`${engine}/${build}: ${passed ? 'PASS' : 'FAIL'}`);
        if (!passed) console.error(actual.error || errors.join('\n') || 'Output differs from the JVM oracle');
        await page.close();
      }
    } finally {
      await browser.close();
    }
  }
  if (!results.length) throw new Error('No browser selected');
  if (results.some(result => !result.passed)) throw new Error('Browser verification failed; see out/' + mode + '-browser-results.json');
} finally {
  server.close();
  fs.mkdirSync(path.join(root, 'out'), {recursive: true});
  fs.writeFileSync(path.join(root, 'out', mode + '-browser-results.json'), JSON.stringify(results, null, 2) + '\n');
}
