package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private T data;
        private Node next;
        private Node prev;

        Node(T d) {
            data = d;
            next = null;
            prev = null;
        }
    }

    private int size;
    private final Node dummyHead;
    private final Node dummyTail;

    public LinkedListDeque() {
        dummyHead = new Node(null);
        dummyTail = new Node(null);
        dummyHead.next = dummyTail;
        dummyTail.prev = dummyHead;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(T item) {
        Node newNode = new Node(item);
        Node temp = dummyHead.next;
        dummyHead.next = newNode;
        newNode.prev = dummyHead;
        newNode.next = temp;
        temp.prev = newNode;
        this.size++;
    }

    @Override
    public void addLast(T item) {
        Node newNode = new Node(item);
        Node temp = dummyTail.prev;
        dummyTail.prev = newNode;
        newNode.next = dummyTail;
        newNode.prev = temp;
        temp.next = newNode;
        this.size++;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node temp = dummyHead.next;
        T data = temp.data;
        dummyHead.next = temp.next;
        temp.next.prev = dummyHead;
        this.size--;
        return data;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node temp = dummyTail.prev;
        T data = temp.data;
        dummyTail.prev = temp.prev;
        temp.prev.next = dummyTail;
        this.size--;
        return data;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node temp = dummyHead.next;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        return temp.data;
    }

    private T getRecursiveHelper(Node cur, int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        if (index == 0) {
            return cur.data;
        }
        return getRecursiveHelper(cur.next, index - 1);
    }

    public T getRecursive(int index) {
        return getRecursiveHelper(dummyHead.next, index);
    }

    @Override
    public void printDeque() {
        Node temp = dummyHead.next;
        while (temp != null) {
            System.out.print(temp.data + " ");
            temp = temp.next;
        }
        System.out.println();
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {
        private Node cur;

        public LinkedListIterator() {
            cur = dummyHead.next;
        }

        @Override
        public boolean hasNext() {
            return cur.next != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T result = cur.data;
            cur = cur.next;
            return result;
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

        Deque<?> other = (Deque<?>) o;

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
