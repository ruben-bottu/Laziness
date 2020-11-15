package domain;

import java.util.function.BiPredicate;
import java.util.function.IntUnaryOperator;

public class Range {
    private final int from;

    private Range(int from) {
        this.from = from;
    }

    public static Range from(int start) {
        return new Range(start);
    }

    private static LazyList<Integer> inclusiveRange(int from, int to, BiPredicate<Integer, Integer> predicate, IntUnaryOperator nextFrom) {
        return predicate.test(from, to) ? LazyList.empty() : LazyList.create(Lazy.of(() -> from), Lazy.of(() -> inclusiveRange(nextFrom.applyAsInt(from), to, predicate, nextFrom)));
    }

    public LazyList<Integer> upToAndIncluding(int end) {
        return inclusiveRange(from, end, (currentFrom, currentTo) -> currentFrom > currentTo, oldFrom -> oldFrom + 1);
    }

    public LazyList<Integer> downToAndIncluding(int end) {
        return inclusiveRange(from, end, (currentFrom, currentDownTo) -> currentFrom < currentDownTo, oldFrom -> oldFrom - 1);
    }

    public LazyList<Integer> upTo(int end) {
        return upToAndIncluding(end - 1);
    }

    public LazyList<Integer> downTo(int end) {
        return downToAndIncluding(end + 1);
    }

    public LazyList<Integer> length(int length) {
        return upTo(from + length);
    }

    public static LazyList<Integer> infiniteIndices() {
        return inclusiveRange(0, Integer.MAX_VALUE, Integer::equals, oldFrom -> oldFrom + 1);
    }
}
