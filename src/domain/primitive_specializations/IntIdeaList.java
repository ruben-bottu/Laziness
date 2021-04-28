/*
package domain.primitive_specializations;

import com.sun.istack.internal.Nullable;
import domain.*;
import domain.Range;
import domain.IndexElement;
import domain.Pair;
import domain.Triplet;
import domain.function.ObjIntFunction;
import domain.function.TriFunction;

import java.util.*;
import java.util.function.*;

public abstract class IntIdeaList implements Iterable<Integer> {
    public final LazyInt value;
    public final Lazy<IntIdeaList> tail;

    // Constructors and factory methods =============================================================
    private IntIdeaList(LazyInt value, Lazy<IntIdeaList> tail) {
        this.value = value;
        this.tail = tail;
    }

    private static IntIdeaList create(LazyInt value, Lazy<IntIdeaList> tail) {
        return new NormalNode(value, tail);
    }

    public static IntIdeaList empty() {
        return new EndNode<>();
    }

    */
/*private static <E> IntIdeaList concat(Iterator<E> iterator, IntIdeaList elements) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return IntIdeaList.create(Lazy.of(() -> element), Lazy.of(() -> concat(iterator, elements)));
        }
        return elements;
    }

    private static <E> IntIdeaList concat(Iterable<E> iterable, IntIdeaList elements) {
        return concat(iterable.iterator(), elements);
    }*//*


    */
/*private static <I> IntIdeaList concat(IntIterator iterator, I elements, Function<I, IntIdeaList> toIntIdeaList) {
        if (iterator.hasNext()) {
            int element = iterator.next();
            return IntIdeaList.create(LazyInt.of(() -> element), Lazy.of(() -> concat(iterator, elements, toIntIdeaList)));
        }
        return toIntIdeaList.apply(elements);
    }*//*


    private static <I> IntIdeaList concat(PrimitiveIterator.OfInt iterator, I elements, Function<I, IntIdeaList> toIntIdeaList) {
        if (iterator.hasNext()) {
            int element = iterator.nextInt();
            return IntIdeaList.create(LazyInt.of(() -> element), Lazy.of(() -> concat(iterator, elements, toIntIdeaList)));
        }
        return toIntIdeaList.apply(elements);
    }

    */
/*private static IntIdeaList concat(IntIterator iterator, Lazy<IntIdeaList> elements) {
        if (iterator.hasNext()) {
            int element = iterator.next();
            return IntIdeaList.create(LazyInt.of(() -> element), Lazy.of(() -> concat(iterator, elements)));
        }
        return elements.value();
    }

    private static IntIdeaList concat(IntIterator iterator, IntIdeaList elements) {
        if (iterator.hasNext()) {
            int element = iterator.next();
            return IntIdeaList.create(LazyInt.of(() -> element), Lazy.of(() -> concat(iterator, elements)));
        }
        return elements;
    }*//*


    */
/*private static IntIdeaList concat(IntIterator iterator, Lazy<IntIdeaList> elements) {
        return concat(iterator, elements, Lazy::value);
    }

    private static IntIdeaList concat(IntIterator iterator, IntIdeaList elements) {
        return concat(iterator, elements, Function.identity());
    }*//*


    */
/*private static IntIdeaList concat(Iterable<Integer> iterable, Lazy<IntIdeaList> elements) {
        return concat(Enumerator.ofInt(iterable), elements);
    }

    private static IntIdeaList concat(Iterable<Integer> iterable, IntIdeaList elements) {
        return concat(iterable, Lazy.of(() -> elements));
    }*//*


    private static IntIdeaList concat(Iterable<Integer> iterable, Lazy<IntIdeaList> elements) {
        return concat(Enumerator.ofInt(iterable), elements, Lazy::value);
    }

    private static IntIdeaList concat(Iterable<Integer> iterable, IntIdeaList elements) {
        return concat(Enumerator.ofInt(iterable), elements, Function.identity());
    }

    public static IntIdeaList of(Iterable<Integer> elements) {
        return concat(elements, IntIdeaList.empty());
    }

    public static IntIdeaList of(int... elements) {
        return concat(Enumerator.of(elements), IntIdeaList.empty(), Function.identity());
    }

    private static IntIdeaList rangeLength(int from, int length) {
        return length == 0 ? IntIdeaList.empty() : IntIdeaList.create(LazyInt.of(() -> from), Lazy.of(() -> rangeLength(from + 1, length - 1)));
    }

    public static IntIdeaList initialiseWith(int length, IntUnaryOperator indexToElement) {
        handleNegativeLength(length);
        return rangeLength(0, length).map(indexToElement);
    }

    // Will become redundant
    */
/*private static IntIdeaList<Long> rangeLength(long from, long length) {
        return length == 0L ? IntIdeaList.empty() : IntIdeaList.create(Lazy.of(() -> from), Lazy.of(() -> rangeLength(from + 1L, length - 1L)));
    }

    public static <E> IntIdeaList initialiseWithLong(long length, LongFunction<E> indexToElement) {
        handleNegativeLength(length);
        return rangeLength(0L, length).map(indexToElement);
    }*//*


    private static void handleNegativeLength(long length) {
        if (length < 0L) throw new IllegalArgumentException("Cannot initialise list with length " + length);
    }

    private static void handleNegativeIndex(int index) {
        if (index < 0) throw new IndexOutOfBoundsException("Index cannot be negative. Given: " + index);
    }

    private static IndexOutOfBoundsException indexTooBigException() {
        return new IndexOutOfBoundsException("Index too big");
    }


    // Getters ======================================================================================
    public int get(int index) {
        handleNegativeIndex(index);
        return withIndex().findFirst(pair -> pair.index == index)
                .orElseThrow(IntIdeaList::indexTooBigException)
                .element;
    }

    public abstract int first();

    public abstract int single();

    public abstract int last();

    public int random() {
        return get(new Random().nextInt(length()));
    }

    public abstract OptionalInt findFirst(IntPredicate predicate);

    public OptionalInt first(IntPredicate predicate) {
        return findFirst(predicate);
    }

    public int indexOfFirst(IntPredicate predicate) {
        return withIndex().findFirst(pair -> predicate.test(pair.element))
                .orElse(IndexElement.ofIndex(-1))
                .index;
    }

    public int indexOfFirst(int element) {
        return indexOfFirst(current -> current == element);
    }

    public int lastIndex() {
        return length() - 1;
    }

    public abstract IntIdeaList indices();

    public abstract int length();

    public List<Integer> toList() {
        List<Integer> list = new ArrayList<>();
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
    public PrimitiveIterator.OfInt iterator() {
        return Enumerator.of(this);
    }


    // Checks =======================================================================================
    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntIdeaList ideaList = (IntIdeaList) o;
        return Enumerable.isContentEqual(this, ideaList);
    }

    public boolean contains(int element) {
        return indexOfFirst(element) != -1;
    }

    public boolean containsAll(Iterable<Integer> elements) {
        return IntIdeaList.of(elements).all(this::contains);
    }

    public boolean isEmpty() {
        return none();
    }

    public abstract boolean isNested();

    public boolean all(IntPredicate predicate) {
        return !map(predicate::test).contains(false);
    }

    public abstract boolean any();

    public boolean any(IntPredicate predicate) {
        return map(predicate::test).contains(true);
    }

    public boolean none() {
        return !any();
    }

    public boolean none(IntPredicate predicate) {
        return !any(predicate);
    }


    // Modifiers ====================================================================================
    public abstract IntIdeaList insertAt(int index, Iterable<Integer> elements);

    public final IntIdeaList insertAt(int index, int... elements) {
        return insertAt(index, Enumerable.of(elements));
    }

    public IntIdeaList concatWith(Iterable<Integer> elements) {
        return concat(this, Lazy.of(() -> IntIdeaList.of(elements)));
    }

    public final IntIdeaList add(int... elements) {
        return concatWith(Enumerable.of(elements));
    }

    public IntIdeaList linkToBackOf(Iterable<Integer> elements) {
        return concat(elements, this);
    }

    public final IntIdeaList addToFront(int... elements) {
        return linkToBackOf(Enumerable.of(elements));
    }

    public abstract IntIdeaList removeFirst(int element);

    public abstract IntIdeaList removeAt(int index);

    public IntIdeaList removeAll(int element) {
        return where(current -> current != element);
    }

    @Override
    public void forEach(Consumer<? super Integer> action) {
        iterator().forEachRemaining((IntConsumer) action);
    }

    */
/*@Override
    public void forEach(Consumer<? super Integer> action) {
        forEach((IntConsumer) action);
    }

    public void forEach(IntConsumer action) {
        iterator().forEachRemaining(action);
    }*//*


    public void forEachIndexed(BiConsumer<Integer, Integer> action) {
        withIndex().forEach(currentPair -> action.accept(currentPair.index, currentPair.element));
    }

    */
/*public void forEachIndexed(IntIntConsumer action) {
        withIndex().forEach(currentPair -> action.accept(currentPair.index, currentPair.element));
    }*//*



    // List operations ==============================================================================
    */
/*private static <A> A reduceHelper(A initialValue, IntIdeaList list, BiFunction<A, Integer, A> operation) {
        A accumulator = initialValue;
        for (int element : list) accumulator = operation.apply(accumulator, element);
        return accumulator;
    }*//*


    private static <A> A reduceHelper(A initialValue, IntIdeaList list, BiFunction<A, Integer, A> operation) {
        A accumulator = initialValue;
        PrimitiveIterator.OfInt it = list.iterator();
        while (it.hasNext()) {
            accumulator = operation.apply(accumulator, it.nextInt());
        }
        return accumulator;
    }

    */
/*private static <A> A reduceHelper(A initialValue, IntIdeaList list, ObjIntFunction<A, A> operation) {
        A accumulator = initialValue;
        PrimitiveIterator.OfInt it = list.iterator();
        while (it.hasNext()) {
            accumulator = operation.apply(accumulator, it.nextInt());
        }
        return accumulator;
    }*//*


    public <A> A reduce(A initialValue, BiFunction<A, Integer, A> operation) {
        return reduceHelper(initialValue, this, operation);
    }

    public <A> A reduceIndexed(A initialValue, TriFunction<Integer, A, Integer, A> operation) {
        return withIndex().reduce(initialValue, (accumulator, currentPair) -> operation.apply(currentPair.index, accumulator, currentPair.element));
    }

    public abstract int reduce(IntBinaryOperator operation);

    */
/*public E reduceIndexed(TriFunction<Integer, E, E, E> operation) {
        return withIndex().reduce((accumulator, currentPair) -> operation.apply(currentPair.index, accumulator, currentPair.element));
    }*//*


    public int reduceIndexed(TriFunction<Integer, Integer, Integer, Integer> operation) {
        return withIndex().reduce((accumulator, currentPair) -> IndexElement.of(0, operation.apply(currentPair.index, accumulator.element, currentPair.element))).element;
    }

    public abstract <R> IdeaList<R> map(IntFunction<R> transform);

    public abstract IntIdeaList map(IntUnaryOperator transform);

    public <R> IdeaList<R> mapIndexed(BiFunction<Integer, Integer, R> transform) {
        return withIndex().map(currentPair -> transform.apply(currentPair.index, currentPair.element));
    }

    public <R> IntIdeaList<R> select(Function<E, R> transform) {
        return map(transform);
    }

    public <R> IntIdeaList<R> selectIndexed(BiFunction<Integer, E, R> transform) {
        return mapIndexed(transform);
    }

    public abstract IntIdeaList where(Predicate<E> predicate);

    public IntIdeaList whereIndexed(BiPredicate<Integer, E> predicate) {
        return withIndex().where(currentPair -> predicate.test(currentPair.index, currentPair.element))
                .select(currentPair -> currentPair.element);
    }

    public IntIdeaList filter(Predicate<E> predicate) {
        return where(predicate);
    }

    public IntIdeaList filterIndexed(BiPredicate<Integer, E> predicate) {
        return whereIndexed(predicate);
    }

    public IntIdeaList findAll(Predicate<E> predicate) {
        return where(predicate);
    }

    public IntIdeaList findAllIndexed(BiPredicate<Integer, E> predicate) {
        return whereIndexed(predicate);
    }

    abstract <N> IntIdeaList<N> concatNestedIterables();

    public <N> IntIdeaList<N> flatten() {
        if (isNested()) return concatNestedIterables();
        throw new UnsupportedOperationException("List contains elements that are not Iterable");
    }

    private static <A, B, C> boolean allHaveNext(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return iterators.first.hasNext() && iterators.second.hasNext() && iterators.third.hasNext();
    }

    private static <A, B, C> Triplet<A, B, C> next(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return Triplet.of(iterators.first.next(), iterators.second.next(), iterators.third.next());
    }

    private static <A, B, C> IntIdeaList<Triplet<A, B, C>> zipWithHelper(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        if (!allHaveNext(iterators)) return IntIdeaList.empty();
        Triplet<A, B, C> elements = next(iterators);
        return IntIdeaList.create(
                Lazy.of(() -> elements),
                Lazy.of(() -> zipWithHelper(iterators))
        );
    }

    public <A, B> IntIdeaList<Triplet<E, A, B>> zipWith(Iterable<A> other, Iterable<B> other2) {
        return zipWithHelper(Triplet.of(iterator(), other.iterator(), other2.iterator()));
    }

    public <A> IntIdeaList<Pair<E, A>> zipWith(Iterable<A> other) {
        return zipWith(other, Enumerable.infiniteNulls()).map(Triplet::toPair);
    }

    public IntIdeaList<IndexElement<E>> withIndex() {
        return Range.infiniteIndices().zipWith(this).map(Pair::toIndexElement);
    }

    public IntIdeaList step(int length) {
        if (length < 1) throw new IllegalArgumentException("Length cannot be smaller than 1. Given: " + length);
        return whereIndexed((index, current) -> index % length == 0);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class NormalNode extends IntIdeaList {

        // Constructors and factory methods =============================================================
        private NormalNode(LazyInt value, Lazy<IntIdeaList> tail) {
            super(value, tail);
        }

        private IntIdeaList createTail(UnaryOperator<IntIdeaList> function) {
            return IntIdeaList.create(value, Lazy.of(() -> function.apply(tail.value())));
        }


        // Getters ======================================================================================
        */
/*@Override
        public E get(int index) {
            handleNegativeIndex(index);
            return index == 0 ? value.value() : tail.value().get(index - 1);
        }*//*


        @Override
        public int first() {
            return value.value();
        }

        @Override
        public E single() {
            if (tail.value().any()) throw new IllegalArgumentException("List has more than one element");
            return first();
        }

        @Override
        public int last() {
            return tail.value().isEmpty() ? value.value() : tail.value().last();
        }

        */
/*@Override
        public E findFirst(Predicate<E> predicate) {
            return predicate.test(value.value()) ? value.value() : tail.value().findFirst(predicate);
        }*//*


        @Override
        public Optional<E> findFirst(Predicate<E> predicate) {
            return predicate.test(value.value()) ? Optional.of(value.value()) : tail.value().findFirst(predicate);
        }

        @Override
        public IntIdeaList<Integer> indices() {
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
        public IntIdeaList insertAt(int index, Iterable<E> elements) {
            handleNegativeIndex(index);
            return index == 0 ? concat(elements, this) :
                    createTail(tail -> tail.insertAt(index - 1, elements));
        }

        @Override
        public IntIdeaList removeFirst(E element) {
            return Objects.equals(value.value(), element) ? tail.value() : createTail(tail -> tail.removeFirst(element));
        }

        @Override
        public IntIdeaList removeAt(int index) {
            handleNegativeIndex(index);
            return index == 0 ? tail.value() : createTail(tail -> tail.removeAt(index - 1));
        }


        // List operations ==============================================================================
        @Override
        public E reduce(BinaryOperator<E> operation) {
            return reduceHelper(first(), tail.value(), operation);
        }

        @Override
        public <R> IdeaList<R> map(IntFunction<R> transform) {
            return IdeaList.create(
                    Lazy.of(() -> transform.apply(value.value())),
                    Lazy.of(() -> tail.value().map(transform))
            );
        }

        @Override
        public IntIdeaList map(IntUnaryOperator transform) {
            return IntIdeaList.create(
                    LazyInt.of(() -> transform.applyAsInt(value.value())),
                    Lazy.of(() -> tail.value().map(transform))
            );
        }

        @Override
        public IntIdeaList where(Predicate<E> predicate) {
            return predicate.test(value.value()) ?
                    createTail(tail -> tail.where(predicate)) : tail.value().where(predicate);
        }

        @Override
        @SuppressWarnings("unchecked") // Cast is safe because isNested() first checks if list does in fact contain nested iterables
        <N> IntIdeaList<N> concatNestedIterables() {
            return concat((Iterable<N>) value.value(), Lazy.of(() -> tail.value().concatNestedIterables()));
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static class EndNode<E> extends IntIdeaList {

        // Constructors and factory methods =============================================================
        private EndNode() {
            super(null, null);
        }

        private static NoSuchElementException noSuchElementException() {
            return new NoSuchElementException("List is empty");
        }

        // Getters ======================================================================================
        @Override
        public int first() {
            throw noSuchElementException();
        }

        @Override
        public E single() {
            throw noSuchElementException();
        }

        @Override
        public int last() {
            throw noSuchElementException();
        }

        */
/*@Override
        public E findFirst(Predicate<E> predicate) {
            return null;
        }*//*


        @Override
        public Optional<E> findFirst(Predicate<E> predicate) {
            return Optional.empty();
        }

        @Override
        public IntIdeaList<Integer> indices() {
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
        public IntIdeaList insertAt(int index, Iterable<E> elements) {
            throw indexTooBigException();
        }

        @Override
        public IntIdeaList removeFirst(E element) {
            return this;
        }

        @Override
        public IntIdeaList removeAt(int index) {
            throw indexTooBigException();
        }


        // List operations ==============================================================================
        @Override
        public E reduce(BinaryOperator<E> operation) {
            throw new UnsupportedOperationException("Empty list cannot be reduced");
        }

        @Override
        public <R> IntIdeaList<R> map(IntFunction<R> transform) {
            return IntIdeaList.empty();
        }

        @Override
        public IntIdeaList map(IntUnaryOperator transform) {
            return IntIdeaList.empty();
        }

        @Override
        public IntIdeaList where(Predicate<E> predicate) {
            return this;
        }

        @Override
        <N> IntIdeaList<N> concatNestedIterables() {
            return IntIdeaList.empty();
        }
    }
}*/
