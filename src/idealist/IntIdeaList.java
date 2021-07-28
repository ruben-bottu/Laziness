package idealist;

import idealist.tuple.IntObjPair;
import idealist.primitive_specializations.PrimitiveEnumerator;
import idealist.tuple.Pair;

import java.util.*;
import java.util.function.*;

public abstract class IntIdeaList implements Iterable<Integer> {
    public final LazyInt value;
    public final Lazy<IntIdeaList> tail;

    protected IntIdeaList(LazyInt value, Lazy<IntIdeaList> tail) {
        this.value = value;
        this.tail = tail;
    }

    static IntIdeaList create(LazyInt value, Lazy<IntIdeaList> tail) {
        return new NormalNode(value, tail);
    }

    public static IntIdeaList empty() {
        return EndNode.EMPTY;
    }

    private static <L> IntIdeaList concat(PrimitiveIterator.OfInt iterator, L elements, Function<L, IntIdeaList> toIntIdeaList) {
        if (iterator.hasNext()) {
            int element = iterator.nextInt();
            return IntIdeaList.create(LazyInt.of(() -> element), Lazy.of(() -> concat(iterator, elements, toIntIdeaList)));
        }
        return toIntIdeaList.apply(elements);
    }

    private static IntIdeaList concat(Iterable<Integer> iterable, Lazy<IntIdeaList> elements) {
        return concat(PrimitiveEnumerator.ofInt(iterable), elements, Lazy::value);
    }

    private static IntIdeaList concat(Iterable<Integer> iterable, IntIdeaList elements) {
        return concat(PrimitiveEnumerator.ofInt(iterable), elements, Function.identity());
    }

    public static IntIdeaList of(Iterable<Integer> elements) {
        return concat(elements, IntIdeaList.empty());
    }

    public static IntIdeaList of(int... elements) {
        return concat(PrimitiveEnumerator.of(elements), IntIdeaList.empty(), Function.identity());
    }

    private static IntIdeaList rangeLength(int from, int length) {
        return length == 0 ? IntIdeaList.empty() : IntIdeaList.create(
                LazyInt.of(() -> from),
                Lazy.of(() -> rangeLength(from + 1, length - 1))
        );
    }

    private static void handleNegativeLength(int length) {
        if (length < 0) throw new IllegalArgumentException("Cannot initialise list with length " + length);
    }

    public static IntIdeaList initialiseWith(int length, IntUnaryOperator indexToElement) {
        handleNegativeLength(length);
        return rangeLength(0, length).map(indexToElement);
    }

    public static IntIdeaList initWith(int length, IntUnaryOperator indexToElement) {
        return initialiseWith(length, indexToElement);
    }

    public abstract int first();

    public List<Integer> toList() {
        return Enumerable.toList(this);
    }

    public int[] toArray() {
        int length = length();
        int[] intArray = new int[length];
        PrimitiveIterator.OfInt iterator = iterator();
        for (int i = 0; i < length; i++) {
            intArray[i] = iterator.nextInt();
        }
        return intArray;
    }

    @Override
    public PrimitiveIterator.OfInt iterator() {
        return PrimitiveEnumerator.of(this);
    }

    private static boolean isContentEqual(IntIdeaList iterable, IntIdeaList other) {
        PrimitiveIterator.OfInt it1 = iterable.iterator();
        PrimitiveIterator.OfInt it2 = other.iterator();
        while (true) {
            if (it1.hasNext() && !it2.hasNext() || !it1.hasNext() && it2.hasNext()) return false;
            if (!it1.hasNext()) return true;
            if (it1.nextInt() != it2.nextInt()) return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntIdeaList integers = (IntIdeaList) o;
        return isContentEqual(this, integers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, tail);
    }

    public abstract boolean any();

    @Override
    public void forEach(Consumer<? super Integer> action) {
        iterator().forEachRemaining(action);
    }

    abstract <A> A lazyReduceRight(A initialValue, BiFunction<LazyInt, Lazy<A>, A> operation);

    public <R> IdeaList<R> mapToObj(IntFunction<R> transform) {
        BiFunction<LazyInt, Lazy<IdeaList<R>>, IdeaList<R>> transformAndPrependElem = (elem, list) -> IdeaList.create(Lazy.of(() -> transform.apply(elem.value())), list);
        return lazyReduceRight(IdeaList.empty(), transformAndPrependElem);
    }

    public IntIdeaList map(IntUnaryOperator transform) {
        BiFunction<LazyInt, Lazy<IntIdeaList>, IntIdeaList> transformAndPrependElem = (elem, list) -> IntIdeaList.create(LazyInt.of(() -> transform.applyAsInt(elem.value())), list);
        return lazyReduceRight(IntIdeaList.empty(), transformAndPrependElem);
    }

    public IntIdeaList where(IntPredicate predicate) {
        BiFunction<LazyInt, Lazy<IntIdeaList>, IntIdeaList> prependElemIfMatch = (elem, list) -> predicate.test(elem.value()) ? IntIdeaList.create(elem, list) : list.value();
        return lazyReduceRight(IntIdeaList.empty(), prependElemIfMatch);
    }

    private static int reduceHelper(IntIdeaList list, int initialValue, IntBinaryOperator operation) {
        PrimitiveIterator.OfInt iterator = list.iterator();
        int accumulator = initialValue;
        while (iterator.hasNext()) {
            accumulator = operation.applyAsInt(accumulator, iterator.nextInt());
        }
        return accumulator;
    }

    public int reduce(int initialValue, IntBinaryOperator operation) {
        return reduceHelper(this, initialValue, operation);
    }

    public abstract int reduce(IntBinaryOperator operation);

    // TODO change to reduce(Integer::sum);
    // Throws exception when list is empty. When the list is empty
    // there is nothing to add up
    public int sum() {
        return reduce(0, Integer::sum);
    }

    public double average() {
        return (double) sum() / length();
    }

    /*public int count2(IntPredicate predicate) {
        return reduce(0, (accum, elem) -> predicate.test(elem) ? accum + 1 : accum);
    }*/

    public int count(IntPredicate predicate) {
        return map(elem -> predicate.test(elem) ? 1 : 0).sum();
    }

    public int length() {
        return count(__ -> true);
    }

    public int max() {
        return reduce(Math::max);
    }

    public int min() {
        return reduce(Math::min);
    }

    /*public <A> IdeaList<Pair<E, A>> zipWith(Iterable<A> other) {
        return Enumerable.zip(this, other);
    }*/

    /*private static <A, B> boolean allHaveNext(Pair<Iterator<A>, Iterator<B>> iterators) {
        return iterators.first.hasNext() && iterators.second.hasNext();
    }

    private static <A, B> Pair<A, B> next(Pair<Iterator<A>, Iterator<B>> iterators) {
        return Pair.of(iterators.first.next(), iterators.second.next());
    }

    private static <A, B> IdeaList<Pair<A, B>> zipHelper(Pair<Iterator<A>, Iterator<B>> iterators) {
        if (!allHaveNext(iterators)) return IdeaList.empty();
        Pair<A, B> elements = next(iterators);
        return IdeaList.create(Lazy.of(() -> elements), Lazy.of(() -> zipHelper(iterators)));
    }*/

    private static <A> boolean allHaveNext(Pair<PrimitiveIterator.OfInt, Iterator<A>> iterators) {
        return iterators.first.hasNext() && iterators.second.hasNext();
    }

    private static <A> IntObjPair<A> next(Pair<PrimitiveIterator.OfInt, Iterator<A>> iterators) {
        return IntObjPair.of(iterators.first.nextInt(), iterators.second.next());
    }

    private static <A> IdeaList<IntObjPair<A>> zipHelper(Pair<PrimitiveIterator.OfInt, Iterator<A>> iterators) {
        if (!allHaveNext(iterators)) return IdeaList.empty();
        IntObjPair<A> elements = next(iterators);
        return IdeaList.create(Lazy.of(() -> elements), Lazy.of(() -> zipHelper(iterators)));
    }

    public <A> IdeaList<IntObjPair<A>> zipWith(Iterable<A> other) {
        return zipHelper(Pair.of(iterator(), other.iterator()));
    }



    private static class NormalNode extends IntIdeaList {

        private NormalNode(LazyInt value, Lazy<IntIdeaList> tail) {
            super(value, tail);
        }

        @Override
        public int first() {
            return value.value();
        }

        @Override
        public boolean any() {
            return true;
        }

        @Override
        public int reduce(IntBinaryOperator operation) {
            return reduceHelper(tail.value(), first(), operation);
        }

        @Override
        <A> A lazyReduceRight(A initialValue, BiFunction<LazyInt, Lazy<A>, A> operation) {
            return operation.apply(value, Lazy.of(() -> tail.value().lazyReduceRight(initialValue, operation)));
        }
    }


    private static class EndNode extends IntIdeaList {
        private static final IntIdeaList EMPTY = new EndNode();

        private EndNode() {
            super(null, null);
        }

        private static NoSuchElementException noSuchElementException() {
            return new NoSuchElementException("List is empty");
        }

        @Override
        public int first() {
            throw noSuchElementException();
        }

        @Override
        public boolean any() {
            return false;
        }

        @Override
        public int reduce(IntBinaryOperator operation) {
            throw new UnsupportedOperationException("Empty list cannot be reduced");
        }

        @Override
        <A> A lazyReduceRight(A initialValue, BiFunction<LazyInt, Lazy<A>, A> operation) {
            return initialValue;
        }
    }
}
