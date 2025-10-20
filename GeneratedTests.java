import java.net.http.*;
import java.net.*;
import java.time.Duration;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GeneratedTests {
  static String BASE = "http://localhost:8080";
  static Map<String,String> DEFAULT_HEADERS = new HashMap<>();
  static HttpClient client;

  public static void main(String[] args) {
    System.out.println("TestLang++ Generated Tests");
    System.out.println("========================");
    setup();
    
    try {
      test_Login();
      test_GetUser();
      test_UpdateUser();
      System.out.println("\nAll tests passed!");
    } catch (Exception e) {
      System.err.println("Test failed: " + e.getMessage());
      e.printStackTrace();
    }
  }

  static void setup() {
    client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    DEFAULT_HEADERS.put("Content-Type","application/json");
  }

  static void test_Login() throws Exception {
    System.out.println("Running test_Login...");
    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + "/api/login"))
      .timeout(Duration.ofSeconds(10))
      .POST(HttpRequest.BodyPublishers.ofString("{ \"username\": \"admin\", \"password\": \"1234\" }"));
    for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());
    HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    assertEqual(200, resp.statusCode(), "Status code should be 200");
    assertTrue(resp.headers().firstValue("Content-Type").orElse("").contains("json"), "Content-Type should contain json");
    assertTrue(resp.body().contains("\"token\":"), "Body should contain token");
    System.out.println("  ✓ test_Login passed");
  }

  static void test_GetUser() throws Exception {
    System.out.println("Running test_GetUser...");
    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + "/api/users/42"))
      .timeout(Duration.ofSeconds(10))
      .GET();
    for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());
    HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    assertEqual(200, resp.statusCode(), "Status code should be 200");
    assertTrue(resp.body().contains("\"id\":42"), "Body should contain id:42");
    System.out.println("  ✓ test_GetUser passed");
  }

  static void test_UpdateUser() throws Exception {
    System.out.println("Running test_UpdateUser...");
    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + "/api/users/42"))
      .timeout(Duration.ofSeconds(10))
      .PUT(HttpRequest.BodyPublishers.ofString("{ \"role\": \"ADMIN\" }"));
    for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());
    b.header("Content-Type", "application/json");
    HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    assertEqual(200, resp.statusCode(), "Status code should be 200");
    assertEqual("TestLangDemo", resp.headers().firstValue("X-App").orElse(""), "X-App header should be TestLangDemo");
    assertTrue(resp.headers().firstValue("Content-Type").orElse("").contains("json"), "Content-Type should contain json");
    assertTrue(resp.body().contains("\"updated\":true"), "Body should contain updated:true");
    assertTrue(resp.body().contains("\"role\":\"ADMIN\""), "Body should contain role:ADMIN");
    System.out.println("  ✓ test_UpdateUser passed");
  }

  static void assertEqual(int expected, int actual, String message) {
    if (expected != actual) {
      throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
    }
  }

  static void assertEqual(String expected, String actual, String message) {
    if (!expected.equals(actual)) {
      throw new AssertionError(message + " - Expected: '" + expected + "', Actual: '" + actual + "'");
    }
  }

  static void assertTrue(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }
}
