import java.io.*;
import java_cup.runtime.*;
import ast.*;

public class Main {
    public static void main(String[] args) {
        try {
            String filename = (args.length > 0) ? args[0] : "examples/example.test";

            FileReader fileReader = new FileReader(filename);
            TestLangScanner scanner = new TestLangScanner(fileReader);
            TestLangParser parser = new TestLangParser(scanner);

            parser.parse();
            Program program = parser.getProgram();

            System.out.println("\n✅ Parsing completed successfully!");

            CodeGenerator generator = new CodeGenerator();
            String javaCode = generator.generate(program);

            try (FileWriter writer = new FileWriter("GeneratedTests.java")) {
                writer.write(javaCode);
            }
            System.out.println("✅ Generated GeneratedTests.java");

        } catch (FileNotFoundException e) {
            System.err.println("❌ File not found: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}