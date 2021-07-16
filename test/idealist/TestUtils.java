package idealist;

import java.util.Optional;

import static idealist.Enumerable.isContentEqual;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestUtils {

    public static void assertContentEqual(Iterable<?> left, Iterable<?> right) {
        assertTrue(isContentEqual(left, right));
    }

    public static void assertEmpty(Iterable<?> iterable) {
        assertFalse(iterable.iterator().hasNext());
    }

    @SuppressWarnings("all") // Suppress Optional parameter warning
    public static void assertEmpty(Optional<?> optional) {
        assertFalse(optional.isPresent());
    }
}
