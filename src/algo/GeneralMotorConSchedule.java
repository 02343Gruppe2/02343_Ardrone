package algo;

import gui.SpaceXGUI;

import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;


//TODO: Test Schedulling 
/**
 * General Motor Controller
 * is the controller that should be used for all flying/moving with the drone.
 * Remember to use setDrone before using any other method in GMC
 * @author Anders Bækhøj Larsen
 *
 */
public class GeneralMotorConSchedule {
	private static GeneralMotorConSchedule ourInstance = new GeneralMotorConSchedule();
	private ARDrone drone;					//The drone object (might be unused because of CommandManager)
	private CommandManager cmd;				//The CommandManager for the drone command
	private static final String TAG= "GMCS";
	
	/* Standard move Variables */
	private int hoverTime = 2000;			// The time to hover (not in use)
	private int sideTime = 100;				// The time the drone fly directly left or right.
	private int speed = 10;					// The speed the drone will move with (Forward and backward)
	
	/* Static Spin Variables */
	private int spinTime = 150;		
	private int spinSpeed = 15;
	
	/* Spin 90 degrees Variables */
	private int spin90Time = 4500;			//TODO: test the time.
	private int spin90Speed = 50;
	
	/* Altitude Variables */
	private int altitudeSpeed = 15;
	private int altitudeTime = 100;
	
	/* Cycle Variables */
	private int cycleSpinSpeed = 15;
	private int cycleTime = 150;
	private int cycleSpeed = 10;
	
	/* Last Movement records */
	private int[] recordedMovement = new int[]{0, 0, 0, 0, 0};
	
	/* Schedule Thread ID */
	private static int runningID = 0;		//The ID of the thread which started last
	private static int runningThreads = 0;	// Number of threads running 
	
	/* Debugging */
	private static final boolean printToConsole = true;
	
	/* Last movement Constants */
	public static final int MOVED_FORWARD = 1;
	public static final int MOVED_BACKWARD = 2;
	public static final int MOVED_RIGHT = 3;
	public static final int MOVED_LEFT = 4;
	public static final int MOVED_RAISEALT = 5;
	public static final int MOVED_LOWERALT = 6;
	public static final int MOVED_SPINRIGHT = 7;
	public static final int MOVED_SPINLEFT = 8;
	public static final int MOVED_CYCLERIGHT = 9;
	public static final int MOVED_CYCLELEFT = 10;
	
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - New Running Thread ID: " + runningID + ", Running threads: " + runningThreads);
		return runningID;
	}
	
	/**
	 * is Running Thread?
	 * checking if the thread has the right to run now
	 * @param id - the tested id
	 * @return - true if the given id is the current running one
	 */
	private synchronized static boolean isRunningThread(int id) {
		//SpaceXGUI.getInstance().appendToConsole(TAG," - is " + id + " running?: ");
		if (id == runningID){
			SpaceXGUI.getInstance().appendToConsole(TAG,id + " yes");
			return true;
		}
		SpaceXGUI.getInstance().appendToConsole(TAG,id + " no");
		return false;
	}
	
	public GeneralMotorConSchedule hover() {
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - Hovering");
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - Forward for: " + time);
		
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int timeDivider = 200;
				int id = newRunningThread();
				int runNum = (int)time/timeDivider;
				addLastMovement(MOVED_FORWARD);
				while (runNum > 0) {
					if(isRunningThread(id)) {
						try {
							cmd.forward(speed);
							Thread.sleep(timeDivider);
						} catch (InterruptedException e) {
							
						}
					}
					runNum--;
				}
				if(isRunningThread(id)){
					try {
						cmd.forward(speed);
						Thread.sleep(time%timeDivider);
					} catch(InterruptedException e) {
						
					}
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - Backward for: " + time);
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id)){
					try {
						cmd.backward(speed);
						addLastMovement(MOVED_BACKWARD);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - Takingoff");
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - landing");
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - wait for: " + millis);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - spinning 90 left");
		
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				addLastMovement(MOVED_SPINLEFT);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - spinning 90 right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				addLastMovement(MOVED_SPINRIGHT);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - lowering altitude");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id)) {
					try {
					cmd.down(altitudeSpeed);
					addLastMovement(MOVED_LOWERALT);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - raising altitude");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id)) {
					try {
					cmd.up(altitudeSpeed);
					addLastMovement(MOVED_RAISEALT);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - Right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id)) {
					try {
					cmd.goRight(speed);
					addLastMovement(MOVED_RIGHT);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - Left");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id)) {
					try {
					cmd.goLeft(speed);
					addLastMovement(MOVED_LEFT);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - Right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id)) {
					try {
					cmd.goRight(speed);
					addLastMovement(MOVED_RIGHT);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - Left");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id)) {
					try {
					cmd.goLeft(speed);
					addLastMovement(MOVED_LEFT);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - spinning left");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id)){
					try {
					cmd.spinLeft(spinSpeed);
					addLastMovement(MOVED_SPINLEFT);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - spinning right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				if(isRunningThread(id)) {
					try {
					cmd.spinRight(spinSpeed);
					addLastMovement(MOVED_SPINRIGHT);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - Cycle right");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				addLastMovement(MOVED_CYCLERIGHT);
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
		if(printToConsole)SpaceXGUI.getInstance().appendToConsole(TAG," - Cycle left");
		cmd.schedule(0, new Runnable(){
			@Override
			public void run() {
				int id = newRunningThread();
				hover();
				addLastMovement(MOVED_CYCLELEFT);
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
	
	/**
	 * Add Last Movement
	 * @param movement - the movement 
	 *  0 - None
	 *  1 - Forward
	 *  2 - Backward
	 *  3 - Right
	 *  4 - Left
	 *  5 - RaiseAlt
	 *  6 - LowerAlt
	 *  7 - SpinRight
	 *  8 - SpinLeft
	 *  9 - CycleRight
	 *  10 - CycleLeft
	 */
	private void addLastMovement(int movement){
		recordedMovement[4] = recordedMovement[3];
		recordedMovement[3] = recordedMovement[2];
		recordedMovement[2] = recordedMovement[1];
		recordedMovement[1] = recordedMovement[0];
		recordedMovement[0] = movement;
	}
	
	/**
	 * Get Last Movement
	 * @param index - which movement to get
	 * @return - the indexed movement
	 */
	public int getLastMovement(int index) {
		return recordedMovement[index];
	}
}