import java.util.function.Function;
import java.util.function.Predicate;

class NormalLazyList<E> extends LazyList<E> {

    /*private NormalLazyList(Iterable<? extends E> elements) {

    }*/

    private NormalLazyList(Lazy<E> value, Lazy<LazyList<E>> tail) {
        super(value, tail);
    }

    public static <E> LazyList<E> of(Lazy<E> value, Lazy<LazyList<E>> tail) {
        return new NormalLazyList<>(value, tail);
    }

    @Override
    public <R> LazyList<R> map(Function<E, R> transform) {
        return NormalLazyList.of(
                Lazy.of(() -> transform.apply(value.value())),
                Lazy.of(() -> tail.value().map(transform))
        );
    }

    @Override
    public LazyList<E> filter(Predicate<E> predicate) {
        return predicate.test(value.value()) ?
                NormalLazyList.of(value, Lazy.of(() -> tail.value().filter(predicate))) :
                tail.value().filter(predicate);
    }
}
