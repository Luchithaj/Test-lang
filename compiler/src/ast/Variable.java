package ast;

public class Variable {
    private String name;
    private String value;

    public Variable(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Variable{name='" + name + "', value='" + value + "'}";
    }
}