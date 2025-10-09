package deque;

public interface Deque<T> {
    default boolean isEmpty() {
        return this.size() == 0;
    }

    int size();

    void addFirst(T item);

    void addLast(T item);

    T removeFirst();

    T removeLast();

    void printDeque();

    T get(int index);
}
