import ast.*;
import java.io.*;
import java.util.*;

public class TestLangParser {
    private StandaloneLexer lexer;
    private Token currentToken;
    
    public TestLangParser(StandaloneLexer lexer) {
        this.lexer = lexer;
    }
    
    public TestFile parse() throws Exception {
        nextToken();
        return parseTestFile();
    }
    
    private void nextToken() throws IOException {
        currentToken = lexer.nextToken();
    }
    
    private void expect(TokenType expected) throws Exception {
        if (currentToken.type != expected) {
            throw new Exception("Expected " + expected + " but found " + currentToken.type + " (" + currentToken.value + ") at line " + currentToken.line + ", column " + currentToken.column);
        }
        nextToken();
    }
    
    private TestFile parseTestFile() throws Exception {
        ConfigBlock config = null;
        List<Variable> variables = new ArrayList<>();
        List<TestBlock> testBlocks = new ArrayList<>();
        
        if (currentToken.type == TokenType.CONFIG) {
            config = parseConfigBlock();
        }
        
        while (currentToken.type == TokenType.LET) {
            variables.add(parseVariable());
        }
        
        while (currentToken.type == TokenType.TEST) {
            testBlocks.add(parseTestBlock());
        }
        
        return new TestFile(config, variables, testBlocks);
    }
    
    private ConfigBlock parseConfigBlock() throws Exception {
        expect(TokenType.CONFIG);
        expect(TokenType.LBRACE);
        
        ConfigBlock config = new ConfigBlock();
        
        while (currentToken.type != TokenType.RBRACE) {
            if (currentToken.type == TokenType.BASE_URL) {
                expect(TokenType.BASE_URL);
                expect(TokenType.EQUALS);
                if (currentToken.type == TokenType.STRING) {
                    config.baseUrl = currentToken.value;
                    nextToken();
                }
                expect(TokenType.SEMICOLON);
            } else if (currentToken.type == TokenType.HEADER) {
                expect(TokenType.HEADER);
                if (currentToken.type == TokenType.STRING) {
                    String key = currentToken.value;
                    nextToken();
                    expect(TokenType.EQUALS);
                    if (currentToken.type == TokenType.STRING) {
                        String value = currentToken.value;
                        nextToken();
                        config.headers.put(key, value);
                    }
                }
                expect(TokenType.SEMICOLON);
            } else {
                nextToken();
            }
        }
        
        expect(TokenType.RBRACE);
        return config;
    }
    
    private Variable parseVariable() throws Exception {
        expect(TokenType.LET);
        String name = currentToken.value;
        expect(TokenType.IDENTIFIER);
        expect(TokenType.EQUALS);
        String value = currentToken.value;
        if (currentToken.type == TokenType.STRING) {
            nextToken();
        } else if (currentToken.type == TokenType.NUMBER) {
            nextToken();
        } else {
            throw new Exception("Expected string or number for variable value");
        }
        expect(TokenType.SEMICOLON);
        return new Variable(name, value);
    }
    
    private TestBlock parseTestBlock() throws Exception {
        expect(TokenType.TEST);
        String name = currentToken.value;
        expect(TokenType.IDENTIFIER);
        expect(TokenType.LBRACE);
        
        List<Statement> statements = new ArrayList<>();
        while (currentToken.type != TokenType.RBRACE && currentToken.type != TokenType.EOF) {
            statements.add(parseStatement());
        }
        
        expect(TokenType.RBRACE);
        return new TestBlock(name, statements);
    }
    
    private Statement parseStatement() throws Exception {
        HttpRequest request = parseHttpRequest();
        
        List<Assertion> assertions = new ArrayList<>();
        while (currentToken.type == TokenType.EXPECT) {
            assertions.add(parseAssertion());
        }
        
        if (assertions.isEmpty()) {
            return request;
        } else {
            return new RequestWithAssertions(request, assertions);
        }
    }
    
    private HttpRequest parseHttpRequest() throws Exception {
        String method;
        String path;
        List<Header> headers = new ArrayList<>();
        String body = null;
        
        if (currentToken.type == TokenType.GET) {
            method = "GET";
            nextToken();
        } else if (currentToken.type == TokenType.POST) {
            method = "POST";
            nextToken();
        } else if (currentToken.type == TokenType.PUT) {
            method = "PUT";
            nextToken();
        } else if (currentToken.type == TokenType.DELETE) {
            method = "DELETE";
            nextToken();
        } else {
            throw new Exception("Expected HTTP method");
        }
        
        path = currentToken.value;
        expect(TokenType.STRING);
        
        if (currentToken.type == TokenType.LBRACE) {
            nextToken();
            while (currentToken.type != TokenType.RBRACE) {
                if (currentToken.type == TokenType.HEADER) {
                    nextToken();
                    String key = currentToken.value;
                    expect(TokenType.STRING);
                    expect(TokenType.EQUALS);
                    String value = currentToken.value;
                    expect(TokenType.STRING);
                    expect(TokenType.SEMICOLON);
                    headers.add(new Header(key, value));
                } else if (currentToken.type == TokenType.BODY) {
                    nextToken();
                    expect(TokenType.EQUALS);
                    body = currentToken.value;
                    expect(TokenType.STRING);
                    expect(TokenType.SEMICOLON);
                } else {
                    nextToken();
                }
            }
            nextToken();
        }
        
        if (currentToken.type == TokenType.SEMICOLON) {
            nextToken();
        }
        return new HttpRequest(method, path, headers, body);
    }
    
    private Assertion parseAssertion() throws Exception {
        expect(TokenType.EXPECT);
        
        if (currentToken.type == TokenType.STATUS) {
            nextToken();
            expect(TokenType.EQUALS);
            int status = Integer.parseInt(currentToken.value);
            expect(TokenType.NUMBER);
            expect(TokenType.SEMICOLON);
            return new StatusAssertion(status);
        } else if (currentToken.type == TokenType.HEADER) {
            nextToken();
            String headerName = currentToken.value;
            expect(TokenType.STRING);
            if (currentToken.type == TokenType.EQUALS) {
                nextToken();
                String value = currentToken.value;
                expect(TokenType.STRING);
                expect(TokenType.SEMICOLON);
                return new HeaderEqualsAssertion(headerName, value);
            } else if (currentToken.type == TokenType.CONTAINS) {
                nextToken();
                String value = currentToken.value;
                expect(TokenType.STRING);
                expect(TokenType.SEMICOLON);
                return new HeaderContainsAssertion(headerName, value);
            } else {
                throw new Exception("Expected = or contains after header name");
            }
        } else if (currentToken.type == TokenType.BODY) {
            nextToken();
            expect(TokenType.CONTAINS);
            String value = currentToken.value;
            expect(TokenType.STRING);
            expect(TokenType.SEMICOLON);
            return new BodyContainsAssertion(value);
        } else {
            throw new Exception("Expected status, header, or body after expect");
        }
    }
}

