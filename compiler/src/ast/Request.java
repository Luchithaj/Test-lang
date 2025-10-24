package ast;

public class Request extends Statement {
    public enum HttpMethod {
        GET, POST, PUT, DELETE
    }

    private HttpMethod method;
    private String path;
    private String body;

    public Request(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
        this.body = null;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Request{method=" + method + ", path='" + path + "', body=" + (body != null ? "'" + body + "'" : "null") + "}";
    }
}
