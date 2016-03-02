package gui.test;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JFrame;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;
import de.yadrone.base.video.ImageListener;
import gui.SpaceXGUI;

public class GUITest extends JFrame {
	private static final long serialVersionUID = 1L;
	
	IARDrone drone = null;
	
	public GUITest() {
		// Instantiate drone and sync with GUI
		try {
			drone = new ARDrone();
			drone.addExceptionListener(new IExceptionListener() {
				public void exeptionOccurred(ARDroneException e) {
					e.printStackTrace();
				}
			});
			
			drone.start();
			
			while(!drone.getCommandManager().isConnected());
			
			drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
			
			drone.getVideoManager().addImageListener(new ImageListener() {
				@Override
	            public void imageUpdated(BufferedImage newImage) {
	            	SpaceXGUI.updateImage(newImage);
	            }
	        });
			
			SpaceXGUI.getInstance().getBPanel().getCamNext().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drone.getCommandManager().setVideoChannel(VideoChannel.NEXT);
				}
			});
			
			while(true);
			
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			if (drone != null)
				drone.stop();

			System.exit(0);
		}
	}

	public static void main(String[] args) {
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
		
        new GUITest();
	}
}
