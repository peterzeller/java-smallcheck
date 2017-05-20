package smallcheck.annotations;

import smallcheck.generators.SeriesGen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface From {


    /**
     * @return the generator used for generating values of the annotated type
     */
    Class<? extends SeriesGen<?>> value();
}
