package idealist;

import idealist.range.Range;
import idealist.tuple.IndexElement;
import idealist.tuple.Pair;
import idealist.tuple.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static idealist.Lambda.alwaysFalse;
import static idealist.Lambda.alwaysTrue;
import static idealist.TestUtils.assertContentEqual;
import static idealist.TestUtils.assertEmpty;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;


@DisplayNameGeneration(TestNameGenerator.class)
public class IdeaListTest {
    private int i1, i2, i3, i4, i5, i6, i7, i8;
    private Person p1, p2, p3, p4, p5, p6;
    private Dog d1, d2, d3, d4, d5, d6;
    private Cat c1, c2, c3, c4;
    private LocalDate l1, l2, l3, l4, l5, l6, l7;

    private IdeaList<Integer> integerIdeaList;
    private IdeaList<Person> personIdeaList;
    private IdeaList<Dog> dogIdeaList;
    private IdeaList<LocalDate> localDateIdeaList;

    private LinkedHashSet<Person> personSet;
    private LinkedList<Dog> dogLinkedList;

    @BeforeEach
    public void setUp() {
        initialiseIntegers();
        initialisePeople();
        initialiseDogs();
        initialiseCats();
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

        /*p1 = new Person("Cornelia", "Viraja");
        p2 = new Person("Dagda", "Alfredo");
        p3 = new Person("Veera", "Raban");
        p4 = new Person("Vanesa", "Luvinia");
        p5 = new Person("Leonard", "Priscilla");
        p6 = new Person("Tory", "Aquila");*/
    }

    private void initialiseDogs() {
        d1 = new Dog(5, "Fifi");
        d2 = new Dog(1, "Lala");
        d3 = new Dog(6, "Momo");
        d4 = new Dog(4, "Shiba");
        d5 = new Dog(13, "Floof");
        d6 = new Dog(9, "Paula");

        // Lorrie
        // Eifion
        // Osheen
        // Bede
        // Quirijn
        // Olle
    }

    private void initialiseCats() {
        c1 = new Cat(105, "Bella");
        c2 = new Cat(0, "Kitty");
        c3 = new Cat(7, "Lily");
        c4 = new Cat(4, "Charlie");

        // Jia
        // Signe
        // Dinh
        // Hakon
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
        dogLinkedList = new LinkedList<>(dogIdeaList.toList());
    }


    // Constructors and factory methods =============================================================
    @Test
    public void empty_ReturnsAnEmptyIdeaList() {
        assertEmpty(IdeaList.empty());
    }

    /*@Test //(expected = NullPointerException.class)
    public void ofIterable_NullAsParam_ThrowException() {
        IdeaList.of((Iterable<Object>) null);
    }*/

    @Test
    public void ofIterable_NullAsParameter_ThrowException() {
        assertThrows(NullPointerException.class, () -> IdeaList.of((Iterable<Object>) null),
                "Method should have thrown a NullPointerException");
    }

    /*@Test
    public void of_Iterable_Creates_empty_IdeaList_if_given_Iterable_is_empty() {
        assertEmpty( IdeaList.of(new PriorityQueue<>()) );
    }*/

    @Test
    public void ofIterable_EmptyIterable_ReturnEmptyIdeaList() {
        Iterable<String> emptyIterable = new PriorityQueue<>();
        assertEmpty( IdeaList.of(emptyIterable) );
    }

    /*@Test
    public void of_Iterable_Creates_IdeaList_of_given_Iterable_containing_null() {
        Set<Dog> dogSet = new LinkedHashSet<>(asList(d2, d6, d1, null, d1, null));
        assertContentEqual(dogSet, IdeaList.of(dogSet));
    }*/

    @Test
    public void ofIterable_IterableContainingNullValues_ReturnIdeaListOfIterable() {
        Set<Dog> iterableContainingNulls = new LinkedHashSet<>(asList(d2, d6, d1, null, d1, null));
        assertContentEqual(iterableContainingNulls, IdeaList.of(iterableContainingNulls));
    }

    /*@Test
    public void of_Iterable_Creates_IdeaList_of_given_Iterable() {
        assertContentEqual(personSet, IdeaList.of(personSet));
    }*/

    @Test
    public void ofIterable_NormalIterable_ReturnIdeaListOfIterable() {
        assertContentEqual(personSet, IdeaList.of(personSet));
    }

    @Test
    public void of_Gives_correct_tail_when_value_is_not_calculated() {
        assertEquals(p2, personIdeaList.tail.value().value.value());
        assertEquals(p5, personIdeaList.tail.value().tail.value().tail.value().tail.value().value.value());
    }

    @Test
    public void of_elements_Creates_singleton_IdeaList_containing_null_if_given_elements_null() {
        assertNull(IdeaList.of((Object) null).single());
    }

    @Test
    public void of_elements_Creates_empty_IdeaList_if_no_elements_are_given() {
        assertEmpty( IdeaList.of() );
    }

    @Test
    public void of_elements_Creates_singleton_IdeaList_of_given_element() {
        Dog dog = new Dog(88, "Fifi");
        IdeaList<Dog> dogs = IdeaList.of(dog);
        assertEquals(dog, dogs.single());
    }

    /*@Test
    public void of_elements_Creates_IdeaList_of_given_elements() {
        IdeaList<Object> objects = IdeaList.of("bla", "foo", "zaza");
        assertEquals("bla", objects.value.value());
        assertEquals("foo", objects.tail.value().value.value());
        assertEquals("zaza", objects.tail.value().tail.value().value.value());
        assertEmpty(objects.tail.value().tail.value().tail.value());
    }*/

    @Test
    public void of_elements_Creates_IdeaList_of_given_elements_containing_null() {
        List<Dog> dogList = asList(null, d3, d4, d1, null, d1, null);
        IdeaList<Dog> dogIdeaList = IdeaList.of(null, d3, d4, d1, null, d1, null);
        assertContentEqual(dogList, dogIdeaList);
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
        assertEmpty(animals.tail.value().tail.value().tail.value().tail.value().tail.value().tail.value());
    }

    @Test
    public void of_MutableList_Creates_empty_IdeaList_if_given_MutableList_is_empty() {
        MutableList<Person> mutableList = MutableList.empty();
        assertEmpty( IdeaList.of(mutableList) );
    }

    @Test
    public void of_MutableList_Creates_IdeaList_of_given_MutableList() {
        Lazy<LocalDate> lazy1 = Lazy.of(() -> l1);
        Lazy<LocalDate> lazy3 = Lazy.of(() -> l3);
        Lazy<LocalDate> lazy5 = Lazy.of(() -> l5);
        Lazy<LocalDate> lazy6 = Lazy.of(() -> l6);

        MutableList<LocalDate> mutableList = MutableList.of(lazy3, lazy1, lazy6, lazy5, lazy1);
        IdeaList<LocalDate> ideaList = IdeaList.of(mutableList);
        List<LocalDate> expected = asList(l3, l1, l6, l5, l1);
        assertContentEqual(expected, ideaList);
    }

    /*@Test //(expected = NullPointerException.class)
    public void initialiseWith_Throws_exception_if_given_indexToElement_null() {
        IdeaList.initialiseWith(6, null);
    }*/

    @Test
    public void initialiseWith_Returns_an_empty_list_if_given_length_0() {
        assertEmpty( IdeaList.initialiseWith(0, index -> index) );
    }

    @Test //(expected = IllegalArgumentException.class)
    public void initialiseWith_Throws_exception_if_given_length_just_negative() {
        IdeaList.initialiseWith(-1, index -> index);
    }

    @Test //(expected = IllegalArgumentException.class)
    public void initialiseWith_Throws_exception_if_given_length_negative() {
        IdeaList.initialiseWith(-24, index -> index);
    }

    @Test
    public void initialiseWith_Creates_IdeaList_with_given_length_by_applying_function_to_each_index() {
        IdeaList<Dog> dogs = IdeaList.initialiseWith(3, index -> new Dog(index, "Lassy"));
        IdeaList<Dog> expected = IdeaList.of(new Dog(0, "Lassy"), new Dog(1, "Lassy"), new Dog(2, "Lassy"));
        assertEquals(expected, dogs);
    }


    // Getters ======================================================================================
    /*@Test //(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_index_negative() {
        localDateIdeaList.get(-75);
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_index_just_negative() {
        integerIdeaList.get(-1);
    }*/

    @Test //(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_negative_index_smaller_than_last_valid_index() {
        dogIdeaList.get(-75);
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_negative_index_just_smaller_than_last_valid_index() {
        localDateIdeaList.get(-8);
    }

    @Test
    public void get_Returns_element_at_given_negative_index_if_index_valid() {
        assertEquals(p6, personIdeaList.get(-1));
        assertEquals(p1, personIdeaList.get(-6));
        assertEquals(d4, dogIdeaList.get(-3));
        assertEquals(d2, dogIdeaList.get(-5));
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_index_just_bigger_than_last_valid_index() {
        personIdeaList.get(6);
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void get_Throws_IndexOutOfBoundsException_if_index_bigger_than_last_valid_index() {
        dogIdeaList.get(21);
    }

    @Test
    public void get_Returns_element_at_given_index_if_index_valid_in_list_containing_nulls() {
        IdeaList<Dog> input = IdeaList.of(d2, null, d3, d1, d2, null);
        assertEquals(d2, input.get(0));
        assertNull(input.get(1));
        assertEquals(d2, input.get(4));
        assertNull(input.get(5));
    }

    @Test
    public void get_Returns_element_at_given_index_if_index_valid() {
        assertEquals(p4, personIdeaList.get(3));
        assertEquals(p6, personIdeaList.get(5));
        assertEquals(d1, dogIdeaList.get(0));
        assertEquals(d3, dogIdeaList.get(2));
    }

    @Test //(expected = NoSuchElementException.class)
    public void first_Throws_exception_if_list_is_empty() {
        IdeaList.empty().first();
    }

    @Test
    public void first_Returns_first_element_of_this_list_containing_nulls() {
        IdeaList<Person> input = IdeaList.of(null, p6, p5, null, null, p1, p1, p1);
        assertNull(input.first());
    }

    @Test
    public void first_Returns_first_element_of_this_list() {
        assertEquals(i1, integerIdeaList.first().intValue());
        assertEquals(p1, personIdeaList.first());
        assertEquals(d1, dogIdeaList.first());
        assertEquals(l1, localDateIdeaList.first());
    }

    @Test //(expected = IllegalArgumentException.class)
    public void single_Throws_exception_if_list_containing_nulls_contains_more_than_one_element() {
        IdeaList.of(7, 8, 9, null, 7, 8, 0).single();
    }

    @Test //(expected = IllegalArgumentException.class)
    public void single_Throws_exception_if_list_contains_two_elements() {
        IdeaList.of(d5, d6).single();
    }

    @Test //(expected = IllegalArgumentException.class)
    public void single_Throws_exception_if_list_contains_more_than_one_element() {
        integerIdeaList.single();
    }

    @Test //(expected = NoSuchElementException.class)
    public void single_Throws_exception_if_list_is_empty() {
        IdeaList.empty().single();
    }

    @Test
    public void single_Returns_the_only_element_in_this_list_given_singleton_list_containing_null() {
        assertNull(IdeaList.of((Object) null).single());
    }

    @Test
    public void single_Returns_the_only_element_in_this_list() {
        assertEquals(p4, IdeaList.of(p4).single());
        assertEquals(d2, IdeaList.of(d2).single());
        assertEquals(l5, IdeaList.of(l5).single());
    }

    @Test //(expected = NoSuchElementException.class)
    public void last_Throws_exception_if_list_is_empty() {
        IdeaList.empty().last();
    }

    @Test
    public void last_Returns_last_element_of_this_list_containing_nulls() {
        IdeaList<Person> input = IdeaList.of(null, p6, p5, null, null, p1, p1, p1, null);
        assertNull(input.last());
    }

    @Test
    public void last_Returns_only_element_in_singleton_list() {
        assertEquals(i7, IdeaList.of(i7).last().intValue());
    }

    @Test
    public void last_Returns_last_element_of_this_list() {
        assertEquals(i8, integerIdeaList.last().intValue());
        assertEquals(p6, personIdeaList.last());
        assertEquals(d6, dogIdeaList.last());
        assertEquals(l7, localDateIdeaList.last());
    }

    @Test //(expected = IllegalArgumentException.class)
    public void random_Throws_exception_if_list_is_empty() {
        IdeaList.empty().random();
    }

    @Test
    public void random_Returns_random_element_from_this_list_containing_null() {
        IdeaList<LocalDate> input = IdeaList.of(null, null, null, null, null);
        assertNull(input.random());
        assertNull(input.random());
        assertNull(input.random());
    }

    @Test
    public void random_Returns_only_element_in_singleton_list() {
        assertEquals(l2, IdeaList.of(l2).random());
    }

    @Test
    public void random_Returns_random_element_from_this_list() {
        assertTrue(integerIdeaList.contains(integerIdeaList.random()));
        assertTrue(personIdeaList.contains(personIdeaList.random()));
        assertTrue(dogIdeaList.contains(dogIdeaList.random()));
        assertTrue(localDateIdeaList.contains(localDateIdeaList.random()));
    }

    @Test
    public void findFirst_Returns_empty_Optional_instance_if_list_is_empty() {
        assertEmpty( IdeaList.empty().findFirst(alwaysTrue()) );
    }

    /*@Test
    public void findFirst_Returns_empty_Optional_instance_if_no_element_matches_given_predicate() {
        assertEmpty( integerIdeaList.findFirst(integer -> integer > 75 && integer < 100) );
        assertEmpty( dogIdeaList.findFirst(animal -> animal.getAge() > 13) );
    }*/

    @Test
    public void findFirst_Returns_empty_Optional_instance_if_no_element_matches_given_predicate() {
        assertEmpty( integerIdeaList.findFirst(alwaysFalse()) );
    }

    /*@Test
    public void findFirst_Returns_first_element_matching_given_predicate() {
        IdeaList<Person> input = IdeaList.of(p1, p1, p2, null, p3, p3, null);
        assertEquals(Optional.empty(), input.findFirst(Objects::isNull));
    }*/

    @Test //(expected = NullPointerException.class)
    public void findFirst_Throws_exception_if_null_satisfies_given_predicate() {
        IdeaList.of(p1, p1, p2, null, p3, p3, null).findFirst(Objects::isNull);
    }

    @Test
    public void findFirst_Returns_first_element_matching_given_predicate_in_list_with_nulls() {
        IdeaList<Dog> input = IdeaList.of(d2, d3, d3, null, d6, d5, null);
        assertEquals(d5, input.findFirst(dog -> Objects.equals(dog, d5)).orElseThrow());
    }

    // Is this test case necessary / useful?
    @Test
    public void findFirst_Returns_only_element_in_singleton_list() {
        assertEquals(p2, IdeaList.of(p2).findFirst(alwaysTrue()).orElseThrow());
    }

    @Test
    public void findFirst_Returns_first_element_matching_given_predicate() {
        assertEquals(i8, integerIdeaList.findFirst(integer -> integer > 100).orElseThrow().intValue());
        assertEquals(p3, personIdeaList.findFirst(person -> person.getFirstName().contains("t")).orElseThrow());
        assertEquals(l4, localDateIdeaList.findFirst(localDate -> localDate.getMonthValue() < 3).orElseThrow());
    }

    @Test
    public void findFirstIndexed_Returns_empty_Optional_instance_if_list_is_empty() {
        assertEmpty( IdeaList.empty().findFirstIndexed((index, elem) -> true) );
    }

    @Test
    public void findFirstIndexed_Returns_empty_Optional_instance_if_no_element_matches_given_predicate() {
        assertEmpty( integerIdeaList.findFirstIndexed((index, integer) -> false) );
    }

    @Test //(expected = NullPointerException.class)
    public void findFirstIndexed_Throws_exception_if_null_satisfies_given_predicate() {
        IdeaList.of(p1, p1, p2, null, p3, p3, null).findFirstIndexed((index, person) -> person == null);
    }

    /*@Test
    public void findFirstIndexed_Returns_first_element_matching_given_predicate_in_list_with_nulls() {
        IdeaList<Dog> input = IdeaList.of(d2, d3, d3, null, d6, d5, null);
        assertEquals(d5, input.findFirstIndexed(dog -> Objects.equals(dog, d5)).orElseThrow());
    }*/

    @Test
    public void findFirstIndexed_Returns_first_element_matching_given_predicate() {
        assertEquals(i5, integerIdeaList.findFirstIndexed((index, integer) -> index > 0 && integer > 2).orElseThrow().intValue());
        assertEquals(p5, personIdeaList.findFirstIndexed((index, person) -> index > 2 && person.getSecondName().contains("n")).orElseThrow());
        assertEquals(l7, localDateIdeaList.findFirstIndexed((index, localDate) -> index > 5 && localDate.getYear() < 2000).orElseThrow());
    }

    @Test
    public void first_Returns_empty_Optional_instance_if_list_is_empty() {
        assertEmpty( IdeaList.empty().first(alwaysTrue()) );
    }

    @Test
    public void first_Returns_empty_Optional_instance_if_no_element_matches_given_predicate() {
        assertEmpty( integerIdeaList.first(alwaysFalse()) );
    }

    @Test //(expected = NullPointerException.class)
    public void first_Throws_exception_if_null_satisfies_given_predicate() {
        IdeaList.of(p1, p1, p2, null, p3, p3, null).first(Objects::isNull);
    }

    @Test
    public void first_Returns_first_element_matching_given_predicate_in_list_with_nulls() {
        IdeaList<Dog> input = IdeaList.of(d2, d3, d3, null, d6, d5, null);
        assertEquals(d5, input.first(dog -> Objects.equals(dog, d5)).orElseThrow());
    }

    @Test
    public void first_Returns_first_element_matching_given_predicate() {
        assertEquals(i8, integerIdeaList.first(integer -> integer > 100).orElseThrow().intValue());
        assertEquals(p3, personIdeaList.first(person -> person.getFirstName().contains("t")).orElseThrow());
        assertEquals(l4, localDateIdeaList.first(localDate -> localDate.getMonthValue() < 3).orElseThrow());
    }

    @Test
    public void indexOfFirst_predicate_Returns_empty_Optional_if_list_is_empty() {
        assertEquals(Optional.empty(), IdeaList.empty().indexOfFirst(alwaysTrue()));
    }

    /*@Test
    public void indexOfFirst_predicate_Returns_empty_OptionalInt_if_no_element_matches_given_predicate() {
        assertEquals(OptionalInt.empty(), integerIdeaList.indexOfFirst(integer -> integer == 2));
        assertEquals(OptionalInt.empty(), personIdeaList.indexOfFirst(person -> person.getFirstName().contains("b") || person.getSecondName().contains("b")));
        assertEquals(OptionalInt.empty(), dogIdeaList.indexOfFirst(animal -> animal.getAge() > 13));
        assertEquals(OptionalInt.empty(), localDateIdeaList.indexOfFirst(localDate -> localDate.getYear() > 2020 && localDate.getYear() < 4058));
    }*/

    @Test
    public void indexOfFirst_predicate_Returns_empty_Optional_if_no_element_matches_given_predicate() {
        assertEquals(Optional.empty(), personIdeaList.indexOfFirst(alwaysFalse()));
    }

    @Test
    public void indexOfFirst_predicate_Returns_index_of_first_element_that_matches_given_predicate_in_list_with_nulls() {
        IdeaList<Person> input = IdeaList.of(p1, p1, p2, null, p3, p3, null);
        assertEquals(3, input.indexOfFirst(Objects::isNull).orElseThrow().intValue());
    }

    @Test
    public void indexOfFirst_predicate_Returns_index_of_first_element_that_matches_given_predicate() {
        assertEquals(7, integerIdeaList.indexOfFirst(integer -> integer > 75).orElseThrow().intValue());
        assertEquals(1, personIdeaList.indexOfFirst(person -> person.getSecondName().contains("h")).orElseThrow().intValue());
        assertEquals(4, dogIdeaList.indexOfFirst(animal -> animal.getAge() > 6).orElseThrow().intValue());
        assertEquals(0, localDateIdeaList.indexOfFirst(alwaysTrue()).orElseThrow().intValue());
    }

    @Test
    public void indexOfFirst_element_Returns_empty_OptionalInt_if_list_is_empty() {
        assertEquals(Optional.empty(), IdeaList.empty().indexOfFirst(p1));
    }

    @Test
    public void indexOfFirst_element_Returns_empty_OptionalInt_if_list_does_not_contain_given_element() {
        assertEquals(Optional.empty(), integerIdeaList.indexOfFirst(101));
        assertEquals(Optional.empty(), personIdeaList.indexOfFirst(new Person("James", "Walker")));
        assertEquals(Optional.empty(), dogIdeaList.indexOfFirst(new Dog(2, "Bolt")));
        assertEquals(Optional.empty(), localDateIdeaList.indexOfFirst(LocalDate.of(441, 3, 11)));
    }

    @Test
    public void indexOfFirst_element_Returns_index_of_first_occurrence_of_given_element_in_this_list_containing_nulls() {
        IdeaList<LocalDate> input = IdeaList.of(l7, l7, l6, l1, l2, l3, null, l7);
        assertEquals(6, input.indexOfFirst((LocalDate) null).orElseThrow().intValue());
    }

    @Test
    public void indexOfFirst_element_Returns_index_of_first_occurrence_of_given_element_in_this_list() {
        assertEquals(0, integerIdeaList.indexOfFirst(i1).orElseThrow().intValue());
        assertEquals(4, personIdeaList.indexOfFirst(p5).orElseThrow().intValue());
        assertEquals(2, dogIdeaList.indexOfFirst(d3).orElseThrow().intValue());
        assertEquals(6, localDateIdeaList.indexOfFirst(l7).orElseThrow().intValue());
    }

    /*@Test
    public void lastIndex_Returns_minus_one_if_list_is_empty() {
        assertEquals(-1, IdeaList.empty().lastIndex());
        assertEquals(-1, IdeaList.empty().lastIndex());
        assertEquals(-1, IdeaList.empty().lastIndex());
        assertEquals(-1, IdeaList.empty().lastIndex());
    }*/

    @Test //(expected = UnsupportedOperationException.class)
    public void lastIndex_Throws_UnsupportedOperationException_if_list_is_empty() {
        IdeaList.empty().lastIndex();
    }

    @Test
    public void lastIndex_Returns_index_of_last_element_in_list_containing_nulls() {
        IdeaList<Person> input = IdeaList.of(p4, null, p4, p5, null, null, p2, p3, p2, null);
        assertEquals(9, input.lastIndex());
    }

    @Test
    public void lastIndex_Returns_index_of_last_element() {
        assertEquals(0, IdeaList.of(i1).lastIndex());
        assertEquals(7, integerIdeaList.lastIndex());
        assertEquals(5, personIdeaList.lastIndex());
        assertEquals(5, dogIdeaList.lastIndex());
        assertEquals(6, localDateIdeaList.lastIndex());
    }

    @Test //(expected = UnsupportedOperationException.class)
    public void indices_Throws_UnsupportedOperationException_if_list_is_empty() {
        IdeaList.empty().indices();
    }

    @Test
    public void indices_Returns_all_indices_of_this_list_containing_nulls() {
        assertEquals(Range.from(0).upToAndIncluding(7), IdeaList.of(p6, p5, p4, null, p2, p1, null, null).indices());
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
    public void length_Returns_the_length_of_this_list_containing_nulls() {
        assertEquals(9, IdeaList.of(null, p6, p5, null, null, p1, p1, p1, null).length());
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
    public void toList_Transforms_IdeaList_containing_nulls_into_List() {
        List<Person> personList = asList(null, p1, null, p2, p3, p4, p5, p6, null);
        assertEquals(personList, IdeaList.of(personList).toList());
    }

    @Test
    public void toList_Transforms_IdeaList_into_List() {
        List<Person> personList = asList(p1, p2, p3, p4, p5, p6);
        List<Dog> dogList = asList(d1, d2, d3, d4, d5, d6);

        assertEquals(personList, IdeaList.of(personList).toList());
        assertEquals(dogList, IdeaList.of(dogList).toList());
    }

    @Test
    public void toArray_Returns_empty_array_if_this_list_is_empty() {
        assertArrayEquals(new Person[0], IdeaList.<Person>empty().toArray(Person.class));
    }

    // TODO change name
    @Test
    public void toArray_Transforms_IdeaList_into_polymorphic_array() {
        Animal[] animalArray = {d1, c4, c1, c1, c2};
        Animal[] animalArray2 = {c2, d5, c4, c4, d1, c3, d2};

        assertArrayEquals(animalArray, IdeaList.of(animalArray).toArray(Animal.class));
        assertArrayEquals(animalArray2, IdeaList.of(animalArray2).toArray(Animal.class));
    }

    @Test
    public void toArray_Transforms_IdeaList_into_array() {
        Person[] personList = {p1, p2, p3, p4, p5, p6};
        Dog[] dogList = {d1, d2, d3, d4, d5, d6};

        assertArrayEquals(personList, IdeaList.of(personList).toArray(Person.class));
        assertArrayEquals(dogList, IdeaList.of(dogList).toArray(Dog.class));
    }

    @Test
    public void hashCode_Returns_1_if_list_is_empty() {
        assertEquals(1, IdeaList.empty().hashCode());
    }

    @Test
    public void hashCode_Always_returns_the_same_hash_code_for_the_same_list() {
        assertEquals(integerIdeaList.hashCode(), integerIdeaList.hashCode());
        assertEquals(personIdeaList.hashCode(), personIdeaList.hashCode());
        assertEquals(dogIdeaList.hashCode(), dogIdeaList.hashCode());
        assertEquals(localDateIdeaList.hashCode(), localDateIdeaList.hashCode());
    }

    @Test
    public void hashCode_Returns_same_hash_code_if_lists_are_equal() {
        IdeaList<Dog> dogs = IdeaList.of(d1, d2, d3, d4, d5, d6);
        assertEquals(dogIdeaList.hashCode(), dogs.hashCode());
    }

    @Test
    public void toString_Returns_String_containing_opening_and_closing_square_brackets_if_list_is_empty() {
        assertEquals("[]", IdeaList.empty().toString());
    }

    @Test
    public void toString_Returns_String_representation_of_each_element_surrounded_by_opening_and_closing_square_brackets() {
        assertEquals("[5, -7, 101, 0]", IdeaList.of(5, -7, 101, 0).toString());
    }

    @Test
    public void iterator_Returns_an_iterator_with_no_elements_if_list_is_empty() {
        Iterator<LocalDate> it = IdeaList.<LocalDate>empty().iterator();
        assertFalse(it.hasNext());
    }

    /*@Test
    public void iterator_Returns_Iterator_that_loops_through_this_list() {
        Iterator<Dog> it = dogIdeaList.iterator();
        assertEquals(d1, it.next());
        assertEquals(d2, it.next());
        assertEquals(d3, it.next());
        assertEquals(d4, it.next());
        assertEquals(d5, it.next());
        assertEquals(d6, it.next());
    }*/

    @Test
    public void iterator_Returns_Iterator_that_loops_through_this_list_containing_nulls() {
        Iterator<LocalDate> it = IdeaList.of(null, null, null, l7, l6, l1, l1, l5, null).iterator();
        assertTrue(it.hasNext());
        assertNull(it.next());
        assertTrue(it.hasNext());
        assertNull(it.next());
        assertTrue(it.hasNext());
        assertNull(it.next());
        assertTrue(it.hasNext());
        assertEquals(l7, it.next());
        assertTrue(it.hasNext());
        assertEquals(l6, it.next());
        assertTrue(it.hasNext());
        assertEquals(l1, it.next());
        assertTrue(it.hasNext());
        assertEquals(l1, it.next());
        assertTrue(it.hasNext());
        assertEquals(l5, it.next());
        assertTrue(it.hasNext());
        assertNull(it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void iterator_Returns_Iterator_that_loops_through_this_list() {
        Iterator<Dog> it = dogIdeaList.iterator();
        assertTrue(it.hasNext());
        assertEquals(d1, it.next());
        assertTrue(it.hasNext());
        assertEquals(d2, it.next());
        assertTrue(it.hasNext());
        assertEquals(d3, it.next());
        assertTrue(it.hasNext());
        assertEquals(d4, it.next());
        assertTrue(it.hasNext());
        assertEquals(d5, it.next());
        assertTrue(it.hasNext());
        assertEquals(d6, it.next());
        assertFalse(it.hasNext());
    }

    /*@Test
    public void iterator_Returns_Iterator_that_loops_through_this_and_correctly_indicates_whether_this_list_has_more_elements() {
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
    }*/


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
    public void equals_Returns_false_if_this_list_is_not_equal_to_given_list_containing_nulls() {
        IdeaList<Person> people1 = IdeaList.of(p2, null, p5, p2, null, p1, p4);
        IdeaList<Person> people2 = IdeaList.of(p2, null, p5, p2, null, p1, p3);

        assertNotEquals(people1, people2);
        assertNotEquals(people2, people1);
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
    public void equals_Returns_true_if_given_list_containing_nulls_has_same_type_as_this_list_and_all_elements_are_equal() {
        Person np1 = new Person("Zoe", "Turner");
        Person np2 = new Person("Alex", "Johnson");
        Person np3 = new Person("Patricia", "Vanilla");
        Person np4 = new Person("Jeffie", "Allstar");
        Person np5 = new Person("Daenerys", "Targaryen");
        Person np6 = new Person("John", "Jefferson");
        IdeaList<Person> people1 = IdeaList.of(null, np1, np2, null, np3, np4, np5, null, np6, null);
        IdeaList<Person> people2 = IdeaList.of(null, p1, p2, null, p3, p4, p5, null, p6, null);

        assertEquals(people1, people2);
        assertEquals(people2, people1);
    }

    @Test
    public void equals_Returns_true_if_given_list_has_same_type_as_this_list_and_all_elements_are_equal() {
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
    public void contains_Returns_false_if_this_list_does_not_contain_given_null_element() {
        assertFalse(personIdeaList.contains(null));
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
    public void contains_Returns_true_if_this_list_contains_given_null_element() {
        assertTrue(IdeaList.of(i7, i1, null).contains(null));
        assertTrue(IdeaList.of(null, l5, l1, l3, null, l1).contains(null));
    }

    @Test
    public void contains_Returns_true_if_this_list_contains_given_element() {
        assertTrue(integerIdeaList.contains(5));
        assertTrue(personIdeaList.contains(new Person("Alex", "Johnson")));
        assertTrue(dogIdeaList.contains(new Dog(5, "Shiba")));
        assertTrue(localDateIdeaList.contains(LocalDate.of(1974, 12, 25)));
    }

    @Test //(expected = NullPointerException.class)
    public void containsAll_Iterable_Throws_exception_if_given_elements_null() {
        assertFalse(localDateIdeaList.containsAll(null));
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
    public void containsAll_Iterable_Returns_true_if_this_list_containing_nulls_contains_all_given_elements() {
        Queue<Person> personQueue = new LinkedList<>(asList(null, new Person("Jefke", "Merens"), new Person("Marie", "Bosmans")));
        IdeaList<Person> personList = IdeaList.of(null, new Person("Jefke", "Merens"), null, new Person("Nathalie", "Hofdaal"), new Person("Marie", "Bosmans"), null);
        assertTrue(personList.containsAll(personQueue));
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
    public void isEmpty_Returns_false_if_list_containing_nulls_is_not_empty() {
        assertFalse(IdeaList.of((Object) null).isEmpty());
        assertFalse(IdeaList.of(l4, null, l4, l1, null, l2, l3, l2).isEmpty());
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
    public void isNested_Returns_false_if_this_list_containing_nulls_is_not_nested() {
        assertFalse(IdeaList.of(null, d1, d1, d1, d2).isNested());
    }

    @Test
    public void isNested_Returns_false_if_this_list_is_not_nested() {
        assertFalse(integerIdeaList.isNested());
        assertFalse(personIdeaList.isNested());
        assertFalse(dogIdeaList.isNested());
        assertFalse(localDateIdeaList.isNested());
    }

    @Test
    public void isNested_Returns_true_if_this_list_containing_nulls_is_nested_with_Iterables() {
        IdeaList<Set<Integer>> nestedIdeaList = IdeaList.of(
                new HashSet<>(asList(111, null, 2)),
                new HashSet<>(asList(1, null)),
                new HashSet<>(asList(null, 270, 31, 73, 500))
        );
        assertTrue(nestedIdeaList.isNested());
    }

    @Test
    public void isNested_Returns_true_if_this_list_is_nested_with_Iterables() {
        IdeaList<Set<Integer>> nestedIdeaList = IdeaList.of(
                new HashSet<>(asList(111, -25, 2)),
                new HashSet<>(asList(1, 88)),
                new HashSet<>(asList(-44, 270, 31, 73, 500))
        );
        assertTrue(nestedIdeaList.isNested());
    }

    @Test
    public void isNested_Returns_true_if_this_list_is_nested_with_IdeaLists() {
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
    public void all_Returns_true_if_all_elements_of_list_containing_nulls_match_given_predicate() {
        assertTrue(IdeaList.of(l3, null, l3, l4, l1).all(element -> !Objects.equals(l2, element)));
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
    public void any_Returns_true_if_list_containing_nulls_contains_elements() {
        assertTrue(IdeaList.of(l5, null, l6, l7, null, l7).any());
    }

    @Test
    public void any_Returns_true_if_list_contains_elements() {
        assertTrue(integerIdeaList.any());
        assertTrue(localDateIdeaList.any());
        assertTrue(IdeaList.of("bla").any());
    }

    @Test
    public void any_predicate_Returns_false_if_list_is_empty() {
        assertFalse(IdeaList.empty().any(alwaysTrue()));
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
    public void any_predicate_Returns_true_if_at_least_one_element_in_list_containing_nulls_matches_predicate() {
        assertTrue(IdeaList.of(p1, null, p3, p4, null, p1).any(element -> Objects.equals(p4, element)));
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
    public void none_predicate_Returns_true_if_no_element_in_list_containing_nulls_matches_predicate() {
        assertTrue(IdeaList.of(null, p2, p2, p4, null, p2).none(element -> Objects.equals(p5, element)));
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
    @Test //(expected = NullPointerException.class)
    public void insertAt_Iterable_Throws_exception_if_given_index_0_and_Iterable_null() {
        personIdeaList.insertAt(0, (Iterable<Person>) null);
    }

    @Test //(expected = IllegalArgumentException.class)
    public void insertAt_Iterable_Throws_exception_if_given_index_2_and_Iterable_null() {
        personIdeaList.insertAt(2, (Iterable<Person>) null);
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_this_list_is_empty() {
        IdeaList.<Person>empty().insertAt(0, personSet);
    }

    /*@Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_index_just_negative() {
        personIdeaList.insertAt(-1, Collections.emptyList());
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_index_negative() {
        personIdeaList.insertAt(-23, Collections.emptyList());
    }*/

    //get_Throws_IndexOutOfBoundsException_if_negative_index_smaller_than_last_valid_index

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_negative_index_just_smaller_than_last_valid_index() {
        personIdeaList.insertAt(-7, personSet);
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_negative_index_smaller_than_last_valid_index() {
        personIdeaList.insertAt(-23, personSet);
    }

    @Test
    public void insertAt_Iterable_Inserts_given_elements_at_specified_negative_index() {
        LocalDate local1 = LocalDate.of(1002, 7, 1);
        LocalDate local2 = LocalDate.of(4014, 6, 9);
        IdeaList<LocalDate> expected = IdeaList.of(l1, l2, local1, local2, l3, l4, l5, l6, l7);
        assertEquals(expected, localDateIdeaList.insertAt(-5, asList(local1, local2)));
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_index_just_bigger_than_upper_bound() {
        personIdeaList.insertAt(6, Collections.emptyList()).toList();
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_index_bigger_than_upper_bound() {
        personIdeaList.insertAt(47, Collections.emptyList()).toList();
    }

    @Test
    public void insertAt_Iterable_Returns_list_if_given_list_is_empty() {
        assertEquals(localDateIdeaList, localDateIdeaList.insertAt(3, new HashSet<>()));
    }

    @Test
    public void insertAt_Iterable_Inserts_given_elements_containing_null_at_specified_index() {
        LocalDate local = LocalDate.of(4014, 6, 9);
        IdeaList<LocalDate> expected = IdeaList.of(null, l4, null, null, local, l5);
        assertEquals(expected, IdeaList.of(null, l4, null, l5).insertAt(3, asList(null, local)));
    }

    @Test
    public void insertAt_Iterable_Inserts_given_elements_at_specified_index() {
        LocalDate local1 = LocalDate.of(1002, 7, 1);
        LocalDate local2 = LocalDate.of(4014, 6, 9);
        IdeaList<LocalDate> expected = IdeaList.of(l1, l2, l3, local1, local2, l4, l5, l6, l7);
        assertEquals(expected, localDateIdeaList.insertAt(3, asList(local1, local2)));
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_elements_Throws_exception_if_this_list_is_empty() {
        IdeaList.<Person>empty().insertAt(0, p1);
    }

    /*@Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_elements_Throws_exception_if_index_just_negative() {
        personIdeaList.insertAt(-1, p1);
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_elements_Throws_exception_if_index_negative() {
        personIdeaList.insertAt(-23, p1);
    }*/

    // TODO refactor from here
    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_elements_Throws_exception_if_negative_index_just_smaller_than_last_valid_index() {
        integerIdeaList.insertAt(-9, i1);
    }

    /*@Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_negative_index_just_smaller_than_last_valid_index() {
        personIdeaList.insertAt(-7, personSet);
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_Iterable_Throws_exception_if_negative_index_smaller_than_last_valid_index() {
        personIdeaList.insertAt(-23, personSet);
    }

    @Test
    public void insertAt_Iterable_Inserts_given_elements_at_specified_negative_index() {
        LocalDate local1 = LocalDate.of(1002, 7, 1);
        LocalDate local2 = LocalDate.of(4014, 6, 9);
        IdeaList<LocalDate> expected = IdeaList.of(l1, l2, local1, local2, l3, l4, l5, l6, l7);
        assertEquals(expected, localDateIdeaList.insertAt(-5, Arrays.asList(local1, local2)));
    }*/

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_elements_Throws_exception_if_index_just_bigger_than_upper_bound() {
        personIdeaList.insertAt(6, p1).toList();
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_elements_Throws_exception_if_index_bigger_than_upper_bound() {
        personIdeaList.insertAt(47, p1).toList();
    }

    @Test
    public void insertAt_elements_Returns_list_if_no_elements_are_given() {
        assertEquals(localDateIdeaList, localDateIdeaList.insertAt(3));
    }

    @Test
    public void insertAt_elements_Inserts_given_elements_containing_null_at_specified_position() {
        IdeaList<Person> expected = IdeaList.of(p1, p2, p3, p4, p5, null, p6);
        assertEquals(expected, personIdeaList.insertAt(5, (Person) null));
    }

    @Test
    public void insertAt_elements_Inserts_given_elements_at_specified_position() {
        LocalDate local1 = LocalDate.of(1002, 7, 1);
        LocalDate local2 = LocalDate.of(4014, 6, 9);
        IdeaList<LocalDate> expected = IdeaList.of(l1, l2, l3, local1, local2, l4, l5, l6, l7);
        assertEquals(expected, localDateIdeaList.insertAt(3, local1, local2));
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_IdeaList_Throws_exception_if_this_list_is_empty() {
        IdeaList.<Person>empty().insertAt(0, personIdeaList);
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_IdeaList_Throws_exception_if_index_just_negative() {
        personIdeaList.insertAt(-1, IdeaList.empty());
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_IdeaList_Throws_exception_if_index_negative() {
        personIdeaList.insertAt(-23, IdeaList.empty());
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void insertAt_IdeaList_Throws_exception_if_index_just_bigger_than_upper_bound() {
        personIdeaList.insertAt(6, IdeaList.empty()).toList();
    }

    @Test //(expected = IndexOutOfBoundsException.class)
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

    /*@Test //(expected = IllegalArgumentException.class)
    public void concatWith_Iterable_Throws_exception_if_given_elements_null() {
        personIdeaList.concatWith(null);
    }*/

    @Test
    public void concatWith_Iterable_Returns_given_list_if_this_list_is_empty() {
        assertEquals(personIdeaList, IdeaList.<Person>empty().concatWith(personSet));
    }

    @Test
    public void concatWith_Iterable_Returns_this_list_if_given_list_is_empty() {
        assertEquals(localDateIdeaList, localDateIdeaList.concatWith(new HashSet<>()));
    }

    @Test
    public void concatWith_Iterable_Concatenates_this_list_containing_nulls_with_given_Iterable() {
        Person np1 = new Person("Jan", "Janssens");
        Person np2 = new Person("Eveline", "Roberts");
        IdeaList<Person> expected = IdeaList.of(np1, np2, null, p2, p3, p4, p5, null);
        List<Person> input = new LinkedList<>(asList(null, p2, p3, p4, p5, null));
        assertEquals(expected, IdeaList.of(np1, np2).concatWith(input));
    }

    @Test
    public void concatWith_Iterable_Concatenates_this_list_with_given_Iterable() {
        Person np1 = new Person("Jan", "Janssens");
        Person np2 = new Person("Eveline", "Roberts");
        IdeaList<Person> expected = IdeaList.of(np1, np2, p1, p2, p3, p4, p5, p6);
        assertEquals(expected, IdeaList.of(np1, np2).concatWith(personSet));
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

    @Test
    public void add_Adds_the_given_elements_to_the_end_of_the_list_with_null() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, null);
        Dog d3 = new Dog(11, "Lala");
        Dog d4 = new Dog(428, "Momo");
        Dog d5 = new Dog(0, null);
        IdeaList<Dog> expected = IdeaList.of(d1, null, d2, null, d3, d4, d5, null, null);
        assertEquals(expected, IdeaList.of(d1, null, d2).add(null, d3, d4, d5, null, null));
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

    @Test //(expected = NullPointerException.class)
    public void linkToBackOf_Iterable_Throws_exception_if_given_elements_null() {
        personIdeaList.linkToBackOf(null);
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
    public void linkToBackOf_Iterable_Links_this_list_containing_nulls_to_the_back_of_the_given_Iterable_containing_nulls() {
        Dog n1 = new Dog(11, "Lala");
        Dog n2 = new Dog(428, "Momo");
        Dog n3 = new Dog(0, "Giri");
        Set<Dog> dogs = new LinkedHashSet<>(asList(n1, null, n2, n3));

        IdeaList<Dog> expected = IdeaList.of(n1, null, n2, n3, null, d5, d6, d2, null, d3, d1, null, d4);
        assertEquals(expected, IdeaList.of(null, d5, d6, d2, null, d3, d1, null, d4).linkToBackOf(dogs));
    }

    @Test
    public void linkToBackOf_Iterable_Links_this_list_to_the_back_of_the_given_Iterable() {
        Dog n1 = new Dog(11, "Lala");
        Dog n2 = new Dog(428, "Momo");
        Dog n3 = new Dog(0, "Giri");
        Set<Dog> dogs = new LinkedHashSet<>(asList(n1, n2, n3));

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

    @Test
    public void addToFront_Adds_given_elements_containing_null_to_front_of_the_list() {
        Dog d1 = new Dog(9, "Lassy");
        Dog d2 = new Dog(1, null);
        Dog d3 = new Dog(11, "Lala");
        Dog d4 = new Dog(428, "Momo");
        Dog d5 = new Dog(0, null);
        IdeaList<Dog> expected = IdeaList.of(d3, null, null, d4, d5, null, d1, null, d2);
        assertEquals(expected, IdeaList.of(null, d1, null, d2).addToFront(d3, null, null, d4, d5));
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
    public void removeFirst_Removes_first_instance_of_the_given_element_from_this_list_containing_nulls() {
        IdeaList<Person> expected = IdeaList.of(p5, p1, p2, p5, p5, p3, null, p4, p5, null);
        IdeaList<Person> inputList = IdeaList.of(p5, p1, null, p2, p5, p5, p3, null, p4, p5, null);
        assertEquals(expected, inputList.removeFirst(null));
    }

    @Test
    public void removeFirst_Removes_first_instance_of_the_given_element_from_this_list() {
        IdeaList<Person> expected = IdeaList.of(p1, p2, p5, p5, p3, p4, p5);
        IdeaList<Person> inputList = IdeaList.of(p5, p1, p2, p5, p5, p3, p4, p5);
        assertEquals(expected, inputList.removeFirst(p5));
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_list_is_empty() {
        IdeaList.<String>empty().removeAt(0);
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_index_just_negative() {
        dogIdeaList.removeAt(-1);
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_index_negative() {
        dogIdeaList.removeAt(-23);
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_index_just_bigger_than_upper_bound() {
        dogIdeaList.removeAt(6).toList();
    }

    @Test //(expected = IndexOutOfBoundsException.class)
    public void removeAt_Throws_exception_if_index_bigger_than_upper_bound() {
        dogIdeaList.removeAt(56).toList();
    }

    @Test
    public void removeAt_Removes_the_element_at_specified_position_from_this_list_containing_nulls() {
        IdeaList<Dog> input = IdeaList.of(null, null, d1, null, d2, d3, d4, d5, null, d6);
        assertEquals(IdeaList.of(null, d1, null, d2, d3, d4, d5, null, d6), input.removeAt(0));
        assertEquals(IdeaList.of(null, d1, null, d2, d3, d4, d5, null, d6), input.removeAt(1));
        assertEquals(IdeaList.of(null, null, d1, null, d3, d4, d5, null, d6), input.removeAt(4));
        assertEquals(IdeaList.of(null, null, d1, null, d2, d3, d4, d5, null), input.removeAt(9));
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
    public void removeAll_Removes_all_instances_of_the_given_element_from_this_list_containing_nulls() {
        IdeaList<Person> expected = IdeaList.of(p5, p1, p2, p5, p5, p3, p4, p5);
        IdeaList<Person> inputList = IdeaList.of(null, p5, p1, null, p2, p5, p5, p3, null, p4, p5, null);
        assertEquals(expected, inputList.removeAll(null));
    }

    @Test
    public void removeAll_Removes_all_instances_of_the_given_element_from_this_list() {
        IdeaList<Person> expected = IdeaList.of(p1, p2, p3, p4);
        IdeaList<Person> inputList = IdeaList.of(p5, p1, p2, p5, p5, p3, p4, p5);
        assertEquals(expected, inputList.removeAll(p5));
    }

    @Test
    public void forEachIndexed_Performs_no_actions_if_list_is_empty() {
        List<Integer> indices = new ArrayList<>();
        List<Dog> dogs = new ArrayList<>();
        IdeaList.<Dog>empty().forEachIndexed((idx, cur) -> {
            indices.add(idx);
            dogs.add(cur);
        });
        assertEmpty(indices);
        assertEmpty(dogs);
    }

    @Test
    public void forEachIndexed_Performs_given_action_on_each_element_of_this_list_containing_nulls_provided_with_its_index() {
        IdeaList<Dog> input = IdeaList.of(null, d5, d5, d6, null, null, null);
        List<Integer> indices = new ArrayList<>();
        List<Dog> dogs = new ArrayList<>();

        input.forEachIndexed((idx, cur) -> {
            indices.add(idx);
            dogs.add(cur);
        });
        assertEquals(asList(0, 1, 2, 3, 4, 5, 6), indices);
        assertEquals(input.toList(), dogs);
    }

    @Test
    public void forEachIndexed_Performs_given_action_on_each_element_of_this_list_provided_with_its_index() {
        List<Integer> indices = new ArrayList<>();
        List<Dog> dogs = new ArrayList<>();
        dogIdeaList.forEachIndexed((idx, cur) -> {
            indices.add(idx);
            dogs.add(cur);
        });
        assertEquals(dogIdeaList.toList(), dogs);
        assertEquals(asList(0, 1, 2, 3, 4, 5), indices);
    }

    @Test
    public void forEach_Performs_no_actions_if_list_is_empty() {
        List<Dog> dogs = new ArrayList<>();
        IdeaList.<Dog>empty().forEach(dogs::add);

        assertEmpty(dogs);
    }

    @Test
    public void forEach_Performs_given_action_on_each_element_of_this_list_containing_nulls() {
        IdeaList<Dog> input = IdeaList.of(d1, d2, null, d1, null, d1, d6, d1);
        List<Dog> dogs = new ArrayList<>();
        input.forEach(dogs::add);

        assertEquals(input.toList(), dogs);
    }

    @Test
    public void forEach_Performs_given_action_on_each_element() {
        List<Dog> dogs = new ArrayList<>();
        dogIdeaList.forEach(dogs::add);

        assertEquals(dogIdeaList.toList(), dogs);
    }


    // List operations ==============================================================================
    @Test
    public void reduce_initialValue_Returns_initialValue_if_operation_returns_accumulator() {
        assertNull(localDateIdeaList.reduce(null, (acc, integer) -> acc));
    }

    @Test
    public void reduce_initialValue_Returns_initialValue_if_this_list_does_not_contain_elements() {
        assertEquals(0, IdeaList.<Integer>empty().reduce(0, (acc, integer) -> 10).intValue());
    }

    @Test
    public void reduce_initialValue_Accumulates_this_list_containing_nulls_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator() {
        IdeaList<LocalDate> input = IdeaList.of(l1, l2, l3, l4, l5, null, null, l6, l7, null);
        IdeaList<LocalDate> expected = IdeaList.of(null, l7, l6, null, null, l5, l4, l3, l2, l1);

        assertEquals(expected, input.reduce(IdeaList.empty(), IdeaList::addToFront));
    }

    @Test
    public void reduce_initialValue_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_primitive_element_with_accumulator() {
        IdeaList<Integer> integers = IdeaList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(13, integers.reduce(0, Integer::sum).intValue());
    }

    @Test
    public void reduce_initialValue_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator() {
        IdeaList<LocalDate> expected = IdeaList.of(l7, l6, l5, l4, l3, l2, l1);
        assertEquals(expected, localDateIdeaList.reduce(IdeaList.empty(), IdeaList::addToFront));
    }

    @Test
    public void reduceIndexed_initialValue_Returns_initialValue_if_this_list_does_not_contain_elements() {
        assertEquals(0, IdeaList.<Integer>empty().reduceIndexed(0, (index, acc, integer) -> 10).intValue());
    }

    @Test
    public void reduceIndexed_initialValue_Accumulates_this_list_containing_nulls_into_a_single_value_by_applying_given_operation_to_each_element_provided_with_accumulator_and_index() {
        IdeaList<LocalDate> input = IdeaList.of(l1, l2, l3, l4, l5, null, null, l6, l7, null);
        IdeaList<Object> expected = IdeaList.of(null, 9, l7, 8, l6, 7, null, 6, null, 5, l5, 4, l4, 3, l3, 2, l2, 1, l1, 0);

        assertEquals(expected, input.reduceIndexed(IdeaList.empty(), (index, acc, date) -> acc.addToFront(index).addToFront(date)));
                             //input.withIndex().reduce(IdeaList.empty(), (acc, idxDate) -> acc.addToFront(idxDate.index).addToFront(idxDate.element));
    }

    @Test
    public void reduceIndexed_initialValue_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_primitive_element_provided_with_accumulator_and_index() {
        IdeaList<Integer> integers = IdeaList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(34, integers.reduceIndexed(0, (index, acc, integer) -> acc + index + integer).intValue());
    }

    @Test
    public void reduceIndexed_initialValue_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_provided_with_accumulator_and_index() {
        String expected = "0Zoe1Alex2Patricia3Jeffie4Daenerys5John";
        assertEquals(expected, personIdeaList.select(Person::getFirstName).reduceIndexed("", (index, acc, firstName) -> acc + index + firstName));
    }

    @Test //(expected = UnsupportedOperationException.class)
    public void reduce_Throws_exception_if_this_list_does_not_contain_elements() {
        IdeaList.<Integer>empty().reduce((acc, integer) -> 10);
    }

    @Test
    public void reduce_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_primitive_element_with_accumulator() {
        IdeaList<Integer> integers = IdeaList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(13, integers.reduce(Integer::sum).intValue());
    }

    @Test
    public void reduce_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_with_accumulator() {
        IdeaList<String> given = IdeaList.of("I", "would", "like", "to", "merge", "this", "list", "into", "one");
        String expected = "Iwouldliketomergethislistintoone";
        assertEquals(expected, given.reduce((acc, string) -> acc + string));
    }

    @Test //(expected = UnsupportedOperationException.class)
    public void reduceIndexed_Throws_exception_if_this_list_does_not_contain_elements() {
        IdeaList.<Integer>empty().reduceIndexed((index, acc, integer) -> 10);
    }

    @Test
    public void reduceIndexed_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_primitive_element_provided_with_accumulator_and_index() {
        IdeaList<Integer> integers = IdeaList.of(6, 7, 0, -1, 4, 5, -8);
        assertEquals(34, integers.reduceIndexed((index, acc, integer) -> acc + index + integer).intValue());
    }

    @Test
    public void reduceIndexed_Accumulates_this_list_into_a_single_value_by_applying_given_operation_to_each_element_provided_with_accumulator_and_index() {
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
        assertEquals(IdeaList.empty(), IdeaList.<Animal>empty().where(__ -> true));
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
        assertEquals(IdeaList.empty(), IdeaList.<Animal>empty().filter(__ -> true));
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
        assertEquals(IdeaList.empty(), IdeaList.<Animal>empty().findAll(__ -> true));
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

    @Test //(expected = UnsupportedOperationException.class)
    public void flatten_Throws_exception_if_list_is_empty() {
        IdeaList.empty().flatten();
    }

    @Test //(expected = UnsupportedOperationException.class)
    public void flatten_Throws_exception_if_list_is_already_flat() {
        personIdeaList.flatten();
    }

    @Test //(expected = UnsupportedOperationException.class)
    public void flatten_Throws_exception_if_list_contains_elements_that_are_not_Iterable() {
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("a", 1); map1.put("b", 2);
        Map<String, Integer> map2 = new HashMap<>();
        map2.put("c", 3); map2.put("d", 4);
        IdeaList.of(map1, map2).flatten();
    }

    @Test
    public void flatten_Concatenates_all_nested_Iterables_containing_null_into_one_list() {
        IdeaList<Set<String>> given = IdeaList.of(
                new LinkedHashSet<>(Collections.singletonList("one")),
                new LinkedHashSet<>(asList("two", null, "four")),
                new LinkedHashSet<>(asList("five", "six", "seven", null))
        );
        IdeaList<String> expected = IdeaList.of("one", "two", null, "four", "five", "six", "seven", null);
        assertEquals(expected, given.flatten());
    }

    @Test
    public void flatten_Concatenates_all_nested_Iterables_into_one_list() {
        IdeaList<Set<String>> given = IdeaList.of(
                new LinkedHashSet<>(Collections.singletonList("one")),
                new LinkedHashSet<>(asList("two", "three", "four")),
                new LinkedHashSet<>(asList("five", "six", "seven", "eight"))
        );
        IdeaList<String> expected = IdeaList.of("one", "two", "three", "four", "five", "six", "seven", "eight");
        assertEquals(expected, given.flatten());
    }

    @Test
    public void flatten_Concatenates_all_nested_Iterables_into_one_list_2() {
        IdeaList<Queue<Person>> given = IdeaList.of(
                new LinkedList<>(asList(p1, p2)),
                new LinkedList<>(Collections.singletonList(p3)),
                new LinkedList<>(asList(p4, p5, p6))
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


    @Test //(expected = NullPointerException.class)
    public void zipWith_Triplet_Throws_exception_if_other_is_null() {
        personIdeaList.zipWith(null, localDateIdeaList);
    }

    @Test //(expected = NullPointerException.class)
    public void zipWith_Triplet_Throws_exception_if_other2_is_null() {
        personIdeaList.zipWith(integerIdeaList, null);
    }

    @Test
    public void zipWith_Triplet_Gives_correct_tail_when_lazy_value_is_not_calculated() {
        IdeaList<Triplet<Person, Integer, LocalDate>> given = personIdeaList.zipWith(integerIdeaList, localDateIdeaList);
        assertEquals(Triplet.of(p2, i2, l2), given.tail.value().value.value());
        assertEquals(Triplet.of(p5, i5, l5), given.tail.value().tail.value().tail.value().tail.value().value.value());
    }

    @Test
    public void zipWith_Triplet_Returns_empty_list_if_this_list_is_empty() {
        LocalDate ld1 = LocalDate.of(1993, 6, 1);
        LocalDate ld2 = LocalDate.of(40, 2, 8);

        Queue<LocalDate> dates = new LinkedList<>(asList(ld1, ld2));
        Set<Integer> ints = new LinkedHashSet<>(asList(-22, 3, 7, 1025, 0));

        assertEquals(IdeaList.empty(), IdeaList.empty().zipWith(dates, ints));
    }

    @Test
    public void zipWith_Triplet_Returns_empty_list_if_first_given_list_is_empty() {
        IdeaList<Dog> dogs = IdeaList.of(d1, d2, d3, d4);
        Set<Integer> ints = new LinkedHashSet<>(asList(-22, 3, 7, 1025, 0));

        assertEquals(IdeaList.empty(), dogs.zipWith(IdeaList.empty(), ints));
    }

    @Test
    public void zipWith_Triplet_Returns_empty_list_if_second_given_list_is_empty() {
        LocalDate ld1 = LocalDate.of(1993, 6, 1);
        LocalDate ld2 = LocalDate.of(40, 2, 8);

        IdeaList<Dog> dogs = IdeaList.of(d1, d2, d3, d4);
        Queue<LocalDate> dates = new LinkedList<>(asList(ld1, ld2));

        assertEquals(IdeaList.empty(), dogs.zipWith(dates, IdeaList.empty()));
    }

    @Test
    public void zipWith_Triplet_Returns_list_of_Tuples_built_from_elements_of_this_list_containing_nulls_and_other_Iterables_with_same_index() {
        LocalDate ld = LocalDate.of(40, 2, 8);

        IdeaList<Dog> dogs = IdeaList.of(null, d2, null, d4);
        Queue<LocalDate> dates = new LinkedList<>(asList(null, ld));
        Set<Integer> ints = new LinkedHashSet<>(asList(-22, null, 7, 1025, null));

        IdeaList<Triplet<Dog, LocalDate, Integer>> expected = IdeaList.of(
                Triplet.of(null, null, -22),
                Triplet.of(d2, ld, null)
        );
        assertEquals(expected, dogs.zipWith(dates, ints));
    }

    @Test
    public void zipWith_Triplet_Returns_list_of_Tuples_built_from_elements_of_this_list_and_other_Iterables_with_same_index() {
        LocalDate ld1 = LocalDate.of(1993, 6, 1);
        LocalDate ld2 = LocalDate.of(40, 2, 8);

        IdeaList<Dog> dogs = IdeaList.of(d1, d2, d3, d4);
        Queue<LocalDate> dates = new LinkedList<>(asList(ld1, ld2));
        Set<Integer> ints = new LinkedHashSet<>(asList(-22, 3, 7, 1025, 0));

        IdeaList<Triplet<Dog, LocalDate, Integer>> expected = IdeaList.of(
                Triplet.of(d1, ld1, -22),
                Triplet.of(d2, ld2, 3)
        );
        assertEquals(expected, dogs.zipWith(dates, ints));
    }

    @Test //(expected = NullPointerException.class)
    public void zipWith_Pair_Throws_exception_if_other_is_null() {
        personIdeaList.zipWith(null);
    }

    @Test
    public void zipWith_Pair_Returns_empty_list_if_this_list_is_empty() {
        Queue<Person> people = new LinkedList<>(asList(p5, p6, p2));
        assertEquals(IdeaList.empty(), IdeaList.empty().zipWith(people));
    }

    @Test
    public void zipWith_Pair_Returns_empty_list_if_given_list_is_empty() {
        IdeaList<Dog> dogs = IdeaList.of(d1, d2, d3, d4);
        assertEquals(IdeaList.empty(), dogs.zipWith(IdeaList.empty()));
    }

    @Test
    public void zipWith_Returns_list_of_Pairs_built_from_elements_of_this_list_containing_nulls_and_other_Iterable_with_same_index() {
        IdeaList<Dog> dogs = IdeaList.of(null, null, d3, d4);
        Queue<Person> people = new LinkedList<>(asList(p5, p6, null));

        IdeaList<Pair<Dog, Person>> expected = IdeaList.of(
                Pair.of(null, p5),
                Pair.of(null, p6),
                Pair.of(d3, null)
        );
        assertEquals(expected, dogs.zipWith(people));
    }

    @Test
    public void zipWith_Returns_list_of_Pairs_built_from_elements_of_this_list_and_other_Iterable_with_same_index() {
        IdeaList<Dog> dogs = IdeaList.of(d1, d2, d3, d4);
        Queue<Person> people = new LinkedList<>(asList(p5, p6, p2));

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
    public void withIndex_Returns_a_list_of_IndexElement_pairs_where_each_nullable_element_is_bundled_together_with_its_index() {
        IdeaList<Person> input = IdeaList.of(null, p5, p6, p4, null, p1);
        IndexElement<Person> pair1 = IndexElement.of(0, null);
        IndexElement<Person> pair2 = IndexElement.of(1, p5);
        IndexElement<Person> pair3 = IndexElement.of(2, p6);
        IndexElement<Person> pair4 = IndexElement.of(3, p4);
        IndexElement<Person> pair5 = IndexElement.of(4, null);
        IndexElement<Person> pair6 = IndexElement.of(5, p1);
        IdeaList<IndexElement<Person>> expected = IdeaList.of(pair1, pair2, pair3, pair4, pair5, pair6);
        assertEquals(expected, input.withIndex());
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

    @Test //(expected = IllegalArgumentException.class)
    public void step_Throws_exception_if_given_length_negative() {
        IdeaList.empty().step(-17);
    }

    @Test //(expected = IllegalArgumentException.class)
    public void step_Throws_exception_if_given_length_just_negative() {
        IdeaList.empty().step(-1);
    }

    @Test //(expected = IllegalArgumentException.class)
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
