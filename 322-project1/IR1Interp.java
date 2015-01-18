// 
// A starting version of IR1 interpreter. (For CS322 W15 Assignment 1)
// Will Hopper
//
import java.lang.Integer;
import java.lang.String;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import ir1.*;

public class IR1Interp {

  static class IntException extends Exception {
    public IntException(String msg) { super(msg); }
  }

  //-----------------------------------------------------------------
  // Value representation
  //-----------------------------------------------------------------
  //
  abstract static class Val {}

  // Integer values
  //
  static class IntVal extends Val {
    int i;
    IntVal(int i) { this.i = i; }
    public String toString() { return "" + i; }
  }

  // Boolean values
  //
  static class BoolVal extends Val {
    boolean b;
    BoolVal(boolean b) { this.b = b; }
    public String toString() { return "" + b; }
  }

  // String values
  //
  static class StrVal extends Val {
    String s;
    StrVal(String s) { this.s = s; }
    public String toString() { return s; }
  }

  // A special "undefined" value
  //
  static class UndVal extends Val {
    public String toString() { return "UndVal"; }
  }

  //-----------------------------------------------------------------
  // Environment representation
  //-----------------------------------------------------------------
  //
  // Think of how to organize environments.
  // 
  // The following environments are shown in the lecture for use in 
  // an IR0 interpreter:
  //
  //   HashMap<String,Integer> labelMap;  // label table
  //   HashMap<Integer,Val> tempMap;	  // temp table
  //   HashMap<String,Val> varMap;	      // var table
  // 
  // For IR1, they need to be managed at per function level.
  // 

  /* A hashmap of function environments. Lookup is done via function
     names, while the value is a nested hashmap to represent the
     label, temp, and var maps.

     Example: {"FunctionA" => { "a" => 10,
                                "myBool" => true
                              }
               "FunctionB" => { "d" => 27,
                                "myString" => "Hello"
                              }
              }

     When a function is called, a new entry will be made in each
     hashmap.
  */
  static HashMap<String, HashMap<String, Integer>> funcLabelMap;
  static HashMap<String, HashMap<Integer, Val>> funcTempMap;
  static HashMap<String, HashMap<String, Val>> funcVarMap;

  // Reference to current function environment
  static String curFunction;

  //-----------------------------------------------------------------
  // Global variables and constants
  //-----------------------------------------------------------------
  //
  // These variables and constants are for your reference only.
  // You may decide to use all of them, some of these, or not at all.
  //

  // Function lookup table
  // - maps function names to their AST nodes
  //
  static HashMap<String, IR1.Func> funcMap; 	

  // Heap memory
  // - for handling 'malloc'ed data
  // - you need to define alloc and access methods for it
  //
  static ArrayList<Val> heap;		

  // Return value
  // - for passing return value from callee to caller
  //
  static Val retVal;

  // Execution status
  // - tells whether to continue with the nest inst, to jump to
  //   a new target inst, or to return to the caller
  //
  static final int CONTINUE = 0;
  static final int RETURN = -1;	



  //-----------------------------------------------------------------
  // The main method
  //-----------------------------------------------------------------
  //
  // 1. Open an IR1 program file. 
  // 2. Call the IR1 AST parser to read in the program and 
  //    convert it to an AST (rooted at an IR1.Program node).
  // 3. Invoke the interpretation process on the root node.
  //
  public static void main(String [] args) throws Exception {
    if (args.length == 1) {
      FileInputStream stream = new FileInputStream(args[0]);
      IR1.Program p = new ir1Parser(stream).Program();
      stream.close();
      IR1Interp.execute(p);
    } else {
      System.out.println("You must provide an input file name.");
    }
  }

  //-----------------------------------------------------------------
  // Top-level IR nodes
  //-----------------------------------------------------------------
  //

  // Program ---
  //  Func[] funcs;
  //
  // 1. Establish the function lookup map
  // 2. Lookup 'main' in funcMap, and 
  // 3. start interpreting from main's AST node
  //
  public static void execute(IR1.Program n) throws Exception { 
    funcMap   = new HashMap<String,IR1.Func>();
    heap      = new ArrayList<Val>();
    retVal    = new UndVal();                 // Changed this from Val.UndefVal to new UndVal

    funcLabelMap = new HashMap<String, HashMap<String, Integer>>();
    funcVarMap   = new HashMap<String, HashMap<String, Val>>();
    funcTempMap  = new HashMap<String, HashMap<Integer, Val>>();

    for (IR1.Func f: n.funcs)                 // Map each of Program n's functions to their names
      funcMap.put(f.name, f);

    execute((IR1.Func) funcMap.get("main"));  // Start interpreting from main's AST node
  }

  // Func ---
  //  String name;
  //  Var[] params;
  //  Var[] locals;
  //  Inst[] code;
  //
  // 1. Collect label decls information and store them in
  //    a label-lookup table for later use.
  // 2. Execute the fetch-and-execute loop.
  //
  static void execute(IR1.Func n) throws Exception { 
    // We are given a single function, n. It has a name, params[]. localvars[] and code[]
    // Collect label declarations, put them in a label-lookup table for later use
    // Also store params and local vars

    HashMap labelMap = new HashMap<String,Integer>();
    HashMap varMap   = new HashMap<String,Val>();
    HashMap tempMap  = new HashMap<Integer,Val>();

    // Load up labels
    for (int i=0; i < n.code.length; ++i) {
      if (n.code[i] instanceof IR1.LabelDec)
        labelMap.put(((IR1.LabelDec) n.code[i]).name, i);
    }

    // Load up vars
    for (int i=0; i < n.locals.length; ++i) {
      varMap.put(n.locals[i], new UndVal());
    }

    // Load this function's context into the global function lookup tables
    funcLabelMap.put(n.name, labelMap);
    funcVarMap.put(n.name, varMap);
    funcTempMap.put(n.name, tempMap);

    // Set curFunc context to this function
    curFunction = n.name;

    // The fetch-and-execute loop
    int idx = 0;
    while (idx < n.code.length) {
      int next = execute(n.code[idx]);
      if (next == CONTINUE)       // Keep going
	    idx++;
      else if (next == RETURN)    // This function is done
        break;
      else
	  idx = next;
    }
  }

  // Dispatch execution to an individual Inst node.
  //
  static int execute(IR1.Inst n) throws Exception {
    if (n instanceof IR1.Binop)    return execute((IR1.Binop) n);
    if (n instanceof IR1.Unop) 	   return execute((IR1.Unop) n);
    if (n instanceof IR1.Move) 	   return execute((IR1.Move) n);
    if (n instanceof IR1.Load) 	   return execute((IR1.Load) n);
    if (n instanceof IR1.Store)    return execute((IR1.Store) n);
    if (n instanceof IR1.Jump) 	   return execute((IR1.Jump) n);
    if (n instanceof IR1.CJump)    return execute((IR1.CJump) n);
    if (n instanceof IR1.Call)     return execute((IR1.Call) n);
    if (n instanceof IR1.Return)   return execute((IR1.Return) n);
    if (n instanceof IR1.LabelDec) return CONTINUE;
    throw new IntException("Unknown Inst: " + n);
  }

  //-----------------------------------------------------------------
  // Execution routines for individual Inst nodes
  //-----------------------------------------------------------------
  //
  // - Each execute() routine returns CONTINUE, RETURN, or a new idx 
  //   (target of jump).
  //

  // Binop ---
  //  BOP op;
  //  Dest dst;
  //  Src src1, src2;
  //
  //  Examples:
  //  t6 = 1 * 4
  //  t7 = b + t6
  static int execute(IR1.Binop n) throws Exception {
    // 1: get the values of the sources. They can be ID, temp, ints, bools, or strings
    // 2: figure out what the op is
    // 3: execute the op on the values of the sources and store to dest, which is either ID or temp.
    //    Will need to search var and temp maps to find it. It will be in current function's maps

    Val srcValue1 = evaluate(n.src1);
    Val srcValue2 = evaluate(n.src2);

    String op = n.op.toString();
    if (op == "+") {

      Val sum = new IR1Interp.IntVal((((IR1Interp.IntVal)srcValue1).i) + (((IR1Interp.IntVal)srcValue2).i));
      if (n.dst instanceof IR1.Id) {
        funcVarMap.get(curFunction).put(((IR1.Id)n.dst).toString(), sum);
      } else if(n.dst instanceof IR1.Temp) {
        funcTempMap.get(curFunction).put(((IR1.Temp)n.dst).num, sum);
      }
    } else if(op == "-") {
      Val difference = new IR1Interp.IntVal((((IR1Interp.IntVal)srcValue1).i) - (((IR1Interp.IntVal)srcValue2).i));
      if (n.dst instanceof IR1.Id) {
        funcVarMap.get(curFunction).put(((IR1.Id)n.dst).toString(), difference);
      } else if(n.dst instanceof IR1.Temp) {
        funcTempMap.get(curFunction).put(((IR1.Temp)n.dst).num, difference);
      }
    } else if (op == "*") {
      Val product = new IR1Interp.IntVal((((IR1Interp.IntVal)srcValue1).i) * (((IR1Interp.IntVal)srcValue2).i));
      if (n.dst instanceof IR1.Id) {
        funcVarMap.get(curFunction).put(((IR1.Id)n.dst).toString(), product);
      } else if(n.dst instanceof IR1.Temp) {
        funcTempMap.get(curFunction).put(((IR1.Temp) n.dst).num, product);
      }
    } else if (op == "/") {
      Val quotient = new IR1Interp.IntVal((((IR1Interp.IntVal) srcValue1).i) / (((IR1Interp.IntVal) srcValue2).i));
      if (n.dst instanceof IR1.Id) {
        funcVarMap.get(curFunction).put(((IR1.Id) n.dst).toString(), quotient);
      } else if (n.dst instanceof IR1.Temp) {
        funcTempMap.get(curFunction).put(((IR1.Temp) n.dst).num, quotient);
      }
    } else if (op == "&&") {
      Val result = new IR1Interp.BoolVal((((IR1Interp.BoolVal)srcValue1).b) && (((IR1Interp.BoolVal)srcValue2).b));
      if (n.dst instanceof IR1.Id) {
        funcVarMap.get(curFunction).put(((IR1.Id)n.dst).toString(), result);
      } else if(n.dst instanceof IR1.Temp) {
        funcTempMap.get(curFunction).put(((IR1.Temp) n.dst).num, result);
      }
    } else if (op == "||") {
      Val result = new IR1Interp.BoolVal((((IR1Interp.BoolVal) srcValue1).b) || (((IR1Interp.BoolVal) srcValue2).b));
      if (n.dst instanceof IR1.Id) {
        funcVarMap.get(curFunction).put(((IR1.Id) n.dst).toString(), result);
      } else if (n.dst instanceof IR1.Temp) {
        funcTempMap.get(curFunction).put(((IR1.Temp) n.dst).num, result);
      }
    }

    return CONTINUE;  
  }

  // Unop ---
  //  UOP op;
  //  Dest dst;
  //  Src src;
  //  Unop     -> Dest "=" UOP Src, so either -<int> or !<bool>
  static int execute(IR1.Unop n) throws Exception {
    //Val val = execute(n.src);
    Val result;
    Val srcValue = evaluate(n.src);

    if (n.op == IR1.UOP.NEG) {
      if (n.dst instanceof IR1.Id) {
        result = new IntVal(-((IntVal) srcValue).i);
        funcVarMap.get(curFunction).put(((IR1.Id) n.dst).toString(), result);
      } else if (n.dst instanceof IR1.Temp) {
        result = new IntVal(-((IntVal) srcValue).i);
        funcTempMap.get(curFunction).put(((IR1.Temp) n.dst).num, result);
      }
    } else if (n.op == IR1.UOP.NOT) {
      if (n.dst instanceof IR1.Id) {
        result = new BoolVal(!((BoolVal) srcValue).b);
        funcVarMap.get(curFunction).put(((IR1.Id) n.dst).toString(), result);
      } else if (n.dst instanceof IR1.Temp) {
        result = new BoolVal(!((BoolVal) srcValue).b);
        funcTempMap.get(curFunction).put(((IR1.Temp) n.dst).num, result);
      }
    } else {
      throw new IntException("Wrong op in Unop inst: " + n.op);
    }

    return CONTINUE;  
  }

  // Move ---
  //  Dest dst;
  //  Src src;
  //
  static int execute(IR1.Move n) throws Exception {
    Val srcValue = evaluate(n.src);

    if (n.dst instanceof IR1.Id) {
      funcVarMap.get(curFunction).put(((IR1.Id) n.dst).toString(), srcValue);
    } else if (n.dst instanceof IR1.Temp) {
      funcTempMap.get(curFunction).put(((IR1.Temp) n.dst).num, srcValue);
    }

    return CONTINUE; // I added this, so it seems I need to return an integer here...
  }

  // Load ---  
  //  Dest dst;
  //  Addr addr;
  //  Dest "=" Addr, so t1 = [a]
  //  An addr is [<IntLit>] "[" Src "]"  -- load some offset from the heap into a destination
  static int execute(IR1.Load n) throws Exception {
    int addrIndex = evaluate(n.addr);
    Val result = heap.get(addrIndex);

    if (n.dst instanceof IR1.Id) {
      funcVarMap.get(curFunction).put(((IR1.Id) n.dst).toString(), result);
    } else if (n.dst instanceof IR1.Temp) {
      funcTempMap.get(curFunction).put(((IR1.Temp) n.dst).num, result);
    }
    return CONTINUE;
  }

  // Store ---  
  //  Addr addr;
  //  Src src;
  // Addr "=" Src, so [t1] = a --- store something into some offset in the heap
  static int execute(IR1.Store n) throws Exception {
    // Get integer value of addr, use it as offset for heap to store sec
    int addrIndex = evaluate(n.addr);
    Val srcValue = evaluate(n.src);
    heap.add(addrIndex, srcValue);
    return CONTINUE;
  }

  // CJump ---
  //  ROP op;
  //  Src src1, src2;
  //  Label lab;
  //  "if" Src ROP Src "goto" Label (execute jump)
  static int execute(IR1.CJump n) throws Exception {
    // If true return target instruction index, else CONTINUE
    boolean ropVal = false;
    Val srcValue1 = evaluate(n.src1);
    Val srcValue2 = evaluate(n.src2);

    String op = n.op.toString();
    if (op == "==") {
      if ((srcValue1.toString()).equals(srcValue2.toString())) {
        ropVal = true;
      } else {
        ropVal = false;
      }
    } else if (op == "!=") {
      if ((srcValue1.toString()).equals(srcValue2.toString())) {
        ropVal = false;
      } else {
        ropVal = true;
      }
    } else if (op == "<") {
     if ((((IR1Interp.IntVal) srcValue1).i) < (((IR1Interp.IntVal) srcValue2).i)) {
        ropVal = true;
      } else {
        ropVal = false;
      }
    } else if (op == "<=") {
      if ((((IR1Interp.IntVal) srcValue1).i) <= (((IR1Interp.IntVal) srcValue2).i)) {
        ropVal = true;
      } else {
        ropVal = false;
      }
    } else if (op == ">") {
      if ((((IR1Interp.IntVal) srcValue1).i) > (((IR1Interp.IntVal) srcValue2).i)) {
        ropVal = true;
      } else {
        ropVal = false;
      }
    } else if (op == ">=") {
      if ((((IR1Interp.IntVal) srcValue1).i) >= (((IR1Interp.IntVal) srcValue2).i)) {
        ropVal = true;
      } else {
        ropVal = false;
      }
    }

    if (ropVal == true) {
      int index = funcLabelMap.get(curFunction).get(n.lab.toString());
      return index;
    } else {
      return CONTINUE;
    }
  }	

  // Jump ---
  //  Label lab;
  //  should return the jump target instruction’s index.
  static int execute(IR1.Jump n) throws Exception {

    int target = funcLabelMap.get(curFunction).get(n.lab);
    System.out.println(target);
    return target;
  }	

  // Call ---
  //  String name;
  //  Src[] args;
  //  Dest rdst;
  //
  static int execute(IR1.Call n) throws Exception {
    if (n.name.equals("printInt") || n.name.equals("printStr")) {    // pre-defined functions with 0 or 1 arg
      Val arg = new StrVal("");
      if (n.args.length > 0) {
        arg = evaluate(n.args[0]);
      }
      System.out.println(arg);
      return CONTINUE;
    } else if (n.name.equals("malloc")) {
      Val size = evaluate(n.args[0]);
      int currentHeapSize = heap.size();

      for (int i=currentHeapSize; i<=((IntVal)size).i + currentHeapSize; ++i) {
        heap.add(i, new UndVal());
      }

      // dest = first index of heap?
      if (n.rdst instanceof IR1.Id) {
        funcVarMap.get(curFunction).put(((IR1.Id) n.rdst).toString(), new IntVal(currentHeapSize));
      } else if (n.rdst instanceof IR1.Temp) {
        funcTempMap.get(curFunction).put(((IR1.Temp) n.rdst).num, new IntVal(currentHeapSize));
      }

      return CONTINUE;
    }

    /* User defined functions */

    // 1: create a new set of data structures for the callee
    HashMap labelMap = new HashMap<String,Integer>();
    HashMap varMap   = new HashMap<String,Val>();
    HashMap tempMap  = new HashMap<Integer,Val>();

    funcLabelMap.put(n.name, labelMap);
    funcVarMap.put(n.name, varMap);
    funcTempMap.put(n.name, tempMap);

    // 2: evaluate arguments and pass them to callee's data structures


    // 3: Find callee's AST node and switch to execute it (???)
    execute((IR1.Func) funcMap.get(n.name));

    // 4: If a return value is expected, copy the value to its destination
    if (n.rdst instanceof IR1.Id) {
      funcVarMap.get(curFunction).put(((IR1.Id) n.rdst).toString(), retVal);
    } else if (n.rdst instanceof IR1.Temp) {
      funcTempMap.get(curFunction).put(((IR1.Temp) n.rdst).num, retVal);
    }
    return CONTINUE;
  }

  // Return ---  
  //  Src val;
  //
  static int execute(IR1.Return n) throws Exception {

    if (n.val == null) {
      return RETURN;
    } else {
      retVal = evaluate(n.val);
      return RETURN;
    }
  }

  //-----------------------------------------------------------------
  // Evaluatation routines for address
  //-----------------------------------------------------------------
  //
  // - Returns an integer (representing index to the heap memory).
  //
  // Address ---
  //  Src base;  
  //  int offset;
  //
  static int evaluate(IR1.Addr n) throws Exception {
    Val srcValue = evaluate(n.base);
    return Integer.parseInt(srcValue.toString()) + n.offset;
  }

  //-----------------------------------------------------------------
  // Evaluatation routines for operands
  //-----------------------------------------------------------------
  //
  // - Each evaluate() routine returns a Val object.
  //
  static Val evaluate(IR1.Src n) throws Exception {
    Val val;
    if (n instanceof IR1.Temp)    val = (funcTempMap.get(curFunction)).get(((IR1.Temp) n).num);
    else if (n instanceof IR1.Id)      val = (funcVarMap.get(curFunction)).get(n.toString());
    else if (n instanceof IR1.IntLit)  val = new IR1Interp.IntVal(((IR1.IntLit)n).i);
    else if (n instanceof IR1.BoolLit) val = new IR1Interp.BoolVal(((IR1.BoolLit)n).b);
    else if (n instanceof IR1.StrLit)  val = new IR1Interp.StrVal(((IR1.StrLit)n).s);
    else val = new IR1Interp.UndVal();
    return val;
  }

  static Val evaluate(IR1.Dest n) throws Exception {
    Val val;
    if (n instanceof IR1.Temp) val = (funcTempMap.get(curFunction)).get(n.toString());
    else if (n instanceof IR1.Id)   val = (funcVarMap.get(curFunction)).get(n.toString());
    else val = new IR1Interp.UndVal();
    return val;
  }
}
