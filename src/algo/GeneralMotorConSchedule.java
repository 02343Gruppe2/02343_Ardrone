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
	private ARDrone drone;					//The drone object (might be unused because of CommandManager)
	private CommandManager cmd;				//The CommandManager for the drone command
	
	/* Standard move Variables */
	private int hoverTime = 2000;			// The time to hover (not in use)
	private int sideTime = 100;				// The time the drone fly directly left or right.
	private int speed = 10;					// The speed the drone will move with (Forward and backward)
	
	/* Static Spin Variables */
	private int spinTime = 150;		
	private int spinSpeed = 15;
	
	/* Spin 90 degrees Variables */
	private int spin90Time = 3000;			//TODO: test the time.
	private int spin90Speed = 50;
	
	/* Altitude Variables */
	private int altitudeSpeed = 15;
	private int altitudeTime = 100;
	
	/* Cycle Variables */
	private int cycleSpinSpeed = 15;
	private int cycleTime = 150;
	private int cycleSpeed = 10;
	
	/* Schedule Thread ID */
	private static int runningID = 0;		//The ID of the thread which started last
	private static int runningThreads = 0;	// Number of threads running 
	
	/* Debugging */
	private static final boolean printToConsole = true;
	
	/* Different variables */
	private int batLvl = 0;

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
	
	/**
	 * New Running Thread
	 * Method for the schedule threads updates the running ID and gives
	 * the new ID as return
	 * @return - The new Running ID
	 */
	private synchronized static int newRunningThread(){
		runningID++;
		runningThreads++;
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - New Running Thread ID: " + runningID + ", Running threads: " + runningThreads);
		return runningID;
	}
	
	/**
	 * is Running Thread?
	 * checking if the thread has the right to run now
	 * @param id - the tested id
	 * @return - true if the given id is the current running one
	 */
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
				int runNum = (int)time/500;
				while (runNum > 0) {
					if(isRunningThread(id)) {
						try {
							cmd.forward(speed);
							Thread.sleep(500);
						} catch (InterruptedException e) {
							
						}
					}
					runNum--;
				}
				
				if(isRunningThread(id))cmd.hover();
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
				if(isRunningThread(id)){
					try {
					cmd.backward(speed); 
						Thread.sleep(time);
					} catch (InterruptedException e) {
						
					}
				}
				if(isRunningThread(id))cmd.hover();
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
				cmd.hover();
				cmd.landing();
				runningThreads--;
			}
		});
	}
	
	
	public void waitFor(int millis) {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] [GMC] - wait for: " + millis);
		//cmd.waitFor(millis);
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			
		}
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
				if(isRunningThread(id)) {
					try {
					cmd.down(altitudeSpeed);
					Thread.sleep(altitudeTime);
					} catch (InterruptedException e) {
						
					}
				}
				if(isRunningThread(id))cmd.hover();
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
				if(isRunningThread(id)) {
					try {
					cmd.up(altitudeSpeed);
					Thread.sleep(altitudeTime);
					} catch (InterruptedException e) {
						
					}
				}
				if(isRunningThread(id))cmd.hover();
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
				if(isRunningThread(id)) {
					try {
					cmd.goRight(speed);
					Thread.sleep(sideTime);
					} catch (InterruptedException e) {
						
					}
				}
				if(isRunningThread(id))cmd.hover();
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
				if(isRunningThread(id)) {
					try {
					cmd.goLeft(speed);
					Thread.sleep(sideTime);
					} catch (InterruptedException e) {
						
					}
				}
				if(isRunningThread(id))cmd.hover();
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
				if(isRunningThread(id)) {
					try {
					cmd.goRight(speed);
					Thread.sleep(millis);
					} catch (InterruptedException e) {
						
					}
				}
				if(isRunningThread(id))cmd.hover();
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
				if(isRunningThread(id)) {
					try {
					cmd.goLeft(speed);
					Thread.sleep(millis);
					} catch (InterruptedException e) {
						
					}
				}
				if(isRunningThread(id))cmd.hover();
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
				if(isRunningThread(id)){
					try {
					cmd.spinLeft(spinSpeed);
					Thread.sleep(spinTime);
					} catch (InterruptedException e) {
						
					}
				}
				//if(isRunningThread(id))cmd.move(0, 0, 0, spinSpeed).doFor(spinTime);
				if(isRunningThread(id))cmd.hover();
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
				if(isRunningThread(id)) {
					try {
					cmd.spinRight(spinSpeed);
					Thread.sleep(spinTime);
					} catch (InterruptedException e) {
						
					}
				}
				if(isRunningThread(id))cmd.hover();
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
	
	/**
	 * Pause for
	 * @param millis - The time you want to pause the calling thread
	 */
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