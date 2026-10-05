goog.module('mpsj2cl.typechecking.entry');
const Test = goog.require('mpsj2cl.typechecking.TypecheckingFoundationTest');
const originalNow = Date.now;
const tick = originalNow();
/** @return {number} */
Date.now = () => tick;
try {
  globalThis['typecheckingFoundationResult'] = Test.m_run__java_lang_String();
} finally {
  Date.now = originalNow;
}
