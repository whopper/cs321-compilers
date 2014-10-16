//----------------------------------------------------------------------
// CS321 Assignment 1 (Fall 2014)
//
// miniJava Lexer2 (Manual Implementation)
//
// Will Hopper
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
  static PushbackReader input = null;

  // Line and column nums
  static int lineNum = 1;
  static int colNum  = 0;

  // Start reading characters from the input file, and print out the tokens
  public static void main(String[] args) {
    try {
      if (args.length == 1) {
        input = new PushbackReader(new FileReader(args[0]));
        Token tkn;
        int tknCount = 0;

        while ((tkn = nextToken()) != null) {
          System.out.print("(" + tkn.line + "," + tkn.column + ")\t");
          if (tkn.kind == ID) {
            System.out.print("ID" + "(" + tkn.lexeme + ")" + "\n");
          } else if (tkn.kind == INTLIT) {
            try {
              System.out.print("INTLIT" + "(" + Integer.parseInt(tkn.lexeme) + ")" + "\n");
            } catch (NumberFormatException e) {
              throw new TokenMgrError("Integer overflow: " + tkn.lexeme, 0);
            }
          } else if (tkn.kind == STRLIT) {
              System.out.print("STRLIT" + "(" + tkn.lexeme + ")" + "\n");
          } else {
              System.out.print(tkn.lexeme + "\n");
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
            ++colNum;
            return new Token(57, lineNum, colNum, "/");
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

            // We need to look ahead for these symbols
            if ((c == '&') || (c == '|') || (c == '=')) {
                tempC = c;
                buffer.append((char) c);
                c = nextChar();  // Check what the next character is
                if (c == tempC) {
                  buffer.append((char) c);
                } else {
                  // Put this character back into the stream for later lexing
                  --colNum;
                  input.unread(c);
                }
            } else if ((c == '>') || (c == '<') || (c == '!')) {
              // These can have an '=' afterwards
              buffer.append((char) c);
              c = nextChar();
              if (c == '=') {
                buffer.append((char) '=');
              } else {
                // Put this character back into the stream for later lexing
                --colNum;
                input.unread(c);
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

            do {
              buffer.append((char) c);
              c = nextChar();
            } while (isLetter(c) || isNumber(c));

            // Check for keywords
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
            } else if (buffer.toString().equals("this")) {
              return new Token(THIS, beginLine, beginColumn, buffer.toString());
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

            if (! isWhiteSpace(c)) {
              input.unread(c); // Put the char after the end of the int back
              colNum -= 1;
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

  private static boolean isWhiteSpace(int c) {
    if (c == '\n' || c == '\r' || c == '\t' || c == ' ') {
      return true;
    } else {
      return false;
    }
  }
}
