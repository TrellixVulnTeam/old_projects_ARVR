PROGRAM = bin/Sudoku
OBJ = obj/Sudoku.o obj/main.o obj/Controller.o

CXX=g++
CXXFLAGS = -Wall -std=c++11
LIB_CXXFLAGS = $(CXXFLAGS) -Iinclude
LIBOBJS = obj/Sudoku.o obj/main.o obj/Controller.o

TESTAPP = bin/sudokutest
TESTOBJS = obj/sudokutest.o
APP_CXXFLAGS = $(CXXFLAGS) -Iinclude

$(LIBRARY): $(LIBOBJS)
	ar cr $(LIBRARY) $(LIBOBJS)

$(PROGRAM): $(OBJ)
	$(CXX) $(OBJ) -o $(PROGRAM) $(CXXFLAGS)

# $(TESTAPP): $(TESTOBJS) $(LIBRARY)
# 	$(CXX) $(LIBOBJS) -o $(TESTAPP) $(LFLAGS)

all: $(LIBRARY) $(PROGRAM)

obj/Sudoku.o: src/Sudoku.cpp
	$(CXX) $(LIB_CXXFLAGS) -c src/Sudoku.cpp -o obj/Sudoku.o

obj/main.o: src/main.cpp
	$(CXX) $(LIB_CXXFLAGS) -c src/main.cpp -o obj/main.o

obj/Controller.o: src/Controller.cpp
	$(CXX) $(LIB_CXXFLAGS) -c src/Controller.cpp -o obj/Controller.o

obj/sudokutest.o: tests/sudokutest.cpp
	$(CXX) $(APP_CXXFLAGS) -c tests/sudokutest.cpp -o obj/sudokutest.o

# test: $(TESTAPP)
# 	./$(TESTAPP)

clean:
	rm -f $(LIBOBJS)
	rm -f $(TESTOBJS)
	rm -f $(LIBRARY)
	rm -f $(TESTAPP)
	rm -rf bin/*
	rm -rf .vscode/*
