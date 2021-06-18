package idealist;

import java.util.Iterator;

// Iterator would be a better name, but Java doesn't have type aliasing
public final class Enumerator {

    // Private constructor to prevent instantiation
    private Enumerator() {}

    public static Iterator<Void> infiniteNulls() {
        return new Iterator<>() {

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

    public static <E> Iterator<E> of(IdeaList<E> elements) {
        return new Iterator<>() {
            private IdeaList<E> innerList = elements;

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

    // TODO remove
    public static <E> Iterator<E> of(IdeaList2<E> elements) {
        return new Iterator<>() {
            private IdeaList2<E> innerList = elements;

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

    public static <E> Iterator<E> of(E[] elements) {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return elements.length > index;
            }

            @Override
            public E next() {
                return elements[index++];
            }
        };
    }
}
