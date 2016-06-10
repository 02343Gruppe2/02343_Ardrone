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
	private int spinTime = 500;	
	private int spinSpeed = 15;
	private int spin90Time = 3000;	//The time for the drone to spin 90degress with given speed, TODO test the time.
	private int spin90Speed = 50;
	private int hoverTime = 2000;
	
	/* Different variables */
	private int batLvl = 0;
	private long threadTimer = 0;

	/**
	 * General Motor Controller Constructor
	 * is private so you can only get the same instance
	 * through getInstance()
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
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Forward for: " + time);
				
		cmd.hover().doFor(hoverTime);
		int thisTime = time;
		if(time > 8000) { thisTime = 8000; }
		cmd.forward(speed).doFor(thisTime);
		//waitFor(thisTime);
		cmd.hover().doFor(hoverTime);	
	}
	
	/**
	 * Backward
	 * Fly the drone backward for given time
	 * @param time - time to fly backward
	 * @throws InterruptedException
	 */
	public void backward(int time) {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Backward");
		cmd.backward(speed).doFor(time);
		//Thread.sleep(time);
		cmd.hover().doFor(hoverTime);
	}
	
	/**
	 * Takeoff
	 * Start the drone with a takeoff, for a fixed amount of time
	 */
	public void takeoff() {
		//TODO: test the time and flattrim stuff to get a good takeoff
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Takingoff");
		//cmd.flatTrim();
		cmd.waitFor(1000);
		cmd.takeOff();
		cmd.waitFor(5000);
		//Thread.sleep(2000);
		cmd.hover().doFor(hoverTime);
	}
	
	/**
	 * Landing
	 * Land the drone
	 */
	public void landing() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - landing");
		cmd.landing().doFor(2000);
	}
	
	
	public void waitFor(int millis) {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - wait for: " + millis);
		cmd.waitFor(millis);
	}
	
	/**
	 * Spin 90 Left
	 * Spin the drone 90 degrees to the left
	 * @throws InterruptedException
	 */
	public void spin90Left() {
		//TODO: test the speed and time90 for both spin to get a good spin
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - spinning 90 left");
		cmd.setLedsAnimation(LEDAnimation.BLINK_ORANGE, 3, (spinTime/1000));
		cmd.spinLeft(spin90Speed).doFor(spin90Time);
		cmd.hover().doFor(hoverTime);
	}
	
	/**
	 * Spin 90 Right
	 * Spin the drone 90 degrees to the right
	 * @throws InterruptedException
	 */
	public void spin90Right() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - spinning 90 right");
		cmd.setLedsAnimation(LEDAnimation.BLINK_RED, 3, (spinTime/1000));
		cmd.spinRight(spin90Speed).doFor(spin90Time);
		cmd.hover().doFor(hoverTime);
	}
	
	/**
	 * Lower Altitude
	 * lowering the altitude a little bit
	 */
	public void lowerAltitude() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - lowering altitude");
		cmd.down(speed).doFor(1000);
		cmd.hover().doFor(hoverTime);
	}
	
	/**
	 * Raise Altitude
	 * raising the altitude a little bit
	 */
	public void raiseAltitude() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - raising altitude");
		cmd.up(speed).doFor(1000);
		cmd.hover().doFor(hoverTime);
	}

	@Override
	public void onStop() {
		cmd.move(0, 0, speed, speed);
		cmd.hover().doFor(hoverTime);
	}

	/**
	 * Right
	 * Move the drone a little to the right
	 */
	public void right() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Right");
		//TODO: Test schedule ting, og se om det er noget man kan bruge, og find ud af hvor meget right den skal.
		/*
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
			cmd.goRight(5).doFor(200);
			cmd.hover().doFor(hoverTime);
			}
		});
		*/
		
		cmd.goRight(5).doFor(1000);
		cmd.hover().doFor(hoverTime);
	}
	
	/**
	 * Left
	 * Move the drone a little to the left
	 */
	public void left() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Left");
		cmd.goLeft(5).doFor(1000);
		cmd.hover().doFor(hoverTime);
	}
	
	/**
	 * SpinLeft
	 * spins the drone a little to the left
	 */
	public void spinLeft() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - spinning left");
		cmd.setLedsAnimation(LEDAnimation.BLINK_RED, 3, (spinTime/1000));
		cmd.spinLeft(spinSpeed).doFor(spinTime);
		cmd.hover().doFor(hoverTime);
	}
	
	/**
	 * SpinRight
	 * spins the drone a little to the right
	 */
	public void spinRight() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - spinning right");
		cmd.setLedsAnimation(LEDAnimation.BLINK_RED, 3, (spinTime/1000));
		cmd.spinRight(spinSpeed).doFor(spinTime);
		cmd.hover().doFor(hoverTime);
	}
	
	public int getBatLvl() {
		return batLvl;
	}

	public void setBatLvl(int batLvl) {
		this.batLvl = batLvl;
	}
	
	public long getThreadTimer() {
		return this.threadTimer;
	}
	
	public void setThreadTimer(long threadTimer) {
		this.threadTimer = threadTimer;
	}
}