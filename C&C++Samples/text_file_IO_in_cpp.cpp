#include <iostream>
#include <fstream>

using namespace std;

int main()
{
    int i;
    float f;
    ifstream in;
    ofstream out;
    
    out.open("myfile.txt");
    
    if (!out)
    {
       printf("output file error!\n");
    }
    else
    {             
        for (i=0 ; i<100 ; ++i)
        {
           if ((i+1) % 10)
             out << i << " " << i/100.0 << " ";
           else
             out << endl; // add new line
        }
        
        out.flush();
        out.close();
    }
    
    // read
    in.open("myfile.txt");
    
    if (!in)
    {
       printf("input file error!\n");
    }
    else
    {    
        for (i=0 ; i<100 ; ++i)
        {
           in >> i >> f; 
           
           // print the read data
           cout << i << ", " << f << endl;            
        }
        in.close();
    }
    
    system("pause");
    return 0;
}
