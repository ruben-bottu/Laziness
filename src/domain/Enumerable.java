package domain;

import java.util.Iterator;
import java.util.Objects;

// Iterable would be a better name, but Java doesn't have type aliasing
public final class Enumerable {

    // Private constructor to prevent instantiation
    private Enumerable() {}

    public static Iterable<Void> infiniteNulls() {
        return Enumerator::infiniteNulls;
    }

    public static boolean isContentEqual(Iterable<?> iterable, Iterable<?> other) {
        Iterator<?> it1 = iterable.iterator();
        Iterator<?> it2 = other.iterator();
        while (true) {
            if (it1.hasNext() && !it2.hasNext() || !it1.hasNext() && it2.hasNext()) return false;
            if (!it1.hasNext()) return true;
            if (!Objects.equals(it1.next(), it2.next())) return false;
        }
    }


    // ==============================================================================================


    public static Iterable<Integer> of(int[] elements) {
        return () -> Enumerator.of(elements);
    }
}