package mpsj2cl.typechecking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import jetbrains.mps.components.ComponentHost;
import jetbrains.mps.core.platform.BrowserPlatform;
import jetbrains.mps.extapi.module.SRepositoryRegistry;
import jetbrains.mps.lang.typesystem.runtime.RuleSet;
import jetbrains.mps.project.Project;
import jetbrains.mps.project.ProjectRepository;
import jetbrains.mps.smodel.BootstrapLanguages;
import jetbrains.mps.smodel.MPSModuleRepository;
import jetbrains.mps.smodel.adapter.ids.MetaIdFactory;
import jetbrains.mps.smodel.adapter.ids.MetaIdHelper;
import jetbrains.mps.smodel.adapter.ids.SLanguageId;
import jetbrains.mps.smodel.language.LanguageRegistry;
import jetbrains.mps.smodel.language.LanguageRegistryListener;
import jetbrains.mps.smodel.language.LanguageRuntime;
import jetbrains.mps.smodel.runtime.ILanguageAspect;
import jetbrains.mps.smodel.runtime.ModuleDeploymentListener;
import jetbrains.mps.typechecking.TypecheckingFacade;
import jetbrains.mps.typesystem.inference.TypeCheckerHelper;
import jetbrains.mps.util.PathManager;
import org.jetbrains.mps.openapi.language.SLanguage;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.repository.CommandListener;

/** Full core/type-plugin lifecycle and real project repository contracts, without editor UI. */
public final class BrowserPlatformTest {
  public static String run() {
    PathManager.configureHomePath("/mps");
    for (int cycle = 0; cycle < 2; cycle++) runCycle();
    return "PASS";
  }

  private static void runCycle() {
    BrowserPlatform platform = new BrowserPlatform();
    TestProject project = new TestProject(platform);
    LanguageRegistry registry = platform.findComponent(LanguageRegistry.class);
    require(registry != null && registry == LanguageRegistry.getInstance(), "real global language registry");
    require(platform.findComponent(MPSModuleRepository.class) != null, "real global model repository");
    TypecheckingFacade facade = TypecheckingFacade.getFromContext();
    require(facade.getData(TypeCheckerHelper.class) != null, "real legacy typechecking provider/helper");

    int[] commands = {0};
    CommandListener listener = new CommandListener() {
      @Override public void commandStarted() { commands[0]++; }
      @Override public void commandFinished() { commands[0]++; }
    };
    project.getModelAccess().addCommandListener(listener);
    project.getModelAccess().executeCommand(() -> require(project.getModelAccess().canWrite(), "project command owns model write action"));
    require(commands[0] == 2, "real project command start/finish notifications");
    project.getModelAccess().removeCommandListener(listener);
    require(((ProjectRepository) project.getRepository()).getProject() == project, "project repository ownership");

    List<String> events = new ArrayList<>();
    LanguageRegistryListener languageListener = new LanguageRegistryListener() {
      @Override public void afterLanguagesLoaded(Iterable<LanguageRuntime> added) {
        for (LanguageRuntime runtime : added) {
          require(registry.getLanguage(runtime.getId()) == runtime, "load notification sees deployed language");
          events.add("+" + runtime.getNamespace());
        }
      }
      @Override public void beforeLanguagesUnloaded(Iterable<LanguageRuntime> removed) {
        for (LanguageRuntime runtime : removed) {
          require(registry.getLanguage(runtime.getId()) == runtime, "unload notification sees live language");
          events.add("-" + runtime.getNamespace());
        }
      }
    };
    registry.addRegistryListener(languageListener);
    RuntimeLanguage core = new RuntimeLanguage("jetbrains.mps.lang.core", MetaIdHelper.getLanguage(BootstrapLanguages.getLangCore()), null);
    RuntimeLanguage base = new RuntimeLanguage("mpsj2cl.lifecycle.base", MetaIdFactory.langId(90, 1), null);
    RuntimeLanguage child = new RuntimeLanguage("mpsj2cl.lifecycle.child", MetaIdFactory.langId(90, 2), base.getIdentity());
    platform.registerLanguage(core);
    platform.registerLanguage(base, OwnedRule.class);
    try {
      platform.registerLanguage(new RuntimeLanguage(base.getNamespace(), MetaIdFactory.langId(90, 3), null), OwnedRule.class);
      throw new AssertionError("duplicate namespace deployment accepted");
    } catch (IllegalStateException expected) {}
    try {
      RuleSet.registerRuleNamespace(OwnedRule.class, "mpsj2cl.lifecycle.conflict");
      throw new AssertionError("duplicate deployment removed original rule ownership");
    } catch (IllegalStateException expected) {}
    platform.registerLanguage(child);
    require(child.getExtendedLanguages().contains(base), "real language extends closure");
    LifeAspect aspect = child.getAspect(LifeAspect.class);
    require(aspect != null && !aspect.disposed(), "real runtime aspect construction contract");
    try { platform.unregisterLanguage(base); throw new AssertionError("base removed before dependant"); } catch (IllegalStateException expected) {}

    Runnable[] release = {null};
    ModuleDeploymentListener tracker = change -> change.forEachRemoved(module -> {
      if (module.getSourceModule().equals(child.getIdentity().getSourceModuleReference())) release[0] = change.acquireRemovedTrackingLock();
    });
    registry.addRegistryListener(tracker);
    platform.unregisterLanguage(child);
    require(release[0] != null && !aspect.disposed(), "removal tracking token delays aspect disposal");
    release[0].run();
    require(aspect.disposed() && registry.getLanguage(child.getId()) == null, "token release disposes and removes language");
    platform.unregisterLanguage(child); // clear platform ownership after deferred disposal
    registry.removeRegistryListener(tracker);
    project.dispose();
    require(project.isDisposed(), "project disposes repository");
    platform.dispose();
    platform.dispose();
    require(registry.getLanguage(base.getId()) == null && registry.getLanguage(core.getId()) == null, "remaining languages disposed in reverse registration order");
    require(events.size() == 6 && events.get(3).equals("-mpsj2cl.lifecycle.child")
        && events.get(4).equals("-mpsj2cl.lifecycle.base") && events.get(5).equals("-jetbrains.mps.lang.core"), "load/unload event ordering");
    require(LanguageRegistry.getInstance() == null, "global language registry released");
    RuleSet.registerRuleNamespace(OwnedRule.class, "mpsj2cl.lifecycle.afterUnload");
    RuleSet.unregisterRuleNamespace(OwnedRule.class);
    try { TypecheckingFacade.getFromContext(); throw new AssertionError("facade retained after shutdown"); } catch (IllegalStateException expected) {}
    try { facade.getData(TypeCheckerHelper.class); throw new AssertionError("old facade revived"); } catch (IllegalStateException expected) {}
  }

  public interface LifeAspect extends ILanguageAspect { boolean disposed(); }
  private static final class OwnedRule {}
  private static final class TestAspect implements LifeAspect {
    private boolean disposed;
    @Override public void dispose() { disposed = true; }
    @Override public boolean disposed() { return disposed; }
  }
  private static final class RuntimeLanguage extends LanguageRuntime {
    private final String namespace;
    private final SLanguageId id;
    private final SLanguage base;
    RuntimeLanguage(String namespace, SLanguageId id, SLanguage base) { this.namespace = namespace; this.id = id; this.base = base; }
    @Override public String getNamespace() { return namespace; }
    @Override public SLanguageId getId() { return id; }
    @Override public int getVersion() { return 0; }
    @Override protected void fillExtendedLanguages(Collection<SLanguage> languages) { if (base != null) languages.add(base); }
    @Override protected <T extends ILanguageAspect> T createAspect(Class<T> type) {
      return type == LifeAspect.class ? type.cast(new TestAspect()) : null;
    }
  }
  private static final class TestProject extends Project {
    private final BrowserPlatform platform;
    TestProject(BrowserPlatform platform) {
      super("browser-lifecycle-test");
      this.platform = platform;
      ProjectRepository repository = new ProjectRepository(this,
          platform.findComponent(MPSModuleRepository.class), platform.findComponent(SRepositoryRegistry.class));
      initRepository(repository);
      repository.init();
    }
    @Override public ComponentHost getPlatform() { return platform; }
    @Override public <T> T getComponent(Class<T> type) { return null; }
    @Override public List<SModule> getProjectModules() { return Collections.emptyList(); }
    @Override public boolean isOpened() { return !isDisposed(); }
    @Override public void addModule(SModule module) { throw new UnsupportedOperationException("This fixture project owns no modules"); }
    @Override public void removeModule(SModule module) { throw new UnsupportedOperationException("This fixture project owns no modules"); }
  }
  private static void require(boolean condition, String detail) { if (!condition) throw new AssertionError(detail); }
}
