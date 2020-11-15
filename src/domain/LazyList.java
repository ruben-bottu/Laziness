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

    static <E> LazyList<E> create(Lazy<E> value, Lazy<LazyList<E>> tail) {
        return new NormalNode<>(value, tail);
    }

    public static <E> LazyList<E> empty() {
        return new EndNode<>();
    }

    private static <E> LazyList<E> concat(Iterator<E> iterator, LazyList<E> elements) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return LazyList.create(
                    Lazy.of(() -> element),
                    Lazy.of(() -> concat(iterator, elements))
            );
        }
        return elements;
    }

    public static <E> LazyList<E> of(Iterable<E> elements) {
        return concat(elements.iterator(), LazyList.empty());
    }

    @SafeVarargs
    public static <E> LazyList<E> of(E... elements) {
        return LazyList.of(Arrays.asList(elements));
    }

    public static <E> LazyList<E> initialiseWith(int length, Function<Integer, E> generator) {
        if (length < 0) throw new IllegalArgumentException("Length cannot be negative. Given: " + length);
        return Range.from(0).length(length).map(generator);
    }

    private static IndexOutOfBoundsException negativeIndexException(int index) {
        return new IndexOutOfBoundsException("Index cannot be negative. Given: " + index);
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

    private static <E> int indexOrMinusOne(IndexElement<E> pair) {
        return pair == null ? -1 : pair.index;
    }

    public int indexOfFirst(Predicate<E> predicate) {
        return indexOrMinusOne(withIndex().findFirst(currentPair -> predicate.test(currentPair.element)));
    }

    public int indexOfFirst(E element) {
        return indexOfFirst(current -> current.equals(element));
    }

    public int lastIndex() {
        return size() - 1;
    }

    public abstract LazyList<Integer> indices();

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


    // Checks =======================================================================================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LazyList<?> lazyList = (LazyList<?>) o;
        if (size() != lazyList.size()) return false;
        return zipWith(lazyList).all(currentPair -> currentPair.first.equals(currentPair.second));
    }

    public boolean contains(E element) {
        return indexOfFirst(element) != -1;
    }

    public boolean containsAll(Iterable<E> elements) {
        return LazyList.of(elements).all(this::contains);
    }

    public boolean isEmpty() {
        return none();
    }

    public abstract boolean isNested();

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
    public abstract LazyList<E> insertAt(int index, Iterable<E> elements);

    public LazyList<E> concatWith(Iterable<E> elements) {
        return concat(iterator(), LazyList.of(elements));
    }

    @SafeVarargs
    public final LazyList<E> add(E... elements) {
        return concatWith(Arrays.asList(elements));
    }

    public LazyList<E> linkToBackOf(Iterable<E> elements) {
        return concat(elements.iterator(), this);
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
        withIndex().forEach(currentPair -> action.accept(currentPair.index, currentPair.element));
    }


    // List operations ==============================================================================
    private static <E, A> A reduceHelper(A initialValue, LazyList<E> list, BiFunction<A, E, A> operation) {
        A accumulator = initialValue;
        for (E element : list) accumulator = operation.apply(accumulator, element);
        return accumulator;
    }

    public <A> A reduce(A initialValue, BiFunction<A, E, A> operation) {
        return reduceHelper(initialValue, this, operation);
    }

    public <A> A reduceIndexed(A initialValue, TriFunction<Integer, A, E, A> operation) {
        return withIndex().reduce(initialValue, (accumulator, currentPair) -> operation.apply(currentPair.index, accumulator, currentPair.element));
    }

    public abstract E reduce(BinaryOperator<E> operation);

    /*public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
        return withIndex().reduce((accumulator, currentPair) -> operation.apply(currentPair.index, accumulator, currentPair.element));
    }*/

    public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
        return withIndex().reduce((accumulator, currentPair) -> IndexElement.of(0, operation.apply(currentPair.index, accumulator.element, currentPair.element))).element;
    }

    public abstract <R> LazyList<R> map(Function<E, R> transform);

    public <R> LazyList<R> mapIndexed(BiFunction<Integer, E, R> transform) {
        return withIndex().map(currentPair -> transform.apply(currentPair.index, currentPair.element));
    }

    public <R> LazyList<R> select(Function<E, R> transform) {
        return map(transform);
    }

    public <R> LazyList<R> selectIndexed(BiFunction<Integer, E, R> transform) {
        return mapIndexed(transform);
    }

    public abstract LazyList<E> where(Predicate<E> predicate);

    public LazyList<E> whereIndexed(BiPredicate<Integer, E> predicate) {
        return withIndex().where(currentPair -> predicate.test(currentPair.index, currentPair.element)).select(currentPair -> currentPair.element);
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

    abstract <R> LazyList<R> concatenateNestedIterables();

    public <R> LazyList<R> flatten() {
        if (isNested()) return concatenateNestedIterables();
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }

    private static <A, B, C> boolean allHaveNext(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return iterators.first.hasNext() && iterators.second.hasNext() && iterators.third.hasNext();
    }

    private static <A, B, C> Triplet<A, B, C> next(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return Triplet.of(iterators.first.next(), iterators.second.next(), iterators.third.next());
    }

    private static <A, B, C> LazyList<Triplet<A, B, C>> zipWithHelper(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        if (!allHaveNext(iterators)) return LazyList.empty();
        Triplet<A, B, C> elements = next(iterators);
        return LazyList.create(
                Lazy.of(() -> elements),
                Lazy.of(() -> zipWithHelper(iterators))
        );
    }

    public <A, B> LazyList<Triplet<E, A, B>> zipWith(Iterable<A> other, Iterable<B> other2) {
        return zipWithHelper(Triplet.of(iterator(), other.iterator(), other2.iterator()));
    }

    public <A> LazyList<Pair<E, A>> zipWith(Iterable<A> other) {
        return zipWith(other, Enumerable.infiniteNulls()).map(Triplet::toPair);
    }

    public LazyList<IndexElement<E>> withIndex() {
        return Range.infiniteIndices().zipWith(this).map(Pair::toIndexElement);
    }

    public LazyList<E> step(int size) {
        if (size < 1) throw new IllegalArgumentException("Size cannot be negative. Given: " + size);
        return whereIndexed((index, current) -> index % size == 0);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class NormalNode<E> extends LazyList<E> {

        // Constructors and factory methods =============================================================
        private NormalNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        private LazyList<E> createTail(UnaryOperator<LazyList<E>> function) {
            return LazyList.create(value, Lazy.of(() -> function.apply(tail.value())));
        }

        private static void handleNegativeIndex(int index) {
            if (index < 0) throw negativeIndexException(index);
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
        public LazyList<Integer> indices() {
            return Range.from(0).upToAndIncluding(lastIndex());
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
        public boolean any() {
            return true;
        }


        // Modifiers ====================================================================================
        @Override
        public LazyList<E> insertAt(int index, Iterable<E> elements) {
            handleNegativeIndex(index);
            return index == 0 ? concat(elements.iterator(), this) :
                    createTail(tail -> tail.insertAt(index - 1, elements));
        }

        @Override
        public LazyList<E> removeFirst(E element) {
            return value.value().equals(element) ? tail.value() :
                    createTail(tail -> tail.removeFirst(element));
        }

        @Override
        public LazyList<E> removeAt(int index) {
            handleNegativeIndex(index);
            return index == 0 ? tail.value() : createTail(tail -> tail.removeAt(index - 1));
        }


        // List operations ==============================================================================
        @Override
        public E reduce(BinaryOperator<E> operation) {
            return reduceHelper(first(), tail.value(), operation);
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
                    createTail(tail -> tail.where(predicate)) : tail.value().where(predicate);
        }

        @Override
        <R> LazyList<R> concatenateNestedIterables() {
            return concat(((Iterable<R>) value.value()).iterator(), tail.value().concatenateNestedIterables());
        }

        /*@Override
        <R> LazyList<R> concatenateNestedIterables() {
            Iterator<E> iterator = iterator();
            if (iterator.hasNext()) {
                Iterable<R> iterable = (Iterable<R>) iterator.next();
                Iterator<R> innerIterator = iterable.iterator();
                if (iterator.hasNext()) {
                    R element = innerIterator.next();
                    return LazyList.create(
                            Lazy.of(() -> element),
                            Lazy.of(() -> tail.value().concatenateNestedIterables())
                    );
                }
            }
        }*/
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

        private static IndexOutOfBoundsException indexTooBigException(int index) {
            return new IndexOutOfBoundsException("Index " + index + "too big for this list");
        }


        // Getters ======================================================================================
        @Override
        public E get(int index) {
            throw indexTooBigException(index);
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
        public LazyList<Integer> indices() {
            throw new UnsupportedOperationException("Empty list has no indices");
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
        public boolean any() {
            return false;
        }


        // Modifiers ====================================================================================
        @Override
        public LazyList<E> insertAt(int index, Iterable<E> elements) {
            throw indexTooBigException(index);
        }

        @Override
        public LazyList<E> removeFirst(E element) {
            return this;
        }

        @Override
        public LazyList<E> removeAt(int index) {
            throw indexTooBigException(index);
        }


        // List operations ==============================================================================
        @Override
        public E reduce(BinaryOperator<E> operation) {
            throw new UnsupportedOperationException("Empty list cannot be reduced");
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
        <R> LazyList<R> concatenateNestedIterables() {
            return LazyList.empty();
        }
    }
}