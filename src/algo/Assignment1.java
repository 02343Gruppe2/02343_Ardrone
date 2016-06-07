package algo;

/**
 * Assignment 1
 * The hulahop assignment.
 * The drone has to find and fly through 4 hulahops in the room.
 * @author Anders Bækhøj Larsen
 * @author Malte Tais Magnussen
 *
 */

import gui.SpaceXGUI;
import core.PicAnal;
import algo.GeneralMotorCon;
import java.util.ArrayList;;

public class Assignment1 {
	// Instantiate picture analyze
	PicAnal obj = new PicAnal();
	// Check if the drone is ready to fly forward
	int forwardControl = 0;
	// For how long the drone should do a task
	int doTime = 1000;
	// A failure margin
	int adjustmentTolerance = 10;
	
	// Instantiate the pictures hula hoop
	Object[] picHulaHops = obj.findHulahops();
	
	// The cordiants and radius of the hula hoop in focus
	int x, y, radius;
	// To check if the drone have flown through the hula hoop
	boolean finished = false;
	// The amount of hula hoops
	int numHulaHoop = 4;
	// The QRcode on the hula hoop
	String qrcode = "";
	
	// List over the hula hoops the drown have flown through
	ArrayList<String> doneHulaHoop = new ArrayList<String>();
	// Making a list with the objects from the picture analyze
	ArrayList<Object[]> hulaHoop = (ArrayList<Object[]>)obj.findHulahops()[1];
	
	public boolean run() {
		// Getting the object array from picture hula hoop
		while (!finished) {
			updateHulahoop();
			
			if(qrcode == ("P.0" + doneHulaHoop.size())) {			
				flyThrough();
			}
			
			
			//TODO: make FlightSearch call here to search for hulaHOOOOOOOPS
			
			if(doneHulaHoop.size() > numHulaHoop-1) finished = true;
		}
		
		return true;
	}
	
	public void flyThrough() {
		boolean middle = false;

		while (!middle) {
			if(updateHulahoop()) {
				if(x < adjustmentTolerance && x > -adjustmentTolerance) {
					if(y < adjustmentTolerance && y > -adjustmentTolerance){
						middle = true;
					} else if(y > 0){
						GeneralMotorCon.getInstance().raiseAltitude();
					} else {
						GeneralMotorCon.getInstance().lowerAltitude();
					}
				} else if(x > 0){
					GeneralMotorCon.getInstance().right();
				} else {
					GeneralMotorCon.getInstance().left();
				}	
			} else {
				SpaceXGUI.getInstance().appendToConsole("Lost the hulahoop");
				return;
			}
		}
		
		GeneralMotorCon.getInstance().forward((2000/radius));
		doneHulaHoop.add(qrcode);
	}
	
	public boolean updateHulahoop() {
		
		
		try{
			hulaHoop = (ArrayList<Object[]>)obj.findHulahops()[1];
			x = Integer.parseInt(hulaHoop.get(0).toString());
			y = Integer.parseInt(hulaHoop.get(1).toString());
			radius = Integer.parseInt(hulaHoop.get(2).toString());
			
			qrcode = hulaHoop.get(3).toString();
			return true;
			
		} catch (NullPointerException e) {
			SpaceXGUI.getInstance().appendToConsole(e.toString());
		} catch (NumberFormatException e) {
			SpaceXGUI.getInstance().appendToConsole(e.toString());
		}
		
		return false;
	}
	
}
