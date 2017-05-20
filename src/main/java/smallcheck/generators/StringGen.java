package smallcheck.generators;

import java.util.stream.Stream;

/**
 *
 */
public class StringGen extends SeriesGen<String> {

    private ArrayGen<Character> arrayGen = new ArrayGen<>(Character.class, new CharGen());

    @Override
    public Stream<String> generate(int depth) {
        return arrayGen.generate(depth).map(ar -> {
            char[] chars = new char[ar.length];
            for (int i = 0; i < chars.length; i++) {
                chars[i] = ar[i];
            }
            return new String(chars);
        });
    }
}
