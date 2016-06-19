package algo;

/**
 * 
 * @author Jiahua Liang
 *
 */

import java.util.ArrayList;

import org.opencv.core.Rect;

import core.ImgProc;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import gui.SpaceXGUI;

public class FlightSearch {
	boolean qrcodeFound = false;
	boolean map = false;
	boolean forwardControl = false;
	boolean newQRCode = true;
	boolean adjust = false;
	
	int errormargin = 0, rectangleWidth = 190;
	
	private final String TAG = "FlightSearch";

	// Instantiate picture analyze
	ImgProc obj = new ImgProc();
	// Instantiate Assignment 1
	Assignment1 assign1 = new Assignment1();

	// Instantiate Object for qrcode
	Object[] res;
	ArrayList<String> qrcode = null;

	// QRcode scanner metode.
	public void qrcodeScan() {
		res = obj.findQRCodes();
		qrcode = (ArrayList<String>) ((Object[]) res[0])[0];
	}

	// Metode for forward check.
	public void forwardCheck() {
		qrcodeScan();

		for (int g = 0; g < qrcode.size(); g++) {
			/*if (assign1.updateHulaHoop()) {
				forwardControl = false;
				map = true;
				break;

			} else*/ if (qrcode.get(g).contains("W")) {
				forwardControl = false;
				break;
			}
		}
		GeneralMotorConSchedule.getInstance().forward(1000).pauseFor(500);;
	}

	public void adjustdrone(int j, String currentQr) {
		
		int scanCounter = 0;
		while ((!adjust) && scanCounter < 50) {
			if (newQRCode) {
				Rect rect = ((ArrayList<Rect>) ((Object[]) res[0])[1]).get(j);
				double x = ((rect.br().x - rect.tl().x) / 2) + rect.tl().x,
						y = ((rect.br().y - rect.tl().y) / 2) + rect.tl().y;
				double[] displacedCoordinates = ImgProc.coordinateDisplacement(x, y);
				x = displacedCoordinates[0];
				y = displacedCoordinates[1];
				
				SpaceXGUI.getInstance().appendToConsole(TAG, "rect str: " + rect.width);
				SpaceXGUI.getInstance().appendToConsole(TAG, "x: " + x + " y: " + y);
				
				/*while((rect.width < 100)) {
					GeneralMotorConSchedule.getInstance().forward(500).pauseFor(500);
					qrcodeScan();
					rect = ((ArrayList<Rect>) ((Object[]) res[0])[1]).get(j);
					SpaceXGUI.getInstance().appendToConsole(TAG, "rect str: " + rect.width);
				}*/
				
				//errormargin=rect.width*2-100; 
				errormargin=(int)(600*0.9886)^rect.width;
				SpaceXGUI.getInstance().appendToConsole(TAG, "Erromargin: " + errormargin);
				if (x < errormargin && x > -errormargin ) {
					if(y < errormargin && y > -errormargin){
						if(rect.width < rectangleWidth){
							GeneralMotorConSchedule.getInstance().forward(500).pauseFor(1000);
						}else{
							adjust = true;
							break;
						}
					}else if(y > 0){
						GeneralMotorConSchedule.getInstance().raiseAltitude();
					}else {
						GeneralMotorConSchedule.getInstance().lowerAltitude();
					}
					
				} else if(x > 0 ){
					GeneralMotorConSchedule.getInstance().right();
				}else {
					GeneralMotorConSchedule.getInstance().left();
				}
			}
			
			//GeneralMotorConSchedule.getInstance().pauseFor(200);
			qrcodeScan();
			scanCounter++;
			newQRCode = false;
			SpaceXGUI.getInstance().appendToConsole(TAG, "Scanned, qrcode size: " + qrcode.size() + " current qrcode: "+currentQr);
			for (j = 0; j < qrcode.size(); j++) {
				if (qrcode.get(j).equals(currentQr)) {
					newQRCode = true;
					scanCounter = 0;
					break;
				}
			}
		}
	}

	public void afstand(){
		while(true){
			qrcodeScan();
			for(int j=0; j<qrcode.size(); j++){
			Rect rect = ((ArrayList<Rect>) ((Object[]) res[0])[1]).get(j);
			SpaceXGUI.getInstance().appendToConsole(TAG, "rect str: " + rect.width);
			} 
		}
		
	}
	
	public void search() {
		// spin 10 times and scan for hulahop and qrcode

		for (int i = 0; i < 4; i++) {
			qrcodeScan();

			/*if (assign1.updateHulaHoop()) {
				qrcodeFound = true;
				map = true;
				break;
			} else*/ if (!qrcode.isEmpty()) {
				qrcodeFound = true;
				break;
			}
			GeneralMotorConSchedule.getInstance().spin90Left().pauseFor(5000);
		}

		while (!qrcodeFound) { // Scan for qrcode..
			qrcodeScan();

			// Checking for Hulahop found
			/*if (assign1.updateHulaHoop()) {
				qrcodeFound = true;
				map = true;
				break;
			} else*/ if (!qrcode.isEmpty()) {
				qrcodeFound = true;
				break;
			}
			// Forward with 2000 mills
			GeneralMotorConSchedule.getInstance().forward(1000).pauseFor(500);
		}

		while (!map) {

			// Scanning for QRcode and storage in arraylist
			qrcodeScan();

			// Counting total QRcode scanned
			for (int j = 0; j < qrcode.size(); j++) {

				// Checking for Hulahop
				/*if (assign1.updateHulaHoop()) {
					map = true;
					break;
				} else*/ if (qrcode.get(j).equals("P.02")) {
					adjustdrone(j, "P.02");
					GeneralMotorConSchedule.getInstance().hover().pauseFor(5000);
					//GeneralMotorConSchedule.getInstance().spin90Left().pauseFor(5000);
					//GeneralMotorConSchedule.getInstance().spin90Left().pauseFor(5000);
					GeneralMotorConSchedule.getInstance().landing();
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet P.02 og landing");
					// spin 180 grader if QRcode match the exactly wall.
					//GeneralMotorConSchedule.getInstance().spin90Right();
					//GeneralMotorConSchedule.getInstance().spin90Right();
					//forwardControl = true;

				} else if (qrcode.get(j).equals("W00.01")) {
					adjustdrone(j, "W00.01");
					GeneralMotorConSchedule.getInstance().landing();
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet 01 og landing");
					//GeneralMotorConSchedule.getInstance().right();

				} else if (qrcode.get(j).equals("W00.02")) {
					adjustdrone(j, "W00.02");
					GeneralMotorConSchedule.getInstance().landing();
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet 02 og landing");
					// Spin 180 grader
					//GeneralMotorConSchedule.getInstance().spin90Right();
					//GeneralMotorConSchedule.getInstance().spin90Right();
					//forwardControl = true;

				} else if (qrcode.get(j).equals("W00.03")) {
					adjustdrone(j, "W00.03");
					GeneralMotorConSchedule.getInstance().landing();
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet 03 og landing");
					//GeneralMotorConSchedule.getInstance().right();

				} else if (qrcode.get(j).equals("W00.04")) {
					adjustdrone(j, "W00.04");
					//GeneralMotorConSchedule.getInstance().landing();
					// Spin 180 grader
					GeneralMotorConSchedule.getInstance().spin90Right();
					GeneralMotorConSchedule.getInstance().spin90Right();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W01.00")) {
					adjustdrone(j, "W01.00");
					GeneralMotorConSchedule.getInstance().landing();
					// Spin 180 grader
					//GeneralMotorConSchedule.getInstance().spin90Left();
					//GeneralMotorConSchedule.getInstance().spin90Left();

				} else if (qrcode.get(j).equals("W01.01")) {
					adjustdrone(j, "W01.01");
					// Spin 180 grader
					GeneralMotorConSchedule.getInstance().landing();
					//GeneralMotorConSchedule.getInstance().spin90Left();
					//GeneralMotorConSchedule.getInstance().spin90Left();

				} else if (qrcode.get(j).equals("W01.02")) {
					adjustdrone(j, "W01.02");
					// Spin 180 grader
					GeneralMotorConSchedule.getInstance().spin90Left();
					GeneralMotorConSchedule.getInstance().spin90Left();

				} else if (qrcode.get(j).equals("W01.03")) {
					adjustdrone(j, "W01.03");
					// Spin 180 grader
					GeneralMotorConSchedule.getInstance().spin90Left();
					GeneralMotorConSchedule.getInstance().spin90Left();

				} else if (qrcode.get(j).equals("W01.04")) {
					adjustdrone(j, "W01.04");
					// Spin 180 grader
					GeneralMotorConSchedule.getInstance().spin90Left();
					GeneralMotorConSchedule.getInstance().spin90Left();

				} else if (qrcode.get(j).equals("W02.00")) {
					adjustdrone(j, "W02.00");
					GeneralMotorConSchedule.getInstance().spin90Right();
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W02.01")) {
					adjustdrone(j, "W02.01");
					// Spin 180 grader
					GeneralMotorConSchedule.getInstance().spin90Left();
					GeneralMotorConSchedule.getInstance().spin90Left();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W02.02")) {
					adjustdrone(j, "W02.02");
					GeneralMotorConSchedule.getInstance().landing();
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet 02.02");
					//GeneralMotorConSchedule.getInstance().spin90Right();
					//GeneralMotorConSchedule.getInstance().spin90Right();
					//forwardControl = true;
					//GeneralMotorConSchedule.getInstance().left();

				} else if (qrcode.get(j).equals("W02.03")) {
					adjustdrone(j, "W02.03");
					// Spin 180 grader
					GeneralMotorConSchedule.getInstance().spin90Left();
					GeneralMotorConSchedule.getInstance().spin90Left();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W02.04")) {
					adjustdrone(j, "W02.04");
					GeneralMotorConSchedule.getInstance().left();

				} else if (qrcode.get(j).equals("W03.00")) {
					adjustdrone(j, "W03.00");
					GeneralMotorConSchedule.getInstance().spin90Right();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W03.01")) {
					adjustdrone(j, "W03.01");
					GeneralMotorConSchedule.getInstance().spin90Right();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W03.02")) {
					adjustdrone(j, "W03.02");
					GeneralMotorConSchedule.getInstance().spin90Right();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W03.03")) {
					adjustdrone(j, "W03.03");
					//GeneralMotorConSchedule.getInstance().landing();
					//SpaceXGUI.getInstance().appendToConsole(TAG, "fundet 03.03 og landing");
					GeneralMotorConSchedule.getInstance().spin90Right();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W03.04")) {
					adjustdrone(j, "W03.04");
					GeneralMotorConSchedule.getInstance().spin90Right();

				}
			}
			while (forwardControl) {
				forwardCheck();
			}
		}

		// TODO ændre run to fly
		GeneralMotorConSchedule.getInstance().lowerAltitude();
		assign1.flyHulaHoop();

	}
}
