package core;

import java.io.Console;

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
			int i = 0;
			
			while(running) {
				
				
				// Keep doing algorithm... (testing for now)
				if(i < 10) {
					if(i % 2 == 0)
						dc.executeCmd(i);
					else
						dc.executeCmd(String.valueOf(i));
				}
				else
					running = false;
				
				i++;
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
