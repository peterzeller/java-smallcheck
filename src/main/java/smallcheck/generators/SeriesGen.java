package smallcheck.generators;

import java.util.stream.Stream;

/*
 * Generates a series of small values up to a given depth
 */
public abstract class SeriesGen<T> {

    public abstract Stream<T> generate(int depth);

}
