package domain;

public class IndexElement<E> {
    private final Pair<Integer, E> pair;
    public final int index;
    public final E element;

    private IndexElement(int index, E element) {
        pair = Pair.of(index, element);
        this.index = index;
        this.element = element;
    }

    public static <E> IndexElement<E> of(int index, E element) {
        return new IndexElement<>(index, element);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return pair.equals(o);
    }

    @Override
    public int hashCode() {
        return pair.hashCode();
    }

    @Override
    public String toString() {
        return pair.toString();
    }
}