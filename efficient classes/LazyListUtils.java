package domain;

public abstract class LazyListUtils {

    public static <E> LazyList<E> flatten(LazyList<Iterable<E>> nestedIterable) {
        return lazyFlatten(nestedIterable.map(LazyList::of));
    }

    public static <E> LazyList<E> lazyFlatten(LazyList<LazyList<E>> nestedLazyList) {
        return nestedLazyList.reduce(LazyList::concatWith);
    }
}
