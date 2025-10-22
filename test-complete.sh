#!/bin/bash

echo "Complete TestLang++ Test"
echo "======================="
echo ""

# Build the compiler
echo "1. Building compiler..."
./build.sh
if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi
echo ""

# Generate tests
echo "2. Generating tests from example.test..."
java -cp build Main example.test
if [ $? -ne 0 ]; then
    echo "Test generation failed!"
    exit 1
fi
echo ""

# Compile generated tests
echo "3. Compiling generated tests..."
javac GeneratedTests.java
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
    echo "5. Running generated tests..."
    java GeneratedTests
else
    echo "Backend is NOT running!"
    echo "Please start the backend in another terminal with: ./run-backend.sh"
    echo ""
    echo "After starting the backend, run the tests with: java GeneratedTests"
fi

echo ""
echo "Test complete!"

