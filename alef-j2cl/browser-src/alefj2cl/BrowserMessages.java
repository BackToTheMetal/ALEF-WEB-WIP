package alefj2cl;

import jetbrains.mps.messages.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/** Host-visible messages, retaining severity and ALEF/ALEF-DEV channel. */
public final class BrowserMessages {
  public static final class Entry {
    public final String channel;
    public final Message message;
    private Entry(String channel, Message message) { this.channel = channel; this.message = message; }
  }
  private static final List<Entry> history = new ArrayList<>();
  private static BiConsumer<String, Message> listener;
  public static void setListener(BiConsumer<String, Message> value) { listener = value; }
  public static List<Entry> history() { return Collections.unmodifiableList(new ArrayList<>(history)); }
  public static void clear() { history.clear(); }
  public static void publish(String channel, Message message) {
    history.add(new Entry(channel, message));
    if (listener != null) listener.accept(channel, message);
  }
}
