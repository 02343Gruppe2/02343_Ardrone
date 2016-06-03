package algo;

import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;

/**
 * General Motor Controller
 * is the controller that should be used for all flying/moving with the drone.
 * Remember to use setDrone before using any other method in GMC
 * @author Anders Bækhøj Larsen
 *
 */
public class GeneralMotorCon {
	private static GeneralMotorCon ourInstance = new GeneralMotorCon();
	private ARDrone drone;		//The drone object (might be unused because of CommandManager)
	private CommandManager cmd;	//The CommandManager for the drone command
	private int speed = 10;		//The speed the drone will move with
	private int time90 = 1500;	//The time for the drone to spin 90degress with given speed, TODO test the time.
	
	/**
	 * General Motor Controller Constructor
	 * is private so you can only get the same instance
	 * through getInstace()
	 */
	private GeneralMotorCon() {
		drone = null;
		cmd = null;
	}
	
	/**
	 * Should be called before once before any other method is called
	 * @param drone
	 */
	public void setDrone(ARDrone drone) {
		this.drone = drone;
		this.cmd = this.drone.getCommandManager();
	}
	
	/**
	 * Get Instance
	 * get the static and only instance of the GMC class
	 * @return The only GMC object
	 */
	public GeneralMotorCon getInstance(){
		return ourInstance;
	}
	
	/**
	 * Forward
	 * Fly the drone forward for given time 
	 * @param time - time to fly forward
	 * @throws InterruptedException
	 */
	public void forward(int time) throws InterruptedException{
		cmd.forward(speed).doFor(time);
		//Thread.sleep(time);
		cmd.hover();
	}
	
	/**
	 * Backward
	 * Fly the drone backward for given time
	 * @param time - time to fly backward
	 * @throws InterruptedException
	 */
	public void backward(int time) throws InterruptedException {
		cmd.backward(speed).doFor(time);
		//Thread.sleep(time);
		cmd.hover();
	}
	
	/**
	 * Takeoff
	 * Start the drone with a takeoff, for a fixed amount of time
	 * @throws InterruptedException
	 */
	public void takeoff() throws InterruptedException {
		//TODO: test the time and flattrim stuff to get a good takeoff
		cmd.flatTrim();
		Thread.sleep(500);
		cmd.takeOff().doFor(2000);
		//Thread.sleep(2000);
		cmd.hover();
	}
	
	/**
	 * Spin 90 Left
	 * Spin the drone 90 degrees to the left
	 * @throws InterruptedException
	 */
	public void spin90Left() throws InterruptedException {
		//TODO: test the speed and time90 for both spin to get a good spin 
		cmd.spinLeft(speed).doFor(time90);
		cmd.hover();
	}
	
	/**
	 * Spin 90 Right
	 * Spin the drone 90 degrees to the right
	 * @throws InterruptedException
	 */
	public void spin90Right() throws InterruptedException {
		cmd.spinRight(speed).doFor(time90);
		cmd.hover();
	}
}