#include <stdio.h>
#include "datatype.h"
#include "des_tables.h"

#define PermutedChoiceOne_Dmoe_main
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

#ifdef PermutedChoiceOne_Dmoe_main
int main(){
	dat64perbit d;
	key56perbit r;
	int x,y;
	for ( y=0 ; y<8 ; y++ ){
		for ( x=0 ; x<8 ; x++ ){
			d.bit[y][x] = y*8+x+1;
			printf("%3d",d.bit[y][x]);
		}
		puts("");
	}
	r = PermutedChoiceOne(d,PCO);
	puts("====");
	for ( y=0 ; y<8 ; y++ ){
		for ( x=0 ; x<7 ; x++ )
			printf("%3d",r.bit[y][x]);
		puts("");
	}
	return 0;
}

