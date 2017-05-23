package smallcheck.generators;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class ListGen<T> extends SeriesGen<List<T>> {
    private SeriesGen<T> elementGen;

    public ListGen(SeriesGen<T> elementGen) {
        super();
        this.elementGen = elementGen;
    }

    @Override
    public Stream<List<T>> generate(int depth) {
        if (depth == 0) {
            return Stream.of(Collections.emptyList());
        }
        return generate(depth - 1).flatMap(l -> {
            return Stream.concat(
                    Stream.of(l),
                    elementGen.generate(depth - 1).map(e -> {
                        List<T> res = new ArrayList<>(depth);
                        res.add(e);
                        for (T t : l) {
                            res.add(elementGen.copy(t));
                        }
                        return res;
                    })
            );
        });
    }

    @Override
    public List<T> copy(List<T> obj) {
        return obj.stream()
                .map(elementGen::copy)
                .collect(Collectors.toList());
    }
}
