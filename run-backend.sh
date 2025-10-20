#!/bin/bash

echo "Building and running TestLang++ Backend..."

cd backend

# Build the Spring Boot application
echo "Building Spring Boot application..."
mvn clean package -DskipTests

# Run the application
echo "Starting backend server on http://localhost:8080..."
java -jar target/testlang-demo-0.0.1-SNAPSHOT.jar
