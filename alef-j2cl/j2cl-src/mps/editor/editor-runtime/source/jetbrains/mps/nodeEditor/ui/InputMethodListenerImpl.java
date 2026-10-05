/*
 * Copyright 2003-2017 JetBrains s.r.o.
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
package jetbrains.mps.nodeEditor.ui;

import jetbrains.mps.nodeEditor.EditorComponent;
import jetbrains.mps.nodeEditor.keyboard.TextChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;

/**
 * User: shatalin
 * Date: 10/03/2017
 */
public class InputMethodListenerImpl implements InputMethodListener {
  private final EditorComponent myEditorComponent;
  private String myUncommittedText = "";

  InputMethodListenerImpl(EditorComponent editorComponent) {
    myEditorComponent = editorComponent;
  }

  @Override
  public void inputMethodTextChanged(InputMethodEvent event) {
    // Browser text insertion/IME dispatches here without a key-typed event.
    if (myEditorComponent.getEditorContext().getRepository().getModelAccess().computeReadAction(() ->
        jetbrains.mps.editor.runtime.cells.ReadOnlyUtil.isSelectionReadOnlyInEditor(myEditorComponent))) {
      myUncommittedText = "";
      event.consume();
      return;
    }
    if (myEditorComponent.peekKeyboardHandler().processTextChanged(myEditorComponent.getEditorContext(), createTextChangeEvent(event))) {
      event.consume();
      myEditorComponent.repaintExternalComponent();
    }
  }

  @Override
  public void caretPositionChanged(InputMethodEvent event) {
  }

  @NotNull
  private TextChangeEvent createTextChangeEvent(InputMethodEvent event) {
    String text = extractText(event);
    int committedCharacterCount = event.getCommittedCharacterCount();
    int charactersToReplace = myUncommittedText.length();

    myUncommittedText = text.substring(committedCharacterCount);
    return new TextChangeEvent(text, charactersToReplace);
  }

  /**
   * Can be used to get text, entered by user, from the input event (if applicable).
   *
   * @param event {@link InputMethodEvent}
   * @return text, entered by user
   */
  @NotNull
  private String extractText(@NotNull InputMethodEvent event) {
    AttributedCharacterIterator text = event.getText();
    if (text == null) {
      return "";
    }
    StringBuilder replacement = new StringBuilder();
    for (char c = text.first(); c != CharacterIterator.DONE; c = text.next()) {
      replacement.append(c);
    }
    return replacement.toString();
  }
}
