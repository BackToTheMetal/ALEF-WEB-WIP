package java.awt;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RenderingHints implements Map<Object, Object>, Cloneable {
    public abstract static class Key {
        private static final Map<Class<?>, Set<Integer>> IDENTITIES = new HashMap<>();
        private final int privateKey;

        protected Key(int privateKey) {
            this.privateKey = privateKey;
            registerIdentity(this);
        }

        public abstract boolean isCompatibleValue(Object value);

        protected final int intKey() {
            return privateKey;
        }

        @Override
        public final int hashCode() {
            return System.identityHashCode(this);
        }

        @Override
        public final boolean equals(Object object) {
            return this == object;
        }

        private static void registerIdentity(Key key) {
            Class<?> keyClass = key.getClass();
            Set<Integer> classKeys = IDENTITIES.get(keyClass);
            if (classKeys == null) {
                classKeys = new HashSet<>();
                IDENTITIES.put(keyClass, classKeys);
            }
            if (!classKeys.add(key.privateKey)) {
                throw new IllegalArgumentException("Duplicate key " + key.privateKey + " for " + keyClass.getName());
            }
        }
    }

    public static final Key KEY_ANTIALIASING = new HintKey(1, "Global antialiasing enable key");
    public static final Object VALUE_ANTIALIAS_ON = new HintValue(KEY_ANTIALIASING, "Antialiased rendering mode");
    public static final Object VALUE_ANTIALIAS_OFF = new HintValue(KEY_ANTIALIASING, "Nonantialiased rendering mode");
    public static final Object VALUE_ANTIALIAS_DEFAULT = new HintValue(KEY_ANTIALIASING, "Default antialiasing rendering mode");
    public static final Key KEY_RENDERING = new HintKey(0, "Global rendering quality key");
    public static final Object VALUE_RENDER_SPEED = new HintValue(KEY_RENDERING, "Fastest rendering methods");
    public static final Object VALUE_RENDER_QUALITY = new HintValue(KEY_RENDERING, "Highest quality rendering methods");
    public static final Object VALUE_RENDER_DEFAULT = new HintValue(KEY_RENDERING, "Default rendering methods");
    public static final Key KEY_DITHERING = new HintKey(4, "Dithering quality key");
    public static final Object VALUE_DITHER_DISABLE = new HintValue(KEY_DITHERING, "Nondithered rendering mode");
    public static final Object VALUE_DITHER_ENABLE = new HintValue(KEY_DITHERING, "Dithered rendering mode");
    public static final Object VALUE_DITHER_DEFAULT = new HintValue(KEY_DITHERING, "Default dithering mode");
    public static final Key KEY_TEXT_ANTIALIASING = new HintKey(2, "Text-specific antialiasing enable key");
    public static final Object VALUE_TEXT_ANTIALIAS_ON = new HintValue(KEY_TEXT_ANTIALIASING, "Antialiased text mode");
    public static final Object VALUE_TEXT_ANTIALIAS_OFF = new HintValue(KEY_TEXT_ANTIALIASING, "Nonantialiased text mode");
    public static final Object VALUE_TEXT_ANTIALIAS_DEFAULT = new HintValue(KEY_TEXT_ANTIALIASING, "Default antialiasing text mode");
    public static final Object VALUE_TEXT_ANTIALIAS_GASP = new HintValue(KEY_TEXT_ANTIALIASING, "gasp antialiasing text mode");
    public static final Object VALUE_TEXT_ANTIALIAS_LCD_HRGB = new HintValue(KEY_TEXT_ANTIALIASING, "LCD HRGB antialiasing text mode");
    public static final Object VALUE_TEXT_ANTIALIAS_LCD_HBGR = new HintValue(KEY_TEXT_ANTIALIASING, "LCD HBGR antialiasing text mode");
    public static final Object VALUE_TEXT_ANTIALIAS_LCD_VRGB = new HintValue(KEY_TEXT_ANTIALIASING, "LCD VRGB antialiasing text mode");
    public static final Object VALUE_TEXT_ANTIALIAS_LCD_VBGR = new HintValue(KEY_TEXT_ANTIALIASING, "LCD VBGR antialiasing text mode");
    public static final Key KEY_TEXT_LCD_CONTRAST = new HintKey(100, "Text-specific LCD contrast key");
    public static final Key KEY_FRACTIONALMETRICS = new HintKey(3, "Fractional metrics enable key");
    public static final Object VALUE_FRACTIONALMETRICS_OFF = new HintValue(KEY_FRACTIONALMETRICS, "Integer text metrics mode");
    public static final Object VALUE_FRACTIONALMETRICS_ON = new HintValue(KEY_FRACTIONALMETRICS, "Fractional text metrics mode");
    public static final Object VALUE_FRACTIONALMETRICS_DEFAULT = new HintValue(KEY_FRACTIONALMETRICS, "Default fractional text metrics mode");
    public static final Key KEY_INTERPOLATION = new HintKey(5, "Image interpolation method key");
    public static final Object VALUE_INTERPOLATION_NEAREST_NEIGHBOR = new HintValue(KEY_INTERPOLATION, "Nearest Neighbor image interpolation mode");
    public static final Object VALUE_INTERPOLATION_BILINEAR = new HintValue(KEY_INTERPOLATION, "Bilinear image interpolation mode");
    public static final Object VALUE_INTERPOLATION_BICUBIC = new HintValue(KEY_INTERPOLATION, "Bicubic image interpolation mode");
    public static final Key KEY_ALPHA_INTERPOLATION = new HintKey(6, "Alpha blending interpolation method key");
    public static final Object VALUE_ALPHA_INTERPOLATION_SPEED = new HintValue(KEY_ALPHA_INTERPOLATION, "Fastest alpha blending methods");
    public static final Object VALUE_ALPHA_INTERPOLATION_QUALITY = new HintValue(KEY_ALPHA_INTERPOLATION, "Highest quality alpha blending methods");
    public static final Object VALUE_ALPHA_INTERPOLATION_DEFAULT = new HintValue(KEY_ALPHA_INTERPOLATION, "Default alpha blending methods");
    public static final Key KEY_COLOR_RENDERING = new HintKey(7, "Color rendering quality key");
    public static final Object VALUE_COLOR_RENDER_SPEED = new HintValue(KEY_COLOR_RENDERING, "Fastest color rendering mode");
    public static final Object VALUE_COLOR_RENDER_QUALITY = new HintValue(KEY_COLOR_RENDERING, "Highest quality color rendering mode");
    public static final Object VALUE_COLOR_RENDER_DEFAULT = new HintValue(KEY_COLOR_RENDERING, "Default color rendering mode");
    public static final Key KEY_STROKE_CONTROL = new HintKey(8, "Stroke normalization control key");
    public static final Object VALUE_STROKE_DEFAULT = new HintValue(KEY_STROKE_CONTROL, "Default stroke normalization");
    public static final Object VALUE_STROKE_NORMALIZE = new HintValue(KEY_STROKE_CONTROL, "Normalize strokes for consistent rendering");
    public static final Object VALUE_STROKE_PURE = new HintValue(KEY_STROKE_CONTROL, "Pure stroke conversion for accurate paths");
    public static final Key KEY_RESOLUTION_VARIANT = new HintKey(9, "Global image resolution variant key");
    public static final Object VALUE_RESOLUTION_VARIANT_DEFAULT = new HintValue(KEY_RESOLUTION_VARIANT, "Choose image resolutions based on a default heuristic");
    public static final Object VALUE_RESOLUTION_VARIANT_BASE = new HintValue(KEY_RESOLUTION_VARIANT, "Use only the standard resolution of an image");
    public static final Object VALUE_RESOLUTION_VARIANT_SIZE_FIT = new HintValue(KEY_RESOLUTION_VARIANT, "Choose image resolutions based on the DPI of the screen and transform in the Graphics2D context");
    public static final Object VALUE_RESOLUTION_VARIANT_DPI_FIT = new HintValue(KEY_RESOLUTION_VARIANT, "Choose image resolutions based only on the DPI of the screen");
    private final Map<Object, Object> hintMap;

    public RenderingHints(Map<Key, ?> initialHints) {
        hintMap = new HashMap<>();
        if (initialHints != null) {
            hintMap.putAll(initialHints);
        }
    }

    public RenderingHints(Key key, Object value) {
        hintMap = new HashMap<>();
        hintMap.put(key, value);
    }

    private RenderingHints(RenderingHints renderingHints) {
        hintMap = new HashMap<>();
        hintMap.putAll(renderingHints.hintMap);
    }

    @Override
    public int size() {
        return hintMap.size();
    }

    @Override
    public boolean isEmpty() {
        return hintMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return hintMap.containsKey((Key) key);
    }

    @Override
    public boolean containsValue(Object value) {
        return hintMap.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return hintMap.get((Key) key);
    }

    @Override
    public Object put(Object key, Object value) {
        Key renderingKey = (Key) key;
        if (!renderingKey.isCompatibleValue(value)) {
            throw new IllegalArgumentException(value + " is incompatible with " + renderingKey);
        }
        return hintMap.put(renderingKey, value);
    }

    public void add(RenderingHints renderingHints) {
        hintMap.putAll(renderingHints.hintMap);
    }

    @Override
    public void clear() {
        hintMap.clear();
    }

    @Override
    public Object remove(Object key) {
        return hintMap.remove((Key) key);
    }

    @Override
    public void putAll(Map<?, ?> map) {
        if (map instanceof RenderingHints) {
            hintMap.putAll(((RenderingHints) map).hintMap);
            return;
        }
        for (Entry<?, ?> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<Object> keySet() {
        return hintMap.keySet();
    }

    @Override
    public Collection<Object> values() {
        return hintMap.values();
    }

    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return Collections.unmodifiableMap(hintMap).entrySet();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof RenderingHints) {
            return hintMap.equals(((RenderingHints) object).hintMap);
        }
        return hintMap.equals(object);
    }

    @Override
    public int hashCode() {
        return hintMap.hashCode();
    }

    public Object clone() {
        return new RenderingHints(this);
    }

    @Override
    public String toString() {
        return hintMap.toString();
    }

    private static final class HintKey extends Key {
        private final String description;

        private HintKey(int privateKey, String description) {
            super(privateKey);
            this.description = description;
        }

        @Override
        public boolean isCompatibleValue(Object value) {
            if (intKey() == 100) {
                return value instanceof Integer && (Integer) value >= 100 && (Integer) value <= 250;
            }
            return value instanceof HintValue && ((HintValue) value).key == this;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private static final class HintValue {
        private final Key key;
        private final String description;

        private HintValue(Key key, String description) {
            this.key = key;
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
