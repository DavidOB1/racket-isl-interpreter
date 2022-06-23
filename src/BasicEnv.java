import java.util.ArrayList;
import java.util.function.Function;

public class BasicEnv {
  // The basic environment
  public static Function<String, Object> env = (s) -> {
    switch (s) {
    // tryna check-expect
    case "check-expect":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        if (a.size() != 2) {
          throw new RuntimeException("check-expect expects 2 arguments, given " + a.size());
        }
        CheckExpect.testStack1.add(a.remove(0));
        CheckExpect.testStack2.add(a.remove(0));
        return null;
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
        if (output % 1 == 0) {
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
        if (output % 1 == 0) {
          return (int) output;
        }
        return output;
      };
    // tryna multiply together numbers
    case "*":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        double output = 0.0;
        for (Object o : a) {
          if (o instanceof Integer) {
            output *= (int) o;
          }
          else if (o instanceof Double) {
            output *= (double) o;
          }
          else {
            throw new RuntimeException("* must be given only numbers");
          }
        }
        if (output % 1 == 0) {
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
        if (output % 1 == 0) {
          return (int) output;
        }
        return output;
      };
    // function is not in the environment
    default:
      throw new RuntimeException("Unknown lookup: " + s);
    }
  };
}