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

  static Token tempToken = new Token(0, 0, 0, ""); // Needed to prevent skipping of characters '1.231'
  static int tempChar; // Also needed to prevent skipping of characters, like in '0main'

  // Start reading characters from the input file, and print out the tokens
  public static void main(String[] args) {
    try {
      if (args.length == 1) {
        input = new FileInputStream(args[0]);
        Token tkn;
        int tknCount = 0;

        while ((tkn = nextToken()) != null) {
          System.out.print("(" + tkn.line + "," + tkn.column + ")\t");
          if (tkn.kind == ID) {
            System.out.print("ID" + "(" + tkn.lexeme + ")" + "\n");
          } else if (tkn.kind == INTLIT) {
            try {
              System.out.print("INTLIT" + "(" + Integer.parseInt(tkn.lexeme) + ")" + "\n");
              // If we had a token directly after an int, we don't want to accidentally skip it
              if (tempToken.kind != 0) {
                if (tempToken.kind == ID) {
                  System.out.print("(" + tempToken.line + "," + tempToken.column + ")\t");
                  System.out.print("ID" + "(" + tempToken.lexeme + ")" + "\n");
                } else {
                  System.out.print("(" + tempToken.line + "," + tempToken.column + ")\t");
                  System.out.print((char)Integer.parseInt((tempToken.lexeme)) + "\n");
                }
                ++tknCount;
                tempToken.kind = 0;
                tempToken.line = 0;
                tempToken.column = 0;
                tempToken.lexeme = "";
              }
            } catch (NumberFormatException e) {
              throw new TokenMgrError("Integer overflow: " + tkn.lexeme, 0);
            }
          } else if (tkn.kind == STRLIT) {
                System.out.print("STRLIT" + "(" + tkn.lexeme + ")" + "\n");
          } else {
              System.out.print(tkn.lexeme + "\n");
              // If we had a token directly after an int, we don't want to accidentally skip it
              if (tempToken.kind != 0) {
                System.out.print("(" + tempToken.line + "," + tempToken.column + ")\t");
                System.out.print((char)Integer.parseInt((tempToken.lexeme)) + "\n");
                ++tknCount;
                tempToken.kind = 0;
                tempToken.line = 0;
                tempToken.column = 0;
                tempToken.lexeme = "";
              }
          }
          ++tknCount;
        }
        System.out.println("Total: " + tknCount + " tokens");
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
    if (c == '\n') {
      ++lineNum;
      colNum = 0;
    } else if (c == '/') {
    } else {
      ++colNum;
    }
    return c;
  }

  // Figure out what the next token is and return its code
  static Token nextToken() throws Exception {
    StringBuilder buffer = new StringBuilder();
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
        case '/':
          c = nextChar();
          if (c == '/') { // We're dealing with a single line comment
            do {
              c = nextChar();
            } while (c != '\n');
            continue;
          } else if (c == '*') { // We're dealing with a multiline comment
            buffer.setLength(0);

            // Load in the next two chars.
            // If they're not '*/' then we ignore anyway.
            do {
              c = nextChar();
              buffer.append((char) c);
            } while(buffer.length() < 2);

            // Once we hit '*/' we're done with the comment
            while(buffer.toString().charAt(buffer.length() - 2) != '*' ||
                  buffer.toString().charAt(buffer.length() -1) != '/') {

              if (c == -1) {  // EOF
                throw new LexError(lineNum, colNum, "Encountered <EOF> before termination of comment: " + buffer.toString());
              }
              c = nextChar();
              buffer.append((char) c);
            }
            c = nextChar();
            buffer.setLength(0);
            continue;
          } else { // This is the '/' operator
            int beginLine = lineNum;
            int beginColumn = colNum;
            return new Token(57, beginLine, beginColumn, "/");
          }
        default:

          if (isString(c)) {
            int beginLine = lineNum;
            int beginColumn = colNum;

            buffer.setLength(0);
            do {
              buffer.append((char) c);
              c = nextChar();
            } while (!isString(c) && c != -1);

            if (c == -1) {
              throw new LexError(lineNum, colNum, "Encountered <EOF> before termination of string: " + buffer.toString());
            }

            buffer.append((char) '"'); // We can do this since we know we hit another "
            // Check for string literals
            return new Token(STRLIT, beginLine, beginColumn, buffer.toString());
          }

          if (isDelimiter(c) || isOperator(c)) {
            int beginLine = lineNum;
            int beginColumn = colNum;
            int tempC;
            buffer.setLength(0);

            if ((c == '&') || (c == '|') || (c == '=')) {
                tempC = c;
                buffer.append((char) c);
                c = nextChar();
                if (c == tempC) {
                  buffer.append((char) c);
                } else if (isDelimiter(c) || isOperator(c)) {
                  tempToken = new Token(33, beginLine, beginColumn+1, Integer.toString(c));
                }
              //System.out.println(buffer.toString());
            } else if ((c == '>') || (c == '<') || (c == '!')) {
              buffer.append((char) c);
              c = nextChar();
              if (c == '=') {
                buffer.append((char) '=');
              } else if (isDelimiter(c) || isOperator(c)) {
                  tempToken = new Token(33, beginLine, beginColumn+1, Integer.toString(c));
              }
            } else {
              buffer.append((char) c);
            }

            // Next check for operators and delimiters
            if (buffer.toString().equals("+")) {
              return new Token(53, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("-")) {
              return new Token(55, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("*")) {
              return new Token(42, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("/")) {
              return new Token(47, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("&&")) {
              return new Token(AND, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("||")) {
              return new Token(OR, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("|")) {
              return new Token(124, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("!")) {
              return new Token(33, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("==")) {
              return new Token(EQ, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("!=")) {
              return new Token(NEQ, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("<")) {
              return new Token(60, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("<=")) {
              return new Token(LE, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals(">")) {
              return new Token(62, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals(">=")) {
              return new Token(GE, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("=")) {
              return new Token(61, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals(";")) {
              return new Token(59, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals(",")) {
              return new Token(44, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals(".")) {
              return new Token(46, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("(")) {
              return new Token(40, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals(")")) {
              return new Token(41, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("[")) {
              return new Token(91, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("]")) {
              return new Token(93, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("{")) {
              return new Token(123, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("}")) {
              return new Token(125, beginLine, beginColumn, buffer.toString());
            }
          }

          if (isLetter(c)) {
            int beginLine = lineNum;
            int beginColumn = colNum;
            buffer.setLength(0);

            // Needed for cases where a letter follows an int, i.e '0main'
            if (tempChar != 0) {
              buffer.append((char) tempChar);
              tempChar = 0;
            }

            do {
              buffer.append((char) c);
              c = nextChar();
            } while (isLetter(c) || isNumber(c));
            // Check if this string is a keyword.
            // We use an if conditional rather than a switch in case we need
            // to do a regex search
            if (buffer.toString().equals("class")) {
              return new Token(CLASS, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("extends")) {
              return new Token(EXTENDS, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("static")) {
              return new Token(STATIC, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("public")) {
              return new Token(PUBLIC, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("void")) {
              return new Token(VOID, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("int")) {
              return new Token(INT, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("boolean")) {
              return new Token(BOOLEAN, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("new")) {
              return new Token(NEW, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("if")) {
              return new Token(IF, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("else")) {
              return new Token(ELSE, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("while")) {
              return new Token(WHILE, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("return")) {
              return new Token(RETURN, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("main")) {
              return new Token(MAIN, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("true")) {
              return new Token(TRUE, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("false")) {
              return new Token(FALSE, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("String")) {
              return new Token(STRING, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("System")) {
              return new Token(SYSTEM, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("out")) {
              return new Token(OUT, beginLine, beginColumn, buffer.toString());
            } else if (buffer.toString().equals("println")) {
              return new Token(PRINTLN, beginLine, beginColumn, buffer.toString());
            }

            // Finally, default to returning token as an ID
            return new Token(ID, beginLine, beginColumn, buffer.toString());
          }

          if (isNumber(c)) {
            int beginLine = lineNum;
            int beginColumn = colNum;

            buffer.setLength(0);
            do {
              buffer.append((char) c);
              c = nextChar();
            } while (isNumber(c));

            // There are cases where another non-number follows a number
            if (isOperator(c) || isDelimiter(c)) {
              tempToken = new Token(33, beginLine, beginColumn+1, Integer.toString(c));
            } else if (isLetter(c)) {
              tempChar = c;
            }

            return new Token(INTLIT, beginLine, beginColumn, buffer.toString());
          }

        throw new LexError(lineNum, colNum, "Illegal char: " + (char)c);
      }
    }
  }

  private static boolean isLetter(int c) {
    return (('A' <= c) && (c <= 'Z') || ('a' <= c) && (c <= 'z'));
  }

  private static boolean isNumber(int c) {
    return ('0' <= c) && (c <= '9');
  }

  private static boolean isString(int c) {
    return (c == '"');
  }

  private static boolean isOperator(int c) {
    if (c == '+' || c == '-' || c == '*' || c == '/' || c == '&' ||
        c == '|' || c == '!' || c == '=' || c == '<' || c == '>') {
      return true;
    } else {
      return false;
    }
  }

  private static boolean isDelimiter(int c) {
    if (c == '=' || c == ';' || c == ',' || c == '.' || c == '(' ||
        c == ')' || c == '[' || c == ']' || c == '{' || c == '}') {
      return true;
    } else {
      return false;
    }
  }
}
