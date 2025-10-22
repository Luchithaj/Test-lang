#!/bin/bash

echo "Building TestLang++ Compiler..."

# Create output directory
mkdir -p build

echo "Compiling..."
javac -d build src/ast/*.java src/CodeGenerator.java src/StandaloneLexer.java src/TestLangParser.java src/Main.java

echo "Build complete!"
echo "Usage: java -cp build Main <input.test>"