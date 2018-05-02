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
	static final int	RemindTime	= 90;	//設定遊戲時間

	fin_work2()
	{
		/** 將各元件放到視窗中 */
		JPanel jp_puzzle	= new JPanel(new GridLayout(3,3,2,2));
		lbl_Time			= new JLabel(""+RemindTime);
		lbl_TimeDescript	= new JLabel("剩餘時間");
		JPanel jp_timer		= new JPanel(new FlowLayout());
		jp_timer.add(lbl_TimeDescript);
		jp_timer.add(lbl_Time);

		Container c			= getContentPane();
		c.setLayout(new BorderLayout(5,5));
		c.add(jp_puzzle ,BorderLayout.CENTER);
		c.add(jp_timer ,BorderLayout.NORTH);

		/** 讀入圖片與建立按鈕實體及設定 */
		for (int i=0;i<puzzle.length ;i++){
			// create Instance & set
			puzzle[i]	= new JButton();
			puzzle[i].addActionListener(this);
			jp_puzzle.add(puzzle[i]);		//加到panel
			// load Image
			imgInOrder[i]	= (i==8)?null:tools.createImageIcon("images/img_"+(i+1)+".jpg");
			imgInRand[i]	= imgInOrder[i];
		}

		/** 將圖片打亂後裝上按鈕 */
		imgInRand = getRandImageIcon(imgInRand);
		for (int i=0;i<puzzle.length ;i++){
				puzzle[i].setIcon(imgInRand[i]);
		}

		/** 將視窗秀出來 */
		createAndShowGUI();

		/** 設定計時器 與啟動 */
		CountTime		= new Timer(1000,this);
		CountTime.start();
	}// end fin_work2

	private void createAndShowGUI(){
		this.setTitle("期末作業 2.拼圖遊戲");
		this.setSize(580,410);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		tools.localCenter(this);
		this.show();
	}//end createAndShowGUI

	public ImageIcon[] getRandImageIcon(ImageIcon[] tar){
		// 利用類似選擇排序法的方式取亂數
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
				Object[] options = { "重新再來? ", "不玩了! " };
				int SelVal = JOptionPane.showOptionDialog(this, "噢!你運氣真不好...\n"+
				"請問你還要玩嗎?? ", "時間到! 未完成 ...", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE , null, options,options[0]);
				if ( SelVal==0 )
					resetPuzzle();
			}//else;
			return;
		}
		else
		{
			/** 檢查作用的按鈕 */
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

			/** 檢查拼圖是否已經完成 */
			for (int i=0;i<puzzle.length ;i++){
				if (imgInOrder[i]!=puzzle[i].getIcon())	return;
			}
			//通過檢查，拼圖完成!
			Object[] options = { "再玩一次? ", "不玩了! " };
			int SelVal = JOptionPane.showOptionDialog(this, "恭喜你! 你完成拼圖了 *^_^*\n"+
				"請問你還要玩嗎?? ", "恭喜!! 拼圖完成", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE , null, options,options[0]);
			if ( SelVal==0 ) resetPuzzle();

		}//end else
	}// end actionPerformed
}//end class

