#!/bin/bash

# colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # no color

echo -e "${YELLOW}starting build pipeline...${NC}\n"

# step 1: compile the scanner
echo -e "${YELLOW}[1/8] compiling scanner...${NC}"
java -jar lib/jflex-full-1.9.1.jar src/TestLang.flex
if [ $? -ne 0 ]; then
    echo -e "${RED}scanner compilation failed${NC}"
    exit 1
fi
echo -e "${GREEN}scanner compiled successfully${NC}\n"

# step 2: compile the parser
echo -e "${YELLOW}[2/8] compiling parser...${NC}"
java -jar lib/java-cup-11b.jar -destdir src -parser TestLangParser -symbols sym src/TestLang.cup
if [ $? -ne 0 ]; then
    echo -e "${RED}parser compilation failed${NC}"
    exit 1
fi
echo -e "${GREEN}parser compiled successfully${NC}\n"

# step 3: compile ast objects
echo -e "${YELLOW}[3/8] compiling ast objects...${NC}"
javac -cp "lib/java-cup-11b-runtime.jar:src" -d build src/ast/*.java
if [ $? -ne 0 ]; then
    echo -e "${RED}ast compilation failed${NC}"
    exit 1
fi
echo -e "${GREEN}ast objects compiled${NC}\n"

# step 4: compile the code generator
echo -e "${YELLOW}[4/8] compiling code generator...${NC}"
javac -cp "lib/java-cup-11b-runtime.jar:src" -d build src/CodeGenerator.java
if [ $? -ne 0 ]; then
    echo -e "${RED}code generator compilation failed${NC}"
    exit 1
fi
echo -e "${GREEN}code generator compiled${NC}\n"

# step 5: compile the scanner and parser java files
echo -e "${YELLOW}[5/8] compiling scanner and parser java files...${NC}"
javac -cp "lib/java-cup-11b-runtime.jar:src" -d build src/TestLangScanner.java src/TestLangParser.java src/sym.java
if [ $? -ne 0 ]; then
    echo -e "${RED}scanner/parser java compilation failed${NC}"
    exit 1
fi
echo -e "${GREEN}scanner and parser compiled${NC}\n"

# step 6: compile main
echo -e "${YELLOW}[6/8] compiling main...${NC}"
javac -cp "lib/java-cup-11b-runtime.jar:build" -d build src/Main.java
if [ $? -ne 0 ]; then
    echo -e "${RED}main compilation failed${NC}"
    exit 1
fi
echo -e "${GREEN}main compiled${NC}\n"

# step 7: run compiler on test file
echo -e "${YELLOW}[7/8] compiling test file...${NC}"
java -cp "lib/java-cup-11b-runtime.jar:build" Main examples/example.test
if [ $? -ne 0 ]; then
    echo -e "${RED}test file compilation failed${NC}"
    exit 1
fi
echo -e "${GREEN}test file compiled, GeneratedTests.java created${NC}\n"

# step 8: compile generated tests
echo -e "${YELLOW}[8/8] compiling generated tests...${NC}"
javac -cp "lib/junit-platform-console-standalone-1.9.3.jar:." GeneratedTests.java
if [ $? -ne 0 ]; then
    echo -e "${RED}generated tests compilation failed${NC}"
    exit 1
fi
echo -e "${GREEN}generated tests compiled${NC}\n"

# step 9: run the tests
echo -e "${YELLOW}running tests against backend...${NC}\n"
java -jar lib/junit-platform-console-standalone-1.9.3.jar --class-path . --scan-class-path

if [ $? -eq 0 ]; then
    echo -e "\n${GREEN}all tests passed!${NC}"
else
    echo -e "\n${RED}some tests failed${NC}"
    exit 1
fi