/* Generated By:JavaCC: Do not edit this line. Lexer1TokenManager.java */
import java.io.*;

/** Token Manager. */
public class Lexer1TokenManager implements Lexer1Constants
{

  /** Debug output. */
  public static  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public static  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private static final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x40L) != 0L)
         {
            jjmatchedKind = 15;
            return 0;
         }
         return -1;
      default :
         return -1;
   }
}
private static final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
static private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
static private int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 47:
         return jjMoveStringLiteralDfa1_0(0x40L);
      default :
         return jjMoveNfa_0(5, 0);
   }
}
static private int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 42:
         if ((active0 & 0x40L) != 0L)
            return jjStopAtPos(1, 6);
         break;
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 105;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 5:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 13)
                        kind = 13;
                     jjCheckNAdd(53);
                  }
                  else if ((0x5000ac0200000000L & l) != 0L)
                  {
                     if (kind > 15)
                        kind = 15;
                  }
                  else if ((0x2800530000000000L & l) != 0L)
                  {
                     if (kind > 16)
                        kind = 16;
                  }
                  else if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 58;
                  else if (curChar == 34)
                     jjCheckNAddTwoStates(55, 56);
                  if (curChar == 62)
                     jjCheckNAdd(62);
                  else if (curChar == 60)
                     jjCheckNAdd(62);
                  else if (curChar == 33)
                     jjCheckNAdd(62);
                  else if (curChar == 61)
                     jjCheckNAdd(62);
                  else if (curChar == 47)
                     jjstateSet[jjnewStateCnt++] = 0;
                  break;
               case 0:
                  if (curChar == 47)
                     jjCheckNAddStates(0, 2);
                  break;
               case 1:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     jjCheckNAddStates(0, 2);
                  break;
               case 2:
                  if ((0x2400L & l) != 0L && kind > 5)
                     kind = 5;
                  break;
               case 3:
                  if (curChar == 10 && kind > 5)
                     kind = 5;
                  break;
               case 4:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 52:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjstateSet[jjnewStateCnt++] = 52;
                  break;
               case 53:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 13)
                     kind = 13;
                  jjCheckNAdd(53);
                  break;
               case 54:
                  if (curChar == 34)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 55:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 56:
                  if (curChar == 34 && kind > 14)
                     kind = 14;
                  break;
               case 57:
                  if ((0x5000ac0200000000L & l) != 0L && kind > 15)
                     kind = 15;
                  break;
               case 58:
                  if (curChar == 38 && kind > 15)
                     kind = 15;
                  break;
               case 59:
                  if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 58;
                  break;
               case 62:
                  if (curChar == 61 && kind > 15)
                     kind = 15;
                  break;
               case 63:
                  if (curChar == 61)
                     jjCheckNAdd(62);
                  break;
               case 64:
                  if (curChar == 33)
                     jjCheckNAdd(62);
                  break;
               case 65:
                  if (curChar == 60)
                     jjCheckNAdd(62);
                  break;
               case 66:
                  if (curChar == 62)
                     jjCheckNAdd(62);
                  break;
               case 67:
                  if ((0x2800530000000000L & l) != 0L && kind > 16)
                     kind = 16;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 5:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 12)
                        kind = 12;
                     jjCheckNAdd(52);
                  }
                  else if ((0x2800000028000000L & l) != 0L)
                  {
                     if (kind > 16)
                        kind = 16;
                  }
                  else if (curChar == 124)
                     jjstateSet[jjnewStateCnt++] = 60;
                  if (curChar == 83)
                     jjAddStates(3, 4);
                  else if (curChar == 116)
                     jjAddStates(5, 6);
                  else if (curChar == 105)
                     jjAddStates(7, 8);
                  else if (curChar == 112)
                     jjAddStates(9, 10);
                  else if (curChar == 101)
                     jjAddStates(11, 12);
                  else if (curChar == 124)
                  {
                     if (kind > 15)
                        kind = 15;
                  }
                  else if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 49;
                  else if (curChar == 102)
                     jjstateSet[jjnewStateCnt++] = 46;
                  else if (curChar == 109)
                     jjstateSet[jjnewStateCnt++] = 42;
                  else if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 39;
                  else if (curChar == 119)
                     jjstateSet[jjnewStateCnt++] = 34;
                  else if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 29;
                  else if (curChar == 98)
                     jjstateSet[jjnewStateCnt++] = 26;
                  else if (curChar == 118)
                     jjstateSet[jjnewStateCnt++] = 19;
                  else if (curChar == 115)
                     jjstateSet[jjnewStateCnt++] = 15;
                  else if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 9;
                  break;
               case 1:
                  jjAddStates(0, 2);
                  break;
               case 6:
                  if (curChar == 115 && kind > 9)
                     kind = 9;
                  break;
               case 7:
                  if (curChar == 115)
                     jjCheckNAdd(6);
                  break;
               case 8:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 7;
                  break;
               case 9:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 10:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 9;
                  break;
               case 11:
                  if (curChar == 99 && kind > 9)
                     kind = 9;
                  break;
               case 12:
               case 77:
                  if (curChar == 105)
                     jjCheckNAdd(11);
                  break;
               case 13:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 14:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 15:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 16:
                  if (curChar == 115)
                     jjstateSet[jjnewStateCnt++] = 15;
                  break;
               case 17:
                  if (curChar == 100 && kind > 9)
                     kind = 9;
                  break;
               case 18:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 19:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 18;
                  break;
               case 20:
                  if (curChar == 118)
                     jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 21:
                  if (curChar == 110 && kind > 9)
                     kind = 9;
                  break;
               case 22:
                  if (curChar == 97)
                     jjCheckNAdd(21);
                  break;
               case 23:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 22;
                  break;
               case 24:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 23;
                  break;
               case 25:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 24;
                  break;
               case 26:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 25;
                  break;
               case 27:
                  if (curChar == 98)
                     jjstateSet[jjnewStateCnt++] = 26;
                  break;
               case 28:
                  if (curChar == 119 && kind > 9)
                     kind = 9;
                  break;
               case 29:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 28;
                  break;
               case 30:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 29;
                  break;
               case 31:
                  if (curChar == 101 && kind > 9)
                     kind = 9;
                  break;
               case 32:
                  if (curChar == 108)
                     jjCheckNAdd(31);
                  break;
               case 33:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 32;
                  break;
               case 34:
                  if (curChar == 104)
                     jjstateSet[jjnewStateCnt++] = 33;
                  break;
               case 35:
                  if (curChar == 119)
                     jjstateSet[jjnewStateCnt++] = 34;
                  break;
               case 36:
                  if (curChar == 114)
                     jjCheckNAdd(21);
                  break;
               case 37:
                  if (curChar == 117)
                     jjstateSet[jjnewStateCnt++] = 36;
                  break;
               case 38:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 37;
                  break;
               case 39:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 38;
                  break;
               case 40:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 39;
                  break;
               case 41:
                  if (curChar == 105)
                     jjCheckNAdd(21);
                  break;
               case 42:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 41;
                  break;
               case 43:
                  if (curChar == 109)
                     jjstateSet[jjnewStateCnt++] = 42;
                  break;
               case 44:
               case 74:
                  if (curChar == 115)
                     jjCheckNAdd(31);
                  break;
               case 45:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 44;
                  break;
               case 46:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 45;
                  break;
               case 47:
                  if (curChar == 102)
                     jjstateSet[jjnewStateCnt++] = 46;
                  break;
               case 48:
                  if (curChar == 116 && kind > 9)
                     kind = 9;
                  break;
               case 49:
                  if (curChar == 117)
                     jjCheckNAdd(48);
                  break;
               case 50:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 49;
                  break;
               case 51:
               case 52:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAdd(52);
                  break;
               case 55:
                  jjAddStates(13, 14);
                  break;
               case 57:
               case 60:
                  if (curChar == 124 && kind > 15)
                     kind = 15;
                  break;
               case 61:
                  if (curChar == 124)
                     jjstateSet[jjnewStateCnt++] = 60;
                  break;
               case 67:
                  if ((0x2800000028000000L & l) != 0L && kind > 16)
                     kind = 16;
                  break;
               case 68:
                  if (curChar == 101)
                     jjAddStates(11, 12);
                  break;
               case 69:
                  if (curChar == 100)
                     jjCheckNAdd(6);
                  break;
               case 70:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 69;
                  break;
               case 71:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 70;
                  break;
               case 72:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 71;
                  break;
               case 73:
                  if (curChar == 120)
                     jjstateSet[jjnewStateCnt++] = 72;
                  break;
               case 75:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 74;
                  break;
               case 76:
                  if (curChar == 112)
                     jjAddStates(9, 10);
                  break;
               case 78:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 77;
                  break;
               case 79:
                  if (curChar == 98)
                     jjstateSet[jjnewStateCnt++] = 78;
                  break;
               case 80:
                  if (curChar == 117)
                     jjstateSet[jjnewStateCnt++] = 79;
                  break;
               case 81:
                  if (curChar == 108)
                     jjCheckNAdd(21);
                  break;
               case 82:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 81;
                  break;
               case 83:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 82;
                  break;
               case 84:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 83;
                  break;
               case 85:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 84;
                  break;
               case 86:
                  if (curChar == 105)
                     jjAddStates(7, 8);
                  break;
               case 87:
                  if (curChar == 110)
                     jjCheckNAdd(48);
                  break;
               case 88:
                  if (curChar == 102 && kind > 9)
                     kind = 9;
                  break;
               case 89:
                  if (curChar == 116)
                     jjAddStates(5, 6);
                  break;
               case 90:
                  if (curChar == 117)
                     jjCheckNAdd(31);
                  break;
               case 91:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 90;
                  break;
               case 92:
                  if (curChar == 105)
                     jjCheckNAdd(6);
                  break;
               case 93:
                  if (curChar == 104)
                     jjstateSet[jjnewStateCnt++] = 92;
                  break;
               case 94:
                  if (curChar == 83)
                     jjAddStates(3, 4);
                  break;
               case 95:
                  if (curChar == 103 && kind > 9)
                     kind = 9;
                  break;
               case 96:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 95;
                  break;
               case 97:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 96;
                  break;
               case 98:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 97;
                  break;
               case 99:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 98;
                  break;
               case 100:
                  if (curChar == 109 && kind > 9)
                     kind = 9;
                  break;
               case 101:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 100;
                  break;
               case 102:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 101;
                  break;
               case 103:
                  if (curChar == 115)
                     jjstateSet[jjnewStateCnt++] = 102;
                  break;
               case 104:
                  if (curChar == 121)
                     jjstateSet[jjnewStateCnt++] = 103;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(0, 2);
                  break;
               case 55:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(13, 14);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 105 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static private int jjMoveStringLiteralDfa0_1()
{
   switch(curChar)
   {
      case 42:
         return jjMoveStringLiteralDfa1_1(0x100L);
      default :
         return 1;
   }
}
static private int jjMoveStringLiteralDfa1_1(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      return 1;
   }
   switch(curChar)
   {
      case 47:
         if ((active0 & 0x100L) != 0L)
            return jjStopAtPos(1, 8);
         break;
      default :
         return 2;
   }
   return 2;
}
static final int[] jjnextStates = {
   1, 2, 4, 99, 104, 91, 93, 87, 88, 80, 85, 73, 75, 55, 56, 
};

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, null, null, };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
   "MULTILINECOMMENT",
};

/** Lex State array. */
public static final int[] jjnewLexState = {
   -1, -1, -1, -1, -1, -1, 1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, 
};
static final long[] jjtoToken = {
   0x1f201L, 
};
static final long[] jjtoSkip = {
   0x17eL, 
};
static final long[] jjtoMore = {
   0x80L, 
};
static protected SimpleCharStream input_stream;
static private final int[] jjrounds = new int[105];
static private final int[] jjstateSet = new int[210];
private static final StringBuilder jjimage = new StringBuilder();
private static StringBuilder image = jjimage;
private static int jjimageLen;
private static int lengthOfMatch;
static protected char curChar;
/** Constructor. */
public Lexer1TokenManager(SimpleCharStream stream){
   if (input_stream != null)
      throw new TokenMgrError("ERROR: Second call to constructor of static lexer. You must use ReInit() to initialize the static variables.", TokenMgrError.STATIC_LEXER_ERROR);
   input_stream = stream;
}

/** Constructor. */
public Lexer1TokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}

/** Reinitialise parser. */
static public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
static private void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 105; i-- > 0;)
      jjrounds[i] = 0x80000000;
}

/** Reinitialise parser. */
static public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}

/** Switch to specified lex state. */
static public void SwitchTo(int lexState)
{
   if (lexState >= 2 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

static protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

static int curLexState = 0;
static int defaultLexState = 0;
static int jjnewStateCnt;
static int jjround;
static int jjmatchedPos;
static int jjmatchedKind;

/** Get the next Token. */
public static Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }
   image = jjimage;
   image.setLength(0);
   jjimageLen = 0;

   for (;;)
   {
     switch(curLexState)
     {
       case 0:
         try { input_stream.backup(0);
            while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
               curChar = input_stream.BeginToken();
         }
         catch (java.io.IOException e1) { continue EOFLoop; }
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_0();
         break;
       case 1:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_1();
         if (jjmatchedPos == 0 && jjmatchedKind > 7)
         {
            jjmatchedKind = 7;
         }
         break;
     }
     if (jjmatchedKind != 0x7fffffff)
     {
        if (jjmatchedPos + 1 < curPos)
           input_stream.backup(curPos - jjmatchedPos - 1);
        if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           matchedToken = jjFillToken();
           TokenLexicalActions(matchedToken);
       if (jjnewLexState[jjmatchedKind] != -1)
         curLexState = jjnewLexState[jjmatchedKind];
           return matchedToken;
        }
        else if ((jjtoSkip[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
         if (jjnewLexState[jjmatchedKind] != -1)
           curLexState = jjnewLexState[jjmatchedKind];
           continue EOFLoop;
        }
        jjimageLen += jjmatchedPos + 1;
      if (jjnewLexState[jjmatchedKind] != -1)
        curLexState = jjnewLexState[jjmatchedKind];
        curPos = 0;
        jjmatchedKind = 0x7fffffff;
        try {
           curChar = input_stream.readChar();
           continue;
        }
        catch (java.io.IOException e1) { }
     }
     int error_line = input_stream.getEndLine();
     int error_column = input_stream.getEndColumn();
     String error_after = null;
     boolean EOFSeen = false;
     try { input_stream.readChar(); input_stream.backup(1); }
     catch (java.io.IOException e1) {
        EOFSeen = true;
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
        if (curChar == '\n' || curChar == '\r') {
           error_line++;
           error_column = 0;
        }
        else
           error_column++;
     }
     if (!EOFSeen) {
        input_stream.backup(1);
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
     }
     throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
   }
  }
}

static void TokenLexicalActions(Token matchedToken)
{
   switch(jjmatchedKind)
   {
      case 12 :
        image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
                                                 Lexer1.tknName = "ID";
         break;
      case 13 :
        image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
                                                 Lexer1.tknName = "INTLIT";
         break;
      case 14 :
        image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
                                                 Lexer1.tknName = "STRLIT";
         break;
      default :
         break;
   }
}
static private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
static private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
static private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

static private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

}
