#include <stdio.h>

int main()
{
    int i;
    float f;
    FILE *in, *out;
    
    out = fopen("myfile.txt", "w");
    if (out == NULL)
    {
       printf("output file error!\n");
    }
    else
    {             
        for (i=0 ; i<100 ; ++i)
        {
          if ((i+1) % 10)
             fprintf(out, "%d %f ", i , i/100.0);
          else
             fprintf(out, "\n"); // add new line
        }
        
        fflush(out);
        fclose(out);
    }
    
    // read
    in = fopen("myfile.txt","r");
    
    if (in == NULL)
    {
       printf("input file error!\n");
    }
    else
    {    
        for (i=0 ; i<100 ; ++i)
        {
            fscanf(in, "%d", &i);
            fscanf(in, "%f", &f);
            
            // print the read data
            printf("%d, %f\n", i, f);
        }
        fclose(in);
    }
    
    system("pause");
    return 0;
}
