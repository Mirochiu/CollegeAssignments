import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

public class InfoDialog extends JDialog
	implements ActionListener
{
    private javax.swing.ImageIcon photo;
    private JPanel main ,		// put JPanel: photo ,info
				   check ,		// put JButton: btn_OK
				   info;		// put author's information
    private JLabel info_photo ,		// show photo
				   info_name ,		// show author's name
				   info_major ,		// show author's major
				   info_mail;		// show author's mail
    private JButton btn_ok;		// user's response

	//Default Constructor
	InfoDialog(){
		this(null,false);
	}

	//Constructor
	InfoDialog(java.awt.Frame owner,boolean modal){
		super(owner ,modal);

		// JPanel: info's configuration
		info		= new JPanel(new FlowLayout());
		info.setBorder(
			javax.swing.BorderFactory.createTitledBorder(
			"關於作者")
		);
		info_name	= new JLabel("邱永祥");
		info_major	= new JLabel("淡江大學資訊工程一年D班");
		info_mail	= new JLabel("mirror1986@yahoo.com.tw");
		info.add(info_name);
		info.add(info_major);
		info.add(info_mail);
				// JPaenl: info_photo's configuration
		info_photo	= new JLabel(
			photo = tools.createImageIcon("images/air.jpg") );
		btn_ok		= new JButton("確定");
		btn_ok.addActionListener(this);

		// Container: c's configuration 
		java.awt.Container c	= getContentPane();
		c.setLayout(new BorderLayout(5,5));
		main		= new JPanel(new GridLayout(1,2));
		check		= new JPanel(new FlowLayout());
		c.add(main ,BorderLayout.CENTER);
		c.add(check ,BorderLayout.SOUTH);

		// JPanel: main ,check 's configuration
		main.add(info_photo);
		main.add(info);
		check.add(btn_ok);

		// Dialog: InfoDialog's configuration
		setTitle("關於");
		setSize(370,180);
		addWindowListener(new evn_exit());
		tools.localCenter(this);
			// show Dialog in the center of screen
		setResizable(false);
	}//end About

	public void actionPerformed(ActionEvent e){
		if(e.getSource()==btn_ok){
			hide();
		}
	}//end actionPerformed

	private class evn_exit extends WindowAdapter{
		public void windowsClosing(WindowEvent e){
			hide();
		}
	}//end class evn_exit

	public static void main(String[] argv){
		InfoDialog a = new InfoDialog(null,false);
		a.show();
		System.out.println("Dialog is hidden , isn't closed !");
	}//end main
}
