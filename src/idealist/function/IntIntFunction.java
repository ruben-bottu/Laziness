package idealist.function;

@FunctionalInterface
public interface IntIntFunction<R> {

    R apply(int value1, int value2);

}
