package domain;

import java.util.*;
import java.util.function.*;

public abstract class IdeaList<E> implements Iterable<E> {
    public final Lazy<E> value;
    public final Lazy<IdeaList<E>> tail;

    // Constructors and factory methods =============================================================
    private IdeaList(Lazy<E> value, Lazy<IdeaList<E>> tail) {
        this.value = value;
        this.tail = tail;
    }

    private static <E> IdeaList<E> create(Lazy<E> value, Lazy<IdeaList<E>> tail) {
        return new NormalNode<>(value, tail);
    }

    public static <E> IdeaList<E> empty() {
        return new EndNode<>();
    }

    private static <E> IdeaList<E> concat(Iterator<E> iterator, IdeaList<E> elements) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return IdeaList.create(
                    Lazy.of(() -> element),
                    Lazy.of(() -> concat(iterator, elements))
            );
        }
        return elements;
    }

    public static <E> IdeaList<E> of(Iterable<E> elements) {
        return concat(elements.iterator(), IdeaList.empty());
    }

    @SafeVarargs
    public static <E> IdeaList<E> of(E... elements) {
        return IdeaList.of(Arrays.asList(elements));
    }

    private static IdeaList<Integer> rangeLength(int from, int length) {
        return length == 0 ? IdeaList.empty() : IdeaList.create(Lazy.of(() -> from), Lazy.of(() -> rangeLength(from + 1, length - 1)));
    }

    public static <E> IdeaList<E> initialiseWith(int length, Function<Integer, E> indexToElement) {
        if (length < 0) throw new IllegalArgumentException("Cannot initialise list with length " + length);
        return rangeLength(0, length).map(indexToElement);
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
        return get(new Random().nextInt(length()));
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
        return length() - 1;
    }

    public abstract IdeaList<Integer> indices();

    public abstract int length();

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
        return Enumerator.of(this);
    }


    // Checks =======================================================================================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdeaList<?> ideaList = (IdeaList<?>) o;
        return equals(ideaList);
    }

    private boolean equals(IdeaList<?> elements) {
        Iterator<E> it1 = iterator();
        Iterator<?> it2 = elements.iterator();
        while (true) {
            if (it1.hasNext() && !it2.hasNext() || !it1.hasNext() && it2.hasNext()) return false;
            if (!it1.hasNext()) return true;
            if (!Objects.equals(it1.next(), it2.next())) return false;
        }
    }

    public boolean contains(E element) {
        return indexOfFirst(element) != -1;
    }

    public boolean containsAll(Iterable<E> elements) {
        return IdeaList.of(elements).all(this::contains);
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
    public abstract IdeaList<E> insertAt(int index, Iterable<E> elements);

    @SafeVarargs
    public final IdeaList<E> insertAt(int index, E... elements) {
        return insertAt(index, Arrays.asList(elements));
    }

    public IdeaList<E> concatWith(Iterable<E> elements) {
        return concat(iterator(), IdeaList.of(elements));
    }

    @SafeVarargs
    public final IdeaList<E> add(E... elements) {
        return concatWith(Arrays.asList(elements));
    }

    public IdeaList<E> linkToBackOf(Iterable<E> elements) {
        return concat(elements.iterator(), this);
    }

    @SafeVarargs
    public final IdeaList<E> addToFront(E... elements) {
        return linkToBackOf(Arrays.asList(elements));
    }

    public abstract IdeaList<E> removeFirst(E element);

    public abstract IdeaList<E> removeAt(int index);

    public IdeaList<E> removeAll(E element) {
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
    private static <E, A> A reduceHelper(A initialValue, IdeaList<E> list, BiFunction<A, E, A> operation) {
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

    public abstract <R> IdeaList<R> map(Function<E, R> transform);

    public <R> IdeaList<R> mapIndexed(BiFunction<Integer, E, R> transform) {
        return withIndex().map(currentPair -> transform.apply(currentPair.index, currentPair.element));
    }

    public <R> IdeaList<R> select(Function<E, R> transform) {
        return map(transform);
    }

    public <R> IdeaList<R> selectIndexed(BiFunction<Integer, E, R> transform) {
        return mapIndexed(transform);
    }

    public abstract IdeaList<E> where(Predicate<E> predicate);

    public IdeaList<E> whereIndexed(BiPredicate<Integer, E> predicate) {
        return withIndex().where(currentPair -> predicate.test(currentPair.index, currentPair.element)).select(currentPair -> currentPair.element);
    }

    public IdeaList<E> filter(Predicate<E> predicate) {
        return where(predicate);
    }

    public IdeaList<E> filterIndexed(BiPredicate<Integer, E> predicate) {
        return whereIndexed(predicate);
    }

    public IdeaList<E> findAll(Predicate<E> predicate) {
        return where(predicate);
    }

    public IdeaList<E> findAllIndexed(BiPredicate<Integer, E> predicate) {
        return whereIndexed(predicate);
    }

    abstract <R> IdeaList<R> concatNestedIterables();

    public <R> IdeaList<R> flatten() {
        if (isNested()) return concatNestedIterables();
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }

    private static <A, B, C> boolean allHaveNext(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return iterators.first.hasNext() && iterators.second.hasNext() && iterators.third.hasNext();
    }

    private static <A, B, C> Triplet<A, B, C> next(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return Triplet.of(iterators.first.next(), iterators.second.next(), iterators.third.next());
    }

    private static <A, B, C> IdeaList<Triplet<A, B, C>> zipWithHelper(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        if (!allHaveNext(iterators)) return IdeaList.empty();
        Triplet<A, B, C> elements = next(iterators);
        return IdeaList.create(
                Lazy.of(() -> elements),
                Lazy.of(() -> zipWithHelper(iterators))
        );
    }

    public <A, B> IdeaList<Triplet<E, A, B>> zipWith(Iterable<A> other, Iterable<B> other2) {
        return zipWithHelper(Triplet.of(iterator(), other.iterator(), other2.iterator()));
    }

    public <A> IdeaList<Pair<E, A>> zipWith(Iterable<A> other) {
        return zipWith(other, Enumerable.infiniteNulls()).map(Triplet::toPair);
    }

    public IdeaList<IndexElement<E>> withIndex() {
        return Range.infiniteIndices().zipWith(this).map(Pair::toIndexElement);
    }

    public IdeaList<E> step(int length) {
        if (length < 1) throw new IllegalArgumentException("Length cannot be smaller than 1. Given: " + length);
        return whereIndexed((index, current) -> index % length == 0);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class NormalNode<E> extends IdeaList<E> {

        // Constructors and factory methods =============================================================
        private NormalNode(Lazy<E> value, Lazy<IdeaList<E>> tail) {
            super(value, tail);
        }

        private IdeaList<E> createTail(UnaryOperator<IdeaList<E>> function) {
            return IdeaList.create(value, Lazy.of(() -> function.apply(tail.value())));
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
        public IdeaList<Integer> indices() {
            return Range.from(0).upToAndIncluding(lastIndex());
        }

        @Override
        public int length() {
            return 1 + tail.value().length();
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
        public IdeaList<E> insertAt(int index, Iterable<E> elements) {
            handleNegativeIndex(index);
            return index == 0 ? concat(elements.iterator(), this) :
                    createTail(tail -> tail.insertAt(index - 1, elements));
        }

        @Override
        public IdeaList<E> removeFirst(E element) {
            return value.value().equals(element) ? tail.value() : createTail(tail -> tail.removeFirst(element));
        }

        @Override
        public IdeaList<E> removeAt(int index) {
            handleNegativeIndex(index);
            return index == 0 ? tail.value() : createTail(tail -> tail.removeAt(index - 1));
        }


        // List operations ==============================================================================
        @Override
        public E reduce(BinaryOperator<E> operation) {
            return reduceHelper(first(), tail.value(), operation);
        }

        @Override
        public <R> IdeaList<R> map(Function<E, R> transform) {
            return IdeaList.create(
                    Lazy.of(() -> transform.apply(value.value())),
                    Lazy.of(() -> tail.value().map(transform))
            );
        }

        @Override
        public IdeaList<E> where(Predicate<E> predicate) {
            return predicate.test(value.value()) ?
                    createTail(tail -> tail.where(predicate)) : tail.value().where(predicate);
        }

        @Override
        <R> IdeaList<R> concatNestedIterables() {
            return concat(((Iterable<R>) value.value()).iterator(), tail.value().concatNestedIterables());
        }

        /*@Override
        <R> IdeaList<R> concatenateNestedIterables() {
            Iterator<E> iterator = iterator();
            if (iterator.hasNext()) {
                Iterable<R> iterable = (Iterable<R>) iterator.next();
                Iterator<R> innerIterator = iterable.iterator();
                if (iterator.hasNext()) {
                    R element = innerIterator.next();
                    return IdeaList.create(
                            Lazy.of(() -> element),
                            Lazy.of(() -> tail.value().concatenateNestedIterables())
                    );
                }
            }
        }*/
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class EndNode<E> extends IdeaList<E> {

        // Constructors and factory methods =============================================================
        private EndNode() {
            super(null, null);
        }

        private static NoSuchElementException noSuchElementException() {
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
            throw noSuchElementException();
        }

        @Override
        public E single() {
            throw noSuchElementException();
        }

        @Override
        public E last() {
            throw noSuchElementException();
        }

        @Override
        public E findFirst(Predicate<E> predicate) {
            return null;
        }

        @Override
        public IdeaList<Integer> indices() {
            throw new UnsupportedOperationException("Empty list has no indices");
        }

        @Override
        public int length() {
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
        public IdeaList<E> insertAt(int index, Iterable<E> elements) {
            throw indexTooBigException(index);
        }

        @Override
        public IdeaList<E> removeFirst(E element) {
            return this;
        }

        @Override
        public IdeaList<E> removeAt(int index) {
            throw indexTooBigException(index);
        }


        // List operations ==============================================================================
        @Override
        public E reduce(BinaryOperator<E> operation) {
            throw new UnsupportedOperationException("Empty list cannot be reduced");
        }

        @Override
        public <R> IdeaList<R> map(Function<E, R> transform) {
            return IdeaList.empty();
        }

        @Override
        public IdeaList<E> where(Predicate<E> predicate) {
            return this;
        }

        @Override
        <R> IdeaList<R> concatNestedIterables() {
            return IdeaList.empty();
        }
    }
}