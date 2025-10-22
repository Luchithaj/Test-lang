import java.io.*;
import java.util.*;

public class StandaloneLexer {
    private Reader reader;
    private int currentChar;
    private int line = 1;
    private int column = 1;
    
    public StandaloneLexer(Reader reader) {
        this.reader = reader;
        try {
            currentChar = reader.read();
        } catch (IOException e) {
            currentChar = -1;
        }
    }
    
    public Token nextToken() throws IOException {
        while (currentChar != -1) {
            if (Character.isWhitespace(currentChar)) {
                if (currentChar == '\n') {
                    line++;
                    column = 1;
                } else {
                    column++;
                }
                currentChar = reader.read();
                continue;
            }
            
            if (currentChar == -1) {
                return new Token(TokenType.EOF, "", line, column);
            }
            
            if (currentChar == '/' && peek() == '/') {
                skipLineComment();
                continue;
            }
            
            if (currentChar == '"') {
                return readString();
            }
            
            if (currentChar == '$') {
                return readVariableRef();
            }
            
            if (Character.isLetter(currentChar) || currentChar == '_') {
                return readIdentifier();
            }
            
            if (Character.isDigit(currentChar)) {
                return readNumber();
            }
            
            TokenType type = getSingleCharToken(currentChar);
            if (type != null) {
                String value = String.valueOf((char) currentChar);
                int currentLine = line;
                int currentColumn = column;
                currentChar = reader.read();
                column++;
                return new Token(type, value, currentLine, currentColumn);
            }
            
            System.err.println("Unknown character: " + (char) currentChar + " at line " + line + ", column " + column);
            currentChar = reader.read();
            column++;
        }
        
        return new Token(TokenType.EOF, "", line, column);
    }
    
    private int peek() throws IOException {
        // Simple peek - just return current char
        return currentChar;
    }
    
    private void skipLineComment() throws IOException {
        while (currentChar != -1 && currentChar != '\n') {
            currentChar = reader.read();
            column++;
        }
        if (currentChar == '\n') {
            line++;
            column = 1;
            currentChar = reader.read();
        }
    }
    
    private Token readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        int startLine = line;
        int startColumn = column;
        
        currentChar = reader.read();
        column++;
        
        while (currentChar != -1 && currentChar != '"') {
            if (currentChar == '\\') {
                currentChar = reader.read();
                column++;
                if (currentChar == 'n') {
                    sb.append('\n');
                } else if (currentChar == 't') {
                    sb.append('\t');
                } else if (currentChar == 'r') {
                    sb.append('\r');
                } else if (currentChar == '\\') {
                    sb.append('\\');
                } else if (currentChar == '"') {
                    sb.append('"');
                } else {
                    sb.append('\\').append((char) currentChar);
                }
            } else {
                sb.append((char) currentChar);
            }
            currentChar = reader.read();
            column++;
        }
        
        if (currentChar == '"') {
            currentChar = reader.read();
            column++;
        }
        
        return new Token(TokenType.STRING, sb.toString(), startLine, startColumn);
    }
    
    private Token readVariableRef() throws IOException {
        StringBuilder sb = new StringBuilder();
        int startLine = line;
        int startColumn = column;
        
        currentChar = reader.read();
        column++;
        
        while (currentChar != -1 && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            sb.append((char) currentChar);
            currentChar = reader.read();
            column++;
        }
        
        return new Token(TokenType.VAR_REF, sb.toString(), startLine, startColumn);
    }
    
    private Token readIdentifier() throws IOException {
        StringBuilder sb = new StringBuilder();
        int startLine = line;
        int startColumn = column;
        
        while (currentChar != -1 && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            sb.append((char) currentChar);
            currentChar = reader.read();
            column++;
        }
        
        String value = sb.toString();
        TokenType type = getKeywordType(value);
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }
        
        return new Token(type, value, startLine, startColumn);
    }
    
    private Token readNumber() throws IOException {
        StringBuilder sb = new StringBuilder();
        int startLine = line;
        int startColumn = column;
        
        while (currentChar != -1 && Character.isDigit(currentChar)) {
            sb.append((char) currentChar);
            currentChar = reader.read();
            column++;
        }
        
        return new Token(TokenType.NUMBER, sb.toString(), startLine, startColumn);
    }
    
    private TokenType getSingleCharToken(int ch) {
        switch (ch) {
            case '=': return TokenType.EQUALS;
            case ';': return TokenType.SEMICOLON;
            case '{': return TokenType.LBRACE;
            case '}': return TokenType.RBRACE;
            case '(': return TokenType.LPAREN;
            case ')': return TokenType.RPAREN;
            default: return null;
        }
    }
    
    private TokenType getKeywordType(String value) {
        switch (value) {
            case "config": return TokenType.CONFIG;
            case "base_url": return TokenType.BASE_URL;
            case "header": return TokenType.HEADER;
            case "let": return TokenType.LET;
            case "test": return TokenType.TEST;
            case "GET": return TokenType.GET;
            case "POST": return TokenType.POST;
            case "PUT": return TokenType.PUT;
            case "DELETE": return TokenType.DELETE;
            case "expect": return TokenType.EXPECT;
            case "status": return TokenType.STATUS;
            case "body": return TokenType.BODY;
            case "contains": return TokenType.CONTAINS;
            default: return null;
        }
    }
}

enum TokenType {
    CONFIG, BASE_URL, HEADER, LET, TEST, GET, POST, PUT, DELETE,
    EXPECT, STATUS, BODY, CONTAINS,
    IDENTIFIER, STRING, NUMBER, VAR_REF,
    EQUALS, SEMICOLON, LBRACE, RBRACE, LPAREN, RPAREN,
    EOF
}

class Token {
    public final TokenType type;
    public final String value;
    public final int line;
    public final int column;
    
    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public String toString() {
        return type + "(" + value + ")";
    }
}