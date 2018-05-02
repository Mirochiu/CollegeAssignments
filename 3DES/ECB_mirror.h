//-----------------------------------------------------------------------------
//	ECB (Electronic Codebook) header file
//	撰寫：邱永祥
//-----------------------------------------------------------------------------
#ifndef ECB_MIRROR_H
#define ECB_MIRROR_H

#include "cooperate.h"

//-----------------------------------------------------------------------------
//	ECB functions
//-----------------------------------------------------------------------------
// ENCRYPT
#define ECB_1key_en(data,key) des_encry(data,key)
#define ECB_2key_en(data,k1,k2) des_encry(des_decry(des_encry(data,k1),k2),k1)
#define ECB_3key_en(data,k1,k2,k3) des_encry(des_decry(des_encry(data,k1),k2),k3)
// DECRYPT
#define ECB_1key_de(data,key) des_decry(data,key)
#define ECB_2key_de(data,k1,k2) des_decry(des_encry(des_decry(data,k1),k2),k1)
#define ECB_3key_de(data,k1,k2,k3) des_decry(des_encry(des_decry(data,k3),k2),k1)

//-----------------------------------------------------------------------------
//	ECB (Electronic Codebook)
//-----------------------------------------------------------------------------
void ECB ( int crypt, int mode, key48perbit **k ) {
	int len;	// data length
	unsigned char finlen;
	unsigned char data[8];
	dat64perbit cryp_data;
	unsigned long cnt = 0;
	// 負責檔案加密的部份。
	if ( ENCRYPT == crypt ) {
		while (1) {
			len = fread (data,1,8,istm);
			if ( len <= 0 ) {
				if ( len==0 ) {
					finlen = 8;
					fwrite(&finlen,1,1,ostm);
				} else {
					fputs("ECB():Error Occur.",stderr);
				}
				break;
			}
			// 進行加密
			if ( mode == _DES )
				cryp_data = ECB_1key_en(uchar8Tobit64(data), k[0]);
			else if ( mode == _3DES_2KEY )
				cryp_data = ECB_2key_en(uchar8Tobit64(data), k[0], k[1]);
			else if ( mode == _3DES_3KEY )
				cryp_data = ECB_3key_en(uchar8Tobit64(data), k[0], k[1], k[2]);
			else
				fputs("ECB():Error Occur",stderr);
			fwrite (bit64Touchar8(cryp_data, data),1,8,ostm);
			if ( len < 8) {
				finlen = len;
				fwrite(&finlen,1,1,ostm);
				break;
			}
		}
	}
	// 負責解密的部份，與加密的差異在於，key與加解密函式的的倒反。
	else if ( DECRYPT == crypt ) {
        unsigned long total;
		// 先讀入檔案長度。
		total = filesize(istm) - 1;
		printf("Decrypt File LEN: %ld bytes\n",total);
		for ( cnt=0 ; cnt<total ;  ){
			if ( (len=fread (data, 1, 8, istm)) <= 0 ){
				if (len != 0) fputs("ECB():Error Occur. FILE LEN:0",stderr);
				break;
			}
			// 進行加密
			if ( mode == _DES )
				cryp_data = ECB_1key_de(uchar8Tobit64(data), k[0]);
			else if ( mode == _3DES_2KEY )
				cryp_data = ECB_2key_de(uchar8Tobit64(data), k[0], k[1]);
			else if ( mode == _3DES_3KEY )
				cryp_data = ECB_3key_de(uchar8Tobit64(data), k[0], k[1], k[2]);
			else
				fputs("ECB():Error Occur.",stderr);
			// 若cnt變成負數，則代表原本資料的結尾不是齊頭的
			// 所以只寫入原檔案長度的資料部分，其他資料則是作廢。
			cnt+=8;
			if ( total-cnt )
				fwrite (bit64Touchar8(cryp_data, data),1,8,ostm);
			else {
				fread(&finlen,1,1,istm);
				len = finlen;
				fwrite (bit64Touchar8(cryp_data, data),1,len,ostm);
			}
		}
	}
	else
		;// 避免不必要的 compile錯誤
	fclose(ostm);
	fclose(istm);
}
#endif
