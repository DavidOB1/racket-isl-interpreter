import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import javalib.impworld.*;
import javalib.worldimages.*;

@SuppressWarnings("unchecked")
public class BigBang extends World {
  
  private Object ws;
  private Function<ArrayList<Object>, Object> toDraw, onTick, onKey, stopWhen, finalScene;
  private int frameWidth, frameHeight;
  
  // Ensures the key event is compatable with expected key values of the Java library
  private static String stringToKey(String ke) {
    switch (ke) {
      case "enter":
        return "\\r";
      case "tab":
        return "\\t";
      case "backspace":
        return "\\b";
      case "right-shift":
        return "rshift";
      case "right-control":
        return "rcontrol";
      default:
        return ke;
    }
  }
  
  // Constructor
  BigBang(Object worldState) {
    ws = worldState;
    toDraw = (a) -> {
      throw new RuntimeException("Cannot call big bang without passing in a to-draw function");
    };
    onTick = (a) -> {
      return a.get(0);
    };
    onKey = (a) -> {
      return a.get(0);
    };
    stopWhen = (a) -> {
      return false;
    };
    finalScene = toDraw;
  }
  
  // Initializes the to-draw function
  public void setToDraw(Object drawFunc) {
    if (!(drawFunc instanceof Function)) {
      throw new RuntimeException("to-draw requires a function");
    }
    if (finalScene.equals(toDraw)) {
      finalScene = (Function<ArrayList<Object>, Object>) drawFunc;
    }
    toDraw = (Function<ArrayList<Object>, Object>) drawFunc;
    WorldImage img = draw();
    frameWidth = (int) img.getWidth();
    frameHeight = (int) img.getHeight();
  }
  
  // Initializes the on-tick function
  public void setOnTick(Object tickFunc) {
    if (!(tickFunc instanceof Function)) {
      throw new RuntimeException("on-tck requires a function");
    }
    onTick = (Function<ArrayList<Object>, Object>) tickFunc;
  }
  
  // Initializes the on-key function
  public void setOnKey(Object keyFunc) {
    if (!(keyFunc instanceof Function)) {
      throw new RuntimeException("on-key requires a function");
    }
    onKey = (Function<ArrayList<Object>, Object>) keyFunc;
  }
  
  // Initalizes the stop-when function
  public void setStopWhen(Object stopFunc) {
    if (!(stopFunc instanceof Function)) {
      throw new RuntimeException("stop-when requires a function");
    }
    stopWhen = (Function<ArrayList<Object>, Object>) stopFunc;
  }
  
  // Initalizes the stop-when function with an end scene function
  public void setStopWhen(Object stopFunc, Object endDrawFunc) {
    setStopWhen(stopFunc);
    if (!(endDrawFunc instanceof Function)) {
      throw new RuntimeException("stop-when requires functions only");
    }
    finalScene = (Function<ArrayList<Object>, Object>) endDrawFunc;
  }
  
  // Draws the current world state
  public WorldImage draw() {
    return (WorldImage) toDraw.apply(new ArrayList<Object>(Arrays.asList(ws)));
  }
  
  // Draws the world scene for bigBang to use
  public WorldScene makeScene() {
    WorldScene output = new WorldScene(frameWidth, frameHeight);
    WorldImage img = draw();
    output.placeImageXY(img, frameWidth / 2, frameHeight / 2);
    return output;
  }
  
  // Updates the world state each tick
  public void onTick() {
    ws = onTick.apply(new ArrayList<Object>(Arrays.asList(ws)));
  }
  
  // Updates the world state on each key press
  public void onKeyEvent(String ke) {
    ws = onKey.apply(new ArrayList<Object>(Arrays.asList(ws, stringToKey(ke)))); 
  }
  
  // Updates whether to end the game or not
  public WorldEnd worldEnds() {
    boolean over = (boolean) stopWhen.apply(new ArrayList<Object>(Arrays.asList(ws)));
    if (over) {
      WorldImage finalImg = (WorldImage) finalScene.apply(new ArrayList<Object>(Arrays.asList(ws)));
      WorldScene scene = new WorldScene(frameWidth, frameHeight);
      scene.placeImageXY(finalImg, frameWidth / 2, frameHeight / 2);
      return new WorldEnd(true, scene);
    }
    else {
      return new WorldEnd(false, new WorldScene(1, 1));
    }
  }
}
