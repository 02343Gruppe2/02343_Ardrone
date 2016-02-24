package network;

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.LEDAnimation;
import de.yadrone.base.command.LEDAnimationCommand;

public class DroneConnection {
	private static DroneConnection instance = null;
	private IARDrone drone = null;
	
	private DroneConnection() {
		// y0mamma
	}
	
	/**
	 * Build an instance of the connection that will allow commands to be sent to an active drone.<br>
	 * If not instance exists a new will be created and returned to the caller (singleton-pattern).
	 * 
	 * @return Instance of current connection
	 */
	public static DroneConnection getInstance() {
		if(instance == null)
			instance = new DroneConnection();
		
		return instance;
	}
	
	/**
	 * Execute string command
	 * 
	 * @param cmd Command given as a string
	 */
	public synchronized void executeCmd(String cmd) {
		System.out.println("Command to be executed[str]: " + cmd);
		
		new LEDAnimationCommand(LEDAnimation.BLINK_GREEN, 0.50f, 1);
	}
	
	/**
	 * Execute integer command
	 * 
	 * @param cmd Command given as an integer
	 */
	public synchronized void executeCmd(int cmd) {
		System.out.println("Command to be executed[int]: " + cmd);
		System.out.println("DUMME GED");
		drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_ORANGE, 3, 10);
	}
	
	/**
	 * Set the drone instance used for the active connection instance
	 * 
	 * @param drone
	 */
	public synchronized void setDrone(IARDrone drone) {
		this.drone = drone;
	}
	
	/**
	 * Returns the current drone instance used by the active connection
	 * 
	 * @return Active connection's drone instance
	 */
	public synchronized IARDrone getDrone() {
		return this.drone;
	}
}
