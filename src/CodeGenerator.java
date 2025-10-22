import ast.*;
import java.io.*;
import java.util.*;

public class CodeGenerator {
    
    public static void generate(TestFile testFile) {
        try {
            PrintWriter writer = new PrintWriter("GeneratedTests.java");
            
            writer.println("import java.net.http.*;");
            writer.println("import java.net.*;");
            writer.println("import java.time.Duration;");
            writer.println("import java.nio.charset.StandardCharsets;");
            writer.println("import java.util.*;");
            writer.println();
            writer.println("public class GeneratedTests {");
            
            // Generate constants
            String baseUrl = (testFile.config != null && testFile.config.baseUrl != null) ? testFile.config.baseUrl : "http://localhost:8080";
            writer.println("  static String BASE = \"" + baseUrl + "\";");
            writer.println("  static Map<String,String> DEFAULT_HEADERS = new HashMap<>();");
            writer.println("  static HttpClient client;");
            writer.println();
            
            // Generate main method
            writer.println("  public static void main(String[] args) {");
            writer.println("    System.out.println(\"TestLang++ Generated Tests\");");
            writer.println("    System.out.println(\"========================\");");
            writer.println("    setup();");
            writer.println("    ");
            writer.println("    try {");
            
            // Generate test method calls
            for (TestBlock testBlock : testFile.testBlocks) {
                writer.println("      test_" + testBlock.name + "();");
            }
            
            writer.println("      System.out.println(\"\\nAll tests passed!\");");
            writer.println("    } catch (Exception e) {");
            writer.println("      System.err.println(\"Test failed: \" + e.getMessage());");
            writer.println("      e.printStackTrace();");
            writer.println("    }");
            writer.println("  }");
            writer.println();
            
            // Generate setup method
            writer.println("  static void setup() {");
            writer.println("    client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();");
            if (testFile.config != null && testFile.config.headers != null) {
                for (Map.Entry<String, String> entry : testFile.config.headers.entrySet()) {
                    writer.println("    DEFAULT_HEADERS.put(\"" + entry.getKey() + "\",\"" + entry.getValue() + "\");");
                }
            }
            writer.println("  }");
            writer.println();
            
            // Generate test methods
            for (TestBlock testBlock : testFile.testBlocks) {
                generateTestMethod(writer, testBlock, testFile.variables);
            }
            
            // Generate assertion methods
            writer.println("  static void assertEqual(int expected, int actual, String message) {");
            writer.println("    if (expected != actual) {");
            writer.println("      throw new AssertionError(message + \", Expected: \" + expected + \", Actual: \" + actual);");
            writer.println("    }");
            writer.println("  }");
            writer.println();
            writer.println("  static void assertEqual(String expected, String actual, String message) {");
            writer.println("    if (!expected.equals(actual)) {");
            writer.println("      throw new AssertionError(message + \", Expected: \" + expected + \", Actual: \" + actual);");
            writer.println("    }");
            writer.println("  }");
            writer.println();
            writer.println("  static void assertTrue(boolean condition, String message) {");
            writer.println("    if (!condition) {");
            writer.println("      throw new AssertionError(message);");
            writer.println("    }");
            writer.println("  }");
            writer.println("}");
            writer.close();
            
            System.out.println("Generated GeneratedTests.java successfully!");
            
        } catch (IOException e) {
            System.err.println("Error generating code: " + e.getMessage());
        }
    }
    
    private static void generateTestMethod(PrintWriter writer, TestBlock testBlock, List<Variable> variables) {
        writer.println("  static void test_" + testBlock.name + "() throws Exception {");
        writer.println("    System.out.println(\"Running test_" + testBlock.name + "...\");");
        
        // Process statements in the test block
        for (Statement stmt : testBlock.statements) {
            if (stmt instanceof HttpRequest) {
                generateHttpRequest(writer, (HttpRequest) stmt, variables);
            } else if (stmt instanceof RequestWithAssertions) {
                RequestWithAssertions reqWithAsserts = (RequestWithAssertions) stmt;
                generateHttpRequest(writer, reqWithAsserts.request, variables);
                generateAssertions(writer, reqWithAsserts.assertions);
            }
        }
        
        writer.println("    System.out.println(\"  ✓ test_" + testBlock.name + " passed\");");
        writer.println("  }");
        writer.println();
    }
    
    private static void generateHttpRequest(PrintWriter writer, HttpRequest request, List<Variable> variables) {
        String url = buildUrl(request.path, variables);
        
        writer.println("    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(" + url + "))");
        writer.println("      .timeout(Duration.ofSeconds(10))");
        
        if ("GET".equals(request.method)) {
            writer.println("      .GET();");
        } else if ("DELETE".equals(request.method)) {
            writer.println("      .DELETE();");
        } else if ("POST".equals(request.method)) {
            String body = substituteVariables(request.body, variables);
            writer.println("      .POST(HttpRequest.BodyPublishers.ofString(\"" + escapeString(body) + "\"));");
        } else if ("PUT".equals(request.method)) {
            String body = substituteVariables(request.body, variables);
            writer.println("      .PUT(HttpRequest.BodyPublishers.ofString(\"" + escapeString(body) + "\"));");
        }
        
        writer.println("    for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());");
        
        for (Header header : request.headers) {
            String value = substituteVariables(header.value, variables);
            writer.println("    b.header(\"" + header.name + "\", \"" + escapeString(value) + "\");");
        }
        
        writer.println("    HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));");
        writer.println();
    }
    
    private static void generateAssertions(PrintWriter writer, List<Assertion> assertions) {
        for (Assertion assertion : assertions) {
            if (assertion instanceof StatusAssertion) {
                StatusAssertion statusAssert = (StatusAssertion) assertion;
                writer.println("    assertEqual(" + statusAssert.expectedStatus + ", resp.statusCode(), \"Status code should be " + statusAssert.expectedStatus + "\");");
            } else if (assertion instanceof HeaderEqualsAssertion) {
                HeaderEqualsAssertion headerAssert = (HeaderEqualsAssertion) assertion;
                writer.println("    assertEqual(\"" + escapeString(headerAssert.expectedValue) + "\", resp.headers().firstValue(\"" + headerAssert.headerName + "\").orElse(\"\"), \"Header " + headerAssert.headerName + " should equal " + headerAssert.expectedValue + "\");");
            } else if (assertion instanceof HeaderContainsAssertion) {
                HeaderContainsAssertion headerAssert = (HeaderContainsAssertion) assertion;
                writer.println("    assertTrue(resp.headers().firstValue(\"" + headerAssert.headerName + "\").orElse(\"\").contains(\"" + escapeString(headerAssert.expectedSubstring) + "\"), \"Header " + headerAssert.headerName + " should contain " + escapeForMessage(headerAssert.expectedSubstring) + "\");");
            } else if (assertion instanceof BodyContainsAssertion) {
                BodyContainsAssertion bodyAssert = (BodyContainsAssertion) assertion;
                writer.println("    assertTrue(resp.body().contains(\"" + escapeString(bodyAssert.expectedSubstring) + "\"), \"Body should contain " + escapeForMessage(bodyAssert.expectedSubstring) + "\");");
            }
        }
    }
    
    private static String buildUrl(String path, List<Variable> variables) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return "\"" + substituteVariables(path, variables) + "\"";
        } else {
            return "BASE + \"" + substituteVariables(path, variables) + "\"";
        }
    }
    
    private static String substituteVariables(String text, List<Variable> variables) {
        if (text == null) return null;
        
        String result = text;
        if (variables != null) {
            for (Variable var : variables) {
                result = result.replace("$" + var.name, var.value);
            }
        }
        return result;
    }
    
    private static String escapeString(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private static String escapeForMessage(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"");
    }
}
