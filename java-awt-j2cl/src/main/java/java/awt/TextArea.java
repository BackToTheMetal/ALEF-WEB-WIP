package java.awt;

public class TextArea extends TextComponent {
    public static final int SCROLLBARS_BOTH = 0;
    public static final int SCROLLBARS_VERTICAL_ONLY = 1;
    public static final int SCROLLBARS_HORIZONTAL_ONLY = 2;
    public static final int SCROLLBARS_NONE = 3;
    private final int scrollbarVisibility;
    private int columns;
    private int rows;

    public TextArea() throws HeadlessException {
        this("", 0, 0, SCROLLBARS_BOTH);
    }

    public TextArea(String text) throws HeadlessException {
        this(text, 0, 0, SCROLLBARS_BOTH);
    }

    public TextArea(int rows, int columns) throws HeadlessException {
        this("", rows, columns, SCROLLBARS_BOTH);
    }

    public TextArea(String text, int rows, int columns) throws HeadlessException {
        this(text, rows, columns, SCROLLBARS_BOTH);
    }

    public TextArea(String text, int rows, int columns, int scrollbarVisibility) throws HeadlessException {
        super("textarea", text);
        if (rows < 0 || columns < 0) {
            throw new IllegalArgumentException("rows and columns must be non-negative");
        }
        this.rows = rows;
        this.columns = columns;
        this.scrollbarVisibility = scrollbarVisibility < SCROLLBARS_BOTH || scrollbarVisibility > SCROLLBARS_NONE ? SCROLLBARS_BOTH : scrollbarVisibility;
    }

    public void insert(String text, int position) {
        insertText(text, position);
    }

    public synchronized void insertText(String text, int position) {
        String current = getText();
        if (position < 0 || position > current.length()) {
            throw new IllegalArgumentException("position out of range");
        }
        setText(current.substring(0, position) + text + current.substring(position));
    }

    public void append(String text) {
        appendText(text);
    }

    public synchronized void appendText(String text) {
        setText(getText() + text);
    }

    public void replaceRange(String text, int start, int end) {
        replaceText(text, start, end);
    }

    public synchronized void replaceText(String text, int start, int end) {
        String current = getText();
        if (start < 0 || end < start || end > current.length()) {
            throw new IllegalArgumentException("replacement range out of bounds");
        }
        setText(current.substring(0, start) + text + current.substring(end));
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows < 0) {
            throw new IllegalArgumentException("rows less than zero");
        }
        this.rows = rows;
        invalidate();
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        if (columns < 0) {
            throw new IllegalArgumentException("columns less than zero");
        }
        this.columns = columns;
        invalidate();
    }

    public int getScrollbarVisibility() {
        return scrollbarVisibility;
    }

    public Dimension getPreferredSize(int rows, int columns) {
        return new Dimension(16 + Math.max(0, columns) * 8, 16 + Math.max(0, rows) * 18);
    }

    public Dimension preferredSize(int rows, int columns) {
        return getPreferredSize(rows, columns);
    }

    @Override
    public Dimension getPreferredSize() {
        return isPreferredSizeSet() ? super.getPreferredSize() : getPreferredSize(rows, columns);
    }

    public Dimension getMinimumSize(int rows, int columns) {
        return getPreferredSize(rows, columns);
    }

    public Dimension minimumSize(int rows, int columns) {
        return getMinimumSize(rows, columns);
    }

    @Override
    public Dimension getMinimumSize() {
        return isMinimumSizeSet() ? super.getMinimumSize() : getMinimumSize(rows, columns);
    }
}
