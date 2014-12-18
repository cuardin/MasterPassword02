CPP=g++
CC=gcc
CFLAGS=-c -I../src -I../test -I../src/scrypt -I../test/gtest-1.7.0/include
LDFLAGS=-lpthread
SRC_D=../src/
TEST_D=../test/

CORE_SOURCES=$(SRC_D)mpw_core.c $(SRC_D)types.c $(SRC_D)scrypt/crypto_scrypt-nosse.c $(SRC_D)scrypt/sha256.c

CLI_SOURCES=$(SRC_D)MasterPasswordCLI.cpp
CLI_OBJECTS=$(CLI_SOURCES:.cpp=.o) $(CORE_SOURCES:.c=.o)
CLI_EXE=mpw

TEST_SOURCES=$(TEST_D)MasterPasswordTest.cpp $(TEST_D)StandardizedTestSuite.cpp $(TEST_D)main.cpp $(TEST_D)testHMAC.cpp $(TEST_D)testutils.cpp $(TEST_D)tinyXML2.cpp
TEST_OBJECTS=$(TEST_SOURCES:.cpp=.o) $(CORE_SOURCES:.c=.o) $(TEST_D)gtest-1.7.0/make/gtest_main.a
TEST_EXE=test

GTEST=$(TEST_D)gtest-1.7.0/make/gtest_main.a


test: clean all
	cp $(TEST_D)mpw_tests.xml .
	./$(TEST_EXE)

all: $(TEST_SOURCES) $(CORE_SOURCES) $(TEST_EXE) $(CLI_EXE)

clean:
	cd $(SRC_D); rm -f *.o
	cd $(SRC_D)scrypt; rm -f *.o
	cd $(TEST_D); rm -f *.o; 
	cd $(TEST_D)gtest-1.7.0/make; make clean
	rm -f mpw_tests.xml $(TEST_EXE) $(CLI_EXE)

$(CLI_EXE): $(CLI_OBJECTS)
	$(CPP) $(CLI_OBJECTS) -o $@

$(GTEST):
	cd $(TEST_D)gtest-1.7.0/make; make

$(TEST_EXE): $(GTEST) $(TEST_OBJECTS) 
	$(CPP) -pthread $(TEST_OBJECTS) -o $@

.cpp.o:
	 $(CPP) $(CFLAGS) $< -o $@

.c.o:
	 $(CC) -std=c99 $(CFLAGS) $< -o $@

