goog.module('alefj2cl.listSelection');
const Test = goog.require('alefj2cl.ListSelectionContracts');
const root = document.createElement('div');
root.id = 'list-root';
document.body.appendChild(root);
/** @return {?string} */
globalThis['listSelectionResult'] = () => {
  const result = Test.m_result__java_lang_String();
  return result == null ? null : String(result);
};
Test.m_start__void();
