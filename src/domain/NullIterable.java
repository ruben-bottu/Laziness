package domain;

import java.util.Iterator;

public class NullIterable<E> implements Iterable<E> {

    private NullIterable() {}

    public static <E> Iterable<E> empty() {
        return new NullIterable<>();
    }
    
    @Override
    public Iterator<E> iterator() {
        return NullIterator.empty();
    }
}
