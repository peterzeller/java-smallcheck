package smallcheck.generators;

import com.google.common.collect.Streams;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Generates integers.
 * First 0,1,2,3,...,depth
 * Then -1,-2,...,-depth
 */
public class ShortGen extends SeriesGen<Short> {
    @Override
    public Stream<Short> generate(int depth) {
        return Streams.stream(new Iterator<Short>() {
            short p = -1;
            short n = 0;

            @Override
            public boolean hasNext() {
                return p <= depth && n > -depth;
            }

            @Override
            public Short next() {
                if (p < depth) {
                    p++;
                    return p;
                } else {
                    n--;
                    return n;
                }
            }
        });
    }
}
