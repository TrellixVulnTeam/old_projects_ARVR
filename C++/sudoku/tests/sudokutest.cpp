#include <iostream>
#include <Controller.h>
#include <Sudoku.h>

using namespace std;

int testcount = 0;
// #define TEST(a,b1,c1,b2,c2) cout << "Test " << ++testcount << ". " << a << ": " << (cmpf(b1,b2) && cmpf(c1,c2) ? "OK" :"FAIL") << endl
// #define TEST2(a,b1,c1,b2,c2) cout << "Test " << ++testcount << ". " << a << ": " << (cmpresult(b1,c1,b2,c2) ? "OK" :"FAIL") << endl
// #define TEST3(a, b1, c1) cout << "Test " << ++testcount << ". " a << ": " << (cmpr())

// bool cmpf (double a, double b) {
// 	if (fabs (a - b) < 0.001f) 
// 		return true;
// 	return false;
// }

// bool cmpresult (double s1, double s2, double r1, double r2) {
// 	return (cmpf (s1, r1) && cmpf (s2, r2)) || (cmpf (s1, r2) && cmpf (s2, r1));
// }

int main (int argc, char* argv[])
{
    cout << endl << "Test" << endl << endl;

    cout << "Testide arv: " << testcount << endl;
	return EXIT_SUCCESS;
}