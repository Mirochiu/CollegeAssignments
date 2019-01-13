#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main()
{
  int i;
  FILE *in, *out;
  const int DATA_MAX_LENGTH = 100;
  int  Data[DATA_MAX_LENGTH];

  srand(time(NULL));

  out = fopen("myfile.bin", "wb");
  if (out == NULL)
  {
    fprintf(stderr, "output file error!\n");
  }
  else
  {
    for (i=0 ; i<DATA_MAX_LENGTH ; ++i)
      Data[i] = rand()%256;

    fwrite(Data, sizeof(Data[0]), DATA_MAX_LENGTH, out);

    fflush(out); // before you close the output file, please flush it!
    fclose(out);
  }

  // read
  in = fopen("myfile.bin","rb");
  if (in == NULL)
  {
    fprintf(stderr, "input file error!\n");
  }
  else
  {
    int filesize, datanum;

    fseek(in, 0, SEEK_END);
    filesize = ftell(in);
    fseek(in, 0, SEEK_SET);
    datanum = (filesize)/sizeof(Data[0]);

    printf("File size=%d\n", filesize);
    printf("Number of data=%d\n", datanum);

    if (datanum != DATA_MAX_LENGTH)
    {
      fprintf(stderr, "Number of data not match!\n");
    }
    else
    {
      fread(Data, sizeof(Data[0]), DATA_MAX_LENGTH, in);

      // if your data type is 'char' or 'BYTE',
      // the cout should use 'printf("%d\n", (int)Data[i]);'
      // for output numbers.
      for (i=0 ; i<DATA_MAX_LENGTH ; ++i)
        printf("%d\n", Data[i]);
    }

    fclose(in);
  }

  system("pause");
  return 0;
}
