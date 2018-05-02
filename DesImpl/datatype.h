//------------------------------------------------------------------------------
//	DES加/解密 資料型別宣告
//	制定者: 邱永祥
//	建立時間: 2006-11-17 16:09:08 
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
//	DES主函式用資料型別
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

//------------------------------------------------------------------------------
//	鎖匙用資料型別
//------------------------------------------------------------------------------
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
