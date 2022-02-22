#include <Sudoku.h>
#include <Controller.h>
#include <time.h>

int main()
{
    srand(time(0)); // for random number generation

    Sudoku game = Sudoku();
    Controller controller = Controller( game );

    game.createField();

    // gameloop
    while (true) {
        game.display();
        controller.controls();
    }
}