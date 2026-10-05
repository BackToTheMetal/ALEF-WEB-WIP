package com.intellij.ide;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import java.awt.AWTEvent;
import java.util.ArrayList;
import java.util.List;
/** Host event bridge. Dispatchers run in registration order until one consumes the event. */
public final class IdeEventQueue {
  public interface EventDispatcher { boolean dispatch(AWTEvent event); }
  private static final IdeEventQueue INSTANCE = new IdeEventQueue();
  private final List<EventDispatcher> dispatchers = new ArrayList<>();
  public static IdeEventQueue getInstance() { return INSTANCE; }
  public void addDispatcher(EventDispatcher dispatcher, Disposable parent) { dispatchers.add(dispatcher); Disposer.register(parent, () -> removeDispatcher(dispatcher)); }
  public void removeDispatcher(EventDispatcher dispatcher) { dispatchers.remove(dispatcher); }
  public boolean dispatchBrowserEvent(AWTEvent event) { for (EventDispatcher dispatcher : new ArrayList<>(dispatchers)) if (dispatcher.dispatch(event)) return true; return false; }
}
