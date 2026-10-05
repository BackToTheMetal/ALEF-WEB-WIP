goog.module('alefj2cl.scrollbarPaint');
const Test = goog.require('alefj2cl.ScrollBarPaintContracts');
const root = document.createElement('div');
root.id = 'scrollbar-root';
document.body.appendChild(root);
/** @return {?string} */
globalThis['scrollbarPaintResult'] = () => {
  const result = Test.m_result__java_lang_String();
  return result == null ? null : String(result);
};
Test.m_start__void();
