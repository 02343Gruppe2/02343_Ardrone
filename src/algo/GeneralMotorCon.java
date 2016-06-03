package algo;

import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;

/**
 * @author Anders Bækhøj Larsen
 *
 */
public class GeneralMotorCon {
	private static GeneralMotorCon ourInstance = new GeneralMotorCon();
	private ARDrone drone;		//The drone object (might be unused because of CommandManager)
	private CommandManager cmd;	//The CommandManager for the drone command
	private int speed = 10;		//The speed the drone will move with
	private int time90 = 1500;	//The time for the drone to spin 90degress with given speed, TODO test the time.
	
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
	
	public GeneralMotorCon getInstance(){
		return ourInstance;
	}
	
	public void forward(int time) throws InterruptedException{
		cmd.forward(speed).doFor(time);
		//Thread.sleep(time);
		cmd.hover();
	}
	
	public void backward(int time) throws InterruptedException {
		cmd.backward(speed).doFor(time);
		//Thread.sleep(time);
		cmd.hover();
		
		drone.spinLeft();
		drone.spinRight();
	}
	
	public void takeoff() throws InterruptedException {
		cmd.flatTrim();
		Thread.sleep(500);
		cmd.takeOff().doFor(2000);
		//Thread.sleep(2000);
		cmd.hover();
	}
	
	public void spin90Left() throws InterruptedException {
		cmd.spinLeft(speed).doFor(time90);
		cmd.hover();
	}
	
	public void spin90Right() throws InterruptedException {
		cmd.spinRight(speed).doFor(time90);
		cmd.hover();
	}
	
}


