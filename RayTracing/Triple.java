package RayTracing;

/**
 * Tuples with three elements
 */
public class Triple<S, T, U> extends Pair<S, T> {
    /**
     * Default serial version ID
     */
    private static final long serialVersionUID = 1L;

    private final U tail; // The third object

    public Triple(S first, T second, U third) {
        super(first, second);
        this.tail = third;
    }

    public U third() {
        return this.tail;
    }
}
