//------------------------------------------------------------------------------
// CTR 可正確加解密版本 
//------------------------------------------------------------------------------
#ifndef CTR_WESLEY_H
#define CTR_WESLEY_H

#include "cooperate.h"
#include "ECB_mirror.h" 

dat64perbit add_counter(dat64perbit dat) {
	int x,y;
    int flag=1;
 	for ( x=0 ; x<8 ; x++ )
		for ( y=0 ; y<8 ; y++ ) 
        {
        	if(dat.bit[y][x]==0||flag==1)
                 dat.bit[y][x]=1;
            else if (dat.bit[y][x]==1||flag==1)
            {
                 dat.bit[y][x]=1;
                 flag=0;
            }
            else
                break;
        }
	return dat;
}

//------------------------------------------------------------------------------
//	CTR
//------------------------------------------------------------------------------

void CTR_encry(key48perbit k[] );
void CTR_decry(key48perbit k[] );
void CTR_encry_2(key48perbit k1[], key48perbit k2[] );
void CTR_decry_2(key48perbit k1[], key48perbit k2[] );
void CTR_encry_3(key48perbit k1[], key48perbit k2[], key48perbit k3[]);
void CTR_decry_3(key48perbit k1[], key48perbit k2[], key48perbit k3[]);

void CTR ( int crypt, int mode, key48perbit **k ) {
	if ( ENCRYPT == crypt ) {
		if ( mode == _DES )
			CTR_encry(k[0]);
		else if ( mode == _3DES_2KEY )
			CTR_encry_2(k[0], k[1]);
		else if ( mode == _3DES_3KEY )
			CTR_encry_3(k[0], k[1], k[2]);
		else
            ;
	}
	else if ( DECRYPT == crypt ) {
		if ( mode == _DES )
			CTR_decry(k[0]);
		else if ( mode == _3DES_2KEY )
			CTR_decry_2(k[0], k[1]);
		else if ( mode == _3DES_3KEY )
			CTR_decry_3(k[0], k[1], k[2]);
		else
		    ;
	}
	else{
		/** empty **/
	}
}

/** one key **/
void CTR_encry(key48perbit k[] )
{
	unsigned char dat[8];
	dat64perbit rdat;
  	int x, y, len;
  	unsigned long cnt;
	dat64perbit counter;
	for ( x=0 ; x<8 ; x++ )
		for ( y=0 ; y<8 ; y++ ) {
            		counter.bit[y][x] = IV64perbit[y][x];
		}

	cnt = 0;
	fprintf(ostm,"%08lX",cnt);
	while(1){
		len = fread (dat,1,8,istm);
		if ( len <= 0 ) {
			if ( cnt==0 ) fputs("ECB():Error Occur. FILE LEN:0",stderr);
			break;
		}
		rdat = xor64bits(des_encry(add_counter(counter),k), uchar8Tobit64(dat));
		fwrite(bit64Touchar8(rdat,dat),1,8,ostm);
		cnt += len;
	}
	fseek(ostm,0,SEEK_SET);
	fprintf(ostm,"%08lX",cnt);
}

void CTR_decry(key48perbit k[] )
{
	unsigned char dat[8];
	unsigned long total, cnt;
	dat64perbit rdat;
  	int x, y, len;
	dat64perbit counter;
	for ( x=0 ; x<8 ; x++ )
		for ( y=0 ; y<8 ; y++ ) {
            		counter.bit[y][x] = IV64perbit[y][x];
		}

	fscanf(istm,"%08lX",&total);
	printf("Decrypt File LEN: %ld bytes\n",total);
	for ( cnt=0 ; cnt<total ; cnt+=8 ){
		if ( (len=fread (dat, 1, 8, istm)) <= 0 ) break;
		rdat = xor64bits(des_encry(add_counter(counter),k), uchar8Tobit64(dat));
		fwrite(bit64Touchar8(rdat,dat),1,(((total-cnt)<8)?(total-cnt):8),ostm);
	}
}


/** two key **/
void CTR_encry_2(key48perbit k1[], key48perbit k2[] )
{
	unsigned char dat[8];
	unsigned long cnt; 
	dat64perbit rdat;
  	int x, y, len;
	dat64perbit counter;
	for ( x=0 ; x<8 ; x++ )
		for ( y=0 ; y<8 ; y++ ) {
            		counter.bit[y][x] = IV64perbit[y][x];
		}

	fprintf(ostm,"%08lX",cnt);
	while(1){
		len = fread (dat,1,8,istm);
		if ( len <= 0 ) {
			if ( cnt==0 ) fputs("ECB():Error Occur. FILE LEN:0",stderr);
			break;
		}
		rdat = xor64bits(ECB_2key_en(add_counter(counter),k1,k2), uchar8Tobit64(dat));
		fwrite(bit64Touchar8(rdat,dat),1,8,ostm);
		cnt += len;
	}
	fseek(ostm,0,SEEK_SET);
	fprintf(ostm,"%08lX",cnt);
}

void CTR_decry_2(key48perbit k1[], key48perbit k2[] )
{
	unsigned char dat[8];
	unsigned long total, cnt;
	dat64perbit rdat;
  	int x, y, len;
	dat64perbit counter;
	for ( x=0 ; x<8 ; x++ )
		for ( y=0 ; y<8 ; y++ ) {
            		counter.bit[y][x] = IV64perbit[y][x];
		}

	fscanf(istm,"%08lX",&total);
	printf("Decrypt File LEN: %ld bytes\n",total);
	for ( cnt=0 ; cnt<total ; cnt+=8 ){
		if ( (len=fread (dat, 1, 8, istm)) <= 0 ) break;
		rdat = xor64bits(ECB_2key_en(add_counter(counter),k1,k2), uchar8Tobit64(dat));
		fwrite(bit64Touchar8(rdat,dat),1,(((total-cnt)<8)?(total-cnt):8),ostm);
	}
}


/** three key **/
void CTR_encry_3(key48perbit k1[], key48perbit k2[], key48perbit k3[] )
{
	unsigned char dat[8];
	unsigned long cnt;
	dat64perbit rdat;
  	int x, y, len;
	dat64perbit counter;
	for ( x=0 ; x<8 ; x++ )
		for ( y=0 ; y<8 ; y++ ) {
            		counter.bit[y][x] = IV64perbit[y][x];
		}

	cnt = 0;
	fprintf(ostm,"%08lX",cnt);
	while(1){
		len = fread (dat,1,8,istm);
		if ( len <= 0 ) {
			if ( cnt==0 ) fputs("ECB():Error Occur. FILE LEN:0",stderr);
			break;
		}
		rdat = xor64bits(ECB_3key_en(add_counter(counter),k1,k2,k3), uchar8Tobit64(dat));
		fwrite(bit64Touchar8(rdat,dat),1,8,ostm);
		cnt += len;
	}
	fseek(ostm,0,SEEK_SET);
	fprintf(ostm,"%08lX",cnt);
}

void CTR_decry_3(key48perbit k1[], key48perbit k2[], key48perbit k3[])
{
	unsigned char dat[8];
	unsigned long total, cnt;
	dat64perbit rdat;
  	int x, y, len;
	dat64perbit counter;
	for ( x=0 ; x<8 ; x++ )
		for ( y=0 ; y<8 ; y++ ) {
            		counter.bit[y][x] = IV64perbit[y][x];
		}

	fscanf(istm,"%08lX",&total);
	printf("Decrypt File LEN: %ld bytes\n",total);
	for ( cnt=0 ; cnt<total ; cnt+=8 ){
		if ( (len=fread (dat, 1, 8, istm)) <= 0 ) break;
		rdat = xor64bits(ECB_3key_en(add_counter(counter),k1,k2,k3), uchar8Tobit64(dat));
		fwrite(bit64Touchar8(rdat,dat),1,(((total-cnt)<8)?(total-cnt):8),ostm);
	}
}

#endif
