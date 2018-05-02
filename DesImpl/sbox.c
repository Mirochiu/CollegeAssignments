//------------------------------------------------------------------------------
//	Author: 邱永祥
//	建立時間: 2006-11-17 16:00:08 
//	最後修改: 2006-11-17 18:07:07 
//------------------------------------------------------------------------------
#include <stdio.h>

#include "datatype.h"
#include "des_tables.h"

// 開關
#define sbox_Demo_main

//------------------------------------------------------------------------------
//	S-box的8個6轉4循序運作
//	48 bits -> 32 bits
//------------------------------------------------------------------------------
//	展示用的遮罩: 0x03 = 11 , 0x0F = 1111 
//	confuse: ((dat.bit[n][0]<<1) | dat.bit[n][5]) & 0x03 沒有分隔會有奇怪的問題發生!
//------------------------------------------------------------------------------
dat32perbit sbox (dat48perbit dat, const int S[8][4][16]) {
	dat32perbit	newdat;
	int n, i, m, data, row, col;
	char bit;
	// 8個6轉4
	for ( n=0 ; n<8 ; n++ ) {
		// 取資料作index
		row = ((dat.bit[n][0]<<1) | dat.bit[n][5]) & 0x03;	// 取前後2bit
		for ( i=1, col=0 ; i<5 ; i++ )
			col |= (dat.bit[n][i]<<(4-i));	// 取中間的4位元到col中
		col &= 0x0F;
		// 將資料逐一分解存到新資料中
		data = S[n][row][col];
		#ifdef sbox_Demo_main
		printf("row=%d, col=%d, data=%d\n",row,col,data);
		#endif
		for ( m=3 ; m>=0 ; m-- ) {
			bit = data & 0x01;
			newdat.bit[n/2][m] = bit;
			data>>=1;
		}
		// 作第二次, 由於48bit的資料寬度=6,以原資料為依據取出來的資料寬是4
		// 而32bit資料寬度=8,所以必須要作第二次存到後半部
		n++;
		row = ((dat.bit[n][0]<<1) | dat.bit[n][5]) & 0x03;
		for ( i=1, col=0 ; i<5 ; i++ )
			col |= (dat.bit[n][i]<<(4-i));
		col &= 0x0F;
		data = S[n][row][col];
		#ifdef sbox_Demo_main
		printf("row=%d, col=%d, data=%d\n",row,col,data);
		#endif
		for ( m=7 ; m>=4 ; m-- ) {
			bit = data & 0x01;
			newdat.bit[n/2][m] = bit;
			data>>=1;
		}
	}
	return newdat;
}

//------------------------------------------------------------------------------
//	展示與除錯用主程式
//------------------------------------------------------------------------------
#ifdef sbox_Demo_main
int main() {
	dat48perbit d;
	dat32perbit r;
	int x,y;
	// Intital data
	for ( y=0 ; y<8 ; y++ ){
		for ( x=0 ; x<6 ; x++ ){
			d.bit[y][x] = y*8+x+1;
			printf("%3d ",d.bit[y][x]);
		}
		puts("");
	}
	// S-Box
	r = sbox (d,Sbox);
	puts("====");
	// Print result
	for ( y=0 ; y<4 ; y++ ){
		for ( x=0 ; x<8 ; x++ )
			printf("%3d ",r.bit[y][x]);
		puts("");
	}
	return 0;
}
#endif
