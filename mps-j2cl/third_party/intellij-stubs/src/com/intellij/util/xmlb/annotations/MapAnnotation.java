package com.intellij.util.xmlb.annotations;
/** Declaration metadata only; reflective XML serialization is outside the browser profile. */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.CLASS)
public @interface MapAnnotation { boolean surroundWithTag() default true; String keyAttributeName() default "key"; String valueAttributeName() default "value"; String entryTagName() default "entry"; boolean surroundKeyWithTag() default true; boolean surroundValueWithTag() default true; boolean sortBeforeSave() default true; }
