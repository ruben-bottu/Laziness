package domain.function;

@FunctionalInterface
public interface IntObjConsumer<A> {

    void accept(int value, A a);

}
