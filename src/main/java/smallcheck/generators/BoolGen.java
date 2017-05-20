package smallcheck.generators;

import java.util.stream.Stream;

/**
 *
 */
public class BoolGen extends SeriesGen<Boolean> {
    @Override
    public Stream<Boolean> generate(int depth) {
        return Stream.of(false, true);
    }
}
