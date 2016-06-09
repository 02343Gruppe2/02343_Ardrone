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

public class Assignment1 implements Runnable{
	// Instantiate picture analyze
	PicAnal obj = new PicAnal();
	// Check if the drone is ready to fly forward
	int forwardControl = 0;
	// For how long the drone should do a task
	int doTime = 1000;
	// A failure margin
	int adjustmentTolerance = 100;
	
	// Instantiate the pictures hula hoop
	Object[] picHulaHops = obj.findHulahops();
	
	// The cordiants and radius of the hula hoop in focus
	double x, y;
	double radius;
	private int magicForwardNum = 430000;
	// To check if the drone have flown through the hula hoop
	private boolean finished = false;
	private boolean threadRun = false;
	// The amount of hula hoops
	private int numHulaHoop = 4;
	// The QRcode on the hula hoop
	String qrcode = "";
	
	long iniTime;
	
	// List of the hula hoops the drone have flown through
	ArrayList<String> doneHulaHoop = new ArrayList<String>();
	// Making a list with the objects from the picture analyze
	ArrayList<String[]> hulaHoop = (ArrayList<String[]>) obj.findHulahops()[1];
	
	public void run() {
		resetTime();
		SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - threadrun start");
		while(threadRun) {
			if(iniTime < new Date().getTime() - 10000){
				SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Threadrun timer finish");
				finished = true;
				threadRun = false;
			}	
		}
		finished = true;
		SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - threadrun finished");
	}
	
	public synchronized void resetTime(){
		iniTime = new Date().getTime();
	}
	
	public boolean fly() {
		finished = false;
		threadRun = true;
		new Thread(this).start();
		SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Fly continue");
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
			boolean check = updateHulahoop();
			if(check) {
				if(x < adjustmentTolerance && x > -adjustmentTolerance) {
					if(y < adjustmentTolerance && y > -adjustmentTolerance){
						SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Middle found");
						middle = true;
						GeneralMotorCon.getInstance().forward((int)(magicForwardNum/radius));
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
				if(!threadRun) return;
			}
		}
		
		//GeneralMotorCon.getInstance().forward((2000/radius));
		//doneHulaHoop.add(qrcode);
	}
	
	public boolean updateHulahoop() {
		try{
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Updating HulaHoop data");
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - " + iniTime);
			hulaHoop = (ArrayList<String[]>)obj.findHulahops()[1];
			radius = 0;
			if(hulaHoop.size() == 0) return false;
			if(hulaHoop.isEmpty()) return false;
			for (String[] s : hulaHoop){
				if(s[3].contains("P")){
					x = Double.parseDouble(s[0]);
					y = Double.parseDouble(s[1]);
					radius = Double.parseDouble(s[2]);
					qrcode = s[3];
					SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Updating HulaHoop data done");
					
					resetTime();
					SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - x: " + x);
					SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - y: " + y);
					SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - radius: " + radius);
					SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - QRcode: " + qrcode);
					return true;
				}
				if(Double.parseDouble(s[2]) > radius){
					x = Double.parseDouble(s[0]);
					y = Double.parseDouble(s[1]);
					radius = Double.parseDouble(s[2]);
					//qrcode = s[3];
				}
			}
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - Updating HulaHoop data done, with no QRcode");
			resetTime();
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - x: " + x);
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - y: " + y);
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - radius: " + radius);
			SpaceXGUI.getInstance().appendToConsole("\n[Assignment1] - QRcode: " + qrcode);
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
}