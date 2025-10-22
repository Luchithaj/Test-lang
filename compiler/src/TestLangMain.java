import ast.*;
import java.io.*;

public class TestLangMain {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java TestLangMain <input.test>");
            System.exit(1);
        }
        
        try {
            // Use JFlex-generated lexer
            TestLangLexer lexer = new TestLangLexer(new java.io.FileReader(args[0]));
            
            // Use hand-written parser (but demonstrate JFlex integration)
            TestLangParser parser = new TestLangParser(lexer);
            TestFile result = parser.parse();
            
            System.out.println("Parsing complete. Generating JUnit code...");
            CodeGenerator.generate(result);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}