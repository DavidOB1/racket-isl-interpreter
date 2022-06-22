import java.util.ArrayList;

public class Main {
  public static void main(String[] args) {
    Interpreter interpreter = new Interpreter();
    // open racket file using args
    String program = "(string-append \"hello \" (string-append \"world\" \"!\"))"
        + "(+ 5 10)";
    ArrayList<Object> output = interpreter.interpret(program);
    for (Object o : output) {
      System.out.println(o);
    }
  }
}