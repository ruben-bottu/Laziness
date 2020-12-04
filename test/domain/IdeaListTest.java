package domain;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

public class IdeaListTest {
    private int i1, i2, i3, i4, i5, i6, i7, i8;
    private Person p1, p2, p3, p4, p5, p6;
    private Dog d1, d2, d3, d4, d5, d6;
    private LocalDate l1, l2, l3, l4, l5, l6, l7;

    private IdeaList<Integer> integerIdeaList;
    private IdeaList<Person> personIdeaList;
    private IdeaList<Dog> dogIdeaList;
    private IdeaList<LocalDate> localDateIdeaList;

    private Set<Person> personSet;

    @Before
    public void setUp() {
        initialiseIntegers();
        initialisePeople();
        initialiseDogs();
        initialiseLocalDates();

        initialiseIterables();
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

    private void initialiseIterables() {
        integerIdeaList = IdeaList.of(i1, i2, i3, i4, i5, i6, i7, i8);
        personIdeaList = IdeaList.of(p1, p2, p3, p4, p5, p6);
        dogIdeaList = IdeaList.of(d1, d2, d3, d4, d5, d6);
        localDateIdeaList = IdeaList.of(l1, l2, l3, l4, l5, l6, l7);
        personSet = new LinkedHashSet<>(personIdeaList.toList());
    }


    // Constructors and factory methods =============================================================
    @Test
    public void empty_Returns_an_empty_IdeaList() {
        assertEquals(0, IdeaList.empty().length());
    }

    @Test
    public void of_Iterable_Creates_empty_IdeaList_if_given_Iterable_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.of(new PriorityQueue<>()));
    }

    @Test
    public void of_Iterable_Creates_IdeaList_of_given_Iterable() {
        IdeaList<Person> currentNode = IdeaList.of(personSet);
        for (Person person : personSet) {
            assertEquals(person, currentNode.value.value());
            currentNode = currentNode.tail.value();
        }
    }

    @Test
    public void of_Gives_correct_tail_when_value_is_not_calculated() {
        assertEquals(p2, personIdeaList.tail.value().value.value());
        assertEquals(p5, personIdeaList.tail.value().tail.value().tail.value().tail.value().value.value());
    }

    @Test
    public void of_elements_Creates_empty_IdeaList_if_no_elements_are_given() {
        assertEquals(IdeaList.empty(), IdeaList.of());
    }

    @Test //TODO make covariant
    public void of_elements_Creates_IdeaList_of_given_covariant_Iterable() { // Not covariance. Needs to be an Iterable, not separate elements
        IdeaList<Object> objects = IdeaList.of("bla", "foo", "zaza");
        assertEquals("bla", objects.value.value());
        assertEquals("foo", objects.tail.value().value.value());
        assertEquals("zaza", objects.tail.value().tail.value().value.value());
    }

    @Test
    public void of_elements_Creates_IdeaList_of_given_objects_with_same_type_or_subtype_of_list() {
        IdeaList<Animal> animals = IdeaList.of(d1, d2, d3, d4, d5, d6);
        assertEquals(d1, animals.value.value());
        assertEquals(d2, animals.tail.value().value.value());
        assertEquals(d3, animals.tail.value().tail.value().value.value());
        assertEquals(d4, animals.tail.value().tail.value().tail.value().value.value());
        assertEquals(d5, animals.tail.value().tail.value().tail.value().tail.value().value.value());
        assertEquals(d6, animals.tail.value().tail.value().tail.value().tail.value().tail.value().value.value());
        assertNull(animals.tail.value().tail.value().tail.value().tail.value().tail.value().tail.value().value);
    }

    @Test
    public void initialiseWith_Returns_an_empty_list_if_given_length_0() {
        assertEquals(IdeaList.empty(), IdeaList.initialiseWith(0, index -> index));
    }

    @Test(expected = IllegalArgumentException.class)
    public void initialiseWith_Throws_exception_if_given_length_just_negative() {
        assertEquals(IdeaList.empty(), IdeaList.initialiseWith(-1, index -> index));
    }

    @Test(expected = IllegalArgumentException.class)
    public void initialiseWith_Throws_exception_if_given_length_negative() {
        assertEquals(IdeaList.empty(), IdeaList.initialiseWith(-24, index -> index));
    }

    @Test
    public void initialiseWith_Initialises_IdeaList_with_given_length_by_applying_function_to_each_index() {
        IdeaList<Dog> dogs = IdeaList.initialiseWith(3, index -> new Dog(index, "Lassy"));
        IdeaList<Dog> expected = IdeaList.of(new Dog(0, "Lassy"), new Dog(1, "Lassy"), new Dog(2, "Lassy"));
        assertEquals(expected, dogs);
    }


    // Getters ======================================================================================
    @Test(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_index_negative() {
        localDateIdeaList.get(-75);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_index_just_negative() {
        integerIdeaList.get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_index_just_bigger_than_last_valid_index() {
        personIdeaList.get(6);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_index_bigger_than_last_valid_index() {
        dogIdeaList.get(21);
    }

    @Test
    public void get_Returns_element_at_given_index_if_index_valid() {
        assertEquals(p4, personIdeaList.get(3));
        assertEquals(p6, personIdeaList.get(5));
        assertEquals(d1, dogIdeaList.get(0));
        assertEquals(d3, dogIdeaList.get(2));
    }

    @Test(expected = NoSuchElementException.class)
    public void first_Throws_exception_if_list_is_empty() {
        IdeaList.empty().first();
    }

    @Test
    public void first_Returns_first_element_of_this_list() {
        assertEquals(i1, integerIdeaList.first().intValue());
        assertEquals(p1, personIdeaList.first());
        assertEquals(d1, dogIdeaList.first());
        assertEquals(l1, localDateIdeaList.first());
    }

    @Test(expected = IllegalArgumentException.class)
    public void single_Throws_exception_if_list_contains_more_than_one_element() {
        integerIdeaList.single();
    }

    @Test(expected = NoSuchElementException.class)
    public void single_Throws_exception_if_list_is_empty() {
        IdeaList.empty().single();
    }

    @Test
    public void single_Returns_the_only_element_in_this_list() {
        assertEquals(p4, IdeaList.of(p4).single());
        assertEquals(d2, IdeaList.of(d2).single());
        assertEquals(l5, IdeaList.of(l5).single());
    }

    @Test(expected = NoSuchElementException.class)
    public void last_Throws_exception_if_list_is_empty() {
        IdeaList.empty().last();
    }

    @Test
    public void last_Returns_last_element_of_this_list() {
        assertEquals(i8, integerIdeaList.last().intValue());
        assertEquals(p6, personIdeaList.last());
        assertEquals(d6, dogIdeaList.last());
        assertEquals(l7, localDateIdeaList.last());
    }

    @Test(expected = IllegalArgumentException.class)
    public void random_Throws_exception_if_list_is_empty() {
        IdeaList.empty().random();
    }

    @Test
    public void random_Returns_random_element_from_this_list() {
        assertTrue(integerIdeaList.contains(integerIdeaList.random()));
        assertTrue(personIdeaList.contains(personIdeaList.random()));
        assertTrue(dogIdeaList.contains(dogIdeaList.random()));
        assertTrue(localDateIdeaList.contains(localDateIdeaList.random()));
    }

    @Test
    public void findFirst_Returns_null_if_list_is_empty() {
        assertNull(IdeaList.empty().findFirst(e -> true));
    }

    @Test
    public void findFirst_Returns_null_if_no_element_matches_given_predicate() {
        assertNull(integerIdeaList.findFirst(integer -> integer > 75 && integer < 100));
        assertNull(dogIdeaList.findFirst(animal -> animal.getAge() > 13));
    }

    @Test
    public void findFirst_Returns_first_element_matching_given_predicate() {
        assertEquals(i8, integerIdeaList.findFirst(integer -> integer > 100).intValue());
        assertEquals(p3, personIdeaList.findFirst(person -> person.getFirstName().contains("t")));
        assertEquals(l4, localDateIdeaList.findFirst(localDate -> localDate.getMonthValue() < 3));
    }

    @Test
    public void first_Returns_null_if_list_is_empty() {
        assertNull(IdeaList.empty().first(e -> true));
    }

    @Test
    public void first_Returns_null_if_no_element_matches_given_predicate() {
        assertNull(integerIdeaList.first(integer -> integer > 75 && integer < 100));
        assertNull(dogIdeaList.first(animal -> animal.getAge() > 13));
    }

    @Test
    public void first_Returns_first_element_matching_given_predicate() {
        assertEquals(i8, integerIdeaList.first(integer -> integer > 100).intValue());
        assertEquals(p3, personIdeaList.first(person -> person.getFirstName().contains("t")));
        assertEquals(l4, localDateIdeaList.first(localDate -> localDate.getMonthValue() < 3));
    }

    @Test
    public void indexOfFirst_predicate_Returns_minus_one_if_list_is_empty() {
        assertEquals(-1, IdeaList.empty().indexOfFirst(e -> true));
    }

    @Test
    public void indexOfFirst_predicate_Returns_minus_one_if_no_element_matches_given_predicate() {
        assertEquals(-1, integerIdeaList.indexOfFirst(integer -> integer == 2));
        assertEquals(-1, personIdeaList.indexOfFirst(person -> person.getFirstName().contains("b") || person.getSecondName().contains("b")));
        assertEquals(-1, dogIdeaList.indexOfFirst(animal -> animal.getAge() > 13));
        assertEquals(-1, localDateIdeaList.indexOfFirst(localDate -> localDate.getYear() > 2020 && localDate.getYear() < 4058));
    }

    @Test
    public void indexOfFirst_predicate_Returns_index_of_first_element_that_matches_given_predicate() {
        assertEquals(7, integerIdeaList.indexOfFirst(integer -> integer > 75));
        assertEquals(1, personIdeaList.indexOfFirst(person -> person.getSecondName().contains("h")));
        assertEquals(4, dogIdeaList.indexOfFirst(animal -> animal.getAge() > 6));
        assertEquals(0, localDateIdeaList.indexOfFirst(localDate -> true));
    }

    @Test
    public void indexOfFirst_element_Returns_minus_one_if_list_is_empty() {
        assertEquals(-1, IdeaList.empty().indexOfFirst(p1));
    }

    @Test
    public void indexOfFirst_element_Returns_minus_one_if_list_does_not_contain_given_element() {
        assertEquals(-1, integerIdeaList.indexOfFirst(101));
        assertEquals(-1, personIdeaList.indexOfFirst(new Person("James", "Walker")));
        assertEquals(-1, dogIdeaList.indexOfFirst(new Dog(2, "Bolt")));
        assertEquals(-1, localDateIdeaList.indexOfFirst(LocalDate.of(441, 3, 11)));
    }

    @Test
    public void indexOfFirst_element_Returns_index_of_first_occurrence_of_given_element_in_this_list() {
        assertEquals(0, integerIdeaList.indexOfFirst(i1));
        assertEquals(4, personIdeaList.indexOfFirst(p5));
        assertEquals(2, dogIdeaList.indexOfFirst(d3));
        assertEquals(6, localDateIdeaList.indexOfFirst(l7));
    }

    @Test
    public void lastIndex_Returns_minus_one_if_list_is_empty() {
        assertEquals(-1, IdeaList.empty().lastIndex());
        assertEquals(-1, IdeaList.empty().lastIndex());
        assertEquals(-1, IdeaList.empty().lastIndex());
        assertEquals(-1, IdeaList.empty().lastIndex());
    }

    @Test
    public void lastIndex_Returns_index_of_last_element() {
        assertEquals(7, integerIdeaList.lastIndex());
        assertEquals(5, personIdeaList.lastIndex());
        assertEquals(5, dogIdeaList.lastIndex());
        assertEquals(6, localDateIdeaList.lastIndex());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void indices_Throws_exception_if_list_is_empty() {
        IdeaList.empty().indices();
    }

    @Test
    public void indices_Returns_all_indices_of_this_list() {
        assertEquals(Range.from(0).upToAndIncluding(7), integerIdeaList.indices());
        assertEquals(Range.from(0).upToAndIncluding(5), personIdeaList.indices());
        assertEquals(Range.from(0).upToAndIncluding(5), dogIdeaList.indices());
        assertEquals(Range.from(0).upToAndIncluding(6), localDateIdeaList.indices());
    }

    @Test
    public void length_Returns_0_if_list_is_empty() {
        assertEquals(0, IdeaList.empty().length());
    }

    @Test
    public void length_Returns_the_length_of_this_list() {
        assertEquals(8, integerIdeaList.length());
        assertEquals(6, personIdeaList.length());
        assertEquals(6, dogIdeaList.length());
        assertEquals(7, localDateIdeaList.length());
    }

    @Test
    public void toList_Returns_empty_List_if_this_list_is_empty() {
        assertEquals(Collections.emptyList(), IdeaList.empty().toList());
    }

    @Test
    public void toList_Transforms_IdeaList_into_List() {
        List<Person> personList = Arrays.asList(p1, p2, p3, p4, p5, p6);
        List<Dog> dogList = Arrays.asList(d1, d2, d3, d4, d5, d6);

        assertEquals(personList, IdeaList.of(personList).toList());
        assertEquals(dogList, IdeaList.of(dogList).toList());
    }

    @Test
    public void iterator_Returns_an_iterator_with_no_elements_if_list_is_empty() {
        Iterator<LocalDate> it = IdeaList.<LocalDate>empty().iterator();
        assertFalse(it.hasNext());
    }

    @Test
    public void iterator_Returns_Iterator_that_loops_through_this_list() {
        Iterator<LocalDate> it = localDateIdeaList.iterator();
        assertEquals(l1, it.next());
        assertEquals(l2, it.next());
        assertEquals(l3, it.next());
        assertEquals(l4, it.next());
        assertEquals(l5, it.next());
        assertEquals(l6, it.next());
        assertEquals(l7, it.next());
    }

    @Test
    public void iterator_Returns_Iterator_that_loops_through_this_list_2() {
        Iterator<Integer> it = IdeaList.of(6, 0, 1, 7, 5, 4).iterator();
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
        assertNotEquals(personIdeaList, null);
        assertNotEquals(null, personIdeaList);
    }

    @Test
    public void equals_Returns_false_if_only_one_of_the_lists_is_empty() {
        assertNotEquals(personIdeaList, IdeaList.empty());
        assertNotEquals(IdeaList.empty(), personIdeaList);
    }

    @Test
    public void equals_Returns_false_if_this_list_is_longer_than_given_list() {
        Person np1 = new Person("Zoe", "Turner");
        Person np2 = new Person("Alex", "Johnson");
        Person np3 = new Person("Patricia", "Vanilla");
        Person np4 = new Person("Jeffie", "Allstar");
        Person np5 = new Person("Daenerys", "Targaryen");
        Person np6 = new Person("John", "Jefferson");
        Person np7 = new Person("Morgan", "Freeman");
        IdeaList<Person> newPeople = IdeaList.of(np1, np2, np3, np4, np5, np6, np7);

        assertNotEquals(personIdeaList, newPeople);
        assertNotEquals(newPeople, personIdeaList);
    }

    @Test
    public void equals_Returns_false_if_this_list_is_shorter_than_given_list() {
        Person np1 = new Person("Zoe", "Turner");
        Person np2 = new Person("Alex", "Johnson");
        Person np3 = new Person("Patricia", "Vanilla");
        Person np4 = new Person("Jeffie", "Allstar");
        Person np5 = new Person("Daenerys", "Targaryen");
        IdeaList<Person> newPeople = IdeaList.of(np1, np2, np3, np4, np5);

        assertNotEquals(personIdeaList, newPeople);
        assertNotEquals(newPeople, personIdeaList);
    }

    @Test
    public void equals_Returns_false_if_this_list_is_not_equal_to_given_list() {
        Person np1 = new Person("Zoe", "Turner");
        Person np2 = new Person("Alex", "Johnson");
        Person np3 = new Person("Patricia", "Vanilla");
        Person np4 = new Person("Jeffie", "Allstar");
        Person np5 = new Person("Daenerys", "Targaryen");
        Person np6 = new Person("John", "Jefferso"); // final n is missing
        IdeaList<Person> newPeople = IdeaList.of(np1, np2, np3, np4, np5, np6);

        assertNotEquals(personIdeaList, newPeople);
        assertNotEquals(newPeople, personIdeaList);
    }

    @Test
    public void equals_Returns_true_if_both_lists_are_empty() {
        assertEquals(IdeaList.empty(), IdeaList.empty());
    }

    @Test
    public void equals_Returns_true_if_this_list_is_equal_to_given_list() {
        Person np1 = new Person("Zoe", "Turner");
        Person np2 = new Person("Alex", "Johnson");
        Person np3 = new Person("Patricia", "Vanilla");
        Person np4 = new Person("Jeffie", "Allstar");
        Person np5 = new Person("Daenerys", "Targaryen");
        Person np6 = new Person("John", "Jefferson");
        IdeaList<Person> newPeople = IdeaList.of(np1, np2, np3, np4, np5, np6);

        assertEquals(personIdeaList, newPeople);
        assertEquals(newPeople, personIdeaList);
    }

    @Test
    public void contains_Returns_false_if_list_is_empty() {
        assertFalse(IdeaList.empty().contains(9));
    }

    @Test
    public void contains_Returns_false_if_this_list_does_not_contain_given_element() {
        assertFalse(integerIdeaList.contains(9));
        assertFalse(personIdeaList.contains(new Person("Magdalena", "Yves")));
        assertFalse(dogIdeaList.contains(new Dog(4, "Shibaa")));
        assertFalse(localDateIdeaList.contains(LocalDate.of(1974, 12, 24)));
    }

    @Test
    public void contains_Returns_true_if_this_list_contains_given_element() {
        assertTrue(integerIdeaList.contains(5));
        assertTrue(personIdeaList.contains(new Person("Alex", "Johnson")));
        assertTrue(dogIdeaList.contains(new Dog(5, "Shiba")));
        assertTrue(localDateIdeaList.contains(LocalDate.of(1974, 12, 25)));
    }

    @Test
    public void containsAll_Iterable_Returns_false_if_this_list_is_empty() {
        assertFalse(IdeaList.empty().containsAll(Collections.singletonList(5)));
    }

    @Test
    public void containsAll_Iterable_Returns_false_if_this_list_does_not_contain_all_given_elements() {
        Queue<Person> queue = new LinkedList<>();
        queue.offer(new Person("Jefke", "Merens"));
        queue.add(new Person("Marie", "Bosmans"));

        IdeaList<Person> personList = IdeaList.of(new Person("Jefke", "Merens"), new Person("Nathalie", "Hofdaal"));
        assertFalse(personList.containsAll(queue));
    }

    @Test
    public void containsAll_Iterable_Returns_true_if_this_list_contains_all_given_elements() {
        Queue<Person> queue = new LinkedList<>();
        queue.offer(new Person("Jefke", "Merens"));
        queue.add(new Person("Marie", "Bosmans"));

        IdeaList<Person> personList = IdeaList.of(new Person("Jefke", "Merens"), new Person("Nathalie", "Hofdaal"), new Person("Marie", "Bosmans"));
        assertTrue(personList.containsAll(queue));
    }

    @Test
    public void containsAll_IdeaList_Returns_false_if_this_list_is_empty() {
        assertFalse(IdeaList.empty().containsAll(IdeaList.of(5)));
    }

    @Test
    public void containsAll_IdeaList_Returns_false_if_list_does_not_contain_all_given_elements() {
        IdeaList<Person> personList = IdeaList.of(new Person("Jefke", "Merens"), new Person("Nathalie", "Hofdaal"));
        IdeaList<Person> elements = IdeaList.of(new Person("Jefke", "Merens"), new Person("Marie", "Bosmans"));
        assertFalse(personList.containsAll(elements));
    }

    @Test
    public void containsAll_IdeaList_Returns_true_if_list_contains_all_given_elements() {
        IdeaList<Person> personList = IdeaList.of(new Person("Jefke", "Merens"), new Person("Nathalie", "Hofdaal"), new Person("Marie", "Bosmans"));
        IdeaList<Person> elements = IdeaList.of(new Person("Jefke", "Merens"), new Person("Marie", "Bosmans"));
        assertTrue(personList.containsAll(elements));
    }

    @Test
    public void isEmpty_Returns_false_if_list_is_not_empty() {
        assertFalse(localDateIdeaList.isEmpty());
        assertFalse(personIdeaList.isEmpty());
    }

    @Test
    public void isEmpty_Returns_true_if_list_is_empty() {
        assertTrue(IdeaList.empty().isEmpty());
    }

    @Test
    public void isNested_Returns_false_if_list_is_empty() {
        assertFalse(IdeaList.empty().isNested());
    }

    @Test
    public void isNested_Returns_false_if_IdeaList_is_not_nested() {
        assertFalse(integerIdeaList.isNested());
        assertFalse(personIdeaList.isNested());
        assertFalse(dogIdeaList.isNested());
        assertFalse(localDateIdeaList.isNested());
    }

    @Test
    public void isNested_Returns_true_if_IdeaList_is_nested_with_Iterables() {
        IdeaList<Set<Integer>> nestedIdeaList = IdeaList.of(
                new HashSet<>(Arrays.asList(111, -25, 2)),
                new HashSet<>(Arrays.asList(1, 88)),
                new HashSet<>(Arrays.asList(-44, 270, 31, 73, 500))
        );
        assertTrue(nestedIdeaList.isNested());
    }

    @Test
    public void isNested_Returns_true_if_IdeaList_is_nested_with_IdeaLists() {
        IdeaList<IdeaList<Integer>> nestedIdeaList = IdeaList.of(
                IdeaList.of(9, 6, 3, 5),
                IdeaList.of(1),
                IdeaList.of(4, 0, 3, 7, 5)
        );
        assertTrue(nestedIdeaList.isNested());
    }

    @Test
    public void all_Returns_false_if_one_or_more_elements_do_not_match_given_predicate() {
        assertFalse(integerIdeaList.all(integer -> integer > 10));
        assertFalse(personIdeaList.all(person -> !person.getFirstName().contains("tr")));
        assertFalse(dogIdeaList.all(animal -> animal.getAge() != 4));
        assertFalse(localDateIdeaList.all(localDate -> localDate.getYear() < 1000 || localDate.getYear() > 2000));
    }

    @Test
    public void all_Returns_true_if_list_is_empty() {
        assertTrue(IdeaList.empty().all(e -> false));
    }

    @Test
    public void all_Returns_true_if_all_elements_match_given_predicate() {
        assertTrue(integerIdeaList.all(integer -> integer > -257));
        assertTrue(personIdeaList.all(person -> person.getFirstName().length() > 2));
        assertTrue(dogIdeaList.all(animal -> animal.getAge() != 10));
        assertTrue(localDateIdeaList.all(localDate -> localDate.getYear() != 1861));
    }

    @Test
    public void any_Returns_false_if_list_does_not_contain_elements() {
        assertFalse(IdeaList.empty().any());
    }

    @Test
    public void any_Returns_true_if_list_contains_elements() {
        assertTrue(integerIdeaList.any());
        assertTrue(localDateIdeaList.any());
        assertTrue(IdeaList.of("bla").any());
    }

    @Test
    public void any_predicate_Returns_false_if_list_is_empty() {
        assertFalse(IdeaList.empty().any(e -> true));
    }

    @Test
    public void any_predicate_Returns_false_if_no_element_matches_predicate() {
        assertFalse(integerIdeaList.any(integer -> integer == 3));
        assertFalse(personIdeaList.any(person -> person.getFirstName().charAt(2) == 'a'));
        assertFalse(dogIdeaList.any(animal -> animal.getAge() == 8));
        assertFalse(localDateIdeaList.any(localDate -> localDate.getDayOfMonth() == 2));
        assertFalse(localDateIdeaList.any(localDate -> localDate.getDayOfMonth() == 3));
    }

    @Test
    public void any_predicate_Returns_true_if_at_least_one_element_matches_predicate() {
        assertTrue(integerIdeaList.any(integer -> integer < 10));
        assertTrue(personIdeaList.any(person -> person.getSecondName().contains("o")));
        assertTrue(dogIdeaList.any(animal -> animal.getAge() > 5));
        assertTrue(localDateIdeaList.any(localDate -> localDate.getDayOfMonth() > 15 && localDate.getDayOfMonth() < 30));
    }

    @Test
    public void none_Returns_true_if_list_does_not_contain_elements() {
        assertTrue(IdeaList.empty().none());
    }

    @Test
    public void none_Returns_false_if_list_contains_elements() {
        assertFalse(personIdeaList.none());
        assertFalse(dogIdeaList.none());
        assertFalse(IdeaList.of("bla").none());
    }

    @Test
    public void none_predicate_Returns_true_if_list_is_empty() {
        assertTrue(IdeaList.empty().none(e -> false));
    }

    @Test
    public void none_predicate_Returns_false_if_at_least_one_element_matches_predicate() {
        assertFalse(integerIdeaList.none(integer -> integer < 10));
        assertFalse(personIdeaList.none(person -> person.getSecondName().contains("o")));
        assertFalse(dogIdeaList.none(animal -> animal.getAge() > 5));
        assertFalse(localDateIdeaList.none(localDate -> localDate.getDayOfMonth() > 15 && localDate.getDayOfMonth() < 30));
    }

    @Test
    public void none_predicate_Returns_true_if_no_element_matches_predicate() {
        assertTrue(integerIdeaList.none(integer -> integer == 3));
        assertTrue(personIdeaList.none(person -> person.getFirstName().charAt(2) == 'a'));
        assertTrue(dogIdeaList.none(animal -> animal.getAge() == 8));
        assertTrue(localDateIdeaList.none(localDate -> localDate.getDayOfMonth() == 2));
        assertTrue(localDateIdeaList.none(localDate -> localDate.getDayOfMonth() == 3));
    }


    // Modifiers ====================================================================================
    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_this_list_is_empty() {
        IdeaList.<Person>empty().insertAt(0, personSet);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_index_just_negative() {
        personIdeaList.insertAt(-1, Collections.emptyList());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_index_negative() {
        personIdeaList.insertAt(-23, Collections.emptyList());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_index_just_bigger_than_upper_bound() {
        personIdeaList.insertAt(6, Collections.emptyList()).toList();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_index_bigger_than_upper_bound() {
        personIdeaList.insertAt(47, Collections.emptyList()).toList();
    }

    @Test
    public void insertAt_Iterable_Returns_list_if_given_list_is_empty() {
        assertEquals(localDateIdeaList, localDateIdeaList.insertAt(3, new HashSet<>()));
    }

    @Test
    public void insertAt_Iterable_Inserts_given_elements_at_specified_position() {
        LocalDate local1 = LocalDate.of(1002, 7, 1);
        LocalDate local2 = LocalDate.of(4014, 6, 9);
        IdeaList<LocalDate> expected = IdeaList.of(l1, l2, l3, local1, local2, l4, l5, l6, l7);
        assertEquals(expected, localDateIdeaList.insertAt(3, Arrays.asList(local1, local2)));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_elements_Throws_exception_if_this_list_is_empty() {
        IdeaList.<Person>empty().insertAt(0, p1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_elements_Throws_exception_if_index_just_negative() {
        personIdeaList.insertAt(-1, p1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_elements_Throws_exception_if_index_negative() {
        personIdeaList.insertAt(-23, p1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_elements_Throws_exception_if_index_just_bigger_than_upper_bound() {
        personIdeaList.insertAt(6, p1).toList();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_elements_Throws_exception_if_index_bigger_than_upper_bound() {
        personIdeaList.insertAt(47, p1).toList();
    }

    @Test
    public void insertAt_elements_Returns_list_if_no_elements_are_given() {
        assertEquals(localDateIdeaList, localDateIdeaList.insertAt(3));
    }

    @Test
    public void insertAt_elements_Inserts_given_elements_at_specified_position() {
        LocalDate local1 = LocalDate.of(1002, 7, 1);
        LocalDate local2 = LocalDate.of(4014, 6, 9);
        IdeaList<LocalDate> expected = IdeaList.of(l1, l2, l3, local1, local2, l4, l5, l6, l7);
        assertEquals(expected, localDateIdeaList.insertAt(3, local1, local2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_IdeaList_Throws_exception_if_this_list_is_empty() {
        IdeaList.<Person>empty().insertAt(0, personIdeaList);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_IdeaList_Throws_exception_if_index_just_negative() {
        personIdeaList.insertAt(-1, IdeaList.empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_IdeaList_Throws_exception_if_index_negative() {
        personIdeaList.insertAt(-23, IdeaList.empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_IdeaList_Throws_exception_if_index_just_bigger_than_upper_bound() {
        personIdeaList.insertAt(6, IdeaList.empty()).toList();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertAt_IdeaList_Throws_exception_if_index_bigger_than_upper_bound() {
        personIdeaList.insertAt(47, IdeaList.empty()).toList();
    }

    @Test
    public void insertAt_IdeaList_Returns_list_if_given_list_is_empty() {
        assertEquals(localDateIdeaList, localDateIdeaList.insertAt(3, IdeaList.empty()));
    }

    @Test
    public void insertAt_IdeaList_Inserts_given_elements_at_specified_position() {
        LocalDate local1 = LocalDate.of(1002, 7, 1);
        LocalDate local2 = LocalDate.of(4014, 6, 9);
        IdeaList<LocalDate> expected = IdeaList.of(l1, l2, l3, local1, local2, l4, l5, l6, l7);
        assertEquals(expected, localDateIdeaList.insertAt(3, IdeaList.of(local1, local2)));
    }

    @Test
    public void concatWith_Iterable_Returns_given_list_if_this_list_is_empty() {
        assertEquals(personIdeaList, IdeaList.<Person>empty().concatWith(personSet));
    }

    @Test
    public void concatWith_Iterable_Returns_this_list_if_given_list_is_empty() {
        assertEquals(localDateIdeaList, localDateIdeaList.concatWith(new HashSet<>()));
    }

    @Test
    public void concatWith_Iterable_Concatenates_this_list_with_given_Iterable() {
        Person np1 = new Person("Jan", "Janssens");
        Person np2 = new Person("Eveline", "Roberts");
        assertEquals(IdeaList.of(np1, np2, p1, p2, p3, p4, p5, p6), IdeaList.of(np1, np2).concatWith(personSet));
    }

    @Test
    public void concatWith_IdeaList_Returns_given_list_if_this_list_is_empty() {
        assertEquals(personIdeaList, IdeaList.<Person>empty().concatWith(personIdeaList));
    }

    @Test
    public void concatWith_IdeaList_Returns_this_list_if_given_list_is_empty() {
        assertEquals(localDateIdeaList, localDateIdeaList.concatWith(IdeaList.empty()));
    }

    /*@Test //TODO
    public void concatWith_IdeaList_Concatenates_IdeaList_with_given_IdeaList_with_null() {
        Person e1 = new Person("Jan", "Janssens");
        Person e2 = new Person("Eveline", null);
        assertEquals(IdeaList.of(null, e2, p1, p2, p3, p4, null, p6), IdeaList.of(null, e2).concatWith(IdeaList.of(p1, p2, p3, p4, null, p6)));
    }*/

    @Test
    public void concatWith_IdeaList_Concatenates_this_list_with_given_IdeaList() {
        Person e1 = new Person("Jan", "Janssens");
        Person e2 = new Person("Eveline", "Roberts");
        assertEquals(IdeaList.of(e1, e2, p1, p2, p3, p4, p5, p6), IdeaList.of(e1, e2).concatWith(personIdeaList));
    }

    @Test
    public void add_Returns_given_elements_if_list_is_empty() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, "Lulu");
        Dog d3 = new Dog(11, "Lala");
        assertEquals(IdeaList.of(d1, d2, d3), IdeaList.empty().add(d1, d2, d3));
    }

    @Test
    public void add_Returns_this_list_if_no_arguments_are_given() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, "Lulu");
        Dog d3 = new Dog(11, "Lala");
        assertEquals(IdeaList.of(d1, d2, d3), IdeaList.of(d1, d2, d3).add());
    }

    @Test //TODO This shouldn't work??
    public void add_Adds_the_given_elements_to_the_end_of_the_list_with_null() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, null);
        Dog d3 = new Dog(11, "Lala");
        Dog d4 = new Dog(428, "Momo");
        Dog d5 = new Dog(0, null);
        assertEquals(IdeaList.of(d1, d2, d3, d4, d5), IdeaList.of(d1, d2).add(d3, d4, d5));
    }

    @Test
    public void add_Adds_the_given_elements_to_the_end_of_the_list() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, "Lulu");
        Dog d3 = new Dog(11, "Lala");
        Dog d4 = new Dog(428, "Momo");
        Dog d5 = new Dog(0, "Giri");
        assertEquals(IdeaList.of(d1, d2, d3, d4, d5), IdeaList.of(d1, d2).add(d3, d4, d5));
    }

    @Test
    public void linkToBackOf_Iterable_Returns_given_list_if_this_list_is_empty() {
        assertEquals(personIdeaList, IdeaList.<Person>empty().linkToBackOf(personSet));
    }

    @Test
    public void linkToBackOf_Iterable_Returns_this_list_if_given_list_is_empty() {
        assertEquals(localDateIdeaList, localDateIdeaList.linkToBackOf(new HashSet<>()));
    }

    @Test
    public void linkToBackOf_Iterable_Links_this_list_to_the_back_of_the_given_Iterable() {
        Dog n1 = new Dog(11, "Lala");
        Dog n2 = new Dog(428, "Momo");
        Dog n3 = new Dog(0, "Giri");
        Set<Dog> dogs = new LinkedHashSet<>(Arrays.asList(n1, n2, n3));

        IdeaList<Dog> expected = IdeaList.of(n1, n2, n3, d1, d2, d3, d4, d5, d6);
        assertEquals(expected, dogIdeaList.linkToBackOf(dogs));
    }

    @Test
    public void linkToBackOf_IdeaList_Returns_given_list_if_this_list_is_empty() {
        assertEquals(personIdeaList, IdeaList.<Person>empty().linkToBackOf(personIdeaList));
    }

    @Test
    public void linkToBackOf_IdeaList_Returns_this_list_if_given_list_is_empty() {
        assertEquals(localDateIdeaList, localDateIdeaList.linkToBackOf(IdeaList.empty()));
    }

    @Test
    public void linkToBackOf_IdeaList_Links_this_list_to_the_back_of_the_given_Iterable() {
        Dog n1 = new Dog(11, "Lala");
        Dog n2 = new Dog(428, "Momo");
        Dog n3 = new Dog(0, "Giri");
        IdeaList<Dog> newDogs = IdeaList.of(n1, n2, n3);

        IdeaList<Dog> expected = IdeaList.of(d1, d2, d3, d4, d5, d6, n1, n2, n3);
        assertEquals(expected, newDogs.linkToBackOf(dogIdeaList));
    }

    @Test
    public void addToFront_Returns_given_elements_if_list_is_empty() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, "Lulu");
        Dog d3 = new Dog(11, "Lala");
        assertEquals(IdeaList.of(d1, d2, d3), IdeaList.empty().addToFront(d1, d2, d3));
    }

    @Test
    public void addToFront_Returns_this_list_if_no_arguments_are_given() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, "Lulu");
        Dog d3 = new Dog(11, "Lala");
        assertEquals(IdeaList.of(d1, d2, d3), IdeaList.of(d1, d2, d3).addToFront());
    }

    @Test //TODO This shouldn't work??
    public void addToFront_Adds_the_given_elements_to_the_end_of_the_list_with_null() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, null);
        Dog d3 = new Dog(11, "Lala");
        Dog d4 = new Dog(428, "Momo");
        Dog d5 = new Dog(0, null);
        assertEquals(IdeaList.of(d3, d4, d5, d1, d2), IdeaList.of(d1, d2).addToFront(d3, d4, d5));
    }

    @Test
    public void addToFront_Adds_given_elements_to_front_of_the_list() {
        Dog n1 = new Dog(11, "Lala");
        Dog n2 = new Dog(428, "Momo");
        Dog n3 = new Dog(0, "Giri");

        IdeaList<Dog> expected = IdeaList.of(n1, n2, n3, d1, d2, d3, d4, d5, d6);
        assertEquals(expected, dogIdeaList.addToFront(n1, n2, n3));
    }

    @Test
    public void removeFirst_Returns_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.empty().removeFirst(p1));
    }

    @Test
    public void removeFirst_Removes_first_instance_of_the_given_element_from_this_list() {
        IdeaList<Person> expected = IdeaList.of(p1, p2, p5, p5, p3, p4, p5);
        IdeaList<Person> inputList = IdeaList.of(p5, p1, p2, p5, p5, p3, p4, p5);
        assertEquals(expected, inputList.removeFirst(p5));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_list_is_empty() {
        IdeaList.<String>empty().removeAt(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_index_just_negative() {
        dogIdeaList.removeAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_index_negative() {
        dogIdeaList.removeAt(-23);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_index_just_bigger_than_upper_bound() {
        dogIdeaList.removeAt(6).toList();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_index_bigger_than_upper_bound() {
        dogIdeaList.removeAt(56).toList();
    }

    @Test
    public void removeAt_Removes_the_element_at_specified_position() {
        assertEquals(IdeaList.of(d2, d3, d4, d5, d6), dogIdeaList.removeAt(0));
        assertEquals(IdeaList.of(d1, d3, d4, d5, d6), dogIdeaList.removeAt(1));
        assertEquals(IdeaList.of(d1, d2, d4, d5, d6), dogIdeaList.removeAt(2));
        assertEquals(IdeaList.of(d1, d2, d3, d4, d5), dogIdeaList.removeAt(5));
    }

    @Test
    public void removeAll_Returns_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.empty().removeAll(p1));
    }

    @Test
    public void removeAll_Removes_all_instances_of_the_given_element_from_this_list() {
        IdeaList<Person> expected = IdeaList.of(p1, p2, p3, p4);
        IdeaList<Person> inputList = IdeaList.of(p5, p1, p2, p5, p5, p3, p4, p5);
        assertEquals(expected, inputList.removeAll(p5));
    }

    @Test
    public void forEachIndexed_Performs_no_actions_if_list_is_empty() {
        List<Dog> dogs = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        IdeaList.<Dog>empty().forEachIndexed((idx, cur) -> {
            indices.add(idx);
            dogs.add(cur);
        });
        assertTrue(dogs.isEmpty());
        assertTrue(indices.isEmpty());
    }

    @Test
    public void forEachIndexed_Performs_given_action_on_each_element_of_this_list_provided_with_its_index() {
        List<Dog> dogs = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        dogIdeaList.forEachIndexed((idx, cur) -> {
            indices.add(idx);
            dogs.add(cur);
        });
        assertEquals(dogIdeaList.toList(), dogs);
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5), indices);
    }

    @Test
    public void forEach_Performs_no_actions_if_list_is_empty() {
        List<Dog> dogs = new ArrayList<>();
        IdeaList.<Dog>empty().forEach(dogs::add);

        assertTrue(dogs.isEmpty());
    }

    @Test
    public void forEach_Performs_given_action_on_each_element() {
        List<Dog> dogs = new ArrayList<>();
        dogIdeaList.forEach(dogs::add);

        assertEquals(dogIdeaList.toList(), dogs);
    }


    // List operations ==============================================================================
    @Test
    public void reduce_initialValue_Returns_initialValue_if_this_list_does_not_contain_elements() {
        assertEquals(0, IdeaList.<Integer>empty().reduce(0, (acc, integer) -> 10).intValue());
    }

    @Test
    public void reduce_initialValue_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator() {
        IdeaList<Integer> integers = IdeaList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(13, integers.reduce(0, Integer::sum).intValue());
    }

    @Test
    public void reduce_initialValue_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator_2() {
        IdeaList<LocalDate> expected = IdeaList.of(l7, l6, l5, l4, l3, l2, l1);
        assertEquals(expected, localDateIdeaList.reduce(IdeaList.empty(), IdeaList::addToFront));
    }

    @Test
    public void reduceIndexed_initialValue_Returns_initialValue_if_this_list_does_not_contain_elements() {
        assertEquals(0, IdeaList.<Integer>empty().reduceIndexed(0, (index, acc, integer) -> 10).intValue());
    }

    @Test
    public void reduceIndexed_initialValue_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_provided_with_accumulator_and_index() {
        IdeaList<Integer> integers = IdeaList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(34, integers.reduceIndexed(0, (index, acc, integer) -> acc + index + integer).intValue());
    }

    @Test
    public void reduceIndexed_initialValue_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_provided_with_accumulator_and_index_2() {
        String expected = "0Zoe1Alex2Patricia3Jeffie4Daenerys5John";
        assertEquals(expected, personIdeaList.select(Person::getFirstName).reduceIndexed("", (index, acc, firstName) -> acc + index + firstName));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void reduce_Throws_exception_if_this_list_does_not_contain_elements() {
        IdeaList.<Integer>empty().reduce((acc, integer) -> 10);
    }

    @Test
    public void reduce_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator() {
        IdeaList<Integer> integers = IdeaList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(13, integers.reduce(Integer::sum).intValue());
    }

    @Test
    public void reduce_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator_2() {
        IdeaList<String> given = IdeaList.of("I", "would", "like", "to", "merge", "this", "list", "into", "one");
        String expected = "Iwouldliketomergethislistintoone";
        assertEquals(expected, given.reduce((acc, string) -> acc + string));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void reduceIndexed_Throws_exception_if_this_list_does_not_contain_elements() {
        IdeaList.<Integer>empty().reduceIndexed((index, acc, integer) -> 10);
    }

    @Test
    public void reduceIndexed_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_provided_with_accumulator_and_index() {
        IdeaList<Integer> integers = IdeaList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(34, integers.reduceIndexed((index, acc, integer) -> acc + index + integer).intValue());
    }

    @Test
    public void reduceIndexed_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_provided_with_accumulator_and_index_2() {
        String expected = "Zoe1Alex2Patricia3Jeffie4Daenerys5John";
        assertEquals(expected, personIdeaList.select(Person::getFirstName).reduceIndexed((index, acc, firstName) -> acc + index + firstName));
    }

    @Test
    public void map_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.<Person>empty().map(Person::getSecondName));
    }

    @Test
    public void map_Applies_given_transformation_on_each_element_in_list() {
        IdeaList<String> expected = IdeaList.of("Turner", "Johnson", "Vanilla", "Allstar", "Targaryen", "Jefferson");
        assertEquals(expected, personIdeaList.map(Person::getSecondName));
    }

    @Test
    public void mapIndexed_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.<Person>empty().mapIndexed((index, person) -> person.getSecondName()));
    }

    @Test
    public void mapIndexed_Applies_given_transformation_on_each_element_provided_with_its_index() {
        IdeaList<String> expectedList = IdeaList.of("Turner0", "Johnson1", "Vanilla2", "Allstar3", "Targaryen4", "Jefferson5");
        assertEquals(expectedList, personIdeaList.mapIndexed((index, person) -> person.getSecondName() + index));
    }

    @Test
    public void select_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.<Person>empty().select(Person::getSecondName));
    }

    @Test
    public void select_Applies_given_transformation_on_each_element_in_list() {
        IdeaList<String> expected = IdeaList.of("Turner", "Johnson", "Vanilla", "Allstar", "Targaryen", "Jefferson");
        assertEquals(expected, personIdeaList.select(Person::getSecondName));
    }

    @Test
    public void selectIndexed_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.<Person>empty().selectIndexed((index, person) -> person.getSecondName()));
    }

    @Test
    public void selectIndexed_Applies_given_transformation_on_each_element_provided_with_its_index() {
        IdeaList<String> expectedList = IdeaList.of("Turner0", "Johnson1", "Vanilla2", "Allstar3", "Targaryen4", "Jefferson5");
        assertEquals(expectedList, personIdeaList.selectIndexed((index, person) -> person.getSecondName() + index));
    }

    @Test
    public void where_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.<Animal>empty().where(animal -> true));
    }

    @Test
    public void where_Returns_all_elements_that_satisfy_given_predicate() {
        IdeaList<Animal> expectedList = IdeaList.of(d3, d5, d6);
        assertEquals(expectedList, dogIdeaList.where(animal -> animal.getAge() > 5));
    }

    @Test
    public void whereIndexed_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.<Animal>empty().whereIndexed((index, animal) -> true));
    }

    @Test
    public void whereIndexed_Returns_all_elements_that_satisfy_given_predicate_provided_with_index() {
        IdeaList<Animal> expectedList = IdeaList.of(d1, d3, d5);
        assertEquals(expectedList, dogIdeaList.whereIndexed((index, animal) -> (index % 2) == 0));
    }

    @Test
    public void filter_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.<Animal>empty().filter(animal -> true));
    }

    @Test
    public void filter_Returns_all_elements_that_satisfy_given_predicate() {
        IdeaList<Animal> expectedList = IdeaList.of(d3, d5, d6);
        assertEquals(expectedList, dogIdeaList.filter(animal -> animal.getAge() > 5));
    }

    @Test
    public void filterIndexed_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.<Animal>empty().filterIndexed((index, animal) -> true));
    }

    @Test
    public void filterIndexed_Returns_all_elements_that_satisfy_given_predicate_provided_with_index() {
        IdeaList<Animal> expectedList = IdeaList.of(d1, d3, d5);
        assertEquals(expectedList, dogIdeaList.filterIndexed((index, animal) -> (index % 2) == 0));
    }

    @Test
    public void findAll_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.<Animal>empty().findAll(animal -> true));
    }

    @Test
    public void findAll_Returns_all_elements_that_satisfy_given_predicate() {
        IdeaList<Animal> expectedList = IdeaList.of(d3, d5, d6);
        assertEquals(expectedList, dogIdeaList.findAll(animal -> animal.getAge() > 5));
    }

    @Test
    public void findAllIndexed_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.<Animal>empty().findAllIndexed((index, animal) -> true));
    }

    @Test
    public void findAllIndexed_Returns_all_elements_that_satisfy_given_predicate_provided_with_index() {
        IdeaList<Animal> expectedList = IdeaList.of(d1, d3, d5);
        assertEquals(expectedList, dogIdeaList.findAllIndexed((index, animal) -> (index % 2) == 0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void flatten_Throws_exception_if_list_is_empty() {
        IdeaList.empty().flatten();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void flatten_Throws_exception_if_list_is_already_flat() {
        personIdeaList.flatten();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void flatten_Throws_exception_if_list_contains_elements_that_are_not_Iterable() {
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("a", 1); map1.put("b", 2);
        Map<String, Integer> map2 = new HashMap<>();
        map2.put("c", 3); map2.put("d", 4);
        IdeaList.of(map1, map2).flatten();
    }

    @Test
    public void flatten_Concatenates_all_nested_Iterables_into_one_list() {
        IdeaList<Set<String>> given = IdeaList.of(
                new LinkedHashSet<>(Collections.singletonList("one")),
                new LinkedHashSet<>(Arrays.asList("two", "three", "four")),
                new LinkedHashSet<>(Arrays.asList("five", "six", "seven", "eight"))
        );
        IdeaList<String> expected = IdeaList.of("one", "two", "three", "four", "five", "six", "seven", "eight");
        assertEquals(expected, given.flatten());
    }

    @Test
    public void flatten_Concatenates_all_nested_Iterables_into_one_list_2() {
        IdeaList<Queue<Person>> given = IdeaList.of(
                new LinkedList<>(Arrays.asList(p1, p2)),
                new LinkedList<>(Collections.singletonList(p3)),
                new LinkedList<>(Arrays.asList(p4, p5, p6))
        );
        assertEquals(personIdeaList, given.flatten());
    }

    @Test
    public void flatten_Concatenates_all_nested_IdeaLists_into_one_list() {
        IdeaList<IdeaList<String>> given = IdeaList.of(
                IdeaList.of("one"),
                IdeaList.of("two", "three", "four"),
                IdeaList.of("five", "six", "seven", "eight")
        );
        IdeaList<String> expected = IdeaList.of("one", "two", "three", "four", "five", "six", "seven", "eight");
        assertEquals(expected, given.flatten());
    }

    @Test
    public void zipWith_Triplet_Gives_correct_tail_when_value_is_not_calculated() {
        IdeaList<Triplet<Person, Integer, LocalDate>> given = personIdeaList.zipWith(integerIdeaList, localDateIdeaList);
        assertEquals(Triplet.of(p2, i2, l2), given.tail.value().value.value());
        assertEquals(Triplet.of(p5, i5, l5), given.tail.value().tail.value().tail.value().tail.value().value.value());
    }

    @Test
    public void zipWith_Triplet_Returns_empty_list_if_this_list_is_empty() {
        LocalDate ld1 = LocalDate.of(1993, 6, 1);
        LocalDate ld2 = LocalDate.of(40, 2, 8);

        Queue<LocalDate> dates = new LinkedList<>(Arrays.asList(ld1, ld2));
        Set<Integer> ints = new LinkedHashSet<>(Arrays.asList(-22, 3, 7, 1025, 0));

        assertEquals(IdeaList.empty(), IdeaList.empty().zipWith(dates, ints));
    }

    @Test
    public void zipWith_Triplet_Returns_empty_list_if_first_given_list_is_empty() {
        IdeaList<Dog> dogs = IdeaList.of(d1, d2, d3, d4);
        Set<Integer> ints = new LinkedHashSet<>(Arrays.asList(-22, 3, 7, 1025, 0));

        assertEquals(IdeaList.empty(), dogs.zipWith(IdeaList.empty(), ints));
    }

    @Test
    public void zipWith_Triplet_Returns_empty_list_if_second_given_list_is_empty() {
        LocalDate ld1 = LocalDate.of(1993, 6, 1);
        LocalDate ld2 = LocalDate.of(40, 2, 8);

        IdeaList<Dog> dogs = IdeaList.of(d1, d2, d3, d4);
        Queue<LocalDate> dates = new LinkedList<>(Arrays.asList(ld1, ld2));

        assertEquals(IdeaList.empty(), dogs.zipWith(dates, IdeaList.empty()));
    }

    @Test
    public void zipWith_Returns_list_of_Tuples_built_from_elements_of_this_list_and_other_Iterables_with_same_index() {
        LocalDate ld1 = LocalDate.of(1993, 6, 1);
        LocalDate ld2 = LocalDate.of(40, 2, 8);

        IdeaList<Dog> dogs = IdeaList.of(d1, d2, d3, d4);
        Queue<LocalDate> dates = new LinkedList<>(Arrays.asList(ld1, ld2));
        Set<Integer> ints = new LinkedHashSet<>(Arrays.asList(-22, 3, 7, 1025, 0));

        IdeaList<Triplet<Dog, LocalDate, Integer>> expected = IdeaList.of(
                Triplet.of(d1, ld1, -22),
                Triplet.of(d2, ld2, 3)
        );
        assertEquals(expected, dogs.zipWith(dates, ints));
    }

    @Test
    public void zipWith_Pair_Returns_empty_list_if_this_list_is_empty() {
        Queue<Person> people = new LinkedList<>(Arrays.asList(p5, p6, p2));
        assertEquals(IdeaList.empty(), IdeaList.empty().zipWith(people));
    }

    @Test
    public void zipWith_Pair_Returns_empty_list_if_given_list_is_empty() {
        IdeaList<Dog> dogs = IdeaList.of(d1, d2, d3, d4);
        assertEquals(IdeaList.empty(), dogs.zipWith(IdeaList.empty()));
    }

    @Test
    public void zipWith_Returns_list_of_Pairs_built_from_elements_of_this_list_and_other_Iterable_with_same_index() {
        IdeaList<Dog> dogs = IdeaList.of(d1, d2, d3, d4);
        Queue<Person> people = new LinkedList<>(Arrays.asList(p5, p6, p2));

        IdeaList<Pair<Dog, Person>> expected = IdeaList.of(
                Pair.of(d1, p5),
                Pair.of(d2, p6),
                Pair.of(d3, p2)
        );
        assertEquals(expected, dogs.zipWith(people));
    }

    @Test
    public void withIndex_Returns_an_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.empty().withIndex());
    }

    @Test
    public void withIndex_Returns_a_list_of_IndexElement_pairs_where_each_element_is_bundled_together_with_its_index() {
        IndexElement<Person> pair1 = IndexElement.of(0, p1);
        IndexElement<Person> pair2 = IndexElement.of(1, p2);
        IndexElement<Person> pair3 = IndexElement.of(2, p3);
        IndexElement<Person> pair4 = IndexElement.of(3, p4);
        IndexElement<Person> pair5 = IndexElement.of(4, p5);
        IndexElement<Person> pair6 = IndexElement.of(5, p6);
        IdeaList<IndexElement<Person>> expected = IdeaList.of(pair1, pair2, pair3, pair4, pair5, pair6);
        assertEquals(expected, personIdeaList.withIndex());
    }

    @Test(expected = IllegalArgumentException.class)
    public void step_Throws_exception_if_given_length_negative() {
        IdeaList.empty().step(-17);
    }

    @Test(expected = IllegalArgumentException.class)
    public void step_Throws_exception_if_given_length_just_negative() {
        IdeaList.empty().step(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void step_Throws_exception_if_given_length_0() {
        IdeaList.empty().step(0);
    }

    @Test
    public void step_Returns_empty_list_if_this_list_is_empty() {
        assertEquals(IdeaList.empty(), IdeaList.empty().step(1));
    }

    @Test
    public void step_Returns_singleton_list_if_given_length_is_bigger_than_length_of_this_list() {
        assertEquals(IdeaList.of(p1), IdeaList.of(p1, p2, p3).step(4));
    }

    @Test
    public void step_Returns_a_list_containing_every_2nd_element_of_this_list_if_given_length_is_2() {
        assertEquals(IdeaList.of(p1, p3, p5), personIdeaList.step(2));
    }

    @Test
    public void step_Returns_a_list_containing_every_3rd_element_of_this_list_if_given_length_is_3() {
        assertEquals(IdeaList.of(p1, p4), personIdeaList.step(3));
    }
}