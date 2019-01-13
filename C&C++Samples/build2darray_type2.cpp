#include <iostream>
#include <cstdlib> // atoi

using namespace std;

int main(int argc, char **argv)
{
    int Height = argc>1?atoi(argv[1]):240;
    int Width  = argc>2?atoi(argv[2]):360;

    // build array
    double** Array = new double*[Height];
    Array[0] = new double[Height*Width];

    for (int h=0 ; h<Height ; ++h)
    {
        if (h>0) Array [h] = Array[h-1] + Width;

        // 初始化
        for (int w=0 ; w<Width  ; ++w)
            Array[h][w] = 0.0;
    }

    ////////////////////
    ////////////////////
    ////////////////////

    // delete array
    delete[] Array[0];
    delete[] Array;
    Array = NULL; // 確保正確性

    system("pause");
    return 0;
}
