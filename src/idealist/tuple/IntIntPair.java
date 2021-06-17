package idealist.tuple;

import java.util.Objects;

public class IntIntPair {
    public final int first;
    public final int second;

    private IntIntPair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public static IntIntPair of(int first, int second) {
        return new IntIntPair(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntIntPair that = (IntIntPair) o;
        return first == that.first && second == that.second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
