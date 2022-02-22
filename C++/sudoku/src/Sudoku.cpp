#include <Sudoku.h>
#include <iostream>
#include <windows.h>
#include <vector>
#include <array>
#include <chrono>
#include <random>
#include <algorithm>

Sudoku::Sudoku()
{
    level = 7;
    pointer = 0;
}

/**
 * @brief create game field. Empty cells equals sudoku level
 * 
 */
void Sudoku::createField()
{
    // create number list and randomize it
    array<int, 9> numbers {1, 2, 3, 4, 5, 6, 7, 8, 9};
    unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
    shuffle (numbers.begin(), numbers.end(), std::default_random_engine(seed));

    // enter first row of randomized numbers
    for (int i = 0; i < 9; i++)
    {
        field[i] = numbers.at(i);
    }

    // for the next 2 rows shift the previous rows numbers by 3
    // 4th and 7th rows shift by 1 and the others by 3 again
    // resulting in valid sudoku field 
    for (int i = 9; i < 81; i += 9)
    {
        if (i == 9 || i == 18 || i == 36 || i == 45 || i == 63 || i == 72) {
            numbers = shift(3, numbers);
        }
        else if (i == 27 || i == 54) {
            numbers = shift(1, numbers);
        }

        for (int j = i; j < (i + 9); j++)
        {
            field[j] = numbers.at((j % 9));
        }
    }

    // remove numbers from field as much as there are levels
    for (int i = 0; i < level; i++)
    {
        int random = rand() % (80 + 1);

        if (field[random] != 0) {
            field[random] = 0;
        }
        else {
            i--;
        }
    }
}

/**
 * @brief shift the number sequence by given times
 *  
 */
array<int, 9> Sudoku::shift(int times, array<int, 9> numbers)
{
    int a;

    for (int  i = 0; i < times; i++)
    {
        a = numbers.at(0);

        for (auto j = 0; j < 8; j++)
        {
            numbers[j] = numbers.at(j+1);
        }

        numbers[8] = a;
    }
    
    return numbers;
}

/**
 * @brief change the console text color
 *  
 */
void Sudoku::colorSet(int color)
{
    SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), color);
}

/**
 * @brief display the game and instructions onto console
 * 
 */
void Sudoku::display()
{
    system("cls"); // clean console

    for (int i = 0; i < 81; i++)
    {
        std::cout << " ";

        if (i % 3 == 0 && i % 9 != 0) {
            std::cout << "| ";
        }

        if (pointer == i) {
            colorSet(6); // yellow
            
            if (field[i] == 0) {
                if (numberAt(pointer) != -1) {
                    std::cout << numberAt(pointer);
                }
                else {
                    std::cout << "X"; // empty cell selected
                }
            }
            else {
                std::cout << field[i];
            }

            colorSet(15); // white
        }
        else {
            if (field[i] == 0) {
                colorSet(10); // green
                if (numberAt(i) != -1) {
                    std::cout << numberAt(i);
                }
                else {
                    // cout << ".";
                    std::cout << " ";
                }
                colorSet(15); // white
            }
            else {
                std::cout << field[i];
            }
        }

        // instructions /1
        switch (i) {
            case 26:
                std::cout << "    Navigate with ARROW keys";
                break;
            case 35:
                std::cout << "    DELELTE key removes added number (while on the number)";
                break;
            case 44:
                std::cout << "    C - checks if the current solution is correct";
                break;
            case 53:
                std::cout << "    N - generates new sudoku game";
                break;
            case 80:
                std::cout << "    EMPTY CELLS = " << level;
        }

        if ((i + 1) % 9 == 0) {
            std::cout << std::endl;
            
            if ((i + 1) % 27 == 0 && (i + 1) != 81) {
                std::cout << " ------|-------|------ ";

                // instrucions /2
                switch (i + 1) {
                    case 27:
                        std::cout << "   Add numbers with NUMBER keys" << std::endl;
                        break;
                    case 54:
                        std::cout << "   ESC - Quit console game" << std::endl;
                        break;
                }
            }
        }
    }
}

/**
 * @brief get selected number at given index if it exists. returns -1 if not
 *  
 */
int Sudoku::numberAt(int index)
{
    for (auto i = numbers.begin(); i < numbers.end(); i++)
    {
        if ((*i).first == index) {
            return (*i).second;
        }
    }

    return -1;
}

/**
 * @brief returns sudoku navigation pointer referrence address
 *  
 */
int& Sudoku::getPointer()
{
    return pointer;
}

/**
 * @brief removes the number at given index if exists
 *  
 */
void Sudoku::removeNumber(int index)
{
    for (auto i = numbers.begin(); i < numbers.end(); i++)
    {
        if (i->first == pointer) {
            numbers.erase(i);
            display();
            return;
        }
    }
}

/**
 * @brief solves the current solution and returns verdict
 * 
 */
bool Sudoku::solver()
{
    int row;
    int col;
    int loner;

    for (int i = 0; i < 81; i++)
    {
        if (field[i] != 0) {
            loner = field[i];
        }
        else {
            if (numberAt(i) != -1) {
                loner = numberAt(i);
            }
            else {
                return false;
            }
        }

        row = floor(i / 9);
        col = i % 9;

        // check row
        for (int x = (row * 9); x < (row * 9) + 9; x++)
        {
            if (x != i && (loner == field[x] || loner == numberAt(x))) {
                return false;
            }
        }

        // check col
        for (int y = col; y < 81; y += 9)
        {
            if (y != i && (loner == field[y] || loner == numberAt(y))) {
                return false;
            }
        }

        // find 3x3 cell corner
        int corner;

        if (row >= 0 && row <= 2) {
            if (col >= 0 && col <= 2) {
                corner = 0;
            }
            else if (col >= 3 && col <= 5) {
                corner = 3;
            }
            else if (col >= 6 && col <= 8) {
                corner = 6;
            }
        }
        else if (row >= 3 && row <= 5) {
            if (col >= 0 && col <= 2) {
                corner = 27;
            }
            else if (col >= 3 && col <= 5) {
                corner = 30;
            }
            else if (col >= 6 && col <= 8) {
                corner = 33;
            }
        }
        else if (row >= 6 && row <= 8) {
            if (col >= 0 && col <= 2) {
                corner = 54;
            }
            else if (col >= 3 && col <= 5) {
                corner = 57;
            }
            else if (col >= 6 && col <= 8) {
                corner = 60;
            }
        }

        // check local 3x3 cell
        for (int z = corner; z < corner + 21; z += 9)
        {
            for (int zz = z; zz < z + 3; zz++)
            {
                if (i != zz && (field[zz] == loner || numberAt(zz) == loner)) {
                    return false;
                }
            }
        }
    }

    return true;
}
