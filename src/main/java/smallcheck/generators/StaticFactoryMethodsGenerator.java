package smallcheck.generators;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 */
public class StaticFactoryMethodsGenerator extends SeriesGen<Object> {
    private final GenFactory genFactory;
    private final List<Method> staticFactoryMethods;

    public StaticFactoryMethodsGenerator(GenFactory genFactory, List<Method> staticFactoryMethods) {
        super();
        this.genFactory = genFactory;
        this.staticFactoryMethods = new ArrayList<>(staticFactoryMethods);
        this.staticFactoryMethods.sort(
                Comparator
                        .comparing((Method m) -> m.getParameters().length));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<Object> generate(int depth) {
        if (depth <= 0) {
            return Stream.empty();
        }
        return staticFactoryMethods.stream().flatMap(m -> {
            return ParamGen.generate(genFactory, m.getParameters(), depth - 1).map(args -> {
                try {
                    return m.invoke(null, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
