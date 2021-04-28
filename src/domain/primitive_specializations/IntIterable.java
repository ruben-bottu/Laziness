package domain.primitive_specializations;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface IntIterable {

    IntIterator iterator();

    default void forEach(IntConsumer action) {
        Objects.requireNonNull(action);
        IntIterator it = iterator();
        while (it.hasNext()) action.accept(it.next());
    }

}
