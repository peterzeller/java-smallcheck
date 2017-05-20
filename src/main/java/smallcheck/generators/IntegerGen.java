package smallcheck.generators;

import com.google.common.collect.Streams;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Generates integers.
 * First 0,1,2,3,...,depth
 * Then -1,-2,...,-depth
 */
public class IntegerGen extends SeriesGen<Integer> {
    @Override
    public Stream<Integer> generate(int depth) {
        return Streams.stream(new Iterator<Integer>() {
            int p = -1;
            int n = 0;

            @Override
            public boolean hasNext() {
                return p <= depth && n > -depth;
            }

            @Override
            public Integer next() {
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
