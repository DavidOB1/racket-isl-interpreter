import java.util.Stack;
import tester.*;

public class CheckExpect {
  public static Stack<Object> testStack1 = new Stack<Object>();
  public static Stack<Object> testStack2 = new Stack<Object>();
  
  public void testCheckExpects(Tester t) {
    while (! (testStack1.empty() || testStack2.empty())) {
      t.checkExpect(testStack1.pop(), testStack2.pop());
    }
  }
}