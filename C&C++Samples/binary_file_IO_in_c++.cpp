#include <iostream>
#include <cstdlib>
#include <fstream>
#include <ctime> // for time

using namespace std;

int main()
{
  const int DATA_MAX_LENGTH = 100;
  int  Data[DATA_MAX_LENGTH];
  
  srand(time(NULL));

  // write
  ofstream out;  
  out.open("myfile.bin", ios::binary);
  if (!out)
  {
    cerr << "output file error!" << endl;
  }
  else
  {
    for (int i=0 ; i<DATA_MAX_LENGTH ; ++i)
      Data[i] = rand()%256;

    out.write((char*)(&Data[0]), sizeof(Data[0])*DATA_MAX_LENGTH);

    out.flush(); // before you close the output file, please flush it!
    out.close();
  }
    
  // read
  ifstream in;
  in.open("myfile.bin", ios::binary);
  if (!in)
  {
    cerr << "input file error!" << endl;
  }
  else
  {
    in.seekg(0, ios_base::end);
    int filesize = in.tellg();
    in.seekg(0, ios_base::beg);
    int datanum = filesize/sizeof(Data[0]);
  
    cout << "File size=" << filesize << endl;
    cout << "Number of data=" << datanum << endl;

    if (datanum != DATA_MAX_LENGTH)
    {
      cerr << "Number of data not match!" << endl;
    }
    else
    {     
      in.read((char*)(&Data[0]), sizeof(Data[0])*DATA_MAX_LENGTH);
    
      // if your data type is 'char' or 'BYTE', 
      // the cout should use 'cout<<(int)Data[i] << ' 
      // for output numbers.
      for (int i=0 ; i<DATA_MAX_LENGTH ; ++i)
        cout << Data[i] << endl;
    }

    in.close();
  }
  
  system("pause");
  return 0;
}