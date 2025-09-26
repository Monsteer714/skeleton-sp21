package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
* Created by hug.
*/
public class TestBuggyAList {
   @Test
   public void testThreeAddThreeRemove() {
       AListNoResizing<Integer> list = new AListNoResizing<>();
       BuggyAList<Integer> buggyAList = new BuggyAList<>();
       for(int j = 0; j < 3; j++){
           list.addLast(j);
           buggyAList.addLast(j);
       }
       for(int j = 0; j < 3; j++){
           assertEquals(list.removeLast(),buggyAList.removeLast());
       }
   }

   @Test
   public void randomizedAListNoResizing() {
       AListNoResizing<Integer> L = new AListNoResizing<>();
       BuggyAList<Integer> BL = new BuggyAList<>();
       int N = 5000;
       for (int i = 0; i < N; i += 1) {
           int operationNumber = StdRandom.uniform(0, 2);
           if (operationNumber == 0) {
               // addLast
               int randVal = StdRandom.uniform(0, 100);
               L.addLast(randVal);
               BL.addLast(randVal);
           } else if (operationNumber == 1) {
               // size
               int sizeL = L.size();
               int sizeBL = BL.size();
               assertEquals(sizeL, sizeBL);
           }
       }
   }

   @Test
   public void randomizedAListResizing2() {
       AListNoResizing<Integer> L = new AListNoResizing<>();
       BuggyAList<Integer> BL = new BuggyAList<>();
       int N = 5000;
       for (int i = 0; i < N; i += 1) {
           int operationNumber = StdRandom.uniform(0, 3);
           if (operationNumber == 0) {
               int randVal = StdRandom.uniform(0, 100);
               L.addLast(randVal);
               BL.addLast(randVal);
           } else if (operationNumber == 1) {
               int size = L.size();
               int sizeBL = BL.size();
               assertEquals(size, sizeBL);
           } else if (operationNumber == 2) {
               if(L.size() * BL.size() <= 0){
                   continue;
               }
               int rVal = L.removeLast();
               int rValBL = BL.removeLast();
               assertEquals(rVal, rValBL);
           }
       }
   }
}
