package deque;

import gh2.GuitarHeroLite;
import gh2.GuitarString;
import org.junit.Test;
import java.util.Comparator;

import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    /** Adds a few things to the deque, checking isEmpty() and size() are correct,
     * finally printing the results. */
    public void addIsEmptySizeTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<>();

        assertTrue("A newly initialized ArrayDeque should be empty", ad1.isEmpty());
        ad1.addFirst("front");

        assertEquals(1, ad1.size());
        assertFalse("ad1 should now contain 1 item", ad1.isEmpty());

        ad1.addLast("middle");
        assertEquals(2, ad1.size());
        ad1.addLast("back");
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
        ad1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that deque is empty afterwards. */
    public void addRemoveTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());

        ad1.removeFirst();
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());
    }

    @Test
    /** Tests removing from an empty deque */
    public void removeEmptyTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /** Check if you can create ArrayDeques with different parameterized types */
    public void multipleParamTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<>();
        ArrayDeque<Double> ad2 = new ArrayDeque<>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();

        assertEquals("string", s);
        assertEquals(3.14159, d, 0.00001);
        assertTrue(b);
    }

    @Test
    /** Check if null is returned when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        assertEquals("Should return null when removeFirst is called on an empty Deque", null, ad1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque", null, ad1.removeLast());
    }

    @Test
    /** Add large number of elements to deque; check if order is correct. */


    public void getTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<>();
        ad1.addLast("a");
        ad1.addLast("b");
        ad1.addLast("c");

        assertEquals("a", ad1.get(0));
        assertEquals("b", ad1.get(1));
        assertEquals("c", ad1.get(2));
        assertNull("Should return null for invalid index", ad1.get(3));
        assertNull("Should return null for negative index", ad1.get(-1));
    }

    @Test
    /** Test resize behavior */
    public void resizeTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        // Add elements to trigger resize (initial capacity is 8)
        for (int i = 0; i < 8; i++) {
            ad1.addLast(i);
        }
        // Adding one more should trigger resize to 16
        ad1.addLast(8);
        assertEquals(9, ad1.size());
        assertEquals(0, (int) ad1.get(0));
        assertEquals(8, (int) ad1.get(8));

        // Remove elements to trigger downsize (size < length/4)
        for (int i = 0; i < 7; i++) {
            ad1.removeFirst();
        }
        assertEquals(2, ad1.size());
        assertEquals(7, (int) ad1.get(0));
        assertEquals(8, (int) ad1.get(1));
    }

    @Test
    /** Test MaxArrayDeque max() methods */
    public void maxArrayDequeTest() {
        // Comparator for Integers
        Comparator<Integer> intComparator = Integer::compare;
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<>(intComparator);

        assertNull("Should return null for empty deque", mad1.max());

        mad1.addLast(5);
        mad1.addLast(2);
        mad1.addLast(8);
        mad1.addLast(1);

        assertEquals("Should return maximum value", Integer.valueOf(8), mad1.max());

        // Test with custom comparator
        Comparator<Integer> reverseComparator = (a, b) -> b - a; // Reverse order
        assertEquals("Should return minimum value with reverse comparator", Integer.valueOf(1), mad1.max(reverseComparator));
    }


}