package gnu.trove;

/** Explicit Java exceptions even in J2CL development builds with debug JRE checks. */
final class ArraySupport {
  private ArraySupport() {}
  static void copy(Object source, int sourceOffset, Object target, int targetOffset, int length) {
    if (source == null || target == null) throw new NullPointerException();
    int sourceLength = length(source), targetLength = length(target);
    if (sourceOffset < 0 || targetOffset < 0 || length < 0
        || sourceOffset > sourceLength - length || targetOffset > targetLength - length) {
      throw new ArrayIndexOutOfBoundsException();
    }
    System.arraycopy(source, sourceOffset, target, targetOffset, length);
    if (target instanceof float[]) {
      float[] floats = (float[])target;
      for (int i = targetOffset; i < targetOffset + length; i++) floats[i] = FloatSupport.round(floats[i]);
    }
  }
  private static int length(Object array) {
    if (array instanceof byte[]) return ((byte[])array).length;
    if (array instanceof int[]) return ((int[])array).length;
    if (array instanceof long[]) return ((long[])array).length;
    if (array instanceof float[]) return ((float[])array).length;
    if (array instanceof double[]) return ((double[])array).length;
    if (array instanceof Object[]) return ((Object[])array).length;
    throw new ArrayStoreException();
  }
}
