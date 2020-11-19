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

    public static <E> Iterator<E> of(LazyList<E> elements) {
        return new Iterator<E>() {

            private LazyList<E> innerList = elements;

            @Override
            public boolean hasNext() {
                return innerList.any();
            }

            @Override
            public E next() {
                E next = innerList.first();
                innerList = innerList.tail.value();
                return next;
            }
        };
    }
}