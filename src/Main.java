import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        Lazy<Integer> x = Lazy.of( () -> 1 + 2 );
        int y = x.value() + 5;

        /*Node<String> list = Node.of(Lazy.of(() -> "bla"), null);
        Node<Integer> list2 = Node.of( Lazy.of(() -> 17) );*/
        IdeaList<String> list3 = IdeaList.of("bla", "foo", "raw");
        IdeaList<Integer> list4 = IdeaList.of(11, -5, 6, 0);
        //System.out.println(list3.map(str -> str.toUpperCase()));
        /*Node<Integer> node3 = Node.of(Lazy.of(() -> 8));
        Node<Integer> node2 = Node.of(Lazy.of(() -> 11), Lazy.of(() -> node3));
        Node<Integer> node1 = Node.of(Lazy.of(() -> -6), Lazy.of(() -> node2));*/
        // [-6, 11, 8]
        //Node<Integer> list5 = node1.map(num -> num + 10);
        /*Node<Integer> list5 = node1.filter(num -> num > 8);
        System.out.println(list5.toList());*/
        Node<Integer> node3 = NormalNode.of(Lazy.of(() -> 1), Lazy.of(EndNode::empty));
        Node<Integer> node2 = NormalNode.of(Lazy.of(() -> -9), Lazy.of(() -> node3));
        Node<Integer> list = NormalNode.of(Lazy.of(() -> 50), Lazy.of(() -> node2));
        System.out.println(list.filter(e -> e < 5).toList());
    }
}
