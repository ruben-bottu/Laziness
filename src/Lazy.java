import java.util.function.Supplier;

public class Lazy<T> {
    private boolean evaluated;
    private T value;
    private final Supplier<T> func;

    private Lazy(Supplier<T> func) {
        this.evaluated = false;
        this.func = func;
    }

    public static <T> Lazy<T> of(Supplier<T> func) {
        return new Lazy<>(func);
    }

    public T value() {
        if (!evaluated) {
            value = func.get();
            evaluated = true;
        }
        return value;
    }
}
