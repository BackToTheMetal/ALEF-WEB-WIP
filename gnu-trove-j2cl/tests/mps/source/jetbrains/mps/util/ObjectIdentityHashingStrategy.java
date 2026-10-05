/*
 * Copyright 2000-2022 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package jetbrains.mps.util;

import gnu.trove.TObjectHashingStrategy;

public final class ObjectIdentityHashingStrategy<T> implements TObjectHashingStrategy<T> {

  public static final TObjectHashingStrategy IDENTITY = new ObjectIdentityHashingStrategy<>();

  public int computeHashCode(T object) {
    return System.identityHashCode(object);
  }

  public boolean equals(T o1, T o2) {
    return o1 == o2;
  }
}
