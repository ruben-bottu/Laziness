package idealist;

import java.util.Comparator;

class MergeSort {
    private static final int NODE_REFERENCE_ARRAY_SIZE = 32;

    /*static <E> IdeaList2<E> sort(Comparator<E> comparator, IdeaList2<E> elements) {
    }*/

    static <E> IdeaList2<E> sort(Comparator<E> comparator, IdeaList2<E> elements) {
        if (elements.isEmpty()) return elements;

        @SuppressWarnings("unchecked")
        MutableList<E>[] temp = (MutableList<E>[]) new MutableList[NODE_REFERENCE_ARRAY_SIZE];
        //Comparator<Lazy<E>> lazyComparator = Comparator.comparing(Lazy::value, comparator);
        int requiredSizeForMerge = 2;
        int currentSize = 2;

        Lazy<E> first = elements.value;
        elements = elements.tail.value();
        Lazy<E> second = elements.value;
        elements = elements.tail.value();
        // 1)
        temp[0] = merge(comparator, first, second);

        Lazy<E> third = elements.value;
        elements = elements.tail.value();
        Lazy<E> fourth = elements.value;
        elements = elements.tail.value();
        // 2)
        temp[1] = merge(comparator, third, fourth);

        // 3)
        temp[0] = merge(comparator, temp[0], temp[1]);

        Lazy<E> fifth = elements.value;
        elements = elements.tail.value();
        Lazy<E> sixth = elements.value;
        elements = elements.tail.value();
        // 4)
        temp[1] = merge(comparator, fifth, sixth);

        Lazy<E> seventh = elements.value;
        elements = elements.tail.value();
        Lazy<E> eighth = elements.value;
        elements = elements.tail.value();
        // 5)
        temp[2] = merge(comparator, seventh, eighth);

        // 6)
        temp[1] = merge(comparator, temp[1], temp[2]);

        // 7)
        temp[0] = merge(comparator, temp[0], temp[1]);

        return IdeaList2.ofLazy(temp[0]);
    }

    private static <E> int compare(Comparator<E> comparator, Lazy<E> left, Lazy<E> right) {
        return comparator.compare(left.value(), right.value());
    }

    private static <E> MutableList<E> merge(Comparator<E> comparator, Lazy<E> left, Lazy<E> right) {
        return compare(comparator, left, right) <= 0
                ? MutableList.of(left, right)
                : MutableList.of(right, left);
    }

    private static <E> MutableList<E> merge(Comparator<E> comparator, MutableList<E> left, MutableList<E> right) {
        MutableList<E> merged = MutableList.empty();
        MutableList<E> temp = merged;

        while (left.any() && right.any()) {
            if (compare(comparator, left.value, right.value) <= 0) {
                temp.tail = left;
                left = left.tail;
            } else {
                temp.tail = right;
                right = right.tail;
            }
            temp = temp.tail;
        }

        // As a replacement for the two following while loops
        if (left.any()) {
            temp.tail = left;
        }

        if (right.any()) {
            temp.tail = right;
        }

        /*while (left.any()) {
            temp.tail = left;
            left = left.tail;
            temp = temp.tail;
        }

        while (right.any()) {
            temp.tail = right;
            right = right.tail;
            temp = temp.tail;
        }*/

        return merged.tail;
    }
}
