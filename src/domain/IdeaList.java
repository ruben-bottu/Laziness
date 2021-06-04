package domain;

import com.sun.istack.internal.Nullable;
import domain.function.TriFunction;
import domain.primitive_specializations.LazyInt;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

import static domain.Enumerable.isContentEqual;
import static java.util.Arrays.asList;
import static java.util.function.Predicate.isEqual;

public abstract class IdeaList<E> implements Iterable<E> {
    public final Lazy<E> value;
    public final Lazy<IdeaList<E>> tail;

    // Constructors and factory methods =============================================================
    private IdeaList(Lazy<E> value, Lazy<IdeaList<E>> tail) {
        this.value = value;
        this.tail = tail;
    }

    static <E> IdeaList<E> create(Lazy<E> value, Lazy<IdeaList<E>> tail) {
        return new NormalNode<>(value, tail);
    }

    @SuppressWarnings("unchecked")
    public static <E> IdeaList<E> empty() {
        return (IdeaList<E>) EndNode.EMPTY;
    }

    private static <L, E> IdeaList<E> concat(Iterator<E> iterator, L elements, Function<L, IdeaList<E>> toIdeaList) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return IdeaList.create(Lazy.of(() -> element), Lazy.of(() -> concat(iterator, elements, toIdeaList)));
        }
        return toIdeaList.apply(elements);
    }

    private static <E> IdeaList<E> concat(Iterable<E> iterable, Lazy<IdeaList<E>> elements) {
        return concat(iterable.iterator(), elements, Lazy::value);
    }

    private static <E> IdeaList<E> concat(Iterable<E> iterable, IdeaList<E> elements) {
        return concat(iterable.iterator(), elements, Function.identity());
    }

    public static <E> IdeaList<E> of(Iterable<E> elements) {
        return concat(elements, IdeaList.empty());
    }

    @SafeVarargs
    public static <E> IdeaList<E> of(E... elements) {
        return IdeaList.of(asList(elements));
    }

    private static IdeaList<Integer> rangeLength(int from, int length) {
        return length == 0 ? IdeaList.empty() : IdeaList.create(Lazy.of(() -> from), Lazy.of(() -> rangeLength(from + 1, length - 1)));
    }

    public static <E> IdeaList<E> initialiseWith(int length, Function<Integer, E> indexToElement) {
        handleNegativeLength(length);
        return rangeLength(0, length).map(indexToElement);
    }

    /*public static <E> IdeaList<E> initialiseWith2(int length, IntFunction<E> indexToElement) {
        return Range.from(0).length(length).map(indexToElement);
    }*/

    public static <E> IdeaList<E> initWith(int length, Function<Integer, E> indexToElement) {
        return initialiseWith(length, indexToElement);
    }

    private static IdeaList<Long> rangeLength(long from, long length) {
        return length == 0L ? IdeaList.empty() : IdeaList.create(Lazy.of(() -> from), Lazy.of(() -> rangeLength(from + 1L, length - 1L)));
    }

    public static <E> IdeaList<E> initialiseWithLong(long length, Function<Long, E> indexToElement) {
        handleNegativeLength(length);
        return rangeLength(0L, length).map(indexToElement);
    }

    private static void handleNegativeLength(long length) {
        if (length < 0L) throw new IllegalArgumentException("Cannot initialise list with length " + length);
    }

    private static IndexOutOfBoundsException indexTooBigException() {
        return new IndexOutOfBoundsException("Index too big");
    }

    int toPositiveIndex(int negativeIndex) {
        int length = length();
        if (-negativeIndex > length) throw indexTooBigException();
        return length + negativeIndex;
    }


    // Getters ======================================================================================
    public E get(int index) {
        if (index < 0) return get(toPositiveIndex(index));
        return withIndex().findFirst(pair -> pair.index == index)
                .orElseThrow(IdeaList::indexTooBigException)
                .element;
    }

    public abstract E first();

    public abstract E single();

    public abstract E last();

    public E random() {
        return get(new Random().nextInt(length()));
    }

    public abstract Optional<E> findFirst(Predicate<E> predicate);

    public Optional<E> first(Predicate<E> predicate) {
        return findFirst(predicate);
    }

    public OptionalInt indexOfFirst(Predicate<E> predicate) {
        return withIndex().findFirst(pair -> predicate.test(pair.element))
                .map(pair -> OptionalInt.of(pair.index))
                .orElseGet(OptionalInt::empty);
    }

    public OptionalInt indexOfFirst(@Nullable E element) {
        return indexOfFirst(isEqual(element));
    }

    public int lastIndex() {
        return length() - 1;
    }

    // Should return IntIdeaList
    public abstract IdeaList<Integer> indices();

    public int length() {
        return count(elem -> true);
    }

    public List<E> toList() {
        return Enumerable.toList(this);
    }

    public <R> R[] toArray(IntFunction<R[]> generator) {
        return generator.apply(length());
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
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdeaList<?> ideaList = (IdeaList<?>) o;
        return isContentEqual(this, ideaList);
    }

    public boolean contains(@Nullable E element) {
        return indexOfFirst(element).isPresent();
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
        return insertAt(index, asList(elements));
    }

    public IdeaList<E> concatWith(Iterable<E> elements) {
        return concat(this, Lazy.of(() -> IdeaList.of(elements)));
    }

    @SafeVarargs
    public final IdeaList<E> add(E... elements) {
        return concatWith(asList(elements));
    }

    @SafeVarargs
    public final IdeaList<E> append(E... elements) {
        return add(elements);
    }

    public IdeaList<E> linkToBackOf(Iterable<E> elements) {
        return concat(elements, this);
    }

    @SafeVarargs
    public final IdeaList<E> addToFront(E... elements) {
        return linkToBackOf(asList(elements));
    }

    @SafeVarargs
    public final IdeaList<E> prepend(E... elements) {
        return addToFront(elements);
    }

    public abstract IdeaList<E> removeFirst(@Nullable E element);

    public abstract IdeaList<E> removeAt(int index);

    public IdeaList<E> removeAll(@Nullable E element) {
        return where(current -> !Objects.equals(current, element));
    }

    public void forEachIndexed(BiConsumer<Integer, E> action) {
        withIndex().forEach(currentPair -> action.accept(currentPair.index, currentPair.element));
    }

    public void forEachIndexed2(BiConsumer<Integer, E> action) {
        AtomicInteger count = new AtomicInteger();
        forEach(elem -> action.accept(count.getAndIncrement(), elem));
    }


    // List operations ==============================================================================
    public <A> A reduce(A initialValue, BiFunction<A, E, A> operation) {
        return Enumerable.reduce(this, initialValue, operation);
    }

    public <A> A reduceIndexed(A initialValue, TriFunction<Integer, A, E, A> operation) {
        return Enumerable.reduceIndexed(this, initialValue, operation);
    }

    public <A> A reduce(BiFunction<A, E, A> operation, Function<E, A> transformFirst) {
        return Enumerable.reduce(this, operation, transformFirst);
    }

    public <A> A reduceIndexed(TriFunction<Integer, A, E, A> operation, Function<E, A> transformFirst) {
        return Enumerable.reduceIndexed(this, operation, transformFirst);
    }

    public E reduce(BinaryOperator<E> operation) {
        return Enumerable.reduce(this, operation);
    }

    public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
        return Enumerable.reduceIndexed(this, operation);
    }

    /*public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
        return withIndex().reduce((accumulator, currentPair) -> IndexElement.of(0, operation.apply(currentPair.index, accumulator.element, currentPair.element))).element;
    }*/

    /*public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
        return withIndex().reduce((accum, idxElem) -> IndexElement.of(0, operation.apply(idxElem.index, accum.element, idxElem.element))).element;
    }*/

    public <A> A reduceRight(A initialValue, BiFunction<E, A, A> operation) {
        return Enumerable.reduceRight(this, initialValue, operation);
    }

    public <A> A reduceRightIndexed(A initialValue, TriFunction<Integer, E, A, A> operation) {
        return Enumerable.reduceRightIndexed(this, initialValue, operation);
    }

    public <A> A reduceRight(BiFunction<E, A, A> operation, Function<E, A> transformLast) {
        return Enumerable.reduceRight(this, operation, transformLast);
    }

    public <A> A reduceRightIndexed(TriFunction<Integer, E, A, A> operation, BiFunction<Integer, E, A> transformLast) {
        return Enumerable.reduceRightIndexed(this, operation, transformLast);
    }

    public E reduceRight(BinaryOperator<E> operation) {
        return Enumerable.reduceRight(this, operation);
    }

    public E reduceRightIndexed(TriFunction<Integer, E, E, E> operation) {
        return Enumerable.reduceRightIndexed(this, operation);
    }

    abstract <A> A lazyReduceRight(A initialValue, BiFunction<Lazy<E>, Lazy<A>, A> operation);

    public <R> IdeaList<R> map(Function<E, R> transform) {
        BiFunction<Lazy<E>, Lazy<IdeaList<R>>, IdeaList<R>> transformAndPrependElem = (elem, list) -> IdeaList.create(Lazy.of(() -> transform.apply(elem.value())), list);
        return lazyReduceRight(IdeaList.empty(), transformAndPrependElem);
    }

    public <R> IdeaList<R> mapIndexed(BiFunction<Integer, E, R> transform) {
        return withIndex().map(currentPair -> transform.apply(currentPair.index, currentPair.element));
    }

    public <R> IdeaList<R> mapIndexed2(BiFunction<Integer, E, R> transform) {
        AtomicInteger index = new AtomicInteger();
        return map(elem -> transform.apply(index.getAndIncrement(), elem));
    }

    public IntIdeaList mapToInt(ToIntFunction<E> transform) {
        BiFunction<Lazy<E>, Lazy<IntIdeaList>, IntIdeaList> transformAndPrependElem = (elem, list) -> IntIdeaList.create(LazyInt.of(() -> transform.applyAsInt(elem.value())), list);
        return lazyReduceRight(IntIdeaList.empty(), transformAndPrependElem);
    }

    public <R> IdeaList<R> select(Function<E, R> transform) {
        return map(transform);
    }

    public <R> IdeaList<R> selectIndexed(BiFunction<Integer, E, R> transform) {
        return mapIndexed(transform);
    }

    public IdeaList<E> where(Predicate<E> predicate) {
        BiFunction<Lazy<E>, Lazy<IdeaList<E>>, IdeaList<E>> prependElemIfMatch = (elem, list) -> predicate.test(elem.value()) ? IdeaList.create(elem, list) : list.value();
        return lazyReduceRight(IdeaList.empty(), prependElemIfMatch);
    }

    public IdeaList<E> whereIndexed(BiPredicate<Integer, E> predicate) {
        return withIndex().where(currentPair -> predicate.test(currentPair.index, currentPair.element))
                .select(currentPair -> currentPair.element);
    }

    public IdeaList<E> whereIndexed2(BiPredicate<Integer, E> predicate) {
        AtomicInteger index = new AtomicInteger();
        return where(elem -> predicate.test(index.getAndIncrement(), elem));
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

    @SuppressWarnings("unchecked") // Cast is safe because isNested() first checks if list does in fact contain nested iterables
    private <N> IdeaList<N> concatNestedIterables() {
        return lazyReduceRight(IdeaList.empty(), (elem, acc) -> concat((Iterable<N>) elem, acc));
    }

    public <N> IdeaList<N> flatten() {
        if (isNested()) return concatNestedIterables();
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }

    public <A, B> IdeaList<Triplet<E, A, B>> zipWith(Iterable<A> other, Iterable<B> other2) {
        return Enumerable.zip(this, other, other2);
    }

    public <A> IdeaList<Pair<E, A>> zipWith(Iterable<A> other) {
        return Enumerable.zip(this, other);
    }

    public IdeaList<IndexElement<E>> withIndex() {
        return Enumerable.withIndex(this);
    }

    public IdeaList<E> step(int length) {
        if (length < 1) throw new IllegalArgumentException("Length cannot be smaller than 1. Given: " + length);
        return whereIndexed((index, current) -> index % length == 0);
    }

    /*public int sumOf(Function<E, Integer> toInt) {
        return map(toInt).reduce(Integer::sum);
    }

    public double sumOfDouble(Function<E, Double> toDouble) {
        return map(toDouble).reduce(Double::sum);
    }

    public int count(Predicate<E> predicate) {
        return sumOf(elem -> predicate.test(elem) ? 1 : 0);
    }*/

    public int sumOf(ToIntFunction<E> toInt) {
        return mapToInt(toInt).sum();
    }

    public double sumOfDouble(Function<E, Double> toDouble) {
        return map(toDouble).reduce(Double::sum);
    }

    public int count(Predicate<E> predicate) {
        return sumOf(elem -> predicate.test(elem) ? 1 : 0);
    }

    public <R extends Comparable<R>> E maxBy(Function<E, R> selector) {
        return map(selector).zipWith(this).reduce((accum, compElem) -> accum.first.compareTo(compElem.first) > 0 ? accum : compElem).second;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class NormalNode<E> extends IdeaList<E> {

        // Constructors and factory methods =============================================================
        private NormalNode(Lazy<E> value, Lazy<IdeaList<E>> tail) {
            super(value, tail);
        }

        private IdeaList<E> retainValueAndTransformTail(UnaryOperator<IdeaList<E>> function) {
            return IdeaList.create(value, Lazy.of(() -> function.apply(tail.value())));
        }

        // Getters ======================================================================================
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
        public Optional<E> findFirst(Predicate<E> predicate) {
            return predicate.test(value.value()) ? Optional.of(value.value()) : tail.value().findFirst(predicate);
        }

        @Override
        public IdeaList<Integer> indices() {
            return Range.from(0).upToAndIncluding(lastIndex());
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
            if (index < 0) return insertAt(toPositiveIndex(index), elements);
            return index == 0
                    ? concat(elements, this)
                    : retainValueAndTransformTail(tail -> tail.insertAt(index - 1, elements));
        }

        @Override
        public IdeaList<E> removeFirst(@Nullable E element) {
            return Objects.equals(value.value(), element) ? tail.value() : retainValueAndTransformTail(tail -> tail.removeFirst(element));
        }

        @Override
        public IdeaList<E> removeAt(int index) {
            if (index < 0) return removeAt(toPositiveIndex(index));
            return index == 0 ? tail.value() : retainValueAndTransformTail(tail -> tail.removeAt(index - 1));
        }


        // List operations ==============================================================================
        @Override
        <A> A lazyReduceRight(A initialValue, BiFunction<Lazy<E>, Lazy<A>, A> operation) {
            return operation.apply(value, Lazy.of(() -> tail.value().lazyReduceRight(initialValue, operation)));
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class EndNode<E> extends IdeaList<E> {
        private static final IdeaList<?> EMPTY = new EndNode<>();

        // Constructors and factory methods =============================================================
        private EndNode() {
            super(null, null);
        }

        private static NoSuchElementException noSuchElementException() {
            return new NoSuchElementException("List is empty");
        }


        // Getters ======================================================================================
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
        public Optional<E> findFirst(Predicate<E> predicate) {
            return Optional.empty();
        }

        @Override
        public IdeaList<Integer> indices() {
            throw new UnsupportedOperationException("Empty list has no indices");
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
            throw indexTooBigException();
        }

        @Override
        public IdeaList<E> removeFirst(@Nullable E element) {
            return this;
        }

        @Override
        public IdeaList<E> removeAt(int index) {
            throw indexTooBigException();
        }


        // List operations ==============================================================================
        @Override
        <A> A lazyReduceRight(A initialValue, BiFunction<Lazy<E>, Lazy<A>, A> operation) {
            return initialValue;
        }
    }
}
