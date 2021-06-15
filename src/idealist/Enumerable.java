package idealist;

import idealist.function.IndexedBiFunction;
import idealist.function.TriFunction;

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
        Iterator<?> it1 = iterable.iterator();
        Iterator<?> it2 = other.iterator();
        while (true) {
            if (it1.hasNext() && !it2.hasNext() || !it1.hasNext() && it2.hasNext()) return false;
            if (!it1.hasNext()) return true;
            if (!Objects.equals(it1.next(), it2.next())) return false;
        }
    }

    private static <A, B, C> boolean allHaveNext(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return iterators.first.hasNext() && iterators.second.hasNext() && iterators.third.hasNext();
    }

    private static <A, B, C> Triplet<A, B, C> next(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return Triplet.of(iterators.first.next(), iterators.second.next(), iterators.third.next());
    }

    private static <A, B, C> IdeaList<Triplet<A, B, C>> zipHelper(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        if (!allHaveNext(iterators)) return IdeaList.empty();
        Triplet<A, B, C> elements = next(iterators);
        return IdeaList.create(Lazy.of(() -> elements), Lazy.of(() -> zipHelper(iterators)));
    }

    public static <A, B, C> IdeaList<Triplet<A, B, C>> zip(Iterable<A> aIterable, Iterable<B> bIterable, Iterable<C> cIterable) {
        return zipHelper(Triplet.of(aIterable.iterator(), bIterable.iterator(), cIterable.iterator()));
    }

    public static <A, B> IdeaList<Pair<A, B>> zip(Iterable<A> aIterable, Iterable<B> bIterable) {
        return zip(aIterable, bIterable, Enumerable.infiniteNulls()).map(Triplet::toPair);
    }

    private static <A, B> boolean allHaveNext(Pair<Iterator<A>, Iterator<B>> iterators) {
        return iterators.first.hasNext() && iterators.second.hasNext();
    }

    private static <A, B> Pair<A, B> next(Pair<Iterator<A>, Iterator<B>> iterators) {
        return Pair.of(iterators.first.next(), iterators.second.next());
    }

    private static <A, B> IdeaList<Pair<A, B>> zipHelper(Pair<Iterator<A>, Iterator<B>> iterators) {
        if (!allHaveNext(iterators)) return IdeaList.empty();
        Pair<A, B> elements = next(iterators);
        return IdeaList.create(Lazy.of(() -> elements), Lazy.of(() -> zipHelper(iterators)));
    }

    // TODO benchmark
    public static <A, B> IdeaList<Pair<A, B>> zipDirectlyImplemented(Iterable<A> aIterable, Iterable<B> bIterable) {
        return zipHelper(Pair.of(aIterable.iterator(), bIterable.iterator()));
    }

    public static <E> IdeaList<IndexElement<E>> withIndex(Iterable<E> iterable) {
        return zip(Range.infiniteIndices(), iterable).map(Pair::toIndexElement);
    }

    /*public static <E> IdeaList<IndexElement<E>> withIndexPrimitive(Iterable<E> iterable) {
        return zip(Range.infiniteIndices(), iterable).map(Pair::toIndexElement);
    }*/

    private static <A, E> A reduceHelper(Iterator<E> iterator, A initialValue, BiFunction<A, E, A> operation) {
        A accumulator = initialValue;
        while (iterator.hasNext()) accumulator = operation.apply(accumulator, iterator.next());
        return accumulator;
    }

    public static <A, E> A reduce(Iterable<E> iterable, A initialValue, BiFunction<A, E, A> operation) {
        return reduceHelper(iterable.iterator(), initialValue, operation);
    }

    public static <A, E> A reduceIndexed(Iterable<E> iterable, A initialValue, TriFunction<Integer, A, E, A> operation) {
        AtomicInteger index = new AtomicInteger();
        return reduce(iterable, initialValue, (accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem));
    }

    public static <E, A> A reduce(Iterable<E> iterable, BiFunction<A, E, A> operation, Function<E, A> transformFirst) {
        Iterator<E> iterator = iterable.iterator();
        if (!iterator.hasNext()) throw new UnsupportedOperationException("Empty list cannot be reduced");
        return reduceHelper(iterator, transformFirst.apply(iterator.next()), operation);
    }

    public static <A, E> A reduceIndexed(Iterable<E> iterable, TriFunction<Integer, A, E, A> operation, Function<E, A> transformFirst) {
        AtomicInteger index = new AtomicInteger();
        return reduce(iterable, (accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), transformFirst);
    }

    // TODO benchmark
    public static <A, E> A reduceIndexedSpecialization(Iterable<E> iterable, IndexedBiFunction<A, E, A> operation, Function<E, A> transformFirst) {
        AtomicInteger index = new AtomicInteger();
        return reduce(iterable, (accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), transformFirst);
    }

    public static <E> E reduce(Iterable<E> iterable, BinaryOperator<E> operation) {
        return reduce(iterable, operation, Function.identity());
    }

    public static <E> E reduceIndexed(Iterable<E> iterable, TriFunction<Integer, E, E, E> operation) {
        AtomicInteger index = new AtomicInteger();
        return reduce(iterable, (accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem));
    }

    private static <A, E> A reduceRightHelper(Iterator<E> iterator, A initialValue, BiFunction<E, A, A> operation) {
        return !iterator.hasNext() ? initialValue : operation.apply(iterator.next(), reduceRightHelper(iterator, initialValue, operation));
    }

    public static <A, E> A reduceRight(Iterable<E> iterable, A initialValue, BiFunction<E, A, A> operation) {
        return reduceRightHelper(iterable.iterator(), initialValue, operation);
    }

    public static <A, E> A reduceRightIndexed(Iterable<E> iterable, A initialValue, TriFunction<Integer, E, A, A> operation) {
        AtomicInteger index = new AtomicInteger();
        return reduceRight(iterable, initialValue, (accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem));
    }

    private static <A, E> A reduceRightHelper(Iterator<E> iterator, BiFunction<E, A, A> operation, Function<E, A> transformLast) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return !iterator.hasNext() ? transformLast.apply(element) : operation.apply(element, reduceRightHelper(iterator, operation, transformLast));
        }
        throw new UnsupportedOperationException("Empty list cannot be reduced");
    }

    public static <A, E> A reduceRight(Iterable<E> iterable, BiFunction<E, A, A> operation, Function<E, A> transformLast) {
        return reduceRightHelper(iterable.iterator(), operation, transformLast);
    }

    public static <A, E> A reduceRightIndexed(Iterable<E> iterable, TriFunction<Integer, E, A, A> operation, Function<E, A> transformLast) {
        AtomicInteger index = new AtomicInteger();
        return reduceRight(iterable, (elem, accum) -> operation.apply(index.getAndIncrement(), elem, accum), transformLast);
    }

    public static <E> E reduceRight(Iterable<E> iterable, BinaryOperator<E> operation) {
        return reduceRight(iterable, operation, Function.identity());
    }

    public static <E> E reduceRightIndexed(Iterable<E> iterable, TriFunction<Integer, E, E, E> operation) {
        AtomicInteger index = new AtomicInteger();
        return reduceRight(iterable, (elem, accum) -> operation.apply(index.getAndIncrement(), elem, accum));
    }

    public static int sum(Iterable<Integer> iterable) {
        return reduce(iterable, 0, Integer::sum);
    }

    public static double sumDouble(Iterable<Double> iterable) {
        return reduce(iterable, 0.0d, Double::sum);
    }

    public static <E> boolean all(Iterable<E> iterable, Predicate<E> predicate) {
        for (E element : iterable) {
            if (!predicate.test(element)) return false;
        }
        return true;
    }

    private static <E> boolean isEmpty(Optional<E> optional) {
        return !optional.isPresent();
    }

    // TODO benchmark
    public static <E> boolean all2(Iterable<E> iterable, Predicate<E> predicate) {
        return ! findFirst(iterable, predicate.negate()).isPresent(); // isEmpty()
    }

    public static <E> boolean all3(Iterable<E> iterable, Predicate<E> predicate) {
        return isEmpty( findFirst(iterable, predicate.negate()) );
    }

    public static <E> boolean any(Iterable<E> iterable, Predicate<E> predicate) {
        for (E element : iterable) {
            if (predicate.test(element)) return true;
        }
        return false;
    }

    public static <E> boolean any2(Iterable<E> iterable, Predicate<E> predicate) {
        return findFirst(iterable, predicate).isPresent();
    }

    // TODO benchmark
    public static <E> Optional<E> findFirst(Iterable<E> iterable, Predicate<E> predicate) {
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
        Iterator<E> iterator = iterable.iterator();
        E single = first(iterator);
        if (iterator.hasNext()) throw new IllegalArgumentException("Iterable has more than one element");
        return single;
    }

    // TODO benchmark
    public static <E> E last(Iterable<E> iterable) {
        Iterator<E> iterator = iterable.iterator();
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
    public static <E> OptionalInt indexOfFirst(Iterable<E> iterable, Predicate<E> predicate) {
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
    public static <E> E get(Iterable<E> iterable, int index) {
        int count = 0;
        for (E element : iterable) {
            if (index == count++) return element;
        }
        throw indexOutOfBoundsException(index, count);
    }

    public static <E> E get2(Iterable<E> iterable, int index) {
        AtomicInteger count = new AtomicInteger();
        return findFirst(iterable, __ -> index == count.getAndIncrement())
                .orElseThrow(() -> indexOutOfBoundsException(index, count.get()));
    }

    public static <E> OptionalInt indexOfFirst(Iterable<E> iterable, @Nullable E element) {
        return indexOfFirst(iterable, isEqual(element));
    }

    // TODO benchmark
    public static <E> boolean contains(Iterable<E> iterable, @Nullable E element) {
        return indexOfFirst(iterable, element).isPresent();
    }

    /*public boolean containsAll(Iterable<E> elements) {
        return IdeaList.of(elements).all(this::contains);
    }*/

    // TODO benchmark
    public static <E> boolean containsAll(Iterable<E> iterable, Iterable<E> elements) {
        return all(elements, elem -> contains(iterable, elem));
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
