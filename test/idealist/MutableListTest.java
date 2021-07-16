package idealist;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static idealist.TestUtils.assertContentEqual;
import static java.util.Arrays.asList;

public class MutableListTest {

    private LocalDate l1, l2, l3, l4, l5, l6, l7;

    @Before
    public void setUp() {
        initialiseLocalDates();
    }

    private void initialiseLocalDates() {
        l1 = LocalDate.of(512, 3, 31);
        l2 = LocalDate.MAX;
        l3 = LocalDate.of(-897, 5, 5);
        l4 = LocalDate.of(-20, 1, 13);
        l5 = LocalDate.of(2016, 10, 1);
        l6 = LocalDate.MIN;
        l7 = LocalDate.of(1974, 12, 25);
    }

    @Test
    public void of_elements_Creates_MutableList_of_given_elements() {
        Lazy<LocalDate> lazy1 = Lazy.of(() -> l1);
        Lazy<LocalDate> lazy3 = Lazy.of(() -> l3);
        Lazy<LocalDate> lazy5 = Lazy.of(() -> l5);
        Lazy<LocalDate> lazy6 = Lazy.of(() -> l6);

        MutableList<LocalDate> mutableList = MutableList.of(lazy3, lazy1, lazy6, lazy5, lazy1);
        List<Lazy<LocalDate>> expected = asList(lazy3, lazy1, lazy6, lazy5, lazy1);
        assertContentEqual(expected, mutableList);
    }
}
