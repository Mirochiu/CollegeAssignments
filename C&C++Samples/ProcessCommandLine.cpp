#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <string>
#include <fstream>

using namespace std;

#define MAXBUFLEN  24

int main(int argc, char** argv)
{
    int    arg_i;
    double arg_d;
    float  arg_f;
    char   arg_s_c[MAXBUFLEN] = {0}; // first element is initialized by 0, not all elements are initialized by 0
    string arg_s_cpp;
    FILE* in;
    ifstream ins;

    // first arg is program name
    printf("program name is first argument='%s'\n", argv[0]);

    // actural arg is starting from 2nd 
    // get an integer argument
    if ( argc > 1 )
    {
        arg_i = atoi(argv[1]);
        cout << "Second arg=" << arg_i << endl;
        printf("Second arg=%d\n", arg_i);
    }

    // get a double argument
    if ( argc > 2 )
    {
        arg_d = atof(argv[2]);
        cout << "Third arg=" << arg_d << endl;
        printf("Third arg=%lf\n", arg_d);
    }

    // get a float
    if ( argc > 3 )
    {
        arg_f = atof(argv[3]);
        cout << "Forth arg=" << arg_f << endl;
        printf("Forth arg=%f\n", arg_f);
    }

    // get a string argument
    if ( argc > 4 )
    {
        strncpy(arg_s_c, argv[4], MAXBUFLEN); // C-style string; strncpy is more safe than strcpy
        arg_s_cpp = argv[4]; // C++ string

        // use C++ string, you should notice the .c_str() to convert it to a c-style string for IO
        printf("show C-style string by C function = '%s'\n", arg_s_c);
        cout << "show C-style string by C++ IO object = '" << arg_s_c << "'" << endl;

        in = fopen(arg_s_c,"r");
        fclose(in);

        ins.open(arg_s_c);
        ins.close();

        //////////////////

        printf("show C++ string by C function = '%s'\n", arg_s_cpp.c_str());
        cout << "show C++ string by C++ IO object = '" << arg_s_cpp << "'" << endl;

        in = fopen(arg_s_cpp.c_str(),"r");
        fclose(in); 

        ins.open(arg_s_cpp.c_str());
        ins.close();
    }

    system("PAUSE");

    return 0;
}
