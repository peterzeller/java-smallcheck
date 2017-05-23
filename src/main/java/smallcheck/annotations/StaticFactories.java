package smallcheck.annotations;

import java.lang.annotation.*;
import java.util.function.Function;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StaticFactories {

    StaticFactory[] value();

}
