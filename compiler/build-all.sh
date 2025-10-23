#!/bin/bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

echo "Building TestLang++ project"

cd "$SCRIPT_DIR"

echo "1) Cleaning build directory"
rm -rf build
mkdir -p build

echo "2) Compiling compiler sources"
javac -d build \
  src/ast/*.java \
  src/StandaloneLexer.java \
  src/TestLangParser.java \
  src/CodeGenerator.java \
  src/Main.java

echo "3) Generating JUnit tests from DSL"
java -cp "build" Main examples/example.test

echo "4) Compiling generated JUnit tests"
javac -cp "lib/junit-platform-console-standalone-1.9.3.jar" GeneratedTests.java

echo "5) Running generated JUnit tests"
java -jar lib/junit-platform-console-standalone-1.9.3.jar --class-path . --select-class GeneratedTests

echo "All steps completed successfully."
