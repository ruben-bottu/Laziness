package domain;

import java.util.function.IntUnaryOperator;

public class Range {
    private final int from;

    private Range(int from) {
        this.from = from;
    }

    public static Range from(int start) {
        return new Range(start);
    }

    private static IdeaList<Integer> createRange(int length, IntUnaryOperator indexToElement) {
        return (length < 0) ? IdeaList.empty() : IdeaList.initialiseWith(length, indexToElement::applyAsInt);
    }

    public IdeaList<Integer> length(int length) {
        //if (((long) from + length - 1) > Integer.MAX_VALUE) throw new IllegalArgumentException("From " + from + " + length " + length + " - 1 will cause integer overflow");
        return createRange(length, index -> from + index);
    }

    public IdeaList<Integer> upToAndIncluding(int end) {
        return upTo(end + 1);
    }

    public IdeaList<Integer> downToAndIncluding(int end) {
        return downTo(end - 1);
    }

    public IdeaList<Integer> upTo(int end) {
        return length(end - from);
    }

    public IdeaList<Integer> downTo(int end) {
        return createRange(from - end, index -> from - index);
    }

    public static IdeaList<Integer> infiniteIndices() {
        return createRange(Integer.MAX_VALUE, index -> index);
    }
}