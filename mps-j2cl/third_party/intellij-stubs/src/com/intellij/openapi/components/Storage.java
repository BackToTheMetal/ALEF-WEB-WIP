package com.intellij.openapi.components;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.CLASS)
public @interface Storage {
  String value() default "";
  String file() default "";
  boolean deprecated() default false;
  RoamingType roamingType() default RoamingType.DEFAULT;
}
