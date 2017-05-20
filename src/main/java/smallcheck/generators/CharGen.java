package smallcheck.generators;

import com.google.common.collect.Streams;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Generates integers.
 * First 0,1,2,3,...,depth
 * Then -1,-2,...,-depth
 */
public class CharGen extends SeriesGen<Character> {
    @Override
    public Stream<Character> generate(int depth) {
        return Streams.stream(new Iterator<Character>() {
            int i = -1;

            @Override
            public boolean hasNext() {
                return i < depth;
            }

            @Override
            public Character next() {
                i++;
                // we start at 'a' because it leads to more readable counter examples
                return (char) ('a' + i);

            }
        });
    }
}
