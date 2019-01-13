#include <stdio.h>
#include <stdlib.h>

typedef struct{
  float x,y,z;
} fPoint;

typedef struct{
  float x,y,z;
} vPoint;

int main()
{
    int i;
    float f;
    int data;
    FILE *in, *out;

    int reval;
    float fx, fy, fz;

    char* p_chr;
    char line_str[1024] = {0};
    char c;

    fPoint fPos[1000];
    vPoint vPos[1000];
    int fidx=0, vidx=0;

    // read - two pass strategy
    in = fopen("myfile.obj.txt","r");

    //1-pass for length of data
    if (in == NULL)
    {
       printf("input file error!\n");
    }
    else
    {
      do
      {
        if (fscanf(in, "%[^\n]", line_str) > 0)
        {
          // process line data

          // # is the comment, ignore any char after the '#' sign
          p_chr = line_str;
          while(*p_chr!='\0' && *p_chr!='#')
            p_chr++;
          *p_chr = '\0';

          reval = sscanf(line_str, "%[vf] %f %f %f", &c, &fx, &fy, &fz);
          if (reval < 4)
          {
            if (reval>=0)
              printf("we cannot process the data=\"%s\"\n", line_str);
          }
          else
          {
            if (c=='v')
            {
              vPoint p;
              p.x = fx;
              p.y = fy;
              p.z = fz;
              vPos[vidx++] = p;
              printf("v data (x,y,z)=(%.3f,%.3f,%.3f)\n", fx, fy, fz);
            }
            else if (c=='f')
            {
              fPoint p;
              p.x = fx;
              p.y = fy;
              p.z = fz;
              fPos[fidx++] = p;
              printf("f data (x,y,z)=(%.3f,%.3f,%.3f)\n", fx, fy, fz);
            }

          }
        }
      }
      while(fscanf(in, "%*c")!=EOF);

      fclose(in);
    }

    system("pause");
    return 0;
}
