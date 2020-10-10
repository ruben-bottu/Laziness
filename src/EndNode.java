import java.util.function.Function;
import java.util.function.Predicate;

public class EndNode<E> extends Node<E> {

    private EndNode(Lazy<E> value, Lazy<Node<E>> tail) {
        super(value, tail);
    }

    public static <E> Node<E> empty() {
        return new EndNode<>(null, null);
    }

    @Override
    public <R> Node<R> map(Function<E, R> transform) {
        return EndNode.empty();
    }

    @Override
    public Node<E> filter(Predicate<E> predicate) {
        return this;
    }
}
