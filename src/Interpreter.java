import java.util.ArrayList;
import java.util.Stack;
import java.util.function.Function;

public class Interpreter {

  private Function<String, Object> mainEnv = BasicEnv.env;
  
  static boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
      return true;
    }
    catch (NumberFormatException nfe) {
      return false;
    }
  }

  static boolean isDouble(String s) {
    try {
      Double.parseDouble(s);
      return true;
    }
    catch (NumberFormatException nfe) {
      return false;
    }
  }

  public ArrayList<Object> interpret(String program) {
    Stack<Integer> programStack = new Stack<Integer>();
    ArrayList<Object> outputArr = new ArrayList<Object>();
    for (int i = 0; i < program.length(); i++) {
      Character cur = program.charAt(i);
      if (cur.equals('(')) {
        programStack.push(i);
      }
      else if (cur.equals(')')) {
        int j = programStack.pop();
        if (programStack.empty()) {
          Object evaluation = eval(program.substring(j, i + 1), mainEnv);
          outputArr.add(evaluation);
        }
      }
    }
    return outputArr;
  }

  Object eval(String expr, Function<String, Object> env) {
    if (expr.equals("")) {
      throw new RuntimeException("Empty string is not a valid input.");
    }
    Character start, end;
    start = expr.charAt(0); end = expr.charAt(expr.length() - 1);
    // expr is definition
    if (expr.length() > 8 && expr.substring(0, 8).equals("(define ") && end.equals(')')) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      if (stringArgs.size() != 2) {
        throw new RuntimeException("define expects 2 arguments, given " + stringArgs.size());
      }
      String firstArg = stringArgs.get(0);
      String secondArg = stringArgs.get(1);
      Character firstA = firstArg.charAt(0); 
      Character lastA = firstArg.charAt(firstArg.length() -1);
      if (firstA.equals('(') && lastA.equals(')')) {
        ArrayList<String> funcArgs = Interpreter.parse(firstArg);
        String funcName = funcArgs.remove(0);
        mainEnv = extendEnv(funcName, (Function<ArrayList<Object>, Object>) (args) -> {
          Function<String, Object> localEnv = mainEnv;
          if (funcArgs.size() != args.size()) {
            throw new RuntimeException(funcName + " expects " + funcArgs.size() + 
                " arguements, given " + args.size());
          }
          for (int i = 0; i < funcArgs.size(); i++) {
            localEnv = extendEnv(funcArgs.get(i), args.get(i), localEnv);
          }
            return eval(secondArg, localEnv);
        }, mainEnv);
      }
      else {
        mainEnv = extendEnv(firstArg, eval(secondArg, mainEnv), mainEnv);
      }
      return null;
    }
    // expr is a function call
    else if (start.equals('(') && end.equals(')')) {
      return evalFunction(expr, env);
    }
    // expr is a string
    else if (start.equals('"') && end.equals('"')) {
      return expr.substring(1, expr.length() - 1);
    }
    // expr is an integer
    else if (isInteger(expr)) {
      return Integer.parseInt(expr);
    }
    // expr is a double
    else if (isDouble(expr)) {
      return Double.parseDouble(expr);
    }
    else {
      return env.apply(expr);
    }
  }

  public static ArrayList<String> parse(String expr) {
    ArrayList<String> stringArgs = new ArrayList<String>();
    Stack<Character> charStack = new Stack<Character>();
    int j = 1;
    Character cur, prev;
    cur = expr.charAt(0);
    for (int i = 1; i < expr.length() - 1; i++) {
      prev = cur;
      cur = expr.charAt(i);
      if (cur.equals(' ') && charStack.empty()) {
        if (!(prev.equals(')') || prev.equals(' '))) {
          stringArgs.add(expr.substring(j, i));
        }
        j = i + 1;
      }
      else if (cur.equals('"')) {
        if (charStack.empty() || charStack.peek().equals('(')) {
          charStack.add(cur);
        }
        else {
          charStack.pop();
        }
      }
      else if (cur.equals('(')) {
        charStack.add(cur);
      }
      else if (cur.equals(')')) {
        // Throw syntax error?
        charStack.pop();
        stringArgs.add(expr.substring(j, i + 1));
        j = i + 1;
      }
    }
    if (j < expr.length() - 1) {
      stringArgs.add(expr.substring(j, expr.length() - 1));
    }
    return stringArgs;
  }
  
  public Object evalFunction(String expr, Function<String, Object> env) {
    ArrayList<String> stringArgs = parse(expr);
    // Running the program
    ArrayList<Object> args = new ArrayList<Object>();
    @SuppressWarnings("unchecked")
    Function<ArrayList<Object>, Object> func = (Function<ArrayList<Object>, Object>) 
        env.apply(stringArgs.remove(0));
    for (String s : stringArgs) {
      args.add(eval(s, env));
    }
    return func.apply(args);
  }
  
  Function<String, Object> extendEnv(String ref, Object body, Function<String, Object> refEnv) {
    Function<String, Object> otherEnv = refEnv;
    return (s) -> {
      if (s.equals(ref)) {
        return body;
      }
      else {
        return otherEnv.apply(s);
      }
    };
  }
      
}