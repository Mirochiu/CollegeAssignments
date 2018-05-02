import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.URL;

public class Game extends JFrame implements ActionListener,ItemListener
{
	private ImageIcon[] img		= new ImageIcon[9];
	private Timer timer;

	private JButton[] puzzle		= new JButton[9];
	private JLabel showTime , showText;
	private JButton pause_start , showImg;
	private JComboBox selectlv;

	private JFrame j;

	String[] strlv  	= {" �� �� "," �� �� "," �� �� "};
	int[] level			= {90 , 60 , 30};
	boolean isPause		= true;

	Game()
	{
		// �ݥ��Ϫ�����
		j = new JFrame();
		j.getContentPane().add(new JLabel(new ImageIcon("images/image.jpg")));
		j.setTitle("����Ϥ�");
		j.setSize(610,510);
		j.setResizable(false);		//����վ�����j�p
		j.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		// �N�Ϥ�Ū�i��
		for ( int i=0 ; i<img.length ; i++ )
		{
			URL path = getClass().getResource("images/img_"+(i+1)+".jpg");
			img[i] = new ImageIcon(path);
		}

		getContentPane().setLayout(new BorderLayout());

		// �N���ϳ]�w�n
		JPanel pl = new JPanel(new GridLayout(3,3,2,2));
		for (int i=0 ; i < 9 ; i++)
		{
			puzzle[i] = new JButton(img[i]);
			puzzle[i].addActionListener(this);
			pl.add(puzzle[i]);
		}
		puzzle[8].setIcon(null);	//�̫�@�Ӥ����
		getContentPane().add(pl,BorderLayout.CENTER);

		// �N����C�]�w�n
		showText	= new JLabel("�ɶ�",JLabel.CENTER);
		showTime	= new JLabel(""+level[0],JLabel.CENTER);
		pause_start	= new JButton("�} �l"); 
		pause_start.addActionListener(this);
		showImg		= new JButton("����Ϥ�");
		showImg.addActionListener(this);
		selectlv	= new JComboBox(strlv);
		selectlv.addItemListener(this);

		JPanel pp	= new JPanel(new GridLayout(1,5));
		pp.add(showText);
		pp.add(showTime);
		pp.add(pause_start);
		pp.add(showImg);
		pp.add(selectlv);
		getContentPane().add(pp,BorderLayout.SOUTH);

		// �C���_�l�]�w
		timer = new Timer(1000,this);
		makeRandom();
	}

	public static void main(String[] args)
	{
		Game f = new Game();
		f.setTitle(" �� �� �C �� ");
		f.setSize(610,540);
		f.setResizable(false);		//����վ�����j�p
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.show();
	}

	public void gameOver(boolean win)
	{
		gamePause();
		if ( win==true )
			JOptionPane.showMessageDialog(this,"���MĹ�F....�j��!!~");
		else
			JOptionPane.showMessageDialog(this,"Game Over!! �A���~������");
	}

	public void gameStart()
	{
		isPause = false;
		pause_start.setText("�� ��");
		timer.start();
	}

	public void gamePause()
	{
		isPause = true;
		pause_start.setText("�} �l");
		timer.stop();
	}

	private void makeRandom()
	{
		for ( int i=7 ; i>=0 ;i-- )
		{
			int s = (int)(Math.random()*i);	// ��o~(i-1)�������Ʀr
			swap( i , s );
		}
	}

	public void swap(int l1 ,int l2)
	{
		Icon tmp = puzzle[l1].getIcon();
		puzzle[l1].setIcon( puzzle[l2].getIcon() );
		puzzle[l2].setIcon( tmp );
	}

	// �]�w��ܵ��Ū��ɶ�
	public void itemStateChanged(ItemEvent ex)
	{
		gamePause();	// �Ȱ��C��
		makeRandom();	// ���s���ùϤ�
		if ( ex.getItem()==strlv[0] )
		{
			showTime.setText(""+level[0]);
		}
		else if ( ex.getItem()==strlv[1] )
		{
			showTime.setText(""+level[1]);
		}
		else
			showTime.setText(""+level[2]);
	}

	// ���U���s�|�o�ͪ��Ʊ�
	public void actionPerformed(ActionEvent e)
	{
		if ( e.getSource()==timer )
		{
			int newtime = Integer.parseInt(showTime.getText())-1;
			showTime.setText(""+newtime);
			if ( newtime == 0 )
			{
				gameOver( false );
			}
		}
		else if ( e.getSource()==pause_start )
		{
			if ( isPause==true )
				gameStart();
			else
				gamePause();
		}
		else if ( e.getSource()==showImg )
		{
			j.show();
		}
		else if ( isPause==false )
		{
			/** �ˬd�@�Ϊ����s */
			for (int i=0; i<puzzle.length ; i++)
			{
				if (e.getSource()!=puzzle[i]) continue;
				Icon icon = puzzle[i].getIcon();
				if (icon==null) continue;

				int left  = i%3-1;
				int right = i%3+1;
				int up    = i-3;
				int down  = i+3;

				if(left>=0)
				{
					if(puzzle[i-1].getIcon()==null)
						swap(i, i-1);
				}
				if(right<3)
				{
					if(puzzle[i+1].getIcon()==null)
						swap(i, i+1);
				}
				if(up>=0)
				{
					if(puzzle[up].getIcon()==null)
						swap(i, up);
				}
				if(down<9)
				{
					if(puzzle[down].getIcon()==null)
						swap(i, down);
				}

				if ( InOrdered()==true )
				{
					gameOver(true);
				}
			}
		}
	}

	public boolean InOrdered()
	{
		for (int i=0 ; i<9 ; i++ )
		{
			if ( puzzle[i]!=(Icon)img[i] )
				return false;
		}
		return true;
	}
}
