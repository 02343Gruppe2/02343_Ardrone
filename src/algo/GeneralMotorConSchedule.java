package algo;

import gui.SpaceXGUI;

import java.awt.event.ActionListener;

import utils.FormattedTimeStamp;
import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.FlyingMode;


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
	private int speed = 10;		//The speed the drone will move with7
	private int sideTime = 200;
	private int spinTime = 50;	
	private int spinSpeed = 2;
	private int spin90Time = 3000;	//The time for the drone to spin 90degress with given speed, TODO test the time.
	private int spin90Speed = 50;
	private int hoverTime = 2000;
	
	private int altitudeSpeed = 30;
	private int altitudeTime = 200;
	
	private int cycleSpinSpeed = 2;
	private int cycleTime = 50;
	private int cycleSpeed = 10;
	
	private static int runningID = 0;
	private static int runningThreads = 0;
	private static final boolean printToConsole = true;
	
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
		//this.cmd.setFlyingMode(FlyingMode.HOVER_ON_TOP_OF_ORIENTED_ROUNDEL);
		//this.cmd.setHoveringRange(2000);
		//this.cmd.setMaxVz(200);
		//this.cmd.setMaxYaw(150);
	}
	
	/**
	 * Get Instance
	 * get the static and only instance of the GMC class
	 * @return The only GMC object
	 */
	public static GeneralMotorConSchedule getInstance(){
		return ourInstance;
	}
	
	private synchronized static int newRunningThread(){
		runningID++;
		runningThreads++;
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - New Running Thread ID: " + runningID + ", Running threads: " + runningThreads);
		return runningID;
	}
	
	private synchronized static boolean isRunningThread(int id) {
		//SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - is " + id + " running?: ");
		if (id == runningID){
			SpaceXGUI.getInstance().appendToConsole("\n "+ id + " yes");
			return true;
		}
		SpaceXGUI.getInstance().appendToConsole("\n" + id + " no");
		return false;
	}
	
	public GeneralMotorConSchedule hover() {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Hovering");
		cmd.hover().doFor(100);
		return this;
	}
	
	/**
	 * Forward
	 * Fly the drone forward for given time 
	 * @param time - time to fly forward
	 * @throws InterruptedException
	 */
	public GeneralMotorConSchedule forward(int time) {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Forward for: " + time);
		
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id))cmd.forward(speed).doFor(time+100);
				if(isRunningThread(id))hover();
				runningThreads--;
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
	public GeneralMotorConSchedule backward(int time) {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Backward for: " + time);
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id))cmd.backward(speed).doFor(time+100);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
	}
	
	/**
	 * Takeoff
	 * Start the drone with a takeoff, for a fixed amount of time
	 */
	public GeneralMotorConSchedule takeoff() {
		//TODO: test the time and flattrim stuff to get a good takeoff
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Takingoff");
		cmd.waitFor(1000);
		cmd.takeOff();
		cmd.waitFor(5000);
		cmd.hover().doFor(hoverTime);
		cmd.waitFor(2000);
		//cmd.flatTrim();
		return this;
	}
	
	/**
	 * Landing
	 * Land the drone
	 */
	public void landing() {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - landing");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				newRunningThread();
				hover();
				cmd.landing();
				runningThreads--;
			}
		});
	}
	
	
	public void waitFor(int millis) {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - wait for: " + millis);
		cmd.waitFor(millis);
	}
	
	/**
	 * Spin 90 Left
	 * Spin the drone 90 degrees to the left
	 * @throws InterruptedException
	 */
	public GeneralMotorConSchedule spin90Left() {
		//TODO: test the speed and time90 for both spin to get a good spin
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - spinning 90 left");
		
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.spinLeft(spin90Speed).doFor(spin90Time);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
	}
	
	/**
	 * Spin 90 Right
	 * Spin the drone 90 degrees to the right
	 * @throws InterruptedException
	 */
	public GeneralMotorConSchedule spin90Right() {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - spinning 90 right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.spinRight(spin90Speed).doFor(spin90Time);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
	}
	
	/**
	 * Lower Altitude
	 * lowering the altitude a little bit
	 */
	public GeneralMotorConSchedule lowerAltitude() {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - lowering altitude");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id))cmd.down(altitudeSpeed).doFor(altitudeTime);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
	}
	
	/**
	 * Raise Altitude
	 * raising the altitude a little bit
	 */
	public GeneralMotorConSchedule raiseAltitude() {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - raising altitude");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id))cmd.up(altitudeSpeed).doFor(altitudeTime);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id))cmd.goRight(speed).doFor(sideTime);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
	}
	
	/**
	 * Left
	 * Move the drone a little to the left
	 */
	public GeneralMotorConSchedule left() {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Left");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id))cmd.goLeft(speed).doFor(sideTime);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
	}
	
	/**
	 * Right
	 * Move the drone a little to the right
	 */
	public GeneralMotorConSchedule right(int millis) {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id))cmd.goRight(speed).doFor(millis+100);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
	}
	
	/**
	 * Left
	 * Move the drone a little to the left
	 */
	public GeneralMotorConSchedule left(int millis) {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Left");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id))cmd.goLeft(speed).doFor(millis+100);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
	}
	
	/**
	 * SpinLeft
	 * spins the drone a little to the left
	 */
	public GeneralMotorConSchedule spinLeft() {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - spinning left");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id))cmd.spinLeft(spinSpeed).doFor(spinTime);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
	}
	
	/**
	 * SpinRight
	 * spins the drone a little to the right
	 */
	public GeneralMotorConSchedule spinRight() {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - spinning right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id))cmd.spinRight(spinSpeed).doFor(spinTime);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
	}
	/**
	 * Cycle Right
	 * turns right while spinning left
	 */
	public GeneralMotorConSchedule cycleRight() {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Cycle right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.move(0, -cycleSpeed, 0, cycleSpinSpeed).doFor(cycleTime);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
	}
	
	/**
	 * Cycle Left
	 * turns left while spinning right
	 */
	public GeneralMotorConSchedule cycleLeft() {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - Cycle left");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				if(isRunningThread(id))cmd.move(0, cycleSpeed, 0, -cycleSpinSpeed).doFor(cycleTime);
				if(isRunningThread(id))hover();
				runningThreads--;
			}
		});
		return this;
	}
	
	public void pauseFor(int millis) {
		waitFor(millis);
	}
	
	public int getBatLvl() {
		return batLvl;
	}

	public void setBatLvl(int batLvl) {
		this.batLvl = batLvl;
	}
}