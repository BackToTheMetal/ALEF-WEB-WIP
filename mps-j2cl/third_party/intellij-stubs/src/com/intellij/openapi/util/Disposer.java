package com.intellij.openapi.util;

import com.intellij.openapi.Disposable;
import com.intellij.util.IncorrectOperationException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/** Single-event-loop disposal tree. Completed objects are remembered weakly. */
public final class Disposer {
  private static final Map<Disposable, List<Disposable>> children = new IdentityHashMap<>();
  private static final Map<Disposable, Disposable> parents = new IdentityHashMap<>();
  private static final List<WeakReference<Disposable>> disposed = new ArrayList<>();
  private static boolean debug;
  private Disposer() {}

  public static Disposable newDisposable() { return newDisposable("anonymous"); }
  public static Disposable newDisposable(String name) { return newCheckedDisposable(name); }
  public static CheckedDisposable newCheckedDisposable() { return newCheckedDisposable("anonymous"); }
  public static CheckedDisposable newCheckedDisposable(String name) {
    return new CheckedDisposable() {
      private boolean done;
      @Override public void dispose() { done = true; }
      @Override public boolean isDisposed() { return done; }
      @Override public String toString() { return name; }
    };
  }
  public static Disposable newDisposable(Disposable parent) { return newDisposable(parent, "anonymous"); }
  public static Disposable newDisposable(Disposable parent, String name) {
    Disposable child = newDisposable(name); register(parent, child); return child;
  }
  public static CheckedDisposable newCheckedDisposable(Disposable parent) { return newCheckedDisposable(parent, "anonymous"); }
  public static CheckedDisposable newCheckedDisposable(Disposable parent, String name) {
    CheckedDisposable child = newCheckedDisposable(name); register(parent, child); return child;
  }
  public static void register(Disposable parent, Disposable child) {
    if (parent == null || child == null) throw new NullPointerException();
    if (isDisposed(parent) || isDisposed(child)) throw new IncorrectOperationException("Cannot register a disposed object");
    for (Disposable cursor = parent; cursor != null; cursor = parents.get(cursor)) {
      if (cursor == child) throw new IncorrectOperationException("Disposal tree cycle");
    }
    detach(child);
    parents.put(child, parent);
    children.computeIfAbsent(parent, ignored -> new ArrayList<>()).add(child);
  }
  public static boolean tryRegister(Disposable parent, Disposable child) {
    if (isDisposed(parent) || isDisposed(child)) return false;
    register(parent, child); return true;
  }
  public static boolean isDisposed(Disposable target) {
    boolean found = target instanceof CheckedDisposable && ((CheckedDisposable) target).isDisposed();
    for (int i = disposed.size() - 1; i >= 0; i--) {
      Disposable value = disposed.get(i).get();
      if (value == null) disposed.remove(i); else if (value == target) found = true;
    }
    return found;
  }
  public static void dispose(Disposable target) { dispose(target, true); }
  public static void dispose(Disposable target, boolean processUnregistered) {
    if (target == null) throw new NullPointerException();
    if (isDisposed(target)) return;
    if (!processUnregistered && !children.containsKey(target) && !parents.containsKey(target)) return;
    disposed.add(new WeakReference<>(target));
    detach(target);
    List<Disposable> pending = children.remove(target);
    Throwable failure = null;
    if (pending != null) {
      for (int i = pending.size() - 1; i >= 0; i--) {
        try { dispose(pending.get(i)); }
        catch (RuntimeException | Error error) { if (failure == null) failure = error; else failure.addSuppressed(error); }
      }
    }
    try { target.dispose(); }
    catch (RuntimeException | Error error) { if (failure == null) failure = error; else failure.addSuppressed(error); }
    if (failure instanceof Error) throw (Error) failure;
    if (failure != null) throw (RuntimeException) failure;
  }
  public static void disposeChildren(Disposable parent, Predicate<? super Disposable> predicate) {
    List<Disposable> list = children.get(parent);
    if (list == null) return;
    List<Disposable> snapshot = new ArrayList<>(list);
    Throwable failure = null;
    for (int i = snapshot.size() - 1; i >= 0; i--) {
      try { if (predicate == null || predicate.test(snapshot.get(i))) dispose(snapshot.get(i)); }
      catch (RuntimeException | Error error) { if (failure == null) failure = error; else failure.addSuppressed(error); }
    }
    if (failure instanceof Error) throw (Error) failure;
    if (failure != null) throw (RuntimeException) failure;
  }
  private static void detach(Disposable child) {
    Disposable parent = parents.remove(child);
    List<Disposable> list = children.get(parent);
    if (list != null) {
      for (int i = list.size() - 1; i >= 0; i--) if (list.get(i) == child) list.remove(i);
      if (list.isEmpty()) children.remove(parent);
    }
  }
  public static boolean setDebugMode(boolean value) { boolean previous = debug; debug = value; return previous; }
  public static boolean isDebugMode() { return debug; }
  public static boolean isDebugDisposerOn() { return debug; }
  public static void assertIsEmpty() { assertIsEmpty(true); }
  public static void assertIsEmpty(boolean throwError) {
    if (throwError && !parents.isEmpty()) throw new IllegalStateException("Undisposed registrations: " + parents.size());
  }
}
