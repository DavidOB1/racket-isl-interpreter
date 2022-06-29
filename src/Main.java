import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Scanner;

// Main class
public class Main {

  // The main method
  public static void main(String[] args) {
    Interpreter interpreter = new Interpreter();
    
    // Loading files
    String program = "";
    try {
      File file = new File("src/functions.rkt");
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
      throw new RuntimeException("Please ensure 'functions.rkt' is contained in the src directory "
          + "and that the file you want ran is in the src directory and the run configuration.");
    }

    // Interprets the program
    ArrayList<Object> output = interpreter.interpret(formatProg(program));
    
    // Performs check-expects
    String[] a = {"CheckExpect"};
    tester.Main.main(a);
    System.out.println("");
    
    // Prints out any non-null outputs
    for (Object o : output) {
      if (o != null) {
        System.out.println(o);
      }
    }
  
    // Creates the interactions window
    interactionsWindow(interpreter);
  }
  
  // Runs the interactions window
  private static void interactionsWindow(Interpreter interpreter) {
    boolean running = true;
    System.out.println("");
    System.out.println("+-------------------------+");
    System.out.println("| Racket ISL Interpreter  |");
    System.out.println("| Type !stop to terminate |");
    System.out.println("+-------------------------+");
    Scanner in = new Scanner(System.in);
    while (running) {
      System.out.print("> ");
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
    in.close();
  }
  
  // Formats the given program
  private static String formatProg(String prog) {
    return removeComments(prog).replace('\n', ' ').replace('\t', ' ')
        .replace('[', '(').replace(']', ')').replace("λ", "lambda").replace("#;", "");
  }
  
  // Removes comments from a given program
  private static String removeComments(String prog) {
    String program = prog.replace("#reader", ";");
    String output = "";
    Stack<Character> charStack = new Stack<Character>();
    for (int i = 0; i < program.length(); i++) {
      Character cur = program.charAt(i);
      if (cur.equals('#') && i < program.length() - 1 && 
          String.valueOf(program.charAt(i+1)).equals("|")) {
        charStack.add(cur);
      }
      if (cur.equals('#') && i > 0 && String.valueOf(program.charAt(i-1)).equals("|")) {
        charStack.pop();
        cur = '\n';
      }
      if (cur.equals(';') && charStack.empty()) {
        charStack.add(cur);
      }
      // Checking whether to pop from the stack, add to the string, or skip the character
      if (charStack.empty()) {
        output += String.valueOf(cur);
      }
      else if (charStack.peek().equals(';') && cur.equals('\n')) {
        charStack.pop();
      }
    }
    return output;
  }
}