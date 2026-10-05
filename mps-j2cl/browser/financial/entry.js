goog.module('mpsj2cl.financial.entry');
const Demo = goog.require('mpsj2cl.financial.FinancialDemo');
/** @return {string} */
globalThis['financialStatus'] = () => String(Demo.m_getStatus__java_lang_String());
/** @return {string} */
globalThis['financialName'] = () => String(Demo.m_getName__java_lang_String());
/** @return {string} */
globalThis['financialInterestType'] = () => String(Demo.m_getInterestType__java_lang_String());
/** @return {string} */
globalThis['financialSelectionState'] = () => String(Demo.m_getSelectionState__java_lang_String());
/** @return {void} */
globalThis['financialFocusName'] = () => Demo.m_focusName__void();
/** @return {void} */
globalThis['financialClose'] = () => Demo.m_close__void();
Demo.m_start__void();
