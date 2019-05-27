package smallcheck.generators;

import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A stateful generator
 */
public class StateGen {
    private List<IteratorWithCurrent> stack = new ArrayList<>();
    private final GenFactory genFactory;
    private int stackDepth = 0;
    private final int depth;
    private int replayUntil = 0;

    public StateGen(GenFactory genFactory, int depth) {
        this.genFactory = genFactory;
        this.depth = depth;
    }

    public void restart(int replayUntil) {
        this.replayUntil = replayUntil;
        stackDepth = 0;
    }

    public void restart() {
        this.replayUntil = stackDepth - 1;
        stackDepth = 0;
    }

    public Object gen(Type t) {
        return gen(() -> genFactory.genForType(t));
    }

    @SuppressWarnings("unchecked")
    public <T> T gen(Class<T> t) {
        return (T) gen(() -> genFactory.genForType(t));
    }

    @SuppressWarnings("unchecked")
    public <T> T gen(Supplier<SeriesGen<T>> genSupplier) {
        if (stackDepth < stack.size()) {
            IteratorWithCurrent gen = stack.get(stackDepth);
            if (stackDepth < replayUntil) {
                stackDepth++;
                return (T) gen.getCurrent();
            } else if (gen.hasNext()) {
                stackDepth++;
                return (T) gen.next();
            } else {
                clearStack(stackDepth);
                throw new RestartException(stackDepth);
            }
        } else {
            if (stackDepth < depth) {
                IteratorWithCurrent it = new IteratorWithCurrent(genSupplier.get().generate(depth - stackDepth));
                stack.add(it);
                if (it.hasNext()) {
                    stackDepth++;
                    return (T) it.next();
                } else {
                    throw new RestartException(stackDepth);
                }
            } else {
                throw new RestartException(stackDepth);
            }
        }
    }

    private void clearStack(int stackDepth) {
        if (stack.size() > stackDepth) {
            stack.subList(stackDepth, stack.size()).clear();
        }
    }



    private static class IteratorWithCurrent implements Iterator<Object> {
        private final Iterator<?> it;
        private Object current = null;

        public IteratorWithCurrent(Stream<?> stream) {
            it = stream.iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Object next() {
            current = it.next();
            return current;
        }

        public Object getCurrent() {
            return current;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("StateGen:\n");
        for (IteratorWithCurrent i : stack) {
            sb.append("\t\tgenerated ").append(i.getCurrent()).append("\n");
        }
        return sb.toString();
    }
}
