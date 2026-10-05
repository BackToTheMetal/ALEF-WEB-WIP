package java.io;

/** Buffered reader that tracks line endings consumed through its own read methods. */
public class LineNumberReader extends BufferedReader {
  private int lineNumber;
  private int markedLineNumber;
  private boolean lastWasCarriageReturn;

  public LineNumberReader(Reader input) {
    super(input);
  }

  public LineNumberReader(Reader input, int size) {
    super(input, size);
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public int read() throws IOException {
    int value = super.read();
    if (value == '\r') {
      lineNumber++;
      lastWasCarriageReturn = true;
    } else if (value == '\n') {
      if (!lastWasCarriageReturn) {
        lineNumber++;
      }
      lastWasCarriageReturn = false;
    } else if (value != -1) {
      lastWasCarriageReturn = false;
    }
    return value;
  }

  @Override
  public int read(char[] buffer, int offset, int length) throws IOException {
    int count = super.read(buffer, offset, length);
    for (int i = 0; i < count; i++) {
      char value = buffer[offset + i];
      if (value == '\r') {
        lineNumber++;
        lastWasCarriageReturn = true;
      } else if (value == '\n') {
        if (!lastWasCarriageReturn) {
          lineNumber++;
        }
        lastWasCarriageReturn = false;
      } else {
        lastWasCarriageReturn = false;
      }
    }
    return count;
  }

  @Override
  public String readLine() throws IOException {
    int lineNumberBeforeRead = lineNumber;
    String line = super.readLine();
    // BufferedReader.readLine() dispatches to this class' read(), so a physical
    // line terminator has already advanced the counter. Only an unterminated
    // final line needs an explicit increment here.
    if (line != null && lineNumber == lineNumberBeforeRead) {
      lineNumber++;
    }
    lastWasCarriageReturn = false;
    return line;
  }

  @Override
  public void mark(int readAheadLimit) throws IOException {
    super.mark(readAheadLimit);
    markedLineNumber = lineNumber;
  }

  @Override
  public void reset() throws IOException {
    super.reset();
    lineNumber = markedLineNumber;
    lastWasCarriageReturn = false;
  }
}
