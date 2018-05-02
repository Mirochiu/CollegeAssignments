//------------------------------------------------------------------------------
//	Author: 邱永祥
//	建立時間: 2006-11-17 18:08:02 
//	最後修改: 
//------------------------------------------------------------------------------
#include <stdio.h>

#include "datatype.h"
#include "des_tables.h"

// 開關
#define Permutation_Demo_main

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

//------------------------------------------------------------------------------
//	展示與除錯用主程式
//------------------------------------------------------------------------------
#ifdef Permutation_Demo_main
int main() {
	dat32perbit d;
	int x,y;
	// Intital data
	for ( y=0 ; y<4 ; y++ ){
		for ( x=0 ; x<8 ; x++ ){
			d.bit[y][x] = y*8+x+1;
			printf("%3d ",d.bit[y][x]);
		}
		puts("");
	}
	// S-Box
	d = Permutation (d,P);
	puts("====");
	// Print result
	for ( y=0 ; y<4 ; y++ ){
		for ( x=0 ; x<8 ; x++ )
			printf("%3d ",d.bit[y][x]);
		puts("");
	}
	return 0;
}
#endif
