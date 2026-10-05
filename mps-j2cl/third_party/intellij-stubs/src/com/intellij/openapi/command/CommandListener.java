package com.intellij.openapi.command;
import com.intellij.util.messages.Topic;
public interface CommandListener extends java.util.EventListener {
  Topic<CommandListener> TOPIC = Topic.create("CommandListener", CommandListener.class);
  default void commandStarted(CommandEvent event) {}
  default void beforeCommandFinished(CommandEvent event) {}
  default void commandFinished(CommandEvent event) {}
  default void undoTransparentActionStarted() {}
  default void beforeUndoTransparentActionFinished() {}
  default void undoTransparentActionFinished() {}
}
