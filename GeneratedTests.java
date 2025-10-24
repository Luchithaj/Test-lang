import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.net.http.*;
import java.net.*;
import java.time.Duration;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GeneratedTests {
  private static String baseUrl = "";
  private static Map<String,String> globalHeaders = new HashMap<>();
  private static HttpClient httpClient;

  @BeforeAll
  static void init() {
    httpClient = HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_1_1)
      .connectTimeout(Duration.ofSeconds(5))
      .build();
    baseUrl = "http://localhost:8080";
    globalHeaders.put("Content-Type", "application/json");
  }

  @Test
  void testLogin() throws Exception {
    System.out.println("-> POST " + baseUrl + "/api/login");
    var reqBuilder = HttpRequest.newBuilder(URI.create(baseUrl + "/api/login"))
      .timeout(Duration.ofSeconds(10))
      .POST(HttpRequest.BodyPublishers.ofString("{ \"username\": \"admin\", \"password\": \"1234\" }"));
    System.out.println("    payload: " + "{ \"username\": \"admin\", \"password\": \"1234\" }");
    globalHeaders.forEach((k, v) -> reqBuilder.header(k, v));
    reqBuilder.header("Accept", "application/json");
    var resp = httpClient.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    System.out.println("<- status: " + resp.statusCode());
    System.out.println(resp.body());

    assertEquals(200, resp.statusCode());
    assertTrue(resp.headers().firstValue("Content-Type").orElse("").contains("json"));
    var cleanBody = resp.body().replaceAll("\\s+", "");
    assertTrue(cleanBody.contains("\"token\":"));
  }

  @Test
  void testGetUser() throws Exception {
    System.out.println("-> GET " + baseUrl + "/api/users/42");
    var reqBuilder = HttpRequest.newBuilder(URI.create(baseUrl + "/api/users/42"))
      .timeout(Duration.ofSeconds(10))
      .GET();
    globalHeaders.forEach((k, v) -> reqBuilder.header(k, v));
    reqBuilder.header("Accept", "application/json");
    var resp = httpClient.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    System.out.println("<- status: " + resp.statusCode());
    System.out.println(resp.body());

    assertEquals(200, resp.statusCode());
    var cleanBody = resp.body().replaceAll("\\s+", "");
    assertTrue(cleanBody.contains("\"id\":42"));
  }

  @Test
  void testUpdateUser() throws Exception {
    System.out.println("-> PUT " + baseUrl + "/api/users/42");
    var reqBuilder = HttpRequest.newBuilder(URI.create(baseUrl + "/api/users/42"))
      .timeout(Duration.ofSeconds(10))
      .PUT(HttpRequest.BodyPublishers.ofString("{ \"role\": \"ADMIN\" }"));
    System.out.println("    payload: " + "{ \"role\": \"ADMIN\" }");
    globalHeaders.forEach((k, v) -> reqBuilder.header(k, v));
    reqBuilder.header("Accept", "application/json");
    var resp = httpClient.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    System.out.println("<- status: " + resp.statusCode());
    System.out.println(resp.body());

    assertEquals(200, resp.statusCode());
    assertEquals("TestLangDemo", resp.headers().firstValue("X-App").orElse(""));
    assertTrue(resp.headers().firstValue("Content-Type").orElse("").contains("json"));
    var cleanBody = resp.body().replaceAll("\\s+", "");
    assertTrue(cleanBody.contains("\"updated\":true"));
    assertTrue(cleanBody.contains("\"role\":\"ADMIN\""));
  }

}
