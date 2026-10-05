package mpsj2cl.modifiers;

import java.lang.reflect.Modifier;

/** Exhaustive 16-bit modifier fingerprint plus high-bit and declaration-mask cases. */
public final class ModifierTest {
  private ModifierTest() {}

  public static String run() {
    // Generated independently by the installed JVM's java.lang.reflect.Modifier.
    String actual = fingerprint();
    if (!"1434653048".equals(actual)) throw new AssertionError("Modifier JVM fingerprint mismatch: " + actual);
    return "Modifier: all 65536 flag combinations, high/negative bits, rendering order and declaration masks match the JVM";
  }

  public static String fingerprint() {
    int fingerprint = 1;
    for (int modifiers = 0; modifiers <= 0xffff; modifiers++) {
      fingerprint = step(fingerprint, modifiers);
    }
    fingerprint = step(fingerprint, Integer.MIN_VALUE);
    fingerprint = step(fingerprint, Integer.MAX_VALUE);
    fingerprint = step(fingerprint, -1);
    fingerprint = 31 * fingerprint + Modifier.classModifiers();
    fingerprint = 31 * fingerprint + Modifier.interfaceModifiers();
    fingerprint = 31 * fingerprint + Modifier.constructorModifiers();
    fingerprint = 31 * fingerprint + Modifier.methodModifiers();
    fingerprint = 31 * fingerprint + Modifier.fieldModifiers();
    fingerprint = 31 * fingerprint + Modifier.parameterModifiers();
    return Integer.toString(fingerprint);
  }

  private static int step(int fingerprint, int modifiers) {
    fingerprint = 31 * fingerprint + Modifier.toString(modifiers).hashCode();
    boolean[] predicates = {Modifier.isPublic(modifiers), Modifier.isProtected(modifiers), Modifier.isPrivate(modifiers),
        Modifier.isAbstract(modifiers), Modifier.isStatic(modifiers), Modifier.isFinal(modifiers),
        Modifier.isTransient(modifiers), Modifier.isVolatile(modifiers), Modifier.isSynchronized(modifiers),
        Modifier.isNative(modifiers), Modifier.isStrict(modifiers), Modifier.isInterface(modifiers)};
    for (boolean predicate : predicates) fingerprint = 31 * fingerprint + (predicate ? 1 : 0);
    return fingerprint;
  }

  public static void main(String[] arguments) {
    System.out.println(run());
  }
}
