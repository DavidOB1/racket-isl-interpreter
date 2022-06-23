import java.util.ArrayList;

public class Main {
  public static void main(String[] args) {
    Interpreter interpreter = new Interpreter();
    // open racket file using args
    String program = "(string-append \"hello \" (string-append \"world\" \"!\"))"
        + "(- 5 2 2) (check-expect 4 (+ 2 2))";
    ArrayList<Object> output = interpreter.interpret(program);
    // doing check-expects
    String[] a = {"CheckExpect"};
    tester.Main.main(a);
    for (Object o : output) {
      if (o != null) {
        System.out.println(o);
      }
    }
  }
}
