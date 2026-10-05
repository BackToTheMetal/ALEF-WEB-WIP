package jetbrains.mps.smodel;
/** Test-only ID values. These are excluded from every published library target. */
public abstract class SNodeId implements org.jetbrains.mps.openapi.model.SNodeId {
  public static final class Regular extends SNodeId {
    private final long id;
    public Regular(long id) { this.id = id; }
    public long getId() { return id; }
    public boolean equals(Object other) { return other instanceof Regular && ((Regular)other).id == id; }
    public int hashCode() { return (int)(id ^ (id >>> 32)); }
  }
  public static final class StringBasedId extends SNodeId {
    private final String id;
    public StringBasedId(String id) { this.id = id; }
    public String getId() { return id; }
    public boolean equals(Object other) { return other instanceof StringBasedId && ((StringBasedId)other).id.equals(id); }
    public int hashCode() { return id.hashCode(); }
  }
}
