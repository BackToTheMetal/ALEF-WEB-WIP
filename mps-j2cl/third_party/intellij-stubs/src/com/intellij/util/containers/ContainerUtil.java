package com.intellij.util.containers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
public final class ContainerUtil {
  private ContainerUtil() {}
  public static <T> List<T> createLockFreeCopyOnWriteList() { return new CopyOnWriteArrayList<>(); }
  public static <K, V> ConcurrentMap<K, V> newConcurrentMap() { return new ConcurrentHashMap<>(); }
  public static <T, R> List<R> map(Iterable<? extends T> source, Function<? super T, ? extends R> mapper) {
    List<R> result = new ArrayList<>(); for (T item : source) result.add(mapper.apply(item)); return result;
  }
  public static <T, R> List<R> map(T[] source, Function<? super T, ? extends R> mapper) { return map(java.util.Arrays.asList(source), mapper); }
}
