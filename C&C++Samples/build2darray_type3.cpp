#include <iostream>
#include <cstdlib> // atoi

#include <vector>

using namespace std;

int main(int argc, char **argv)
{
    int Height = argc>1?atoi(argv[1]):240;
    int Width  = argc>2?atoi(argv[2]):360;

    // build array
    vector< vector<double> > Array(Height);
    for (int h=0 ; h<Height ; ++h)
    {
        Array[h].resize(Width);

        // 初始化
        for (int w=0 ; w<Width ; ++w)
            Array[h][w] = 0.0;
    }

    ////////////////////
    ////////////////////
    ////////////////////

    // delete array
    for (int h=0 ; h<Height ; ++h)
    {
        Array[h].clear();
    }
    Array.clear();

    system("pause");
    return 0;
}
