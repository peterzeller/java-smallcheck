package smallcheck.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.Duration;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface Property {
    int maxDepth() default 5;

    int maxInvocations() default 100000;

    long minExamples() default 20;

    /** timeout in seconds */
    int timeout() default -1;
}
