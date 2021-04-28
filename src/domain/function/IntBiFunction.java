package domain.function;

@FunctionalInterface
public interface IntBiFunction<A, B, R> {

    R apply(int value, A a, B b);

}
