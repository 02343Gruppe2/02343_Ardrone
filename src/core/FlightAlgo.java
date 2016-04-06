package core;

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.navdata.GyroListener;
import de.yadrone.base.navdata.GyroPhysData;
import de.yadrone.base.navdata.GyroRawData;
import de.yadrone.base.navdata.NavDataManager;
import gui.SpaceXGUI;

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
	
	IARDrone drone = null;
	CommandManager cmd;
	
	public FlightAlgo(IARDrone drone) {
		this.drone = drone;
		cmd = drone.getCommandManager();
	}
	
	public void testHover() {
		GyroListener mGyroListener = new GyroListener() {
			
			@Override
			public void receivedRawData(GyroRawData arg0) {
				// TODO Auto-generated method stub
				SpaceXGUI.getInstance().appendToConsole("\n" + "receivedRawData, getRawGyros(): " + arg0.getRawGyros().toString());
				SpaceXGUI.getInstance().appendToConsole("\n" + "receivedRawData, getRawGyros110(): " + arg0.getRawGyros110().toString());
			}
			
			@Override
			public void receivedPhysData(GyroPhysData arg0) {
				// TODO Auto-generated method stub
				
				SpaceXGUI.getInstance().appendToConsole("\n" + "receivedPhysData, getAlim3v3(): " + arg0.getAlim3v3());
				SpaceXGUI.getInstance().appendToConsole("\n" + "receivedPhysData, getGyroTemp(): " + arg0.getGyroTemp());
				SpaceXGUI.getInstance().appendToConsole("\n" + "receivedPhysData, getPhysGyros(): " + arg0.getPhysGyros().toString());
				SpaceXGUI.getInstance().appendToConsole("\n" + "receivedPhysData, getVrefEpson(): " + arg0.getVrefEpson());
				SpaceXGUI.getInstance().appendToConsole("\n" + "receivedPhysData, getVrefIDG(): " + arg0.getVrefIDG());
			}
			
			@Override
			public void receivedOffsets(float[] arg0) {
				// TODO Auto-generated method stub
				SpaceXGUI.getInstance().appendToConsole("\n" + "receivedOffSets: ");
				for(int i = 0; i < arg0.length; i++) {
					SpaceXGUI.getInstance().appendToConsole("\n[" + i + "](" + arg0[i] + "), ");
				}
				SpaceXGUI.getInstance().appendToConsole(".\n");
			}
		};
		
		drone.getNavDataManager().addGyroListener(mGyroListener);
				
		try {
			SpaceXGUI.getInstance().appendToConsole("\n" + "Starter med at flyve frem");
			cmd.forward(10).doFor(500);
			SpaceXGUI.getInstance().appendToConsole("\n" + "F�rdig med at flyve frem" + "\n" + "Starter p� hover");
			cmd.up(5).doFor(2000);
			Thread.sleep(2000);
			SpaceXGUI.getInstance().appendToConsole("\n" + "Starter med at flyve ned");
			cmd.down(5).doFor(2000);
			Thread.sleep(2000);
			SpaceXGUI.getInstance().appendToConsole("\n" + "Lander nu");
			cmd.landing();

		} catch (Exception e) {
			System.out.println(e.getMessage());
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

