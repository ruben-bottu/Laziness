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

        initialisePersonSet();
        initialiseLists();
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

    private void initialisePersonSet() {
        personSet = new LinkedHashSet<>();
        personSet.add(p1);
        personSet.add(p2);
        personSet.add(p3);
        personSet.add(p4);
        personSet.add(p5);
        personSet.add(p6);
    }

    private void initialiseLists() {
        integerList = Arrays.asList(i1, i2, i3, i4, i5, i6, i7, i8);
        personList = Arrays.asList(p1, p2, p3, p4, p5, p6);
        animalList = Arrays.asList(d1, d2, d3, d4, d5, d6);
        localDateList = Arrays.asList(l1, l2, l3, l4, l5, l6, l7);
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
    public void LazyList_of_Creates_LazyList_with_given_Iterable() {
        LazyList<Person> currentNode = LazyList.of(personSet);
        for (Person p : personSet) {
            assertEquals(p, currentNode.value.value());
            currentNode = currentNode.tail.value();
        }
    }

    @Test
    public void LazyList_of_Creates_empty_list_if_given_Iterable_is_empty() {
        assertEquals(LazyList.empty(), LazyList.of(new HashSet<>()));
    }

    @Test //TODO make covariant
    public void LazyList_of_Creates_LazyList_with_given_covariant_Iterable() { // Not covariance. Needs to be an Iterable, not separate elements
        LazyList<Object> objects = LazyList.of("bla", "foo", "zaza");
        assertEquals("bla", objects.value.value());
        assertEquals("foo", objects.tail.value().value.value());
        assertEquals("zaza", objects.tail.value().tail.value().value.value());
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
    public void LazyList_of_Creates_empty_list_if_no_elements_are_given() {
        assertEquals(LazyList.empty(), LazyList.of());
    }

    @Test
    public void empty_Returns_an_empty_LazyList() {
        assertEquals(0, LazyList.empty().size());
    }

    @Test
    public void initialiseWith_Initialises_the_list_with_given_length_by_applying_generator_to_each_index() {
        LazyList<Dog> dogs = LazyList.initialiseWith(3, index -> new Dog(index, "Lassy"));
        LazyList<Dog> expected = LazyList.of(new Dog(0, "Lassy"), new Dog(1, "Lassy"), new Dog(2, "Lassy"));
        assertEquals(expected, dogs);
    }

    @Test
    public void initialiseWith_Returns_an_empty_list_if_length_0() {
        assertEquals(LazyList.empty(), LazyList.initialiseWith(0, index -> new Dog(index, "Momo")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void initialiseWith_Throws_exception_list_if_length_just_negative() {
        assertEquals(LazyList.empty(), LazyList.initialiseWith(-1, index -> new Dog(index, "Momo")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void initialiseWith_Throws_exception_list_if_length_negative() {
        assertEquals(LazyList.empty(), LazyList.initialiseWith(-24, index -> new Dog(index, "Momo")));
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

    @Test(expected = NoSuchElementException.class)
    public void first_Throws_exception_if_list_is_empty() {
        LazyList.empty().first();
    }

    @Test(expected = IllegalArgumentException.class)
    public void single_Throws_exception_if_LazyList_contains_more_than_one_element() {
        integerLazyList.single();
    }

    @Test(expected = NoSuchElementException.class)
    public void single_Throws_exception_if_LazyList_is_empty() {
        LazyList.empty().single();
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

    @Test(expected = NoSuchElementException.class)
    public void last_Throws_exception_if_list_is_empty() {
        LazyList.empty().last();
    }

    @Test
    public void random_Returns_random_element_of_LazyList() {
        assertTrue(integerLazyList.contains(integerLazyList.random()));
        assertTrue(personLazyList.contains(personLazyList.random()));
        assertTrue(animalLazyList.contains(animalLazyList.random()));
        assertTrue(localDateLazyList.contains(localDateLazyList.random()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void random_Throws_exception_if_list_is_empty() {
        LazyList.empty().random();
    }

    @Test
    public void findFirst_Returns_first_element_matching_given_predicate() {
        assertEquals(l4, localDateLazyList.findFirst(localDate -> localDate.getMonthValue() < 3));
        assertEquals(p3, personLazyList.findFirst(person -> person.getFirstName().contains("t")));
        assertEquals(i8, integerLazyList.findFirst(integer -> integer > 100).intValue());
    }

    @Test
    public void findFirst_Returns_null_if_no_element_matches_given_predicate() {
        assertNull(integerLazyList.findFirst(integer -> integer > 75 && integer < 100));
        assertNull(animalLazyList.findFirst(animal -> animal.getAge() > 13));
    }

    @Test
    public void findFirst_Returns_null_if_list_is_empty() {
        assertNull(LazyList.of(5).tail.value().findFirst(integer -> integer > 75 && integer < 100));
        assertNull(LazyList.of(d1).tail.value().findFirst(animal -> animal.getAge() > 13));
    }

    @Test
    public void first_Returns_first_element_matching_given_predicate() {
        assertEquals(l4, localDateLazyList.first(localDate -> localDate.getMonthValue() < 3));
        assertEquals(p3, personLazyList.first(person -> person.getFirstName().contains("t")));
        assertEquals(i8, integerLazyList.first(integer -> integer > 100).intValue());
    }

    @Test
    public void first_Returns_null_if_no_element_matches_given_predicate() {
        assertNull(integerLazyList.first(integer -> integer > 75 && integer < 100));
        assertNull(animalLazyList.first(animal -> animal.getAge() > 13));
    }

    @Test
    public void first_Returns_null_if_list_is_empty() {
        assertNull(LazyList.of(5).tail.value().first(integer -> integer > 75 && integer < 100));
        assertNull(LazyList.of(d1).tail.value().first(animal -> animal.getAge() > 13));
    }

    @Test
    public void indexOfFirst_Returns_index_of_first_occurrence_of_given_element_in_LazyList() {
        assertEquals(0, localDateLazyList.indexOfFirst(localDate -> localDate.getDayOfMonth() < 50));
        assertEquals(1, personLazyList.indexOfFirst(person -> person.getSecondName().contains("h")));
        assertEquals(4, animalLazyList.indexOfFirst(animal -> animal.getAge() > 6));
        assertEquals(7, integerLazyList.indexOfFirst(integer -> integer > 75));
    }

    @Test
    public void indexOfFirst_Returns_minus_one_if_LazyList_does_not_contain_given_element() {
        assertEquals(-1, integerLazyList.indexOfFirst(integer -> integer == 2));
        assertEquals(-1, animalLazyList.indexOfFirst(animal -> animal.getAge() > 13));
        assertEquals(-1, personLazyList.indexOfFirst(person -> person.getFirstName().contains("b") || person.getSecondName().contains("b")));
        assertEquals(-1, localDateLazyList.indexOfFirst(localDate -> localDate.getYear() > 2020 && localDate.getYear() < 4058));
    }

    @Test
    public void indexOfFirst_Returns_minus_one_if_list_is_empty() {
        assertEquals(-1, LazyList.of(0).tail.value().indexOfFirst(integer -> integer == 2));
        assertEquals(-1, LazyList.of(d1).tail.value().indexOfFirst(animal -> animal.getAge() > 13));
    }

    @Test
    public void indexOfFirst_element_Returns_index_of_first_occurrence_of_given_element_in_LazyList() {
        assertEquals(0, integerLazyList.indexOfFirst(i1));
        assertEquals(4, personLazyList.indexOfFirst(p5));
        assertEquals(2, animalLazyList.indexOfFirst(d3));
        assertEquals(6, localDateLazyList.indexOfFirst(l7));
    }

    @Test
    public void indexOfFirst_element_Returns_minus_one_if_LazyList_does_not_contain_given_element_() {
        assertEquals(-1, integerLazyList.indexOfFirst(101));
        assertEquals(-1, animalLazyList.indexOfFirst(new Animal(2)));
        assertEquals(-1, personLazyList.indexOfFirst(new Person("James", "Walker")));
        assertEquals(-1, localDateLazyList.indexOfFirst(LocalDate.of(441, 3, 11)));
    }

    @Test
    public void indexOfFirst_element_Returns_minus_one_if_list_is_empty() {
        assertEquals(-1, LazyList.of(0).tail.value().indexOfFirst(101));
        assertEquals(-1, LazyList.of(d1).tail.value().indexOfFirst(new Dog(2, "Momo")));
    }

    @Test
    public void lastIndex_Returns_the_index_of_the_last_element() {
        assertEquals(7, integerLazyList.lastIndex());
        assertEquals(5, personLazyList.lastIndex());
        assertEquals(5, animalLazyList.lastIndex());
        assertEquals(6, localDateLazyList.lastIndex());
    }

    @Test
    public void lastIndex_Returns_minus_one_if_list_is_empty() {
        assertEquals(-1, LazyList.empty().lastIndex());
        assertEquals(-1, LazyList.empty().lastIndex());
        assertEquals(-1, LazyList.empty().lastIndex());
        assertEquals(-1, LazyList.empty().lastIndex());
    }

    @Test
    public void indices_Returns_a_range_of_all_indices_in_this_list() {
        assertEquals(LazyList.rangeInclusive(0, 7), integerLazyList.indices());
        assertEquals(LazyList.rangeInclusive(0, 5), personLazyList.indices());
        assertEquals(LazyList.rangeInclusive(0, 5), animalLazyList.indices());
        assertEquals(LazyList.rangeInclusive(0, 6), localDateLazyList.indices());
    }

    @Test
    public void indices_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(LazyList.empty(), LazyList.empty().indices());
        assertEquals(LazyList.empty(), LazyList.empty().indices());
        assertEquals(LazyList.empty(), LazyList.empty().indices());
        assertEquals(LazyList.empty(), LazyList.empty().indices());
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

    @Test
    public void iterator_Returns_Iterator_that_loops_through_LazyList_2() {
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
    public void equals_Returns_true_if_this_list_is_equal_to_the_given_object() {
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
    public void equals_Returns_false_if_this_list_is_not_equal_to_the_given_object() {
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
    public void containsAll_Returns_true_if_list_contains_all_given_LazyList_elements() {
        LazyList<Person> personList = LazyList.of(new Person("Jefke", "Merens"), new Person("Nathalie", "Hofdaal"), new Person("Marie", "Bosmans"));
        LazyList<Person> elements = LazyList.of(new Person("Jefke", "Merens"), new Person("Marie", "Bosmans"));
        assertTrue(personList.containsAll(elements));
    }

    @Test
    public void containsAll_Returns_false_if_list_does_not_contain_all_given_LazyList_elements() {
        LazyList<Person> personList = LazyList.of(new Person("Jefke", "Merens"), new Person("Nathalie", "Hofdaal"));
        LazyList<Person> elements = LazyList.of(new Person("Jefke", "Merens"), new Person("Marie", "Bosmans"));
        assertFalse(personList.containsAll(elements));
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
    public void isNested_Returns_true_if_LazyList_is_nested_2() {
        Set<Integer> set1 = new HashSet<>(Arrays.asList(111, -25, 2));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(1, 88));
        Set<Integer> set3 = new HashSet<>(Arrays.asList(-44, 270, 31, 73, 500));

        LazyList<Set<Integer>> nestedLazyList = LazyList.of(set1, set2, set3);
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
    public void isNested_Returns_false_if_list_is_empty() {
        assertFalse(LazyList.empty().isNested());
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
        assertFalse(localDateLazyList.any(localDate -> localDate.getDayOfMonth() == 2));
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
        assertTrue(localDateLazyList.none(localDate -> localDate.getDayOfMonth() == 2));
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
    public void insert_Inserts_given_elements_at_specified_position() {
        LocalDate local1 = LocalDate.of(1002, 7, 1);
        LocalDate local2 = LocalDate.of(4014, 6, 9);
        LazyList<LocalDate> expected = LazyList.of(l1, l2, l3, local1, local2, l4, l5, l6, l7);
        assertEquals(expected.toList(), localDateLazyList.insert(3, Arrays.asList(local1, local2)).toList());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insert_Throws_Exception_if_index_just_negative() {
        personLazyList.insert(-1, personSet);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insert_Throws_Exception_if_index_negative() {
        personLazyList.insert(-23, personSet);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insert_Throws_Exception_if_index_just_bigger_than_upper_bound() {
        personLazyList.insert(6, personSet).toList();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insert_Throws_Exception_if_index_bigger_than_upper_bound() {
        personLazyList.insert(47, personSet).toList();
    }

    @Test
    public void insert_Inserts_given_LazyList_elements_at_specified_position() {
        LocalDate local1 = LocalDate.of(1002, 7, 1);
        LocalDate local2 = LocalDate.of(4014, 6, 9);
        LazyList<LocalDate> expected = LazyList.of(l1, l2, l3, local1, local2, l4, l5, l6, l7);
        assertEquals(expected.toList(), localDateLazyList.insert(3, LazyList.of(local1, local2)).toList());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insert_Throws_Exception_if_index_just_negative_Lazy() {
        personLazyList.insert(-1, LazyList.empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insert_Throws_Exception_if_index_negative_Lazy() {
        personLazyList.insert(-23, LazyList.empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insert_Throws_Exception_if_index_just_bigger_than_upper_bound_Lazy() {
        personLazyList.insert(6, LazyList.empty()).toList();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insert_Throws_Exception_if_index_bigger_than_upper_bound_Lazy() {
        personLazyList.insert(47, LazyList.empty()).toList();
    }

    @Test
    public void concatWith_Concatenates_LazyList_with_given_Iterable() {
        Person e1 = new Person("Jan", "Janssens");
        Person e2 = new Person("Eveline", "Roberts");
        assertEquals(Arrays.asList(e1, e2, p1, p2, p3, p4, p5, p6), LazyList.of(e1, e2).concatWith(personSet).toList());
    }

    @Test
    public void concatWith_Concatenates_LazyList_with_given_LazyList() {
        Person e1 = new Person("Jan", "Janssens");
        Person e2 = new Person("Eveline", "Roberts");
        assertEquals(Arrays.asList(e1, e2, p1, p2, p3, p4, p5, p6), LazyList.of(e1, e2).concatWith(personLazyList).toList());
    }

    @Test
    public void add_Adds_the_given_elements_to_the_end_of_the_list() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, "Lulu");
        Dog d3 = new Dog(11, "Lala");
        Dog d4 = new Dog(428, "Momo");
        Dog d5 = new Dog(0, "Giri");
        assertEquals(Arrays.asList(d1, d2, d3, d4, d5), LazyList.of(d1, d2).add(d3, d4, d5).toList());
    }

    @Test
    public void linkToBackOf_Concatenates_to_the_back_of_the_given_Iterable() {
        Dog n1 = new Dog(11, "Lala");
        Dog n2 = new Dog(428, "Momo");
        Dog n3 = new Dog(0, "Giri");
        Set<Dog> dogs = new LinkedHashSet<>(Arrays.asList(n1, n2, n3));

        LazyList<Dog> expected = LazyList.of(n1, n2, n3, d1, d2, d3, d4, d5, d6);
        assertEquals(expected.toList(), dogLazyList.linkToBackOf(dogs).toList());
    }

    @Test
    public void linkToBackOf_Concatenates_to_the_back_of_the_given_LazyList() {
        Dog n1 = new Dog(11, "Lala");
        Dog n2 = new Dog(428, "Momo");
        Dog n3 = new Dog(0, "Giri");
        LazyList<Dog> newDogs = LazyList.of(n1, n2, n3);

        LazyList<Dog> expected = LazyList.of(d1, d2, d3, d4, d5, d6, n1, n2, n3);
        assertEquals(expected.toList(), newDogs.linkToBackOf(dogLazyList).toList());
    }

    @Test
    public void addToFront_Adds_given_elements_to_front_of_the_list() {
        Dog n1 = new Dog(11, "Lala");
        Dog n2 = new Dog(428, "Momo");
        Dog n3 = new Dog(0, "Giri");

        LazyList<Dog> expected = LazyList.of(n1, n2, n3, d1, d2, d3, d4, d5, d6);
        assertEquals(expected.toList(), dogLazyList.addToFront(n1, n2, n3).toList());
    }

    @Test
    public void removeFirst_Removes_first_instance_of_the_given_element_from_the_list() {
        LazyList<Person> expected = LazyList.of(p1, p2, p5, p5, p3, p4, p5);
        LazyList<Person> inputList = LazyList.of(p5, p1, p2, p5, p5, p3, p4, p5);
        assertEquals(expected.toList(), inputList.removeFirst(p5).toList());
    }

    @Test
    public void removeFirst_Returns_empty_list_if_list_empty() {
        assertEquals(LazyList.empty().toList(), LazyList.empty().removeFirst(p1).toList());
    }

    @Test
    public void removeAll_Removes_all_instances_of_the_given_element_from_the_list() {
        LazyList<Person> expected = LazyList.of(p1, p2, p3, p4);
        LazyList<Person> inputList = LazyList.of(p5, p1, p2, p5, p5, p3, p4, p5);
        assertEquals(expected.toList(), inputList.removeAll(p5).toList());
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
    public void forEach_Performs_given_action_on_each_element() {
        List<Dog> dogs = new ArrayList<>();
        dogLazyList.forEach(dogs::add); // USE FOREACH NOT FOREACHINDEXED

        assertEquals(dogLazyList.toList(), dogs);
    }


    // List operations ==============================================================================
    @Test
    public void reduce_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator() {
        LazyList<Integer> integers = LazyList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(13, integers.reduce(0, Integer::sum).intValue());
    }

    @Test
    public void reduce_Returns_0_if_this_list_does_not_contain_elements() {
        LazyList<Integer> integers = LazyList.empty();
        assertEquals(0, integers.reduce(0, Integer::sum).intValue());
    }

    @Test
    public void reduce_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator_3() {
        LazyList<LocalDate> expected = LazyList.of(l7, l6, l5, l4, l3, l2, l1);
        assertEquals(expected, localDateLazyList.reduce(LazyList.empty(), LazyList::addToFront));
    }

    @Test
    public void reduceIndexed_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator_provided_with_index() {
        String expected = "0Zoe1Alex2Patricia3Jeffie4Daenerys5John";
        assertEquals(expected, personLazyList.select(Person::getFirstName).reduceIndexed("", (index, acc, current) -> acc + index + current));
    }

    @Test
    public void map_Applies_given_transformation_on_each_element_in_LazyList() {
        List<String> expectedList = Arrays.asList("Turner", "Johnson", "Vanilla", "Allstar", "Targaryen", "Jefferson");
        assertEquals(expectedList, personLazyList.map(Person::getSecondName).toList());
    }

    @Test
    public void mapIndexed_Applies_given_transformation_on_each_element_provided_with_its_index() {
        List<String> expectedList = Arrays.asList("Turner0", "Johnson1", "Vanilla2", "Allstar3", "Targaryen4", "Jefferson5");
        assertEquals(expectedList, personLazyList.mapIndexed((index, person) -> person.getSecondName() + index).toList());
    }

    @Test
    public void select_Applies_given_transformation_on_each_element_in_LazyList() {
        List<String> expectedList = Arrays.asList("Turner", "Johnson", "Vanilla", "Allstar", "Targaryen", "Jefferson");
        assertEquals(expectedList, personLazyList.select(Person::getSecondName).toList());
    }

    @Test
    public void where_Returns_all_elements_that_satisfy_given_predicate() {
        List<Animal> expectedList = Arrays.asList(d3, d5, d6);
        assertEquals(expectedList, animalLazyList.where(animal -> animal.getAge() > 5).toList());
    }

    @Test
    public void whereIndexed_Returns_all_elements_that_satisfy_given_predicate_provided_with_index() {
        List<Animal> expectedList = Arrays.asList(d1, d3, d5);
        assertEquals(expectedList, animalLazyList.whereIndexed((index, animal) -> (index % 2) == 0).toList());
    }

    @Test
    public void filter_Returns_all_elements_that_satisfy_given_predicate() {
        List<Animal> expectedList = Arrays.asList(d3, d5, d6);
        assertEquals(expectedList, animalLazyList.filter(animal -> animal.getAge() > 5).toList());
    }

    @Test
    public void findAll_Returns_all_elements_that_satisfy_given_predicate() {
        List<Animal> expectedList = Arrays.asList(d3, d5, d6);
        assertEquals(expectedList, animalLazyList.findAll(animal -> animal.getAge() > 5).toList());
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
        assertEquals(expected.toList(), dogs.zipWith(dates, ints).toList());
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
        assertEquals(expected.toList(), dogs.zipWith(people).toList());
    }


    // Ranges =======================================================================================
    @Test
    public void rangeInclusive_Creates_a_range_of_integers_from_given_start_until_end_inclusive() {
        LazyList<Integer> intRange = LazyList.rangeInclusive(-3, 4);
        LazyList<Integer> expected = LazyList.of(-3, -2, -1, 0, 1, 2, 3, 4);
        assertEquals(expected.toList(), intRange.toList());
    }

    @Test
    public void rangeInclusive_Returns_singleton_list_if_from_equals_to() {
        LazyList<Integer> intRange = LazyList.rangeInclusive(741, 741);
        assertEquals(LazyList.of(741).toList(), intRange.toList());
    }

    @Test
    public void rangeInclusive_Returns_empty_list_if_from_bigger_than_to() {
        LazyList<Integer> intRange = LazyList.rangeInclusive(50, 3);
        assertEquals(LazyList.empty().toList(), intRange.toList());
    }

    @Test
    public void rangeExclusive_Creates_a_range_of_integers_from_given_start_until_end_exclusive() {
        LazyList<Integer> intRange = LazyList.rangeExclusive(-2, 5);
        LazyList<Integer> expected = LazyList.of(-2, -1, 0, 1, 2, 3, 4);
        assertEquals(expected.toList(), intRange.toList());
    }

    @Test
    public void rangeExclusive_Returns_singleton_list_if_from_just_smaller_than_to() {
        LazyList<Integer> intRange = LazyList.rangeExclusive(121, 122);
        assertEquals(LazyList.of(121).toList(), intRange.toList());
    }

    @Test
    public void rangeExclusive_Returns_empty_list_if_from_equals_to() {
        LazyList<Integer> intRange = LazyList.rangeExclusive(42, 42);
        assertEquals(LazyList.empty().toList(), intRange.toList());
    }

    @Test
    public void rangeExclusive_Returns_empty_list_if_from_bigger_than_to() {
        LazyList<Integer> intRange = LazyList.rangeExclusive(98, 88);
        assertEquals(LazyList.empty().toList(), intRange.toList());
    }

    @Test
    public void rangeLength_Creates_an_incrementing_range_of_integers_from_given_start_until_length_is_reached() {
        LazyList<Integer> intRange = LazyList.rangeLength(-5, 10);
        LazyList<Integer> expected = LazyList.of(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4);
        assertEquals(expected.toList(), intRange.toList());
    }

    @Test
    public void rangeLength_Returns_singleton_list_if_length_0() {
        LazyList<Integer> intRange = LazyList.rangeLength(-33, 0);
        assertEquals(LazyList.empty().toList(), intRange.toList());
    }

    @Test
    public void rangeLength_Returns_singleton_list_if_length_negative() {
        LazyList<Integer> intRange = LazyList.rangeLength(9, -6);
        assertEquals(LazyList.empty().toList(), intRange.toList());
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

    /*@Test
    public void withIndex_Creates_index_element_pairs_for_this_list() {
        Pair<Integer, Person> pair1 = Pair.of(0, p1);
        Pair<Integer, Person> pair2 = Pair.of(1, p2);
        Pair<Integer, Person> pair3 = Pair.of(2, p3);
        Pair<Integer, Person> pair4 = Pair.of(3, p4);
        Pair<Integer, Person> pair5 = Pair.of(4, p5);
        Pair<Integer, Person> pair6 = Pair.of(5, p6);
        LazyList<Pair<Integer, Person>> expected = LazyList.of(pair1, pair2, pair3, pair4, pair5, pair6);
        assertEquals(expected.toList(), personLazyList.withIndex().toList());
    }*/

    @Test
    public void withIndex_Creates_index_element_pairs_for_this_list() {
        IndexElement<Person> pair1 = IndexElement.of(0, p1);
        IndexElement<Person> pair2 = IndexElement.of(1, p2);
        IndexElement<Person> pair3 = IndexElement.of(2, p3);
        IndexElement<Person> pair4 = IndexElement.of(3, p4);
        IndexElement<Person> pair5 = IndexElement.of(4, p5);
        IndexElement<Person> pair6 = IndexElement.of(5, p6);
        LazyList<IndexElement<Person>> expected = LazyList.of(pair1, pair2, pair3, pair4, pair5, pair6);
        assertEquals(expected.toList(), personLazyList.withIndex().toList());
    }
}