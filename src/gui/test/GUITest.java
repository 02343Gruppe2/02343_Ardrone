package gui.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
	private static final int DEFAULT_SPEED = 1;
	
	private IARDrone drone = null;
	
	public GUITest() {
		// Instantiate drone and sync with GUI
		try {
			drone = new ARDrone();
			drone.addExceptionListener(new IExceptionListener() {
				public void exeptionOccurred(ARDroneException e) {
					e.printStackTrace();
				}
			});
			
			//drone.start();
			
			while(!drone.getCommandManager().isConnected());
			
			drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
			
			drone.getVideoManager().addImageListener(new ImageListener() {
				@Override
	            public void imageUpdated(BufferedImage newImage) {
	            	SpaceXGUI.updateImage(newImage);
	            }
	        });
			
			/**							  **/
			/** Listeners for all buttons **/
			/**							  **/
			SpaceXGUI.getInstance().getBPanel().getCamNext().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drone.getCommandManager().setVideoChannel(VideoChannel.NEXT);
				}
			});
			
			SpaceXGUI.getInstance().getBPanel().getCamHori().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
				}
			});
			
			SpaceXGUI.getInstance().getBPanel().getCamVert().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drone.getCommandManager().setVideoChannel(VideoChannel.VERT);
				}
			});
			
			SpaceXGUI.getInstance().getBPanel().getNavHover().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drone.getCommandManager().hover();
				}
			});
			
			SpaceXGUI.getInstance().getBPanel().getNavKill().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drone.getCommandManager().emergency();
				}
			});
			
			SpaceXGUI.getInstance().getBPanel().getNavLand().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drone.getCommandManager().landing();
				}
			});
			
			SpaceXGUI.getInstance().getBPanel().getPanLeft().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drone.getCommandManager().goLeft(DEFAULT_SPEED);
				}
			});
			
			SpaceXGUI.getInstance().getBPanel().getPanRight().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drone.getCommandManager().goRight(DEFAULT_SPEED);
				}
			});
			
			SpaceXGUI.getInstance().getBPanel().getPanUp().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drone.getCommandManager().up(DEFAULT_SPEED);
				}
			});
			
			SpaceXGUI.getInstance().getBPanel().getPanDown().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drone.getCommandManager().down(DEFAULT_SPEED);
				}
			});
			
			// Keep running 'till GUI is closed.
			while(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (drone != null)
				drone.stop();

			System.exit(0);
		}
	}

	public static void main(String[] args) {
        new GUITest();
	}
}
