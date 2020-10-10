import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class IdeaList<E> {
    private final List<Lazy<E>> innerList;

    // Constructors and factory methods ===========================================================
    private IdeaList(Iterable<? extends E> elements) {
        List<Lazy<E>> result = new ArrayList<>();
        elements.forEach(cur -> result.add( Lazy.of(() -> cur) ));
        innerList = result;
    }

    public static <E> IdeaList<E> of(Iterable<? extends E> elements) {
        return new IdeaList<>(elements);
    }

    @SafeVarargs
    public static <E> IdeaList<E> of(E... elements) {
        return IdeaList.of(Arrays.asList(elements));
    }

    public static <E> IdeaList<E> empty() {
        return IdeaList.of();
    }

    private IdeaList(List<Lazy<E>> elements) {
        innerList = elements;
    }

    private static <E> IdeaList<E> of(List<Lazy<E>> elements) {
        return new IdeaList<>(elements);
    }

    public E get(int index) {
        return innerList.get(index).value();
    }

    private void set(int index, Lazy<E> element) {
        innerList.set(index, element);
    }

    public int size() {
        return innerList.size();
    }

    /*public <R> IdeaList<R> map(Function<E, R> transform) {
        return mapIndexed((cur, idx) -> transform.apply(cur));
    }*/

    /*for(int i = 0; i < aList.length; i++){
        aList[i] = new A(temp++);
    }*/

    public <R> IdeaList<R> map(Function<E, R> transform) {
        List<Lazy<R>> result = new ArrayList<>(size());
        for(Lazy<E> element : innerList) {
            result.add( Lazy.of(() -> transform.apply(element.value())) );
        }
        return IdeaList.of(result);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Lazy<E> element : innerList) {
            result.append(element.value().toString());
        }
        return result.toString();
    }
}
