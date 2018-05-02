import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class fin_work1 extends JFrame implements ActionListener ,KeyListener
{
	private int QueenNum;
	private JButton		prev ,
						next ;
	private JLabel		info ,
						page ;
	private JMenuItem	jmhSubAbout ,jmfSubSave ,jmfSubSaveAll ,jmfSubExit ,
						jmoSubNext ,jmoSubPrev ,jmoSubGoto;
	private JRadioButtonMenuItem[]	jmoSubTrans = new JRadioButtonMenuItem[7];
	private BoradPanel	borad;
	private InfoDialog	infowin;

// 建構子 -----------------------------------------------------------
	fin_work1()
	{
		QueenNum		= 8;
		infowin			= new InfoDialog(this,true);
		getContentPane().setLayout( new BorderLayout(5,5) );
		getContentPane().add(borad = new BoradPanel(QueenNum));

		createTitleBar();
		createCtrlBar();
		createMenu();
		createAndShowGUI();
	}//end fin_work1

// 建立GUI物件的函式 -------------------------------------------------------
	/** 建立標題列 */
	public void createTitleBar()
	{
		JPanel tpanel	= new JPanel(new FlowLayout(FlowLayout.LEFT));
		getContentPane().add(tpanel,BorderLayout.NORTH);

		info = new JLabel(QueenNum +" 皇后( "+ QueenNum +" Queen)所有解展示 :");
		page = new JLabel("(" + borad.curr() +"/"+ borad.count()+ ")");
		tpanel.add(info);
		tpanel.add(page);
	}

	/** 建立控制列 */
	public void createCtrlBar()
	{
		JPanel bpanel	= new JPanel(new FlowLayout(FlowLayout.CENTER ,5 ,5));
		getContentPane().add(bpanel,BorderLayout.SOUTH);

		prev = new JButton("▲前一個");
		next = new JButton("後一個▼");

		prev.setToolTipText("往前一個解(Previous Solution)");
		next.setToolTipText("往下一個(Next Solution)");

		prev.setMnemonic('P');
		next.setMnemonic('N');

		prev.setEnabled( borad.hasPrev() );
		next.setEnabled( borad.hasMore() );

		prev.addActionListener(this);
		next.addActionListener(this);
		prev.addKeyListener(this);
		next.addKeyListener(this);

		bpanel.add(prev);
		bpanel.add(next);
	}//end createCtrlBar

	/** 建立視窗 */
	private void createAndShowGUI()
	{
		this.setSize(400,450);
		this.setTitle("八皇后(Eight Queen)   Ver. N Queen Revolution ");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// frame on the center of screen
		tools.localCenter(this);
		setResizable(false);
		show();
	}//end setBasicGUI()

	/** 建立工具列選單 */
	private void createMenu()
	{
		JMenuBar jmb		= new JMenuBar();
		setJMenuBar(jmb);

		JMenu jmmFile		= new JMenu(" 檔案(F) ");
		JMenu jmmOperator	= new JMenu(" 操作(O) ");
		JMenu jmmHelp		= new JMenu(" 求助(H) ");

		jmmFile.setMnemonic('F');
		jmmOperator.setMnemonic('O');
		jmmHelp.setMnemonic('H');
		
		jmb.add(jmmFile);
		jmb.add(jmmOperator);
		jmb.add(jmmHelp);

		/** set JMenu File's Item */
		jmfSubSave		= new JMenuItem("將這個解存成檔案   Save  " ,'S');
		jmfSubSaveAll	= new JMenuItem("儲存所有的解到檔案  Save All " ,'A');
		jmfSubExit		= new JMenuItem("離開 Exit  " ,'X');


		jmfSubSave.setIcon(tools.createImageIcon("images/File.gif"));
		jmfSubSave.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK) );

		jmfSubSave.addActionListener(this);
		jmfSubSaveAll.addActionListener(this);
		jmfSubExit.addActionListener(this);
		
		jmmFile.add(jmfSubSave);
		jmmFile.add(jmfSubSaveAll);
		jmmFile.addSeparator();
		jmmFile.add(jmfSubExit);

		/** set JMenu Opreator's Item */
		jmoSubNext		= new JMenuItem("後一個解 Next  " ,'N');
		jmoSubPrev		= new JMenuItem("前一個解 Privous  " ,'P');
		jmoSubGoto		= new JMenuItem("到...  GoTo  " ,'G');

		jmoSubNext.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK) );
		jmoSubPrev.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK) );
		jmoSubGoto.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK) );

		jmoSubNext.addActionListener(this);
		jmoSubPrev.addActionListener(this);
		jmoSubGoto.addActionListener(this);

		jmmOperator.add(jmoSubNext);
		jmmOperator.add(jmoSubPrev);
		jmmOperator.add(jmoSubGoto);

		jmmOperator.addSeparator();

		// 改變為 n 皇后的解的選單
		ButtonGroup g = new ButtonGroup();
		for (int i=0;i<jmoSubTrans.length;i++)
		{
			jmoSubTrans[i] = new JRadioButtonMenuItem((i+4)+"皇后");
			g.add(jmoSubTrans[i]);
			jmoSubTrans[i].addActionListener(this);
			jmmOperator.add(jmoSubTrans[i]);
		}
		jmoSubTrans[QueenNum-4].setSelected(true);

		/** set JMenu Help's Item */
		jmhSubAbout		= new JMenuItem("關於 About  " ,'A');

		jmhSubAbout.setIcon(tools.createImageIcon("images/test.gif"));

		jmhSubAbout.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK) );

		jmhSubAbout.addActionListener(this);
		jmmHelp.add(jmhSubAbout);
	}//end createMenu

// 測試主程式 -------------------------------------------------------
	public static void main(String[] args){
		fin_work1 f = new fin_work1();
	}//end main

// 按鈕&選單作用 ----------------------------------------------------
	public void actionPerformed(ActionEvent e)
	{
		Object S = e.getSource();
		/** 按鈕:前一個 */
		if ( S==prev || S==jmoSubPrev )
		{
			if ( !borad.hasPrev() )
			{	/** Can't go to Previous Exception Handle */
				JOptionPane.showMessageDialog(this,"已經是第一個解!!" ,
					"已到前端", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			borad.viewPrev();
			page.setText("(" + borad.curr() + 
				"/" + borad.count() + ")");
			prev.setEnabled( borad.hasPrev() );
			next.setEnabled( borad.hasMore() );
		}//end Source == prev
		/** 按鈕:後一個 */
		else if( S==next || S==jmoSubNext )
		{
			if ( !borad.hasMore() ){
				JOptionPane.showMessageDialog(this,"已經是最後一個解!!" ,
					"已到後端",	JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			borad.viewNext();
			page.setText("(" + borad.curr() + 
				"/" + borad.count() + ")");
			prev.setEnabled( borad.hasPrev() );
			next.setEnabled( borad.hasMore() );
		}//end Source == nexts
		/** 選單 :往任意解 */
		else if( S==jmoSubGoto )
		{
			int SelVal;
			String Sel = JOptionPane.showInputDialog(this,
				"請輸入欲觀看的解答編號(1~"+borad.count()+")",	"到... GoTo"
				,JOptionPane.QUESTION_MESSAGE);

			/** NumberFormatException */
			try{
				SelVal = Integer.parseInt(Sel);
			}catch(NumberFormatException ex){
				JOptionPane.showMessageDialog(this ,
					"你輸入不正確的數字喔!!","警告!!" ,
					JOptionPane.ERROR_MESSAGE);
				return;
			}

			boolean CanGo = borad.go(SelVal);
			if ( !CanGo ) return;
			page.setText("(" + borad.curr() + 
				"/" + borad.count() + ")");
			prev.setEnabled( borad.hasPrev() );
			next.setEnabled( borad.hasMore() );
		}//end Source == jmoSubGoto
		/** 選單 : 關於*/
		else if( S==jmhSubAbout )
		{
			infowin.show();
		}//end Source == about
		else if ( S==jmfSubExit )
		{
			System.exit(0);
		}//end Source exit
		/** The Plain Give up.*/
		else if ( S==jmfSubSave )
		{
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
				try{
					borad.writeToFile( file.getPath() );
				}catch( IOException ex ){
					JOptionPane.showMessageDialog(this ,
						"無法儲存正確儲存檔案，無法存取檔案或硬碟空間不足!!","存檔錯誤" ,
						JOptionPane.ERROR_MESSAGE);
				}
            }//else Nothing
		}//end Source == jmfSubSave
		else if ( S==jmfSubSaveAll )
		{
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
				try{
					borad.writeAllToFile( file.getPath() );
				}catch( IOException ex ){
					JOptionPane.showMessageDialog(this ,
						"無法儲存正確儲存檔案，無法存取檔案或硬碟空間不足!!","存檔錯誤" ,
						JOptionPane.ERROR_MESSAGE);
				}
            }//else Nothing
		}//end Source == jmfSubSaveAll
		else{
			for(int i=0;i<jmoSubTrans.length;i++)
			{
				if (S!=jmoSubTrans[i]) continue;
				// 移除原本的 nQueen的Panel
				getContentPane().remove(borad);
				getContentPane().add(borad = new BoradPanel( QueenNum = i+4));

				// 更新物件
				info.setText(QueenNum +" 皇后( "+ QueenNum +" Queen)所有解展示 :");
				page.setText("(" + borad.curr() +"/"+ borad.count()+ ")");

				prev.setEnabled( borad.hasPrev() );
				next.setEnabled( borad.hasMore() );

				show();
				break;
			}
		}
	}//end actionPerformed

// 鍵盤作用 ---------------------------------------------------------
	public void keyTyped(KeyEvent e){
	}
	public void keyPressed(KeyEvent e){
	}
	public void keyReleased(KeyEvent e){
		int code = e.getKeyCode();
		if ( code==e.VK_LEFT ){
			prev.doClick();
		}else if ( code==e.VK_RIGHT ){
			next.doClick();
		}
	}

}//end class fin_work


