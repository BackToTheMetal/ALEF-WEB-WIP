package mpsj2cl.runtime;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

/** Shared JVM/browser behavior oracle for UUIDs, cryptographic randomness, and runtime arrays. */
public final class RuntimeSupportTest {
  private static int initializations;
  private static final class Dormant {
    static { initializations++; }
  }

  private static void require(boolean value, String message) {
    if (!value) { throw new AssertionError(message); }
  }

  // This oracle deliberately invokes equals(null) to verify the replacement's contract.
  @SuppressWarnings("EqualsNull")
  private static boolean equalsNull(UUID value) { return value.equals(null); }

  private static void reject(Runnable operation, Class<? extends Throwable> expected) {
    try {
      operation.run();
    } catch (Throwable failure) {
      require(expected.isInstance(failure), "unexpected failure " + failure);
      return;
    }
    throw new AssertionError("missing expected exception");
  }

  public static String run() {
    StringBuilder result = new StringBuilder();
    result.append(CharacterSupportTest.run());
    long state = 0x123456789abcdef0L;
    UUID previous = new UUID(0, 0);
    for (int i = 0; i < 128; i++) {
      state = state * 6364136223846793005L + 1442695040888963407L;
      long first = state;
      state = state * 6364136223846793005L + 1442695040888963407L;
      UUID value = new UUID(first, state);
      require(UUID.fromString(value.toString()).equals(value), "UUID round trip");
      require(UUID.fromString(value.toString().toUpperCase()).equals(value), "UUID upper case");
      require(value.getMostSignificantBits() == first && value.getLeastSignificantBits() == state,
          "UUID bits");
      require(!equalsNull(value) && !value.equals(value.toString()), "UUID equality type");
      result.append(value).append(':').append(value.hashCode()).append(':').append(value.version())
          .append(':').append(value.variant()).append(':').append(value.compareTo(previous)).append('\n');
      if (value.version() == 1) {
        result.append(value.timestamp()).append(':').append(value.clockSequence()).append(':')
            .append(value.node()).append('\n');
      } else {
        reject(() -> value.timestamp(), UnsupportedOperationException.class);
        reject(() -> value.clockSequence(), UnsupportedOperationException.class);
        reject(() -> value.node(), UnsupportedOperationException.class);
      }
      previous = value;
    }
    String[] strings = {
      "00000000-0000-0000-0000-000000000000", "ffffffff-ffff-ffff-ffff-ffffffffffff",
      "80000000-0000-0000-8000-000000000000", "1-2-3-4-5", "+1-+2-+3-+4-+5",
      "123456789-12345-12345-12345-123456789", "123456789-12345-12345-1-1", "0-0-0-0-7fffffffffffffff",
      "0-0-0-0-8000000000000000", "\uff11-\uff12-\uff13-\uff14-\uff15",
      "\u0661-\u0662-\u0663-\u0664-\u0665", null, "", "1", "1-2-3-4", "1-2-3-4-5-6",
      "-1-2-3-4-5", "1--3-4-5", "1-2-3-4-", "x-2-3-4-5", " -2-3-4-5", "+-2-3-4-5",
      "000000000-0000-0000-0000-000000000000", "1-2-3-4-+0000000000005"
    };
    for (String text : strings) {
      try {
        result.append(UUID.fromString(text)).append('\n');
      } catch (NullPointerException expected) {
        result.append("NPE\n");
      } catch (NumberFormatException expected) {
        result.append("NFE\n");
      } catch (IllegalArgumentException expected) {
        result.append("IAE\n");
      }
    }
    long[] boundaries = {Long.MIN_VALUE, -1, 0, 1, Long.MAX_VALUE};
    for (long first : boundaries) {
      for (long second : boundaries) {
        result.append(new UUID(first, 0).compareTo(new UUID(second, 0)))
            .append(new UUID(0, first).compareTo(new UUID(0, second))).append(':');
      }
    }
    reject(() -> new UUID(0, 0).compareTo(null), NullPointerException.class);
    testKernelApis();
    testArrays();
    testRandom();
    result.append("\nPASS\n");
    return result.toString();
  }

  private static void testKernelApis() {
    require(System.lineSeparator().equals(System.getProperty("line.separator", "\n")), "line separator");
    Enumeration<String> empty = Collections.emptyEnumeration();
    require(!empty.hasMoreElements(), "empty enumeration has an element");
    reject(() -> empty.nextElement(), NoSuchElementException.class);
    reject(() -> empty.nextElement(), NoSuchElementException.class);
    Iterator<String> iterator = empty.asIterator();
    require(!iterator.hasNext(), "empty enumeration iterator has an element");
    reject(() -> iterator.next(), NoSuchElementException.class);
    reject(() -> iterator.remove(), IllegalStateException.class);
    Throwable cause = new IllegalStateException("cause");
    LinkageError linkage = new LinkageError("link", cause);
    require(linkage.getMessage().equals("link") && linkage.getCause() == cause, "linkage message/cause");
    require(new LinkageError().getMessage() == null, "default linkage message");
    NoClassDefFoundError missing = new NoClassDefFoundError("missing");
    missing.initCause(cause);
    require(missing.getMessage().equals("missing") && missing.getCause() == cause, "missing class cause");
    require(LinkageError.class.isInstance(missing) && Error.class.isInstance(missing), "linkage hierarchy");
    VirtualMachineError broken = new VirtualMachineError("runtime", cause) {};
    require(broken.getMessage().equals("runtime") && broken.getCause() == cause, "runtime error cause");
    VirtualMachineError caused = new VirtualMachineError(cause) {};
    require(caused.getCause() == cause && caused.getMessage().equals(cause.toString()), "runtime cause constructor");
    require(VirtualMachineError.class.isInstance(broken), "runtime error hierarchy");
  }

  private static void testArrays() {
    Class<?>[] components = {Object.class, String.class, Runnable.class, Dormant.class,
        boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class,
        int[].class, String[][].class};
    Class<?>[] expected = {Object[].class, String[].class, Runnable[].class, Dormant[].class,
        boolean[].class, byte[].class, char[].class, short[].class, int[].class, long[].class, float[].class, double[].class,
        int[][].class, String[][][].class};
    for (int i = 0; i < components.length; i++) {
      Object array = Array.newInstance(components[i], 3);
      require(array.getClass() == expected[i] && Array.getLength(array) == 3, "array class/length");
      require(expected[i].isInstance(array), "array instanceof");
      require(Array.newInstance(components[i], 0).getClass() == expected[i], "empty array class");
    }
    require(!((boolean[]) Array.newInstance(boolean.class, 1))[0], "boolean default");
    require(((byte[]) Array.newInstance(byte.class, 1))[0] == 0, "byte default");
    require(((char[]) Array.newInstance(char.class, 1))[0] == 0, "char default");
    require(((short[]) Array.newInstance(short.class, 1))[0] == 0, "short default");
    require(((int[]) Array.newInstance(int.class, 1))[0] == 0, "int default");
    require(((long[]) Array.newInstance(long.class, 1))[0] == 0, "long default");
    require(((float[]) Array.newInstance(float.class, 1))[0] == 0, "float default");
    require(((double[]) Array.newInstance(double.class, 1))[0] == 0, "double default");
    require(((String[]) Array.newInstance(String.class, 1))[0] == null, "reference default");
    int[][] ints = (int[][]) Array.newInstance(int.class, 2, 3);
    require(ints.length == 2 && ints[0].length == 3 && ints[0][2] == 0, "multidimensional primitive");
    String[][][] strings = (String[][][]) Array.newInstance(String[].class, 2, 3);
    require(strings.length == 2 && strings[0].length == 3 && strings[0][0] == null, "unallocated trailing dimension");
    Object[] typed = (Object[]) Array.newInstance(String.class, 1);
    typed[0] = "valid";
    reject(() -> typed[0] = new Object(), ArrayStoreException.class);
    int[] dimensions = {2, 3};
    Array.newInstance(String.class, dimensions);
    require(dimensions[0] == 2 && dimensions[1] == 3, "dimensions mutated");
    reject(() -> Array.newInstance(null, 1), NullPointerException.class);
    reject(() -> Array.newInstance(String.class, (int[]) null), NullPointerException.class);
    reject(() -> Array.newInstance(void.class, 1), IllegalArgumentException.class);
    reject(() -> Array.newInstance(void.class, -1), NegativeArraySizeException.class);
    reject(() -> Array.newInstance(void.class, 0, -1), NegativeArraySizeException.class);
    reject(() -> Array.newInstance(null, -1), NullPointerException.class);
    reject(() -> Array.newInstance(String.class, new int[0]), IllegalArgumentException.class);
    reject(() -> Array.newInstance(String.class, new int[256]), IllegalArgumentException.class);
    reject(() -> Array.newInstance(String.class, -1), NegativeArraySizeException.class);
    reject(() -> Array.newInstance(String.class, 0, -1), NegativeArraySizeException.class);
    Object deep = Array.newInstance(String.class, new int[255]);
    Class<?> deepClass = deep.getClass();
    Class<?> limitClass = deepClass;
    reject(() -> Array.newInstance(limitClass, 0), IllegalArgumentException.class);
    for (int i = 0; i < 255; i++) { deepClass = deepClass.getComponentType(); }
    require(deepClass == String.class, "maximum dimensions");
    require(initializations == 0, "array allocation initialized component class");
  }

  private static void testRandom() {
    CountingRandom routed = new CountingRandom();
    entropy(() -> routed.nextBoolean(), routed);
    entropy(() -> routed.nextInt(), routed);
    entropy(() -> routed.nextInt(31), routed);
    entropy(() -> routed.nextInt(16), routed);
    entropy(() -> routed.nextLong(), routed);
    entropy(() -> routed.nextFloat(), routed);
    entropy(() -> routed.nextDouble(), routed);
    entropy(() -> routed.nextGaussian(), routed);
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[65537];
    random.nextBytes(bytes);
    boolean nonzero = false;
    for (byte value : bytes) { nonzero |= value != 0; }
    require(nonzero, "cryptographic bytes all zero");
    require(random.generateSeed(7).length == 7 && SecureRandom.getSeed(11).length == 11, "seed bytes");
    reject(() -> random.nextBytes(null), NullPointerException.class);
    reject(() -> random.generateSeed(-1), IllegalArgumentException.class);
    reject(() -> random.nextInt(0), IllegalArgumentException.class);
    Set<UUID> uuids = new HashSet<>();
    Set<Long> longs = new HashSet<>();
    for (int i = 0; i < 128; i++) {
      UUID value = UUID.randomUUID();
      require(value.version() == 4 && value.variant() == 2, "random UUID layout");
      require(uuids.add(value), "duplicate random UUID");
      require(longs.add(random.nextLong()), "duplicate random long");
      int bounded = random.nextInt(31);
      require(bounded >= 0 && bounded < 31, "bounded random int");
      bounded = random.nextInt(16);
      require(bounded >= 0 && bounded < 16, "power of two bound");
      bounded = random.nextInt(Integer.MAX_VALUE);
      require(bounded >= 0, "large bound");
      float real = random.nextFloat();
      require(real >= 0 && real < 1, "random float");
      double fraction = random.nextDouble();
      require(fraction >= 0 && fraction < 1, "random double");
      require(!Double.isNaN(random.nextGaussian()), "random Gaussian");
      random.nextInt();
      random.nextBoolean();
    }
  }

  private static void entropy(Runnable operation, CountingRandom random) {
    int previous = random.draws;
    operation.run();
    require(random.draws > previous, "random generator bypassed cryptographic nextBytes");
  }

  private static final class CountingRandom extends SecureRandom {
    int draws;
    @Override
    public void nextBytes(byte[] bytes) {
      super.nextBytes(bytes);
      draws++;
    }
  }

  public static void main(String[] args) { System.out.print(run()); }
}
