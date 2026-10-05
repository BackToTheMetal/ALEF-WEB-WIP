package com.intellij.compat;
import com.intellij.openapi.project.Project;
public final class BrowserProject extends BrowserComponentManager implements Project {
  private final String name;
  private final String basePath;
  public BrowserProject(String name, String basePath, BrowserApplication application) {
    super(application.getMessageBus());
    this.name = java.util.Objects.requireNonNull(name);
    this.basePath = java.util.Objects.requireNonNull(basePath);
  }
  @Override public String getName() { return name; }
  @Override public String getBasePath() { return basePath; }
}
