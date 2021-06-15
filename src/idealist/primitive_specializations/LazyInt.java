package idealist.primitive_specializations;

import java.util.function.IntSupplier;

public class LazyInt {
    private boolean evaluated;
    private int value;
    private final IntSupplier function;

    private LazyInt(IntSupplier function) {
        this.evaluated = false;
        this.function = function;
    }

    public static LazyInt of(IntSupplier function) {
        return new LazyInt(function);
    }

    public int value() {
        if (!evaluated) {
            value = function.getAsInt();
            evaluated = true;
        }
        return value;
    }
}
