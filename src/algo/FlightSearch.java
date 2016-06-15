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

	private final String TAG = "FlightSearch";

	// Instantiate picture analyze
	ImgProc obj = new ImgProc();
	// Instantiate Assignment 1
	Assignment1 assign1 = new Assignment1();

	// Instantiate Object for qrcode
	Object[] res;
	ArrayList<String> qrcode = null;

	/*
	 * String[][] grid = new String[963][1078];
	 * 
	 * public void FlyMapping() {
	 * 
	 * // Wall 0 grid[188][23] = "W000.00"; grid[338][28] = "W00.01";
	 * grid[515][23] = "W00.02"; grid[694][28] = "W00.03"; grid[840][23] =
	 * "W00.04";
	 * 
	 * // Wall 1 grid[926][115] = "W01.00"; grid[926][324] = "W01.01";
	 * grid[926][566] = "W01.02"; grid[926][721] = "W01.03"; grid[926][904] =
	 * "W01.04";
	 * 
	 * // Wall 2 grid[847][1064] = "W02.00"; grid[656][995] = "W02.01";
	 * grid[420][1070] = "W02.02"; grid[350][1070] = "W02.03"; grid[150][1070] =
	 * "W02.04";
	 * 
	 * // Wall 3 grid[10][997] = "W03.00"; grid[10][740] = "W03.01";
	 * grid[10][561] = "W03.02"; grid[10][357] = "W03.03"; grid[10][108] =
	 * "W03.04"; }
	 */

	// QRcode scanner metode.
	public void qrcodeScan() {
		res = obj.findQRCodes();
		qrcode = (ArrayList<String>) ((Object[]) res[0])[0];
	}

	// Metode for forward check.
	public void forwardCheck() {
		qrcodeScan();

		for (int g = 0; g < qrcode.size(); g++) {
			if (assign1.updateHulaHoop()) {
				forwardControl = false;
				map = true;
				break;

			} else if (qrcode.get(g).contains("W")) {
				forwardControl = false;
				break;
			}
		}
		GeneralMotorCon.getInstance().forward(2000);
	}

	public void adjustdrone(int j, String currentQr) {
		int errormargin = 100, rectangleWidth = 180;
		boolean isyadjusted = false, isxadjusted = false, newQRCode = true, distance = false;

		int scanCounter = 0;
		while ((!isyadjusted || !isxadjusted || !distance) && scanCounter < 5) {
			if (newQRCode) {
				Rect rect = ((ArrayList<Rect>) ((Object[]) res[0])[1]).get(j);
				double x = ((rect.br().x - rect.tl().x) / 2) + rect.tl().x,
						y = ((rect.br().y - rect.tl().y) / 2) + rect.tl().y;
				double[] displacedCoordinates = ImgProc.coordinateDisplacement(x, y);
				x = displacedCoordinates[0];
				y = displacedCoordinates[1];

				SpaceXGUI.getInstance().appendToConsole(TAG, "x: " + x + " y: " + y);
				if (x > errormargin) {
					GeneralMotorConSchedule.getInstance().right().pauseFor(2000);
				} else if (x < -errormargin) {
					GeneralMotorConSchedule.getInstance().left().pauseFor(2000);
				} else {
					isxadjusted = true;
				}
				if (y > errormargin) {
					GeneralMotorConSchedule.getInstance().raiseAltitude().pauseFor(2000);
				} else if (y < -errormargin) {
					GeneralMotorConSchedule.getInstance().lowerAltitude().pauseFor(2000);
				} else {
					isyadjusted = true;
				}
				SpaceXGUI.getInstance().appendToConsole(TAG, "rect str: " + rect.width);
				if (rect.width < rectangleWidth) {
					GeneralMotorConSchedule.getInstance().forward(100);
				} else {
					distance = true;
				}
			}
			qrcodeScan();

			scanCounter++;
			newQRCode = false;
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

		for (int i = 0; i < 10; i++) {
			qrcodeScan();

			if (assign1.updateHulaHoop()) {
				qrcodeFound = true;
				map = true;
				break;
			} else if (!qrcode.isEmpty()) {
				qrcodeFound = true;
				break;
			}
			GeneralMotorCon.getInstance().spin90Left();
		}

		while (!qrcodeFound) { // Scan for qrcode.
			qrcodeScan();

			// Checking for Hulahop found
			if (assign1.updateHulaHoop()) {
				qrcodeFound = true;
				map = true;
				break;
			} else if (!qrcode.isEmpty()) {
				qrcodeFound = true;
				break;
			}
			// Forward with 2000 mills
			GeneralMotorCon.getInstance().forward(2000);
		}

		while (!map) {

			// Scanning for QRcode and storage in arraylist
			qrcodeScan();

			// Counting total QRcode scanned
			for (int j = 0; j < qrcode.size(); j++) {

				// Checking for Hulahop
				if (assign1.updateHulaHoop()) {
					map = true;
					break;
				} else if (qrcode.get(j).equals("W00.00")) {
					adjustdrone(j, "W00.00");
					//GeneralMotorConSchedule.getInstance().landing();
					// spin 180 grader if QRcode match the exactly wall.
					GeneralMotorCon.getInstance().spin90Right();
					GeneralMotorCon.getInstance().spin90Right();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W00.01")) {
					adjustdrone(j, "W00.01");
					//GeneralMotorConSchedule.getInstance().landing();
					GeneralMotorCon.getInstance().right();

				} else if (qrcode.get(j).equals("W00.02")) {
					adjustdrone(j, "W00.02");
					//GeneralMotorConSchedule.getInstance().landing();
					//SpaceXGUI.getInstance().appendToConsole(TAG, "fundet 02 og landing");
					// Spin 180 grader
					GeneralMotorCon.getInstance().spin90Right();
					GeneralMotorCon.getInstance().spin90Right();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W00.03")) {
					adjustdrone(j, "W00.03");
					//GeneralMotorConSchedule.getInstance().landing();
					SpaceXGUI.getInstance().appendToConsole(TAG, "fundet 03 og landing");
					GeneralMotorCon.getInstance().right();

				} else if (qrcode.get(j).equals("W00.04")) {
					adjustdrone(j, "W00.04");
					//GeneralMotorConSchedule.getInstance().landing();
					// Spin 180 grader
					GeneralMotorCon.getInstance().spin90Right();
					GeneralMotorCon.getInstance().spin90Right();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W01.00")) {
					adjustdrone(j, "W01.00");
					// Spin 180 grader
					GeneralMotorCon.getInstance().spin90Left();
					GeneralMotorCon.getInstance().spin90Left();

				} else if (qrcode.get(j).equals("W01.01")) {
					adjustdrone(j, "W01.01");
					// Spin 180 grader
					GeneralMotorCon.getInstance().spin90Left();
					GeneralMotorCon.getInstance().spin90Left();

				} else if (qrcode.get(j).equals("W01.02")) {
					adjustdrone(j, "W01.02");
					// Spin 180 grader
					GeneralMotorCon.getInstance().spin90Left();
					GeneralMotorCon.getInstance().spin90Left();

				} else if (qrcode.get(j).equals("W01.03")) {
					adjustdrone(j, "W01.03");
					// Spin 180 grader
					GeneralMotorCon.getInstance().spin90Left();
					GeneralMotorCon.getInstance().spin90Left();

				} else if (qrcode.get(j).equals("W01.04")) {
					adjustdrone(j, "W01.04");
					// Spin 180 grader
					GeneralMotorCon.getInstance().spin90Left();
					GeneralMotorCon.getInstance().spin90Left();

				} else if (qrcode.get(j).equals("W02.00")) {
					adjustdrone(j, "W02.00");
					GeneralMotorCon.getInstance().spin90Right();
					forwardControl = true;
					
				} else if (qrcode.get(j).equals("W02.01")) {
					adjustdrone(j, "W02.01");
					// Spin 180 grader
					GeneralMotorCon.getInstance().spin90Left();
					GeneralMotorCon.getInstance().spin90Left();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W02.02")) {
					adjustdrone(j, "W02.02");
					GeneralMotorCon.getInstance().left();

				} else if (qrcode.get(j).equals("W02.03")) {
					adjustdrone(j, "W02.03");
					// Spin 180 grader
					GeneralMotorCon.getInstance().spin90Left();
					GeneralMotorCon.getInstance().spin90Left();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W02.04")) {
					adjustdrone(j, "W02.04");
					GeneralMotorCon.getInstance().left();

				} else if (qrcode.get(j).equals("W03.00")) {
					adjustdrone(j, "W03.00");
					GeneralMotorCon.getInstance().spin90Right();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W03.01")) {
					adjustdrone(j, "W03.01");
					GeneralMotorCon.getInstance().spin90Right();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W03.02")) {
					adjustdrone(j, "W03.02");
					GeneralMotorCon.getInstance().spin90Right();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W03.03")) {
					adjustdrone(j, "W03.03");
					//GeneralMotorConSchedule.getInstance().landing();
					//SpaceXGUI.getInstance().appendToConsole(TAG, "fundet 03.03 og landing");
					GeneralMotorCon.getInstance().spin90Right();
					forwardControl = true;

				} else if (qrcode.get(j).equals("W03.04")) {
					adjustdrone(j, "W03.04");
					GeneralMotorCon.getInstance().spin90Right();

				}
			}
			while (forwardControl) {
				forwardCheck();
			}
		}

		// TODO ændre run to fly
		GeneralMotorCon.getInstance().lowerAltitude();
		assign1.flyHulaHoop();

	}
}
