package smallcheck.generators;

import java.util.stream.Stream;

/**
 *
 */
public class ArrayGen<T> extends SeriesGen<T[]> {

    private ArrayGenG<T, T[]> gen;

    public ArrayGen(Class<T> clazz, SeriesGen<T> elementGen) {
        this.gen = ArrayGenG.generic(clazz, elementGen);
    }

    @Override
    public Stream<T[]> generate(int depth) {
        return gen.generate(depth);
    }

    @Override
    public T[] copy(T[] obj) {
        return gen.copy(obj);
    }
}
