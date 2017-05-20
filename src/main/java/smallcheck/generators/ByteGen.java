package smallcheck.generators;

import com.google.common.collect.Streams;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Generates integers.
 * First 0,1,2,3,...,depth
 * Then -1,-2,...,-depth
 */
public class ByteGen extends SeriesGen<Byte> {
    @Override
    public Stream<Byte> generate(int depth) {
        return Streams.stream(new Iterator<Byte>() {
            int p = -1;
            int n = 0;

            @Override
            public boolean hasNext() {
                int depth2 = Math.min(128, depth);
                return p <= depth2 && n > -depth2;
            }

            @Override
            public Byte next() {
                if (p < depth) {
                    p++;
                    return (byte) p;
                } else {
                    n--;
                    return (byte) n;
                }
            }
        });
    }
}
