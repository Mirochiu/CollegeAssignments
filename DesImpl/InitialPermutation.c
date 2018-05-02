//------------------------------------------------------------------------------
//	Author: 邱永祥
//	建立時間: 2006-11-17 15:25:48
//	最後修改: 2006-11-17 15:56:25  
//------------------------------------------------------------------------------
#include <stdio.h>

#include "datatype.h"
#include "des_tables.h"

// 開關
#define InitialPermutation_Demo_main

//------------------------------------------------------------------------------
//	參考IP表的資料，將所需的bit抓進newdat，IP是8x8的二維矩陣。
//	適用於：InitialPermutation , Inverse Initial Permutation
//	64 bits -> 64 bits
//------------------------------------------------------------------------------
dat64perbit InitialPermutation (dat64perbit dat,const int IP[8][8]) {
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

//------------------------------------------------------------------------------
//	展示與除錯用主程式
//------------------------------------------------------------------------------
#ifdef InitialPermutation_Demo_main
int main(){
	dat64perbit d;
	int x,y;
	// Intital data
	for ( y=0 ; y<8 ; y++ ){
		for ( x=0 ; x<8 ; x++ ){
			d.bit[y][x] = y*8+x+1;
			printf("%3d ",d.bit[y][x]);
		}
		puts("");
	}
	puts(" =IP=== ");
	// IntitialPermutation - ip
	d = InitialPermutation(d,IP);
	// Print result
	for ( y=0 ; y<8 ; y++ ){
		for ( x=0 ; x<8 ; x++ )
			printf("%3d ",d.bit[y][x]);
		puts("");
	}
	puts(" =IIP=== ");
	// IntitialPermutation - iip
	d = InitialPermutation(d,IIP);
	// Print result
	for ( y=0 ; y<8 ; y++ ){
		for ( x=0 ; x<8 ; x++ )
			printf("%3d ",d.bit[y][x]);
		puts("");
	}
	return 0;
}
#endif
