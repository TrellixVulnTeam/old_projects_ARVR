#include <iostream>
#include <Controller.h>
#include <conio.h>
#include <windows.h>
#include <Sudoku.h>

Controller::Controller( Sudoku& game )
    : sudoku { &game }
{
    // give controller the reference of sudoku navigation pointer
    pointer = &(sudoku->getPointer());
}

/**
 * @brief sudoku navigation controlls
 * 
 */
void Controller::controls()
{
    while (true) {
        int keyPress;
        keyPress = 0;
        keyPress = getch();
        
        if (keyPress == 224 || keyPress == 0 || keyPress == 27) {
            keyPress = 256 + getch();

            if (keyPress == 328) // UP
            {
                upKey();
                break;
            }
            else if (keyPress == 336) // DOWN
            {
                downKey();
                break;
            }
            else if (keyPress == 331) // LEFT
            {
                leftKey();
                break;
            }
            else if (keyPress == 333) // RIGHT
            {
                rightKey();
                break;
            }
            else if (keyPress == 339) { // DELETE
                sudoku->removeNumber(*pointer);
            }
            else if (keyPress == 283) { // ESC
                exit(1);
            }
        }
        // save inserted numbers to voctor<pair>
        else if (isdigit(keyPress) && (keyPress - '0') > 0 && (keyPress - '0') < 10) {
            if (sudoku->field[*pointer] == 0 && sudoku->numberAt(*pointer) == -1) {
                sudoku->numbers.push_back(std::make_pair(*pointer, keyPress - '0'));
                sudoku->display();
            }
        }
        // create new game new game
        else if (keyPress == 110) {
            newGen:
            sudoku->createField();
            sudoku->display();
        }
        // solution check
        else if (keyPress == 99) {
                bool end = sudoku->solver();

                std::cout << std::endl << std::endl;
                std::cout << " This solution is " << ((end) ? "Correct" : "Incorrect") << std::endl;
        }
        // change amount of empty cells
        else {
            if (keyPress == 119) { // level up | w
                if (sudoku->level < 13) {
                    sudoku->level++;
                    goto newGen;
                }
            }
            else if (keyPress == 115) { // level down | s
                if (sudoku->level > 1) {
                    sudoku->level--;
                    goto newGen;
                } 
            }
        }
    }
}

void Controller::upKey()
{
    *pointer -= 9;

    if (*pointer < 0) {
        *pointer += 81;
    }
}

void Controller::downKey()
{
    *pointer += 9;

    if (*pointer > 80) {
        *pointer -= 81;
    }
}

void Controller::leftKey()
{
    if (*pointer % 9 == 0) {
        *pointer += 8;
    }
    else {
        *pointer -= 1;
    }
}

void Controller::rightKey()
{
    if ((*pointer + 1) % 9 == 0) {
        *pointer -= 8;
    }
    else {
        *pointer += 1;
    }
}