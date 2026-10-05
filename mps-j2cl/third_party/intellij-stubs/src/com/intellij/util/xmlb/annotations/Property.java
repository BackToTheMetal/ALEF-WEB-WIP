package com.intellij.util.xmlb.annotations;
/** Declaration metadata only; reflective XML serialization is outside the browser profile. */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.CLASS)
public @interface Property { boolean surroundWithTag() default true; boolean flat() default false; boolean alwaysWrite() default false; boolean assertIfNoBindings() default true; }
