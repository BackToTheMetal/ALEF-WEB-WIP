package com.intellij.openapi.wm;
import com.intellij.openapi.project.Project;
import javax.swing.JComponent;
public interface IdeFrame { StatusBar getStatusBar(); Project getProject(); JComponent getComponent(); }
