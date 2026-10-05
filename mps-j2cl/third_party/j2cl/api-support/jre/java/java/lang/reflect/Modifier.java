package java.lang.reflect;

/** Standard Java modifier bit constants and predicates; performs no reflection or discovery. */
public class Modifier {
  public static final int PUBLIC = 0x0001;
  public static final int PRIVATE = 0x0002;
  public static final int PROTECTED = 0x0004;
  public static final int STATIC = 0x0008;
  public static final int FINAL = 0x0010;
  public static final int SYNCHRONIZED = 0x0020;
  public static final int VOLATILE = 0x0040;
  public static final int TRANSIENT = 0x0080;
  public static final int NATIVE = 0x0100;
  public static final int INTERFACE = 0x0200;
  public static final int ABSTRACT = 0x0400;
  public static final int STRICT = 0x0800;

  static final int BRIDGE = 0x0040;
  static final int VARARGS = 0x0080;
  static final int SYNTHETIC = 0x1000;
  static final int ANNOTATION = 0x2000;
  static final int ENUM = 0x4000;
  static final int MANDATED = 0x8000;
  static final int ACCESS_MODIFIERS = PUBLIC | PROTECTED | PRIVATE;

  private Modifier() {
    throw new AssertionError();
  }

  public static boolean isPublic(int modifiers) { return (modifiers & PUBLIC) != 0; }
  public static boolean isPrivate(int modifiers) { return (modifiers & PRIVATE) != 0; }
  public static boolean isProtected(int modifiers) { return (modifiers & PROTECTED) != 0; }
  public static boolean isStatic(int modifiers) { return (modifiers & STATIC) != 0; }
  public static boolean isFinal(int modifiers) { return (modifiers & FINAL) != 0; }
  public static boolean isSynchronized(int modifiers) { return (modifiers & SYNCHRONIZED) != 0; }
  public static boolean isVolatile(int modifiers) { return (modifiers & VOLATILE) != 0; }
  public static boolean isTransient(int modifiers) { return (modifiers & TRANSIENT) != 0; }
  public static boolean isNative(int modifiers) { return (modifiers & NATIVE) != 0; }
  public static boolean isInterface(int modifiers) { return (modifiers & INTERFACE) != 0; }
  public static boolean isAbstract(int modifiers) { return (modifiers & ABSTRACT) != 0; }
  public static boolean isStrict(int modifiers) { return (modifiers & STRICT) != 0; }
  static boolean isSynthetic(int modifiers) { return (modifiers & SYNTHETIC) != 0; }
  static boolean isMandated(int modifiers) { return (modifiers & MANDATED) != 0; }

  public static int classModifiers() { return ACCESS_MODIFIERS | ABSTRACT | STATIC | FINAL | STRICT; }
  public static int interfaceModifiers() { return ACCESS_MODIFIERS | ABSTRACT | STATIC | STRICT; }
  public static int constructorModifiers() { return ACCESS_MODIFIERS; }
  public static int methodModifiers() { return ACCESS_MODIFIERS | ABSTRACT | STATIC | FINAL | SYNCHRONIZED | NATIVE | STRICT; }
  public static int fieldModifiers() { return ACCESS_MODIFIERS | STATIC | FINAL | TRANSIENT | VOLATILE; }
  public static int parameterModifiers() { return FINAL; }

  public static String toString(int modifiers) {
    StringBuilder result = new StringBuilder();
    append(result, modifiers, PUBLIC, "public");
    append(result, modifiers, PROTECTED, "protected");
    append(result, modifiers, PRIVATE, "private");
    append(result, modifiers, ABSTRACT, "abstract");
    append(result, modifiers, STATIC, "static");
    append(result, modifiers, FINAL, "final");
    append(result, modifiers, TRANSIENT, "transient");
    append(result, modifiers, VOLATILE, "volatile");
    append(result, modifiers, SYNCHRONIZED, "synchronized");
    append(result, modifiers, NATIVE, "native");
    append(result, modifiers, STRICT, "strictfp");
    append(result, modifiers, INTERFACE, "interface");
    return result.toString();
  }

  private static void append(StringBuilder destination, int modifiers, int flag, String name) {
    if ((modifiers & flag) != 0) {
      if (destination.length() != 0) destination.append(' ');
      destination.append(name);
    }
  }
}
