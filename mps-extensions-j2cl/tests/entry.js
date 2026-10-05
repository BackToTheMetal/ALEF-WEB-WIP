goog.module('extensionsj2cl.entry');
const Test = goog.require('extensionsj2cl.RuntimeContracts');
const root = document.createElement('div');
root.id = 'extensions-root';
document.body.appendChild(root);
/** @return {?string} */
globalThis['extensionResult'] = () => {
  const result = Test.m_result__java_lang_String();
  return result == null ? null : String(result);
};
Test.m_start__void();
