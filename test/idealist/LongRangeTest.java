package idealist;

import org.junit.Test;

import static org.junit.Assert.*;

public class LongRangeTest {

    @Test
    public void length_Returns_empty_list_if_given_length_negative() {
        assertEquals(IdeaList.empty(), Range.from(2L).length(-6L));
    }

    @Test
    public void length_Returns_empty_list_if_given_length_just_negative() {
        assertEquals(IdeaList.empty(), Range.from(0L).length(-1L));
    }

    @Test
    public void length_Returns_empty_list_if_given_length_0L() {
        assertEquals(IdeaList.empty(), Range.from(741L).length(0L));
    }

    @Test
    public void length_Returns_singleton_list_if_given_length_just_positive() {
        assertEquals(IdeaList.of(-959L), Range.from(-959L).length(1L));
    }

    @Test
    public void length_Returns_incrementing_list_of_longs_if_given_length_positive() {
        IdeaList<Long> expected = IdeaList.of(-5L, -4L, -3L, -2L, -1L, 0L, 1L, 2L, 3L, 4L);
        assertEquals(expected, Range.from(-5L).length(10L));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void upTo_Returns_empty_list_if_start_bigger_than_end() {
        assertEquals(IdeaList.empty(), Range.from(7L).upTo(-5L));
    }

    @Test
    public void upTo_Returns_empty_list_if_start_just_bigger_than_end() {
        assertEquals(IdeaList.empty(), Range.from(0L).upTo(-1L));
    }

    @Test
    public void upTo_Returns_empty_list_if_start_equal_to_end() {
        assertEquals(IdeaList.empty(), Range.from(741L).upTo(741L));
    }

    @Test
    public void upTo_Returns_singleton_list_if_start_just_smaller_than_end() {
        assertEquals(IdeaList.of(-894L), Range.from(-894L).upTo(-893L));
    }

    @Test
    public void upTo_Returns_incrementing_list_of_longs_if_start_smaller_than_end() {
        IdeaList<Long> expected = IdeaList.of(-6L, -5L, -4L, -3L, -2L, -1L, 0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L);
        assertEquals(expected, Range.from(-6L).upTo(8L));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void upToAndIncluding_Returns_empty_list_if_start_bigger_than_end() {
        assertEquals(IdeaList.empty(), Range.from(7L).upToAndIncluding(-2L));
    }

    @Test
    public void upToAndIncluding_Returns_empty_list_if_start_just_bigger_than_end() {
        assertEquals(IdeaList.empty(), Range.from(4L).upToAndIncluding(3L));
    }

    @Test
    public void upToAndIncluding_Returns_singleton_list_if_start_equal_to_end() {
        assertEquals(IdeaList.of(741L), Range.from(741L).upToAndIncluding(741L));
    }

    @Test
    public void upToAndIncluding_Returns_incrementing_list_of_longs_if_start_just_smaller_than_end() {
        assertEquals(IdeaList.of(-894L, -893L), Range.from(-894L).upToAndIncluding(-893L));
    }

    @Test
    public void upToAndIncluding_Returns_incrementing_list_of_longs_if_start_smaller_than_end() {
        IdeaList<Long> expected = IdeaList.of(-6L, -5L, -4L, -3L, -2L, -1L, 0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L);
        assertEquals(expected, Range.from(-6L).upToAndIncluding(8L));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void downTo_Returns_decrementing_list_of_longs_if_start_bigger_than_end() {
        IdeaList<Long> expected = IdeaList.of(7L, 6L, 5L, 4L, 3L, 2L, 1L, 0L, -1L, -2L, -3L, -4L);
        assertEquals(expected, Range.from(7L).downTo(-5L));
    }

    @Test
    public void downTo_Returns_singleton_list_if_start_just_bigger_than_end() {
        assertEquals(IdeaList.of(0L), Range.from(0L).downTo(-1L));
    }

    @Test
    public void downTo_Returns_empty_list_if_start_equal_to_end() {
        assertEquals(IdeaList.empty(), Range.from(741L).downTo(741L));
    }

    @Test
    public void downTo_Returns_empty_list_if_start_just_smaller_than_end() {
        assertEquals(IdeaList.empty(), Range.from(-894L).downTo(-893L));
    }

    @Test
    public void downTo_Returns_empty_list_if_start_smaller_than_end() {
        assertEquals(IdeaList.empty(), Range.from(-6L).downTo(8L));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void downToAndIncluding_Returns_decrementing_list_of_longs_if_start_bigger_than_end() {
        IdeaList<Long> expected = IdeaList.of(7L, 6L, 5L, 4L, 3L, 2L, 1L, 0L, -1L, -2L);
        assertEquals(expected, Range.from(7L).downToAndIncluding(-2L));
    }

    @Test
    public void downToAndIncluding_Returns_decrementing_list_of_longs_if_start_just_bigger_than_end() {
        assertEquals(IdeaList.of(4L, 3L), Range.from(4L).downToAndIncluding(3L));
    }

    @Test
    public void downToAndIncluding_Returns_singleton_list_if_start_equal_to_end() {
        assertEquals(IdeaList.of(741L), Range.from(741L).downToAndIncluding(741L));
    }

    @Test
    public void downToAndIncluding_Returns_empty_list_if_start_just_smaller_than_end() {
        assertEquals(IdeaList.empty(), Range.from(-894L).downToAndIncluding(-893L));
    }

    @Test
    public void downToAndIncluding_Returns_empty_list_if_start_smaller_than_end() {
        assertEquals(IdeaList.empty(), Range.from(-6L).downToAndIncluding(8L));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void infiniteIndices_Creates_an_infinite_incrementing_list_of_longs_starting_from_0L() {
        IdeaList<Long> infiniteIndices = Range.longInfiniteIndices();
        assertEquals(0L, infiniteIndices.value.value().intValue());
        assertEquals(1L, infiniteIndices.tail.value().value.value().intValue());
        assertEquals(2L, infiniteIndices.tail.value().tail.value().value.value().intValue());
        assertEquals(3L, infiniteIndices.tail.value().tail.value().tail.value().value.value().intValue());
        assertEquals(4L, infiniteIndices.tail.value().tail.value().tail.value().tail.value().value.value().intValue());
    }
}
