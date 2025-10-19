public class Main {
    public static void main(String[] args) throws Exception {
        ExprLexer lexer = new ExprLexer(new java.io.FileReader("input.txt"));
        ExprParser parser = new ExprParser(lexer);
        System.out.println("Result: " + parser.parse().value);
    }
}