package ast;

import java.util.*;

public class RequestBlock {
    public List<Header> headers;
    public String body;

    public RequestBlock(List<Header> headers, String body) {
        this.headers = headers != null ? headers : new ArrayList<Header>();
        this.body = body;
    }
}

