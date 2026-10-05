goog.module('mpsj2cl.dynamiccomponents.entry');
const Gc = goog.require('mpsj2cl.dynamiccomponents.DynamicComponentGcTest');
const Lifecycle = goog.require('mpsj2cl.dynamiccomponents.DynamicComponentLifecycleTest');

globalThis['dynamicComponentsResult'] = Lifecycle.m_run__java_lang_String();
globalThis['dynamicComponentsBoundaryResult'] = Lifecycle.m_runBrowserBoundaries__java_lang_String();
/** @return {void} */
function prepareDynamicGc() { Gc.m_prepare__void(); }
/** @return {?string} */
function pollDynamicGc() { return Gc.m_poll__java_lang_String(); }
/** @return {?string} */
function finishDynamicGc() { return Gc.m_finish__java_lang_String(); }
globalThis['prepareDynamicGc'] = prepareDynamicGc;
globalThis['pollDynamicGc'] = pollDynamicGc;
globalThis['finishDynamicGc'] = finishDynamicGc;
