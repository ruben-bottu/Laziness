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

    private static LazyList<Integer> createRange(int length, IntUnaryOperator indexToElement) {
        return (length < 0) ? LazyList.empty() : LazyList.initialiseWith(length, indexToElement::applyAsInt);
    }

    public LazyList<Integer> length(int length) {
        if (((long) from + length - 1) > Integer.MAX_VALUE) throw new IllegalArgumentException("From " + from + " + length " + length + " - 1 will cause integer overflow");
        return createRange(length, index -> from + index);
    }

    public LazyList<Integer> upToAndIncluding(int end) {
        return upTo(end + 1);
    }

    public LazyList<Integer> downToAndIncluding(int end) {
        return downTo(end - 1);
    }

    public LazyList<Integer> upTo(int end) {
        return length(end - from);
    }

    public LazyList<Integer> downTo(int end) {
        return createRange(from - end, index -> from - index);
    }

    public static LazyList<Integer> infiniteIndices() {
        return createRange(Integer.MAX_VALUE, index -> index);
    }
}