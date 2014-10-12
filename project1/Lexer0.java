//----------------------------------------------------------------------
// CS321 Assignment 1 (Fall 2014)
//
// miniJava Lexer2 (Manual Implementation)
//----------------------------------------------------------------------

import java.io.*;

public class Lexer2 implements mjTokenConstants {

  static class LexError extends Exception {
    int line;
    int column;
    public LexError(int line, int column, String errorMsg) {
      super("at line " + line + " column " + column + ": " + errorMsg);
      this.line = line; this.column = column;
    }

    static final int EOF = 0;
    static final int ID = 1;
  }

  static class Token {
    int kind;      // token code
    int line;      // line number of token's first char
    int column;    // column number of token's first char
    String lexeme; // token lexeme

    public Token(int k, int l, int c, String lex) {
      kind = k; line = l; column = c; lexeme = lex;
    }

    public String toString() {
      return lexeme;
    }
  }

  // Fine to read from
  static FileInputStream input = null;

  // Line and column nums
  static int lineNum = 1;
  static int colNum  = 0;

  // Start reading characters from the input file, and print out the tokens
  public static void main(String[] args) {
    try {
      if (args.length == 1) {
        input = new FileInputStream(args[0]);
        Token tkn;
        int tknCount = 0;

        while ((tkn = nextToken()) != null) {
          // For ID tokens
          System.out.println("(" + tkn.line + "," + tkn.column + ")\t"
              + tkn.kind + "(" + tkn.lexeme + ")");
        }
      } else {
        System.err.println("Input filename must be specified as argument\n");
      }
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  // Handles line and column number watching
  static int nextChar() throws Exception {
    int c = input.read();
    if (c == "\n") {
      ++linNum;
      colNum = 0;
    } else {
      ++colNum;
    }
    return c;
  }

  // Figure out what the next token is and return its code
  static Token nextToken() throws Exception {
    StringBuilder buffer = new StringBulder();
    int c = nextChar();

    for (;;) {
      switch(c) {
        case -1:
          return null;
        // The next cases skip all whitespace
        case ' ':
        case '\t':
        case '\n':
        case '\r':
          c = nextChar();
          continue;
        default:
          if (isLetter(c)) {
            int beginine = lineNum;
            int beginColumn = colNum;

            buffer.setLength(0);
            do {
              buffer.append((char) c);
              c = nextChar();
            } while (isLetter(c));
            return new Token(ID, beginLine, beginColumn, buffer.toString());
          }
          throw new LexError(lineNum, colNum, "Illegal char: " + (char)c);
      }
    }
  }

  private static boolean isLetter(int c) {
    return (('A' <= c) && (c <= 'Z') || ('a' <= c) && (c <= 'z'));
  }
}
