public class Game extends javax.swing.JFrame
	implements java.awt.event.ActionListener
{
	// Game's Comp
	javax.swing.JLabel[] gfw				// 遊戲區域
		= new javax.swing.JLabel[18*21];
	javax.swing.Timer 	gt , st;			// 遊戲時間 , 更新蛇位置時間
	javax.swing.JLabel	Score , Times;		// show Score , GameTime
	java.awt.Color[]	mark 
		= new java.awt.Color[3];			// food, body , common

	// Record
	private int score;						// 分數紀錄
	private int currTime;					// 剩餘時間紀錄
	private int direct;						// 前進方向
	private Snake snake;					// 蛇物件
	private int lastBody[] ,newBody[];		// 蛇身體位置(前) ,(後)
	private int foodpos;					// 食物的位置
	private boolean GameOver;				// 遊戲結束
	private int speed;						// 遊戲速度等級

	/** Constructor */
	public Game()
	{
		score		= 0;					// 起始分數
		currTime	= 120;					// 遊戲起始時間120s
		direct		= Snake.RIGHT;
		speed		= 5;					// 速度Lv5
		// 設定基本色彩
		mark[0] = java.awt.Color.WHITE;		// common
		mark[1] = java.awt.Color.GRAY;		// body
		mark[2] = java.awt.Color.GREEN;		// food
		// 建立計時器
		gt = new javax.swing.Timer(1000,this);	// 遊戲計時器
		st = new javax.swing.Timer(500,this);	// 蛇的移動更新

		// 建立貪食蛇遊戲區域
		javax.swing.JPanel gp = new javax.swing.JPanel(
			new java.awt.GridLayout(18,21,0,0)
			);
		for (int i=0;i<gfw.length;i++){
			gfw[i] = new javax.swing.JLabel();
			gfw[i].setOpaque(true);
			gfw[i].setBackground(mark[0]);
			gp.add(gfw[i]);
		}
		getContentPane().add(gp);
		// 建立遊戲狀態顯示區域
		javax.swing.JPanel sp = new javax.swing.JPanel(new java.awt.FlowLayout());
		sp.add(Times = new javax.swing.JLabel("剩餘時間"+currTime));
		sp.add(Score = new javax.swing.JLabel("目前所得分數: "+score));
		getContentPane().add(sp,java.awt.BorderLayout.SOUTH);
		// 建立鍵盤事件
		addKeyListener(new KeyEvn());
		// 初始遊戲
		startGame();
		// 設定Menu
		setJMenuBar(new conf());
		//set Basic GUI
		setTitle("貪食蛇");
		setSize(540,465);
		setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		show();
	}
	class conf extends javax.swing.JMenuBar implements java.awt.event.ActionListener
	{
		private javax.swing.JMenu speed , game;
		private javax.swing.JRadioButtonMenuItem speedLv[]
			= new javax.swing.JRadioButtonMenuItem[9];
		private javax.swing.JMenuItem restart;
		conf()
		{
			game = new javax.swing.JMenu(" 遊 戲 (G)");
			speed = new javax.swing.JMenu(" 速 度 (S)");
			add(game);
			add(speed);
			// game
			restart = new javax.swing.JMenuItem("重新遊戲 Resart");
			restart.addActionListener(this);
			game.add(restart);
			// speed
			javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
			for (int i=0;i<speedLv.length;i++){
				speedLv[i] = new javax.swing.JRadioButtonMenuItem("Level "+(i+1));
				speedLv[i].addActionListener(this);
				group.add(speedLv[i]);
				speed.add(speedLv[i]);
			}
			speedLv[4].setSelected(true);
		}
		public void actionPerformed(java.awt.event.ActionEvent e){
			Object s = e.getSource();
			for (int i=0;i<speedLv.length;i++){
				if ( s!=speedLv[i] ) continue;

				speedLv[i].setSelected(true);
				setSpeed(i);
				return;
			}
			if ( s==restart )
			{
				startGame();
			}
		}
	}//end conf
	public void setSpeed(int speedLv){
		speed = speedLv%10;
		st.setDelay(290-30*speed);
	}
	private void startGame(){
		flush();
		// 建立Snake物件
		snake = new Snake();
		// 取得蛇的身體的位置 ,並
		setBody(newBody = snake.getSnakeBody());
		// 產生新的食物
		setNewFood();
		// 洗掉遊戲紀錄
		score = 0;
		currTime = 120;
		setSpeed(speed);
		GameOver = false;
	}
	public void flush(){
		for (int i=0;i<gfw.length;i++){
			gfw[i].setBackground(mark[0]);
		}
	}
	/** Snake eat the food */
	private void eatFood(){
		// 增加分數 ,增加長度 ,產生新的食物
		Score.setText("目前所得分數: "+(score+=5));
		snake.eatFood();
		setNewFood();
	}// end eatFood

	/** Generate the new food */
	private void setNewFood(){
		while (true){
			// 亂數位置
			foodpos = (int)(Math.random()*(18*21));
				System.err.println("food in: ("+(foodpos%21)+","+(foodpos/21)+")");
			//'一般'區域才能放食物
			if ( gfw[foodpos].getBackground()==mark[0] ){
				gfw[foodpos].setBackground(mark[2]);
				return;		// break;
			}
		}
	}// end setNewFood

	/** 事件處理 */
	public void actionPerformed(java.awt.event.ActionEvent e)
	{
		/**
		* 將蛇的身體往某方向移動一格
		* 如果移動到的那格是食物
		* 就呼叫eatFood()
		*/
		if ( e.getSource()==st )
		{
			switch ( direct ){
				case Snake.UP:		snake.moveUp(); break;
				case Snake.DOWN:	snake.moveDown(); break;
				case Snake.LEFT:	snake.moveLeft(); break;
				case Snake.RIGHT:	snake.moveRight(); break;
			}

			// 取得新的蛇位置
			lastBody = newBody;
			newBody = snake.getSnakeBody();
			// 檢查有沒有吃到食物
			if ( foodpos==newBody[0] ){
				System.err.println("Eaten!");
				eatFood();
			}
			// 檢查有沒有咬到自己
			if ( gfw[newBody[0]].getBackground()==mark[1] )
			{
				javax.swing.JOptionPane.showMessageDialog(this, 
					"你咬死自己了~遊戲結束! 你總共得了"+score+"分", "GameOver", 
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
				GameOver = true;
				st.stop();
				gt.stop();
				return;
			}
			// 清除遊戲區域上蛇的舊位置
			clearBody(lastBody);

			// 設定新的蛇位置到遊戲區域
			setBody(newBody);
		}
		else if ( e.getSource()==gt ){
			currTime--;
			Times.setText("剩餘時間: "+currTime);
			if ( currTime==0 ){
				javax.swing.JOptionPane.showMessageDialog(null, 
					"時間到了~你總共得了"+score+"分", "GameOver", 
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
				GameOver = true;
				st.stop();
				gt.stop();
			}
		}
	}//end actionPerformed

	private void clearBody(int[] a){
		for (int i=0;i<a.length ; i++){
			gfw[a[i]].setBackground(mark[0]);
		}
	}//end clearBody

	private void setBody(int[] a){
		for (int i=0;i<a.length ; i++){
			gfw[a[i]].setBackground(mark[1]);
		}
	}//end setBody

	class KeyEvn extends java.awt.event.KeyAdapter{
		public void keyPressed(java.awt.event.KeyEvent e){
			if ( GameOver ) return;
			// 檢查按下去的鍵值 
			int psk = e.getKeyCode();
			switch (psk){
				case java.awt.event.KeyEvent.VK_UP:
					direct = Snake.UP; break;
				case java.awt.event.KeyEvent.VK_DOWN:
					direct = Snake.DOWN; break;
				case java.awt.event.KeyEvent.VK_LEFT:
					direct = Snake.LEFT; break;
				case java.awt.event.KeyEvent.VK_RIGHT:
					direct = Snake.RIGHT; break;
			}
			if ( st.isRunning()==false || gt.isRunning()==false ){
				st.start();
				gt.start();
			}
		}
	}//end class KeyEvn

	public static void main(String[] a){
		Game g = new Game();
	}
}//end class Game