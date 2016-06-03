package algo;

/**
 * 
 * @author Anders Bækhøj Larsen
 * @author Malte Tais Magnussen
 *
 */

import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.navdata.AttitudeListener;
import de.yadrone.base.navdata.GyroListener;
import de.yadrone.base.navdata.GyroPhysData;
import de.yadrone.base.navdata.GyroRawData;
import de.yadrone.base.navdata.NavDataManager;
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
	// For how long the threads should sleep
	int sleepTime = 1000;
	// A failure margin
	int adjustmentTolerance = 0;
	
	// Instantiate the pictures hula hoop
	Object[] picHulaHops = obj.findHulahops();
	
	// Getting the object array from picture hula hoop
	ArrayList<Object[]> hulaHoop = (ArrayList<Object[]>)picHulaHops[1];
	
	
}
