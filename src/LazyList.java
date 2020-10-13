import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class LazyList<E> {
    public final Lazy<E> value;
    public final Lazy<LazyList<E>> tail;

    private LazyList(Lazy<E> value, Lazy<LazyList<E>> tail) {
        this.value = value;
        this.tail = tail;
    }

    private static <E> LazyList<E> ofHelper(Iterator<? extends E> iterator) {
        if (iterator.hasNext()) {
            return NormalNode.of(Lazy.of(iterator::next), Lazy.of(() -> ofHelper(iterator)));
        }
        return EndNode.empty();
    }

    public static <E> LazyList<E> of(Iterable<? extends E> elements) {
        return ofHelper(elements.iterator());
    }

    @SafeVarargs
    public static <E> LazyList<E> of(E... elements) {
        return LazyList.of(Arrays.asList(elements));
    }

    public abstract <R> LazyList<R> map(Function<E, R> transform);

    public abstract LazyList<E> filter(Predicate<E> predicate);

    private List<E> toListHelper(List<E> result) {
        if (this instanceof EndNode) return result;
        result.add(value.value());
        return tail.value().toListHelper(result);
    }

    public List<E> toList() {
        return toListHelper(new ArrayList<>());
    }



    private static class NormalNode<E> extends LazyList<E> {

        private NormalNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        public static <E> LazyList<E> of(Lazy<E> value, Lazy<LazyList<E>> tail) {
            return new NormalNode<>(value, tail);
        }

        @Override
        public <R> LazyList<R> map(Function<E, R> transform) {
            return NormalNode.of(
                    Lazy.of(() -> transform.apply(value.value())),
                    Lazy.of(() -> tail.value().map(transform))
            );
        }

        @Override
        public LazyList<E> filter(Predicate<E> predicate) {
            return predicate.test(value.value()) ?
                    NormalNode.of(value, Lazy.of(() -> tail.value().filter(predicate))) :
                    tail.value().filter(predicate);
        }
    }



    private static class EndNode<E> extends LazyList<E> {

        private EndNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        public static <E> LazyList<E> empty() {
            return new EndNode<>(null, null);
        }

        @Override
        public <R> LazyList<R> map(Function<E, R> transform) {
            return EndNode.empty();
        }

        @Override
        public LazyList<E> filter(Predicate<E> predicate) {
            return this;
        }
    }
}
