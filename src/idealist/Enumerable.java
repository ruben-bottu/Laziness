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

    public static boolean isContentEqual(Iterable<?> iterable, Iterable<?> other) {
        var it1 = iterable.iterator();
        var it2 = other.iterator();
        while (true) {
            if (it1.hasNext() && !it2.hasNext() || !it1.hasNext() && it2.hasNext()) return false;
            if (!it1.hasNext()) return true;
            if (!Objects.equals(it1.next(), it2.next())) return false;
        }
    }

    // anyDoNotHaveNext
    // anyDontHaveNext
    private static <A, B, C> boolean allHaveNext(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return iterators.first.hasNext() && iterators.second.hasNext() && iterators.third.hasNext();
    }

    private static <A, B, C> Triplet<A, B, C> next(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return Triplet.of(iterators.first.next(), iterators.second.next(), iterators.third.next());
    }

    private static <A, B, C> IdeaList<Triplet<A, B, C>> zipHelper(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        if (!allHaveNext(iterators)) return IdeaList.empty();
        var elements = next(iterators);
        return IdeaList.create(Lazy.of(() -> elements), Lazy.of(() -> zipHelper(iterators)));
    }

    public static <A, B, C> IdeaList<Triplet<A, B, C>> zip(Iterable<A> first, Iterable<B> second, Iterable<C> third) {
        return zipHelper(Triplet.of(first.iterator(), second.iterator(), third.iterator()));
    }

    public static <A, B> IdeaList<Pair<A, B>> zip(Iterable<A> first, Iterable<B> second) {
        return zip(first, second, Enumerable.infiniteNulls()).map(Triplet::toPair);
    }

    private static <A, B> boolean allHaveNext(Pair<Iterator<A>, Iterator<B>> iterators) {
        return iterators.first.hasNext() && iterators.second.hasNext();
    }

    private static <A, B> Pair<A, B> next(Pair<Iterator<A>, Iterator<B>> iterators) {
        return Pair.of(iterators.first.next(), iterators.second.next());
    }

    private static <A, B> IdeaList<Pair<A, B>> zipHelper(Pair<Iterator<A>, Iterator<B>> iterators) {
        if (!allHaveNext(iterators)) return IdeaList.empty();
        var elements = next(iterators);
        return IdeaList.create(Lazy.of(() -> elements), Lazy.of(() -> zipHelper(iterators)));
    }

    // TODO benchmark
    public static <A, B> IdeaList<Pair<A, B>> zipDirectlyImplemented(Iterable<A> first, Iterable<B> second) {
        return zipHelper(Pair.of(first.iterator(), second.iterator()));
    }

    public static <E> IdeaList<IndexElement<E>> withIndex(Iterable<E> iterable) {
        return zip(Range.infiniteIndices(), iterable).map(Pair::toIndexElement);
    }

    /*public static <E> IdeaList<IndexElement<E>> withIndexPrimitive(Iterable<E> iterable) {
        return zip(Range.infiniteIndices(), iterable).map(Pair::toIndexElement);
    }*/

    private static <A, E> A reduceHelper(A initialValue, BiFunction<A, E, A> operation, Iterator<E> iterator) {
        A accumulator = initialValue;
        while (iterator.hasNext()) accumulator = operation.apply(accumulator, iterator.next());
        return accumulator;
    }

    public static <A, E> A reduce(A initialValue, BiFunction<A, E, A> operation, Iterable<E> iterable) {
        return reduceHelper(initialValue, operation, iterable.iterator());
    }

    public static <A, E> A reduceIndexed(A initialValue, TriFunction<Integer, A, E, A> operation, Iterable<E> iterable) {
        var index = new AtomicInteger();
        return reduce(initialValue, (accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), iterable);
    }

    public static <E, A> A reduce(BiFunction<A, E, A> operation, Function<E, A> transformFirst, Iterable<E> iterable) {
        var iterator = iterable.iterator();
        if (!iterator.hasNext()) throw new UnsupportedOperationException("Empty list cannot be reduced");
        return reduceHelper(transformFirst.apply(iterator.next()), operation, iterator);
    }

    public static <E, A> A reduceT2(Function<E, A> transformFirst, BiFunction<A, E, A> operation, Iterable<E> iterable) {
        var iterator = iterable.iterator();
        if (!iterator.hasNext()) throw new UnsupportedOperationException("Empty list cannot be reduced");
        return reduceHelper(transformFirst.apply(iterator.next()), operation, iterator);
    }

    public static <A, E> A reduceIndexed(TriFunction<Integer, A, E, A> operation, Function<E, A> transformFirst, Iterable<E> iterable) {
        var index = new AtomicInteger();
        return reduce((accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), transformFirst, iterable);
    }

    // TODO benchmark
    public static <A, E> A reduceIndexedSpecialization(IndexedBiFunction<A, E, A> operation, Function<E, A> transformFirst, Iterable<E> iterable) {
        var index = new AtomicInteger();
        return reduce((accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), transformFirst, iterable);
    }

    public static <E> E reduce(BinaryOperator<E> operation, Iterable<E> iterable) {
        return reduce(operation, Function.identity(), iterable);
    }

    public static <E> E reduceIndexed(TriFunction<Integer, E, E, E> operation, Iterable<E> iterable) {
        var index = new AtomicInteger();
        return reduce((accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), iterable);
    }

    private static <A, E> A reduceRightHelper(A initialValue, BiFunction<E, A, A> operation, Iterator<E> iterator) {
        return !iterator.hasNext() ? initialValue : operation.apply(iterator.next(), reduceRightHelper(initialValue, operation, iterator));
    }

    public static <A, E> A reduceRight(A initialValue, BiFunction<E, A, A> operation, Iterable<E> iterable) {
        return reduceRightHelper(initialValue, operation, iterable.iterator());
    }

    public static <A, E> A reduceRightIndexed(A initialValue, TriFunction<Integer, E, A, A> operation, Iterable<E> iterable) {
        var index = new AtomicInteger();
        return reduceRight(initialValue, (accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), iterable);
    }

    private static <A, E> A reduceRightHelper(BiFunction<E, A, A> operation, Function<E, A> transformLast, Iterator<E> iterator) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return !iterator.hasNext() ? transformLast.apply(element) : operation.apply(element, reduceRightHelper(operation, transformLast, iterator));
        }
        throw new UnsupportedOperationException("Empty list cannot be reduced");
    }

    public static <A, E> A reduceRight(BiFunction<E, A, A> operation, Function<E, A> transformLast, Iterable<E> iterable) {
        return reduceRightHelper(operation, transformLast, iterable.iterator());
    }

    public static <A, E> A reduceRightIndexed(TriFunction<Integer, E, A, A> operation, Function<E, A> transformLast, Iterable<E> iterable) {
        var index = new AtomicInteger();
        return reduceRight((elem, accum) -> operation.apply(index.getAndIncrement(), elem, accum), transformLast, iterable);
    }

    public static <E> E reduceRight(BinaryOperator<E> operation, Iterable<E> iterable) {
        return reduceRight(operation, Function.identity(), iterable);
    }

    public static <E> E reduceRightIndexed(TriFunction<Integer, E, E, E> operation, Iterable<E> iterable) {
        var index = new AtomicInteger();
        return reduceRight((elem, accum) -> operation.apply(index.getAndIncrement(), elem, accum), iterable);
    }

    public static int sum(Iterable<Integer> iterable) {
        return reduce(0, Integer::sum, iterable);
    }

    public static double sumDouble(Iterable<Double> iterable) {
        return reduce(0.0d, Double::sum, iterable);
    }

    public static <E> boolean all(Predicate<E> predicate, Iterable<E> iterable) {
        for (E element : iterable) {
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

    public static <E> boolean all2(Predicate<E> predicate, Iterable<E> iterable) {
        return findFirst(predicate.negate(), iterable).isEmpty();
    }

    /*public static <E> boolean all3(Iterable<E> iterable, Predicate<E> predicate) {
        return isEmpty( findFirst(iterable, predicate.negate()) );
    }*/

    public static <E> boolean any(Predicate<E> predicate, Iterable<E> iterable) {
        for (E element : iterable) {
            if (predicate.test(element)) return true;
        }
        return false;
    }

    public static <E> boolean any2(Predicate<E> predicate, Iterable<E> iterable) {
        return findFirst(predicate, iterable).isPresent();
    }

    // TODO benchmark
    public static <E> Optional<E> findFirst(Predicate<E> predicate, Iterable<E> iterable) {
        for (E element : iterable) {
            if (predicate.test(element)) return Optional.of(element);
        }
        return Optional.empty();
    }

    private static <E> E first(Iterator<E> iterator) {
        if (iterator.hasNext()) return iterator.next();
        throw new NoSuchElementException("Iterable is empty");
    }

    // TODO benchmark
    public static <E> E first(Iterable<E> iterable) {
        return first(iterable.iterator());
    }

    // TODO benchmark
    public static <E> E single(Iterable<E> iterable) {
        var iterator = iterable.iterator();
        E single = first(iterator);
        if (iterator.hasNext()) throw new IllegalArgumentException("Iterable has more than one element");
        return single;
    }

    // TODO benchmark
    public static <E> E last(Iterable<E> iterable) {
        var iterator = iterable.iterator();
        if (!iterator.hasNext()) throw new NoSuchElementException("Iterable is empty");
        E last = iterator.next();
        while (iterator.hasNext()) {
            last = iterator.next();
        }
        return last;
    }

    /*public <E> OptionalInt indexOfFirst2(Iterable<E> iterable, Predicate<E> predicate) {
        AtomicInteger index = new AtomicInteger(0);
        return findFirst(iterable, elem -> {
            if (predicate.test(elem)) {
                return true;
            } else {
                index.getAndIncrement();
                return false;
            }
        } ).map(elem -> OptionalInt.of(index.intValue())).orElseGet(OptionalInt::empty);
    }*/

    /*public <E> OptionalInt indexOfFirst2(Iterable<E> iterable, Predicate<E> predicate) {
        AtomicInteger index = new AtomicInteger(-1);
        Predicate<E> incrementAndTest = elem -> {
            index.getAndIncrement();
            return predicate.test(elem);
        };
        return findFirst(iterable, incrementAndTest)
                .map(elem -> OptionalInt.of(index.intValue()))
                .orElseGet(OptionalInt::empty);
    }*/

    // TODO benchmark
    public static <E> OptionalInt indexOfFirst(Predicate<E> predicate, Iterable<E> iterable) {
        int index = 0;
        for (E element : iterable) {
            if (predicate.test(element)) return OptionalInt.of(index);
            index++;
        }
        return OptionalInt.empty();
    }

    /*public <E> E lazyGet(Iterable<Lazy<E>> iterable, int index) {
        int count = 0;
        for (Lazy<E> element : iterable) {
            if (index == count++) return element.value();
        }
        throw new IndexOutOfBoundsException("Index too big");
    }*/

    private static IndexOutOfBoundsException indexOutOfBoundsException(int index, int length) {
        return new IndexOutOfBoundsException("Index "+index+" out of bounds for length "+length);
    }

    // TODO benchmark
    public static <E> E get(int index, Iterable<E> iterable) {
        int count = 0;
        for (E element : iterable) {
            if (index == count++) return element;
        }
        throw indexOutOfBoundsException(index, count);
    }

    public static <E> E get2(int index, Iterable<E> iterable) {
        var count = new AtomicInteger();
        return findFirst(__ -> index == count.getAndIncrement(), iterable)
                .orElseThrow(() -> indexOutOfBoundsException(index, count.get()));
    }

    public static <E> OptionalInt indexOfFirst(@Nullable E element, Iterable<E> iterable) {
        return indexOfFirst(isEqual(element), iterable);
    }

    // TODO benchmark
    public static <E> boolean contains(@Nullable E element, Iterable<E> iterable) {
        return indexOfFirst(element, iterable).isPresent();
    }

    /*public boolean containsAll(Iterable<E> elements) {
        return IdeaList.of(elements).all(this::contains);
    }*/

    // TODO benchmark
    public static <E> boolean containsAll(Iterable<E> elements, Iterable<E> iterable) {
        return all(elem -> contains(elem, iterable), elements);
    }

    // TODO benchmark
    public static <E> boolean isEmpty(Iterable<E> iterable) {
        return none(iterable);
    }

    // TODO benchmark
    public static <E> boolean any(Iterable<E> iterable) {
        return iterable.iterator().hasNext();
    }

    // TODO benchmark
    public static <E> boolean none(Iterable<E> iterable) {
        return !any(iterable);
    }

    public static <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
