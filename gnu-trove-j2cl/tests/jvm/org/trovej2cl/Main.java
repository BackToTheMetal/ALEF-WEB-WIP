package org.trovej2cl;
public final class Main {
  public static void main(String[] args) {
    int seed = args.length == 0 ? 20260908 : Integer.parseInt(args[0]);
    System.out.print(ContractSuite.run(seed));
  }
}
