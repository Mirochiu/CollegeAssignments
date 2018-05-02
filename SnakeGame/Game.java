public class Game extends javax.swing.JFrame
	implements java.awt.event.ActionListener
{
	// Game's Comp
	javax.swing.JLabel[] gfw				// �C���ϰ�
		= new javax.swing.JLabel[18*21];
	javax.swing.Timer 	gt , st;			// �C���ɶ� , ��s�D��m�ɶ�
	javax.swing.JLabel	Score , Times;		// show Score , GameTime
	java.awt.Color[]	mark 
		= new java.awt.Color[3];			// food, body , common

	// Record
	private int score;						// ���Ƭ���
	private int currTime;					// �Ѿl�ɶ�����
	private int direct;						// �e�i��V
	private Snake snake;					// �D����
	private int lastBody[] ,newBody[];		// �D�����m(�e) ,(��)
	private int foodpos;					// ��������m
	private boolean GameOver;				// �C������
	private int speed;						// �C���t�׵���

	/** Constructor */
	public Game()
	{
		score		= 0;					// �_�l����
		currTime	= 120;					// �C���_�l�ɶ�120s
		direct		= Snake.RIGHT;
		speed		= 5;					// �t��Lv5
		// �]�w�򥻦�m
		mark[0] = java.awt.Color.WHITE;		// common
		mark[1] = java.awt.Color.GRAY;		// body
		mark[2] = java.awt.Color.GREEN;		// food
		// �إ߭p�ɾ�
		gt = new javax.swing.Timer(1000,this);	// �C���p�ɾ�
		st = new javax.swing.Timer(500,this);	// �D�����ʧ�s

		// �إ߳g���D�C���ϰ�
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
		// �إ߹C�����A��ܰϰ�
		javax.swing.JPanel sp = new javax.swing.JPanel(new java.awt.FlowLayout());
		sp.add(Times = new javax.swing.JLabel("�Ѿl�ɶ�"+currTime));
		sp.add(Score = new javax.swing.JLabel("�ثe�ұo����: "+score));
		getContentPane().add(sp,java.awt.BorderLayout.SOUTH);
		// �إ���L�ƥ�
		addKeyListener(new KeyEvn());
		// ��l�C��
		startGame();
		// �]�wMenu
		setJMenuBar(new conf());
		//set Basic GUI
		setTitle("�g���D");
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
			game = new javax.swing.JMenu(" �C �� (G)");
			speed = new javax.swing.JMenu(" �t �� (S)");
			add(game);
			add(speed);
			// game
			restart = new javax.swing.JMenuItem("���s�C�� Resart");
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
		// �إ�Snake����
		snake = new Snake();
		// ���o�D�����骺��m ,��
		setBody(newBody = snake.getSnakeBody());
		// ���ͷs������
		setNewFood();
		// �~���C������
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
		// �W�[���� ,�W�[���� ,���ͷs������
		Score.setText("�ثe�ұo����: "+(score+=5));
		snake.eatFood();
		setNewFood();
	}// end eatFood

	/** Generate the new food */
	private void setNewFood(){
		while (true){
			// �üƦ�m
			foodpos = (int)(Math.random()*(18*21));
				System.err.println("food in: ("+(foodpos%21)+","+(foodpos/21)+")");
			//'�@��'�ϰ�~��񭹪�
			if ( gfw[foodpos].getBackground()==mark[0] ){
				gfw[foodpos].setBackground(mark[2]);
				return;		// break;
			}
		}
	}// end setNewFood

	/** �ƥ�B�z */
	public void actionPerformed(java.awt.event.ActionEvent e)
	{
		/**
		* �N�D�����驹�Y��V���ʤ@��
		* �p�G���ʨ쪺����O����
		* �N�I�seatFood()
		*/
		if ( e.getSource()==st )
		{
			switch ( direct ){
				case Snake.UP:		snake.moveUp(); break;
				case Snake.DOWN:	snake.moveDown(); break;
				case Snake.LEFT:	snake.moveLeft(); break;
				case Snake.RIGHT:	snake.moveRight(); break;
			}

			// ���o�s���D��m
			lastBody = newBody;
			newBody = snake.getSnakeBody();
			// �ˬd���S���Y�쭹��
			if ( foodpos==newBody[0] ){
				System.err.println("Eaten!");
				eatFood();
			}
			// �ˬd���S���r��ۤv
			if ( gfw[newBody[0]].getBackground()==mark[1] )
			{
				javax.swing.JOptionPane.showMessageDialog(this, 
					"�A�r���ۤv�F~�C������! �A�`�@�o�F"+score+"��", "GameOver", 
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
				GameOver = true;
				st.stop();
				gt.stop();
				return;
			}
			// �M���C���ϰ�W�D���¦�m
			clearBody(lastBody);

			// �]�w�s���D��m��C���ϰ�
			setBody(newBody);
		}
		else if ( e.getSource()==gt ){
			currTime--;
			Times.setText("�Ѿl�ɶ�: "+currTime);
			if ( currTime==0 ){
				javax.swing.JOptionPane.showMessageDialog(null, 
					"�ɶ���F~�A�`�@�o�F"+score+"��", "GameOver", 
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
			// �ˬd���U�h����� 
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