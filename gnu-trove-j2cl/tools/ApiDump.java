import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

/** Build-time reflection only; never a dependency of the portable library. */
public final class ApiDump {
  public static void main(String[] args) throws Exception {
    try (URLClassLoader loader = new URLClassLoader(new URL[] {Path.of(args[0]).toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
      for (String name : Files.readAllLines(Path.of(args[1]))) {
        Class<?> type;
        try { type = Class.forName(name, false, loader); }
        catch (ClassNotFoundException e) { System.out.println(name + "\tMISSING\tclass\t"); continue; }
        List<String> rows = new ArrayList<>();
        rows.add(name + "\tCLASS\tclass\t" + type.toGenericString() + " extends " + type.getGenericSuperclass()
            + " implements " + Arrays.toString(type.getGenericInterfaces()));
        for (Constructor<?> c : type.getDeclaredConstructors()) {
          if (visible(c.getModifiers())) rows.add(name + "\tCTOR\t" + parameters(c.getParameterTypes()) + "\t" + c.toGenericString());
        }
        // Include inherited members so covariant clone overrides are checked too.
        Set<String> seenMethods = new HashSet<>(), seenFields = new HashSet<>();
        for (Class<?> owner = type; owner != null && owner.getName().startsWith("gnu.trove."); owner = owner.getSuperclass()) {
          for (Method m : owner.getDeclaredMethods()) {
            String key = m.getName() + parameters(m.getParameterTypes());
            if (visible(m.getModifiers()) && !m.isSynthetic() && seenMethods.add(key))
              rows.add(name + "\tMETHOD\t" + key + "\t" + m.toGenericString());
          }
          for (Field f : owner.getDeclaredFields()) {
            if (visible(f.getModifiers()) && !f.isSynthetic() && seenFields.add(f.getName())) {
              String constant = "";
              if (Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers())
                  && (f.getType().isPrimitive() || f.getType() == String.class)) {
                f.setAccessible(true); constant = " = " + f.get(null);
              }
              rows.add(name + "\tFIELD\t" + f.getName() + "\t" + f.toGenericString() + constant);
            }
          }
        }
        Collections.sort(rows);
        rows.forEach(System.out::println);
      }
    }
  }
  static boolean visible(int modifiers) { return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers); }
  static String parameters(Class<?>[] types) {
    StringJoiner names = new StringJoiner(",", "(", ")");
    for (Class<?> type : types) names.add(type.getTypeName());
    return names.toString();
  }
}
