package smallcheck.generators;

import java.lang.reflect.Parameter;
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
            return Stream.<Object[]>of(new Object[parameters.length]);
        }
        Parameter p = parameters[pos];
        SeriesGen<?> seriesGen = genFactory.genForType(p.getAnnotatedType());

        return seriesGen.generate(depth).flatMap(o ->
                generate(genFactory, parameters, depth, pos - 1).map(os -> {
                    os[pos] = o;
                    return os;
                }));
    }
}
