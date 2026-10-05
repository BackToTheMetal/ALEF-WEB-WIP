// Signature dependency only; see compatibility/external-support.json.
package java.security;

public abstract class BasicPermission extends Permission implements java.io.Serializable {
  public BasicPermission(String name) { super(name); }
  public BasicPermission(String name, String actions) { super(name); }
  public boolean implies(Permission permission) { throw unsupported(); }
  public boolean equals(Object other) { throw unsupported(); }
  public int hashCode() { throw unsupported(); }
  public String getActions() { throw unsupported(); }
  private static UnsupportedOperationException unsupported() { return javaemul.internal.nio.UnsupportedNio.operation("java.security.BasicPermission"); }
}
