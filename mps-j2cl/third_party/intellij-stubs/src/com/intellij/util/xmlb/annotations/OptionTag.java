package com.intellij.util.xmlb.annotations;
/** Declaration metadata only; reflective XML serialization is outside the browser profile. */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.CLASS)
public @interface OptionTag { String value() default ""; String tag() default "option"; String nameAttribute() default "name"; String valueAttribute() default "value"; }
