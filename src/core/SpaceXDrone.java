package core;

import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

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


public class SpaceXDrone {
	FlightAlgo flightAlgo;
	Boolean isFront;
	Boolean imageIsReady;
	public void swapCamera(IARDrone drone) {
		imageIsReady = false;
		if(isFront) {
			isFront = false;
			drone.getCommandManager().setVideoChannel(VideoChannel.LARGE_HORI_SMALL_VERT);
		} else {
			isFront = true;
			drone.getCommandManager().setVideoChannel(VideoChannel.LARGE_VERT_SMALL_HORI);
		}
	}
	
	public SpaceXDrone() {
		// We instantiate a null-object with the ARDrone interface
		IARDrone drone = null;
		boolean running = false;
		isFront = true;
		imageIsReady = true;
		
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
			
			drone.getCommandManager().setVideoChannel(VideoChannel.LARGE_VERT_SMALL_HORI);
			drone.getVideoManager().addImageListener(new ImageListener() {
				@Override
	            public void imageUpdated(BufferedImage newImage) {
					if(imageIsReady)
	            	SpaceXGUI.updateImage(newImage, isFront);
	            }
	        });
			running = true;
			/*flightAlgo = new FlightAlgo(drone);
			Thread.sleep(10000);
			drone.getCommandManager().takeOff().doFor(2000);
			Thread.sleep(2000);
		
			flightAlgo.theAmazingHoverMode(5000);
			drone.getCommandManager().landing().doFor(2000);*/
			int counter = 0;
			
			while(running) {
				
				counter++;
				swapCamera(drone);
				Thread.sleep(200);
				imageIsReady = true;
				Thread.sleep(200);
				if(counter > 1000000)
					running = false;
			}
			
			//infinite loop should go here
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// We're done using the drone, now close everything
			// If drone was actually instantiated and started, close all services
			if (drone != null)
				drone.getCommandManager().landing().doFor(2000);
			
			// Exit local program
			System.exit(0);
		}
	}
	
	// Run this shiiiieeeeet
	public static void main(String[] args) {
		new SpaceXDrone();
	}
}


