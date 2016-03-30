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
			System.out.println("Flyver ned fordi anders mor vil ha den ned i sin hals");
			System.out.println(hulaHoop[0]);
			cmd.up(30).doFor(1000);
			Thread.sleep(1000);
			cmd.hover();
		}
		else if((hulaHoop[0] + hulaHoop[1]) < (0 - adjustmentTolerance)) {
			//flyv op
			System.out.println("Flyver op fordi anders mor vil ha den op i røven");
			cmd.down(30).doFor(1000);
			Thread.sleep(1000);
			cmd.hover();
		}
		else {
			forwardControl++;
		}
		
		if((hulaHoop[2] + hulaHoop[3]) > (0 + adjustmentTolerance)) {
			//flyv højre
			System.out.println("Flyver til højre fordi fuck anders");
			cmd.goRight(30).doFor(1000);
			Thread.sleep(1000);
			cmd.hover();
		}
		else if((hulaHoop[2] + hulaHoop[3]) < (0 - adjustmentTolerance)) {
			// flyv venstre
			System.out.println("Flyver til venstre fordi anders mor best kan li at få den i røven");
			cmd.goLeft(30).doFor(1000);
			Thread.sleep(100);
			cmd.hover();
		}
		else {
			forwardControl++;
		}
		
		if (forwardControl == 0) {
			//flyv frem;
			System.out.println("Flyver frem fordi Anders er grim");
			cmd.forward(30).doFor(1000);
			Thread.sleep(1000);
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

