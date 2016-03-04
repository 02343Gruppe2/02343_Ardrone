package core;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import network.DroneConnection;

/**
 * 
 * @author Kristin Hansen
 *
 */
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
}
