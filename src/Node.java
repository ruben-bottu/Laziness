import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Node<E> {
    public final Lazy<E> value;
    public final Lazy<Node<E>> tail;

    public Node(Lazy<E> value, Lazy<Node<E>> tail) {
        this.value = value;
        this.tail = tail;
    }

    /*public static <E> Node<E> of(Lazy<E> value, Lazy<Node<E>> tail) {
        return new Node<>(value, tail);
    }*/

    /*public static <E> Node<E> of(Lazy<E> value) {
        return Node.of(value, null);
    }*/

    /*Node<U> Map<U>(Func<T, U> f, Node<T> head){
        if ( head == null ) return null;
        return new Node( new Lazy( () => f(head.Value.Value) ), new Lazy( () => Map(f, head.Tail.Value) ) );
    }*/

    /*public <R> Node<R> map(Function<E, R> transform, Node<E> head) {
        if (head == null) return null;
        return Node.of( Lazy.of(() -> transform.apply(head.value.value())),
                Lazy.of(() -> map(transform, head.tail.value())) );
    }*/

    // Latest
    /*public <R> Node<R> map(Function<E, R> transform) {
        return Node.of(
                Lazy.of(() -> transform.apply(value.value())),
                (tail == null ? null : Lazy.of(() -> tail.value().map(transform)))
        );
    }*/

    /*public Node<E> filter(Predicate<E> predicate) {
        return (predicate.test(value.value()) ?
                Node.of(value, Lazy.of(() -> tail.value().filter(predicate))) :
                tail.value().filter(predicate)
        );
    }*/

    /*public Node<E> filter(Predicate<E> predicate) {
        return predicate.test(value.value()) ?
                Node.of(value, (tail == null ? null : Lazy.of(() -> tail.value().filter(predicate)))) :
                tail == null ? null : tail.value().filter(predicate);
    }*/

    // Latest
    /*public Node<E> filter(Predicate<E> predicate) {
        return predicate.test(value.value()) ?
                tail == null ? this : Node.of(value, Lazy.of(() -> tail.value().filter(predicate))) :
                tail == null ? null : tail.value().filter(predicate);
    }*/

    /*public <R> Node<R> map(Function<E, R> transform) {
        if (value == null) return null;
        return Node.of(
                Lazy.of(() -> transform.apply(value.value())),
                Lazy.of(() -> tail.value().map(transform))
        );
    }*/

    public abstract <R> Node<R> map(Function<E, R> transform);

    public abstract Node<E> filter(Predicate<E> predicate);

    /*private List<E> toListHelper(List<E> result) {
        result.add(value.value());
        return (tail == null ? result : tail.value().toListHelper(result));
    }*/

    private List<E> toListHelper(List<E> result) {
        if (this instanceof EndNode) return result;
        result.add(value.value());
        return tail.value().toListHelper(result);
    }

    public List<E> toList() {
        return toListHelper(new ArrayList<>());
    }
}
