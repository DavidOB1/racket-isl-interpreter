import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayDeque;
import java.util.function.Function;
import javalib.worldimages.WorldImage;

public class Interpreter {

  private Function<String, Object> mainEnv = BasicEnv.env;
  private ArrayDeque<String> futureTestQueue1 = new ArrayDeque<String>();
  private ArrayDeque<String> futureTestQueue2 = new ArrayDeque<String>();
  
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
      outputArr.add(eval(s));
    }
    
    while (!(futureTestQueue1.isEmpty() || futureTestQueue2.isEmpty())) {
      CheckExpect.testQueue1.add(eval(futureTestQueue1.remove()));
      CheckExpect.testQueue2.add(eval(futureTestQueue2.remove()));
    }
    
    return outputArr;
  }

  // Evaluates the given expression in the given environment
  @SuppressWarnings("finally")
  private Object eval(String expr) {
    if (expr.equals("")) {
      throw new RuntimeException("Empty string is not a valid input.");
    }
    Character start, end;
    start = expr.charAt(0); end = expr.charAt(expr.length() - 1);
    // expr is definition
    if (expr.length() > 8 && expr.substring(0, 8).equals("(define ")) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      if (stringArgs.size() != 2 && !expr.contains("...")) {
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
        if (lamArgs.size() > 0) {
          for (String p : lamArgs) {
            lamParams += p + " ";
          }
          lamParams = lamParams.substring(0, lamParams.length() - 1) + ")";
        }
        else {
          lamParams += ")";
        }
        lamExpr = "(lambda " + lamParams + " "  + lamExpr + ")";
        mainEnv = extendEnv(funcName, eval(lamExpr), mainEnv);
      }
      else {
        mainEnv = extendEnv(firstArg, eval(secondArg), mainEnv);
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
        Function<String, Object> tempEnv = mainEnv;
        if (lambdaParams.size() != args.size()) {
          throw new RuntimeException("function expects " + lambdaParams.size() + 
              " arguements, given " + args.size());
        }
        for (int i = 0; i < lambdaParams.size(); i++) {
          mainEnv = extendEnv(lambdaParams.get(i), args.get(i), mainEnv);
        }
          Object lamOutput = eval(lambdaBody);
          mainEnv = tempEnv;
          return lamOutput;
      };
    }
    // expr is an if statement
    else if (expr.length() > 4 && expr.substring(0, 4).equals("(if ") && end.equals(')')) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      if (stringArgs.size() != 3) {
        throw new RuntimeException("if expects 3 arguements, given " + stringArgs.size());
      }
      if ((boolean) eval(stringArgs.get(0))) {
        return eval(stringArgs.get(1));
      }
      else {
        return eval(stringArgs.get(2));
      }
    }
    else if (expr.length() > 14 && expr.substring(0, 14).equals("(check-expect ") && end.equals(')')) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      if (stringArgs.size() != 2) {
        throw new RuntimeException("check-expect expects 2 arguments, given " + stringArgs.size());
      }
      futureTestQueue1.push(stringArgs.get(0));
      futureTestQueue2.push(stringArgs.get(1));
      return null;
    }
    // expr is a local
    else if (expr.length() > 7 && expr.substring(0, 7).equals("(local ")) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      if (stringArgs.size() != 2) {
        throw new RuntimeException("local should have only 2 parts");
      }
      ArrayList<String> defs = parse(stringArgs.get(0));
      String body = stringArgs.get(1);
      Function<String, Object> tempEnv = mainEnv;
      for (String def : defs) {
        if (def.length() < 8 || !def.substring(0, 8).equals("(define ")) {
          throw new RuntimeException("local must be given only definitions");
        }
        ArrayList<String> defArgs = parse(def);
        defArgs.remove(0);
        if (defArgs.size() != 2) {
          throw new RuntimeException("local defintion should have only 2 parts");
        }
        String defDef = defArgs.get(0);
        if (defDef.substring(0, 1).equals("(") && defDef.substring(defDef.length() -1, 
            defDef.length()).equals(")")) {
          String lamExpr = defArgs.get(1);
          ArrayList<String> lamArgs = parse(defDef);
          String funcName = lamArgs.remove(0);
          String lamParams = "(";
          if (lamArgs.size() > 0) {
            for (String p : lamArgs) {
              lamParams += p + " ";
            }
            lamParams = lamParams.substring(0, lamParams.length() - 1) + ")";
          }
          else {
            lamParams += ")";
          }
          lamExpr = "(lambda " + lamParams + " "  + lamExpr + ")";
          mainEnv = extendEnv(funcName, eval(lamExpr), mainEnv);
        }
        else {
          mainEnv = extendEnv(defDef, eval(defArgs.get(1)), mainEnv);
        }
      }
      Object localOutput = eval(body);
      mainEnv = tempEnv;
      return localOutput;
    }
    // expr is big bang
    else if (expr.length() > 10 && expr.substring(0, 10).equals("(big-bang ")) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      BigBang bigBangGame = new BigBang(eval(stringArgs.get(0)));
      for (int i = 1; i < stringArgs.size(); i++) {
        ArrayList<String> bangArgs = parse(stringArgs.get(i));
        if (bangArgs.size() != 2 && !bangArgs.get(0).equals("stop-when")) {
          throw new RuntimeException("Invalid number of arguements given to " + bangArgs.get(0));
        }
        String bbFunc = bangArgs.get(0);
        String stringFunc = bangArgs.get(1);
        // Checking which function to add
        if (bbFunc.equals("to-draw")) {
          bigBangGame.setToDraw(eval(stringFunc));
        }
        else if (bbFunc.equals("on-tick")) {
          bigBangGame.setOnTick(eval(stringFunc));
        }
        else if (bbFunc.equals("on-key")) {
          bigBangGame.setOnKey(eval(stringFunc));
        }
        else if (bbFunc.equals("stop-when")) {
          if (bangArgs.size() == 2) {
            bigBangGame.setStopWhen(eval(stringFunc));
          }
          else if (bangArgs.size() == 3) {
            bigBangGame.setStopWhen(eval(stringFunc), eval(bangArgs.get(2)));
          }
          else {
            throw new RuntimeException("stop-when should only be given 2 or 3 arguments");
          }
        }
        else {
          throw new RuntimeException("Unknown big-bang arg: " + bbFunc);
        }
      }
      WorldImage firstFrame = bigBangGame.draw();
      bigBangGame.bigBang((int) firstFrame.getWidth(), (int) firstFrame.getHeight(), 1.0 / 58.0);
      return null;
    }
    // expr is an import
    else if (expr.length() > 9 && expr.substring(0, 9).equals("(require ")) {
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
        struct.put(";type", structName);
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
            output = structName.equals((String) thisStruct.get(";type"));
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
            if (args.size() == 1 && ((String) thisStruct.get(";type")).equals(structName)) {
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
      for (String clause : stringArgs) {
        ArrayList<String> pair = parse(clause);
        if (pair.size() != 2) {
          throw new RuntimeException("cond branch expects 2 clauses, given " + pair.size());
        }
        if (pair.get(0).equals("else") || (boolean) eval(pair.get(0))) {
          return eval(pair.get(1));
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
        boolean result = (boolean) eval(stringArgs.remove(0));
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
        boolean result = (boolean) eval(stringArgs.remove(0));
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
      Object first = eval(stringArgs.get(0));
      Object restObj = eval(stringArgs.get(1));
      if (!(restObj instanceof HashMap)) {
        throw new RuntimeException("cons second argument must be a list");
      }
      @SuppressWarnings("unchecked")
      HashMap<String, Object> rest = (HashMap<String, Object>) restObj;
      if (!(rest.get(";type").equals("mt") || rest.get(";type").equals("cons"))) {
        throw new RuntimeException("cons second argument must be a list");
      }
      HashMap<String, Object> output = new HashMap<String, Object>();
      output.put(";type", "cons");
      output.put("first", first);
      output.put("rest", rest);
      return output;
    }
    // expr is list (not in cons form)
    else if (expr.length() > 6 && expr.substring(0, 6).equals("(list ")) {
      ArrayList<String> stringArgs = parse(expr);
      stringArgs.remove(0);
      if (stringArgs.size() == 0) {
        return eval("empty");
      }
      String stringList = "empty";
      for (int i = stringArgs.size() - 1; i >= 0; i--) {
        stringList = "(cons " + stringArgs.get(i)+ " " + stringList + ")";
      }
      return eval(stringList);
    }
    // expr is a function call
    else if (start.equals('(') && end.equals(')')) {
      return evalFunction(expr);
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
      return mainEnv.apply(expr);
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
        if (!(prev.equals(')') || prev.equals('(') || prev.equals(' '))) {
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
  
  public Object evalFunction(String expr) {
    ArrayList<String> stringArgs = parse(expr);
    // Running the program
    ArrayList<Object> args = new ArrayList<Object>();
    @SuppressWarnings("unchecked")
    Function<ArrayList<Object>, Object> func = (Function<ArrayList<Object>, Object>) 
        eval(stringArgs.remove(0));
    for (String s : stringArgs) {
      args.add(eval(s));
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