package smallcheck.generators;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 *
 */
public class ArrayGen<T> extends SeriesGen<T[]> {

    private Class<T> clazz;
    private final SeriesGen<T> elementGen;

    public ArrayGen(Class<T> clazz, SeriesGen<T> elementGen) {
        this.clazz = clazz;
        this.elementGen = elementGen;
    }

    @Override
    public Stream<T[]> generate(int depth) {
        if (depth == 0) {
            @SuppressWarnings("unchecked")
            T[] ar = (T[]) Array.newInstance(clazz, 0);
            return Stream.<T[]>of(ar);
        }
        return generate(depth - 1).flatMap(ar -> {
            return Stream.concat(
                    Stream.<T[]>of(ar),
                    elementGen.generate(depth - 1).map(e -> {
                        @SuppressWarnings("unchecked")
                        T[] res = (T[]) Array.newInstance(clazz, ar.length + 1);
                        System.arraycopy(ar, 0, res, 1, ar.length);
                        res[0] = e;
                        return res;
                    })
            );
        });
    }

    @Override
    public T[] copy(T[] obj) {
        T[] res = Arrays.copyOf(obj, obj.length);
        for (int i = 0; i < res.length; i++) {
            res[i] = elementGen.copy(res[i]);
        }
        return res;
    }
}
