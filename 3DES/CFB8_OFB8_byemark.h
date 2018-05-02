#ifndef CFB8_OFB8_BYEMARK_H
#define CFB8_OFB8_BYEMARK_H

#include "cooperate.h"

//------------------------------------------------------------------------------
// 將CFB與OFB的Feedback加到vector裡 
//------------------------------------------------------------------------------
void append8toIV(dat64perbit nextIV64, unsigned char dat)
{
	int i,j;
	for(i=7,j=7;i>=0&&j>=1;i--){
		nextIV64.bit[j][i]=nextIV64.bit[j-1][i];
		if(i==0){
			j--;
			continue;
		}
	}
	for(i=0;i<8;i++){
		nextIV64.bit[0][i]=(dat>>i & 0x01);
	}
}
//------------------------------------------------------------------------------
//	選擇key與vector的其中一部分for CFB(8) OFB(8) 
//------------------------------------------------------------------------------
unsigned char bit64Touchar(dat64perbit dat)
{
	int i;
	unsigned char reval=0;
	for ( i=0 ; i<8 ; i++ )
		reval |= (dat.bit[7][i]<<i);
	return reval;
}

//------------------------------------------------------------------------------
//	CFB
//------------------------------------------------------------------------------
void CFB(int crypt, int mode, key48perbit *key[])
{
	int i,j,flag=1 ,len,read;
	unsigned char plaintext=0,ciphertextfb=0;
	dat64perbit nextIV64_0,nextIV64_1,nextIV64_2,key0,key1,key2;
	len = filesize(istm);
	for(i=0,j=0;i<8&&j<8;i++){
		nextIV64_0.bit[j][i]=IV64perbit[j][i];
		nextIV64_1.bit[j][i]=IV64perbit[j][i];
		nextIV64_2.bit[j][i]=IV64perbit[j][i];
		if(i==7){
			j++;
			continue;
		}
	}
	if(crypt==ENCRYPT) {
		puts("CFB Encoding ...");
		while(flag){
			plaintext=0;
			read=fread(&plaintext,1,1,istm);
			len-=read;
			if ( len <= 0 )	flag = 0;
			if(mode==_DES) {
				key0 = des_encry(nextIV64_0, key[0]);
				ciphertextfb = plaintext ^ bit64Touchar(key0);
				append8toIV(nextIV64_0, ciphertextfb);
			} else if(mode==_3DES_2KEY) {
				key0 = des_encry(nextIV64_0, key[0]);
				ciphertextfb = plaintext ^ bit64Touchar(key0);
				append8toIV(nextIV64_0, ciphertextfb);
				key1 = des_encry(nextIV64_1, key[1]);
				plaintext = ciphertextfb ^ bit64Touchar(key1);
				append8toIV(nextIV64_1, ciphertextfb);
				key0 = des_encry(nextIV64_2, key[0]);
				ciphertextfb = plaintext ^ bit64Touchar(key0);
				append8toIV(nextIV64_2, ciphertextfb);
			} else if(mode==_3DES_3KEY) {
				key0 = des_encry(nextIV64_0, key[0]);
				ciphertextfb = plaintext ^ bit64Touchar(key0);
				append8toIV(nextIV64_0, ciphertextfb);
				key1 = des_encry(nextIV64_1, key[1]);
				plaintext = ciphertextfb ^ bit64Touchar(key1);
				append8toIV(nextIV64_1, ciphertextfb);
				key2 = des_encry(nextIV64_2, key[2]);
				ciphertextfb = plaintext ^ bit64Touchar(key2);
				append8toIV(nextIV64_2, ciphertextfb);
			} else {}
			fwrite(&ciphertextfb,1,1,ostm);
		}
		puts("Encoding completed.");
	} else if(crypt==DECRYPT) {
		puts("CFB Decoding ...");
		while(flag){
			plaintext=0;
			read=fread(&ciphertextfb,1,1,istm);
			len-=read;
			if ( len <= 0 )	flag = 0;
			if(mode==_DES) {
				key0 = des_encry(nextIV64_0, key[0]);
				plaintext = ciphertextfb ^ bit64Touchar(key0);
				append8toIV(nextIV64_0, ciphertextfb);
			} else if(mode==_3DES_2KEY) {
				key0 = des_encry(nextIV64_0, key[0]);
				plaintext = ciphertextfb ^ bit64Touchar(key0);
				append8toIV(nextIV64_0, ciphertextfb);
				key1 = des_encry(nextIV64_1, key[1]);
				ciphertextfb = plaintext ^ bit64Touchar(key1);
				append8toIV(nextIV64_1, ciphertextfb);
				key0 = des_encry(nextIV64_2, key[0]);
				plaintext = ciphertextfb ^ bit64Touchar(key0);
				append8toIV(nextIV64_2, ciphertextfb);
			} else if(mode==_3DES_3KEY) {
				key2 = des_encry(nextIV64_2, key[2]);
				plaintext = ciphertextfb ^ bit64Touchar(key2);
				append8toIV(nextIV64_2, ciphertextfb);
				key1 = des_encry(nextIV64_1, key[1]);
				ciphertextfb = plaintext ^ bit64Touchar(key1);
				append8toIV(nextIV64_1, ciphertextfb);
				key0 = des_encry(nextIV64_0, key[0]);
				plaintext = ciphertextfb ^ bit64Touchar(key0);
				append8toIV(nextIV64_0, ciphertextfb);
			} else {}
			fwrite(&plaintext,1,1,ostm);
		}
		puts("Decoding completed.");
	} else;
}

//------------------------------------------------------------------------------
//	OFB
//------------------------------------------------------------------------------
void OFB(int crypt, int mode, key48perbit *key[])
{
	int i,j,flag=1,len,read;
	unsigned char plaintext=0,ciphertextfb=0;
	dat64perbit nextIV64_0,nextIV64_1,nextIV64_2,key0,key1,key2;
	len = filesize(istm);
	for(i=0,j=0;i<8&&j<8;i++){
		nextIV64_0.bit[j][i]=IV64perbit[j][i];
		nextIV64_1.bit[j][i]=IV64perbit[j][i];
		nextIV64_2.bit[j][i]=IV64perbit[j][i];
		if(i==7){
			j++;
			continue;
		}
	}
	if(crypt==ENCRYPT) {
		puts("OFB Encoding ...");
		while(flag){
			plaintext=0;
			read=fread(&plaintext,1,1,istm);
			len-=read;
			if ( len <= 0 )	flag = 0;
			if(mode==_DES) {
				key0 = des_encry(nextIV64_0, key[0]);
				append8toIV(nextIV64_0, bit64Touchar(key0));
				ciphertextfb = plaintext ^ bit64Touchar(key0);
			} else if(mode==_3DES_2KEY) {
				key0 = des_encry(nextIV64_0, key[0]);
				append8toIV(nextIV64_0, bit64Touchar(key0));
				ciphertextfb = plaintext ^ bit64Touchar(key0);
				key1 = des_encry(nextIV64_1, key[1]);
				append8toIV(nextIV64_1, bit64Touchar(key1));
				plaintext = ciphertextfb ^ bit64Touchar(key1);
				key0 = des_encry(nextIV64_0, key[0]);
				append8toIV(nextIV64_0, bit64Touchar(key0));
				ciphertextfb = plaintext ^ bit64Touchar(key0);
			} else if(mode==_3DES_3KEY) {
				key0 = des_encry(nextIV64_0, key[0]);
				append8toIV(nextIV64_0, bit64Touchar(key0));
				ciphertextfb = plaintext ^ bit64Touchar(key0);
				key1 = des_encry(nextIV64_1, key[1]);
				append8toIV(nextIV64_1, bit64Touchar(key1));
				plaintext = ciphertextfb ^ bit64Touchar(key1);
				key2 = des_encry(nextIV64_2, key[2]);
				append8toIV(nextIV64_2, bit64Touchar(key2));
				ciphertextfb = plaintext ^ bit64Touchar(key2);
			} else {}
			fwrite(&ciphertextfb,1,1,ostm);
		}
		puts("Encoding completed.");
	} else if(crypt==DECRYPT) {
		puts("OFB Decoding ...");
		while(flag){
			plaintext=0;
			read=fread(&ciphertextfb,1,1,istm);
			len-=read;
			if ( len <= 0 )	flag = 0;
			if(mode==_DES) {
				key0 = des_encry(nextIV64_0, key[0]);
				append8toIV(nextIV64_0, bit64Touchar(key0));
				plaintext = ciphertextfb ^ bit64Touchar(key0);
			} else if(mode==_3DES_2KEY) {
				key0 = des_encry(nextIV64_0, key[0]);
				append8toIV(nextIV64_0, bit64Touchar(key0));
				plaintext = ciphertextfb ^ bit64Touchar(key0);
				key1 = des_encry(nextIV64_1, key[1]);
				append8toIV(nextIV64_1, bit64Touchar(key1));
				ciphertextfb = plaintext ^ bit64Touchar(key1);
				key0 = des_encry(nextIV64_0, key[0]);
				append8toIV(nextIV64_0, bit64Touchar(key0));
				plaintext = ciphertextfb ^ bit64Touchar(key0);
			} else if(mode==_3DES_3KEY) {
				key2 = des_encry(nextIV64_2, key[2]);
				append8toIV(nextIV64_2, bit64Touchar(key2));
				plaintext = ciphertextfb ^ bit64Touchar(key2);
				key1 = des_encry(nextIV64_1, key[1]);
				append8toIV(nextIV64_1, bit64Touchar(key1));
				ciphertextfb = plaintext ^ bit64Touchar(key1);
				key0 = des_encry(nextIV64_0, key[0]);
				append8toIV(nextIV64_0, bit64Touchar(key0));
				plaintext = ciphertextfb ^ bit64Touchar(key0);
			} else {}
			fwrite(&plaintext,1,1,ostm);
		}
		puts("Decoding completed.");
	} else;
}
#endif

