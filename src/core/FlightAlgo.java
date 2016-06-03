package core;

import java.util.Date;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.navdata.AttitudeListener;
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

@Deprecated
public class FlightAlgo {
	int [] hulaHoop = setHulaHoop(100, -200, 50, -250);
	int adjustmentTolerance = 0;
	int forwardControl = 0;
	int doTime = 1000;
	int sleepTime = 1000;
	int flightState = 0; //flightState, 0 = hovering, 1 = flying 
	GyroListener mGyroListener;
	AttitudeListener mAttitudeListener;
	
	float physGyros[] = {0,0,0};
	
	IARDrone drone = null;
	CommandManager cmd;
	
	public FlightAlgo(IARDrone drone) {
		this.drone = drone;
		
		cmd = this.drone.getCommandManager();
		mAttitudeListener = new AttitudeListener() {
			
			@Override
			public void windCompensation(float arg0, float arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void attitudeUpdated(float arg0, float arg1, float arg2) {
				// TODO Auto-generated method stub
				SpaceXGUI.getInstance().appendToConsole("\n0: " + arg0 + " 1: " + arg1 + " 2: " +arg2);
			}
			
			@Override
			public void attitudeUpdated(float arg0, float arg1) {
				// TODO Auto-generated method stub
				
			}
		};
		mGyroListener = new GyroListener() {
			
			@Override
			public void receivedRawData(GyroRawData arg0) {}
			
			@Override
			public void receivedPhysData(GyroPhysData arg0) {
				//physGyros = arg0.getPhysGyros();
				GyroData.ourIstance.physGyros = arg0.getPhysGyros();
				//SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] ReceivedPhysData");	
				//SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] PhysThings: " + GyroData.ourIstance.physGyros[0] + ". " + GyroData.ourIstance.physGyros[1] + ". " + GyroData.ourIstance.physGyros[2]);
				SpaceXGUI.getInstance().appendToConsole(".");
			}
			
			@Override
			public void receivedOffsets(float[] arg0) {}
		};
		this.drone.getNavDataManager().addGyroListener(mGyroListener);
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
	 * ontop of our Drones or any other brewage)
	 */
	public void theAmazingHoverMode(long millis) {
		
		float pitch = 0; 
		float roll = 0; 
		float yaw = 0;
		boolean isHardCheck = false;
		double hardCheck = 5.0;
		double softCheck = 0.1;
		float hardTurn = 1f;
		float softTurn = 0.8f;
		int timer = 250;
		double timeLeft = millis / timer;
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] AmazingHovering has initiated");
		this.drone.getNavDataManager().addGyroListener(mGyroListener);
		//drone.getCommandManager().move((int)30,(int)0,(int)0,(int)0).doFor(timer);
		
		for (int i = 0; i < timeLeft; i++) {
			pitch = 0;
			roll = 0;
			yaw = 0;
			isHardCheck = false;
			float rollGyro = GyroData.ourIstance.physGyros[0];
			float pitchGyro = GyroData.ourIstance.physGyros[1];
			
			
			
			if(rollGyro > hardCheck) {
				//If the Drone is tilting alot to the Right
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] AmazingHovering: Drone was tilting hard Right");
				isHardCheck = true;
				//roll = -hardTurn;
				roll = hardTurn*rollGyro;
			} else if (rollGyro < (-1*hardCheck) ) {
				//If the Drone is tilting alot to the Left
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] AmazingHovering: Drone was tilting hard Left");
				isHardCheck = true;
				//roll = hardTurn;
				roll = hardTurn*rollGyro;
			}
			
			if(pitchGyro > hardCheck) {
				//If the Drone is tilting alot backward
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] AmazingHovering: Drone was tilting hard Backward");
				isHardCheck = true;
				//pitch = -hardTurn;
				pitch = hardTurn*pitchGyro;
			} else if (pitchGyro < (-1*hardCheck) ) {
				//If the Drone is tilting alot forward
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] AmazingHovering: Drone was tilting hard Forward");
				isHardCheck = true;
				//pitch = hardTurn;
				pitch = hardTurn*pitchGyro;
			}
			
			if(!isHardCheck) {
				if(rollGyro > softCheck) {
					//If the Drone is tilting abit to the Right
					SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] AmazingHovering: Drone was tilting abit Right");
					//roll = -softTurn;
					roll = softTurn*rollGyro;
				} else if (rollGyro < (-1*softCheck) ) {
					//If the Drone is tilting abit to the Left
					SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] AmazingHovering: Drone was tilting abit Left");
					//roll = softTurn;
					roll = softTurn*rollGyro;
				}
				
				if(pitchGyro > softCheck) {
					//If the Drone is tilting abit backward
					SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] AmazingHovering: Drone was tilting abit Backward");
					//pitch = -softTurn;
					pitch = softTurn*pitchGyro;
				} else if (pitchGyro < (-1*softCheck) ) {
					//If the Drone is tilting abit forward
					SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] AmazingHovering: Drone was tilting abit Forward");
					//pitch = softTurn;
					pitch = softTurn*pitchGyro;
				}
			}
			
			try {
				//cmd.manualTrim(pitch, roll, yaw);  //.doFor(timer);
				cmd.manualTrim(0, 0, 0);
			//	drone.getCommandManager().move(0, 0, 0, 0).doFor(timer/2);
				//drone.getCommandManager().move(roll, pitch, 0, yaw).doFor(timer); // speedX , speedY , speedZ, speedSpin
				drone.getCommandManager().move((int)-roll, pitch, 0, yaw).doFor(timer);
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] Pitch: " + pitch + ". Roll: " + roll + ". Yaw: " + yaw + ".");
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] physGyros[0]: " + rollGyro + ". physGyros[1]: " + pitchGyro);
				//Thread.sleep(timer);
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] AmazingHovering has Runned, " + i + "/" + timeLeft);
			} catch (Exception e) {
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "] Error in TheAmazingHoveringMode");
			}	
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
	
	public float perc2float(int speed) {
		return (float) (speed / 100.0f);
	}
	
}

