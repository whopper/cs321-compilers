// This is supporting software for CS322 Compilers and Language Design II
// Copyright (c) Portland State University
// 
// IR code generator for miniJava's AST.
//
// (Starter version.)
//

import java.util.*;
import java.io.*;
import java.util.ArrayList;

import ast.*;
import ir.*;

public class IRGen {

  static class GenException extends Exception {
    public GenException(String msg) { super(msg); }
  }

  //------------------------------------------------------------------------------
  // ClassInfo
  //----------
  // For keeping all useful information about a class declaration for use 
  // in the codegen.
  //
  static class ClassInfo {
    Ast.ClassDecl cdecl; 	   // classDecl AST
    ClassInfo parent; 		   // pointer to parent
    List<String> vtable; 	   // method-label table
    List<Ast.VarDecl> fdecls;  // field decls (incl. inherited ones)
    List<Integer> offsets;     // field offsets
    int objSize; 		       // object size

    // Constructor -- clone a parent's record
    //
    ClassInfo(Ast.ClassDecl cdecl, ClassInfo parent) {
      this.cdecl = cdecl;
      this.parent = parent;
      this.vtable = new ArrayList<String>(parent.vtable);
      this.fdecls = new ArrayList<Ast.VarDecl>(parent.fdecls); 
      this.offsets = new ArrayList<Integer>(parent.offsets); 
      this.objSize = parent.objSize;
    }      

    // Constructor -- create a new record
    //
    ClassInfo(Ast.ClassDecl cdecl) {
      this.cdecl = cdecl;
      this.parent = null;
      this.vtable = new ArrayList<String>();
      this.fdecls = new ArrayList<Ast.VarDecl>(); 
      this.offsets = new ArrayList<Integer>(); 
      this.objSize = IR.Type.PTR.size; 	// reserve space for ptr to class
    }      

    // Utility Routines
    // ----------------
    // For accessing information stored in class information record
    //

    // Return the name of this class 
    //
    String className() { return cdecl.nm; }

    // Find method's base class record
    //
    ClassInfo methodBaseClass(String mname) throws Exception {
      for (Ast.MethodDecl mdecl: cdecl.mthds)
	    if (mdecl.nm.equals(mname))
	      return this;

      if (parent != null)
        return parent.methodBaseClass(mname);

      throw new GenException("Can't find base class for method " + mname);
    }	

    // Find method's return type
    //
    Ast.Type methodType(String mname) throws Exception {
      for (Ast.MethodDecl mdecl: cdecl.mthds)
	    if (mdecl.nm.equals(mname))
	      return mdecl.t;

      if (parent != null)
        return parent.methodType(mname);

      throw new GenException("Can't find MethodDecl for method " + mname);
    }

    // Return method's vtable offset
    //
    int methodOffset(String mname) {
      return vtable.indexOf(mname) * IR.Type.PTR.size;
    }

    // Find field variable's type
    //
    Ast.Type fieldType(String fname) throws Exception {
      for (Ast.VarDecl fdecl: cdecl.flds) {
	    if (fdecl.nm.equals(fname))
	      return fdecl.t;
      }

      if (parent != null)
        return parent.fieldType(fname);

      throw new GenException("Can't find VarDecl for field " + fname);
    }

    // Return field variable's offset
    //
    int fieldOffset(String fname) throws Exception {
      for (int i=fdecls.size()-1; i>=0; i--) {
	    if (fdecls.get(i).nm.equals(fname))
	    return offsets.get(i);
      }

      throw new GenException("Can't find offset for field " + fname);
    }

    public String toString() {
      return "ClassInfo: " + " " + cdecl + " " + parent + " "
	+ " " + vtable + " " + offsets + " " + objSize;
    }
  }

  //------------------------------------------------------------------------------
  // Other Supporting Data Structures
  //---------------------------------

  // CodePack
  // --------
  // For returning <type,src,code> tuple from gen() routines
  //
  static class CodePack {
    IR.Type type;
    IR.Src src;
    List<IR.Inst> code;
    CodePack(IR.Type type, IR.Src src, List<IR.Inst> code) { 
      this.type=type; this.src=src; this.code=code; 
    }
    CodePack(IR.Type type, IR.Src src) { 
      this.type=type; this.src=src; code=new ArrayList<IR.Inst>(); 
    }
  }

  // AddrPack
  // --------
  // For returning <type,addr,code> tuple from genAddr routines
  //
  static class AddrPack {
    IR.Type type;
    IR.Addr addr;
    List<IR.Inst> code;
    AddrPack(IR.Type type, IR.Addr addr, List<IR.Inst> code) { 
      this.type=type; this.addr=addr; this.code=code; 
    }
  }

  // Env
  // ---
  // For keeping track of local variables and parameters and for finding 
  // their types.
  //
  private static class Env extends HashMap<String,Ast.Type> {}


  //------------------------------------------------------------------------------
  // Global Variables
  // ----------------
  //

  // Env for ClassInfo records
  private static HashMap<String,ClassInfo> classEnv = new HashMap<String,ClassInfo>();

  // IR code representation of the current object
  private static IR.Src thisObj = new IR.Id("obj");


  //------------------------------------------------------------------------------
  // Utility routines
  // ----------------
  //

  // Sort ClassDecls based on parent-children relationship.
  //
  private static Ast.ClassDecl[] topoSort(Ast.ClassDecl[] classes) {
    List<Ast.ClassDecl> cl = new ArrayList<Ast.ClassDecl>();
    Vector<String> done = new Vector<String>();
    int cnt = classes.length;
    while (cnt > 0) {
      for (Ast.ClassDecl cd: classes)
	    if (!done.contains(cd.nm) && ((cd.pnm == null) || done.contains(cd.pnm))) {
	      cl.add(cd);
	      done.add(cd.nm);
	      cnt--;
	    }
    }

    return cl.toArray(new Ast.ClassDecl[0]);
  }

  // Return an object's base classInfo.
  //  (The parameter n is known to represent an object when call
  //  is made.)
  //
  private static ClassInfo getClassInfo(Ast.Exp n, ClassInfo cinfo, Env env) throws Exception {
    Ast.Type typ = null;
    if (n instanceof Ast.This)
      return cinfo;
    if (n instanceof Ast.Id) {
      typ = env.get(((Ast.Id) n).nm);
      if (typ == null) // id is a field with a missing "this" pointer
	    typ = cinfo.fieldType(((Ast.Id) n).nm);
    } else if (n instanceof Ast.Field) {
      ClassInfo base = getClassInfo(((Ast.Field) n).obj, cinfo, env);
      typ = base.fieldType(((Ast.Field) n).nm);
    } else {
      throw new GenException("Unexpected obj epxression " + n);  
    }

    if (!(typ instanceof Ast.ObjType))
      throw new GenException("Expects an ObjType, got " + typ);

    return classEnv.get(((Ast.ObjType) typ).nm);
  }	

  // Create ClassInfo record
  //
  // Codegen Guideline: 
  // 1. If parent exists, clone parent's record; otherwise create a new one
  // 2. Walk the MethodDecl list. If a method is not in the v-table, add it in;
  // 3. Compute offset values for field variables
  // 4. Decide object's size
  //
  private static ClassInfo createClassInfo(Ast.ClassDecl n) throws Exception {
    int obj_size = 0;
    // 1) If parent exists, clone parent's record, otherwise create a new one
    ClassInfo cinfo = (n.pnm != null) ? new ClassInfo(n, classEnv.get(n.pnm)) : new ClassInfo(n);

    // 2) Walk MethodDecl list. If a method is not in the v-table, add it
    for (Ast.MethodDecl mdecl: n.mthds) {
      if (!cinfo.vtable.contains(n.nm)) {
        cinfo.vtable.add(mdecl.nm);
      }
    }

    // 3) Compute offset values for field vars
    for (Ast.VarDecl var: n.flds) {
      if (var.t == Ast.IntType) {   // Int. Size = 4
        cinfo.offsets.add(IR.Type.INT.size);
        obj_size = obj_size + IR.Type.INT.size;
      } else if (var.t == Ast.BoolType) {  // bool. Size = 1
        cinfo.offsets.add(IR.Type.BOOL.size);
        obj_size = obj_size + IR.Type.BOOL.size;
      } else { // Pointer. Size = 8
        cinfo.offsets.add(IR.Type.PTR.size);
        obj_size = obj_size + IR.Type.PTR.size;
      }
    }

    // 4) Decide object's size
    cinfo.objSize = obj_size + IR.Type.PTR.size; // Additional pointer for pointer to class itself
    return cinfo;
  }


  //------------------------------------------------------------------------------
  // The Main Routine
  //-----------------
  //
  public static void main(String [] args) throws Exception {
    if (args.length == 1) {
      FileInputStream stream = new FileInputStream(args[0]);
      Ast.Program p = new astParser(stream).Program();
      stream.close();
      IR.Program ir = gen(p);
      System.out.print(ir.toString());
    } else {
      System.out.println("You must provide an input file name.");
    }
  }

  //------------------------------------------------------------------------------
  // Codegen Routines for Individual AST Nodes
  //------------------------------------------

  // Program ---
  // ClassDecl[] classes;
  //
  // Three passes over a program:
  //  0. topo-sort class decls
  //  1. create ClassInfo records 
  //  2. generate IR code
  //     2.1 generate list of static data (i.e. class descriptors)
  //     2.2 generate list of functions
  //
  public static IR.Program gen(Ast.Program n) throws Exception {
    Ast.ClassDecl[] classes = topoSort(n.classes);
    ClassInfo cinfo;
    for (Ast.ClassDecl c: classes) {
      cinfo = createClassInfo(c);
      classEnv.put(c.nm, cinfo);
    }
    List<IR.Data> allData = new ArrayList<IR.Data>();
    List<IR.Func> allFuncs = new ArrayList<IR.Func>();
    for (Ast.ClassDecl c: classes) {
      cinfo = classEnv.get(c.nm);
      IR.Data data = genData(c, cinfo);
      List<IR.Func> funcs = gen(c, cinfo);
      if (data != null)
	    allData.add(data);
      allFuncs.addAll(funcs);
    }
    return new IR.Program(allData, allFuncs);
  }

  // ClassDecl ---
  // String nm, pnm;
  // VarDecl[] flds;
  // MethodDecl[] mthds;
  //

  // 1. Generate static data
  //
  // Codegen Guideline: 
  //   1.1 For each method in class's vtable, construct a global label of form
  //       "<base class name>_<method name>" and save it in an IR.Global node
  //   1.2 Assemble the list of IR.Global nodes into an IR.Data node with a
  //       global label "class_<class name>"
  //
  static IR.Data genData(Ast.ClassDecl n, ClassInfo cinfo) throws Exception {
    List<IR.Global> class_methods = new ArrayList<IR.Global>();

    for (String var: cinfo.vtable) {
      if (var.equals("main")) {
        class_methods.add(new IR.Global(var));
      } else {
        class_methods.add(new IR.Global(cinfo.className() + "_" + var));
      }
    }


    return new IR.Data(new IR.Global("class_" + n.nm), class_methods.size() * IR.Type.PTR.size, class_methods);
  }

  // 2. Generate code
  //
  // Codegen Guideline: 
  //   Straightforward -- generate a IR.Func for each mthdDecl.
  //
  static List<IR.Func> gen(Ast.ClassDecl n, ClassInfo cinfo) throws Exception {
    List<IR.Func> func_list = new ArrayList<IR.Func>();

    for (Ast.MethodDecl mdecl: n.mthds)
      func_list.add(gen(mdecl, cinfo));

    return func_list;
  }

  // MethodDecl ---
  // Type t;
  // String nm;
  // Param[] params;
  // VarDecl[] vars;
  // Stmt[] stmts;
  //
  // Codegen Guideline: 
  // 1. Construct a global label of form "<base class name>_<method name>"
  // 2. Add "obj" into the params list as the 0th item
  // (Skip these two steps if method is "main".)
  // 3. Create an Env() containing all params and all local vars 
  // 4. Generate IR code for all statements
  // 5. Return an IR.Func with the above
  //
  static IR.Func gen(Ast.MethodDecl n, ClassInfo cinfo) throws Exception {
    List<IR.Inst> code = new ArrayList<IR.Inst>();
    List<String> params = new ArrayList<String>();
    List<String> vars = new ArrayList<String>();
    List<Ast.Stmt> stmts = new ArrayList<Ast.Stmt>();


    if (!n.nm.equals("main")) {
      // 1) Construct a global label of form "<base class name>_<method name>"
      IR.Global label = new IR.Global(cinfo.className() + "_" + n.nm);

      // 2) Add "obj" into the params list as the 0th item
      params.add("obj");
    }

    // 3) Create an Env() containing all params and all local vars
    Env env = new Env();

    // code.add(new IR.LabelDec("Begin"));
    for (Ast.Param param: n.params) {
      env.put(param.nm, param.t);
      params.add(param.nm);
    }

    for (Ast.VarDecl var: n.vars) {
      env.put(var.nm, var.t);
      vars.add(var.nm);
    }

    // 4) Generate IR code for all statements
    for (Ast.Stmt stmt: n.stmts) {
      code.addAll(gen(stmt, cinfo, env));
    }

    if (cinfo.methodType(n.nm) == null) {
      Ast.Stmt returnstmt = new Ast.Return(null);
      code.addAll(gen(returnstmt, cinfo, env));
    }

    // 5) Return an IR.Func with the above

    return new IR.Func(n.nm, params, vars, code);
  } 

  // VarDecl ---
  // Type t;
  // String nm;
  // Exp init;
  //
  // Codegen Guideline: 
  // 1. If init exp exists, generate IR code for it and assign result to var
  // 2. Return generated code (or null if none)
  //
  private static List<IR.Inst> gen(Ast.VarDecl n, ClassInfo cinfo, 
				    Env env) throws Exception {

    List<IR.Inst> code = new ArrayList<IR.Inst>();

    // 1) If init exp not null, generate IR code for it, assign result to var
    if (n.init != null) {
      // Add an assign inst for nm = <code for init>
      CodePack p = gen(n.init, cinfo, env);
      code.addAll(p.code);

      IR.Move move = new IR.Move(new IR.Id(n.nm), p.src);
      code.add(move);
    }

    // 2) return generated code (or null if none)
    if (code.size() > 0) {
      return code;
    } else {
      return null;
    }
  }

  // STATEMENTS

  // Dispatch a generic call to a specific Stmt routine
  // 
  static List<IR.Inst> gen(Ast.Stmt n, ClassInfo cinfo, Env env) throws Exception {
    if (n instanceof Ast.Block)    return gen((Ast.Block) n, cinfo, env);
    if (n instanceof Ast.Assign)   return gen((Ast.Assign) n, cinfo, env);
    if (n instanceof Ast.CallStmt) return gen((Ast.CallStmt) n, cinfo, env);
    if (n instanceof Ast.If)       return gen((Ast.If) n, cinfo, env);
    if (n instanceof Ast.While)    return gen((Ast.While) n, cinfo, env);
    if (n instanceof Ast.Print)    return gen((Ast.Print) n, cinfo, env);
    if (n instanceof Ast.Return)   return gen((Ast.Return) n, cinfo, env);
    throw new GenException("Illegal Ast Stmt: " + n);
  }

  // Block ---
  // Stmt[] stmts;
  //
  static List<IR.Inst> gen(Ast.Block n, ClassInfo cinfo, Env env) throws Exception {

    List<IR.Inst> code = new ArrayList<IR.Inst>();
    List<IR.Inst> temp;

    for (Ast.Stmt stmt: n.stmts) {
      code.addAll(gen(stmt, cinfo, env));
      //List<IR.Inst> insts = gen(stmt, cinfo, env);
      //for (IR.Inst inst: insts) {
      //  code.add(inst);
      //}
    }

    return code;
  }

  // Assign ---
  // Exp lhs, rhs;
  //
  // Codegen Guideline: 
  // 1. call gen() on rhs
  // 2. if lhs is ID, check against Env to see if it's a local var or a param;
  //    if yes, generate an IR.Move instruction
  // 3. otherwise, call genAddr() on lhs, and generate an IR.Store instruction
  //
  static List<IR.Inst> gen(Ast.Assign n, ClassInfo cinfo, Env env) throws Exception {

    List<IR.Inst> code = new ArrayList<IR.Inst>();
    CodePack rhs = gen(n.rhs, cinfo, env);
    code.addAll(rhs.code);

    if (n.lhs instanceof Ast.Id) {
      if (env.containsKey(((Ast.Id) n.lhs).nm)) {
        IR.Dest lhs = new IR.Id(((Ast.Id) n.lhs).nm); // just these 2
        code.add(new IR.Move(lhs, rhs.src));
      } else {
        Ast.Field f = new Ast.Field(new Ast.This(), ((Ast.Id)n.lhs).nm);
        AddrPack p = genAddr(f, cinfo, env);
        code.addAll(p.code);
        //code.add(new IR.Store(gen(rhs.type), p.addr, rhs.src));
      }

    } else {
      AddrPack p = genAddr(n.lhs, cinfo, env);
      code.addAll(p.code);
      // code.add(new IR.Store(gen(p.type), p.addr, rhs.src));
      // code.add(new IR.Load(gen(rhs.type), new IR.Temp(), p.addr));
    }

    return code;
  }

  // CallStmt ---
  // Exp obj; 
  // String nm;
  // Exp[] args;
  //
  //
  static List<IR.Inst> gen(Ast.CallStmt n, ClassInfo cinfo, Env env) throws Exception {
    if (n.obj != null) {
      CodePack p = handleCall(n.obj, n.nm, n.args, cinfo, env, false);
      return p.code;
    }
    throw new GenException("In CallStmt, obj is null " + n);  
  }

  // handleCall
  // ----------
  // Common routine for Call and CallStmt nodes
  //
  // Codegen Guideline: 
  // 1. Invoke gen() on obj, which returns obj's storage address (and type and code)
  // 2. Call getClassInfo() on obj to get base ClassInfo
  // 3. Access the base class's ClassInfo rec to get the method's offset in vtable 
  // 4. Add obj's as the 0th argument to the args list
  // 5. Generate an IR.Load to get the class descriptor from obj's storage
  // 6. Generate another IR.Load to get the method's global label
  // 7. If retFlag is set, prepare a temp for receiving return value; also figure
  //    out return value's type (through method's decl in ClassInfo rec)
  // 8. Generate an indirect call with the global label
  //
  static CodePack handleCall(Ast.Exp obj, String name, Ast.Exp[] args,
			     ClassInfo cinfo, Env env, boolean retFlag) throws Exception {

    List<IR.Inst> code = new ArrayList<IR.Inst>();
    List<IR.Src> srcs = new ArrayList<IR.Src>();


    // 1) Invoke gen() on obj
    CodePack p = gen(obj, cinfo, env);
    srcs.add(p.src);
    code.addAll(p.code);

    Ast.Type type;
    if (p.type == IR.Type.INT) {
      type = new Ast.IntType();
    } else if (p.type == IR.Type.BOOL) {
      type = new Ast.BoolType();
    } else {
      type = new Ast.ObjType(name);
    }

    //Ast.Type type = p.type;

    // 2) Call getClassInfo() on obj to get base ClassInfo
    ClassInfo this_cinfo = getClassInfo(obj, cinfo, env);
    // ClassInfo c = classInfos.get(((AST.ObjType) p.type).nm)

    // 3) Access base class ClassInfo to get method's offset in vtable
    int m_offset = this_cinfo.methodOffset(name);

    for (Ast.Exp e: args) {
      code.addAll(gen(e, cinfo, env).code);
      srcs.add(gen(e, cinfo, env).src);
    }

    IR.Temp t = new IR.Temp();
    IR.Temp t2 = new IR.Temp();


    //for (String mdecl: this_cinfo.vtable) {
    //  if (mdecl.equals(name)) {
    //    break;
    //  }
    //  m_offset += IR.Type.PTR.size;
    //}

    // 4) Add obj's as 0th argument to args list
    //args[0] = obj; wat

    // 5) Generate an IR.Load to get class descriptor from obj's storage
    //AddrPack ap = genAddr((Ast.Field) obj, cinfo, env);
    //IR.Dest dest = new IR.Id(name);
    //code.add(new IR.Load(p.type, dest, p.src));
    IR.Addr addr = new IR.Addr(p.src, 0);
    IR.Load load = new IR.Load(gen(type), t, addr);
    code.add(load);


    // 6)
    load = new IR.Load(gen(type), t2, new IR.Addr(t, m_offset));
    code.add(load);

    if (retFlag) {
      t = new IR.Temp();
      IR.Temp t3 = new IR.Temp();
      IR.Call call = new IR.Call(t2, true, srcs, t);
      code.add(call);
    } else {
      IR.Call call = new IR.Call(t2, true, srcs, null);
      code.add(call);
    }

    IR.Temp t3 = null;
    IR.Call call2 = new IR.Call(new IR.Id(name), true, srcs, t3);

    return new CodePack(p.type, t, code);

  }

  // If ---
  // Exp cond;
  // Stmt s1, s2;
  //
  // (See class notes.)
  //
  static List<IR.Inst> gen(Ast.If n, ClassInfo cinfo, Env env) throws Exception {
    List<IR.Inst> code = new ArrayList<IR.Inst>();
    IR.Label L1 = new IR.Label();
    CodePack p = gen(n.cond, cinfo, env);
    code.addAll(p.code);
    code.add(new IR.CJump(IR.ROP.EQ, p.src, IR.FALSE, L1));
    code.addAll(gen(n.s1, cinfo, env));
    if (n.s2 == null) {
      code.add(new IR.LabelDec(L1));
    } else {
      IR.Label L2 = new IR.Label();
      code.add(new IR.Jump(L2));
      code.add(new IR.LabelDec(L1));
      code.addAll(gen(n.s2, cinfo, env));
      code.add(new IR.LabelDec(L2));
    }

    return code;
  }

  // While ---
  // Exp cond;
  // Stmt s;
  //
  // (See class notes.)
  //
  static List<IR.Inst> gen(Ast.While n, ClassInfo cinfo, Env env) throws Exception {
    List<IR.Inst> code = new ArrayList<IR.Inst>();
    IR.Label L1 = new IR.Label();
    IR.Label L2 = new IR.Label();
    code.add(new IR.LabelDec(L1));
    CodePack p = gen(n.cond, cinfo, env);
    code.addAll(p.code);
    code.add(new IR.CJump(IR.ROP.EQ, p.src, IR.FALSE, L2));
    code.addAll(gen(n.s, cinfo, env));
    code.add(new IR.Jump(L1));
    code.add(new IR.LabelDec(L2));
    return code;
  }
  
  // Print ---
  // PrArg arg;
  //
  // Codegen Guideline: 
  // 1. If arg is null, generate an IR.Call to "printStr" with an empty string arg
  // 2. If arg is StrLit, generate an IR.Call to "printStr"
  // 3. Otherwise, generate IR code for arg, and use its type info
  //    to decide which of the two functions, "printInt" and "printBool",
  //    to call
  //
  static List<IR.Inst> gen(Ast.Print n, ClassInfo cinfo, Env env) throws Exception {
    List<IR.Inst> code = new ArrayList<IR.Inst>();
    List<IR.Src> args = new ArrayList<IR.Src>();

    if (n.arg == null) {
      args.add(new IR.StrLit(""));
      IR.Global printStr = new IR.Global("printStr");
      code.add(new IR.Call(printStr, false, args));
      // IR.Call call = new IR.Call(new IR.Global("print"), false, src, null);
    } else if (n.arg instanceof Ast.StrLit) {
      args.add(new IR.StrLit(n.arg.toString()));
      IR.Global printStr = new IR.Global("printStr");
      code.add(new IR.Call(printStr, false, args));

      /*
        CodePack p = gen(n.arg, cinfo, env);
        code.addAll(p.code);
        srcs.add(p.src);
        IR.Call call = new IR.Call(new IR.Global("printStr"), false, srcs, null);

       */
    } else {
      CodePack p = gen((Ast.Exp) n.arg, cinfo, env);
      code.addAll(p.code);

      if (p.type == IR.Type.INT) {
        args.add(p.src);
        args.add(new IR.IntLit(((Ast.IntLit)n.arg).i));
        IR.Global printInt = new IR.Global("printInt");
        // code.addAll(p.code);
        code.add(new IR.Call(printInt, false, args));
        // code.a
      } else {
        args.add(new IR.BoolLit(((Ast.BoolLit)n.arg).b));
        IR.Global printBool = new IR.Global("printBool");
        // code.addAll(p.code);
        code.add(new IR.Call(printBool, false, args));
      }
    }

    return code;
  }

  // Return ---  
  // Exp val;
  //
  // Codegen Guideline: 
  // 1. If val is non-null, generate IR code for it, and generate an IR.Return
  //    with its value
  // 2. Otherwise, generate an IR.Return with no value
  //
  static List<IR.Inst> gen(Ast.Return n, ClassInfo cinfo, Env env) throws Exception {
    List<IR.Inst> code = new ArrayList<IR.Inst>();

    if (n.val != null) {
      CodePack p;
      p = gen(n.val, cinfo, env);
      code.addAll(p.code);
      code.add(new IR.Return(p.src));
    } else {
      code.add(new IR.Return());
    }

    return code;
  }

  // EXPRESSIONS

  // 1. Dispatch a generic gen() call to a specific gen() routine
  //
  static CodePack gen(Ast.Exp n, ClassInfo cinfo, Env env) throws Exception {
    if (n instanceof Ast.Call)     return gen((Ast.Call) n, cinfo, env);
    if (n instanceof Ast.NewObj)   return gen((Ast.NewObj) n, cinfo, env);
    if (n instanceof Ast.Field)    return gen((Ast.Field) n, cinfo, env);
    if (n instanceof Ast.Id)       return gen((Ast.Id) n, cinfo, env);
    if (n instanceof Ast.This)     return gen((Ast.This) n, cinfo);
    if (n instanceof Ast.IntLit)   return gen((Ast.IntLit) n);
    if (n instanceof Ast.BoolLit)  return gen((Ast.BoolLit) n);
    throw new GenException("Exp node not supported in this codegen: " + n);
  }

  // 2. Dispatch a generic genAddr call to a specific genAddr routine
  //    (Only one LHS Exp needs to be implemented for this assignment)
  //
  static AddrPack genAddr(Ast.Exp n, ClassInfo cinfo, Env env) throws Exception {
    if (n instanceof Ast.Field) return genAddr((Ast.Field) n, cinfo, env);
    throw new GenException(" LHS Exp node not supported in this codegen: " + n);
  }

  // Call ---
  // Exp obj; 
  // String nm;
  // Exp[] args;
  //
  static CodePack gen(Ast.Call n, ClassInfo cinfo, Env env) throws Exception {
    if (n.obj != null)
	return handleCall(n.obj, n.nm, n.args, cinfo, env, true);
    throw new GenException("In Call, obj is null: " + n);  
  } 
  
  // NewObj ---
  // String nm;
  //
  // Codegen Guideline: 
  //  1. Use class name to find the corresponding ClassInfo record from classEnv
  //  2. Find the class's type and object size from the ClassInfo record
  //  3. Cosntruct a malloc call to allocate space for the object
  //  4. Store a pointer to the class's descriptor into the first slot of
  //     the allocated space
  //
  static CodePack gen(Ast.NewObj n, ClassInfo cinfo, Env env) throws Exception {

/*
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    IR0.IntLit arg = new IR0.IntLit(n.len * 4);
    IR0.Temp t = new IR0.Temp();
    code.add(new IR0.Malloc(t, arg));
    return new CodePack(t, code);
*/

    List<IR.Inst> code = new ArrayList<IR.Inst>();
    List<IR.Src> args = new ArrayList<IR.Src>();

    // 1) Use class name to find corresponding ClassInfo record from ClassEnv
    ClassInfo class_info = classEnv.get(cinfo.className());
    IR.Global cname = new IR.Global(cinfo.className());

    // 2) Find class's type and object size from ClassInfo record
    int obj_size = class_info.objSize;
    IR.Type class_type = IR.Type.INT;

    // 3) Construct a malloc call to allocate space for object
    IR.Temp t = new IR.Temp();
    args.add(new IR.IntLit(obj_size + IR.Type.PTR.size));
    IR.Global malloc = new IR.Global("malloc");
    code.add(new IR.Call(malloc, false, args, t));

    // 4) Store ptr to the class's descriptor in first slot of allocated space
    // public Store(Type t, Addr a, Src s)

    IR.Temp t2 = new IR.Temp();
    int offset = class_info.fieldOffset(n.nm);
    // TODO: What is the type?
    IR.Addr addr = new IR.Addr(t2);
    code.add(new IR.Store(class_type, addr, cname ));

    return new CodePack(class_type, t, code);
  }
  
  // Field ---
  // Exp obj; 
  // String nm;
  //

  // 1. gen()
  //
  // Codegen Guideline: 
  //   1.1 Call genAddr() to generate field variable's address
  //   1.2 Add an IR.Load to get its value
  //
  static CodePack gen(Ast.Field n, ClassInfo cinfo, Env env) throws Exception {

    List<IR.Inst> code = new ArrayList<IR.Inst>();

    AddrPack ap = genAddr(n, cinfo, env);
    code.addAll(ap.code);

    IR.Temp t = new IR.Temp();

    IR.Global src = new IR.Global(n.nm);
    IR.Dest dest = new IR.Id(n.nm);
    code.add(new IR.Load(ap.type, dest, ap.addr));
    return new CodePack(ap.type, t, code);
  }
  
  // 2. genAddr()
  //
  // Codegen Guideline: 
  //   2.1 Call gen() on the obj component
  //   2.2 Call getClassInfo() on the obj component to get base ClassInfo
  //   2.3 Access base ClassInfo rec to get field variable's offset
  //   2.4 Generate an IR.Addr based on the offset
  //
  static AddrPack genAddr(Ast.Field n, ClassInfo cinfo, Env env) throws Exception {
    /*
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    CodePack ar = gen(n.ar);
    code.addAll(ar.code);
    IR0.Temp t2 = new IR0.Temp();
    IR0.IntLit intSz = new IR0.IntLit(4);
    return new AddrPack(new IR0.Addr(t2, 0), code);
     */

    List<IR.Inst> code = new ArrayList<IR.Inst>();
    CodePack obj = gen(n.obj, cinfo, env);

    code.addAll(obj.code);
    IR.Temp t2 = new IR.Temp();

    ClassInfo this_cinfo = getClassInfo(n.obj, cinfo, env);
    int offset = this_cinfo.fieldOffset(n.nm);
    // TODO: What is the type?
    return new AddrPack(IR.Type.PTR, new IR.Addr(obj.src, offset), code);
  }
  
  // Id ---
  // String nm;
  //
  // Codegen Guideline: 
  //  1. Check to see if the Id is in the env.
  //  2. If so, it means it is a local variable or a parameter. Just return
  //     a CodePack containing the Id.
  //  3. Otherwise, the Id is an instance variable. Convert it into an
  //     Ast.Field node with Ast.This() as its obj, and invoke the gen() routine 
  //     on this new node
  //
  static CodePack gen(Ast.Id n, ClassInfo cinfo, Env env) throws Exception {
    if (env.containsKey(n.nm)) {
      return new CodePack(gen(env.get(n.nm)), new IR.Id(n.nm));
    } else {
      Ast.Field thisField = new Ast.Field(new Ast.This(), n.nm);
      CodePack p = gen(thisField, cinfo, env);
      return p;
    }
  }

  // This ---
  //
  static CodePack gen(Ast.This n, ClassInfo cinfo) throws Exception {
    return new CodePack(IR.Type.PTR, thisObj);
  }

  // IntLit ---
  // int i;
  //
  static CodePack gen(Ast.IntLit n) throws Exception {
    // return new CodePack(new IR0.IntLit(n.i));
    return  new CodePack(IR.Type.INT, new IR.IntLit(n.i));
  }

  // BoolLit ---
  // boolean b;
  //
  static CodePack gen(Ast.BoolLit n) throws Exception {
    return  new CodePack(IR.Type.BOOL, n.b ? IR.TRUE : IR.FALSE);
  }

  // StrLit ---
  // String s;
  //
  static CodePack gen(Ast.StrLit n) throws Exception {
    return new CodePack(null, new IR.StrLit(n.s));
  }

  // Type mapping (AST -> IR)
  //
  static IR.Type gen(Ast.Type n) throws Exception {
    if (n == null)                  return null;
    if (n instanceof Ast.IntType)   return IR.Type.INT;
    if (n instanceof Ast.BoolType)  return IR.Type.BOOL;
    if (n instanceof Ast.ArrayType) return IR.Type.PTR;
    if (n instanceof Ast.ObjType)   return IR.Type.PTR;
    throw new GenException("Invalid Ast type: " + n);
  }

  static Ast.Type gen(IR.Type n) throws Exception {
    if (n == null)                  return null;
    if (n == IR.Type.INT)           return new Ast.IntType();
    if (n == IR.Type.BOOL)          return new Ast.BoolType();
    if (n == IR.Type.PTR)           return new Ast.ObjType("temp");
    throw new GenException("Invalid IR type: " + n);
  }
}
