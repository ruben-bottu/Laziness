package domain;

import java.util.function.BiPredicate;

public class IMath {

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static long clamp(long val, long min, long max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    /*public static int safeAdd(int left, int right) {
        if (right > 0 ? left > Integer.MAX_VALUE - right
                : left < Integer.MIN_VALUE - right) {
            throw new ArithmeticException("Integer overflow");
        }
        return left + right;
    }*/

    private static int addHelper(int value, int other, BiPredicate<Integer, Integer> comparator, int extreme) {
        return comparator.test(value, extreme - other) ? extreme : value + other;
    }

    // Source: https://wiki.sei.cmu.edu/confluence/display/java/NUM00-J.+Detect+or+prevent+integer+overflow
    /*public static int add(int value, int other) {
        return (other > 0) ? (value > Integer.MAX_VALUE - other) ? Integer.MAX_VALUE : value + other
                           : (value < Integer.MIN_VALUE - other) ? Integer.MIN_VALUE : value + other;
    }*/

    public static int add(int value, int other) {
        return (other > 0) ? addHelper(value, other, IMath::biggerThan, Integer.MAX_VALUE)
                           : addHelper(value, other, IMath::smallerThan, Integer.MIN_VALUE);
    }

    public static int subtract(int value, int other) {
        return add(value, -other);
    }

    public static boolean biggerThan(int value, int other) {
        return value > other;
    }

    public static boolean smallerThan(int value, int other) {
        return value < other;
    }
}
