#!/bin/bash

echo "Building TestLang++ Compiler..."

# Create output directory
mkdir -p build

echo "Compiling..."
javac -cp ".:lib/java-cup-11b-runtime.jar" -d build src/ast/*.java src/CodeGenerator.java src/TestLangScanner.java src/TestLangParser.java src/sym.java src/Main.java

echo "Build complete!"
echo "Usage: java -cp build Main <input.test>"