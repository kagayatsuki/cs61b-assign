package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
public static void testAlist(){
    AListNoResizing<Integer>list1= new AListNoResizing<>();
    BuggyAList<Integer> list2= new BuggyAList<>();
    list1.addLast(3);
    list1.addLast(5);
    list1.addLast(7);
    list2.addLast(3);
    list2.addLast(5);
    list2.addLast(7);
    list2.addLast(9);
    int a=list1.size();
    int b=list2.size();

    AListNoResizing<Integer> L = new AListNoResizing<>();

    int N = 500;
    for (int i = 0; i < N; i += 1) {
        int operationNumber = StdRandom.uniform(0, 2);
        if (operationNumber == 0) {
            // addLast
            int randVal = StdRandom.uniform(0, 100);
            L.addLast(randVal);
            System.out.println("addLast(" + randVal + ")");
        } else if (operationNumber == 1) {
            // size
            int size = L.size();
            System.out.println("size: " + size);
        }
    }
}
public static void main(String[] args) {
    testAlist();
}
}
