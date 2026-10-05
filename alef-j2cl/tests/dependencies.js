goog.module('alefj2cl.dependencies');
const Extensions = goog.require('extensionsj2cl.RuntimeContracts');
const Jre = goog.require('alefj2cl.JreContracts');
const root = document.createElement('div');
root.id = 'extensions-root';
document.body.appendChild(root);
/** @return {?string} */
globalThis['dependencyResult'] = () => {
  const result = Extensions.m_result__java_lang_String();
  return result == null ? null : String(result);
};
globalThis['jreResult'] = Jre.m_run__java_lang_String();
Extensions.m_start__void();
