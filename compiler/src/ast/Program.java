package ast;

import java.util.List;
import java.util.ArrayList;

public class Program {
    private Config config;
    private List<Variable> variables;
    private List<Test> tests;

    public Program() {
        this.variables = new ArrayList<>();
        this.tests = new ArrayList<>();
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void addVariable(Variable variable) {
        this.variables.add(variable);
    }

    public List<Test> getTests() {
        return tests;
    }

    public void addTest(Test test) {
        this.tests.add(test);
    }

    @Override
    public String toString() {
        return "Program{config=" + config + ", variables=" + variables.size() + ", tests=" + tests.size() + "}";
    }
}
