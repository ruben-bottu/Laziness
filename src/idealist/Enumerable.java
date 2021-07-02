package idealist;

import idealist.function.IndexedBiFunction;
import idealist.function.TriFunction;
import idealist.range.Range;
import idealist.tuple.IndexElement;
import idealist.tuple.Pair;
import idealist.tuple.Triplet;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.function.Predicate.isEqual;

// Iterable would be a better name, but Java doesn't have type aliasing
public final class Enumerable {

    // Private constructor to prevent instantiation
    private Enumerable() {}

    public static Iterable<Void> infiniteNulls() {
        return Enumerator::infiniteNulls;
    }

    public static boolean isContentEqual(Iterable<?> left, Iterable<?> right) {
        var it1 = left.iterator();
        var it2 = right.iterator();
        while (true) {
            if (it1.hasNext() && !it2.hasNext() || !it1.hasNext() && it2.hasNext()) return false;
            if (!it1.hasNext()) return true;
            if (!Objects.equals(it1.next(), it2.next())) return false;
        }
    }

    /*private static <A, B, C> boolean allHaveNext(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return iterators.first.hasNext() && iterators.second.hasNext() && iterators.third.hasNext();
    }*/

    private static <A, B, C> boolean anyIsEmpty(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return !iterators.first.hasNext() || !iterators.second.hasNext() || !iterators.third.hasNext();
    }

    private static <A, B, C> Triplet<A, B, C> next(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return Triplet.of(iterators.first.next(), iterators.second.next(), iterators.third.next());
    }

    /*private static <A, B, C> IdeaList<Triplet<A, B, C>> zipHelper(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        if (!allHaveNext(iterators)) return IdeaList.empty();
        var elements = next(iterators);
        return IdeaList.create(Lazy.of(() -> elements), Lazy.of(() -> zipHelper(iterators)));
    }*/

    private static <A, B, C> IdeaList<Triplet<A, B, C>> zipHelper(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        if (anyIsEmpty(iterators)) return IdeaList.empty();
        var elements = next(iterators);
        return IdeaList.create(Lazy.of(() -> elements), Lazy.of(() -> zipHelper(iterators)));
    }

    public static <A, B, C> IdeaList<Triplet<A, B, C>> zip(Iterable<A> left, Iterable<B> middle, Iterable<C> right) {
        return zipHelper(Triplet.of(left.iterator(), middle.iterator(), right.iterator()));
    }

    public static <A, B> IdeaList<Pair<A, B>> zip(Iterable<A> left, Iterable<B> right) {
        return zip(left, right, Enumerable.infiniteNulls()).map(Triplet::toPair);
    }

    private static <A, B> boolean anyIsEmpty(Pair<Iterator<A>, Iterator<B>> iterators) {
        return !iterators.first.hasNext() || !iterators.second.hasNext();
    }

    private static <A, B> Pair<A, B> next(Pair<Iterator<A>, Iterator<B>> iterators) {
        return Pair.of(iterators.first.next(), iterators.second.next());
    }

    private static <A, B> IdeaList<Pair<A, B>> zipHelper(Pair<Iterator<A>, Iterator<B>> iterators) {
        if (anyIsEmpty(iterators)) return IdeaList.empty();
        var elements = next(iterators);
        return IdeaList.create(Lazy.of(() -> elements), Lazy.of(() -> zipHelper(iterators)));
    }

    // TODO benchmark
    public static <A, B> IdeaList<Pair<A, B>> zipDirectlyImplemented(Iterable<A> left, Iterable<B> right) {
        return zipHelper(Pair.of(left.iterator(), right.iterator()));
    }

    /*public static <E> IdeaList<IndexElement<E>> withIndex(Iterable<E> iterable) {
        return zip(Range.infiniteIndices(), iterable).map(Pair::toIndexElement);
    }*/

    /*public static <E> IdeaList<IndexElement<E>> withIndexPrimitive(Iterable<E> iterable) {
        return zip(Range.infiniteIndices(), iterable).map(Pair::toIndexElement);
    }*/

    private static <A, E> A reduceHelper(A initialValue, BiFunction<A, E, A> operation, Iterator<E> iterator) {
        A accumulator = initialValue;
        while (iterator.hasNext()) accumulator = operation.apply(accumulator, iterator.next());
        return accumulator;
    }

    public static <A, E> A reduce(A initialValue, BiFunction<A, E, A> operation, Iterable<E> elements) {
        return reduceHelper(initialValue, operation, elements.iterator());
    }

    public static <A, E> A reduceIndexed(A initialValue, TriFunction<Integer, A, E, A> operation, Iterable<E> elements) {
        var index = new AtomicInteger();
        return reduce(initialValue, (accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), elements);
    }

    public static <E, A> A reduce(BiFunction<A, E, A> operation, Function<E, A> transformFirst, Iterable<E> elements) {
        var iterator = elements.iterator();
        if (!iterator.hasNext()) throw new UnsupportedOperationException("Empty list cannot be reduced");
        return reduceHelper(transformFirst.apply(iterator.next()), operation, iterator);
    }

    public static <E, A> A reduceT(Function<E, A> transformFirst, BiFunction<A, E, A> operation, Iterable<E> elements) {
        var iterator = elements.iterator();
        if (!iterator.hasNext()) throw new UnsupportedOperationException("Empty list cannot be reduced");
        return reduceHelper(transformFirst.apply(iterator.next()), operation, iterator);
    }

    public static <A, E> A reduceIndexed(TriFunction<Integer, A, E, A> operation, Function<E, A> transformFirst, Iterable<E> elements) {
        var index = new AtomicInteger();
        return reduce((accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), transformFirst, elements);
    }

    // TODO benchmark
    public static <A, E> A reduceIndexedSpecialization(IndexedBiFunction<A, E, A> operation, Function<E, A> transformFirst, Iterable<E> elements) {
        var index = new AtomicInteger();
        return reduce((accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), transformFirst, elements);
    }

    public static <E> E reduce(BinaryOperator<E> operation, Iterable<E> elements) {
        return reduce(operation, Function.identity(), elements);
    }

    /*public static <E> E reduceIndexed(TriFunction<Integer, E, E, E> operation, Iterable<E> iterable) {
        var index = new AtomicInteger();
        return reduce((accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), iterable);
    }*/

    public static <E> E reduceIndexed(TriFunction<Integer, E, E, E> operation, Iterable<E> elements) {
        return reduceIndexed(operation, Function.identity(), elements);
    }

    private static <A, E> A reduceRightHelper(A initialValue, BiFunction<E, A, A> operation, Iterator<E> iterator) {
        return !iterator.hasNext() ? initialValue : operation.apply(iterator.next(), reduceRightHelper(initialValue, operation, iterator));
    }

    public static <A, E> A reduceRight(A initialValue, BiFunction<E, A, A> operation, Iterable<E> elements) {
        return reduceRightHelper(initialValue, operation, elements.iterator());
    }

    public static <A, E> A reduceRightIndexed(A initialValue, TriFunction<Integer, E, A, A> operation, Iterable<E> elements) {
        var index = new AtomicInteger();
        return reduceRight(initialValue, (accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), elements);
    }

    private static <A, E> A reduceRightHelper(BiFunction<E, A, A> operation, Function<E, A> transformLast, Iterator<E> iterator) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return !iterator.hasNext() ? transformLast.apply(element) : operation.apply(element, reduceRightHelper(operation, transformLast, iterator));
        }
        throw new UnsupportedOperationException("Empty list cannot be reduced");
    }

    public static <A, E> A reduceRight(BiFunction<E, A, A> operation, Function<E, A> transformLast, Iterable<E> elements) {
        return reduceRightHelper(operation, transformLast, elements.iterator());
    }

    public static <A, E> A reduceRightIndexed(TriFunction<Integer, E, A, A> operation, Function<E, A> transformLast, Iterable<E> elements) {
        var index = new AtomicInteger();
        return reduceRight((elem, accum) -> operation.apply(index.getAndIncrement(), elem, accum), transformLast, elements);
    }

    public static <E> E reduceRight(BinaryOperator<E> operation, Iterable<E> elements) {
        return reduceRight(operation, Function.identity(), elements);
    }

    /*public static <E> E reduceRightIndexed(TriFunction<Integer, E, E, E> operation, Iterable<E> iterable) {
        var index = new AtomicInteger();
        return reduceRight((elem, accum) -> operation.apply(index.getAndIncrement(), elem, accum), iterable);
    }*/

    public static <E> E reduceRightIndexed(TriFunction<Integer, E, E, E> operation, Iterable<E> elements) {
        return reduceIndexed(operation, Function.identity(), elements);
    }

    /*public static int sum(Iterable<Integer> elements) {
        return reduce(0, Integer::sum, elements);
    }

    public static double sumDouble(Iterable<Double> elements) {
        return reduce(0.0d, Double::sum, elements);
    }*/

    public static <E> boolean all(Predicate<E> predicate, Iterable<E> elements) {
        for (E element : elements) {
            if (!predicate.test(element)) return false;
        }
        return true;
    }

    /*private static <E> boolean isEmpty(Optional<E> optional) {
        return !optional.isPresent();
    }*/

    // TODO benchmark
    /*public static <E> boolean all2(Iterable<E> iterable, Predicate<E> predicate) {
        return ! findFirst(iterable, predicate.negate()).isPresent(); // isEmpty()
    }*/

    public static <E> boolean all2(Predicate<E> predicate, Iterable<E> elements) {
        return findFirst(predicate.negate(), elements).isEmpty();
    }

    /*public static <E> boolean all3(Iterable<E> iterable, Predicate<E> predicate) {
        return isEmpty( findFirst(iterable, predicate.negate()) );
    }*/

    public static <E> boolean any(Predicate<E> predicate, Iterable<E> elements) {
        for (E element : elements) {
            if (predicate.test(element)) return true;
        }
        return false;
    }

    public static <E> boolean any2(Predicate<E> predicate, Iterable<E> elements) {
        return findFirst(predicate, elements).isPresent();
    }

    // TODO benchmark
    public static <E> Optional<E> findFirst(Predicate<E> predicate, Iterable<E> elements) {
        for (E element : elements) {
            if (predicate.test(element)) return Optional.of(element);
        }
        return Optional.empty();
    }

    /*private static <E> E first(Iterator<E> iterator) {
        if (iterator.hasNext()) return iterator.next();
        throw new NoSuchElementException("Iterable is empty");
    }

    // TODO benchmark
    public static <E> E first(Iterable<E> elements) {
        return first(elements.iterator());
    }

    // TODO benchmark
    public static <E> E single(Iterable<E> elements) {
        var iterator = elements.iterator();
        E single = first(iterator);
        if (iterator.hasNext()) throw new IllegalArgumentException("Iterable has more than one element");
        return single;
    }

    // TODO benchmark
    public static <E> E last(Iterable<E> elements) {
        var iterator = elements.iterator();
        if (!iterator.hasNext()) throw new NoSuchElementException("Iterable is empty");
        E last = iterator.next();
        while (iterator.hasNext()) {
            last = iterator.next();
        }
        return last;
    }*/

    /*public <E> OptionalInt indexOfFirst2(Iterable<E> elements, Predicate<E> predicate) {
        AtomicInteger index = new AtomicInteger(0);
        return findFirst(elements, elem -> {
            if (predicate.test(elem)) {
                return true;
            } else {
                index.getAndIncrement();
                return false;
            }
        } ).map(elem -> OptionalInt.of(index.intValue())).orElseGet(OptionalInt::empty);
    }*/

    /*public <E> OptionalInt indexOfFirst2(Iterable<E> elements, Predicate<E> predicate) {
        AtomicInteger index = new AtomicInteger(-1);
        Predicate<E> incrementAndTest = elem -> {
            index.getAndIncrement();
            return predicate.test(elem);
        };
        return findFirst(elements, incrementAndTest)
                .map(elem -> OptionalInt.of(index.intValue()))
                .orElseGet(OptionalInt::empty);
    }*/

    // TODO benchmark
    public static <E> OptionalInt indexOfFirst(Predicate<E> predicate, Iterable<E> elements) {
        int index = 0;
        for (E element : elements) {
            if (predicate.test(element)) return OptionalInt.of(index);
            index++;
        }
        return OptionalInt.empty();
    }

    public static <E> OptionalInt indexOfFirst(@Nullable E element, Iterable<E> elements) {
        return indexOfFirst(isEqual(element), elements);
    }

    /*public <E> E lazyGet(Iterable<Lazy<E>> elements, int index) {
        int count = 0;
        for (Lazy<E> element : elements) {
            if (index == count++) return element.value();
        }
        throw new IndexOutOfBoundsException("Index too big");
    }*/

    private static IndexOutOfBoundsException indexOutOfBoundsException(int index, int length) {
        return new IndexOutOfBoundsException("Index "+index+" out of bounds for length "+length);
    }

    // TODO benchmark
    public static <E> E get(int index, Iterable<E> elements) {
        int count = 0;
        for (E element : elements) {
            if (index == count++) return element;
        }
        throw indexOutOfBoundsException(index, count);
    }

    /*public static <E> E get2(int index, Iterable<E> elements) {
        var count = new AtomicInteger();
        return findFirst(__ -> index == count.getAndIncrement(), elements)
                .orElseThrow(() -> indexOutOfBoundsException(index, count.get()));
    }*/

    /*// TODO benchmark
    public static <E> boolean contains(@Nullable E element, Iterable<E> iterable) {
        return indexOfFirst(element, iterable).isPresent();
    }*/

    /*public boolean containsAll(Iterable<E> elements) {
        return IdeaList.of(elements).all(this::contains);
    }*/

    /*// TODO benchmark
    public static <E> boolean containsAll(Iterable<E> elements, Iterable<E> iterable) {
        return all(elem -> contains(elem, iterable), elements);
    }*/

    /*// TODO benchmark
    public static <E> boolean isEmpty(Iterable<E> elements) {
        return none(elements);
    }

    // TODO benchmark
    public static <E> boolean any(Iterable<E> elements) {
        return elements.iterator().hasNext();
    }

    // TODO benchmark
    public static <E> boolean none(Iterable<E> elements) {
        return !any(elements);
    }*/

    public static <E> List<E> toList(Iterable<E> elements) {
        List<E> list = new ArrayList<>();
        elements.forEach(list::add);
        return list;
    }
}
