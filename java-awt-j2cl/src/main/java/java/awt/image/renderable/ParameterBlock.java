package java.awt.image.renderable;

import java.awt.image.RenderedImage;
import java.io.Serializable;
import java.util.Vector;

public class ParameterBlock implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    protected Vector<Object> parameters = new Vector<Object>();
    protected Vector<Object> sources = new Vector<Object>();

    public ParameterBlock() {
    }

    public ParameterBlock(Vector<Object> sources) {
        setSources(sources);
    }

    public ParameterBlock(Vector<Object> sources, Vector<Object> parameters) {
        setSources(sources);
        setParameters(parameters);
    }

    public Object shallowClone() {
        return new ParameterBlock(sources, parameters);
    }

    public Object clone() {
        return new ParameterBlock(copy(sources), copy(parameters));
    }

    public ParameterBlock addSource(Object source) {
        sources.addElement(source);
        return this;
    }

    public Object getSource(int index) {
        return sources.elementAt(index);
    }

    public ParameterBlock setSource(Object source, int index) {
        ensureSize(sources, index);
        sources.setElementAt(source, index);
        return this;
    }

    public RenderedImage getRenderedSource(int index) {
        return (RenderedImage) sources.elementAt(index);
    }

    public RenderableImage getRenderableSource(int index) {
        return (RenderableImage) sources.elementAt(index);
    }

    public int getNumSources() {
        return sources.size();
    }

    public Vector<Object> getSources() {
        return sources;
    }

    public void setSources(Vector<Object> sources) {
        this.sources = sources;
    }

    public void removeSources() {
        sources = new Vector<Object>();
    }

    public int getNumParameters() {
        return parameters.size();
    }

    public Vector<Object> getParameters() {
        return parameters;
    }

    public void setParameters(Vector<Object> parameters) {
        this.parameters = parameters;
    }

    public void removeParameters() {
        parameters = new Vector<Object>();
    }

    public ParameterBlock add(Object parameter) {
        parameters.addElement(parameter);
        return this;
    }

    public ParameterBlock add(byte parameter) {
        return add(Byte.valueOf(parameter));
    }

    public ParameterBlock add(char parameter) {
        return add(Character.valueOf(parameter));
    }

    public ParameterBlock add(short parameter) {
        return add(Short.valueOf(parameter));
    }

    public ParameterBlock add(int parameter) {
        return add(Integer.valueOf(parameter));
    }

    public ParameterBlock add(long parameter) {
        return add(Long.valueOf(parameter));
    }

    public ParameterBlock add(float parameter) {
        return add(Float.valueOf(parameter));
    }

    public ParameterBlock add(double parameter) {
        return add(Double.valueOf(parameter));
    }

    public ParameterBlock set(Object parameter, int index) {
        ensureSize(parameters, index);
        parameters.setElementAt(parameter, index);
        return this;
    }

    public ParameterBlock set(byte parameter, int index) {
        return set(Byte.valueOf(parameter), index);
    }

    public ParameterBlock set(char parameter, int index) {
        return set(Character.valueOf(parameter), index);
    }

    public ParameterBlock set(short parameter, int index) {
        return set(Short.valueOf(parameter), index);
    }

    public ParameterBlock set(int parameter, int index) {
        return set(Integer.valueOf(parameter), index);
    }

    public ParameterBlock set(long parameter, int index) {
        return set(Long.valueOf(parameter), index);
    }

    public ParameterBlock set(float parameter, int index) {
        return set(Float.valueOf(parameter), index);
    }

    public ParameterBlock set(double parameter, int index) {
        return set(Double.valueOf(parameter), index);
    }

    public Object getObjectParameter(int index) {
        return parameters.elementAt(index);
    }

    public byte getByteParameter(int index) {
        return ((Byte) parameters.elementAt(index)).byteValue();
    }

    public char getCharParameter(int index) {
        return ((Character) parameters.elementAt(index)).charValue();
    }

    public short getShortParameter(int index) {
        return ((Short) parameters.elementAt(index)).shortValue();
    }

    public int getIntParameter(int index) {
        return ((Integer) parameters.elementAt(index)).intValue();
    }

    public long getLongParameter(int index) {
        return ((Long) parameters.elementAt(index)).longValue();
    }

    public float getFloatParameter(int index) {
        return ((Float) parameters.elementAt(index)).floatValue();
    }

    public double getDoubleParameter(int index) {
        return ((Double) parameters.elementAt(index)).doubleValue();
    }

    public Class<?>[] getParamClasses() {
        Class<?>[] classes = new Class<?>[parameters.size()];
        for (int index = 0; index < classes.length; index++) {
            Object parameter = parameters.elementAt(index);
            classes[index] = primitiveClass(parameter);
        }
        return classes;
    }

    private static Vector<Object> copy(Vector<Object> values) {
        return values == null ? null : new Vector<Object>(values);
    }

    private static void ensureSize(Vector<Object> values, int index) {
        if (values.size() <= index) {
            values.setSize(index + 1);
        }
    }

    private static Class<?> primitiveClass(Object value) {
        if (value instanceof Byte) {
            return byte.class;
        }
        if (value instanceof Character) {
            return char.class;
        }
        if (value instanceof Short) {
            return short.class;
        }
        if (value instanceof Integer) {
            return int.class;
        }
        if (value instanceof Long) {
            return long.class;
        }
        if (value instanceof Float) {
            return float.class;
        }
        if (value instanceof Double) {
            return double.class;
        }
        return value.getClass();
    }
}
