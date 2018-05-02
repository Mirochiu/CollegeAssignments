#ifndef CBC_KIWE_H
#define CBC_KIWE_H

#include "cooperate.h"

//------------------------------------------------------------------------------
//	CBC
//------------------------------------------------------------------------------
void CBC (int crypt, int mode, key48perbit *key[] )
{
	int i,x,y;
	char time = 0;				/* 判別加密迴圈運作是否為第一次 */
	char dat[8];				/* 由資料流讀取的資料 */
	char flab;					/* 多餘位元組數目 */
	dat64perbit ciphertext;		/* IV 表格轉換結構後存於此 */
	dat64perbit new_dat_64;		/* 由資料流讀取的資料轉換結構而得 */
	dat64perbit old_dat_64;		/* 紀錄 new_dat_64 加密結果 */

	if(crypt == ENCRYPT)
	{
		/* 取得檔案長度 */
		long len = filesize(istm);
		/* 計算檔案加密時會多寫幾個 Byte */
		ltoa (len%8, &flab, 10);
		if( flab != '0' ) flab= '8' - flab + '0';

		puts("CBC Encoding ...");
		while(len)
		{
			/* 讀取檔案 */
			len -= fread(dat, 1, 8, istm); 

			/* 將讀取到的資料作結構轉換，進行 XOR 運算 */
			new_dat_64 = uchar8Tobit64(dat);
			if ( time != 0 )				// 不是第一次, 上次加密結果與明文做
				new_dat_64 = xor64bits( new_dat_64, old_dat_64 );
			else {							// 第一次, ciphertext 存入 IV , 與明文做 XOR
				for( y=0; y<8; y++ )
					for ( x=0; x<8; x++ )
						ciphertext.bit[y][x]=IV64perbit[y][x];
				new_dat_64 = xor64bits( new_dat_64, ciphertext );
				time++;
			}

			/* one key, two key, three key 三種加密模式 */
			if( mode == _DES )
				new_dat_64 = des_encry( new_dat_64, key[0] );
			else if ( mode == _3DES_2KEY ) {
				new_dat_64 = des_encry( new_dat_64, key[0] );
				new_dat_64 = des_decry( new_dat_64, key[1] );
				new_dat_64 = des_encry( new_dat_64, key[0] );
			}
			else if ( mode == _3DES_3KEY ) {
				new_dat_64 = des_encry( new_dat_64, key[0] );
				new_dat_64 = des_decry( new_dat_64, key[1] );
				new_dat_64 = des_encry( new_dat_64, key[2] );
			}

			/* 紀錄結果給下一次XOR用 */
			old_dat_64 = new_dat_64;

			/* 將加密完的資料作結構轉換寫到檔案 */
			bit64Touchar8(new_dat_64, dat);
			fwrite(dat, 1, 8, ostm);
		}
		/* 將多餘的 Byte 數目寫入檔尾 */
		fputc( flab , ostm);
		puts("Encoding completed.");
	}
	
	else if(crypt == DECRYPT)
	{
		dat64perbit tmp_dat_64;			/* 紀錄讀取結果給下次迴圈運作使用 */
		long len = filesize(istm)-1;	/* 檔案長度，扣除一個存放多餘字元數的位元組 */

		puts("CBC Decoding ...");
		while(len)
		{
			/* 讀取資料，並作結構轉換 */
			len -= fread(dat, 1, 8, istm);
			new_dat_64 = uchar8Tobit64(dat);
			tmp_dat_64 = new_dat_64;			// 紀錄讀取結果
			
			/* one key, two key, three key 三種加密模式 */
			if( mode == _DES )
				new_dat_64 = des_decry( new_dat_64, key[0] );
			else if ( mode == _3DES_2KEY ) {
				new_dat_64 = des_decry( new_dat_64, key[0] );
				new_dat_64 = des_encry( new_dat_64, key[1] );
				new_dat_64 = des_decry( new_dat_64, key[0] );
			}
			else if ( mode == _3DES_3KEY ) {
				new_dat_64 = des_decry( new_dat_64, key[2] );
				new_dat_64 = des_encry( new_dat_64, key[1] );
				new_dat_64 = des_decry( new_dat_64, key[0] );
			}
			
			/* XOR 運算 */
			if ( time != 0 )			// 不是第一次，解密過的明文與上次迴圈的密文做 XOR
				new_dat_64 = xor64bits( new_dat_64, old_dat_64 );
			else {						// 第一次，將 IV 存至 ciphertext，與密文做 XOR
				for( y=0; y<8; y++ )
					for ( x=0; x<8; x++ )
						ciphertext.bit[y][x] = IV64perbit[y][x];
				new_dat_64 = xor64bits( new_dat_64, ciphertext );
				time++;
			}
			/* 儲存讀取結果給下一個 XOR 使用 */
			old_dat_64 = tmp_dat_64;

			/* 將解密完的資料作結構轉換寫到檔案 */
			bit64Touchar8(new_dat_64, dat);
			if ( len > 0 ) fwrite(dat, 1, 8, ostm);
			else { 
				flab = fgetc(istm);					// 取得檔尾的多餘字元數目
				fwrite(dat, 1, 8-(flab-'0'), ostm);	// 避免輸出多餘字元
			} 
		}
	}
}
#endif
