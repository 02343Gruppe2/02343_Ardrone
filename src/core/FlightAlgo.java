package core;
import de.yadrone.base.IARDrone;

/**
 * 
 * @author Anders Bækhøj Larsen er en kæmpe kælling
 * @author Malte Tais Magnussen
 *
 */

public class FlightAlgo {
	int [] hulaHoop = new int[4];
	int adjustmentTolerance = 0;
	int forwardControl = 0;
	
	IARDrone drone = null;
	
	public void assignment1 () {
		forwardControl = -2;
		
		if((hulaHoop[0] + hulaHoop[1]) > (0 + adjustmentTolerance)) {
			//flyv ned
			drone.down();
		}
		else if((hulaHoop[0] + hulaHoop[1]) < (0 - adjustmentTolerance)) {
			//flyv op
			drone.up();
		}
		else {
			forwardControl++;
		}
		
		if((hulaHoop[2] + hulaHoop[3]) > (0 + adjustmentTolerance)) {
			//flyv højre
			drone.goRight();
		}
		else if((hulaHoop[2] + hulaHoop[3]) < (0 - adjustmentTolerance)) {
			// flyv venstre
			drone.goLeft();
		}
		else {
			forwardControl++;
		}
		
		if (forwardControl == 0) {
			//flyv frem;
			drone.forward();
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

