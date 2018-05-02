#ifndef CBC_KIWE_H
#define CBC_KIWE_H

#include "cooperate.h"

//------------------------------------------------------------------------------
//	CBC
//------------------------------------------------------------------------------
void CBC (int crypt, int mode, key48perbit *key[] )
{
	int i,x,y;
	char time = 0;				/* �P�O�[�K�j��B�@�O�_���Ĥ@�� */
	char dat[8];				/* �Ѹ�ƬyŪ������� */
	char flab;					/* �h�l�줸�ռƥ� */
	dat64perbit ciphertext;		/* IV ����ഫ���c��s�� */
	dat64perbit new_dat_64;		/* �Ѹ�ƬyŪ��������ഫ���c�ӱo */
	dat64perbit old_dat_64;		/* ���� new_dat_64 �[�K���G */

	if(crypt == ENCRYPT)
	{
		/* ���o�ɮת��� */
		long len = filesize(istm);
		/* �p���ɮץ[�K�ɷ|�h�g�X�� Byte */
		ltoa (len%8, &flab, 10);
		if( flab != '0' ) flab= '8' - flab + '0';

		puts("CBC Encoding ...");
		while(len)
		{
			/* Ū���ɮ� */
			len -= fread(dat, 1, 8, istm); 

			/* �NŪ���쪺��Ƨ@���c�ഫ�A�i�� XOR �B�� */
			new_dat_64 = uchar8Tobit64(dat);
			if ( time != 0 )				// ���O�Ĥ@��, �W���[�K���G�P���尵
				new_dat_64 = xor64bits( new_dat_64, old_dat_64 );
			else {							// �Ĥ@��, ciphertext �s�J IV , �P���尵 XOR
				for( y=0; y<8; y++ )
					for ( x=0; x<8; x++ )
						ciphertext.bit[y][x]=IV64perbit[y][x];
				new_dat_64 = xor64bits( new_dat_64, ciphertext );
				time++;
			}

			/* one key, two key, three key �T�إ[�K�Ҧ� */
			if( mode == _DES )
				new_dat_64 = des_encry( new_dat_64, key[0] );
			else if ( mode == _3DES_2KEY ) {
				new_dat_64 = des_encry( new_dat_64, key[0] );
				new_dat_64 = des_decry( new_dat_64, key[1] );
				new_dat_64 = des_encry( new_dat_64, key[0] );
			}
			else if ( mode == _3DES_3KEY ) {
				new_dat_64 = des_encry( new_dat_64, key[0] );
				new_dat_64 = des_decry( new_dat_64, key[1] );
				new_dat_64 = des_encry( new_dat_64, key[2] );
			}

			/* �������G���U�@��XOR�� */
			old_dat_64 = new_dat_64;

			/* �N�[�K������Ƨ@���c�ഫ�g���ɮ� */
			bit64Touchar8(new_dat_64, dat);
			fwrite(dat, 1, 8, ostm);
		}
		/* �N�h�l�� Byte �ƥؼg�J�ɧ� */
		fputc( flab , ostm);
		puts("Encoding completed.");
	}
	
	else if(crypt == DECRYPT)
	{
		dat64perbit tmp_dat_64;			/* ����Ū�����G���U���j��B�@�ϥ� */
		long len = filesize(istm)-1;	/* �ɮת��סA�����@�Ӧs��h�l�r���ƪ��줸�� */

		puts("CBC Decoding ...");
		while(len)
		{
			/* Ū����ơA�ç@���c�ഫ */
			len -= fread(dat, 1, 8, istm);
			new_dat_64 = uchar8Tobit64(dat);
			tmp_dat_64 = new_dat_64;			// ����Ū�����G
			
			/* one key, two key, three key �T�إ[�K�Ҧ� */
			if( mode == _DES )
				new_dat_64 = des_decry( new_dat_64, key[0] );
			else if ( mode == _3DES_2KEY ) {
				new_dat_64 = des_decry( new_dat_64, key[0] );
				new_dat_64 = des_encry( new_dat_64, key[1] );
				new_dat_64 = des_decry( new_dat_64, key[0] );
			}
			else if ( mode == _3DES_3KEY ) {
				new_dat_64 = des_decry( new_dat_64, key[2] );
				new_dat_64 = des_encry( new_dat_64, key[1] );
				new_dat_64 = des_decry( new_dat_64, key[0] );
			}
			
			/* XOR �B�� */
			if ( time != 0 )			// ���O�Ĥ@���A�ѱK�L������P�W���j�骺�K�尵 XOR
				new_dat_64 = xor64bits( new_dat_64, old_dat_64 );
			else {						// �Ĥ@���A�N IV �s�� ciphertext�A�P�K�尵 XOR
				for( y=0; y<8; y++ )
					for ( x=0; x<8; x++ )
						ciphertext.bit[y][x] = IV64perbit[y][x];
				new_dat_64 = xor64bits( new_dat_64, ciphertext );
				time++;
			}
			/* �x�sŪ�����G���U�@�� XOR �ϥ� */
			old_dat_64 = tmp_dat_64;

			/* �N�ѱK������Ƨ@���c�ഫ�g���ɮ� */
			bit64Touchar8(new_dat_64, dat);
			if ( len > 0 ) fwrite(dat, 1, 8, ostm);
			else { 
				flab = fgetc(istm);					// ���o�ɧ����h�l�r���ƥ�
				fwrite(dat, 1, 8-(flab-'0'), ostm);	// �קK��X�h�l�r��
			} 
		}
	}
}
#endif
