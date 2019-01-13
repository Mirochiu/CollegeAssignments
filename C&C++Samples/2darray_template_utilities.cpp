#include <iostream>
#include <cstdlib> // atoi

using namespace std;

template<typename T>
T** new2d(int Height, int Width)
{
    T** Array = new T*[Height];
    Array[0] = new T[Height*Width];

    for (int h=0 ; h<Height ; ++h)
    {
        if (h>0) Array[h] = Array[h-1] + Width;

        for (int w=0 ; w<Width ; ++w)
            Array[h][w] = 0.0;
    }

    return Array;
}

template<typename T>
void delete2d(T**& Array)
{
    delete[] Array[0];
    delete[] Array;
    Array = NULL; // 確保正確性
}

int main(int argc, char **argv)
{
    int Height = argc>1?atoi(argv[1]):240;
    int Width  = argc>2?atoi(argv[2]):360;

    // build array
    double** Array = new2d<double>(Height,Width);

    // delete array
    delete2d(Array);

    system("pause");
    return 0;
}
