package idealist.function;

@FunctionalInterface
public interface IntObjFunction<A, R> {

    R apply(int value, A a);

}
