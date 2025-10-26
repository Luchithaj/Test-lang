# TestLang++: HTTP API Testing DSL

A domain-specific language for writing HTTP API tests that compiles to JUnit 5 test cases.

## What it does

TestLang++ lets you write API tests in a simple, readable syntax instead of verbose Java code. You define your tests in `.test` files, and the compiler generates executable JUnit 5 tests that use Java's HttpClient.

## How it works

The compiler uses JFlex for lexical analysis and CUP for parsing. It builds an abstract syntax tree (AST) from your `.test` file, then generates JUnit 5 code.

```
.test file → JFlex → CUP → AST → Code Generator → GeneratedTests.java
```

## Project structure

```
├── src/
│   ├── ast/              # AST classes
│   ├── TestLang.flex     # Lexer rules
│   ├── TestLang.cup      # Parser grammar
│   ├── CodeGenerator.java
│   └── Main.java
├── examples/             # Sample .test files
├── lib/                  # JFlex, CUP, JUnit jars
├── backend/              # Test server
└── GeneratedTests.java   # Output
```

## Syntax

### Configuration
```javascript
config {
    base_url = "http://localhost:8080";
    header "Content-Type" = "application/json";
}
```

### Variables
```javascript
let user = "admin";
let id = 42;
```

### Tests
```javascript
test Login {
    POST "/api/login" {
        body = "{ \"username\": \"$user\", \"password\": \"1234\" }";
    };
    expect status = 200;
    expect header "Content-Type" contains "json";
    expect body contains "\"token\":";
}
```

### Supported operations
- HTTP methods: `GET`, `POST`, `PUT`, `DELETE`
- Assertions: `expect status = 200`, `expect header "key" contains "value"`, `expect body contains "text"`

## Getting started

You'll need Java 8+ and three terminal windows.

**Terminal 1: Build the compiler**
```bash
chmod +x build-all.sh
./build-all.sh
```

**Terminal 2: Start the test server**
```bash
cd backend
mvn spring-boot:run
```

**Terminal 3: Run the tests**
```bash
java -jar lib/junit-platform-console-standalone-1.9.3.jar --class-path . --scan-class-path
```

## Manual build (to see each step)

```bash
# Generate lexer and parser
java -jar lib/jflex-full-1.9.1.jar src/TestLang.flex
java -jar lib/java-cup-11b.jar -destdir src -parser TestLangParser -symbols sym src/TestLang.cup

# Compile everything
javac -cp "lib/java-cup-11b-runtime.jar:src" -d build src/ast/*.java
javac -cp "lib/java-cup-11b-runtime.jar:src" -d build src/CodeGenerator.java
javac -cp "lib/java-cup-11b-runtime.jar:src" -d build src/TestLangScanner.java src/TestLangParser.java src/sym.java
javac -cp "lib/java-cup-11b-runtime.jar:build" -d build src/Main.java

# Run compiler on a test file (defaults to example.test)
java -cp "lib/java-cup-11b-runtime.jar:build" Main examples/example.test
# Or run the second example:
java -cp "lib/java-cup-11b-runtime.jar:build" Main examples/example2.test

# Compile and run generated tests
javac -cp "lib/junit-platform-console-standalone-1.9.3.jar:." GeneratedTests.java
java -jar lib/junit-platform-console-standalone-1.9.3.jar --class-path . --scan-class-path
```

## Example

Here's what a `.test` file looks like:

```javascript
config {
    base_url = "http://localhost:8080";
    header "Content-Type" = "application/json";
}

let user = "admin";
let id = 42;

test Login {
    POST "/api/login" {
        body = "{ \"username\": \"$user\", \"password\": \"1234\" }";
    };
    expect status = 200;
    expect header "Content-Type" contains "json";
    expect body contains "\"token\":";
}

test GetUser {
    GET "/api/users/$id";
    expect status = 200;
    expect body contains "\"id\":42";
}
```

The compiler generates JUnit 5 tests that use Java's HttpClient to make the actual HTTP requests and verify the responses.

## Dependencies

- JFlex 1.9.1 (lexer generator)
- CUP 11b (parser generator) 
- JUnit 5 (testing framework)
- Java HttpClient (HTTP client)



