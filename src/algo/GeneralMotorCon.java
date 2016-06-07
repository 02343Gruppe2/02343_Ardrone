package algo;

import gui.SpaceXGUI;

import java.awt.event.ActionListener;

import utils.FormattedTimeStamp;
import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.LEDAnimation;

/**
 * General Motor Controller
 * is the controller that should be used for all flying/moving with the drone.
 * Remember to use setDrone before using any other method in GMC
 * @author Anders Bækhøj Larsen
 *
 */
public class GeneralMotorCon implements GeneralMotorListener{
	private static GeneralMotorCon ourInstance = new GeneralMotorCon();
	private ARDrone drone;		//The drone object (might be unused because of CommandManager)
	private CommandManager cmd;	//The CommandManager for the drone command
	private int speed = 10;		//The speed the drone will move with
	private int spinTime = 3000;	//The time for the drone to spin 90degress with given speed, TODO test the time.
	private int spinSpeed = 50;
	private int hoverTime = 2000;
	
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
	public static GeneralMotorCon getInstance(){
		return ourInstance;
	}
	
	/**
	 * Forward
	 * Fly the drone forward for given time 
	 * @param time - time to fly forward
	 * @throws InterruptedException
	 */
	public void forward(int time) {
		cmd.forward(speed).doFor(time);
		//Thread.sleep(time);
		cmd.hover().doFor(hoverTime);
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
		cmd.hover().doFor(hoverTime);
	}
	
	/**
	 * Takeoff
	 * Start the drone with a takeoff, for a fixed amount of time
	 * @throws InterruptedException
	 */
	public void takeoff() throws InterruptedException {
		//TODO: test the time and flattrim stuff to get a good takeoff
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] takingoff");
		//cmd.flatTrim();
		cmd.waitFor(1000);
		cmd.takeOff();
		cmd.waitFor(5000);
		//Thread.sleep(2000);
		cmd.hover().doFor(hoverTime);
	}
	
	public void landing() throws InterruptedException {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] landing");
		cmd.landing().doFor(2000);
	}
	
	
	public void waitFor(int millis) throws InterruptedException{
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] wait for: " + millis);
		cmd.waitFor(millis);
	}
	
	/**
	 * Spin 90 Left
	 * Spin the drone 90 degrees to the left
	 * @throws InterruptedException
	 */
	public void spin90Left() throws InterruptedException {
		//TODO: test the speed and time90 for both spin to get a good spin
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] spinning 90 left");
		cmd.setLedsAnimation(LEDAnimation.BLINK_ORANGE, 3, (spinTime/1000));
		cmd.spinLeft(spinSpeed).doFor(spinTime);
		cmd.hover().doFor(hoverTime);
	}
	
	/**
	 * Spin 90 Right
	 * Spin the drone 90 degrees to the right
	 * @throws InterruptedException
	 */
	public void spin90Right() throws InterruptedException {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] spinning 90 right");
		cmd.setLedsAnimation(LEDAnimation.BLINK_RED, 3, (spinTime/1000));
		cmd.spinRight(spinSpeed).doFor(spinTime);
		cmd.hover().doFor(hoverTime);
	}
	
	public void lowerAltitude() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] lowering altitude");
		cmd.down(speed).doFor(1000);
		cmd.hover().doFor(hoverTime);
	}
	
	public void raiseAltitude() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] raising altitude");
		cmd.up(speed).doFor(1000);
		cmd.hover().doFor(hoverTime);
	}

	@Override
	public void onStop() {
		cmd.move(0, 0, speed, speed);
		cmd.hover().doFor(hoverTime);
	}

	public void right() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Right");
		cmd.goRight(speed).doFor(500);
		cmd.hover().doFor(hoverTime);
	}
	public void left() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Left");
		cmd.goLeft(speed).doFor(500);
		cmd.hover().doFor(hoverTime);
	}
}