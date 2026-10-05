goog.module('mpsj2cl.utilcompat.entry');

const Test = goog.require('mpsj2cl.utilcompat.UtilCompatibilityTest');

globalThis['utilResult'] = Test.m_run__java_lang_String();
/** @return {void} */
function prepareWeakProbe() { Test.m_prepareWeakProbe__void(); }
/** @return {number} */
function weakProbeSize() { return Test.m_weakProbeSize__int(); }
globalThis['prepareWeakProbe'] = prepareWeakProbe;
globalThis['weakProbeSize'] = weakProbeSize;
