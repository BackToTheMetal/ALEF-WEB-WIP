package com.intellij.openapi.components;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.CLASS)
public @interface State {
  String name();
  Storage[] storages() default {};
  boolean reportStatistic() default false;
  boolean reloadable() default true;
  boolean defaultStateAsResource() default false;
}
