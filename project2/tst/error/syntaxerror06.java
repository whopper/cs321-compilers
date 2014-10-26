/*
 * Invalid statement syntax (multiple expressions for return)
*/
class Test {
  public static void main(String[] ignore) {
    int i = 1;
    return 2, 3;
  }
} 
