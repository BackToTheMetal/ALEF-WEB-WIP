goog.module('mpsj2cl.deployment.entry');
const Factories = goog.require('jetbrains.mps.smodel.runtime.ModuleRuntimeFactoryTest');
const Loaders = goog.require('jetbrains.mps.classloading.ModuleClassLoaderTest');
const Queries = goog.require('mpsj2cl.queries.QueryMethodsTest');
globalThis['runtimeDetails'] = [Factories.m_run__java_lang_String(), Loaders.m_run__java_lang_String(), Queries.m_run__java_lang_String()];
globalThis['runtimeResult'] = 'PASS';
