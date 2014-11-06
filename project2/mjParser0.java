/* Generated By:JavaCC: Do not edit this line. mjParser0.java */
import java.io.*;

public class mjParser0 implements mjParser0Constants {
  public static void main(String [] args) {
    try {
      if (args.length == 1) {
        FileInputStream stream = new FileInputStream(args[0]);
        new mjParser0(stream).Program();
        stream.close();
        System.out.println("Program's syntax is valid.");
      } else {
        System.out.println("Need a file name as command-line argument.");
      }
    } catch (TokenMgrError e) {
      System.err.println(e);
    } catch (Exception e) {
      System.err.println(e);
    }
  }

//
// PARSER SECTION ---------------------------------------------------------------
//

// Program -> {ClassDecl}
//
  static final public void Program() throws ParseException {
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 10:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      ClassDecl();
    }
    jj_consume_token(0);
  }

/* Original:
 * ClassDecl -> "class" <ID> ["extends" <ID>] "{" {VarDecl} {MethodDecl} "}"
*/
  static final public void ClassDecl() throws ParseException {
    jj_consume_token(10);
    jj_consume_token(ID);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 11:
      jj_consume_token(11);
      jj_consume_token(ID);
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
    jj_consume_token(57);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 15:
      case 16:
      case ID:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_2;
      }
      VarDecl();
    }
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 13:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_3;
      }
      MethodDecl();
    }
    jj_consume_token(58);
  }

/* Original:
 * MethodDecl -> "public" ExtType <ID> "(" [Param {"," Param}] ")" 
 *                 "{" {VarDecl} {Stmt} "}"
 *            |  "public" "static" "void" "main" "(" "String" "[" "]" <ID> ")"
 *                 "{" {VarDecl} {Stmt} "}" 
*/
  static final public void MethodDecl() throws ParseException {
    jj_consume_token(13);
    MethodDeclLeftFactor();
  }

  static final public void MethodDeclLeftFactor() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 14:
    case 15:
    case 16:
    case ID:
      ExtType();
      jj_consume_token(ID);
      jj_consume_token(53);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 15:
      case 16:
      case ID:
        Param();
        break;
      default:
        jj_la1[4] = jj_gen;
        ;
      }
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 51:
          ;
          break;
        default:
          jj_la1[5] = jj_gen;
          break label_4;
        }
        jj_consume_token(51);
        Param();
      }
      jj_consume_token(54);
      jj_consume_token(57);
      StmtOrVarDecl();
      jj_consume_token(58);
      break;
    case 12:
      jj_consume_token(12);
      jj_consume_token(14);
      jj_consume_token(23);
      jj_consume_token(53);
      jj_consume_token(24);
      jj_consume_token(55);
      jj_consume_token(56);
      jj_consume_token(ID);
      jj_consume_token(54);
      jj_consume_token(57);
      StmtOrVarDecl();
      jj_consume_token(58);
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void StmtOrVarDecl() throws ParseException {
    label_5:
    while (true) {
      if (jj_2_1(2)) {
        ;
      } else {
        break label_5;
      }
      VarDecl();
    }
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 18:
      case 19:
      case 21:
      case 22:
      case 27:
      case ID:
      case 57:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_6;
      }
      Stmt();
    }
  }

// Param -> Type <ID>
//
  static final public void Param() throws ParseException {
    Type();
    jj_consume_token(ID);
  }

// VarDecl -> Type <ID> ["=" InitExpr] ";"
//
  static final public void VarDecl() throws ParseException {
    Type();
    jj_consume_token(ID);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 49:
      jj_consume_token(49);
      InitExpr();
      break;
    default:
      jj_la1[8] = jj_gen;
      ;
    }
    jj_consume_token(50);
  }

// ExtType -> Type | "void"
//
  static final public void ExtType() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 15:
    case 16:
    case ID:
      Type();
      break;
    case 14:
      jj_consume_token(14);
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// Type -> BasicType
//      |  BasicType "[" "]"
//      |  <ID>
//
  static final public void Type() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 15:
    case 16:
      BasicType();
      TypeLeftFactor();
      break;
    case ID:
      jj_consume_token(ID);
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void TypeLeftFactor() throws ParseException {
    label_7:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 55:
        ;
        break;
      default:
        jj_la1[11] = jj_gen;
        break label_7;
      }
      jj_consume_token(55);
      jj_consume_token(56);
    }
  }

// BasicType -> "int" | "boolean"
//
  static final public void BasicType() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 15:
      jj_consume_token(15);
      break;
    case 16:
      jj_consume_token(16);
      break;
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// Stmt -> "{" {Stmt} "}"
//      |  ExtId "(" [Args] ")" ";"
//      |  Lvalue "=" InitExpr ";"
//      |  "if" "(" Expr ")" Stmt ["else" Stmt]
//      |  "while" "(" Expr ")" Stmt
//      |  "System" "." "out" "." "println" "(" [PrintArg] ")" ";"
//      |  "return" [Expr] ";"
//
  static final public void Stmt() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 57:
      jj_consume_token(57);
      label_8:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 18:
        case 19:
        case 21:
        case 22:
        case 27:
        case ID:
        case 57:
          ;
          break;
        default:
          jj_la1[13] = jj_gen;
          break label_8;
        }
        Stmt();
      }
      jj_consume_token(58);
      break;
    case 18:
    case ID:
      ExtId();
      if (jj_2_2(2)) {
        jj_consume_token(53);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 18:
        case 25:
        case 26:
        case INTLIT:
        case ID:
        case 37:
        case 42:
        case 53:
          Args();
          break;
        default:
          jj_la1[14] = jj_gen;
          ;
        }
        jj_consume_token(54);
        jj_consume_token(50);
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 49:
        case 55:
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case 55:
            jj_consume_token(55);
            Expr();
            jj_consume_token(56);
            break;
          default:
            jj_la1[15] = jj_gen;
            ;
          }
          jj_consume_token(49);
          InitExpr();
          jj_consume_token(50);
          break;
        default:
          jj_la1[16] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
      break;
    case 19:
      jj_consume_token(19);
      jj_consume_token(53);
      Expr();
      jj_consume_token(54);
      Stmt();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 20:
        jj_consume_token(20);
        Stmt();
        break;
      default:
        jj_la1[17] = jj_gen;
        ;
      }
      break;
    case 21:
      jj_consume_token(21);
      jj_consume_token(53);
      Expr();
      jj_consume_token(54);
      Stmt();
      break;
    case 27:
      jj_consume_token(27);
      jj_consume_token(52);
      jj_consume_token(28);
      jj_consume_token(52);
      jj_consume_token(29);
      jj_consume_token(53);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 18:
      case 25:
      case 26:
      case INTLIT:
      case STRLIT:
      case ID:
      case 37:
      case 42:
      case 53:
        PrintArg();
        break;
      default:
        jj_la1[18] = jj_gen;
        ;
      }
      jj_consume_token(54);
      jj_consume_token(50);
      break;
    case 22:
      jj_consume_token(22);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 18:
      case 25:
      case 26:
      case INTLIT:
      case ID:
      case 37:
      case 42:
      case 53:
        Expr();
        break;
      default:
        jj_la1[19] = jj_gen;
        ;
      }
      jj_consume_token(50);
      break;
    default:
      jj_la1[20] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// Args -> Expr {"," Expr}
//
  static final public void Args() throws ParseException {
    Expr();
    label_9:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 51:
        ;
        break;
      default:
        jj_la1[21] = jj_gen;
        break label_9;
      }
      jj_consume_token(51);
      Expr();
    }
  }

// PrintArg -> Expr | <STRLIT>
//
  static final public void PrintArg() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 18:
    case 25:
    case 26:
    case INTLIT:
    case ID:
    case 37:
    case 42:
    case 53:
      Expr();
      break;
    case STRLIT:
      jj_consume_token(STRLIT);
      break;
    default:
      jj_la1[22] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// InitExpr -> "new" BasicType "[" <INTLIT> "]"
//          |  "new" <ID> "(" ")"
//          |  Expr
//
  static final public void InitExpr() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 17:
      jj_consume_token(17);
      InitExprLeftFactor();
      break;
    case 18:
    case 25:
    case 26:
    case INTLIT:
    case ID:
    case 37:
    case 42:
    case 53:
      Expr();
      break;
    default:
      jj_la1[23] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void InitExprLeftFactor() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 15:
    case 16:
      BasicType();
      jj_consume_token(55);
      jj_consume_token(INTLIT);
      jj_consume_token(56);
      break;
    case ID:
      jj_consume_token(ID);
      jj_consume_token(53);
      jj_consume_token(54);
      break;
    default:
      jj_la1[24] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

/* Original:
 * Expr -> Expr BinOp Expr
 *      |  UnOp Expr
 *      |  "(" Expr ")"
 *      |  ExtId "(" [Args] ")"
 *      |  Lvalue
 *      |  Literal
*/
  static final public void Expr() throws ParseException {
    A();
  }

/*  Operator precedence functions
 *  Needed to remove ambiguity from Expr()
 */
  static final public void A() throws ParseException {
    B();
    label_10:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 41:
        ;
        break;
      default:
        jj_la1[25] = jj_gen;
        break label_10;
      }
      jj_consume_token(41);
      B();
    }
  }

  static final public void B() throws ParseException {
    C();
    label_11:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 40:
        ;
        break;
      default:
        jj_la1[26] = jj_gen;
        break label_11;
      }
      jj_consume_token(40);
      C();
    }
  }

  static final public void C() throws ParseException {
    D();
    label_12:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 43:
      case 44:
        ;
        break;
      default:
        jj_la1[27] = jj_gen;
        break label_12;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 43:
        jj_consume_token(43);
        break;
      case 44:
        jj_consume_token(44);
        break;
      default:
        jj_la1[28] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      D();
    }
  }

  static final public void D() throws ParseException {
    E();
    label_13:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 45:
      case 46:
      case 47:
      case 48:
        ;
        break;
      default:
        jj_la1[29] = jj_gen;
        break label_13;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 45:
        jj_consume_token(45);
        break;
      case 46:
        jj_consume_token(46);
        break;
      case 47:
        jj_consume_token(47);
        break;
      case 48:
        jj_consume_token(48);
        break;
      default:
        jj_la1[30] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      E();
    }
  }

  static final public void E() throws ParseException {
    F();
    label_14:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 36:
      case 37:
        ;
        break;
      default:
        jj_la1[31] = jj_gen;
        break label_14;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 36:
        jj_consume_token(36);
        break;
      case 37:
        jj_consume_token(37);
        break;
      default:
        jj_la1[32] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      F();
    }
  }

  static final public void F() throws ParseException {
    G();
    label_15:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 38:
      case 39:
        ;
        break;
      default:
        jj_la1[33] = jj_gen;
        break label_15;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 38:
        jj_consume_token(38);
        break;
      case 39:
        jj_consume_token(39);
        break;
      default:
        jj_la1[34] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      G();
    }
  }

  static final public void G() throws ParseException {
    label_16:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 37:
      case 42:
        ;
        break;
      default:
        jj_la1[35] = jj_gen;
        break label_16;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 37:
        jj_consume_token(37);
        break;
      case 42:
        jj_consume_token(42);
        break;
      default:
        jj_la1[36] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    H();
  }

  static final public void H() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 18:
    case ID:
      ExtId();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 53:
      case 55:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 53:
          jj_consume_token(53);
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case 18:
          case 25:
          case 26:
          case INTLIT:
          case ID:
          case 37:
          case 42:
          case 53:
            Args();
            break;
          default:
            jj_la1[37] = jj_gen;
            ;
          }
          jj_consume_token(54);
          break;
        case 55:
          jj_consume_token(55);
          Expr();
          jj_consume_token(56);
          break;
        default:
          jj_la1[38] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[39] = jj_gen;
        ;
      }
      break;
    case 53:
      jj_consume_token(53);
      Expr();
      jj_consume_token(54);
      break;
    case 25:
    case 26:
    case INTLIT:
      Literal();
      break;
    default:
      jj_la1[40] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// ExtId -> ["this" "."] <ID> {"." <ID>}
//
  static final public void ExtId() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 18:
      jj_consume_token(18);
      jj_consume_token(52);
      break;
    default:
      jj_la1[41] = jj_gen;
      ;
    }
    jj_consume_token(ID);
    label_17:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 52:
        ;
        break;
      default:
        jj_la1[42] = jj_gen;
        break label_17;
      }
      jj_consume_token(52);
      jj_consume_token(ID);
    }
  }

// Literal -> <INTLIT> | "true" | "false"
//
  static final public void Literal() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTLIT:
      jj_consume_token(INTLIT);
      break;
    case 25:
      jj_consume_token(25);
      break;
    case 26:
      jj_consume_token(26);
      break;
    default:
      jj_la1[43] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  static private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  static private boolean jj_3R_40() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(32)) {
    jj_scanpos = xsp;
    if (jj_scan_token(25)) {
    jj_scanpos = xsp;
    if (jj_scan_token(26)) return true;
    }
    }
    return false;
  }

  static private boolean jj_3_1() {
    if (jj_3R_18()) return true;
    return false;
  }

  static private boolean jj_3R_41() {
    if (jj_scan_token(18)) return true;
    return false;
  }

  static private boolean jj_3R_39() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_41()) jj_scanpos = xsp;
    if (jj_scan_token(ID)) return true;
    return false;
  }

  static private boolean jj_3R_24() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(15)) {
    jj_scanpos = xsp;
    if (jj_scan_token(16)) return true;
    }
    return false;
  }

  static private boolean jj_3R_19() {
    if (jj_3R_21()) return true;
    return false;
  }

  static private boolean jj_3R_34() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(37)) {
    jj_scanpos = xsp;
    if (jj_scan_token(42)) return true;
    }
    return false;
  }

  static private boolean jj_3R_33() {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_34()) { jj_scanpos = xsp; break; }
    }
    if (jj_3R_35()) return true;
    return false;
  }

  static private boolean jj_3R_32() {
    if (jj_3R_33()) return true;
    return false;
  }

  static private boolean jj_3R_27() {
    if (jj_scan_token(55)) return true;
    return false;
  }

  static private boolean jj_3R_31() {
    if (jj_3R_32()) return true;
    return false;
  }

  static private boolean jj_3R_38() {
    if (jj_3R_40()) return true;
    return false;
  }

  static private boolean jj_3R_25() {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_27()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  static private boolean jj_3R_30() {
    if (jj_3R_31()) return true;
    return false;
  }

  static private boolean jj_3R_37() {
    if (jj_scan_token(53)) return true;
    return false;
  }

  static private boolean jj_3R_29() {
    if (jj_3R_30()) return true;
    return false;
  }

  static private boolean jj_3R_36() {
    if (jj_3R_39()) return true;
    return false;
  }

  static private boolean jj_3R_35() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_36()) {
    jj_scanpos = xsp;
    if (jj_3R_37()) {
    jj_scanpos = xsp;
    if (jj_3R_38()) return true;
    }
    }
    return false;
  }

  static private boolean jj_3R_28() {
    if (jj_3R_29()) return true;
    return false;
  }

  static private boolean jj_3R_26() {
    if (jj_3R_28()) return true;
    return false;
  }

  static private boolean jj_3R_22() {
    if (jj_3R_24()) return true;
    if (jj_3R_25()) return true;
    return false;
  }

  static private boolean jj_3R_20() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_22()) {
    jj_scanpos = xsp;
    if (jj_scan_token(35)) return true;
    }
    return false;
  }

  static private boolean jj_3R_21() {
    if (jj_3R_23()) return true;
    return false;
  }

  static private boolean jj_3_2() {
    if (jj_scan_token(53)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_19()) jj_scanpos = xsp;
    if (jj_scan_token(54)) return true;
    return false;
  }

  static private boolean jj_3R_18() {
    if (jj_3R_20()) return true;
    if (jj_scan_token(ID)) return true;
    return false;
  }

  static private boolean jj_3R_23() {
    if (jj_3R_26()) return true;
    return false;
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public mjParser0TokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private Token jj_scanpos, jj_lastpos;
  static private int jj_la;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[44];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x400,0x800,0x18000,0x2000,0x18000,0x0,0x1d000,0x86c0000,0x0,0x1c000,0x18000,0x0,0x18000,0x86c0000,0x6040000,0x0,0x0,0x100000,0x6040000,0x6040000,0x86c0000,0x0,0x6040000,0x6060000,0x18000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x6040000,0x0,0x0,0x6040000,0x40000,0x0,0x6000000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x8,0x0,0x8,0x80000,0x8,0x2000008,0x20000,0x8,0x8,0x800000,0x0,0x2000008,0x200429,0x800000,0x820000,0x0,0x20042b,0x200429,0x2000008,0x80000,0x20042b,0x200429,0x8,0x200,0x100,0x1800,0x1800,0x1e000,0x1e000,0x30,0x30,0xc0,0xc0,0x420,0x420,0x200429,0xa00000,0xa00000,0x200009,0x0,0x100000,0x1,};
   }
  static final private JJCalls[] jj_2_rtns = new JJCalls[2];
  static private boolean jj_rescan = false;
  static private int jj_gc = 0;

  /** Constructor with InputStream. */
  public mjParser0(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public mjParser0(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new mjParser0TokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 44; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 44; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public mjParser0(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new mjParser0TokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 44; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 44; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public mjParser0(mjParser0TokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 44; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(mjParser0TokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 44; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  static final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  static private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;
  static private int[] jj_lasttokens = new int[100];
  static private int jj_endpos;

  static private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        exists = true;
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.add(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[59];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 44; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 59; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

  static private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 2; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  static private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
