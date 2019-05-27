import org.junit.runner.RunWith;
import smallcheck.SmallCheckRunner;
import smallcheck.annotations.Property;
import smallcheck.generators.*;

import java.util.Comparator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(SmallCheckRunner.class)
public class StringCmpExample {


    class StringLexicoIgnoreCaseComparator implements Comparator<String> {

        @Override
        public int compare(String first, String second) {
            if ((first == null) || (second == null)) {
                throw new NullPointerException();
            }

            if (first.equalsIgnoreCase(second)) {
                return 0;
            }

            return first.compareTo(second);
        }

    }

    @Property(maxDepth = 3)
    public void compareTransitive(StateGen gen) {
        StringLexicoIgnoreCaseComparator cmp = new StringLexicoIgnoreCaseComparator();
        String x = gen.gen(() -> new StringGen(new CharGenCase()));
        String y = gen.gen(() -> new StringGen(new CharGenCase()));
        String z = gen.gen(() -> new StringGen(new CharGenCase()));
        System.out.print("x = '" + x);
        System.out.print("', y = '" + y);
        System.out.println("', z = '" + z + "'");
        assumeTrue(cmp.compare(x, y) <= 0);
        assumeTrue(cmp.compare(y, z) <= 0);
        assertTrue(cmp.compare(x, z) <= 0);
    }


    @Property(maxDepth = 3)
    public void genInts(StateGen gen) {
        int x = gen.gen(IntegerGen::new);
        int y = gen.gen(IntegerGen::new);
        int z = gen.gen(IntegerGen::new);
        System.out.print("x = '" + x);
        System.out.print("', y = '" + y);
        System.out.println("', z = '" + z + "'");
    }


}
