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

// �غc�l -----------------------------------------------------------
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

// �إ�GUI���󪺨禡 -------------------------------------------------------
	/** �إ߼��D�C */
	public void createTitleBar()
	{
		JPanel tpanel	= new JPanel(new FlowLayout(FlowLayout.LEFT));
		getContentPane().add(tpanel,BorderLayout.NORTH);

		info = new JLabel(QueenNum +" �ӦZ( "+ QueenNum +" Queen)�Ҧ��Ѯi�� :");
		page = new JLabel("(" + borad.curr() +"/"+ borad.count()+ ")");
		tpanel.add(info);
		tpanel.add(page);
	}

	/** �إ߱���C */
	public void createCtrlBar()
	{
		JPanel bpanel	= new JPanel(new FlowLayout(FlowLayout.CENTER ,5 ,5));
		getContentPane().add(bpanel,BorderLayout.SOUTH);

		prev = new JButton("���e�@��");
		next = new JButton("��@�ӡ�");

		prev.setToolTipText("���e�@�Ӹ�(Previous Solution)");
		next.setToolTipText("���U�@��(Next Solution)");

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

	/** �إߵ��� */
	private void createAndShowGUI()
	{
		this.setSize(400,450);
		this.setTitle("�K�ӦZ(Eight Queen)   Ver. N Queen Revolution ");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// frame on the center of screen
		tools.localCenter(this);
		setResizable(false);
		show();
	}//end setBasicGUI()

	/** �إߤu��C��� */
	private void createMenu()
	{
		JMenuBar jmb		= new JMenuBar();
		setJMenuBar(jmb);

		JMenu jmmFile		= new JMenu(" �ɮ�(F) ");
		JMenu jmmOperator	= new JMenu(" �ާ@(O) ");
		JMenu jmmHelp		= new JMenu(" �D�U(H) ");

		jmmFile.setMnemonic('F');
		jmmOperator.setMnemonic('O');
		jmmHelp.setMnemonic('H');
		
		jmb.add(jmmFile);
		jmb.add(jmmOperator);
		jmb.add(jmmHelp);

		/** set JMenu File's Item */
		jmfSubSave		= new JMenuItem("�N�o�ӸѦs���ɮ�   Save  " ,'S');
		jmfSubSaveAll	= new JMenuItem("�x�s�Ҧ����Ѩ��ɮ�  Save All " ,'A');
		jmfSubExit		= new JMenuItem("���} Exit  " ,'X');


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
		jmoSubNext		= new JMenuItem("��@�Ӹ� Next  " ,'N');
		jmoSubPrev		= new JMenuItem("�e�@�Ӹ� Privous  " ,'P');
		jmoSubGoto		= new JMenuItem("��...  GoTo  " ,'G');

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

		// ���ܬ� n �ӦZ���Ѫ����
		ButtonGroup g = new ButtonGroup();
		for (int i=0;i<jmoSubTrans.length;i++)
		{
			jmoSubTrans[i] = new JRadioButtonMenuItem((i+4)+"�ӦZ");
			g.add(jmoSubTrans[i]);
			jmoSubTrans[i].addActionListener(this);
			jmmOperator.add(jmoSubTrans[i]);
		}
		jmoSubTrans[QueenNum-4].setSelected(true);

		/** set JMenu Help's Item */
		jmhSubAbout		= new JMenuItem("���� About  " ,'A');

		jmhSubAbout.setIcon(tools.createImageIcon("images/test.gif"));

		jmhSubAbout.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK) );

		jmhSubAbout.addActionListener(this);
		jmmHelp.add(jmhSubAbout);
	}//end createMenu

// ���եD�{�� -------------------------------------------------------
	public static void main(String[] args){
		fin_work1 f = new fin_work1();
	}//end main

// ���s&���@�� ----------------------------------------------------
	public void actionPerformed(ActionEvent e)
	{
		Object S = e.getSource();
		/** ���s:�e�@�� */
		if ( S==prev || S==jmoSubPrev )
		{
			if ( !borad.hasPrev() )
			{	/** Can't go to Previous Exception Handle */
				JOptionPane.showMessageDialog(this,"�w�g�O�Ĥ@�Ӹ�!!" ,
					"�w��e��", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			borad.viewPrev();
			page.setText("(" + borad.curr() + 
				"/" + borad.count() + ")");
			prev.setEnabled( borad.hasPrev() );
			next.setEnabled( borad.hasMore() );
		}//end Source == prev
		/** ���s:��@�� */
		else if( S==next || S==jmoSubNext )
		{
			if ( !borad.hasMore() ){
				JOptionPane.showMessageDialog(this,"�w�g�O�̫�@�Ӹ�!!" ,
					"�w����",	JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			borad.viewNext();
			page.setText("(" + borad.curr() + 
				"/" + borad.count() + ")");
			prev.setEnabled( borad.hasPrev() );
			next.setEnabled( borad.hasMore() );
		}//end Source == nexts
		/** ��� :�����N�� */
		else if( S==jmoSubGoto )
		{
			int SelVal;
			String Sel = JOptionPane.showInputDialog(this,
				"�п�J���[�ݪ��ѵ��s��(1~"+borad.count()+")",	"��... GoTo"
				,JOptionPane.QUESTION_MESSAGE);

			/** NumberFormatException */
			try{
				SelVal = Integer.parseInt(Sel);
			}catch(NumberFormatException ex){
				JOptionPane.showMessageDialog(this ,
					"�A��J�����T���Ʀr��!!","ĵ�i!!" ,
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
		/** ��� : ����*/
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
						"�L�k�x�s���T�x�s�ɮסA�L�k�s���ɮשεw�ЪŶ�����!!","�s�ɿ��~" ,
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
						"�L�k�x�s���T�x�s�ɮסA�L�k�s���ɮשεw�ЪŶ�����!!","�s�ɿ��~" ,
						JOptionPane.ERROR_MESSAGE);
				}
            }//else Nothing
		}//end Source == jmfSubSaveAll
		else{
			for(int i=0;i<jmoSubTrans.length;i++)
			{
				if (S!=jmoSubTrans[i]) continue;
				// �����쥻�� nQueen��Panel
				getContentPane().remove(borad);
				getContentPane().add(borad = new BoradPanel( QueenNum = i+4));

				// ��s����
				info.setText(QueenNum +" �ӦZ( "+ QueenNum +" Queen)�Ҧ��Ѯi�� :");
				page.setText("(" + borad.curr() +"/"+ borad.count()+ ")");

				prev.setEnabled( borad.hasPrev() );
				next.setEnabled( borad.hasMore() );

				show();
				break;
			}
		}
	}//end actionPerformed

// ��L�@�� ---------------------------------------------------------
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


