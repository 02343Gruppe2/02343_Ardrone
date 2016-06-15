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
	
	// Instantiate picture analyze
	ImgProc obj = new ImgProc();
	
	// For how long the drone should do a task
	int doTime = 1000;
	
	/* A failure margin */
	int adjustmentTolerance = 100;			//adjustment used for the y-axis
	int variableTolerance = 0; 				//adjustment used for the x-axis, calculated: (-2/3)*radius + 200 = tolerance
	private int shittyDroneConstant = 0;	//To make sure the drone get through the circle, since the drones forward strafs abit to the right 65 positiv for shitty right movement and negative for shitty left movement
	
	/* The information of the last found hulahoop */
	double x, y;		//middle coordinate
	double radius;		//Radius 
	String qrcode = "";	//The QRCode to the hulahoop
	
	// A number to make sure the drone fly forward long enough
	private int magicForwardNum = 430000*4;		//calculated 4300007
	 
	/* Check timer variables */
	// Used to check which movement the drone did last
	private int lastMovement = 0;	// 1 = forward, 2 = backwards, 3 = right, 4 = left
	// Check if the threadRun shoud stop the drone
	private static boolean threadRun = false;
	// Last time the drone saw a circle
	private static long lastCircleTime = new Date().getTime();
	
	
	
	// To check if the drone have flown through the hula hoop
	private static boolean finished = false;
	// Makes sure the drone doesn't do a cycle movement 2 times in a row
	private boolean randomNoCycleLoop = false;
	// The amount of hula hoops
	private int numHulaHoop = 4;
	
	// List of the hula hoops the drone have flown through
	ArrayList<String> doneHulaHoop = new ArrayList<String>();
	// Making a list with the objects from the picture analyze
	ArrayList<String[]> hulaHoop = (ArrayList<String[]>) obj.findHulaHoops()[1];
	
	/**
	 * 
	 */
	public void run() {
		resetTime();
		SpaceXGUI.getInstance().appendToConsole(TAG," - threadrun start");
		while(threadRun) {
			try{
				if(lastCircleTime < (new Date().getTime()-20000)) {
					SpaceXGUI.getInstance().appendToConsole(TAG," - Threadrun no circle found for too long timer, stopping search");
					threadRun = false;
					finished = true;
					return;
				}
				else if(lastCircleTime < (new Date().getTime()-5000)){
					SpaceXGUI.getInstance().appendToConsole(TAG," - Threadrun no circle found timer, start backin up");
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
				Thread.sleep(5000);
			} catch (InterruptedException e ){
				SpaceXGUI.getInstance().appendToConsole(TAG," - threadrun InteruptedException ");		
			}
			
		}
		finished = true;
		SpaceXGUI.getInstance().appendToConsole(TAG," - threadrun finished");
	}
	
	/**
	 * 
	 */
	public void resetTime(){
		SpaceXGUI.getInstance().appendToConsole(TAG," - reset thread timer");
		lastCircleTime = new Date().getTime();
	}
	
	/**
	 * 
	 */
	private void setVariableTolerance() {
		variableTolerance = (int)(-((0.666) * radius) + 200);
	}
	
	/**
	 * 
	 */
	public boolean flyHulaHoop() {
		finished = false;
		threadRun = true;
		new Thread(this).start();
		SpaceXGUI.getInstance().appendToConsole(TAG," - Fly Ini");
		while (!finished) {
			updateHulaHoop();
					
			flyThrough();
	
			/*if(qrcode == ("P.0" + doneHulaHoop.size())) {			
				flyThrough();
			}*/
			
			//if(doneHulaHoop.size() > numHulaHoop-1) finished = true;
		}
		threadRun = false;
		return true;
	}
	
	/**
	 * 
	 */
	public boolean isFinished() {
		if(doneHulaHoop.size() > numHulaHoop-1) return true;
		return false;
	}
	
	/**
	 * 
	 */
	public void flyThrough() {
		boolean middle = false;
		SpaceXGUI.getInstance().appendToConsole(TAG," - FlyThrough");
		while (!middle) {
			SpaceXGUI.getInstance().appendToConsole(TAG," - FlyThrough while loop");
			boolean check = updateHulaHoop();
			if(check) {
				setVariableTolerance();
				if(x < (variableTolerance-shittyDroneConstant) && x > -(variableTolerance+shittyDroneConstant)) {
					if(y < (adjustmentTolerance) && y > -(adjustmentTolerance)){
						SpaceXGUI.getInstance().appendToConsole(TAG," - Middle found");
						SpaceXGUI.getInstance().appendToConsole(TAG," - Radius: " + radius);
						SpaceXGUI.getInstance().appendToConsole(TAG," - variableTolerance: " + variableTolerance);
						if(radius> 160){
							middle = true;
							SpaceXGUI.getInstance().appendToConsole(TAG," - Final fly through");
							doneHulaHoop.add(qrcode);
							int forwardFor = (int)(magicForwardNum/radius);
							GeneralMotorConSchedule.getInstance().forward(forwardFor).pauseFor(forwardFor);	
						} else {
							GeneralMotorConSchedule.getInstance().forward(1000).pauseFor(500);
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
				SpaceXGUI.getInstance().appendToConsole(TAG," - Lost the hulahoop");
				if(!threadRun) return;
			}
			GeneralMotorConSchedule.getInstance().pauseFor(200);
		}
		finished = true;
		threadRun = false;
		//GeneralMotorCon.getInstance().forward((2000/radius));
		//doneHulaHoop.add(qrcode);
	}
	 
	/**
	 * 
	 */
	public boolean updateHulaHoop() {
		int updateInteger = 0;
		try{
			
			SpaceXGUI.getInstance().appendToConsole(TAG," - Updating HulaHoop data");
			hulaHoop = (ArrayList<String[]>)obj.findHulaHoops()[1];
			radius = 0;
			updateInteger = 1;
			if(hulaHoop.size() == 0) return false;
			updateInteger = 2;
			if(hulaHoop.isEmpty()) return false;
			updateInteger = 3;
			for (String[] s : hulaHoop){
				updateInteger = 4;
				if(s[3].contains("P")){
					updateInteger = 5;
					x = Double.parseDouble(s[0]);
					updateInteger = 6;
					y = Double.parseDouble(s[1]);
					updateInteger = 7;
					radius = Double.parseDouble(s[2]);
					updateInteger = 8;
					qrcode = s[3];
					updateInteger = 9;
					SpaceXGUI.getInstance().appendToConsole(TAG," - Updating HulaHoop data done");
					updateInteger = 10;
					resetTime();
					updateInteger = 11;
					SpaceXGUI.getInstance().appendToConsole(TAG," - x: " + x);
					updateInteger = 12;
					SpaceXGUI.getInstance().appendToConsole(TAG," - y: " + y);
					updateInteger = 13;
					SpaceXGUI.getInstance().appendToConsole(TAG," - radius: " + radius);
					updateInteger = 14;
					SpaceXGUI.getInstance().appendToConsole(TAG," - QRcode: " + qrcode);
					updateInteger = 15;
					lastCircleTime = new Date().getTime();
					return true;
				}
				if(Double.parseDouble(s[2]) > radius){
					updateInteger = 16;
					x = Double.parseDouble(s[0]);
					updateInteger = 17;
					y = Double.parseDouble(s[1]);
					updateInteger = 18;
					radius = Double.parseDouble(s[2]);
					updateInteger = 19;
					//qrcode = s[3];
				}
			}
			updateInteger = 20;
			SpaceXGUI.getInstance().appendToConsole(TAG," - Updating HulaHoop data done, with no QRcode");
			resetTime();
			SpaceXGUI.getInstance().appendToConsole(TAG," - x: " + x);
			SpaceXGUI.getInstance().appendToConsole(TAG," - y: " + y);
			SpaceXGUI.getInstance().appendToConsole(TAG," - radius: " + radius);
			SpaceXGUI.getInstance().appendToConsole(TAG," - QRcode: " + qrcode);
			lastCircleTime = new Date().getTime();
			updateInteger = 21;
			return true;
			
		} catch (NullPointerException e) {
			SpaceXGUI.getInstance().appendToConsole(TAG," - NullPointerException, Error " + updateInteger + ": " + e.toString());
		} catch (NumberFormatException e) {
			SpaceXGUI.getInstance().appendToConsole(TAG," - NumberFormatException, Error: " + e.toString());
		} catch (IndexOutOfBoundsException e) {
			SpaceXGUI.getInstance().appendToConsole(TAG," - IndexOutOfBoundsException, Error: " + e.toString());
		}		
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
}