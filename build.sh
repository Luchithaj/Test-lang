#!/bin/bash

echo "TestLang++ Project Build Script"
echo "==============================="
echo ""

# Build the compiler
echo "Building TestLang++ Compiler..."
cd compiler
./scripts/build.sh
if [ $? -ne 0 ]; then
    echo "Compiler build failed!"
    exit 1
fi

echo ""
echo "Building Spring Boot Backend..."
cd ../backend
mvn -q -DskipTests package
if [ $? -ne 0 ]; then
    echo "Backend build failed!"
    exit 1
fi

cd ..
echo ""
echo "Build complete!"
echo ""
echo "To run tests:"
echo "  1. Start backend: ./run-backend.sh"
echo "  2. Run tests: cd compiler && ./scripts/test-complete.sh"