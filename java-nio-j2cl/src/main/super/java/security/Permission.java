// Signature dependency only; see compatibility/external-support.json.
package java.security;

public abstract class Permission implements java.io.Serializable {
  private final String name;
  public Permission(String name) { this.name = name; }
  public final String getName() { return name; }
  public abstract boolean implies(Permission permission);
  public abstract boolean equals(Object other);
  public abstract int hashCode();
  public abstract String getActions();
}
