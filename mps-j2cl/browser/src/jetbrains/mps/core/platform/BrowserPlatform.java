package jetbrains.mps.core.platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import jetbrains.mps.components.ComponentHost;
import jetbrains.mps.components.ComponentPlugin;
import jetbrains.mps.components.CoreComponent;
import jetbrains.mps.lang.typesystem.runtime.RuleSet;
import jetbrains.mps.smodel.language.LanguageRegistry;
import jetbrains.mps.smodel.language.LanguageRuntime;
import jetbrains.mps.typechecking.backend.TypecheckingFacadeComponent;
import jetbrains.mps.typechecking.internal.MPSTypechecking;
import jetbrains.mps.typesystem.MPSTypesystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.mps.openapi.module.SModuleReference;

/** Explicit browser composition of the real MPS core and typechecking services. */
public final class BrowserPlatform implements Platform {
  private final List<ComponentPlugin> plugins = new ArrayList<>();
  private final List<LanguageRegistration> languages = new ArrayList<>();
  private LanguageRegistry languageRegistry;
  private boolean disposed;

  public BrowserPlatform() {
    MPSCore core = new MPSCore();
    core.setParentHost(this);
    try {
      install(core);
      languageRegistry = core.getLanguageRegistry();
      MPSTypechecking typechecking = new MPSTypechecking(core.getLanguageRegistry(), core.findComponent(jetbrains.mps.smodel.MPSModuleRepository.class));
      install(typechecking);
      install(new MPSTypesystem(core.getLanguageRegistry(), core.getClassLoaderManager(), typechecking));
    } catch (RuntimeException | Error failure) {
      dispose();
      throw failure;
    }
  }

  /** Register compiled language code and the rule classes owned by this language. */
  public void registerLanguage(LanguageRuntime language, Class<?>... ruleClasses) {
    registerLanguage(language, ClassLoader.getSystemClassLoader(), Collections.emptyList(), Collections.emptyList(), ruleClasses);
  }

  public void registerLanguage(LanguageRuntime language, ClassLoader loader,
                               Collection<SModuleReference> runtimeModules,
                               Collection<SModuleReference> generationTargets, Class<?>... ruleClasses) {
    if (disposed) throw new IllegalStateException("Browser platform is disposed");
    if (language == null || ruleClasses == null) throw new NullPointerException();
    // Reject an existing deployment before touching its class-identity registrations.
    // Otherwise a failed duplicate deployment could roll back the live runtime's rules.
    if (languageRegistry.getLanguage(language.getId()) != null
        || languageRegistry.getLanguage(language.getNamespace()) != null) {
      throw new IllegalStateException("Language is already deployed: " + language.getNamespace());
    }
    for (LanguageRegistration registration : languages) {
      if (registration.language == language) throw new IllegalArgumentException("Language is already registered");
    }
    List<Class<?>> registered = new ArrayList<>();
    try {
      for (Class<?> rule : ruleClasses) {
        RuleSet.registerRuleNamespace(rule, language.getNamespace());
        registered.add(rule);
      }
      languageRegistry.registerCompiledLanguage(language, loader, runtimeModules, generationTargets);
      languages.add(new LanguageRegistration(language, registered));
    } catch (RuntimeException | Error failure) {
      registered.forEach(RuleSet::unregisterRuleNamespace);
      throw failure;
    }
  }

  /** Extended languages are registered first, and unregistered after their dependants. */
  public void unregisterLanguage(LanguageRuntime language) {
    if (disposed) throw new IllegalStateException("Browser platform is disposed");
    for (int i = 0; i < languages.size(); i++) {
      LanguageRegistration registration = languages.get(i);
      if (registration.language != language) continue;
      if (languageRegistry.getLanguage(language.getId()) == language) {
        languageRegistry.unregisterCompiledLanguage(language);
      }
      // An editor/deployment listener may still own a removal tracking token.
      // Keep namespace ownership until final disposal has actually happened.
      if (languageRegistry.getLanguage(language.getId()) != language) {
        registration.rules.forEach(RuleSet::unregisterRuleNamespace);
        languages.remove(i);
      }
      return;
    }
    throw new IllegalArgumentException("Language is not owned by this browser platform");
  }

  @Override
  public void install(ComponentPlugin plugin) {
    if (disposed) throw new IllegalStateException("Browser platform is disposed");
    if (plugin == null) throw new NullPointerException("plugin");
    if (plugins.contains(plugin)) throw new IllegalArgumentException("Plugin is already installed");
    // A plugin may consult services installed earlier while it initializes.
    plugin.init();
    plugins.add(plugin);
  }

  @Override
  public <T extends CoreComponent> T findComponent(@NotNull Class<T> type) {
    if (disposed) throw new IllegalStateException("Browser platform is disposed");
    for (int i = plugins.size() - 1; i >= 0; i--) {
      ComponentPlugin plugin = plugins.get(i);
      if (plugin instanceof ComponentHost) {
        T value = ((ComponentHost) plugin).findComponent(type);
        if (value != null) return value;
      }
    }
    return null;
  }

  @Override
  public void dispose() {
    if (disposed) return;
    TypecheckingFacadeComponent facade = findComponent(TypecheckingFacadeComponent.class);
    if (facade != null) facade.dispose();
    // Drop sessions/caches before removing their rule namespace registrations.
    for (int i = languages.size() - 1; i >= 0; i--) unregisterLanguage(languages.get(i).language);
    if (!languages.isEmpty()) {
      throw new IllegalStateException("Release editor deployment tracking tokens before disposing the platform");
    }
    RuntimeException first = null;
    // Keep dependencies available while their dependants are being disposed.
    while (!plugins.isEmpty()) {
      ComponentPlugin plugin = plugins.remove(plugins.size() - 1);
      try { plugin.dispose(); }
      catch (RuntimeException failure) { if (first == null) first = failure; }
    }
    disposed = true;
    if (first != null) throw first;
  }

  private static final class LanguageRegistration {
    private final LanguageRuntime language;
    private final List<Class<?>> rules;
    private LanguageRegistration(LanguageRuntime language, List<Class<?>> rules) {
      this.language = language;
      this.rules = new ArrayList<>(rules);
    }
  }
}
