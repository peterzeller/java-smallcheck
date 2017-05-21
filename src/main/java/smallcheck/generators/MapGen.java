package smallcheck.generators;

import java.util.*;
import java.util.stream.Stream;

/**
 *
 */
public class MapGen<K, V> extends SeriesGen<Map<K, V>> {

    private final SeriesGen<K> keyGen;
    private final SeriesGen<V> valueGen;

    public MapGen(SeriesGen<K> keyGen, SeriesGen<V> valueGen) {
        super();
        this.keyGen = keyGen;
        this.valueGen = valueGen;
    }

    @Override
    public Stream<Map<K, V>> generate(int depth) {
        if (depth == 0) {
            return Stream.of(Collections.emptyMap());
        }
        return generate(depth - 1).flatMap(m -> {
            return Stream.concat(
                    Stream.of(m),
                    keyGen.generate(depth - 1).flatMap(k -> {
                        return valueGen.generate(depth - 1).map(v -> {
                            Map<K, V> res = new HashMap<>();
                            res.put(k, v);
                            res.putAll(m);
                            return res;
                        });
                    })
            );
        });
    }
}
