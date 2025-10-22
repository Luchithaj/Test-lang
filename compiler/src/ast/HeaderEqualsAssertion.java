package ast;

public class HeaderEqualsAssertion extends Assertion {
    public String headerName;
    public String expectedValue;
    
    public HeaderEqualsAssertion(String headerName, String expectedValue) {
        this.headerName = headerName;
        this.expectedValue = expectedValue;
    }
}
