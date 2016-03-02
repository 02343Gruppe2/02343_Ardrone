package gui.test;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;
import de.yadrone.base.video.ImageListener;
import gui.SpaceXGUI;

public class GUITest {
	IARDrone drone;
	
	public GUITest() {
		createAndShowGUI();
		
		drone = new ARDrone();
		drone.addExceptionListener(new IExceptionListener() {
			public void exeptionOccurred(ARDroneException exc)
			{
				exc.printStackTrace();
			}
		});
		
		drone.start();
		
		drone.getVideoManager().addImageListener(new ImageListener() {
            public void imageUpdated(BufferedImage newImage) {
            	SpaceXGUI.updateImage(newImage);
            }
        });
	}
	
	public static void main(String[] args) {
		new GUITest();
	}
	
	public void createAndShowGUI() {
		// Create window
		JFrame f = new JFrame("Space X");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBackground(Color.decode("#333333"));
		f.setResizable(false);

        // Create the content pane
        JComponent c = SpaceXGUI.getInstance();
        c.setOpaque(false);
        f.setContentPane(c);

        // Draw the window
        f.pack();
        f.setVisible(true);
	}
}
