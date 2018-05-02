import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

/**
 *  加密器圖形介面。
 *  @author 邱永祥
 */
public class aes_cryptor extends JFrame implements ActionListener {
	private boolean DEBUG = false;		// 是否顯示偵錯訊息
	private cryptor InternalData;		// 二次加密器
    private JFileChooser chooser;		// 檔案選擇視窗
	private int keylen;					// 鎖鑰長度, 單位byte

	/**
	 *  佈置預設元件與初始化加密器。
	 */
    public aes_cryptor() {
        try {
	        chooser = new JFileChooser();
        	// 佈置元件
            jbInit();

			// 設定初使選擇項目
			jRadioButton1.setSelected(true);		
			jRadioButton4.setSelected(true);
	        jRadioButton7.setSelected(true);

	        // 建立加密器並產生假事件來設定加密器
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
            System.err.println("錯誤:初始化視窗介面及加密器失敗，程式關閉。");
            System.exit(-1);
        }
    }

	/**
	 *  處理使用者觸發的事件，以事件命令作為處理依據。
	 *  @param e 觸發的事件
	 */
	public void actionPerformed(ActionEvent e) {
		String actcmd = e.getActionCommand();
		
		if (DEBUG) System.out.println("aes_cryptor::ActionCommand=" + actcmd);

		//---------------------------------------------------------------------
		// 鎖鑰長度 128 ,192 ,256
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
		// 加密模式
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
		// 加/解密模式
		//---------------------------------------------------------------------
		else if ( actcmd.equals("Encrypt") ) {
			InternalData.setCipher(InternalData.ENCRYPT);
			jButton1.setText("開始加密");
		}
		else if ( actcmd.equals("Decrypt") ) {
			InternalData.setCipher(InternalData.DECRYPT);
			jButton1.setText("開始解密");
		}
		//---------------------------------------------------------------------
		// 額外的功能 [加速版]
		//---------------------------------------------------------------------
		else if (actcmd.equals("TableQuick")) {
			mirror_aes.LookupTableQuickly = !mirror_aes.LookupTableQuickly;
		}
		//---------------------------------------------------------------------
		// 使用者互動
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
						"您選取的檔案無法使用，請重新選取檔案。", "選取檔案錯誤",
						JOptionPane.ERROR_MESSAGE );
				}
		    }
		}
		else if ( actcmd.equals("StartCryption") ) {
			/*
			// DEMO用程式碼
			new Thread (){
				public void run(){
					setEnabled(false);
					for (int i=0 ; i<=100 ; i++){
						try{ setProgress(i);
							Thread.sleep(50);
						}catch(Exception e){}
					}
					JOptionPane.showMessageDialog (null,
						"僅為展示模擬功能選項，尚未實作。", "DEMO版本功能限制",
						JOptionPane.INFORMATION_MESSAGE );
					setEnabled(true);
				}
			}.start();
			*/
			if (InternalData.isSelectedFile() == false) {
				JOptionPane.showMessageDialog (this,
					"請先選取一個檔案，才能進行" + 
					(InternalData.getCipher()==InternalData.DECRYPT?"解":"加") + "密。",
					(InternalData.getCipher()==InternalData.DECRYPT?"解":"加") + "密中斷",
					JOptionPane.INFORMATION_MESSAGE );
			}
			else {
				setEnabled(false);

				// 當要進行加密的時候才建立鎖鑰空間，減少在選擇時建立的空間浪費。
				byte[] key = new byte[keylen];
				String keystring = jTextArea1.getText().trim();
				boolean randomkey = false;

				if (isleagalKey(keystring, keylen) == false) {
					// 解密時不應該是出現自動產生所要的訊息，所以添加此修正。
					if (InternalData.getCipher() == InternalData.DECRYPT) {
						JOptionPane.showMessageDialog (null,
							"你輸入的鎖鑰格式不正確唷！請確認你的鎖鑰是否輸入錯誤",
							"鎖鑰格式錯誤，請確認",
							JOptionPane.INFORMATION_MESSAGE);
						setEnabled(true);
						return;
					}
					else {
						int reval = JOptionPane.showConfirmDialog (
							null,
							"你輸入的鎖鑰格式不正確，需要要自動產生鎖鑰嗎？",
							"自動產生鎖鑰確認",
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

				// 亂數產生鎖鑰
				if (randomkey) {
					getRandom(key);
					jTextArea1.setText(asHex(key));
					randomkey = false;
				}
				else{
					Str2Key(keystring,key);
				}

				// 建立執行緒並開始加密。
				InternalData.setKey(key);
				new Thread(InternalData).start();

				// 加密後的物件開啟時機必須由多執行緒來決定。
				// setEnabled(true);
			}
		}
		else if ( actcmd.equals("AutoKeyGenerator") ) {
			byte[] key = new byte[keylen];
	    	getRandom(key);
			jTextArea1.setText(asHex(key));
		}
		//---------------------------------------------------------------------
		//  未知的事件發生，僅顯示訊息在Command Line中。
		//---------------------------------------------------------------------
		else {
			System.err.println("aes_cryptor::Unknown ActionCommand="+actcmd);
		}
	}

	/**
	 *  產生亂數的資料，可產生亂數資料或是鎖鑰，主要是拿來產生隨機鎖鑰。
	 *  @param space 會將亂數資料存入這個空間內，若傳入為null，則不做任何事直接回傳。
	 *  @return 即space。
	 */
	public static byte[] getRandom (byte[] space) {
		if (space == null) return space;
		new java.util.Random().nextBytes(space);
		return space;
	}

     /**
     *  將byte資料以十六進位的方式轉換為字串。
     *  @param buf	要轉換成字串的byte資料。
     *  @return	轉換完成的字串資料。
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
	 *  檢查輸入的鎖鑰字串是否能夠使用。
	 *  @param s 要檢查的字串。
	 *  @param len 鎖鑰應有的長度
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
	 *  轉換Hex表示資料的字串為byte陣列資料，這個方法必須給與正確的字串才能確保達成功用。
	 *  @param str 要被轉換的字串。
	 *  @param key 轉換時，端看key的長度，決定從字串擷取的資料長度。
	 */    
	private static byte[] Str2Key(String str, byte[] key) {
		for (int i=0,val ; key!=null && i<key.length ; i++) {
			val = Integer.parseInt(str.substring(i*2, i*2+2),16);
			key[i] = (byte)val;
		}
		return key;
	}

	/**
	 *  設定顯示進度狀態，給多執行緒類別呼叫用。
	 *  @param n 目前進度的百分比數。
	 */
	public void	setProgress(int n) {
		jProgressBar1.setValue(n);
	}

	/**
	 *  設定元件開啟狀態，當加密動作進行時，會呼叫此方法，防止使用者更動資料。
	 *  @param bool 狀態參數，true則開啟，false則關閉。
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
	 *  設定開/關DEBUG訊息，預設訊息為不顯示。
	 *  @param bool 開啟狀態，true，開啟訊息顯示，false，關閉訊息顯示。
	 */
	public void setDEBUG(boolean bool) {
		DEBUG = bool;
		InternalData.DEBUG = bool;
	}

	/**
	 *  主程式，主要工作是設定視窗外觀以及參數判斷。
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
        acryptor.setTitle("AES 加解密程式 - AES Cryptor [資訊安全期末程式計劃]");
		acryptor.setDefaultCloseOperation(acryptor.DO_NOTHING_ON_CLOSE );
		acryptor.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					if ( JOptionPane.showConfirmDialog (null, "你確定要結束嗎？",
						"結束確認", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == 0 )
						System.exit(0);
				}
			}
		);
        acryptor.setVisible(true);        
    }

//------------------------------------------------------------------------------
// 元件宣告區
//------------------------------------------------------------------------------
    private JButton jButton1 = new JButton();
    private ButtonGroup buttonGroup1 = new ButtonGroup();
    private JProgressBar jProgressBar1 = new JProgressBar();
    private JButton jButton2 = new JButton();
    private TitledBorder titledBorder1 = new TitledBorder("AES加/解密鎖鑰");
    private JPanel jPanel1 = new JPanel();
    private TitledBorder titledBorder2 = new TitledBorder("AES鎖鑰長度");
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
    private TitledBorder titledBorder3 = new TitledBorder("AES 加密模式");
    private JButton jButton3 = new JButton();
    private JPanel jPanel3 = new JPanel();
    private JRadioButton jRadioButton7 = new JRadioButton();
    private JRadioButton jRadioButton8 = new JRadioButton();
    private TitledBorder titledBorder4 = new TitledBorder("進行加/解密");
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
//  佈置GUI元件
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
        jRadioButton7.setText("加密");
		jRadioButton7.setActionCommand("Encrypt");
        jRadioButton7.addActionListener(this);
        jRadioButton8.setText("解密");
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
		// 這個按鈕上的字改由觸發事件控制
        // jButton1.setText("開始加密");
        jButton1.addActionListener(this);
        jButton1.setActionCommand("StartCryption");
        jButton2.setBounds(new Rectangle(365, 20, 130, 30));
        jButton2.setText("開啟檔案");
        jButton2.setActionCommand("OpenFile");
        jButton2.addActionListener(this);
        jButton3.setBounds(new Rectangle(365, 80, 130, 30));
        jButton3.setText("自動產生鎖鑰");
        jButton3.setActionCommand("AutoKeyGenerator");
        jButton3.addActionListener(this);
        jcheckbox1.setBounds(new Rectangle(365, 120, 130, 30));
        jcheckbox1.setText("啟用查表加速");
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


