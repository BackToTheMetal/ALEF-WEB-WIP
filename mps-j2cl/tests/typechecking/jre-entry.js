goog.module('mpsj2cl.typechecking.jreEntry');
const Test = goog.require('mpsj2cl.typechecking.FoundationJreTest');
/** @return {void} */
globalThis['prepareFoundationGc'] = () => Test.m_prepareGc__void();
/** @return {string} */
globalThis['pollFoundationGc'] = () => String(Test.m_pollGc__java_lang_String());
/** @return {void} */
globalThis['prepareIteratorGc'] = () => Test.m_prepareIteratorGc__void();
/** @return {string} */
globalThis['advanceIteratorGc'] = () => String(Test.m_advanceIteratorGc__java_lang_String());
/** @return {string} */
globalThis['removeIteratorGc'] = () => String(Test.m_removeIteratorGc__java_lang_String());
/** @return {string} */
globalThis['pollIteratorGc'] = () => String(Test.m_pollIteratorGc__java_lang_String());
globalThis['foundationJreResult'] = Test.m_run__java_lang_String();
