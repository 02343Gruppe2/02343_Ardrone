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
	boolean revert = false;
	
	int errormargin = 0, rectangleWidth = 160;
	long imgNumber = SpaceXGUI.getInstance().getImageNumber();
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
		
		//scan qrcode
		qrcodeScan();

		// count size of qrcode scanned
		for (int g = 0; g < qrcode.size(); g++) {
			
			// Checking if hula hoop ring found.
			if (assign1.checkQRCode(qrcode)) {
				SpaceXGUI.getInstance().appendToConsole(TAG, "Fundet en mulig hulahoop ring");
				SpaceXGUI.getInstance().appendToConsole(TAG, "qrcode: " + qrcode + assign1.checkQRCode(qrcode));
				forwardControl = false;
				map = true;
				break;

			} else if (qrcode.get(g).contains("W")) {
				GeneralMotorConSchedule.getInstance().backward(500).pauseFor(1000);
				forwardControl = false;
				break;
			}
		}
		//Forward 500 ms and pause for 500 ms.
		GeneralMotorConSchedule.getInstance().forward(500).pauseFor(1000);
	}
	
	//Spin 180 metode
	public void spin180(){
		
		for(int i = 0; i < 2; i++){
			GeneralMotorConSchedule.getInstance().spin90Right().pauseFor(3000);
			GeneralMotorConSchedule.getInstance().hover().pauseFor(1000);
		}
			
	}
	
	// Adjust drone position metode
	public void adjustdrone(int j, String currentQr) {
		
		//counter for scanning (50 times)
		int scanCounter = 0;
		while ((!adjust) && scanCounter < 50) {
			if (newQRCode) {
				
				//Get rect from arraylist and calculate the new x and y.
				Rect rect = ((ArrayList<Rect>) ((Object[]) res[0])[1]).get(j);
				double x = ((rect.br().x - rect.tl().x) / 2) + rect.tl().x,
						y = ((rect.br().y - rect.tl().y) / 2) + rect.tl().y;
				double[] displacedCoordinates = ImgProc.coordinateDisplacement(x, y);
				x = displacedCoordinates[0];
				y = displacedCoordinates[1];
				
				SpaceXGUI.getInstance().appendToConsole(TAG, "rect str: " + rect.width);
				SpaceXGUI.getInstance().appendToConsole(TAG, "x: " + x + " y: " + y);
				
				//Tolerance for adjust the dronen position.
				errormargin = (int)(600*Math.pow(0.9886, rect.width));
				SpaceXGUI.getInstance().appendToConsole(TAG, "Erromargin: " + errormargin);
				
				//Check horizontal adjustment
				if (x < errormargin && x > -errormargin ) {
					//Check vertical adjustment
					if(y < errormargin && y > -errormargin){
						//Check if rect width less then rectangleWidth and forward a bit.
						if(rect.width < rectangleWidth){
							GeneralMotorConSchedule.getInstance().forward(500).pauseFor(1000);
						}else{
							GeneralMotorConSchedule.getInstance().backward(500).pauseFor(1000);
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
			
			GeneralMotorConSchedule.getInstance().pauseFor(200);
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
	
	public void search() {
		// spin 10 times and scan for hulahop and qrcode

	/*	for (int i = 0; i < 4; i++) {
			qrcodeScan();
			SpaceXGUI.getInstance().appendToConsole(TAG, "qrcode:" + qrcode);
		
			if (assign1.checkQRCode(qrcode)) {
				SpaceXGUI.getInstance().appendToConsole(TAG, "Fundet en mulig hulahoop ring");
				SpaceXGUI.getInstance().appendToConsole(TAG, "qrcode: " + qrcode + assign1.checkQRCode(qrcode));
				qrcodeFound = true;
				map = true;
				break;
							
			} else if (!qrcode.isEmpty()) {
				SpaceXGUI.getInstance().appendToConsole(TAG, "Der er en qrcode");
				qrcodeFound = true;
				break;
			}
			GeneralMotorConSchedule.getInstance().spin90Right().pauseFor(3000);
			GeneralMotorConSchedule.getInstance().hover().pauseFor(1000);
		}

		while (!qrcodeFound) { // Scan for qrcode..
			SpaceXGUI.getInstance().appendToConsole(TAG, "Spinnet 360 grader men har ikke fundet noget");
			qrcodeScan();

			// Checking for Hulahop found
			if (assign1.checkQRCode(qrcode)) {
				SpaceXGUI.getInstance().appendToConsole(TAG, "Fundet en mulig hulahoop ring");
				qrcodeFound = true;
				map = true;
				break;
				
			} else if (!qrcode.isEmpty()) {
				qrcodeFound = true;
				break;
			}
			// Forward with 500 mills and pause 1 second after.
			GeneralMotorConSchedule.getInstance().forward(500).pauseFor(1000);
		} */

		while (!map) {

			// Scanning for QRcode and storage in arraylist
			qrcodeScan();
			SpaceXGUI.getInstance().appendToConsole(TAG, "!MAP QRcode " + qrcode);
			// Counting total QRcode scanned
			for (int j = 0; j < qrcode.size(); j++) {

				// Checking for Hulahop
				if (assign1.checkQRCode(qrcode)) {
					SpaceXGUI.getInstance().appendToConsole(TAG, "Fundet en mulig hulahoop ring");
					SpaceXGUI.getInstance().appendToConsole(TAG, "qrcode: " + qrcode + assign1.checkQRCode(qrcode));
					map = true;
					break; 
					
				} else if (qrcode.get(j).equals("W00.00")) {
					//adjustdrone(j, "W00.00");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W00.00");
					GeneralMotorConSchedule.getInstance().spin90Right().pauseFor(3000);
					GeneralMotorConSchedule.getInstance().hover().pauseFor(1000);
					forwardControl = true;

				} else if (qrcode.get(j).equals("W00.01")) {
					//adjustdrone(j, "W00.01");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W00.01");
					GeneralMotorConSchedule.getInstance().spin90Right().pauseFor(3000);
					GeneralMotorConSchedule.getInstance().hover().pauseFor(1000);
					forwardControl = true;

				} else if (qrcode.get(j).equals("W00.02") && !revert) {
					adjustdrone(j, "W00.02");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W00.02");
					spin180();
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W00.02") && revert){
					adjustdrone(j, "W00.02");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet revert W00.02");
					SpaceXGUI.getInstance().appendToConsole(TAG, "Dronen Landing");
					GeneralMotorConSchedule.getInstance().landing();
					forwardControl = false;

				} else if (qrcode.get(j).equals("W00.03") && !revert) {
					adjustdrone(j, "W00.03");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W00.03");
					GeneralMotorConSchedule.getInstance().right(1000).pauseFor(200);
					forwardControl = false;
					
				} else if (qrcode.get(j).equals("W00.03") && revert){
					adjustdrone(j, "W00.03");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W00.03");
					spin180();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W00.04") && !revert) {
					adjustdrone(j, "W00.04");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W00.04");
					spin180();
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W00.04") && revert){
					adjustdrone(j, "W00.04");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W00.04");
					GeneralMotorConSchedule.getInstance().left(1000).pauseFor(200);
					forwardControl = false;
					
				} else if (qrcode.get(j).equals("W01.00")) {
					adjustdrone(j, "W01.00");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W01.00");
					GeneralMotorConSchedule.getInstance().spin90Right().pauseFor(3000);
					GeneralMotorConSchedule.getInstance().hover().pauseFor(1000);
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W01.01")) {
					adjustdrone(j, "W01.01");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W01.01");
					GeneralMotorConSchedule.getInstance().spin90Right().pauseFor(3000);
					GeneralMotorConSchedule.getInstance().hover().pauseFor(1000);
					forwardControl = true;

				} else if (qrcode.get(j).equals("W01.02")) {
					adjustdrone(j, "W01.02");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W01.02");
					GeneralMotorConSchedule.getInstance().spin90Right().pauseFor(3000);
					GeneralMotorConSchedule.getInstance().hover().pauseFor(1000);
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W01.03")) {
					adjustdrone(j, "W01.03");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W01.03");
					GeneralMotorConSchedule.getInstance().spin90Right().pauseFor(3000);
					GeneralMotorConSchedule.getInstance().hover().pauseFor(1000);
					forwardControl = true;

				} else if (qrcode.get(j).equals("W01.04")) {
					adjustdrone(j, "W01.04");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W01.04");
					GeneralMotorConSchedule.getInstance().spin90Right().pauseFor(3000);
					GeneralMotorConSchedule.getInstance().hover().pauseFor(1000);
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W02.00") && !revert) {
					adjustdrone(j, "W02.00");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W02.00");
					spin180();
					forwardControl = true;
					revert = true;					
					
				} else if (qrcode.get(j).equals("W02.01") && !revert) {
					adjustdrone(j, "W02.01");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W02.01");
					spin180();
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W02.01") && revert){
					adjustdrone(j, "W02.01");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W02.01");
					GeneralMotorConSchedule.getInstance().right(1000).pauseFor(200);
					forwardControl = false;
					
				} else if (qrcode.get(j).equals("W02.02") && !revert) {
					adjustdrone(j, "W02.02");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W02.02");
					GeneralMotorConSchedule.getInstance().left(1000).pauseFor(200);
					forwardControl = false;
					
				} else if (qrcode.get(j).equals("W02.02") && revert){
					adjustdrone(j, "W02.02");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W02.02");
					spin180();
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W02.03")) {
					//adjustdrone(j, "W02.03");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W02.03");
					GeneralMotorConSchedule.getInstance().spin90Left().pauseFor(3000);
					GeneralMotorConSchedule.getInstance().hover().pauseFor(1000);
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W02.04")) {
					//adjustdrone(j, "W02.04");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W02.04");
					GeneralMotorConSchedule.getInstance().spin90Left().pauseFor(3000);
					GeneralMotorConSchedule.getInstance().hover().pauseFor(1000);
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W03.00")) {
					//adjustdrone(j, "W03.00");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W03.00");
					spin180();
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W03.01")) {
					//adjustdrone(j, "W03.01");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W03.01");
					spin180();
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W03.02")) {
					//adjustdrone(j, "W03.02");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W03.02");
					spin180();
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W03.03")) {
					//adjustdrone(j, "W03.03");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W03.03");
					spin180();
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W03.04")) {
					//adjustdrone(j, "W03.04");
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet W03.04");
					spin180();
					forwardControl = true;
					
				}
			}
			while (forwardControl) {
				forwardCheck();
			}
		}

		// TODO ændre run to fly
		SpaceXGUI.getInstance().appendToConsole(TAG, "Assign1 tager over");
		assign1.flyHulaHoop();

	}
}
