package algo;

import gui.SpaceXGUI;

import java.awt.event.ActionListener;

import utils.FormattedTimeStamp;
import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.LEDAnimation;


//TODO: Test Schedulling 
/**
 * General Motor Controller
 * is the controller that should be used for all flying/moving with the drone.
 * Remember to use setDrone before using any other method in GMC
 * @author Anders Bækhøj Larsen
 *
 */
public class GeneralMotorConSchedule implements GeneralMotorListener{
	private static GeneralMotorConSchedule ourInstance = new GeneralMotorConSchedule();
	private ARDrone drone;		//The drone object (might be unused because of CommandManager)
	private CommandManager cmd;	//The CommandManager for the drone command
	private int speed = 10;		//The speed the drone will move with
	private int spinTime = 500;	
	private int spinSpeed = 40;
	private int spin90Time = 3000;	//The time for the drone to spin 90degress with given speed, TODO test the time.
	private int spin90Speed = 50;
	private int hoverTime = 2000;
	
	private static int runningID = 0;
	
	/* Different variables */
	private int batLvl = 0;
	private long threadTimer = 0;

	/**
	 * General Motor Controller Constructor
	 * is private so you can only get the same instance
	 * through getInstance()
	 */
	private GeneralMotorConSchedule() {
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
	public static GeneralMotorConSchedule getInstance(){
		return ourInstance;
	}
	
	private int newRunningThread(){
		runningID++;
		//SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - New Running Thread ID: " + (runningID));
		return runningID;
	}
	
	private boolean isRunningThread(int id) {
		//SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - is " + id + " running?: ");
		if (id == runningID){
			//SpaceXGUI.getInstance().appendToConsole("yes");
			return true;
		}
		//SpaceXGUI.getInstance().appendToConsole("no");
		return false;
	}
	
	public void hover() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Hovering");
		cmd.hover().doFor(500);
	}
	
	/**
	 * Forward
	 * Fly the drone forward for given time 
	 * @param time - time to fly forward
	 * @throws InterruptedException
	 */
	public GeneralMotorConSchedule forward(int time) {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Forward for: " + time);
		
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.forward(speed).doFor(time);
				if(isRunningThread(id))hover();
			}
		});
		return this;
	}
	
	/**
	 * Backward
	 * Fly the drone backward for given time
	 * @param time - time to fly backward
	 * @throws InterruptedException
	 */
	public void backward(int time) {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Backward for: " + time);
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.backward(speed).doFor(time);
				if(isRunningThread(id))hover();
			}
		});
	}
	
	/**
	 * Takeoff
	 * Start the drone with a takeoff, for a fixed amount of time
	 */
	public void takeoff() {
		//TODO: test the time and flattrim stuff to get a good takeoff
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Takingoff");
		cmd.waitFor(1000);
		cmd.takeOff();
		cmd.waitFor(5000);
		cmd.hover().doFor(hoverTime);
		cmd.waitFor(2000);
		//cmd.flatTrim();
	}
	
	/**
	 * Landing
	 * Land the drone
	 */
	public void landing() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - landing");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				newRunningThread();
				hover();
				cmd.landing();
			}
		});
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
		
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.spinLeft(spin90Speed).doFor(spin90Time);
				if(isRunningThread(id))hover();
			}
		});
	}
	
	/**
	 * Spin 90 Right
	 * Spin the drone 90 degrees to the right
	 * @throws InterruptedException
	 */
	public void spin90Right() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - spinning 90 right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.spinRight(spin90Speed).doFor(spin90Time);
				if(isRunningThread(id))hover();
			}
		});
	}
	
	/**
	 * Lower Altitude
	 * lowering the altitude a little bit
	 */
	public void lowerAltitude() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - lowering altitude");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.down(speed).doFor(1000);
				if(isRunningThread(id))hover();
			}
		});
	}
	
	/**
	 * Raise Altitude
	 * raising the altitude a little bit
	 */
	public void raiseAltitude() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - raising altitude");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.up(speed).doFor(1000);
				if(isRunningThread(id))hover();
			}
		});
	}

	@Override
	public void onStop() {
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
			cmd.stop();
			cmd.hover().doFor(2000);
			}
		});
	}

	/**
	 * Right
	 * Move the drone a little to the right
	 */
	public GeneralMotorConSchedule right() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.goRight(speed).doFor(1000);
				if(isRunningThread(id))hover();
			}
		});
		return this;
	}
	
	/**
	 * Left
	 * Move the drone a little to the left
	 */
	public GeneralMotorConSchedule left() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Left");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.goLeft(speed).doFor(1000);
				if(isRunningThread(id))hover();
			}
		});
		return this;
	}
	
	/**
	 * Right
	 * Move the drone a little to the right
	 */
	public GeneralMotorConSchedule right(int millis) {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.goRight(speed).doFor(millis);
				if(isRunningThread(id))hover();
			}
		});
		return this;
	}
	
	/**
	 * Left
	 * Move the drone a little to the left
	 */
	public GeneralMotorConSchedule left(int millis) {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Left");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.goLeft(speed).doFor(millis);
				if(isRunningThread(id))hover();
			}
		});
		return this;
	}
	
	/**
	 * SpinLeft
	 * spins the drone a little to the left
	 */
	public void spinLeft() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - spinning left");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.spinLeft(spinSpeed).doFor(5000);
				if(isRunningThread(id))hover();
			}
		});
	}
	
	/**
	 * SpinRight
	 * spins the drone a little to the right
	 */
	public void spinRight() {
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - spinning right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.spinRight(spinSpeed).doFor(5000);
				if(isRunningThread(id))hover();
			}
		});
	}
	
	public void doFor(int millis) {
		waitFor(millis);
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