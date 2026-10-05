// Modified for J2CL; see tools/port_sources.py and THIRD_PARTY_NOTICES.md.
package gnu.trove;

public interface Equality<T> {
  Equality CANONICAL = new CanonicalEquality();
  Equality IDENTITY = new IdentityEquality();
  boolean equals(T o1, T o2);
}
