# TestLang++: A Domain Specific Language for HTTP API Testing

## 📋 Project Overview

TestLang++ is a sophisticated Domain Specific Language (DSL) compiler that transforms high-level testing specifications into executable JUnit 5 tests for HTTP API validation. Developed as part of SE2062 - TestLang++ (Java) - Backend API Testing DSL, this project bridges the gap between human-readable test definitions and robust, automated API testing.

## 🎯 The Problem

Traditional API testing often involves:

- Verbose, repetitive test code
- Complex setup and configuration
- Manual assertion writing
- Difficult maintenance as APIs evolve

## 💡 The Solution

TestLang++ provides:

- Concise, readable syntax for defining API tests
- Automatic compilation to JUnit 5 with Java HttpClient
- Reusable configurations and variables
- Comprehensive assertion capabilities
- Seamless integration with existing Java ecosystems

## 🏗️ Architectural Design

### Compiler Pipeline

```
DSL Input (.test) 
    → [JFlex Lexer] 
    → [CUP Parser] 
    → [AST Generation] 
    → [Code Generator] 
    → JUnit 5 Output (.java)
```

### Core Components

| Component | Technology | Purpose |
|-----------|------------|---------|
| Lexer | JFlex | Tokenizes input stream into syntactic elements |
| Parser | CUP | Constructs parse tree from token stream |
| AST | Custom Java Classes | Represents program structure semantically |
| CodeGen | Java Template Engine | Generates executable JUnit 5 tests |

### Project Structure

```
TestLang++/
├── src/
│   ├── ast/                 # Abstract Syntax Tree classes
│   │   ├── Program.java     # Root AST node
│   │   ├── Config.java      # Configuration block
│   │   ├── Test.java        # Test case definition
│   │   └── ...             # Other AST components
│   ├── TestLang.flex        # JFlex lexer specification
│   ├── TestLang.cup         # CUP parser grammar
│   ├── CodeGenerator.java   # JUnit 5 code generation
│   └── Main.java           # Compiler entry point
├── examples/
│   └── example.test        # DSL input examples
├── lib/                    # Dependencies (JFlex, CUP, JUnit)
├── backend/               # Spring Boot test server
└── GeneratedTests.java    # Generated output
```

## 📝 DSL Syntax Reference

### Configuration Block

```javascript
config {
    base_url = "http://localhost:8080";
    header "Content-Type" = "application/json";
    header "Authorization" = "Bearer token123";
}
```

### Variable Declarations

```javascript
let username = "testuser";
let userId = 42;
let endpoint = "/api/users";
```

### Test Definitions

```javascript
test UserLoginTest {
    POST "/api/login" {
        body = "{ \"username\": \"$username\", \"password\": \"secret\" }";
    };
    expect status = 200;
    expect header "Content-Type" contains "json";
    expect body contains "\"token\":";
}
```

### HTTP Methods & Assertions

- **Requests**: `GET|POST|PUT|DELETE "path" { body = "json"; }`
- **Status Assertions**: `expect status = 200;`
- **Header Assertions**: `expect header "Key" contains "value";`
- **Body Assertions**: `expect body contains "text";`

## 🚀 Quick Start Guide

### Prerequisites

- Java 8 or higher
- Spring Boot (for test backend)
- Basic terminal/command line knowledge

### Automated Build & Execution

Make the build script executable:

```bash
chmod +x build-all.sh
```

Run the complete pipeline:

```bash
./build-all.sh
```

### Manual Build Process

For educational purposes or debugging, here's the step-by-step compilation:

**Generate Lexer:**

```bash
java -jar lib/jflex-full-1.9.1.jar src/TestLang.flex
```

**Generate Parser:**

```bash
java -jar lib/java-cup-11b.jar -destdir src -parser TestLangParser -symbols sym src/TestLang.cup
```

**Compile AST Classes:**

```bash
javac -cp "lib/java-cup-11b-runtime.jar:src" -d build src/ast/*.java
```

**Compile Code Generator:**

```bash
javac -cp "lib/java-cup-11b-runtime.jar:src" -d build src/CodeGenerator.java
```

**Compile Parser Components:**

```bash
javac -cp "lib/java-cup-11b-runtime.jar:src" -d build src/TestLangScanner.java src/TestLangParser.java src/sym.java
```

**Compile Main Class:**

```bash
javac -cp "lib/java-cup-11b-runtime.jar:build" -d build src/Main.java
```

**Run Compiler:**

```bash
java -cp "lib/java-cup-11b-runtime.jar:build" Main examples/example.test
```

### Testing the Generated Code

**Start the Test Backend:**

```bash
cd backend
./mvnw spring-boot:run
```

**Compile Generated Tests:**

```bash
javac -cp "lib/junit-platform-console-standalone-1.9.3.jar" GeneratedTests.java
```

**Execute Tests:**

```bash
java -jar lib/junit-platform-console-standalone-1.9.3.jar --class-path . --scan-class-path
```

## 🔍 Example Workflow

### Input DSL (example.test)

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

### Generated JUnit 5 Output

The compiler produces comprehensive JUnit 5 tests that:

- Handle HTTP connections with Java HttpClient
- Manage headers and authentication
- Execute assertions on status codes, headers, and response bodies
- Provide detailed error reporting for test failures

## 🎓 Educational Value

This project demonstrates several important software engineering concepts:

- **Compiler Design**: Lexical analysis, parsing, and code generation
- **Domain Specific Languages**: Creating tailored languages for specific problem domains
- **Abstract Syntax Trees**: Representing program structure
- **API Testing Patterns**: Modern REST API validation techniques
- **Build Automation**: Managing complex compilation pipelines

## 🔧 Technical Details

### Dependencies

- **JFlex 1.9.1**: Lexical analyzer generator
- **CUP 11b**: Parser generator for LALR grammars
- **JUnit 5**: Modern testing framework
- **Java HttpClient**: HTTP client API (Java 11+)

### Language Features

- **Strong Typing**: Compile-time validation of test structure
- **Variable Interpolation**: Dynamic value substitution in requests
- **Header Management**: Automatic header propagation across tests
- **Comprehensive Assertions**: Multi-faceted response validation

## 📈 Future Enhancements

Potential extensions to TestLang++:

- Support for GraphQL APIs
- Database state validation
- Performance testing directives
- OAuth 2.0 flow testing
- OpenAPI specification integration
- Test data generation and factories

## 🤝 Contributing

This project was developed as part of an academic assignment. For questions or contributions, please refer to the course guidelines and academic integrity policies.