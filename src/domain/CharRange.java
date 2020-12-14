package domain;

public class CharRange {
    private final IntRange intRange;

    CharRange(char from) {
        intRange = new IntRange(from);
    }

    private static IdeaList<Character> intToCharRange(IdeaList<Integer> integers) {
        return integers.map(integer -> (char) integer.intValue());
    }

    public IdeaList<Character> length(int length) {
        return intToCharRange(intRange.length(length));
    }

    public IdeaList<Character> upTo(char end) {
        return intToCharRange(intRange.upTo(end));
    }

    public IdeaList<Character> upToAndIncluding(char end) {
        return intToCharRange(intRange.upToAndIncluding(end));
    }

    public IdeaList<Character> downTo(char end) {
        return intToCharRange(intRange.downTo(end));
    }

    public IdeaList<Character> downToAndIncluding(char end) {
        return intToCharRange(intRange.downToAndIncluding(end));
    }
}
