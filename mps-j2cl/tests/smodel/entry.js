goog.module('mpsj2cl.smodel.entry');
const IconTest = goog.require('mpsj2cl.resources.IconResourceTest');
const Test = goog.require('jetbrains.mps.smodel.SModelAdaptationTest');
globalThis['smodelDetails'] = [Test.m_run__java_lang_String(), IconTest.m_run__java_lang_String()];
Test.m_startAsync__void();
const interval = globalThis.setInterval(() => {
  if (Test.m_isAsyncDone__boolean()) {
    globalThis.clearInterval(interval);
    globalThis['smodelAsyncDetails'] = Test.m_getAsyncResult__java_lang_String();
    globalThis['smodelResult'] = 'PASS';
  }
}, 10);
