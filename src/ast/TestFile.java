package ast;

import java.util.*;

public class TestFile {
    public ConfigBlock config;
    public List<Variable> variables;
    public List<TestBlock> testBlocks;
    
    public TestFile(ConfigBlock config, List<Variable> variables, List<TestBlock> testBlocks) {
        this.config = config;
        this.variables = variables;
        this.testBlocks = testBlocks;
    }
}
