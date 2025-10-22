package ast;

import java.util.*;

public class HttpRequest extends Statement {
    public String method;
    public String path;
    public List<Header> headers;
    public String body;
    
    public HttpRequest(String method, String path, List<Header> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers != null ? headers : new ArrayList<>();
        this.body = body;
    }
}
