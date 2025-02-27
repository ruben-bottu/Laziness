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

    private static <E> LazyList<E> create(Lazy<E> value, Lazy<LazyList<E>> tail) {
        return new NormalNode<>(value, tail);
    }

    public static <E> LazyList<E> empty() {
        return new EndNode<>();
    }

    private static <I, E> LazyList<E> concatHelper(Iterator<I> iterator, Function<I, Lazy<E>> makeLazy, LazyList<E> elements) {
        if (iterator.hasNext()) {
            I element = iterator.next();
            return LazyList.create(
                    makeLazy.apply(element),
                    Lazy.of(() -> concatHelper(iterator, makeLazy, elements))
            );
        }
        return elements;
    }

    private static <E> LazyList<E> concat(Iterator<E> iterator, LazyList<E> elements) {
        return concatHelper(iterator, element -> Lazy.of(() -> element), elements);
    }

    private static <E> LazyList<E> lazyConcat(Iterator<Lazy<E>> iterator, LazyList<E> elements) {
        return concatHelper(iterator, element -> element, elements);
    }

    public static <E> LazyList<E> of(Iterable<E> elements) {
        return concat(elements.iterator(), LazyList.empty());
    }

    @SafeVarargs
    public static <E> LazyList<E> of(E... elements) {
        return LazyList.of(Arrays.asList(elements));
    }

    public static <E> LazyList<E> initialiseWith(int length, Function<Integer, E> generator) {
        if (length < 0) throw new IllegalArgumentException("Illegal capacity: " + length);
        return rangeLength(0, length).map(generator);
    }

    private static IndexOutOfBoundsException indexOutOfBoundsException(int index) {
        return new IndexOutOfBoundsException("Index " + index + " out of bounds of this list");
    }


    // Getters ======================================================================================
    public abstract E get(int index);

    public abstract E first();

    public abstract E single();

    public abstract E last();

    public E random() {
        return get(new Random().nextInt(size()));
    }

    public abstract E findFirst(Predicate<E> predicate);

    public E first(Predicate<E> predicate) {
        return findFirst(predicate);
    }

    private static <V, R> R or(V value, Function<V, R> transform, R alternative) {
        return value == null ? alternative : transform.apply(value);
    }

    private static <E> int orIndex(IndexElement<E> pair, int alternative) {
        return or(pair, duo -> duo.index, alternative);
    }

    private static <E> int orMinusOne(IndexElement<E> pair) {
        return pair == null ? -1 : pair.index;
    }

    private static <E> int indexOrMinusOne(IndexElement<E> pair) {
        return pair == null ? -1 : pair.index;
    }

    public int indexOfFirst(Predicate<E> predicate) {
        return orIndex(withIndex().findFirst(pair -> predicate.test(pair.element)), -1);
    }

    public int indexOfFirst(E element) {
        return indexOfFirst(current -> current.equals(element));
    }

    public int lastIndex() {
        return size() - 1;
    }

    public LazyList<Integer> indices() {
        return rangeInclusive(0, lastIndex());
    }

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
    public String toString() {
        return toList().toString();
    }

    @Override
    public Iterator<E> iterator() {
        return Enumerator.of(Lazy.of(() -> this));
    }

    private Iterator<Lazy<E>> lazyIterator() {
        return Enumerator.lazyOf(Lazy.of(() -> this));
    }


    // Checks =======================================================================================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LazyList<?> lazyList = (LazyList<?>) o;
        return zipWith(lazyList).all(pair -> pair.first.equals(pair.second));
    }

    public boolean contains(E element) {
        return findFirst(current -> current.equals(element)) != null;
    }

    public boolean containsAll(Iterable<E> elements) {
        return containsAll(LazyList.of(elements));
    }

    public boolean containsAll(LazyList<E> elements) {
        return elements.all(this::contains);
    }

    public boolean isEmpty() {
        return none();
    }

    public abstract boolean isNested();

    protected abstract boolean containsLazyLists();

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

    public LazyList<E> concatWith(Iterable<E> elements) {
        return concatWith(LazyList.of(elements));
    }

    public LazyList<E> concatWith(LazyList<E> elements) {
        return lazyConcat(lazyIterator(), elements);
    }

    @SafeVarargs
    public final LazyList<E> add(E... elements) {
        return concatWith(Arrays.asList(elements));
    }

    public LazyList<E> linkToBackOf(Iterable<E> elements) {
        return concat(elements.iterator(), this);
    }

    public LazyList<E> linkToBackOf(LazyList<E> elements) {
        return lazyConcat(elements.lazyIterator(), this);
    }

    @SafeVarargs
    public final LazyList<E> addToFront(E... elements) {
        return linkToBackOf(Arrays.asList(elements));
    }

    public abstract LazyList<E> removeFirst(E element);

    public abstract LazyList<E> removeAt(int index);

    public LazyList<E> removeAll(E element) {
        return where(current -> !current.equals(element));
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        for (E element : this) action.accept(element);
    }

    public void forEachIndexed(BiConsumer<Integer, E> action) {
        withIndex().forEach(pair -> action.accept(pair.index, pair.element));
    }


    // List operations ==============================================================================
    public abstract <A> A reduce(A initialValue, BiFunction<A, E, A> operation);

    public <A> A reduceIndexed(A initialValue, TriFunction<Integer, A, E, A> operation) {
        return withIndex().reduce(initialValue, (acc, pair) -> operation.apply(pair.index, acc, pair.element));
    }

    public abstract E reduce(BinaryOperator<E> operation);

    /*public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
        return withIndex().reduce((acc, pair) -> operation.apply(pair.index, acc, pair.element));
    }*/

    /*public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
        return withIndex().reduce((acc, pair) -> IndexElement.of(0, operation.apply(pair.index, acc.element, pair.element))).element;
    }*/

    public abstract E reduceIndexed(TriFunction<Integer, E, E, E> operation);

    public abstract <R> LazyList<R> map(Function<E, R> transform);

    public <R> LazyList<R> mapIndexed(BiFunction<Integer, E, R> transform) {
        return withIndex().map(pair -> transform.apply(pair.index, pair.element));
    }

    public <R> LazyList<R> select(Function<E, R> transform) {
        return map(transform);
    }

    public <R> LazyList<R> selectIndexed(BiFunction<Integer, E, R> transform) {
        return mapIndexed(transform);
    }

    public abstract LazyList<E> where(Predicate<E> predicate);

    public LazyList<E> whereIndexed(BiPredicate<Integer, E> predicate) {
        return withIndex().where(pair -> predicate.test(pair.index, pair.element)).select(pair -> pair.element);
    }

    public LazyList<E> filter(Predicate<E> predicate) {
        return where(predicate);
    }

    public LazyList<E> filterIndexed(BiPredicate<Integer, E> predicate) {
        return whereIndexed(predicate);
    }

    public LazyList<E> findAll(Predicate<E> predicate) {
        return where(predicate);
    }

    public LazyList<E> findAllIndexed(BiPredicate<Integer, E> predicate) {
        return whereIndexed(predicate);
    }

    private <R> LazyList<R> flattenNestedIterables() {
        return ((LazyList<Iterable<R>>) this).map(LazyList::of).flattenNestedLazyLists();
    }

    /*private <R> LazyList<R> flattenNestedLazyLists() {
        return ((LazyList<LazyList<R>>) this).reduce(LazyList::concatWith);
    }*/

    /*private <R> LazyList<R> flattenNestedLazyLists() {
        return LazyList.create(
                Lazy.of(() -> ((LazyList<R>) value.value())),
                Lazy.of(() -> tail.value().flattenNestedLazyLists())
        );
    }*/

    protected abstract <R> LazyList<R> flattenNestedLazyLists();

    public <R> LazyList<R> flatten() {
        if (containsLazyLists()) return flattenNestedLazyLists();
        else if (isNested()) return flattenNestedIterables();
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }

    private static <E, A, B> boolean allHaveNext(Triplet<Iterator<E>, Iterator<A>, Iterator<B>> iterators) {
        return iterators.first.hasNext() && iterators.second.hasNext() && iterators.third.hasNext();
    }

    private static <E, A, B> Triplet<E, A, B> next(Triplet<Iterator<E>, Iterator<A>, Iterator<B>> iterators) {
        return Triplet.of(iterators.first.next(), iterators.second.next(), iterators.third.next());
    }

    private <A, B> LazyList<Triplet<E, A, B>> zipWithHelper(Triplet<Iterator<E>, Iterator<A>, Iterator<B>> iterators) {
        if (!allHaveNext(iterators)) return LazyList.empty();
        return LazyList.create(
                Lazy.of(() -> next(iterators)),
                Lazy.of(() -> tail.value().zipWithHelper(iterators)) // remove tail.value() ??
        );
    }

    public <A, B> LazyList<Triplet<E, A, B>> zipWith(Iterable<A> other, Iterable<B> other2) {
        return zipWithHelper(Triplet.of(iterator(), other.iterator(), other2.iterator()));
    }

    public <A> LazyList<Pair<E, A>> zipWith(Iterable<A> other) {
        return zipWith(other, Enumerable.infiniteNulls()).map(Triplet::toPair);
    }


    // Ranges =======================================================================================
    public static LazyList<Integer> rangeInclusive(int from, int to) {
        return (from > to) ? LazyList.empty() : LazyList.create(Lazy.of(() -> from), Lazy.of(() -> rangeInclusive(from + 1, to)));
    }

    public static LazyList<Integer> rangeExclusive(int from, int upTo) {
        return rangeInclusive(from, upTo - 1);
    }

    public static LazyList<Integer> rangeLength(int from, int length) {
        return rangeExclusive(from, from + length);
    }

    public static LazyList<Integer> infiniteIndices() {
        return rangeInclusive(0, Integer.MAX_VALUE);
    }

    /*public LazyList<Pair<Integer, E>> withIndex() {
        return infiniteIndices().zipWith(this);
    }*/

    public LazyList<IndexElement<E>> withIndex() {
        return infiniteIndices().zipWith(this).map(Pair::toIndexElement);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class NormalNode<E> extends LazyList<E> {

        // Constructors and factory methods =============================================================
        private NormalNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        private LazyList<E> constructTail(UnaryOperator<LazyList<E>> function) {
            return LazyList.create(value, Lazy.of(() -> function.apply(tail.value())));
        }

        private static void handleNegativeIndex(int index) {
            if (index < 0) throw indexOutOfBoundsException(index);
        }


        // Getters ======================================================================================
        @Override
        public E get(int index) {
            handleNegativeIndex(index);
            return index == 0 ? value.value() : tail.value().get(index - 1);
        }

        @Override
        public E first() {
            return value.value();
        }

        @Override
        public E single() {
            if (tail.value().any()) throw new IllegalArgumentException("List has more than one element");
            return first();
        }

        @Override
        public E last() {
            return tail.value().isEmpty() ? value.value() : tail.value().last();
        }

        @Override
        public E findFirst(Predicate<E> predicate) {
            return predicate.test(value.value()) ? value.value() : tail.value().findFirst(predicate);
        }

        @Override
        public int size() {
            return 1 + tail.value().size();
        }


        // Checks =======================================================================================
        @Override
        public boolean isNested() {
            return value.value() instanceof Iterable;
        }

        @Override
        protected boolean containsLazyLists() {
            return value.value() instanceof LazyList;
        }

        @Override
        public boolean any() {
            return true;
        }


        // Modifiers ====================================================================================
        private LazyList<E> insertHelper(int index, Iterable<E> elements, BiFunction<Iterable<E>, LazyList<E>, LazyList<E>> concat) {
            handleNegativeIndex(index);
            return index == 0 ? concat.apply(elements, this) :
                    constructTail(tail -> tail.insert(index - 1, elements));
        }

        @Override
        public LazyList<E> insert(int index, Iterable<E> elements) {
            return insertHelper(index, elements, (newElements, tail) -> concat(newElements.iterator(), tail));
        }

        @Override
        public LazyList<E> insert(int index, LazyList<E> elements) {
            return insertHelper(index, elements, (newElements, tail) -> lazyConcat(((LazyList<E>) newElements).lazyIterator(), tail));
        }

        @Override
        public LazyList<E> removeFirst(E element) {
            return value.value().equals(element) ? tail.value() :
                    constructTail(tail -> tail.removeFirst(element));
        }

        @Override
        public LazyList<E> removeAt(int index) {
            handleNegativeIndex(index);
            return index == 0 ? tail.value() : constructTail(tail -> tail.removeAt(index - 1));
        }


        // List operations ==============================================================================
        private static <E, A> A reduceHelper(A initialValue, LazyList<E> list, BiFunction<A, E, A> operation) {
            A accumulator = initialValue;
            for (E element : list) accumulator = operation.apply(accumulator, element);
            return accumulator;
        }

        @Override
        public <A> A reduce(A initialValue, BiFunction<A, E, A> operation) {
            return reduceHelper(initialValue, this, operation);
        }

        @Override
        public E reduce(BinaryOperator<E> operation) {
            return reduceHelper(first(), tail.value(), operation);
        }

        /*public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
            if (isEmpty()) throw new UnsupportedOperationException();
            return tail.value().withIndex().reduce(first(), (acc, pair) -> operation.apply(pair.index, acc, pair.element));
        }*/

        /*@Override
        public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
            return reduceHelper(first(), withIndex().tail.value(), (acc, pair) -> operation.apply(pair.index, acc, pair.element));
        }*/

        @Override
        public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
            return reduceHelper(first(), withIndex().tail.value().tail.value(), (acc, pair) -> operation.apply(pair.index, acc, pair.element));
        }

        @Override
        public <R> LazyList<R> map(Function<E, R> transform) {
            return LazyList.create(
                    Lazy.of(() -> transform.apply(value.value())),
                    Lazy.of(() -> tail.value().map(transform))
            );
        }

        @Override
        public LazyList<E> where(Predicate<E> predicate) {
            return predicate.test(value.value()) ?
                    constructTail(tail -> tail.where(predicate)) : tail.value().where(predicate);
        }

        /*@Override
        public LazyList<E> where(Predicate<E> predicate) {
            return LazyList.create(
                    Lazy.of(() -> predicate.test(value.value()) ? value : tail.value().where(predicate)),
                    Lazy.of(() -> tail.value().where(predicate))
            );
        }*/

        @Override
        protected <R> LazyList<R> flattenNestedLazyLists() {
            //return LazyList.create(Lazy.of(() -> ))
            return ((LazyList<R>) value.value()).concatWith(tail.value().flattenNestedLazyLists());
        }


        // Ranges =======================================================================================
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class EndNode<E> extends LazyList<E> {

        // Constructors and factory methods =============================================================
        private EndNode() {
            super(null, null);
        }

        private static NoSuchElementException listIsEmptyException() {
            return new NoSuchElementException("List is empty");
        }

        private static UnsupportedOperationException reduceEmptyListException() {
            return new UnsupportedOperationException("Empty list cannot be reduced");
        }


        // Getters ======================================================================================
        @Override
        public E get(int index) {
            throw indexOutOfBoundsException(index);
        }

        @Override
        public E first() {
            throw listIsEmptyException();
        }

        @Override
        public E single() {
            throw listIsEmptyException();
        }

        @Override
        public E last() {
            throw listIsEmptyException();
        }

        @Override
        public E findFirst(Predicate<E> predicate) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }


        // Checks =======================================================================================
        @Override
        public boolean isNested() {
            return false;
        }

        @Override
        protected boolean containsLazyLists() {
            return false;
        }

        @Override
        public boolean any() {
            return false;
        }


        // Modifiers ====================================================================================
        @Override
        public LazyList<E> insert(int index, Iterable<E> elements) {
            throw indexOutOfBoundsException(index);
        }

        @Override
        public LazyList<E> insert(int index, LazyList<E> elements) {
            throw indexOutOfBoundsException(index);
        }

        @Override
        public LazyList<E> removeFirst(E element) {
            return this;
        }

        @Override
        public LazyList<E> removeAt(int index) {
            throw indexOutOfBoundsException(index);
        }


        // List operations ==============================================================================
        @Override
        public <A> A reduce(A initialValue, BiFunction<A, E, A> operation) {
            return initialValue;
        }

        @Override
        public E reduce(BinaryOperator<E> operation) {
            throw reduceEmptyListException();
        }

        @Override
        public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
            throw reduceEmptyListException();
        }

        @Override
        public <R> LazyList<R> map(Function<E, R> transform) {
            return LazyList.empty();
        }

        @Override
        public LazyList<E> where(Predicate<E> predicate) {
            return this;
        }

        @Override
        protected <R> LazyList<R> flattenNestedLazyLists() {
            return LazyList.empty();
        }

        // Ranges =======================================================================================
    }
}
