import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class fin_work2 extends JFrame implements ActionListener
{
	private JButton[] puzzle		= new JButton[9];
	private ImageIcon[] imgInRand	= new ImageIcon[9];
	private ImageIcon[] imgInOrder	= new ImageIcon[9];
	private Timer CountTime;
	private JLabel lbl_Time ,lbl_TimeDescript;
	static final int	RemindTime	= 90;	//�]�w�C���ɶ�

	fin_work2()
	{
		/** �N�U����������� */
		JPanel jp_puzzle	= new JPanel(new GridLayout(3,3,2,2));
		lbl_Time			= new JLabel(""+RemindTime);
		lbl_TimeDescript	= new JLabel("�Ѿl�ɶ�");
		JPanel jp_timer		= new JPanel(new FlowLayout());
		jp_timer.add(lbl_TimeDescript);
		jp_timer.add(lbl_Time);

		Container c			= getContentPane();
		c.setLayout(new BorderLayout(5,5));
		c.add(jp_puzzle ,BorderLayout.CENTER);
		c.add(jp_timer ,BorderLayout.NORTH);

		/** Ū�J�Ϥ��P�إ߫��s����γ]�w */
		for (int i=0;i<puzzle.length ;i++){
			// create Instance & set
			puzzle[i]	= new JButton();
			puzzle[i].addActionListener(this);
			jp_puzzle.add(puzzle[i]);		//�[��panel
			// load Image
			imgInOrder[i]	= (i==8)?null:tools.createImageIcon("images/img_"+(i+1)+".jpg");
			imgInRand[i]	= imgInOrder[i];
		}

		/** �N�Ϥ����ë�ˤW���s */
		imgInRand = getRandImageIcon(imgInRand);
		for (int i=0;i<puzzle.length ;i++){
				puzzle[i].setIcon(imgInRand[i]);
		}

		/** �N�����q�X�� */
		createAndShowGUI();

		/** �]�w�p�ɾ� �P�Ұ� */
		CountTime		= new Timer(1000,this);
		CountTime.start();
	}// end fin_work2

	private void createAndShowGUI(){
		this.setTitle("�����@�~ 2.���ϹC��");
		this.setSize(580,410);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		tools.localCenter(this);
		this.show();
	}//end createAndShowGUI

	public ImageIcon[] getRandImageIcon(ImageIcon[] tar){
		// �Q��������ܱƧǪk���覡���ü�
		ImageIcon[] goal = tar;
		for(int i=goal.length-1 ; i>0 ; i--){
            int j = tools.rand(i);
			// swap
            ImageIcon tmp = goal[i];
            goal[i] = goal[j];
            goal[j] = tmp;
        }
		return goal;
	}//end getRandImageIcon

	public static void main(String[] args){
		fin_work2 f = new fin_work2();
	}// end main 

	public void exchangeButtonImage(int i1 ,int i2){
		Icon tmp	= puzzle[i1].getIcon();
		puzzle[i1].setIcon(puzzle[i2].getIcon());
		puzzle[i2].setIcon(tmp);
	}// end exchangeButtonImage

	public void enablePuzzle(boolean m){
		for (int i=0; i<puzzle.length ; i++){
			puzzle[i].setEnabled(m);
		}
	}// end enablePuzzle

	public void resetPuzzle(){
		imgInRand = getRandImageIcon(imgInRand);
		for (int i=0;i<puzzle.length ;i++){
				puzzle[i].setIcon(imgInRand[i]);
		}
		lbl_Time.setText(""+RemindTime);
		enablePuzzle(true);
		CountTime.start();
	}

	private void checkFinished(){
	}
	public void actionPerformed(ActionEvent e){
		/**/
		Object evn = e.getSource();
		if ( evn==CountTime )
		{
			int rmd		= Integer.parseInt(lbl_Time.getText())-1;
			lbl_Time.setText("" + rmd);
			if ( rmd==0 ){
				enablePuzzle(false);
				CountTime.stop();
				Object[] options = { "���s�A��? ", "�����F! " };
				int SelVal = JOptionPane.showOptionDialog(this, "��!�A�B��u���n...\n"+
				"�аݧA�٭n����?? ", "�ɶ���! ������ ...", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE , null, options,options[0]);
				if ( SelVal==0 )
					resetPuzzle();
			}//else;
			return;
		}
		else
		{
			/** �ˬd�@�Ϊ����s */
			for (int i=0; i<puzzle.length ; i++){
				if (evn!=puzzle[i]) continue;
				Icon curr_icon = puzzle[i].getIcon();
				if(curr_icon==null) continue;

				int left  = i%3-1;
				int right = i%3+1;
				int up    = i-3;
				int down  = i+3;

				if(left>=0)
					if(puzzle[i-1].getIcon()==null)
						exchangeButtonImage(i, i-1);
				if(right<3)
					if(puzzle[i+1].getIcon()==null)
						exchangeButtonImage(i, i+1);
				if(up>=0)
					if(puzzle[up].getIcon()==null)
						exchangeButtonImage(i, up);
				if(down<9)
					if(puzzle[down].getIcon()==null)
						exchangeButtonImage(i, down);
			}//end for

			/** �ˬd���ϬO�_�w�g���� */
			for (int i=0;i<puzzle.length ;i++){
				if (imgInOrder[i]!=puzzle[i].getIcon())	return;
			}
			//�q�L�ˬd�A���ϧ���!
			Object[] options = { "�A���@��? ", "�����F! " };
			int SelVal = JOptionPane.showOptionDialog(this, "���ߧA! �A�������ϤF *^_^*\n"+
				"�аݧA�٭n����?? ", "����!! ���ϧ���", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE , null, options,options[0]);
			if ( SelVal==0 ) resetPuzzle();

		}//end else
	}// end actionPerformed
}//end class

