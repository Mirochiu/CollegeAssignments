#include <stdio.h>
#include <stdlib.h>

#define PLEN ((offset)+(length))

long filelength(FILE*);

int main(int argc, char *argv[])
{
	int i, nl, ln, tmp, len;
	char* filename;
	int offset, length;
	FILE* fp;
	unsigned char c,ch[16];
	for(i=0; i<16; i++)
		ch[i]=255;
	if(argc==1)
	{
		puts("dumpx can print the file in hex format.\n");
		puts("dumpx [-f filename [-o offset -l length]]\n");
		puts("\t-f filename\tThe name of file.");
		puts("\t-o offset\tOffset.");
		puts("\t-l length\tLength.\n");
		return 0;
	}
	else if(argc==3)
	{
		if(argv[1][1]=='f')
		{
			filename=argv[2];
			fp = fopen(filename,"r");
			offset=0;
			length=filelength(fp);
		}
		else return 1;
	}
	else if(argc==7)
	{
		for(i=1; i<7; i+=2)
			switch(argv[i][1])
			{
			case 'f': filename=argv[i+1]; break;
			case 'o': offset=atoi(argv[i+1]); break;
			case 'l': length=atoi(argv[i+1]); break;
			}
		fp = fopen(filename,"r");
	}
	else
	{
		fprintf(stderr, "dumpx : warning arguments.");
		return 1;
	}
/*	printf("f=%s,o=%d,l=%d\n", filename, offset, length);*/
	if(!fp)
	{
		fprintf(stderr, "dumpx : file is not exist.");
		return 1;
	}
	if((PLEN)>filelength(fp))
	{
		fprintf(stderr, "dumpx : out of range.");
		return 1;
	}
	fseek(fp, offset, SEEK_SET);
	printf("File name: %s, File size: %d\nOffset: %d, print length: %d\n",
		filename, filelength(fp), offset, length);
	printf("--offset-- ");
	for(i=0x0; i<=0xF; i++)
		printf("%02X ", i);
	printf("  ");
	for(i=0x0; i<=0xF; i++)
		printf("%X", i);
	nl=16;
	ln=(offset/16)*0x10;
	tmp=offset%16;
	len=length+16-((PLEN%16==0)?16:(PLEN%16));
	for(i=0; i<len; i++)
	{
		if(nl==16)
		{
			printf("\n%08Xh: ", ln);
			nl=tmp, ln+=0x10;
		}
		for(; tmp>0; tmp--)
			printf("   ");
		if(i<length)
		{
			c = fgetc(fp);
			printf("%02X ", c);
		}
		else
		{
			c = ' ';
			printf("   ");
		}
		ch[nl++] = c;
		if(nl==16)
		{
			printf("; ");
			for(nl=0; nl<16; nl++)
				if(ch[nl]>126 || ch[nl]<20)
					putchar(' ');
				else
					putchar(ch[nl]);
		}
	}
	puts("");
	fclose(fp);
	return 0;
}

long filelength(FILE* fp)
{
	long curpos, length;
	curpos = ftell(fp);
	fseek(fp, 0, SEEK_END);
	length = ftell(fp);
	fseek(fp, curpos, SEEK_SET);
	return length;
}
