/*
 * Copyright 2003-2024 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.messages;

import jetbrains.mps.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.module.SModule;


public class Message implements IMessage {
  private static final Logger LOG = Logger.getLogger(Message.class);

  private final String mySender;
  private final MessageKind myKind;
  private final String myText;
  private final long myCreationTime = System.currentTimeMillis();
  private Throwable myException;
  private String myHelpUrl;
  private Object myHintObject;

  public Message(MessageKind kind, @Nullable String sender, String text) {
    myKind = kind;
    mySender = sender;
    myText = text;
  }

  public Message(MessageKind kind, @NotNull Class sender, String text) {
    this(kind, sender.getSimpleName(), text);
  }

  public Message(MessageKind kind, String text) {
    this(kind, (String) null, text);
  }

  @Override
  public String getHelpUrl() {
    return myHelpUrl;
  }

  public void setHelpUrl(String helpUrl) {
    myHelpUrl = helpUrl;
  }

  /**
   * @return <code>this</code> for convenience
   */
  public Message setException(@Nullable Throwable exception) {
    myException = exception;
    return this;
  }

  @Override
  public MessageKind getKind() {
    return myKind;
  }

  @Override
  public Throwable getException() {
    return myException;
  }

  @Override
  public String getText() {
    return myText;
  }

  @Override
  public String getSender() {
    return mySender;
  }

  public String toString() {
    String prefix = ((mySender == null) ? "" : "[" + mySender + "] ");
    return prefix + getText();
  }

  @Override
  public long getCreationTime() {
    return myCreationTime;
  }

  /**
   * @return <code>this</code> for convenience
   */
  public Message setHintObject(@Nullable Object obj) {
    boolean error = true;
    if (obj instanceof SNode) {
      myHintObject = ((SNode) obj).getReference();
    } else if (obj instanceof SModel) {
      myHintObject = ((SModel) obj).getReference();
    } else if (obj instanceof SModule) {
      myHintObject = ((SModule) obj).getModuleReference();
    } else {
      myHintObject = obj;
      error = false;
    }

    if (error) {
      LOG.error("Adding a message with " + obj.getClass().getSimpleName() + " hint object. This can lead to memleaks. Changing hint object to a reference.", new Throwable());
    }
    return this;
  }

  @Override
  public Object getHintObject() {
    return myHintObject;
  }

  public static IMessage createMessage(@NotNull MessageKind kind, @NotNull String sender, @NotNull String text) {
    return createMessage(kind, sender, text, null);
  }

  public static IMessage createMessage(@NotNull MessageKind kind, @NotNull String sender, @NotNull String text, @Nullable Object hint) {
    Message m = new Message(kind, sender, text);
    m.setHintObject(hint);
    return m;
  }

  public static IMessage createMessage(@NotNull MessageKind kind, @NotNull String sender, @NotNull String text, @Nullable Throwable ex) {
    Message m = new Message(kind, sender, text);
    if (ex != null) {
      m.setException(ex);
    }
    return m;
  }

  public static IMessage info(@NotNull Class<?> sender, @NotNull String text, @Nullable Object hint, @Nullable Throwable ex) {
    return new Message(MessageKind.INFORMATION, sender, text).setHintObject(hint).setException(ex);
  }

  public static IMessage error(@NotNull Class<?> sender, @NotNull String text, @Nullable Object hint, @Nullable Throwable ex) {
    return new Message(MessageKind.ERROR, sender, text).setHintObject(hint).setException(ex);
  }
}
