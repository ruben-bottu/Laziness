package idealist.function;

@FunctionalInterface
public interface FloatConsumer {

    void accept(float value);

    // TODO remove
    /*default FloatConsumer andThen(FloatConsumer after) {
        Objects.requireNonNull(after);
        return (float t) -> {
            accept(t);
            after.accept(t);
        };
    }*/
}