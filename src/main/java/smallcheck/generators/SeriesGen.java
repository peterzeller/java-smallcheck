package smallcheck.generators;

import java.util.stream.Stream;

/*
 * Generates a series of small values up to a given depth
 */
public abstract class SeriesGen<T> {

    public abstract Stream<T> generate(int depth);

    /**
     * Copies the object.
     * If the object is immutable, just return the same object.
     */
    public T copy(T obj) {
        return obj;
    }

}
