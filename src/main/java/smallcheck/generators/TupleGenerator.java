package smallcheck.generators;

import java.util.stream.Stream;

/**
 *
 */
public class TupleGenerator extends SeriesGen<Object[]> {

    // TODO add copy

    private SeriesGen<?>[] gens;

    public TupleGenerator(SeriesGen<?>... gens) {
        this.gens = gens;
    }

    @Override
    public Stream<Object[]> generate(int depth) {
        return generate(depth, 0);
    }

    private Stream<Object[]> generate(int depth, int start) {
        if (start >= gens.length) {
            return Stream.<Object[]>of(new Object[gens.length]);
        }
        SeriesGen<?> seriesGen = gens[start];

        return seriesGen.generate(depth).flatMap(o ->
                generate(depth, start + 1).map(os -> {
                    os[start] = o;
                    return os;
                }));
    }
}
