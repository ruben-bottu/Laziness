package domain;

import domain.primitive_specializations.IntIterator;

import java.util.Iterator;
import java.util.PrimitiveIterator;

// Iterator would be a better name, but Java doesn't have type aliasing
public final class Enumerator {

    // Private constructor to prevent instantiation
    private Enumerator() {}

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

    public static <E> Iterator<E> of(IdeaList<E> elements) {
        return new Iterator<E>() {
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

    // ===================================================================================

    // NoSuchElementException - if the iteration has no more elements
    /*public static PrimitiveIterator.OfInt of(IntIdeaList elements) {
        return new PrimitiveIterator.OfInt() {
            private IntIdeaList innerList = elements;

            @Override
            public boolean hasNext() {
                return innerList.any();
            }

            @Override
            public int nextInt() {
                int next = innerList.first();
                innerList = innerList.tail.value();
                return next;
            }
        };
    }*/

    /*public static IntIterator ofInt(Iterable<Integer> iterable) {
        return new IntIterator(iterable);
    }*/

    /*public static IntIterator ofInt(Iterable<Integer> elements) {
        return new IntIterator() {
            private final Iterator<Integer> innerIterator = elements.iterator();

            @Override
            public boolean hasNext() {
                return innerIterator.hasNext();
            }

            @Override
            public int next() {
                return innerIterator.next();
            }
        };
    }*/

    public static PrimitiveIterator.OfInt ofInt(Iterable<Integer> elements) {
        return new PrimitiveIterator.OfInt() {
            private final Iterator<Integer> innerIterator = elements.iterator();

            @Override
            public boolean hasNext() {
                return innerIterator.hasNext();
            }

            @Override
            public int nextInt() {
                return innerIterator.next();
            }
        };
    }

    // TODO significant performance improvement?
    /*public static <T> Iterator<T> of(T[] elements) {

    }*/

    /*public static IntIterator of(int[] elements) {
        return new IntIterator() {
            private final int[] innerArray = elements;
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < innerArray.length;
            }

            @Override
            public int next() {
                int next = innerArray[currentIndex];
                currentIndex++;
                return next;
            }
        };
    }*/

    public static PrimitiveIterator.OfInt of(int[] elements) {
        return new PrimitiveIterator.OfInt() {
            private final int[] innerArray = elements;
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < innerArray.length;
            }

            //TODO has to throw NoSuchElementException when there is no next element
            @Override
            public int nextInt() {
                int next = innerArray[currentIndex];
                currentIndex++;
                return next;
            }
        };
    }
}