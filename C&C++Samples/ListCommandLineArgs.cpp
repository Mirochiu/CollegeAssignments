#include <iostream>
#include <cstdlib>

using namespace std;

int main(int argc, char** argv)
{
    for (int i=0 ; i<argc ; ++i)
        cout<<"["<<i<<"]"<<argv[i]<<endl;

    system("PAUSE");

    return 0;
}
