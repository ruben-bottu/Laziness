package domain;

import java.util.Iterator;
import java.util.Objects;

public abstract class Enumerable {

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
}