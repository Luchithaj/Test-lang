package ast;

public class Assertion extends Statement {
    public enum AssertionType {
        STATUS, BODY_CONTAINS, HEADER_EQUALS, HEADER_CONTAINS
    }

    private AssertionType type;
    private int statusCode;
    private String headerName;
    private String expectedValue;

    private Assertion(AssertionType type) {
        this.type = type;
    }

    public static Assertion status(int code) {
        Assertion a = new Assertion(AssertionType.STATUS);
        a.statusCode = code;
        return a;
    }

    public static Assertion bodyContains(String text) {
        Assertion a = new Assertion(AssertionType.BODY_CONTAINS);
        a.expectedValue = text;
        return a;
    }

    public static Assertion headerEquals(String name, String value) {
        Assertion a = new Assertion(AssertionType.HEADER_EQUALS);
        a.headerName = name;
        a.expectedValue = value;
        return a;
    }

    public static Assertion headerContains(String name, String substr) {
        Assertion a = new Assertion(AssertionType.HEADER_CONTAINS);
        a.headerName = name;
        a.expectedValue = substr;
        return a;
    }

    public AssertionType getType() {
        return type;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getHeaderName() {
        return headerName;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    @Override
    public String toString() {
        switch (type) {
            case STATUS:
                return "Assertion{type=STATUS, code=" + statusCode + "}";
            case BODY_CONTAINS:
                return "Assertion{type=BODY_CONTAINS, text='" + expectedValue + "'}";
            case HEADER_EQUALS:
                return "Assertion{type=HEADER_EQUALS, header='" + headerName + "', value='" + expectedValue + "'}";
            case HEADER_CONTAINS:
                return "Assertion{type=HEADER_CONTAINS, header='" + headerName + "', substr='" + expectedValue + "'}";
            default:
                return "Assertion{type=" + type + "}";
        }
    }
}