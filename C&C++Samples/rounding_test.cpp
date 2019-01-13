#include <iostream>
#include <cmath>
#include <cstdlib>

using namespace std;

int main()
{
    cout << "Rounding in negtive value:" << endl;
    cout << (int)(-0.4 + 0.5) << endl;
    cout << (int)(-0.5 + 0.5) << endl;
    cout << (int)(-1.4 + 0.5) << endl;
    cout << (int)(-1.5 + 0.5) << endl;

    cout << "Rounding in negtive value(shift&shift back):" << endl;
    cout << (int)(-0.4 + 128 + 0.5) -128 << endl;
    cout << (int)(-0.5 + 128 + 0.5) -128 << endl;
    cout << (int)(-1.4 + 128 + 0.5) -128 << endl;
    cout << (int)(-1.5 + 128 + 0.5) -128 << endl;

    cout << "Rounding in negtive value(+0.5&floor):" << endl;
    cout << floor(-0.4 + 0.5) << endl; 
    cout << floor(-0.5 + 0.5) << endl;
    cout << floor(-1.4 + 0.5) << endl;
    cout << floor(-1.5 + 0.5) << endl;

    system("pause");
    return 0;
}
/*
DEV C++
Rounding in negtive value:
0
0
0
-1
Rounding in negtive value(shift&shift back):
0
0
-1
-1
Rounding in negtive value(+0.5&floor):
0
0
-1
-1
*/