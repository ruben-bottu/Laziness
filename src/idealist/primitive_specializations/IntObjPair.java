package idealist.primitive_specializations;

import java.util.Objects;

public class IntObjPair<A> {
    public final int integer;
    public final A object;

    private IntObjPair(int integer, A object) {
        this.integer = integer;
        this.object = object;
    }

    public static <A> IntObjPair<A> of(int integer, A object) {
        return new IntObjPair<>(integer, object);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntObjPair<?> intPair = (IntObjPair<?>) o;
        return integer == intPair.integer && Objects.equals(object, intPair.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(integer, object);
    }

    @Override
    public String toString() {
        return "(" + integer + ", " + object + ")";
    }
}
