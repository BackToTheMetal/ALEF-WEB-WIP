import java.lang.reflect.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/** Build-time reflection only: exports public/protected API, not JDK implementations. */
public final class ExtractApi {
  static String q(String s) { return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\""; }
  static String type(Type t) { return t.getTypeName().replace('$', '.'); }
  static boolean visible(int m) { return Modifier.isPublic(m) || Modifier.isProtected(m); }
  static String params(TypeVariable<?>[] vars) {
    if (vars.length == 0) return "";
    return Arrays.stream(vars).map(v -> v.getName() + (Arrays.equals(v.getBounds(), new Type[]{Object.class}) ? "" : " extends " + Arrays.stream(v.getBounds()).map(ExtractApi::type).collect(Collectors.joining(" & ")))).collect(Collectors.joining(", ", "<", ">"));
  }
  static String member(String name, String declaration, boolean abstractMember, String value) {
    return "{\"name\":"+q(name)+",\"declaration\":"+q(declaration.replace('$','.'))+",\"abstract\":"+abstractMember+",\"value\":"+(value == null ? "null" : q(value))+"}";
  }
  public static void main(String[] args) throws Exception {
    List<String> names;
    if (args.length > 0) names = Arrays.asList(args);
    else try (Stream<Path> paths = Files.walk(FileSystems.getFileSystem(URI.create("jrt:/")).getPath("/modules/java.base/java/nio"))) {
      names = paths.filter(p -> p.toString().endsWith(".class")).map(p -> p.toString().substring("/modules/java.base/".length()).replace('/', '.').replace(".class", "")).sorted().toList();
    }
    List<String> classes = new ArrayList<>();
    for (String name : names) {
      Class<?> c = Class.forName(name, false, null);
      boolean accessible = true;
      for (Class<?> enclosing = c; enclosing != null; enclosing = enclosing.getEnclosingClass()) accessible &= visible(enclosing.getModifiers());
      if (!accessible || c.isSynthetic()) continue;
      int mods = c.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.STATIC | Modifier.FINAL | Modifier.ABSTRACT);
      if (c.isInterface()) mods &= ~Modifier.ABSTRACT;
      if (c.isEnum()) mods &= ~(Modifier.FINAL | Modifier.ABSTRACT);
      String declaration = Modifier.toString(mods) + " " + (c.isEnum()?"enum":c.isInterface()?"interface":"class") + " " + c.getSimpleName() + params(c.getTypeParameters());
      if (!c.isEnum() && c.getSuperclass() != null && c.getSuperclass() != Object.class) declaration += " extends " + type(c.getGenericSuperclass());
      if (c.getInterfaces().length > 0) declaration += (c.isInterface()?" extends ":" implements ") + Arrays.stream(c.getGenericInterfaces()).map(ExtractApi::type).collect(Collectors.joining(", "));
      List<String> fields = new ArrayList<>(), constructors = new ArrayList<>(), methods = new ArrayList<>();
      for (Field f : c.getDeclaredFields()) if (visible(f.getModifiers()) && !f.isSynthetic()) {
        String value = null;
        if (Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()) && (f.getType().isPrimitive() || f.getType() == String.class)) {
          try { Object v = f.get(null); value = f.getType() == String.class ? q((String)v) : String.valueOf(v) + (f.getType()==long.class?"L":f.getType()==float.class?"f":""); } catch (IllegalAccessException ignored) {}
        }
        fields.add(member(f.getName(), f.toGenericString().replace(c.getName()+".", ""), false, value));
      }
      for (Constructor<?> ctor : c.getDeclaredConstructors()) if (visible(ctor.getModifiers()) && !ctor.isSynthetic()) constructors.add(member(c.getSimpleName(), ctor.toGenericString().replace(c.getName(), c.getSimpleName()), false, null));
      for (Method method : c.getDeclaredMethods()) if (visible(method.getModifiers()) && !method.isSynthetic() && !method.isBridge()) {
        if (c.isEnum() && (method.getName().equals("values") || method.getName().equals("valueOf"))) continue;
        methods.add(member(method.getName(), method.toGenericString().replace(c.getName()+".", ""), Modifier.isAbstract(method.getModifiers()), null));
      }
      Collections.sort(fields); Collections.sort(constructors); Collections.sort(methods);
      String enums = c.isEnum() ? Arrays.stream(c.getEnumConstants()).map(e -> q(((Enum<?>)e).name())).collect(Collectors.joining(",")) : "";
      classes.add("{\"name\":"+q(c.getName().replace('$','.'))+",\"binaryName\":"+q(c.getName())+",\"package\":"+q(c.getPackageName())+",\"simpleName\":"+q(c.getSimpleName())+",\"enclosing\":"+(c.getEnclosingClass()==null?"null":q(c.getEnclosingClass().getName().replace('$','.')))+",\"superclass\":"+(c.getSuperclass()==null?"null":q(c.getSuperclass().getName().replace('$','.')))+",\"declaration\":"+q(declaration)+",\"enumConstants\":["+enums+"],\"fields\":["+String.join(",",fields)+"],\"constructors\":["+String.join(",",constructors)+"],\"methods\":["+String.join(",",methods)+"]}");
    }
    System.out.println("{\"jdk\":"+q(System.getProperty("java.runtime.version"))+",\"classes\":["+String.join(",\n",classes)+"]}");
  }
}
