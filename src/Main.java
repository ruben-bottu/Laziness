import java.time.LocalDate;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Lazy<Integer> x = Lazy.of( () -> 1 + 2 );
        int y = x.value() + 5;

        /*LazyList<String> list = LazyList.of(Lazy.of(() -> "bla"), null);
        LazyList<Integer> list2 = LazyList.of( Lazy.of(() -> 17) );*/
        /*IdeaList<String> list3 = IdeaList.of("bla", "foo", "raw");
        IdeaList<Integer> list4 = IdeaList.of(11, -5, 6, 0);*/
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

        Set<LocalDate> dates = new HashSet<>();
        dates.add(LocalDate.MAX);
        dates.add(LocalDate.now());
        dates.add(LocalDate.of(793, 1, 30));

        LazyList<LocalDate> dList = LazyList.of(LocalDate.of(512, 7, 21), LocalDate.of(-20, 12, 1));

        LazyList<String> sList = LazyList.of(cars);
        LazyList<String> sList1 = LazyList.of("foo", "bar", "ye");

        LazyList<Integer> iList = LazyList.of(2, -7, 11, -95, 15, 0, 1);
        LazyList<Integer> iList1 = LazyList.of(9, 8, 7);
        LazyList<Integer> iList2 = LazyList.of(1, 2, 3);

        LazyList<Animal> aList = LazyList.of(new Dog(12, "jef"), new Dog(5, "johanna"), new Dog(17, "emilia"));
        LazyList<Animal> aList1 = LazyList.of(new Dog(-5, "yonas"), new Dog(0, "thomas"));

        LazyList<Animal> aConcat = aList.concat(aList1);
        //System.out.println(aList.concat(aList1).map(Animal::getAge).toList());
        //System.out.println(sList1.concat(cars).toList());
        ArrayList<Integer> ints = new ArrayList<>(Arrays.asList(0, 5, 6, -9, 223, 4));
        //System.out.println(iList.concatToBack(ints).toList());
        LazyList<LocalDate> dList1 = LazyList.of(dates);
        //System.out.println(dList1.toList());
        //Iterator<Integer> it = iList.iterator();
        /*for (String i : sList) {
            System.out.println(i);
        }*/
        System.out.println(sList1.concat(cars).toList());
    }
}
