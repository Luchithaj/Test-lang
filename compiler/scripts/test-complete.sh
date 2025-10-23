#!/bin/bash

echo "Complete TestLang++ Test"
echo "======================="
echo ""

# Build the compiler
echo "1. Building compiler..."
cd "$(dirname "$0")/.."
./scripts/build.sh
if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi
echo ""

# Generate tests
echo "2. Generating tests from examples/example.test..."
java -cp "lib/java-cup-11b-runtime.jar:build" Main examples/example.test
if [ $? -ne 0 ]; then
    echo "Test generation failed!"
    exit 1
fi
echo ""

# Compile generated tests with JUnit 5
echo "3. Compiling generated tests with JUnit 5..."
javac -cp "lib/junit-platform-console-standalone-1.9.3.jar" GeneratedTests.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi
echo ""

# Check if backend is running
echo "4. Checking if backend is running..."
if curl -s http://localhost:8080/api/users/42 > /dev/null 2>&1; then
    echo "Backend is running!"
    echo ""
    echo "5. Running generated tests with JUnit 5..."
    java -jar lib/junit-platform-console-standalone-1.9.3.jar --class-path . --select-class GeneratedTests
else
    echo "Backend is NOT running!"
    echo "Please start the backend in another terminal with: ../../run-backend.sh"
    echo ""
    echo "After starting the backend, run the tests with: java -jar lib/junit-platform-console-standalone-1.9.3.jar --class-path . --select-class GeneratedTests"
fi

echo ""
echo "Test complete!"
