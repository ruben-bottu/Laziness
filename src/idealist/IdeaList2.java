package idealist;

import idealist.function.TriFunction;
import idealist.primitive_specializations.LazyInt;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

import static idealist.Enumerable.isContentEqual;
import static java.util.Arrays.asList;
import static java.util.function.Predicate.isEqual;

public abstract class IdeaList2<E> implements Iterable<E> {
    public final Lazy<E> value;
    public final Lazy<IdeaList2<E>> tail;

    // Constructors and factory methods =============================================================
    private IdeaList2(Lazy<E> value, Lazy<IdeaList2<E>> tail) {
        this.value = value;
        this.tail = tail;
    }

    static <E> IdeaList2<E> create(Lazy<E> value, Lazy<IdeaList2<E>> tail) {
        return new NormalNode<>(value, tail);
    }

    @SuppressWarnings("unchecked")
    public static <E> IdeaList2<E> empty() {
        return (IdeaList2<E>) EndNode.EMPTY;
    }

    private static <E, O> IdeaList2<E> concat(IdeaList2<E> elements, O other, Function<O, IdeaList2<E>> toIdeaList) {
        return elements.lazyReduceRight(toIdeaList.apply(other), IdeaList2::create);
    }

    private static <E> IdeaList2<E> concat(IdeaList2<E> elements, Lazy<IdeaList2<E>> other) {
        return concat(elements, other, Lazy::value);
    }

    private static <E> IdeaList2<E> concat(IdeaList2<E> elements, IdeaList2<E> other) {
        return concat(elements, other, Function.identity());
    }

    private static <E> IdeaList2<E> constructIdeaListFromIterator(Iterator<E> iterator) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return IdeaList2.create(Lazy.of(() -> element), Lazy.of(() -> constructIdeaListFromIterator(iterator)));
        }
        return IdeaList2.empty();
    }

    public static <E> IdeaList2<E> of(Iterable<E> elements) {
        return constructIdeaListFromIterator(elements.iterator());
    }

    @SafeVarargs
    public static <E> IdeaList2<E> of(E... elements) {
        return IdeaList2.of(asList(elements));
    }

    @SafeVarargs
    public static <E> IdeaList2<E> ofDirect(E... elements) {
        return constructIdeaListFromIterator(Enumerator.of(elements));
    }

    private static IdeaList2<Integer> rangeLength(int from, int length) {
        return length == 0 ? IdeaList2.empty() : IdeaList2.create(Lazy.of(() -> from), Lazy.of(() -> rangeLength(from + 1, length - 1)));
    }

    public static <E> IdeaList2<E> initialiseWith(int length, Function<Integer, E> indexToElement) {
        handleNegativeLength(length);
        return rangeLength(0, length).map(indexToElement);
    }

    public static <E> IdeaList2<E> initWith(int length, Function<Integer, E> indexToElement) {
        return initialiseWith(length, indexToElement);
    }

    private static IdeaList2<Long> rangeLength(long from, long length) {
        return length == 0L ? IdeaList2.empty() : IdeaList2.create(Lazy.of(() -> from), Lazy.of(() -> rangeLength(from + 1L, length - 1L)));
    }

    public static <E> IdeaList2<E> initialiseWithLong(long length, Function<Long, E> indexToElement) {
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
    public abstract E get(int index);

    public abstract E first();

    public abstract E single();

    public abstract E last();

    public E random() {
        return get(new Random().nextInt(length()));
    }

    public abstract Optional<E> findFirst(Predicate<E> predicate);

    public Optional<E> findFirstIndexed(BiPredicate<Integer, E> predicate) {
        var index = new AtomicInteger(); // TODO change
        return findFirst(elem -> predicate.test(index.getAndIncrement(), elem));
    }

    public Optional<E> first(Predicate<E> predicate) {
        return findFirst(predicate);
    }

    abstract OptionalInt indexOfFirstHelper(Predicate<E> predicate, int index);

    public OptionalInt indexOfFirst(Predicate<E> predicate) {
        return indexOfFirstHelper(predicate, 0);
    }

    public OptionalInt indexOfFirst(@Nullable E element) {
        return indexOfFirst(isEqual(element));
    }

    public int lastIndex() {
        return length() - 1;
    }

    // Should return IntIdeaList
    //public abstract IdeaList2<Integer> indices();

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

    /*@Override
    public Iterator<E> iterator() {
        return Enumerator.of(this);
    }*/

    // TODO change body
    @Override
    public Iterator<E> iterator() {
        return null;
    }


    // Checks =======================================================================================
    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdeaList2<?> ideaList2 = (IdeaList2<?>) o;
        return isContentEqual(this, ideaList2);
    }

    public boolean contains(@Nullable E element) {
        return indexOfFirst(element).isPresent();
    }

    public boolean containsAll(IdeaList2<E> elements) {
        return elements.all(this::contains);
    }

    public boolean isEmpty() {
        return none();
    }

    public abstract boolean isNestedIdeaLists();

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
    public abstract IdeaList2<E> insertAt(int index, IdeaList2<E> elements);

    @SafeVarargs
    public final IdeaList2<E> insertAt(int index, E... elements) {
        return insertAt(index, IdeaList2.of(elements));
    }

    public IdeaList2<E> concatWith(IdeaList2<E> elements) {
        return concat(this, elements);
    }

    @SafeVarargs
    public final IdeaList2<E> add(E... elements) {
        return concatWith(IdeaList2.of(elements));
    }

    @SafeVarargs
    public final IdeaList2<E> append(E... elements) {
        return add(elements);
    }

    public IdeaList2<E> linkToBackOf(IdeaList2<E> elements) {
        return concat(elements, this);
    }

    @SafeVarargs
    public final IdeaList2<E> addToFront(E... elements) {
        return linkToBackOf(IdeaList2.of(elements));
    }

    @SafeVarargs
    public final IdeaList2<E> prepend(E... elements) {
        return addToFront(elements);
    }

    public abstract IdeaList2<E> removeFirst(@Nullable E element);

    public abstract IdeaList2<E> removeAt(int index);

    public IdeaList2<E> removeAll(@Nullable E element) {
        return where(current -> !Objects.equals(current, element));
    }

    public void forEachIndexed(BiConsumer<Integer, E> action) {
        var count = new AtomicInteger(); // TODO changed
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

    public <A> A reduceRight(A initialValue, BiFunction<E, A, A> operation) {
        return Enumerable.reduceRight(this, initialValue, operation);
    }

    public <A> A reduceRightIndexed(A initialValue, TriFunction<Integer, E, A, A> operation) {
        return Enumerable.reduceRightIndexed(this, initialValue, operation);
    }

    public <A> A reduceRight(BiFunction<E, A, A> operation, Function<E, A> transformLast) {
        return Enumerable.reduceRight(this, operation, transformLast);
    }

    public <A> A reduceRightIndexed(TriFunction<Integer, E, A, A> operation, Function<E, A> transformLast) {
        return Enumerable.reduceRightIndexed(this, operation, transformLast);
    }

    public E reduceRight(BinaryOperator<E> operation) {
        return Enumerable.reduceRight(this, operation);
    }

    public E reduceRightIndexed(TriFunction<Integer, E, E, E> operation) {
        return Enumerable.reduceRightIndexed(this, operation);
    }

    abstract <A> A lazyReduceRight(A initialValue, BiFunction<Lazy<E>, Lazy<A>, A> operation);

    public <R> IdeaList2<R> map(Function<E, R> transform) {
        BiFunction<Lazy<E>, Lazy<IdeaList2<R>>, IdeaList2<R>> transformAndPrependElem = (elem, list) -> IdeaList2.create(Lazy.of(() -> transform.apply(elem.value())), list);
        return lazyReduceRight(IdeaList2.empty(), transformAndPrependElem);
    }

    public <R> IdeaList2<R> mapIndexed(BiFunction<Integer, E, R> transform) {
        var index = new AtomicInteger(); // TODO changed
        return map(elem -> transform.apply(index.getAndIncrement(), elem));
    }

    public IntIdeaList mapToInt(ToIntFunction<E> transform) {
        BiFunction<Lazy<E>, Lazy<IntIdeaList>, IntIdeaList> transformAndPrependElem = (elem, list) -> IntIdeaList.create(LazyInt.of(() -> transform.applyAsInt(elem.value())), list);
        return lazyReduceRight(IntIdeaList.empty(), transformAndPrependElem);
    }

    public <R> IdeaList2<R> select(Function<E, R> transform) {
        return map(transform);
    }

    public <R> IdeaList2<R> selectIndexed(BiFunction<Integer, E, R> transform) {
        return mapIndexed(transform);
    }

    public IdeaList2<E> where(Predicate<E> predicate) {
        BiFunction<Lazy<E>, Lazy<IdeaList2<E>>, IdeaList2<E>> prependElemIfMatch = (elem, list) -> predicate.test(elem.value()) ? IdeaList2.create(elem, list) : list.value();
        return lazyReduceRight(IdeaList2.empty(), prependElemIfMatch);
    }

    public IdeaList2<E> whereIndexed(BiPredicate<Integer, E> predicate) {
        var index = new AtomicInteger(); // TODO changed
        return where(elem -> predicate.test(index.getAndIncrement(), elem));
    }

    public IdeaList2<E> filter(Predicate<E> predicate) {
        return where(predicate);
    }

    public IdeaList2<E> filterIndexed(BiPredicate<Integer, E> predicate) {
        return whereIndexed(predicate);
    }

    public IdeaList2<E> findAll(Predicate<E> predicate) {
        return where(predicate);
    }

    public IdeaList2<E> findAllIndexed(BiPredicate<Integer, E> predicate) {
        return whereIndexed(predicate);
    }

    /*@SuppressWarnings("unchecked") // Cast is safe because isNested() first checks if list does in fact contain nested iterables
    private <N> IdeaList2<N> concatNestedIterables() {
        return lazyReduceRight(IdeaList2.empty(), (elem, acc) -> concat((Iterable<N>) elem, acc));
    }

    public <N> IdeaList2<N> flatten() {
        if (isNested()) return concatNestedIterables();
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }*/

    private static <N> IdeaList2<N> concatNestedIdeaLists(IdeaList2<IdeaList2<N>> elements) {
        return elements.lazyReduceRight(IdeaList2.empty(), (elem, accum) -> concat(elem.value(), accum));
    }

    /*private static <N> IdeaList2<N> concatNestedIterables(IdeaList2<Iterable<N>> elements) {
        return elements.lazyReduceRight(IdeaList2.empty(), (elem, accum) -> concat(elem, accum));
    }*/

    /*private static <N> IdeaList2<N> flatten(IdeaList2<Iterable<N>> elements) {
        return elements.lazyReduceRight(IdeaList2.empty(), (elem, accum) -> concat(elem.value(), accum));
    }*/

    @SuppressWarnings("unchecked") // Cast is safe because isNested() first checks if list does in fact contain nested iterables
    private <N> IdeaList2<N> concatNestedIdeaLists() {
        return lazyReduceRight(IdeaList2.empty(), (elem, list) -> concat((IdeaList2<N>) elem.value(), list));
    }

    /*public <N> IdeaList2<N> flatten() {
        if (isNestedIdeaLists()) return concatNestedIdeaLists();
        // if (isNested()) return concatNestedIterables();
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }*/

    public <N> IdeaList2<N> flatten() {
        if (isNestedIdeaLists()) return concatNestedIdeaLists((IdeaList2<IdeaList2<N>>) this);
        // if (isNested()) return concatNestedIterables();
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }

    /*public <A, B> IdeaList2<Triplet<E, A, B>> zipWith(Iterable<A> other, Iterable<B> other2) {
        return Enumerable.zip(this, other, other2);
    }

    public <A> IdeaList2<Pair<E, A>> zipWith(Iterable<A> other) {
        return Enumerable.zip(this, other);
    }

    public IdeaList2<IndexElement<E>> withIndex() {
        return Enumerable.withIndex(this);
    }*/

    public IdeaList2<E> step(int length) {
        if (length < 1) throw new IllegalArgumentException("Length cannot be smaller than 1. Given: " + length);
        return whereIndexed((index, current) -> index % length == 0);
    }

    public int sumOf(ToIntFunction<E> selector) {
        return mapToInt(selector).sum();
    }

    public double sumOfDouble(Function<E, Double> selector) {
        return map(selector).reduce(Double::sum);
    }

    public int count(Predicate<E> predicate) {
        return sumOf(elem -> predicate.test(elem) ? 1 : 0);
    }

    /*public <R extends Comparable<R>> E maxBy(Function<E, R> selector) {
        return map(selector).zipWith(this).reduce((accum, compElem) -> accum.first.compareTo(compElem.first) > 0 ? accum : compElem).second;
    }*/

    /*public <R extends Comparable<R>> E maxBy(Function<E, R> selector) {
        return map(selector).zipWith(this).reduce((accum, compElem) -> accum.first.compareTo(compElem.first) > 0 ? accum : compElem).second;
    }*/


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class NormalNode<E> extends IdeaList2<E> {

        // Constructors and factory methods =============================================================
        private NormalNode(Lazy<E> value, Lazy<IdeaList2<E>> tail) {
            super(value, tail);
        }

        private IdeaList2<E> retainValueAndTransformTail(UnaryOperator<IdeaList2<E>> function) {
            return IdeaList2.create(value, Lazy.of(() -> function.apply(tail.value())));
        }

        // Getters ======================================================================================
        @Override
        public E get(int index) {
            if (index < 0) return get(toPositiveIndex(index));
            return index == 0 ? value.value() : tail.value().get(index - 1);
        }

        public E get2(int index) {
            return index < 0 ? get2(toPositiveIndex(index)) : (index == 0 ? value.value() : tail.value().get(index - 1));
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
        public Optional<E> findFirst(Predicate<E> predicate) {
            return predicate.test(value.value()) ? Optional.of(value.value()) : tail.value().findFirst(predicate);
        }

        @Override
        OptionalInt indexOfFirstHelper(Predicate<E> predicate, int index) {
            return predicate.test(value.value()) ? OptionalInt.of(index) : indexOfFirstHelper(predicate, index + 1);
        }

        /*@Override
        public IdeaList2<Integer> indices() {
            return Range.from(0).upToAndIncluding(lastIndex());
        }*/


        // Checks =======================================================================================
        @Override
        public boolean isNestedIdeaLists() {
            return value.value() instanceof IdeaList2;
        }

        @Override
        public boolean any() {
            return true;
        }


        // Modifiers ====================================================================================
        @Override
        public IdeaList2<E> insertAt(int index, IdeaList2<E> elements) {
            if (index < 0) return insertAt(toPositiveIndex(index), elements);
            return index == 0
                    ? concat(elements, this)
                    : retainValueAndTransformTail(tail -> tail.insertAt(index - 1, elements));
        }

        @Override
        public IdeaList2<E> removeFirst(@Nullable E element) {
            return Objects.equals(value.value(), element) ? tail.value() : retainValueAndTransformTail(tail -> tail.removeFirst(element));
        }

        @Override
        public IdeaList2<E> removeAt(int index) {
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
    private static class EndNode<E> extends IdeaList2<E> {
        private static final IdeaList2<?> EMPTY = new IdeaList2.EndNode<>();

        // Constructors and factory methods =============================================================
        private EndNode() {
            super(null, null);
        }

        private static NoSuchElementException noSuchElementException() {
            return new NoSuchElementException("List is empty");
        }


        // Getters ======================================================================================
        @Override
        public E get(int index) {
            throw indexTooBigException();
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
        public Optional<E> findFirst(Predicate<E> predicate) {
            return Optional.empty();
        }

        @Override
        OptionalInt indexOfFirstHelper(Predicate<E> predicate, int index) {
            return OptionalInt.empty();
        }

        /*@Override
        public IdeaList2<Integer> indices() {
            throw new UnsupportedOperationException("Empty list has no indices");
        }*/


        // Checks =======================================================================================
        @Override
        public boolean isNestedIdeaLists() {
            return false;
        }

        @Override
        public boolean any() {
            return false;
        }


        // Modifiers ====================================================================================
        @Override
        public IdeaList2<E> insertAt(int index, IdeaList2<E> elements) {
            throw indexTooBigException();
        }

        @Override
        public IdeaList2<E> removeFirst(@Nullable E element) {
            return this;
        }

        @Override
        public IdeaList2<E> removeAt(int index) {
            throw indexTooBigException();
        }


        // List operations ==============================================================================
        @Override
        <A> A lazyReduceRight(A initialValue, BiFunction<Lazy<E>, Lazy<A>, A> operation) {
            return initialValue;
        }
    }
}
