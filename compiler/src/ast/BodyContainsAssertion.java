package ast;

public class BodyContainsAssertion extends Assertion {
    public String expectedSubstring;
    
    public BodyContainsAssertion(String expectedSubstring) {
        this.expectedSubstring = expectedSubstring;
    }
}
