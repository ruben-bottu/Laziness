package domain;

import java.util.Iterator;

public class LazyListIterator<E> implements Iterator<E> {
    private LazyList<E> currentNode;

    private LazyListIterator(LazyList<E> elements) {
        currentNode = elements;
    }

    public static <E> Iterator<E> of(LazyList<E> elements) {
        return new LazyListIterator<>(elements);
    }

    @Override
    public boolean hasNext() {
        return currentNode.any();
    }

    @Override
    public E next() {
        E nextValue = currentNode.value.value();
        currentNode = currentNode.tail.value();
        return nextValue;
    }
}
