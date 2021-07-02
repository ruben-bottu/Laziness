package idealist;

import idealist.function.TriFunction;
import idealist.range.Range;
import idealist.tuple.IndexElement;
import idealist.tuple.Pair;
import idealist.tuple.Triplet;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

import static idealist.Enumerable.isContentEqual;
import static idealist.Lambda.alwaysTrue;
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

    private static <E, O> IdeaList<E> concat(IdeaList<E> elements, O other, Function<O, IdeaList<E>> toIdeaList) {
        return elements.lazyReduceRight(toIdeaList.apply(other), IdeaList::create);
    }

    private static <E> IdeaList<E> concat(IdeaList<E> elements, Lazy<IdeaList<E>> other) {
        return concat(elements, other, Lazy::value);
    }

    private static <E> IdeaList<E> concat(IdeaList<E> elements, IdeaList<E> other) {
        return concat(elements, other, Function.identity());
    }

    // TODO benchmark agains concat methods
    private static <E> IdeaList<E> constructIdeaListFromIterator(Iterator<E> iterator) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return IdeaList.create(Lazy.of(() -> element), Lazy.of(() -> constructIdeaListFromIterator(iterator)));
        }
        return IdeaList.empty();
    }

    private static <E, O> IdeaList<E> concat(Iterator<E> iterator, O other, Function<O, IdeaList<E>> toIdeaList) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return IdeaList.create(Lazy.of(() -> element), Lazy.of(() -> concat(iterator, other, toIdeaList)));
        }
        return toIdeaList.apply(other);
    }

    private static <E> IdeaList<E> concat(Iterable<E> elements, Lazy<IdeaList<E>> other) {
        return concat(elements.iterator(), other, Lazy::value);
    }

    private static <E> IdeaList<E> concat(Iterable<E> elements, IdeaList<E> other) {
        return concat(elements.iterator(), other, Function.identity());
    }

    static <E> IdeaList<E> of(MutableList<E> elements) {
        if (elements.isEmpty()) return IdeaList.empty();
        return IdeaList.create(elements.value, Lazy.of(() -> of(elements.tail)));
    }

    public static <E> IdeaList<E> of(Iterable<E> elements) {
        return constructIdeaListFromIterator(elements.iterator());
    }

    public static <E> IdeaList<E> of2(Iterable<E> elements) {
        return concat(elements, IdeaList.empty());
    }

    @SafeVarargs
    public static <E> IdeaList<E> of(E... elements) {
        return IdeaList.of(asList(elements));
    }

    /*public static IntIdeaList of(int... elements) { }*/

    /*public static CharIdeaList of(char... elements) { }*/

    /*public static LongIdeaList of(long... elements) { }*/

    /*public static FloatIdeaList of(float... elements) { }*/

    /*public static DoubleIdeaList of(double... elements) { }*/

    @SafeVarargs
    public static <E> IdeaList<E> ofDirect(E... elements) {
        return constructIdeaListFromIterator(Enumerator.of(elements));
    }

    private static IdeaList<Integer> rangeLength(int from, int length) {
        return length == 0 ? IdeaList.empty() : IdeaList.create(Lazy.of(() -> from), Lazy.of(() -> rangeLength(from + 1, length - 1)));
    }

    public static <E> IdeaList<E> initialiseWith(int length, Function<Integer, E> indexToElement) {
        handleNegativeLength(length);
        return rangeLength(0, length).map(indexToElement);
    }

    // TODO benchmark
    /*public static <E> IdeaList<E> initialiseWith2(int length, IntFunction<E> indexToElement) {
        handleNegativeLength(length);
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

    // TODO use int
    private static void handleNegativeLength(long length) {
        if (length < 0L) throw new IllegalArgumentException("Cannot initialise list with length " + length);
    }

    private static IndexOutOfBoundsException indexTooBigException() {
        return new IndexOutOfBoundsException("Index too big");
    }

    protected int toPositiveIndex(int negativeIndex) {
        int length = length();
        if (-negativeIndex > length) throw indexTooBigException();
        return length + negativeIndex;
    }


    // Getters ======================================================================================
    public abstract E get(int index);

    /*public E get(int index) {
        if (index < 0) return get(toPositiveIndex(index));
        return withIndex().findFirst(pair -> pair.index == index)
                .orElseThrow(IdeaList::indexTooBigException)
                .element;
    }

    public E get2(int index) {
        if (index < 0) return get(toPositiveIndex(index));
        return findFirstIndexed((idx, elem) -> idx == index)
                .orElseThrow(IdeaList::indexTooBigException);
    }*/

    public abstract E first();

    public abstract E single();

    public abstract E last();

    public E random() {
        return get(new Random().nextInt(length()));
    }

    public abstract Optional<E> findFirst(Predicate<E> predicate);

    public Optional<E> findFirstIndexed(BiPredicate<Integer, E> predicate) {
        var index = new AtomicInteger();
        return findFirst(elem -> predicate.test(index.getAndIncrement(), elem));
    }

    public Optional<E> first(Predicate<E> predicate) {
        return findFirst(predicate);
    }

    protected abstract OptionalInt indexOfFirstHelper(Predicate<E> predicate, int index);

    public OptionalInt indexOfFirst(Predicate<E> predicate) {
        return indexOfFirstHelper(predicate, 0);
    }

    public OptionalInt indexOfFirst2(Predicate<E> predicate) {
        return withIndex().findFirst(idxElem -> predicate.test(idxElem.element))
                .map(idxElem -> OptionalInt.of(idxElem.index))
                .orElseGet(OptionalInt::empty);
    }

    public OptionalInt indexOfFirst3(Predicate<E> predicate) {
        return Enumerable.indexOfFirst(predicate, this);
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
        return count(__ -> true);
    }

    public int length2() {
        return count(alwaysTrue());
    }

    public List<E> toList() {
        return Enumerable.toList(this);
    }

    @SuppressWarnings("unchecked") // Since we take the class of "value" and value is of type E, the cast is safe
    private E[] createArrayWithSameTypeAndSizeAsThisList() {
        return (E[]) Array.newInstance(value.value().getClass(), length());
    }

    /*public E[] toArray() {
        E[] result = createArrayWithSameTypeAndSizeAsThisList();
        int index = 0;
        for (E element : this) result[index++] = element;
        return result;
    }*/

    public E[] toArray() {
        E[] result = createArrayWithSameTypeAndSizeAsThisList();
        forEachIndexed((index, elem) -> result[index] = elem);
        return result;
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
        IdeaList<?> IdeaList = (IdeaList<?>) o;
        return isContentEqual(this, IdeaList);
    }

    public boolean contains(@Nullable E element) {
        return indexOfFirst(element).isPresent();
    }

    // Is this method redundant?
    public boolean containsAll(IdeaList<E> elements) {
        return elements.all(this::contains);
    }

    public boolean containsAll(Iterable<E> elements) {
        return IdeaList.of(elements).all(this::contains);
    }

    /*public boolean containsAll2(Iterable<E> elements) {
        for (E element : elements) if (!contains(element)) return false;
        return true;
    }*/

    public boolean containsAll2(Iterable<E> elements) {
        return Enumerable.all(this::contains, elements);
    }

    public boolean isEmpty() {
        return none();
    }

    public abstract boolean isNested();

    public abstract boolean containsNestedIdeaLists();

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
    public abstract IdeaList<E> insertAt(int index, IdeaList<E> elements);

    public IdeaList<E> insertAt(int index, Iterable<E> elements) {
        return insertAt(index, IdeaList.of(elements));
    }

    protected abstract <I> IdeaList<E> insertAtHelper(int index, I elements, BiFunction<I, IdeaList<E>, IdeaList<E>> concat);

    public IdeaList<E> insertAt2(int index, IdeaList<E> elements) {
        return insertAtHelper(index, elements, IdeaList::concat);
    }

    public IdeaList<E> insertAt2(int index, Iterable<E> elements) {
        return insertAtHelper(index, elements, IdeaList::concat);
    }

    @SafeVarargs
    public final IdeaList<E> insertAt(int index, E... elements) {
        return insertAt(index, IdeaList.of(elements));
    }

    @SafeVarargs
    public final IdeaList<E> insertAt2(int index, E... elements) {
        return insertAt(index, asList(elements));
    }

    public IdeaList<E> concatWith(IdeaList<E> elements) {
        return concat(this, elements);
    }

    public IdeaList<E> concatWith(Iterable<E> elements) {
        return concat(this, Lazy.of(() -> IdeaList.of(elements)));
    }

    @SafeVarargs
    public final IdeaList<E> add(E... elements) {
        return concatWith(IdeaList.of(elements));
    }

    @SafeVarargs
    public final IdeaList<E> add2(E... elements) {
        return concatWith(asList(elements));
    }

    @SafeVarargs
    public final IdeaList<E> append(E... elements) {
        return add(elements);
    }

    public IdeaList<E> linkToBackOf(IdeaList<E> elements) {
        return concat(elements, this);
    }

    public IdeaList<E> linkToBackOf(Iterable<E> elements) {
        return concat(IdeaList.of(elements), this);
    }

    public IdeaList<E> linkToBackOf2(Iterable<E> elements) {
        return concat(elements, this);
    }

    @SafeVarargs
    public final IdeaList<E> addToFront(E... elements) {
        return linkToBackOf(IdeaList.of(elements));
    }

    @SafeVarargs
    public final IdeaList<E> addToFront2(E... elements) {
        return linkToBackOf(asList(elements));
    }

    @SafeVarargs
    public final IdeaList<E> prepend(E... elements) {
        return addToFront(elements);
    }

    public abstract IdeaList<E> removeFirst(@Nullable E element);

    public abstract IdeaList<E> removeAt(int index);

    public IdeaList<E> removeAll(@Nullable E element) {
        return where(elem -> !Objects.equals(elem, element));
    }

    public void forEachIndexed(BiConsumer<Integer, E> action) {
        var index = new AtomicInteger();
        forEach(elem -> action.accept(index.getAndIncrement(), elem));
    }

    public void forEachIndexed2(BiConsumer<Integer, E> action) {
        int index = 0;
        for (E element : this) action.accept(index++, element);
    }


    // List operations ==============================================================================
    public <A> A reduce(A initialValue, BiFunction<A, E, A> operation) {
        return Enumerable.reduce(initialValue, operation, this);
    }

    public <A> A reduceIndexed(A initialValue, TriFunction<Integer, A, E, A> operation) {
        return Enumerable.reduceIndexed(initialValue, operation, this);
    }

    public <A> A reduce(BiFunction<A, E, A> operation, Function<E, A> transformFirst) {
        return Enumerable.reduce(operation, transformFirst, this);
    }

    public <A> A reduceIndexed(TriFunction<Integer, A, E, A> operation, Function<E, A> transformFirst) {
        return Enumerable.reduceIndexed(operation, transformFirst, this);
    }

    public E reduce(BinaryOperator<E> operation) {
        return Enumerable.reduce(operation, this);
    }

    // Example of what it was like without AtomicInteger
    /*public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
        return withIndex().reduce((accum, idxElem) -> IndexElement.of(0, operation.apply(idxElem.index, accum.element, idxElem.element))).element;
    }*/

    public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
        return Enumerable.reduceIndexed(operation, this);
    }

    public <A> A reduceRight(A initialValue, BiFunction<E, A, A> operation) {
        return Enumerable.reduceRight(initialValue, operation, this);
    }

    public <A> A reduceRightIndexed(A initialValue, TriFunction<Integer, E, A, A> operation) {
        return Enumerable.reduceRightIndexed(initialValue, operation, this);
    }

    public <A> A reduceRight(BiFunction<E, A, A> operation, Function<E, A> transformLast) {
        return Enumerable.reduceRight(operation, transformLast, this);
    }

    public <A> A reduceRightIndexed(TriFunction<Integer, E, A, A> operation, Function<E, A> transformLast) {
        return Enumerable.reduceRightIndexed(operation, transformLast, this);
    }

    public E reduceRight(BinaryOperator<E> operation) {
        return Enumerable.reduceRight(operation, this);
    }

    public E reduceRightIndexed(TriFunction<Integer, E, E, E> operation) {
        return Enumerable.reduceRightIndexed(operation, this);
    }
    // public <A> A reduce(A initialValue, BiFunction<A, E, A> operation) {
    // abstract <A> A lazyReduce(A initialValue, BiFunction<Lazy<A>, Lazy<E>, A> operation);

    // public <A> A reduceRight(A initialValue, BiFunction<E, A, A> operation) {
    protected abstract <A> A lazyReduceRight(A initialValue, BiFunction<Lazy<E>, Lazy<A>, A> operation);

    public <R> IdeaList<R> map(Function<E, R> transform) {
        BiFunction<Lazy<E>, Lazy<IdeaList<R>>, IdeaList<R>> transformAndPrependElem = (elem, list) -> IdeaList.create(Lazy.of(() -> transform.apply(elem.value())), list);
        return lazyReduceRight(IdeaList.empty(), transformAndPrependElem);
    }

    public <R> IdeaList<R> mapIndexed(BiFunction<Integer, E, R> transform) {
        var index = new AtomicInteger();
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
        var index = new AtomicInteger();
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

    /*@SuppressWarnings("unchecked") // Cast is safe because isNested() first checks if list does in fact contain nested iterables
    private <N> IdeaList<N> concatNestedIterables() {
        return lazyReduceRight(IdeaList.empty(), (elem, acc) -> concat((Iterable<N>) elem, acc));
    }

    public <N> IdeaList<N> flatten() {
        if (isNested()) return concatNestedIterables();
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }*/

    /*// concat(IdeaList<E> elements, IdeaList<E> other)
        // concat(Iterable<E> elements, IdeaList<E> other)
        @Override
        protected <I> IdeaList<E> insertAtHelper(int index, I elements, BiFunction<I, IdeaList<E>, IdeaList<E>> concat) {
            if (index < 0) return insertAtHelper(toPositiveIndex(index), elements, concat);
            return index == 0
                    ? concat.apply(elements, this)
                    : keepValueAndTransformTail(tail -> tail.insertAtHelper(index - 1, elements, concat));
        }*/

    //private static <N, I> IdeaList<N> concatNestedHelper(IdeaList<I> elements, BiFunction<I, Lazy<IdeaList<E>>, >)

    private static <N> IdeaList<N> concatNestedIdeaLists(IdeaList<IdeaList<N>> elements) {
        return elements.lazyReduceRight(IdeaList.empty(), (elem, accum) -> concat(elem.value(), accum));
    }

    private static <N> IdeaList<N> concatNestedIterables(IdeaList<Iterable<N>> elements) {
        return elements.lazyReduceRight(IdeaList.empty(), (elem, accum) -> concat(elem.value(), accum));
    }

    /*private static <N> IdeaList<N> concatNestedIdeaLists(IdeaList<IdeaList<N>> elements) {
        return elements.lazyReduceRight(IdeaList.empty(), (elem, accum) -> concat(elem.value(), accum));
    }

    private static <N> IdeaList<N> concatNestedIterables(IdeaList<Iterable<N>> elements) {
        return elements.lazyReduceRight(IdeaList.empty(), (elem, accum) -> concat(elem.value(), accum));
    }*/

    /*private static <N> IdeaList<N> concatNestedIterables(IdeaList<Iterable<N>> elements) {
        return elements.lazyReduceRight(IdeaList.empty(), (elem, accum) -> concat(elem.value(), accum));
    }*/



    @SuppressWarnings("unchecked") // Cast is safe because isNested() first checks if list does in fact contain nested iterables
    private <N> IdeaList<N> concatNestedIdeaLists() {
        return lazyReduceRight(IdeaList.empty(), (elem, list) -> concat((IdeaList<N>) elem.value(), list));
    }

    /*public <N> IdeaList<N> flatten() {
        if (isNestedIdeaLists()) return concatNestedIdeaLists();
        // if (isNested()) return concatNestedIterables();
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }*/

    @SuppressWarnings("unchecked") // Cast is safe because isNested() first checks if list does in fact contain nested iterables
    public <N> IdeaList<N> flatten() {
        if (containsNestedIdeaLists()) return concatNestedIdeaLists((IdeaList<IdeaList<N>>) this);
        // if (isNested()) return concatNestedIterables();
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }

    /*public <N> IdeaList<N> flatten() {
        // maybe put result of concatNestedIdeaLists() in variable for more precise @SuppressWarnings("unchecked")
        if (isNestedIdeaLists()) {
            @SuppressWarnings("unchecked") // Cast is safe because isNested() first checks if list does in fact contain nested iterables
            var list = concatNestedIdeaLists((IdeaList<IdeaList<N>>) this);
            return list;
        }
        // if (isNested()) return concatNestedIterables();
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }*/

    public <A, B> IdeaList<Triplet<E, A, B>> zipWith(Iterable<A> other, Iterable<B> other2) {
        return Enumerable.zip(this, other, other2);
    }

    public <A> IdeaList<Pair<E, A>> zipWith(Iterable<A> other) {
        return Enumerable.zip(this, other);
    }

    /*public IdeaList<IndexElement<E>> withIndex() {
        return Enumerable.withIndex(this);
    }*/

    public IdeaList<IndexElement<E>> withIndex() {
        return mapIndexed(IndexElement::of);
    }

    public IdeaList<E> step(int length) {
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

    private <R extends Comparable<R>> List<E> toListSortedByAscending(Function<E, R> selector) {
        List<E> list = toList();
        list.sort(Comparator.comparing(selector));
        return list;
    }

    public <R extends Comparable<R>> IdeaList<E> sortByAscending(Function<E, R> selector) {
        return IdeaList.of(toListSortedByAscending(selector));
    }

    public <R extends Comparable<R>> IdeaList<E> sortByAsc(Function<E, R> selector) {
        return sortByAscending(selector);
    }

    private <R extends Comparable<R>> List<E> toListSortedByDescending(Function<E, R> selector) {
        List<E> list = toList();
        list.sort(Comparator.comparing(selector, Comparator.reverseOrder()));
        return list;
    }

    public <R extends Comparable<R>> IdeaList<E> sortByDescending(Function<E, R> selector) {
        return IdeaList.of(toListSortedByDescending(selector));
    }

    public <R extends Comparable<R>> IdeaList<E> sortByDesc(Function<E, R> selector) {
        return sortByDescending(selector);
    }

    /*public IdeaList<E> sortWith(Comparator<E> comparator) {
        Comparator<Lazy<E>> lazyComparator = Comparator.comparing(Lazy::value, comparator);
        return MergeSort.sort(lazyComparator, this);
    }*/

    /*public IdeaList<E> reverse() {
        return reduce(IdeaList.empty(), (accum, elem) -> IdeaList.create(elem, accum));
    }*/


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class NormalNode<E> extends IdeaList<E> {

        // Constructors and factory methods =============================================================
        private NormalNode(Lazy<E> value, Lazy<IdeaList<E>> tail) {
            super(value, tail);
        }

        private IdeaList<E> keepValueAndTransformTail(UnaryOperator<IdeaList<E>> function) {
            return IdeaList.create(value, Lazy.of(() -> function.apply(tail.value())));
        }

        // Getters ======================================================================================
        @Override
        public E get(int index) {
            if (index < 0) return get(toPositiveIndex(index));
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
        public Optional<E> findFirst(Predicate<E> predicate) {
            return predicate.test(value.value()) ? Optional.of(value.value()) : tail.value().findFirst(predicate);
        }

        @Override
        protected OptionalInt indexOfFirstHelper(Predicate<E> predicate, int index) {
            return predicate.test(value.value()) ? OptionalInt.of(index) : indexOfFirstHelper(predicate, index + 1);
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
        public boolean containsNestedIdeaLists() {
            return value.value() instanceof IdeaList;
        }

        @Override
        public boolean any() {
            return true;
        }


        // Modifiers ====================================================================================
        @Override
        public IdeaList<E> insertAt(int index, IdeaList<E> elements) {
            if (index < 0) return insertAt(toPositiveIndex(index), elements);
            return index == 0
                    ? concat(elements, this)
                    : keepValueAndTransformTail(tail -> tail.insertAt(index - 1, elements));
        }

        /*@Override
        public IdeaList<E> insertAt2(int index, Iterable<E> elements) {
            if (index < 0) return insertAt(toPositiveIndex(index), elements);
            return index == 0
                    ? concat(elements, this)
                    : keepValueAndTransformTail(tail -> tail.insertAt(index - 1, elements));
        }*/

        // concat(IdeaList<E> elements, IdeaList<E> other)
        // concat(Iterable<E> elements, IdeaList<E> other)
        @Override
        protected <I> IdeaList<E> insertAtHelper(int index, I elements, BiFunction<I, IdeaList<E>, IdeaList<E>> concat) {
            if (index < 0) return insertAtHelper(toPositiveIndex(index), elements, concat);
            return index == 0
                    ? concat.apply(elements, this)
                    : keepValueAndTransformTail(tail -> tail.insertAtHelper(index - 1, elements, concat));
        }

        /*@Override
        public IdeaList<E> insertAt2(int index, IdeaList<E> elements) {
            if (index < 0) return insertAt(toPositiveIndex(index), elements);
            return index == 0
                    ? concat(elements, this)
                    : keepValueAndTransformTail(tail -> tail.insertAt(index - 1, elements));
        }

        @Override
        public IdeaList<E> insertAt2(int index, Iterable<E> elements) {
            if (index < 0) return insertAt(toPositiveIndex(index), elements);
            return index == 0
                    ? concat(elements, this)
                    : keepValueAndTransformTail(tail -> tail.insertAt(index - 1, elements));
        }*/

        @Override
        public IdeaList<E> removeFirst(@Nullable E element) {
            return Objects.equals(value.value(), element) ? tail.value() : keepValueAndTransformTail(tail -> tail.removeFirst(element));
        }

        @Override
        public IdeaList<E> removeAt(int index) {
            if (index < 0) return removeAt(toPositiveIndex(index));
            return index == 0 ? tail.value() : keepValueAndTransformTail(tail -> tail.removeAt(index - 1));
        }


        // List operations ==============================================================================
        @Override
        protected <A> A lazyReduceRight(A initialValue, BiFunction<Lazy<E>, Lazy<A>, A> operation) {
            return operation.apply(value, Lazy.of(() -> tail.value().lazyReduceRight(initialValue, operation)));
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class EndNode<E> extends IdeaList<E> {
        private static final IdeaList<?> EMPTY = new EndNode<>();

        // Constructors and factory methods =============================================================
        private static <E> E throwNoSuchValueException() {
            throw new NoSuchElementException("The value field in EndNode contains no value");
        }

        private static <E> IdeaList<E> throwNoSuchTailException() {
            throw new NoSuchElementException("The tail field in EndNode contains no value");
        }

        private EndNode() {
            super(Lazy.of(EndNode::throwNoSuchValueException), Lazy.of(EndNode::throwNoSuchTailException));
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
        protected OptionalInt indexOfFirstHelper(Predicate<E> predicate, int index) {
            return OptionalInt.empty();
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
        public boolean containsNestedIdeaLists() {
            return false;
        }

        @Override
        public boolean any() {
            return false;
        }


        // Modifiers ====================================================================================
        @Override
        public IdeaList<E> insertAt(int index, IdeaList<E> elements) {
            throw indexTooBigException();
        }

        @Override
        protected <I> IdeaList<E> insertAtHelper(int index, I elements, BiFunction<I, IdeaList<E>, IdeaList<E>> concat) {
            throw indexTooBigException();
        }

        /*@Override
        public IdeaList<E> insertAt2(int index, IdeaList<E> elements) {
            throw indexTooBigException();
        }

        @Override
        public IdeaList<E> insertAt2(int index, Iterable<E> elements) {
            throw indexTooBigException();
        }*/

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
        protected <A> A lazyReduceRight(A initialValue, BiFunction<Lazy<E>, Lazy<A>, A> operation) {
            return initialValue;
        }
    }
}
