package parser;

import java_cup.runtime.*;

%%

%class TestLangLexer
%cup
%line
%column
%unicode

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1, yytext());
    }
    
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
    
    private void error(String message) {
        System.err.println("Lexical error at line " + (yyline + 1) + ", column " + (yycolumn + 1) + ": " + message);
    }
%}

%%

<YYINITIAL> {
    // Keywords
    "config"          { return symbol(sym.CONFIG); }
    "base_url"        { return symbol(sym.BASE_URL); }
    "header"          { return symbol(sym.HEADER); }
    "let"             { return symbol(sym.LET); }
    "test"            { return symbol(sym.TEST); }
    "GET"             { return symbol(sym.GET); }
    "POST"            { return symbol(sym.POST); }
    "PUT"             { return symbol(sym.PUT); }
    "DELETE"          { return symbol(sym.DELETE); }
    "expect"          { return symbol(sym.EXPECT); }
    "status"          { return symbol(sym.STATUS); }
    "body"            { return symbol(sym.BODY); }
    "contains"        { return symbol(sym.CONTAINS); }
    
    // Identifiers
    [A-Za-z_][A-Za-z0-9_]* { return symbol(sym.IDENTIFIER, yytext()); }
    
    // Numbers
    [0-9]+            { return symbol(sym.NUMBER, Integer.valueOf(yytext())); }
    
    // Strings with escape sequences
    \"([^\"\\]|\\.)*\" { 
        String str = yytext();
        str = str.substring(1, str.length() - 1);
        str = str.replace("\\\"", "\"").replace("\\\\", "\\");
        return symbol(sym.STRING, str);
    }
    
    // Variable references
    \$[A-Za-z_][A-Za-z0-9_]* { return symbol(sym.VAR_REF, yytext().substring(1)); }
    
    // Operators and punctuation
    "="               { return symbol(sym.EQUALS); }
    ";"               { return symbol(sym.SEMICOLON); }
    "{"               { return symbol(sym.LBRACE); }
    "}"               { return symbol(sym.RBRACE); }
    "("               { return symbol(sym.LPAREN); }
    ")"               { return symbol(sym.RPAREN); }
    
    // Line comments
    "//"[^\r\n]*      { /* ignore line comments */ }
    
    // Whitespace
    [ \t\r\n]+        { /* ignore whitespace */ }
    
    // Error
    .                 { error("Illegal character: " + yytext()); }
}
