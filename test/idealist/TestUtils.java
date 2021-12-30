package idealist;

import java.util.List;
import java.util.Optional;

import static idealist.Enumerable.isContentEqual;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtils {

    public static void assertContentEqual(Iterable<?> left, Iterable<?> right) {
        assertTrue(isContentEqual(left, right));
    }

    public static void assertEmpty(Iterable<?> iterable) {
        assertFalse(iterable.iterator().hasNext());
    }

    public static void assertEmpty(List<?> list) {
        assertTrue(list.isEmpty());
    }

    public static void assertEmpty(IdeaList<?> ideaList) {
        assertTrue(ideaList.isEmpty());
    }

    public static void assertEmpty(MutableList<?> mutableList) {
        assertTrue(mutableList.isEmpty());
    }

    @SuppressWarnings("all") // Suppress Optional parameter warning
    public static void assertEmpty(Optional<?> optional) {
        assertFalse(optional.isPresent());
    }
}
