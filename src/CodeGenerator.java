import ast.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CodeGenerator {
    private StringBuilder code = new StringBuilder();
    private Program prog;
    private Map<String, String> vars = new HashMap<>();
    private boolean respBodyNormalized = false;

    public String generate(Program program){
        this.prog = program;
        
        collectVariables(program.getVariables());
        generateClassStructure();
        
        for (Test t : prog.getTests()) {
            generateTestMethod(t);
        }
        
        code.append("}\n");
        return code.toString();
    }

    private void collectVariables(List<Variable> variables) {
        for (Variable v : variables) {
            vars.put(v.getName(), v.getValue());
        }
    }

    private void generateClassStructure() {
        code.append("import org.junit.jupiter.api.*;\n");
        code.append("import static org.junit.jupiter.api.Assertions.*;\n");
        code.append("import java.net.http.*;\n");
        code.append("import java.net.*;\n");
        code.append("import java.time.Duration;\n");
        code.append("import java.nio.charset.StandardCharsets;\n");
        code.append("import java.util.*;\n\n");

        code.append("public class GeneratedTests {\n");
        code.append("  private static String baseUrl = \"\";\n");
        code.append("  private static Map<String,String> globalHeaders = new HashMap<>();\n");
        code.append("  private static HttpClient httpClient;\n\n");

        code.append("  @BeforeAll\n");
        code.append("  static void init() {\n");
        code.append("    httpClient = HttpClient.newBuilder()\n");
        code.append("      .version(HttpClient.Version.HTTP_1_1)\n");
        code.append("      .connectTimeout(Duration.ofSeconds(5))\n");
        code.append("      .build();\n");

        if (prog.getConfig() != null) {
            Config config = prog.getConfig();
            if (config.getBaseUrl() != null) {
                code.append("    baseUrl = \"").append(sanitize(config.getBaseUrl())).append("\";\n");
            }
            for (Map.Entry<String, String> h : config.getDefaultHeaders().entrySet()) {
                code.append("    globalHeaders.put(\"").append(sanitize(h.getKey()))
                    .append("\", \"").append(sanitize(h.getValue())).append("\");\n");
            }
        }

        code.append("  }\n\n");
    }

    private void generateTestMethod(Test test) {
        code.append("  @Test\n");
        code.append("  void test").append(test.getName()).append("() throws Exception {\n");
        respBodyNormalized = false;

        for (Statement s : test.getStatements()) {
            if (s instanceof Request) {
                handleRequest((Request) s);
            } else if (s instanceof Assertion) {
                handleAssertion((Assertion) s);
            }
        }

        code.append("  }\n\n");
    }

    private void handleRequest(Request r) {
        String endpoint = replaceVariables(r.getPath());
        
        String fullUrl;
        if (endpoint.startsWith("/")) {
            fullUrl = "baseUrl + \"" + sanitize(endpoint) + "\"";
        } else {
            fullUrl = "\"" + sanitize(endpoint) + "\"";
        }

        code.append("    System.out.println(\"-> ").append(r.getMethod())
            .append(" \" + ").append(fullUrl).append(");\n");
        code.append("    var reqBuilder = HttpRequest.newBuilder(URI.create(")
            .append(fullUrl).append("))\n");
        code.append("      .timeout(Duration.ofSeconds(10))\n");

        switch (r.getMethod()) {
            case GET:
                code.append("      .GET();\n");
                break;
            case POST:
                String postData = replaceVariables(r.getBody());
                code.append("      .POST(HttpRequest.BodyPublishers.ofString(\"")
                    .append(sanitize(postData)).append("\"));\n");
                code.append("    System.out.println(\"    payload: \" + \"")
                    .append(sanitize(postData)).append("\");\n");
                break;
            case PUT:
                String putData = replaceVariables(r.getBody());
                code.append("      .PUT(HttpRequest.BodyPublishers.ofString(\"")
                    .append(sanitize(putData)).append("\"));\n");
                code.append("    System.out.println(\"    payload: \" + \"")
                    .append(sanitize(putData)).append("\");\n");
                break;
            case DELETE:
                code.append("      .DELETE();\n");
                break;
        }

        code.append("    globalHeaders.forEach((k, v) -> reqBuilder.header(k, v));\n");
        code.append("    reqBuilder.header(\"Accept\", \"application/json\");\n");

        for (Map.Entry<String, String> hdr : r.getHeaders().entrySet()) {
            code.append("    reqBuilder.header(\"").append(sanitize(hdr.getKey()))
                .append("\", \"").append(sanitize(hdr.getValue())).append("\");\n");
        }

        code.append("    var resp = httpClient.send(reqBuilder.build(), ")
            .append("HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));\n");
        code.append("    System.out.println(\"<- status: \" + resp.statusCode());\n");
        code.append("    System.out.println(resp.body());\n\n");
        
        respBodyNormalized = false;
    }

    private void handleAssertion(Assertion a) {
        switch (a.getType()) {
            case STATUS:
                code.append("    assertEquals(").append(a.getStatusCode())
                    .append(", resp.statusCode());\n");
                break;
            case BODY_CONTAINS:
                if (!respBodyNormalized) {
                    code.append("    var cleanBody = resp.body()")
                        .append(".replaceAll(\"\\\\s+\", \"\");\n");
                    respBodyNormalized = true;
                }
                String searchText = sanitize(a.getExpectedValue()).replaceAll("\\s+", "");
                code.append("    assertTrue(cleanBody.contains(\"")
                    .append(searchText).append("\"));\n");
                break;
            case HEADER_EQUALS:
                code.append("    assertEquals(\"").append(sanitize(a.getExpectedValue()))
                    .append("\", resp.headers().firstValue(\"")
                    .append(sanitize(a.getHeaderName())).append("\").orElse(\"\"));\n");
                break;
            case HEADER_CONTAINS:
                code.append("    assertTrue(resp.headers().firstValue(\"")
                    .append(sanitize(a.getHeaderName())).append("\").orElse(\"\").contains(\"")
                    .append(sanitize(a.getExpectedValue())).append("\"));\n");
                break;
        }
    }

    private String replaceVariables(String text) {
        if (text == null) return null;
        
        String result = text;
        for (Map.Entry<String, String> v : vars.entrySet()) {
            result = result.replace("$" + v.getKey(), v.getValue());
        }
        return result;
    }

    private String sanitize(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}