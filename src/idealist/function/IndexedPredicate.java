package idealist.function;

@FunctionalInterface
public interface IndexedPredicate<A> {

    boolean test(int value, A a);

}
