package idealist;

import idealist.range.Range;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntRangeTest {

    @Test
    public void length_Returns_empty_list_if_given_length_negative() {
        assertEquals(IdeaList.empty(), Range.from(2).length(-6));
    }

    @Test
    public void length_Returns_empty_list_if_given_length_just_negative() {
        assertEquals(IdeaList.empty(), Range.from(0).length(-1));
    }

    @Test
    public void length_Returns_empty_list_if_given_length_0() {
        assertEquals(IdeaList.empty(), Range.from(741).length(0));
    }

    @Test
    public void length_Returns_singleton_list_if_given_length_just_positive() {
        assertEquals(IdeaList.of(-959), Range.from(-959).length(1));
    }

    @Test
    public void length_Returns_incrementing_list_of_integers_if_given_length_positive() {
        IdeaList<Integer> expected = IdeaList.of(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4);
        assertEquals(expected, Range.from(-5).length(10));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void upTo_Returns_empty_list_if_start_bigger_than_end() {
        assertEquals(IdeaList.empty(), Range.from(7).upTo(-5));
    }

    @Test
    public void upTo_Returns_empty_list_if_start_just_bigger_than_end() {
        assertEquals(IdeaList.empty(), Range.from(0).upTo(-1));
    }

    @Test
    public void upTo_Returns_empty_list_if_start_equal_to_end() {
        assertEquals(IdeaList.empty(), Range.from(741).upTo(741));
    }

    @Test
    public void upTo_Returns_singleton_list_if_start_just_smaller_than_end() {
        assertEquals(IdeaList.of(-894), Range.from(-894).upTo(-893));
    }

    @Test
    public void upTo_Returns_incrementing_list_of_integers_if_start_smaller_than_end() {
        IdeaList<Integer> expected = IdeaList.of(-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7);
        assertEquals(expected, Range.from(-6).upTo(8));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void upToAndIncluding_Returns_empty_list_if_start_bigger_than_end() {
        assertEquals(IdeaList.empty(), Range.from(7).upToAndIncluding(-2));
    }

    @Test
    public void upToAndIncluding_Returns_empty_list_if_start_just_bigger_than_end() {
        assertEquals(IdeaList.empty(), Range.from(4).upToAndIncluding(3));
    }

    @Test
    public void upToAndIncluding_Returns_singleton_list_if_start_equal_to_end() {
        assertEquals(IdeaList.of(741), Range.from(741).upToAndIncluding(741));
    }

    @Test
    public void upToAndIncluding_Returns_incrementing_list_of_integers_if_start_just_smaller_than_end() {
        assertEquals(IdeaList.of(-894, -893), Range.from(-894).upToAndIncluding(-893));
    }

    @Test
    public void upToAndIncluding_Returns_incrementing_list_of_integers_if_start_smaller_than_end() {
        IdeaList<Integer> expected = IdeaList.of(-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(expected, Range.from(-6).upToAndIncluding(8));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void downTo_Returns_decrementing_list_of_integers_if_start_bigger_than_end() {
        IdeaList<Integer> expected = IdeaList.of(7, 6, 5, 4, 3, 2, 1, 0, -1, -2, -3, -4);
        assertEquals(expected, Range.from(7).downTo(-5));
    }

    @Test
    public void downTo_Returns_singleton_list_if_start_just_bigger_than_end() {
        assertEquals(IdeaList.of(0), Range.from(0).downTo(-1));
    }

    @Test
    public void downTo_Returns_empty_list_if_start_equal_to_end() {
        assertEquals(IdeaList.empty(), Range.from(741).downTo(741));
    }

    @Test
    public void downTo_Returns_empty_list_if_start_just_smaller_than_end() {
        assertEquals(IdeaList.empty(), Range.from(-894).downTo(-893));
    }

    @Test
    public void downTo_Returns_empty_list_if_start_smaller_than_end() {
        assertEquals(IdeaList.empty(), Range.from(-6).downTo(8));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void downToAndIncluding_Returns_decrementing_list_of_integers_if_start_bigger_than_end() {
        IdeaList<Integer> expected = IdeaList.of(7, 6, 5, 4, 3, 2, 1, 0, -1, -2);
        assertEquals(expected, Range.from(7).downToAndIncluding(-2));
    }

    @Test
    public void downToAndIncluding_Returns_decrementing_list_of_integers_if_start_just_bigger_than_end() {
        assertEquals(IdeaList.of(4, 3), Range.from(4).downToAndIncluding(3));
    }

    @Test
    public void downToAndIncluding_Returns_singleton_list_if_start_equal_to_end() {
        assertEquals(IdeaList.of(741), Range.from(741).downToAndIncluding(741));
    }

    @Test
    public void downToAndIncluding_Returns_empty_list_if_start_just_smaller_than_end() {
        assertEquals(IdeaList.empty(), Range.from(-894).downToAndIncluding(-893));
    }

    @Test
    public void downToAndIncluding_Returns_empty_list_if_start_smaller_than_end() {
        assertEquals(IdeaList.empty(), Range.from(-6).downToAndIncluding(8));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void infiniteIndices_Creates_an_infinite_incrementing_list_of_integers_starting_from_0() {
        IdeaList<Integer> infiniteIndices = Range.infiniteIndices();
        assertEquals(0, infiniteIndices.value.value().intValue());
        assertEquals(1, infiniteIndices.tail.value().value.value().intValue());
        assertEquals(2, infiniteIndices.tail.value().tail.value().value.value().intValue());
        assertEquals(3, infiniteIndices.tail.value().tail.value().tail.value().value.value().intValue());
        assertEquals(4, infiniteIndices.tail.value().tail.value().tail.value().tail.value().value.value().intValue());
    }
}
