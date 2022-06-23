import java.util.ArrayList;
import java.util.Stack;

public class Main {
  public static void main(String[] args) {
    Interpreter interpreter = new Interpreter();
    // open racket file using args
    String program = ";this is a comment \n (string-append \"hello \" (string-append \"world\" \"!\"))"
        + "(/ 5 2 2) (define TWO 2) (check-expect (- 4 2) TWO)"
        + "(define (combine-string a b) (string-append \"Combined string: \" a \" \" b))"
        + "(combine-string \"pog\" \"champ\")";
    program = removeComments(program);
    ArrayList<Object> output = interpreter.interpret(program.replace('\n', ' '));
    // doing check-expects
    String[] a = {"CheckExpect"};
    tester.Main.main(a);
    for (Object o : output) {
      if (o != null) {
        System.out.println(o);
      }
    }
  }
  
  public static String removeComments(String prog) {
    String output = "";
    Stack<Character> charStack = new Stack<Character>();
    for (int i = 0; i < prog.length(); i++) {
      Character cur = prog.charAt(i);
      if (cur.equals(';') && charStack.empty()) {
        charStack.add(cur);
      }
      if (charStack.empty()) {
        output += String.valueOf(cur);
      }
      else if (cur.equals('\n')) {
        charStack.pop();
      }
    }
    return output;
  }
}