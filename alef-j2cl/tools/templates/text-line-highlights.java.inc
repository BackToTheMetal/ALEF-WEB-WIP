  private int[] myTextHighlights;
  private Color myHighlightForeground;
  private Color myHighlightBackground;

  /** Optional presentation-only ranges; model contents and cell geometry stay native. */
  public void setTextHighlights(int[] ranges, Color foreground, Color background) {
    myTextHighlights = ranges;
    myHighlightForeground = foreground;
    myHighlightBackground = background;
  }

  public void setTextSelectionColors(Color foreground, Color background) {
    myTextSelectedTextColor = foreground;
    myTextSelectedBackgroundColor = background;
  }

  private void paintTextHighlights(Graphics graphics, int shiftX, int shiftY) {
    if (myTextHighlights == null) return;
    for (int i = 0; i < myTextHighlights.length; i += 2) {
      int start = getTextWidth(myTextHighlights[i]);
      int end = getTextWidth(myTextHighlights[i + 1]);
      int x = shiftX + getPaddingLeft() + start;
      int y = shiftY + getPaddingTop();
      Graphics g = graphics.create();
      try {
        g.clipRect(x, y, Math.max(2, end - start), myTextHeight);
        g.setColor(myHighlightBackground);
        g.fillRect(x, y, Math.max(2, end - start), myTextHeight);
        g.setFont(getFont());
        g.setColor(myHighlightForeground);
        int baseline = shiftY + myHeight - myDescent - getPaddingBottom();
        // Draw the whole shaped run through the clip, preserving kerning/ligatures.
        g.drawString(myText, shiftX + getPaddingLeft() + myFontCorrectionTextShift, baseline);
        if (isUnderlined()) g.drawLine(x, baseline + 1, x + end - start, baseline + 1);
      } finally { g.dispose(); }
    }
  }
