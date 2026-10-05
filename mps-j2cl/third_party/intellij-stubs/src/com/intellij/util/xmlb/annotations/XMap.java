package com.intellij.util.xmlb.annotations;
/** Declaration metadata only; reflective XML serialization is outside the browser profile. */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.CLASS)
public @interface XMap { String propertyElementName() default ""; String entryTagName() default "entry"; String keyAttributeName() default "key"; String valueAttributeName() default "value"; }
