package domain.function;

@FunctionalInterface
public interface IntObjPredicate<A> {

    boolean test(int value, A a);

}
