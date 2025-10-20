package ast;

public class HeaderContainsAssertion extends Assertion {
    public String headerName;
    public String expectedSubstring;
    
    public HeaderContainsAssertion(String headerName, String expectedSubstring) {
        this.headerName = headerName;
        this.expectedSubstring = expectedSubstring;
    }
}
