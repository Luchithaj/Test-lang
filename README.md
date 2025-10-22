# TestLang++ - HTTP API Testing DSL

A simple DSL I built for testing HTTP APIs. It compiles to JUnit 5 tests so you can run them normally.

## What's in here

```
Test-lang/
├── compiler/                    # The main compiler code
│   ├── src/                    # Java source files
│   │   ├── ast/               # AST classes for parsing
│   │   ├── CodeGenerator.java # Generates JUnit code
│   │   ├── Main.java          # Entry point
│   │   ├── StandaloneLexer.java # Tokenizer
│   │   └── TestLangParser.java # Parser
│   ├── lib/                   # JAR dependencies
│   ├── scripts/               # Build scripts
│   └── examples/              # Example .test files
├── backend/                   # Spring Boot app for testing
└── build.sh                   # Main build script
```

## Quick start

Build everything:
```bash
./build.sh
```

Start the backend (in one terminal):
```bash
./run-backend.sh
```

Run tests (in another terminal):
```bash
cd compiler && ./scripts/test-complete.sh
```

## How to use manually

1. Build the compiler:
```bash
cd compiler
./scripts/build.sh
```

2. Generate tests from a .test file:
```bash
java -cp build Main examples/example.test
```

3. Compile and run the generated tests:
```bash
javac -cp "lib/junit-platform-console-standalone-1.9.3.jar" GeneratedTests.java
java -jar lib/junit-platform-console-standalone-1.9.3.jar --class-path . --select-class GeneratedTests
```

## DSL syntax

The .test files look like this:

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

test GetUser {
  GET "/api/users/$id";
  expect status = 200;
  expect body contains "\"id\":42";
}
```

## What it does

- Parses .test files into an AST
- Substitutes variables like `$id` with actual values
- Generates JUnit 5 test classes
- Supports GET, POST, PUT, DELETE requests
- Has assertions for status codes, headers, and body content
- Works with the included Spring Boot backend

## Requirements

- Java 11 or higher
- Maven (for the backend)

## Assignment stuff

This was built for SE2062 - TestLang++ (Java) - Backend API Testing DSL.

Features implemented:
- Custom lexer and parser (no JFlex/CUP, wrote my own)
- AST for representing parsed code
- Code generation to JUnit 5
- Variable substitution in URLs and request bodies
- HTTP client integration
- Spring Boot backend for testing

The generated tests show up in a nice JUnit tree view with green checkmarks and everything.