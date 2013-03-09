/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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



package tatami.core.agent.claim.parser;



//#line 5 "parser.y"
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;



import tatami.core.interfaces.Logger;
import tatami.core.util.logging.Log;




public class ParserClaim2
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
//## **user defined:ParserClaim2Val
String   yytext;//user variable to return contextual strings
ParserClaim2Val yyval; //used to return semantic vals from action routines
ParserClaim2Val yylval;//the 'lval' (result) I got from yylex()
ParserClaim2Val valstk[] = new ParserClaim2Val[YYSTACKSIZE];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
final void val_init()
{
  yyval=new ParserClaim2Val();
  yylval=new ParserClaim2Val();
  valptr=-1;
}
final void val_push(ParserClaim2Val val)
{
  try {
    valptr++;
    valstk[valptr]=val;
  }
  catch (ArrayIndexOutOfBoundsException e) {
    int oldsize = valstk.length;
    int newsize = oldsize*2;
    ParserClaim2Val[] newstack = new ParserClaim2Val[newsize];
    System.arraycopy(valstk,0,newstack,0,oldsize);
    valstk = newstack;
    valstk[valptr]=val;
  }
}
final ParserClaim2Val val_pop()
{
  return valstk[valptr--];
}
final void val_drop(int cnt)
{
  valptr -= cnt;
}
final ParserClaim2Val val_peek(int relative)
{
  return valstk[valptr-relative];
}
final ParserClaim2Val dup_yyval(ParserClaim2Val val)
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
    4,    4,    3,    4,    4,    4,    4,    4,    1,    1,
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
    0,    0,    0,    0,    0,    0,   43,    0,    0,    0,
    0,    0,   60,    0,   94,    0,    0,   37,   35,   36,
   72,    0,   75,    0,   77,    0,   45,   20,   46,   47,
    0,   38,   39,   40,   41,   42,   44,   48,    0,   69,
    0,    0,   61,    0,    8,    9,   11,   10,    0,    0,
   76,   78,    0,    0,    0,    0,    0,   30,   12,   13,
    0,   73,   68,    0,   66,    0,    0,   29,    0,   62,
   67,    0,   64,   65,   74,    0,   63,
};
final static short yydgoto[] = {                          2,
   86,  102,  103,  157,  104,  159,  105,  106,  107,  108,
   74,   10,   11,   25,   89,   90,   91,   32,   33,   34,
   35,   36,   37,   38,   39,   40,   41,   42,   43,   44,
   45,  174,  151,  175,   96,   46,   47,   48,   49,   50,
   51,   52,   53,   18,   19,   12,
};
final static short yysindex[] = {                         1,
 -221,    0, -203,    0,   89,    0,    0, -206,    0,    0,
   89,   39,   52,    0,   54,    0, -219,    0,  -27,    0,
    0,    0,    0,    0, -203,    0,    0,   61,  -76,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   -4,    0,    0,
    0,   21,    0,   71,   71,   64,   67,   72,   77,   77,
   77,   64,   77,   77,   77,   77,   77,   73,   77,   77,
 -203, -203, -203,  -37,  158,    0,    0,    0,   25,    0,
    0,    0,    0,    0, -234,    0,    0,    0,    0,    0,
  -31,  -25, -254,    0,    0,   74, -150,   31, -203,   33,
 -242,    0,    0,    0,    0,    0,    0,  -14,   -8,   -1,
 -164,    5,   11,   17,   23,   29,    0,   35,   41,   77,
 -182, -182,    0,   47,    0, -154,   83,    0,    0,    0,
    0,   83,    0,   43,    0,   45,    0,    0,    0,    0,
   79,    0,    0,    0,    0,    0,    0,    0, -173,    0,
 -174, -172,    0,   83,    0,    0,    0,    0,   53,   59,
    0,    0,  -39, -182, -182, -182,   65,    0,    0,    0,
   79,    0,    0,  -21,    0,  -41,   84,    0,   49,    0,
    0, -182,    0,    0,    0,  -19,    0,
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
    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   13,  299,  -24, -148,   22, -126,  247,  320,  321,  341,
   99,  116,    0,    0,    0,   18,   76,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0, -157,    6, -132,   70,    0,    0,  -45,    0,    0,
   81,    0,   12,  115,    0,  126,
};
final static int YYTABLESIZE=500;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        183,
   75,  172,  101,  123,    4,  160,   79,  176,   85,  128,
  169,  169,   17,   26,   85,  130,    4,    9,  169,  180,
  132,  187,   60,    9,  186,  101,  137,  167,  126,   88,
   88,  101,  139,  177,   60,   75,   76,  127,  101,  140,
    1,  181,    3,  181,  101,  142,   21,   22,   23,   24,
  101,  143,  134,  181,  136,    4,  101,  144,   13,   77,
   29,   78,  101,  145,   75,  125,   88,   88,  101,  146,
   75,  133,   75,  135,  101,  147,   81,   82,   98,   16,
  101,  148,   75,  161,   75,  162,  101,  153,   75,  185,
   77,   17,   97,  168,   20,  163,  150,  150,   97,  170,
   29,    5,  156,   93,   97,  178,   97,  156,  129,  129,
   85,   99,  132,  117,  131,  141,  101,  154,   75,  164,
  165,  166,   97,   28,  184,  179,   14,  152,    8,  156,
   92,  111,   80,   27,  156,  156,   15,    0,    0,  173,
  173,  173,  156,    0,    0,   77,    0,   77,  158,  173,
    0,  173,    0,  158,    0,    0,    0,  173,    0,    0,
    0,  173,    0,    0,    0,    0,    0,    0,    0,  120,
  121,  122,    0,    0,   77,  158,    0,    0,    0,    0,
  158,  158,    4,    0,    0,    0,    0,    0,  158,    0,
   77,    0,    0,   54,   55,    0,   56,   57,   58,   59,
   60,   61,   62,    0,    0,   63,   64,   65,   66,   67,
   68,   69,   70,   71,   72,   73,    0,   81,   82,    6,
    7,   81,   82,   83,   84,    6,    7,   81,   82,   83,
   84,    6,    7,   81,   82,   83,   84,   81,   82,   81,
   82,  171,    6,    7,   81,   82,   83,   84,    6,    7,
   81,   82,   83,   84,  182,    6,    7,   81,   82,   83,
   84,    6,    7,   81,   82,   83,   84,    6,    7,   81,
   82,   83,   84,    6,    7,   81,   82,   83,   84,    6,
    7,   81,   82,   83,   84,    6,    7,   81,   82,   83,
   84,    6,    7,   81,   82,   83,   84,    6,    7,   81,
   82,   83,   84,    6,    7,   81,   82,   83,   84,    6,
    7,   81,   82,   83,   84,    6,    7,   81,   82,   83,
   84,    6,    7,   81,   82,   83,   84,    6,    7,   81,
   82,   83,   84,    6,    7,   81,   82,   83,   84,    6,
    7,   81,   82,   83,   84,    6,    7,   30,   31,    0,
    0,    0,   87,   87,  138,  138,  138,    0,  138,  138,
  138,  138,  138,    0,  138,  138,  149,   30,   31,    0,
  138,   30,   31,    0,    0,   94,   95,  100,    0,    0,
    0,   94,   95,    0,    0,    0,    0,    0,    0,   87,
   87,    0,    0,    0,    0,    0,    0,    0,   30,   31,
  109,  110,    0,  112,  113,  114,  115,  116,    0,  118,
  119,    0,    0,    0,  124,    0,    4,   30,   31,   30,
   31,    0,    0,    0,    0,  155,    0,    0,   55,    0,
  155,   57,   58,   59,   60,   61,   62,    0,    0,    0,
   64,   65,   66,   67,   68,   69,   70,   71,   72,   73,
    0,    0,  155,   30,   31,   30,   31,  155,  155,    0,
   30,   31,    0,    0,    0,  155,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   30,   31,    0,    0,    0,    0,    0,    0,
   30,   31,    0,    0,    0,    0,    0,    0,   30,   31,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   40,   41,   40,   41,  259,  132,   52,  165,   40,   41,
  159,  160,   40,   41,   40,   41,  259,    5,  167,   41,
  263,   41,  277,   11,  182,   40,   41,  154,  263,   54,
   55,   40,   41,  166,  277,   40,   41,  272,   40,   41,
   40,  174,  264,  176,   40,   41,  266,  267,  268,  269,
   40,   41,   98,  186,  100,  259,   40,   41,  265,   48,
   40,   41,   40,   41,   40,   41,   91,   92,   40,   41,
   40,   41,   40,   41,   40,   41,  259,  260,   57,   41,
   40,   41,   40,   41,   40,   41,   40,   41,   40,   41,
   79,   40,   40,   41,   41,  141,  121,  122,   40,   41,
   40,    3,  127,   40,   40,   41,   40,  132,   91,   92,
   40,   40,  263,   41,   41,  280,   40,  272,   40,  293,
  295,  294,   40,   25,   41,  171,   11,  122,   40,  154,
   55,   62,   52,   19,  159,  160,   11,   -1,   -1,  164,
  165,  166,  167,   -1,   -1,  134,   -1,  136,  127,  174,
   -1,  176,   -1,  132,   -1,   -1,   -1,  182,   -1,   -1,
   -1,  186,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   71,
   72,   73,   -1,   -1,  163,  154,   -1,   -1,   -1,   -1,
  159,  160,  259,   -1,   -1,   -1,   -1,   -1,  167,   -1,
  179,   -1,   -1,  270,  271,   -1,  273,  274,  275,  276,
  277,  278,  279,   -1,   -1,  282,  283,  284,  285,  286,
  287,  288,  289,  290,  291,  292,   -1,  259,  260,  257,
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
  258,  259,  260,  261,  262,  257,  258,   28,   28,   -1,
   -1,   -1,   54,   55,  108,  109,  110,   -1,  112,  113,
  114,  115,  116,   -1,  118,  119,  120,   48,   48,   -1,
  124,   52,   52,   -1,   -1,   56,   56,   58,   -1,   -1,
   -1,   62,   62,   -1,   -1,   -1,   -1,   -1,   -1,   91,
   92,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   79,   79,
   60,   61,   -1,   63,   64,   65,   66,   67,   -1,   69,
   70,   -1,   -1,   -1,   74,   -1,  259,   98,   98,  100,
  100,   -1,   -1,   -1,   -1,  127,   -1,   -1,  271,   -1,
  132,  274,  275,  276,  277,  278,  279,   -1,   -1,   -1,
  283,  284,  285,  286,  287,  288,  289,  290,  291,  292,
   -1,   -1,  154,  134,  134,  136,  136,  159,  160,   -1,
  141,  141,   -1,   -1,   -1,  167,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  163,  163,   -1,   -1,   -1,   -1,   -1,   -1,
  171,  171,   -1,   -1,   -1,   -1,   -1,   -1,  179,  179,
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
"out_function : '(' OUT argument_list ')'",
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

//#line 775 "parser.y"
private static String unitName = "parser";

/** the logger */
public Logger log = Log.getLogger(unitName);

/** a reference to the agent structure returned by the parser */
public ClaimAgentDefinition parsedAgent;

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
 * Function used after the identification of a behavior by the parser, in order to set the field
 * myBehavior to all the Claim constructs that belong to the specified behavior, in order to give
 * them access to the symbol table.
 * 
 * @param currentBehavior - the behavior that was read by the parser
 */
public void setBehaviorToSubConstructs(ClaimBehaviorDefinition currentBehavior)
{
	for(ClaimConstruct currentConstruct:currentBehavior.getStatements())
		setBehaviorRecursively(currentConstruct, currentBehavior);
}

/**
 * Function used by the method <em>setBehaviorToSubConstructs(ClaimBehaviorDefinition)</em> in order to set the field
 * myBehavior to the current constructs and to all the constructs that are included in it
 *  
 * @param currentConstruct - the construct to be modified
 * @param currentBehavior - the behavior that was read by the parser
 */
public void setBehaviorRecursively(ClaimConstruct currentConstruct, ClaimBehaviorDefinition currentBehavior)
{
	ClaimConstruct currentSubConstruct;
	switch(currentConstruct.getType())
	{
	case VARIABLE:
		 ClaimVariable currentVariable = (ClaimVariable) currentConstruct;
		 currentVariable.setMyBehavior(currentBehavior);
		break;
	case FUNCTION_CALL:
		ClaimFunctionCall functionCall = (ClaimFunctionCall) currentConstruct;
		functionCall.setMyBehavior(currentBehavior);
		if(functionCall.getArguments()!=null)
			for (ClaimConstruct subConstruct:functionCall.getArguments())
				setBehaviorRecursively(subConstruct, currentBehavior);
		break;
	case STRUCTURE:
		ClaimStructure currentStructure = (ClaimStructure) currentConstruct;
		currentStructure.setMyBehavior(currentBehavior);
		for (ClaimConstruct subConstruct:currentStructure.getFields())
			setBehaviorRecursively(subConstruct, currentBehavior);
		break;
	case IF:
		ClaimIf currentIf = (ClaimIf) currentConstruct;
		currentIf.setMyBehavior(currentBehavior);
		
		currentSubConstruct = currentIf.getCondition();
		setBehaviorRecursively(currentSubConstruct, currentBehavior);
		
		for (ClaimConstruct subConstruct:currentIf.getTrueBranch())
			setBehaviorRecursively(subConstruct, currentBehavior);
		
		if(currentIf.getFalseBranch() != null)
			for (ClaimConstruct subConstruct:currentIf.getFalseBranch())
				setBehaviorRecursively(subConstruct, currentBehavior);
		break;
	case FORALLK:
		ClaimForAllK currentForAllK = (ClaimForAllK) currentConstruct;
		currentForAllK.setMyBehavior(currentBehavior);
		
		currentSubConstruct = currentForAllK.getStructure();
		setBehaviorRecursively(currentSubConstruct, currentBehavior);
		
		for (ClaimConstruct subConstruct:currentForAllK.getStatements())
			setBehaviorRecursively(subConstruct, currentBehavior);
		break;
	case WHILE:
		ClaimWhile currentWhile = (ClaimWhile) currentConstruct;
		currentWhile.setMyBehavior(currentBehavior);
		
		currentSubConstruct = currentWhile.getCondition();
		setBehaviorRecursively(currentSubConstruct, currentBehavior);
		
		for (ClaimConstruct subConstruct:currentWhile.getStatements())
			setBehaviorRecursively(subConstruct, currentBehavior);
		break;
	case CONDITION:
		ClaimCondition currentCond = (ClaimCondition) currentConstruct;
		currentCond.setMyBehavior(currentBehavior);
		
		currentSubConstruct = currentCond.getCondition();
		setBehaviorRecursively(currentSubConstruct, currentBehavior);
		break;
	default:
		break;
	}
}

/**
 * Function used after the creation of the agent definition object, in order to populate the symbol table 
 * with variables (which are unbound, for the moment)
 * 
 * @param currentBehavior - the behavior which is used as scope for the symbol table
 */
public void fillSymbolTable(ClaimBehaviorDefinition currentBehavior)
{
	for(ClaimConstruct currentConstruct:currentBehavior.getStatements())
		fillSymbolTableRecursively(currentConstruct);
}

/**
 * Function used by the method <em>fillSymbolTable(ClaimBehaviorDefinition)</em> in order to put the
 * variables that belong to a behavior in the corresponding symbol table
 *  
 * @param currentConstruct - the construct which is analyzed in order to be put in the symbol table
 */
public void fillSymbolTableRecursively(ClaimConstruct currentConstruct)
{
	ClaimConstruct currentSubConstruct;
	switch(currentConstruct.getType())
	{
	case VARIABLE:
		ClaimVariable currentVariable = (ClaimVariable) currentConstruct;
		if(currentVariable.getMyBehavior().getSymbolTable().containsSymbol(currentVariable)==false)
		{
			ClaimVariable complementaryVariable = currentVariable.getComplement(); //variable complementary in what concerns the affectability
			if(currentVariable.getMyBehavior().getSymbolTable().containsSymbol(complementaryVariable)==false)
				currentVariable.getMyBehavior().getSymbolTable().put(currentVariable, null);
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
		break;
	case FUNCTION_CALL:
		ClaimFunctionCall functionCall = (ClaimFunctionCall) currentConstruct;
		if(functionCall.getArguments()!=null)
			for (ClaimConstruct subConstruct:functionCall.getArguments())
				fillSymbolTableRecursively(subConstruct);
		break;
	case STRUCTURE:
		ClaimStructure currentStructure = (ClaimStructure) currentConstruct;
		for (ClaimConstruct subConstruct:currentStructure.getFields())
			fillSymbolTableRecursively(subConstruct);
		break;
	case IF:
		ClaimIf currentIf = (ClaimIf) currentConstruct;
		
		currentSubConstruct = currentIf.getCondition();
		fillSymbolTableRecursively(currentSubConstruct);
		
		for (ClaimConstruct subConstruct:currentIf.getTrueBranch())
			fillSymbolTableRecursively(subConstruct);
		
		if(currentIf.getFalseBranch() != null)
			for (ClaimConstruct subConstruct:currentIf.getFalseBranch())
				fillSymbolTableRecursively(subConstruct);
		break;
	case FORALLK:
		ClaimForAllK currentForAllK = (ClaimForAllK) currentConstruct;
		
		currentSubConstruct = currentForAllK.getStructure();
		fillSymbolTableRecursively(currentSubConstruct);
		
		for (ClaimConstruct subConstruct:currentForAllK.getStatements())
			fillSymbolTableRecursively(subConstruct);
		break;
	case WHILE:
		ClaimWhile currentWhile = (ClaimWhile) currentConstruct;
		
		currentSubConstruct = currentWhile.getCondition();
		fillSymbolTableRecursively(currentSubConstruct);
		
		for (ClaimConstruct subConstruct:currentWhile.getStatements())
			fillSymbolTableRecursively(subConstruct);
		break;
	case CONDITION:
		ClaimCondition currentCond = (ClaimCondition) currentConstruct;
		
		currentSubConstruct = currentCond.getCondition();
		fillSymbolTableRecursively(currentSubConstruct);
		break;
	default:
		break;
	}
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
public ParserClaim2(String filePathAndName) {
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
  ParserClaim2 yyparser;
  if(args.length>0)
  {
	yyparser = new ParserClaim2(args[0]);
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
//#line 773 "ParserClaim2.java"
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
//#line 35 "parser.y"
{
			/*log.info("variable -> VARIABLE:"+lexer.yytext().substring(1));*/
			yyval = new ParserClaim2Val(new ClaimVariable(lexer.yytext().substring(1)));
		}
break;
case 2:
//#line 40 "parser.y"
{
			/*log.info("variable -> AFFECTABLE_VARIABLE:"+lexer.yytext().substring(2));*/
			yyval = new ParserClaim2Val(new ClaimVariable(lexer.yytext().substring(2),true));
		}
break;
case 3:
//#line 47 "parser.y"
{
			/*log.info("claim_variable -> variable");*/
			yyval = val_peek(0);
		}
break;
case 4:
//#line 52 "parser.y"
{
			/*log.info("claim_variable -> THIS");*/
			yyval = new ParserClaim2Val(new ClaimVariable("this"));
		}
break;
case 5:
//#line 57 "parser.y"
{
			/*log.info("claim_variable -> PARENT");*/
			yyval = new ParserClaim2Val(new ClaimVariable("parent",true));
		}
break;
case 6:
//#line 65 "parser.y"
{
			/*//log.info("constant -> STRING_LITERAL:"+lexer.yytext());*/
			String content = lexer.yytext();
			yyval = new ParserClaim2Val(new ClaimValue(content.substring(1,content.length()-1)));
		}
break;
case 7:
//#line 71 "parser.y"
{
			/*log.info("constant -> CONSTANT: "+lexer.yytext());*/
			yyval = new ParserClaim2Val(new ClaimValue(lexer.yytext()));
		}
break;
case 8:
//#line 80 "parser.y"
{
			/*log.info("structure_field -> claim_variable");*/
			yyval = val_peek(0);
		}
break;
case 9:
//#line 85 "parser.y"
{
			/*log.info("structure_field -> constant: "+lexer.yytext());*/
			yyval = val_peek(0);
		}
break;
case 10:
//#line 90 "parser.y"
{
			/*log.info("structure_field -> structure");*/
			yyval = val_peek(0);
		}
break;
case 11:
//#line 98 "parser.y"
{
			/*log.info("structure_field_list -> structure_field");*/
			yyval = new ParserClaim2Val(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 12:
//#line 104 "parser.y"
{
			/*log.info("structure_field_list -> structure_field_list structure_field");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 13:
//#line 118 "parser.y"
{
			/*log.info("structure -> '(' STRUCT structure_field_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimStructure(val_peek(1).claimConstructVector));
		}
break;
case 14:
//#line 126 "parser.y"
{
			/*log.info("argument -> claim_variable");*/
			yyval = val_peek(0);
		}
break;
case 15:
//#line 131 "parser.y"
{
			/*log.info("argument -> constant: "+lexer.yytext());*/
			yyval = val_peek(0);
		}
break;
case 16:
//#line 136 "parser.y"
{
			/*log.info("argument -> structure");*/
			yyval = val_peek(0);
		}
break;
case 17:
//#line 141 "parser.y"
{
			/*log.info("argument -> functions");*/
			yyval = val_peek(0);
		}
break;
case 18:
//#line 146 "parser.y"
{
			/*log.info("argument -> readK_function");*/
			yyval = val_peek(0);
		}
break;
case 19:
//#line 154 "parser.y"
{
			/*log.info("argument_list -> argument");*/
			yyval = new ParserClaim2Val(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 20:
//#line 160 "parser.y"
{
			/*log.info("argument_list -> argument_list argument");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 21:
//#line 169 "parser.y"
{
			/*log.info("name -> CONSTANT: "+lexer.yytext());*/
			yyval = new ParserClaim2Val(lexer.yytext());
		}
break;
case 22:
//#line 177 "parser.y"
{
			/*log.info("agent_argument -> variable");*/
			yyval = val_peek(0);
		}
break;
case 23:
//#line 185 "parser.y"
{
			/*log.info("agent_argument_list -> agent_argument");*/
			
			/*register the language variables in the list of agent parameters*/
			Vector<ClaimConstruct> languageParameters = new Vector<ClaimConstruct>();
			languageParameters.add(new ClaimVariable("this"));
			languageParameters.add(new ClaimVariable("parent",true));
			
			yyval = new ParserClaim2Val(new Vector<ClaimConstruct>(languageParameters));
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 24:
//#line 197 "parser.y"
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
//#line 223 "parser.y"
{
			/*log.info("behavior_type -> INITIAL");*/
			yyval = new ParserClaim2Val(ClaimBehaviorType.INITIAL);
		}
break;
case 26:
//#line 228 "parser.y"
{
			/*log.info("behavior_type -> REACTIVE");*/
			yyval = new ParserClaim2Val(ClaimBehaviorType.REACTIVE);
		}
break;
case 27:
//#line 233 "parser.y"
{
			/*log.info("behavior_type -> CYCLIC");*/
			yyval = new ParserClaim2Val(ClaimBehaviorType.CYCLIC);
		}
break;
case 28:
//#line 238 "parser.y"
{
			/*log.info("behavior_type -> PROACTIVE");*/
			yyval = new ParserClaim2Val(ClaimBehaviorType.PROACTIVE);
		}
break;
case 29:
//#line 251 "parser.y"
{
			/*log.info("message_structure -> '(' STRUCT MESSAGE structure_field_list ')'");*/
			Vector<ClaimConstruct> structFields = new Vector<ClaimConstruct>();
			structFields.add(new ClaimValue(new String("message")));
			structFields.addAll(val_peek(1).claimConstructVector);
			yyval = new ParserClaim2Val(new ClaimStructure(structFields));
		}
break;
case 30:
//#line 259 "parser.y"
{
			/*log.info("message_structure -> '(' MESSAGE structure_field_list ')'");*/
			Vector<ClaimConstruct> structFields = new Vector<ClaimConstruct>();
			structFields.add(new ClaimValue(new String("message")));
			structFields.addAll(val_peek(1).claimConstructVector);
			yyval = new ParserClaim2Val(new ClaimStructure(structFields));
		}
break;
case 31:
//#line 270 "parser.y"
{
			/*log.info("message_argument -> variable");*/
			yyval = val_peek(0);
		}
break;
case 32:
//#line 275 "parser.y"
{
			/*log.info("message_argument -> constant: "+lexer.yytext());*/
			yyval = val_peek(0);
		}
break;
case 33:
//#line 280 "parser.y"
{
			/*log.info("message_argument -> message_structure");*/
			yyval = val_peek(0);
		}
break;
case 34:
//#line 288 "parser.y"
{
			/*log.info("argument_list -> message_argument");*/
			yyval = new ParserClaim2Val(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 35:
//#line 294 "parser.y"
{
			/*log.info("argument_list -> message_argument_list message_argument");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 36:
//#line 303 "parser.y"
{
			/*log.info("send_function -> '(' SEND message_argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.SEND ,new String("send"),val_peek(1).claimConstructVector));
		}
break;
case 37:
//#line 311 "parser.y"
{
			/*log.info("receive_function -> '(' RECEIVE message_argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.RECEIVE ,new String("receive"),val_peek(1).claimConstructVector));
		}
break;
case 38:
//#line 319 "parser.y"
{
			/*log.info("in_function -> '(' INPUT argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.INPUT ,new String("input"),val_peek(1).claimConstructVector));
		}
break;
case 39:
//#line 326 "parser.y"
{
			/*log.info("in_function -> '(' OUTPUT argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.OUTPUT ,new String("output"),val_peek(1).claimConstructVector));
		}
break;
case 40:
//#line 333 "parser.y"
{
			/*log.info("in_function -> '(' IN argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.IN ,new String("in"),val_peek(1).claimConstructVector));
		}
break;
case 41:
//#line 341 "parser.y"
{
			/*log.info("out_function -> '(' OUT argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.OUT ,new String("out"),val_peek(1).claimConstructVector));
		}
break;
case 42:
//#line 349 "parser.y"
{
			/*log.info("open_function -> '(' OPEN argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.OPEN ,new String("open"),val_peek(1).claimConstructVector));
		}
break;
case 43:
//#line 357 "parser.y"
{
			/*log.info("acid_function -> '(' ACID argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.ACID ,new String("acid"),null));
		}
break;
case 44:
//#line 365 "parser.y"
{
			/*log.info("new_function -> '(' NEW argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.NEW ,new String("new"),val_peek(1).claimConstructVector));
		}
break;
case 45:
//#line 373 "parser.y"
{
			/*log.info("addK_function -> '(' ADDK argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.ADDK ,new String("addK"),val_peek(1).claimConstructVector));
		}
break;
case 46:
//#line 381 "parser.y"
{
			/*log.info("readK_function -> '(' READK argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.READK ,new String("readK"),val_peek(1).claimConstructVector));
		}
break;
case 47:
//#line 389 "parser.y"
{
			/*log.info("removeK_function -> '(' REMOVEK argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.REMOVEK ,new String("removeK"),val_peek(1).claimConstructVector));
		}
break;
case 48:
//#line 397 "parser.y"
{
			/*log.info("wait_function -> '(' WAIT argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall( ClaimFunctionType.WAIT ,new String("wait"),val_peek(1).claimConstructVector));
		}
break;
case 49:
//#line 405 "parser.y"
{
			/*log.info("language_function -> send_function");*/
			yyval = val_peek(0);
		}
break;
case 50:
//#line 415 "parser.y"
{
			/*log.info("language_function -> output_function");*/
			yyval = val_peek(0);
		}
break;
case 51:
//#line 420 "parser.y"
{
			/*log.info("language_function -> in_function");*/
			yyval = val_peek(0);
		}
break;
case 52:
//#line 425 "parser.y"
{
			/*log.info("language_function -> out_function");*/
			yyval = val_peek(0);
		}
break;
case 53:
//#line 430 "parser.y"
{
			/*log.info("language_function -> open_function");*/
			yyval = val_peek(0);
		}
break;
case 54:
//#line 435 "parser.y"
{
			/*log.info("language_function -> acid_function");*/
			yyval = val_peek(0);
		}
break;
case 55:
//#line 440 "parser.y"
{
			/*log.info("language_function -> new_function");*/
			yyval = val_peek(0);
		}
break;
case 56:
//#line 445 "parser.y"
{
			/*log.info("language_function -> addk_function");*/
			yyval = val_peek(0);
		}
break;
case 57:
//#line 450 "parser.y"
{
			/*log.info("language_function -> readk_function");*/
			yyval = val_peek(0);
		}
break;
case 58:
//#line 455 "parser.y"
{
			/*log.info("language_function -> removek_function");*/
			yyval = val_peek(0);
		}
break;
case 59:
//#line 460 "parser.y"
{
			/*log.info("language_function -> wait_function");*/
			yyval = val_peek(0);
		}
break;
case 60:
//#line 468 "parser.y"
{
			/*log.info("function -> '(' name ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall(ClaimFunctionType.JAVA,val_peek(1).sval,null));
		}
break;
case 61:
//#line 473 "parser.y"
{
			/*log.info("function -> '(' name argument_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimFunctionCall(ClaimFunctionType.JAVA,val_peek(2).sval,val_peek(1).claimConstructVector));
		}
break;
case 62:
//#line 481 "parser.y"
{
			/*log.info("goal -> '(' AGOAL name proposition_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimaGoal(ClaimConstructType.AGOAL, val_peek(4).sval, val_peek(1).claimConstructVector, val_peek(3).ival));
		}
break;
case 63:
//#line 486 "parser.y"
{
			yyval = new ParserClaim2Val(new ClaimmGoal(ClaimConstructType.MGOAL, val_peek(6).sval, val_peek(3).claimConstructVector, val_peek(1).claimConstructVector, Integer.parseInt(((ClaimValue) val_peek(5).obj).toString())));	
		}
break;
case 64:
//#line 490 "parser.y"
{
			yyval = yyval = new ParserClaim2Val(new ClaimmGoal(ClaimConstructType.MGOAL, val_peek(4).sval, val_peek(1).claimConstructVector, null, Integer.parseInt(((ClaimValue) val_peek(3).obj).toString())));
		}
break;
case 65:
//#line 494 "parser.y"
{
			yyval = new ParserClaim2Val(new ClaimpGoal(ClaimConstructType.PGOAL, val_peek(4).sval, val_peek(1).claimConstruct, Integer.parseInt(((ClaimValue) val_peek(3).obj).toString())));
		}
break;
case 66:
//#line 501 "parser.y"
{
			/*log.info("proposition_list -> proposition");*/
			yyval = new ParserClaim2Val(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 67:
//#line 507 "parser.y"
{
			/*log.info("proposition_list -> proposition_list proposition");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 68:
//#line 516 "parser.y"
{
			/*log.info("proposition -> constant ");*/
			yyval = val_peek(0);
		}
break;
case 69:
//#line 524 "parser.y"
{
			/*log.info("priority -> constant");*/
			yyval = val_peek(0);
		}
break;
case 70:
//#line 533 "parser.y"
{
			/*log.info("valid_condition -> readK_function");*/
			yyval = val_peek(0);
		}
break;
case 71:
//#line 538 "parser.y"
{
			/*log.info("valid_condition -> function");*/
			yyval = val_peek(0);
		}
break;
case 72:
//#line 546 "parser.y"
{
			/*log.info("condition -> '(' CONDITION valid_condition ')'");*/
			yyval = new ParserClaim2Val(new ClaimCondition((ClaimFunctionCall) val_peek(1).claimConstruct));
		}
break;
case 73:
//#line 554 "parser.y"
{
			/*log.info("if_stmt -> '(' IF if_valid_condition THEN behavior_content_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimIf((ClaimFunctionCall) val_peek(3).claimConstruct,val_peek(1).claimConstructVector, null));
		}
break;
case 74:
//#line 559 "parser.y"
{
			/*log.info("if_stmt -> '(' IF if_valid_condition THEN behavior_content_list ELSE behavior_content_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimIf((ClaimFunctionCall) val_peek(5).claimConstruct,val_peek(3).claimConstructVector,val_peek(1).claimConstructVector));
		}
break;
case 75:
//#line 567 "parser.y"
{
			/*log.info("forAllK -> '(' FORALLK structure ')'");*/
			if (!verifyVariablesInStructure((ClaimStructure) val_peek(1).claimConstruct))
				yywarn("there is no variable in the structure of forAllK");
			yyval = new ParserClaim2Val(new ClaimForAllK((ClaimStructure) val_peek(1).claimConstruct, null));
		}
break;
case 76:
//#line 574 "parser.y"
{
			/*log.info("forAllK -> '(' FORALLK structure behavior_content_list ')'");*/
			if (!verifyVariablesInStructure((ClaimStructure) val_peek(2).claimConstruct))
				yywarn("there is no variable in the structure of forAllK");
			yyval = new ParserClaim2Val(new ClaimForAllK((ClaimStructure) val_peek(2).claimConstruct, val_peek(1).claimConstructVector));
		}
break;
case 77:
//#line 584 "parser.y"
{
			/*log.info("while -> '(' WHILE function ')'");*/
			yyval = new ParserClaim2Val(new ClaimWhile((ClaimFunctionCall) val_peek(1).claimConstruct, null));
		}
break;
case 78:
//#line 589 "parser.y"
{
			/*log.info("while -> '(' WHILE function behavior_content_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimWhile((ClaimFunctionCall) val_peek(2).claimConstruct, val_peek(1).claimConstructVector));
		}
break;
case 79:
//#line 597 "parser.y"
{
			/*log.info("behavior_content_header -> receive_function");*/
			yyval = val_peek(0);
		}
break;
case 80:
//#line 602 "parser.y"
{
			/*log.info("behavior_content_header -> receive_function");*/
			yyval = val_peek(0);
		}
break;
case 81:
//#line 607 "parser.y"
{
			/*log.info("behavior_content_header -> condition");*/
			yyval = val_peek(0);
		}
break;
case 82:
//#line 615 "parser.y"
{
			/*log.info("behavior_content_header_list -> behavior_content_header");*/
			yyval = new ParserClaim2Val(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 83:
//#line 621 "parser.y"
{
			/*log.info("behavior_content_header_list -> behavior_content_header_list behavior_content_header");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 84:
//#line 631 "parser.y"
{
			/*log.info("behavior_content -> language_function");*/
			yyval = val_peek(0);
		}
break;
case 85:
//#line 636 "parser.y"
{
			/*log.info("behavior_content -> function");*/
			yyval = val_peek(0);
		}
break;
case 86:
//#line 641 "parser.y"
{
			/*log.info("behavior_content -> if_stmt");*/
			yyval = val_peek(0);
		}
break;
case 87:
//#line 646 "parser.y"
{
			/*log.info("behavior_content -> forAllK");*/
			yyval = val_peek(0);
		}
break;
case 88:
//#line 651 "parser.y"
{
			/*log.info("behavior_content -> while");*/
			yyval = val_peek(0);
		}
break;
case 89:
//#line 656 "parser.y"
{
			yyval = val_peek(0);
		}
break;
case 90:
//#line 663 "parser.y"
{
			/*log.info("behavior_content_list -> behavior_content");*/
			yyval = new ParserClaim2Val(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 91:
//#line 669 "parser.y"
{
			/*log.info("behavior_content_list -> behavior_content_list behavior_content");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 92:
//#line 685 "parser.y"
{
			/*log.info("behavior -> '(' behavior_type name behavior_content_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimBehaviorDefinition(val_peek(2).sval, val_peek(3).claimBehaviorType, val_peek(1).claimConstructVector));
			ClaimBehaviorDefinition currentBehavior = (ClaimBehaviorDefinition) yyval.claimConstruct;
			setBehaviorToSubConstructs(currentBehavior);
		}
break;
case 93:
//#line 692 "parser.y"
{
			/*log.info("behavior -> '(' behavior_type name behavior_content_header_list ')'");*/
			yyval = new ParserClaim2Val(new ClaimBehaviorDefinition(val_peek(2).sval, val_peek(3).claimBehaviorType, val_peek(1).claimConstructVector));
			ClaimBehaviorDefinition currentBehavior = (ClaimBehaviorDefinition) yyval.claimConstruct;
			setBehaviorToSubConstructs(currentBehavior);
		}
break;
case 94:
//#line 699 "parser.y"
{
			/*log.info("behavior -> '(' behavior_type name behavior_content_header behavior_content_list ')'");*/
			Boolean concatenateSucceeded = val_peek(2).claimConstructVector.addAll(val_peek(1).claimConstructVector);
			if(concatenateSucceeded)
				yyval = new ParserClaim2Val(new ClaimBehaviorDefinition(val_peek(3).sval, val_peek(4).claimBehaviorType, val_peek(2).claimConstructVector));
			else
				log.error("error while concatenating the vectors of statements, while creating the behavior");
			ClaimBehaviorDefinition currentBehavior = (ClaimBehaviorDefinition) yyval.claimConstruct;
			setBehaviorToSubConstructs(currentBehavior);
		}
break;
case 95:
//#line 713 "parser.y"
{
			/*log.info("behavior_list -> behavior");*/
			yyval = new ParserClaim2Val(new Vector<ClaimConstruct>());
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 96:
//#line 719 "parser.y"
{
			/*log.info("behavior_list -> behavior_list behavior");*/
			yyval = val_peek(1);
			yyval.claimConstructVector.add(val_peek(0).claimConstruct);
		}
break;
case 97:
//#line 728 "parser.y"
{
			/*log.info("behavior_declaration -> '(' BEHAVIOR behavior_list ')'");*/
			yyval = val_peek(1);
		}
break;
case 98:
//#line 736 "parser.y"
{
			/*log.info("agent_specification -> '(' AGENT name agent_argument_list behaviors_declaration ')'");*/
			parsedAgent = new ClaimAgentDefinition(val_peek(3).sval, 
					new Vector<ClaimConstruct>(val_peek(2).claimConstructVector),
					new Vector<ClaimBehaviorDefinition>(
							Arrays.asList(val_peek(1).claimConstructVector.toArray(new ClaimBehaviorDefinition [0]))
					));
			/*Set the references of the contained behaviors to this agent:*/
			for(ClaimBehaviorDefinition currentBehavior:parsedAgent.getBehaviors())
			{
				currentBehavior.setMyAgent(parsedAgent);
				currentBehavior.initSymbolTable();
				fillSymbolTable(currentBehavior);
			}
		}
break;
case 99:
//#line 752 "parser.y"
{
			/*log.info("agent_specification -> '(' AGENT name behaviors_declaration ')'");*/
			
			/*register the language variables in the list of agent parameters*/
			Vector<ClaimConstruct> languageParameters = new Vector<ClaimConstruct>();
			languageParameters.add(new ClaimVariable("this"));
			languageParameters.add(new ClaimVariable("parent",true));
			
			parsedAgent = new ClaimAgentDefinition(val_peek(2).sval,languageParameters,
					new Vector<ClaimBehaviorDefinition>(
							Arrays.asList(val_peek(1).claimConstructVector.toArray(new ClaimBehaviorDefinition [0]))
					));
			/*Set the references of the contained behaviors to this agent:*/
			for(ClaimBehaviorDefinition currentBehavior:parsedAgent.getBehaviors())
			{
				currentBehavior.setMyAgent(parsedAgent);
				currentBehavior.initSymbolTable();
				fillSymbolTable(currentBehavior);
			}
		}
break;
//#line 1698 "ParserClaim2.java"
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
public ParserClaim2()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public ParserClaim2(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
