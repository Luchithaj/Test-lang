package ast;

public class StatusAssertion extends Assertion {
    public int expectedStatus;
    
    public StatusAssertion(int expectedStatus) {
        this.expectedStatus = expectedStatus;
    }
}
