import org.junit.runner.RunWith;
import smallcheck.SmallCheckRunner;
import smallcheck.annotations.Property;
import smallcheck.generators.StateGen;

import static org.junit.Assert.assertTrue;

/**
 *
 */
@RunWith(SmallCheckRunner.class)
public class Max3Example {

    static int max3(int x, int y, int z) {
        if (x > y && x > z) {
            return x;
        } else if (y > x && y > z) {
            return y;
        } else {
            return z;
        }
    }

    @Property
    public void testMax3(int x, int y, int z) {
        int result = max3(x, y, z);
        assertTrue(result >= x);
        assertTrue(result >= y);
        assertTrue(result >= z);
    }


    @Property
    public void testMax3S(StateGen sg) {
        int x = sg.gen(Integer.class);
        int y = sg.gen(Integer.class);
        int z = sg.gen(Integer.class);
        int result = max3(x, y, z);
        assertTrue(result >= x);
        assertTrue(result >= y);
        assertTrue(result >= z);
    }


}
