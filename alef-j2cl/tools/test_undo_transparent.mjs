import {createRequire} from 'node:module';
import path from 'node:path';
import assert from 'node:assert/strict';

// The development bundle exposes the actual compatibility classes for this
// isolated history contract; the optimized UI is covered by test_create_root.
const root = path.resolve(import.meta.dirname, '..');
const {chromium} = createRequire(path.join(root, '../org-jdom-j2cl/package.json'))('playwright');
const browser = await chromium.launch({headless: true});
try {
  const page = await browser.newPage();
  const errors = [];
  page.on('pageerror', error => errors.push(String(error)));
  await page.goto(process.env.ALEF_EXAMPLE_URL || 'http://127.0.0.1:8812/');
  await page.waitForFunction(() => window.alefExample?.status() === 'READY');
  const actual = await page.evaluate(() => {
    const history = goog.module.get('com.intellij.openapi.command.undo.UndoManager').$create__();
    const processor = goog.module.get('com.intellij.openapi.command.CommandProcessor').m_getInstance__com_intellij_openapi_command_CommandProcessor();
    const Runnable = goog.module.get('java.lang.Runnable');
    let value = 0;
    const change = delta => {
      value += delta;
      history.m_undoableActionPerformed__com_intellij_openapi_command_undo_UndoableAction__void({
        $implements__com_intellij_openapi_command_undo_UndoableAction: true,
        m_undo__void: () => { value -= delta; },
        m_redo__void: () => { value += delta; },
      });
    };
    const correction = delta => processor.m_runUndoTransparentAction__java_lang_Runnable__void(Runnable.$adapt(() => change(delta)));
    const undo = () => history.m_undo__void();
    const redo = () => history.m_redo__void();
    const snapshot = () => ({value, undo: history.m_isUndoAvailable__com_intellij_openapi_fileEditor_FileEditor__boolean(null),
      redo: history.m_isRedoAvailable__com_intellij_openapi_fileEditor_FileEditor__boolean(null)});
    const result = [];
    correction(10); result.push(snapshot()); // Initial corrections are baseline.
    change(1); correction(2); result.push(snapshot());
    undo(); result.push(snapshot()); // One undo reverses the edit and its correction.
    redo(); result.push(snapshot());
    change(4); undo(); correction(8); result.push(snapshot()); // Keep redo available.
    redo(); result.push(snapshot());
    undo(); undo(); result.push(snapshot());
    history.m_dispose__void();
    return result;
  });
  assert.deepEqual(actual, [
    {value: 10, undo: false, redo: false},
    {value: 13, undo: true, redo: false},
    {value: 10, undo: false, redo: true},
    {value: 13, undo: true, redo: false},
    {value: 21, undo: true, redo: true},
    {value: 25, undo: true, redo: false},
    {value: 10, undo: false, redo: true},
  ]);
  assert.deepEqual(errors, []);
  console.log(JSON.stringify({passed: true, checks: 'transparent baseline, grouped corrections, undo/redo ordering and redo preservation', errors}));
} finally { await browser.close(); }
