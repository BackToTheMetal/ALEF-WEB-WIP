package mpsj2cl.typechecking;

/** Runs the deterministic public-contract assertions on the reference JVM. */
public final class FoundationJreJvm {
  public static void main(String[] args) { System.out.println(FoundationJreTest.run()); }
}
