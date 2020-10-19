package domain;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class LazyListTest {
    private Person p1, p2, p3, p4, p5, p6;
    private Set<Person> personSet;
    private Dog d1, d2, d3, d4, d5, d6;
    private List<Person> personList;
    private List<Animal> animalList;
    private LazyList<Person> personLazyList;
    private LazyList<Animal> animalLazyList;

    @Before
    public void setUp() throws Exception {
        p1 = new Person("Zoe", "Turner");
        p2 = new Person("Alex", "Johnson");
        p3 = new Person("Patricia", "Vanilla");
        p4 = new Person("Jeffie", "Allstar");
        p5 = new Person("Daenerys", "Targaryen");
        p6 = new Person("John", "Jefferson");

        personSet = new LinkedHashSet<>();
        personSet.add(p1);
        personSet.add(p2);
        personSet.add(p3);
        personSet.add(p4);
        personSet.add(p5);
        personSet.add(p6);

        d1 = new Dog(5, "Fifi");
        d2 = new Dog(1, "Lala");
        d3 = new Dog(6, "Momo");
        d4 = new Dog(4, "Shiba");
        d5 = new Dog(13, "Floof");
        d6 = new Dog(9, "Paula");

        personList = new ArrayList<>(Arrays.asList(p1, p2, p3, p4, p5, p6));
        animalList = new ArrayList<>(Arrays.asList(d1, d2, d3, d4, d5, d6));

        personLazyList = LazyList.of(personSet);
        animalLazyList = LazyList.of(animalList);
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
        LazyList<Animal> animals = LazyList.of(d1, d2, d3, d4, d5);
        assertEquals(d1, animals.value.value());
        assertEquals(d2, animals.tail.value().value.value());
        assertEquals(d3, animals.tail.value().tail.value().value.value());
        assertEquals(d4, animals.tail.value().tail.value().tail.value().value.value());
        assertEquals(d5, animals.tail.value().tail.value().tail.value().tail.value().value.value());
    }

    @Test
    public void toList_Transforms_LazyList_into_List() {
        assertEquals(personList, LazyList.of(personList).toList());
        assertEquals(animalList, LazyList.of(animalList).toList());
    }

    @Test
    public void get_Returns_element_at_given_index() {
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

    @Test
    public void single() {
    }

    @Test
    public void concat() {
    }

    @Test
    public void any() {
    }

    @Test
    public void none() {
    }

    @Test
    public void map() {
    }

    @Test
    public void filter() {
    }

    @Test
    public void iterator() {
    }
}