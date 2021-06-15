package idealist.function;

@FunctionalInterface
public interface IntObjIntFunction<A, R> {

    R apply(int value1, A a, int value2);

}
