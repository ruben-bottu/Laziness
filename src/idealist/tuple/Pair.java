package idealist.tuple;


import idealist.Nullable;

import java.util.Objects;

public class Pair<A, B> {
    public final A first;
    public final B second;

    private Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<>(first, second);
    }

    public static <A, B, I> Pair<A, B> ofIgnoreLast(A first, B second, I ignore) {
        return Pair.of(first, second);
    }

    public <C> Triplet<A, B, C> add(C element) {
        return Triplet.of(first, second, element);
    }

    public IndexElement<B> toIndexElement() {
        if (first instanceof Integer) return IndexElement.of((Integer) first, second);
        throw new IllegalArgumentException("First element of Pair is not an Integer");
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
