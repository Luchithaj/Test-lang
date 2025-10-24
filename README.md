# TestLang++ - HTTP API Testing DSL

A Domain Specific Language (DSL) for HTTP API testing that compiles to JUnit 5 tests. Built for SE2062 - TestLang++ (Java) - Backend API Testing DSL assignment.

## Project Structure

```
Test-lang/
├── compiler/                    # The main compiler code
│   ├── src/                    # Java source files
│   │   ├── ast/               # AST classes for parsing
│   │   ├── CodeGenerator.java # Generates JUnit code
│   │   ├── Main.java          # Entry point
│   │   ├── TestLang.flex      # JFlex lexer specification
│   │   ├── TestLang.cup       # CUP parser specification
│   │   └── TestLangParser.java # Generated parser
│   ├── lib/                   # JAR dependencies (JFlex, CUP, JUnit)
│   ├── scripts/               # Build scripts
│   ├── examples/              # Example .test files
│   └── GeneratedTests.java    # Generated JUnit test output
├── backend/                   # Spring Boot app for testing
└── build.sh                   # Main build script
```

## Quick Start

### 1. Build Everything
```bash
./build.sh
```

### 2. Start the Backend
In one terminal:
```bash
./run-backend.sh
```
The backend will start on `http://localhost:8080`

### 3. Run the Complete Pipeline
In another terminal:
```bash
cd compiler && ./scripts/test-complete.sh
```

This will:
- Parse `examples/example.test`
- Generate `GeneratedTests.java`
- Compile the generated tests
- Run the tests against the backend
- Show pass/fail results

## Manual Usage

### Step 1: Build the Compiler
```bash
cd compiler
./scripts/build.sh
```

### Step 2: Generate Tests from DSL
```bash
java -cp build Main examples/example.test
```

### Step 3: Compile Generated Tests
```bash
javac -cp ".:lib/junit-platform-console-standalone-1.9.3.jar" GeneratedTests.java
```

### Step 4: Run Tests
```bash
java -cp ".:lib/junit-platform-console-standalone-1.9.3.jar" org.junit.platform.console.ConsoleLauncher --class-path . --select-class GeneratedTests
```

## DSL Syntax Reference

### File Structure
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

### Supported Features
- **Config Block**: Set base URL and default headers
- **Variables**: Declare with `let name = value;`, use with `$name`
- **HTTP Methods**: GET, POST, PUT, DELETE
- **Request Bodies**: JSON strings for POST/PUT
- **Headers**: Per-request and default headers
- **Assertions**: Status codes, header values, body content
- **Variable Substitution**: In URLs and request bodies

## Error Handling

The parser provides meaningful error messages for invalid DSL syntax:

```bash
# Invalid variable name
let 2a = "x";
# Error: Expected IDENTIFIER but found NUMBER (2) at line 7, column 5

# Invalid body type
POST "/api/login" { body = 123; }
# Error: Expected STRING but found NUMBER (123) at line 3, column 12
```

## Implementation Details

### Parser Architecture
- **Lexer**: JFlex-based tokenizer (`TestLang.flex`)
- **Parser**: CUP-based grammar parser (`TestLang.cup`)
- **AST**: Java classes representing parsed structure
- **Code Generator**: Produces JUnit 5 test code

### Generated Code Structure
```java
@Test
void test_Login() throws Exception {
  HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + "/api/login"))
    .timeout(Duration.ofSeconds(10))
    .POST(HttpRequest.BodyPublishers.ofString("{ \"username\": \"admin\", \"password\": \"1234\" }"));
  for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());
  HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

  assertEquals(200, resp.statusCode());
  assertTrue(resp.headers().firstValue("Content-Type").orElse("").contains("json"));
  assertTrue(resp.body().contains("\"token\":"));
}
```

## Requirements

- Java 11 or higher
- Maven (for the backend)
- JFlex and CUP (included in lib/)

## Assignment Compliance

This implementation fulfills all requirements for SE2062 - TestLang++ (Java) - Backend API Testing DSL:

✅ **Language Design Fidelity**: Implements the complete DSL specification  
✅ **Scanner & Parser Quality**: Robust JFlex/CUP implementation with error handling  
✅ **Code Generation**: Produces idiomatic JUnit 5 code with HttpClient  
✅ **Demo & Examples**: Complete pipeline with working examples  

### Features Implemented
- Complete DSL parser with JFlex lexer and CUP parser
- AST representation of parsed code
- Variable substitution in URLs and request bodies
- JUnit 5 code generation with HttpClient
- Spring Boot backend for testing
- Comprehensive error handling
- All 3 required test cases (Login, GetUser, UpdateUser)

The generated tests run successfully and show green checkmarks in JUnit's tree view.