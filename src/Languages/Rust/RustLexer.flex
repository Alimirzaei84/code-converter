package Languages.Rust;

import java_cup.runtime.*;

%%

%unicode
%cup
%class RustLexer

number	    = [0-9][0-9]*
variable    = [a-zA-Z][a-zA-Z0-9_]*
space       = [\r\n\f\t ]+

%%

"fn"                    {return new Symbol(RustParserSym.FUNCTION);}
"let"                   {return new Symbol(RustParserSym.LET);}
"("                     {return new Symbol(RustParserSym.LPAR);}
")"                     {return new Symbol(RustParserSym.RPAR);}
"{"                     {return new Symbol(RustParserSym.LBRACKET);}
"}"                     {return new Symbol(RustParserSym.RBRACKET);}
";"                     {return new Symbol(RustParserSym.SEMICOLON);}
","                     {return new Symbol(RustParserSym.COMMA);}
"break"                 {return new Symbol(RustParserSym.BREAK);}
"continue"              {return new Symbol(RustParserSym.CONTINUE);}
"println!"              {return new Symbol(RustParserSym.PRINT);}
"=="                    {return new Symbol(RustParserSym.EQ);}
"=>"                    {return new Symbol(RustParserSym.ARROW);}
"="                     {return new Symbol(RustParserSym.EQUAL);}
"_"                     {return new Symbol(RustParserSym.DEFAULT);}
"if"                    {return new Symbol(RustParserSym.IF);}
"else"                  {return new Symbol(RustParserSym.ELSE);}
"match"                 {return new Symbol(RustParserSym.MATCH);}
"while"                 {return new Symbol(RustParserSym.WHILE);}
"||"                    {return new Symbol(RustParserSym.OR);}
"|"                     {return new Symbol(RustParserSym.CASE_OR);}
"&&"                    {return new Symbol(RustParserSym.AND);}
"!"                     {return new Symbol(RustParserSym.NOT);}
"<"                     {return new Symbol(RustParserSym.LT);}
">"                     {return new Symbol(RustParserSym.GT);}
"+"                     {return new Symbol(RustParserSym.PLUS);}
"-"                     {return new Symbol(RustParserSym.MINUS);}
"*"                     {return new Symbol(RustParserSym.TIMES);}
"/"                     {return new Symbol(RustParserSym.DIVIDES);}
"%"                     {return new Symbol(RustParserSym.MOD);}
{number}                {return new Symbol(RustParserSym.NUM, yytext());}
{variable}              {return new Symbol(RustParserSym.ID, yytext());}
{space}                 {}
.		                {System.err.println("Error:" + yytext());}