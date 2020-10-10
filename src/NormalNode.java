import java.util.function.Function;
import java.util.function.Predicate;

public class NormalNode<E> extends Node<E> {

    private NormalNode(Lazy<E> value, Lazy<Node<E>> tail) {
        super(value, tail);
    }

    public static <E> Node<E> of(Lazy<E> value, Lazy<Node<E>> tail) {
        return new NormalNode<>(value, tail);
    }

    @Override
    public <R> Node<R> map(Function<E, R> transform) {
        return NormalNode.of(
                Lazy.of(() -> transform.apply(value.value())),
                Lazy.of(() -> tail.value().map(transform))
        );
    }

    @Override
    public Node<E> filter(Predicate<E> predicate) {
        return predicate.test(value.value()) ?
                NormalNode.of(value, Lazy.of(() -> tail.value().filter(predicate))) :
                tail.value().filter(predicate);
    }
}
