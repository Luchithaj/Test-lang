# TestLang++ - HTTP API Testing DSL

A simple DSL for writing HTTP API tests that compiles to JUnit test classes.

Overview

TestLang++ lets you write HTTP API tests in a readable DSL format and automatically generates JUnit test classes using Java HttpClient.

Quick Start

### Build the compiler
```bash
./build.sh
```

### Start the test backend
```bash
./run-backend.sh
```

### Compile a test file
```bash
java -cp build Main example.test
```

### Run the generated tests
```bash
java GeneratedTests
```

## DSL Syntax

### Basic structure
```testlang
config {
  base_url = "http://localhost:8080";
  header "Content-Type" = "application/json";
}

let user = "admin";
let id = 42;

test Login {
  POST "/api/login" {
    body = "{ \"username\": \"$user\", \"password\": \"1234\" }";
  }
  expect status = 200;
  expect header "Content-Type" contains "json";
  expect body contains "\"token\":";
}
```

### Features
- Config blocks for base URL and default headers
- Variable declarations with `let`
- HTTP methods: GET, POST, PUT, DELETE
- Request bodies for POST/PUT
- Headers for requests
- Assertions for status, headers, and body content
- Variable substitution with `$variable`

### Assertions
- `expect status = 200;` - Check HTTP status
- `expect header "Name" = "Value";` - Check exact header
- `expect header "Name" contains "substring";` - Check header contains
- `expect body contains "substring";` - Check body contains

## Example

See `example.test` for a complete example with three test cases:
1. Login test with POST request and JSON body
2. Get user test with GET request and variable substitution
3. Update user test with PUT request and multiple assertions

## Generated Code

The compiler generates a JUnit class with:
- HttpClient setup
- Individual test methods for each test block
- HTTP request building with headers and bodies
- Assertions for status codes, headers, and body content
- Variable substitution in the generated code

## Backend

The project includes a Spring Boot backend for testing:
- POST /api/login - Authentication endpoint
- GET /api/users/{id} - Get user by ID
- PUT /api/users/{id} - Update user

## Building

1. Make sure Java 11+ is installed
2. Run `./build.sh` to compile
3. The compiler uses a hand-written lexer and simple parser

## Testing

Run the complete pipeline:
1. Start backend: `./run-backend.sh`
2. Compile DSL: `java -cp build Main example.test`
3. Run tests: `java GeneratedTests`

## Files

- `src/StandaloneLexer.java` - Lexical analyzer
- `src/SimpleParser.java` - Parser and code generator
- `src/Main.java` - Main compiler class
- `backend/` - Spring Boot test server
- `example.test` - Sample DSL file
- `GeneratedTests.java` - Generated JUnit code