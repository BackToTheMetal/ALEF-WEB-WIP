package com.intellij.util.xmlb.annotations;
/** Declaration metadata only; reflective XML serialization is outside the browser profile. */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.CLASS)
public @interface XCollection { String propertyElementName() default ""; String elementName() default "option"; String valueAttributeName() default "value"; Class<?>[] elementTypes() default {}; }
