package domain;

public abstract class Enumerable {

    public static Iterable<Void> infiniteNulls() {
        return Enumerator::infiniteNulls;
    }
}