package smallcheck.generators;

import java.util.*;
import java.util.stream.Stream;

/**
 *
 */
public class SetGen<T> extends SeriesGen<Set<T>> {
    private SeriesGen<T> elementGen;

    public SetGen(SeriesGen<T> elementGen) {
        super();
        this.elementGen = elementGen;
    }

    @Override
    public Stream<Set<T>> generate(int depth) {
        if (depth == 0) {
            return Stream.of(Collections.emptySet());
        }
        return generate(depth - 1).flatMap(l -> {
            return Stream.concat(
                    Stream.of(l),
                    elementGen.generate(depth - 1).map(e -> {
                        Set<T> res = new HashSet<>(depth);
                        res.add(e);
                        res.addAll(l);
                        return res;
                    })
            );
        });
    }
}
