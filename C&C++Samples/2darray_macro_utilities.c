#include <stdio.h>
#include <stdlib.h> // atoi

#define malloc2d(Array, Height, Width) { \
  int h=0,w=0; \
  Array = malloc(sizeof(**Array)*Height); \
  Array[0] = malloc(sizeof(*Array)*Height*Width); \
  while(++h<Height) \
    Array[h] = Array[h-1] + Width; \
}

#define free2d(Array) { \
  free(Array[0]); \
  free(Array); \
  Array=NULL; \
}

int main(int argc, char **argv)
{
  int Height = argc>1?atoi(argv[1]):240;
  int Width  = argc>2?atoi(argv[2]):360;

  double** Array;

  // build array
  malloc2d(Array,Height,Width);

  // delete array
  free2d(Array);

  system("pause");
  return 0;
}
