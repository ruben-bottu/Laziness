package domain;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class LazyList<E> implements Iterable<E> {
    public final Lazy<E> value;
    public final Lazy<LazyList<E>> tail;

    // Constructors and factory methods =============================================================
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


    // Getters ======================================================================================
    public abstract E get(int index);

    public E first() {
        return value.value();
    }

    public abstract E single();

    public E last() {
        return isTailEmpty() ? value.value() : tail.value().last();
    }

    public E random() {
        return get(new Random().nextInt(size()));
    }

    public abstract E find(Predicate<E> predicate);

    protected abstract int indexOfFirstHelper(int counter, Predicate<E> predicate);

    public int indexOfFirst(Predicate<E> predicate) {
        return indexOfFirstHelper(0, predicate);
    }

    public int indexOf(E element) {
        return indexOfFirst(item -> item.equals(element));
    }

    public int lastIndex() {
        return size() - 1;
    }

    /*public IdeaList<Integer> indices() {
        return rangeInclusive(0, lastIndex());
    }*/

    public abstract int size();

    protected abstract List<E> toListHelper(List<E> result);

    public List<E> toList() {
        return toListHelper(new ArrayList<>());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, tail);
    }

    @Override
    public Iterator<E> iterator() {
        return LazyListIterator.of(this);
    }


    // Checks =======================================================================================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LazyList<?> lazyList = (LazyList<?>) o;
        return Objects.equals(value, lazyList.value) &&
                Objects.equals(tail, lazyList.tail);
    }

    /*public boolean contains(E element) {
        return indexOf(element) != -1;
    }*/

    public boolean contains(E element) {
        return find(item -> item.equals(element)) != null;
    }

    public boolean isEmpty() {
        return none();
    }

    public boolean isNested() {
        return value.value() instanceof LazyList;
    }

    private boolean isTailEmpty() {
        return tail.value().isEmpty();
    }

    public boolean all(Predicate<E> predicate) {
        return !map(predicate::test).contains(false);
    }

    public abstract boolean any();

    public boolean any(Predicate<E> predicate) {
        return map(predicate::test).contains(true);
    }

    public boolean none() {
        return !any();
    }

    public boolean none(Predicate<E> predicate) {
        return !any(predicate);
    }

    /*public LazyList<E> concat(Iterable<E> elements) {
        LazyList<E> partTwo = (elements instanceof LazyList ? (LazyList<E>) elements : LazyList.of(elements));
        return isEmpty() ? partTwo :
                NormalNode.of(value, Lazy.of(() -> tail.value().concat(elements)));
    }*/


    // Modifiers ====================================================================================
    public abstract LazyList<E> concat(Iterable<E> elements);


    // List operations ==============================================================================
    public abstract <R> LazyList<R> map(Function<E, R> transform);

    public abstract LazyList<E> filter(Predicate<E> predicate);


    // Ranges =======================================================================================


    // ==============================================================================================
    // ==============================================================================================
    private static class NormalNode<E> extends LazyList<E> {

        // Constructors and factory methods =============================================================
        private NormalNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        public static <E> LazyList<E> of(Lazy<E> value, Lazy<LazyList<E>> tail) {
            return new NormalNode<>(value, tail);
        }


        // Getters ======================================================================================
        public E get(int index) {
            if (index < 0) throw new IndexOutOfBoundsException("Index cannot be smaller than 0");
            return index == 0 ? value.value() : tail.value().get(index - 1);
        }

        public E single() {
            if (tail.value().any()) throw new IllegalStateException("List cannot contain more than one element");
            return first();
        }

        public E find(Predicate<E> predicate) {
            return predicate.test(value.value()) ? value.value() : tail.value().find(predicate);
        }

        protected int indexOfFirstHelper(int counter, Predicate<E> predicate) {
            return predicate.test(value.value()) ? counter : tail.value().indexOfFirstHelper(counter + 1, predicate);
        }

        public int size() {
            return 1 + tail.value().size();
        }

        @Override
        protected List<E> toListHelper(List<E> result) {
            result.add(value.value());
            return tail.value().toListHelper(result);
        }


        // Checks =======================================================================================
        @Override
        public boolean any() {
            return true;
        }


        // Modifiers ====================================================================================
        public LazyList<E> concat(Iterable<E> elements) {
            return NormalNode.of(value, Lazy.of(() -> tail.value().concat(elements)));
        }


        // List operations ==============================================================================
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


        // Ranges =======================================================================================
    }


    // ==============================================================================================
    // ==============================================================================================
    private static class EndNode<E> extends LazyList<E> {

        // Constructors and factory methods =============================================================
        private EndNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        public static <E> LazyList<E> empty() {
            return new EndNode<>(null, null);
        }


        // Getters ======================================================================================
        public E get(int index) {
            throw new IndexOutOfBoundsException("Index cannot be bigger than size of list - 1");
        }

        public E single() {
            throw new IllegalStateException("List cannot be empty");
        }

        public E find(Predicate<E> predicate) {
            return null;
        }

        protected int indexOfFirstHelper(int counter, Predicate<E> predicate) {
            return -1;
        }

        public int size() {
            return 0;
        }

        @Override
        protected List<E> toListHelper(List<E> result) {
            return result;
        }


        // Checks =======================================================================================
        @Override
        public boolean any() {
            return false;
        }


        // Modifiers ====================================================================================
        public LazyList<E> concat(Iterable<E> elements) {
            return elements instanceof LazyList ? (LazyList<E>) elements : LazyList.of(elements);
        }


        // List operations ==============================================================================
        @Override
        public <R> LazyList<R> map(Function<E, R> transform) {
            return EndNode.empty();
        }

        @Override
        public LazyList<E> filter(Predicate<E> predicate) {
            return this;
        }


        // Ranges =======================================================================================
    }
}
