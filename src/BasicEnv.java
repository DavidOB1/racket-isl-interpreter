import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class BasicEnv {
  
  public static void invalidArgsCheck(String f, int intendedArgs, ArrayList<Object> args) {
    if (intendedArgs != args.size()) {
      throw new RuntimeException(f + " expects " + intendedArgs 
          + " arguements, given " + args.size());
    }
  }
  
  // The basic environment
  @SuppressWarnings("unchecked")
  public static Function<String, Object> env = (s) -> {
    switch (s) {
    // returns the identity of the input
    case "identity":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        return a.get(0);
      };
    // defining the empty list
    case "'()":
    case "empty":
    case "null":
      HashMap<String, Object> mtOutput = new HashMap<String, Object>();
      mtOutput.put("type", "mt");
      return mtOutput;
    // determining if list is empty
    case "empty?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        try {
          return ((String) ((HashMap<String, Object>) a.get(0)).get("type")).equals("mt");
        }
        catch (Exception e) {
          return false;
        }
      };
    // determining if list is non-empty
    case "cons?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        try {
          return ((String) ((HashMap<String, Object>) a.get(0)).get("type")).equals("cons");
        }
        catch (Exception e) {
          return false;
        }
      };
   // retrieving first element from a list
    case "first":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        boolean mtList = false;
        try {
          HashMap<String, Object> listObj = ((HashMap<String, Object>) a.get(0));
          String listType = (String) listObj.get("type");
          mtList = listType.equals("mt");
          if (listType.equals("cons")) {
            return listObj.get("first");
          }
        }
        catch (Exception e) {
          if (mtList) {
            throw new RuntimeException("Cannot call first on an empty list");
          }
          throw new RuntimeException("first must be given a list");
        }
        return null;
      };
   // retrieving rest from a list
    case "rest":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        boolean mtList = false;
        try {
          HashMap<String, Object> listObj = ((HashMap<String, Object>) a.get(0));
          String listType = (String) listObj.get("type");
          mtList = listType.equals("mt");
          if (listType.equals("cons")) {
            return listObj.get("rest");
          }
        }
        catch (Exception e) {
          if (mtList) {
            throw new RuntimeException("Cannot call rest on an empty list");
          }
          throw new RuntimeException("rest must be given a list");
        }
        return null;
      };
    // tryna compare string equality
    case "string=?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 2, a);
        if (!((a.get(0) instanceof String) && (a.get(1) instanceof String))) {
          throw new RuntimeException("string=? must be given strings only");
        }
        String s1 = (String) a.get(0);
        String s2 = (String) a.get(1);
        return s1.equals(s2);
      };
    // tryna check if object is a string
    case "string?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        try {
          String str = (String) a.get(0);
          return (str.length() == 0 || !str.substring(0, 1).equals("'"));
        }
        catch (Exception e) {
          return false;
        }
      };
    // tryna substring
    case "substring":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        if (a.size() < 2 || a.size() > 3) {
          throw new RuntimeException("substring expects 2 or 3 arguments, given " + a.size());
        }
        if (!(a.get(0) instanceof String)) {
          throw new RuntimeException("substring first arg must be string");
        }
        String str = (String) a.remove(0);
        if (a.size() == 1) {
          a.add(str.length());
        }
        if (!(a.get(0) instanceof Integer)) {
          throw new RuntimeException("Substring second arg must be int");
        }
        int n1 = (int) a.get(0);
        if (!(a.get(1) instanceof Integer)) {
          throw new RuntimeException("Substring third arg must be int");
        }
        int n2 = (int) a.get(1);
        return str.substring(n1, n2);
      };
    case "string-length":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        if (!(a.get(0) instanceof String)) {
          throw new RuntimeException("string-length requires a string as input");
        }
        String str = (String) a.get(0);
        return str.length();
      };
    // tryna string append
    case "string-append":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        String output = "";
        for (Object o : a) {
          if (!(o instanceof String)) {
            throw new RuntimeException("string-append must be given only Strings");
          }
          output += (String) o;
        }
        return output;
      };
    // tryna check if arg is a number
    case "number?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1 ,a);
        return (a.get(0) instanceof Integer) || (a.get(0) instanceof Double);
      };
    // tryna add together numbers
    case "+":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        double output = 0.0;
        for (Object o : a) {
          if (o instanceof Integer) {
            output += (int) o;
          }
          else if (o instanceof Double) {
            output += (double) o;
          }
          else {
            throw new RuntimeException("+ must be given only numbers");
          }
        }
        if (output % 1 < 0.00001) {
          return (int) output;
        }
        return output;
      };
    // tryna subtract together numbers
    case "-":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        double output = 0.0;
        boolean first = true;
        for (Object o : a) {
          if (o instanceof Integer) {
            if (first) {
              output += (int) o;
              first = false;
            }
            else {
              output -= (int) o;
            }
          }
          else if (o instanceof Double) {
            if (first) {
              output += (double) o;
              first = false;
            }
            else {
              output -= (double) o;
            }
          }
          else {
            throw new RuntimeException("- must be given only numbers");
          }
        }
        if (output % 1 < 0.00001) {
          return (int) output;
        }
        return output;
      };
    // tryna multiply together numbers
    case "*":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        double output = 0.0;
        boolean first = true;
        for (Object o : a) {
          if (o instanceof Integer) {
            if (first) {
              output += (int) o;
              first = false;
            }
            else {
              output *= (int) o;
            }
          }
          else if (o instanceof Double) {
            if (first) {
              output += (double) o;
              first = false;
            }
            else {
              output *= (double) o;
            }
          }
          else {
            throw new RuntimeException("* must be given only numbers");
          }
        }
        if (output % 1 < 0.00001) {
          return (int) output;
        }
        return output;
      };
    // tryna divide together numbers
    case "/":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        double output = 0.0;
        boolean first = true;
        for (Object o : a) {
          if (o instanceof Integer) {
            if (first) {
              output += (int) o;
              first = false;
            }
            else {
              output /= (int) o;
            }
          }
          else if (o instanceof Double) {
            if (first) {
              output += (double) o;
              first = false;
            }
            else {
              output /= (double) o;
            }
          }
          else {
            throw new RuntimeException("/ must be given only numbers");
          }
        }
        if (output % 1 < 0.00001) {
          return (int) output;
        }
        return output;
      };
   // checking number equality
    case "=":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 2, a);
        Object o1 = a.get(0);
        Object o2 = a.get(1);
        if (o1 instanceof Integer) {
          return (o2 instanceof Integer) && ((int)o1 == (int)o2);
        }
        else if (o1 instanceof Double) {
          return (o2 instanceof Double) && ((double)o1 == (double)o2);
        }
        else {
          throw new RuntimeException("= should be given numbers only");
        }
      };
   // tryna check if greater than
    case ">":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        if (a.size() < 2) {
          throw new RuntimeException("> expects at least two arguments");
        }
        boolean output = true;
        for (int i = 1; i < a.size(); i++) {
          Object o1 = a.get(i-1);
          Object o2 = a.get(i);
          double n1, n2;
          if (o1 instanceof Integer) {
           n1 = Double.valueOf((int) o1);
          }
          else if (o1 instanceof Double) {
           n1 = (double) o1;
          }
          else {
            throw new RuntimeException("> expects a number");
          }
          if (o2 instanceof Integer) {
            n2 = Double.valueOf((int) o2);
          }
          else if (o2 instanceof Double) {
            n2 = (double) o2;
          }
          else {
            throw new RuntimeException("> expects a number");
          }
          output &= n1 > n2;
        }
        return output;
      };
   // tryna check if less than
    case "<":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        if (a.size() < 2) {
          throw new RuntimeException("> expects at least two arguments");
        }
        boolean output = true;
        for (int i = 1; i < a.size(); i++) {
          Object o1 = a.get(i-1);
          Object o2 = a.get(i);
          double n1, n2;
          if (o1 instanceof Integer) {
           n1 = Double.valueOf((int) o1);
          }
          else if (o1 instanceof Double) {
           n1 = (double) o1;
          }
          else {
            throw new RuntimeException("> expects a number");
          }
          if (o2 instanceof Integer) {
            n2 = Double.valueOf((int) o2);
          }
          else if (o2 instanceof Double) {
            n2 = (double) o2;
          }
          else {
            throw new RuntimeException("> expects a number");
          }
          output &= n1 < n2;
        }
        return output;
      };
   // tryna check if greater than or equal to
    case ">=":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        if (a.size() < 2) {
          throw new RuntimeException("> expects at least two arguments");
        }
        boolean output = true;
        for (int i = 1; i < a.size(); i++) {
          Object o1 = a.get(i-1);
          Object o2 = a.get(i);
          double n1, n2;
          if (o1 instanceof Integer) {
           n1 = Double.valueOf((int) o1);
          }
          else if (o1 instanceof Double) {
           n1 = (double) o1;
          }
          else {
            throw new RuntimeException("> expects a number");
          }
          if (o2 instanceof Integer) {
            n2 = Double.valueOf((int) o2);
          }
          else if (o2 instanceof Double) {
            n2 = (double) o2;
          }
          else {
            throw new RuntimeException("> expects a number");
          }
          output &= n1 >= n2;
        }
        return output;
      };
   // tryna check if less than or equal to
    case "<=":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        if (a.size() < 2) {
          throw new RuntimeException("> expects at least two arguments");
        }
        boolean output = true;
        for (int i = 1; i < a.size(); i++) {
          Object o1 = a.get(i-1);
          Object o2 = a.get(i);
          double n1, n2;
          if (o1 instanceof Integer) {
           n1 = Double.valueOf((int) o1);
          }
          else if (o1 instanceof Double) {
           n1 = (double) o1;
          }
          else {
            throw new RuntimeException("> expects a number");
          }
          if (o2 instanceof Integer) {
            n2 = Double.valueOf((int) o2);
          }
          else if (o2 instanceof Double) {
            n2 = (double) o2;
          }
          else {
            throw new RuntimeException("> expects a number");
          }
          output &= n1 <= n2;
        }
        return output;
      };
    // function is not in the environment
    default:
      throw new RuntimeException("Unknown lookup: " + s);
    }
  };
}