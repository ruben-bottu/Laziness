package domain.primitive_specializations;

import domain.IntIdeaList;

import java.util.Iterator;
import java.util.PrimitiveIterator;

public final class PrimitiveEnumerator {

    // Private constructor to prevent instantiation
    private PrimitiveEnumerator() {}

    public static PrimitiveIterator.OfInt of(IntIdeaList elements) {
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
    }

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

    public static PrimitiveIterator.OfInt of(int[] elements) {
        return new PrimitiveIterator.OfInt() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < elements.length;
            }

            @Override
            public int nextInt() {
                return elements[index++];
            }
        };
    }
}
