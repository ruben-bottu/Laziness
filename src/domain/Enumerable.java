package domain;

import domain.function.TriFunction;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public static <E> IdeaList<IndexElement<E>> withIndex(Iterable<E> iterable) {
        return zip(Range.infiniteIndices(), iterable).map(Pair::toIndexElement);
    }

    private static <A, E> A reduceHelper(Iterator<E> iterator, A initialValue, BiFunction<A, E, A> operation) {
        A accumulator = initialValue;
        while (iterator.hasNext()) accumulator = operation.apply(accumulator, iterator.next());
        return accumulator;
    }

    public static <A, E> A reduce(Iterable<E> iterable, A initialValue, BiFunction<A, E, A> operation) {
        return reduceHelper(iterable.iterator(), initialValue, operation);
    }

    public static <A, E> A reduceIndexed(Iterable<E> iterable, A initialValue, TriFunction<Integer, A, E, A> operation) {
        return reduce(withIndex(iterable), initialValue, (accum, idxElem) -> operation.apply(idxElem.index, accum, idxElem.element));
    }

    public static <E, A> A reduce(Iterable<E> iterable, BiFunction<A, E, A> operation, Function<E, A> transformFirst) {
        Iterator<E> iterator = iterable.iterator();
        if (!iterator.hasNext()) throw new UnsupportedOperationException("Empty list cannot be reduced");
        return reduceHelper(iterator, transformFirst.apply(iterator.next()), operation);
    }

    public static <A, E> A reduceIndexed(Iterable<E> iterable, TriFunction<Integer, A, E, A> operation, BiFunction<Integer, E, A> transformFirst) {
        return reduce(withIndex(iterable),
                (accum, idxElem) -> operation.apply(idxElem.index, accum, idxElem.element),
                idxElem -> transformFirst.apply(idxElem.index, idxElem.element));
    }

    public static <E> E reduce(Iterable<E> iterable, BinaryOperator<E> operation) {
        return reduce(iterable, operation, Function.identity());
    }

    public static <E> E reduceIndexed(Iterable<E> iterable, TriFunction<Integer, E, E, E> operation) {
        return reduce(withIndex(iterable),
                (accum, idxElem) -> operation.apply(idxElem.index, accum, idxElem.element),
                idxElem -> idxElem.element);
    }

    private static <A, E> A reduceRightHelper(Iterator<E> iterator, A initialValue, BiFunction<E, A, A> operation) {
        return !iterator.hasNext() ? initialValue : operation.apply(iterator.next(), reduceRightHelper(iterator, initialValue, operation));
    }

    public static <A, E> A reduceRight(Iterable<E> iterable, A initialValue, BiFunction<E, A, A> operation) {
        return reduceRightHelper(iterable.iterator(), initialValue, operation);
    }

    public static <A, E> A reduceRightIndexed(Iterable<E> iterable, A initialValue, TriFunction<Integer, E, A, A> operation) {
        return reduceRight(withIndex(iterable), initialValue, (idxElem, accum) -> operation.apply(idxElem.index, idxElem.element, accum));
    }

    private static <A, E> A reduceRightHelper(Iterator<E> iterator, BiFunction<E, A, A> operation, Function<E, A> transformLast) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            if (!iterator.hasNext()) return transformLast.apply(element);
            return operation.apply(element, reduceRightHelper(iterator, operation, transformLast));
        }
        throw new UnsupportedOperationException("Empty list cannot be reduced");
    }

    public static <A, E> A reduceRight(Iterable<E> iterable, BiFunction<E, A, A> operation, Function<E, A> transformLast) {
        return reduceRightHelper(iterable.iterator(), operation, transformLast);
    }

    public static <A, E> A reduceRightIndexed(Iterable<E> iterable, TriFunction<Integer, E, A, A> operation, BiFunction<Integer, E, A> transformLast) {
        return reduceRight(withIndex(iterable),
                (idxElem, accum) -> operation.apply(idxElem.index, idxElem.element, accum),
                idxElem -> transformLast.apply(idxElem.index, idxElem.element));
    }

    public static <E> E reduceRight(Iterable<E> iterable, BinaryOperator<E> operation) {
        return reduceRight(iterable, operation, Function.identity());
    }

    public static <E> E reduceRightIndexed(Iterable<E> iterable, TriFunction<Integer, E, E, E> operation) {
        return reduceRight(withIndex(iterable),
                (idxElem, accum) -> operation.apply(idxElem.index, idxElem.element, accum),
                idxElem -> idxElem.element);
    }

    public static int sum(Iterable<Integer> iterable) {
        return reduce(iterable, 0, Integer::sum);
    }

    public static double sumDouble(Iterable<Double> iterable) {
        return reduce(iterable, 0.0d, Double::sum);
    }

    public <E> boolean all(Iterable<E> iterable, Predicate<E> predicate) {
        for (E element : iterable) {
            if (!predicate.test(element)) return false;
        }
        return true;
    }

    public <E> boolean any(Iterable<E> iterable, Predicate<E> predicate) {
        for (E element : iterable) {
            if (predicate.test(element)) return true;
        }
        return false;
    }
}
