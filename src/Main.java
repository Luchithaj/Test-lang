public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java Main <input.test>");
            System.exit(1);
        }
        
        StandaloneLexer lexer = new StandaloneLexer(new java.io.FileReader(args[0]));
        SimpleParser parser = new SimpleParser(lexer);
        parser.parse();
    }
}