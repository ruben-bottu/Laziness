package idealist;

import java.util.function.Supplier;

public class Lazy<T> {
    private boolean evaluated;
    private T value;
    private final Supplier<T> function;

    private Lazy(Supplier<T> function) {
        this.evaluated = false;
        this.function = function;
    }

    public static <T> Lazy<T> of(Supplier<T> function) {
        return new Lazy<>(function);
    }

    public T value() {
        if (!evaluated) {
            value = function.get();
            evaluated = true;
        }
        return value;
    }

    @Override
    public String toString() {
        return evaluated
                ? "Lazy{ " + value + " }"
                : super.toString();
    }
}
