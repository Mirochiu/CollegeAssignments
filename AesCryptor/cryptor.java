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

    private File file;            // �n�[�K���ɮ�
    private mirror_aes aes;        // �@���[�K��
    private aes_cryptor frame;
    
    private String cmode;        // �P�_�[�K�覡
    private int demode;            // �P�_�n�[�K�٬O�ѱK
    private byte[][] exkey;        // �[�ѱK�Τl���_

    public static boolean DEBUG = false;

    /** 
     *  ���F���XGUI����A�������Paes_cryptor��������C
     */
    cryptor(aes_cryptor f) {
        frame = f;
        aes = new mirror_aes();
    }

    /**
     *  �]�w�ɮת��[�K��H�C
     *  @param f �n�[�K���ɮסC
     */
    public void setFile(File f) {
        file = f;
    }

    /**
     *  �^�ǬO�_���ɮ׳Q����C
     *  @return �ɮ׬O�_����C
     */
    public boolean isSelectedFile() {
        return (file == null)?false:true;
    }

    /**
     *  �]�w�[�ѱK�����_�C
     *  @param key �]�w���_�A���_���צ��T�ءA���O�O16�B24�B32bytes�C
     */
    public void setKey(byte[] key) {
        if (key.length!=KeyLen128bits && key.length!=KeyLen192bits && key.length!=KeyLen256bits)
            throw new IllegalArgumentException("cryptor::setMode() failed");
        else
            // ���ͤl���_�C
            exkey = aes.KeyExpansion(key);
    }

    /**
     *  �]�w�ϥΪ��[�K�Ҧ��C
     *  @param mode �C�ؼҦ��CECB�BCBC�BCTR�BCFB1�BCFB8�BOFB1�BOFB8�C
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
     *  �]�w�[�K���Ҧ�(�[�K�θѱK)�C
     *  @param mode DECRYPT��ENCRYPT�C
     */
    public void setCipher(int mode) {
        if (mode!=DECRYPT && mode!=ENCRYPT)
            throw new IllegalArgumentException("cryptor::setCipher() failed");
        else
            demode = mode;
    }
    
    /**
     *  ���o�ثe�n�i�檺�[�K���Ҧ��C
     *  @return �[�K���ثe���Ҧ��C
     */
    public int getCipher() {
        return demode;
    }

    /**
     *  �h������A�B�Τ@���[�K���B�z�U�إ[�K�Ҧ��C
     */
    public void run() {
        if (DEBUG) System.out.println("cryptor::run() start");
        try {
            // �B��[�ѱK
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
                // �̫�]�w�i�צC��100%
                frame.setProgress(100);
                JOptionPane.showMessageDialog (frame, "���߱z�I�[�K�@�~���\�F*^_^*", "�[�K����",
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

                // �̫�]�w�i�צC��100%
                frame.setProgress(100);
                JOptionPane.showMessageDialog (frame, "���߱z�I�ѱK�@�~���\�F*^_^*", "�ѱK����",
                    JOptionPane.INFORMATION_MESSAGE );
            }
            else {
                System.err.println("���~:�[�K�]�w���~�A�L�k�����[�K�@�~�C");
            }
        }
        catch (IOException ioe) {
            if (DEBUG) ioe.printStackTrace();
            JOptionPane.showMessageDialog (frame, 
                "�[/�ѱK�ɵo�Ϳ��~�C\n"+ioe.getMessage(),
                "�o�Ϳ��~", JOptionPane.ERROR_MESSAGE );            
        }
        catch (Exception e) {
            if (DEBUG) e.printStackTrace();
            if ("StopAction".equals(e.getMessage())) {
                JOptionPane.showMessageDialog (frame, "�ʧ@�Q�Q�פ�", "�[�ѱK�@�~���_",
                    JOptionPane.INFORMATION_MESSAGE );
            }
            else {
                if (DEBUG) e.printStackTrace();
                JOptionPane.showMessageDialog (frame, "�[/�ѱK�ɵo�Ϳ��~�A�����ʧ@",
                    "�o�Ϳ��~", JOptionPane.ERROR_MESSAGE );
            }
        }
        frame.setEnabled(true);
        if (DEBUG) System.out.println("cryptor::run() END");
    }

    /**
     *    ECB �[�K��k
     */
    private void ECB_encrypt() throws IOException,Exception {
        long length = file.length();            // ���o�ɮת���
        long setprogress = length/100;            // �M�w����ɭԭn�]�w�i�צC
        byte[] plaintext = new byte[16];        // �n�[�K�����        
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".ecb"));

        int remind = 0;
        for (long i=0 ; i<length ; i+=16) 
        {
            // Ū���ɮרå[�K�M��g�J
            remind = inf.read(plaintext);
            aes.Encrypt(plaintext,exkey);
            outf.write(plaintext); 
            outf.flush();

            // �]�wGUI�i�צC
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // �g�J�ɧ��A��̫ܳ�@�����[�K�ɡA���h�֭�byte����ơC
        outf.write(remind);
        outf.flush();
        outf.close();
    }

    /**
     *    ECB �ѱK��k
     */
    private void ECB_decrypt() throws Exception {
        long length = file.length()-1;            // ���o�ɮת��רô�h�ɧ���1��byte
        long setprogress = length/100;            // �M�w����ɭԭn�]�w�i�צC
        byte[] plaintext = new byte[16];        // �n�ѱK�����
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".de_ecb"));

        // �u�B�z���ɮ׵������e�@���[�K
        for (long i=0 ; i<length-16 ; i+=16)
        {
            inf.read(plaintext);
            aes.Decrypt(plaintext,exkey);
            outf.write(plaintext);
            outf.flush();

            // �]�wGUI�i�צC
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // �̫�@���[�K�n���ɧ���T�Ӽg�J��ơC
        inf.read(plaintext);
        aes.Decrypt(plaintext,exkey);
        outf.write(plaintext,0,inf.read());
        outf.flush();
        outf.close();
    }

    /** 
     *  Cipher Feedback Chaining (CBC)�[�K��k
     */
    private void CBC_encrypt() throws Exception {
        byte[] iv = 
            {(byte)0xC3, (byte)0x92, (byte)0x43, (byte)0xF5, 
            (byte)0xD4, (byte)0xE2,(byte)0x66, (byte)0x21, 
            (byte)0x47, (byte)0xB8, (byte)0xE7, (byte)0x14,
            (byte)0x59, (byte)0x87, (byte)0x87, (byte)0x32};
        long length = file.length();            // ���o�ɮת���
        long setprogress = length/100;            // �M�w����ɭԭn�]�w�i�צC
        byte[] plaintext = new byte[16];        // �n�[�K�����        
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".cbc"));

        int remind = 0;
        for (long i=0 ; i<length ; i+=16) 
        {
            // Ū����ơA�Niv�P��ư�xor�B��C
            remind = inf.read(plaintext);
            xor2State(plaintext,iv);

            // �[�K�üg�J�ɮ�
            aes.Encrypt(plaintext,exkey);
            outf.write(plaintext); 
            outf.flush();
            
            // �N�[�K���G�]���s��iv��
            aes.CopyState(iv,plaintext);

            // �]�wGUI�i�צC
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // �g�J�ɧ��A��̫ܳ�@�����[�K�ɡA���h�֭�byte����ơC
        outf.write(remind);
        outf.flush();
        outf.close();
    }
    
    private void xor2State(byte[] dest, byte[] src) {
        for (int i=0 ; i<src.length ; i++)
            dest[i] ^= src[i];
    }

    /** 
     *  Cipher Feedback Chaining (CBC)�ѱK��k
     */
    private void CBC_decrypt() throws Exception {
        byte[] iv = 
            {(byte)0xC3, (byte)0x92, (byte)0x43, (byte)0xF5, 
            (byte)0xD4, (byte)0xE2,(byte)0x66, (byte)0x21, 
            (byte)0x47, (byte)0xB8, (byte)0xE7, (byte)0x14,
            (byte)0x59, (byte)0x87, (byte)0x87, (byte)0x32};
        byte[] iv2 = new byte[16];
        long length = file.length()-1;            // ���o�ɮת��רô�h�ɧ���1��byte
        long setprogress = length/100;            // �M�w����ɭԭn�]�w�i�צC
        byte[] plaintext = new byte[16];        // �n�ѱK�����
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".de_cbc"));

        // �u�B�z���ɮ׵������e�@���[�K
        int remind = 0;
        for (long i=0 ; i<length-16 ; i+=16)
        {
            // Ū�����, �ñN�n�ѱK����ƽƻs�@����iv2
            remind = inf.read(plaintext);
            aes.CopyState(iv2,plaintext);

            // �ѱK�ûPiv�@xor�B��
            aes.Decrypt(plaintext,exkey);
            xor2State(plaintext,iv);
            outf.write(plaintext); 
            outf.flush();

            // �Niv2�Ȧs����ƽƻs�iiv�A�����U���ѱK��iv
            aes.CopyState(iv,iv2);

            // �]�wGUI�i�צC
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // �̫�@���[�K�n���ɧ���T�Ӽg�J��ơC
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
        long length = file.length();            // ���o�ɮת���
        long setprogress = length/100;            // �M�w����ɭԭn�]�w�i�צC
        byte[] plaintext = new byte[16];        // �n�[�K�����        
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
            // �]�wGUI�i�צC
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // �g�J�ɧ��A��̫ܳ�@�����[�K�ɡA���h�֭�byte����ơC
        outf.write(remind);
        outf.flush();
        outf.close();
    }
    
    private void CTR_decrypt() throws Exception {
        byte[] cnttmp = new byte[16];
        byte[] counter = new byte[16];
        long length = file.length()-1;            // ���o�ɮת��רô�h�ɧ���1��byte
        long setprogress = length/100;            // �M�w����ɭԭn�]�w�i�צC
        byte[] plaintext = new byte[16];        // �n�ѱK�����
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".de_ctr"));

        // �u�B�z���ɮ׵������e�@���[�K
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
            // �]�wGUI�i�צC
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // �̫�@���[�K�n���ɧ���T�Ӽg�J��ơC
        
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
        long length = file.length();            // ���o�ɮת���
        long setprogress = length/100;            // �M�w����ɭԭn�]�w�i�צC
        byte[] plaintext = new byte[16];        // �n�[�K�����        
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".cfb8"));

        int remind = 0;
        for (long i=0 ; i<length ; i+=16)
        {
            remind = inf.read (plaintext);
            for (int j=0 ; j<remind ; j++ ) {
                aes.CopyState (iv2, iv);        // �NIV���ƻs�@����iv2
                
                aes.Encrypt (iv, exkey);    // iv�[�K��N���O�쥻��iv�F
                plaintext[j] ^= iv[0];

                // �]��iv���Oiv�A�ҥH��iv2�Ȧs��iv��ơA
                // �i�楪��1-byte�æb�̫�@��byte����J����xor�B�⪺��ơC
                for (int shift=1 ; shift<16 ; shift++)
                    iv2 [shift] = iv2 [shift-1];
                iv2 [15] = plaintext[j];

                aes.CopyState (iv, iv2);        // �N�������᪺iv2��Ʃ�^iv�A�i��U���@�~
            }
            outf.write (plaintext, 0, remind);
            outf.flush ();

            // �]�wGUI�i�צC
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // CFB�O���ݭn�K�[�ɧ����[�K�y�{�A�ҥH�h���C
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
        long length = file.length();            // ���o�ɮת���
        long setprogress = length/100;            // �M�w����ɭԭn�]�w�i�צC
        byte[] plaintext = new byte[16];        // �n�[�K�����        
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+".de_cfb8"));

        // �MCFB�[�K�y�{���P���O�B��xor���ɾ�
        // Ū��Ӫ�ciphertext������ƥ����b�U�@��iv���ϥΡA�ҥH�n���ܬy�{�C
        int remind = 0;
        for (long i=0 ; i<length ; i+=16)
        {
            remind = inf.read (plaintext);
            for (int j=0 ; j<remind  ; j++ ) {
                aes.CopyState (iv2, iv);        // �ѩ�iv�[�K��N���O�쥻��iv�F
                aes.Encrypt (iv, exkey);    // �ҥH�Niv���ƻs�@����iv2

                // ����1-byte�å��NŪ��Ӫ���ƥ[��iv��
                for (int shift=1 ; shift<16 ; shift++)
                    iv2 [shift] = iv2 [shift-1];
                iv2 [15] = plaintext[j];

                plaintext[j] ^= iv[0];
                aes.CopyState (iv, iv2);        // �N�������᪺iv2��Ʃ�^iv�A�i��U���@�~
            }
            outf.write (plaintext, 0, remind);
            outf.flush ();

            // �]�wGUI�i�צC
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // �P�[�K�A�����ɧ��C
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
        long length = file.length();            // ���o�ɮת���
        long setprogress = length/100;            // �M�w����ɭԭn�]�w�i�צC
        byte[] plaintext = new byte[16];        // �n�[�K�����        
        FileInputStream inf =
            new FileInputStream(file);
        FileOutputStream outf = 
            new FileOutputStream(new File(file.getPath()+suffix));

        // OFB�PCFB�̤j�����P�N�O�Aiv�[�K���N�^���e1-byte��ƶ�^�hiv
        int remind = 0;
        for (long i=0 ; i<length ; i+=16)
        {
            remind = inf.read (plaintext);
            for (int j=0 ; j<remind ; j++ ) {
                aes.CopyState (iv2, iv);        // �ѩ�iv�[�K��N���O�쥻��iv�F        
                aes.Encrypt (iv, exkey);    // �ҥH�Niv���ƻs�@����iv2

                for (int shift=1 ; shift<16 ; shift++)
                    iv2 [shift] = iv2 [shift-1];
                iv2 [15] = iv[0];

                plaintext[j] ^= iv[0];
                aes.CopyState (iv, iv2);        // �N�������᪺iv2��Ʃ�^iv�A�i��U���@�~
            }
            outf.write (plaintext, 0, remind);
            outf.flush ();

            // �]�wGUI�i�צC
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        // OFB�P�ˤ��ݭn�ɧ�
        // outf.write(remind);
        // outf.flush();
        outf.close();
    }

    private void OFB8_encrypt() throws Exception {
        OFB8_encrypt(".ofb8");
    }
    
    // OFB�ѱK�����Q�Υ[�K���N�i�H�F�A���F�[�K�y�{�ۦP�H�~�A�٦��N�O���ݭn�B�z�ɧ��A�G�����Q�ΡC
    private void OFB8_decrypt() throws Exception {
        OFB8_encrypt(".de_ofb8");
    }

    // CFB-1�D�n�N�O�b�[�K���a��ʤ�}
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
                plaintext[j/8] ^= (iv[0]<0)?(0x80>>(j%8)):0;    // plaintext�x�s�B�z�n�P���B�z�����

                for (int shift=1 ; shift<16 ; shift++) {
                    iv2 [shift-1] <<= 1;
                    if (iv2 [shift] < 0) {
                        iv2 [shift-1] |= 0x01;
                    }
                }
                // �q�[�K�n��bit���X�ӹL�{����·�, �Υb�p��b�������覡�C
                // �Q�ξB�n�B�X�ڭ̭n��bit�A�M��A�ݬO0�٬O1�C
                iv2[15] <<= 1;
                iv2[15] |= ((plaintext[j/8]&(0x80>>(j%8)))==0)?0x00:0x01;

                aes.CopyState (iv, iv2);        // �N�������᪺iv2��Ʃ�^iv�A�i��U���@�~
            }
            outf.write (plaintext, 0, remind);
            outf.flush ();

            // �]�wGUI�i�צC
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

                // �q�ɮר��o����ƭn��iv�ΡA�S�n�ѱK�ΡA���ڤS�Q�Nplaintext��@�B�z�n�����
                // �ҥH��������plaintext�� iv ����~�i��ѱK�C
                for (int shift=1 ; shift<16 ; shift++) {
                    iv2 [shift-1] <<= 1;
                    if (iv2 [shift] < 0) {
                        iv2 [shift-1] |= 0x01;
                    }
                }
                // �Q�ξB�n�B�X�ڭ̭n��bit�A�M��A�ݬO0�٬O1�C
                iv2[15] <<= 1;
                iv2[15] |= ((plaintext[j/8] & (0x80>>(j%8))) == 0)?0x00:0x01;

                aes.Encrypt (iv, exkey);
                plaintext[j/8] ^= (iv[0]<0)?(0x80>>(j%8)):0x00;

                aes.CopyState (iv, iv2);        // �N�������᪺iv2��Ʃ�^iv�A�i��U���@�~
            }
            outf.write (plaintext, 0, remind);
            outf.flush ();

            // �]�wGUI�i�צC
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
            for (int j=0 ; j<remind*8 ; j++ ) {    // ���W8�O�]�� 1�ӭ�bit�n�ӧ@�[�K
                aes.CopyState (iv2, iv);
                aes.Encrypt (iv, exkey);

                // �p��s�N��ܻ� - ��ܲŸ����Ĥ@��bit��1�A�Φ��ӧP�_�O�_�n�N�᭱��Ʃ��e�[
                for (int shift=1 ; shift<16 ; shift++) {
                    iv2 [shift-1] <<= 1;
                    if (iv2 [shift] < 0) {
                        iv2 [shift-1] |= 0x01;
                    }
                }
                // �]���O�@��bit�C�C�@�A�ҥH�o�̱N��ƨ��X�ӳB�z�@�U
                // ���L�O�I�]�O�ΤW������k�N�n�F�A�o�ˤ����XD
                iv2[15] <<= 1;
                iv2[15] |= (iv[0] < 0)?0x01:0x00;

                // �o�̤]�O�ΤW�����P�_�ޥ��A���L�O�����إ߸�ƶi��B�� 
                // ��o�˰��O�]����ƩM0�i��xor�B���٬O��ơC
                plaintext[j/8] ^= (iv[0]<0)?(0x80>>(j%8)):0x00;
                aes.CopyState (iv, iv2);
            }
            outf.write (plaintext, 0, remind);
            outf.flush ();

            // �]�wGUI�i�צC
            if (i%setprogress == 0)
                frame.setProgress((int)(i/setprogress));
        }

        outf.close();
    }
    
    private void OFB1_encrypt() throws Exception {
        OFB1_encrypt(".ofb1");
    }
    
    // �����I�s���z�Ѹ�OFB8�ۦP�A�O�[�ѱK�y�{�����ۦP���t�G�C
    private void OFB1_decrypt() throws Exception {
        OFB1_encrypt(".de_ofb1");
    }
}
