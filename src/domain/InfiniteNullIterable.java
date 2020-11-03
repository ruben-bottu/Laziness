package domain;

import java.util.Iterator;

public class InfiniteNullIterable<E> implements Iterable<E> {

    private InfiniteNullIterable() {}

    //TODO Rename this method
    public static <E> Iterable<E> empty() {
        return new InfiniteNullIterable<>();
    }
    
    @Override
    public Iterator<E> iterator() {
        return InfiniteNullIterator.empty();
    }
}
