#!/bin/bash

echo "TestLang++ DSL Compiler Demo"
echo "============================"
echo

echo "1. Building the compiler..."
./build.sh
echo

echo "2. Compiling example.test to GeneratedTests.java..."
java -cp build Main example.test
echo

echo "3. Compiling the generated test..."
javac GeneratedTests.java
echo

echo "4. Running the generated test..."
echo "   (Make sure backend is running with ./run-backend.sh)"
echo
java GeneratedTests
echo

echo "Demo complete!"