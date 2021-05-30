package domain.function;

@FunctionalInterface
public interface IndexedBiFunction<A, B, R> {

    R apply(int value, A a, B b);

}
