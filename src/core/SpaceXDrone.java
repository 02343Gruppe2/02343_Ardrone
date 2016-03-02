package core;

import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.LEDAnimation;
import network.DroneConnection;

public class SpaceXDrone {
	public SpaceXDrone() {
		// We instantiate a null-object with the ARDrone interface
		IARDrone d = null;
		boolean running = false;
		DroneConnection dc = DroneConnection.getInstance();
		
		try {
			// Create instance of new ARDrone
			d = new ARDrone();
			
			// Start the drone managers (NavData, CommandManager etc.)
			d.start();
			
			if(d.getNavDataManager() != null)
				running = true;
			else
				System.out.println("Couldn't instantiate drone");
			
			dc.setDrone(d);
			
			// TEST DATA
			while(running) {
				Thread.sleep(10000);
				running = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// We're done using the drone, now close everything
			// If drone was actually instantiated and started, close all services
			if (d != null)
				d.stop();
			
			// Exit local program
			System.exit(0);
		}
	}
	
	// Run this shiiiieeeeet
	public static void main(String[] args) {
		new SpaceXDrone();
	}
	
	public static void waitForEnter(String message, Object... args) {
	    Console c = System.console();
	    
	    if (c != null) {
	        // printf-like arguments
	        if (message != null)
	            c.format(message, args);
	        c.format("\nPress ENTER to proceed.\n");
	        c.readLine();
	    }
	}
}
