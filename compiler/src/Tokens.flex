import java_cup.runtime.Symbol;

%%

%public
%class StandaloneLexer
%cup
%unicode
%line
%column

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1, null);
    }
    
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
    
    private void error(String message) {
        System.err.println("Lexical error at line " + (yyline + 1) + ", column " + (yycolumn + 1) + ": " + message);
    }
    
    private String unescape(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\' && i + 1 < text.length()) {
                char next = text.charAt(++i);
                switch (next) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case '\\': sb.append('\\'); break;
                    case '"': sb.append('\"'); break;
                    default: sb.append(next); break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
%}

%%

<YYINITIAL> {
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

    [A-Za-z_][A-Za-z0-9_]* { return symbol(sym.IDENTIFIER, yytext()); }

    [0-9]+ { return symbol(sym.NUMBER, Integer.valueOf(yytext())); }

    \"([^\"\\]|\\.)*\" {
        String raw = yytext();
        String inner = raw.substring(1, raw.length() - 1);
        return symbol(sym.STRING, unescape(inner));
    }

    \$[A-Za-z_][A-Za-z0-9_]* { return symbol(sym.VAR_REF, yytext().substring(1)); }

    "="  { return symbol(sym.EQUALS); }
    ";"  { return symbol(sym.SEMICOLON); }
    "{"  { return symbol(sym.LBRACE); }
    "}"  { return symbol(sym.RBRACE); }
    "("  { return symbol(sym.LPAREN); }
    ")"  { return symbol(sym.RPAREN); }

    "//"[^\r\n]* { /* skip comments */ }

    [ \t\r\n]+ { /* skip whitespace */ }

    . { error("Illegal character: " + yytext()); }
}
