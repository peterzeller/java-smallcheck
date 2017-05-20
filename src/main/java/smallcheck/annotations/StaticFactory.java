package smallcheck.annotations;

import java.lang.annotation.*;

/**
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(StaticFactories.class)
public @interface StaticFactory {

    Class<?> value();
}
