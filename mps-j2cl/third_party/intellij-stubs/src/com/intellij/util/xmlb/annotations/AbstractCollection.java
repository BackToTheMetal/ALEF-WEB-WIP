package com.intellij.util.xmlb.annotations;
/** Declaration metadata only; reflective XML serialization is outside the browser profile. */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.CLASS)
public @interface AbstractCollection { boolean surroundWithTag() default true; boolean sortOrderedSet() default true; String elementTag() default "option"; String elementValueAttribute() default "value"; Class<?>[] elementTypes() default {}; }
