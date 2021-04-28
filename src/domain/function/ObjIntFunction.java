package domain.function;

@FunctionalInterface
public interface ObjIntFunction<A, R> {

    R apply(A a, int value);

}
