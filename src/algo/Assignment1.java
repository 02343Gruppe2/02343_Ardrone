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

import java.util.ArrayList;
import java.util.Date;

public class Assignment1 extends Thread{
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
	double x, y;
	double radius;
	// To check if the drone have flown through the hula hoop
	boolean finished = false;
	boolean threadRun = false;
	// The amount of hula hoops
	int numHulaHoop = 4;
	// The QRcode on the hula hoop
	String qrcode = "";
	
	 long iniTime;
	
	// List of the hula hoops the drone have flown through
	ArrayList<String> doneHulaHoop = new ArrayList<String>();
	// Making a list with the objects from the picture analyze
	ArrayList<String[]> hulaHoop = (ArrayList<String[]>) obj.findHulahops()[1];
	
	public void run() {
		resetTime();
		while(threadRun) {
			if(iniTime > new Date().getTime() + 10000){
				finished = true;
			}	
		}
	}
	
	public void resetTime(){
		iniTime = new Date().getTime();
	}
	
	public boolean fly() {
		// Getting the object array from picture hula hoop
		finished = false;
		threadRun = true;
		new Thread(this).run();
		while (!finished) {
			updateHulahoop();
			
			
			flyThrough();
			
			/*if(qrcode == ("P.0" + doneHulaHoop.size())) {			
				flyThrough();
			}*/
			
			//if(doneHulaHoop.size() > numHulaHoop-1) finished = true;
		}
		threadRun = false;
		return true;
	}
	
	public boolean isFinished() {
		if(doneHulaHoop.size() > numHulaHoop-1) return true;
		return false;
	}
	
	public void flyThrough() {
		boolean middle = false;
		SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - FlyThrough");
		while (!middle) {
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - FlyThrough while loop");
			if(updateHulahoop()) {
				if(x < adjustmentTolerance && x > -adjustmentTolerance) {
					if(y < adjustmentTolerance && y > -adjustmentTolerance){
						SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Middle found");
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
				SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Lost the hulahoop");
				return;
			}
		}
		
		//GeneralMotorCon.getInstance().forward((2000/radius));
		//doneHulaHoop.add(qrcode);
	}
	
	public boolean updateHulahoop() {
		
		
		try{
			hulaHoop = (ArrayList<String[]>)obj.findHulahops()[1];
			x = Double.parseDouble(hulaHoop.get(0)[0]);
			y = Double.parseDouble(hulaHoop.get(0)[1]);
			radius = Double.parseDouble(hulaHoop.get(0)[2]);
			
			qrcode = hulaHoop.get(0)[3];
			resetTime();
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Updated HulaHoop data");
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - x: " + x);
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - y: " + y);
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - radius: " + radius);
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - QRcode: " + qrcode);
			return true;
			
		} catch (NullPointerException e) {
			SpaceXGUI.getInstance().appendToConsole(e.toString() + "Assignment 1 update hula hoop fucker");
		} catch (NumberFormatException e) {
			SpaceXGUI.getInstance().appendToConsole("\n[" + e.toString());
		}
		
		return false;
	}
	
}
