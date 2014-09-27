//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package sclaim.parser.generation;



//#line 5 "parser.y"
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import tatami.core.interfaces.Logger;
import tatami.core.util.logging.Log;
import sclaim.constructs.basic.*;
import sclaim.constructs.goal_driven.simons_and_garella.*;
//#line 29 "ParserSClaim.java"




public class ParserSClaim
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:ParserSClaimVal
String   yytext;//user variable to return contextual strings
ParserSClaimVal yyval; //used to return semantic vals from action routines
ParserSClaimVal yylval;//the 'lval' (result) I got from yylex()
ParserSClaimVal valstk[] = new ParserSClaimVal[YYSTACKSIZE];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
final void val_init()
{
  yyval=new ParserSClaimVal();
  yylval=new ParserSClaimVal();
  valptr=-1;
}
final void val_push(ParserSClaimVal val)
{
  try {
    valptr++;
    valstk[valptr]=val;
  }
  catch (ArrayIndexOutOfBoundsException e) {
    int oldsize = valstk.length;
    int newsize = oldsize*2;
    ParserSClaimVal[] newstack = new ParserSClaimVal[newsize];
    System.arraycopy(valstk,0,newstack,0,oldsize);
    valstk = newstack;
    valstk[valptr]=val;
  }
}
final ParserSClaimVal val_pop()
{
  return valstk[valptr--];
}
final void val_drop(int cnt)
{
  valptr -= cnt;
}
final ParserSClaimVal val_peek(int relative)
{
  return valstk[valptr-relative];
}
final ParserSClaimVal dup_yyval(ParserSClaimVal val)
{
  return val;
}
//#### end semantic value section ####
public final static short VARIABLE=257;
public final static short AFFECTABLE_VARIABLE=258;
public final static short CONSTANT=259;
public final static short STRING_LITERAL=260;
public final static short THIS=261;
public final static short PARENT=262;
public final static short STRUCT=263;
public final static short AGENT=264;
public final static short BEHAVIOR=265;
public final static short INITIAL=266;
public final static short REACTIVE=267;
public final static short CYCLIC=268;
public final static short PROACTIVE=269;
public final static short RECEIVE=270;
public final static short SEND=271;
public final static short MESSAGE=272;
public final static short CONDITION=273;
public final static short FORALLK=274;
public final static short WHILE=275;
public final static short ADDK=276;
public final static short READK=277;
public final static short REMOVEK=278;
public final static short IF=279;
public final static short THEN=280;
public final static short ELSE=281;
public final static short INPUT=282;
public final static short OUTPUT=283;
public final static short IN=284;
public final static short OUT=285;
public final static short OPEN=286;
public final static short ACID=287;
public final static short NEW=288;
public final static short WAIT=289;
public final static short AGOAL=290;
public final static short MGOAL=291;
public final static short PGOAL=292;
public final static short ACHIEVE=293;
public final static short ACTION=294;
public final static short MAINTAIN=295;
public final static short TARGET=296;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    1,    1,    2,    2,    2,    3,    3,    4,    4,    4,
    6,    6,    5,    7,    7,    7,    7,    7,   10,   10,
   11,   12,   13,   13,   14,   14,   14,   14,   15,   15,
   16,   16,   16,   17,   17,   18,   19,   20,   21,   22,
   23,   24,   25,   26,   27,    9,   28,   29,   30,   30,
   30,   30,   30,   30,   30,   30,   30,   30,   30,    8,
    8,   31,   31,   31,   31,   32,   32,   34,   33,   35,
   35,   36,   37,   37,   39,   39,   40,   40,   41,   41,
   41,   42,   42,   43,   43,   43,   43,   43,   43,   38,
   38,   44,   44,   44,   45,   45,   46,    0,    0,
};
final static short yylen[] = {                            2,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    2,    4,    1,    1,    1,    1,    1,    1,    2,
    1,    1,    1,    2,    1,    1,    1,    1,    5,    4,
    1,    1,    1,    1,    2,    4,    4,    4,    4,    4,
    3,    4,    3,    4,    4,    4,    4,    4,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    3,
    4,    7,    9,    7,    7,    1,    2,    1,    1,    1,
    1,    4,    6,    8,    4,    5,    4,    5,    1,    1,
    1,    1,    2,    1,    1,    1,    1,    1,    1,    1,
    2,    5,    5,    6,    1,    2,    4,    6,    5,
};
final static short yydefred[] = {                         0,
    0,    0,    0,   21,    0,    1,    2,    0,   22,   23,
    0,    0,    0,   24,    0,   99,    0,   95,    0,   98,
   25,   26,   27,   28,    0,   97,   96,    0,    0,   85,
   57,   49,   79,   80,   50,   51,   52,   53,   54,   55,
   56,   58,   59,   84,   89,   81,   86,    0,   87,   88,
   82,    0,   90,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   92,   91,   93,    0,   83,
    7,    6,    4,    5,    0,    3,   31,   32,   33,   34,
    0,    0,    0,   71,   70,    0,    0,    0,    0,    0,
    0,   14,   15,   16,   19,   17,   18,    0,    0,    0,
    0,    0,    0,    0,   41,    0,   43,    0,    0,    0,
    0,    0,   60,    0,   94,    0,    0,   37,   35,   36,
   72,    0,   75,    0,   77,    0,   45,   20,   46,   47,
    0,   38,   39,   40,   42,   44,   48,    0,   69,    0,
    0,   61,    0,    8,    9,   11,   10,    0,    0,   76,
   78,    0,    0,    0,    0,    0,   30,   12,   13,    0,
   73,   68,    0,   66,    0,    0,   29,    0,   62,   67,
    0,   64,   65,   74,    0,   63,
};
final static short yydgoto[] = {                          2,
   86,  102,  103,  156,  104,  158,  105,  106,  107,  108,
   74,   10,   11,   25,   89,   90,   91,   32,   33,   34,
   35,   36,   37,   38,   39,   40,   41,   42,   43,   44,
   45,  173,  150,  174,   96,   46,   47,   48,   49,   50,
   51,   52,   53,   18,   19,   12,
};
final static short yysindex[] = {                        -3,
 -250,    0, -218,    0,   83,    0,    0, -212,    0,    0,
   83,   30,   34,    0,   63,    0, -189,    0,   -5,    0,
    0,    0,    0,    0, -218,    0,    0,   72,  -85,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    2,    0,    0,
    0,    9,    0,   65,   65,   73,   74,   75,   71,   71,
   71,   73,   71,   71,   71,   78,   71,   80,   71,   71,
 -218, -218, -218,  -37,  145,    0,    0,    0,   19,    0,
    0,    0,    0,    0, -225,    0,    0,    0,    0,    0,
  -31,  -25, -254,    0,    0,   81, -139,   27, -218,   43,
 -215,    0,    0,    0,    0,    0,    0,  -14,   -8,   -1,
 -152,    5,   11,   17,    0,   23,    0,   29,   35,   71,
 -174, -174,    0,   41,    0, -143,   77,    0,    0,    0,
    0,   77,    0,   49,    0,   51,    0,    0,    0,    0,
   91,    0,    0,    0,    0,    0,    0, -159,    0, -160,
 -158,    0,   77,    0,    0,    0,    0,   47,   53,    0,
    0,  -39, -174, -174, -174,   59,    0,    0,    0,   91,
    0,    0,  -21,    0,  -41,  100,    0,   62,    0,    0,
 -174,    0,    0,    0,  -19,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    1,   18,  -26, -141,   -2, -119,  236,  314,  315,  318,
   95,  131,    0,    0,    0,   16,   88,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0, -157,   24, -154,   86,    0,    0,  -44,    0,    0,
  101,    0,  -18,  133,    0,  143,
};
final static int YYTABLESIZE=493;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        182,
   75,  171,  101,  123,    4,    9,  175,   79,   85,  128,
  176,    9,  159,    3,   85,  130,  168,  168,  180,  179,
  180,  186,   60,  185,  168,  101,  137,   88,   88,   77,
  180,  101,  139,  166,   17,   26,    1,  126,  101,  140,
    4,   75,   76,    4,  101,  142,  127,  132,   29,   78,
  101,  143,   13,  134,   98,  136,  101,  144,   75,  125,
   77,   60,  101,  145,   88,   88,   75,  133,  101,  146,
   16,   87,   87,   17,  101,  147,   21,   22,   23,   24,
  101,  152,   75,  135,   81,   82,   97,  167,   75,  160,
   75,  161,   97,  169,  149,  149,  162,    5,   97,  177,
  155,   75,  184,   20,   85,  155,  129,  129,   87,   87,
  101,   29,   93,   97,   99,   77,   97,   77,  115,   28,
  117,  131,    8,  132,  157,  178,  155,  141,  153,  157,
   75,  155,  155,  163,  164,  165,  172,  172,  172,  155,
  183,   14,   92,   77,  154,  151,  172,  111,  172,  154,
  157,   27,   80,   15,  172,  157,  157,    0,  172,   77,
    0,    0,    0,  157,    0,  120,  121,  122,    0,    0,
  154,    0,    0,    4,    0,  154,  154,    0,    0,    0,
    0,    0,    0,  154,   54,   55,    0,   56,   57,   58,
   59,   60,   61,   62,    0,    0,   63,   64,   65,   66,
   67,   68,   69,   70,   71,   72,   73,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   81,   82,    6,
    7,   81,   82,   83,   84,    6,    7,   81,   82,   83,
   84,    6,    7,   81,   82,   83,   84,   81,   82,   81,
   82,  170,    6,    7,   81,   82,   83,   84,    6,    7,
   81,   82,   83,   84,  181,    6,    7,   81,   82,   83,
   84,    6,    7,   81,   82,   83,   84,    6,    7,   81,
   82,   83,   84,    6,    7,   81,   82,   83,   84,    6,
    7,   81,   82,   83,   84,    6,    7,   81,   82,   83,
   84,    6,    7,   81,   82,   83,   84,    6,    7,   81,
   82,   83,   84,    6,    7,   81,   82,   83,   84,    6,
    7,   81,   82,   83,   84,    6,    7,   81,   82,   83,
   84,    6,    7,   81,   82,   83,   84,    6,    7,   81,
   82,   83,   84,    6,    7,   81,   82,   83,   84,    6,
    7,   30,   31,  138,  138,  138,    0,  138,  138,  138,
    0,  138,    0,  138,  138,  148,    0,    0,    0,  138,
    0,   30,   31,    0,    0,   30,   31,    0,    0,   94,
   95,  100,    0,    0,    0,   94,   95,  109,  110,    0,
  112,  113,  114,    0,  116,    0,  118,  119,    0,    0,
    0,  124,   30,   31,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    4,    0,    0,    0,    0,    0,    0,
    0,   30,   31,   30,   31,   55,    0,    0,   57,   58,
   59,   60,   61,   62,    0,    0,    0,   64,   65,   66,
   67,   68,   69,   70,   71,   72,   73,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   30,   31,   30,
   31,    0,    0,    0,   30,   31,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   30,   31,    0,    0,    0,
    0,    0,    0,   30,   31,    0,    0,    0,    0,    0,
    0,   30,   31,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   40,   41,   40,   41,  259,    5,  164,   52,   40,   41,
  165,   11,  132,  264,   40,   41,  158,  159,  173,   41,
  175,   41,  277,  181,  166,   40,   41,   54,   55,   48,
  185,   40,   41,  153,   40,   41,   40,  263,   40,   41,
  259,   40,   41,  259,   40,   41,  272,  263,   40,   41,
   40,   41,  265,   98,   57,  100,   40,   41,   40,   41,
   79,  277,   40,   41,   91,   92,   40,   41,   40,   41,
   41,   54,   55,   40,   40,   41,  266,  267,  268,  269,
   40,   41,   40,   41,  259,  260,   40,   41,   40,   41,
   40,   41,   40,   41,  121,  122,  141,    3,   40,   41,
  127,   40,   41,   41,   40,  132,   91,   92,   91,   92,
   40,   40,   40,   40,   40,  134,   40,  136,   41,   25,
   41,   41,   40,  263,  127,  170,  153,  280,  272,  132,
   40,  158,  159,  293,  295,  294,  163,  164,  165,  166,
   41,   11,   55,  162,  127,  122,  173,   62,  175,  132,
  153,   19,   52,   11,  181,  158,  159,   -1,  185,  178,
   -1,   -1,   -1,  166,   -1,   71,   72,   73,   -1,   -1,
  153,   -1,   -1,  259,   -1,  158,  159,   -1,   -1,   -1,
   -1,   -1,   -1,  166,  270,  271,   -1,  273,  274,  275,
  276,  277,  278,  279,   -1,   -1,  282,  283,  284,  285,
  286,  287,  288,  289,  290,  291,  292,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  259,  260,  257,
  258,  259,  260,  261,  262,  257,  258,  259,  260,  261,
  262,  257,  258,  259,  260,  261,  262,  259,  260,  259,
  260,  281,  257,  258,  259,  260,  261,  262,  257,  258,
  259,  260,  261,  262,  296,  257,  258,  259,  260,  261,
  262,  257,  258,  259,  260,  261,  262,  257,  258,  259,
  260,  261,  262,  257,  258,  259,  260,  261,  262,  257,
  258,  259,  260,  261,  262,  257,  258,  259,  260,  261,
  262,  257,  258,  259,  260,  261,  262,  257,  258,  259,
  260,  261,  262,  257,  258,  259,  260,  261,  262,  257,
  258,  259,  260,  261,  262,  257,  258,  259,  260,  261,
  262,  257,  258,  259,  260,  261,  262,  257,  258,  259,
  260,  261,  262,  257,  258,  259,  260,  261,  262,  257,
  258,   28,   28,  108,  109,  110,   -1,  112,  113,  114,
   -1,  116,   -1,  118,  119,  120,   -1,   -1,   -1,  124,
   -1,   48,   48,   -1,   -1,   52,   52,   -1,   -1,   56,
   56,   58,   -1,   -1,   -1,   62,   62,   60,   61,   -1,
   63,   64,   65,   -1,   67,   -1,   69,   70,   -1,   -1,
   -1,   74,   79,   79,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  259,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   98,   98,  100,  100,  271,   -1,   -1,  274,  275,
  276,  277,  278,  279,   -1,   -1,   -1,  283,  284,  285,
  286,  287,  288,  289,  290,  291,  292,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  134,  134,  136,
  136,   -1,   -1,   -1,  141,  141,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  162,  162,   -1,   -1,   -1,
   -1,   -1,   -1,  170,  170,   -1,   -1,   -1,   -1,   -1,
   -1,  178,  178,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=296;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'",null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,"VARIABLE","AFFECTABLE_VARIABLE","CONSTANT",
"STRING_LITERAL","THIS","PARENT","STRUCT","AGENT","BEHAVIOR","INITIAL",
"REACTIVE","CYCLIC","PROACTIVE","RECEIVE","SEND","MESSAGE","CONDITION",
"FORALLK","WHILE","ADDK","READK","REMOVEK","IF","THEN","ELSE","INPUT","OUTPUT",
"IN","OUT","OPEN","ACID","NEW","WAIT","AGOAL","MGOAL","PGOAL","ACHIEVE",
"ACTION","MAINTAIN","TARGET",
};
final static String yyrule[] = {
"$accept : agent_specification",
"variable : VARIABLE",
"variable : AFFECTABLE_VARIABLE",
"claim_variable : variable",
"claim_variable : THIS",
"claim_variable : PARENT",
"constant : STRING_LITERAL",
"constant : CONSTANT",
"structure_field : claim_variable",
"structure_field : constant",
"structure_field : structure",
"structure_field_list : structure_field",
"structure_field_list : structure_field_list structure_field",
"structure : '(' STRUCT structure_field_list ')'",
"argument : claim_variable",
"argument : constant",
"argument : structure",
"argument : function",
"argument : readK_function",
"argument_list : argument",
"argument_list : argument_list argument",
"name : CONSTANT",
"agent_argument : variable",
"agent_argument_list : agent_argument",
"agent_argument_list : agent_argument_list agent_argument",
"behavior_type : INITIAL",
"behavior_type : REACTIVE",
"behavior_type : CYCLIC",
"behavior_type : PROACTIVE",
"message_structure : '(' STRUCT MESSAGE structure_field_list ')'",
"message_structure : '(' MESSAGE structure_field_list ')'",
"message_argument : claim_variable",
"message_argument : constant",
"message_argument : message_structure",
"message_argument_list : message_argument",
"message_argument_list : message_argument_list message_argument",
"send_function : '(' SEND message_argument_list ')'",
"receive_function : '(' RECEIVE message_argument_list ')'",
"input_function : '(' INPUT argument_list ')'",
"output_function : '(' OUTPUT argument_list ')'",
"in_function : '(' IN argument_list ')'",
"out_function : '(' OUT ')'",
"open_function : '(' OPEN argument_list ')'",
"acid_function : '(' ACID ')'",
"new_function : '(' NEW argument_list ')'",
"addK_function : '(' ADDK argument_list ')'",
"readK_function : '(' READK argument_list ')'",
"removeK_function : '(' REMOVEK argument_list ')'",
"wait_function : '(' WAIT argument_list ')'",
"language_function : send_function",
"language_function : output_function",
"language_function : in_function",
"language_function : out_function",
"language_function : open_function",
"language_function : acid_function",
"language_function : new_function",
"language_function : addK_function",
"language_function : readK_function",
"language_function : removeK_function",
"language_function : wait_function",
"function : '(' name ')'",
"function : '(' name argument_list ')'",
"goal : '(' AGOAL name argument ACHIEVE proposition_list ')'",
"goal : '(' MGOAL name priority MAINTAIN proposition_list TARGET proposition_list ')'",
"goal : '(' MGOAL name priority MAINTAIN proposition_list ')'",
"goal : '(' PGOAL name priority ACTION proposition ')'",
"proposition_list : proposition",
"proposition_list : proposition_list proposition",
"proposition : constant",
"priority : constant",
"valid_condition : readK_function",
"valid_condition : function",
"condition : '(' CONDITION valid_condition ')'",
"if_stmt : '(' IF valid_condition THEN behavior_content_list ')'",
"if_stmt : '(' IF valid_condition THEN behavior_content_list ELSE behavior_content_list ')'",
"forAllK : '(' FORALLK structure ')'",
"forAllK : '(' FORALLK structure behavior_content_list ')'",
"while : '(' WHILE function ')'",
"while : '(' WHILE function behavior_content_list ')'",
"behavior_content_header : receive_function",
"behavior_content_header : input_function",
"behavior_content_header : condition",
"behavior_content_header_list : behavior_content_header",
"behavior_content_header_list : behavior_content_header_list behavior_content_header",
"behavior_content : language_function",
"behavior_content : function",
"behavior_content : if_stmt",
"behavior_content : forAllK",
"behavior_content : while",
"behavior_content : goal",
"behavior_content_list : behavior_content",
"behavior_content_list : behavior_content_list behavior_content",
"behavior : '(' behavior_type name behavior_content_list ')'",
"behavior : '(' behavior_type name behavior_content_header_list ')'",
"behavior : '(' behavior_type name behavior_content_header_list behavior_content_list ')'",
"behavior_list : behavior",
"behavior_list : behavior_list behavior",
"behaviors_declaration : '(' BEHAVIOR behavior_list ')'",
"agent_specification : '(' AGENT name agent_argument_list behaviors_declaration ')'",
"agent_specification : '(' AGENT name behaviors_declaration ')'",
};

//#line 778 "parser.y"
private static String unitName = "parser";

/** the logger */
public Logger log = Log.getLogger(unitName);

/** a reference to the agent structure returned by the parser */
public ClaimAgentDefinition parsedAgent;

/** the list of agent class names used by the new primitive */
Vector<String> agentClasses = null;

/** a reference to the lexer object */
private Yylex lexer;

/** interface to the lexer */
private int yylex () {
  int yyl_return = -1;
  try {
    yyl_return = lexer.yylex();
  }
  catch (IOException e) {
    System.err.println("IO error :"+e);
  }
  return yyl_return;
}

/**
 * Verifies if the structure given as argument, or any of its substructures, has at least one variable.
 * This function is useful in the case of statements like ForAllk, where passing a structure 
 * without any variable doesn't make sense.
 * 
 * @param structure - the structure to be verified
 */
public boolean verifyVariablesInStructure(ClaimStructure structure)
{
	for (ClaimConstruct currentField:structure.getFields())
	{
		switch(currentField.getType())
		{
		case VARIABLE:
			return true;
		case STRUCTURE:
			if(verifyVariablesInStructure((ClaimStructure) currentField))
				return true;
			break;
		default:
			break;	
		}
	}
	return false;
}

/** error reporting */
public void yyerror (String error) {
  log.error("Error "/*+lexer.getLine()*/+": " + error);
  System.exit(1);
}

/** warning reporting */
public void yywarn (String warning) {
  log.warn("Warning "/*+lexer.getLine()*/+": " + warning);
}

/** constructor which receives a String with the file name and path as argument 
 * @throws FileNotFoundException */
public ParserSClaim(String filePathAndName) {
  try {
	if (filePathAndName==null)
		throw new Exception();
	lexer = new Yylex(new FileReader(filePathAndName), this);
  } catch (FileNotFoundException e) {
	log.error("The file specified as argument could not be opened. Make sure that you have correctly written the name and the path!");
	Log.exitLogger(unitName);
  }
  catch (Exception e)
  {
	log.error("The name of the file to be parsed was not specified.");
    Log.exitLogger(unitName);
  }
}

/** a way to use the parser - main function 
 * @throws FileNotFoundException */
public static void main(String args[]) {
  ParserSClaim yyparser;
  if(args.length>0)
  {
	yyparser = new ParserSClaim(args[0]);
    yyparser.parse();
  }
  else
  {
	System.out.println("No argument was specified. The file name to be parsed together with its path are needed.");
  }
}

/** 
 * a way to use the parser - inside the code
 */
public ClaimAgentDefinition parse() {
  int parsingResult = yyparse();

  if(parsingResult == 0) {
    log.info("Parsing successfully finished! The agent with the class \""+parsedAgent.getClassName()+"\" is ready to be run.");
    Log.exitLogger(unitName);
    return parsedAgent;
  }
  else {
    Log.exitLogger(unitName);
    return null;
  }
}
//#line 595 "ParserSClaim.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 36 "parser.y"
{
			/*log.info("variable -> VARIABLE:"+lexer.yytext().substring(1));*/
			yyval = new ParserSClaimVal(new ClaimVariable(lexer.yytext().substring(1)));
		}
break;
case 2:
//#line 41 "parser.y"
{
			/*log.info("variable -> AFFECTABLE_VARIABLE:"+lexer.yytext().substring(2));*/
			yyval = new ParserSClaimVal(new ClaimVariable(lexer.yytext().substring(2),true));
		}
break;
case 3:
//#line 48 "parser.y"
{
			/*log.info("claim_variable -> variable");*/
			yyval = val_peek(0);
		}
break;
case 4:
//#line 53 "parser.y"
{
			/*log.info("claim_variable -> THIS");*/
			yyval = new ParserSClaimVal(new ClaimVariable("this"));
		}
break;
case 5:
//#line 58 "parser.y"
{
			/*log.info("claim_variable -> PARENT");*/
			yyval = new ParserSClaimVal(new ClaimVariable("parent",true));
		}
break;
case 6:
//#line 66 "parser.y"
{
			/*//log.info("constant -> STRING_LITERAL:"+lexer.yytext());*/
			String content = lexer.yytext();
			yyval = new ParserSClaimVal(new ClaimValue(content.substring(1,content.length()-1)));
		}
break;
case 7:
//#line 72 "parser.y"
{
			/*log.info("constant -> CONSTANT: "+lexer.yytext());*/
			yyval = new ParserSClaimVal(new ClaimValue(lexer.yytext()));
		}
break;
case 8:
//#line 81 "parser.y"
{
			/*log.info("structure_field -> claim_variable");*/
			yyval = val_peek(0);
		}
break;
case 9:
//#line 86 "parser.y"
{
			/*log.info("structure_field -> constant: "+lexer.yytext());*/
			yyval = val_peek(0);
		}
break;
case 10:
//#line 91 "parser.y"
{
			/*log.info("structure_field -> structure");*/
			yyval = val_peek(0);
		}
break;
case 11:
//#line 99 "parser.y"
{
			/*log.info("structure_field_list -> structure_field");*/
			yyval = new ParserSClaimVal(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 12:
//#line 105 "parser.y"
{
			/*log.info("structure_field_list -> structure_field_list structure_field");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 13:
//#line 119 "parser.y"
{
			/*log.info("structure -> '(' STRUCT structure_field_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimStructure(val_peek(1).claimConstructVector));
		}
break;
case 14:
//#line 127 "parser.y"
{
			/*log.info("argument -> claim_variable");*/
			yyval = val_peek(0);
		}
break;
case 15:
//#line 132 "parser.y"
{
			/*log.info("argument -> constant: "+lexer.yytext());*/
			yyval = val_peek(0);
		}
break;
case 16:
//#line 137 "parser.y"
{
			/*log.info("argument -> structure");*/
			yyval = val_peek(0);
		}
break;
case 17:
//#line 142 "parser.y"
{
			/*log.info("argument -> functions");*/
			yyval = val_peek(0);
		}
break;
case 18:
//#line 147 "parser.y"
{
			/*log.info("argument -> readK_function");*/
			yyval = val_peek(0);
		}
break;
case 19:
//#line 155 "parser.y"
{
			/*log.info("argument_list -> argument");*/
			yyval = new ParserSClaimVal(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 20:
//#line 161 "parser.y"
{
			/*log.info("argument_list -> argument_list argument");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 21:
//#line 170 "parser.y"
{
			/*log.info("name -> CONSTANT: "+lexer.yytext());*/
			yyval = new ParserSClaimVal(lexer.yytext());
		}
break;
case 22:
//#line 178 "parser.y"
{
			/*log.info("agent_argument -> variable");*/
			yyval = val_peek(0);
		}
break;
case 23:
//#line 186 "parser.y"
{
			/*log.info("agent_argument_list -> agent_argument");*/
			
			/*register the language variables in the list of agent parameters*/
			Vector<ClaimConstruct> languageParameters = new Vector<ClaimConstruct>();
			languageParameters.add(new ClaimVariable("this"));
			languageParameters.add(new ClaimVariable("parent",true));
			
			yyval = new ParserSClaimVal(new Vector<ClaimConstruct>(languageParameters));
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 24:
//#line 198 "parser.y"
{
			/*log.info("agent_argument_list -> agent_argument_list agent_argument");*/
			yyval = val_peek(1);
			
			ClaimVariable currentVariable = (ClaimVariable) val_peek(0).claimConstruct;
			
			if(yyval.claimConstructVector.contains(currentVariable)==false)
			{
				ClaimVariable complementaryVariable = currentVariable.getComplement(); /*variable complementary in what concerns the affectability*/
				if(yyval.claimConstructVector.contains(complementaryVariable)==false)
					yyval.claimConstructVector.add(currentVariable);
				else {
					String msg = new String("The variable: "+currentVariable.getName()+" is used both as affectable and as not affectable. ");
					if(currentVariable.getName().equals(new String("parent")))
						yyerror(msg+"The \"parent\" variable belongs to the language and it could be only affectable.");
					else if(currentVariable.getName().equals(new String("this")))
						yyerror(msg+"The \"this\" variable belongs to the language and it could be only not affectable.");
					else
						yyerror(msg);
				}
			}
		}
break;
case 25:
//#line 224 "parser.y"
{
			/*log.info("behavior_type -> INITIAL");*/
			yyval = new ParserSClaimVal(ClaimBehaviorType.INITIAL);
		}
break;
case 26:
//#line 229 "parser.y"
{
			/*log.info("behavior_type -> REACTIVE");*/
			yyval = new ParserSClaimVal(ClaimBehaviorType.REACTIVE);
		}
break;
case 27:
//#line 234 "parser.y"
{
			/*log.info("behavior_type -> CYCLIC");*/
			yyval = new ParserSClaimVal(ClaimBehaviorType.CYCLIC);
		}
break;
case 28:
//#line 239 "parser.y"
{
			/*log.info("behavior_type -> PROACTIVE");*/
			yyval = new ParserSClaimVal(ClaimBehaviorType.PROACTIVE);
		}
break;
case 29:
//#line 252 "parser.y"
{
			/*log.info("message_structure -> '(' STRUCT MESSAGE structure_field_list ')'");*/
			Vector<ClaimConstruct> structFields = new Vector<ClaimConstruct>();
			structFields.add(new ClaimValue(new String("message")));
			structFields.addAll(val_peek(1).claimConstructVector);
			yyval = new ParserSClaimVal(new ClaimStructure(structFields));
		}
break;
case 30:
//#line 260 "parser.y"
{
			/*log.info("message_structure -> '(' MESSAGE structure_field_list ')'");*/
			Vector<ClaimConstruct> structFields = new Vector<ClaimConstruct>();
			structFields.add(new ClaimValue(new String("message")));
			structFields.addAll(val_peek(1).claimConstructVector);
			yyval = new ParserSClaimVal(new ClaimStructure(structFields));
		}
break;
case 31:
//#line 271 "parser.y"
{
			/*log.info("message_argument -> variable");*/
			yyval = val_peek(0);
		}
break;
case 32:
//#line 276 "parser.y"
{
			/*log.info("message_argument -> constant: "+lexer.yytext());*/
			yyval = val_peek(0);
		}
break;
case 33:
//#line 281 "parser.y"
{
			/*log.info("message_argument -> message_structure");*/
			yyval = val_peek(0);
		}
break;
case 34:
//#line 289 "parser.y"
{
			/*log.info("argument_list -> message_argument");*/
			yyval = new ParserSClaimVal(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 35:
//#line 295 "parser.y"
{
			/*log.info("argument_list -> message_argument_list message_argument");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 36:
//#line 304 "parser.y"
{
			/*log.info("send_function -> '(' SEND message_argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.SEND ,new String("send"),val_peek(1).claimConstructVector));
		}
break;
case 37:
//#line 312 "parser.y"
{
			/*log.info("receive_function -> '(' RECEIVE message_argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.RECEIVE ,new String("receive"),val_peek(1).claimConstructVector));
		}
break;
case 38:
//#line 320 "parser.y"
{
			/*log.info("in_function -> '(' INPUT argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.INPUT ,new String("input"),val_peek(1).claimConstructVector));
		}
break;
case 39:
//#line 327 "parser.y"
{
			/*log.info("in_function -> '(' OUTPUT argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.OUTPUT ,new String("output"),val_peek(1).claimConstructVector));
		}
break;
case 40:
//#line 334 "parser.y"
{
			/*log.info("in_function -> '(' IN argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.IN ,new String("in"),val_peek(1).claimConstructVector));
		}
break;
case 41:
//#line 343 "parser.y"
{
			/*log.info("out_function -> '(' OUT argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.OUT ,new String("out"),val_peek(0).claimConstructVector));
		}
break;
case 42:
//#line 351 "parser.y"
{
			/*log.info("open_function -> '(' OPEN argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.OPEN ,new String("open"),val_peek(1).claimConstructVector));
		}
break;
case 43:
//#line 359 "parser.y"
{
			/*log.info("acid_function -> '(' ACID argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.ACID ,new String("acid"),null));
		}
break;
case 44:
//#line 367 "parser.y"
{
			/*log.info("new_function -> '(' NEW argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.NEW ,new String("new"),val_peek(1).claimConstructVector));
			
			if(agentClasses == null)
				agentClasses = new Vector<String>();
			
			if(val_peek(1).claimConstructVector.isEmpty())
				yyerror("new without arguments");
			else {
				ClaimConstruct agentClassClaimConstruct = val_peek(1).claimConstructVector.firstElement();
				
				if (agentClassClaimConstruct.getType() != ClaimConstructType.VALUE)
					yyerror("the first argument of new is not an S-CLAIM constant");
				else {
					String agentClass = new String(((ClaimValue) agentClassClaimConstruct).toString());
					if(!agentClasses.contains(agentClass))	
						agentClasses.add(agentClass);
				}
			}
		}
break;
case 45:
//#line 392 "parser.y"
{
			/*log.info("addK_function -> '(' ADDK argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.ADDK ,new String("addK"),val_peek(1).claimConstructVector));
		}
break;
case 46:
//#line 400 "parser.y"
{
			/*log.info("readK_function -> '(' READK argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.READK ,new String("readK"),val_peek(1).claimConstructVector));
		}
break;
case 47:
//#line 408 "parser.y"
{
			/*log.info("removeK_function -> '(' REMOVEK argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.REMOVEK ,new String("removeK"),val_peek(1).claimConstructVector));
		}
break;
case 48:
//#line 416 "parser.y"
{
			/*log.info("wait_function -> '(' WAIT argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall( ClaimFunctionType.WAIT ,new String("wait"),val_peek(1).claimConstructVector));
		}
break;
case 49:
//#line 424 "parser.y"
{
			/*log.info("language_function -> send_function");*/
			yyval = val_peek(0);
		}
break;
case 50:
//#line 434 "parser.y"
{
			/*log.info("language_function -> output_function");*/
			yyval = val_peek(0);
		}
break;
case 51:
//#line 439 "parser.y"
{
			/*log.info("language_function -> in_function");*/
			yyval = val_peek(0);
		}
break;
case 52:
//#line 444 "parser.y"
{
			/*log.info("language_function -> out_function");*/
			yyval = val_peek(0);
		}
break;
case 53:
//#line 449 "parser.y"
{
			/*log.info("language_function -> open_function");*/
			yyval = val_peek(0);
		}
break;
case 54:
//#line 454 "parser.y"
{
			/*log.info("language_function -> acid_function");*/
			yyval = val_peek(0);
		}
break;
case 55:
//#line 459 "parser.y"
{
			/*log.info("language_function -> new_function");*/
			yyval = val_peek(0);
		}
break;
case 56:
//#line 464 "parser.y"
{
			/*log.info("language_function -> addk_function");*/
			yyval = val_peek(0);
		}
break;
case 57:
//#line 469 "parser.y"
{
			/*log.info("language_function -> readk_function");*/
			yyval = val_peek(0);
		}
break;
case 58:
//#line 474 "parser.y"
{
			/*log.info("language_function -> removek_function");*/
			yyval = val_peek(0);
		}
break;
case 59:
//#line 479 "parser.y"
{
			/*log.info("language_function -> wait_function");*/
			yyval = val_peek(0);
		}
break;
case 60:
//#line 487 "parser.y"
{
			/*log.info("function -> '(' name ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall(ClaimFunctionType.JAVA,val_peek(1).sval,null));
		}
break;
case 61:
//#line 492 "parser.y"
{
			/*log.info("function -> '(' name argument_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimFunctionCall(ClaimFunctionType.JAVA,val_peek(2).sval,val_peek(1).claimConstructVector));
		}
break;
case 62:
//#line 500 "parser.y"
{
			/*log.info("goal -> '(' AGOAL name proposition_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimaGoal(ClaimConstructType.AGOAL, val_peek(4).sval, val_peek(1).claimConstructVector, val_peek(3).ival));
		}
break;
case 63:
//#line 505 "parser.y"
{
			yyval = new ParserSClaimVal(new ClaimmGoal(ClaimConstructType.MGOAL, val_peek(6).sval, val_peek(3).claimConstructVector, val_peek(1).claimConstructVector, Integer.parseInt(((ClaimValue) val_peek(5).obj).toString())));	
		}
break;
case 64:
//#line 509 "parser.y"
{
			yyval = yyval = new ParserSClaimVal(new ClaimmGoal(ClaimConstructType.MGOAL, val_peek(4).sval, val_peek(1).claimConstructVector, null, Integer.parseInt(((ClaimValue) val_peek(3).obj).toString())));
		}
break;
case 65:
//#line 513 "parser.y"
{
			yyval = new ParserSClaimVal(new ClaimpGoal(ClaimConstructType.PGOAL, val_peek(4).sval, val_peek(1).claimConstruct, Integer.parseInt(((ClaimValue) val_peek(3).obj).toString())));
		}
break;
case 66:
//#line 520 "parser.y"
{
			/*log.info("proposition_list -> proposition");*/
			yyval = new ParserSClaimVal(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 67:
//#line 526 "parser.y"
{
			/*log.info("proposition_list -> proposition_list proposition");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 68:
//#line 535 "parser.y"
{
			/*log.info("proposition -> constant ");*/
			yyval = val_peek(0);
		}
break;
case 69:
//#line 543 "parser.y"
{
			/*log.info("priority -> constant");*/
			yyval = val_peek(0);
		}
break;
case 70:
//#line 552 "parser.y"
{
			/*log.info("valid_condition -> readK_function");*/
			yyval = val_peek(0);
		}
break;
case 71:
//#line 557 "parser.y"
{
			/*log.info("valid_condition -> function");*/
			yyval = val_peek(0);
		}
break;
case 72:
//#line 565 "parser.y"
{
			/*log.info("condition -> '(' CONDITION valid_condition ')'");*/
			yyval = new ParserSClaimVal(new ClaimCondition((ClaimFunctionCall) val_peek(1).claimConstruct));
		}
break;
case 73:
//#line 573 "parser.y"
{
			/*log.info("if_stmt -> '(' IF if_valid_condition THEN behavior_content_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimIf((ClaimFunctionCall) val_peek(3).claimConstruct,val_peek(1).claimConstructVector, null));
		}
break;
case 74:
//#line 578 "parser.y"
{
			/*log.info("if_stmt -> '(' IF if_valid_condition THEN behavior_content_list ELSE behavior_content_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimIf((ClaimFunctionCall) val_peek(5).claimConstruct,val_peek(3).claimConstructVector,val_peek(1).claimConstructVector));
		}
break;
case 75:
//#line 586 "parser.y"
{
			/*log.info("forAllK -> '(' FORALLK structure ')'");*/
			if (!verifyVariablesInStructure((ClaimStructure) val_peek(1).claimConstruct))
				yywarn("there is no variable in the structure of forAllK");
			yyval = new ParserSClaimVal(new ClaimForAllK((ClaimStructure) val_peek(1).claimConstruct, null));
		}
break;
case 76:
//#line 593 "parser.y"
{
			/*log.info("forAllK -> '(' FORALLK structure behavior_content_list ')'");*/
			if (!verifyVariablesInStructure((ClaimStructure) val_peek(2).claimConstruct))
				yywarn("there is no variable in the structure of forAllK");
			yyval = new ParserSClaimVal(new ClaimForAllK((ClaimStructure) val_peek(2).claimConstruct, val_peek(1).claimConstructVector));
		}
break;
case 77:
//#line 603 "parser.y"
{
			/*log.info("while -> '(' WHILE function ')'");*/
			yyval = new ParserSClaimVal(new ClaimWhile((ClaimFunctionCall) val_peek(1).claimConstruct, null));
		}
break;
case 78:
//#line 608 "parser.y"
{
			/*log.info("while -> '(' WHILE function behavior_content_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimWhile((ClaimFunctionCall) val_peek(2).claimConstruct, val_peek(1).claimConstructVector));
		}
break;
case 79:
//#line 616 "parser.y"
{
			/*log.info("behavior_content_header -> receive_function");*/
			yyval = val_peek(0);
		}
break;
case 80:
//#line 621 "parser.y"
{
			/*log.info("behavior_content_header -> receive_function");*/
			yyval = val_peek(0);
		}
break;
case 81:
//#line 626 "parser.y"
{
			/*log.info("behavior_content_header -> condition");*/
			yyval = val_peek(0);
		}
break;
case 82:
//#line 634 "parser.y"
{
			/*log.info("behavior_content_header_list -> behavior_content_header");*/
			yyval = new ParserSClaimVal(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 83:
//#line 640 "parser.y"
{
			/*log.info("behavior_content_header_list -> behavior_content_header_list behavior_content_header");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 84:
//#line 650 "parser.y"
{
			/*log.info("behavior_content -> language_function");*/
			yyval = val_peek(0);
		}
break;
case 85:
//#line 655 "parser.y"
{
			/*log.info("behavior_content -> function");*/
			yyval = val_peek(0);
		}
break;
case 86:
//#line 660 "parser.y"
{
			/*log.info("behavior_content -> if_stmt");*/
			yyval = val_peek(0);
		}
break;
case 87:
//#line 665 "parser.y"
{
			/*log.info("behavior_content -> forAllK");*/
			yyval = val_peek(0);
		}
break;
case 88:
//#line 670 "parser.y"
{
			/*log.info("behavior_content -> while");*/
			yyval = val_peek(0);
		}
break;
case 89:
//#line 675 "parser.y"
{
			yyval = val_peek(0);
		}
break;
case 90:
//#line 682 "parser.y"
{
			/*log.info("behavior_content_list -> behavior_content");*/
			yyval = new ParserSClaimVal(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 91:
//#line 688 "parser.y"
{
			/*log.info("behavior_content_list -> behavior_content_list behavior_content");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 92:
//#line 702 "parser.y"
{
			/*log.info("behavior -> '(' behavior_type name behavior_content_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimBehaviorDefinition(val_peek(2).sval, val_peek(3).claimBehaviorType, val_peek(1).claimConstructVector));
		}
break;
case 93:
//#line 707 "parser.y"
{
			/*log.info("behavior -> '(' behavior_type name behavior_content_header_list ')'");*/
			yyval = new ParserSClaimVal(new ClaimBehaviorDefinition(val_peek(2).sval, val_peek(3).claimBehaviorType, val_peek(1).claimConstructVector));
		}
break;
case 94:
//#line 712 "parser.y"
{
			/*log.info("behavior -> '(' behavior_type name behavior_content_header behavior_content_list ')'");*/
			Boolean concatenateSucceeded = val_peek(2).claimConstructVector.addAll(val_peek(1).claimConstructVector);
			if(concatenateSucceeded)
				yyval = new ParserSClaimVal(new ClaimBehaviorDefinition(val_peek(3).sval, val_peek(4).claimBehaviorType, val_peek(2).claimConstructVector));
			else
				log.error("error while concatenating the vectors of statements, while creating the behavior");
		}
break;
case 95:
//#line 724 "parser.y"
{
			/*log.info("behavior_list -> behavior");*/
			yyval = new ParserSClaimVal(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 96:
//#line 730 "parser.y"
{
			/*log.info("behavior_list -> behavior_list behavior");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 97:
//#line 739 "parser.y"
{
			/*log.info("behavior_declaration -> '(' BEHAVIOR behavior_list ')'");*/
			yyval = val_peek(1);
		}
break;
case 98:
//#line 747 "parser.y"
{
			/*log.info("agent_specification -> '(' AGENT name agent_argument_list behaviors_declaration ')'");*/
			parsedAgent = new ClaimAgentDefinition(val_peek(3).sval, 
					new Vector<ClaimConstruct>(val_peek(2).claimConstructVector),
					new Vector<ClaimBehaviorDefinition>(
							Arrays.asList(val_peek(1).claimConstructVector.toArray(new ClaimBehaviorDefinition [0]))
					), agentClasses);
			/*Set the references of the contained behaviors to this agent:*/
			for(ClaimBehaviorDefinition currentBehavior:parsedAgent.getBehaviors())
				currentBehavior.setMyAgent(parsedAgent);
		}
break;
case 99:
//#line 759 "parser.y"
{
			/*log.info("agent_specification -> '(' AGENT name behaviors_declaration ')'");*/
			
			/*register the language variables in the list of agent parameters*/
			Vector<ClaimConstruct> languageParameters = new Vector<ClaimConstruct>();
			languageParameters.add(new ClaimVariable("this"));
			languageParameters.add(new ClaimVariable("parent",true));
			
			parsedAgent = new ClaimAgentDefinition(val_peek(2).sval,languageParameters,
					new Vector<ClaimBehaviorDefinition>(
							Arrays.asList(val_peek(1).claimConstructVector.toArray(new ClaimBehaviorDefinition [0]))
					), agentClasses);
			/*Set the references of the contained behaviors to this agent:*/
			for(ClaimBehaviorDefinition currentBehavior:parsedAgent.getBehaviors())
				currentBehavior.setMyAgent(parsedAgent);
		}
break;
//#line 1523 "ParserSClaim.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public ParserSClaim()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public ParserSClaim(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
