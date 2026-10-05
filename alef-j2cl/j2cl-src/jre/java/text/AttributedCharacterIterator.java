package java.text;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface AttributedCharacterIterator extends CharacterIterator {
    int getRunStart();
    int getRunStart(Attribute attribute);
    int getRunStart(Set<? extends Attribute> attributes);
    int getRunLimit();
    int getRunLimit(Attribute attribute);
    int getRunLimit(Set<? extends Attribute> attributes);
    Map<Attribute, Object> getAttributes();
    Object getAttribute(Attribute attribute);
    Set<Attribute> getAllAttributeKeys();

    class Attribute implements Serializable {
        private static final long serialVersionUID = -9142742483513960612L;
        public static final Attribute LANGUAGE = new Attribute("language");
        public static final Attribute READING = new Attribute("reading");
        public static final Attribute INPUT_METHOD_SEGMENT = new Attribute("input_method_segment");
        private final String name;

        protected Attribute(String name) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            this.name = name;
        }

        @Override
        public final boolean equals(Object object) {
            return this == object;
        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }

        @Override
        public String toString() {
            return getClass().getName() + "(" + name + ")";
        }

        protected String getName() {
            return name;
        }
    }
}
