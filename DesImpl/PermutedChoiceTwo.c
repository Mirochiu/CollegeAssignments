#include <stdio.h>
#include "datatype.h"
#include "des_tables.h"

#define PermutedChoiceTwo_Dmoe_main
///------------------------------------------------------------------------------
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

#ifdef PermutedChoiceTwo_Dmoe_main
int main(){
	key56perbit d;
	key48perbit r;
	int x,y;
	for ( y=0 ; y<8 ; y++ ){
		for ( x=0 ; x<7 ; x++ ){
			d.bit[y][x] = y*7+x+1;
			printf("%3d",d.bit[y][x]);
		}
		puts("");
	}
	r = PermutedChoiceTwo(d,PCT);
	puts("====");
	for ( y=0 ; y<6 ; y++ ){
		for ( x=0 ; x<8 ; x++ )
			printf("%3d",r.bit[y][x]);
		puts("");
	}
	return 0;
}
#endif

