#!/bin/bash

set -euo pipefail

echo "Building TestLang++ Compiler (hand-written parser)..."

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
COMPILER_DIR="${SCRIPT_DIR%/scripts}"
cd "$COMPILER_DIR"

rm -rf build
mkdir -p build

echo "Compiling sources..."
javac -d build \
  src/ast/*.java \
  src/StandaloneLexer.java \
  src/TestLangParser.java \
  src/CodeGenerator.java \
  src/Main.java

echo "Build complete!"
echo "Usage:"
echo "  java -cp 'build' Main <input.test>"
