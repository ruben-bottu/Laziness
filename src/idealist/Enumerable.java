package idealist;

import idealist.function.IndexedBiFunction;
import idealist.function.TriFunction;
import idealist.tuple.Pair;
import idealist.tuple.Triplet;

import java.lang.reflect.Array;
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

    private static <A, B, C> boolean anyIsEmpty(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return !iterators.first.hasNext() || !iterators.second.hasNext() || !iterators.third.hasNext();
    }

    private static <A, B, C> Triplet<A, B, C> next(Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return Triplet.of(iterators.first.next(), iterators.second.next(), iterators.third.next());
    }

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

    private static <A, B, C, R> R next2(TriFunction<A, B, C, R> constructor, Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return constructor.apply(iterators.first.next(), iterators.second.next(), iterators.third.next());
    }

    private static <A, B, C, R> IdeaList<R> zipHelper2(TriFunction<A, B, C, R> constructor, Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        if (anyIsEmpty(iterators)) return IdeaList.empty();
        var elements = next2(constructor, iterators);
        return IdeaList.create(Lazy.of(() -> elements), Lazy.of(() -> zipHelper2(constructor, iterators)));
    }

    private static <A, B, C> Triplet<Iterator<A>, Iterator<B>, Iterator<C>> tripletOfIterators(Iterable<A> left, Iterable<B> middle, Iterable<C> right) {
        return Triplet.of(left.iterator(), middle.iterator(), right.iterator());
    }

    public static <A, B, C> IdeaList<Triplet<A, B, C>> zip2(Iterable<A> left, Iterable<B> middle, Iterable<C> right) {
        return zipHelper2(Triplet::of, tripletOfIterators(left, middle, right));
    }

    public static <A, B> IdeaList<Pair<A, B>> zip2(Iterable<A> left, Iterable<B> right) {
        return zipHelper2(Pair::ofIgnoreLast, tripletOfIterators(left, right, Enumerable.infiniteNulls()));
    }

    private static <A, E> A reduceHelper(A initialValue, BiFunction<A, E, A> operation, Iterator<E> iterator) {
        A accumulator = initialValue;
        while (iterator.hasNext()) {
            accumulator = operation.apply(accumulator, iterator.next());
        }
        return accumulator;
    }

    /*private <R, S> R createIndexed(BiFunction<IdeaList<E>, Function<E, S>, R> function, IntObjFunction<E, S> lambda) {
        var index = new AtomicInteger();
        return function.apply(this, elem -> lambda.apply(index.getAndIncrement(), elem));
    }*/

    private static <F, A, E> A createIndexed(TriFunction<F, BiFunction<A, E, A>, Iterable<E>, A> function, F initial, IndexedBiFunction<A, E, A> operation, Iterable<E> elements) {
        var index = new AtomicInteger();
        return function.apply(initial, (accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), elements);
    }

    private static <A, B, C, D, R, S> R createIndexed(TriFunction<BiFunction<A, B, S>, C, D, R> function, IndexedBiFunction<A, B, S> innerFunction, C a, D b) {
        var index = new AtomicInteger();
        return function.apply((x, y) -> innerFunction.apply(index.getAndIncrement(), x, y), a, b);
    }

    public static <A, E> A reduce(A initialValue, BiFunction<A, E, A> operation, Iterable<E> elements) {
        return reduceHelper(initialValue, operation, elements.iterator());
    }

    /*public static <A, E> A reduceIndexed(A initialValue, IndexedBiFunction<A, E, A> operation, Iterable<E> elements) {
        var index = new AtomicInteger();
        return reduce(initialValue, (accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), elements);
    }*/

    /*public static <A, E> A reduceIndexed(A initialValue, IndexedBiFunction<A, E, A> operation, Iterable<E> elements) {
        return createIndexed(Enumerable::reduce, initialValue, operation, elements);
    }*/

    public static <A, E> A reduceIndexed(A initialValue, IndexedBiFunction<A, E, A> operation, Iterable<E> elements) {
        return createIndexed((operat, initial, iterable) -> reduce(initial, operat, iterable), operation, initialValue, elements);
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

    /*public static <A, E> A reduceIndexed(IndexedBiFunction<A, E, A> operation, Function<E, A> transformFirst, Iterable<E> elements) {
        var index = new AtomicInteger();
        return reduce((accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), transformFirst, elements);
    }*/

    public static <A, E> A reduceIndexed(IndexedBiFunction<A, E, A> operation, Function<E, A> transformFirst, Iterable<E> elements) {
        return createIndexed(Enumerable::reduce, operation, transformFirst, elements);
    }

    public static <E> E reduce(BinaryOperator<E> operation, Iterable<E> elements) {
        return reduce(operation, Function.identity(), elements);
    }

    public static <E> E reduceIndexed(IndexedBiFunction<E, E, E> operation, Iterable<E> elements) {
        return reduceIndexed(operation, Function.identity(), elements);
    }

    private static <A, E> A reduceRightHelper(A initialValue, BiFunction<E, A, A> operation, Iterator<E> iterator) {
        return !iterator.hasNext() ? initialValue : operation.apply(iterator.next(), reduceRightHelper(initialValue, operation, iterator));
    }

    public static <A, E> A reduceRight(A initialValue, BiFunction<E, A, A> operation, Iterable<E> elements) {
        return reduceRightHelper(initialValue, operation, elements.iterator());
    }

    /*public static <A, E> A reduceRightIndexed(A initialValue, IndexedBiFunction<E, A, A> operation, Iterable<E> elements) {
        var index = new AtomicInteger();
        return reduceRight(initialValue, (accum, elem) -> operation.apply(index.getAndIncrement(), accum, elem), elements);
    }*/

    public static <A, E> A reduceRightIndexed(A initialValue, IndexedBiFunction<E, A, A> operation, Iterable<E> elements) {
        return createIndexed((operat, initial, iterable) -> reduceRight(initial, operat, iterable), operation, initialValue, elements);
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

    /*public static <A, E> A reduceRightIndexed(IndexedBiFunction<E, A, A> operation, Function<E, A> transformLast, Iterable<E> elements) {
        var index = new AtomicInteger();
        return reduceRight((elem, accum) -> operation.apply(index.getAndIncrement(), elem, accum), transformLast, elements);
    }*/

    public static <A, E> A reduceRightIndexed(IndexedBiFunction<E, A, A> operation, Function<E, A> transformLast, Iterable<E> elements) {
        return createIndexed(Enumerable::reduceRight, operation, transformLast, elements);
    }

    public static <E> E reduceRight(BinaryOperator<E> operation, Iterable<E> elements) {
        return reduceRight(operation, Function.identity(), elements);
    }

    public static <E> E reduceRightIndexed(IndexedBiFunction<E, E, E> operation, Iterable<E> elements) {
        return reduceIndexed(operation, Function.identity(), elements);
    }

    public static <E> boolean all(Predicate<E> predicate, Iterable<E> elements) {
        for (E element : elements) {
            if (!predicate.test(element)) return false;
        }
        return true;
    }

    public static <E> boolean all2(Predicate<E> predicate, Iterable<E> elements) {
        return findFirst(predicate.negate(), elements).isEmpty();
    }

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

    /*
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

    public static <E> List<E> toList(Iterable<E> elements) {
        List<E> list = new ArrayList<>();
        elements.forEach(list::add);
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] unsafeArrayOf(Class<?> elementType, int length) {
        return (E[]) Array.newInstance(elementType, length);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] arrayOf(Class<T> elementType, int length) {
        return (T[]) Array.newInstance(elementType, length);
    }
}
