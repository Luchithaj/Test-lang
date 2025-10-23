#!/bin/bash

set -euo pipefail

echo "Building TestLang++ Compiler with JFlex + CUP..."

# Always run from compiler/ root
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
COMPILER_DIR="${SCRIPT_DIR%/scripts}"
cd "$COMPILER_DIR"

# Prepare output directory
mkdir -p build
# Clean any previously generated Java sources (keep .class is harmless but tidy up sources)
rm -f build/parser/TestLangLexer.java build/parser/TestLangParser.java build/parser/sym.java || true
mkdir -p build/parser

echo "1) Generating lexer with JFlex..."
java -jar lib/jflex-full-1.9.1.jar -d build/parser src/Tokens.flex

echo "2) Generating parser with CUP..."
# Generate parser and symbols into build/parser (matches package path)
java -jar lib/java-cup-11b.jar \
  -destdir build/parser \
  -parser TestLangParser \
  -symbols sym \
  src/Parser.cup

echo "3) Compiling compiler sources + generated lexer/parser..."
javac -cp "lib/java-cup-11b-runtime.jar:build" -d build \
  src/ast/*.java \
  src/CodeGenerator.java \
  src/Main.java \
  build/parser/TestLangLexer.java \
  build/parser/sym.java \
  build/parser/TestLangParser.java

echo "Build complete!"
echo "Usage:"
echo "  java -cp 'lib/java-cup-11b-runtime.jar:build' parser.TestLangParser <input.test>"
