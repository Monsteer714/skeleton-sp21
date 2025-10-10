package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> compare;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.compare = c;
    }

    public T max() {
        return max(compare);
    }

    public T max(Comparator<T> c) {
        if (this.isEmpty()) {
            return null;
        }

        T maxItem = null;
        Iterator<T> it = this.iterator();

        if (it.hasNext()) {
            maxItem = it.next();
            while (it.hasNext()) {
                T item = it.next();
                if (c.compare(item, maxItem) > 0) {
                    maxItem = item;
                }
            }
        }

        return maxItem;
    }
}
