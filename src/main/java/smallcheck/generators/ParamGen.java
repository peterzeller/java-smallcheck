package smallcheck.generators;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 *
 */
public class ParamGen {
    public static Stream<Object[]> generate(GenFactory genFactory, Parameter[] parameters, int depth) {
        return generate(genFactory, parameters, depth, parameters.length-1);
    }

    private static Stream<Object[]> generate(GenFactory genFactory, Parameter[] parameters, int depth, int pos) {
        if (pos < 0) {
            return Stream.<Object[]>of(new Object[0]);
        }
        Parameter p = parameters[pos];

        @SuppressWarnings("unchecked")
        SeriesGen<Object> seriesGen = (SeriesGen<Object>) genFactory.genForType(p.getAnnotatedType());

        return seriesGen.generate(depth).flatMap(o ->
                generate(genFactory, parameters, depth, pos - 1).map(os -> {
                    Object[] res = Arrays.copyOf(os, os.length + 1);
                    res[pos] = seriesGen.copy(o);
                    return res;
                }));
    }
}
