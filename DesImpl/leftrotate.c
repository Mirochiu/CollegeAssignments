#include <stdio.h>
#include "datatype.h"
#include "des_tables.h"

int main(){
	key56perbit key;
	int n,r,i;
	int x,y;
	for ( y=0 ; y<8 ; y++ ){
		for ( x=0 ; x<7 ; x++){
			key.bit[y][x] = y*7+x+1;
			printf("%3d",key.bit[y][x]);
		}
		puts("");
	}
		puts("");
	for ( n=0 ; n<16 ; n++ ){
		for ( r=0 ; r<leftrotate[n] ; r++ ){
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
		for ( y=0 ; y<8 ; y++ ){
			for ( x=0 ; x<7 ; x++){
				printf("%3d",key.bit[y][x]);
			}
			puts("");
		}
		puts("");
	}
	return 0;
}
