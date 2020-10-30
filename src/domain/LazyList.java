package domain;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

public abstract class LazyList<E> implements Iterable<E> {
    public final Lazy<E> value;
    public final Lazy<LazyList<E>> tail;

    // Constructors and factory methods =============================================================
    private LazyList(Lazy<E> value, Lazy<LazyList<E>> tail) {
        this.value = value;
        this.tail = tail;
    }

    // Maybe replace this with ofHelper if lazyConcat can be used for addToFront(), concatToFront() etc.
    protected static <E> LazyList<E> concat(Iterator<E> iterator, LazyList<E> elements) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return NormalNode.of(
                    Lazy.of(() -> element),
                    Lazy.of(() -> concat(iterator, elements))
            );
        }
        return elements;
    }

    protected static <E> LazyList<E> lazyConcat(Iterator<Lazy<E>> iterator, LazyList<E> elements) {
        if (iterator.hasNext()) {
            Lazy<E> element = iterator.next();
            return NormalNode.of(element, Lazy.of(() -> lazyConcat(iterator, elements)));
        }
        return elements;
    }

    /*private static <E> LazyList<E> ofHelper(Iterator<? extends E> iterator) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return NormalNode.of(Lazy.of(() -> element), Lazy.of(() -> ofHelper(iterator)));
        }
        return EndNode.empty();
    }*/

    public static <E> LazyList<E> of(Iterable<E> elements) {
        //return ofHelper(elements.iterator());
        return concat(elements.iterator(), EndNode.empty());
    }

    @SafeVarargs
    public static <E> LazyList<E> of(E... elements) {
        return LazyList.of(Arrays.asList(elements));
    }

    public static <E> LazyList<E> empty() {
        return LazyList.of();
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

    public abstract E findFirst(Predicate<E> predicate);

    protected abstract int indexOfFirstHelper(int counter, Predicate<E> predicate);

    public int indexOfFirst(Predicate<E> predicate) {
        return indexOfFirstHelper(0, predicate);
    }

    public int indexOfFirst(E element) {
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

    private Iterator<Lazy<E>> lazyIterator() {
        return LazyListLazyIterator.of(Lazy.of(() -> this));
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
        return findFirst(item -> item.equals(element)) != null;
    }

    public boolean containsAll(Iterable<E> elements) {
        return LazyList.of(elements).all(this::contains);
    }

    public boolean containsAll(LazyList<E> elements) {
        return elements.all(this::contains);
    }

    public boolean isEmpty() {
        return none();
    }

    // What if the nested element is not a LazyList?? Maybe with LazyList.of(E... elements) it is possible to insert other types of collection into LazyList
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


    // Modifiers ====================================================================================
    public abstract LazyList<E> insert(int index, Iterable<E> elements);

    public abstract LazyList<E> insert(int index, LazyList<E> elements);

    /*public abstract LazyList<E> concat(Iterable<E> elements);*/

    /*public LazyList<E> concat(Iterable<E> elements) {
        return concat(iterator(), LazyList.of(elements));
    }*/

    public LazyList<E> concat(Iterable<E> elements) {
        return lazyConcat(lazyIterator(), LazyList.of(elements));
    }

    public LazyList<E> concat(LazyList<E> elements) {
        return lazyConcat(lazyIterator(), elements);
    }

    @SafeVarargs
    public final LazyList<E> add(E... elements) {
        return concat(Arrays.asList(elements));
    }

    /*@Override
    public LazyList<E> filter(Predicate<E> predicate) {
        return predicate.test(value.value()) ?
                NormalNode.of(value, Lazy.of(() -> tail.value().filter(predicate))) :
                tail.value().filter(predicate);
    }*/

    public abstract LazyList<E> removeFirst(E element);

    public LazyList<E> removeAll(E element) {
        return filter(cur -> !cur.equals(element));
    }

    // Kan efficiënter denk ik
    /*public void forEachIndexed(ObjIntConsumer<? super E> action) {
        for (int i = 0; i < size(); i++) {
            action.accept(get(i), i);
        }
    }*/


    // List operations ==============================================================================
    public abstract <R> LazyList<R> map(Function<E, R> transform);

    public abstract LazyList<E> filter(Predicate<E> predicate);


    // Ranges =======================================================================================


    // ==============================================================================================
    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    private static class NormalNode<E> extends LazyList<E> {

        // Constructors and factory methods =============================================================
        private NormalNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        public static <E> LazyList<E> of(Lazy<E> value, Lazy<LazyList<E>> tail) {
            return new NormalNode<>(value, tail);
        }

        public void handleNegativeIndex(int index) {
            if (index < 0) throw new IndexOutOfBoundsException("Index cannot be negative");
        }


        // Getters ======================================================================================
        @Override
        public E get(int index) {
            if (index < 0) throw new IndexOutOfBoundsException("Index cannot be negative");
            return index == 0 ? value.value() : tail.value().get(index - 1);
        }

        @Override
        public E single() {
            if (tail.value().any()) throw new IllegalStateException("List cannot contain more than one element");
            return first();
        }

        @Override
        public E findFirst(Predicate<E> predicate) {
            return predicate.test(value.value()) ? value.value() : tail.value().findFirst(predicate);
        }

        @Override
        protected int indexOfFirstHelper(int counter, Predicate<E> predicate) {
            return predicate.test(value.value()) ? counter : tail.value().indexOfFirstHelper(counter + 1, predicate);
        }

        @Override
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
        /*@Override
        public LazyList<E> insert(int index, Iterable<E> elements) {
            if (index < 0) throw new IndexOutOfBoundsException();
            if (index == 0) return concat(elements.iterator(), this);
            return NormalNode.of(value, Lazy.of(() -> tail.value().insert(index - 1, elements)));
        }*/

        private LazyList<E> insertHelper(int index, Iterable<E> elements, BiFunction<Iterable<E>, LazyList<E>, LazyList<E>> concat) {
            if (index < 0) throw new IndexOutOfBoundsException("Index cannot be negative");
            return index == 0 ? concat.apply(elements, this) :
                    NormalNode.of(value, Lazy.of(() -> tail.value().insert(index - 1, elements)));
        }

        /*@Override
        public LazyList<E> insert(int index, Iterable<E> elements) {
            if (index < 0) throw new IndexOutOfBoundsException("Index cannot be negative");
            return index == 0 ? concat(elements.iterator(), this) :
                    NormalNode.of(value, Lazy.of(() -> tail.value().insert(index - 1, elements)));
        }*/

        @Override
        public LazyList<E> insert(int index, Iterable<E> elements) {
            return insertHelper(index, elements, (toBeInserted, tail) -> concat(toBeInserted.iterator(), tail));
        }

        /*@Override
        public LazyList<E> insert(int index, LazyList<E> elements) {
            if (index < 0) throw new IndexOutOfBoundsException("Index cannot be negative");
            return index == 0 ? lazyConcat(elements.lazyIterator(), this) :
                    NormalNode.of(value, Lazy.of(() -> tail.value().insert(index - 1, elements)));
        }*/

        @Override
        public LazyList<E> insert(int index, LazyList<E> elements) {
            return insertHelper(index, elements, (toBeInserted, tail) -> lazyConcat(((LazyList<E>)toBeInserted).lazyIterator(), tail));
        }

        /*@Override
        public LazyList<E> concat(Iterable<E> elements) {
            return NormalNode.of(value, Lazy.of(() -> tail.value().concat(elements)));
        }*/

        /*public LazyList<E> removeFirst(E element) {
            if (value.value().equals(element)) return tail.value();
            return NormalNode.of(value, Lazy.of(() -> tail.value().removeFirst(element)));
        }*/

        public LazyList<E> removeFirst(E element) {
            return value.value().equals(element) ? tail.value() :
                    NormalNode.of(value, Lazy.of(() -> tail.value().removeFirst(element)));
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
        @Override
        public E get(int index) {
            throw new IndexOutOfBoundsException("Index cannot be bigger than size of list - 1");
        }

        @Override
        public E single() {
            throw new IllegalStateException("List cannot be empty");
        }

        @Override
        public E findFirst(Predicate<E> predicate) {
            return null;
        }

        @Override
        protected int indexOfFirstHelper(int counter, Predicate<E> predicate) {
            return -1;
        }

        @Override
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
        @Override
        public LazyList<E> insert(int index, Iterable<E> elements) {
            throw new IndexOutOfBoundsException("Index cannot be bigger than size of list - 1");
        }

        @Override
        public LazyList<E> insert(int index, LazyList<E> elements) {
            throw new IndexOutOfBoundsException("Index cannot be bigger than size of list - 1");
        }

        /*@Override
        public LazyList<E> concat(Iterable<E> elements) {
            return LazyList.of(elements);
        }*/

        public LazyList<E> removeFirst(E element) {
            return this;
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
