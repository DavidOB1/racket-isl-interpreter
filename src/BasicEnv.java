import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import javalib.worldimages.*;
import java.awt.Color;

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
    OUTLINE_MAP.put("'solid", OutlineMode.SOLID);
    OUTLINE_MAP.put("solid", OutlineMode.SOLID);
    OUTLINE_MAP.put("'outline", OutlineMode.OUTLINE);
    OUTLINE_MAP.put("outline", OutlineMode.OUTLINE);
  }
  
  private static final HashMap<String, AlignModeX> ALX_MAP;
  static {
    ALX_MAP = new HashMap<String, AlignModeX>();
    ALX_MAP.put("left", AlignModeX.LEFT);
    ALX_MAP.put("right", AlignModeX.RIGHT);
    ALX_MAP.put("center", AlignModeX.CENTER);
    ALX_MAP.put("'left", AlignModeX.LEFT);
    ALX_MAP.put("'right", AlignModeX.RIGHT);
    ALX_MAP.put("'center", AlignModeX.CENTER);
  }
  
  private static final HashMap<String, AlignModeY> ALY_MAP;
  static {
    ALY_MAP = new HashMap<String, AlignModeY>();
    ALY_MAP.put("top", AlignModeY.TOP);
    ALY_MAP.put("bottom", AlignModeY.BOTTOM);
    ALY_MAP.put("middle", AlignModeY.MIDDLE);
    ALY_MAP.put("'top", AlignModeY.TOP);
    ALY_MAP.put("'bottom", AlignModeY.BOTTOM);
    ALY_MAP.put("'middle", AlignModeY.MIDDLE);
  }
  
  private static Color getColor(String s) {
    Color color = IMG_COLOR.get(s.toLowerCase());
    if (color == null) {
      throw new RuntimeException("Unfortunately the image color " + s + " is not supported.");
    }
    return color;
  }
  
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
        if (!(a.get(0) instanceof String)) {
          throw new RuntimeException("error should recieve a string as an argument");
        }
        throw new RuntimeException((String) a.get(0));
      };
    // negation
    case "not":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        if (!(a.get(0) instanceof Boolean)) {
          throw new RuntimeException("not should recieve a boolean as an arguement");
        }
        return !(boolean)a.get(0);
      };
    // Creates a circle
    case "circle":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 3, a);
        int radius;
        if ((a.get(0) instanceof Integer) || (a.get(0) instanceof Double)) {
          radius = (int) a.get(0);
        }
        else {
          throw new RuntimeException("circle should be given a number as the first arg");
        }
        if (!((a.get(1) instanceof String) && (a.get(2) instanceof String))) {
          throw new RuntimeException("circle should be given Number String String");
        }
        return new CircleImage(radius, OUTLINE_MAP.get((String) a.get(1)),
            getColor((String) a.get(2)));
      };
   // Creates an ellipse
    case "ellipse":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 4, a);
        int width, height;
        if ((a.get(0) instanceof Integer) || (a.get(0) instanceof Double)
            && (a.get(1) instanceof Integer) || (a.get(1) instanceof Double)) {
          width = (int) a.get(0);
          height = (int) a.get(1);
        }
        else {
          throw new RuntimeException("ellipse should be given numbers as the first two args");
        }
        if (!((a.get(2) instanceof String) && (a.get(3) instanceof String))) {
          throw new RuntimeException("ellipse should be given Number Number String String");
        }
        return new EllipseImage(width, height, OUTLINE_MAP.get((String) a.get(2)),
            getColor((String) a.get(3)));
      };
    // Creates a line image
    case "line":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 3, a);
        int lineX, lineY;
        if ((a.get(0) instanceof Integer) || (a.get(0) instanceof Double)
            && (a.get(1) instanceof Integer) || (a.get(1) instanceof Double)) {
          lineX = (int) a.get(0);
          lineY = (int) a.get(1);
        }
        else {
          throw new RuntimeException("line should be given numbers as the first two args");
        }
        if (!(a.get(2) instanceof String)) {
          throw new RuntimeException("line should be given String as the last arg");
        }
        return new LineImage(new Posn(lineX, lineY), getColor((String) a.get(2)));
      };
    // Creates a text image
    case "text":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 3, a);
        int size;
        if (!((a.get(0) instanceof String) && (a.get(2) instanceof String))) {
          throw new RuntimeException("text should be given String Number String");
        }
        if ((a.get(1) instanceof Integer) || (a.get(1) instanceof Double)) {
          size = (int) a.get(1);
        }
        else {
          throw new RuntimeException("text should be given String Number String");
        }
        return new TextImage((String) a.get(0), size, getColor((String) a.get(2)));
      };
    // Creates an empty image
    case "empty-image":
      return new EmptyImage();
   // Creates a triangle
    case "triangle":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 3, a);
        if (!((a.get(0) instanceof Integer) || (a.get(0) instanceof Double)
            || ((int) a.get(0) < 0)) || !(a.get(1) instanceof String) 
            || !(a.get(2) instanceof String)) {
          throw new RuntimeException("triangle should be given PosNum String String");
        }
        int triSize = (int) a.get(0);
        String triType = (String) a.get(1);
        if (triType.substring(0, 1).equals("'")) {
          triType = triType.substring(1);
        }
        return new TriangleImage(new Posn(0, (int) Math.sqrt(3) * triSize), 
            new Posn(triSize / 2, 0), new Posn(triSize, (int) Math.sqrt(3) * triSize),
            triType, getColor((String) a.get(2)));
      };
    // Creates a right triangle
    case "right-triangle":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 4, a);
        if (!((a.get(0) instanceof Integer) || (a.get(0) instanceof Double)
            || ((int) a.get(0) < 0)) || !((a.get(1) instanceof Integer)
            || (a.get(1) instanceof Double) || ((int) a.get(1) < 0))
            || !(a.get(2) instanceof String) || !(a.get(3) instanceof String)) {
          throw new RuntimeException("right-triangle should be given PosNum PosNum String String");
        }
        int triSize1 = (int) a.get(0);
        int triSize2 = (int) a.get(1);
        String triType = (String) a.get(2);
        if (triType.substring(0, 1).equals("'")) {
          triType = triType.substring(1);
        }
        return new TriangleImage(new Posn(0, 0), new Posn(0, triSize2),
            new Posn(triSize1, triSize2), triType, getColor((String) a.get(2)));
      };
    // Creates a rectangle
    case "rectangle":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 4, a);
        if (!((a.get(0) instanceof Integer) || (a.get(0) instanceof Double)
            || ((int) a.get(0) < 0)) || !((a.get(1) instanceof Integer)
            || (a.get(1) instanceof Double) || ((int) a.get(1) < 0))
            || !(a.get(2) instanceof String) || !(a.get(3) instanceof String)) {
          throw new RuntimeException("rectangle should be given PosNum PosNum String String");
        }
        int w = (a.get(0) instanceof Double) ? (int) (double) a.get(0) : (int) a.get(0);
        int h = (a.get(0) instanceof Double) ? (int) (double) a.get(0) : (int) a.get(0);
        return new RectangleImage(w, h, 
            OUTLINE_MAP.get((String) a.get(2)), getColor((String) a.get(3)));
      };
    // Overlays the first image on top of the other
    case "overlay":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        WorldImage overlayOutput = new EmptyImage();
        for (Object o : a) {
          if (!(o instanceof WorldImage)) {
            throw new RuntimeException("overlay requires arguments of type image");
          }
          overlayOutput = new OverlayImage(overlayOutput, (WorldImage) o);
        }
        return overlayOutput;
      };
    // Overlays the images and aligns them
    case "overlay/align":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        WorldImage overlayAllignOutput = new EmptyImage();
        if (a.size() < 3) {
          throw new RuntimeException("overlay/align requires at least 3 arguments");
        }
        AlignModeX alX = ALX_MAP.get((String) a.remove(0));
        AlignModeY alY = ALY_MAP.get((String) a.remove(0));
        for (Object o : a) {
          if (!(o instanceof WorldImage)) {
            throw new RuntimeException("overlay/align requires arguments of type image");
          }
          overlayAllignOutput = new OverlayOffsetAlign(alX, alY, overlayAllignOutput, 
              0, 0, (WorldImage) o);
        }
        return overlayAllignOutput;
      };
    // Places all the images beside each other
    case "beside":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        WorldImage besideOutput = new EmptyImage();
        for (Object o : a) {
          if (!(o instanceof WorldImage)) {
            throw new RuntimeException("beside requires arguments of type image");
          }
          besideOutput = new BesideImage(besideOutput, (WorldImage) o);
        }
        return besideOutput;
      };
    // Places all the images above each other
    case "above":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        WorldImage aboveOutput = new EmptyImage();
        for (Object o : a) {
          if (!(o instanceof WorldImage)) {
            throw new RuntimeException("above requires arguments of type image");
          }
          aboveOutput = new AboveImage(aboveOutput, (WorldImage) o);
        }
        return aboveOutput;
      };
    // Places one of the images on top of the other
    case "place-image":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 4, a);
        if (!(a.get(0) instanceof WorldImage) || !(a.get(3) instanceof WorldImage)
            || !((a.get(1) instanceof Double) || (a.get(1) instanceof Integer))
            || !((a.get(2) instanceof Double) || (a.get(2) instanceof Integer))) {
          throw new RuntimeException("place-image should be given Image Number Number Image");
        }
        int x = (a.get(1) instanceof Double) ? (int) (double) a.get(1) : (int) a.get(1);
        int y = (a.get(2) instanceof Double) ? (int) (double) a.get(2) : (int) a.get(2);
        WorldImage top = (WorldImage) a.get(0);
        WorldImage bottom = (WorldImage) a.get(3);
        WorldImage overlayed = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
            top, top.getWidth() / 2 - x, top.getHeight() / 2 - y, bottom);
        int overlayW = (int) overlayed.getWidth();
        int bottomW = (int) bottom.getWidth();
        int topW = (int) top.getWidth();
        int overlayH = (int) overlayed.getHeight();
        int bottomH = (int) bottom.getHeight();
        int topH = (int) top.getHeight();
        if (overlayW == bottomW && overlayH == bottomH) {
          return overlayed;
        }
        int topX = Math.min(x - topW / 2, 0);
        int topY = Math.min(y - topH / 2, 0);
        return new CropImage(-topX, - topY, bottomW, bottomH, overlayed);
      };
    // returns the height of an image
    case "image-height":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        if (!(a.get(0) instanceof WorldImage)) {
          throw new RuntimeException("image-height expects args of type Image");
        }
        return ((WorldImage) a.get(0)).getHeight();
      };
    // returns the width of an image
    case "image-width":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        if (!(a.get(0) instanceof WorldImage)) {
          throw new RuntimeException("image-width expects args of type Image");
        }
        return ((WorldImage) a.get(0)).getWidth();
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
          return ((String) ((HashMap<String, Object>) a.get(0)).get(";type")).equals("mt");
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
          return ((String) ((HashMap<String, Object>) a.get(0)).get(";type")).equals("cons");
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
          String listType = (String) listObj.get(";type");
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
          String listType = (String) listObj.get(";type");
          mtList = listType.equals("mt");
          if (listType.equals("cons")) {
            return listObj.get("rest");
          }
        }
        catch (Exception e) {
          if (mtList) {
            throw new RuntimeException("Cannot call rest on an empty list");
          }
          throw new RuntimeException("rest must be given a cons list");
        }
        return null;
      };
    // Applies the given function to all items in the list
    case "apply":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        if (!((a.get(0) instanceof Function) && (a.get(1) instanceof HashMap))) {
          throw new RuntimeException("Apply should take in a function and a list");
        }
        ArrayList<Object> inputs = new ArrayList<Object>();
        HashMap<String, Object> curList = (HashMap<String, Object>) a.get(1);
        while (!curList.get(";type").equals("mt")) {
          if (!curList.get(";type").equals("cons")) {
            throw new RuntimeException("Apply expects a list, given " + curList.get(";type"));
          }
          inputs.add(curList.get("first"));
          curList = (HashMap<String, Object>) curList.get("rest");
        }
        return ((Function<ArrayList<Object>, Object>) a.get(0)).apply(inputs);
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
    // Checks if the first string is contained inside the second string
    case "string-contains?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 2, a);
        if (!((a.get(0) instanceof String) && (a.get(1) instanceof String))) {
          throw new RuntimeException("string-contains? must be given strings only");
        }
        return ((String) a.get(1)).contains((String) a.get(0));
      };
   // Capitalizes the string
    case "string-upcase":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        if (!(a.get(0) instanceof String)) {
          throw new RuntimeException("string-upcase must be given a string");
        }
        return ((String) a.get(0)).toUpperCase();
      };
   // Lowercases the string
    case "string-downcase":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 1, a);
        if (!(a.get(0) instanceof String)) {
          throw new RuntimeException("string-downcase must be given a string");
        }
        return ((String) a.get(0)).toLowerCase();
      };
   // String alphabet comparisons
    case "string<?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        if (a.size() < 2) {
          throw new RuntimeException("string<? should recieve at least two strings");
        }
        boolean boolOutput = true;
        if (!(a.get(0) instanceof String)) {
          throw new RuntimeException("string<? should be given only strings");
        }
        String prev = (String) a.get(0);
        for (int i = 1; i < a.size(); i++) {
          if (!(a.get(i) instanceof String)) {
            throw new RuntimeException("string<? should be given only strings");
          }
          String thisOne = (String) a.get(i);
          boolOutput &= prev.compareTo(thisOne) < 0;
          prev = thisOne;
        }
        return boolOutput;
      };
   // String alphabet comparisons
    case "string<=?":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        if (a.size() < 2) {
          throw new RuntimeException("string<=? should recieve at least two strings");
        }
        boolean boolOutput = true;
        if (!(a.get(0) instanceof String)) {
          throw new RuntimeException("string<=? should be given only strings");
        }
        String prev = (String) a.get(0);
        for (int i = 1; i < a.size(); i++) {
          if (!(a.get(i) instanceof String)) {
            throw new RuntimeException("string<=? should be given only strings");
          }
          String thisOne = (String) a.get(i);
          boolOutput &= prev.compareTo(thisOne) <= 0;
          prev = thisOne;
        }
        return boolOutput;
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
        if (a.size() < 1) {
          throw new RuntimeException("max expects at least 1 argument");
        }
        if (!(a.get(0) instanceof Integer) && !(a.get(0) instanceof Double)) {
          throw new RuntimeException("max should receive numbers as arguments");
        }
        double maxNum = (a.get(0) instanceof Double) ? (double) a.remove(0) :
          (double) (int) a.remove(0);
        for (Object o : a) {
          if (!(o instanceof Integer) && !(o instanceof Double)) {
            throw new RuntimeException("max should receive numbers as arguments");
          }
          maxNum = Math.max(maxNum, (o instanceof Double) ? (double) o : (double) (int) o);
        }
        if (maxNum % 1 < 0.001) {
          return (int) maxNum;
        }
        return maxNum;
      };
    // Returns the min value
    case "min":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        if (a.size() < 1) {
          throw new RuntimeException("min expects at least 1 argument");
        }
        if (!(a.get(0) instanceof Integer) && !(a.get(0) instanceof Double)) {
          throw new RuntimeException("min should receive numbers as arguments");
        }
        double minNum = (double) a.remove(0);
        for (Object o : a) {
          if (!(o instanceof Integer) && !(o instanceof Double)) {
            throw new RuntimeException("min should receive numbers as arguments");
          }
          minNum = Math.min(minNum, (double) o);
        }
        if (minNum % 1 < 0.001) {
          return (int) minNum;
        }
        return minNum;
      };
    // Getting the remainder of a number
    case "modulo":
      return (Function<ArrayList<Object>, Object>) (a) -> {
        invalidArgsCheck(s, 2, a);
        if (a.get(0) instanceof Integer) {
          int n1 = (int) a.get(0);
          if (a.get(1) instanceof Integer) {
            return n1 % ((int) a.get(1));
          }
          else if (a.get(1) instanceof Double) {
            return n1 % ((double) a.get(1));
          }
        }
        if (a.get(0) instanceof Double) {
          double n1 = (double) a.get(0);
          if (a.get(1) instanceof Integer) {
            return n1 % ((int) a.get(1));
          }
          else if (a.get(1) instanceof Double) {
            return n1 % ((double) a.get(1));
          }
        }
        throw new RuntimeException("modulo should be given args Number Number");
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
      throw new RuntimeException("Unknown variable: " + s);
    }
  };
}