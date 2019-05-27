package smallcheck.annotations;

import smallcheck.generators.SeriesGen;

import java.lang.annotation.*;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RegisterGenerators.class)
public @interface RegisterGenerator {

    Class<? extends SeriesGen<?>> value();

}
