/*
 * Name : Tool's Collection
 * Copyright : no
 * Description : 從各路收集而來的獨立method
 *               收集起來以方便於製作程式
*/

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.Math;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.ImageIcon;

public class tools
{
	// 產生0到n-1的亂數
	public static int rand(int n) {
		return (int)(Math.random()*n); 
	}

	// set JFrame locate at the center of screen
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
