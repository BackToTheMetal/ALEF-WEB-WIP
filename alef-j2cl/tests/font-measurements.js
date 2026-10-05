goog.module('alefj2cl.fontMeasurements');
const Probe = goog.require('alefj2cl.FontMeasurementProbe');
globalThis['fontMeasurement'] = (/** string */ font, /** string */ text) =>
    Probe.m_measure__java_lang_String__java_lang_String__arrayOf_double(font, text);
