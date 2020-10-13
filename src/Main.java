import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Lazy<Integer> x = Lazy.of( () -> 1 + 2 );
        int y = x.value() + 5;

        /*LazyList<String> list = LazyList.of(Lazy.of(() -> "bla"), null);
        LazyList<Integer> list2 = LazyList.of( Lazy.of(() -> 17) );*/
        IdeaList<String> list3 = IdeaList.of("bla", "foo", "raw");
        IdeaList<Integer> list4 = IdeaList.of(11, -5, 6, 0);
        //System.out.println(list3.map(str -> str.toUpperCase()));
        /*LazyList<Integer> lazyList3 = LazyList.of(Lazy.of(() -> 8));
        LazyList<Integer> lazyList2 = LazyList.of(Lazy.of(() -> 11), Lazy.of(() -> lazyList3));
        LazyList<Integer> node1 = LazyList.of(Lazy.of(() -> -6), Lazy.of(() -> lazyList2));*/
        // [-6, 11, 8]
        //LazyList<Integer> list5 = node1.map(num -> num + 10);
        /*LazyList<Integer> list5 = node1.filter(num -> num > 8);
        System.out.println(list5.toList());*/
        /*LazyList<Integer> lazyList3 = LazyList.normalOf(Lazy.of(() -> 1), Lazy.of(LazyList::endOf));
        LazyList<Integer> lazyList2 = LazyList.normalOf(Lazy.of(() -> -9), Lazy.of(() -> lazyList3));
        LazyList<Integer> list = LazyList.normalOf(Lazy.of(() -> 50), Lazy.of(() -> lazyList2));*/
        //System.out.println(list.filter(e -> e < 5).map(e -> e + 10).toList());
        ArrayList<String> cars = new ArrayList<>();
        cars.add("Volvo");
        cars.add("BMW");
        cars.add("Ford");
        cars.add("Mazda");

        LazyList<String> lazyList = LazyList.of(cars);
        LazyList<String> lazyList1 = LazyList.of("foo", "bar", "ye");
        LazyList<Integer> lazyList4 = LazyList.of(50, -9, 1);
        //System.out.println(lazyList1.toList());
        LazyList<Animal> animalLazyList = LazyList.of(new Dog(12, "jef"), new Dog(5, "johanna"));
        System.out.println(animalLazyList.map(Animal::getAge).toList());
        System.out.println(lazyList4.value.value() + " ;;; " + lazyList4.tail.value().value.value());
    }
}
