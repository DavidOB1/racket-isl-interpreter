import java.util.ArrayDeque;
import tester.*;

// Performs check expects
public class CheckExpect {
  
  public static ArrayDeque<Object> testQueue1 = new ArrayDeque<Object>();
  public static ArrayDeque<Object> testQueue2 = new ArrayDeque<Object>();
  
  // Ensures each item in the two queues are equal
  public void testCheckExpects(Tester t) {
    while (! (testQueue1.isEmpty() || testQueue2.isEmpty())) {
      t.checkExpect(testQueue1.removeFirst(), testQueue2.removeFirst());
    }
  }
}