package org.trovej2cl;

import gnu.trove.THashMap;
import jetbrains.mps.smodel.SNodeId.Regular;
import jetbrains.mps.smodel.SNodeId.StringBasedId;
import jetbrains.mps.smodel.nodeidmap.INodeIdToNodeMap;
import jetbrains.mps.smodel.nodeidmap.StringBasedNodeIdMap;
import jetbrains.mps.smodel.nodeidmap.UniversalOptimizedNodeIdMap;
import jetbrains.mps.util.ObjectIdentityHashingStrategy;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.model.SNodeId;

/** Runs unchanged pinned MPS map code with test-only model boundary values. */
final class MpsScenarios {
  static final class Node implements SNode { final int value; Node(int value) { this.value = value; } }
  static final class OtherId implements SNodeId {
    final int value; OtherId(int value) { this.value = value; }
    public boolean equals(Object other) { return other instanceof OtherId && ((OtherId)other).value == value; }
    public int hashCode() { return value; }
  }
  static void run() {
    for (INodeIdToNodeMap map : new INodeIdToNodeMap[] {new UniversalOptimizedNodeIdMap(), new StringBasedNodeIdMap()}) {
      SNodeId[] ids = {new Regular(9007199254740992L), new Regular(9007199254740993L),
          new Regular(Long.MIN_VALUE), new StringBasedId("node"), new OtherId(7)};
      for (int i = 0; i < ids.length; i++) map.put(ids[i], new Node(i));
      for (int i = 0; i < ids.length; i++) ContractSuite.check(((Node)map.get(ids[i])).value == i, "MPS node lookup");
      ContractSuite.check(((Node)map.get(new StringBasedId("node"))).value == 3, "MPS string lookup");
      ContractSuite.check(map.containsKey(new OtherId(7)), "MPS custom ID equality");
      int sum = 0; for (SNode n : map.values()) sum += ((Node)n).value;
      ContractSuite.check(sum == 10 && map.size() == 5, "MPS value snapshots");
      map.remove(ids[1]); ContractSuite.check(map.get(ids[1]) == null && map.size() == 4, "MPS remove");
    }
    OtherId a = new OtherId(1), b = new OtherId(1);
    THashMap<OtherId, Integer> identity = new THashMap<>(new ObjectIdentityHashingStrategy<OtherId>());
    identity.put(a, 1); identity.put(b, 2);
    ContractSuite.check(identity.size() == 2 && identity.get(a) == 1, "MPS identity strategy");
    UniversalOptimizedNodeIdMap workload = new UniversalOptimizedNodeIdMap();
    for (int i = 0; i < 20000; i++) workload.put(new Regular(9007199254740992L + i), new Node(i));
    for (int i = 0; i < 20000; i++) ContractSuite.check(((Node)workload.get(new Regular(9007199254740992L + i))).value == i, "MPS model workload");
    for (int i = 0; i < 20000; i += 2) workload.remove(new Regular(9007199254740992L + i));
    ContractSuite.check(workload.size() == 10000, "MPS incremental removal");
    ContractSuite.record("mpsNodes=" + workload.size());
  }
}
