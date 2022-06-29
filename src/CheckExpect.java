import java.util.ArrayDeque;
import tester.*;

public class CheckExpect {
  public static ArrayDeque<Object> testQueue1 = new ArrayDeque<Object>();
  public static ArrayDeque<Object> testQueue2 = new ArrayDeque<Object>();
  
  public void testCheckExpects(Tester t) {
    while (! (testQueue1.isEmpty() || testQueue2.isEmpty())) {
      t.checkExpect(testQueue1.removeFirst(), testQueue2.removeFirst());
    }
  }
}