package idealist;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Users of this library should never know of the existence of
 * this class. Its only purpose is to exploit the efficiency of
 * mutable linked lists within algorithms of this library.
 * @param <E>
 */
abstract class MutableList<E> implements Iterable<Lazy<E>> {
    public final Lazy<E> value;
    public MutableList<E> tail;

    private MutableList(Lazy<E> value, MutableList<E> tail) {
        this.value = value;
        this.tail = tail;
    }

    static <E> MutableList<E> create(Lazy<E> value, MutableList<E> tail) {
        return new NormalNode<>(value, tail);
    }

    @SuppressWarnings("unchecked")
    public static <E> MutableList<E> empty() {
        return (MutableList<E>) EndNode.EMPTY;
    }

    public static <E> MutableList<E> of(Lazy<E> left, Lazy<E> right) {
        return MutableList.create(left, MutableList.create(right, empty()));
    }

    @SafeVarargs
    public static <E> MutableList<E> of(Lazy<E>... elements) {
        MutableList<E> result = empty();
        int lastIndex = elements.length - 1;

        for (int i = lastIndex; i >= 0; i--) {
            result = MutableList.create(elements[i], result);
        }
        return result;
    }

    public abstract Lazy<E> first();

    public abstract boolean any();

    public boolean isEmpty() {
        return !any();
    }

    abstract void toStringHelper(StringBuilder builder);

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        toStringHelper(builder);
        return builder.append("]").toString();
    }

    @Override
    public Iterator<Lazy<E>> iterator() {
        return Enumerator.of(this);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class NormalNode<E> extends MutableList<E> {

        private NormalNode(Lazy<E> value, MutableList<E> tail) {
            super(value, tail);
        }

        @Override
        public Lazy<E> first() {
            return value;
        }

        @Override
        public boolean any() {
            return true;
        }

        @Override
        void toStringHelper(StringBuilder builder) {
            builder.append(value.value());
            if (tail.any()) builder.append(", ");
            tail.toStringHelper(builder);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class EndNode<E> extends MutableList<E> {
        private static final MutableList<?> EMPTY = new EndNode<>();

        private static <E> E throwNoSuchValueException() {
            throw new NoSuchElementException("The value field in EndNode contains no value");
        }

        private EndNode() {
            super(Lazy.of(EndNode::throwNoSuchValueException), null);
        }

        @Override
        public Lazy<E> first() {
            throw new NoSuchElementException("List is empty");
        }

        @Override
        public boolean any() {
            return false;
        }

        @Override
        void toStringHelper(StringBuilder builder) { }
    }
}
