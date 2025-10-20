#!/bin/bash

echo "Testing Generated JUnit Tests..."

# First, let's compile with JUnit dependencies
echo "Compiling GeneratedTests.java with JUnit 5..."

# Create a simple test runner
cat > TestRunner.java << 'EOF'
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Running TestLang++ Generated Tests...");
        
        try {
            GeneratedTests test = new GeneratedTests();
            
            // Run setup
            test.setup();
            
            // Run test_Login
            System.out.println("Running test_Login...");
            test.test_Login();
            System.out.println("✓ test_Login passed");
            
            // Run test_GetUser
            System.out.println("Running test_GetUser...");
            test.test_GetUser();
            System.out.println("✓ test_GetUser passed");
            
            // Run test_UpdateUser
            System.out.println("Running test_UpdateUser...");
            test.test_UpdateUser();
            System.out.println("✓ test_UpdateUser passed");
            
            System.out.println("\n🎉 All tests passed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
EOF

# Compile and run
echo "Compiling test runner..."
javac -cp ".:build" TestRunner.java GeneratedTests.java

echo "Running tests..."
java -cp ".:build" TestRunner

# Cleanup
rm -f TestRunner.java TestRunner.class