import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JDialog;

public class tools
{
	public static void localCenter(JFrame f){
		// Get the dimension of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;

		// Get the dimension of the frame
		Dimension frameSize = f.getSize();
		int x = (screenWidth - frameSize.width)/2;
		int y = (screenHeight - frameSize.height)/2;
		f.setLocation(x,y);
	}//end showCenter

	public static void localCenter(JDialog f){
		// Get the dimension of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;

		// Get the dimension of the frame
		Dimension frameSize = f.getSize();
		int x = (screenWidth - frameSize.width)/2;
		int y = (screenHeight - frameSize.height)/2;
		f.setLocation(x,y);
	}//end showCenter

	public static ImageIcon createImageIcon(String path){
		java.net.URL imgURL = tools.class.getResource(path);
        if (imgURL != null){
			return new ImageIcon(imgURL);
		}else{
			System.err.println("Couldn't find file: " + path);
			return null;
		}
    }//end createImageIcon

	/** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path,
                                               String description)
	{
        java.net.URL imgURL = tools.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }// end createImageIcon
}
