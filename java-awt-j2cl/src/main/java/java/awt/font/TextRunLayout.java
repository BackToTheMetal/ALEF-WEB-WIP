package java.awt.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Immutable run snapshot shared by attributed painting and paragraph measurement. */
final class TextRunLayout {
    final String text;
    final int begin;
    final List<Run> runs;

    TextRunLayout(String text, Font font) {
        this.text = text;
        begin = 0;
        runs = new ArrayList<>();
        runs.add(new Run(0, text.length(), Collections.singletonMap(TextAttribute.FONT, font)));
    }

    TextRunLayout(AttributedCharacterIterator iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("iterator must not be null");
        }
        begin = iterator.getBeginIndex();
        runs = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        int saved = iterator.getIndex();
        try {
            for (int index = begin; index < iterator.getEndIndex();) {
                iterator.setIndex(index);
                int limit = Math.min(iterator.getEndIndex(), iterator.getRunLimit());
                if (limit <= index) {
                    throw new IllegalArgumentException("Iterator run must advance");
                }
                runs.add(new Run(index - begin, limit - begin, iterator.getAttributes()));
                for (; index < limit; index++) {
                    value.append(iterator.setIndex(index));
                }
            }
        } finally {
            iterator.setIndex(saved);
        }
        text = value.toString();
    }

    private TextRunLayout(String text, int begin, List<Run> runs) {
        this.text = text;
        this.begin = begin;
        this.runs = runs;
    }

    TextRunLayout slice(int start, int limit) {
        if (start < 0 || limit < start || limit > text.length()) {
            throw new IndexOutOfBoundsException("Invalid run range");
        }
        List<Run> selected = new ArrayList<>();
        for (Run run : runs) {
            int first = Math.max(start, run.start);
            int last = Math.min(limit, run.limit);
            if (first < last) {
                selected.add(new Run(first - start, last - start, run.attributes));
            }
        }
        return new TextRunLayout(text.substring(start, limit), begin + start, selected);
    }

    boolean rightToLeft() {
        if (!runs.isEmpty()) {
            Object direction = runs.get(0).attributes.get(TextAttribute.RUN_DIRECTION);
            if (direction instanceof Boolean) { return (Boolean) direction; }
        }
        for (int index = 0; index < text.length(); index++) {
            char value = text.charAt(index);
            if (value >= 0x0590 && value <= 0x08ff) { return true; }
            if (Character.isLetter(value)) { return false; }
        }
        return false;
    }

    boolean mixedDirections() {
        boolean left = false;
        boolean right = false;
        for (int index = 0; index < text.length(); index++) {
            char value = text.charAt(index);
            if (value >= 0x05d0 && value <= 0x05ea || value >= 0x0620 && value <= 0x064a || value >= 0x066e && value <= 0x06d3) {
                right = true;
            } else if (Character.isLetter(value)) {
                left = true;
            }
        }
        return left && right;
    }

    Font firstFont() {
        return runs.isEmpty() ? new Font(Font.DIALOG, Font.PLAIN, 12) : runs.get(0).font;
    }

    TextRunLayout scaled(float scale) {
        List<Run> selected = new ArrayList<>();
        for (Run run : runs) {
            Map<AttributedCharacterIterator.Attribute, Object> attributes = new HashMap<>(run.attributes);
            java.awt.geom.AffineTransform transform = run.font.getTransform();
            transform.scale(scale, 1);
            attributes.put(TextAttribute.FONT, run.font.deriveFont(transform));
            selected.add(new Run(run.start, run.limit, attributes));
        }
        return new TextRunLayout(text, begin, selected);
    }

    boolean sameRuns(TextRunLayout other) {
        if (runs.size() != other.runs.size()) {
            return false;
        }
        for (int index = 0; index < runs.size(); index++) {
            Run first = runs.get(index);
            Run second = other.runs.get(index);
            if (first.start != second.start || first.limit != second.limit || !first.attributes.equals(second.attributes)) {
                return false;
            }
        }
        return true;
    }

    float advance(int start, int limit, FontRenderContext context) {
        float result = 0;
        for (Run run : runs) {
            int first = Math.max(start, run.start);
            int last = Math.min(limit, run.limit);
            if (first < last) {
                result += run.font.getStringBounds(text, first, last, context).getWidth();
            }
        }
        return result;
    }

    float[] positions(FontRenderContext context) {
        float[] positions = new float[text.length() + 1];
        float origin = 0;
        for (Run run : runs) {
            for (int index = run.start + 1; index <= run.limit; index++) {
                positions[index] = origin + (float) run.font.getStringBounds(text, run.start, index, context).getWidth();
            }
            origin = positions[run.limit];
        }
        return positions;
    }

    float metric(FontRenderContext context, int metric) {
        float result = 0;
        for (Run run : runs) {
            LineMetrics line = run.font.getLineMetrics(text.substring(run.start, run.limit), context);
            result = Math.max(result, metric == 0 ? line.getAscent() : metric == 1 ? line.getDescent() : line.getLeading());
        }
        return result;
    }

    void draw(Graphics2D graphics, float x, float y, FontRenderContext context) {
        if (runs.size() > 1 && mixedDirections()) {
            throw new UnsupportedOperationException("Mixed-direction attributed runs require a bidi layout engine");
        }
        boolean rtl = rightToLeft();
        float cursor = rtl ? x + advance(0, text.length(), context) : x;
        for (Run run : runs) {
            String value = text.substring(run.start, run.limit);
            float width = (float) run.font.getStringBounds(value, context).getWidth();
            if (rtl) { cursor -= width; }
            Graphics2D child = (Graphics2D) graphics.create();
            try {
                child.setFont(run.font);
                Paint foreground = run.attributes.get(TextAttribute.FOREGROUND) instanceof Paint ? (Paint) run.attributes.get(TextAttribute.FOREGROUND) : child.getPaint();
                Paint background = run.attributes.get(TextAttribute.BACKGROUND) instanceof Paint ? (Paint) run.attributes.get(TextAttribute.BACKGROUND) : null;
                if (Boolean.TRUE.equals(run.attributes.get(TextAttribute.SWAP_COLORS))) {
                    Paint swap = foreground;
                    foreground = background == null ? Color.WHITE : background;
                    background = swap;
                }
                LineMetrics line = run.font.getLineMetrics(value, context);
                if (background != null) {
                    child.setPaint(background);
                    child.fill(new Rectangle2D.Float(cursor, y - line.getAscent(), width, line.getAscent() + line.getDescent() + line.getLeading()));
                }
                child.setPaint(foreground);
                child.drawString(value, cursor, y);
                if (TextAttribute.UNDERLINE_ON.equals(run.attributes.get(TextAttribute.UNDERLINE))) {
                    child.fill(new Rectangle2D.Float(cursor, y + line.getUnderlineOffset(), width, Math.max(1, line.getUnderlineThickness())));
                }
                if (Boolean.TRUE.equals(run.attributes.get(TextAttribute.STRIKETHROUGH))) {
                    child.fill(new Rectangle2D.Float(cursor, y + line.getStrikethroughOffset(), width, Math.max(1, line.getStrikethroughThickness())));
                }
            } finally {
                child.dispose();
            }
            if (!rtl) { cursor += width; }
        }
    }

    private static final class Run {
        final int start;
        final int limit;
        final Font font;
        final Map<AttributedCharacterIterator.Attribute, Object> attributes;

        Run(int start, int limit, Map<? extends AttributedCharacterIterator.Attribute, ?> attributes) {
            this.start = start;
            this.limit = limit;
            this.attributes = Collections.unmodifiableMap(new HashMap<>(attributes));
            font = attributes.isEmpty() ? new Font(Font.DIALOG, Font.PLAIN, 12) : Font.getFont(attributes);
            if (attributes.containsKey(TextAttribute.CHAR_REPLACEMENT) || attributes.containsKey(TextAttribute.NUMERIC_SHAPING) || attributes.containsKey(TextAttribute.BIDI_EMBEDDING)) {
                throw new UnsupportedOperationException("Replacement glyphs, numeric shaping and explicit bidi embeddings are outside the attributed text profile");
            }
        }
    }
}
