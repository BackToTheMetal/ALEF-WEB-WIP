package com.intellij.openapi.command;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
public class CommandEvent extends java.util.EventObject {
  private final Runnable command;
  private final Project project;
  private final String name;
  private final Object group;
  private final UndoConfirmationPolicy policy;
  private final Document document;
  public CommandEvent(CommandProcessor processor, Runnable command, Project project, UndoConfirmationPolicy policy) {
    this(processor, command, processor.getCurrentCommandName(), processor.getCurrentCommandGroupId(), project, policy, true, null);
  }
  public CommandEvent(CommandProcessor processor, Runnable command, String name, Object group, Project project, UndoConfirmationPolicy policy, boolean original, Document document) {
    super(processor); this.command = command; this.project = project; this.name = name; this.group = group; this.policy = policy; this.document = document;
  }
  public CommandProcessor getCommandProcessor() { return (CommandProcessor) getSource(); }
  public Runnable getCommand() { return command; }
  public Project getProject() { return project; }
  public String getCommandName() { return name; }
  public Object getCommandGroupId() { return group; }
  public UndoConfirmationPolicy getUndoConfirmationPolicy() { return policy; }
  public Document getDocument() { return document; }
  public boolean shouldRecordActionForOriginalDocument() { return true; }
}
