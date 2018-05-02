/** 
 *  AES加密器，不包含加密流程，也就是ECB、CBC、CTR等。
 *  這個類別內的所有方法都是static的，不需要建立物件直接就可以使用。
 */
public class mirror_aes {
	/**
	 *  設定是否要開啟除錯訊息。
	 */
	static public boolean DEBUG = false;
	/**
	 *  決定是否要使用AES查表加速。 
	 */
	static public boolean LookupTableQuickly = false;
	/**
	 *  拓展鎖鑰時會使用的表格。
	 */
	static private byte[] Rcon = {
		(byte)0x01, (byte)0x02, (byte)0x04, (byte)0x08, (byte)0x10, (byte)0x20,
		(byte)0x40, (byte)0x80, (byte)0x1B, (byte)0x36, (byte)0x6C, (byte)0xC3,
		(byte)0x9D, (byte)0x21, (byte)0x42};

	/**
	 *  鎖鑰的拓展方法，會因為鎖鑰的長度而建立不同的拓展鎖鑰。
	 *  @param key 尚未拓展的鎖鑰，長度必須為16、24、32之一。
	 *  @return 拓展後的鎖鑰。
	 */
	static public byte[][] KeyExpansion (byte[] key) {
		// 測試鎖鑰料長度
		if (key.length!=16 && key.length!=24 && key.length!=32) {
			throw new IllegalArgumentException (
				"mirror_aes::KeyExpansion() key length not right. len:" + 
				key.length*8 + "bits");
		}

		int KeyRows		= key.length/4;		// 傳入的鎖鑰列數
		int ExkeyAmount	= KeyRows+7;		// 拓展後的鎖鑰數量
		byte[] temp 	= new byte[4];		// 運算拓展鎖鑰時所使用的空間。
		byte[][] exkey	= new byte[ExkeyAmount][16];
		int col;

		// 傳入的鎖鑰將成為初使子鎖鑰
		for (int i=0 ; i<key.length ; i++)
			exkey[i/16][i%16] = key[i];

		// 產生另外的幾隻鎖鑰
		for (int ExkeyRows=KeyRows ; ExkeyRows<ExkeyAmount*4 ; ExkeyRows++)
		{
			// 運算新列的前置作業: 複製前一列的資料
			for (col=0 ; col<4 ; col++) 
				temp[col] = (ExkeyRows%4 == 0)?
							exkey[ExkeyRows/4-1][12+col]:
							exkey[ExkeyRows/4][((ExkeyRows%4)-1)*4+col];

			// 128, 192 bits會做的事情
			if (ExkeyRows%KeyRows == 0) {
				// RotWord: left rotate 1-byte
				byte nowmove = temp[0];
				temp[0] = temp[1];
				temp[1] = temp[2];
				temp[2] = temp[3];
				temp[3] = nowmove;

				// SubWord: subsitatute bytes
				for (col=0 ; col<4 ; col++)
					temp[col] = Aes_Tables.Sbox[temp[col]<0?256+temp[col]:temp[col]];

				// xor Rcon[]: xor operation
				temp[0] ^= Rcon[ExkeyRows/4-1];
			}
			// 256 bits長度的鎖鑰才需要做這件事情
			else if (KeyRows==8 && ExkeyRows%KeyRows==4) {
				// SubWord
				for (col=0 ; col<4 ; col++)
					temp[col] = Aes_Tables.Sbox[temp[col]<0?256+temp[col]:temp[col]];
			}

			// 將前一個循環的鎖鑰，與這次處理完成的鎖鑰進行xor運算
			for (col=0 ; col<4 ; col++) 
				exkey[ExkeyRows/4][(ExkeyRows%4)*4+col] = 
						(byte)(exkey[ExkeyRows/4-1][(ExkeyRows%4)*4+col]^temp[col]);
		}//end for

		return exkey;
	}
	/**
	 *  進行AES的加密作業，隨者拓展鎖鑰長度的不同，加密的回合數也會不同。
	 *  此外，使用這個方法後，明文內容將被更改成密文。
	 *  @param plaintext 要加密的明文資料，資料將會變成密文。
	 *  @param exkey 拓展後的鎖鑰。
	 *  @return 加密過的明文，也就是密文。
	 */
	static public byte[] Encrypt (byte[] plaintext, byte[][] exkey) {
		byte[] swap = new byte[plaintext.length];
		AddRoundKey (plaintext, exkey, 0);
		for (int i=1 ; i<exkey.length-1 ; i++) {
			SubstituteBytes(plaintext);
			ShiftRows(plaintext);
			MixColumns(plaintext,swap);
			AddRoundKey(swap,exkey,i);
			CopyState(plaintext,swap);
		}
		SubstituteBytes(plaintext);
		ShiftRows(plaintext);
		AddRoundKey(plaintext,exkey,exkey.length-1);
		return plaintext;
	}
	/**
	 *  進行AEE的解密作業，隨著拓展鎖鑰的不同，加密回合數也不同。
	 *  此外，使用這個方法後，密文內容將被更改明文內容。
	 *  @param ciphertext 要解密的密文資料，資料將會變成明文。
	 *  @param exkey 經過拓展後的鎖鑰。
	 *  @return 解密過的密文，也就是明文。
	 */
	static public byte[] Decrypt (byte[] ciphertext, byte[][] exkey) {
		byte[] swap = new byte[ciphertext.length];
		InverseAddRoundKey(ciphertext, exkey, exkey.length-1);
		for (int i=1 ; i<exkey.length-1 ; i++) {
			InverseShiftRows(ciphertext);
			InverseSubstituteBytes(ciphertext);
			InverseAddRoundKey(ciphertext, exkey, exkey.length-1-i);
					// exkey.length-1-i 會讓我們用的key反過來。
			InverseMixColumns(ciphertext, swap);
			CopyState(ciphertext,swap);
		}
		InverseShiftRows(ciphertext);
		InverseSubstituteBytes(ciphertext);
		InverseAddRoundKey(ciphertext, exkey, 0);
		return ciphertext;
	}
	/**
	 *  產生亂數的資料，可產生亂數資料或是鎖鑰，主要是拿來產生隨機鎖鑰。
	 *  @param space 會將亂數資料存入這個空間內。若給與null值，
	 *               將會自動產生16的元素的陣列並取得亂數後回傳。
	 *  @return 回傳產生好的資料內容。
	 */
	static public byte[] getRandom (byte[] space) {
		if (space == null) space = new byte[16];
		new java.util.Random().nextBytes(space);
		return space;
	}
	/**
	 *  展示&測試用主程式。
	 */
	static public void main(String [] args) {
		byte[] data = new byte[16];
		byte[] rdata = new byte[16];
		byte[] key = new byte[16];
		aes_cryptor.getRandom(key);
		byte[][] exkey = KeyExpansion(key);
		boolean flag = true;
		DEBUG = true;

		System.out.println("展示 & 測試選項:");
		System.out.println("0) All Demo.");
		System.out.println("1) Genreal Demo.");
		System.out.println("2) ECB Demo. (need a file named 'test')");
		System.out.println("3) Key Expansion Demo.");
		System.out.println("4) All key length cryption Demo.");

		java.util.Scanner stdin = new java.util.Scanner(System.in);
		switch (Integer.parseInt(stdin.nextLine())) {
			case 0:
				flag = false;
			case 1:
				getRandom(data);
				System.out.println("The data : "+aes_cryptor.asHex(data));

				// 測試各部份
				System.out.println("SubstituteBytes: " +
					aes_cryptor.asHex(SubstituteBytes(data)));
				System.out.println("InverseSubstituteBytes: " +
					aes_cryptor.asHex(InverseSubstituteBytes(data)));
				System.out.println("");

				for (int t=0 ; t<11 ; t++) {
					System.out.println("1st AddRoundKey: "+aes_cryptor.asHex(AddRoundKey(data,exkey,t)));
					System.out.println("2nd AddRoundKey: "+aes_cryptor.asHex(AddRoundKey(data,exkey,t)));
					System.out.println("");
				}

				System.out.println("MixColumns: "+aes_cryptor.asHex(MixColumns(data,rdata)));
				System.out.println("InverseMixColumns: "+aes_cryptor.asHex(InverseMixColumns(rdata,data)));
				System.out.println("");
				
				System.out.println("ShiftRows: "+aes_cryptor.asHex(ShiftRows(data)));
				System.out.println("InverseShiftRows: "+aes_cryptor.asHex(InverseShiftRows(data)));		
				System.out.println("");
				
				System.out.println("Encrypted: "+aes_cryptor.asHex(Encrypt(data,exkey)));
				System.out.println("Decrypted: "+aes_cryptor.asHex(Decrypt(data,exkey)));
				if (flag) break;
			case 2:
				try {
					java.io.File testfile = new java.io.File("test");
					java.io.File cipherfile = new java.io.File("test.des");
					java.io.File retestfile = new java.io.File("etest");
					ECB_encrypt (testfile, cipherfile, exkey);
					ECB_decrypt (cipherfile, retestfile, exkey);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (flag) break;
			case 3:
				byte[] examkey128 = {
					(byte)0x2b, (byte)0x7e, (byte)0x15, (byte)0x16, (byte)0x28, (byte)0xae,
					(byte)0xd2, (byte)0xa6, (byte)0xab, (byte)0xf7, (byte)0x15, (byte)0x88,
					(byte)0x09, (byte)0xcf, (byte)0x4f, (byte)0x3c};
				byte[][] exkey128 = KeyExpansion (examkey128);
				System.out.println("\n128-bits key");
				for (byte[] row:exkey128)
					System.out.println(aes_cryptor.asHex(row));

				byte[] examkey192 = {
					(byte)0x8e, (byte)0x73, (byte)0xb0, (byte)0xf7, (byte)0xda, (byte)0x0e,
					(byte)0x64, (byte)0x52, (byte)0xc8, (byte)0x10, (byte)0xf3, (byte)0x2b,
					(byte)0x80, (byte)0x90, (byte)0x79, (byte)0xe5, (byte)0x62, (byte)0xf8,
					(byte)0xea, (byte)0xd2, (byte)0x52, (byte)0x2c, (byte)0x6b, (byte)0x7b};
				byte[][] exkey192 = KeyExpansion (examkey192);
				System.out.println("\n192-bits key");
				for (byte[] row:exkey192)
					System.out.println(aes_cryptor.asHex(row));

				byte[] examkey256 = {
					(byte)0x60, (byte)0x3d, (byte)0xeb, (byte)0x10, (byte)0x15, (byte)0xca,
					(byte)0x71, (byte)0xbe, (byte)0x2b, (byte)0x73, (byte)0xae, (byte)0xf0,
					(byte)0x85, (byte)0x7d, (byte)0x77, (byte)0x81, (byte)0x1f, (byte)0x35,
					(byte)0x2c, (byte)0x07, (byte)0x3b, (byte)0x61, (byte)0x08, (byte)0xd7,
					(byte)0x2d, (byte)0x98, (byte)0x10, (byte)0xa3, (byte)0x09, (byte)0x14,
					(byte)0xdf, (byte)0xf4};
				byte[][] exkey256 = KeyExpansion (examkey256);
				System.out.println("\n256-bits key");
				for (byte[] row:exkey256)
					System.out.println(aes_cryptor.asHex(row));
				if (flag) break;
			case 4:
				getRandom(data);
				System.out.println("The data : "+aes_cryptor.asHex(data));

				getRandom(key);
				exkey = KeyExpansion(key);
				System.out.println("128-bits key : "+aes_cryptor.asHex(key));
				System.out.println("Encrypted: "+aes_cryptor.asHex(Encrypt(data,exkey)));
				System.out.println("Decrypted: "+aes_cryptor.asHex(Decrypt(data,exkey)));

				key = new byte[24];
				getRandom(key);
				exkey = KeyExpansion(key);
				System.out.println("192-bits key : "+aes_cryptor.asHex(key));
				System.out.println("Encrypted: "+aes_cryptor.asHex(Encrypt(data,exkey)));
				System.out.println("Decrypted: "+aes_cryptor.asHex(Decrypt(data,exkey)));

				key = new byte[32];
				getRandom(key);
				exkey = KeyExpansion(key);
				System.out.println("256-bits key : "+aes_cryptor.asHex(key));
				System.out.println("Encrypted: "+aes_cryptor.asHex(Encrypt(data,exkey)));
				System.out.println("Decrypted: "+aes_cryptor.asHex(Decrypt(data,exkey)));
				if (flag) break;
			default:
				System.out.println("No such option.");
		}
	}

//------------------------------------------------------------------------------
// AES 加密流程 ECB (展示用)
//------------------------------------------------------------------------------
	/**
	 *	ECB 加密方法
	 */
	static public void ECB_encrypt (java.io.File enf, java.io.File def, byte[][] exkey)
		throws java.io.IOException
	{
		// 處理加密所使用的空間
		byte[] plain	= new byte[16];

		// 要處理的檔案
		java.io.FileInputStream in		= new java.io.FileInputStream (enf);
		java.io.FileOutputStream out	= new java.io.FileOutputStream (def);

		int remind = 0;
		for (long i=0 ; i<enf.length() ; i+=16)
		{
			// 讀取檔案並加密然後寫入
			remind = in.read (plain);
			Encrypt (plain, exkey);
			out.write (plain);
			out.flush ();
		}

		if (DEBUG) {
			System.out.println("ECB encryption tailer=" + remind);
		}
		// 寫入檔尾，表示最後一次的加密時，有多少個byte的資料。
		out.write(remind);
		out.flush();
		out.close();
	}
	
	/**
	 *	ECB 解密方法
	 */
	static public void ECB_decrypt (java.io.File enf, java.io.File def, byte[][] exkey)
		throws java.io.IOException
	{
		// 處理加密所使用的空間
		byte[] ciphe	= new byte[16];

		// 要處理的檔案
		java.io.FileInputStream in		= new java.io.FileInputStream (enf);
		java.io.FileOutputStream out	= new java.io.FileOutputStream (def);

		// 只處理到檔案結尾的前一次加密
		for (long i=0 ; i<enf.length()-17 ; i+=16)
		{
			in.read (ciphe);
			Decrypt (ciphe, exkey);
			out.write (ciphe);
			out.flush ();
		}

		// 最後一次加密要看檔尾資訊來寫入資料。
		in.read (ciphe);
		Decrypt (ciphe, exkey);

		int remind = in.read();
		out.write (ciphe, 0, remind);
		if (DEBUG) {
			System.out.println("ECB encryption tailer=" + remind);
		}
		out.flush ();
		out.close ();		
	}



//------------------------------------------------------------------------------
// AES 加解密使用的方法群
//------------------------------------------------------------------------------
	/**
	 *	複製加密資料區段。
	 *  @param src 從這個資料區段複製到另一個資料區段。
	 *  @param dest 要覆蓋的資料區段。
	 */
	static public void CopyState(byte[] dest, byte[] src) {
		for (int i=0 ; i<src.length ; i++)
			dest[i] = src[i];
	}
	/**
	 *  
	 */
	static public byte[] SubstituteBytes(byte[] aesstate) {
		for (int i=0 ; i<16 ; i++)
			aesstate[i] = Aes_Tables.Sbox[aesstate[i]<0?256+aesstate[i]:aesstate[i]];
		return aesstate;
	}

	/**
	 *  反取代位元組，與取代位元組的不同在於使用的對照表格。
	 *  @param aesstate 將會直接取代這個資料區段的內容。
	 *  @return 取代後的資料區段內容。
	 */
	static public byte[] InverseSubstituteBytes(byte[] aesstate) {
		for (int i=0 ; i<16 ; i++)
			aesstate[i] = Aes_Tables.InvSbox[aesstate[i]<0?256+aesstate[i]:aesstate[i]];
		return aesstate;
	}

	/**
	 *  拓展鎖鑰與資料區段的xor運算。
	 *  @param aesstate 資料區段，修改後的資料將儲存在這。
	 *  @param exkey 拓展鎖鑰。
	 *  @param keyno 鎖鑰號碼，或者稱為第幾回合的AddRoundKey要使用的鎖鑰。
	 *  @return 運算後的資料區段。
	 */
	static public byte[] AddRoundKey (byte[] aesstate, byte[][] exkey, int keyno) {
		for (int i=0 ; i<16 ; i++)
			aesstate[i] ^= exkey[keyno][i];
		return aesstate;
	}

	/**
	 *  
	 */
	static public byte[] InverseAddRoundKey (byte[] aesstate, byte[][] exkey, int keyno) {
		return AddRoundKey (aesstate,exkey,keyno);
	}

	/**
	 *
	 */
	static public byte[] MixColumns(byte[] src, byte[] dest) {
		for (int col=0 ; col<4 ; col++) {
			for (int row=0 ; row<4 ; row++) {
				dest[row*4+col] = (byte)(
					MULTIPLICATION_02(src[row*4+col]) ^
					MULTIPLICATION_03(src[((row+1)%4)*4+col]) ^
					src[((row+2)%4)*4+col] ^
					src[((row+3)%4)*4+col]);
			}
		}
		return dest;
	}

	/**
	 *
	 */
	static public byte[] InverseMixColumns(byte[] src, byte[] dest) {
		for (int col=0 ; col<4 ; col++) {
			for (int row=0 ; row<4 ; row++) {
				dest[row*4+col] = (byte)(
					MULTIPLICATION_0E(src[row*4+col]) ^
					MULTIPLICATION_0B(src[((row+1)%4)*4+col]) ^
					MULTIPLICATION_0D(src[((row+2)%4)*4+col]) ^
					MULTIPLICATION_09(src[((row+3)%4)*4+col]));
			}
		}
		return dest;
	}

	/**
	 *
	 */
	static public byte[] ShiftRows(byte[] dest) {
		// left rotate 1
		byte nowmove = dest[4];
		dest[4] = dest[5];
		dest[5] = dest[6];
		dest[6] = dest[7];
		dest[7] = nowmove;

		// left rotate 2 => 左右吊換
		nowmove = dest[8];
		dest[8] = dest[10];
		dest[10] = nowmove;
		nowmove = dest[9];
		dest[9] = dest[11];
		dest[11] = nowmove;

		// left rotate 3 => right rotate 1
		nowmove = dest[12];
		dest[12] = dest[15];
		dest[15] = dest[14];
		dest[14] = dest[13];
		dest[13] = nowmove;
		return dest;
	}

	/**
	 *
	 */
	static public byte[] InverseShiftRows(byte[] dest) {
		// right rotate 1
		byte nowmove = dest[4];
		dest[4] = dest[7];
		dest[7] = dest[6];
		dest[6] = dest[5];
		dest[5] = nowmove;

		// right rotate 2 => 左右吊換
		nowmove = dest[8];
		dest[8] = dest[10];
		dest[10] = nowmove;
		nowmove = dest[9];
		dest[9] = dest[11];
		dest[11] = nowmove;

		// right rotate 1 = left roate 1
		nowmove = dest[12];
		dest[12] = dest[13];
		dest[13] = dest[14];
		dest[14] = dest[15];
		dest[15] = nowmove;
		return dest;
	}
	 
//------------------------------------------------------------------------------
// AES MixColumns 的數學運算方法
//------------------------------------------------------------------------------
	static final byte GF_MODULOR = (byte)0x1B;
	static private byte MULTIPLICATION_02(byte i) {
		return (LookupTableQuickly)?
			Aes_Tables.Mutipication2[i<0?256+i:i]:
			(byte)(((i<0)?(i<<1)^GF_MODULOR:i<<1)&0xff);
	}
	static private byte MULTIPLICATION_04(byte i) {
		return (byte)(MULTIPLICATION_02(MULTIPLICATION_02(i)));
	}
	static private byte MULTIPLICATION_08(byte i) {
		return (byte)(MULTIPLICATION_02(MULTIPLICATION_02(MULTIPLICATION_02(i))));
	}
	static private byte MULTIPLICATION_03(byte i) {
		return  (LookupTableQuickly)?
			Aes_Tables.Mutipication3[i<0?256+i:i]:
			(byte)(MULTIPLICATION_02(i)^i);
	}
	static private byte MULTIPLICATION_09(byte i) {
		return (LookupTableQuickly)?
			Aes_Tables.Mutipication9[i<0?256+i:i]:
			(byte)(MULTIPLICATION_08(i)^i);
	}
	static private byte MULTIPLICATION_0B(byte i) {
		return (LookupTableQuickly)?
			Aes_Tables.MutipicationB[i<0?256+i:i]:
			(byte)(MULTIPLICATION_08(i)^MULTIPLICATION_02(i)^i);
	}
	static private byte MULTIPLICATION_0D(byte i) {
		return (LookupTableQuickly)?
			Aes_Tables.MutipicationD[i<0?256+i:i]:
			(byte)(MULTIPLICATION_08(i)^MULTIPLICATION_04(i)^i);
	}
	static private byte MULTIPLICATION_0E(byte i) {
		return (LookupTableQuickly)?
			Aes_Tables.MutipicationE[i<0?256+i:i]:
			(byte)(MULTIPLICATION_08(i)^MULTIPLICATION_04(i)^MULTIPLICATION_02(i));
	}
}
