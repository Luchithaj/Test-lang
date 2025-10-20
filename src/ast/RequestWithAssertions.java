package ast;

import java.util.*;

public class RequestWithAssertions extends Statement {
    public HttpRequest request;
    public List<Assertion> assertions;
    
    public RequestWithAssertions(HttpRequest request, List<Assertion> assertions) {
        this.request = request;
        this.assertions = assertions;
    }
}
