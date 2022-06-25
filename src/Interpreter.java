import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.function.Function;

public class Interpreter {

  private Function<String, Object> mainEnv = BasicEnv.env;
  private Stack<String> futureTestStack1 = new Stack<String>();
  private Stack<String> futureTestStack2 = new Stack<String>();
  
  // Determines if the string is an int
  static boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
      return true;
    }
    catch (NumberFormatException nfe) {
      return false;
    }
  }

  // Determines if the string is a double
  static boolean isDouble(String s) {
    try {
      Double.parseDouble(s);
      return true;
    }
    catch (NumberFormatException nfe) {
      return false;
    }
  }

  // Interprets the given program
  public ArrayList<Object> interpret(String program) {
    ArrayList<Object> outputArr = new ArrayList<Object>();
    while (String.valueOf(program.charAt(0)).equals(" ")) {
      program = program.substring(1, program.length());
    }
    ArrayList<String> stringArgs = parse("(" + program + ")");
    
    for (String s : stringArgs) {
      outputArr.add(eval(s, mainEnv));
    }
    
    while (!(futureTestStack1.empty() || futureTestStack2.empty())) {
      CheckExpect.testStack1.push(eval(futureTestStack1.pop(), mainEnv));
      CheckExpect.testStack2.push(eval(futureTestStack2.pop(), mainEnv));
    }
    
    return outputArr;
  }

  // Evaluates the given expression in the given environment
  @SuppressWarnings("finally")
  Object eval(String expr, Function<String, Object> env) {
    if (expr.equals("")) {
      throw new RuntimeException("Empty string is not a valid input.");
    }
    Character start, end;
    start = expr.charAt(0); end = expr.charAt(expr.length() - 1);
    // expr is definition
    if (expr.length() > 8 && expr.substring(0, 8).equals("(define ")) {
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
        if (expr.contains("...")) {
          // definition is a template
          return null;
        }
        String lamExpr = secondArg;
        ArrayList<String> lamArgs = parse(firstArg);
        String funcName = lamArgs.remove(0);
        String lamParams = "(";
        for (String p : lamArgs) {
          lamParams += p + " ";
        }
        lamParams = lamParams.substring(0, lamParams.length() - 1) + ")";
        lamExpr = "(lambda " + lamParams + " "  + lamExpr + ")";
        mainEnv = extendEnv(funcName, eval(lamExpr, mainEnv), mainEnv);
      }
      else {
        mainEnv = extendEnv(firstArg, eval(secondArg, mainEnv), mainEnv);
      }
      return null;
    }
    // expr is a lambda
    else if (expr.length() > 8 && expr.substring(0, 8).equals("(lambda ")) {
      ArrayList<String> lambdaArgs = Interpreter.parse(expr);
      lambdaArgs.remove(0);
      if (lambdaArgs.size() != 2) {
        throw new RuntimeException("Function expects 2 arguments, given " + lambdaArgs.size());
      }
      ArrayList<String> lambdaParams = parse(lambdaArgs.get(0));
      String lambdaBody = lambdaArgs.get(1);
      return (Function<ArrayList<Object>, Object>) (args) -> {
        Function<String, Object> localEnv = mainEnv;
        if (lambdaParams.size() != args.size()) {
          throw new RuntimeException("function expects " + lambdaParams.size() + 
              " arguements, given " + args.size());
        }
        for (int i = 0; i < lambdaParams.size(); i++) {
          localEnv = extendEnv(lambdaParams.get(i), args.get(i), localEnv);
        }
          return eval(lambdaBody, localEnv);
      };
    }
    // expr is an if statement
    else if (expr.length() > 4 && expr.substring(0, 4).equals("(if ") && end.equals(')')) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      if (stringArgs.size() != 3) {
        throw new RuntimeException("if expects 3 arguements, given " + stringArgs.size());
      }
      if ((boolean) eval(stringArgs.get(0), env)) {
        return eval(stringArgs.get(1), env);
      }
      else {
        return eval(stringArgs.get(2), env);
      }
    }
    else if (expr.length() > 14 && expr.substring(0, 14).equals("(check-expect ") && end.equals(')')) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      if (stringArgs.size() != 2) {
        throw new RuntimeException("check-expect expects 2 arguments, given " + stringArgs.size());
      }
      futureTestStack1.push(stringArgs.get(0));
      futureTestStack2.push(stringArgs.get(1));
      return null;
    }
    // expr is a define struct
    else if (expr.length() > 15 && expr.substring(0, 15).equals("(define-struct ")) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      if (stringArgs.size() != 2) {
        throw new RuntimeException("Invalid number of arguments given to define-struct");
      }
      String structName = stringArgs.get(0);
      ArrayList<String> structParams = parse(stringArgs.get(1));
      
      // Adding functionality for the struct
      
      mainEnv = extendEnv("make-" + structName, (Function<ArrayList<Object>, Object>) (args) -> {
        HashMap<String, Object> struct = new HashMap<String, Object>();
        struct.put("$$type$$", structName);
        if (args.size() != structParams.size()) {
          throw new RuntimeException("make-" + structName + " expects " + structParams.size()
          + " arguments, given " + args.size());
        }
        for (int i = 0; i < structParams.size(); i++) {
          struct.put(structParams.get(i), args.get(i));
        }
        return struct;
      }, mainEnv);
      
      mainEnv = extendEnv(structName + "?", (Function<ArrayList<Object>, Object>) (args) -> {
        boolean output = false;
        try {
          @SuppressWarnings("unchecked")
          HashMap<String, Object> thisStruct = (HashMap<String, Object>) args.get(0);
          if (args.size() == 1) {
            output = structName.equals((String) thisStruct.get("$$type$$"));
          }
        }
        finally {
          if (args.size() != 1) {
            throw new RuntimeException(structName + "? should only have 1 argument");
          }
          return output;
        }
      }, mainEnv);
      
      for (String param: structParams) {
        mainEnv = extendEnv(structName + "-" + param, (Function<ArrayList<Object>, Object>) (args) -> {
          Object result = null;
          try {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> thisStruct = (HashMap<String, Object>) args.get(0);
            if (args.size() == 1 && ((String) thisStruct.get("$$type$$")).equals(structName)) {
              result = thisStruct.get(param);
            }
          }
          catch (Exception e) {
            if (args.size() != 1) {
              throw new RuntimeException(structName + "-" + param + " should only have 1 argument");
            }
            throw new RuntimeException(structName + "-" + param + " should be given argument"
                + " of type " + structName);
          }
          return result;
        }, mainEnv);
      }
      
      return null;
    }
    // expr is a cond
    else if (expr.length() > 6 && expr.substring(0, 6).equals("(cond ")) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      Function<String, Object> localEnv = (s) -> {
        if (s.equals("else")) {
          return true;
        }
        else {
          return env.apply(s);
        }
      };
      for (String clause : stringArgs) {
        ArrayList<String> pair = parse(clause);
        if (pair.size() != 2) {
          throw new RuntimeException("cond branch expects 2 clauses, given " + pair.size());
        }
        if ((boolean) eval(pair.get(0), localEnv)) {
          return eval(pair.get(1), env);
        }
      }
      throw new RuntimeException("Every cond branch evaluated to false.");
    }
    // expr is a true boolean
    else if (expr.equals("true") || expr.equals("#true") || expr.equals("#t")) {
      return true;
    }
    // expr is a false boolean
    else if (expr.equals("false") || expr.equals("#false") || expr.equals("#f")) {
      return false;
    }
 // expr is an and statement
    else if (expr.length() > 5 && expr.substring(0, 5).equals("(and ")) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      while (!stringArgs.isEmpty()) {
        boolean result = (boolean) eval(stringArgs.remove(0), env);
        if (!result) {
          return false;
        }
      }
      return true;
    }
 // expr is an or statement
    else if (expr.length() > 4 && expr.substring(0, 4).equals("(or ")) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      while (!stringArgs.isEmpty()) {
        boolean result = (boolean) eval(stringArgs.remove(0), env);
        if (result) {
          return true;
        }
      }
      return false;
    }
    // expr is a cons
    else if (expr.length() > 6 && expr.substring(0, 6).equals("(cons ")) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      if (stringArgs.size() != 2) {
        throw new RuntimeException("cons must only recieve 2 arguments");
      }
      Object first = eval(stringArgs.get(0), env);
      Object restObj = eval(stringArgs.get(1), env);
      if (!(restObj instanceof HashMap)) {
        throw new RuntimeException("cons second argument must be a list");
      }
      @SuppressWarnings("unchecked")
      HashMap<String, Object> rest = (HashMap<String, Object>) restObj;
      if (!(rest.get("$$type$$").equals("mt") || rest.get("$$type$$").equals("cons"))) {
        throw new RuntimeException("cons second argument must be a list");
      }
      HashMap<String, Object> output = new HashMap<String, Object>();
      output.put("$$type$$", "cons");
      output.put("first", first);
      output.put("rest", rest);
      return output;
    }
    // expr is list (not in cons form)
    else if (expr.length() > 6 && expr.substring(0, 6).equals("(list ")) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      if (stringArgs.size() == 0) {
        return eval("empty", env);
      }
      String stringList = "empty";
      for (int i = stringArgs.size() - 1; i >= 0; i--) {
        stringList = "(cons " + stringArgs.get(i)+ " " + stringList + ")";
      }
      return eval(stringList, env);
    }
    // expr is a function call
    else if (start.equals('(') && end.equals(')')) {
      return evalFunction(expr, env);
    }
    // expr is a symbol
    else if (start.equals('\'') && !expr.equals("'()")) {
      return expr;
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
      else if (cur.equals('(') && (charStack.empty() || !charStack.peek().equals('"'))) {
        charStack.add(cur);
      }
      else if (cur.equals(')') && (charStack.empty() || !charStack.peek().equals('"'))) {
        // Throw syntax error?
        charStack.pop();
        if (charStack.empty()) {
          stringArgs.add(expr.substring(j, i + 1));
          j = i + 1;
        }
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
        eval(stringArgs.remove(0), env);
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