package flik;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class FlikTest {
    @Test
    public void testFlik() {
        for(int i=0,j=0;i<100;i++,j++){
            System.out.println(i+"  "+j);
            if(!Flik.isSameNumber(i,j)){
                System.out.println(String.format("i:%d not same as j:%d ??", i, j));
                break;
            }
        }
    }

    @Test
    public void testFlikFor128() {
        for(int i=0,j=0;i<129;i++,j++){
            System.out.println(i+"  "+j);
            if(!Flik.isSameNumber(i,j)){
                System.out.println(String.format("i:%d not same as j:%d ??", i, j));
                break;
            }
        }
    }

    @Test
    public void testFlikFor200() {
        for(int i=0,j=0;i<200;i++,j++){
            System.out.println(i+"  "+j);
            if(!Flik.isSameNumber(i,j)){
                System.out.println(String.format("i:%d not same as j:%d ??", i, j));
            }
        }
    }

    @Test
    public void testFlikFor200ByAssertTrue() {
        for(int i=0,j=0;i<200;i++,j++){
            System.out.println(i+"  "+j);
            if(!Flik.isSameNumber(i,j)){
                System.out.println(String.format("i:%d not same as j:%d ??", i, j));
            }
        }
    }


}
