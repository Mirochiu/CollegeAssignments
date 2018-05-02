import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

/**
 *  �[�K���ϧΤ����C
 *  @author ���ò�
 */
public class aes_cryptor extends JFrame implements ActionListener {
	private boolean DEBUG = false;		// �O�_��ܰ����T��
	private cryptor InternalData;		// �G���[�K��
    private JFileChooser chooser;		// �ɮ׿�ܵ���
	private int keylen;					// ���_����, ���byte

	/**
	 *  �G�m�w�]����P��l�ƥ[�K���C
	 */
    public aes_cryptor() {
        try {
	        chooser = new JFileChooser();
        	// �G�m����
            jbInit();

			// �]�w��Ͽ�ܶ���
			jRadioButton1.setSelected(true);		
			jRadioButton4.setSelected(true);
	        jRadioButton7.setSelected(true);

	        // �إߥ[�K���ò��Ͱ��ƥ�ӳ]�w�[�K��
			InternalData = new cryptor(this);
	   		this.actionPerformed(
	   		new ActionEvent(jRadioButton1,0,jRadioButton1.getActionCommand()));
   			this.actionPerformed(
	   		new ActionEvent(jRadioButton4,0,jRadioButton4.getActionCommand()));
	   		this.actionPerformed(
   			new ActionEvent(jRadioButton7,0,jRadioButton7.getActionCommand()));
        }
        catch (Exception e) {
        	if (DEBUG) e.printStackTrace();
            System.err.println("���~:��l�Ƶ��������Υ[�K�����ѡA�{�������C");
            System.exit(-1);
        }
    }

	/**
	 *  �B�z�ϥΪ�Ĳ�o���ƥ�A�H�ƥ�R�O�@���B�z�̾ڡC
	 *  @param e Ĳ�o���ƥ�
	 */
	public void actionPerformed(ActionEvent e) {
		String actcmd = e.getActionCommand();
		
		if (DEBUG) System.out.println("aes_cryptor::ActionCommand=" + actcmd);

		//---------------------------------------------------------------------
		// ���_���� 128 ,192 ,256
		//---------------------------------------------------------------------
		if ( actcmd.equals("128-bits") ) {
			keylen = 16;
		}
		else if ( actcmd.equals("192-bits") ) {
			keylen = 24;
		}
		else if ( actcmd.equals("256-bits") ) {
			keylen = 32;
		}
		//---------------------------------------------------------------------
		// �[�K�Ҧ�
		//---------------------------------------------------------------------
		else if ( actcmd.equals("ECB") ) {
			InternalData.setMode(InternalData.ECB);
		}
		else if ( actcmd.equals("CBC") ) {
			InternalData.setMode(InternalData.CBC);
		}
		else if ( actcmd.equals("CTR") ) {
			InternalData.setMode(InternalData.CTR);
		}
		else if ( actcmd.equals("CFB-8") ) {
			InternalData.setMode(InternalData.CFB8);
		}
		else if ( actcmd.equals("OFB-8") ) {
			InternalData.setMode(InternalData.OFB8);
		}
		else if ( actcmd.equals("CFB-1") ) {
			InternalData.setMode(InternalData.CFB1);
		}
		else if ( actcmd.equals("OFB-1") ) {
			InternalData.setMode(InternalData.OFB1);
		}
		//---------------------------------------------------------------------
		// �[/�ѱK�Ҧ�
		//---------------------------------------------------------------------
		else if ( actcmd.equals("Encrypt") ) {
			InternalData.setCipher(InternalData.ENCRYPT);
			jButton1.setText("�}�l�[�K");
		}
		else if ( actcmd.equals("Decrypt") ) {
			InternalData.setCipher(InternalData.DECRYPT);
			jButton1.setText("�}�l�ѱK");
		}
		//---------------------------------------------------------------------
		// �B�~���\�� [�[�t��]
		//---------------------------------------------------------------------
		else if (actcmd.equals("TableQuick")) {
			mirror_aes.LookupTableQuickly = !mirror_aes.LookupTableQuickly;
		}
		//---------------------------------------------------------------------
		// �ϥΪ̤���
		//---------------------------------------------------------------------
		else if ( actcmd.equals("OpenFile") ) {
		    int returnVal = chooser.showOpenDialog(this);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		    	File SelFile = chooser.getSelectedFile();
		    	if (DEBUG) System.out.println("Chosed file=" + SelFile.getPath());

				if (SelFile != null && SelFile.isFile() && SelFile.canWrite()){
					InternalData.setFile(SelFile);
				}
				else {
					JOptionPane.showMessageDialog (this,
						"�z������ɮ׵L�k�ϥΡA�Э��s����ɮסC", "����ɮ׿��~",
						JOptionPane.ERROR_MESSAGE );
				}
		    }
		}
		else if ( actcmd.equals("StartCryption") ) {
			/*
			// DEMO�ε{���X
			new Thread (){
				public void run(){
					setEnabled(false);
					for (int i=0 ; i<=100 ; i++){
						try{ setProgress(i);
							Thread.sleep(50);
						}catch(Exception e){}
					}
					JOptionPane.showMessageDialog (null,
						"�Ȭ��i�ܼ����\��ﶵ�A�|����@�C", "DEMO�����\�୭��",
						JOptionPane.INFORMATION_MESSAGE );
					setEnabled(true);
				}
			}.start();
			*/
			if (InternalData.isSelectedFile() == false) {
				JOptionPane.showMessageDialog (this,
					"�Х�����@���ɮסA�~��i��" + 
					(InternalData.getCipher()==InternalData.DECRYPT?"��":"�[") + "�K�C",
					(InternalData.getCipher()==InternalData.DECRYPT?"��":"�[") + "�K���_",
					JOptionPane.INFORMATION_MESSAGE );
			}
			else {
				setEnabled(false);

				// ��n�i��[�K���ɭԤ~�إ����_�Ŷ��A��֦b��ܮɫإߪ��Ŷ����O�C
				byte[] key = new byte[keylen];
				String keystring = jTextArea1.getText().trim();
				boolean randomkey = false;

				if (isleagalKey(keystring, keylen) == false) {
					// �ѱK�ɤ����ӬO�X�{�۰ʲ��ͩҭn���T���A�ҥH�K�[���ץ��C
					if (InternalData.getCipher() == InternalData.DECRYPT) {
						JOptionPane.showMessageDialog (null,
							"�A��J�����_�榡�����T��I�нT�{�A�����_�O�_��J���~",
							"���_�榡���~�A�нT�{",
							JOptionPane.INFORMATION_MESSAGE);
						setEnabled(true);
						return;
					}
					else {
						int reval = JOptionPane.showConfirmDialog (
							null,
							"�A��J�����_�榡�����T�A�ݭn�n�۰ʲ������_�ܡH",
							"�۰ʲ������_�T�{",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
						if (reval == 0) {
							randomkey = true;
						}
						else {
							setEnabled(true);
							return;
						}
					}
				}

				// �üƲ������_
				if (randomkey) {
					getRandom(key);
					jTextArea1.setText(asHex(key));
					randomkey = false;
				}
				else{
					Str2Key(keystring,key);
				}

				// �إ߰�����ö}�l�[�K�C
				InternalData.setKey(key);
				new Thread(InternalData).start();

				// �[�K�᪺����}�Үɾ������Ѧh������ӨM�w�C
				// setEnabled(true);
			}
		}
		else if ( actcmd.equals("AutoKeyGenerator") ) {
			byte[] key = new byte[keylen];
	    	getRandom(key);
			jTextArea1.setText(asHex(key));
		}
		//---------------------------------------------------------------------
		//  �������ƥ�o�͡A����ܰT���bCommand Line���C
		//---------------------------------------------------------------------
		else {
			System.err.println("aes_cryptor::Unknown ActionCommand="+actcmd);
		}
	}

	/**
	 *  ���Ͷüƪ���ơA�i���ͶüƸ�ƩάO���_�A�D�n�O���Ӳ����H�����_�C
	 *  @param space �|�N�üƸ�Ʀs�J�o�ӪŶ����A�Y�ǤJ��null�A�h��������ƪ����^�ǡC
	 *  @return �Yspace�C
	 */
	public static byte[] getRandom (byte[] space) {
		if (space == null) return space;
		new java.util.Random().nextBytes(space);
		return space;
	}

     /**
     *  �Nbyte��ƥH�Q���i�쪺�覡�ഫ���r��C
     *  @param buf	�n�ഫ���r�ꪺbyte��ơC
     *  @return	�ഫ�������r���ơC
     */
    public static String asHex (byte buf[]) {
	    StringBuffer strbuf = new StringBuffer(buf.length * 2);

	    for (int i = 0; i < buf.length; i++) {
		    if (((int) buf[i] & 0xff) < 0x10)
			    strbuf.append("0");
		    strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
	    }
	    return strbuf.toString();
    }

	/**
	 *  �ˬd��J�����_�r��O�_����ϥΡC
	 *  @param s �n�ˬd���r��C
	 *  @param len ���_����������
	 */
    private static boolean isleagalKey(String s,int len) {
    	if (s==null || s.length()!=len*2 || s.length()%2!=0 ) return false;

		try {
			for (int i=0 ; i<s.length()/2 ; i++)
				Integer.parseInt(s.substring(i*2, i*2+2),16);
			return true;
		} catch (Exception e) {
			return false;
		}
    }

	/**
	 *  �ഫHex��ܸ�ƪ��r�ꬰbyte�}�C��ơA�o�Ӥ�k�������P���T���r��~��T�O�F���\�ΡC
	 *  @param str �n�Q�ഫ���r��C
	 *  @param key �ഫ�ɡA�ݬ�key�����סA�M�w�q�r���^������ƪ��סC
	 */    
	private static byte[] Str2Key(String str, byte[] key) {
		for (int i=0,val ; key!=null && i<key.length ; i++) {
			val = Integer.parseInt(str.substring(i*2, i*2+2),16);
			key[i] = (byte)val;
		}
		return key;
	}

	/**
	 *  �]�w��ܶi�ת��A�A���h��������O�I�s�ΡC
	 *  @param n �ثe�i�ת��ʤ���ơC
	 */
	public void	setProgress(int n) {
		jProgressBar1.setValue(n);
	}

	/**
	 *  �]�w����}�Ҫ��A�A��[�K�ʧ@�i��ɡA�|�I�s����k�A����ϥΪ̧�ʸ�ơC
	 *  @param bool ���A�ѼơAtrue�h�}�ҡAfalse�h�����C
	 */
	public void setEnabled(boolean bool) {
		jButton1.setEnabled(bool);
		jButton2.setEnabled(bool);
		jButton3.setEnabled(bool);
		jRadioButton1.setEnabled(bool);
		jRadioButton2.setEnabled(bool);
		jRadioButton3.setEnabled(bool);
		jRadioButton4.setEnabled(bool);
		jRadioButton5.setEnabled(bool);
		jRadioButton6.setEnabled(bool);
		jRadioButton7.setEnabled(bool);
		jRadioButton8.setEnabled(bool);
		jRadioButton9.setEnabled(bool);
		jRadioButton10.setEnabled(bool);
		jRadioButton11.setEnabled(bool);
		jRadioButton12.setEnabled(bool);
		jTextArea1.setEnabled(bool);
		jcheckbox1.setEnabled(bool);
	}

	/**
	 *  �]�w�}/��DEBUG�T���A�w�]�T��������ܡC
	 *  @param bool �}�Ҫ��A�Atrue�A�}�ҰT����ܡAfalse�A�����T����ܡC
	 */
	public void setDEBUG(boolean bool) {
		DEBUG = bool;
		InternalData.DEBUG = bool;
	}

	/**
	 *  �D�{���A�D�n�u�@�O�]�w�����~�[�H�ΰѼƧP�_�C
	 **/
    public static void main(String[] args) {
    	boolean debugflag = false;
    	if (args.length>0) {
    		if (args[0].trim().equals("debug"))
				debugflag = true;
		}
        aes_cryptor acryptor = new aes_cryptor();
    	acryptor.setDEBUG(debugflag);
		Dimension scr_size = Toolkit.getDefaultToolkit().getScreenSize();
		acryptor.setLocation((scr_size.width-535)/2, (scr_size.height-300)/2);
		acryptor.setSize(535, 300);
        acryptor.setResizable(false);
        acryptor.setTitle("AES �[�ѱK�{�� - AES Cryptor [��T�w�������{���p��]");
		acryptor.setDefaultCloseOperation(acryptor.DO_NOTHING_ON_CLOSE );
		acryptor.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					if ( JOptionPane.showConfirmDialog (null, "�A�T�w�n�����ܡH",
						"�����T�{", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == 0 )
						System.exit(0);
				}
			}
		);
        acryptor.setVisible(true);        
    }

//------------------------------------------------------------------------------
// ����ŧi��
//------------------------------------------------------------------------------
    private JButton jButton1 = new JButton();
    private ButtonGroup buttonGroup1 = new ButtonGroup();
    private JProgressBar jProgressBar1 = new JProgressBar();
    private JButton jButton2 = new JButton();
    private TitledBorder titledBorder1 = new TitledBorder("AES�[/�ѱK���_");
    private JPanel jPanel1 = new JPanel();
    private TitledBorder titledBorder2 = new TitledBorder("AES���_����");
    private JRadioButton jRadioButton1 = new JRadioButton();
    private JRadioButton jRadioButton2 = new JRadioButton();
    private JRadioButton jRadioButton3 = new JRadioButton();
    private JPanel jPanel2 = new JPanel();
    private JRadioButton jRadioButton4 = new JRadioButton();
    private JRadioButton jRadioButton5 = new JRadioButton();
    private JRadioButton jRadioButton6 = new JRadioButton();
    private ButtonGroup buttonGroup2 = new ButtonGroup();
    private GridLayout gridLayout1 = new GridLayout();
    private GridLayout gridLayout2 = new GridLayout();
    private TitledBorder titledBorder3 = new TitledBorder("AES �[�K�Ҧ�");
    private JButton jButton3 = new JButton();
    private JPanel jPanel3 = new JPanel();
    private JRadioButton jRadioButton7 = new JRadioButton();
    private JRadioButton jRadioButton8 = new JRadioButton();
    private TitledBorder titledBorder4 = new TitledBorder("�i��[/�ѱK");
    private GridLayout gridLayout3 = new GridLayout();
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JTextArea jTextArea1 = new JTextArea();
    private ButtonGroup buttonGroup3 = new ButtonGroup();
    private JRadioButton jRadioButton9 = new JRadioButton();
    private JRadioButton jRadioButton10 = new JRadioButton();
    private JRadioButton jRadioButton11 = new JRadioButton();
    private JRadioButton jRadioButton12 = new JRadioButton();
    private JCheckBox jcheckbox1 = new JCheckBox();
//------------------------------------------------------------------------------
//  �G�mGUI����
//------------------------------------------------------------------------------
    private void jbInit() throws Exception {
        getContentPane().setLayout(null);
        // set radio button group
        jRadioButton1.setText("128-bits");
        jRadioButton1.addActionListener(this);
        jRadioButton2.setText("192-bits");
        jRadioButton2.addActionListener(this);
        jRadioButton3.setText("256-bits");
        jRadioButton3.addActionListener(this);
        jRadioButton4.setText("ECB");
        jRadioButton4.addActionListener(this);
        jRadioButton5.setText("CBC");
        jRadioButton5.addActionListener(this);
        jRadioButton6.setText("CTR");
        jRadioButton6.addActionListener(this);
        jRadioButton7.setText("�[�K");
		jRadioButton7.setActionCommand("Encrypt");
        jRadioButton7.addActionListener(this);
        jRadioButton8.setText("�ѱK");
		jRadioButton8.setActionCommand("Decrypt");
        jRadioButton8.addActionListener(this);
        jRadioButton9.setText("CFB-1");
        jRadioButton9.addActionListener(this);
        jRadioButton10.setText("CFB-8");
        jRadioButton10.addActionListener(this);
        jRadioButton11.setText("OFB-1");
        jRadioButton11.addActionListener(this);
        jRadioButton12.setText("OFB-8");
        jRadioButton12.addActionListener(this);
        buttonGroup1.add(jRadioButton1);
        buttonGroup1.add(jRadioButton2);
        buttonGroup1.add(jRadioButton3);
        buttonGroup2.add(jRadioButton4);
        buttonGroup2.add(jRadioButton5);
        buttonGroup2.add(jRadioButton6);
        buttonGroup2.add(jRadioButton9);
		buttonGroup2.add(jRadioButton10);
		buttonGroup2.add(jRadioButton11);
		buttonGroup2.add(jRadioButton12);
        buttonGroup3.add(jRadioButton7);
        buttonGroup3.add(jRadioButton8);
   		
        gridLayout2.setColumns(1);
        gridLayout2.setRows(3);
        jPanel1.setBorder(titledBorder2);
        jPanel1.setBounds(new Rectangle(20, 15, 110, 95));
        jPanel1.setLayout(gridLayout2);
        jPanel1.add(jRadioButton1, null);
        jPanel1.add(jRadioButton2, null);
        jPanel1.add(jRadioButton3, null);
        this.getContentPane().add(jPanel1);

        gridLayout1.setColumns(1);
        gridLayout1.setRows(7);
        jPanel2.setBorder(titledBorder3);
        jPanel2.setBounds(new Rectangle(140, 15, 95, 155));
        jPanel2.setLayout(gridLayout1);
        jPanel2.add(jRadioButton4, null);
        jPanel2.add(jRadioButton5, null);
        jPanel2.add(jRadioButton6, null);
        jPanel2.add(jRadioButton9, null);
        jPanel2.add(jRadioButton10, null);
        jPanel2.add(jRadioButton11, null);
        jPanel2.add(jRadioButton12, null);
        this.getContentPane().add(jPanel2);
        
		gridLayout3.setColumns(1);
        gridLayout3.setRows(2);
        jPanel3.setBorder(titledBorder4);
        jPanel3.setBounds(new Rectangle(250, 15, 90, 70));
        jPanel3.setLayout(gridLayout3);
        jPanel3.add(jRadioButton7);
        jPanel3.add(jRadioButton8);
		this.getContentPane().add(jPanel3);

        jScrollPane1.setBorder(titledBorder1);
        jScrollPane1.setBounds(new Rectangle(22, 180, 480, 70));
        jScrollPane1.getViewport().add(jTextArea1);
        jProgressBar1.setBounds(new Rectangle(22, 250, 480, 15));

        jButton1.setBounds(new Rectangle(365, 50, 130, 30));
		// �o�ӫ��s�W���r���Ĳ�o�ƥ󱱨�
        // jButton1.setText("�}�l�[�K");
        jButton1.addActionListener(this);
        jButton1.setActionCommand("StartCryption");
        jButton2.setBounds(new Rectangle(365, 20, 130, 30));
        jButton2.setText("�}���ɮ�");
        jButton2.setActionCommand("OpenFile");
        jButton2.addActionListener(this);
        jButton3.setBounds(new Rectangle(365, 80, 130, 30));
        jButton3.setText("�۰ʲ������_");
        jButton3.setActionCommand("AutoKeyGenerator");
        jButton3.addActionListener(this);
        jcheckbox1.setBounds(new Rectangle(365, 120, 130, 30));
        jcheckbox1.setText("�ҥάd��[�t");
        jcheckbox1.setActionCommand("TableQuick");
        jcheckbox1.addActionListener(this);
        this.getContentPane().add(jProgressBar1);
        this.getContentPane().add(jButton1);
        this.getContentPane().add(jButton2);
        this.getContentPane().add(jButton3);
        this.getContentPane().add(jcheckbox1);
        this.getContentPane().add(jScrollPane1);
    }
}


