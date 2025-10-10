package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
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

    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<T> {
        private int index;

        public ArrayIterator() {
            this.index = head + 1;
        }

        @Override
        public boolean hasNext() {
            return index < tail;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in deque");
            }
            T item = array[index];
            index++;
            return item;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof Deque)) {
            return false;
        }

        ArrayDeque<?> other = (ArrayDeque<?>) o;

        if (other.size() != this.size()) {
            return false;
        }

        Iterator<T> thisIterator = this.iterator();
        Iterator<?> otherIterator = other.iterator();

        while (thisIterator.hasNext() && otherIterator.hasNext()) {
            T thisItem = thisIterator.next();
            Object otherItem = otherIterator.next();

            if (thisItem == null) {
                if (otherItem != null) {
                    return false;
                }
            } else {
                if (!thisItem.equals(otherItem)) {
                    return false;
                }
            }
        }
        return true;
    }
}
