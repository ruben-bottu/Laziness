package domain;

import java.util.Iterator;

public class LazyIterator<E> implements Iterator<Lazy<E>> {
    private Lazy<LazyList<E>> currentNode;

    private LazyIterator(Lazy<LazyList<E>> elements) {
        currentNode = elements;
    }

    public static <E> Iterator<Lazy<E>> of(Lazy<LazyList<E>> elements) {
        return new LazyIterator<>(elements);
    }

    @Override
    public boolean hasNext() {
        return currentNode.value().any();
    }

    @Override
    public Lazy<E> next() {
        Lazy<E> currentValue = currentNode.value().value;
        currentNode = currentNode.value().tail;
        return currentValue;
    }
}
