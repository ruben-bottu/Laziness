package domain;

public class Range {

    public static IntRange from(int start) {
        return new IntRange(start);
    }

    public static CharRange from(char start) {
        return new CharRange(start);
    }

    public static IdeaList<Integer> infiniteIndices() {
        return IntRange.infiniteIndices();
    }
}