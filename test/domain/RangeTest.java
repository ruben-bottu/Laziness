package domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class RangeTest {

    @Test
    public void upToAndIncluding_Returns_empty_list_if_from_bigger_than_to() {
        assertEquals(LazyList.empty(), Range.from(7).upToAndIncluding(-2));
    }

    @Test
    public void upToAndIncluding_Returns_empty_list_if_from_just_bigger_than_to() {
        assertEquals(LazyList.empty(), Range.from(4).upToAndIncluding(3));
    }

    @Test
    public void upToAndIncluding_Returns_singleton_list_if_from_equal_to_to() {
        assertEquals(LazyList.of(741), Range.from(741).upToAndIncluding(741));
    }

    @Test
    public void upToAndIncluding_Returns_incrementing_list_of_integers_if_from_just_smaller_than_to() {
        assertEquals(LazyList.of(-894, -893), Range.from(-894).upToAndIncluding(-893));
    }

    @Test
    public void upToAndIncluding_Returns_incrementing_list_of_integers_if_from_smaller_than_to() {
        LazyList<Integer> expected = LazyList.of(-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(expected, Range.from(-6).upToAndIncluding(8));
    }

    @Test
    public void downToAndIncluding_Returns_decrementing_list_of_integers_if_from_bigger_than_to() {
        LazyList<Integer> expected = LazyList.of(7, 6, 5, 4, 3, 2, 1, 0, -1, -2);
        assertEquals(expected, Range.from(7).downToAndIncluding(-2));
    }

    @Test
    public void downToAndIncluding_Returns_decrementing_list_of_integers_if_from_just_bigger_than_to() {
        assertEquals(LazyList.of(4, 3), Range.from(4).downToAndIncluding(3));
    }

    @Test
    public void downToAndIncluding_Returns_singleton_list_if_from_equal_to_to() {
        assertEquals(LazyList.of(741), Range.from(741).downToAndIncluding(741));
    }

    @Test
    public void downToAndIncluding_Returns_empty_list_if_from_just_smaller_than_to() {
        assertEquals(LazyList.empty(), Range.from(-894).downToAndIncluding(-893));
    }

    @Test
    public void downToAndIncluding_Returns_empty_list_if_from_smaller_than_to() {
        assertEquals(LazyList.empty(), Range.from(-6).downToAndIncluding(8));
    }

    @Test
    public void upTo_Returns_empty_list_if_from_bigger_than_to() {
        assertEquals(LazyList.empty(), Range.from(7).upTo(-5));
    }

    @Test
    public void upTo_Returns_empty_list_if_from_just_bigger_than_to() {
        assertEquals(LazyList.empty(), Range.from(0).upTo(-1));
    }

    @Test
    public void upTo_Returns_empty_list_if_from_equal_to_to() {
        assertEquals(LazyList.empty(), Range.from(741).upTo(741));
    }

    @Test
    public void upTo_Returns_singleton_list_if_from_just_smaller_than_to() {
        assertEquals(LazyList.of(-894), Range.from(-894).upTo(-893));
    }

    @Test
    public void upTo_Returns_incrementing_list_of_integers_if_from_smaller_than_to() {
        LazyList<Integer> expected = LazyList.of(-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7);
        assertEquals(expected, Range.from(-6).upTo(8));
    }

    @Test
    public void downTo_Returns_decrementing_list_of_integers_if_from_bigger_than_to() {
        LazyList<Integer> expected = LazyList.of(7, 6, 5, 4, 3, 2, 1, 0, -1, -2, -3, -4);
        assertEquals(expected, Range.from(7).downTo(-5));
    }

    @Test
    public void downTo_Returns_singleton_list_if_from_just_bigger_than_to() {
        assertEquals(LazyList.of(0), Range.from(0).downTo(-1));
    }

    @Test
    public void downTo_Returns_empty_list_if_from_equal_to_to() {
        assertEquals(LazyList.empty(), Range.from(741).downTo(741));
    }

    @Test
    public void downTo_Returns_empty_list_if_from_just_smaller_than_to() {
        assertEquals(LazyList.empty(), Range.from(-894).downTo(-893));
    }

    @Test
    public void downTo_Returns_empty_list_if_from_smaller_than_to() {
        assertEquals(LazyList.empty(), Range.from(-6).downTo(8));
    }

    @Test
    public void length_Returns_empty_list_if_given_length_negative() {
        assertEquals(LazyList.empty(), Range.from(2).length(-6));
    }

    @Test
    public void length_Returns_empty_list_if_given_length_just_negative() {
        assertEquals(LazyList.empty(), Range.from(0).length(-1));
    }

    @Test
    public void length_Returns_empty_list_if_given_length_0() {
        assertEquals(LazyList.empty(), Range.from(741).length(0));
    }

    @Test
    public void length_Returns_singleton_list_if_given_length_just_positive() {
        assertEquals(LazyList.of(-959), Range.from(-959).length(1));
    }

    @Test
    public void length_Returns_incrementing_list_of_integers_if_given_length_positive() {
        LazyList<Integer> expected = LazyList.of(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4);
        assertEquals(expected, Range.from(-5).length(10));
    }

    @Test
    public void length_Returns_empty_list_if_from_plus_length_minus_1_is_larger_than_Integer_MAX_VALUE() {
        assertEquals(LazyList.empty(), Range.from(6).length(Integer.MAX_VALUE));
    }
}