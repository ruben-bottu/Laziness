package idealist.range;

import idealist.IdeaList;

import java.util.function.LongUnaryOperator;

public class LongRange {
    private final long from;

    LongRange(long from) {
        this.from = from;
    }

    private static IdeaList<Long> createRange(long length, LongUnaryOperator indexToElement) {
        return (length < 0L) ? IdeaList.empty() : IdeaList.initialiseWithLong(length, indexToElement::applyAsLong);
    }

    public IdeaList<Long> length(long length) {
        return createRange(length, index -> from + index);
    }

    public IdeaList<Long> upTo(long end) {
        return length(end - from);
    }

    public IdeaList<Long> upToAndIncluding(long end) {
        return upTo(end + 1L);
    }

    public IdeaList<Long> downTo(long end) {
        return createRange(from - end, index -> from - index);
    }

    public IdeaList<Long> downToAndIncluding(long end) {
        return downTo(end - 1L);
    }

    public static IdeaList<Long> infiniteIndices() {
        return IdeaList.initialiseWithLong(Long.MAX_VALUE, index -> index);
    }
}
