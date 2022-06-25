import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) {
    boolean running = true;
    Interpreter interpreter = new Interpreter();
    // Loading macros
    String program = "";
    try {
      File file = new File("src/macros.rkt");
      Scanner fileReader = new Scanner(file);
      while (fileReader.hasNextLine()) {
        program += fileReader.nextLine() + "\n";
      }
      file = new File("src/" + args[0]);
      fileReader.close();
      fileReader = new Scanner(file);
      while (fileReader.hasNextLine()) {
        program += fileReader.nextLine() + "\n";
      }
      fileReader.close();
    }
    catch (FileNotFoundException e) {
      throw new RuntimeException("Please ensure 'macros.txt' is contained in the src directory "
          + "and that the file you want ran is in the src directory and the run configuration.");
    }

    program = removeComments(program.replace("#reader", ";"));
    ArrayList<Object> output = 
        interpreter.interpret(program.replace('\n', ' ').replace('\t', ' ')
            .replace('[', '(').replace(']', ')').replace("λ", "lambda"));
    // doing check-expects
    String[] a = {"CheckExpect"};
    tester.Main.main(a);
    System.out.println("");
    for (Object o : output) {
      if (o != null) {
        System.out.println(o);
      }
    }
    
    // Interactions window
    System.out.println("");
    System.out.println("--------------------------");
    System.out.println(" DrRacket ISL Interpreter ");
    System.out.println("  Type !stop to terminate ");
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
      try {
        ArrayList<Object> lineEval = interpreter.interpret(line);
        for (Object o : lineEval) {
          if (o != null) {
            System.out.println(o);
          }
        }
      }
      catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
  }
  
  public static String removeComments(String prog) {
    String output = "";
    Stack<Character> charStack = new Stack<Character>();
    for (int i = 0; i < prog.length(); i++) {
      Character cur = prog.charAt(i);
      if (cur.equals('#') && i < prog.length() - 1 && String.valueOf(prog.charAt(i+1)).equals("|")) {
        charStack.add(cur);
      }
      if (cur.equals('#') && i > 0 && String.valueOf(prog.charAt(i-1)).equals("|")) {
        charStack.pop();
        cur = '\n';
      }
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