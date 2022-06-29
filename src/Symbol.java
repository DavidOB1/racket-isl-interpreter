// Represents a symbol in Racket
public class Symbol {
  
  public String symbol;
  
  // Constructor
  Symbol(String s) {
    if (s.length() > 0 && s.substring(0, 1).equals("'")) {
      symbol = s;
    }
    else {
      throw new RuntimeException("symbol should recieve a symbol, but got " + s + " instead.");
    }
  }
  
  // Returns the value of the symbol as a string
  public String value() {
    return symbol;
  }
}