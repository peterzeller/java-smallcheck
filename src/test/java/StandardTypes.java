/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import smallcheck.SmallCheckRunner;
import smallcheck.annotations.Property;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SmallCheckRunner.class)
public class StandardTypes {

    @Test
    public void blub() {
        assertEquals(5, 2 + 2);
    }

    @Property
    public void test(int x, long y) {
        assertEquals(x + y, x * y);
    }


    @Property
    public void generate(int x, char c) {
        System.out.println(x + ", " + c);
    }

    @Property(maxDepth = 6, maxInvocations = 1000, minExamples = 100, timeout = 30)
    public void configuredExample(int x, char c) {
        System.out.println(x + ", " + c);
    }


    @Property
    public void testArray(Integer[] ar) {
        int sum = 0;
        for (int i : ar) {
            sum += i;
        }
        assertTrue(sum != 10);
    }

    @Property
    public void testString(String s) {
        assertTrue(s.length() < 4);
    }

    @Property
    public void testStringList(List<String> strings) {
        assertEquals(0, strings.size() > 3 ? strings.get(3).length() : 0);
    }

    @Property
    public void testCollection(Collection<Integer> ints) {
        assertTrue(ints.stream().mapToInt(i -> i).sum() <= 6);
    }

    @Property
    public void testIterable(Iterable<Integer> ints) {
        int sum = 0;
        for (Integer i : ints) {
            sum += i;
        }
        assertTrue(sum <= 6);
    }

    @Property(maxInvocations = Integer.MAX_VALUE, maxDepth = 10)
    public void testSet(Set<Integer> ints) {
        int sum = 0;
        for (Integer i : ints) {
            sum += i;
        }
        assertTrue(sum <= 6);
    }

    @Property(maxInvocations = Integer.MAX_VALUE, maxDepth = 10)
    public void testMap(Map<Character, Integer> m) {
        Assume.assumeTrue(m.containsKey('a'));
        Assume.assumeTrue(m.containsKey('b'));
        assertTrue(m.get('a') != 2 || m.get('b') != 3);
    }

    @Property
    public void testAssume(List<String> x, List<String> y) {
        Assume.assumeTrue(x.contains("b"));
        Assume.assumeTrue(y.contains("b"));
        assertEquals(x, y);
    }

    @Property
    public void testAssume2(List<String> x) {
        Assume.assumeTrue(x.size() > 4);
        assertEquals(1, 2);
    }

    @Property
    public void testEnum(Set<X> s) {
        assertTrue(s.size() < 3);
    }

    @Property(timeout = 5)
    public void testIntArray(int[] ar) {
        for (int i = 0; i < ar.length - 1; i++) {
            assertTrue(ar[i] <= ar[i + 1]);
        }
    }

    @Property(timeout = 5)
    public void testIntArrayException(int[] ar) {
        for (int i = 0; i < ar.length; i++) {
            assertTrue(ar[i] <= ar[i + 1]);
        }
    }

    @Property(timeout = 5)
    public void testIntArrayInfiniteLoop(int[] ar) {
        int sum = 0;
        for (int i : ar) {
            sum += i;
        }
        if (sum + ar.length > 6) {
            while (true) {
            }
        }
    }

    enum X {
        A, B, C
    }


}
