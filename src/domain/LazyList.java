package domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class LazyList<E> implements Iterable<E> {
    public final Lazy<E> value;
    public final Lazy<LazyList<E>> tail;

    private LazyList(Lazy<E> value, Lazy<LazyList<E>> tail) {
        this.value = value;
        this.tail = tail;
    }

    // FOUT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! E element = iterator.next()
    /*private static <E> LazyList<E> concat(Iterator<E> iterator, LazyList<E> elements) {
        if (iterator.hasNext()) {
            return NormalNode.of(
                    Lazy.of(iterator::next),
                    Lazy.of(() -> concat(iterator, elements))
            );
        }
        return elements;
    }*/

    /*private static <E> LazyList<E> ofHelper(Iterator<E> iterator) {
        if (iterator.hasNext()) {
            return NormalNode.of(Lazy.of(iterator::next), Lazy.of(() -> ofHelper(iterator)));
        }
        return EndNode.empty();
    }*/

    private static <E> LazyList<E> ofHelper(Iterator<E> iterator) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return NormalNode.of(Lazy.of(() -> element), Lazy.of(() -> ofHelper(iterator)));
        }
        return EndNode.empty();
    }

    public static <E> LazyList<E> of(Iterable<E> elements) {
        return ofHelper(elements.iterator());
        //return concat(elements.iterator(), EndNode.empty());
    }

    @SafeVarargs
    public static <E> LazyList<E> of(E... elements) {
        return LazyList.of(Arrays.asList(elements));
    }

    public abstract E get(int index);

    public E first() {
        return value.value();
    }

    public E single() {
        System.out.println(tail.value().any());
        if (tail.value().any()) throw new IllegalStateException("List must contain exactly one element");
        return first();
    }

    /*public E get(int index) {
        return (index == 0 ? value.value() : tail.value().get(index - 1));
    }*/

    /*public LazyList<E> concat(LazyList<E> elements) {
        return none() ? elements :
                NormalNode.of(value, Lazy.of(() -> tail.value().concat(elements)));
    }*/

    /*public LazyList<E> concat(Iterable<E> elements) {
        return none() ? LazyList.of(elements) :
                NormalNode.of(value, Lazy.of(() -> tail.value().concat(elements)));
    }*/

    public LazyList<E> concat(Iterable<E> elements) {
        LazyList<E> partTwo = (elements instanceof LazyList ? (LazyList<E>) elements : LazyList.of(elements));
        return none() ? partTwo :
                NormalNode.of(value, Lazy.of(() -> tail.value().concat(elements)));
    }

    public abstract boolean any();

    public boolean none() {
        return !any();
    }

    public abstract <R> LazyList<R> map(Function<E, R> transform);

    public abstract LazyList<E> filter(Predicate<E> predicate);

    private List<E> toListHelper(List<E> result) {
        if (none()) return result;
        result.add(value.value());
        return tail.value().toListHelper(result);
    }

    public List<E> toList() {
        return toListHelper(new ArrayList<>());
    }

    @Override
    public Iterator<E> iterator() {
        return LazyListIterator.of(this);
    }



    private static class NormalNode<E> extends LazyList<E> {

        private NormalNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        public static <E> LazyList<E> of(Lazy<E> value, Lazy<LazyList<E>> tail) {
            return new NormalNode<>(value, tail);
        }

        public E get(int index) {
            if (index < 0) throw new IndexOutOfBoundsException();
            return index == 0 ? value.value() : tail.value().get(index - 1);
        }

        @Override
        public boolean any() {
            return true;
        }

        @Override
        public <R> LazyList<R> map(Function<E, R> transform) {
            return NormalNode.of(
                    Lazy.of(() -> transform.apply(value.value())),
                    Lazy.of(() -> tail.value().map(transform))
            );
        }

        @Override
        public LazyList<E> filter(Predicate<E> predicate) {
            return predicate.test(value.value()) ?
                    NormalNode.of(value, Lazy.of(() -> tail.value().filter(predicate))) :
                    tail.value().filter(predicate);
        }
    }



    private static class EndNode<E> extends LazyList<E> {

        private EndNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        public static <E> LazyList<E> empty() {
            return new EndNode<>(null, null);
        }

        public E get(int index) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean any() {
            return false;
        }

        @Override
        public <R> LazyList<R> map(Function<E, R> transform) {
            return EndNode.empty();
        }

        @Override
        public LazyList<E> filter(Predicate<E> predicate) {
            return this;
        }
    }
}
