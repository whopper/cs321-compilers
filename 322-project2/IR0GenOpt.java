// This is supporting software for CS321/CS322 Compilers and Language Design.
// Copyright (c) Portland State University
// 
// IR0 code generator. (For CS322)
//
//  - short-circuit semantics for Boolean expressions
//
//  Will Hopper
//
import java.lang.Integer;
import java.util.*;
import java.io.*;
import ast0.*;
import ir0.*;

class IR0GenOpt {

  static class GenException extends Exception {
    public GenException(String msg) { super(msg); }
  }

  // For returning <src,code> pair from gen routines
  //
  static class CodePack {
    IR0.Src src;
    List<IR0.Inst> code;
    CodePack(IR0.Src src, List<IR0.Inst> code) { 
      this.src=src; this.code=code; 
    }
    CodePack(IR0.Src src) { 
      this.src=src; code=new ArrayList<IR0.Inst>(); 
    }
  } 

  // For returning <addr,code> pair from genAddr routines
  //
  static class AddrPack {
    IR0.Addr addr;
    List<IR0.Inst> code;
    AddrPack(IR0.Addr addr, List<IR0.Inst> code) { 
      this.addr=addr; this.code=code; 
    }
    AddrPack(IR0.Addr addr) { 
      this.addr=addr; code=new ArrayList<IR0.Inst>(); 
    }
  }

  // The main routine
  //
  public static void main(String [] args) throws Exception {
    if (args.length == 1) {
      FileInputStream stream = new FileInputStream(args[0]);
      Ast0.Program p = new ast0Parser(stream).Program();
      stream.close();
      IR0.Program ir0 = IR0GenOpt.gen(p);
      System.out.print(ir0.toString());
    } else {
      System.out.println("You must provide an input file name.");
    }
  }

  // Ast0.Program ---
  // Ast0.Stmt[] stmts;
  //
  // AG:
  //   code: stmts.c  -- append all individual stmt.c
  //
  public static IR0.Program gen(Ast0.Program n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    for (Ast0.Stmt s: n.stmts) {
      code.addAll(gen(s));
    }
    return new IR0.Program(code);
  }

  // STATEMENTS

  static List<IR0.Inst> gen(Ast0.Stmt n) throws Exception {
    if (n instanceof Ast0.Assign)      return gen((Ast0.Assign) n);
    else if (n instanceof Ast0.If)     return gen((Ast0.If) n);
    else if (n instanceof Ast0.While)  return gen((Ast0.While) n);
    else if (n instanceof Ast0.Print)  return gen((Ast0.Print) n);
    throw new GenException("Unknown Ast0 Stmt: " + n);
  }

  // Ast0.Assign ---
  // Ast0.Exp lhs, rhs;
  //
  // AG:
  //   code: rhs.c + lhs.c + ( "lhs.l = rhs.v"     # if lhs is id
  //                         | "[lhs.l] = rhs.v" ) # otherwise
  //
  static List<IR0.Inst> gen(Ast0.Assign n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    CodePack rhs = gen(n.rhs);
    code.addAll(rhs.code);
    if (n.lhs instanceof Ast0.Id) {
      IR0.Dest lhs = new IR0.Id(((Ast0.Id) n.lhs).nm);
      code.add(new IR0.Move(lhs, rhs.src));
    } else if (n.lhs instanceof Ast0.ArrayElm) {
      AddrPack p = genAddr((Ast0.ArrayElm) n.lhs);
      code.addAll(p.code);
      code.add(new IR0.Store(p.addr, rhs.src));
    } else {
      throw new GenException("Wrong Ast0 lhs Exp: " + n.lhs);
    }
    return code;
  }

  // Ast0.If ---
  // Ast0.Exp cond;
  // Ast0.Stmt s1, s2;
  //
  // AG:
  //   newLabel: L1[,L2]
  //   code: cond.c 
  //         + "if cond.v == false goto L1" 
  //         + s1.c 
  //         [+ "goto L2"] 
  //         + "L1:" 
  //         [+ s2.c]
  //         [+ "L2:"]
  //
  static List<IR0.Inst> gen(Ast0.If n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    IR0.Label L1 = new IR0.Label();

    /* First check if cond is a relational expression */

    if (n.cond instanceof Ast0.Binop) {
      CodePack l = gen(((Ast0.Binop)n.cond).e1);
      CodePack r = gen(((Ast0.Binop)n.cond).e2);

      // Can we evaluate right now?
      if (((l.src instanceof IR0.IntLit) && (r.src instanceof IR0.IntLit))
              || ((l.src instanceof IR0.BoolLit) && (r.src instanceof IR0.BoolLit))) {
        CodePack result = gen(((Ast0.Binop)n.cond));
        if (((IR0.BoolLit) result.src).b) {
          code.addAll(gen(n.s1));
          return code;
        } else {
          if (n.s2 != null) {
            code.addAll(gen(n.s2));
          }
          return code;
        }
      }

      if (isROP(((Ast0.Binop) n.cond).op)) {
        code.addAll(l.code);
        code.addAll(r.code);

        if (((Ast0.Binop) n.cond).op == Ast0.BOP.GT) {
          code.add(new IR0.CJump(IR0.ROP.LE, l.src, r.src, L1));
          code.addAll(gen(n.s1));
          code.add(new IR0.LabelDec(L1));
          return code;
        } else if (((Ast0.Binop) n.cond).op == Ast0.BOP.GE) {
          code.add(new IR0.CJump(IR0.ROP.LT, l.src, r.src, L1));
          code.addAll(gen(n.s1));
          code.add(new IR0.LabelDec(L1));
        } else if (((Ast0.Binop) n.cond).op == Ast0.BOP.LT) {
          code.add(new IR0.CJump(IR0.ROP.GE, l.src, r.src, L1));
          code.addAll(gen(n.s1));
          code.add(new IR0.LabelDec(L1));
        } else if (((Ast0.Binop) n.cond).op == Ast0.BOP.LE) {
          code.add(new IR0.CJump(IR0.ROP.GT, l.src, r.src, L1));
          code.addAll(gen(n.s1));
          code.add(new IR0.LabelDec(L1));
        } else if (((Ast0.Binop) n.cond).op == Ast0.BOP.EQ) {
          code.add(new IR0.CJump(IR0.ROP.NE, l.src, r.src, L1));
          code.addAll(gen(n.s1));
          code.add(new IR0.LabelDec(L1));
        } else if (((Ast0.Binop) n.cond).op == Ast0.BOP.NE) {
          code.add(new IR0.CJump(IR0.ROP.EQ, l.src, r.src, L1));
          code.addAll(gen(n.s1));
          code.add(new IR0.LabelDec(L1));
        }
      }
    }

    // If we can't do comparison embedding, move on

    CodePack p = gen(n.cond);

    if (p.src instanceof IR0.BoolLit) {
      if (((IR0.BoolLit) p.src).b == true) {
        code.addAll(gen(n.s1));
        return code;
      } else {
        if (n.s2 != null) { // Execute else stmt instead
          code.addAll(gen(n.s2));
          return code;
        } else {
          return code;
        }
      }
    }

    code.addAll(p.code);
    code.add(new IR0.CJump(IR0.ROP.EQ, p.src, IR0.FALSE, L1));
    code.addAll(gen(n.s1));
    if (n.s2 == null) {
      code.add(new IR0.LabelDec(L1));
    } else {	
      IR0.Label L2 = new IR0.Label();
      code.add(new IR0.Jump(L2));
      code.add(new IR0.LabelDec(L1));
      code.addAll(gen(n.s2));
      code.add(new IR0.LabelDec(L2));
    }
    return code;
  }

  // Ast0.While ---
  // Ast0.Exp cond;
  // Ast0.Stmt s;
  //
  // AG:
  //   newLabel: L1,L2
  //   code: "L1:" 
  //         + cond.c 
  //         + "if cond.v == false goto L2" 
  //         + s.c 
  //         + "goto L1" 
  //         + "L2:"
  //
  static List<IR0.Inst> gen(Ast0.While n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    IR0.Label L1 = new IR0.Label();
    IR0.Label L2 = new IR0.Label();
    /* First, check if cond is a relational expression */
    // EQ("=="), NE("!="), LT("<"), LE("<="), GT(">"), GE(">=")

    if (n.cond instanceof Ast0.Binop) {
      CodePack l = gen(((Ast0.Binop)n.cond).e1);
      CodePack r = gen(((Ast0.Binop)n.cond).e2);

      // Can we evaluate right now?
      if (((l.src instanceof IR0.IntLit) && (r.src instanceof IR0.IntLit))
              || ((l.src instanceof IR0.BoolLit) && (r.src instanceof IR0.BoolLit))) {

        CodePack result = gen(((Ast0.Binop)n.cond));
        if (((IR0.BoolLit) result.src).b) {
          code.addAll(gen(n.s));
          return code;
        } else {
          return code;
        }
      }

      /* TODO: Add support for every relational operator */
      if (((Ast0.Binop)n.cond).op == Ast0.BOP.EQ) {
        code.add(new IR0.LabelDec(L1));
        code.add(new IR0.CJump(IR0.ROP.NE, l.src, r.src, L2));
        code.addAll(gen(n.s));
        code.add(new IR0.Jump(L1));
        code.add(new IR0.LabelDec(L2));
        return code;
      } else if (((Ast0.Binop)n.cond).op == Ast0.BOP.GT) {
        code.add(new IR0.LabelDec(L1));
        code.addAll(l.code);
        code.addAll(r.code);
        code.add(new IR0.CJump(IR0.ROP.LE, l.src, r.src, L2));
        code.addAll(gen(n.s));
        code.add(new IR0.Jump(L1));
        code.add(new IR0.LabelDec(L2));
        return code;
      } else if (((Ast0.Binop)n.cond).op == Ast0.BOP.GE) {
        code.add(new IR0.LabelDec(L1));
        code.addAll(l.code);
        code.addAll(r.code);
        code.add(new IR0.CJump(IR0.ROP.LT, l.src, r.src, L2));
        code.addAll(gen(n.s));
        code.add(new IR0.Jump(L1));
        code.add(new IR0.LabelDec(L2));
        return code;
      } else if (((Ast0.Binop)n.cond).op == Ast0.BOP.LT) {
        code.add(new IR0.LabelDec(L1));
        code.addAll(l.code);
        code.addAll(r.code);
        code.add(new IR0.CJump(IR0.ROP.GE, l.src, r.src, L2));
        code.addAll(gen(n.s));
        code.add(new IR0.Jump(L1));
        code.add(new IR0.LabelDec(L2));
        return code;
      } else if (((Ast0.Binop)n.cond).op == Ast0.BOP.LE) {
        code.add(new IR0.LabelDec(L1));
        code.addAll(l.code);
        code.addAll(r.code);
        code.add(new IR0.CJump(IR0.ROP.GT, l.src, r.src, L2));
        code.addAll(gen(n.s));
        code.add(new IR0.Jump(L1));
        code.add(new IR0.LabelDec(L2));
        return code;
      }
    }

    // If we can't do comparison embedding, move on
    CodePack p = gen(n.cond);

    if (p.src instanceof IR0.BoolLit) {
      if (((IR0.BoolLit) p.src).b == true) {
        code.addAll(gen(n.s));
        return code;
      } else {
        return code;
      }
    }

    code.add(new IR0.LabelDec(L1));
    code.addAll(p.code);
    code.add(new IR0.CJump(IR0.ROP.EQ, p.src, IR0.FALSE, L2));
    code.addAll(gen(n.s));
    code.add(new IR0.Jump(L1));
    code.add(new IR0.LabelDec(L2));
    return code;
  }
  
  // Ast0.Print ---
  // Ast0.PrArg [arg];
  //
  // AG:
  //   code: arg.c + "print (arg.v)"  if arg is Exp
  //     or  "print ()"               if arg==null
  //
  static List<IR0.Inst> gen(Ast0.Print n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    if (n.arg == null) {
      code.add(new IR0.Print());
    } else {
      CodePack p = gen((Ast0.Exp) n.arg);
      code.addAll(p.code);
      code.add(new IR0.Print(p.src));
    }
    return code;
  }

  // EXPRESSIONS

  static CodePack gen(Ast0.Exp n) throws Exception {
    if (n instanceof Ast0.Binop)    return gen((Ast0.Binop) n);
    if (n instanceof Ast0.Unop)     return gen((Ast0.Unop) n);
    if (n instanceof Ast0.NewArray) return gen((Ast0.NewArray) n);
    if (n instanceof Ast0.ArrayElm) return gen((Ast0.ArrayElm) n);
    if (n instanceof Ast0.Id)	    return gen((Ast0.Id) n);
    if (n instanceof Ast0.IntLit)   return gen((Ast0.IntLit) n);
    if (n instanceof Ast0.BoolLit)  return gen((Ast0.BoolLit) n);
    throw new GenException("Unknown Exp node: " + n);
  }

  // Ast0.Binop

  static CodePack gen(Ast0.Binop n) throws Exception {
    if (isAOP(n.op)) return genAOP(n);
    else if (isROP(n.op)) return genROP(n);
    else                  return genLOP(n);
  }

  // Ast0.Binop --- arithmetic op case
  // Ast0.BOP op;
  // Ast0.Exp e1,e2;
  //
  // AG:
  //   newTemp: t
  //   code: e1.c + e2.c
  //         + "t = e1.v op e2.v"
  //
  static CodePack genAOP(Ast0.Binop n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    CodePack l = gen(n.e1);
    CodePack r = gen(n.e2);
    IR0.Temp t = new IR0.Temp();
    int int_result;

    if (n.op == Ast0.BOP.ADD) {
      if ((l.src instanceof IR0.IntLit) && (r.src instanceof IR0.IntLit)) {
        int_result = ((IR0.IntLit) l.src).i + ((IR0.IntLit) r.src).i;
        IR0.IntLit opt_val = new IR0.IntLit(int_result);
        return new CodePack(opt_val);
      }
    } else if (n.op == Ast0.BOP.SUB) {
      if ((l.src instanceof IR0.IntLit) && (r.src instanceof IR0.IntLit)) {
        int_result = ((IR0.IntLit) l.src).i - ((IR0.IntLit) r.src).i;
        IR0.IntLit opt_val = new IR0.IntLit(int_result);
        return new CodePack(opt_val);
      }
    } else if (n.op == Ast0.BOP.MUL) {
      if ((l.src instanceof IR0.IntLit) && (r.src instanceof IR0.IntLit)) {
        int_result = ((IR0.IntLit) l.src).i * ((IR0.IntLit) r.src).i;
        IR0.IntLit opt_val = new IR0.IntLit(int_result);
        return new CodePack(opt_val);
      }
    } else {
      if ((l.src instanceof IR0.IntLit) && (r.src instanceof IR0.IntLit)) {
        int_result = ((IR0.IntLit) l.src).i / ((IR0.IntLit) r.src).i;
        IR0.IntLit opt_val = new IR0.IntLit(int_result);
        return new CodePack(opt_val);
      }
    }

    code.addAll(l.code);
    code.addAll(r.code);
    code.add(new IR0.Binop(gen(n.op), t, l.src, r.src));
    return new CodePack(t, code);
  }

  // Ast0.Binop --- logical op case
  // Ast0.BOP op;
  // Ast0.Exp e1,e2;
  //
  // AG:
  //   newTemp: t
  //   newLabel: L
  //   let val=true if op==OR
  //   let val=false if op==AND
  //   code: "t = val"		       
  //         + e1.c 		     
  //         + "if e1.v==val goto L"     
  //         + e2.c			       
  //         + "if e2.v==val goto L"     
  //         + "t = !val"		       
  //         + "L:"                      
  //
  static CodePack genLOP(Ast0.Binop n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    CodePack l = gen(n.e1);
    CodePack r = gen(n.e2);
    IR0.Temp t = new IR0.Temp();

    if (n.op == Ast0.BOP.OR) {
      boolean right_can_eval = false,
              right_value = false,
              left_can_eval = false,
              left_value = false;

      // If either right or left is true, return true
      // true || false, true || true, true || x, x || true
      if (l.src instanceof IR0.BoolLit) {
        left_can_eval = true;
        left_value = ((IR0.BoolLit) l.src).b;
      } else {
        left_can_eval = false;
      }

      if (r.src instanceof IR0.BoolLit) {
        right_can_eval = true;
        right_value = ((IR0.BoolLit) r.src).b;
      } else {
        right_can_eval = false;
      }

      // Case 1: Only left is bool literal
      if (left_can_eval && !right_can_eval) {
        if (left_value) {
          IR0.BoolLit opt_val = new IR0.BoolLit(true);
          return new CodePack(opt_val);
        } else {
          IR0.Id opt_val = new IR0.Id(r.src.toString());
          return new CodePack(opt_val);
        }
      }

      // Case 2: Only right is bool literal
      if (right_can_eval && !left_can_eval) {
        if (right_value) {
          IR0.BoolLit opt_val = new IR0.BoolLit(true);
          return new CodePack(opt_val);
        } else {
          IR0.Id opt_val = new IR0.Id(l.src.toString());
          return new CodePack(opt_val);
        }
      }

      // Case 2.5: Neither right or left are bool literals
      if (!right_can_eval && !left_can_eval) {
        IR0.BoolLit val = (n.op==Ast0.BOP.OR) ? IR0.TRUE : IR0.FALSE;
        IR0.BoolLit nval = (n.op==Ast0.BOP.OR) ? IR0.FALSE : IR0.TRUE;
        IR0.Label L = new IR0.Label();
        code.add(new IR0.Move(t, val));
        code.addAll(l.code);
        code.add(new IR0.CJump(IR0.ROP.EQ, l.src, val, L));
        code.addAll(r.code);
        code.add(new IR0.CJump(IR0.ROP.EQ, r.src, val, L));
        code.add(new IR0.Move(t, nval));
        code.add(new IR0.LabelDec(L));
        return new CodePack(t, code);
      }

      // Case 3: Left and right are both bool literals
      if (right_value || left_value) {
        IR0.BoolLit opt_val = new IR0.BoolLit(true);
        return new CodePack(opt_val);
      } else {
        IR0.BoolLit opt_val = new IR0.BoolLit(false);
        return new CodePack(opt_val);
      }
    } else { // This is an AND
      if (l.src instanceof IR0.BoolLit && r.src instanceof IR0.BoolLit) {
        if ((((IR0.BoolLit) l.src).b) && (((IR0.BoolLit) r.src).b)){
          IR0.BoolLit opt_val = new IR0.BoolLit(true);
          return new CodePack(opt_val);
        } else {
          IR0.BoolLit opt_val = new IR0.BoolLit(false);
          return new CodePack(opt_val);
        }
      } else if (l.src instanceof IR0.BoolLit && !((IR0.BoolLit) l.src).b) {
        IR0.BoolLit opt_val = new IR0.BoolLit(false);
        return new CodePack(opt_val);
      } else if (r.src instanceof IR0.BoolLit && !((IR0.BoolLit) r.src).b) {
        IR0.BoolLit opt_val = new IR0.BoolLit(false);
        return new CodePack(opt_val);
      } else {
        code.addAll(l.code);
        code.addAll(r.code);
        code.add(new IR0.Binop(gen(n.op), t, l.src, r.src));
        return new CodePack(t, code);
      }
    }
  }

  // Ast0.Binop --- relational op case
  // Ast0.BOP op;
  // Ast0.Exp e1,e2;
  //
  // AG:
  //   newTemp: t
  //   newLabel: L
  //   code: e1.c + e2.c
  //         + "t = true"		       
  //         + "if e1.v op e2.v goto L"     
  //         + "t = false"		       
  //         + "L:"                      
  //
  static CodePack genROP(Ast0.Binop n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    CodePack l = gen(n.e1);
    CodePack r = gen(n.e2);
    IR0.Temp t = new IR0.Temp();

    if (n.op == Ast0.BOP.EQ) {
      if ((l.src instanceof IR0.IntLit) && (r.src instanceof IR0.IntLit)) {
        if (((IR0.IntLit) l.src).i == ((IR0.IntLit) r.src).i) {
          IR0.BoolLit opt_val = new IR0.BoolLit(true);
          return new CodePack(opt_val);
        } else {
          IR0.BoolLit opt_val = new IR0.BoolLit(false);
          return new CodePack(opt_val);
        }
      } else if (((l.src instanceof IR0.BoolLit) && (r.src instanceof IR0.BoolLit))){
        if ((((IR0.BoolLit) l.src).b) == (((IR0.BoolLit) r.src).b)) {
          IR0.BoolLit opt_val = new IR0.BoolLit(true);
          return new CodePack(opt_val);
        } else {
          IR0.BoolLit opt_val = new IR0.BoolLit(false);
          return new CodePack(opt_val);
        }
      }
    } else if (n.op == Ast0.BOP.NE) {
      if ((l.src instanceof IR0.IntLit) && (r.src instanceof IR0.IntLit)) {
        if (((IR0.IntLit) l.src).i != ((IR0.IntLit) r.src).i) {
          IR0.BoolLit opt_val = new IR0.BoolLit(true);
          return new CodePack(opt_val);
        } else {
          IR0.BoolLit opt_val = new IR0.BoolLit(false);
          return new CodePack(opt_val);
        }
      } else if (((l.src instanceof IR0.BoolLit) && (r.src instanceof IR0.BoolLit))){
        if ((((IR0.BoolLit) l.src).b) != (((IR0.BoolLit) r.src).b)) {
          IR0.BoolLit opt_val = new IR0.BoolLit(true);
          return new CodePack(opt_val);
        } else {
          IR0.BoolLit opt_val = new IR0.BoolLit(false);
          return new CodePack(opt_val);
        }
      }
    } else if(n.op == Ast0.BOP.LT) {
      if ((l.src instanceof IR0.IntLit) && (r.src instanceof IR0.IntLit)) {
        if (((IR0.IntLit) l.src).i < ((IR0.IntLit) r.src).i) {
          IR0.BoolLit opt_val = new IR0.BoolLit(true);
          return new CodePack(opt_val);
        } else {
          IR0.BoolLit opt_val = new IR0.BoolLit(false);
          return new CodePack(opt_val);
        }
      }
    } else if (n.op == Ast0.BOP.LE) {
      if ((l.src instanceof IR0.IntLit) && (r.src instanceof IR0.IntLit)) {
        if (((IR0.IntLit) l.src).i <= ((IR0.IntLit) r.src).i) {
          IR0.BoolLit opt_val = new IR0.BoolLit(true);
          return new CodePack(opt_val);
        } else {
          IR0.BoolLit opt_val = new IR0.BoolLit(false);
          return new CodePack(opt_val);
        }
      }
    } else if(n.op == Ast0.BOP.GT) {
      if ((l.src instanceof IR0.IntLit) && (r.src instanceof IR0.IntLit)) {
        if (((IR0.IntLit) l.src).i > ((IR0.IntLit) r.src).i) {
          IR0.BoolLit opt_val = new IR0.BoolLit(true);
          return new CodePack(opt_val);
        } else {
          IR0.BoolLit opt_val = new IR0.BoolLit(false);
          return new CodePack(opt_val);
        }
      }
    } else if (n.op == Ast0.BOP.GE) {
      if ((l.src instanceof IR0.IntLit) && (r.src instanceof IR0.IntLit)) {
        if (((IR0.IntLit) l.src).i >= ((IR0.IntLit) r.src).i) {
          IR0.BoolLit opt_val = new IR0.BoolLit(true);
          return new CodePack(opt_val);
        } else {
          IR0.BoolLit opt_val = new IR0.BoolLit(false);
          return new CodePack(opt_val);
        }
      }
    }

    /* If no optimizations can be made, fall through to default IR code */
    IR0.Label L = new IR0.Label();
    code.addAll(l.code);
    code.addAll(r.code);
    code.add(new IR0.Move(t, IR0.TRUE));
    code.add(new IR0.CJump((IR0.ROP)gen(n.op), l.src, r.src, L));
    code.add(new IR0.Move(t, IR0.FALSE));
    code.add(new IR0.LabelDec(L));
    return new CodePack(t, code);
  }

  // Ast0.Unop ---
  // Ast0.UOP op;
  // Ast0.Exp e;
  //
  // AG:
  //   newTemp: t
  //   code: e.c + "t = op e.v"
  //
  static CodePack gen(Ast0.Unop n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    CodePack p = gen(n.e);
    code.addAll(p.code);
    IR0.UOP op = (n.op == Ast0.UOP.NEG) ? IR0.UOP.NEG : IR0.UOP.NOT;
    IR0.Temp t = new IR0.Temp();
    int result;
    boolean bool_result;

    if (n.op == Ast0.UOP.NEG) {
      if (p.src instanceof IR0.IntLit) {
        result = -(((IR0.IntLit) p.src).i);
        IR0.IntLit opt_val = new IR0.IntLit(result);
        return new CodePack(opt_val);
      }
    } else {
      if (p.src instanceof IR0.BoolLit) {
        bool_result = !(((IR0.BoolLit) p.src).b);
        IR0.BoolLit opt_val = new IR0.BoolLit(bool_result);
        return new CodePack(opt_val);
      }
    }

    // If no optimizations to be made, return expression
    code.add(new IR0.Unop(op, t, p.src));
    return new CodePack(t, code);
  }
  
  // Ast0.NewArray ---
  // int len;
  // 
  // AG:
  //   newTemp: t
  //   code: "t = malloc (len * 4)"
  //
  static CodePack gen(Ast0.NewArray n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    IR0.IntLit arg = new IR0.IntLit(n.len * 4);
    IR0.Temp t = new IR0.Temp();
    code.add(new IR0.Malloc(t, arg));
    return new CodePack(t, code);
  }

  // Ast0.ArrayElm ---
  // Ast0.Exp ar, idx;
  //
  // AG:
  //   newTemp: t1,t2,t3
  //   code: ar.c + idx.c
  //         + "t1 = idx.v * 4"
  //         + "t2 = ar.v + t1"
  //         + "t3 = [t2]"
  //
  static CodePack gen(Ast0.ArrayElm n) throws Exception {
    AddrPack p = genAddr(n);
    IR0.Temp t = new IR0.Temp();
    p.code.add(new IR0.Load(t, p.addr));
    return new CodePack(t, p.code);
  }
  
  static AddrPack genAddr(Ast0.ArrayElm n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    CodePack ar = gen(n.ar);
    CodePack idx = gen(n.idx);


    if (idx.src instanceof IR0.IntLit) {
      int offset = ((IR0.IntLit)idx.src).i * 4;
      IR0.Id base = new IR0.Id(ar.src.toString());
      IR0.Addr opt_addr = new IR0.Addr(base, offset);
      return new IR0GenOpt.AddrPack(opt_addr);
    }

    // If no optimization can be made, continue with default IR code
    code.addAll(ar.code);
    code.addAll(idx.code);
    IR0.Temp t1 = new IR0.Temp();
    IR0.Temp t2 = new IR0.Temp();
    IR0.IntLit intSz = new IR0.IntLit(4);
    code.add(new IR0.Binop(IR0.AOP.MUL, t1, idx.src, intSz));
    code.add(new IR0.Binop(IR0.AOP.ADD, t2, ar.src, t1));
    return new AddrPack(new IR0.Addr(t2, 0), code);
  }

  // Ast0.Id ---
  // String nm;
  //
  static CodePack gen(Ast0.Id n) throws Exception {
    return new CodePack(new IR0.Id(n.nm));
  }

  // Ast0.IntLit ---
  // int i;
  //
  static CodePack gen(Ast0.IntLit n) throws Exception {
    return  new CodePack(new IR0.IntLit(n.i));
  }

  // Ast0.BoolLit ---
  // boolean b;
  //
  static CodePack gen(Ast0.BoolLit n) throws Exception {
    return  new CodePack(n.b ? IR0.TRUE : IR0.FALSE);
  }

  // OPERATORS

  static IR0.BOP gen(Ast0.BOP op) {
    IR0.BOP irOp = null;
    switch (op) {
    case ADD: irOp = IR0.AOP.ADD; break;
    case SUB: irOp = IR0.AOP.SUB; break;
    case MUL: irOp = IR0.AOP.MUL; break;
    case DIV: irOp = IR0.AOP.DIV; break;
    case AND: irOp = IR0.AOP.AND; break;
    case OR:  irOp = IR0.AOP.OR;  break;
    case EQ:  irOp = IR0.ROP.EQ;  break;
    case NE:  irOp = IR0.ROP.NE;  break;
    case LT:  irOp = IR0.ROP.LT;  break;
    case LE:  irOp = IR0.ROP.LE;  break;
    case GT:  irOp = IR0.ROP.GT;  break;
    case GE:  irOp = IR0.ROP.GE;  break;
    }
    return irOp;
  }
   
  static boolean isAOP(Ast0.BOP op) {
    switch (op) {
    case ADD:  return true; 
    case SUB:  return true; 
    case MUL:  return true; 
    case DIV:  return true; 
    }
    return false;
  }
   
  static boolean isROP(Ast0.BOP op) {
    switch (op) {
    case EQ:  return true; 
    case NE:  return true; 
    case LT:  return true; 
    case LE:  return true; 
    case GT:  return true; 
    case GE:  return true; 
    }
    return false;
  }

}
