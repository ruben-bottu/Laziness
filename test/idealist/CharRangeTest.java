package idealist;

import org.junit.Test;

import static org.junit.Assert.*;

public class CharRangeTest {

    @Test
    public void length_Returns_empty_list_if_given_length_negative() {
        assertEquals(IdeaList.empty(), Range.from('c').length(-6));
    }

    @Test
    public void length_Returns_empty_list_if_given_length_just_negative() {
        assertEquals(IdeaList.empty(), Range.from('a').length(-1));
    }

    @Test
    public void length_Returns_empty_list_if_given_length_0() {
        assertEquals(IdeaList.empty(), Range.from('q').length(0));
    }

    @Test
    public void length_Returns_singleton_list_if_given_length_just_positive() {
        assertEquals(IdeaList.of('y'), Range.from('y').length(1));
    }

    @Test
    public void length_Returns_incrementing_list_of_chars_if_given_length_positive() {
        IdeaList<Character> expected = IdeaList.of('e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n');
        assertEquals(expected, Range.from('e').length(10));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void upTo_Returns_empty_list_if_start_bigger_than_end() {
        assertEquals(IdeaList.empty(), Range.from('h').upTo('e'));
    }

    @Test
    public void upTo_Returns_empty_list_if_start_just_bigger_than_end() {
        assertEquals(IdeaList.empty(), Range.from('w').upTo('v'));
    }

    @Test
    public void upTo_Returns_empty_list_if_start_equal_to_end() {
        assertEquals(IdeaList.empty(), Range.from('i').upTo('i'));
    }

    @Test
    public void upTo_Returns_singleton_list_if_start_just_smaller_than_end() {
        assertEquals(IdeaList.of('j'), Range.from('j').upTo('k'));
    }

    @Test
    public void upTo_Returns_incrementing_list_of_integers_if_start_smaller_than_end() {
        IdeaList<Character> expected = IdeaList.of('b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l');
        assertEquals(expected, Range.from('b').upTo('m'));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void upToAndIncluding_Returns_empty_list_if_start_bigger_than_end() {
        assertEquals(IdeaList.empty(), Range.from('r').upToAndIncluding('m'));
    }

    @Test
    public void upToAndIncluding_Returns_empty_list_if_start_just_bigger_than_end() {
        assertEquals(IdeaList.empty(), Range.from('t').upToAndIncluding('s'));
    }

    @Test
    public void upToAndIncluding_Returns_singleton_list_if_start_equal_to_end() {
        assertEquals(IdeaList.of('x'), Range.from('x').upToAndIncluding('x'));
    }

    @Test
    public void upToAndIncluding_Returns_incrementing_list_of_chars_if_start_just_smaller_than_end() {
        assertEquals(IdeaList.of('y', 'z'), Range.from('y').upToAndIncluding('z'));
    }

    @Test
    public void upToAndIncluding_Returns_incrementing_list_of_chars_if_start_smaller_than_end() {
        IdeaList<Character> expected = IdeaList.of('d', 'e', 'f', 'g', 'h', 'i', 'j');
        assertEquals(expected, Range.from('d').upToAndIncluding('j'));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void downTo_Returns_decrementing_list_of_chars_if_start_bigger_than_end() {
        IdeaList<Character> expected = IdeaList.of('k', 'j', 'i', 'h');
        assertEquals(expected, Range.from('k').downTo('g'));
    }

    @Test
    public void downTo_Returns_singleton_list_if_start_just_bigger_than_end() {
        assertEquals(IdeaList.of('c'), Range.from('c').downTo('b'));
    }

    @Test
    public void downTo_Returns_empty_list_if_start_equal_to_end() {
        assertEquals(IdeaList.empty(), Range.from('s').downTo('s'));
    }

    @Test
    public void downTo_Returns_empty_list_if_start_just_smaller_than_end() {
        assertEquals(IdeaList.empty(), Range.from('o').downTo('p'));
    }

    @Test
    public void downTo_Returns_empty_list_if_start_smaller_than_end() {
        assertEquals(IdeaList.empty(), Range.from('e').downTo('k'));
    }

    //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    @Test
    public void downToAndIncluding_Returns_decrementing_list_of_chars_if_start_bigger_than_end() {
        IdeaList<Character> expected = IdeaList.of('i', 'h', 'g', 'f');
        assertEquals(expected, Range.from('i').downToAndIncluding('f'));
    }

    @Test
    public void downToAndIncluding_Returns_decrementing_list_of_chars_if_start_just_bigger_than_end() {
        assertEquals(IdeaList.of('m', 'l'), Range.from('m').downToAndIncluding('l'));
    }

    @Test
    public void downToAndIncluding_Returns_singleton_list_if_start_equal_to_end() {
        assertEquals(IdeaList.of('u'), Range.from('u').downToAndIncluding('u'));
    }

    @Test
    public void downToAndIncluding_Returns_empty_list_if_start_just_smaller_than_end() {
        assertEquals(IdeaList.empty(), Range.from('d').downToAndIncluding('e'));
    }

    @Test
    public void downToAndIncluding_Returns_empty_list_if_start_smaller_than_end() {
        assertEquals(IdeaList.empty(), Range.from('o').downToAndIncluding('v'));
    }
}
