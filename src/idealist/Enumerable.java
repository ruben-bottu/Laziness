package idealist;

import idealist.function.IndexedBiFunction;
import idealist.function.IntObjFunction;
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

    public static <E> Iterable<Lazy<E>> ofLazy(IdeaList<E> elements) {
        return () -> Enumerator.ofLazy(elements);
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

    private static <A, B, C, R> R next(TriFunction<A, B, C, R> constructor, Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        return constructor.apply(iterators.first.next(), iterators.second.next(), iterators.third.next());
    }

    private static <A, B, C, R> IdeaList<R> zipHelper(TriFunction<A, B, C, R> constructor, Triplet<Iterator<A>, Iterator<B>, Iterator<C>> iterators) {
        if (anyIsEmpty(iterators)) return IdeaList.empty();
        R elements = next(constructor, iterators);
        return IdeaList.create(Lazy.of(() -> elements), Lazy.of(() -> zipHelper(constructor, iterators)));
    }

    private static <A, B, C> Triplet<Iterator<A>, Iterator<B>, Iterator<C>> tripletOfIterators(Iterable<A> left, Iterable<B> middle, Iterable<C> right) {
        return Triplet.of(left.iterator(), middle.iterator(), right.iterator());
    }

    public static <A, B, C> IdeaList<Triplet<A, B, C>> zip(Iterable<A> left, Iterable<B> middle, Iterable<C> right) {
        return zipHelper(Triplet::of, tripletOfIterators(left, middle, right));
    }

    public static <A, B> IdeaList<Pair<A, B>> zip(Iterable<A> left, Iterable<B> right) {
        return zipHelper(Pair::ofIgnoreLast, tripletOfIterators(left, right, infiniteNulls()));
    }

    static <A, B, C, D, R, S> R createIndexed(TriFunction<A, BiFunction<C, D, S>, B, R> function, A a, IndexedBiFunction<C, D, S> innerFunction, B b) {
        var index = new AtomicInteger();
        return function.apply(a, (p1, p2) -> innerFunction.apply(index.getAndIncrement(), p1, p2), b);
    }

    static <A, B, C, D, R, S> R createIndexed(TriFunction<BiFunction<C, D, S>, A, B, R> function, IndexedBiFunction<C, D, S> innerFunction, A a, B b) {
        return createIndexed((pA, pBiFunc, pB) -> function.apply(pBiFunc, pA, pB), a, innerFunction, b);
    }

    static <A, B, R, S> R createIndexed(BiFunction<A, Function<B, S>, R> function, A a, IntObjFunction<B, S> innerFunction) {
        var index = new AtomicInteger();
        return function.apply(a, p -> innerFunction.apply(index.getAndIncrement(), p));
    }

    /*private static <A, B, R, S> R createIndexed(BiFunction<A, Function<B, S>, R> function, A a, IntObjFunction<B, S> innerFunction) {
        return createIndexed((pA, pBiFunc, __) -> pBiFunc.apply(pA, function), a, (index, pC, __) -> innerFunction.apply(index, pC), null);
    }*/

    private static <A, E> A reduceHelper(A initialValue, BiFunction<A, E, A> operation, Iterator<E> iterator) {
        A accumulator = initialValue; // Objects.requireNonNull(initialValue) ?
        while (iterator.hasNext()) {
            accumulator = operation.apply(accumulator, iterator.next());
        }
        return accumulator;
    }

    public static <A, E> A reduce(A initialValue, BiFunction<A, E, A> operation, Iterable<E> elements) {
        return reduceHelper(initialValue, operation, elements.iterator());
    }

    public static <A, E> A reduceIndexed(A initialValue, IndexedBiFunction<A, E, A> operation, Iterable<E> elements) {
        return createIndexed(Enumerable::reduce, initialValue, operation, elements);
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

    public static <A, E> A reduceIndexed(IndexedBiFunction<A, E, A> operation, Function<E, A> transformFirst, Iterable<E> elements) {
        return createIndexed(Enumerable::reduce, operation, transformFirst, elements);
    }

    public static <E> E reduce(BinaryOperator<E> operation, Iterable<E> elements) {
        return reduce(operation, Function.identity(), elements);
    }

    public static <E> E reduceIndexed(IndexedBiFunction<E, E, E> operation, Iterable<E> elements) {
        return reduceIndexed(operation, Function.identity(), elements);
    }

    /*private static <A, E> A reduceRightHelper(A initialValue, BiFunction<E, A, A> operation, Iterator<E> iterator) {
        return !iterator.hasNext() ? initialValue : operation.apply(iterator.next(), reduceRightHelper(initialValue, operation, iterator));
    }

    public static <A, E> A reduceRight(A initialValue, BiFunction<E, A, A> operation, Iterable<E> elements) {
        return reduceRightHelper(initialValue, operation, elements.iterator());
    }*/

    public static <A, E> A reduceRight(A initialValue, BiFunction<E, A, A> operation, Iterable<E> elements) {
        return reduceRight(operation, lastElem -> operation.apply(lastElem, initialValue), elements);
    }

    public static <A, E> A reduceRightIndexed(A initialValue, IndexedBiFunction<E, A, A> operation, Iterable<E> elements) {
        return createIndexed(Enumerable::reduceRight, initialValue, operation, elements);
    }

    private static <A, E> A reduceRightHelper(BiFunction<E, A, A> operation, Function<E, A> transformLast, Iterator<E> iterator) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return !iterator.hasNext() ? transformLast.apply(element) : operation.apply(element, reduceRightHelper(operation, transformLast, iterator));
        }
        throw new UnsupportedOperationException("Empty list cannot be reduced");
    }

    /*private static <A, E> A reduceRightHelper2(BiFunction<E, A, A> operation, Function<E, A> transformLast, Iterator<E> iterator) {
        Deque<E> stack = new ArrayDeque<>();
        if (iterator.hasNext()) {
            E element = iterator.next();
            if (!iterator.hasNext()) {
                return reduce(transformLast.apply(element), (accum, elem) -> operation.apply(elem, accum), stack);
            }
            stack.push(element);
        }
        throw new UnsupportedOperationException("Empty list cannot be reduced");
    }*/

    public static <A, E> A reduceRight(BiFunction<E, A, A> operation, Function<E, A> transformLast, Iterable<E> elements) {
        return reduceRightHelper(operation, transformLast, elements.iterator());
    }

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
    public static <E> E[] arrayOf(Class<E> elementType, int length) {
        return (E[]) Array.newInstance(elementType, length);
    }
}
