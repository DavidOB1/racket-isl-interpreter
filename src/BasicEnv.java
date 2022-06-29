import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;
import javalib.worldimages.*;
import java.awt.Color;
import java.text.DecimalFormat;

public class BasicEnv {
  
  private static final HashMap<String, Color> IMG_COLOR;
  static {
    IMG_COLOR = new HashMap<String, Color>();
    IMG_COLOR.put("red", Color.red);
    IMG_COLOR.put("pink", Color.pink);
    IMG_COLOR.put("orange", Color.orange);
    IMG_COLOR.put("black", Color.black);
    IMG_COLOR.put("gray", Color.gray);
    IMG_COLOR.put("light gray", Color.LIGHT_GRAY);
    IMG_COLOR.put("dark gray", Color.DARK_GRAY);
    IMG_COLOR.put("lightgray", Color.LIGHT_GRAY);
    IMG_COLOR.put("darkgray", Color.DARK_GRAY);
    IMG_COLOR.put("magenta", Color.magenta);
    IMG_COLOR.put("purple", Color.MAGENTA);
    IMG_COLOR.put("blue", Color.blue);
    IMG_COLOR.put("cyan", Color.cyan);
    IMG_COLOR.put("green", Color.green);
    IMG_COLOR.put("white", Color.white);
    IMG_COLOR.put("yellow", Color.yellow);
  }
  
  private static final HashMap<String, OutlineMode> OUTLINE_MAP;
  static {
    OUTLINE_MAP = new HashMap<String, OutlineMode>();
    OUTLINE_MAP.put("solid", OutlineMode.SOLID);
    OUTLINE_MAP.put("outline", OutlineMode.OUTLINE);
  }
  
  private static final HashMap<String, AlignModeX> ALX_MAP;
  static {
    ALX_MAP = new HashMap<String, AlignModeX>();
    ALX_MAP.put("left", AlignModeX.LEFT);
    ALX_MAP.put("right", AlignModeX.RIGHT);
    ALX_MAP.put("center", AlignModeX.CENTER);
    ALX_MAP.put("middle", AlignModeX.CENTER);
  }
  
  private static final HashMap<String, AlignModeY> ALY_MAP;
  static {
    ALY_MAP = new HashMap<String, AlignModeY>();
    ALY_MAP.put("top", AlignModeY.TOP);
    ALY_MAP.put("bottom", AlignModeY.BOTTOM);
    ALY_MAP.put("middle", AlignModeY.MIDDLE);
    ALY_MAP.put("center", AlignModeY.MIDDLE);
  }
  
  private static Color getColor(String s) {
    Color color = IMG_COLOR.get(s.toLowerCase());
    if (color == null) {
      throw new RuntimeException("Unfortunately the image color " + s + " is not supported.");
    }
    return color;
  }
  
  private static void invalidArgsCheck(String f, int intendedArgs, ArrayList<Object> args) {
    if (intendedArgs != args.size()) {
      throw new RuntimeException(f + " expects " + intendedArgs 
          + " arguments, given " + args.size());
    }
  }
  
  private static void minArgsCheck(String f, int minArgs, ArrayList<Object> args) {
    if (minArgs > args.size()) {
      throw new RuntimeException(f + " expects at least " + minArgs
          + " arguments, given " + args.size());
    }
  }
  
  private static double returnDouble(String f, Object o) {
    if (o instanceof Integer) {
      return Double.valueOf((int) o);
    }
    if (o instanceof Double) {
      return (double) o;
    }
    throw new RuntimeException(f + "expects a number, given " + o.getClass());
  }
  
  private static int returnInt(String f, Object o) {
    if (o instanceof Integer) {
      return (int) o;
    }
    if (o instanceof Double) {
      return (int) Math.round((double) o);
    }
    throw new RuntimeException(f + " expects a number, given " + o.getClass());
  }
  
  
  private static int strictlyPos(String f, Object o) {
    if (o instanceof Integer) {
      int output = (int) o;
      if (output >= 0) {
        return output;
      }
    }
    if (o instanceof Double) {
      double output = (double) o;
      if (output >= 0) {
        return (int) Math.round(output);
      }
    }
    throw new RuntimeException(f + " expects a positive number, given " + o.getClass());
  } 
  
  private static int strictlyPosInt(String f, Object o) {
    if (o instanceof Integer) {
      int output = (int) o;
      if (output >= 0) {
        return output;
      }
    }
    throw new RuntimeException(f + " expects a non-negative integer, given " + o.getClass());
  }
  
  private static String returnString(String f, Object o) {
    if (o instanceof String) {
      String stringOut = (String) o;
      if (stringOut.length() > 0 && stringOut.substring(0, 1).equals("'")) {
        throw new RuntimeException(f + " expects a String, given a symbol");
      }
      return stringOut;
    }
    throw new RuntimeException(f + "expects a String, given " + o.getClass());
  } 
  
  private static boolean returnBool(String f, Object o) {
    if (o instanceof Boolean) {
      return (boolean) o;
    }
    throw new RuntimeException(f + " expects a boolean, given " + o.getClass());
  }
  
  @SuppressWarnings("unchecked")
  private static Function<ArrayList<Object>, Object> returnFunc(String f, Object o) {
    if (o instanceof Function) {
      return (Function<ArrayList<Object>, Object>) o;
    }
    throw new RuntimeException(f + "expects a function, given " + o.getClass());
  }
  
  @SuppressWarnings("unchecked")
  private static HashMap<String, Object> returnStruct(String f, Object o) {
    if (o instanceof HashMap) {
      return (HashMap<String, Object>) o;
    }
    throw new RuntimeException(f + "expects one of (list or struct), given " + o.getClass());
  }
  
  private static HashMap<String, Object> returnList(String f, Object o, boolean consOnly) {
    HashMap<String, Object> lst = returnStruct(f, o);
    String type = (String) lst.get(";type");
    if (type.equals("cons")) {
      return lst;
    }
    if (type.equals("mt")) {
      if (consOnly) {
        throw new RuntimeException(f + " expects a cons list, given an empty list");
      }
      return lst;
    }
    throw new RuntimeException(f + " expects a list, given " + type);
  }
  
  private static WorldImage returnImage(String f, Object o) {
    if (o instanceof WorldImage) {
      return (WorldImage) o;
    }
    throw new RuntimeException(f + " expects an image, given " + o.getClass());
  }
  
  private static Object numVal(double n) {
    if (n % 1 <= 0.000000001 || n % 1 > 0.99999999) {
      return (int) Math.round(n);
    }
    DecimalFormat df = new DecimalFormat("#.##########");
    return Double.valueOf(df.format(n));
  }
  
  
  // The basic environment
  @SuppressWarnings("unchecked")
  public static Function<String, Object> env = (s) -> {
    switch (s) {
    // checks the equality of two items
    case "equal?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 2, a);
        return a.get(0).equals(a.get(1));
      };
    // throwing an error
    case "error":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        String message = returnString(s, a.get(0));
        throw new RuntimeException(message);
      };
    // negation
    case "not":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        return !returnBool(s, a.get(0));
      };
    // Creates a circle
    case "circle":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 3, a);
        int radius = strictlyPos(s, a.get(0));
        String outline = returnString(s, a.get(1));
        String color = returnString(s, a.get(2));
        return new CircleImage(radius, OUTLINE_MAP.get(outline),
            getColor(color));
      };
   // Creates an ellipse
    case "ellipse":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 4, a);
        int width = strictlyPos(s, a.get(0));
        int height = strictlyPos(s, a.get(1));
        String outline = returnString(s, a.get(2));
        String color = returnString(s, a.get(3)); 
        return new EllipseImage(width, height, OUTLINE_MAP.get(outline),
            getColor(color));
      };
    // Creates a line image
    case "line":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 3, a);
        int lineX = returnInt(s, a.get(0));
        int lineY = returnInt(s, a.get(1));
        String color = returnString(s, a.get(2));
        return new LineImage(new Posn(lineX, lineY), getColor(color));
      };
    // Creates a text image
    case "text":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 3, a);
        String text = returnString(s, a.get(0));
        int size = strictlyPos(s, a.get(1));
        String color = returnString(s, a.get(2));
        return new TextImage(text, size, getColor(color));
      };
    // Creates an empty image
    case "empty-image":
      return new EmptyImage();
   // Creates a triangle
    case "triangle":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 3, a);
        int triSize = strictlyPos(s, a.get(0));
        String triType = returnString(s, a.get(1));
        String color = returnString(s, a.get(2));
        return new TriangleImage(new Posn(0, (int) Math.sqrt(3) * triSize), 
            new Posn(triSize / 2, 0), new Posn(triSize, (int) Math.sqrt(3) * triSize),
            triType, getColor(color));
      };
    // Creates a right triangle
    case "right-triangle":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 4, a);
        int triSize1 = strictlyPos(s, a.get(0));
        int triSize2 = strictlyPos(s, a.get(1));
        String triType = returnString(s, a.get(2));
        String color = returnString(s, a.get(3));
        return new TriangleImage(new Posn(0, 0), new Posn(0, triSize2),
            new Posn(triSize1, triSize2), triType, getColor(color));
      };
    // Creates a rectangle
    case "rectangle":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 4, a);
        int w = strictlyPos(s, a.get(0));
        int h = strictlyPos(s, a.get(1));
        String outline = returnString(s, a.get(2));
        String color = returnString(s, a.get(3));
        return new RectangleImage(w, h, OUTLINE_MAP.get(outline), getColor(color));
      };
    // Overlays the first image on top of the other
    case "overlay":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        WorldImage overlayOutput = new EmptyImage();
        for (Object o : a) {
          overlayOutput = new OverlayImage(overlayOutput, returnImage(s, o));
        }
        return overlayOutput;
      };
    // Overlays the images and aligns them
    case "overlay/align":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 3, a);
        WorldImage overlayAllignOutput = new EmptyImage();
        AlignModeX alX = ALX_MAP.get(returnString(s, a.remove(0)));
        AlignModeY alY = ALY_MAP.get(returnString(s, a.remove(0)));
        for (Object o : a) {
          overlayAllignOutput = new OverlayOffsetAlign(alX, alY, overlayAllignOutput, 
              0, 0, returnImage(s, o));
        }
        return overlayAllignOutput;
      };
    // Places all the images beside each other
    case "beside":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        WorldImage besideOutput = new EmptyImage();
        for (Object o : a) {
          besideOutput = new BesideImage(besideOutput, returnImage(s, o));
        }
        return besideOutput;
      };
    // Places all the images above each other
    case "above":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        WorldImage aboveOutput = new EmptyImage();
        for (Object o : a) {
          aboveOutput = new AboveImage(aboveOutput, returnImage(s, o));
        }
        return aboveOutput;
      };
    // Places one of the images on top of the other
    case "place-image":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 4, a);
        WorldImage top = returnImage(s, a.get(0));
        int x = returnInt(s, a.get(1));
        int y = returnInt(s, a.get(2));
        WorldImage bottom = returnImage(s, a.get(3));
        WorldImage overlayed = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
            top, top.getWidth() / 2 - x, top.getHeight() / 2 - y, bottom);
        int overlayW = (int) overlayed.getWidth();
        int bottomW = (int) bottom.getWidth();
        int bottomH = (int) bottom.getHeight();
        int overlayH = (int) overlayed.getHeight();
        if (overlayW == bottomW && overlayH == bottomH) {
          return overlayed;
        }
        int topW = (int) top.getWidth();
        int topH = (int) top.getHeight();
        int topX = Math.min(x - topW / 2, 0);
        int topY = Math.min(y - topH / 2, 0);
        return new CropImage(-topX, - topY, bottomW, bottomH, overlayed);
      };
    // returns the height of an image
    case "image-height":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        return returnImage(s, a.get(0)).getHeight();
      };
    // returns the width of an image
    case "image-width":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        return returnImage(s, a.get(0)).getWidth();
      };
    // Determines if the input is a boolean or not
    case "boolean?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        return (a.get(0) instanceof Boolean);
      };
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
      mtOutput.put(";type", "mt");
      return mtOutput;
    // determining if list is empty
    case "empty?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        try {
          return returnList(s, a.get(0), false).get(";type").equals("mt");
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
          returnList(s, a.get(0), true);
          return true;
        }
        catch (Exception e) {
          return false;
        }
      };
   // retrieving first element from a list
    case "first":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        HashMap<String, Object> lst = returnList(s, a.get(0), true);
        return lst.get("first");
      };
   // retrieving rest from a list
    case "rest":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        HashMap<String, Object> lst = returnList(s, a.get(0), true);
        return lst.get("rest");
      };
    // Applies the given function to all items in the list
    case "apply":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        Function<ArrayList<Object>, Object> func = returnFunc(s, a.get(0));
        HashMap<String, Object> curList = returnList(s, a.get(1), false);
        ArrayList<Object> inputs = new ArrayList<Object>();
        while (!curList.get(";type").equals("mt")) {
          if (!curList.get(";type").equals("cons")) {
            throw new RuntimeException("Apply expects a list, given " + curList.get(";type"));
          }
          inputs.add(curList.get("first"));
          curList = (HashMap<String, Object>) curList.get("rest");
        }
        return func.apply(inputs);
      };
    // tryna compare string equality
    case "string=?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 2, a);
        return returnString(s, a.get(0)).equals(returnString(s, a.get(1)));
      };
    // tryna check if object is a string
    case "string?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        try {
          returnString(s, a.get(0));
          return true;
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
        String str = returnString(s, a.remove(0));
        if (a.size() == 1) {
          a.add(str.length());
        }
        return str.substring(strictlyPosInt(s, a.get(0)), strictlyPosInt(s, a.get(1)));
      };
    case "string-length":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        return returnString(s, a.get(0)).length();
      };
    // tryna string append
    case "string-append":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        String output = "";
        for (Object o : a) {
          output += returnString(s, o);
        }
        return output;
      };
    // Checks if the first string is contained inside the second string
    case "string-contains?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 2, a);
        return returnString(s, a.get(1)).contains(returnString(s, a.get(0)));
      };
   // Capitalizes the string
    case "string-upcase":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        return returnString(s, a.get(0)).toUpperCase();
      };
   // Lowercases the string
    case "string-downcase":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        return returnString(s, a.get(0)).toLowerCase();
      };
   // String alphabet comparisons
    case "string<?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 2, a);
        String prev = returnString(s, a.remove(0));
        for (Object o : a) {
          String thisOne = returnString(s, o);
          if (prev.compareTo(thisOne) >= 0) {
            return false;
          }
          prev = thisOne;
        }
        return true;
      };
    // String alphabet comparisons
    case "string<=?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 2, a);
        String prev = returnString(s, a.remove(0));
        for (Object o : a) {
          String thisOne = returnString(s, o);
          if (prev.compareTo(thisOne) > 0) {
            return false;
          }
          prev = thisOne;
        }
        return true;
      };
    // String alphabet comparisons
    case "string>?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 2, a);
        String prev = returnString(s, a.remove(0));
        for (Object o : a) {
          String thisOne = returnString(s, o);
          if (prev.compareTo(thisOne) <= 0) {
            return false;
          }
          prev = thisOne;
        }
        return true;
      };
     // String alphabet comparisons
    case "string>=?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 2, a);
        String prev = returnString(s, a.remove(0));
        for (Object o : a) {
          String thisOne = returnString(s, o);
          if (prev.compareTo(thisOne) < 0) {
            return false;
          }
          prev = thisOne;
        }
        return true;
      };
    // Converts a number to a string
    case "number->string":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        Object o1 = a.get(0);
        if (o1 instanceof Integer) {
          return String.valueOf((int) o1);
        }
        else if (o1 instanceof Double) {
          return String.valueOf((double) o1);
        }
        else {
          throw new RuntimeException("number->string must be given a number");
        }
      };
    // Checks if a given string is numeric
    case "string-numeric?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        String numericStr = returnString(s, a.get(0));
        try {
          Integer.valueOf(numericStr);
          return true;
        }
        catch (Exception e) {
          try {
            Double.valueOf(numericStr);
            return true;
          }
          catch (Exception ex) {
            return false;
          }
        }
      };
    // Converts a string to a number
    case "string->number":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        String in = returnString(s, a.get(0));
        try {
          int intStr = Integer.valueOf(in);
          return intStr;
        }
        catch (Exception e) {
          try {
            double doubleStr = Double.valueOf(in);
            return doubleStr;
          }
          catch (Exception ex) {
            throw new RuntimeException("string-numeric expects a numeric string, given " + in);
          }
        }
      };
    // tryna check if arg is a number
    case "number?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1 ,a);
        return (a.get(0) instanceof Integer) || (a.get(0) instanceof Double);
      };
    // Checking if an arg is an integer
    case "integer?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        return (a.get(0) instanceof Integer);
      };
    // Returns the max value
    case "max":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        double maxNum = returnDouble(s, a.remove(0));
        for (Object o : a) {
          maxNum = Math.max(maxNum, returnDouble(s, o));
        }
        return numVal(maxNum);
      };
    // Returns the min value
    case "min":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        double maxNum = returnDouble(s, a.remove(0));
        for (Object o : a) {
          maxNum = Math.min(maxNum, returnDouble(s, o));
        }
        return numVal(maxNum);
      };
    // Returns a random number within the range 0 to n
    case "random":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        return new Random().nextInt(strictlyPosInt(s, a.get(0)));
      };
    // Getting the remainder of a number
    case "modulo":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 2, a);
        double n1 = returnDouble(s, a.get(0));
        double n2 = returnDouble(s, a.get(1));
        return numVal(n1 % n2);
      };
    // tryna add together numbers
    case "+":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        double output = 0.0;
        for (Object o : a) {
          output += returnDouble(s, o);
        }
        return numVal(output);
      };
    // tryna subtract together numbers
    case "-":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        double output = returnDouble(s, a.remove(0));
        for (Object o : a) {
          output -= returnDouble(s, o);
        }
        return numVal(output);
      };
    // tryna multiply together numbers
    case "*":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        double output = 1.0;
        for (Object o : a) {
          output *= returnDouble(s, o);
        }
        return numVal(output);
      };
    // tryna divide together numbers
    case "/":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        double output = returnDouble(s, a.remove(0));
        for (Object o : a) {
          output /= returnDouble(s, o);
        }
        return numVal(output);
      };
   // checking number equality
    case "=":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        double num = returnDouble(s, a.remove(0));
        for (Object o : a) {
          if (returnDouble(s, o) != num) {
            return false;
          }
        }
        return true;
      };
   // tryna check if greater than
    case ">":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        double prev = returnDouble(s, a.remove(0));
        for (Object o : a) {
          double cur = returnDouble(s, o);
          if (prev <= cur) {
            return false;
          }
          prev = cur;
        }
        return true;
      };
   // tryna check if less than
    case "<":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        double prev = returnDouble(s, a.remove(0));
        for (Object o : a) {
          double cur = returnDouble(s, o);
          if (prev >= cur) {
            return false;
          }
          prev = cur;
        }
        return true;
      };
   // tryna check if greater than or equal to
    case ">=":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        double prev = returnDouble(s, a.remove(0));
        for (Object o : a) {
          double cur = returnDouble(s, o);
          if (prev < cur) {
            return false;
          }
          prev = cur;
        }
        return true;
      };
   // tryna check if less than or equal to
    case "<=":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        minArgsCheck(s, 1, a);
        double prev = returnDouble(s, a.remove(0));
        for (Object o : a) {
          double cur = returnDouble(s, o);
          if (prev > cur) {
            return false;
          }
          prev = cur;
        }
        return true;
      };
    // function is not in the environment
    default:
      throw new RuntimeException("Unknown variable: " + s);
    }
  };
}