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

    // Constructors

    public Triple(S first, T second, U third) {
        super(first, second);
        this.tail = third;
    }

    public Triple(Pair<S, T> pair, U tail) {
        this(pair.first(), pair.second(), tail);
    }

    public Triple(S head, Pair<T, U> pair) {
        this(head, pair.first(), pair.second());
    }

    /**
     * @return The third element in the triple.
     */
    public U third() {
        return this.tail;
    }

    /**
     * Get the first two elements.
     * 
     * @return A pair of the first two elements in the triple.
     */
    public Pair<S, T> leftPair() {
        return new Pair<S, T>(this.first(), this.second());
    }

    /**
     * Get the last two elements.
     * 
     * @return A pair of the last two elements in the triple.
     */
    public Pair<T, U> rightPair() {
        return new Pair<T, U>(this.second(), this.third());
    }

    /**
     * Group the first two elements.
     * 
     * @return A pair, consisting of a pair of the first two elements, and the third
     *         element.
     */
    public Pair<Pair<S, T>, U> groupLeft() {
        return new Pair<Pair<S, T>, U>(this.leftPair(), this.third());
    }

    /**
     * Group the last two elements.
     * 
     * @return A pair, consisting of the first element, and a pair of the last two
     *         elements.
     */
    public Pair<S, Pair<T, U>> groupRight() {
        return new Pair<S, Pair<T, U>>(this.first(), this.rightPair());
    }
}
