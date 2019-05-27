package smallcheck.generators;

import java.util.stream.Stream;

/**
 *
 */
public class StringGen extends SeriesGen<String> {

    private final ArrayGenG<Character, char[]> arrayGen;

    public StringGen() {
        this(new CharGen());
    }

    public StringGen(ArrayGenG<Character, char[]> arrayGen) {
        this.arrayGen = arrayGen;
    }

    public StringGen(SeriesGen<Character> charGen) {
        this.arrayGen =  ArrayGenG.charArray(charGen);
    }

    @Override
    public Stream<String> generate(int depth) {
        return arrayGen.generate(depth).map(String::new);
    }
}
