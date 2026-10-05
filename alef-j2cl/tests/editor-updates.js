goog.module('alefj2cl.editorUpdates');
const Probe = goog.require('alefj2cl.EditorUpdateContracts');
globalThis['editorUpdates'] = {
  'status': () => String(Probe.m_status__java_lang_String()),
  'text': /** @param {number} index */ index => String(Probe.m_text__int__java_lang_String(index)),
  'change': /**
   * @param {string} mode
   * @param {string} value
   */ (mode, value) => Probe.m_change__java_lang_String__java_lang_String__void(mode, value),
};
Probe.m_start__void();
