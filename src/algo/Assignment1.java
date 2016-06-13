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

import utils.FormattedTimeStamp;

public class Assignment1 implements Runnable{
	// Instantiate picture analyze
	ImgProc obj = new ImgProc();
	// Check if the drone is ready to fly forward
	int forwardControl = 0;
	// For how long the drone should do a task
	int doTime = 1000;
	// A failure margin
	int adjustmentTolerance = 85;	//about 75 (or 3.50)
	int variableTolerance = 0; // (-2/3)*radius + 200 = tolerance
	// Instantiate the pictures hula hoop
	//Object[] picHulaHoops = obj.findHulaHoops();
	
	// The cordiants and radius of the hula hoop in focus
	double x, y;
	double radius;
	private int magicForwardNum = 645000;		//calculated 430000
	private int shittyDroneConstant = 0;		//65 positiv for shitty right movement and negative for shitty left movement
	private int lastMovement = 0;	// 1 = forward, 2 = backwards, 3 = right, 4 = left
	// To check if the drone have flown through the hula hoop
	private static boolean finished = false;
	private static boolean threadRun = false;
	private static long lastCircleTime = new Date().getTime();
	private boolean randomNoCycleLoop = false;
	// The amount of hula hoops
	private int numHulaHoop = 4;
	// The QRcode on the hula hoop
	String qrcode = "";
	
	// List of the hula hoops the drone have flown through
	ArrayList<String> doneHulaHoop = new ArrayList<String>();
	// Making a list with the objects from the picture analyze
	ArrayList<String[]> hulaHoop = (ArrayList<String[]>) obj.findHulaHoops()[1];
	
	public void run() {
		resetTime();
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "]Assignment1] - threadrun start");
		while(threadRun) {
			try{
				/*Long timeChecker = (new Date().getTime() - 20000);
				Long iniTime = GeneralMotorCon.getInstance().getThreadTimer();
				if(iniTime < timeChecker){
					SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "][Assignment1] - Threadrun timer finish, timeChecker: " + timeChecker + ". ThreadTimer: " + GeneralMotorCon.getInstance().getThreadTimer());
					finished = true;
					threadRun = false;
				}
				*/
				if(lastCircleTime < (new Date().getTime()-20000)) {
					SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "][Assignment1] - Threadrun no circle found for too long timer, stopping search");
					threadRun = false;
					finished = true;
				}
				else if(lastCircleTime < (new Date().getTime()-5000)){
					SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "][Assignment1] - Threadrun no circle found timer, start backin up");
					/*switch(doRandom()) {
					case 0:
						GeneralMotorConSchedule.getInstance().forward(500);
						break;
					case 1:
						GeneralMotorConSchedule.getInstance().backward(500);
						break;
					case 2:
						switch(doRandom()){
						case 0:
							GeneralMotorConSchedule.getInstance().spinLeft();
							break;
						case 1:
							GeneralMotorConSchedule.getInstance().spinRight();
							break;
						default:
							GeneralMotorConSchedule.getInstance().backward(500);
							break;
						}
						break;
					} */
					switch(lastMovement) {
					case 1:
						GeneralMotorConSchedule.getInstance().backward(500);
						break;
					case 2:
						GeneralMotorConSchedule.getInstance().forward(500);
						break;
					case 3:
						switch(doRandom()) {
						case 0:
							GeneralMotorConSchedule.getInstance().left();
							break;
						case 1:
							GeneralMotorConSchedule.getInstance().spinLeft();
							break;
						case 2:
							GeneralMotorConSchedule.getInstance().cycleRight();
							break;
						}
						break;
					case 4:
						switch(doRandom()) {
						case 0:
							GeneralMotorConSchedule.getInstance().right();
							break;
						case 1:
							GeneralMotorConSchedule.getInstance().spinRight();
							break;
						case 2:
							GeneralMotorConSchedule.getInstance().cycleLeft();
							break;
						}
						break;
					case 5:
						GeneralMotorConSchedule.getInstance().lowerAltitude();
						break;
					case 6:
						GeneralMotorConSchedule.getInstance().raiseAltitude();
						break;
					}
				}
				Thread.sleep(5000);
			} catch (InterruptedException e ){
				SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "][Assignment1] - threadrun InteruptedException ");		
			}
			
		}
		finished = true;
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "][Assignment1] - threadrun finished");
	}
	
	public void resetTime(){
		SpaceXGUI.getInstance().appendToConsole("\n[" + FormattedTimeStamp.getTime() + "][Assignment1] - reset thread timer");
		lastCircleTime = new Date().getTime();
	}
	
	private void setVariableTolerance() {
		variableTolerance = (int)(-((0.666) * radius) + 200);
	}
	
	public boolean flyHulaHoop() {
		finished = false;
		threadRun = true;
		new Thread(this).start();
		SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Fly Ini");
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
	
	public boolean isFinished() {
		if(doneHulaHoop.size() > numHulaHoop-1) return true;
		return false;
	}
	
	public void flyThrough() {
		boolean middle = false;
		SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - FlyThrough");
		while (!middle) {
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - FlyThrough while loop");
			boolean check = updateHulaHoop();
			if(check) {
				setVariableTolerance();
				if(x < (variableTolerance-shittyDroneConstant) && x > -(variableTolerance+shittyDroneConstant)) {
					if(y < (adjustmentTolerance) && y > -(adjustmentTolerance)){
						SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Middle found");
						SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Radius: " + radius);
						SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - variableTolerance: " + variableTolerance);
						if(radius> 160){
							middle = true;
							SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Final fly through");
							doneHulaHoop.add(qrcode);
							GeneralMotorConSchedule.getInstance().forward((int)(magicForwardNum/radius)).pauseFor((int)(magicForwardNum/radius));	
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
						break;
					case 1:
						GeneralMotorConSchedule.getInstance().spinRight();
						break;
					case 2:
						GeneralMotorConSchedule.getInstance().cycleLeft();
						break;
					}
					GeneralMotorConSchedule.getInstance().pauseFor(100);
					lastMovement = 3;
				} else {
					switch(doRandom()) {
					case 0:
						GeneralMotorConSchedule.getInstance().left();
						break;
					case 1:
						GeneralMotorConSchedule.getInstance().spinLeft();
						break;
					case 2:
						GeneralMotorConSchedule.getInstance().cycleRight();
						break;
					}
					GeneralMotorConSchedule.getInstance().pauseFor(100);
					lastMovement = 4;
				}	
			} else {
				SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Lost the hulahoop");
				if(!threadRun) return;
			}
			GeneralMotorConSchedule.getInstance().pauseFor(50);
		}
		finished = true;
		threadRun = false;
		//GeneralMotorCon.getInstance().forward((2000/radius));
		//doneHulaHoop.add(qrcode);
	}
	 
	public boolean updateHulaHoop() {
		try{
			int updateInteger = 0;
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Updating HulaHoop data");
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
					SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Updating HulaHoop data done");
					updateInteger = 10;
					resetTime();
					updateInteger = 11;
					SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - x: " + x);
					updateInteger = 12;
					SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - y: " + y);
					updateInteger = 13;
					SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - radius: " + radius);
					updateInteger = 14;
					SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - QRcode: " + qrcode);
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
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Updating HulaHoop data done, with no QRcode");
			resetTime();
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - x: " + x);
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - y: " + y);
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - radius: " + radius);
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - QRcode: " + qrcode);
			lastCircleTime = new Date().getTime();
			updateInteger = 21;
			return true;
			
		} catch (NullPointerException e) {
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - NullPointerException, Error: " + e.toString());
		} catch (NumberFormatException e) {
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - NumberFormatException, Error: " + e.toString());
		} catch (IndexOutOfBoundsException e) {
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - IndexOutOfBoundsException, Error: " + e.toString());
		}		
		return false;
	}
	
	/**
	 * Do Random
	 * gives a random number between 1-3 for making a movement decision
	 * 1 - straight to the side
	 * 2 - spin to the side
	 * 3 - cycle to the side (cannot happen twice in a row)  
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
		return 2;
	}
}