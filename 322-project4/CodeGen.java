// This is supporting software for CS322 Compilers and Language Design II
// Copyright (c) Portland State University
// 
// X86-64 code generator for IR1 (A starter version, For CS322 HW4)
//

import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.PrimitiveIterator;

import ir1.*;

class CodeGen {
  static class GenException extends Exception {
    public GenException(String msg) { super(msg); }
  }

  public static void main(String [] args) throws Exception {
    if (args.length == 1) {
      FileInputStream stream = new FileInputStream(args[0]);
      IR1.Program p = new ir1Parser(stream).Program();
      stream.close();
      // IR1.indexed = true;
      gen(p);
    } else {
      System.out.println("You must provide an input file name.");
    }
  }

  //----------------------------------------------------------------------------------
  // Global Variables
  //------------------

  // Per-program globals
  //
  static List<String> stringLiterals; 	    // accumulated string literals, 
                                            //  indexed by position

  static final X86.Reg tempReg1 = X86.R10;  // scratch registers - need to be
  static final X86.Reg tempReg2 = X86.R11;  //  in sync with RegAlloc

  // Per-function globals
  //
  static Map<IR1.Dest,X86.Reg> regMap; 	    // register mapping 
  static int frameSize; 		            // in bytes
  static String fnName; 		            // function's name


  //----------------------------------------------------------------------------------
  // Gen Routines
  //--------------

  // Program ---
  // Func[] funcs;
  //
  // Guideline:
  // - generate code for each function
  // - emit any accumulated string literals
  //
  public static void gen(IR1.Program n) throws Exception { 
    stringLiterals = new ArrayList<String>();
    X86.emit0(".text");
    for (IR1.Func f: n.funcs) {
      gen(f);
    }

    int i = 0;
    for (String s: stringLiterals) {
      X86.GLabel lab = new X86.GLabel("_S" + i);
      X86.emitGLabel(lab);
      X86.emitString(s);
      i++;
    }      
  }

  // Func ---
  // String name;
  // Var[] params;
  // Var[] locals;
  // Inst[] code;
  //
  // Guideline:
  // - 1. call reg-alloc routine to assign registers to all Ids and Temps
  // - 2. emit the function header
  // - 3. save any callee-save registers on the stack
  // - 4. make space for the local frame --- use the following calculation:
  //    "if ((calleeSaveSize % 16) == 0) 
  //	  frameSize += 8;"
  //   where 'calleeSaveSize' represents the total size (in bytes) of
  //   all saved callee-registers
  // - 5. move the incoming actual arguments to their assigned locations
  //   . simply fail if function has more than 6 args
  //   . call X86's parallelMove routine to emit code 
  // - 6. emit code for the body
  //
  // Note: The restoring of the saved registers is carried out in the 
  //   	code for Return instruction.
  //
  static void gen(IR1.Func n) throws Exception { 
    fnName = n.name;
    int calleeSaveSize = 0;

    /* 1) call reg-alloc routine to assign registers to all Ids and Temps */
    regMap = RegAlloc.linearScan(n);

    /* 2) Emit the function header. Thanks for doing these out of order */
    System.out.print("\t\t\t  # " + n.header());

    for (Map.Entry<IR1.Dest,X86.Reg> me: regMap.entrySet())
      System.out.print("\t\t\t  # " + me.getKey() + "\t" + me.getValue() + "\n");

    X86.emit0(".p2align 4,0x90");
    X86.emit0(".globl _" + fnName);
    X86.emit("_" + fnName + ":");


    /* 3) Save any callee-save registers on the stack */
    // generates code to save any callee-save registers that get assigned
    // by the register allocator.
    // For every calleeSaveReg in regMap, do something
    for (X86.Reg callee_reg: X86.calleeSaveRegs) {
      if (regMap.containsValue(callee_reg)) {
        X86.emit1("pushq", callee_reg);
      }
    }

    /*
    ArrayList<X86.Reg> callee_Saves = new ArrayList<X86.Reg>();
    for (X86.Reg callee_reg: X86.calleeSaveRegs) {
      if (regMap.containsKey(callee_reg)) {

      }
    }
    */

    /* 4) Make space for the local frame */
    if ((calleeSaveSize % 16) == 0) {
      frameSize += 8;
    }

    X86.emit2("subq", new X86.Imm(frameSize), X86.RSP);


    /* 5) Move the incoming actual arguments to their assigned locations */

    /* 6) Emit code for the body */
    for (IR1.Inst inst: n.code) {
      gen(inst);
    }

  }

  // INSTRUCTIONS

  static void gen(IR1.Inst n) throws Exception {
    System.out.print("\t\t\t  # " + n);
    if (n instanceof IR1.Binop) 	gen((IR1.Binop) n);
    else if (n instanceof IR1.Unop) 	gen((IR1.Unop) n);
    else if (n instanceof IR1.Move) 	gen((IR1.Move) n);
    else if (n instanceof IR1.Load) 	gen((IR1.Load) n);
    else if (n instanceof IR1.Store) 	gen((IR1.Store) n);
    else if (n instanceof IR1.LabelDec) gen((IR1.LabelDec) n);
    else if (n instanceof IR1.CJump) 	gen((IR1.CJump) n);
    else if (n instanceof IR1.Jump) 	gen((IR1.Jump) n);
    else if (n instanceof IR1.Call)     gen((IR1.Call) n);
    else if (n instanceof IR1.Return)   gen((IR1.Return) n);
    else throw new GenException("Illegal IR1 instruction: " + n);
  }

  // For Binop, Unop, Move, and Load nodes:
  // - If dst is not assigned a register, it means that the
  //   instruction is dead; just return
  //

  // Binop ---
  //  BOP op;
  //  Dest dst;
  //  Src src1, src2;
  //
  // Guideline:
  // - call gen_source() to generate code for both left and right
  //   and right operands
  //  
  // * Regular cases (ADD, SUB, MUL, AND, OR):
  // - make sure right operand is not occupying the dst reg (if so,
  //   generate a "mov" to move it to a tempReg)
  // - generate a "mov" to move left operand to the dst reg
  // - generate code for the Binop
  //
  // * For DIV:
  //   The RegAlloc module guaranteeds that no caller-save register
  //   (including RAX, RDX) is allocated across a DIV. (It also
  //   preferenced the left operand and result to RAX.)  But it is 
  //   still possible that the right operand is in RAX or RDX.
  // - if so, generate a "mov" to move it to a tempReg
  // - generate "cqto" (sign-extend into RDX) and "idivq"	
  // - generate a "mov" to move the result to the dst reg
  //
  // * For relational ops:
  // - generate "cmp" and "set"	
  //   . note that set takes a byte-sized register
  // - generate "movzbq" to size--extend the result register
  //
  static void gen(IR1.Binop n) throws Exception {

    // ... need code ...


  }	

  // Unop ---
  //  UOP op;
  //  Dest dst;
  //  Src src;
  //
  // Guideline:
  // - call gen_source() to generate code for the operand
  // - generate a "mov" to move the operand to the dst reg
  // - generate code for the op
  //  
  static void gen(IR1.Unop n) throws Exception {


  }

  // Move ---
  //  Dest dst;
  //  Src src;
  //
  // Guideline:
  // - call gen_source() to generate code for the src
  // - generate a "mov"
  //  
  static void gen(IR1.Move n) throws Exception {

    /* 1) Call gen_source() to generate code for the src */
    // give it a src and a temp reg
    X86.Reg code = gen_source(n.src, regMap.get(n.dst));

    /* 2) Generate a "mov"*/
  }

  // Load ---  
  //  Dest dst;
  //  Addr addr;
  //
  // Guideline:
  // - call gen_addr() to generate code for addr
  // - generate a "mov"
  //   . pay attention to size info (all IR1's stored values
  //     are integers)
  //
  static void gen(IR1.Load n) throws Exception {

    // ... need code ...


  }

  // Store ---  
  //  Addr addr;
  //  Src src;
  //
  // Guideline:
  // - call gen_source() to generate code for src
  // - call gen_addr() to generate code for addr
  // - generate a "mov"
  //   . pay attention to size info (IR1's stored values
  //     are all integers)
  //
  static void gen(IR1.Store n) throws Exception {

    // ... need code ...


  }

  // LabelDec ---  
  //  Label lab;
  //
  // Guideline:
  // - emit an unique local label by adding func's name in
  //   front of IR1's label name
  //
  static void gen(IR1.LabelDec n) {
    X86.emitLabel(new X86.Label(fnName + "_" + n.lab.name));
  }

  // CJump ---
  //  ROP op;
  //  Src src1, src2;
  //  Label lab;
  //
  // Guideline:
  // - recursively generate code for the two operands
  // - generate a "cmp" and a jump instruction
  //   . remember: left and right are switched under gnu assembler
  //   . conveniently, IR1 and X86 names for the condition 
  //     suffixes are the same
  //
  static void gen(IR1.CJump n) throws Exception {

    // ... need code ...


  }	

  // Jump ---
  //  Label lab;
  //
  // Guideline:
  // - generate a "jmp" to a local label
  //   . again, add func's name in front of IR1's label name
  //
  static void gen(IR1.Jump n) throws Exception {

    // ... need code ...


  }	

  // Call ---
  //  String name;
  //  Src[] args;
  //  Dest rdst;
  //
  // Guideline:
  // - count args; if there are more than 6 args, just fail
  // - move arguments into the argument regs
  //   . first call X86's parallelMove() to move registered args 
  //   . then generate "mov" to move immediate args
  // - emit a "call" with a global label (i.e. "_" preceding func's name)
  // - if return value is expected, emit a "mov" to move result from
  //   rax to target reg
  //
  static void gen(IR1.Call n) throws Exception {

    /* 1) count args; if more than 6, fail */
    int arg_count = 0;

    for (IR1.Src src: n.args)
      ++arg_count;

    if (arg_count > 6)
      throw new GenException("Too many arguments in Call");

    /* 2) move arguments into argument registers */

    // First call X86 parallelMove() to move registered args
    // static void parallelMove(int n0, Reg[] src0, Reg[] dst0, Reg tmp0)
    X86.Reg temp = new X86.Reg(20);

    /*
    for (IR1.Src src: n.args) {
      X86.argRegs[i] = 0;
      ++i;
    }
    */

    // System.out.println(n.rdst);
    // System.out.println(regMap.get(n.rdst));
    // X86.Reg[] dests = {regMap.get(n.rdst)};
    // X86.Reg dest = regMap.get(n.rdst);
    // X86.parallelMove(arg_count, X86.argRegs, X86.callerSaveRegs, temp);

    for (IR1.Src src: n.args) {
      X86.Reg src_reg = gen_source(src, X86.RDI);

      // Generate "mov" to move immediate args
      X86.emitMov(X86.Size.Q, src_reg, X86.RDI);
    }

    /* 3) Emit a "call" with a global label */
    X86.emit1("call", new X86.GLabel("_" + n.name));


    /* 4) If return value expected, emit a "mov" to move result from rax to target reg*/

    /*
    X86.Reg dest = regMap.get(n.dst);
    if (dest == null)
      return;
    X86.Reg src = gen_source(n.src, dest);
    X86.emitMov(X86.Size.Q, src, dest);
    */
  }

  // Return ---  
  //  Src val;
  //
  // Guideline:
  // - if there is a value, emit a "mov" to move it to rax
  // - pop the frame (add framesize back to stack pointer)
  // - restore any saved callee-save registers
  // - emit a "ret"
  //
  static void gen(IR1.Return n) throws Exception {

    /* 1) If there is a value, emit a "mov" and move it to rax */
    if (n.val != null) {
      X86.emit0("mov");
    }

    /* 2) Pop the frame (add framesize back to stack pointer */
    X86.emit2("addq", new X86.Imm(frameSize), X86.RSP );
    for (int i=5; i >= 0; --i) {
      if (regMap.containsValue(X86.calleeSaveRegs[i])) {
        X86.emit1("popq", X86.calleeSaveRegs[i]);
      }
    }
    /*
    for (X86.Reg callee_reg: X86.calleeSaveRegs) {
      if (regMap.containsValue(callee_reg)) {
        X86.emit1("popq", callee_reg);
      }

    }
*/
    X86.emit0("ret");
  }

  // OPERANDS

  // Src -> Id | Temp | IntLit | BoolLit | StrLit 
  //
  // Return the Src's value in a register. Use the temp register
  // for the literal nodes.
  //
  // Guideline:
  // * Id and Temp:
  //   - get their assigned reg from regMap and return it
  // * IntLit:
  //   - emit code to move the value to the temp reg and return the reg
  // * BoolLit:
  //   - same as IntLit, except that use 1 for "true" and 0 for "false"
  // * StrLit:
  //   - add the string to 'stringLiterals' collection to be emitted late
  //   - construct a globel label "_Sn" where n is the index of the string
  //   in the 'stringLiterals' collection
  //   - emit a "lea" to move the label to the temp reg and return the reg
  //
  static X86.Reg gen_source(IR1.Src n, final X86.Reg temp) throws Exception {

    if (n instanceof IR1.Id) {
      return regMap.get(n);
    } else if (n instanceof IR1.Temp) {
      return regMap.get(n);
    } else if (n instanceof IR1.IntLit) {
      X86.emit2("movq", new X86.Imm(((IR1.IntLit) n).i), temp);
    } else if (n instanceof IR1.BoolLit) {
      X86.emit2("movq", new X86.Imm(((IR1.BoolLit)n).b == true ? 1 : 0), temp);
    } else if (n instanceof IR1.StrLit) {
      stringLiterals.add(((IR1.StrLit)n).s);
      int size = stringLiterals.size() - 1;
      X86.GLabel glab = new X86.GLabel("_S" + Integer.toString(size));
      X86.AddrName glab_addr = new X86.AddrName(glab.s);
      X86.emit2("leaq", glab_addr, temp);
    }
    return temp;

  }

  // Addr ---
  // Src base;  
  // int offset;
  //
  // Guideline:
  // - call gen_source() on base to place it in a reg
  // - generate a memory operand (i.e. X86.Mem)
  //
  static X86.Operand gen_addr(IR1.Addr addr, X86.Reg temp) throws Exception {
    X86.Reg base = gen_source(addr.base, temp);
    return new X86.Mem(base, addr.offset);
  }

  //----------------------------------------------------------------------------------
  // Ultilities
  //------------

  static String opname(IR1.AOP op) {
    switch(op) {
    case ADD: return "add";
    case SUB: return "sub";
    case MUL: return "imul";
    case DIV: return "idiv"; // not used 
    case AND: return "and";
    case OR:  return "or";
    }
    return null; // impossible
  }
     
  static String opname(IR1.UOP op) {
    switch(op) {
    case NEG: return "neg";
    case NOT: return "not";
    }
    return null; // impossible
  }

  static String opname(IR1.ROP op) {
    switch(op) {
    case EQ: return "e";
    case NE: return "ne";
    case LT: return "l";
    case LE: return "le";
    case GT: return "g";
    case GE: return "ge";
    }
    return null; // impossible
  }
}
