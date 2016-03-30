package core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import gui.SpaceXGUI;

import org.opencv.core.Core;

import utils.FormattedTimeStamp;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;
import de.yadrone.base.video.ImageListener;
import network.DroneConnection;

/**
 * 
 * @author Kristin Hansen
 *
 */
public class SpaceXDrone {

	public static boolean isHorizontal = true;
	private FlightAlgo flightAlgo;
	
	public SpaceXDrone() {
		
		// We instantiate a null-object with the ARDrone interface
		IARDrone drone = null;
		boolean running = false;
		try {
			SpaceXGUI.getInstance("[" + FormattedTimeStamp.getTime() + "] Welcome to SpaceX Drone GUI");
			// Create instance of new ARDrone
			drone = new ARDrone();
			
			drone.addExceptionListener(new IExceptionListener() {
				public void exeptionOccurred(ARDroneException e) {
					System.err.println("Drone exception: " + e.getMessage());
					SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] Critical drone error: " + e.getMessage());
				}
			});
			// Start the drone managers (NavData, CommandManager etc.)
			drone.start();
			
			drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
			drone.getVideoManager().addImageListener(new ImageListener() {
				@Override
	            public void imageUpdated(BufferedImage newImage) {
					SpaceXGUI.updateImage(newImage, isHorizontal);
	            }
	        });
			flightAlgo = new FlightAlgo(drone);
			
			Thread.sleep(5000);
			drone.getCommandManager().takeOff().doFor(5000);
			running = true;
			flightAlgo.assignment1();
			/*while(running) {
				PicAnal.analyse(isHorizontal);
				if(isHorizontal) {
					drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
					isHorizontal = false;
				} else {
					drone.getCommandManager().setVideoChannel(VideoChannel.VERT);
					isHorizontal = true;
				}
				Thread.sleep(25);
			
			}*/
			//infinite loop should go here
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// We're done using the drone, now close everything
			// If drone was actually instantiated and started, close all services
			if (drone != null)
				drone.stop();
			
			// Exit local program
			System.exit(0);
		}
	}
	
	// Run this shiiiieeeeet
	public static void main(String[] args) {
		new SpaceXDrone();
		//PicAnal.analyse();
	}
	
	public void setupGUIBtn(ARDrone drone) {
		SpaceXGUI.getInstance().getBPanel().getNavHover().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					flightAlgo.assignment1();
				} catch (Exception ex) {
					
				}
			}
		});
	}
}
