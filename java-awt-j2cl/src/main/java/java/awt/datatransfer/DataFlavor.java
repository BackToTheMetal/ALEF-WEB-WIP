package java.awt.datatransfer;

import java.io.Serializable;
import java.util.Locale;

public class DataFlavor implements Serializable, Cloneable {
    public static final String javaSerializedObjectMimeType = "application/x-java-serialized-object";
    public static final String javaJVMLocalObjectMimeType = "application/x-java-jvm-local-objectref";
    public static final String javaRemoteObjectMimeType = "application/x-java-remote-object";
    public static final DataFlavor stringFlavor = new DataFlavor(String.class, "Unicode String");
    public static final DataFlavor plainTextFlavor = new DataFlavor("text/plain;charset=unicode", "Plain Text");
    public static final DataFlavor javaFileListFlavor = new DataFlavor("application/x-java-file-list", "Java File List");
    public static final DataFlavor imageFlavor = new DataFlavor("image/x-java-image", "Image");
    public static final DataFlavor selectionHtmlFlavor = new DataFlavor("text/html;document=selection", "HTML selection");
    public static final DataFlavor fragmentHtmlFlavor = new DataFlavor("text/html;document=fragment", "HTML fragment");
    public static final DataFlavor allHtmlFlavor = new DataFlavor("text/html;document=all", "HTML document");
    private String humanPresentableName;
    private String mimeType;
    private Class<?> representationClass;

    public DataFlavor() {
        this("application/octet-stream", "application/octet-stream");
    }

    public DataFlavor(Class<?> representationClass, String humanPresentableName) {
        if (representationClass == null) {
            throw new NullPointerException("representationClass");
        }
        this.representationClass = representationClass;
        mimeType = javaSerializedObjectMimeType;
        this.humanPresentableName = humanPresentableName == null ? mimeType : humanPresentableName;
    }

    public DataFlavor(String mimeType, String humanPresentableName) {
        initialize(mimeType, humanPresentableName);
    }

    public String getMimeType() {
        return mimeType;
    }

    public Class<?> getRepresentationClass() {
        return representationClass;
    }

    public String getHumanPresentableName() {
        return humanPresentableName;
    }

    public void setHumanPresentableName(String name) {
        humanPresentableName = name;
    }

    public String getPrimaryType() {
        int slash = mimeType.indexOf('/');
        return slash < 0 ? mimeType : mimeType.substring(0, slash);
    }

    public String getSubType() {
        int slash = mimeType.indexOf('/');
        int semicolon = mimeType.indexOf(';');
        int end = semicolon < 0 ? mimeType.length() : semicolon;
        return slash < 0 ? "" : mimeType.substring(slash + 1, end);
    }

    public String getParameter(String parameterName) {
        if (parameterName == null) {
            throw new NullPointerException("parameterName");
        }
        if (parameterName.equalsIgnoreCase("humanPresentableName")) {
            return humanPresentableName;
        }
        String key = parameterName.toLowerCase(Locale.ROOT) + "=";
        int start = mimeType.toLowerCase(Locale.ROOT).indexOf(key);
        if (start < 0) {
            return null;
        }
        start += key.length();
        int end = mimeType.indexOf(';', start);
        return mimeType.substring(start, end < 0 ? mimeType.length() : end).trim();
    }

    public boolean isMimeTypeEqual(String mimeType) {
        if (mimeType == null) {
            throw new NullPointerException("mimeType");
        }
        return baseMimeType(this.mimeType).equals(baseMimeType(mimeType));
    }

    public boolean isMimeTypeEqual(DataFlavor dataFlavor) {
        return dataFlavor != null && isMimeTypeEqual(dataFlavor.mimeType);
    }

    public boolean isFlavorJavaFileListType() {
        return isMimeTypeEqual(javaFileListFlavor);
    }

    public boolean isFlavorSerializedObjectType() {
        return isMimeTypeEqual(javaSerializedObjectMimeType);
    }

    public boolean isRepresentationClassInputStream() {
        return false;
    }

    public boolean isRepresentationClassReader() {
        return false;
    }

    public boolean isRepresentationClassCharBuffer() {
        return false;
    }

    public boolean isRepresentationClassByteBuffer() {
        return false;
    }

    public boolean isRepresentationClassSerializable() {
        return representationClass == String.class;
    }

    public boolean isRepresentationClassRemote() {
        return false;
    }

    public boolean isFlavorTextType() {
        return getPrimaryType().equals("text") || equals(stringFlavor);
    }

    public Object clone() throws CloneNotSupportedException {
        DataFlavor copy = new DataFlavor(mimeType, humanPresentableName);
        copy.representationClass = representationClass;
        return copy;
    }

    @Override
    public int hashCode() {
        return 31 * baseMimeType(mimeType).hashCode() + (representationClass == null ? 0 : representationClass.hashCode());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DataFlavor)) {
            return false;
        }
        DataFlavor flavor = (DataFlavor) object;
        return isMimeTypeEqual(flavor) && representationClass == flavor.representationClass;
    }

    public boolean equals(DataFlavor flavor) {
        return equals((Object) flavor);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[mimetype=" + mimeType + ";representationclass=" + representationClass + "]";
    }

    private void initialize(String mimeType, String humanPresentableName) {
        if (mimeType == null) {
            throw new NullPointerException("mimeType");
        }
        this.mimeType = mimeType.toLowerCase(Locale.ROOT);
        this.humanPresentableName = humanPresentableName == null ? mimeType : humanPresentableName;
        representationClass = isFlavorTextTypeName(mimeType) ? String.class : Object.class;
    }

    private static String baseMimeType(String value) {
        int semicolon = value.indexOf(';');
        return value.substring(0, semicolon < 0 ? value.length() : semicolon).trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isFlavorTextTypeName(String value) {
        return baseMimeType(value).startsWith("text/");
    }
    public boolean equals(String mimeType) { return isMimeTypeEqual(mimeType); }
    public boolean match(DataFlavor flavor) { return equals(flavor); }
    public boolean isMimeTypeSerializedObject() { return isMimeTypeEqual(javaSerializedObjectMimeType); }
    public boolean isFlavorRemoteObjectType() { return false; }
    protected String normalizeMimeTypeParameter(String name, String value) { return value; }
    protected String normalizeMimeType(String mimeType) { return mimeType; }
}
