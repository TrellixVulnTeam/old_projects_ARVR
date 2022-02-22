#ifndef CONTROLLER_H
#define CONTROLLER_H

#include <Sudoku.h>

class Controller
{
public:
    int * pointer;
    Sudoku * sudoku;

    Controller (Sudoku& game);

    void upKey();
    void downKey();
    void leftKey();
    void rightKey();
    void controls();
};
#endif