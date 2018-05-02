//
// 搭配 n-Queen 所做的修改
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


// 建構子 -----------------------------------------------------------
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
			JOptionPane.showMessageDialog(null,"沒有找到必要影像檔!! "+
				" 將以文字Q (大寫q) 作為皇后位置表示 !!","警告!!",
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

// 設定 -------------------------------------------------------------
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

// 測試主程式 -------------------------------------------------------
	public static void main(String[] args)
	{
		JFrame f = new JFrame("測試程式");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new BoradPanel());
		f.setSize(300,300);
		f.show();
	}// end main

// 操作函式 -----------------------------------------------------------

	/** 是否有後一個解答 */
	public boolean hasMore(){
		return eq.hasMoreSolution();
	}// end hasMore

	/** 是否有前一個解答 */
	public boolean hasPrev(){
		return eq.hasPrevSolution();
	}//end hasPrev

	/** 看前一個解答 */
	public void viewPrev(){
		if ( !hasPrev() )
			return;
		setQueen(eq.prevSolution());
	}//end viewPrevious

	/** 看後一個解答 */
	public void viewNext(){
		if ( !hasMore() )
			return;
		setQueen(eq.nextSolution());
	}//end viewNext

	/** 目前所在解答編號 */
	public int curr(){
		return eq.currSolution();
	}//end curr

	/** 總共解答數目 */
	public int count(){
		return eq.countSolution();
	}//end count

	/** 前往第 n 個解答 */
	public boolean go(int indx){
		/** OutOfIndexException */
		if ( indx<1 || indx>eq.countSolution() ){
			JOptionPane.showMessageDialog(this ,
				"你輸入的數字超過範圍了!!","警告!!",
				JOptionPane.ERROR_MESSAGE);
			return false;
		}
		setQueen(eq.gotoSolution(indx));
		return true;
	}//end go

// 寫入檔案 ---------------------------------------------------------
	/** 將目前這個解答寫入檔案中 */
	public void writeToFile(String path)throws IOException 
	{
		File outputFile = new File(path);
        FileWriter out = new FileWriter(outputFile);

		out.write("這是第"+eq.currSolution()+"個解\r\n"+
				"================\r\n");
		for ( int i=0;i<borad.length;i++){
			for (int j=0;j<borad[i].length;j++)
				out.write( ( borad[i][j].getIcon()==icon[0]
					|| borad[i][j].getIcon()==icon[1] )?"Q ":"- " );
			out.write("\r\n");
		}out.write("\r\n");
        out.close();
	}//end writeToFile

	/** 將所有解答寫入檔案中 */
	public void writeAllToFile(String path)throws IOException 
	{
		EightQueen qq = new EightQueen(GRID_NUM);
		
		File outputFile = new File(path);
        FileWriter out = new FileWriter(outputFile);

		boolean[][] tmp;
		while( qq.hasMoreSolution()!=false )
		{
			tmp = qq.nextSolution();
			out.write("這是第"+qq.currSolution()+"個解\r\n"+
					"================\r\n");
			for ( int i=0;i<tmp.length;i++){
				for (int j=0;j<tmp[i].length;j++)
					out.write( (tmp[i][j]==true)?"Q ":"- " );
				out.write("\r\n");
			}out.write("\r\n");
		}
		out.write("================\r\n"+
			"總共有"+qq.countSolution()+"個解\r\n");
        out.close();
	}//end writeAllToFile

}//end BoradPanel
