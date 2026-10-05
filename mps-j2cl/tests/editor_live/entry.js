goog.module('mpsj2cl.editorlive.entry');
const Test = goog.require('mpsj2cl.editorlive.EditorLiveTest');
const root = document.createElement('div');
root.id = 'mps-editor-root';
root.style.width = '900px';
root.style.height = '400px';
document.body.appendChild(root);
/** @return {void} */
globalThis['prepareEditorKeyboard'] = () => Test.m_prepareKeyboard__void();
/** @return {void} */
globalThis['prepareEditorIme'] = () => Test.m_prepareIme__void();
/** @return {void} */
globalThis['showEditorDiagnostic'] = () => Test.m_showDiagnostic__void();
/** @return {string} */
globalThis['editorKeyboardState'] = () => {
  const state = Test.m_getKeyboardState__java_lang_String();
  return state == null ? 'null' : String(state);
};
/** @return {string} */
globalThis['editorModelValue'] = () => String(Test.m_getValue__java_lang_String());
/** @return {string} */
globalThis['editorPropertyBounds'] = () => String(Test.m_getPropertyScreenBounds__java_lang_String());
/** @return {number} */
globalThis['editorAutomaticErrors'] = () => Test.m_getAutomaticErrorCount__int();
/** @return {string} */
globalThis['editorProgress'] = () => String(Test.m_getProgress__java_lang_String());
/** @return {number} */
globalThis['editorIntentionQueries'] = () => Test.m_getIntentionQueryCount__int();
/** @return {number} */
globalThis['editorIntentionBulbs'] = () => Test.m_getIntentionBulbCount__int();
/** @return {void} */
globalThis['disposeEditorLive'] = () => Test.m_dispose__void();
const timer = setInterval(() => {
  const result = Test.m_getResult__java_lang_String();
  // J2CL nullable fields can cross the JS boundary as undefined before the
  // queued Swing startup completes; both values mean that no result is ready.
  if (result != null) {
    globalThis['editorLiveResult'] = result;
    clearInterval(timer);
  }
}, 1);
Test.m_start__void();
