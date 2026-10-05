// Modified for J2CL; see tools/port_sources.py and THIRD_PARTY_NOTICES.md.
package gnu.trove;


class CanonicalEquality<T> implements Equality<T> {
  public boolean equals(T o1, T o2) {
    return o1 != null ? o1.equals(o2) : o2 == null;
  }
}
