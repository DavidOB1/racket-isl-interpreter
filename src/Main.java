import java.util.ArrayList;
import java.util.Stack;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    boolean running = true;
    Interpreter interpreter = new Interpreter();
    // open racket file using args
    String program = ";; this is a comment \n"
        + "(string-append \"hello \" (string-append \"world\" \"!\"))"
        + "(/ 5 2 2) (define TWO 2) (check-expect (- 4 2) TWO) 24 "
        + "(define (combine-string a b) (string-append \"Combined string: \" a \" \" b))"
        + "(combine-string \"cool\" \"function\")"
        + "(define (sum n) (if (> n 0) (+ n (sum (- n 1))) 0))"
        + "(check-expect (sum 20) (/ (* 20 21) 2))"
        + "(check-expect (sum 4) 10)";
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
    
    // Interactions window
    System.out.println("--------------------------");
    System.out.println(" DrRacket ISL Interpreter ");
    System.out.println("--------------------------");
    while (running) {
      System.out.print("> ");
      @SuppressWarnings("resource")
      Scanner in = new Scanner(System.in);
      String line = in.nextLine();
      if (line.equals("!stop")) {
        running = false;
        System.out.println("Ending interpreter.");
        break;
      }
      ArrayList<Object> lineEval = interpreter.interpret(line);
      for (Object o : lineEval) {
        if (o != null) {
          System.out.println(o);
        }
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