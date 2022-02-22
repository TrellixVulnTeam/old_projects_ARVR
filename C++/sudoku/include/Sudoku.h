#ifndef SUDOKU_H
#define SUDOKU_H

#include <vector>
#include <iostream>
#include <array>

using std::pair;
using std::vector;
using std::ostream;
using std::array;

class Sudoku
{
public:
    int pointer;
    int level;
    int field[81];
    vector<pair<int, int>> numbers;

    Sudoku();

    void createField();
    void userControl();
    void colorSet(int color);
    void removeNumber(int index);
    int numberAt(int index);
    bool solver();
    array<int, 9> shift(int times, array<int, 9> numbers);
    void display();
    int& getPointer();
};

#endif