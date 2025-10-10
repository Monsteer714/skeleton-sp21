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

    @Test
    public void equalsTest() {
        LinkedListDeque<Integer> test1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> test2 = new LinkedListDeque<>();
        test1.addFirst(1);
        test1.addFirst(2);
        test1.addFirst(3);
        test2.addFirst(1);
        test2.addFirst(2);
        test2.addFirst(3);
        assertTrue(test1.equals(test2));

        test2.addFirst(4);
        assertFalse(test1.equals(test2));
    }

    @Test
    public void bigAmountEqualsTest() {
        LinkedListDeque<Integer> test1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> test2 = new LinkedListDeque<>();
        for (int i = 0; i < 5000; i++) {
            test1.addFirst(i);
            test2.addFirst(i);
        }
        assertTrue(test1.equals(test2));
        test1.addFirst(1);
        assertFalse(test1.equals(test2));
    }
}
