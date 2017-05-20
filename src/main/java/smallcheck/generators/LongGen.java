package smallcheck.generators;

import com.google.common.collect.Streams;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Generates integers.
 * First 0,1,2,3,...,depth
 * Then -1,-2,...,-depth
 */
public class LongGen extends SeriesGen<Long> {
    @Override
    public Stream<Long> generate(int depth) {
        return Streams.stream(new Iterator<Long>() {
            long p = -1;
            long n = 0;

            @Override
            public boolean hasNext() {
                return p <= depth && n > -depth;
            }

            @Override
            public Long next() {
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
