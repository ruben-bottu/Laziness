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
    private LazyList<Dog> dogLazyList;
    private LazyList<LocalDate> localDateLazyList;

    @Before
    public void setUp() {
        initialiseIntegers();
        initialisePeople();
        initialiseDogs();
        initialiseLocalDates();

        initialiseCollections();
        initialiseLazyLists();
    }

    private void initialiseIntegers() {
        i1 = 5;
        i2 = -256;
        i3 = 0;
        i4 = -2;
        i5 = 75;
        i6 = 4;
        i7 = 1;
        i8 = 20470;
    }

    private void initialisePeople() {
        p1 = new Person("Zoe", "Turner");
        p2 = new Person("Alex", "Johnson");
        p3 = new Person("Patricia", "Vanilla");
        p4 = new Person("Jeffie", "Allstar");
        p5 = new Person("Daenerys", "Targaryen");
        p6 = new Person("John", "Jefferson");
    }

    private void initialiseDogs() {
        d1 = new Dog(5, "Fifi");
        d2 = new Dog(1, "Lala");
        d3 = new Dog(6, "Momo");
        d4 = new Dog(4, "Shiba");
        d5 = new Dog(13, "Floof");
        d6 = new Dog(9, "Paula");
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

    private void initialiseCollections() {
        integerList = Arrays.asList(i1, i2, i3, i4, i5, i6, i7, i8);
        personList = Arrays.asList(p1, p2, p3, p4, p5, p6);
        animalList = Arrays.asList(d1, d2, d3, d4, d5, d6);
        localDateList = Arrays.asList(l1, l2, l3, l4, l5, l6, l7);
        personSet = new LinkedHashSet<>(personList);
    }

    private void initialiseLazyLists() {
        integerLazyList = LazyList.of(i1, i2, i3, i4, i5, i6, i7, i8);
        personLazyList = LazyList.of(p1, p2, p3, p4, p5, p6);
        animalLazyList = LazyList.of(d1, d2, d3, d4, d5, d6);
        dogLazyList = LazyList.of(d1, d2, d3, d4, d5, d6);
        localDateLazyList = LazyList.of(l1, l2, l3, l4, l5, l6, l7);
    }


    // Constructors and factory methods =============================================================
    @Test
    public void LazyList_of_Iterable_Creates_empty_LazyList_if_given_Iterable_is_empty() {
        assertEquals(LazyList.empty(), LazyList.of(new HashSet<>()));
    }

    @Test
    public void LazyList_of_Iterable_Creates_LazyList_of_given_Iterable() {
        LazyList<Person> currentNode = LazyList.of(personSet);
        for (Person p : personSet) {
            assertEquals(p, currentNode.value.value());
            currentNode = currentNode.tail.value();
        }
    }

    @Test
    public void LazyList_of_Gives_correct_tail_when_value_is_not_calculated() {
        assertEquals(p2, personLazyList.tail.value().value.value());
        assertEquals(p5, personLazyList.tail.value().tail.value().tail.value().tail.value().value.value());
    }

    @Test
    public void LazyList_of_elements_Creates_empty_LazyList_if_no_elements_are_given() {
        assertEquals(LazyList.empty(), LazyList.of());
    }

    @Test //TODO make covariant
    public void LazyList_of_elements_Creates_LazyList_of_given_covariant_Iterable() { // Not covariance. Needs to be an Iterable, not separate elements
        LazyList<Object> objects = LazyList.of("bla", "foo", "zaza");
        assertEquals("bla", objects.value.value());
        assertEquals("foo", objects.tail.value().value.value());
        assertEquals("zaza", objects.tail.value().tail.value().value.value());
    }

    @Test
    public void LazyList_of_elements_Creates_LazyList_of_given_objects_with_same_type_or_subtype_of_list() {
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
        assertEquals(0, LazyList.empty().size());
    }

    @Test
    public void initialiseWith_Returns_an_empty_list_if_given_length_0() {
        assertEquals(LazyList.empty(), LazyList.initialiseWith(0, index -> new Dog(index, "Momo")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void initialiseWith_Throws_exception_if_given_length_just_negative() {
        assertEquals(LazyList.empty(), LazyList.initialiseWith(-1, index -> new Dog(index, "Momo")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void initialiseWith_Throws_exception_if_given_length_negative() {
        assertEquals(LazyList.empty(), LazyList.initialiseWith(-24, index -> new Dog(index, "Momo")));
    }

    @Test
    public void initialiseWith_Initialises_LazyList_with_given_length_by_applying_generator_to_each_index() {
        LazyList<Dog> dogs = LazyList.initialiseWith(3, index -> new Dog(index, "Lassy"));
        LazyList<Dog> expected = LazyList.of(new Dog(0, "Lassy"), new Dog(1, "Lassy"), new Dog(2, "Lassy"));
        assertEquals(expected, dogs);
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

    @Test(expected = NoSuchElementException.class)
    public void first_Throws_exception_if_list_is_empty() {
        LazyList.empty().first();
    }

    @Test
    public void first_Returns_first_element_of_list() {
        assertEquals(i1, integerLazyList.first().intValue());
        assertEquals(p1, personLazyList.first());
        assertEquals(d1, animalLazyList.first());
        assertEquals(l1, localDateLazyList.first());
    }

    @Test(expected = IllegalArgumentException.class)
    public void single_Throws_exception_if_list_contains_more_than_one_element() {
        integerLazyList.single();
    }

    @Test(expected = NoSuchElementException.class)
    public void single_Throws_exception_if_list_is_empty() {
        LazyList.empty().single();
    }

    @Test
    public void single_Returns_only_element_in_LazyList() {
        assertEquals(l5, LazyList.of(l5).single());
        assertEquals(d2, LazyList.of(d2).single());
        assertEquals(p4, LazyList.of(p4).single());
    }

    @Test(expected = NoSuchElementException.class)
    public void last_Throws_exception_if_list_is_empty() {
        LazyList.empty().last();
    }

    @Test
    public void last_Returns_last_element_of_LazyList() {
        assertEquals(i8, integerLazyList.last().intValue());
        assertEquals(p6, personLazyList.last());
        assertEquals(d6, animalLazyList.last());
        assertEquals(l7, localDateLazyList.last());
    }

    @Test(expected = IllegalArgumentException.class)
    public void random_Throws_exception_if_list_is_empty() {
        LazyList.empty().random();
    }

    @Test
    public void random_Returns_random_element_from_this_list() {
        assertTrue(integerLazyList.contains(integerLazyList.random()));
        assertTrue(personLazyList.contains(personLazyList.random()));
        assertTrue(animalLazyList.contains(animalLazyList.random()));
        assertTrue(localDateLazyList.contains(localDateLazyList.random()));
    }

    @Test
    public void findFirst_Returns_null_if_list_is_empty() {
        assertNull(LazyList.empty().findFirst(e -> true));
    }

    @Test
    public void findFirst_Returns_null_if_no_element_matches_given_predicate() {
        assertNull(integerLazyList.findFirst(integer -> integer > 75 && integer < 100));
        assertNull(animalLazyList.findFirst(animal -> animal.getAge() > 13));
    }

    @Test
    public void findFirst_Returns_first_element_matching_given_predicate() {
        assertEquals(l4, localDateLazyList.findFirst(localDate -> localDate.getMonthValue() < 3));
        assertEquals(p3, personLazyList.findFirst(person -> person.getFirstName().contains("t")));
        assertEquals(i8, integerLazyList.findFirst(integer -> integer > 100).intValue());
    }

    @Test
    public void first_Returns_null_if_list_is_empty() {
        assertNull(LazyList.empty().first(e -> true));
    }

    @Test
    public void first_Returns_null_if_no_element_matches_given_predicate() {
        assertNull(integerLazyList.first(integer -> integer > 75 && integer < 100));
        assertNull(animalLazyList.first(animal -> animal.getAge() > 13));
    }

    @Test
    public void first_Returns_first_element_matching_given_predicate() {
        assertEquals(l4, localDateLazyList.first(localDate -> localDate.getMonthValue() < 3));
        assertEquals(p3, personLazyList.first(person -> person.getFirstName().contains("t")));
        assertEquals(i8, integerLazyList.first(integer -> integer > 100).intValue());
    }

    @Test
    public void indexOfFirst_predicate_Returns_minus_one_if_list_is_empty() {
        assertEquals(-1, LazyList.empty().indexOfFirst(e -> true));
    }

    @Test
    public void indexOfFirst_predicate_Returns_minus_one_if_no_element_matches_given_predicate() {
        assertEquals(-1, integerLazyList.indexOfFirst(integer -> integer == 2));
        assertEquals(-1, animalLazyList.indexOfFirst(animal -> animal.getAge() > 13));
        assertEquals(-1, personLazyList.indexOfFirst(person -> person.getFirstName().contains("b") || person.getSecondName().contains("b")));
        assertEquals(-1, localDateLazyList.indexOfFirst(localDate -> localDate.getYear() > 2020 && localDate.getYear() < 4058));
    }

    @Test
    public void indexOfFirst_predicate_Returns_index_of_first_element_that_matches_given_predicate() {
        assertEquals(0, localDateLazyList.indexOfFirst(localDate -> true));
        assertEquals(1, personLazyList.indexOfFirst(person -> person.getSecondName().contains("h")));
        assertEquals(4, animalLazyList.indexOfFirst(animal -> animal.getAge() > 6));
        assertEquals(7, integerLazyList.indexOfFirst(integer -> integer > 75));
    }

    @Test
    public void indexOfFirst_element_Returns_minus_one_if_list_is_empty() {
        assertEquals(-1, LazyList.empty().indexOfFirst(p1));
    }

    @Test
    public void indexOfFirst_element_Returns_minus_one_if_list_does_not_contain_given_element() {
        assertEquals(-1, integerLazyList.indexOfFirst(101));
        assertEquals(-1, animalLazyList.indexOfFirst(new Animal(2)));
        assertEquals(-1, personLazyList.indexOfFirst(new Person("James", "Walker")));
        assertEquals(-1, localDateLazyList.indexOfFirst(LocalDate.of(441, 3, 11)));
    }

    @Test
    public void indexOfFirst_element_Returns_index_of_first_occurrence_of_given_element_in_LazyList() {
        assertEquals(0, integerLazyList.indexOfFirst(i1));
        assertEquals(4, personLazyList.indexOfFirst(p5));
        assertEquals(2, animalLazyList.indexOfFirst(d3));
        assertEquals(6, localDateLazyList.indexOfFirst(l7));
    }

    @Test
    public void lastIndex_Returns_minus_one_if_list_is_empty() {
        assertEquals(-1, LazyList.empty().lastIndex());
        assertEquals(-1, LazyList.empty().lastIndex());
        assertEquals(-1, LazyList.empty().lastIndex());
        assertEquals(-1, LazyList.empty().lastIndex());
    }

    @Test
    public void lastIndex_Returns_index_of_last_element() {
        assertEquals(7, integerLazyList.lastIndex());
        assertEquals(5, personLazyList.lastIndex());
        assertEquals(5, animalLazyList.lastIndex());
        assertEquals(6, localDateLazyList.lastIndex());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void indices_Throws_exception_if_list_is_empty() {
        LazyList.empty().indices();
    }

    @Test
    public void indices_Returns_all_indices_of_this_list() {
        assertEquals(LazyList.rangeInclusive(0, 7), integerLazyList.indices());
        assertEquals(LazyList.rangeInclusive(0, 5), personLazyList.indices());
        assertEquals(LazyList.rangeInclusive(0, 5), animalLazyList.indices());
        assertEquals(LazyList.rangeInclusive(0, 6), localDateLazyList.indices());
    }

    @Test
    public void size_Returns_0_if_list_is_empty() {
        assertEquals(0, LazyList.empty().size());
    }

    @Test
    public void size_Returns_the_size_of_this_list() {
        assertEquals(8, integerLazyList.size());
        assertEquals(6, personLazyList.size());
        assertEquals(6, animalLazyList.size());
        assertEquals(7, localDateLazyList.size());
    }

    @Test
    public void toList_Returns_empty_List_if_this_list_is_empty() {
        assertEquals(Collections.emptyList(), LazyList.empty().toList());
    }

    @Test
    public void toList_Transforms_LazyList_into_List() {
        assertEquals(personList, LazyList.of(personList).toList());
        assertEquals(animalList, LazyList.of(animalList).toList());
    }

    @Test
    public void iterator_Returns_an_iterator_with_no_elements_if_list_is_empty() {
        Iterator<LocalDate> it = LazyList.<LocalDate>empty().iterator();
        assertFalse(it.hasNext());
    }

    @Test
    public void iterator_Returns_Iterator_that_loops_through_this_list() {
        Iterator<LocalDate> it = localDateLazyList.iterator();
        assertEquals(l1, it.next());
        assertEquals(l2, it.next());
        assertEquals(l3, it.next());
        assertEquals(l4, it.next());
        assertEquals(l5, it.next());
        assertEquals(l6, it.next());
        assertEquals(l7, it.next());
    }

    @Test
    public void iterator_Returns_Iterator_that_loops_through_this_list_extensive() {
        Iterator<Integer> it = LazyList.of(6, 0, 1, 7, 5, 4).iterator();
        assertTrue(it.hasNext());
        assertEquals(6, it.next().intValue());
        assertTrue(it.hasNext());
        assertEquals(0, it.next().intValue());
        assertTrue(it.hasNext());
        assertEquals(1, it.next().intValue());
        assertTrue(it.hasNext());
        assertEquals(7, it.next().intValue());
        assertTrue(it.hasNext());
        assertEquals(5, it.next().intValue());
        assertTrue(it.hasNext());
        assertEquals(4, it.next().intValue());
        assertFalse(it.hasNext());
    }


    // Checks =======================================================================================
    @Test
    public void equals_Returns_false_if_given_object_is_null() {
        assertNotEquals(personLazyList, null);
        assertNotEquals(null, personLazyList);
    }

    @Test
    public void equals_Returns_false_if_only_one_of_the_lists_is_empty() {
        assertNotEquals(personLazyList, LazyList.empty());
        assertNotEquals(LazyList.empty(), personLazyList);
    }

    @Test
    public void equals_Returns_false_if_this_list_is_longer_than_given_object() {
        Person np1 = new Person("Zoe", "Turner");
        Person np2 = new Person("Alex", "Johnson");
        Person np3 = new Person("Patricia", "Vanilla");
        Person np4 = new Person("Jeffie", "Allstar");
        Person np5 = new Person("Daenerys", "Targaryen");
        Person np6 = new Person("John", "Jefferson");
        Person np7 = new Person("Morgan", "Freeman");
        LazyList<Person> newPeople = LazyList.of(np1, np2, np3, np4, np5, np6, np7);

        assertNotEquals(personLazyList, newPeople);
        assertNotEquals(newPeople, personLazyList);
    }

    @Test
    public void equals_Returns_false_if_this_list_is_shorter_than_given_object() {
        Person np1 = new Person("Zoe", "Turner");
        Person np2 = new Person("Alex", "Johnson");
        Person np3 = new Person("Patricia", "Vanilla");
        Person np4 = new Person("Jeffie", "Allstar");
        Person np5 = new Person("Daenerys", "Targaryen");
        LazyList<Person> newPeople = LazyList.of(np1, np2, np3, np4, np5);

        assertNotEquals(personLazyList, newPeople);
        assertNotEquals(newPeople, personLazyList);
    }

    @Test
    public void equals_Returns_false_if_this_list_is_not_equal_to_given_object() {
        Person np1 = new Person("Zoe", "Turner");
        Person np2 = new Person("Alex", "Johnson");
        Person np3 = new Person("Patricia", "Vanilla");
        Person np4 = new Person("Jeffie", "Allstar");
        Person np5 = new Person("Daenerys", "Targaryen");
        Person np6 = new Person("John", "Jefferso"); // final n is missing
        LazyList<Person> newPeople = LazyList.of(np1, np2, np3, np4, np5, np6);

        assertNotEquals(personLazyList, newPeople);
        assertNotEquals(newPeople, personLazyList);
    }

    @Test
    public void equals_Returns_true_if_both_lists_are_empty() {
        assertEquals(LazyList.empty(), LazyList.empty());
    }

    @Test
    public void equals_Returns_true_if_this_list_is_equal_to_given_object() {
        Person np1 = new Person("Zoe", "Turner");
        Person np2 = new Person("Alex", "Johnson");
        Person np3 = new Person("Patricia", "Vanilla");
        Person np4 = new Person("Jeffie", "Allstar");
        Person np5 = new Person("Daenerys", "Targaryen");
        Person np6 = new Person("John", "Jefferson");
        LazyList<Person> newPeople = LazyList.of(np1, np2, np3, np4, np5, np6);

        assertEquals(personLazyList, newPeople);
        assertEquals(newPeople, personLazyList);
    }

    @Test
    public void contains_Returns_false_if_list_is_empty() {
        assertFalse(LazyList.empty().contains(9));
    }

    @Test
    public void contains_Returns_false_if_this_list_does_not_contain_given_element() {
        assertFalse(integerLazyList.contains(9));
        assertFalse(personLazyList.contains(new Person("Magdalena", "Yves")));
        assertFalse(animalList.contains(new Dog(4, "Shibaa")));
        assertFalse(localDateLazyList.contains(LocalDate.of(1974, 12, 24)));
    }

    @Test
    public void contains_Returns_true_if_this_list_contains_given_element() {
        assertTrue(integerLazyList.contains(5));
        assertTrue(personLazyList.contains(new Person("Alex", "Johnson")));
        assertTrue(animalList.contains(new Dog(5, "Shiba")));
        assertTrue(localDateLazyList.contains(LocalDate.of(1974, 12, 25)));
    }

    @Test
    public void containsAll_Iterable_Returns_false_if_this_list_is_empty() {
        assertFalse(LazyList.empty().containsAll(Collections.singletonList(5)));
    }

    @Test
    public void containsAll_Iterable_Returns_false_if_this_list_does_not_contain_all_given_elements() {
        Queue<Person> queue = new LinkedList<>();
        queue.offer(new Person("Jefke", "Merens"));
        queue.add(new Person("Marie", "Bosmans"));

        LazyList<Person> personList = LazyList.of(new Person("Jefke", "Merens"), new Person("Nathalie", "Hofdaal"));
        assertFalse(personList.containsAll(queue));
    }

    @Test
    public void containsAll_Iterable_Returns_true_if_this_list_contains_all_given_elements() {
        Queue<Person> queue = new LinkedList<>();
        queue.offer(new Person("Jefke", "Merens"));
        queue.add(new Person("Marie", "Bosmans"));

        LazyList<Person> personList = LazyList.of(new Person("Jefke", "Merens"), new Person("Nathalie", "Hofdaal"), new Person("Marie", "Bosmans"));
        assertTrue(personList.containsAll(queue));
    }

    @Test
    public void containsAll_LazyList_Returns_false_if_this_list_is_empty() {
        assertFalse(LazyList.empty().containsAll(LazyList.of(5)));
    }

    @Test
    public void containsAll_LazyList_Returns_false_if_list_does_not_contain_all_given_elements() {
        LazyList<Person> personList = LazyList.of(new Person("Jefke", "Merens"), new Person("Nathalie", "Hofdaal"));
        LazyList<Person> elements = LazyList.of(new Person("Jefke", "Merens"), new Person("Marie", "Bosmans"));
        assertFalse(personList.containsAll(elements));
    }

    @Test
    public void containsAll_LazyList_Returns_true_if_list_contains_all_given_elements() {
        LazyList<Person> personList = LazyList.of(new Person("Jefke", "Merens"), new Person("Nathalie", "Hofdaal"), new Person("Marie", "Bosmans"));
        LazyList<Person> elements = LazyList.of(new Person("Jefke", "Merens"), new Person("Marie", "Bosmans"));
        assertTrue(personList.containsAll(elements));
    }

    @Test
    public void isEmpty_Returns_false_if_list_is_not_empty() {
        assertFalse(localDateLazyList.isEmpty());
        assertFalse(personLazyList.isEmpty());
    }

    @Test
    public void isEmpty_Returns_true_if_list_is_empty() {
        assertTrue(LazyList.empty().isEmpty());
    }

    @Test
    public void isNested_Returns_false_if_list_is_empty() {
        assertFalse(LazyList.empty().isNested());
    }

    @Test
    public void isNested_Returns_false_if_LazyList_is_not_nested() {
        assertFalse(integerLazyList.isNested());
        assertFalse(personLazyList.isNested());
        assertFalse(animalLazyList.isNested());
        assertFalse(localDateLazyList.isNested());
    }

    @Test
    public void isNested_Returns_true_if_LazyList_is_nested_with_LazyLists() {
        LazyList<LazyList<Integer>> nestedLazyList = LazyList.of(
                LazyList.of(9, 6, 3, 5),
                LazyList.of(1),
                LazyList.of(4, 0, 3, 7, 5)
        );
        assertTrue(nestedLazyList.isNested());
    }

    @Test
    public void isNested_Returns_true_if_LazyList_is_nested_with_Iterables() {
        Set<Integer> set1 = new HashSet<>(Arrays.asList(111, -25, 2));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(1, 88));
        Set<Integer> set3 = new HashSet<>(Arrays.asList(-44, 270, 31, 73, 500));

        LazyList<Set<Integer>> nestedLazyList = LazyList.of(set1, set2, set3);
        assertTrue(nestedLazyList.isNested());
    }

    @Test
    public void all_Returns_false_if_one_or_more_elements_do_not_match_given_predicate() {
        assertFalse(integerLazyList.all(integer -> integer > 10));
        assertFalse(personLazyList.all(person -> !person.getFirstName().contains("tr")));
        assertFalse(animalLazyList.all(animal -> animal.getAge() != 4));
        assertFalse(localDateLazyList.all(localDate -> localDate.getYear() < 1000 || localDate.getYear() > 2000));
    }

    @Test
    public void all_Returns_true_if_list_is_empty() {
        assertTrue(LazyList.empty().all(e -> false));
    }

    @Test
    public void all_Returns_true_if_all_elements_match_given_predicate() {
        assertTrue(integerLazyList.all(integer -> integer > -257));
        assertTrue(personLazyList.all(person -> person.getFirstName().length() > 2));
        assertTrue(animalLazyList.all(animal -> animal.getAge() != 10));
        assertTrue(localDateLazyList.all(localDate -> localDate.getYear() != 1861));
    }

    @Test
    public void any_Returns_false_if_list_does_not_contain_elements() {
        assertFalse(LazyList.empty().any());
    }

    @Test
    public void any_Returns_true_if_list_contains_elements() {
        assertTrue(localDateLazyList.any());
        assertTrue(integerLazyList.any());
        assertTrue(LazyList.of("bla").any());
    }

    @Test
    public void any_predicate_Returns_false_if_list_is_empty() {
        assertFalse(LazyList.empty().any(e -> true));
    }

    @Test
    public void any_predicate_Returns_false_if_no_element_matches_predicate() {
        assertFalse(integerLazyList.any(integer -> integer == 3));
        assertFalse(personLazyList.any(person -> person.getFirstName().charAt(2) == 'a'));
        assertFalse(animalLazyList.any(animal -> animal.getAge() == 8));
        assertFalse(localDateLazyList.any(localDate -> localDate.getDayOfMonth() == 2));
        assertFalse(localDateLazyList.any(localDate -> localDate.getDayOfMonth() == 3));
    }

    @Test
    public void any_predicate_Returns_true_if_at_least_one_element_matches_predicate() {
        assertTrue(integerLazyList.any(integer -> integer < 10));
        assertTrue(personLazyList.any(person -> person.getSecondName().contains("o")));
        assertTrue(animalLazyList.any(animal -> animal.getAge() > 5));
        assertTrue(localDateLazyList.any(localDate -> localDate.getDayOfMonth() > 15 && localDate.getDayOfMonth() < 30));
    }

    @Test
    public void none_Returns_true_if_list_does_not_contain_elements() {
        assertTrue(LazyList.empty().none());
    }

    @Test
    public void none_Returns_false_if_list_contains_elements() {
        assertFalse(personLazyList.none());
        assertFalse(animalLazyList.none());
        assertFalse(LazyList.of("bla").none());
    }

    @Test
    public void none_predicate_Returns_true_if_list_is_empty() {
        assertTrue(LazyList.empty().none(e -> false));
    }

    @Test
    public void none_predicate_Returns_false_if_at_least_one_element_matches_predicate() {
        assertFalse(integerLazyList.none(integer -> integer < 10));
        assertFalse(personLazyList.none(person -> person.getSecondName().contains("o")));
        assertFalse(animalLazyList.none(animal -> animal.getAge() > 5));
        assertFalse(localDateLazyList.none(localDate -> localDate.getDayOfMonth() > 15 && localDate.getDayOfMonth() < 30));
    }

    @Test
    public void none_predicate_Returns_true_if_no_element_matches_predicate() {
        assertTrue(integerLazyList.none(integer -> integer == 3));
        assertTrue(personLazyList.none(person -> person.getFirstName().charAt(2) == 'a'));
        assertTrue(animalLazyList.none(animal -> animal.getAge() == 8));
        assertTrue(localDateLazyList.none(localDate -> localDate.getDayOfMonth() == 2));
        assertTrue(localDateLazyList.none(localDate -> localDate.getDayOfMonth() == 3));
    }


    // Modifiers ====================================================================================
    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_this_list_is_empty() {
        LazyList.<Person>empty().insertAt(0, personSet);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_index_just_negative() {
        personLazyList.insertAt(-1, Collections.emptyList());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_index_negative() {
        personLazyList.insertAt(-23, Collections.emptyList());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_index_just_bigger_than_upper_bound() {
        personLazyList.insertAt(6, Collections.emptyList()).toList();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_index_bigger_than_upper_bound() {
        personLazyList.insertAt(47, Collections.emptyList()).toList();
    }

    @Test
    public void insertAt_Iterable_Returns_list_if_given_list_is_empty() {
        assertEquals(localDateLazyList, localDateLazyList.insertAt(3, new HashSet<>()));
    }

    @Test
    public void insertAt_Iterable_Inserts_given_elements_at_specified_position() {
        LocalDate local1 = LocalDate.of(1002, 7, 1);
        LocalDate local2 = LocalDate.of(4014, 6, 9);
        LazyList<LocalDate> expected = LazyList.of(l1, l2, l3, local1, local2, l4, l5, l6, l7);
        assertEquals(expected, localDateLazyList.insertAt(3, Arrays.asList(local1, local2)));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_LazyList_Throws_exception_if_this_list_is_empty() {
        LazyList.<Person>empty().insertAt(0, personLazyList);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_LazyList_Throws_exception_if_index_just_negative() {
        personLazyList.insertAt(-1, LazyList.empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_LazyList_Throws_exception_if_index_negative() {
        personLazyList.insertAt(-23, LazyList.empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_LazyList_Throws_exception_if_index_just_bigger_than_upper_bound() {
        personLazyList.insertAt(6, LazyList.empty()).toList();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_LazyList_Throws_exception_if_index_bigger_than_upper_bound() {
        personLazyList.insertAt(47, LazyList.empty()).toList();
    }

    @Test
    public void insertAt_LazyList_Returns_list_if_given_list_is_empty() {
        assertEquals(localDateLazyList, localDateLazyList.insertAt(3, LazyList.empty()));
    }

    @Test
    public void insertAt_LazyList_Inserts_given_elements_at_specified_position() {
        LocalDate local1 = LocalDate.of(1002, 7, 1);
        LocalDate local2 = LocalDate.of(4014, 6, 9);
        LazyList<LocalDate> expected = LazyList.of(l1, l2, l3, local1, local2, l4, l5, l6, l7);
        assertEquals(expected, localDateLazyList.insertAt(3, LazyList.of(local1, local2)));
    }

    @Test
    public void concatWith_Iterable_Returns_given_list_if_this_list_is_empty() {
        assertEquals(personLazyList, LazyList.<Person>empty().concatWith(personSet));
    }

    @Test
    public void concatWith_Iterable_Returns_this_list_if_given_list_is_empty() {
        assertEquals(localDateLazyList, localDateLazyList.concatWith(new HashSet<>()));
    }

    @Test
    public void concatWith_Iterable_Concatenates_LazyList_with_given_Iterable() {
        Person e1 = new Person("Jan", "Janssens");
        Person e2 = new Person("Eveline", "Roberts");
        assertEquals(LazyList.of(e1, e2, p1, p2, p3, p4, p5, p6), LazyList.of(e1, e2).concatWith(personSet));
    }

    @Test
    public void concatWith_LazyList_Returns_given_list_if_this_list_is_empty() {
        assertEquals(personLazyList, LazyList.<Person>empty().concatWith(personLazyList));
    }

    @Test
    public void concatWith_LazyList_Returns_this_list_if_given_list_is_empty() {
        assertEquals(localDateLazyList, localDateLazyList.concatWith(LazyList.empty()));
    }

    /*@Test //TODO
    public void concatWith_LazyList_Concatenates_LazyList_with_given_LazyList_with_null() {
        Person e1 = new Person("Jan", "Janssens");
        Person e2 = new Person("Eveline", null);
        assertEquals(LazyList.of(null, e2, p1, p2, p3, p4, null, p6), LazyList.of(null, e2).concatWith(LazyList.of(p1, p2, p3, p4, null, p6)));
    }*/

    @Test
    public void concatWith_LazyList_Concatenates_LazyList_with_given_LazyList() {
        Person e1 = new Person("Jan", "Janssens");
        Person e2 = new Person("Eveline", "Roberts");
        assertEquals(LazyList.of(e1, e2, p1, p2, p3, p4, p5, p6), LazyList.of(e1, e2).concatWith(personLazyList));
    }

    @Test
    public void add_Returns_given_elements_if_list_is_empty() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, "Lulu");
        Dog d3 = new Dog(11, "Lala");
        assertEquals(LazyList.of(d1, d2, d3), LazyList.empty().add(d1, d2, d3));
    }

    @Test
    public void add_Returns_this_list_if_no_arguments_are_given() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, "Lulu");
        Dog d3 = new Dog(11, "Lala");
        assertEquals(LazyList.of(d1, d2, d3), LazyList.of(d1, d2, d3).add());
    }

    @Test //TODO This shouldn't work??
    public void add_Adds_the_given_elements_to_the_end_of_the_list_with_null() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, null);
        Dog d3 = new Dog(11, "Lala");
        Dog d4 = new Dog(428, "Momo");
        Dog d5 = new Dog(0, null);
        assertEquals(LazyList.of(d1, d2, d3, d4, d5), LazyList.of(d1, d2).add(d3, d4, d5));
    }

    @Test
    public void add_Adds_the_given_elements_to_the_end_of_the_list() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, "Lulu");
        Dog d3 = new Dog(11, "Lala");
        Dog d4 = new Dog(428, "Momo");
        Dog d5 = new Dog(0, "Giri");
        assertEquals(LazyList.of(d1, d2, d3, d4, d5), LazyList.of(d1, d2).add(d3, d4, d5));
    }

    @Test
    public void linkToBackOf_Iterable_Returns_given_list_if_this_list_is_empty() {
        assertEquals(personLazyList, LazyList.<Person>empty().linkToBackOf(personSet));
    }

    @Test
    public void linkToBackOf_Iterable_Returns_this_list_if_given_list_is_empty() {
        assertEquals(localDateLazyList, localDateLazyList.linkToBackOf(new HashSet<>()));
    }

    @Test
    public void linkToBackOf_Iterable_Links_this_list_to_the_back_of_the_given_Iterable() {
        Dog n1 = new Dog(11, "Lala");
        Dog n2 = new Dog(428, "Momo");
        Dog n3 = new Dog(0, "Giri");
        Set<Dog> dogs = new LinkedHashSet<>(Arrays.asList(n1, n2, n3));

        LazyList<Dog> expected = LazyList.of(n1, n2, n3, d1, d2, d3, d4, d5, d6);
        assertEquals(expected, dogLazyList.linkToBackOf(dogs));
    }

    @Test
    public void linkToBackOf_LazyList_Returns_given_list_if_this_list_is_empty() {
        assertEquals(personLazyList, LazyList.<Person>empty().linkToBackOf(personLazyList));
    }

    @Test
    public void linkToBackOf_LazyList_Returns_this_list_if_given_list_is_empty() {
        assertEquals(localDateLazyList, localDateLazyList.linkToBackOf(LazyList.empty()));
    }

    @Test
    public void linkToBackOf_LazyList_Links_this_list_to_the_back_of_the_given_Iterable() {
        Dog n1 = new Dog(11, "Lala");
        Dog n2 = new Dog(428, "Momo");
        Dog n3 = new Dog(0, "Giri");
        LazyList<Dog> newDogs = LazyList.of(n1, n2, n3);

        LazyList<Dog> expected = LazyList.of(d1, d2, d3, d4, d5, d6, n1, n2, n3);
        assertEquals(expected, newDogs.linkToBackOf(dogLazyList));
    }

    @Test
    public void addToFront_Returns_given_elements_if_list_is_empty() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, "Lulu");
        Dog d3 = new Dog(11, "Lala");
        assertEquals(LazyList.of(d1, d2, d3), LazyList.empty().addToFront(d1, d2, d3));
    }

    @Test
    public void addToFront_Returns_this_list_if_no_arguments_are_given() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, "Lulu");
        Dog d3 = new Dog(11, "Lala");
        assertEquals(LazyList.of(d1, d2, d3), LazyList.of(d1, d2, d3).addToFront());
    }

    @Test //TODO This shouldn't work??
    public void addToFront_Adds_the_given_elements_to_the_end_of_the_list_with_null() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, null);
        Dog d3 = new Dog(11, "Lala");
        Dog d4 = new Dog(428, "Momo");
        Dog d5 = new Dog(0, null);
        assertEquals(LazyList.of(d3, d4, d5, d1, d2), LazyList.of(d1, d2).addToFront(d3, d4, d5));
    }

    @Test
    public void addToFront_Adds_given_elements_to_front_of_the_list() {
        Dog n1 = new Dog(11, "Lala");
        Dog n2 = new Dog(428, "Momo");
        Dog n3 = new Dog(0, "Giri");

        LazyList<Dog> expected = LazyList.of(n1, n2, n3, d1, d2, d3, d4, d5, d6);
        assertEquals(expected, dogLazyList.addToFront(n1, n2, n3));
    }

    @Test
    public void removeFirst_Returns_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.empty().removeFirst(p1));
    }

    @Test
    public void removeFirst_Removes_first_instance_of_the_given_element_from_this_list() {
        LazyList<Person> expected = LazyList.of(p1, p2, p5, p5, p3, p4, p5);
        LazyList<Person> inputList = LazyList.of(p5, p1, p2, p5, p5, p3, p4, p5);
        assertEquals(expected, inputList.removeFirst(p5));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_list_is_empty() {
        LazyList.<String>empty().removeAt(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_index_just_negative() {
        dogLazyList.removeAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_index_negative() {
        dogLazyList.removeAt(-23);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_index_just_bigger_than_upper_bound() {
        dogLazyList.removeAt(6).toList();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_index_bigger_than_upper_bound() {
        dogLazyList.removeAt(56).toList();
    }

    @Test
    public void removeAt_Removes_the_element_at_specified_position() {
        assertEquals(LazyList.of(d2, d3, d4, d5, d6), dogLazyList.removeAt(0));
        assertEquals(LazyList.of(d1, d3, d4, d5, d6), dogLazyList.removeAt(1));
        assertEquals(LazyList.of(d1, d2, d4, d5, d6), dogLazyList.removeAt(2));
        assertEquals(LazyList.of(d1, d2, d3, d4, d5), dogLazyList.removeAt(5));
    }

    @Test
    public void removeAll_Returns_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.empty().removeAll(p1));
    }

    @Test
    public void removeAll_Removes_all_instances_of_the_given_element_from_this_list() {
        LazyList<Person> expected = LazyList.of(p1, p2, p3, p4);
        LazyList<Person> inputList = LazyList.of(p5, p1, p2, p5, p5, p3, p4, p5);
        assertEquals(expected, inputList.removeAll(p5));
    }

    @Test
    public void forEachIndexed_Performs_no_actions_if_list_is_empty() {
        List<Dog> dogs = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        LazyList.<Dog>empty().forEachIndexed((idx, cur) -> {
            indices.add(idx);
            dogs.add(cur);
        });
        assertTrue(dogs.isEmpty());
        assertTrue(indices.isEmpty());
    }

    @Test
    public void forEachIndexed_Performs_given_action_on_each_element_provided_with_its_index() {
        List<Dog> dogs = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        dogLazyList.forEachIndexed((idx, cur) -> {
            indices.add(idx);
            dogs.add(cur);
        });
        assertEquals(dogLazyList.toList(), dogs);
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5), indices);
    }

    @Test
    public void forEach_Performs_no_actions_if_list_is_empty() {
        List<Dog> dogs = new ArrayList<>();
        LazyList.<Dog>empty().forEach(dogs::add);

        assertTrue(dogs.isEmpty());
    }

    @Test
    public void forEach_Performs_given_action_on_each_element() {
        List<Dog> dogs = new ArrayList<>();
        dogLazyList.forEach(dogs::add);

        assertEquals(dogLazyList.toList(), dogs);
    }


    // List operations ==============================================================================
    @Test
    public void reduce_initialValue_Returns_initialValue_if_this_list_does_not_contain_elements() {
        assertEquals(0, LazyList.<Integer>empty().reduce(0, (acc, integer) -> 10).intValue());
    }

    @Test
    public void reduce_initialValue_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator() {
        LazyList<Integer> integers = LazyList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(13, integers.reduce(0, Integer::sum).intValue());
    }

    @Test
    public void reduce_initialValue_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator_2() {
        LazyList<LocalDate> expected = LazyList.of(l7, l6, l5, l4, l3, l2, l1);
        assertEquals(expected, localDateLazyList.reduce(LazyList.empty(), LazyList::addToFront));
    }

    @Test
    public void reduceIndexed_initialValue_Returns_initialValue_if_this_list_does_not_contain_elements() {
        assertEquals(0, LazyList.<Integer>empty().reduceIndexed(0, (index, acc, integer) -> 10).intValue());
    }

    @Test
    public void reduceIndexed_initialValue_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_provided_with_accumulator_and_index() {
        LazyList<Integer> integers = LazyList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(34, integers.reduceIndexed(0, (index, acc, integer) -> acc + index + integer).intValue());
    }

    @Test
    public void reduceIndexed_initialValue_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_provided_with_accumulator_and_index_2() {
        String expected = "0Zoe1Alex2Patricia3Jeffie4Daenerys5John";
        assertEquals(expected, personLazyList.select(Person::getFirstName).reduceIndexed("", (index, acc, firstName) -> acc + index + firstName));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void reduce_Throws_exception_if_this_list_does_not_contain_elements() {
        LazyList.<Integer>empty().reduce((acc, integer) -> 10);
    }

    @Test
    public void reduce_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator() {
        LazyList<Integer> integers = LazyList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(13, integers.reduce(Integer::sum).intValue());
    }

    @Test
    public void reduce_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator_2() {
        LazyList<String> given = LazyList.of("I", "would", "like", "to", "merge", "this", "list", "into", "one");
        String expected = "Iwouldliketomergethislistintoone";
        assertEquals(expected, given.reduce((acc, string) -> acc + string));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void reduceIndexed_Throws_exception_if_this_list_does_not_contain_elements() {
        LazyList.<Integer>empty().reduceIndexed((index, acc, integer) -> 10);
    }

    @Test
    public void reduceIndexed_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_provided_with_accumulator_and_index() {
        LazyList<Integer> integers = LazyList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(34, integers.reduceIndexed((index, acc, integer) -> acc + index + integer).intValue());
    }

    @Test
    public void reduceIndexed_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_provided_with_accumulator_and_index_2() {
        String expected = "Zoe1Alex2Patricia3Jeffie4Daenerys5John";
        assertEquals(expected, personLazyList.select(Person::getFirstName).reduceIndexed((index, acc, firstName) -> acc + index + firstName));
    }

    @Test
    public void map_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.<Person>empty().map(Person::getSecondName));
    }

    @Test
    public void map_Applies_given_transformation_on_each_element_in_list() {
        LazyList<String> expected = LazyList.of("Turner", "Johnson", "Vanilla", "Allstar", "Targaryen", "Jefferson");
        assertEquals(expected, personLazyList.map(Person::getSecondName));
    }

    @Test
    public void mapIndexed_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.<Person>empty().mapIndexed((index, person) -> person.getSecondName()));
    }

    @Test
    public void mapIndexed_Applies_given_transformation_on_each_element_provided_with_its_index() {
        LazyList<String> expectedList = LazyList.of("Turner0", "Johnson1", "Vanilla2", "Allstar3", "Targaryen4", "Jefferson5");
        assertEquals(expectedList, personLazyList.mapIndexed((index, person) -> person.getSecondName() + index));
    }

    @Test
    public void select_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.<Person>empty().select(Person::getSecondName));
    }

    @Test
    public void select_Applies_given_transformation_on_each_element_in_list() {
        LazyList<String> expected = LazyList.of("Turner", "Johnson", "Vanilla", "Allstar", "Targaryen", "Jefferson");
        assertEquals(expected, personLazyList.select(Person::getSecondName));
    }

    @Test
    public void selectIndexed_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.<Person>empty().selectIndexed((index, person) -> person.getSecondName()));
    }

    @Test
    public void selectIndexed_Applies_given_transformation_on_each_element_provided_with_its_index() {
        LazyList<String> expectedList = LazyList.of("Turner0", "Johnson1", "Vanilla2", "Allstar3", "Targaryen4", "Jefferson5");
        assertEquals(expectedList, personLazyList.selectIndexed((index, person) -> person.getSecondName() + index));
    }

    @Test
    public void where_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.<Animal>empty().where(animal -> true));
    }

    @Test
    public void where_Returns_all_elements_that_satisfy_given_predicate() {
        LazyList<Animal> expectedList = LazyList.of(d3, d5, d6);
        assertEquals(expectedList, animalLazyList.where(animal -> animal.getAge() > 5));
    }

    @Test
    public void whereIndexed_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.<Animal>empty().whereIndexed((index, animal) -> true));
    }

    @Test
    public void whereIndexed_Returns_all_elements_that_satisfy_given_predicate_provided_with_index() {
        LazyList<Animal> expectedList = LazyList.of(d1, d3, d5);
        assertEquals(expectedList, animalLazyList.whereIndexed((index, animal) -> (index % 2) == 0));
    }

    @Test
    public void filter_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.<Animal>empty().filter(animal -> true));
    }

    @Test
    public void filter_Returns_all_elements_that_satisfy_given_predicate() {
        LazyList<Animal> expectedList = LazyList.of(d3, d5, d6);
        assertEquals(expectedList, animalLazyList.filter(animal -> animal.getAge() > 5));
    }

    @Test
    public void filterIndexed_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.<Animal>empty().filterIndexed((index, animal) -> true));
    }

    @Test
    public void filterIndexed_Returns_all_elements_that_satisfy_given_predicate_provided_with_index() {
        LazyList<Animal> expectedList = LazyList.of(d1, d3, d5);
        assertEquals(expectedList, animalLazyList.filterIndexed((index, animal) -> (index % 2) == 0));
    }

    @Test
    public void findAll_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.<Animal>empty().findAll(animal -> true));
    }

    @Test
    public void findAll_Returns_all_elements_that_satisfy_given_predicate() {
        LazyList<Animal> expectedList = LazyList.of(d3, d5, d6);
        assertEquals(expectedList, animalLazyList.findAll(animal -> animal.getAge() > 5));
    }

    @Test
    public void findAllIndexed_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.<Animal>empty().findAllIndexed((index, animal) -> true));
    }

    @Test
    public void findAllIndexed_Returns_all_elements_that_satisfy_given_predicate_provided_with_index() {
        LazyList<Animal> expectedList = LazyList.of(d1, d3, d5);
        assertEquals(expectedList, animalLazyList.findAllIndexed((index, animal) -> (index % 2) == 0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void flatten_Throws_exception_if_list_is_empty() {
        LazyList.empty().flatten();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void flatten_Throws_exception_if_list_is_already_flat() {
        personLazyList.flatten();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void flatten_Throws_exception_if_list_contains_elements_that_are_not_Iterable() {
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("a", 1); map1.put("b", 2);
        Map<String, Integer> map2 = new HashMap<>();
        map2.put("c", 3); map2.put("d", 4);
        LazyList.of(map1, map2).flatten();
    }

    @Test
    public void flatten_Concatenates_all_nested_LazyLists_into_one_list() {
        LazyList<LazyList<String>> given = LazyList.of(
                LazyList.of("one"),
                LazyList.of("two", "three", "four"),
                LazyList.of("five", "six", "seven", "eight")
        );
        LazyList<String> expected = LazyList.of("one", "two", "three", "four", "five", "six", "seven", "eight");
        assertEquals(expected, given.flatten());
    }

    @Test
    public void flatten_Concatenates_all_nested_Iterables_into_one_list() {
        LazyList<Set<String>> given = LazyList.of(
                new LinkedHashSet<>(Collections.singletonList("one")),
                new LinkedHashSet<>(Arrays.asList("two", "three", "four")),
                new LinkedHashSet<>(Arrays.asList("five", "six", "seven", "eight"))
        );
        LazyList<String> expected = LazyList.of("one", "two", "three", "four", "five", "six", "seven", "eight");
        assertEquals(expected, given.flatten());
    }

    @Test
    public void flatten_Concatenates_all_nested_Iterables_into_one_list_2() {
        LazyList<Queue<Person>> given = LazyList.of(
                new LinkedList<>(Arrays.asList(p1, p2)),
                new LinkedList<>(Collections.singletonList(p3)),
                new LinkedList<>(Arrays.asList(p4, p5, p6))
        );
        assertEquals(personLazyList, given.flatten());
    }

    @Test
    public void zipWith_Triplet_Gives_correct_tail_when_value_is_not_calculated() {
        LazyList<Triplet<Person, Integer, LocalDate>> given = personLazyList.zipWith(integerLazyList, localDateLazyList);
        assertEquals(Triplet.of(p2, i2, l2), given.tail.value().value.value());
        assertEquals(Triplet.of(p5, i5, l5), given.tail.value().tail.value().tail.value().tail.value().value.value());
    }

    @Test
    public void zipWith_Triplet_Returns_empty_list_if_this_list_is_empty() {
        LocalDate ld1 = LocalDate.of(1993, 6, 1);
        LocalDate ld2 = LocalDate.of(40, 2, 8);

        Queue<LocalDate> dates = new LinkedList<>(Arrays.asList(ld1, ld2));
        Set<Integer> ints = new LinkedHashSet<>(Arrays.asList(-22, 3, 7, 1025, 0));

        assertEquals(LazyList.empty(), LazyList.empty().zipWith(dates, ints));
    }

    @Test
    public void zipWith_Triplet_Returns_empty_list_if_first_given_list_is_empty() {
        LazyList<Dog> dogs = LazyList.of(d1, d2, d3, d4);
        Set<Integer> ints = new LinkedHashSet<>(Arrays.asList(-22, 3, 7, 1025, 0));

        assertEquals(LazyList.empty(), dogs.zipWith(LazyList.empty(), ints));
    }

    @Test
    public void zipWith_Triplet_Returns_empty_list_if_second_given_list_is_empty() {
        LocalDate ld1 = LocalDate.of(1993, 6, 1);
        LocalDate ld2 = LocalDate.of(40, 2, 8);

        LazyList<Dog> dogs = LazyList.of(d1, d2, d3, d4);
        Queue<LocalDate> dates = new LinkedList<>(Arrays.asList(ld1, ld2));

        assertEquals(LazyList.empty(), dogs.zipWith(dates, LazyList.empty()));
    }

    @Test
    public void zipWith_Returns_list_of_Tuples_built_from_elements_of_this_list_and_other_Iterables_with_same_index() {
        LocalDate ld1 = LocalDate.of(1993, 6, 1);
        LocalDate ld2 = LocalDate.of(40, 2, 8);

        LazyList<Dog> dogs = LazyList.of(d1, d2, d3, d4);
        Queue<LocalDate> dates = new LinkedList<>(Arrays.asList(ld1, ld2));
        Set<Integer> ints = new LinkedHashSet<>(Arrays.asList(-22, 3, 7, 1025, 0));

        LazyList<Triplet<Dog, LocalDate, Integer>> expected = LazyList.of(
                Triplet.of(d1, ld1, -22),
                Triplet.of(d2, ld2, 3)
        );
        assertEquals(expected, dogs.zipWith(dates, ints));
    }

    @Test
    public void zipWith_Pair_Returns_empty_list_if_this_list_is_empty() {
        Queue<Person> people = new LinkedList<>(Arrays.asList(p5, p6, p2));
        assertEquals(LazyList.empty(), LazyList.empty().zipWith(people));
    }

    @Test
    public void zipWith_Pair_Returns_empty_list_if_given_list_is_empty() {
        LazyList<Dog> dogs = LazyList.of(d1, d2, d3, d4);
        assertEquals(LazyList.empty(), dogs.zipWith(LazyList.empty()));
    }

    @Test
    public void zipWith_Returns_list_of_Pairs_built_from_elements_of_this_list_and_other_Iterable_with_same_index() {
        LazyList<Dog> dogs = LazyList.of(d1, d2, d3, d4);
        Queue<Person> people = new LinkedList<>(Arrays.asList(p5, p6, p2));

        LazyList<Pair<Dog, Person>> expected = LazyList.of(
                Pair.of(d1, p5),
                Pair.of(d2, p6),
                Pair.of(d3, p2)
        );
        assertEquals(expected, dogs.zipWith(people));
    }


    // Ranges =======================================================================================
    @Test
    public void rangeInclusive_Returns_decrementing_list_if_from_bigger_than_to() {
        LazyList<Integer> expected = LazyList.of(7, 6, 5, 4, 3, 2, 1, 0, -1, -2);
        assertEquals(expected, LazyList.rangeInclusive(7, -2));
    }

    @Test
    public void rangeInclusive_Returns_decrementing_list_if_from_just_bigger_than_to() {
        assertEquals(LazyList.of(4, 3), LazyList.rangeInclusive(4, 3));
    }

    @Test
    public void rangeInclusive_Returns_singleton_list_if_from_equal_to_to() {
        assertEquals(LazyList.of(741), LazyList.rangeInclusive(741, 741));
    }

    @Test
    public void rangeInclusive_Returns_incrementing_list_if_from_smaller_than_to() {
        LazyList<Integer> expected = LazyList.of(-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(expected, LazyList.rangeInclusive(-6, 8));
    }

    @Test
    public void rangeInclusive_Returns_incrementing_list_if_from_just_smaller_than_to() {
        assertEquals(LazyList.of(-894, -893), LazyList.rangeInclusive(-894, -893));
    }

    @Test
    public void rangeExclusive_Returns_decrementing_list_if_from_bigger_than_to() {
        LazyList<Integer> expected = LazyList.of(7, 6, 5, 4, 3, 2, 1, 0, -1, -2, -3, -4);
        assertEquals(expected, LazyList.rangeExclusive(7, -5));
    }

    @Test
    public void rangeExclusive_Returns_singleton_list_if_from_just_bigger_than_to() {
        assertEquals(LazyList.of(0), LazyList.rangeExclusive(0, -1));
    }

    @Test
    public void rangeExclusive_Returns_empty_list_if_from_equal_to_to() {
        assertEquals(LazyList.empty(), LazyList.rangeExclusive(741, 741));
    }

    @Test
    public void rangeExclusive_Returns_incrementing_list_if_from_smaller_than_to() {
        LazyList<Integer> expected = LazyList.of(-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7);
        assertEquals(expected, LazyList.rangeExclusive(-6, 8));
    }

    @Test
    public void rangeExclusive_Returns_singleton_list_if_from_just_smaller_than_to_2() {
        assertEquals(LazyList.of(-894), LazyList.rangeExclusive(-894, -893));
    }

    @Test
    public void rangeLength_Returns_decrementing_list_if_given_length_negative() {
        LazyList<Integer> expected = LazyList.of(2, 1, 0, -1, -2, -3);
        assertEquals(expected, LazyList.rangeLength(2, -6));
    }

    @Test
    public void rangeLength_Returns_singleton_list_if_given_length_just_negative() {
        assertEquals(LazyList.of(0), LazyList.rangeLength(0, -1));
    }

    @Test
    public void rangeLength_Returns_empty_list_if_given_length_0() {
        assertEquals(LazyList.empty(), LazyList.rangeLength(741, 0));
    }

    @Test
    public void rangeLength_Returns_incrementing_list_if_given_length_positive() {
        LazyList<Integer> expected = LazyList.of(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4);
        assertEquals(expected, LazyList.rangeLength(-5, 10));
    }

    @Test
    public void rangeLength_Returns_singleton_list_if_given_length_just_positive() {
        assertEquals(LazyList.of(-959), LazyList.rangeLength(-959, 1));
    }

    @Test
    public void infiniteIndices_Creates_an_infinite_range_of_incrementing_indices() {
        LazyList<Integer> infiniteIndices = LazyList.infiniteIndices();
        assertEquals(0, infiniteIndices.value.value().intValue());
        assertEquals(1, infiniteIndices.tail.value().value.value().intValue());
        assertEquals(2, infiniteIndices.tail.value().tail.value().value.value().intValue());
        assertEquals(3, infiniteIndices.tail.value().tail.value().tail.value().value.value().intValue());
        assertEquals(4, infiniteIndices.tail.value().tail.value().tail.value().tail.value().value.value().intValue());
    }

    @Test
    public void withIndex_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.empty().withIndex());
    }

    @Test
    public void withIndex_Returns_a_list_of_IndexElement_pairs_where_each_element_is_bundled_together_with_its_index() {
        IndexElement<Person> pair1 = IndexElement.of(0, p1);
        IndexElement<Person> pair2 = IndexElement.of(1, p2);
        IndexElement<Person> pair3 = IndexElement.of(2, p3);
        IndexElement<Person> pair4 = IndexElement.of(3, p4);
        IndexElement<Person> pair5 = IndexElement.of(4, p5);
        IndexElement<Person> pair6 = IndexElement.of(5, p6);
        LazyList<IndexElement<Person>> expected = LazyList.of(pair1, pair2, pair3, pair4, pair5, pair6);
        assertEquals(expected, personLazyList.withIndex());
    }
}