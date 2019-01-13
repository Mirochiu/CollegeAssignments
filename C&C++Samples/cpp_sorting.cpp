#include <iostream>
#include <cstdlib>
#include <algorithm>
#include <vector>

using namespace std;

bool compare_by_function (int i, int j) { return (i<j); }

// functor
struct myclass {
  bool operator() (int i, int j) { return (i<j);}
} compare_by_functor;


int main(int argc, char *argv[])
{
  const int arr_len = 5;

  // demo for int array
  int arr[arr_len] = {2,4,1,8,10};
 
  cout << "orig data:";
  for (int i=0 ; i<arr_len ; ++i)
    cout << arr[i] << ", ";
  cout << endl;

  sort(arr, arr+arr_len, compare_by_function);

  cout << "sorted data:";
  for(int i=0 ; i<arr_len ; ++i)
    cout << arr[i] << ", ";
  cout << endl;
  
  
  // demo for STL container
  vector<int> arr_vec(5);
  
  for (int i=0 ; i<arr_vec.size() ; ++i)
    arr_vec[i] = rand()%100+1;

  cout << "orig data:";
  for (int i=0 ; i<arr_vec.size() ; ++i)
    cout << arr_vec[i] << ", ";
  cout << endl;

  sort(arr_vec.begin(), arr_vec.end(), compare_by_functor);
  
  cout << "sorted data:";
  for(int i=0 ; i<arr_vec.size() ; ++i)
    cout << arr_vec[i]  << ", ";
  cout << endl;
 
  system("PAUSE");
  return EXIT_SUCCESS;
}

