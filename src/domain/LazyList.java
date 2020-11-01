package domain;

import java.util.*;
import java.util.function.*;

public abstract class LazyList<E> implements Iterable<E> {
    public final Lazy<E> value;
    public final Lazy<LazyList<E>> tail;

    // Constructors and factory methods =============================================================
    private LazyList(Lazy<E> value, Lazy<LazyList<E>> tail) {
        this.value = value;
        this.tail = tail;
    }

    private static <I, E> LazyList<E> concatHelper(Iterator<I> iterator, LazyList<E> elements, Function<I, Lazy<E>> makeLazy) {
        if (iterator.hasNext()) {
            I element = iterator.next();
            return NormalNode.of(
                    makeLazy.apply(element),
                    Lazy.of(() -> concatHelper(iterator, elements, makeLazy))
            );
        }
        return elements;
    }

    protected static <E> LazyList<E> concat(Iterator<E> iterator, LazyList<E> elements) {
        return concatHelper(iterator, elements, element -> Lazy.of(() -> element));
    }

    protected static <E> LazyList<E> lazyConcat(Iterator<Lazy<E>> iterator, LazyList<E> elements) {
        return concatHelper(iterator, elements, element -> element);
    }

    public static <E> LazyList<E> of(Iterable<E> elements) {
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
        return tail.value().isEmpty() ? value.value() : tail.value().last();
    }

    public E random() {
        return get(new Random().nextInt(size()));
    }

    public abstract E findFirst(Predicate<E> predicate);

    public E first(Predicate<E> predicate) {
        return findFirst(predicate);
    }

    protected abstract int indexOfFirstHelper(int counter, Predicate<E> predicate);

    public int indexOfFirst(Predicate<E> predicate) {
        return indexOfFirstHelper(0, predicate);
    }

    public int indexOfFirst(E element) {
        return indexOfFirst(current -> current.equals(element));
    }

    public int lastIndex() {
        return size() - 1;
    }

    /*public IdeaList<Integer> indices() {
        return rangeInclusive(0, lastIndex());
    }*/

    public abstract int size();

    public List<E> toList() {
        List<E> list = new ArrayList<>();
        forEach(list::add);
        return list;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, tail);
    }

    @Override
    public Iterator<E> iterator() {
        return LazyListIterator.of(Lazy.of(() -> this));
    }

    private Iterator<Lazy<E>> lazyIterator() {
        return LazyIterator.of(Lazy.of(() -> this));
    }

    private Iterator<E> nullIterator() {
        return NullIterator.empty();
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
        return findFirst(current -> current.equals(element)) != null;
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

    public boolean isNested() {
        return value.value() instanceof Iterable;
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

    public LazyList<E> concatToBackOf(Iterable<E> elements) {
        return concat(elements.iterator(), this);
    }

    public LazyList<E> concatToBackOf(LazyList<E> elements) {
        return lazyConcat(elements.lazyIterator(), this);
    }

    @SafeVarargs
    public final LazyList<E> addToFront(E... elements) {
        return concatToBackOf(Arrays.asList(elements));
    }

    public abstract LazyList<E> removeFirst(E element);

    public LazyList<E> removeAll(E element) {
        return where(current -> !current.equals(element));
    }

    public void forEachIndexed(BiConsumer<Integer, E> action) {
        int index = 0;
        for(E element : this) {
            action.accept(index, element);
            index++;
        }
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        forEachIndexed((index, current) -> action.accept(current));
    }


    // List operations ==============================================================================
    //public abstract <A> A reduceIndexed(TriFunction<A, E, Integer, A> operation, A initialValue);

    public abstract <R> LazyList<R> map(Function<E, R> transform);

    public <R> LazyList<R> select(Function<E, R> transform) {
        return map(transform);
    }

    public abstract LazyList<E> where(Predicate<E> predicate);

    public LazyList<E> filter(Predicate<E> predicate) {
        return where(predicate);
    }

    private static <E, A, B> boolean allHaveNext(Triplet<Iterator<E>, Iterator<A>, Iterator<B>> iterators) {
        return iterators.first.hasNext() && iterators.second.hasNext() && iterators.third.hasNext();
    }

    /*private static <E, A, B> boolean allHaveNext(Triplet<Iterator<E>, Iterator<A>, Iterator<B>> iterators) {
        return iterators.all((first, second, third) -> first.hasNext() && second.hasNext() && third.hasNext());
    }*/

    private static <E, A, B> Triplet<E, A, B> next(Triplet<Iterator<E>, Iterator<A>, Iterator<B>> iterators) {
        return Triplet.of(iterators.first.next(), iterators.second.next(), iterators.third.next());
    }

    private <A, B> LazyList<Triplet<E, A, B>> zipWithHelper(Triplet<Iterator<E>, Iterator<A>, Iterator<B>> iterators) {
        if (!allHaveNext(iterators)) return EndNode.empty();
        return NormalNode.of(
                Lazy.of(() -> next(iterators)),
                Lazy.of(() -> tail.value().zipWithHelper(iterators))
        );
    }

    public <A, B> LazyList<Triplet<E, A, B>> zipWith(Iterable<A> other, Iterable<B> other2) {
        return zipWithHelper( Triplet.of(iterator(), other.iterator(), other2.iterator()) );
    }

    public <A> LazyList<Pair<E, A>> zipWith(Iterable<A> other) {
        return zipWithHelper( Triplet.of(iterator(), other.iterator(), nullIterator()) ).map(Triplet::toPair);
    }


    // Ranges =======================================================================================



    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class NormalNode<E> extends LazyList<E> {

        // Constructors and factory methods =============================================================
        private NormalNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        public static <E> LazyList<E> of(Lazy<E> value, Lazy<LazyList<E>> tail) {
            return new NormalNode<>(value, tail);
        }

        private LazyList<E> create(UnaryOperator<LazyList<E>> function) {
            return NormalNode.of(value, Lazy.of(() -> function.apply(tail.value())));
        }

        private void handleNegativeIndex(int index) {
            if (index < 0) throw new IndexOutOfBoundsException("Index cannot be negative");
        }


        // Getters ======================================================================================
        @Override
        public E get(int index) {
            handleNegativeIndex(index);
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


        // Checks =======================================================================================
        @Override
        public boolean any() {
            return true;
        }


        // Modifiers ====================================================================================
        private LazyList<E> insertHelper(int index, Iterable<E> elements, BiFunction<Iterable<E>, LazyList<E>, LazyList<E>> concat) {
            handleNegativeIndex(index);
            return index == 0 ? concat.apply(elements, this) :
                    create(tail -> tail.insert(index - 1, elements));
        }

        @Override
        public LazyList<E> insert(int index, Iterable<E> elements) {
            return insertHelper(index, elements, (newElements, tail) -> concat(newElements.iterator(), tail));
        }

        @Override
        public LazyList<E> insert(int index, LazyList<E> elements) {
            return insertHelper(index, elements, (newElements, tail) -> lazyConcat(((LazyList<E>)newElements).lazyIterator(), tail));
        }

        public LazyList<E> removeFirst(E element) {
            return value.value().equals(element) ? tail.value() :
                    create(tail -> tail.removeFirst(element));
        }


        // List operations ==============================================================================
        /*@Override
        public <A> A reduceIndexed(TriFunction<Integer, A, E, A> operation, A initialValue) {

        }*/

        @Override
        public <R> LazyList<R> map(Function<E, R> transform) {
            return NormalNode.of(
                    Lazy.of(() -> transform.apply(value.value())),
                    Lazy.of(() -> tail.value().map(transform))
            );
        }

        @Override
        public LazyList<E> where(Predicate<E> predicate) {
            return predicate.test(value.value()) ?
                    create(tail -> tail.where(predicate)) : tail.value().where(predicate);
        }


        // Ranges =======================================================================================
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class EndNode<E> extends LazyList<E> {

        // Constructors and factory methods =============================================================
        private EndNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        public static <E> LazyList<E> empty() {
            return new EndNode<>(null, null);
        }

        private IndexOutOfBoundsException tooHighIndexException() {
            return new IndexOutOfBoundsException("Index cannot be bigger than upper bound");
        }


        // Getters ======================================================================================
        /*@Override
        public E get(int index) {
            throw new IndexOutOfBoundsException("Index cannot be bigger than upper bound");
        }*/

        @Override
        public E get(int index) {
            throw tooHighIndexException();
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


        // Checks =======================================================================================
        @Override
        public boolean any() {
            return false;
        }


        // Modifiers ====================================================================================
        /*@Override
        public LazyList<E> insert(int index, Iterable<E> elements) {
            throw new IndexOutOfBoundsException("Index cannot be bigger than size of list - 1");
        }*/

        @Override
        public LazyList<E> insert(int index, Iterable<E> elements) {
            throw tooHighIndexException();
        }

        /*@Override
        public LazyList<E> insert(int index, LazyList<E> elements) {
            throw new IndexOutOfBoundsException("Index cannot be bigger than size of list - 1");
        }*/

        @Override
        public LazyList<E> insert(int index, LazyList<E> elements) {
            throw tooHighIndexException();
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
        public LazyList<E> where(Predicate<E> predicate) {
            return this;
        }


        // Ranges =======================================================================================
    }
}
