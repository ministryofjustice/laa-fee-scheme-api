package uk.gov.justice.laa.fee.scheme.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
  // Optional: specify which fields to log
  String[] fields() default {};

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface LogEntry {
    String[] fields() default {};
  }

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface LogExit {
    boolean logResult() default true;
  }

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface LogCustomResponseOnExit {
    String[] fields() default {};
  }
}
