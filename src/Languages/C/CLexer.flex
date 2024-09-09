package Languages.C;

import java_cup.runtime.*;

%%

%unicode
%cup
%class CLexer

number	    = [0-9][0-9]*
variable    = [a-zA-Z][a-zA-Z0-9_]*
space       = [\r\n\f\t ]+

%%

"void"      {return new Symbol(CParserSym.VOID);}
"int"       {return new Symbol(CParserSym.INT);}
"("         {return new Symbol(CParserSym.LPAR);}
")"         {return new Symbol(CParserSym.RPAR);}
"{"         {return new Symbol(CParserSym.LBRACKET);}
"}"         {return new Symbol(CParserSym.RBRACKET);}
";"         {return new Symbol(CParserSym.SEMICOLON);}
","         {return new Symbol(CParserSym.COMMA);}
"break"     {return new Symbol(CParserSym.BREAK);}
"continue"  {return new Symbol(CParserSym.CONTINUE);}
"cout"      {return new Symbol(CParserSym.COUT);}
"<<"        {return new Symbol(CParserSym.OUTPUT);}
"=="        {return new Symbol(CParserSym.EQ);}
"="         {return new Symbol(CParserSym.EQUAL);}
"if"        {return new Symbol(CParserSym.IF);}
"else"      {return new Symbol(CParserSym.ELSE);}
"switch"    {return new Symbol(CParserSym.SWITCH);}
"case"      {return new Symbol(CParserSym.CASE);}
":"         {return new Symbol(CParserSym.COLON);}
"default"   {return new Symbol(CParserSym.DEFAULT);}
"while"     {return new Symbol(CParserSym.WHILE);}
"||"        {return new Symbol(CParserSym.OR);}
"&&"        {return new Symbol(CParserSym.AND);}
"!"         {return new Symbol(CParserSym.NOT);}
"<"         {return new Symbol(CParserSym.LT);}
">"         {return new Symbol(CParserSym.GT);}
"+"         {return new Symbol(CParserSym.PLUS);}
"-"         {return new Symbol(CParserSym.MINUS);}
"*"         {return new Symbol(CParserSym.TIMES);}
"/"         {return new Symbol(CParserSym.DIVIDES);}
"%"         {return new Symbol(CParserSym.MOD);}
{number}    {return new Symbol(CParserSym.NUM, yytext());}
{variable}  {return new Symbol(CParserSym.ID, yytext());}
{space}     {}
.		{System.err.println("Error:" + yytext());}
