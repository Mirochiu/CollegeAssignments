//
// �f�t n-Queen �Ұ����ק�
import java.awt.*;
import javax.swing.*;
import java.io.*;

public class BoradPanel extends JPanel
{

	private static final Color mcolor[]	= { Color.GRAY , Color.WHITE };
	private static ImageIcon icon[]		= new ImageIcon[2];
	private boolean isFindImage			= false;
	private int GRID_NUM;
	private EightQueen eq;
	private JLabel borad[][];


// �غc�l -----------------------------------------------------------
	BoradPanel(){
		this(8);
	}

	BoradPanel(int num)
	{
		GRID_NUM		= num;
		borad			= new JLabel[GRID_NUM][GRID_NUM];
		eq				= new EightQueen(num);

		/** try to Read the Images */
		icon[0] = tools.createImageIcon("images/wqueen.gif");
		icon[1] = tools.createImageIcon("images/bqueen.gif");

		/** File Not Find Exception Handle */
		if ( icon[0]==null || icon[1]==null ){
			isFindImage = false;
			JOptionPane.showMessageDialog(null,"�S����쥲�n�v����!! "+
				" �N�H��rQ (�j�gq) �@���ӦZ��m��� !!","ĵ�i!!",
				JOptionPane.ERROR_MESSAGE);
		}else
			isFindImage = true;
		
		/** set Layout & put Label on it */
		this.setLayout( new GridLayout(GRID_NUM,GRID_NUM) );
		for ( int r=0 ; r<GRID_NUM ; r++ ){
			for ( int c=0 ; c<GRID_NUM ; c++ ){
				borad[r][c] = new JLabel("",JLabel.CENTER);
				borad[r][c].setForeground(mcolor[(r+c+1)%mcolor.length]);
				borad[r][c].setBackground(mcolor[(r+c)%mcolor.length]);
				// opaque the background color of the Buttons
				borad[r][c].setOpaque(true);
				// add 
				this.add( borad[r][c] );
			}
		}//end for

		/** get Eight Queen Solution and diplay on the board */
		setQueen( eq.nextSolution() );
	}

// �]�w -------------------------------------------------------------
	public void setQueen(boolean[][] m)
	{
		for (int r=0;r<m.length ;r++){
			for (int c=0;c<m[r].length ;c++ ){
				// makesure the statue
				if ( isFindImage ){
					borad[r][c].setIcon( m[r][c] ? 
						icon[(r+c)%icon.length] : null );	//Image Mode					
				}else
					borad[r][c].setText( m[r][c]?"Q":"" );	//Text Mode
			}//end for
		}//end for
	}//end setQueen

// ���եD�{�� -------------------------------------------------------
	public static void main(String[] args)
	{
		JFrame f = new JFrame("���յ{��");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new BoradPanel());
		f.setSize(300,300);
		f.show();
	}// end main

// �ާ@�禡 -----------------------------------------------------------

	/** �O�_����@�Ӹѵ� */
	public boolean hasMore(){
		return eq.hasMoreSolution();
	}// end hasMore

	/** �O�_���e�@�Ӹѵ� */
	public boolean hasPrev(){
		return eq.hasPrevSolution();
	}//end hasPrev

	/** �ݫe�@�Ӹѵ� */
	public void viewPrev(){
		if ( !hasPrev() )
			return;
		setQueen(eq.prevSolution());
	}//end viewPrevious

	/** �ݫ�@�Ӹѵ� */
	public void viewNext(){
		if ( !hasMore() )
			return;
		setQueen(eq.nextSolution());
	}//end viewNext

	/** �ثe�Ҧb�ѵ��s�� */
	public int curr(){
		return eq.currSolution();
	}//end curr

	/** �`�@�ѵ��ƥ� */
	public int count(){
		return eq.countSolution();
	}//end count

	/** �e���� n �Ӹѵ� */
	public boolean go(int indx){
		/** OutOfIndexException */
		if ( indx<1 || indx>eq.countSolution() ){
			JOptionPane.showMessageDialog(this ,
				"�A��J���Ʀr�W�L�d��F!!","ĵ�i!!",
				JOptionPane.ERROR_MESSAGE);
			return false;
		}
		setQueen(eq.gotoSolution(indx));
		return true;
	}//end go

// �g�J�ɮ� ---------------------------------------------------------
	/** �N�ثe�o�Ӹѵ��g�J�ɮפ� */
	public void writeToFile(String path)throws IOException 
	{
		File outputFile = new File(path);
        FileWriter out = new FileWriter(outputFile);

		out.write("�o�O��"+eq.currSolution()+"�Ӹ�\r\n"+
				"================\r\n");
		for ( int i=0;i<borad.length;i++){
			for (int j=0;j<borad[i].length;j++)
				out.write( ( borad[i][j].getIcon()==icon[0]
					|| borad[i][j].getIcon()==icon[1] )?"Q ":"- " );
			out.write("\r\n");
		}out.write("\r\n");
        out.close();
	}//end writeToFile

	/** �N�Ҧ��ѵ��g�J�ɮפ� */
	public void writeAllToFile(String path)throws IOException 
	{
		EightQueen qq = new EightQueen(GRID_NUM);
		
		File outputFile = new File(path);
        FileWriter out = new FileWriter(outputFile);

		boolean[][] tmp;
		while( qq.hasMoreSolution()!=false )
		{
			tmp = qq.nextSolution();
			out.write("�o�O��"+qq.currSolution()+"�Ӹ�\r\n"+
					"================\r\n");
			for ( int i=0;i<tmp.length;i++){
				for (int j=0;j<tmp[i].length;j++)
					out.write( (tmp[i][j]==true)?"Q ":"- " );
				out.write("\r\n");
			}out.write("\r\n");
		}
		out.write("================\r\n"+
			"�`�@��"+qq.countSolution()+"�Ӹ�\r\n");
        out.close();
	}//end writeAllToFile

}//end BoradPanel
