import ast.*;
import parser.TestLangLexer;
import parser.TestLangParser;
import parser.CodeGenerator;
import java_cup.runtime.Symbol;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java Main <input.test>");
            System.exit(1);
        }
        
        TestLangLexer lexer = new TestLangLexer(new java.io.FileReader(args[0]));
        TestLangParser parser = new TestLangParser(lexer);
        Symbol sym = parser.parse();
        TestFile result = (TestFile) sym.value;
        
        System.out.println("Parsing complete. Generating JUnit code...");
        CodeGenerator.generate(result);
    }
}
