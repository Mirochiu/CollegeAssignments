import javax.swing.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;

public class cryptor extends Thread
{
    static final int KeyLen128bits = 16;
    static final int KeyLen192bits = 24;
    static final int KeyLen256bits = 32;

    static final int ENCRYPT = Cipher.ENCRYPT_MODE;
    static final int DECRYPT = Cipher.DECRYPT_MODE;
    static final String ECB = "ECB";
    static final String CBC = "CBC";
    static final String CTR = "CTR";
    static final String CFB1 = "CFB-1";
    static final String CFB8 = "CFB-8";
    static final String OFB1 = "OFB-1";
    static final String OFB8 = "OFB-8";

    private File file;            // 要加密的檔案
    private mirror_aes aes;        // 一次加密器
    private aes_cryptor frame;
    
    private String cmode;        // 判斷加密方式
    private int demode;            // 判斷要加密還是解密
    private byte[][] exkey;        // 加解密用子鎖鑰

    public static boolean DEBUG = false;

    /** 
     *  為了結合GUI元件，必須給與aes_cryptor視窗元件。
     */
    cryptor(aes_cryptor f) {
        frame = f;
        aes = new mirror_aes();
    }

    /**
     *  設定檔案的加密對象。
     *  @param f 要加密的檔案。
     */
    public void setFile(File f) {
        file = f;
    }

    /**
     *  回傳是否有檔案被選取。
     *  @return 檔案是否選取。
     */
    public boolean isSelectedFile() {
        return (file == null)?false:true;
    }

    /**
     *  設定加解密用鎖鑰。
     *  @param key 設定鎖鑰，鎖鑰長度有三種，分別是16、24、32bytes。
     */
    public void setKey(byte[] key) {
        if (key.length!=KeyLen128bits && key.length!=KeyLen192bits && key.length!=KeyLen256bits)
            throw new IllegalArgumentException("cryptor::setMode() failed");
        else
            // 產生子鎖鑰。
            exkey = aes.KeyExpansion(key);
    }

    /**
     *  設定使用的加密模式。
     *  @param mode 七種模式。ECB、CBC、CTR、CFB1、CFB8、OFB1、OFB8。
     */
    public void setMode(String mode) {
        if ( mode.equals(ECB)==false &&
            mode.equals(CBC)==false &&
            mode.equals(CTR)==false &&
            mode.equals(CFB1)==false &&
            mode.equals(CFB8)==false &&
            mode.equals(OFB1)==false &&
            mode.equals(OFB8)==false )
            throw new IllegalArgumentException("cryptor::setMode() failed");
        else
            cmode = mode;
    }

    /**
     *  設定加密器模式(加密或解密)。
     *  @param mode DECRYPT或ENCRYPT。
     */
    public void setCipher(int mode) {
        if (mode!=DECRYPT && mode!=ENCRYPT)
            throw new IllegalArgumentException("cryptor::setCipher() failed");
        else
            demode = mode;
    }
    
    /**
     *  取得目前要進行的加密器模式。
     *  @return 加密器目前的模式。
     */
    public int getCipher() {
        return demode;
    }

    /**
     *  多執行緒，運用一次加密器處理各種加密模式。
     */
    public void run() {
        if (DEBUG) System.out.println("cryptor::run() start");
        try {
            // 運行加解密
            if (demode == Cipher.ENCRYPT_MODE) {
                if (ECB.equals(cmode))
                    ECB_encrypt();
                else if (CBC.equals(cmode))
                    CBC_encrypt();
                else if (CTR.equals(cmode))
                    CTR_encrypt();
                else if (CFB8.equals(cmode))
                    CFB8_encrypt();
                else if (OFB8.equals(cmode))
                    OFB8_encrypt();
                else if (CFB1.equals(cmode))
                    CFB1_encrypt();
                else if (OFB1.equals(cmode))
                    OFB1_encrypt();
                else
                    System.err.println("??? mode");
                // 最後設定進度列為100%
                frame.setProgress(100);
                JOptionPane.showMessageDialog (frame, "恭喜您！加密作業成功了*^_^*", "加密完成",
                    JOptionPane.INFORMATION_MESSAGE );
            }
            else if (demode == Cipher.DECRYPT_MODE) {
                if (ECB.equals(cmode))
                    ECB_decrypt();
                else if (CBC.equals(cmode))
                    CBC_decrypt();
                else if (CTR.equals(cmode))
                    CTR_decrypt();
                else if (CFB8.equals(cmode))
                    CFB8_decrypt();
                else if (OFB8.equals(cmode))
                    OFB8_decrypt();
                else if (CFB1.equals(cmode))
                    CFB1_decrypt();
                else if (OFB1.equals(cmode))
                    OFB1_decrypt();
                else
                    System.err.println("??? mode");

                // 最後設定進度列為100%
                frame.setProgress(100);
                JOptionPane.showMessageDialog (frame, "恭喜您！解密作業成功了*^_^*", "解密完成",
                    JOptionPane.INFORMATION_MESSAGE );
            }
            else {
                System.err.println("錯誤:加密設定錯誤，無法完成加密作業。");
            }
        }
        catch (IOException ioe) {
            if (DEBUG) ioe.printStackTrace();
            JOptionPane.showMessageDialog (frame, 
                "加/解密時發生錯誤。\n"+ioe.getMessage(),
                "發生錯誤", JOptionPane.ERROR_MESSAGE );            
        }
        catch (Exception e) {
            if (DEBUG) e.printStackTrace();
            if ("StopAction".equals(e.getMessage())) {
                JOptionPane.showMessageDialog (frame, "動作被被終止", "加解密作業中斷",
                    JOptionPane.INFORMATION_MESSAGE );
            }
            else {
                if (DEBUG) e.printStackTrace();
                JOptionPane.showMessageDialog (frame, "加/解密時發生錯誤，取消動作",
                    "發生錯誤", JOptionPane.ERROR_MESSAGE );
            }
        }
        frame.setEnabled(true);
        if (DEBUG) System.out.println("cryptor::run() END");
    }

    /**
     *    ECB 加密方法
     */
    private void ECB_encrypt() throws IOException,Exception {
        long length = file.length();            // 取得檔案長度
        long setprogress = length/100;            // 決定什麼時候要設定進度列
        byte[] plaintext = new byte[16];        // 要加密的資料        
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".ecb"));

        int remind = 0;
        for (long i=0 ; i<length ; i+=16) 
        {
            // 讀取檔案並加密然後寫入
            remind = inf.read(plaintext);
            aes.Encrypt(plaintext,exkey);
            outf.write(plaintext); 
            outf.flush();

            // 設定GUI進度列
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // 寫入檔尾，表示最後一次的加密時，有多少個byte的資料。
        outf.write(remind);
        outf.flush();
        outf.close();
    }

    /**
     *    ECB 解密方法
     */
    private void ECB_decrypt() throws Exception {
        long length = file.length()-1;            // 取得檔案長度並減去檔尾的1個byte
        long setprogress = length/100;            // 決定什麼時候要設定進度列
        byte[] plaintext = new byte[16];        // 要解密的資料
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".de_ecb"));

        // 只處理到檔案結尾的前一次加密
        for (long i=0 ; i<length-16 ; i+=16)
        {
            inf.read(plaintext);
            aes.Decrypt(plaintext,exkey);
            outf.write(plaintext);
            outf.flush();

            // 設定GUI進度列
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // 最後一次加密要看檔尾資訊來寫入資料。
        inf.read(plaintext);
        aes.Decrypt(plaintext,exkey);
        outf.write(plaintext,0,inf.read());
        outf.flush();
        outf.close();
    }

    /** 
     *  Cipher Feedback Chaining (CBC)加密方法
     */
    private void CBC_encrypt() throws Exception {
        byte[] iv = 
            {(byte)0xC3, (byte)0x92, (byte)0x43, (byte)0xF5, 
            (byte)0xD4, (byte)0xE2,(byte)0x66, (byte)0x21, 
            (byte)0x47, (byte)0xB8, (byte)0xE7, (byte)0x14,
            (byte)0x59, (byte)0x87, (byte)0x87, (byte)0x32};
        long length = file.length();            // 取得檔案長度
        long setprogress = length/100;            // 決定什麼時候要設定進度列
        byte[] plaintext = new byte[16];        // 要加密的資料        
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".cbc"));

        int remind = 0;
        for (long i=0 ; i<length ; i+=16) 
        {
            // 讀取資料，將iv與資料做xor運算。
            remind = inf.read(plaintext);
            xor2State(plaintext,iv);

            // 加密並寫入檔案
            aes.Encrypt(plaintext,exkey);
            outf.write(plaintext); 
            outf.flush();
            
            // 將加密結果設成新的iv值
            aes.CopyState(iv,plaintext);

            // 設定GUI進度列
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // 寫入檔尾，表示最後一次的加密時，有多少個byte的資料。
        outf.write(remind);
        outf.flush();
        outf.close();
    }
    
    private void xor2State(byte[] dest, byte[] src) {
        for (int i=0 ; i<src.length ; i++)
            dest[i] ^= src[i];
    }

    /** 
     *  Cipher Feedback Chaining (CBC)解密方法
     */
    private void CBC_decrypt() throws Exception {
        byte[] iv = 
            {(byte)0xC3, (byte)0x92, (byte)0x43, (byte)0xF5, 
            (byte)0xD4, (byte)0xE2,(byte)0x66, (byte)0x21, 
            (byte)0x47, (byte)0xB8, (byte)0xE7, (byte)0x14,
            (byte)0x59, (byte)0x87, (byte)0x87, (byte)0x32};
        byte[] iv2 = new byte[16];
        long length = file.length()-1;            // 取得檔案長度並減去檔尾的1個byte
        long setprogress = length/100;            // 決定什麼時候要設定進度列
        byte[] plaintext = new byte[16];        // 要解密的資料
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".de_cbc"));

        // 只處理到檔案結尾的前一次加密
        int remind = 0;
        for (long i=0 ; i<length-16 ; i+=16)
        {
            // 讀取資料, 並將要解密的資料複製一份到iv2
            remind = inf.read(plaintext);
            aes.CopyState(iv2,plaintext);

            // 解密並與iv作xor運算
            aes.Decrypt(plaintext,exkey);
            xor2State(plaintext,iv);
            outf.write(plaintext); 
            outf.flush();

            // 將iv2暫存的資料複製進iv，成為下次解密的iv
            aes.CopyState(iv,iv2);

            // 設定GUI進度列
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // 最後一次加密要看檔尾資訊來寫入資料。
        inf.read(plaintext);
        aes.Decrypt(plaintext,exkey);
        xor2State(plaintext,iv);
        outf.write(plaintext,0,inf.read());
        outf.flush();
        outf.close();
    }
    
    private void addCounter (byte[] cnt) {
        for (int i=15 ; i>=0 ; i--) {
            if ( ++cnt[i] != 0 ) break;
        }
    }
    
    private void CTR_encrypt() throws Exception {
        byte[] cnttmp = new byte[16];
        byte[] counter = new byte[16];
        long length = file.length();            // 取得檔案長度
        long setprogress = length/100;            // 決定什麼時候要設定進度列
        byte[] plaintext = new byte[16];        // 要加密的資料        
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".ctr"));

        int remind = 0;
        for (long i=0 ; i<length ; i+=16) 
        {
            aes.CopyState (cnttmp, counter);

            aes.Encrypt (counter, exkey);
            remind = inf.read (plaintext);
            xor2State (plaintext, counter);
            outf.write (plaintext);
            outf.flush ();

            aes.CopyState (counter, cnttmp);
            addCounter (counter);
            // 設定GUI進度列
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // 寫入檔尾，表示最後一次的加密時，有多少個byte的資料。
        outf.write(remind);
        outf.flush();
        outf.close();
    }
    
    private void CTR_decrypt() throws Exception {
        byte[] cnttmp = new byte[16];
        byte[] counter = new byte[16];
        long length = file.length()-1;            // 取得檔案長度並減去檔尾的1個byte
        long setprogress = length/100;            // 決定什麼時候要設定進度列
        byte[] plaintext = new byte[16];        // 要解密的資料
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".de_ctr"));

        // 只處理到檔案結尾的前一次加密
        for (long i=0 ; i<length-16 ; i+=16)
        {
            aes.CopyState (cnttmp, counter);

            aes.Encrypt(counter,exkey);
            inf.read(plaintext);
            xor2State (plaintext, counter);
            outf.write(plaintext);
            outf.flush();

            aes.CopyState (counter, cnttmp);
            addCounter (counter);
            // 設定GUI進度列
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // 最後一次加密要看檔尾資訊來寫入資料。
        
        aes.Encrypt(counter,exkey);
        inf.read(plaintext);
        xor2State (plaintext, counter);
        outf.write(plaintext,0,inf.read());
        outf.flush();
        outf.close();
    }

    private void CFB8_encrypt() throws Exception {
        byte[] iv =
            {(byte)0xC3, (byte)0x92, (byte)0x43, (byte)0xF5, 
            (byte)0xD4, (byte)0xE2,(byte)0x66, (byte)0x21, 
            (byte)0x47, (byte)0xB8, (byte)0xE7, (byte)0x14,
            (byte)0x59, (byte)0x87, (byte)0x87, (byte)0x32};
        byte[] iv2 = new byte[16];
        long length = file.length();            // 取得檔案長度
        long setprogress = length/100;            // 決定什麼時候要設定進度列
        byte[] plaintext = new byte[16];        // 要加密的資料        
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".cfb8"));

        int remind = 0;
        for (long i=0 ; i<length ; i+=16)
        {
            remind = inf.read (plaintext);
            for (int j=0 ; j<remind ; j++ ) {
                aes.CopyState (iv2, iv);        // 將IV先複製一份到iv2
                
                aes.Encrypt (iv, exkey);    // iv加密後就不是原本的iv了
                plaintext[j] ^= iv[0];

                // 因為iv不是iv，所以用iv2暫存的iv資料，
                // 進行左移1-byte並在最後一個byte中填入做完xor運算的資料。
                for (int shift=1 ; shift<16 ; shift++)
                    iv2 [shift] = iv2 [shift-1];
                iv2 [15] = plaintext[j];

                aes.CopyState (iv, iv2);        // 將左移之後的iv2資料放回iv，進行下次作業
            }
            outf.write (plaintext, 0, remind);
            outf.flush ();

            // 設定GUI進度列
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // CFB是不需要添加檔尾的加密流程，所以去除。
        // outf.write(remind);
        // outf.flush();
        outf.close();
    }

    private void CFB8_decrypt() throws Exception {
        byte[] iv =
            {(byte)0xC3, (byte)0x92, (byte)0x43, (byte)0xF5, 
            (byte)0xD4, (byte)0xE2,(byte)0x66, (byte)0x21, 
            (byte)0x47, (byte)0xB8, (byte)0xE7, (byte)0x14,
            (byte)0x59, (byte)0x87, (byte)0x87, (byte)0x32};
        byte[] iv2 = new byte[16];
        long length = file.length();            // 取得檔案長度
        long setprogress = length/100;            // 決定什麼時候要設定進度列
        byte[] plaintext = new byte[16];        // 要加密的資料        
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".de_cfb8"));

        // 和CFB加密流程不同的是運行xor的時機
        // 讀近來的ciphertext部份資料必須在下一次iv中使用，所以要改變流程。
        int remind = 0;
        for (long i=0 ; i<length ; i+=16)
        {
            remind = inf.read (plaintext);
            for (int j=0 ; j<remind  ; j++ ) {
                aes.CopyState (iv2, iv);        // 由於iv加密後就不是原本的iv了
                aes.Encrypt (iv, exkey);    // 所以將iv先複製一份到iv2

                // 左移1-byte並先將讀近來的資料加到iv中
                for (int shift=1 ; shift<16 ; shift++)
                    iv2 [shift] = iv2 [shift-1];
                iv2 [15] = plaintext[j];

                plaintext[j] ^= iv[0];
                aes.CopyState (iv, iv2);        // 將左移之後的iv2資料放回iv，進行下次作業
            }
            outf.write (plaintext, 0, remind);
            outf.flush ();

            // 設定GUI進度列
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // 同加密，不須檔尾。
        // outf.write(remind);
        // outf.flush();
        outf.close();
    }
    
    private void OFB8_encrypt(String suffix) throws Exception {
        byte[] iv =
            {(byte)0xC3, (byte)0x92, (byte)0x43, (byte)0xF5, 
            (byte)0xD4, (byte)0xE2,(byte)0x66, (byte)0x21, 
            (byte)0x47, (byte)0xB8, (byte)0xE7, (byte)0x14,
            (byte)0x59, (byte)0x87, (byte)0x87, (byte)0x32};
        byte[] iv2 = new byte[16];
        long length = file.length();            // 取得檔案長度
        long setprogress = length/100;            // 決定什麼時候要設定進度列
        byte[] plaintext = new byte[16];        // 要加密的資料        
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+suffix));

        // OFB與CFB最大的不同就是，iv加密完就擷取前1-byte資料填回去iv
        int remind = 0;
        for (long i=0 ; i<length ; i+=16)
        {
            remind = inf.read (plaintext);
            for (int j=0 ; j<remind ; j++ ) {
                aes.CopyState (iv2, iv);        // 由於iv加密後就不是原本的iv了        
                aes.Encrypt (iv, exkey);    // 所以將iv先複製一份到iv2

                for (int shift=1 ; shift<16 ; shift++)
                    iv2 [shift] = iv2 [shift-1];
                iv2 [15] = iv[0];

                plaintext[j] ^= iv[0];
                aes.CopyState (iv, iv2);        // 將左移之後的iv2資料放回iv，進行下次作業
            }
            outf.write (plaintext, 0, remind);
            outf.flush ();

            // 設定GUI進度列
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // OFB同樣不需要檔尾
        // outf.write(remind);
        // outf.flush();
        outf.close();
    }

    private void OFB8_encrypt() throws Exception {
        OFB8_encrypt(".ofb8");
    }
    
    // OFB解密直接利用加密的就可以了，除了加密流程相同以外，還有就是不需要處理檔尾，故直接利用。
    private void OFB8_decrypt() throws Exception {
        OFB8_encrypt(".de_ofb8");
    }

    // CFB-1主要就是在加密的地方動手腳
    private void CFB1_encrypt() throws Exception {
        byte[] iv =
            {(byte)0xC3, (byte)0x92, (byte)0x43, (byte)0xF5, 
            (byte)0xD4, (byte)0xE2,(byte)0x66, (byte)0x21, 
            (byte)0x47, (byte)0xB8, (byte)0xE7, (byte)0x14,
            (byte)0x59, (byte)0x87, (byte)0x87, (byte)0x32};
        byte[] iv2 = new byte[16];
        long length = file.length();
        long setprogress = length/100;
        byte[] plaintext = new byte[16];
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".cfb1"));

        int remind = 0;
        for (long i=0 ; i<length ; i+=16)
        {
            remind = inf.read (plaintext);
            for (int j=0 ; j<remind*8 ; j++ ) {
                aes.CopyState (iv2, iv);

                aes.Encrypt (iv, exkey);
                plaintext[j/8] ^= (iv[0]<0)?(0x80>>(j%8)):0;    // plaintext儲存處理好與未處理的資料

                for (int shift=1 ; shift<16 ; shift++) {
                    iv2 [shift-1] <<= 1;
                    if (iv2 [shift] < 0) {
                        iv2 [shift-1] |= 0x01;
                    }
                }
                // 從加密好的bit取出來過程比較麻煩, 用半計算半偵測的方式。
                // 利用遮罩遮出我們要的bit，然後再看是0還是1。
                iv2[15] <<= 1;
                iv2[15] |= ((plaintext[j/8]&(0x80>>(j%8)))==0)?0x00:0x01;

                aes.CopyState (iv, iv2);        // 將左移之後的iv2資料放回iv，進行下次作業
            }
            outf.write (plaintext, 0, remind);
            outf.flush ();

            // 設定GUI進度列
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        outf.close();
    }

    private void CFB1_decrypt() throws Exception {
        byte[] iv =
            {(byte)0xC3, (byte)0x92, (byte)0x43, (byte)0xF5, 
            (byte)0xD4, (byte)0xE2,(byte)0x66, (byte)0x21, 
            (byte)0x47, (byte)0xB8, (byte)0xE7, (byte)0x14,
            (byte)0x59, (byte)0x87, (byte)0x87, (byte)0x32};
        byte[] iv2 = new byte[16];
        long length = file.length();
        long setprogress = length/100;
        byte[] plaintext = new byte[16];
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".de_cfb1"));

        int remind = 0;
        for (long i=0 ; i<length ; i+=16)
        {
            remind = inf.read (plaintext);
            for (int j=0 ; j<remind*8 ; j++ ) {
                aes.CopyState (iv2, iv);

                // 從檔案取得的資料要給iv用，又要解密用，但我又想將plaintext當作處理好的資料
                // 所以必須先把plaintext給 iv 之後才進行解密。
                for (int shift=1 ; shift<16 ; shift++) {
                    iv2 [shift-1] <<= 1;
                    if (iv2 [shift] < 0) {
                        iv2 [shift-1] |= 0x01;
                    }
                }
                // 利用遮罩遮出我們要的bit，然後再看是0還是1。
                iv2[15] <<= 1;
                iv2[15] |= ((plaintext[j/8] & (0x80>>(j%8))) == 0)?0x00:0x01;

                aes.Encrypt (iv, exkey);
                plaintext[j/8] ^= (iv[0]<0)?(0x80>>(j%8)):0x00;

                aes.CopyState (iv, iv2);        // 將左移之後的iv2資料放回iv，進行下次作業
            }
            outf.write (plaintext, 0, remind);
            outf.flush ();

            // 設定GUI進度列
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        outf.close();
    }
    
    private void OFB1_encrypt(String suffix) throws Exception {
        byte[] iv =
            {(byte)0xC3, (byte)0x92, (byte)0x43, (byte)0xF5, 
            (byte)0xD4, (byte)0xE2,(byte)0x66, (byte)0x21, 
            (byte)0x47, (byte)0xB8, (byte)0xE7, (byte)0x14,
            (byte)0x59, (byte)0x87, (byte)0x87, (byte)0x32};
        byte[] iv2 = new byte[16];
        long length = file.length();
        long setprogress = length/100;
        byte[] plaintext = new byte[16];
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+suffix));

        int remind = 0;
        for (long i=0 ; i<length ; i+=16)
        {
            remind = inf.read (plaintext);
            for (int j=0 ; j<remind*8 ; j++ ) {    // 乘上8是因為 1個個bit要細作加密
                aes.CopyState (iv2, iv);
                aes.Encrypt (iv, exkey);

                // 小於零就表示說 - 表示符號的第一個bit為1，用此來判斷是否要將後面資料往前加
                for (int shift=1 ; shift<16 ; shift++) {
                    iv2 [shift-1] <<= 1;
                    if (iv2 [shift] < 0) {
                        iv2 [shift-1] |= 0x01;
                    }
                }
                // 因為是一個bit慢慢作，所以這裡將資料取出來處理一下
                // 不過呢！也是用上面的方法就好了，這樣比較快XD
                iv2[15] <<= 1;
                iv2[15] |= (iv[0] < 0)?0x01:0x00;

                // 這裡也是用上面的判斷技巧，不過是直接建立資料進行運算 
                // 能這樣做是因為整數和0進行xor運算還是原數。
                plaintext[j/8] ^= (iv[0]<0)?(0x80>>(j%8)):0x00;
                aes.CopyState (iv, iv2);
            }
            outf.write (plaintext, 0, remind);
            outf.flush ();

            // 設定GUI進度列
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        outf.close();
    }
    
    private void OFB1_encrypt() throws Exception {
        OFB1_encrypt(".ofb1");
    }
    
    // 直接呼叫的理由跟OFB8相同，是加解密流程完全相同的緣故。
    private void OFB1_decrypt() throws Exception {
        OFB1_encrypt(".de_ofb1");
    }
}
