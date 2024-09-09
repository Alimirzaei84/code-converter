package Languages.Java;

import java_cup.runtime.*;

%%

%unicode
%cup
%class JavaLexer

number	    = [0-9][0-9]*
variable    = [a-zA-Z][a-zA-Z0-9_]*
space       = [\r\n\f\t ]+

%%

"public"                {return new Symbol(JavaParserSym.PUBLIC);}
"static"                {return new Symbol(JavaParserSym.STATIC);}
"void"                  {return new Symbol(JavaParserSym.VOID);}
"int"                   {return new Symbol(JavaParserSym.INT);}
"("                     {return new Symbol(JavaParserSym.LPAR);}
")"                     {return new Symbol(JavaParserSym.RPAR);}
"{"                     {return new Symbol(JavaParserSym.LBRACKET);}
"}"                     {return new Symbol(JavaParserSym.RBRACKET);}
";"                     {return new Symbol(JavaParserSym.SEMICOLON);}
","                     {return new Symbol(JavaParserSym.COMMA);}
"break"                 {return new Symbol(JavaParserSym.BREAK);}
"continue"              {return new Symbol(JavaParserSym.CONTINUE);}
"System.out.println"    {return new Symbol(JavaParserSym.PRINT);}
"=="                    {return new Symbol(JavaParserSym.EQ);}
"="                     {return new Symbol(JavaParserSym.EQUAL);}
"if"                    {return new Symbol(JavaParserSym.IF);}
"else"                  {return new Symbol(JavaParserSym.ELSE);}
"switch"                {return new Symbol(JavaParserSym.SWITCH);}
"case"                  {return new Symbol(JavaParserSym.CASE);}
":"                     {return new Symbol(JavaParserSym.COLON);}
"default"               {return new Symbol(JavaParserSym.DEFAULT);}
"while"                 {return new Symbol(JavaParserSym.WHILE);}
"||"                    {return new Symbol(JavaParserSym.OR);}
"&&"                    {return new Symbol(JavaParserSym.AND);}
"!"                     {return new Symbol(JavaParserSym.NOT);}
"<"                     {return new Symbol(JavaParserSym.LT);}
">"                     {return new Symbol(JavaParserSym.GT);}
"+"                     {return new Symbol(JavaParserSym.PLUS);}
"-"                     {return new Symbol(JavaParserSym.MINUS);}
"*"                     {return new Symbol(JavaParserSym.TIMES);}
"/"                     {return new Symbol(JavaParserSym.DIVIDES);}
"%"                     {return new Symbol(JavaParserSym.MOD);}
{number}                {return new Symbol(JavaParserSym.NUM, yytext());}
{variable}              {return new Symbol(JavaParserSym.ID, yytext());}
{space}                 {}
.		                {System.err.println("Error:" + yytext());}
