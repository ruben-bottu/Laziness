package domain;

import java.util.Iterator;

public class InfiniteNullIterator<E> implements Iterator<E> {

    private InfiniteNullIterator() {}

    //TODO Rename this method
    public static <E> Iterator<E> empty() {
        return new InfiniteNullIterator<>();
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public E next() {
        return null;
    }
}
