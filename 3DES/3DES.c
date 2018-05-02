#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "cooperate.h"
#include "ECB_mirror.h"
#include "CTR_wesley.h"
#include "CBC_kiwe.h"
#include "CFB8_OFB8_byemark.h"

//------------------------------------------------------------------------------
//	主程式
//------------------------------------------------------------------------------
int main (int argc,char** argv) {
	char fname[128];
	key48perbit **keys;	// 2維陣列，內容是[3][16]
	unsigned char key[9];
	unsigned char key0[9] = "qwertyui";
	unsigned char key1[9] = "12345678";
	unsigned char key2[9] = "asdfghjk";

	int ende, crypt, mode, block;

	puts(" 33333       DDDD  EEEEE  SSSS");
	puts("     3       D   D E     S    ");
	puts(" 33333 ----- D   D EEEEE  SSS ");
	puts("     3       D   D E         S");
	puts(" 33333       DDDD  EEEEE SSSS ");
	
	if (argc == 1){
		do { printf("Input filename: ");
		} while(scanf("%s",fname) == -1);
		istm = fopen(fname,"rb");
		if (istm == NULL ) {
			fprintf(stderr,"Cannot read input file.\n");
			exit(-1);
		}
		strcat(fname,".des");
		ostm = fopen(fname,"wb");
		if (ostm == NULL) {
			fprintf(stderr,"Cannot write output file.\n");
			exit(-1);			
		}/*
		do { puts("Input key(1~8 char):");
		} while (scanf("%s",key) == -1);*/
	} else {
		/** 事前為有參數的留下的 **/
		exit(-1);
	}
	puts("generate DES keys...");
	keys = (key48perbit**)malloc(sizeof(key48perbit*)*3);	// 先配置一維空間
	keys[0] = KeyGenerator(key0,leftrotate);
	keys[1] = KeyGenerator(key1,leftrotate);
	keys[2] = KeyGenerator(key2,leftrotate);

	do { printf("1. Encrypt\n2. Decrypt\n/> "); }
	while (scanf("%d",&ende) == -1);
	do { printf("1. ECB\n2. CBC\n3. CTR\n4. CFB\n5. OFB\n/> "); }
	while (scanf("%d",&block) == -1);
	do { printf("1. DES\n2. 3DES with 2KEY\n3. 3DES with 3KEY\n/> "); }
	while (scanf("%d",&mode) == -1);

	if (ende==1) crypt = ENCRYPT;
	else if (ende==2) crypt = DECRYPT;

	switch(block) {
		case 1 :
			ECB(crypt, mode, keys);
			break;
		case 2 :
			CBC(crypt, mode, keys);
			break;
		case 3 :
			CTR(crypt, mode, keys);
			break;
		case 4 :
			CFB(crypt, mode, keys);
			break;
		case 5 :
			OFB(crypt, mode, keys);
			break;		
		default :
			puts("do nothing.");
			break;
	}
	system("pause");
	return 0;
}
