package domain;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

public class LazyListTest {
    private int i1, i2, i3, i4, i5, i6, i7, i8;
    private Person p1, p2, p3, p4, p5, p6;
    private Dog d1, d2, d3, d4, d5, d6;
    private LocalDate l1, l2, l3, l4, l5, l6, l7;

    private Set<Person> personSet;

    private List<Integer> integerList;
    private List<Person> personList;
    private List<Animal> animalList;
    private List<LocalDate> localDateList;

    private LazyList<Integer> integerLazyList;
    private LazyList<Person> personLazyList;
    private LazyList<Animal> animalLazyList;
    private LazyList<LocalDate> localDateLazyList;

    @Before
    public void setUp() {
        initializeIntegers();
        initializePeople();
        initializeDogs();
        initializeLocalDates();

        initializePersonSet();
        initializeLists();
        initializeLazyLists();
    }

    private void initializeIntegers() {
        i1 = 5;
        i2 = -256;
        i3 = 0;
        i4 = -2;
        i5 = 75;
        i6 = 4;
        i7 = 1;
        i8 = 20470;
    }

    private void initializePeople() {
        p1 = new Person("Zoe", "Turner");
        p2 = new Person("Alex", "Johnson");
        p3 = new Person("Patricia", "Vanilla");
        p4 = new Person("Jeffie", "Allstar");
        p5 = new Person("Daenerys", "Targaryen");
        p6 = new Person("John", "Jefferson");
    }

    private void initializeDogs() {
        d1 = new Dog(5, "Fifi");
        d2 = new Dog(1, "Lala");
        d3 = new Dog(6, "Momo");
        d4 = new Dog(4, "Shiba");
        d5 = new Dog(13, "Floof");
        d6 = new Dog(9, "Paula");
    }

    private void initializeLocalDates() {
        l1 = LocalDate.of(512, 3, 31);
        l2 = LocalDate.MAX;
        l3 = LocalDate.now();
        l4 = LocalDate.of(-20, 1, 13);
        l5 = LocalDate.of(2016, 10, 1);
        l6 = LocalDate.MIN;
        l7 = LocalDate.of(1974, 12, 25);
    }

    private void initializePersonSet() {
        personSet = new LinkedHashSet<>();
        personSet.add(p1);
        personSet.add(p2);
        personSet.add(p3);
        personSet.add(p4);
        personSet.add(p5);
        personSet.add(p6);
    }

    private void initializeLists() {
        integerList = Arrays.asList(i1, i2, i3, i4, i5, i6, i7, i8);
        personList = Arrays.asList(p1, p2, p3, p4, p5, p6);
        animalList = Arrays.asList(d1, d2, d3, d4, d5, d6);
        localDateList = Arrays.asList(l1, l2, l3, l4, l5, l6, l7);
    }

    private void initializeLazyLists() {
        integerLazyList = LazyList.of(i1, i2, i3, i4, i5, i6, i7, i8);
        personLazyList = LazyList.of(p1, p2, p3, p4, p5, p6);
        animalLazyList = LazyList.of(d1, d2, d3, d4, d5, d6);
        localDateLazyList = LazyList.of(l1, l2, l3, l4, l5, l6, l7);
    }

    @Test
    public void LazyList_of_Creates_LazyList_with_given_Iterable() {
        LazyList<Person> currentNode = LazyList.of(personSet);
        for (Person p : personSet) {
            assertEquals(p, currentNode.value.value());
            currentNode = currentNode.tail.value();
        }
    }

    @Test
    public void LazyList_of_Creates_LazyList_with_given_objects() {
        LazyList<Animal> animals = LazyList.of(d1, d2, d3, d4, d5, d6);
        assertEquals(d1, animals.value.value());
        assertEquals(d2, animals.tail.value().value.value());
        assertEquals(d3, animals.tail.value().tail.value().value.value());
        assertEquals(d4, animals.tail.value().tail.value().tail.value().value.value());
        assertEquals(d5, animals.tail.value().tail.value().tail.value().tail.value().value.value());
        assertEquals(d6, animals.tail.value().tail.value().tail.value().tail.value().tail.value().value.value());
        assertNull(animals.tail.value().tail.value().tail.value().tail.value().tail.value().tail.value().value);
    }

    @Test
    public void toList_Transforms_LazyList_into_List() {
        assertEquals(personList, LazyList.of(personList).toList());
        assertEquals(animalList, LazyList.of(animalList).toList());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_index_negative() {
        localDateLazyList.get(-75);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_index_just_negative() {
        integerLazyList.get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_index_bigger_than_last_valid_index() {
        animalLazyList.get(21);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_index_just_bigger_than_last_valid_index() {
        personLazyList.get(6);
    }

    @Test
    public void get_Returns_element_at_given_index_if_index_valid() {
        assertEquals(d1, animalLazyList.get(0));
        assertEquals(d3, animalLazyList.get(2));
        assertEquals(p4, personLazyList.get(3));
        assertEquals(p6, personLazyList.get(5));
    }

    @Test
    public void first_Returns_first_element_of_LazyList() {
        assertEquals(p1, personLazyList.first());
        assertEquals(d1, animalLazyList.first());
    }

    @Test(expected = IllegalStateException.class)
    public void single_Throws_exception_if_LazyList_contains_more_than_one_element() {
        integerLazyList.single();
    }

    @Test
    public void single_Returns_first_element_if_LazyList_contains_exactly_one_element() {
        assertEquals(l5, LazyList.of(l5).single());
        assertEquals(d2, LazyList.of(d2).single());
        assertEquals(p4, LazyList.of(p4).single());
    }

    @Test
    public void concat_Concatenates_LazyList_with_given_Iterable() {
        Person e1 = new Person("Jan", "Janssens");
        Person e2 = new Person("Eveline", "Roberts");
        assertEquals(Arrays.asList(e1, e2, p1, p2, p3, p4, p5, p6), LazyList.of(e1, e2).concat(personSet).toList());
    }

    @Test
    public void any_Returns_true_if_LazyList_contains_elements() {
        assertTrue(localDateLazyList.any());
        assertTrue(integerLazyList.any());
        assertTrue(LazyList.of("bla").any());
    }

    @Test
    public void any_Returns_false_if_LazyList_does_not_contain_elements() {
        assertFalse(LazyList.of().any());
    }

    @Test
    public void none_Returns_true_if_LazyList_does_not_contain_elements() {
        assertTrue(LazyList.of().none());
    }

    @Test
    public void none_Returns_false_if_LazyList_contains_elements() {
        assertFalse(personLazyList.none());
        assertFalse(animalLazyList.none());
        assertFalse(LazyList.of("bla").none());
    }

    @Test
    public void map_Applies_given_transformation_on_each_element_in_LazyList() {
        List<String> expectedList = Arrays.asList("Turner", "Johnson", "Vanilla", "Allstar", "Targaryen", "Jefferson");
        assertEquals(expectedList, personLazyList.map(Person::getSecondName).toList());
    }

    @Test
    public void filter_Removes_elements_that_do_not_satisfy_given_predicate() {
        List<Animal> expectedList = Arrays.asList(d3, d5, d6);
        assertEquals(expectedList, animalLazyList.filter(animal -> animal.getAge() > 5).toList());
    }

    @Test
    public void iterator_Returns_Iterator_that_loops_through_LazyList() {
        Iterator<LocalDate> it = localDateLazyList.iterator();
        assertEquals(l1, it.next());
        assertEquals(l2, it.next());
        assertEquals(l3, it.next());
        assertEquals(l4, it.next());
        assertEquals(l5, it.next());
        assertEquals(l6, it.next());
        assertEquals(l7, it.next());
    }
}