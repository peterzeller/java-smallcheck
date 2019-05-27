import org.junit.Assert;
import org.junit.Assume;
import org.junit.runner.RunWith;
import smallcheck.SmallCheckRunner;
import smallcheck.annotations.Property;
import smallcheck.generators.IntegerGen;
import smallcheck.generators.StateGen;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RunWith(SmallCheckRunner.class)
public class LazyExample {

    static <T extends Comparable<? super T>> boolean sorted(List<T> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).compareTo(list.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }

    static <T extends Comparable<? super T>> void insert(T elem, List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            if (elem.compareTo(list.get(i)) <= 0) {
                list.add(i, elem);
                return;
            }
        }
        list.add(elem);
    }

    @Property
    public void test(StateGen g) {
        int size = g.gen(Integer.class);
        List<LazyInt> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(new LazyInt(g));
        }
        Assume.assumeTrue(sorted(list));
        LazyInt elem = new LazyInt(g);
        System.out.println("pre  = " + list);
        System.out.println("elem = " + elem);
        insert(elem, list);
        System.out.println("post = " + list);
        Assert.assertTrue(sorted(list));
    }

    static class LazyInt implements Comparable<LazyInt> {
        private Integer i = null;
        private StateGen g;

        public LazyInt(StateGen g) {
            this.g = g;
        }

        @Override
        public int compareTo(LazyInt other) {
            return Integer.compare(get(), other.get());
        }

        int get() {
            if (i == null) {
                i = g.gen(IntegerGen::new);
            }
            return i;
        }

        @Override
        public String toString() {
            return i == null ? "?" : "" + i;
        }
    }


}
