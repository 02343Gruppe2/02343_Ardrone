package core;

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.navdata.GyroListener;
import de.yadrone.base.navdata.GyroPhysData;
import de.yadrone.base.navdata.GyroRawData;
import de.yadrone.base.navdata.NavDataManager;
import gui.SpaceXGUI;
import utils.FormattedTimeStamp;

/**
 * 
 * @author Anders B�kh�j Larsen
 * @author Malte Tais Magnussen
 *
 */

public class FlightAlgo {
	int [] hulaHoop = setHulaHoop(100, -200, 50, -250);
	int adjustmentTolerance = 0;
	int forwardControl = 0;
	int doTime = 1000;
	int sleepTime = 1000;
	int flightState = 0; //flightState, 0 = hovering, 1 = flying 
	
	static float physGyros[] = {0,0,0};
	
	IARDrone drone = null;
	CommandManager cmd;
	
	public FlightAlgo(IARDrone drone) {
		this.drone = drone;
		cmd = drone.getCommandManager();
		
		GyroListener mGyroListener = new GyroListener() {
			
			@Override
			public void receivedRawData(GyroRawData arg0) {}
			
			@Override
			public void receivedPhysData(GyroPhysData arg0) {
				physGyros = arg0.getPhysGyros();
			}
			
			@Override
			public void receivedOffsets(float[] arg0) {}
		};
	}
	
	/**
	 * testListener
	 * 
	 * Will printout every GyroListener Data there is into our SpaceXConsole.
	 * Data is given in this order:
	 * 
	 * - RawGyros
	 * - RawGyros110
	 * 
	 * - Alim3v3
	 * - GyroTemp
	 * - PhysGyros
	 * - VrefEpson
	 * - VrefIDG
	 * 
	 * - OffSets
	 * 
	 */
	public void testListener () {
		GyroListener mGyroListener = new GyroListener() {
			
			@Override
			public void receivedRawData(GyroRawData arg0) {
				// TODO Auto-generated method stub
				short[] mRawGyros = arg0.getRawGyros();
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] receivedRawData, getRawGyros(): ");
				for(int i = 0; i < mRawGyros.length; i++) {
					SpaceXGUI.getInstance().appendToConsole("[" + i + "](" +mRawGyros[i] + "), ");	
				}
				
				short[] mRawGyros110 = arg0.getRawGyros110();
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] receivedRawData, getRawGyros110(): ");
				for(int i = 0; i < mRawGyros110.length; i++) {
					SpaceXGUI.getInstance().appendToConsole("[" + i + "](" +mRawGyros110[i] + "), ");	
				}
			}
			
			@Override
			public void receivedPhysData(GyroPhysData arg0) {
				// TODO Auto-generated method stub
				
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] receivedPhysData, getAlim3v3(): " + arg0.getAlim3v3());
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] receivedPhysData, getGyroTemp(): " + arg0.getGyroTemp());
				
				float[] mPhysGyros = arg0.getPhysGyros();
				physGyros = mPhysGyros;
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] receivedPhysData, getPhysGyros(): ");
				for(int i = 0; i < mPhysGyros.length; i++) {
					SpaceXGUI.getInstance().appendToConsole("[" + i + "](" +mPhysGyros[i] + "), ");	
				}
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] receivedPhysData, getVrefEpson(): " + arg0.getVrefEpson());
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] receivedPhysData, getVrefIDG(): " + arg0.getVrefIDG());
			}
			
			@Override
			public void receivedOffsets(float[] arg0) {
				// TODO Auto-generated method stub
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] receivedOffSets: ");
				for(int i = 0; i < arg0.length; i++) {
					SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] " + i + "](" + arg0[i] + "), ");
				}
				SpaceXGUI.getInstance().appendToConsole(".\n");
			}
		};
		
		drone.getNavDataManager().addGyroListener(mGyroListener);
	}
	
	public void testHover() {		
		try {
			testListener();
			//SpaceXGUI.getInstance().appendToConsole("\n" + "Starter med at flyve frem");
			cmd.forward(10).doFor(500);
			//SpaceXGUI.getInstance().appendToConsole("\n" + "F�rdig med at flyve frem" + "\n" + "Starter p� hover");
			cmd.up(5).doFor(2000);
			Thread.sleep(2000);
		//	SpaceXGUI.getInstance().appendToConsole("\n" + "Starter med at flyve ned");
			cmd.down(5).doFor(2000);
			Thread.sleep(2000);
		//	SpaceXGUI.getInstance().appendToConsole("\n" + "Lander nu");
			//cmd.landing();
			cmd.hover().doFor(1000);
			SpaceXGUI.getInstance().appendToConsole("\n" + "Hover f�rdig");
			

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	/**
	 * TheAmazingHoverMode
	 * @author Anders
	 * 
	 * It's the ultimate hovering mode you will ever experience! it is unnecessarily precise and still!
	 * You can even place your beer on it without spilling it! (SpaceXDrone A/S does not insure any beer placed
	 * ontop our Drones or any other brewage)
	 */
	public void theAmazingHoverMode(long millis) {
		
		
		float pitch = 0; 
		float roll = 0; 
		float yaw = 0;
		boolean isHardCheck = false;
		double hardCheck = 2.0;
		double softCheck = 0.4;
		
		if(physGyros[0] > hardCheck) {
			//If the Drone is tilting alot to the Right
			isHardCheck = true;
			roll = -5;
		} else if (physGyros[0] < (-1*hardCheck) ) {
			//If the Drone is tilting alot to the Left
			isHardCheck = true;
			roll = 5;
		}
		
		if(physGyros[1] > hardCheck) {
			//If the Drone is tilting alot backward
			isHardCheck = true;
			pitch = -5;
		} else if (physGyros[1] < (-1*hardCheck) ) {
			//If the Drone is tilting alot forward
			isHardCheck = true;
			pitch = 5;
		}
		
		if(!isHardCheck) {
			if(physGyros[0] > softCheck) {
				//If the Drone is tilting abit to the Right
				roll = -2;
			} else if (physGyros[0] < (-1*softCheck) ) {
				//If the Drone is tilting abit to the Left
				roll = 2;
			}
			
			if(physGyros[1] > softCheck) {
				//If the Drone is tilting abit backward
				pitch = -2;
			} else if (physGyros[1] < (-1*softCheck) ) {
				//If the Drone is tilting abit forward
				pitch = 2;
			}
		}
		
		try {
			SpaceXGUI.getInstance().appendToConsole("\n" + "AmazingHovering has initiated");
			cmd.manualTrim(pitch, roll, yaw).doFor(millis);
			Thread.sleep(millis);
			SpaceXGUI.getInstance().appendToConsole("\n" + "AmazingHovering has finished, after " + millis + "milliSeconds");
		} catch (Exception e) {
			SpaceXGUI.getInstance().appendToConsole("\n" + "Error in TheAmazingHoveringMode");
		}
	}
	
	public void assignment1 () throws InterruptedException {
		forwardControl = -2;
		
		if((hulaHoop[0] + hulaHoop[1]) > (0 + adjustmentTolerance)) {
			//flyv ned
			SpaceXGUI.getInstance().appendToConsole("\n" + "Gonna get low low low low low!");
			System.out.println(hulaHoop[0]);
			cmd.down(30).doFor(doTime);
			Thread.sleep(sleepTime);
			cmd.hover();
		}
		else if((hulaHoop[0] + hulaHoop[1]) < (0 - adjustmentTolerance)) {
			//flyv op
			SpaceXGUI.getInstance().appendToConsole("\n" + "Because i got high!");
			cmd.up(30).doFor(doTime);
			Thread.sleep(sleepTime);
			cmd.hover();
		}
		else {
			forwardControl++;
		}
		
		if((hulaHoop[2] + hulaHoop[3]) > (0 + adjustmentTolerance)) {
			//flyv h�jre
			SpaceXGUI.getInstance().appendToConsole("\n" + "You spin med right round");
			cmd.goRight(30).doFor(doTime);
			Thread.sleep(sleepTime);
			cmd.hover();
		}
		else if((hulaHoop[2] + hulaHoop[3]) < (0 - adjustmentTolerance)) {
			// flyv venstre
			SpaceXGUI.getInstance().appendToConsole("\n" + "everything you own in the box to the left");
			cmd.goLeft(30).doFor(doTime);
			Thread.sleep(sleepTime);
			cmd.hover();
		}
		else {
			forwardControl++;
		}
		
		if (forwardControl == 0) {
			//flyv frem;
			SpaceXGUI.getInstance().appendToConsole("\n" + "Run niggauh Run niggauh!");
			cmd.forward(30).doFor(doTime);
			Thread.sleep(sleepTime);
			cmd.hover();
		}
		Thread.sleep(2000);
		cmd.landing();
		
	}
	
	public void assignment2() {
		
	}
	
	public void assignment3() {
		
	}

	int [] setHulaHoop (int high, int low, int right, int left) {
		int [] tmp = new int[4];
		
		tmp[0] = high;
		tmp[1] = low;
		tmp[2] = right;
		tmp[3] = left;
		
		return tmp;
	}
	
}

