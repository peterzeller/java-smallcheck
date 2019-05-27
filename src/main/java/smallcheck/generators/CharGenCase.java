package smallcheck.generators;

import com.google.common.collect.Streams;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Generates lower and upper case letters
 * 'a', 'A', 'b', 'B', ...
 */
public class CharGenCase extends SeriesGen<Character> {
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
                if (i % 2 == 0) {
                    return (char) ('a' + i / 2);
                } else {
                    return (char) ('A' + i / 2);
                }
            }
        });
    }
}
