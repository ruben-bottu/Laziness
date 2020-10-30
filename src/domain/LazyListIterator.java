package domain;

import java.util.Iterator;

public class LazyListIterator<E> implements Iterator<E> {
    private final Iterator<Lazy<E>> iterator;

    private LazyListIterator(Lazy<LazyList<E>> elements) {
        iterator = LazyIterator.of(elements);
    }

    public static <E> Iterator<E> of(Lazy<LazyList<E>> elements) {
        return new LazyListIterator<>(elements);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public E next() {
        return iterator.next().value();
    }
}