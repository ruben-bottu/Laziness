package idealist;

import idealist.function.TriFunction;
import idealist.range.Range;
import idealist.tuple.IndexElement;
import idealist.tuple.Pair;
import idealist.tuple.Triplet;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

import static idealist.Enumerable.arrayOf;
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

    static <E> IdeaList<E> of(MutableList<E> elements) {
        if (elements.isEmpty()) return IdeaList.empty();
        return IdeaList.create(elements.value, Lazy.of(() -> of(elements.tail)));
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

    /*public E get2(int index) {
        if (index < 0) return takeLast(-index).first();
        return findFirstIndexed((idx, __) -> idx == index)
                .orElseThrow(IdeaList::indexTooBigException);
    }*/

    // or even better: (not every value needs to get evaluated)
    /*public E get2(int index) {
        if (index < 0) return takeLast(-index).first();
        return lazyFindFirstIndexed((idx, __) -> idx == index)
                .orElseThrow(IdeaList::indexTooBigException)
                .value(); // this is optional, lazyFindFirst() can already calc the value
    }*/

    /*public E get2(int index) { // [d, b, c]
        if (index < 0) {
            @SuppressWarnings("unchecked")
            Lazy<E>[] previousValues = (Lazy<E>[]) new Lazy[-index-1]; // length = 3
            Lazy<E>[] previousValues2 = Enumerable.arrayOf(value, -index - 1);
            Lazy<E>[] previousValues3 = Enumerable.arrayOf(Lazy.class, -index - 1);
            int firstValue = 0;

            if (tail.value().isEmpty()) {
                return previousValues[0].value();
            } else {
                previousValues[firstValue++] = value;
            }
        }
    }*/

    /*public E get(int index) {
        if (index < 0) return get(toPositiveIndex(index));
        return withIndex().findFirst(pair -> pair.index == index)
                .orElseThrow(IdeaList::indexTooBigException)
                .element;
    }

    // findFirstIndexed() will unnecessarily evaluate values, even when they will never be used
    // see predicate.test(value.value()) in findFirst()
    public E get2(int index) {
        if (index < 0) return get(toPositiveIndex(index));
        return findFirstIndexed((idx, elem) -> idx == index)
                .orElseThrow(IdeaList::indexTooBigException);
    }*/

    public abstract E first();

    public abstract E single();

    // return get(-1);
    public abstract E last();

    // TODO benchmark against variant with RANDOM constant
    public E random() {
        return get(new Random().nextInt(length()));
    }

    /*public E random() {
        return get(RANDOM.nextInt(length()));
    }*/

    public abstract Optional<E> findFirst(Predicate<E> predicate);

    public Optional<E> findFirstIndexed(BiPredicate<Integer, E> predicate) {
        var index = new AtomicInteger();
        return findFirst(elem -> predicate.test(index.getAndIncrement(), elem));
    }

    public Optional<E> first(Predicate<E> predicate) {
        return findFirst(predicate);
    }

    public Optional<E> firstIndexed(BiPredicate<Integer, E> predicate) {
        return findFirstIndexed(predicate);
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

    public abstract int lastIndex();

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

    public E[] toArray(Class<E> elementType) {
        E[] result = arrayOf(elementType, length());
        forEachIndexed((index, elem) -> result[index] = elem);
        return result;
    }

    @Override
    public int hashCode() {
        var hashCode = new AtomicInteger(1);
        IntBinaryOperator accumulatorFunction = (accum, elem) -> 31 * accum + elem;
        // forEach(elem -> hashCode.accumulateAndGet(elem.hashCode(), accumulatorFunction));
        forEach(elem -> hashCode.accumulateAndGet((elem == null ? 0 : elem.hashCode()), accumulatorFunction));
        return hashCode.get();
    }

    // If this is most efficient one -> move to Enumerable
    public int hashCode2() {
        int hashCode = 1;
        for (E element : this) {
            hashCode = 31 * hashCode + (element == null ? 0 : element.hashCode());
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return toList().toString();
    }

    @Override
    public Iterator<E> iterator() {
        return Enumerator.of(this);
    }

    protected abstract Optional<IndexElement<IdeaList<E>>> findFirstIndexedNodeHelper(int index, BiPredicate<Integer, Lazy<E>> predicate);

    private Optional<IndexElement<IdeaList<E>>> findFirstIndexedNode(BiPredicate<Integer, Lazy<E>> predicate) {
        return findFirstIndexedNodeHelper(0, predicate);
    }

    protected abstract IndexElement<IdeaList<E>> findFirstIndexedNodeHelper2(int index, BiPredicate<Integer, Lazy<E>> predicate);

    // !!!!! MAY RETURN NULL !!!!!
    private IndexElement<IdeaList<E>> findFirstIndexedNode2(BiPredicate<Integer, Lazy<E>> predicate) {
        return findFirstIndexedNodeHelper2(0, predicate);
    }

    public E get2(int index) {
        if (index < 0) return get(toPositiveIndex(index));
        return findFirstIndexedNode((idx, __) -> idx == index)
                .orElseThrow(IdeaList::indexTooBigException)
                .element
                .first();
    }

    public E get3(int index) {
        if (index < 0) return get(toPositiveIndex(index));
        var indexedNode = findFirstIndexedNode2((idx, __) -> idx == index);
        return indexedNode == null ? throwIndexTooBigException() : indexedNode.element.first();
    }

    /*public Optional<E> findFirst2(Predicate<E> predicate) {
        return findFirstIndexedNode((__, value) -> predicate.test(value.value()))
                .map(idxElem -> idxElem.element.first());
    }*/

    public Optional<E> findFirst2(Predicate<E> predicate) {
        return findFirstIndexed2((__, elem) -> predicate.test(elem));
    }

    public Optional<E> findFirstIndexed2(BiPredicate<Integer, E> predicate) {
        return findFirstIndexedNode((index, value) -> predicate.test(index, value.value()))
                .map(idxList -> idxList.element.first());
    }

    public Optional<Integer> indexOfFirst4(Predicate<E> predicate) {
        return findFirstIndexedNode((__, value) -> predicate.test(value.value()))
                .map(idxList -> idxList.index);
    }

    public OptionalInt indexOfFirst5(Predicate<E> predicate) {
        return findFirstIndexedNode((__, value) -> predicate.test(value.value()))
                .map(idxList -> OptionalInt.of(idxList.index))
                .orElseGet(OptionalInt::empty);
    }


    // Checks =======================================================================================
    // X checking tests
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

    @Override
    public void forEach(Consumer<? super E> action) {
        for (E element : this) action.accept(element);
    }

    public abstract void forEach2(Consumer<? super E> action);

    public void forEachIndexed(BiConsumer<Integer, E> action) {
        var index = new AtomicInteger();
        forEach(elem -> action.accept(index.getAndIncrement(), elem));
    }

    public void forEachIndexed2(BiConsumer<Integer, E> action) {
        int index = 0;
        for (E element : this) action.accept(index++, element);
    }

    /*public IdeaList<E> takeLast(int n) {

    }*/

    /*@Override
    protected <I> IdeaList<E> insertAtHelper(int index, I elements, BiFunction<I, IdeaList<E>, IdeaList<E>> concat) {
        if (index < 0) return insertAtHelper(toPositiveIndex(index), elements, concat);
        return index == 0
                ? concat.apply(elements, this)
                : keepValueAndTransformTail(tail -> tail.insertAtHelper(index - 1, elements, concat));
    }

    @Override
    public IdeaList<E> removeFirst(@Nullable E element) {
        return Objects.equals(first(), element) ? tail.value() : keepValueAndTransformTail(tail -> tail.removeFirst(element));
    }

    @Override
    public IdeaList<E> removeAt(int index) {
        if (index < 0) return removeAt(toPositiveIndex(index));
        return index == 0 ? tail.value() : keepValueAndTransformTail(tail -> tail.removeAt(index - 1));
    }*/

    private IdeaList<E> keepValueAndTransformTail(UnaryOperator<IdeaList<E>> function) {
        return IdeaList.create(value, Lazy.of(() -> function.apply(tail.value())));
    }

    protected abstract IdeaList<E> concatWhenHelper(int index, BiPredicate<Integer, Lazy<E>> predicate, Function<IdeaList<E>, IdeaList<E>> listAtMatchToNewList, Supplier<IdeaList<E>> ifNoMatchReturn);

    private IdeaList<E> concatWhen(BiPredicate<Integer, Lazy<E>> predicate, Function<IdeaList<E>, IdeaList<E>> listAtMatchToNewList, Supplier<IdeaList<E>> ifNoMatchReturn) {
        return concatWhenHelper(0, predicate, listAtMatchToNewList, ifNoMatchReturn);
    }

    /*private static <E> IdeaList<E> throwIndexTooBigException() {
        throw indexTooBigException();
    }*/

    private static <A> A throwIndexTooBigException() {
        throw indexTooBigException();
    }

    /*@Override
    protected <I> IdeaList<E> insertAtHelper(int index, I elements, BiFunction<I, IdeaList<E>, IdeaList<E>> concat) {
        if (index < 0) return insertAtHelper(toPositiveIndex(index), elements, concat);
        return index == 0
                ? concat.apply(elements, this)
                : keepValueAndTransformTail(tail -> tail.insertAtHelper(index - 1, elements, concat));
    }*/

    protected <I> IdeaList<E> insertAtHelper2(int index, I elements, BiFunction<I, IdeaList<E>, IdeaList<E>> concat) {
        if (index < 0) return insertAtHelper2(toPositiveIndex(index), elements, concat);
        return concatWhen((idx, __) -> idx == index, listAtMatch -> concat.apply(elements, listAtMatch), IdeaList::throwIndexTooBigException);
    }

    /*@Override
    public IdeaList<E> removeFirst(@Nullable E element) {
        return Objects.equals(first(), element) ? tail.value() : keepValueAndTransformTail(tail -> tail.removeFirst(element));
    }*/

    public IdeaList<E> removeFirst2(@Nullable E element) {
        return concatWhen((__, value) -> Objects.equals(value.value(), element), listAtMatch -> listAtMatch.tail.value(), IdeaList::empty);
    }

    /*@Override
    public IdeaList<E> removeAt(int index) {
        if (index < 0) return removeAt(toPositiveIndex(index));
        return index == 0 ? tail.value() : keepValueAndTransformTail(tail -> tail.removeAt(index - 1));
    }*/

    public IdeaList<E> removeAt2(int index) {
        if (index < 0) return removeAt(toPositiveIndex(index));
        return concatWhen((idx, __) -> idx == index, listAtMatch -> listAtMatch.tail.value(), IdeaList::throwIndexTooBigException);
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

    private static <I, N> IdeaList<N> concatNestedHelper(IdeaList<I> elements, BiFunction<I, Lazy<IdeaList<N>>, IdeaList<N>> concat) {
        return elements.lazyReduceRight(IdeaList.empty(), (elem, accum) -> concat.apply(elem.value(), accum));
    }

    private static <N> IdeaList<N> concatNestedIdeaLists(IdeaList<IdeaList<N>> elements) {
        return concatNestedHelper(elements, IdeaList::concat);
    }

    private static <N> IdeaList<N> concatNestedIterables(IdeaList<Iterable<N>> elements) {
        return concatNestedHelper(elements, IdeaList::concat);
    }

    /*private static <N> IdeaList<N> concatNestedIdeaLists(IdeaList<IdeaList<N>> elements) {
        return elements.lazyReduceRight(IdeaList.empty(), (elem, accum) -> concat(elem.value(), accum));
    }

    private static <N> IdeaList<N> concatNestedIterables(IdeaList<Iterable<N>> elements) {
        return elements.lazyReduceRight(IdeaList.empty(), (elem, accum) -> concat(elem.value(), accum));
    }*/

    @SuppressWarnings("unchecked") // Cast is safe because isNested() first checks if list does in fact contain nested iterables
    public <N> IdeaList<N> flatten() {
        if (containsNestedIdeaLists()) return concatNestedIdeaLists((IdeaList<IdeaList<N>>) this);
        if (isNested()) return concatNestedIterables((IdeaList<Iterable<N>>) this);
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }

    /*@SuppressWarnings("unchecked") // Cast is safe because isNested() first checks if list does in fact contain nested iterables
    private <N> IdeaList<N> concatNestedIdeaLists() {
        return lazyReduceRight(IdeaList.empty(), (elem, list) -> concat((IdeaList<N>) elem.value(), list));
    }*/

    @SuppressWarnings("unchecked")
    public <N> IdeaList<N> flatten2() {
        return lazyReduceRight(IdeaList.empty(), (elem, list) ->
                elem.value() instanceof IdeaList
                        ? concat((IdeaList<N>) elem.value(), list)
                        : concat((Iterable<N>) elem.value(), list));
    }

    private static boolean isInstanceOfNormalNode(Object object) {
        return object.getClass() == IdeaList.NormalNode.class;
    }

    private static boolean isInstanceOfEndNode(Object object) {
        return object.getClass() == IdeaList.EndNode.class;
    }

    // What about EndNode? Just return list
    @SuppressWarnings("unchecked")
    private static <N> IdeaList<N> concatContainerToList(Lazy<?> container, Lazy<IdeaList<N>> list) {
        return isInstanceOfNormalNode(container.value())
                ? concat((IdeaList<N>) container.value(), list)
                : concat((Iterable<N>) container.value(), list);
    }

    @SuppressWarnings("unchecked")
    private static <N> IdeaList<N> concatContainerToList2(Lazy<?> container, Lazy<IdeaList<N>> list) {
        if (isInstanceOfNormalNode(container.value())) return concat((IdeaList<N>) container.value(), list);
        if (isInstanceOfEndNode(container.value())) return list.value();
        return concat((Iterable<N>) container.value(), list);
    }

    /*@SuppressWarnings("unchecked")
    public <N> IdeaList<N> flatten3() {
        return lazyReduceRight(IdeaList.empty(), (elem, list) ->
                elem.value().getClass() == IdeaList.NormalNode.class
                        ? concat((IdeaList<N>) elem.value(), list)
                        : concat((Iterable<N>) elem.value(), list));
    }*/

    public <N> IdeaList<N> flatten3() {
        return lazyReduceRight(IdeaList.empty(), IdeaList::concatContainerToList);
    }

    @SuppressWarnings("unchecked") // This implementation also takes nested empty IdeaLists into account
    private static <N> IdeaList<N> concatContainerToList3(Lazy<?> container, Lazy<IdeaList<N>> list) {
        return container.value() instanceof IdeaList
                ? concat((IdeaList<N>) container.value(), list)
                : concat((Iterable<N>) container.value(), list);
    }

    public <N> IdeaList<N> flatten4() {
        return lazyReduceRight(IdeaList.empty(), IdeaList::concatContainerToList3);
    }

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
            return index == 0 ? first() : tail.value().get(index - 1);
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
            return tail.value().isEmpty() ? first() : tail.value().last();
        }

        @Override
        public Optional<E> findFirst(Predicate<E> predicate) {
            return predicate.test(first()) ? Optional.of(first()) : tail.value().findFirst(predicate);
        }

        @Override
        protected OptionalInt indexOfFirstHelper(Predicate<E> predicate, int index) {
            return predicate.test(first()) ? OptionalInt.of(index) : tail.value().indexOfFirstHelper(predicate, index + 1);
        }

        @Override
        public int lastIndex() {
            return length() - 1;
        }

        @Override
        public IdeaList<Integer> indices() {
            return Range.from(0).upToAndIncluding(lastIndex());
        }

        @Override
        protected Optional<IndexElement<IdeaList<E>>> findFirstIndexedNodeHelper(int index, BiPredicate<Integer, Lazy<E>> predicate) {
            return predicate.test(index, value) ? Optional.of(IndexElement.of(index, this)) : tail.value().findFirstIndexedNodeHelper(index + 1, predicate);
        }

        @Override
        protected IndexElement<IdeaList<E>> findFirstIndexedNodeHelper2(int index, BiPredicate<Integer, Lazy<E>> predicate) {
            return predicate.test(index, value) ? IndexElement.of(index, this) : tail.value().findFirstIndexedNodeHelper2(index + 1, predicate);
        }

        // Checks =======================================================================================
        @Override
        public boolean isNested() {
            return first() instanceof Iterable;
        }

        @Override
        public boolean containsNestedIdeaLists() {
            return first() instanceof IdeaList;
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
            return Objects.equals(first(), element) ? tail.value() : keepValueAndTransformTail(tail -> tail.removeFirst(element));
        }

        @Override
        public IdeaList<E> removeAt(int index) {
            if (index < 0) return removeAt(toPositiveIndex(index));
            return index == 0 ? tail.value() : keepValueAndTransformTail(tail -> tail.removeAt(index - 1));
        }

        protected IdeaList<E> concatWhenHelper(int index, BiPredicate<Integer, Lazy<E>> predicate, Function<IdeaList<E>, IdeaList<E>> listAtMatchToNewList, Supplier<IdeaList<E>> ifNoMatchReturn) {
            return predicate.test(index, value) ? listAtMatchToNewList.apply(this) : IdeaList.create(value, Lazy.of(() -> tail.value().concatWhenHelper(index + 1, predicate, listAtMatchToNewList, ifNoMatchReturn)));
        }

        /*protected IdeaList<E> helperIndexed(int index, BiPredicate<Integer, Lazy<E>> predicate, Function<IdeaList<E>, IdeaList<E>> getRest) {
            return predicate.test(index, value) ? getRest.apply(this) : keepValueAndTransformTail(tail -> tail.helperIndexed(index - 1, predicate, getRest));
        }*/

        /*@Override
        public Optional<E> findFirst(Predicate<E> predicate) {
            return predicate.test(first()) ? Optional.of(first()) : tail.value().findFirst(predicate);
        }*/

        @Override
        public void forEach2(Consumer<? super E> action) {
            action.accept(first());
            tail.value().forEach2(action);
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

        private static UnsupportedOperationException emptyListHasNoIndicesException() {
            return new UnsupportedOperationException("Empty list has no indices");
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
        public int lastIndex() {
            throw emptyListHasNoIndicesException();
        }

        @Override
        public IdeaList<Integer> indices() {
            throw emptyListHasNoIndicesException();
        }

        @Override
        protected Optional<IndexElement<IdeaList<E>>> findFirstIndexedNodeHelper(int index, BiPredicate<Integer, Lazy<E>> predicate) {
            return Optional.empty();
        }

        @Override
        protected IndexElement<IdeaList<E>> findFirstIndexedNodeHelper2(int index, BiPredicate<Integer, Lazy<E>> predicate) {
            return null;
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

        @Override
        protected IdeaList<E> concatWhenHelper(int index, BiPredicate<Integer, Lazy<E>> predicate, Function<IdeaList<E>, IdeaList<E>> listAtMatchToNewList, Supplier<IdeaList<E>> ifNoMatchReturn) {
            return ifNoMatchReturn.get();
        }

        @Override
        public void forEach2(Consumer<? super E> action) {}

        // List operations ==============================================================================
        @Override
        protected <A> A lazyReduceRight(A initialValue, BiFunction<Lazy<E>, Lazy<A>, A> operation) {
            return initialValue;
        }
    }
}
