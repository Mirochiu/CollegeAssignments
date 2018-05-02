//------------------------------------------------------------------------------
//	Author: 邱永祥
//	建立時間: 2006-11-17 16:00:08 
//	最後修改: 2006-11-17 16:00:56 
//------------------------------------------------------------------------------
#include <stdio.h>

#include "datatype.h"
#include "des_tables.h"

// 開關
#define ExpansionPermutation_Demo_main

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
//	展示與除錯用主程式
//------------------------------------------------------------------------------
#ifdef ExpansionPermutation_Demo_main
int main(){
	dat64perbit d;
	dat48perbit r;
	int x,y;
	// Intital data
	for ( y=0 ; y<8 ; y++ ){
		for ( x=0 ; x<8 ; x++ ){
			d.bit[y][x] = y*8+x+1;
			printf("%3d ",d.bit[y][x]);
		}
		puts("");
	}
	puts(" =left=== ");
	// ExpansionPermutation - left
	r = ExpansionPermutation(d,0,E);
	// Print result
	for ( y=0 ; y<8 ; y++ ){
		for ( x=0 ; x<6 ; x++ )
			printf("%3d ",r.bit[y][x]);
		puts("");
	}
	puts(" =right=== ");
	// ExpansionPermutation - right
	r = ExpansionPermutation(d,1,E);
	for ( y=0 ; y<8 ; y++ ){
		for ( x=0 ; x<6 ; x++ )
			printf("%3d ",r.bit[y][x]);
		puts("");
	}
	return 0;
}
#endif
