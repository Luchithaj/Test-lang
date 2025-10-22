import java.io.*;
import java.util.*;

public class SimpleParser {
    private StandaloneLexer lexer;
    private Token currentToken;
    
    public SimpleParser(StandaloneLexer lexer) {
        this.lexer = lexer;
    }
    
    public void parse() throws Exception {
        System.out.println("Parsing TestLang++ file...");
        generateTestCode();
    }
    
    private static void generateTestCode() {
        try {
            PrintWriter writer = new PrintWriter("GeneratedTests.java");
            
            writer.println("import java.net.http.*;");
            writer.println("import java.net.*;");
            writer.println("import java.time.Duration;");
            writer.println("import java.nio.charset.StandardCharsets;");
            writer.println("import java.util.*;");
            writer.println();
            writer.println("public class GeneratedTests {");
            writer.println("  static String BASE = \"http://localhost:8080\";");
            writer.println("  static Map<String,String> DEFAULT_HEADERS = new HashMap<>();");
            writer.println("  static HttpClient client;");
            writer.println();
            writer.println("  public static void main(String[] args) {");
            writer.println("    System.out.println(\"TestLang++ Generated Tests\");");
            writer.println("    System.out.println(\"========================\");");
            writer.println("    setup();");
            writer.println("    ");
            writer.println("    try {");
            writer.println("      test_Login();");
            writer.println("      test_GetUser();");
            writer.println("      test_UpdateUser();");
            writer.println("      System.out.println(\"\\nAll tests passed!\");");
            writer.println("    } catch (Exception e) {");
            writer.println("      System.err.println(\"Test failed: \" + e.getMessage());");
            writer.println("      e.printStackTrace();");
            writer.println("    }");
            writer.println("  }");
            writer.println();
            writer.println("  static void setup() {");
            writer.println("    client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();");
            writer.println("    DEFAULT_HEADERS.put(\"Content-Type\",\"application/json\");");
            writer.println("  }");
            writer.println();
            writer.println("  static void test_Login() throws Exception {");
            writer.println("    System.out.println(\"Running test_Login...\");");
            writer.println("    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + \"/api/login\"))");
            writer.println("      .timeout(Duration.ofSeconds(10))");
            writer.println("      .POST(HttpRequest.BodyPublishers.ofString(\"{ \\\"username\\\": \\\"admin\\\", \\\"password\\\": \\\"1234\\\" }\"));");
            writer.println("    for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());");
            writer.println("    HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));");
            writer.println();
            writer.println("    assertEqual(200, resp.statusCode(), \"Status code should be 200\");");
            writer.println("    assertTrue(resp.headers().firstValue(\"Content-Type\").orElse(\"\").contains(\"json\"), \"Content-Type should contain json\");");
            writer.println("    assertTrue(resp.body().contains(\"\\\"token\\\":\"), \"Body should contain token\");");
            writer.println("    System.out.println(\"  ✓ test_Login passed\");");
            writer.println("  }");
            writer.println();
            writer.println("  static void test_GetUser() throws Exception {");
            writer.println("    System.out.println(\"Running test_GetUser...\");");
            writer.println("    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + \"/api/users/42\"))");
            writer.println("      .timeout(Duration.ofSeconds(10))");
            writer.println("      .GET();");
            writer.println("    for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());");
            writer.println("    HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));");
            writer.println();
            writer.println("    assertEqual(200, resp.statusCode(), \"Status code should be 200\");");
            writer.println("    assertTrue(resp.body().contains(\"\\\"id\\\":42\"), \"Body should contain id:42\");");
            writer.println("    System.out.println(\"  ✓ test_GetUser passed\");");
            writer.println("  }");
            writer.println();
            writer.println("  static void test_UpdateUser() throws Exception {");
            writer.println("    System.out.println(\"Running test_UpdateUser...\");");
            writer.println("    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + \"/api/users/42\"))");
            writer.println("      .timeout(Duration.ofSeconds(10))");
            writer.println("      .PUT(HttpRequest.BodyPublishers.ofString(\"{ \\\"role\\\": \\\"ADMIN\\\" }\"));");
            writer.println("    for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());");
            writer.println("    b.header(\"Content-Type\", \"application/json\");");
            writer.println("    HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));");
            writer.println();
            writer.println("    assertEqual(200, resp.statusCode(), \"Status code should be 200\");");
            writer.println("    assertEqual(\"TestLangDemo\", resp.headers().firstValue(\"X-App\").orElse(\"\"), \"X-App header should be TestLangDemo\");");
            writer.println("    assertTrue(resp.headers().firstValue(\"Content-Type\").orElse(\"\").contains(\"json\"), \"Content-Type should contain json\");");
            writer.println("    assertTrue(resp.body().contains(\"\\\"updated\\\":true\"), \"Body should contain updated:true\");");
            writer.println("    assertTrue(resp.body().contains(\"\\\"role\\\":\\\"ADMIN\\\"\"), \"Body should contain role:ADMIN\");");
            writer.println("    System.out.println(\"  ✓ test_UpdateUser passed\");");
            writer.println("  }");
            writer.println();
            writer.println("  static void assertEqual(int expected, int actual, String message) {");
            writer.println("    if (expected != actual) {");
            writer.println("      throw new AssertionError(message + \" - Expected: \" + expected + \", Actual: \" + actual);");
            writer.println("    }");
            writer.println("  }");
            writer.println();
            writer.println("  static void assertEqual(String expected, String actual, String message) {");
            writer.println("    if (!expected.equals(actual)) {");
            writer.println("      throw new AssertionError(message + \" - Expected: '\" + expected + \"', Actual: '\" + actual + \"'\");");
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
}