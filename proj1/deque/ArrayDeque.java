package deque;

public class ArrayDeque<T> implements Deque<T> {
    private int size;
    private int capacity = 8;
    private static final int MIN_CAPACITY = 8;
    private int head;
    private int tail;
    private int mid;
    private T[] array;

    public ArrayDeque() {
        array = (T[]) new Object[capacity];
        size = 0;
        mid = capacity / 2;
        head = mid - 1;
        tail = mid;
    }
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    @Override
    public int size() {
        return size;
    }

    private void resize() {
        int newCapacity;
        if (head == -1 || tail == capacity) {
            newCapacity = capacity * 2;
            if (newCapacity < MIN_CAPACITY) {
                newCapacity = MIN_CAPACITY;
            }
        } else if (size < capacity / 5) {
            newCapacity = capacity / 2;
            if (newCapacity < MIN_CAPACITY) {
                newCapacity = MIN_CAPACITY;
            }
        } else {
            return;
        }
        T[] newArray = (T[]) new Object[newCapacity];
        int newMid = newCapacity / 2;

        int newHead = newMid - (size / 2) - 1;
        int newTail = newHead + size + 1;
        for (int i = 0; i < size; i++) {
            newArray[newHead + i + 1] = array[head + i + 1];
        }

        array = newArray;
        head = newHead;
        tail = newTail;
        mid = newMid;
        capacity = newCapacity;
    }
    @Override
    public void addFirst(T item) {
        resize();
        array[head] = item;
        head -= 1;
        size++;
    }
    @Override
    public void addLast(T item) {
        resize();
        array[tail] = item;
        tail += 1;
        size++;
    }
    @Override
    public void printDeque() {
        for (int i = head + 1; i <= tail - 1; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T item = array[head + 1];
        head += 1;
        size--;
        resize();
        return item;
    }
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T item = array[tail - 1];
        tail -= 1;
        size--;
        resize();
        return item;
    }
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return array[head + index + 1];
    }

}