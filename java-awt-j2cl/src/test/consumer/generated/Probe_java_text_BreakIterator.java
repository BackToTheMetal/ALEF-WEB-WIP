package java.text;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_text_BreakIterator {
    static void call0() { int value = java.text.BreakIterator.DONE; }
    static void call1(java.text.BreakIterator receiver)  { java.lang.Object value = receiver.clone(); }
    static void call2(java.text.BreakIterator receiver)  { int value = receiver.first(); }
    static void call3(java.text.BreakIterator receiver)  { int value = receiver.last(); }
    static void call4(java.text.BreakIterator receiver)  { int value = receiver.next((int) 0); }
    static void call5(java.text.BreakIterator receiver)  { int value = receiver.next(); }
    static void call6(java.text.BreakIterator receiver)  { int value = receiver.previous(); }
    static void call7(java.text.BreakIterator receiver)  { int value = receiver.following((int) 0); }
    static void call8(java.text.BreakIterator receiver)  { int value = receiver.preceding((int) 0); }
    static void call9(java.text.BreakIterator receiver)  { boolean value = receiver.isBoundary((int) 0); }
    static void call10(java.text.BreakIterator receiver)  { int value = receiver.current(); }
    static void call11(java.text.BreakIterator receiver)  { java.text.CharacterIterator value = receiver.getText(); }
    static void call12(java.text.BreakIterator receiver)  { receiver.setText((java.lang.String) null); }
    static void call13(java.text.BreakIterator receiver)  { receiver.setText((java.text.CharacterIterator) null); }
    static void call14()  { java.text.BreakIterator value = java.text.BreakIterator.getWordInstance(); }
    static void call15()  { java.text.BreakIterator value = java.text.BreakIterator.getWordInstance((java.util.Locale) null); }
    static void call16()  { java.text.BreakIterator value = java.text.BreakIterator.getLineInstance(); }
    static void call17()  { java.text.BreakIterator value = java.text.BreakIterator.getLineInstance((java.util.Locale) null); }
    static void call18()  { java.text.BreakIterator value = java.text.BreakIterator.getCharacterInstance(); }
    static void call19()  { java.text.BreakIterator value = java.text.BreakIterator.getCharacterInstance((java.util.Locale) null); }
    static void call20()  { java.text.BreakIterator value = java.text.BreakIterator.getSentenceInstance(); }
    static void call21()  { java.text.BreakIterator value = java.text.BreakIterator.getSentenceInstance((java.util.Locale) null); }
    static void call22()  { java.util.Locale[] value = java.text.BreakIterator.getAvailableLocales(); }
}

abstract class Override_java_text_BreakIterator extends java.text.BreakIterator {
    Override_java_text_BreakIterator()  { super(); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
    @Override
    public int first()  { throw new UnsupportedOperationException(); }
    @Override
    public int last()  { throw new UnsupportedOperationException(); }
    @Override
    public int next(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int next()  { throw new UnsupportedOperationException(); }
    @Override
    public int previous()  { throw new UnsupportedOperationException(); }
    @Override
    public int following(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int preceding(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isBoundary(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int current()  { throw new UnsupportedOperationException(); }
    @Override
    public java.text.CharacterIterator getText()  { throw new UnsupportedOperationException(); }
    @Override
    public void setText(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setText(java.text.CharacterIterator arg0)  { throw new UnsupportedOperationException(); }
}
