package domain.primitive_specializations;

import domain.function.FloatConsumer;

import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;

public interface FloatIterator extends PrimitiveIterator<Float, FloatConsumer> {
    float nextFloat();

    default void forEachRemaining(FloatConsumer action) {
        Objects.requireNonNull(action);
        while (hasNext()) action.accept(nextFloat());
    }

    @Override
    default Float next() {
        return nextFloat();
    }

    @Override
    default void forEachRemaining(Consumer<? super Float> action) {
        if (action instanceof FloatConsumer) {
            forEachRemaining((FloatConsumer) action);
        }
        else {
            // The method reference action::accept is never null
            Objects.requireNonNull(action);
            forEachRemaining((FloatConsumer) action::accept);
        }
    }
}