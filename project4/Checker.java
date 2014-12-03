// This is supporting software for CS322 Compilers and Language Design II
// Copyright (c) Portland State University
//
// Static analysis for miniJava (F14) ((A starting version.)
//  1. Type-checking
//  2. Detecting missing return statement
//  3. (Optional) Detecting uninitialized variables
//
// (For CS321 Fall 2014 - Jingke Li)
// Will Hopper

import java.util.*;
import java.io.*;
import ast.*;

public class Checker {

  static class TypeException extends Exception {
    public TypeException(String msg) { super(msg); }
  }

  //------------------------------------------------------------------------------
  // ClassInfo
  //----------
  // For easy access to class hierarchies (i.e. finding parent's info).
  //
  static class ClassInfo {
    Ast.ClassDecl cdecl;  // classDecl AST
    ClassInfo parent;     // pointer to parent

    ClassInfo(Ast.ClassDecl cdecl, ClassInfo parent) {
      this.cdecl = cdecl;
      this.parent = parent;
    }

    // Return the name of this class
    //
    String className() { return cdecl.nm; }

    // Given a method name, return the method's declaration
    // - if the method is not found in the current class, recursively
    //   search ancestor classes; return null if all fail
    //
    Ast.MethodDecl findMethodDecl(String mname) {
      for (Ast.MethodDecl mdecl: cdecl.mthds)
        if (mdecl.nm.equals(mname))
          return mdecl;
      if (parent != null)
        return parent.findMethodDecl(mname);
      return null;
    }

    // Given a field name, return the field's declaration
    // - if the field is not found in the current class, recursively
    //   search ancestor classes; return null if all fail
    //
    Ast.VarDecl findFieldDecl(String fname) {
      for (Ast.VarDecl fdecl: cdecl.flds) {
        if (fdecl.nm.equals(fname))
          return fdecl;
      }
      if (parent != null)
        return parent.findFieldDecl(fname);
      return null;
    }
  }

  //------------------------------------------------------------------------------
  // Global Variables
  // ----------------
  // For type-checking:
  // classEnv  - an environment (a className-classInfo mapping) for class declarations
  // typeEnv   - an environment (a var-type mapping) for a method's params and local vars
  // thisCInfo - points to the current class's ClassInfo
  // thisMDecl - points to the current method's MethodDecl
  //
  // For other analyses:
  // condHasReturn - if set to false indicates a missing return statement
  //
  private static HashMap<String, ClassInfo> classEnv = new HashMap<String, ClassInfo>();
  private static HashMap<String, Ast.Type> typeEnv = new HashMap<String, Ast.Type>();
  private static ClassInfo thisCInfo = null;
  private static Ast.MethodDecl thisMDecl = null;
  private static boolean condHasReturn = true;

  //------------------------------------------------------------------------------
  // Type Compatibility Routines
  // ---------------------------

  // Returns true if tsrc is assignable to tdst.
  //
  // Pseudo code:
  //   if tdst==tsrc or both are the same basic type
  //     return true
  //   else if both are ArrayType // structure equivalence
  //     return assignable result on their element types
  //   else if both are ObjType   // name equivalence
  //     if (their class names match, or
  //         tdst's class name matches an tsrc ancestor's class name)
  //       return true
  //   else
  //     return false
  //
  private static boolean assignable(Ast.Type tdst, Ast.Type tsrc) throws Exception {
    if (tdst == tsrc
        || (tdst instanceof Ast.IntType) && (tsrc instanceof Ast.IntType)
        || (tdst instanceof Ast.BoolType) && (tsrc instanceof Ast.BoolType)) {
      return true;

    } else if((tdst instanceof Ast.ArrayType) && (tsrc instanceof Ast.ArrayType)) {
      return(assignable(((Ast.ArrayType)tdst).et, ((Ast.ArrayType)tsrc).et));

/*    } else if((tdst instanceof Ast.ArrayType) && (tsrc instanceof Ast.IntType)) {
      if (((Ast.ArrayType)tdst).et instanceof Ast.IntType) {
        return true;
      } else {
        return false;
      }
*/
    } else if((tdst instanceof Ast.ObjType) && (tsrc instanceof Ast.ObjType)) {
      ClassInfo srcClass = classEnv.get(((Ast.ObjType)tsrc).nm);
      if(((Ast.ObjType)tdst).nm.equals(((Ast.ObjType)tsrc).nm) /*|| tsrc ancestor name*/) {
        return true;
      } else if (srcClass != null) {
        while (srcClass != null && srcClass.parent != null) {
          if (srcClass.parent.className().equals(((Ast.ObjType)tdst).nm)) {
            return true;
          }
          srcClass = srcClass.parent;
        }
      }
    }

    return false;
  }

  // Returns true if t1 and t2 can be compared with "==" or "!=".
  //
  private static boolean comparable(Ast.Type t1, Ast.Type t2) throws Exception {
    return assignable(t1,t2) || assignable(t2,t1);
  }

  //------------------------------------------------------------------------------
  // The Main Routine
  //-----------------
  //
  public static void main(String [] args) throws Exception {
    try {
      if (args.length == 1) {
        FileInputStream stream = new FileInputStream(args[0]);
        Ast.Program p = new astParser(stream).Program();
        stream.close();
        check(p);
      } else {
        System.out.println("Need a file name as command-line argument.");
      }
    } catch (TypeException e) {
      System.err.print(e + "\n");
    } catch (Exception e) {
      System.err.print(e + "\n");
    }
  }

  //------------------------------------------------------------------------------
  // Checker Routines for Individual AST Nodes
  //------------------------------------------

  // Program ---
  //  ClassDecl[] classes;
  //
  // 1. Sort ClassDecls, so parent will be visited before children.
  // 2. For each ClassDecl, create an ClassInfo (with link to parent if exists),
  //    and add to classEnv.
  // 3. Actual type-checking traversal over ClassDecls.
  //
  static void check(Ast.Program n) throws Exception {
    Ast.ClassDecl[] classes = topoSort(n.classes);
    for (Ast.ClassDecl c: classes) {
      ClassInfo pcinfo = (c.pnm == null) ? null : classEnv.get(c.pnm);
      classEnv.put(c.nm, new ClassInfo(c, pcinfo));
    }
    for (Ast.ClassDecl c: classes) {
      check(c);
    }

  }

  // Utility routine
  // - Sort ClassDecls based on parent-chidren relationship.
  //
  private static Ast.ClassDecl[] topoSort(Ast.ClassDecl[] classes) {
    List<Ast.ClassDecl> cl = new ArrayList<Ast.ClassDecl>();
    Vector<String> done = new Vector<String>();
    int cnt = classes.length;
    while (cnt > 0) {
      for (Ast.ClassDecl cd: classes)
        if (!done.contains(cd.nm)
            && ((cd.pnm == null) || done.contains(cd.pnm))) {
          cl.add(cd);
          done.add(cd.nm);
          cnt--;
            }
    }
    return cl.toArray(new Ast.ClassDecl[0]);
  }

  // ClassDecl ---
  //  String nm, pnm;
  //  VarDecl[] flds;
  //  MethodDecl[] mthds;
  //
  //  1. Set thisCInfo pointer to this class's ClassInfo, and reset
  //     typeEnv to empty.
  //  2. Recursively check n.flds and n.mthds.
  //
  static void check(Ast.ClassDecl n) throws Exception {
    ClassInfo cur_class = classEnv.get(n.nm);
    thisCInfo = cur_class;
    typeEnv.clear();

    // Check each field
    for (Ast.VarDecl vd: n.flds) {
      check(vd);
    }

    // Check each method
    for (Ast.MethodDecl md: n.mthds) {
      check(md);
    }
  }

  // MethodDecl ---
  //  Type t;
  //  String nm;
  //  Param[] params;
  //  VarDecl[] vars;
  //  Stmt[] stmts;
  //
  //  1. Set thisMDecl pointer and reset typeEnv to empty.
  //  2. Recursively check n.params, n.vars, and n.stmts.
  //  3. For each VarDecl, add a new name-type binding to typeEnv.
  //
  static void check(Ast.MethodDecl n) throws Exception {
    boolean has_return = false;
    boolean requires_return = false;
    if (n.t != null) {
      requires_return = true;
    }

    thisMDecl = n;
    typeEnv.clear();

    for(Ast.Param p: n.params) {
      check(p);
    }

    for(Ast.VarDecl vd: n.vars) {
      check(vd);
      typeEnv.put(vd.nm, vd.t);
    }

    for(Ast.Stmt s: n.stmts) {
      // We need to make sure we get a return stmt if we need one
      if (s instanceof Ast.Return) {
        has_return = true;
      }
      check(s);
    }

    if (requires_return == true && has_return == false) {
      throw new TypeException("(In MethodDecl) Missing return statement");
    }
  }

  // Param ---
  //  Type t;
  //  String nm;
  //
  //  If n.t is ObjType, make sure its corresponding class exists.
  //
  static void check(Ast.Param n) throws Exception {
    if (n.t instanceof Ast.ObjType) {
      ClassInfo obj = classEnv.get(((Ast.ObjType)n.t).nm);
      if (obj == null) {
        throw new TypeException("(In Param) Can't find class " + ((Ast.ObjType)n.t).nm);
      }
    }

    // Make sure arg type is correct
  }

  // VarDecl ---
  //  Type t;
  //  String nm;
  //  Exp init;
  //
  //  1. If n.t is ObjType, make sure its corresponding class exists.
  //  2. If n.init exists, make sure it is assignable to the var.
  //
  static void check(Ast.VarDecl n) throws Exception {
    if (n.t instanceof Ast.ObjType) {
      ClassInfo obj = classEnv.get(((Ast.ObjType)n.t).nm);
      if (obj == null) {
        throw new TypeException("(In VarDecl) Can't find class " + ((Ast.ObjType)n.t).nm);
      }
    }

    if (n.init != null) {
      Ast.Type init_type = check(n.init);
      if (!assignable(n.t, init_type)) {
        throw new TypeException("(In VarDecl) Init expr not assignable");
      }
    }
  }

  // STATEMENTS

  // Dispatch a generic check call to a specific check routine
  //
  static void check(Ast.Stmt n) throws Exception {
    if (n instanceof Ast.Block)   check((Ast.Block) n);
    else if (n instanceof Ast.Assign)   check((Ast.Assign) n);
    else if (n instanceof Ast.CallStmt) check((Ast.CallStmt) n);
    else if (n instanceof Ast.If)   check((Ast.If) n);
    else if (n instanceof Ast.While)    check((Ast.While) n);
    else if (n instanceof Ast.Print)    check((Ast.Print) n);
    else if (n instanceof Ast.Return)   check((Ast.Return) n);
    else
      throw new TypeException("Illegal Ast Stmt: " + n);
  }

  // Block ---
  //  Stmt[] stmts;
  // Recursively check each stmt in the block?
  static void check(Ast.Block n) throws Exception {
    for (Ast.Stmt s: n.stmts) {
      check(s);
    }
  }

  // Assign ---
  //  Exp lhs;
  //  Exp rhs;
  //
  //  Make sure n.rhs is assignable to n.lhs.
  //
  static void check(Ast.Assign n) throws Exception {
    // Figure out where lhs and rhs were declared
    Ast.Type lhs_type = check(n.lhs);
    Ast.Type rhs_type = check(n.rhs);

    if (!(assignable(lhs_type, rhs_type))) {
      throw new TypeException("(In Assign) lhs and rhs types don't match: " + lhs_type
        + " <- " + rhs_type);
    }
  }

  // CallStmt ---
  //  Exp obj;
  //  String nm;
  //  Exp[] args;
  //
  //  1. Check that n.obj is ObjType and the corresponding class exists.
  //  2. Check that the method n.nm exists.
  //  3. Check that the count and types of the actual arguments match those of
  //     the formal parameters.
  //
  static void check(Ast.CallStmt n) throws Exception {
    // 1: Check that n.obj is ObjType and the corresponding class exists
    Ast.Type obj_type;

    if ((n.obj instanceof Ast.IntLit)) {
      obj_type = new Ast.IntType();
    } else if ((n.obj instanceof Ast.BoolLit)) {
      obj_type = new Ast.BoolType();
    } else {
      obj_type = typeEnv.get(n.obj.toString());
    }

    if (obj_type instanceof Ast.ObjType) {
      ClassInfo obj = classEnv.get(((Ast.ObjType)obj_type).nm);
      if (obj == null) {
        throw new TypeException("(In CallStmt) Missing class");
      }
    }
    // 2: check that n.nm method exists
    Ast.MethodDecl cur_method = thisCInfo.findMethodDecl(n.nm);
    if (cur_method == null) {
      throw new TypeException("(In CallStmt) Can't find method " + n.nm);
    }

    // 3: Check that the count and types of the args match those of formal params
    int given_arg_count = n.args.length;
    int required_arg_count = cur_method.params.length;
    if (given_arg_count != required_arg_count) {
      throw new TypeException("(In CallStmt) Wrong number of arguments: " +
          given_arg_count + " for " + required_arg_count);
    }

    // Now make sure the types are correct
    for (int i=0; i < required_arg_count; ++i) {
      Ast.Type required_type = cur_method.params[i].t;
      Ast.Type got_type;
      if (n.args[i] instanceof Ast.IntLit) {
        got_type = new Ast.IntType();
      } else if (n.args[i] instanceof Ast.BoolLit) {
        got_type = new Ast.BoolType();
      } else {
        got_type = typeEnv.get(n.args[i].toString());
      }

      if (!((required_type instanceof Ast.IntType) && (got_type instanceof Ast.IntType))
      && !((required_type instanceof Ast.BoolType) && (got_type instanceof Ast.BoolType)) ){
        throw new TypeException("(In CallStmt) Param and arg types don't match: " +
          required_type + " vs. " + got_type);
      }
    }
  }

  // If ---
  //  Exp cond;
  //  Stmt s1, s2;
  //
  //  Make sure n.cond is boolean.
  //
  static void check(Ast.If n) throws Exception {
    Ast.Type cond_type = check(n.cond);

    if (!(cond_type instanceof Ast.BoolType)) {
      throw new TypeException("(In If) Cond exp type is not boolean: " + cond_type);
    }

    if (n.s1 != null) {
      check(n.s1);
    }

    if (n.s2 != null) {
      check(n.s2);
    }
  }

  // While ---
  //  Exp cond;
  //  Stmt s;
  //
  //  Make sure n.cond is boolean.
  //
  static void check(Ast.While n) throws Exception {
    if (!(n.cond instanceof Ast.BoolLit) && !(n.cond instanceof Ast.Binop) ) {
      throw new TypeException("While: cond must be boolean");
    }

    check(n.s);
  }

  // Print ---
  //  PrArg arg;
  //
  //  Make sure n.arg is integer, boolean, or string.
  //
  static void check(Ast.Print n) throws Exception {
    if (n.arg == null) {
      return;
    }

    if (n.arg instanceof Ast.StrLit) {
      return;
    }

    Ast.Type arg_type = check((Ast.Exp) n.arg);

    if (!(arg_type instanceof Ast.IntType || arg_type instanceof Ast.BoolType)) {
      throw new TypeException("(In Print) Arg type is not int, boolean, or string: "
        + arg_type);
    }
  }

  // Return ---
  //  Exp val;
  //
  //  If n.val exists, make sure it matches the expected return type.
  //
  static void check(Ast.Return n) throws Exception {
    Ast.Type method_type = thisMDecl.t;
    if (n.val != null) {
      // Find the method decl and grab its type
      Ast.Type val_type = check(n.val);

      if (method_type != null) {
        String method_type_string = method_type.toString();
        String val_type_string = val_type.toString();
        if (method_type == null) {
          throw new TypeException("(In Return) Unexpected return value");
        } else if (!method_type_string.equals(val_type_string)) {
          throw new TypeException("(In Return) Return type mismatch: " + method_type
          + " <- " + val_type);
        }
      } else {
        if (val_type != null) {
          throw new TypeException("(In Return) Unexpected return value");
        }
      }
    } else {
      if (method_type != null) {
        throw new TypeException("(In Return) Missing return value of type "
        + method_type);
      }
    }
  }

  // EXPRESSIONS

  // Dispatch a generic check call to a specific check routine
  //
  static Ast.Type check(Ast.Exp n) throws Exception {
    if (n instanceof Ast.Binop)    return check((Ast.Binop) n);
    if (n instanceof Ast.Unop)     return check((Ast.Unop) n);
    if (n instanceof Ast.Call)     return check((Ast.Call) n);
    if (n instanceof Ast.NewArray) return check((Ast.NewArray) n);
    if (n instanceof Ast.ArrayElm) return check((Ast.ArrayElm) n);
    if (n instanceof Ast.NewObj)   return check((Ast.NewObj) n);
    if (n instanceof Ast.Field)    return check((Ast.Field) n);
    if (n instanceof Ast.Id)     return check((Ast.Id) n);
    if (n instanceof Ast.This)     return check((Ast.This) n);
    if (n instanceof Ast.IntLit)   return check((Ast.IntLit) n);
    if (n instanceof Ast.BoolLit)  return check((Ast.BoolLit) n);
    throw new TypeException("Exp node not recognized: " + n);
  }

  // Binop ---
  //  BOP op;
  //  Exp e1,e2;
  //
  //  Make sure n.e1's and n.e2's types are legal with respect to n.op.
  //
  static Ast.Type check(Ast.Binop n) throws Exception {
    // Need to get types of exps by looking at VarDecls
    Ast.Type e1_type = check(n.e1);
    Ast.Type e2_type = check(n.e2);

    if (n.op == Ast.BOP.EQ || n.op == Ast.BOP.NE) {
      if (!(e1_type.getClass().getName().equals(e2_type.getClass().getName()))) {
        throw new TypeException("(In Binop) Operand types don't match: " + e1_type
          + " " + n.op + " " + e2_type);
      }
      return new Ast.BoolType();
    } else if (n.op == Ast.BOP.AND || n.op == Ast.BOP.OR) {
      if (!((e1_type instanceof Ast.BoolType) && (e2_type instanceof Ast.BoolType))) {
        throw new TypeException("(In Binop) Operand types don't match: " + e1_type
        + " " + n.op + " " + e2_type);
      }
      return new Ast.BoolType();
    } else if (n.op == Ast.BOP.SUB | n.op == Ast.BOP.ADD || n.op == Ast.BOP.MUL || n.op == Ast.BOP.DIV) {
      if (!((e1_type instanceof Ast.IntType) && (e2_type instanceof Ast.IntType))) {
        throw new TypeException("(In Binop) Operand types don't match: " + e1_type
        + " " + n.op + " " + e2_type);
      }
      return new Ast.IntType();
    } else {
       if (!((e1_type instanceof Ast.IntType) && (e2_type instanceof Ast.IntType))) {
        throw new TypeException("(In Binop) Operand types don't match: " + e1_type
        + " " + n.op + " " + e2_type);
      }
      return new Ast.BoolType();
    }
  }

  // Unop ---
  //  UOP op;
  //  Exp e;
  //
  //  Make sure n.e's type is legal with respect to n.op.
  //
  static Ast.Type check(Ast.Unop n) throws Exception {
    Ast.Type e_type = check(n.e);

    if (n.op == Ast.UOP.NOT) {
      if (!(e_type instanceof Ast.BoolType)) {
        throw new TypeException("(In Unop) Bad operand type: " + n.op + " " + e_type);
      } else {
        return new Ast.BoolType();
      }
    } else {
      if (!(e_type instanceof Ast.IntType)) {
        throw new TypeException("(In Unop) Bad operand type: " + n.op + " " + e_type);
      } else {
        return new Ast.IntType();
      }
    }
  }

  // Call ---
  //  Exp obj;
  //  String nm;
  //  Exp[] args;
  //
  //  (See the hints in CallStmt.)
  //  In addition, this routine needs to return the method's return type.
  //
  static Ast.Type check(Ast.Call n) throws Exception {
    Ast.Type obj_type = check(n.obj);
    Ast.Type cur_method_type = thisMDecl.t;
    Ast.MethodDecl cur_method_decl = thisCInfo.findMethodDecl(n.nm);

    // First make sure arg count is the same
    int given_arg_count = n.args.length;
    int required_arg_count = cur_method_decl.params.length;

    if (given_arg_count != required_arg_count) {
      throw new TypeException("(In Call) Param and arg counts don't match: " +
          required_arg_count + " vs. " + given_arg_count);
    }

    // Now make sure the types are correct
    for (int i=0; i < required_arg_count; ++i) {
      Ast.Type required_type = cur_method_decl.params[i].t;
      Ast.Type got_type;
      if (n.args[i] instanceof Ast.IntLit) {
        got_type = new Ast.IntType();
      } else if (n.args[i] instanceof Ast.BoolLit) {
        got_type = new Ast.BoolType();
      } else {
        got_type = typeEnv.get(n.args[i].toString());
      }

      if (!((required_type instanceof Ast.IntType) && (got_type instanceof Ast.IntType))
      && !((required_type instanceof Ast.BoolType) && (got_type instanceof Ast.BoolType)) ){
        throw new TypeException("(In Call) Param and arg types don't match: " +
          required_type + " vs. " + got_type);
      }
    }

    return cur_method_decl.t;
  }

  // NewArray ---
  //  Type et;
  //  int len;
  //
  //  1. Verify that n.et is either integer or boolean.
  //  2. Varify that n.len is non-negative.
  //  (Note: While the AST representation allows these cases to happen, our
  //  miniJava parser does not, so these checks are not very meaningful.)
  //
  static Ast.Type check(Ast.NewArray n) throws Exception {
    if (n.et instanceof Ast.IntType || n.et instanceof Ast.BoolType) {
      if (n.len >= 0) {
        return new Ast.ArrayType(n.et);
      } else {
        throw new TypeException("(In NewArray) Array length is negative");
      }
    } else {
      throw new TypeException("(In NewArray) Not an integer or boolean array");
    }
  }

  // ArrayElm ---
  //  Exp ar, idx;
  //
  //  Varify that n.ar is array and n.idx is integer.
  //
  static Ast.Type check(Ast.ArrayElm n) throws Exception {
    Ast.Type ar_type = check(n.ar);
    Ast.Type idx_type = check(n.idx);

    if ((ar_type instanceof Ast.ArrayType)) {
      if (idx_type instanceof Ast.IntType) {
        return new Ast.ArrayType(idx_type);
      } else {
        throw new TypeException("(In ArrayElm) Index is not integer: " + idx_type);
      }
    } else {
      throw new TypeException("(In ArrayElm) Object is not array: " + ar_type);
    }
  }

  // NewObj ---
  //  String nm;
  //
  //  Verify that the corresponding class exists.
  //
  static Ast.Type check(Ast.NewObj n) throws Exception {
    ClassInfo thisClass = classEnv.get(n.nm);
    if (thisClass != null) {
      return new Ast.ObjType(n.nm);
    } else {
      throw new TypeException("(In NewObj) Can't find class " + n.nm);
    }
  }

  // Field ---
  //  Exp obj;
  //  String nm;
  //
  //  1. Verify that n.onj is ObjType, and its corresponding class exists.
  //  2. Verify that n.nm is a valid field in the object.
  //
  static Ast.Type check(Ast.Field n) throws Exception {
    Ast.Type obj_type = check(n.obj);
    if (!(obj_type instanceof Ast.ObjType)) {
      throw new TypeException("(In Field) Object is not of ObjType: " + obj_type);
    }

    // Verify corresponding class exists
    ClassInfo obj_class_decl = classEnv.get(((Ast.ObjType)obj_type).nm);
    if (obj_class_decl == null) {
      throw new TypeException("(In Field) Class doesn't exist " + (((Ast.ObjType)obj_type).nm));
    }

    // Verify that n.nm is a valid field in the object
    // Make sure a vardecl exists for this field
    Ast.VarDecl valid_field = obj_class_decl.findFieldDecl(n.nm.toString());
    if (valid_field == null) {
      throw new TypeException("(In Field) Can't find field " + n.nm);
    }

    return obj_type;
  }

  // Id ---
  //  String nm;
  //
  //  1. Check if n.nm is in typeEnv. If so, the Id is a param or a local var.
  //     Obtain and return its type.
  //  2. Otherwise, the Id is a field variable. Find and return its type (through
  //     the current ClassInfo).
  //
  static Ast.Type check(Ast.Id n) throws Exception {
    Ast.Type idType = typeEnv.get(n.nm);
    if (idType != null) {
      return idType;
    } else {
      // Doesn't exist in typeEnv map, so this ID is a field.
      Ast.VarDecl cur_decl = thisCInfo.findFieldDecl(n.nm);
      if(cur_decl != null) {
        return cur_decl.t;
      } else {
        throw new TypeException("(In Id) Can't find variable " + n.nm);
      }
    }
  }

  // This ---
  //
  //  Find and return an ObjType that corresponds to the current class
  //  (through the current ClassInfo).
  //
  static Ast.Type check(Ast.This n) {
    return new Ast.ObjType(thisCInfo.className());
  }

  // Literals
  //
  public static Ast.Type check(Ast.IntLit n) {
    return Ast.IntType;
  }

  public static Ast.Type check(Ast.BoolLit n) {
    return Ast.BoolType;
  }

  public static void check(Ast.StrLit n) {
    // nothing to check or return
  }
}
