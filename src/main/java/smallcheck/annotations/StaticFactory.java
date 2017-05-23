package smallcheck.annotations;

import java.lang.annotation.*;
import java.util.function.Function;

/**
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(StaticFactories.class)
public @interface StaticFactory {

    Class<?> value();


    Class<? extends Function<Object, Object>> copyFunc() default IdentityFunc.class;


    class IdentityFunc implements Function<Object, Object> {

        @Override
        public Object apply(Object o) {
            return o;
        }
    }
}
