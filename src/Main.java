import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Scanner;
// AGENDA: Adding images and lambdas
public class Main {
  public static void main(String[] args) {
    boolean running = true;
    Interpreter interpreter = new Interpreter();
    // Loading macros
    String program = "";
    try {
      File file = new File("C:\\Users\\ottob\\Downloads\\fundies2\\EclipseWorkspace\\ISL Interpreter\\src\\macros.txt");
      Scanner fileReader = new Scanner(file);
      while (fileReader.hasNextLine()) {
        program += fileReader.nextLine() + "\n";
      }
      file = new File("C:\\Users\\ottob\\Downloads\\fundies2\\EclipseWorkspace\\ISL Interpreter\\src\\program.txt");
      fileReader.close();
      fileReader = new Scanner(file);
      while (fileReader.hasNextLine()) {
        program += fileReader.nextLine() + "\n";
      }
      fileReader.close();
    }
    catch (FileNotFoundException e) {
      throw new RuntimeException("Please ensure 'macros.txt' is contained in the src directory.");
    }

    program = removeComments(program);
    ArrayList<Object> output = 
        interpreter.interpret(program.replace('\n', ' ').replace('\t', ' ')
            .replace('[', '(').replace(']', ')'));
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