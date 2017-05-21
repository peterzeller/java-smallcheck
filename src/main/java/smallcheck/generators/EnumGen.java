package smallcheck.generators;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 *
 */
public class EnumGen<T extends Enum<T>> extends SeriesGen<T> {

    private final T[] values;

    public EnumGen(T...values) {
        this.values = values;
    }

    public EnumGen(Class<T> enumClass) {
        try {
            Method m = enumClass.getMethod("values");
            m.setAccessible(true);
            Object valuesObj = m.invoke(null);
            @SuppressWarnings("unchecked")
            T[] values = (T[]) valuesObj;
            this.values = values;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<T> generate(int depth) {
        return Stream.of(values).limit(depth);
    }
}
