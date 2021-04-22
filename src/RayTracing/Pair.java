package RayTracing;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.stream.Stream;

/**
 * Tuples with two elements
 */
public class Pair<S, T> extends SimpleImmutableEntry<S, T> {
    /**
     * Default serial version ID
     */
    private static final long serialVersionUID = 1L;

    public Pair(S first, T second) {
        super(first, second);
    }

    public S first() {
        return this.getKey();
    }

    public T second() {
        return this.getValue();
    }

    public static <T> Stream<T> stream(Pair<T, T> pair) {
        return Stream.of(pair.first(), pair.second());
    }
}
