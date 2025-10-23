import ast.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java Main <input.test>");
            System.exit(1);
        }
        
        StandaloneLexer lexer = new StandaloneLexer(new java.io.FileReader(args[0]));
        TestLangParser parser = new TestLangParser(lexer);
        TestFile result = parser.parse();
        
        System.out.println("Parsing complete. Generating JUnit code...");
        CodeGenerator.generate(result);
    }
}
