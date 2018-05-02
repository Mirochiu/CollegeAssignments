//-----------------------------------------------------------------------------
//	ECB (Electronic Codebook) header file
//	���g�G���ò�
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
	// �t�d�ɮץ[�K�������C
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
			// �i��[�K
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
	// �t�d�ѱK�������A�P�[�K���t���b��Akey�P�[�ѱK�禡�����ˤϡC
	else if ( DECRYPT == crypt ) {
        unsigned long total;
		// ��Ū�J�ɮת��סC
		total = filesize(istm) - 1;
		printf("Decrypt File LEN: %ld bytes\n",total);
		for ( cnt=0 ; cnt<total ;  ){
			if ( (len=fread (data, 1, 8, istm)) <= 0 ){
				if (len != 0) fputs("ECB():Error Occur. FILE LEN:0",stderr);
				break;
			}
			// �i��[�K
			if ( mode == _DES )
				cryp_data = ECB_1key_de(uchar8Tobit64(data), k[0]);
			else if ( mode == _3DES_2KEY )
				cryp_data = ECB_2key_de(uchar8Tobit64(data), k[0], k[1]);
			else if ( mode == _3DES_3KEY )
				cryp_data = ECB_3key_de(uchar8Tobit64(data), k[0], k[1], k[2]);
			else
				fputs("ECB():Error Occur.",stderr);
			// �Ycnt�ܦ��t�ơA�h�N��쥻��ƪ��������O���Y��
			// �ҥH�u�g�J���ɮת��ת���Ƴ����A��L��ƫh�O�@�o�C
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
		;// �קK�����n�� compile���~
	fclose(ostm);
	fclose(istm);
}
#endif
