package domain;

public abstract class IdeaListUtils {

    public static <E> IdeaList<E> flatten(IdeaList<Iterable<E>> nestedIterable) {
        return lazyFlatten(nestedIterable.map(IdeaList::of));
    }

    public static <E> IdeaList<E> lazyFlatten(IdeaList<IdeaList<E>> nestedIdeaList) {
        return nestedIdeaList.reduce(IdeaList::concatWith);
    }
}
