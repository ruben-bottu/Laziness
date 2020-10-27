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


    // Constructors and factory methods =============================================================
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
    public void empty_Returns_an_empty_LazyList() {
        assertTrue(LazyList.empty().isEmpty());
    }


    // Getters ======================================================================================
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
        assertEquals(i1, integerLazyList.first().intValue());
        assertEquals(p1, personLazyList.first());
        assertEquals(d1, animalLazyList.first());
        assertEquals(l1, localDateLazyList.first());
    }

    @Test(expected = IllegalStateException.class)
    public void single_Throws_exception_if_LazyList_contains_more_than_one_element() {
        integerLazyList.single();
    }

    @Test(expected = IllegalStateException.class)
    public void single_Throws_exception_if_LazyList_is_empty() {
        LazyList.of().single();
    }

    @Test
    public void single_Returns_only_element_in_LazyList() {
        assertEquals(l5, LazyList.of(l5).single());
        assertEquals(d2, LazyList.of(d2).single());
        assertEquals(p4, LazyList.of(p4).single());
    }

    @Test
    public void last_Returns_last_element_of_LazyList() {
        assertEquals(i8, integerLazyList.last().intValue());
        assertEquals(p6, personLazyList.last());
        assertEquals(d6, animalLazyList.last());
        assertEquals(l7, localDateLazyList.last());
    }

    @Test
    public void random_Returns_random_element_of_LazyList() {
        assertTrue(integerLazyList.contains(integerLazyList.random()));
        assertTrue(personLazyList.contains(personLazyList.random()));
        assertTrue(animalLazyList.contains(animalLazyList.random()));
        assertTrue(localDateLazyList.contains(localDateLazyList.random()));
    }

    @Test
    public void find_Returns_first_element_matching_given_predicate() {
        assertEquals(l4, localDateLazyList.find(localDate -> localDate.getMonthValue() < 3));
        assertEquals(p3, personLazyList.find(person -> person.getFirstName().contains("t")));
        assertEquals(i8, integerLazyList.find(integer -> integer > 100).intValue());
    }

    @Test
    public void find_Returns_null_if_no_element_matches_given_predicate() {
        assertNull(integerLazyList.find(integer -> integer > 75 && integer < 100));
        assertNull(animalLazyList.find(animal -> animal.getAge() > 13));
    }

    @Test
    public void indexOfFirst_Returns_index_of_first_occurrence_of_given_element_in_LazyList() {
        assertEquals(0, localDateLazyList.indexOfFirst(localDate -> localDate.getDayOfMonth() < 50));
        assertEquals(1, personLazyList.indexOfFirst(person -> person.getSecondName().contains("h")));
        assertEquals(4, animalLazyList.indexOfFirst(animal -> animal.getAge() > 6));
        assertEquals(7, integerLazyList.indexOfFirst(integer -> integer > 75));
    }

    @Test
    public void indexOfFirst_Returns_minus_one_if_LazyList_does_not_contain_given_element_() {
        assertEquals(-1, integerLazyList.indexOfFirst(integer -> integer == 2));
        assertEquals(-1, animalLazyList.indexOfFirst(animal -> animal.getAge() > 13));
        assertEquals(-1, personLazyList.indexOfFirst(person -> person.getFirstName().contains("b") || person.getSecondName().contains("b")));
        assertEquals(-1, localDateLazyList.indexOfFirst(localDate -> localDate.getYear() > 2020 && localDate.getYear() < 4058));
    }

    @Test
    public void indexOf_Returns_index_of_first_occurrence_of_given_element_in_LazyList() {
        assertEquals(0, integerLazyList.indexOfFirst(i1));
        assertEquals(4, personLazyList.indexOfFirst(p5));
        assertEquals(2, animalLazyList.indexOfFirst(d3));
        assertEquals(6, localDateLazyList.indexOfFirst(l7));
    }

    @Test
    public void indexOf_Returns_minus_one_if_LazyList_does_not_contain_given_element_() {
        assertEquals(-1, integerLazyList.indexOfFirst(101));
        assertEquals(-1, animalLazyList.indexOfFirst(new Animal(2)));
        assertEquals(-1, personLazyList.indexOfFirst(new Person("James", "Walker")));
        assertEquals(-1, localDateLazyList.indexOfFirst(LocalDate.of(441, 3, 11)));
    }

    @Test
    public void lastIndex_Returns_the_index_of_the_last_element() {
        assertEquals(7, integerLazyList.lastIndex());
        assertEquals(5, personLazyList.lastIndex());
        assertEquals(5, animalLazyList.lastIndex());
        assertEquals(6, localDateLazyList.lastIndex());
    }

    @Test
    public void size_Returns_the_size_of_LazyList() {
        assertEquals(8, integerLazyList.size());
        assertEquals(6, personLazyList.size());
        assertEquals(6, animalLazyList.size());
        assertEquals(7, localDateLazyList.size());
    }

    @Test
    public void toList_Transforms_LazyList_into_List() {
        assertEquals(personList, LazyList.of(personList).toList());
        assertEquals(animalList, LazyList.of(animalList).toList());
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


    // Checks =======================================================================================
    @Test
    public void contains_Returns_true_if_LazyList_contains_the_given_element() {
        assertTrue(integerLazyList.contains(5));
        assertTrue(personLazyList.contains(new Person("Alex", "Johnson")));
        assertTrue(animalList.contains(new Dog(5, "Shiba")));
        assertTrue(localDateLazyList.contains(LocalDate.of(1974, 12, 25)));
    }

    @Test
    public void contains_Returns_false_if_LazyList_does_not_contain_the_given_element() {
        assertFalse(integerLazyList.contains(9));
        assertFalse(personLazyList.contains(new Person("Magdalena", "Yves")));
        assertFalse(animalList.contains(new Dog(4, "Shibaa")));
        assertFalse(localDateLazyList.contains(LocalDate.of(1974, 12, 24)));
    }

    @Test
    public void containsAll_Returns_true_if_LazyList_contains_all_given_elements() {
        Queue<Person> queue = new LinkedList<>();
        queue.offer(new Person("Jefke", "Merens"));
        queue.add(new Person("Marie", "Bosmans"));

        LazyList<Person> personList = LazyList.of(new Person("Jefke", "Merens"), new Person("Nathalie", "Hofdaal"), new Person("Marie", "Bosmans"));
        assertTrue(personList.containsAll(queue));
    }

    @Test
    public void containsAll_Returns_false_if_LazyList_does_not_contain_all_given_elements() {
        Queue<Person> queue = new LinkedList<>();
        queue.offer(new Person("Jefke", "Merens"));
        queue.add(new Person("Marie", "Bosmans"));

        LazyList<Person> personList = LazyList.of(new Person("Jefke", "Merens"), new Person("Nathalie", "Hofdaal"));
        assertFalse(personList.containsAll(queue));
    }

    @Test
    public void isEmpty_Returns_true_if_LazyList_is_empty() {
        assertTrue(LazyList.of().isEmpty());
    }

    @Test
    public void isEmpty_Returns_false_if_LazyList_is_not_empty() {
        assertFalse(localDateLazyList.isEmpty());
        assertFalse(personLazyList.isEmpty());
    }

    @Test
    public void isNested_Returns_true_if_LazyList_is_nested() {
        LazyList<LazyList<Integer>> nestedLazyList = LazyList.of(
                LazyList.of(9, 6, 3, 5),
                LazyList.of(1),
                LazyList.of(4, 0, 3, 7, 5)
        );
        assertTrue(nestedLazyList.isNested());
    }

    @Test
    public void isNested_Returns_false_if_LazyList_is_not_nested() {
        assertFalse(integerLazyList.isNested());
        assertFalse(personLazyList.isNested());
        assertFalse(animalLazyList.isNested());
        assertFalse(localDateLazyList.isNested());
    }

    @Test
    public void all_Returns_true_if_all_elements_match_given_predicate() {
        assertTrue(integerLazyList.all(integer -> integer > -257));
        assertTrue(personLazyList.all(person -> person.getFirstName().length() > 2));
        assertTrue(animalLazyList.all(animal -> animal.getAge() != 10));
        assertTrue(localDateLazyList.all(localDate -> localDate.getYear() != 1861));
    }

    @Test
    public void all_Returns_false_if_one_or_more_elements_do_not_match_given_predicate() {
        assertFalse(integerLazyList.all(integer -> integer > 10));
        assertFalse(personLazyList.all(person -> !person.getFirstName().contains("tr")));
        assertFalse(animalLazyList.all(animal -> animal.getAge() != 4));
        assertFalse(localDateLazyList.all(localDate -> localDate.getYear() < 1000 || localDate.getYear() > 2000));
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
    public void any_Returns_true_if_at_least_one_element_matches_predicate() {
        assertTrue(integerLazyList.any(integer -> integer < 10));
        assertTrue(personLazyList.any(person -> person.getSecondName().contains("o")));
        assertTrue(animalLazyList.any(animal -> animal.getAge() > 5));
        assertTrue(localDateLazyList.any(localDate -> localDate.getDayOfMonth() > 15 && localDate.getDayOfMonth() < 30));
    }

    @Test
    public void any_Returns_false_if_no_element_matches_predicate() {
        assertFalse(integerLazyList.any(integer -> integer == 3));
        assertFalse(personLazyList.any(person -> person.getFirstName().charAt(2) == 'a'));
        assertFalse(animalLazyList.any(animal -> animal.getAge() == 8));
        assertFalse(localDateLazyList.any(localDate -> localDate.getDayOfMonth() == 3));
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
    public void none_Returns_true_if_no_element_matches_predicate() {
        assertTrue(integerLazyList.none(integer -> integer == 3));
        assertTrue(personLazyList.none(person -> person.getFirstName().charAt(2) == 'a'));
        assertTrue(animalLazyList.none(animal -> animal.getAge() == 8));
        assertTrue(localDateLazyList.none(localDate -> localDate.getDayOfMonth() == 3));
    }

    @Test
    public void none_Returns_false_if_at_least_one_element_matches_predicate() {
        assertFalse(integerLazyList.none(integer -> integer < 10));
        assertFalse(personLazyList.none(person -> person.getSecondName().contains("o")));
        assertFalse(animalLazyList.none(animal -> animal.getAge() > 5));
        assertFalse(localDateLazyList.none(localDate -> localDate.getDayOfMonth() > 15 && localDate.getDayOfMonth() < 30));
    }


    // Modifiers ====================================================================================
    @Test
    public void concat_Concatenates_LazyList_with_given_Iterable() {
        Person e1 = new Person("Jan", "Janssens");
        Person e2 = new Person("Eveline", "Roberts");
        assertEquals(Arrays.asList(e1, e2, p1, p2, p3, p4, p5, p6), LazyList.of(e1, e2).concat(personSet).toList());
    }

    /*@Test
    public void concat_Concatenates_LazyList_with_given_LazyList() {
        Person e1 = new Person("Jan", "Janssens");
        Person e2 = new Person("Eveline", "Roberts");
        assertEquals(Arrays.asList(e1, e2, p1, p2, p3, p4, p5, p6), LazyList.of(e1, e2).concat(personLazyList).toList());
    }*/


    // List operations ==============================================================================
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


    // Ranges =======================================================================================
}