import java.util.function.Function;
import java.util.function.Predicate;

class EndLazyList<E> extends LazyList<E> {

    private EndLazyList(Lazy<E> value, Lazy<LazyList<E>> tail) {
        super(value, tail);
    }

    public static <E> LazyList<E> empty() {
        return new EndLazyList<>(null, null);
    }

    @Override
    public <R> LazyList<R> map(Function<E, R> transform) {
        return EndLazyList.empty();
    }

    @Override
    public LazyList<E> filter(Predicate<E> predicate) {
        return this;
    }
}
