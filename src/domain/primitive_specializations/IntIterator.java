package domain.primitive_specializations;

import java.util.Iterator;

/*public class IntIterator implements Iterator<Integer> {
    private final Iterator<Integer> innerIterator;

    public IntIterator(Iterable<Integer> iterable) {
        innerIterator = iterable.iterator();
    }

    @Override
    public boolean hasNext() {
        return innerIterator.hasNext();
    }

    @Override
    public Integer next() {
        return innerIterator.next();
    }

    public int nextInt() {
        return innerIterator.next();
    }
}*/

public interface IntIterator {

    boolean hasNext();

    int next();
}