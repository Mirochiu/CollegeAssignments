//------------------------------------------------------------------------------
//	���g: ���ò�
//	�إߤ��: ����
//	�̫�ק�: 2006-11-23
//------------------------------------------------------------------------------
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

//------------------------------------------------------------------------------
//	��ƫ��O�ŧi��
//------------------------------------------------------------------------------
// 64bits ��l��ơC
typedef struct{
	long dat32[2];
} dat64;
// 64��bit��ơA�C��bit���O�Τ@��char�x�s�C
typedef struct{
	char bit[8][8];
} dat64perbit;
// 64-bits��ƪ��@�b�A�P�˨C��bit���O�Τ@��char�x�s�C
typedef struct{
	char bit[4][8];
} dat32perbit;
// des�B�⤤�|�Ψ��X�W���c48-bits�C
typedef struct{
	char bit[8][6];
}dat48perbit;

// key�@PCT���ɭԷ|�Ψ�
typedef struct{
	char bit[6][8];
}key48perbit;
// key�|�B�Φ���Ƶ��c�@�x�s�A�覡�P�W�C
typedef struct{
	char bit[8][7];
} key56perbit;
// key������b�@����A�@�bkey�����c
typedef struct{
	char bit[4][7];
} key28perbit;
//------------------------------------------------------------------------------
//	�ѷӥΪ��w�q��
//------------------------------------------------------------------------------
// Initial Permutation
const int IP[8][8] = {
	{58,50,42,34,26,18,10,2},
	{60,52,44,36,28,20,12,4},
	{62,54,46,38,30,22,14,6},
	{64,56,48,40,32,24,16,8},
	{57,49,41,33,25,17,9,1},
	{59,51,43,35,27,19,11,3},
	{61,53,45,37,29,21,13,5},
	{63,55,47,39,31,23,15,7}
};
// Inverse Initial Permutation
const int IIP[8][8] = {
	{40,8,48,16,56,24,64,32},
	{39,7,47,15,55,23,63,31},
	{38,6,46,14,54,22,62,30},
	{37,5,45,13,53,21,61,29},
	{36,4,44,12,52,20,60,28},
	{35,3,43,11,51,19,59,27},
	{34,2,42,10,50,18,58,26},
	{33,1,41,9,49,17,57,25}
};
// Expansion Permutation
const int E[8][6] = {
	{32,1,2,3,4,5},
	{4,5,6,7,8,9},
	{8,9,10,11,12,13},
	{12,13,14,15,16,17},
	{16,17,18,19,20,21},
	{20,21,22,23,24,25},
	{24,25,26,27,28,29},
	{28,29,30,31,32,1}
};
// Permutation Function
const int P[4][8] = {
	{16,7,20,21,29,12,28,17},
	{1,15,23,26,5,18,31,10},
	{2,8,24,14,32,27,3,9},
	{19,13,30,6,22,11,4,25}
};
// S-box
const int Sbox[8][4][16] = {
	// S1
	{{14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
	{0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
	{4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
	{15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}},
	// S2
	{{15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
	{3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
	{0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
	{13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}},
	// S3
	{{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
	{13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
	{13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
	{1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}},
	// S4
	{{7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
	{13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
	{10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
	{3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}},
	// S5
	{{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
	{14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
	{4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
	{11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}},
	// S6
	{{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
	{10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
	{9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
	{4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}},
	// S7
	{{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
	{13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
	{1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
	{6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}},
	// S8
	{{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
	{1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
	{7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
	{2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}}
};
// Permuted Choice One (PC-1)
const int PCO[8][7] = {
	{57,49,41,33,25,17,9},
	{1,58,50,42,34,26,18},
	{10,2,59,51,43,35,27},
	{19,11,3,60,52,44,36},
	{63,55,47,39,31,23,15},
	{7,62,54,46,38,30,22},
	{14,6,61,53,45,37,29},
	{21,13,5,28,20,12,4}};
// Permutation Choice Two(PC-2)
const int PCT[6][8] = {
	{14,17,11,24,1,5,3,28},
	{15,6,21,10,23,19,12,4},
	{26,8,16,7,27,20,13,2},
	{41,52,31,37,47,55,30,40},
	{51,45,33,48,44,49,39,56},
	{34,53,46,42,50,36,29,32}
};
const int leftrotate[16] = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};

//------------------------------------------------------------------------------
//	�Ѧ�IP����ơA�N�һݪ�bit��inewdat�AIP�O8x8���G���x�}�C
//	�A�Ω�GInitialPermutation , Inverse Initial Permutation
//	64 bits -> 64 bits
//------------------------------------------------------------------------------
dat64perbit InitialPermutation (dat64perbit dat, const int IP[8][8]) {
	dat64perbit newdat;
	int x,y;
	for ( x=0 ; x <8 ; x++ )
		for ( y=0 ; y <8 ; y ++ ) {
			int opos = IP[y][x] - 1;		// �}�C�q0�}�l
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
	int x,y;
	for ( x=0 ; x<6 ; x++ )
		for ( y=0 ; y<8 ; y++ ) {
			int opos = E[y][x] + (half?31:-1);	// ��32��bit�H��O�k��
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
dat48perbit xor48bits(dat48perbit dat, key48perbit key) {
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
		// �@�ĤG��, �ѩ�48bit����Ƽe��=6,�H���Ƭ��̾ڨ��X�Ӫ���Ƽe�O4
		// ��32bit��Ƽe��=8, �ҥH�����n�@�ĤG���s���b��
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
//	puts("sbox(): FINISH");
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
dat32perbit xor32bits(dat32perbit dat, dat32perbit ldat) {
	int i;
	for ( i=0 ; i<32 ; i++ )
		dat.bit[i/8][i%8] ^= ldat.bit[i/8][i%8];
	return dat;
}
//------------------------------------------------------------------------------
//	���X64bit��ƪ��䤤�@�b
//------------------------------------------------------------------------------
//	���~�ץ�: �������k���䪺index���Ӥ��P,�쥻�g�ۦP���C
//------------------------------------------------------------------------------
dat32perbit dat64parseTo32(dat64perbit dat,int ctl){
	dat32perbit newdat;
	int i,b;
	for ( i=(ctl?32:0),b=0 ; i<(ctl?64:32) ; i++,b++)
		newdat.bit[b/8][b%8] = dat.bit[i/8][i%8];
	return newdat;
}
//------------------------------------------------------------------------------
//	���32bit��ƦX���@��64bit���
//------------------------------------------------------------------------------
dat64perbit Twodat32concat (dat32perbit left, dat32perbit right){
	dat64perbit	newdat;
	int i,b;
	for ( i=0 ; i<32 ; i++)
		newdat.bit[i/8][i%8] = left.bit[i/8][i%8];
	for ( i=32,b=0 ; i<64 ; i++,b++)
		newdat.bit[i/8][i%8] = right.bit[b/8][b%8];
	return newdat;
}
//------------------------------------------------------------------------------
//	DES �@�^�X���[�K
//------------------------------------------------------------------------------
dat64perbit des_one_round(dat64perbit dat, key48perbit key) {
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
dat64perbit des_encry (dat64perbit dat,key48perbit key[]){
	int r;
	dat = InitialPermutation(dat, IP);
	for ( r=0 ; r<16 ; r++)
		dat = des_one_round(dat, key[r]);
	// 32-bit swap and iip 
	dat = InitialPermutation(Twodat32concat(
		dat64parseTo32(dat,1),dat64parseTo32(dat,0)), IIP);
	return dat;
}
//------------------------------------------------------------------------------
//	DES�ѽX�A�{�ǥu�ݭn�_�ͪ��^�X�ƭ˵ۦ^�h�C
//------------------------------------------------------------------------------
//	���~�ץ��G DES�[�K�̫�ݭn�i�楪�k32bits������
//  ���~�ץ�: 0~15 �ڼg��0~16 
//------------------------------------------------------------------------------
dat64perbit des_decry (dat64perbit dat,key48perbit key[]){
	int r;
	dat = InitialPermutation(dat, IP);
	for ( r=15 ; r>=0 ; r-- )
		dat = des_one_round(dat, key[r]);
	// 32-bit swap and iip 
	dat = InitialPermutation(Twodat32concat(  
		dat64parseTo32(dat,1),dat64parseTo32(dat,0)), IIP);
	return dat;	
}
//------------------------------------------------------------------------------
//	�N�L�Ÿ����A��char[8](�@��byte)�ഫ��dat64perbit��8*8��bits
//------------------------------------------------------------------------------
dat64perbit uchar8Tobit64(unsigned char dat[8]){
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
unsigned char* bit64Touchar8(dat64perbit dat,unsigned char* reval){
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
//------------------------------------------------------------------------------
key48perbit* KeyGenerator(unsigned char dkey[8], const int rotate[16]) {
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
//	�D�{��
//------------------------------------------------------------------------------
int main (int argc,char** argv) {
	char fname[128], inputs[32];
	unsigned char key[9];
	unsigned char dat[8];
	dat64perbit rdat;
	key48perbit *keys;
	FILE *istm, *ostm;
	int flag=1, i, cnt;
	
	if (argc-1) return -1;	// foolu
	
	do { puts("Input filename:");
	} while(scanf("%s",fname) == -1);
	istm = fopen(fname,"rb");
	if (istm == NULL ) {
		fprintf(stderr,"Cannot read input file.\n");
		return -1;
	}
	strcat(fname,".des");
	ostm = fopen(fname,"wb");
	if (ostm == NULL) {
		fprintf(stderr,"Cannot write output file.\n");
		return -1;
	}
	do {
		// initialize key for codec safety.
		for (i=0 ; i<8 ;i++) key[i] = 1;
		puts("Input key(1~8 char):");
	} while (scanf("%s",key) == -1);

	puts("Generating DES subkeys");
	keys = KeyGenerator(key,leftrotate);
	puts("ENCRYPT(1)/DECRYPT(!1)"); 
	scanf ("%s",inputs);
	// those code is not all right, just running is ok.
	if ( inputs[0] == '1' ) {
		cnt=0;
		puts("DES Encoding ...");
		while(flag){
			if (fread(dat,1,8,istm) <8) flag = 0;
			rdat = des_encry(uchar8Tobit64(dat),keys);
			bit64Touchar8(rdat,dat);
			fwrite(dat,1,8,ostm);
			cnt++;
		}
	} else {
		cnt=0;
		puts("DES Decoding ...");	
		while(flag){
			if (fread(dat,1,8,istm) <8) flag = 0;
			rdat = des_decry(uchar8Tobit64(dat),keys);
			bit64Touchar8(rdat,dat);
			fwrite(dat,1,8,ostm);
			cnt++;
		}
	}
	puts("Action completed.");
	return 0;
}

