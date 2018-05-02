//------------------------------------------------------------------------------
//	撰寫: 邱永祥
//	建立日期: 不明
//	最後修改: 2006-11-23
//------------------------------------------------------------------------------
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

//------------------------------------------------------------------------------
//	資料型別宣告區
//------------------------------------------------------------------------------
// 64bits 原始資料。
typedef struct{
	long dat32[2];
} dat64;
// 64個bit資料，每個bit分別用一個char儲存。
typedef struct{
	char bit[8][8];
} dat64perbit;
// 64-bits資料的一半，同樣每個bit分別用一個char儲存。
typedef struct{
	char bit[4][8];
} dat32perbit;
// des運算中會用到擴增結構48-bits。
typedef struct{
	char bit[8][6];
}dat48perbit;

// key作PCT的時候會用到
typedef struct{
	char bit[6][8];
}key48perbit;
// key會運用此資料結構作儲存，方式同上。
typedef struct{
	char bit[8][7];
} key56perbit;
// key分成兩半作旋轉，一半key的結構
typedef struct{
	char bit[4][7];
} key28perbit;
//------------------------------------------------------------------------------
//	參照用表格定義區
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
//	參考IP表的資料，將所需的bit抓進newdat，IP是8x8的二維矩陣。
//	適用於：InitialPermutation , Inverse Initial Permutation
//	64 bits -> 64 bits
//------------------------------------------------------------------------------
dat64perbit InitialPermutation (dat64perbit dat, const int IP[8][8]) {
	dat64perbit newdat;
	int x,y;
	for ( x=0 ; x <8 ; x++ )
		for ( y=0 ; y <8 ; y ++ ) {
			int opos = IP[y][x] - 1;		// 陣列從0開始
			newdat.bit[y][x] = dat.bit[opos/8][opos%8];
		}
	return newdat;
}
//------------------------------------------------------------------------------
//	參考E表的資料，然後從dat中取出所需的bit放入新資料中。
//	half = 0，代表取左邊32bit產生48bits；half = 1，代表右邊取32bit產生48bits
//	32=4*8 =>> 8*6=48
//	這個函式省略了從64bits對應到32bits結構中，直接用half參數控制擷取。
//------------------------------------------------------------------------------
dat48perbit ExpansionPermutation (dat64perbit dat, int half, const int E[8][6]) {
	dat48perbit newdat;
	int x,y;
	for ( x=0 ; x<6 ; x++ )
		for ( y=0 ; y<8 ; y++ ) {
			int opos = E[y][x] + (half?31:-1);	// 第32個bit以後是右邊
			newdat.bit[y][x] = dat.bit[opos/8][opos%8];
				// 等號右邊是依據原本資料的寬,而不是新資料寬
		}
	return newdat;
}
//------------------------------------------------------------------------------
// 48bits的data與key作xor運算
//------------------------------------------------------------------------------
//	因為都是0 xor 0 所以其他bit不會影響到結果
//------------------------------------------------------------------------------
dat48perbit xor48bits(dat48perbit dat, key48perbit key) {
	int i;
	for (i=0 ; i<48 ; i++ )
		dat.bit[i/6][i%6] ^= key.bit[i/8][i%8];
			// data=>(y=8,x=6), key=>(y=6,x=8)
	return dat;
}
//------------------------------------------------------------------------------
//	S-box的8個6轉4循序運作
//	48 bits -> 32 bits
//------------------------------------------------------------------------------
//	錯誤修正: 分解資料沒有將資料右移，使輸出資料都相同。
//------------------------------------------------------------------------------
dat32perbit sbox (dat48perbit dat, const int S[8][4][16]) {
	dat32perbit	newdat;
	int n, i, m, data, row, col;
	char bit;
	// 8個6轉4
	for ( n=0 ; n<8 ; n++ ) {
		// 取資料作index
		row = ((dat.bit[n][0]<<1) | dat.bit[n][5]);	// 取前後2bit
		for ( i=1, col=0 ; i<5 ; i++ )
			col |= (dat.bit[n][i]<<(4-i));	// 取中間的4位元到col中
		// 將資料逐一分解存到新資料中
		data = S[n][row][col];
		for ( m=3 ; m>=0 ; m-- ) {
			bit = data & 0x01;
			newdat.bit[n/2][m] = bit;
			data>>=1;
		}
		// 作第二次, 由於48bit的資料寬度=6,以原資料為依據取出來的資料寬是4
		// 而32bit資料寬度=8, 所以必須要作第二次存到後半部
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
//	使用P所提供的新資料所需要的bit編號，然後從dat中將bit取出來放入新資料中。
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
// 32bits的xor二元運算
//------------------------------------------------------------------------------
dat32perbit xor32bits(dat32perbit dat, dat32perbit ldat) {
	int i;
	for ( i=0 ; i<32 ; i++ )
		dat.bit[i/8][i%8] ^= ldat.bit[i/8][i%8];
	return dat;
}
//------------------------------------------------------------------------------
//	取出64bit資料的其中一半
//------------------------------------------------------------------------------
//	錯誤修正: 等號左右兩邊的index應該不同,原本寫相同的。
//------------------------------------------------------------------------------
dat32perbit dat64parseTo32(dat64perbit dat,int ctl){
	dat32perbit newdat;
	int i,b;
	for ( i=(ctl?32:0),b=0 ; i<(ctl?64:32) ; i++,b++)
		newdat.bit[b/8][b%8] = dat.bit[i/8][i%8];
	return newdat;
}
//------------------------------------------------------------------------------
//	兩份32bit資料合成一份64bit資料
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
//	DES 一回合的加密
//------------------------------------------------------------------------------
dat64perbit des_one_round(dat64perbit dat, key48perbit key) {
	return Twodat32concat(
			dat64parseTo32(dat,1),	// 右半資料成為左半資料
			xor32bits(	// 5作左邊資料與右邊加密資料的xor
				Permutation(	// 4作排列
					sbox(	// 3作非線性的S-Box
						xor48bits(	// 2作資料與key的xor
							ExpansionPermutation(dat,1, E),	// 先作擴充排列
							key),
						Sbox), 
					P),
				dat64parseTo32(dat,0)));
}
//------------------------------------------------------------------------------
//	DES加密，必須事前完成key的產生，才能用這個函式。
//------------------------------------------------------------------------------
//	錯誤修正： DES加密最後需要進行左右32bits的掉換
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
//	DES解碼，程序只需要鑰匙的回合數倒著回去。
//------------------------------------------------------------------------------
//	錯誤修正： DES加密最後需要進行左右32bits的掉換
//  錯誤修正: 0~15 我寫成0~16 
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
//	將無符號型態的char[8](一個byte)轉換成dat64perbit的8*8的bits
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
//	將dat64perbit的資料轉換回char[8],需要一個空間給與這個函式運作
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
//	key起始的篩選。
//	64 bits -> 56 bits  
//------------------------------------------------------------------------------
//	錯誤修正: array索引與表格資料的差為1
//	錯誤修正: 前後資料寬度不同dat64寬度=8,key56寬度=7
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
//	每回合轉移過之後的key經過這個函式產生48bits的key，之後再與資料作結合。
//	56 bits -> 48 bits
//------------------------------------------------------------------------------
//	錯誤修正: array索引與表格資料的差為1
//	錯誤修正: 前後資料寬度不同 key48=8,key56=7
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
//	產生每回合的48-bits key，這部份可以自己獨立完成，故只寫成一個函式。
//------------------------------------------------------------------------------
key48perbit* KeyGenerator(unsigned char dkey[8], const int rotate[16]) {
	key56perbit key = PermutedChoiceOne(uchar8Tobit64(dkey),PCO);
	key48perbit *karr = malloc(sizeof(key48perbit)*16);
	int n,r,i;
	for ( n=0 ; n<16 ; n++ ){
		for ( r=0 ; r<rotate[n] ; r++ ){
			char moved;
			int nowmove;
			// 將後面的bit往前移
			for ( i=27 ; i>=0 ; i-- ){
				nowmove = key.bit[i/7][i%7];
				key.bit[i/7][i%7] = moved;
				moved = nowmove;
			}
			key.bit[3][6]= moved;
			// 分左右半部去作
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
//	主程式
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

