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
import core.ImgProc;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Assignment1 implements Runnable{
	private static final String TAG = "Assignment1";
	private static final boolean printToConsoleDebug = true;
	
	/* Picture Analyze */
	ImgProc obj = new ImgProc();
	ArrayList<String[]> hulaHoop = (ArrayList<String[]>) obj.findHulaHoops()[1];		// Making a list with the objects from the picture analyze


	/* A failure margin */
	int adjustmentTolerance = 120;			//adjustment used for the y-axis
	int variableTolerance = 0; 				//adjustment used for the x-axis, calculated: (-2/3)*radius + 200 = tolerance
	private int shittyDroneConstant = 0;	//To make sure the drone get through the circle, since the drones forward strafs abit to the right 65 positiv for shitty right movement and negative for shitty left movement
	
	/* The information of the last found hulahoop */
	double x, y;				//middle coordinate
	double radius;				//Radius 
	String qrcode = "";			//The QRCode to the hulahoop
	
	private final int magicForwardNum = 430000*2;	// A number to make sure the drone fly forward long enough
	private int lastMovement = 0;					// 1 = forward, 2 = backwards, 3 = right, 4 = left
	
	/* Running variables */	
	private static boolean finished = false;						// The finishing of the main thread
	private boolean randomNoCycleLoop = false;						// Makes sure the drone doesn't do a cycle movement 2 times in a row
	private static boolean threadRun = false;						// Check for the timerThread
	private static long lastCircleTime = new Date().getTime();		// Last time the drone saw a circle	

	/* Hula Hoop Variables and Constants*/
	private int numHulaHoop = 4;									// The amount of hula hoops		
	private final int radiusForwardCheck = 190;						// The radius the hula hops has to have for the drone to fly forward
	private final int pauseWhile = 200;								// The amount the while loop has to wait
	private final int shortWaitTime = 5000;							// The short wait time before the drone backtracks last movements
	private final int longWaitTime = shortWaitTime*4;							// The long wait time before the thread stops
	ArrayList<String> doneHulaHoop = new ArrayList<String>();		// List of the hula hoops the drone have flown through
	ArrayList<String> allHulaHoops = new ArrayList<String>(); 		// The HulaHoop QRCodes
	
	public Assignment1() {
		for(int i = 0; i < numHulaHoop; i++) {	//TODO: i start from 0
			if(i < 10) {
				allHulaHoops.add("P.0" + i );
			}
			else {
				allHulaHoops.add("P." + i);
			}
		}
	}
	
	/**
	 * Fly Hula Hoop
	 * This is the method to call when you've found hulahoop and want to fly through it
	 * @return - true if it flew through a hula hoop
	 */
	public boolean flyHulaHoop() {
		finished = false;
		threadRun = true;
		new Thread(this).start();
		if(printToConsoleDebug)SpaceXGUI.getInstance().appendToConsole(TAG," - Fly Ini");
		while (!finished) {
	
			flyThrough();
			
			if(doneHulaHoop.size() >= numHulaHoop) finished = true;
		}
		if(printToConsoleDebug) SpaceXGUI.getInstance().appendToConsole(TAG, "Next QRCode to find: " + allHulaHoops.get(doneHulaHoop.size()));
		threadRun = false;
		return true;
	}
	
	/**
	* Fly Through
	*/
	private void flyThrough() {
		boolean middle = false;
		while (!middle) {
			boolean check = updateHulaHoop();
			if(check) {
				setVariableTolerance();
				if(x < (variableTolerance-shittyDroneConstant) && x > -(variableTolerance+shittyDroneConstant)) {
					if(y < (adjustmentTolerance) && y > -(adjustmentTolerance)){
						if(printToConsoleDebug){
							SpaceXGUI.getInstance().appendToConsole(TAG," - Middle found");
							SpaceXGUI.getInstance().appendToConsole(TAG," - Radius: " + radius);
							SpaceXGUI.getInstance().appendToConsole(TAG," - variableTolerance: " + variableTolerance);
						}
						if(radius > radiusForwardCheck){
							middle = true;
							SpaceXGUI.getInstance().appendToConsole(TAG," - Final fly through");
							doneHulaHoop.add(qrcode);
							int forwardFor = (int)(magicForwardNum/radius);
							if(printToConsoleDebug)GeneralMotorConSchedule.getInstance().forward(forwardFor).pauseFor(forwardFor);	
						} else {
							GeneralMotorConSchedule.getInstance().forward(1000).pauseFor(1000);
						}
						lastMovement = 1;
					} else if(y > 0){
						GeneralMotorConSchedule.getInstance().raiseAltitude();
						lastMovement = 5;
					} else {
						GeneralMotorConSchedule.getInstance().lowerAltitude();
						lastMovement = 6;
					}
				} else if(x > 0){
					switch(doRandom()) {
					case 0:
						GeneralMotorConSchedule.getInstance().right();
						lastMovement = 3;
						break;
					case 1:
						GeneralMotorConSchedule.getInstance().spinRight();
						lastMovement = 7;
						break;
					case 2:
						GeneralMotorConSchedule.getInstance().cycleLeft();
						lastMovement = 10;
						break;
					}
					GeneralMotorConSchedule.getInstance().pauseFor(100);
					
				} else {
					switch(doRandom()) {
					case 0:
						GeneralMotorConSchedule.getInstance().left();
						lastMovement = 4;
						break;
					case 1:
						GeneralMotorConSchedule.getInstance().spinLeft();
						lastMovement = 8;
						break;
					case 2:
						GeneralMotorConSchedule.getInstance().cycleRight();
						lastMovement = 9;
						break;
					}
					GeneralMotorConSchedule.getInstance().pauseFor(100);
				}	
			} else {
				if(printToConsoleDebug)SpaceXGUI.getInstance().appendToConsole(TAG," - Lost the hulahoop");
				if(!threadRun) return;
			}
			GeneralMotorConSchedule.getInstance().pauseFor(pauseWhile);
		}
		finished = true;
		threadRun = false;
		doneHulaHoop.add(qrcode);
	}
	 
	/**
	 * Update Hula Hoop
	 * checks for hula hoop and saves the x, y, radius and QRcode
	 * @return - true, if a hulahoop was found.
	 */
	public boolean updateHulaHoop() {
		try{
			if(printToConsoleDebug)SpaceXGUI.getInstance().appendToConsole(TAG," - Updating HulaHoop data, looking for: " + allHulaHoops.get(doneHulaHoop.size()));
			hulaHoop = (ArrayList<String[]>)obj.findHulaHoops()[1];
			if(hulaHoop.size() == 0) return false;
			if(hulaHoop.isEmpty()) return false;
			for (String[] s : hulaHoop){
				if(s[3].contains(allHulaHoops.get(doneHulaHoop.size()))){
					x = Double.parseDouble(s[0]);
					y = Double.parseDouble(s[1]);
					radius = Double.parseDouble(s[2]);
					qrcode = s[3];
					resetTime();
					SpaceXGUI.getInstance().appendToConsole(TAG," - x: " + x);
					SpaceXGUI.getInstance().appendToConsole(TAG," - y: " + y);
					SpaceXGUI.getInstance().appendToConsole(TAG," - radius: " + radius);
					SpaceXGUI.getInstance().appendToConsole(TAG," - QRcode: " + qrcode);
					return true;
				}
			}
		} catch (NullPointerException e) {
			SpaceXGUI.getInstance().appendToConsole(TAG," - NullPointerException, Error: " + e.toString());
		} catch (NumberFormatException e) {
			SpaceXGUI.getInstance().appendToConsole(TAG," - NumberFormatException, Error: " + e.toString());
		} catch (IndexOutOfBoundsException e) {
			SpaceXGUI.getInstance().appendToConsole(TAG," - IndexOutOfBoundsException, Error: " + e.toString());
		}
		if(printToConsoleDebug)SpaceXGUI.getInstance().appendToConsole(TAG," - No QRcode");
		return false;
	}
	
	/**
	 * Do Random
	 * gives a random number between 1-3 for making a movement decision
	 * 0 - straight to the side
	 * 1 - spin to the side
	 * 2 - cycle to the side (cannot happen twice in a row)  
	 * @return - random number
	 */
	private int doRandom(){
		boolean runner = true;
		while(runner){
			int randomNum = ThreadLocalRandom.current().nextInt(0, 101);
			if(randomNum >= 66 ){
				randomNoCycleLoop = false;
				return 0;
			} else if(randomNum >= 33) {
				randomNoCycleLoop = false;
				return 1;
			} else if(!randomNoCycleLoop) {
				runner = false;
			}	
		}
		randomNoCycleLoop = true;
		return 0;
	}


	/**
	 * Reset Time
	 * method which resets the last circle found time.
	 */
	private void resetTime(){
		if(printToConsoleDebug)SpaceXGUI.getInstance().appendToConsole(TAG," - reset thread timer");
		lastCircleTime = new Date().getTime();
	}
	
	/**
	 * Set Variable Tolerance
	 * Setting the variable tolerance for the x-axis
	 */
	private void setVariableTolerance() {
		variableTolerance = (int)(-((0.666) * radius) + 300);
		if(printToConsoleDebug)SpaceXGUI.getInstance().appendToConsole(TAG, "variableTolerance: " + variableTolerance);
	}
	
	/**
	 * is Finished?
	 * @return - true, if all hula hoops have been found
	 */
	public boolean isFinished() {
		if(doneHulaHoop.size() > numHulaHoop-1) return true;
		return false;
	}
	
	/**
	 * The Thread timer
	 * a thread run which controls if the drone 
	 * haven't been able to find a hula hoop
	 * for a specific time and makes counter movements
	 * the first few times.  
	 */
	public void run() {
		resetTime();
		if(printToConsoleDebug)SpaceXGUI.getInstance().appendToConsole(TAG," - threadrun start");
		while(threadRun) {
			try{
				if(lastCircleTime < (new Date().getTime()-longWaitTime)) {
					if(printToConsoleDebug)SpaceXGUI.getInstance().appendToConsole(TAG," - Threadrun no circle found for too long timer, stopping search");
					threadRun = false;
					finished = true;
					return;
				}
				else if(lastCircleTime < (new Date().getTime()-shortWaitTime)){
					if(printToConsoleDebug)SpaceXGUI.getInstance().appendToConsole(TAG," - Threadrun no circle found timer, start backin up");
					switch(lastMovement) {
					case 1:
						GeneralMotorConSchedule.getInstance().backward(500);
						break;
					case 2:
						GeneralMotorConSchedule.getInstance().forward(500);
						break;
					case 3:
						GeneralMotorConSchedule.getInstance().left();
						break;
					case 4:
						GeneralMotorConSchedule.getInstance().right();
						break;
					case 5:
						GeneralMotorConSchedule.getInstance().lowerAltitude();
						break;
					case 6:
						GeneralMotorConSchedule.getInstance().raiseAltitude();
						break;
					case 7:
						GeneralMotorConSchedule.getInstance().spinLeft();
						break;
					case 8:
						GeneralMotorConSchedule.getInstance().spinRight();
						break;
					case 9:
						GeneralMotorConSchedule.getInstance().cycleLeft();
						break;
					case 10:
						GeneralMotorConSchedule.getInstance().cycleRight();
						break;
					}
				}
				Thread.sleep(shortWaitTime);
			} catch (InterruptedException e ){
				if(printToConsoleDebug)SpaceXGUI.getInstance().appendToConsole(TAG," - threadrun InteruptedException ");		
			}
			
		}
		finished = true;
		if(printToConsoleDebug)SpaceXGUI.getInstance().appendToConsole(TAG," - threadrun finished");
	}
}