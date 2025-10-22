#!/bin/bash

echo "Building TestLang++ Compiler with JFlex + CUP..."

# Create output directory
mkdir -p build

echo "Generating lexer with JFlex..."
java -jar lib/jflex-full-1.9.1.jar -d build src/Tokens.flex

echo "Compiling with JFlex-generated lexer..."
javac -cp "lib/java-cup-11b-runtime.jar:build" -d build src/ast/*.java src/CodeGenerator.java src/TestLangMain.java src/TestLangLexer.java src/TestLangParser.java

echo "Build complete!"
echo "Usage: java -cp 'lib/java-cup-11b-runtime.jar:build' TestLangMain <input.test>"