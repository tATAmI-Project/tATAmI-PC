/* Lexer specification to be used with jflex, in order to generate the lexical analyzer of Claim2.
To generate the Lexer, in Linux, run the script "generateLexer.sh", included in the directory */
package core.claim.parser;

%%
%class Yylex
%unicode
%byaccj
%line
%column

%{
  /* store a reference to the parser object */
  private ParserClaim2 yyparser;

  /* constructor taking an additional ParserClaim2 object */
  public Yylex(java.io.Reader r, ParserClaim2 yyparser) {
    this(r);
    this.yyparser = yyparser;
  }
  
  public String getPosition()
  {
  	return new String("at line "+(yyline+1)+" and column "+(yycolumn+1));
  }

  public String getLine()
  {
  	return new String("at line "+(yyline+1));
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

D		=	[0-9]
L		=	[a-zA-Z_]

StringLiteral = \"(\\.|[^\\\"]|[^\\\n])*\" 
Variable = "?"({L}|{D})+
AffectableVariable = "??"({L}|{D})+
/*Constant = ({L}|{D}|"~"|"!"|"@"|"#"|"$"|"%"|"^"|"&"|"*"|"_"|"+"|"-"|"="|"`"|"}"|"{"|"]"|"["|"|"|":"|"\\"|";"|"\""|"'"|">"|"<"|"."|","|"/")+*/
Constant = ([^?)(\t\f \r\n])+

%%
/* the reference to the instantiated agent */
"this"			{ return ParserClaim2.THIS; }

/* the reference to the parent of the agent */
"parent"			{ return ParserClaim2.PARENT; }

/* special claim keywords */
"struct"		{ return ParserClaim2.STRUCT; }
"agent"			{ return ParserClaim2.AGENT; }
"behavior"		{ return ParserClaim2.BEHAVIOR; }

/* input output functions */
"input"		{ return ParserClaim2.INPUT; }
"output"	{ return ParserClaim2.OUTPUT; }

/* behavior types */
"initial"		{ return ParserClaim2.INITIAL; }
"reactive"		{ return ParserClaim2.REACTIVE; }
"cyclic"		{ return ParserClaim2.CYCLIC; }
"proactive"		{ return ParserClaim2.PROACTIVE; }


/* Constructs that do something based on a condition and their associated keywords */
"if"			{ return ParserClaim2.IF; }
"condition"		{ return ParserClaim2.CONDITION; }
"then"			{ return ParserClaim2.THEN; }
"else"			{ return ParserClaim2.ELSE; }

/* functions to be used in order to communicate - syntax similar to the function calls */
"receive"		{ return ParserClaim2.RECEIVE; }
"send"			{ return ParserClaim2.SEND; }
"message"		{ return ParserClaim2.MESSAGE; }

/* Interaction with the knowledge base - syntax similar to the function calls */
"addK"			{ return ParserClaim2.ADDK; }
"readK"			{ return ParserClaim2.READK; }
"removeK"		{ return ParserClaim2.REMOVEK; }

/* actions that affect the topology of the population of the agents - syntax similar to the function calls */
"in"		{ return ParserClaim2.IN; }
"out"		{ return ParserClaim2.OUT; }
"open"		{ return ParserClaim2.OPEN; }
"acid"		{ return ParserClaim2.ACID; }
"new"		{ return ParserClaim2.NEW; }

/* proactive behavior */
"aGoal"		{ return ParserClaim2.AGOAL; } /* Achievement Goal */
"mGoal"		{ return ParserClaim2.MGOAL; } /* Maintain Goal */
"pGoal"		{ return ParserClaim2.PGOAL; } /* Perform Goal */

/* GOAL KEYWORDS */
"achieve" 	{ return ParserClaim2.ACHIEVE; }
"target"	{ return ParserClaim2.TARGET; }
"maintain"	{ return ParserClaim2.MAINTAIN; }
"action"	{ return ParserClaim2.ACTION; }

/* looping constructs */
"forAllK"	{ return ParserClaim2.FORALLK; }
"while"		{ return ParserClaim2.WHILE; }

/* other keywords - syntax similar to the function calls */
"wait"		{ return ParserClaim2.WAIT; }

"("			{ return (int) yycharat(0); }
")"			{ return (int) yycharat(0); }

{Variable}	{ 
				/* yyparser.yylval = new ParserClaim2Val(yytext().substring(1));*/
				return ParserClaim2.VARIABLE; 
			}
{AffectableVariable}	{ 
				/* yyparser.yylval = new ParserClaim2Val(yytext().substring(1));*/
				return ParserClaim2.AFFECTABLE_VARIABLE; 
			}
{StringLiteral}
			{
				return ParserClaim2.STRING_LITERAL;
			}

{Comment}               { /* ignore */ }

{WhiteSpace}            { /* ignore */ }

{Constant}	{ 
				/* yyparser.yylval = new ParserClaim2Val(yytext());*/
				return ParserClaim2.CONSTANT; 
			}

.			{ /* ignore bad characters */ }

