import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class LazyList<E> {
    public final Lazy<E> value;
    public final Lazy<LazyList<E>> tail;

    public LazyList(Lazy<E> value, Lazy<LazyList<E>> tail) {
        this.value = value;
        this.tail = tail;
    }

    public abstract <R> LazyList<R> map(Function<E, R> transform);

    public abstract LazyList<E> filter(Predicate<E> predicate);

    private List<E> toListHelper(List<E> result) {
        if (this instanceof EndLazyList) return result;
        result.add(value.value());
        return tail.value().toListHelper(result);
    }

    public List<E> toList() {
        return toListHelper(new ArrayList<>());
    }
}
