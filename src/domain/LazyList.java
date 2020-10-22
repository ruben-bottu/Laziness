package domain;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class LazyList<E> implements Iterable<E> {
    public final Lazy<E> value;
    public final Lazy<LazyList<E>> tail;

    private LazyList(Lazy<E> value, Lazy<LazyList<E>> tail) {
        this.value = value;
        this.tail = tail;
    }

    // FOUT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! E element = iterator.next()
    /*private static <E> LazyList<E> concat(Iterator<E> iterator, LazyList<E> elements) {
        if (iterator.hasNext()) {
            return NormalNode.of(
                    Lazy.of(iterator::next),
                    Lazy.of(() -> concat(iterator, elements))
            );
        }
        return elements;
    }*/

    /*private static <E> LazyList<E> ofHelper(Iterator<E> iterator) {
        if (iterator.hasNext()) {
            return NormalNode.of(Lazy.of(iterator::next), Lazy.of(() -> ofHelper(iterator)));
        }
        return EndNode.empty();
    }*/

    private static <E> LazyList<E> ofHelper(Iterator<E> iterator) {
        if (iterator.hasNext()) {
            E element = iterator.next();
            return NormalNode.of(Lazy.of(() -> element), Lazy.of(() -> ofHelper(iterator)));
        }
        return EndNode.empty();
    }

    public static <E> LazyList<E> of(Iterable<E> elements) {
        return ofHelper(elements.iterator());
        //return concat(elements.iterator(), EndNode.empty());
    }

    @SafeVarargs
    public static <E> LazyList<E> of(E... elements) {
        return LazyList.of(Arrays.asList(elements));
    }

    public abstract E get(int index);

    public E first() {
        return value.value();
    }

    public E single() {
        if (isEmpty() || tail.value().any()) throw new IllegalStateException("List must contain exactly one element");
        return first();
    }

    public E last() {
        return isTailEmpty() ? value.value() : tail.value().last();
    }

    /*private int sizeHelper(int counter) {
        if (tail.value().isEmpty()) return counter;
        return tail.value().sizeHelper(counter + 1);
    }*/

    /*public int size() {
        if (tail.value().isEmpty()) return 1;
        return 1 + tail.value().size();
    }*/

    /*public int size() {
        return isTailEmpty() ? 1 : tail.value().size() + 1;
    }*/

    public abstract int size();

    public E random() {
        return get(new Random().nextInt(size()));
    }

    /*public E find(Predicate<E> predicate) {
        if (tail.value().isEmpty()) return null;
        if (predicate.test(value.value())) return value.value();
        return tail.value().find(predicate);
    }*/

    /*public E find(Predicate<E> predicate) {
        return isTailEmpty() ? null :
                (predicate.test(value.value()) ? value.value() : tail.value().find(predicate));
    }*/

    /// Werkt
    /*public E find(Predicate<E> predicate) {
        return predicate.test(value.value()) ? value.value() :
                (isTailEmpty() ? null : tail.value().find(predicate));
    }*/

    public abstract E find(Predicate<E> predicate);

    /*public int indexOf(E element) {
        if (tail.value().isEmpty()) return -1;
        return value.value().equals(element) ? 0 : tail.value().indexOf(element) + 1;
    }*/

    /*public int indexOf(E element) {
        if (!value.value().equals(element) && tail.value().isEmpty()) return -1;
        if (tail.value().indexOf(element) == -1) return -1;
        return value.value().equals(element) ? 0 : tail.value().indexOf(element) + 1;
    }*/

    /*private int indexOfHelper(int counter, E element) {
        if (!value.value().equals(element) && tail.value().isEmpty()) return -1;
        return value.value().equals(element) ? counter : tail.value().indexOfHelper(counter + 1, element);
    }*/

    /*private int indexOfHelper(int counter, E element) {
        return value.value().equals(element) ? counter :
                (isTailEmpty() ? -1 : tail.value().indexOfHelper(counter + 1, element));
    }*/

    protected abstract int indexOfHelper(int counter, E element);

    public int indexOf(E element) {
        return indexOfHelper(0, element);
    }

    /*public abstract int indexOf(E element);

    protected abstract int indexOfHelper(int i, E element);*/

    /*public E find(Predicate<E> predicate) {
        if (tail.value().isEmpty()) return null;
        return predicate.test(value.value()) ? value.value() : tail.value().find(predicate);
    }*/

    /*public E get(int index) {
        return (index == 0 ? value.value() : tail.value().get(index - 1));
    }*/

    /*public LazyList<E> concat(LazyList<E> elements) {
        return none() ? elements :
                NormalNode.of(value, Lazy.of(() -> tail.value().concat(elements)));
    }*/

    /*public LazyList<E> concat(Iterable<E> elements) {
        return none() ? LazyList.of(elements) :
                NormalNode.of(value, Lazy.of(() -> tail.value().concat(elements)));
    }*/

    public LazyList<E> concat(Iterable<E> elements) {
        LazyList<E> partTwo = (elements instanceof LazyList ? (LazyList<E>) elements : LazyList.of(elements));
        return isEmpty() ? partTwo :
                NormalNode.of(value, Lazy.of(() -> tail.value().concat(elements)));
    }

    public abstract boolean any();

    public boolean none() {
        return !any();
    }

    public boolean isEmpty() {
        return none();
    }

    private boolean isTailEmpty() {
        return tail.value().isEmpty();
    }

    public abstract <R> LazyList<R> map(Function<E, R> transform);

    public abstract LazyList<E> filter(Predicate<E> predicate);

    /*private List<E> toListHelper(List<E> result) {
        if (isEmpty()) return result;
        result.add(value.value());
        return tail.value().toListHelper(result);
    }*/

    protected abstract List<E> toListHelper(List<E> result);

    public List<E> toList() {
        return toListHelper(new ArrayList<>());
    }

    @Override
    public Iterator<E> iterator() {
        return LazyListIterator.of(this);
    }



    private static class NormalNode<E> extends LazyList<E> {

        private NormalNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        public static <E> LazyList<E> of(Lazy<E> value, Lazy<LazyList<E>> tail) {
            return new NormalNode<>(value, tail);
        }

        public E get(int index) {
            if (index < 0) throw new IndexOutOfBoundsException();
            return index == 0 ? value.value() : tail.value().get(index - 1);
        }

        /*protected int indexOfHelper(int counter, E element) {
            return value.value().equals(element) ? counter : tail.value().indexOfHelper(counter + 1, element);
        }

        public int indexOf(E element) {
            return indexOfHelper(0, element);
        }*/
        public int size() {
            return 1 + tail.value().size();
        }

        public E find(Predicate<E> predicate) {
            return predicate.test(value.value()) ? value.value() : tail.value().find(predicate);
        }

        protected int indexOfHelper(int counter, E element) {
            return value.value().equals(element) ? counter : tail.value().indexOfHelper(counter + 1, element);
        }

        @Override
        public boolean any() {
            return true;
        }

        /*public int indexOf(E element) {
            if (tail.value().indexOf(element) == -1) return -1;
            return value.value().equals(element) ? 0 : tail.value().indexOf(element) + 1;
        }*/

        @Override
        public <R> LazyList<R> map(Function<E, R> transform) {
            return NormalNode.of(
                    Lazy.of(() -> transform.apply(value.value())),
                    Lazy.of(() -> tail.value().map(transform))
            );
        }

        @Override
        public LazyList<E> filter(Predicate<E> predicate) {
            return predicate.test(value.value()) ?
                    NormalNode.of(value, Lazy.of(() -> tail.value().filter(predicate))) :
                    tail.value().filter(predicate);
        }

        @Override
        protected List<E> toListHelper(List<E> result) {
            result.add(value.value());
            return tail.value().toListHelper(result);
        }
    }



    private static class EndNode<E> extends LazyList<E> {

        private EndNode(Lazy<E> value, Lazy<LazyList<E>> tail) {
            super(value, tail);
        }

        public static <E> LazyList<E> empty() {
            return new EndNode<>(null, null);
        }

        public E get(int index) {
            throw new IndexOutOfBoundsException();
        }

        public int size() {
            return 0;
        }

        public E find(Predicate<E> predicate) {
            return null;
        }

        protected int indexOfHelper(int counter, E element) {
            return -1;
        }

        @Override
        public boolean any() {
            return false;
        }

        /*protected int indexOfHelper(int counter, E element) {
            return -1;
        }

        public int indexOf(E element) {
            throw new UnsupportedOperationException();
        }*/

        @Override
        public <R> LazyList<R> map(Function<E, R> transform) {
            return EndNode.empty();
        }

        @Override
        public LazyList<E> filter(Predicate<E> predicate) {
            return this;
        }

        @Override
        protected List<E> toListHelper(List<E> result) {
            return result;
        }
    }
}
