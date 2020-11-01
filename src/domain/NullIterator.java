package domain;

import java.util.Iterator;

public class NullIterator<E> implements Iterator<E> {

    private NullIterator() {}

    public static <E> Iterator<E> empty() {
        return new NullIterator<>();
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
