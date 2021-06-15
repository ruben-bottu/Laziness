package idealist;

public final class Range {

    // Private constructor to prevent instantiation
    private Range() {}

    public static IntRange from(int start) {
        return new IntRange(start);
    }

    public static CharRange from(char start) {
        return new CharRange(start);
    }

    public static LongRange from(long start) {
        return new LongRange(start);
    }

    public static IdeaList<Integer> infiniteIndices() {
        return IntRange.infiniteIndices();
    }

    public static IdeaList<Long> longInfiniteIndices() {
        return LongRange.infiniteIndices();
    }
}
