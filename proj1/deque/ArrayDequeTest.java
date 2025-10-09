package deque;

import org.junit.Test;
import static org.junit.Assert.*;


public class ArrayDequeTest {
    @Test
    public void isEmptyAddSizeTest() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        assertTrue(deque.isEmpty());
        deque.addFirst(1);

        assertFalse(deque.isEmpty());

        assertEquals(1,deque.size());

        deque.addFirst(2);
        assertEquals(2,deque.size());

        deque.addFirst(3);
        assertEquals(3,deque.size());

        deque.addLast(4);
        assertEquals(4,deque.size());

        deque.addLast(5);
        assertEquals(5,deque.size());
    }

    @Test
    public void bigAmountAddSizeTest() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for(int i = 0; i < 10000; i++){
            deque.addFirst(i);
            assertEquals(i * 2 + 1, deque.size());
            deque.addLast(i);
            assertEquals(i * 2 + 2, deque.size());
        }
    }
    @Test
    public void bigAmountRemoveSizeTest() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for(int i = 0; i < 10000; i++){
            deque.addFirst(i);
            assertEquals(i * 2 + 1, deque.size());
            deque.addLast(i);
            assertEquals(i * 2 + 2, deque.size());
        }
        for(int i = 0; i< 5000; i++){
            assertEquals(20000 - i, deque.size());
            deque.removeFirst();
        }
    }
}
