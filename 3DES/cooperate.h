#ifndef COOPERAT_H
#define COOPERAT_H

#include "des.h"

#define ENCRYPT 0
#define DECRYPT 1
#define _DES 1
#define _3DES_2KEY 2
#define _3DES_3KEY 3

FILE *istm, *ostm;

void ECB(int crypt, int mode, key48perbit **key);
void CBC(int crypt, int mode, key48perbit **key);
void CTR(int crypt, int mode, key48perbit **key);
void CFB(int crypt, int mode, key48perbit **key);
void OFB(int crypt, int mode, key48perbit **key);

//------------------------------------------------------------------------------
//	計算檔案長度 
//------------------------------------------------------------------------------
unsigned long filesize (FILE *stream) {
	long curpos, length;
	curpos = ftell(stream);
	fseek(stream, 0L, SEEK_END);
	length = ftell(stream);
	fseek(stream, curpos, SEEK_SET);
	return length;
}



#endif
