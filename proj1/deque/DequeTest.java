package deque;

import org.junit.Test;

import static org.junit.Assert.*;

public class DequeTest {
    @Test
    public void testArrayAddFirst() {
        Deque<Integer> deque = new ArrayDeque<>();
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        deque.addFirst(4);
        assertEquals(4, deque.size());
    }

    @Test
    public void testLinkedListAddFirst() {
        Deque<Integer> deque = new LinkedListDeque<>();
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        deque.addFirst(4);
        assertEquals(4, deque.size());
    }

    @Test
    public void testArrayBigAmount() {
        Deque<Integer> deque = new LinkedListDeque<>();
        for (int i = 0; i < 1000000; i++) {
            deque.addFirst(i);
            assertEquals(i + 1, deque.size());
        }
    }

    @Test
    public void testLinkedListBigAmount() {
        Deque<Integer> deque = new LinkedListDeque<>();
        for (int i = 0; i < 1000000; i++) {
            deque.addFirst(i);
            assertEquals(i + 1, deque.size());
        }
    }
}
