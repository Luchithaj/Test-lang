import ast.*;
import java.util.*;
import java.io.*;

public class CodeGenerator {
    
    public static void generate(TestFile testFile, ConfigBlock config, List<Variable> variables, List<TestBlock> testBlocks) {
        try {
            PrintWriter writer = new PrintWriter("GeneratedTests.java");
            
            // Generate class header
            writer.println("import org.junit.jupiter.api.*;");
            writer.println("import static org.junit.jupiter.api.Assertions.*;");
            writer.println("import java.net.http.*;");
            writer.println("import java.net.*;");
            writer.println("import java.time.Duration;");
            writer.println("import java.nio.charset.StandardCharsets;");
            writer.println("import java.util.*;");
            writer.println();
            writer.println("public class GeneratedTests {");
            
            // Generate constants
            String baseUrl = (config != null && config.baseUrl != null) ? config.baseUrl : "http://localhost:8080";
            writer.println("  static String BASE = \"" + baseUrl + "\";");
            writer.println("  static Map<String,String> DEFAULT_HEADERS = new HashMap<>();");
            writer.println("  static HttpClient client;");
            writer.println();
            
            // Generate setup method
            writer.println("  @BeforeAll");
            writer.println("  static void setup() {");
            writer.println("    client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();");
            if (config != null && config.headers != null) {
                for (Map.Entry<String, String> entry : config.headers.entrySet()) {
                    writer.println("    DEFAULT_HEADERS.put(\"" + entry.getKey() + "\",\"" + entry.getValue() + "\");");
                }
            }
            writer.println("  }");
            writer.println();
            
            // Generate test methods
            for (TestBlock testBlock : testBlocks) {
                generateTestMethod(writer, testBlock, variables);
            }
            
            writer.println("}");
            writer.close();
            
            System.out.println("Generated GeneratedTests.java successfully!");
            
        } catch (IOException e) {
            System.err.println("Error generating code: " + e.getMessage());
        }
    }
    
    private static void generateTestMethod(PrintWriter writer, TestBlock testBlock, List<Variable> variables) {
        writer.println("  @Test");
        writer.println("  void test_" + testBlock.name + "() throws Exception {");
        
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
        
        writer.println("  }");
        writer.println();
    }
    
    private static void generateHttpRequest(PrintWriter writer, HttpRequest request, List<Variable> variables) {
        // Build the URL
        String url = buildUrl(request.path, variables);
        
        writer.println("    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(\"" + url + "\"))");
        writer.println("      .timeout(Duration.ofSeconds(10))");
        
        // Set HTTP method and body
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
        
        // Add default headers
        writer.println("    for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());");
        
        // Add request-specific headers
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
                writer.println("    assertEquals(" + statusAssert.expectedStatus + ", resp.statusCode());");
            } else if (assertion instanceof HeaderEqualsAssertion) {
                HeaderEqualsAssertion headerAssert = (HeaderEqualsAssertion) assertion;
                writer.println("    assertEquals(\"" + escapeString(headerAssert.expectedValue) + "\", resp.headers().firstValue(\"" + headerAssert.headerName + "\").orElse(\"\"));");
            } else if (assertion instanceof HeaderContainsAssertion) {
                HeaderContainsAssertion headerAssert = (HeaderContainsAssertion) assertion;
                writer.println("    assertTrue(resp.headers().firstValue(\"" + headerAssert.headerName + "\").orElse(\"\").contains(\"" + escapeString(headerAssert.expectedSubstring) + "\"));");
            } else if (assertion instanceof BodyContainsAssertion) {
                BodyContainsAssertion bodyAssert = (BodyContainsAssertion) assertion;
                writer.println("    assertTrue(resp.body().contains(\"" + escapeString(bodyAssert.expectedSubstring) + "\"));");
            }
        }
    }
    
    private static String buildUrl(String path, List<Variable> variables) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return substituteVariables(path, variables);
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
}
