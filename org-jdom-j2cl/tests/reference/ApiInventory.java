import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;

/** JVM-only inventory. Compares effective public APIs and declared protected hooks, including generics. */
public final class ApiInventory {
  public static void main(String[] args)throws Exception {
    for(String name:Files.readAllLines(Path.of(args[0]))) {
      try {
        Class<?> type=Class.forName(name,false,ApiInventory.class.getClassLoader());
        System.out.println(name+"\tTYPE\t"+type.toGenericString()+" extends "+type.getGenericSuperclass()+" implements "+Arrays.toString(type.getGenericInterfaces()));
        SortedSet<String> members=new TreeSet<>();
        for(Constructor<?> c:type.getDeclaredConstructors())if(visible(c.getModifiers()))members.add(c.toGenericString());
        for(Method m:type.getMethods())if(!m.isSynthetic())members.add(m.toGenericString());
        for(Field f:type.getFields())if(!f.isSynthetic())members.add(f.toGenericString());
        for(Method m:type.getDeclaredMethods())if(Modifier.isProtected(m.getModifiers())&&!m.isSynthetic())members.add(m.toGenericString());
        for(Field f:type.getDeclaredFields())if(Modifier.isProtected(f.getModifiers())&&!f.isSynthetic())members.add(f.toGenericString());
        for(String member:members)System.out.println(name+"\tMEMBER\t"+member);
      }catch(ClassNotFoundException missing){System.out.println(name+"\tMISSING\t");}
    }
  }
  private static boolean visible(int modifiers){return Modifier.isPublic(modifiers)||Modifier.isProtected(modifiers);}
}
