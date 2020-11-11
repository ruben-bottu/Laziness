package domain;

import java.util.Iterator;

public abstract class Enumerator {

    public static Iterator<Void> infiniteNulls() {
        return new Iterator<Void>() {

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Void next() {
                return null;
            }
        };
    }

    public static <E> Iterator<E> of(Lazy<LazyList<E>> elements) {
        return new Iterator<E>() {

            private final Iterator<Lazy<E>> iterator = lazyOf(elements);

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return iterator.next().value();
            }
        };
    }

    public static <E> Iterator<Lazy<E>> lazyOf(Lazy<LazyList<E>> elements) {
        return new Iterator<Lazy<E>>() {

            private Lazy<LazyList<E>> currentNode = elements;

            @Override
            public boolean hasNext() {
                return currentNode.value().any();
            }

            @Override
            public Lazy<E> next() {
                Lazy<E> next = currentNode.value().value;
                currentNode = currentNode.value().tail;
                return next;
            }
        };
    }
}