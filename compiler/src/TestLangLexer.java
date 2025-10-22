import java.io.*;
import java.util.*;

public class TestLangLexer {
    private Reader reader;
    private int currentChar;
    private int line = 1;
    private int column = 1;
    private Token currentToken;
    
    public TestLangLexer(Reader reader) {
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
            
            if (currentChar == '/') {
                int next = peek();
                if (next == '/') {
                    skipLineComment();
                    continue;
                }
            }
            
            // Keywords
            if (Character.isLetter(currentChar)) {
                StringBuilder sb = new StringBuilder();
                while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
                    sb.append((char) currentChar);
                    currentChar = reader.read();
                    column++;
                }
                
                String word = sb.toString();
                switch (word) {
                    case "config": return new Token(TokenType.CONFIG, word, line, column - word.length());
                    case "base_url": return new Token(TokenType.BASE_URL, word, line, column - word.length());
                    case "header": return new Token(TokenType.HEADER, word, line, column - word.length());
                    case "let": return new Token(TokenType.LET, word, line, column - word.length());
                    case "test": return new Token(TokenType.TEST, word, line, column - word.length());
                    case "GET": return new Token(TokenType.GET, word, line, column - word.length());
                    case "POST": return new Token(TokenType.POST, word, line, column - word.length());
                    case "PUT": return new Token(TokenType.PUT, word, line, column - word.length());
                    case "DELETE": return new Token(TokenType.DELETE, word, line, column - word.length());
                    case "expect": return new Token(TokenType.EXPECT, word, line, column - word.length());
                    case "status": return new Token(TokenType.STATUS, word, line, column - word.length());
                    case "body": return new Token(TokenType.BODY, word, line, column - word.length());
                    case "contains": return new Token(TokenType.CONTAINS, word, line, column - word.length());
                    default: return new Token(TokenType.IDENTIFIER, word, line, column - word.length());
                }
            }
            
            // Numbers
            if (Character.isDigit(currentChar)) {
                StringBuilder sb = new StringBuilder();
                while (Character.isDigit(currentChar)) {
                    sb.append((char) currentChar);
                    currentChar = reader.read();
                    column++;
                }
                return new Token(TokenType.NUMBER, sb.toString(), line, column - sb.length());
            }
            
            // Strings
            if (currentChar == '"') {
                StringBuilder sb = new StringBuilder();
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
                return new Token(TokenType.STRING, sb.toString(), line, column - sb.length() - 2);
            }
            
            // Variable references
            if (currentChar == '$') {
                currentChar = reader.read();
                column++;
                StringBuilder sb = new StringBuilder();
                while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
                    sb.append((char) currentChar);
                    currentChar = reader.read();
                    column++;
                }
                return new Token(TokenType.VAR_REF, sb.toString(), line, column - sb.length() - 1);
            }
            
            // Operators and punctuation
            switch (currentChar) {
                case '=':
                    currentChar = reader.read();
                    column++;
                    return new Token(TokenType.EQUALS, "=", line, column - 1);
                case ';':
                    currentChar = reader.read();
                    column++;
                    return new Token(TokenType.SEMICOLON, ";", line, column - 1);
                case '{':
                    currentChar = reader.read();
                    column++;
                    return new Token(TokenType.LBRACE, "{", line, column - 1);
                case '}':
                    currentChar = reader.read();
                    column++;
                    return new Token(TokenType.RBRACE, "}", line, column - 1);
                case '(':
                    currentChar = reader.read();
                    column++;
                    return new Token(TokenType.LPAREN, "(", line, column - 1);
                case ')':
                    currentChar = reader.read();
                    column++;
                    return new Token(TokenType.RPAREN, ")", line, column - 1);
                default:
                    System.err.println("Unknown character: " + (char) currentChar + " at line " + line + ", column " + column);
                    currentChar = reader.read();
                    column++;
            }
        }
        
        return new Token(TokenType.EOF, "", line, column);
    }
    
    private int peek() throws IOException {
        return currentChar;
    }
    
    private void skipLineComment() throws IOException {
        while (currentChar != -1 && currentChar != '\n') {
            currentChar = reader.read();
            column++;
        }
    }
}
