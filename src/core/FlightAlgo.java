package core;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;

/**
 * 
 * @author Anders Bækhøj Larsen
 * @author Malte Tais Magnussen
 *
 */

public class FlightAlgo {
	int [] hulaHoop = new int[4];
	int adjustmentTolerance = 0;
	int forwardControl = 0;
	
	IARDrone drone = null;
	CommandManager cmd = drone.getCommandManager();
	
	public void assignment1 () throws InterruptedException {
		forwardControl = -2;
		
		//hulaHoop(high, low, right, left)
		hulaHoop(200, -100, 50, -250);
		
		if((hulaHoop[0] + hulaHoop[1]) > (0 + adjustmentTolerance)) {
			//flyv ned
			System.out.println(hulaHoop[0]);
			cmd.up(30).doFor(500);
			cmd.hover();
		}
		else if((hulaHoop[0] + hulaHoop[1]) < (0 - adjustmentTolerance)) {
			//flyv op
			cmd.down(30).doFor(500);
			cmd.hover();
		}
		else {
			forwardControl++;
		}
		
		if((hulaHoop[2] + hulaHoop[3]) > (0 + adjustmentTolerance)) {
			//flyv højre
			cmd.goRight(30).doFor(500);
			cmd.hover();
		}
		else if((hulaHoop[2] + hulaHoop[3]) < (0 - adjustmentTolerance)) {
			// flyv venstre
			cmd.goLeft(30).doFor(500);
			cmd.hover();
		}
		else {
			forwardControl++;
		}
		
		if (forwardControl == 0) {
			//flyv frem;
			cmd.forward(30).doFor(500);
			cmd.hover();
		}
		
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

