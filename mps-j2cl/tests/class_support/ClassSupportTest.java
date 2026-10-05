package mpsj2cl.classsupport;

import java.io.Serializable;
import mpsj2cl.classsupport.fixtures.FixtureTypes;
import mpsj2cl.classsupport.fixtures.FixtureTypes.Action;
import mpsj2cl.classsupport.fixtures.FixtureTypes.Base;
import mpsj2cl.classsupport.fixtures.FixtureTypes.Choice;
import mpsj2cl.classsupport.fixtures.FixtureTypes.Diamond;
import mpsj2cl.classsupport.fixtures.FixtureTypes.Dormant;
import mpsj2cl.classsupport.fixtures.FixtureTypes.Leaf;
import mpsj2cl.classsupport.fixtures.FixtureTypes.Left;
import mpsj2cl.classsupport.fixtures.FixtureTypes.Middle;
import mpsj2cl.classsupport.fixtures.FixtureTypes.Other;
import mpsj2cl.classsupport.fixtures.FixtureTypes.Right;
import mpsj2cl.classsupport.fixtures.FixtureTypes.Root;

/** The same deterministic relation matrix runs on the JVM and in both JS compiler modes. */
public final class ClassSupportTest {
  private ClassSupportTest() {}

  public static String run() {
    class Local extends Base implements Right {}
    Base anonymous = new Base() {};
    Action lambda = () -> {};
    Object[] instances = {
      null, new Object(), new Base(), new Middle(), new Leaf(), new Other(),
      "text", Boolean.TRUE, Double.valueOf(1), Integer.valueOf(1), Long.valueOf(1),
      Byte.valueOf((byte) 1), Short.valueOf((short) 1), Character.valueOf('a'), Float.valueOf(1),
      Choice.PLAIN, Choice.SPECIAL, new Object[0], new String[0], new int[0], new long[0],
      new int[0][0], new int[0][0][0], new Leaf[0], new Leaf[0][0], new Root[0],
      new Serializable[0], new Cloneable[0], new Local(), anonymous, lambda,
    };
    Class<?>[] types = {
      Object.class, Base.class, Middle.class, Leaf.class, Other.class,
      Root.class, Left.class, Right.class, Diamond.class, Runnable.class, Action.class,
      Cloneable.class, Serializable.class, CharSequence.class, Comparable.class,
      Number.class, String.class, Boolean.class, Double.class, Integer.class, Long.class,
      Byte.class, Short.class, Character.class, Float.class, Enum.class, Choice.class,
      Choice.SPECIAL.getClass(), Dormant.class,
      boolean.class, byte.class, short.class, char.class, int.class, long.class,
      float.class, double.class, void.class,
      Object[].class, Object[][].class, Object[][][].class,
      String[].class, String[][].class, CharSequence[].class,
      Base[].class, Leaf[].class, Leaf[][].class, Root[].class, Root[][].class,
      Cloneable[].class, Cloneable[][].class, Serializable[].class, Serializable[][].class,
      int[].class, int[][].class, int[][][].class, long[].class, boolean[].class,
      Number[].class, Integer[].class, Comparable[].class,
      Local.class, anonymous.getClass(), lambda.getClass(),
    };

    require(Root.class.isAssignableFrom(Leaf.class), "transitive interface");
    require(!Leaf.class.isAssignableFrom(Root.class), "interface direction");
    require(Object.class.isAssignableFrom(Root.class), "Object from interface");
    require(Cloneable.class.isAssignableFrom(int[].class), "primitive array Cloneable");
    require(Serializable[].class.isAssignableFrom(int[][].class), "array Serializable covariance");
    require(Cloneable[].class.isAssignableFrom(int[][].class), "array Cloneable covariance");
    require(Object[].class.isAssignableFrom(int[][].class), "multidimensional primitive array");
    require(!Object[].class.isAssignableFrom(int[].class), "primitive array is not Object array");
    require(!long.class.isAssignableFrom(int.class), "no numeric widening");
    require(!Integer.class.isAssignableFrom(int.class), "no boxing conversion");
    require(!int.class.isInstance(Integer.valueOf(1)), "primitive has no boxed instances");
    require(CharSequence.class.isInstance("text"), "native-backed string interface");
    require(Comparable.class.isInstance(Boolean.TRUE), "native-backed Boolean interface");
    require(Number.class.isInstance(Double.valueOf(1)), "native-backed Double superclass");
    require(Action.class.isInstance(lambda), "ordinary lambda interface");
    require(Base.class.isInstance(anonymous), "anonymous subclass");

    StringBuilder result = new StringBuilder();
    result.append("types=").append(types.length).append(";instances=").append(instances.length).append('\n');
    for (Class<?> target : types) {
      for (Class<?> source : types) {
        boolean assignable = target.isAssignableFrom(source);
        result.append(assignable ? '1' : '0');
        try {
          require(source.asSubclass(target) == source, "asSubclass changed Class identity");
          require(assignable, "asSubclass accepted incompatible class");
        } catch (ClassCastException expected) {
          require(!assignable, "asSubclass rejected compatible class");
        }
      }
      result.append(':');
      for (Object instance : instances) {
        result.append(target.isInstance(instance) ? '1' : '0');
      }
      result.append(':');
      for (Object instance : instances) {
        try {
          Object cast = target.cast(instance);
          require(cast == instance, "cast changed reference identity");
          result.append('1');
        } catch (ClassCastException expected) {
          result.append('0');
        }
      }
      boolean threw = false;
      try {
        target.isAssignableFrom(null);
      } catch (NullPointerException expected) {
        threw = true;
      }
      require(threw, "null class argument");
      threw = false;
      try { target.asSubclass(null); } catch (NullPointerException expected) { threw = true; }
      require(threw, "null asSubclass argument");
      result.append('\n');
    }
    require(FixtureTypes.initializations == 0, "type check initialized Dormant");
    return result.toString();
  }

  private static void require(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }

  public static void main(String[] args) {
    System.out.print(run());
  }
}
