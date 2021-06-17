package idealist.range;

import idealist.IdeaList;

import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public class IntRange {
    private final int from;

    IntRange(int from) {
        this.from = from;
    }

    private static IdeaList<Integer> createRange(int length, IntUnaryOperator indexToElement) {
        return (length < 0) ? IdeaList.empty() : IdeaList.initialiseWith(length, indexToElement::applyAsInt);
    }

    public IdeaList<Integer> length(int length) {
        return createRange(length, index -> from + index);
    }

    public IdeaList<Integer> upTo(int end) {
        return length(end - from);
    }

    public IdeaList<Integer> upToAndIncluding(int end) {
        return upTo(end + 1);
    }

    public IdeaList<Integer> downTo(int end) {
        return createRange(from - end, index -> from - index);
    }

    public IdeaList<Integer> downToAndIncluding(int end) {
        return downTo(end - 1);
    }

    public static IdeaList<Integer> infiniteIndices() {
        return IdeaList.initialiseWith(Integer.MAX_VALUE, Function.identity());
    }

    /*private static IntIdeaList createRange(int length, IntUnaryOperator indexToElement) {
        return (length < 0) ? IntIdeaList.empty() : IntIdeaList.initialiseWith(length, indexToElement);
    }

    public IntIdeaList length(int length) {
        return createRange(length, index -> from + index);
    }

    public IntIdeaList upTo(int end) {
        return length(end - from);
    }

    public IntIdeaList upToAndIncluding(int end) {
        return upTo(end + 1);
    }

    public IntIdeaList downTo(int end) {
        return createRange(from - end, index -> from - index);
    }

    public IntIdeaList downToAndIncluding(int end) {
        return downTo(end - 1);
    }

    public static IntIdeaList infiniteIndices() {
        return IntIdeaList.initialiseWith(Integer.MAX_VALUE, index -> index);
    }*/
}
