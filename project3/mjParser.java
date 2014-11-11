/* Generated By:JavaCC: Do not edit this line. mjParser.java */
import java.util.*;
import java.io.*;
import ast.*;

public class mjParser implements mjParserConstants {
  public static void main(String [] args) {
    try {
      if (args.length == 1) {
        FileInputStream stream = new FileInputStream(args[0]);
        Ast.Program p = new mjParser(stream).Program();
        stream.close();
        System.out.print(p);
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
  static final public Ast.Program Program() throws ParseException {
  List<Ast.ClassDecl> cl = new ArrayList<Ast.ClassDecl>();
  Ast.ClassDecl c;
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
      c = ClassDecl();
                   cl.add(c);
    }
    jj_consume_token(0);
    {if (true) return new Ast.Program(cl);}
    throw new Error("Missing return statement in function");
  }

/*
 * ClassDecl -> "class" <ID> ["extends" <ID>] "{" {VarDecl} {MethodDecl} "}"
 * (String classname, String parentclass, list of VarDecls, list of MethodDecls)
*/
  static final public Ast.ClassDecl ClassDecl() throws ParseException {
  Token tkn;
  String cn;
  String pc="";
  List<Ast.VarDecl> vdl = new ArrayList<Ast.VarDecl>();
  List<Ast.MethodDecl> mdl = new ArrayList<Ast.MethodDecl>();
  Ast.VarDecl v;
  Ast.MethodDecl m;
    jj_consume_token(10);
    tkn = jj_consume_token(ID);
                     cn=new String(tkn.image);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 11:
      jj_consume_token(11);
      tkn = jj_consume_token(ID);
                                                                      pc=new String(tkn.image);
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
      v = VarDecl();
                         vdl.add(v);
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
      m = MethodDecl();
                            mdl.add(m);
    }
    jj_consume_token(58);
    {if (true) return new Ast.ClassDecl(cn, pc, vdl, mdl);}
    throw new Error("Missing return statement in function");
  }

/* Original:
 * MethodDecl -> "public" ExtType <ID> "(" [Param {"," Param}] ")"
 *                 "{" {VarDecl} {Stmt} "}"
 *            |  "public" "static" "void" "main" "(" "String" "[" "]" <ID> ")"
 *                 "{" {VarDecl} {Stmt} "}"
 * (Type returntype, String name, list of params, list of VarDecls, list of Stmts)
*/
  static final public Ast.MethodDecl MethodDecl() throws ParseException {
  Token tkn;
  Ast.Type t=null;
  Ast.Param p;
  Ast.VarDecl v;
  Ast.Stmt s;
  List<Ast.Param> plist = new ArrayList<Ast.Param>();
  List<Ast.VarDecl> vlist = new ArrayList<Ast.VarDecl>();
  List<Ast.Stmt> slist = new ArrayList<Ast.Stmt>();
    jj_consume_token(13);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 14:
    case 15:
    case 16:
    case ID:
      t = ExtType();
      tkn = jj_consume_token(ID);
      jj_consume_token(53);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 15:
      case 16:
      case ID:
        p = Param();
                                                plist.add(p);
        label_4:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case 51:
            ;
            break;
          default:
            jj_la1[4] = jj_gen;
            break label_4;
          }
          jj_consume_token(51);
          p = Param();
                                                                               plist.add(p);
        }
        break;
      default:
        jj_la1[5] = jj_gen;
        ;
      }
      jj_consume_token(54);
      break;
    case 12:
      jj_consume_token(12);
      jj_consume_token(14);
      tkn = jj_consume_token(23);
      jj_consume_token(53);
      jj_consume_token(24);
      jj_consume_token(55);
      jj_consume_token(56);
      jj_consume_token(ID);
      jj_consume_token(54);
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(57);
    label_5:
    while (true) {
      if (jj_2_1(2)) {
        ;
      } else {
        break label_5;
      }
      v = VarDecl();
                                       vlist.add(v);
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
      s = Stmt();
                                                                   slist.add(s);
    }
    jj_consume_token(58);
            {if (true) return new Ast.MethodDecl(t, tkn.image, plist, vlist, slist);}
    throw new Error("Missing return statement in function");
  }

// Param -> Type <ID>
// (Type type, String name)
  static final public Ast.Param Param() throws ParseException {
  Token tkn;
  Ast.Type t;
    t = Type();
    tkn = jj_consume_token(ID);
    {if (true) return new Ast.Param(t, tkn.image);}
    throw new Error("Missing return statement in function");
  }

// VarDecl -> Type <ID> ["=" InitExpr] ";"
// (Type type, String name, Exp initExpr (possibly null))
  static final public Ast.VarDecl VarDecl() throws ParseException {
  Token tkn;
  Ast.Type t;
  Ast.Exp initExp=null;
    t = Type();
    tkn = jj_consume_token(ID);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 49:
      jj_consume_token(49);
      initExp = InitExpr();
      break;
    default:
      jj_la1[8] = jj_gen;
      ;
    }
    jj_consume_token(50);
    {if (true) return new Ast.VarDecl(t, tkn.image, initExp);}
    throw new Error("Missing return statement in function");
  }

// ExtType -> Type | "void"
//
  static final public Ast.Type ExtType() throws ParseException {
                     Ast.Type t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 15:
    case 16:
    case ID:
      t = Type();
             {if (true) return t;}
      break;
    case 14:
      jj_consume_token(14);
             {if (true) return null;}
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

// Type -> BasicType
//      |  BasicType "[" "]"
//      |  <ID>
//
  static final public Ast.Type Type() throws ParseException {
                  Ast.Type t; Token tkn; boolean isArray=false;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 15:
    case 16:
      t = BasicType();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 55:
        jj_consume_token(55);
        jj_consume_token(56);
                            isArray = true;
        break;
      default:
        jj_la1[10] = jj_gen;
        ;
      }
        if(isArray == true) {
          {if (true) return new Ast.ArrayType(t);}
        } else {
          {if (true) return t;}
        }
      break;
    case ID:
      tkn = jj_consume_token(ID);
                {if (true) return new Ast.ObjType(tkn.image);}
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

// BasicType -> "int" | "boolean"
//
  static final public Ast.Type BasicType() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 15:
      jj_consume_token(15);
              {if (true) return new Ast.IntType();}
      break;
    case 16:
      jj_consume_token(16);
              {if (true) return new Ast.BoolType();}
      break;
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

// Stmt -> "{" {Stmt} "}"
//      |  ExtId "(" [Args] ")" ";"
//      |  Lvalue "=" InitExpr ";"
//      |  "if" "(" Expr ")" Stmt ["else" Stmt]
//      |  "while" "(" Expr ")" Stmt
//      |  "System" "." "out" "." "println" "(" [PrintArg] ")" ";"
//      |  "return" [Expr] ";"
//
  static final public Ast.Stmt Stmt() throws ParseException {
  Ast.Type t;
  List<Ast.Param> plist = new ArrayList<Ast.Param>();
  Ast.Param p;
  List<Ast.VarDecl> vlist = new ArrayList<Ast.VarDecl>();
  Ast.VarDecl v;
  List<Ast.Stmt> slist = new ArrayList<Ast.Stmt>();
  Ast.Stmt s1;
  Ast.Stmt s2=null;
  Ast.Stmt finalStmt;

  Ast.Exp eLeft;
  Ast.Exp e1=null;
  Ast.Exp e2=null;
  Ast.Exp e3=null;
  List<Ast.Exp> elist = new ArrayList<Ast.Exp>();
  Ast.PrArg pr=null;
  String str = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 57:
      jj_consume_token(57);
      label_7:
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
          break label_7;
        }
        s1 = Stmt();
                  slist.add(s1);
      }
      jj_consume_token(58);
                                          {if (true) return new Ast.Block(slist);}
      break;
    case 18:
    case ID:
      eLeft = ExtId();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 49:
      case 55:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 55:
          jj_consume_token(55);
          e1 = Expr();
          jj_consume_token(56);
          break;
        default:
          jj_la1[14] = jj_gen;
          ;
        }
        jj_consume_token(49);
        e2 = InitExpr();
        jj_consume_token(50);
        if(e1 != null) {  // eLeft[10] = x; Assignment
          e3 = new Ast.ArrayElm(eLeft, e1);
          {if (true) return new Ast.Assign(e3, e2);}
        } else {         // eLeft = x; Assignment
          {if (true) return new Ast.Assign(eLeft, e2);}
        }
        break;
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
          elist = Args();
          break;
        default:
          jj_la1[15] = jj_gen;
          ;
        }
        jj_consume_token(54);
        jj_consume_token(50);
        if(eLeft instanceof Ast.Id) {
          e3  = new Ast.This();    // The object is This
          str = ((Ast.Id)eLeft).nm; // Get method name
        } else if(eLeft instanceof Ast.Field) {
          e3  = ((Ast.Field)eLeft).obj; // Get object name
          str = ((Ast.Field)eLeft).nm;  // Get method name
        }
        {if (true) return new Ast.CallStmt(e3, str, elist);}
        break;
      default:
        jj_la1[16] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    case 19:
      jj_consume_token(19);
      jj_consume_token(53);
      e1 = Expr();
      jj_consume_token(54);
      s1 = Stmt();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 20:
        jj_consume_token(20);
        s2 = Stmt();
        break;
      default:
        jj_la1[17] = jj_gen;
        ;
      }
                                                        {if (true) return new Ast.If(e1, s1, s2);}
      break;
    case 21:
      jj_consume_token(21);
      jj_consume_token(53);
      e1 = Expr();
      jj_consume_token(54);
      s1 = Stmt();
                                                        {if (true) return new Ast.While(e1, s1);}
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
        pr = PrintArg();
        break;
      default:
        jj_la1[18] = jj_gen;
        ;
      }
      jj_consume_token(54);
      jj_consume_token(50);
                                                                   {if (true) return new Ast.Print(pr);}
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
        e1 = Expr();
        break;
      default:
        jj_la1[19] = jj_gen;
        ;
      }
      jj_consume_token(50);
                                                        {if (true) return new Ast.Return(e1);}
      break;
    default:
      jj_la1[20] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

// Args -> Expr {"," Expr}
//
  static final public List<Ast.Exp> Args() throws ParseException {
  Ast.Exp e1;
  Ast.Exp e2;
  List<Ast.Exp> elist = new ArrayList<Ast.Exp>();
    e1 = Expr();
    label_8:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 51:
        ;
        break;
      default:
        jj_la1[21] = jj_gen;
        break label_8;
      }
      jj_consume_token(51);
      e2 = Expr();
                            elist.add(e2);
    }
                                                {if (true) return elist;}
    throw new Error("Missing return statement in function");
  }

// PrintArg -> Expr | <STRLIT>
//
  static final public Ast.PrArg PrintArg() throws ParseException {
  Token tkn;
  Ast.Exp e;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 18:
    case 25:
    case 26:
    case INTLIT:
    case ID:
    case 37:
    case 42:
    case 53:
      e = Expr();
                  {if (true) return e;}
      break;
    case STRLIT:
      tkn = jj_consume_token(STRLIT);
                  {if (true) return new Ast.StrLit(tkn.image.replace("\u005c"", ""));}
      break;
    default:
      jj_la1[22] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

// InitExpr -> "new" BasicType "[" <INTLIT> "]"
//          |  "new" <ID> "(" ")"
//          |  Expr
//
  static final public Ast.Exp InitExpr() throws ParseException {
  Token tkn;
  Ast.Type t=null;
  Ast.Exp e;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 17:
      jj_consume_token(17);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 15:
      case 16:
        t = BasicType();
        jj_consume_token(55);
        tkn = jj_consume_token(INTLIT);
        jj_consume_token(56);
        break;
      case ID:
        tkn = jj_consume_token(ID);
        jj_consume_token(53);
        jj_consume_token(54);
        break;
      default:
        jj_la1[23] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      if(t != null) { // This is an array declaration
        {if (true) return new Ast.NewArray(t, Integer.parseInt(tkn.image));}
      } else {        // This is an object declaration
        {if (true) return new Ast.NewObj(tkn.image);}
      }
      break;
    case 18:
    case 25:
    case 26:
    case INTLIT:
    case ID:
    case 37:
    case 42:
    case 53:
      e = Expr();
             {if (true) return e;}
      break;
    default:
      jj_la1[24] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public Ast.Exp Expr() throws ParseException {
                 Ast.Exp e1, e2=null; Ast.BOP op; Token tkn=null;
    e1 = AndExpr();
    label_9:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 41:
        ;
        break;
      default:
        jj_la1[25] = jj_gen;
        break label_9;
      }
      op = BinopA();
      e2 = AndExpr();
                                           e1 = new Ast.Binop(op, e1, e2);
    }
    {if (true) return e1;}
    throw new Error("Missing return statement in function");
  }

  static final public Ast.Exp AndExpr() throws ParseException {
                    Ast.Exp e1, e2=null; Ast.BOP op; Token tkn=null;
    e1 = RelExpr();
    label_10:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 40:
        ;
        break;
      default:
        jj_la1[26] = jj_gen;
        break label_10;
      }
      op = BinopB();
      e2 = RelExpr();
                                           e1 = new Ast.Binop(op, e1, e2);
    }
    {if (true) return e1;}
    throw new Error("Missing return statement in function");
  }

  static final public Ast.Exp RelExpr() throws ParseException {
                    Ast.Exp e1, e2=null; Ast.BOP op; Token tkn=null;
    e1 = ArithExpr();
    label_11:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 48:
        ;
        break;
      default:
        jj_la1[27] = jj_gen;
        break label_11;
      }
      op = BinopC();
      e2 = ArithExpr();
                                               e1 = new Ast.Binop(op, e1, e2);
    }
    {if (true) return e1;}
    throw new Error("Missing return statement in function");
  }

  static final public Ast.Exp ArithExpr() throws ParseException {
                      Ast.Exp e1, e2=null; Ast.BOP op; Token tkn=null;
    e1 = Term();
    label_12:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 36:
      case 37:
        ;
        break;
      default:
        jj_la1[28] = jj_gen;
        break label_12;
      }
      op = BinopD();
      e2 = Term();
                                     e1 = new Ast.Binop(op, e1, e2);
    }
   {if (true) return e1;}
    throw new Error("Missing return statement in function");
  }

  static final public Ast.Exp Term() throws ParseException {
                 Ast.Exp e1, e2=null; Ast.BOP op; Token tkn=null;
    e1 = Factor();
    label_13:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 38:
      case 39:
        ;
        break;
      default:
        jj_la1[29] = jj_gen;
        break label_13;
      }
      op = BinopE();
      e2 = Factor();
                                         e1 = new Ast.Binop(op, e1, e2);
    }
    {if (true) return e1;}
    throw new Error("Missing return statement in function");
  }

  static final public Ast.Exp Factor() throws ParseException {
  Token tkn;
  Ast.Exp e1=null, e2=null, e3=null, eExpr=null;
  Ast.UOP u;
  List<Ast.Exp> elist = new ArrayList<Ast.Exp>();
  boolean b, isCall=false;
  int i;
  String str = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 37:
    case 42:
      u = UnOp();
      e2 = Factor();
                                  {if (true) return new Ast.Unop(u, e2);}
      break;
    case 53:
      jj_consume_token(53);
      e1 = Expr();
      jj_consume_token(54);
                                  {if (true) return e1;}
      break;
    case INTLIT:
      i = IntLit();
                                  {if (true) return new Ast.IntLit(i);}
      break;
    case 25:
    case 26:
      b = BoolLit();
                                  {if (true) return new Ast.BoolLit(b);}
      break;
    case 18:
    case ID:
      e1 = ExtId();
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
          elist = Args();
          break;
        default:
          jj_la1[30] = jj_gen;
          ;
        }
        jj_consume_token(54);
        if(e1 instanceof Ast.Id) {
          e3 = new Ast.This();
          str = ((Ast.Id)e1).nm;
        } else if (e1 instanceof Ast.Field) {
          e3 = ((Ast.Field)e1).obj;
          str = ((Ast.Field)e1).nm;
        }
        {if (true) return new Ast.Call(e3, str, elist);}
        break;
      default:
        jj_la1[32] = jj_gen;
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 55:
          jj_consume_token(55);
          e2 = Expr();
          jj_consume_token(56);
                      {if (true) return new Ast.ArrayElm(e1,e2);}
          break;
        default:
          jj_la1[31] = jj_gen;
          ;
        }
      }
  {if (true) return e1;}
      break;
    default:
      jj_la1[33] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

// ExtId -> ["this" "."] <ID> {"." <ID>}
//
  static final public Ast.Exp ExtId() throws ParseException {
  Token tkn;
  Ast.Exp e1 = null;
  Ast.Exp e2 = null;
  Ast.Exp e3 = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 18:
      jj_consume_token(18);
           e1=new Ast.This();
      jj_consume_token(52);
      break;
    default:
      jj_la1[34] = jj_gen;
      ;
    }
    tkn = jj_consume_token(ID);
          if (e1 == null) {
            e2 = new Ast.Id(tkn.image);
          } else {
            e2 = new Ast.Field(e1, tkn.image);
          }
    label_14:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 52:
        ;
        break;
      default:
        jj_la1[35] = jj_gen;
        break label_14;
      }
      jj_consume_token(52);
      tkn = jj_consume_token(ID);
                  e3=new Ast.Field(e2, tkn.image);
    }
    if(e3 != null) {
      {if (true) return new Ast.Field(e2, tkn.image);}
    } else {
      {if (true) return e2;}
    }
    throw new Error("Missing return statement in function");
  }

  static final public Ast.UOP UnOp() throws ParseException {
                 Token tkn;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 37:
      tkn = jj_consume_token(37);
            {if (true) return Ast.UOP.NEG;}
      break;
    case 42:
      tkn = jj_consume_token(42);
            {if (true) return Ast.UOP.NOT;}
      break;
    default:
      jj_la1[36] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public Ast.BOP BinopA() throws ParseException {
    jj_consume_token(41);
         {if (true) return Ast.BOP.OR;}
    throw new Error("Missing return statement in function");
  }

  static final public Ast.BOP BinopB() throws ParseException {
    jj_consume_token(40);
        {if (true) return Ast.BOP.AND;}
    throw new Error("Missing return statement in function");
  }

  static final public Ast.BOP BinopC() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 47:
      jj_consume_token(47);
         {if (true) return Ast.BOP.GT;}
      break;
    case 48:
      jj_consume_token(48);
         {if (true) return Ast.BOP.GE;}
      break;
    case 45:
      jj_consume_token(45);
         {if (true) return Ast.BOP.LT;}
      break;
    case 46:
      jj_consume_token(46);
         {if (true) return Ast.BOP.LE;}
      break;
    case 43:
      jj_consume_token(43);
         {if (true) return Ast.BOP.EQ;}
      break;
    case 44:
      jj_consume_token(44);
         {if (true) return Ast.BOP.NE;}
      break;
    default:
      jj_la1[37] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public Ast.BOP BinopD() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 36:
      jj_consume_token(36);
         {if (true) return Ast.BOP.ADD;}
      break;
    case 37:
      jj_consume_token(37);
         {if (true) return Ast.BOP.SUB;}
      break;
    default:
      jj_la1[38] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public Ast.BOP BinopE() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 38:
      jj_consume_token(38);
         {if (true) return Ast.BOP.MUL;}
      break;
    case 39:
      jj_consume_token(39);
         {if (true) return Ast.BOP.DIV;}
      break;
    default:
      jj_la1[39] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public int IntLit() throws ParseException {
               Token tkn;
    tkn = jj_consume_token(INTLIT);
                 {if (true) return Integer.parseInt(tkn.image);}
    throw new Error("Missing return statement in function");
  }

  static final public boolean BoolLit() throws ParseException {
                    Token tkn;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 25:
      tkn = jj_consume_token(25);
                 {if (true) return true;}
      break;
    case 26:
      tkn = jj_consume_token(26);
                 {if (true) return false;}
      break;
    default:
      jj_la1[40] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  static private boolean jj_3_1() {
    if (jj_3R_15()) return true;
    return false;
  }

  static private boolean jj_3R_18() {
    if (jj_scan_token(ID)) return true;
    return false;
  }

  static private boolean jj_3R_15() {
    if (jj_3R_16()) return true;
    if (jj_scan_token(ID)) return true;
    return false;
  }

  static private boolean jj_3R_16() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_17()) {
    jj_scanpos = xsp;
    if (jj_3R_18()) return true;
    }
    return false;
  }

  static private boolean jj_3R_17() {
    if (jj_3R_19()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_20()) jj_scanpos = xsp;
    return false;
  }

  static private boolean jj_3R_20() {
    if (jj_scan_token(55)) return true;
    return false;
  }

  static private boolean jj_3R_22() {
    if (jj_scan_token(16)) return true;
    return false;
  }

  static private boolean jj_3R_19() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_21()) {
    jj_scanpos = xsp;
    if (jj_3R_22()) return true;
    }
    return false;
  }

  static private boolean jj_3R_21() {
    if (jj_scan_token(15)) return true;
    return false;
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public mjParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private Token jj_scanpos, jj_lastpos;
  static private int jj_la;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[41];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x400,0x800,0x18000,0x2000,0x0,0x18000,0x1d000,0x86c0000,0x0,0x1c000,0x0,0x18000,0x18000,0x86c0000,0x0,0x6040000,0x0,0x100000,0x6040000,0x6040000,0x86c0000,0x0,0x6040000,0x18000,0x6060000,0x0,0x0,0x0,0x0,0x0,0x6040000,0x0,0x0,0x6040000,0x40000,0x0,0x0,0x0,0x0,0x0,0x6000000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x8,0x0,0x80000,0x8,0x8,0x2000008,0x20000,0x8,0x800000,0x8,0x0,0x2000008,0x800000,0x200429,0xa20000,0x0,0x20042b,0x200429,0x2000008,0x80000,0x20042b,0x8,0x200429,0x200,0x100,0x1f800,0x30,0xc0,0x200429,0x800000,0x200000,0x200429,0x0,0x100000,0x420,0x1f800,0x30,0xc0,0x0,};
   }
  static final private JJCalls[] jj_2_rtns = new JJCalls[1];
  static private boolean jj_rescan = false;
  static private int jj_gc = 0;

  /** Constructor with InputStream. */
  public mjParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public mjParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new mjParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 41; i++) jj_la1[i] = -1;
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
    for (int i = 0; i < 41; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public mjParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new mjParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 41; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 41; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public mjParser(mjParserTokenManager tm) {
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
    for (int i = 0; i < 41; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(mjParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 41; i++) jj_la1[i] = -1;
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
    for (int i = 0; i < 41; i++) {
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
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
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
