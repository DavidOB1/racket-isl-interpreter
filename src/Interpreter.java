import java.util.ArrayList;
import java.util.Stack;
import java.util.function.BiFunction;

public class Interpreter {
  
  // Top environment
  private static BiFunction<String, ArrayList<Object>, Object> mainEnv = 
      (f, a) -> {
        switch (f) {
        // tryna check-expect
        case "check-expect":
          if (a.size() != 2) {
            throw new RuntimeException("check-expect needs 2 arguments");
          }
          CheckExpect.testStack1.add(a.remove(0));
          CheckExpect.testStack2.add(a.remove(0));
        // tryna string append
        case "string-append":
          String output1 = "";
          for (Object o : a) {
            if (!(o instanceof String)) {
              throw new RuntimeException("string-append must be given only Strings");
            }
            output1 += (String)o;
          }
          return output1;
        // tryna add together numbers
        case "+":
          double output2 = 0.0;
          for (Object o : a) {
            if (o instanceof Integer) {
              output2 += (int)o;
            }
            else if (o instanceof Double) {
              output2 += (double)o;
            }
            else {
              throw new RuntimeException("+ must be given only numbers");
            }
          }
          if (output2 % 1 == 0) {
            return (int)output2;
          }
          return output2;
        // tryna subtract together numbers
        case "-":
          double output3 = 0.0;
          boolean first3 = true;
          for (Object o : a) {
            if (o instanceof Integer) {
              if (first3) {
                output3 += (int)o;
                first3 = false;
              }
              else {
                output3 -= (int)o;
              }
            }
            else if (o instanceof Double) {
              if (first3) {
                output3 += (double)o;
                first3 = false;
              }
              else {
                output3 -= (double)o;
              }
            }
            else {
              throw new RuntimeException("- must be given only numbers");
            }
          }
          if (output3 % 1 == 0) {
            return (int)output3;
          }
          return output3;
        // tryna multiply together numbers
        case "*":
          double output4 = 0.0;
          for (Object o : a) {
            if (o instanceof Integer) {
              output4 *= (int)o;
            }
            else if (o instanceof Double) {
              output4 *= (double)o;
            }
            else {
              throw new RuntimeException("* must be given only numbers");
            }
          }
          if (output4 % 1 == 0) {
            return (int)output4;
          }
          return output4;
        // tryna divide together numbers
        case "/":
          double output5;
          Object first5 = a.remove(0);
          if (first5 instanceof Integer) {
            output5 = Double.valueOf((int)first5);
          }
          else if (first5 instanceof Double) {
            output5 = (double)first5;
          }
          else {
            throw new RuntimeException("/ must be given only numbers");
          }
          for (Object o : a) {
            if (o instanceof Integer) {
              output5 /= (int)o;
            }
            else if (o instanceof Double) {
              output5 /= (double)o;
            }
            else {
              throw new RuntimeException("/ must be given only numbers");
            }
          }
          if (output5 % 1 == 0) {
            return (int)output5;
          }
          return output5;
          // function is not in the environment
        default:
          throw new RuntimeException("Unknown lookup: " + f);
        }
      };

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

  Object eval(String expr, BiFunction<String, ArrayList<Object>, Object> env) {
    if (expr.equals("")) {
      throw new RuntimeException("Empty string is not a valid input.");
    }
    Character start, end;
    start = expr.charAt(0); end = expr.charAt(expr.length() - 1);
    // expr is a function call
    if (start.equals('(') && end.equals(')')) {
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
      return env.apply(expr, null);
    }
  }

  public Object evalFunction(String expr, BiFunction<String, ArrayList<Object>, Object> env) {
    ArrayList<String> stringArgs = new ArrayList<String>();
    Stack<Character> charStack = new Stack<Character>();
    int j = 1;
    for (int i = 1; i < expr.length() - 1; i++) {
      Character cur = expr.charAt(i);
      if (cur.equals(' ') && charStack.empty()) {
        stringArgs.add(expr.substring(j, i));
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
    ArrayList<Object> args = new ArrayList<Object>();
    String func = stringArgs.remove(0);
    for (String s : stringArgs) {
      args.add(eval(s, env));
    }
    return env.apply(func, args);
  }
  
//  BiFunction<String, ArrayList<Object>, Object> extendEnv(String f, ArrayList<Object> a, Object o) {
//    return (s, args) -> {
//      if (s.equals(f)) {
//        
//      }
//    };
//  }
      
}
