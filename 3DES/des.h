//------------------------------------------------------------------------------
//	���g�H: ���ò�
//	�إߤ��: ����
//	�̫�ק�: 2006-11-22 07:52:09 
//------------------------------------------------------------------------------
//	�]�p�z���G���[�B²��
//	
// ���o�ӳ]�p�z���A�ҥH�ګ��ӽҥ��Ҥ��Ϊ��\��A�v�@�s�@�����@�ӭӨ禡�A 
// ��subkey���ͪ������~�A�j�����㪺�Ϥ��U�۪��u�@�C�����Ƶ��c���w�q�A�h 
// �O�ϥ�2���}�C�A�C��bit�N�x�s�@�Ӥ����A�ڬO�Hchar���A�@���x�s��ƪ���� 
// �A�o��@�O���F���g���w����(safety)�P�i�a��(reliability)�A�]���ڥ����X 
// �G�S�����abit���g��A�B���o�˪��{���X�A��ı�o�٭n���ӷ�����A�i����� 
// �A�ҥH�����ѷӽҥ����P��Ƥj�p�i��U�ӳ�������Ƶ��c�w�q�C�ܩ�t��k 
// �A�h�O�ϥΥH�ecoding���@�ǧޥ��A�N�ҥ����ҳ��z���y�y��Ʀ��{���X�A�j�� 
// �O�S�b�Ҽ{�{���Ĳv���覡�A�]�O�p���~��F���y²��z��r���ت��C
//------------------------------------------------------------------------------
#ifndef DES_H
#define DES_H

#include <stdlib.h>		// malloc, free

#include "des_tables.h"
#include "des_types.h"

//------------------------------------------------------------------------------
//	�Ѧ�IP����ơA�N�һݪ�bit��inewdat�AIP�O8x8���G���x�}�C
//	�A�Ω�GInitialPermutation , Inverse Initial Permutation
//	64 bits -> 64 bits
//------------------------------------------------------------------------------
dat64perbit InitialPermutation (dat64perbit dat, const int IP[8][8]) {
	dat64perbit newdat;
	int x, y, opos;
	for ( x=0 ; x <8 ; x++ )
		for ( y=0 ; y <8 ; y ++ ) {
			opos = IP[y][x]-1;
			newdat.bit[y][x] = dat.bit[opos/8][opos%8];
		}
	return newdat;
}
//------------------------------------------------------------------------------
//	�Ѧ�E����ơA�M��qdat�����X�һݪ�bit��J�s��Ƥ��C
//	half = 0�A�N�������32bit����48bits�Fhalf = 1�A�N��k���32bit����48bits
//	32=4*8 =>> 8*6=48
//	�o�Ө禡�ٲ��F�q64bits������32bits���c���A������half�ѼƱ����^���C
//------------------------------------------------------------------------------
dat48perbit ExpansionPermutation (dat64perbit dat, int half, const int E[8][6]) {
	dat48perbit newdat;
	int x, y, opos;
	for ( x=0 ; x<6 ; x++ )
		for ( y=0 ; y<8 ; y++ ) {
			opos = E[y][x] + (half?31:-1);	// ��32��bit�H��O�k��
			newdat.bit[y][x] = dat.bit[opos/8][opos%8];
				// �����k��O�̾ڭ쥻��ƪ��e,�Ӥ��O�s��Ƽe
		}
	return newdat;
}
//------------------------------------------------------------------------------
// 48bits��data�Pkey�@xor�B��
//------------------------------------------------------------------------------
//	�]�����O0 xor 0 �ҥH��Lbit���|�v�T�쵲�G
//------------------------------------------------------------------------------
dat48perbit xor48bits (dat48perbit dat, key48perbit key) {
	int i;
	for (i=0 ; i<48 ; i++ )
		dat.bit[i/6][i%6] ^= key.bit[i/8][i%8];
			// data=>(y=8,x=6), key=>(y=6,x=8)
	return dat;
}
//------------------------------------------------------------------------------
//	S-box��8��6��4�`�ǹB�@
//	48 bits -> 32 bits
//------------------------------------------------------------------------------
//	���~�ץ�: ���Ѹ�ƨS���N��ƥk���A�Ͽ�X��Ƴ��ۦP�C
//------------------------------------------------------------------------------
// * �{���X�ѻ��@�G
//		for ( i=1, col=0 ; i<5 ; i++ )
//			col |= (dat.bit[n][i]<<(4-i));	// ��������4�줸��col��
//
// col �b�@�}�l���ɭԷ|�k�s�A���k�s�Y�i��t���U����ơA�y�������`����C�o�ӳ� 
// �����{���X��ڤW�i�H�Τ@�Ӫ����l�i��N���A�u�O�ڬݨ�n�i��}�C�������[�`�A 
// �������N�g�W�j��F�A�p�G�ݤ����N�ΤU�����N�����o�I
// col = dat.bit[n][1]<<3 + dat.bit[n][1]<<2 + dat.bit[n][1]<<1 + dat.bit[n][1];
//	
// * �{���X�ѻ��G�G
//		for ( m=3 ; m>=0 ; m-- ) {
//			bit = data & 0x01;
//			newdat.bit[n/2][m] = bit;
//			data>>=1;
//		}
//
// �o�Ө禡�|�ϥΤ���j��A�O�ѩ�n���Ѹ��data�A�N��ƪ�bit�@���ѡA�ϥΥk�첾
// �����Nbit���X�ӡA�]�N�O���ڭ̪��[�Ҭݨ쪺�Ĥ@��bit�A���O�̫�@�Ө��X���A
// �ҥH�ϥΥ��j��|�y����ƭ˧ǡA���O�ڨä��O�n�˧Ǹ�ơA�ӬO�n���b�쥻��bit
// ��m�A�N�O�}�C��i�Ӥ����N���ӬO�A��ƪ���i��bit�A�ҥH�N�Q�Τ���j�骺�覡�A
// �ܶ��K���Nbit��i�}�C�̡A�ڱM�D�@�~Bit Plain�U�����Үɦ����L�C
//
//	MSB          LSB        	MSB          LSB     bit
//	+---+---+---+---+		  +---+---+---+---+   +---+
//	| 1 | 2 | 3 | 4 | mask =  | 1 | 2 | 3 | 4 | + | 4 | << bit�n�s��}�C����4
//	+---+---+---+---+		  +---+---+---+---+   +---+
//
//	��M�A�A�]�i�H�Υt�~�@�ؤ�k�G�N�O��X�A�n��bit�A�M������w���}�C������ 
// �A�ϥΦ��k���ɾ��A����O�����F����n��bit����ƨϥΡC�U���Y�O���^�骺�Ҥl�G
//	for ( m=0 ; m<4 ; m++) {
//		bit = data & (0x01<<(4-m));
//			// �N�B�n����A�Q�n��bit�W�i��AND�B��A�Y���Xbit���ʧ@
//		newdat.bit[n/2][m] = bit>>(4-m);
//			// ���O�o��bit�ٻݭn����LSB����m�A
//          // �~��s��bit����ح��A���M��ƴN����F�C
//	}
//------------------------------------------------------------------------------
dat32perbit sbox (dat48perbit dat, const int S[8][4][16]) {
	dat32perbit	newdat;
	int n, i, m, data, row, col;
	char bit;
	// 8��6��4
	for ( n=0 ; n<8 ; n++ ) {
		// ����Ƨ@index
		row = ((dat.bit[n][0]<<1) | dat.bit[n][5]);	// ���e��2bit
		for ( i=1, col=0 ; i<5 ; i++ )
			col |= (dat.bit[n][i]<<(4-i));	// ��������4�줸��col��
		// �N��Ƴv�@���Ѧs��s��Ƥ�
		data = S[n][row][col];
		for ( m=3 ; m>=0 ; m-- ) {
			bit = data & 0x01;
			newdat.bit[n/2][m] = bit;
			data>>=1;
		}
	 // �@�ĤG��, �ѩ�48bit����Ƽe��=6,�H�o�Ӹ�Ƭ��̾ڰ��X�Ӫ����G��Ƽe��=4
	// �Ӧ^�Ǹ�ƪ�32bit��Ƽe��=8, ���L�Φ^��S�Q��i�H�������k�A
	//	�ҥH�N�N�{���X�g�F����A�i�����ഫ��ä��O�s��e�᳡���C
		n++;
		row = ((dat.bit[n][0]<<1) | dat.bit[n][5]);
		for ( i=1, col=0 ; i<5 ; i++ )
			col |= (dat.bit[n][i]<<(4-i));
		data = S[n][row][col];
		for ( m=7 ; m>=4 ; m-- ) {
			bit = data & 0x01;
			newdat.bit[n/2][m] = bit;
			data>>=1;
		}
	}
	return newdat;
}
//------------------------------------------------------------------------------
//	�ϥ�P�Ҵ��Ѫ��s��Ʃһݭn��bit�s���A�M��qdat���Nbit���X�ө�J�s��Ƥ��C
//	32 bits -> 32 bits
//------------------------------------------------------------------------------
dat32perbit Permutation (dat32perbit dat, const int P[4][8]) {
	dat32perbit newdat;
	int x,y,pos;
	for ( x=0 ; x<8 ; x++ )
		for ( y=0 ; y<4 ; y++ ) {
			pos = P[y][x] - 1;
			newdat.bit[y][x] = dat.bit[pos/8][pos%8];
		}
	return newdat;
}
//------------------------------------------------------------------------------
// 32bits��xor�G���B��
//------------------------------------------------------------------------------
dat32perbit xor32bits (dat32perbit dat, dat32perbit ldat) {
	int i;
	for ( i=0 ; i<32 ; i++ )
		dat.bit[i/8][i%8] ^= ldat.bit[i/8][i%8];
	return dat;
}
//------------------------------------------------------------------------------
//	64bits��xor�G���B��
//------------------------------------------------------------------------------
dat64perbit xor64bits (dat64perbit dat, dat64perbit ldat) {
	int i;
	for ( i=0 ; i<64 ; i++ )
		dat.bit[i/8][i%8] ^= ldat.bit[i/8][i%8];
	return dat;
}
//------------------------------------------------------------------------------
//	���X64bit��ƪ��䤤�@�b
//------------------------------------------------------------------------------
//	���~�ץ�: �������k���䪺index���Ӥ��P,�쥻�g�ۦP���C
//------------------------------------------------------------------------------
dat32perbit dat64parseTo32 (dat64perbit dat,int ctl) {
	dat32perbit newdat;
	int i,b;
	for ( i=(ctl?32:0), b=0 ; i<(ctl?64:32) ; i++,b++)
		newdat.bit[b/8][b%8] = dat.bit[i/8][i%8];
	return newdat;
}
//------------------------------------------------------------------------------
//	���32bit��ƦX���@��64bit���
//------------------------------------------------------------------------------
dat64perbit Twodat32concat (dat32perbit left, dat32perbit right) {
	dat64perbit	newdat;
	int i,b;
	for ( i=0 ; i<32 ; i++)
		newdat.bit[i/8][i%8] = left.bit[i/8][i%8];
	for ( i=32,b=0 ; i<64 ; i++,b++)
		newdat.bit[i/8][i%8] = right.bit[b/8][b%8];
	return newdat;
}
//------------------------------------------------------------------------------
//	DES �@�^�X���[�K�C(�̿@�Y��=�ݤ�����=�H�I�]�p�z����XD)
//	�ѩ��v�d�Ϥ������Y�A�ҥH�o�Ө禡���ӴN�O���h�Ӧb�禡�N�J�Ӥw�C
//------------------------------------------------------------------------------
//�`�N:�Y�O�n�N�o�Ө禡����Ƨ令�ϥΫ��ЩάO�ѦҡA�����`�N�B�z�Ѽƪ����ǰ��D�C
//------------------------------------------------------------------------------
dat64perbit des_one_round (dat64perbit dat, key48perbit key) {
	return Twodat32concat(
			dat64parseTo32(dat,1),	// �k�b��Ʀ������b���
			xor32bits(	// 5�@�����ƻP�k��[�K��ƪ�xor
				Permutation(	// 4�@�ƦC
					sbox(	// 3�@�D�u�ʪ�S-Box
						xor48bits(	// 2�@��ƻPkey��xor
							ExpansionPermutation(dat,1, E),	// ���@�X�R�ƦC
							key),
						Sbox), 
					P),
				dat64parseTo32(dat,0)));
}
//------------------------------------------------------------------------------
//	DES�[�K�A�����ƫe����key�����͡A�~��γo�Ө禡�C
//------------------------------------------------------------------------------
//	���~�ץ��G DES�[�K�̫�ݭn�i�楪�k32bits������
//------------------------------------------------------------------------------
dat64perbit des_encry (dat64perbit dat,key48perbit key[]) {
	int r;
	dat = InitialPermutation(dat, IP);
	for ( r=0 ; r<16 ; r++)
		dat = des_one_round(dat, key[r]);
	dat = InitialPermutation(Twodat32concat(
		dat64parseTo32(dat,1),dat64parseTo32(dat,0)), IIP);
	return dat;
}
//------------------------------------------------------------------------------
//	DES�ѽX�A�{�ǥu�ݭn�_�ͪ��^�X�ƭ˵ۦ^�h�C
//------------------------------------------------------------------------------
//	���~�ץ��G DES�[�K�̫�ݭn�i�楪�k32bits������
//	���~�ץ��G key��index�u��0~15�A�o�q16�}�l���
//------------------------------------------------------------------------------
dat64perbit des_decry (dat64perbit dat,key48perbit key[]) {
	int r;
	dat = InitialPermutation(dat, IP);
	for ( r=15 ; r>=0 ; r-- )
		dat = des_one_round(dat, key[r]);
	dat = InitialPermutation(Twodat32concat(
		dat64parseTo32(dat,1),dat64parseTo32(dat,0)), IIP);
	return dat;
}
//------------------------------------------------------------------------------
//	�N�L�Ÿ����A��char[8](�@��byte)�ഫ��dat64perbit��8*8��bits
//------------------------------------------------------------------------------
dat64perbit uchar8Tobit64 (unsigned char dat[8]) {
	dat64perbit newdat;
	int i,b;
	for ( i=0 ; i<8 ; i++)
		for ( b=7 ; b>=0 ; b--,dat[i]>>=1)
			newdat.bit[i][b] = dat[i] & 0x01;
	return newdat;
}
//------------------------------------------------------------------------------
//	�Ndat64perbit������ഫ�^char[8],�ݭn�@�ӪŶ����P�o�Ө禡�B�@
//------------------------------------------------------------------------------
unsigned char* bit64Touchar8 (dat64perbit dat,unsigned char* reval) {
	int i,b;
	if (reval != NULL)
		for ( i=0 ; i<8 ; i++ )
			for( b=0, reval[i]=0 ; b<8 ; b++ )
				reval[i] |= (dat.bit[i][b]<<(7-b));
	return reval;
}
//------------------------------------------------------------------------------
//	key�_�l���z��C
//	64 bits -> 56 bits  
//------------------------------------------------------------------------------
//	���~�ץ�: array���޻P����ƪ��t��1
//	���~�ץ�: �e���Ƽe�פ��Pdat64�e��=8,key56�e��=7
//------------------------------------------------------------------------------
key56perbit PermutedChoiceOne (dat64perbit key, const int PC1[8][7]) {
	key56perbit newdat;
	int x,y;
	for ( x=0 ; x<7 ; x++ ) 
		for ( y=0 ; y<8 ; y++ ) {
			int pos = PC1[y][x]-1;
			newdat.bit[y][x] = key.bit[pos/8][pos%8];
		}
	return newdat;
}
//------------------------------------------------------------------------------
//	�C�^�X�ಾ�L���᪺key�g�L�o�Ө禡����48bits��key�A����A�P��Ƨ@���X�C
//	56 bits -> 48 bits
//------------------------------------------------------------------------------
//	���~�ץ�: array���޻P����ƪ��t��1
//	���~�ץ�: �e���Ƽe�פ��P key48=8,key56=7
//------------------------------------------------------------------------------
key48perbit PermutedChoiceTwo (key56perbit key, const int PC2[6][8]) {
	key48perbit newdat;
	int x,y;
	for ( x=0 ; x<8 ; x++ )
		for ( y=0 ; y<6 ; y++ ) {
			int pos = PC2[y][x]-1;
			newdat.bit[y][x] = key.bit[pos/7][pos%7];
		}
	return newdat;
}
//------------------------------------------------------------------------------
//	���ͨC�^�X��48-bits key�A�o�����i�H�ۤv�W�ߧ����A�G�u�g���@�Ө禡�C
//	!!!!!! �`�N !!!!!! �^�ǭȬ��ʺA�t�m���O�����m�A��������C
//------------------------------------------------------------------------------
key48perbit* KeyGenerator (unsigned char dkey[8], const int rotate[16]) {
	key56perbit key = PermutedChoiceOne(uchar8Tobit64(dkey),PCO);
	key48perbit *karr = malloc(sizeof(key48perbit)*16);
	int n,r,i;
	for ( n=0 ; n<16 ; n++ ){
		for ( r=0 ; r<rotate[n] ; r++ ){
			char moved;
			int nowmove;
			// �N�᭱��bit���e��
			for ( i=27 ; i>=0 ; i-- ){
				nowmove = key.bit[i/7][i%7];
				key.bit[i/7][i%7] = moved;
				moved = nowmove;
			}
			key.bit[3][6]= moved;
			// �����k�b���h�@
			for ( i=55 ; i>=28 ; i-- ){
				nowmove = key.bit[i/7][i%7];
				key.bit[i/7][i%7] = moved;
				moved = nowmove;
			}
			key.bit[7][6]= moved;
		}
		karr[n] = PermutedChoiceTwo(key,PCT);
	}
	return karr;
}
//------------------------------------------------------------------------------
//	����sub-key���O�����m
//------------------------------------------------------------------------------
key48perbit* KeyReleaser (key48perbit* tar) {
	free(tar);
	return NULL;
}

#endif
