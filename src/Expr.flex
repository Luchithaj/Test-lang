import java_cup.runtime.*;

%%

%class ExprLexer
%cup

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yytext());
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, value);
    }
%}

%%

<YYINITIAL> {
    [0-9]+          { return symbol(sym.NUMBER, Integer.valueOf(yytext())); }
    "+"             { return symbol(sym.PLUS); }
    "*"             { return symbol(sym.TIMES); }
    "("             { return symbol(sym.LPAREN); }
    ")"             { return symbol(sym.RPAREN); }
    [ \t\n\r]       { /* ignore whitespace */ }
    .               { System.err.println("Illegal character: " + yytext()); }
}

