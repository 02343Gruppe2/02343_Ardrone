package core;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;

/**
 * 
 * @author Anders B�kh�j Larsen
 * @author Malte Tais Magnussen
 *
 */

public class FlightAlgo {
	int [] hulaHoop = new int[4];
	int adjustmentTolerance = 0;
	int forwardControl = 0;
	int doTime = 1000;
	int sleepTime = 1000;
	
	IARDrone drone = null;
	CommandManager cmd;
	
	public FlightAlgo(IARDrone drone) {
		this.drone = drone;
		cmd = drone.getCommandManager();
	}
	
	public void assignment1 () throws InterruptedException {
		forwardControl = -2;
		
		//hulaHoop(high, low, right, left)
		hulaHoop(100, -200, 50, -250);
		
		if((hulaHoop[0] + hulaHoop[1]) > (0 + adjustmentTolerance)) {
			//flyv ned
			System.out.println("Gonna get low low low low low!");
			System.out.println(hulaHoop[0]);
			cmd.up(30).doFor(doTime);
			Thread.sleep(sleepTime);
			cmd.hover();
		}
		else if((hulaHoop[0] + hulaHoop[1]) < (0 - adjustmentTolerance)) {
			//flyv op
			System.out.println("Because i got high!");
			cmd.down(30).doFor(doTime);
			Thread.sleep(sleepTime);
			cmd.hover();
		}
		else {
			forwardControl++;
		}
		
		if((hulaHoop[2] + hulaHoop[3]) > (0 + adjustmentTolerance)) {
			//flyv h�jre
			System.out.println("You spin med right round");
			cmd.goRight(30).doFor(doTime);
			Thread.sleep(sleepTime);
			cmd.hover();
		}
		else if((hulaHoop[2] + hulaHoop[3]) < (0 - adjustmentTolerance)) {
			// flyv venstre
			System.out.println("everything you own in the box to the left");
			cmd.goLeft(30).doFor(doTime);
			Thread.sleep(sleepTime);
			cmd.hover();
		}
		else {
			forwardControl++;
		}
		
		if (forwardControl == 0) {
			//flyv frem;
			System.out.println("Run niggauh Run niggauh!");
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

	int [] hulaHoop (int high, int low, int right, int left) {
		int [] tmp = new int[4];
		
		tmp[0] = high;
		tmp[1] = low;
		tmp[2] = right;
		tmp[3] = left;
		
		return tmp;
	}
	
}

