goog.module('alefj2cl.languages');
const Test = goog.require('alefj2cl.LanguageContracts');
const root = document.createElement('div');
root.id = 'alef-root';
document.body.appendChild(root);
/** @return {?string} */
globalThis['languageResult'] = () => {
  const result = Test.m_result__java_lang_String();
  return result == null ? null : String(result);
};
Test.m_start__void();
