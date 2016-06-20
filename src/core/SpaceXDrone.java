package core;

import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import gui.SpaceXGUI;

import org.opencv.core.Core;

import de.yadrone.apps.controlcenter.plugins.battery.BatteryPanel;
import de.yadrone.apps.paperchase.controller.PaperChaseKeyboardController;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;
import de.yadrone.base.navdata.BatteryListener;
import de.yadrone.base.video.ImageListener;
import algo.Assignment1;
import algo.FlightSearch;
import algo.GeneralMotorCon;
import algo.GeneralMotorConSchedule;


public class SpaceXDrone {
	FlightSearch fs;
	Assignment1 assig1;
	Boolean isFront;
	Boolean imageIsReady;
	int batteryPercentage;
	int voltagePercentage;
	final private String TAG = "SpaceX";
	
	public void swapCamera(IARDrone drone) {
		imageIsReady = false;
		if(isFront) {
			isFront = false;
			drone.getCommandManager().setVideoChannel(VideoChannel.LARGE_HORI_SMALL_VERT);
		} else {
			isFront = true;
			drone.getCommandManager().setVideoChannel(VideoChannel.LARGE_VERT_SMALL_HORI);
		}
	}
	
	public SpaceXDrone() {
		// We instantiate a null-object with the ARDrone interface
		ARDrone drone = null;
		boolean running = false;
		isFront = true;
		imageIsReady = true;
		
		
		try {
			SpaceXGUI.getInstance("Welcome to SpaceX Drone GUI");
			// Create instance of new ARDrone
			drone = new ARDrone();
			
			drone.addExceptionListener(new IExceptionListener() {
				public void exeptionOccurred(ARDroneException e) {
					System.err.println("Drone exception: " + e.getMessage());
					SpaceXGUI.getInstance().appendToConsole(TAG ,"Critical drone error: " + e.getMessage());
				}
			});
			// Start the drone managers (NavData, CommandManager etc.)
			drone.start();
			drone.reset();
			drone.getCommandManager().setVideoCodec(VideoCodec.H264_720P);
			drone.getCommandManager().setVideoChannel(VideoChannel.LARGE_HORI_SMALL_VERT);
			drone.getVideoManager().addImageListener(new ImageListener() {
				@Override
	            public void imageUpdated(BufferedImage newImage) {
					if(imageIsReady) {

		            	SpaceXGUI.updateImage(newImage, isFront);
					}
	            }
	        });
			
			drone.getNavDataManager().addBatteryListener(new powerLevel());
			running = true;
			Thread.sleep(10000);
			assig1 = new Assignment1();
			fs = new FlightSearch();
			

			// keyboard controller is always enabled and cannot be disabled (for safety reasons)
			PaperChaseKeyboardController keyboardController = new PaperChaseKeyboardController(drone);
			keyboardController.start();
			
			/*
			GeneralMotorConSchedule.getInstance().setDrone(drone);
			GeneralMotorConSchedule.getInstance().takeoff();
			GeneralMotorConSchedule.getInstance().forward(2000).doFor(2000);
			GeneralMotorConSchedule.getInstance().right();
			GeneralMotorConSchedule.getInstance().left();
			GeneralMotorConSchedule.getInstance().landing();
			*/
			GeneralMotorConSchedule.getInstance().setDrone(drone);
			GeneralMotorConSchedule.getInstance().takeoff();
			for (int i = 0; i < 5; i++) {
				GeneralMotorConSchedule.getInstance().raiseAltitude().pauseFor(500);
			}
			
			//GeneralMotorConSchedule.getInstance().hover();
			//GeneralMoto.ConSchedule.getInstance().pauseFor(5000);
			//GeneralMotorConSchedule.getInstance().spinLeft();
			//GeneralMotorConSchedule.getInstance().cycleLeft();
			//GeneralMotorConSchedule.getInstance().pauseFor(5000);
			//GeneralMotorConSchedule.getInstance().landing();
			
			
			//fs.afstand();	
			fs.search();
			//assig1.flyHulaHoop();
			
			/*GeneralMotorCon.getInstance().setDrone(drone);
			GeneralMotorCon.getInstance().takeoff();
			GeneralMotorCon.getInstance().spin90Left();
			GeneralMotorCon.getInstance().raiseAltitude();
			GeneralMotorCon.getInstance().spin90Right();
			GeneralMotorCon.getInstance().waitFor(5000);
			GeneralMotorCon.getInstance().landing(); 
			*/
			
			
			int counter = 0;
			ImgProc imgobj = new ImgProc();
			Thread.sleep(5000);
			while(running) {
				imgobj.findHulaHoops();
				counter++;
				//Thread.sleep(200);
				if(counter > 1000000)
					running = false;
				
				
			}
			
			//infinite loop should go here
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// We're done using the drone, now close everything
			// If drone was actually instantiated and started, close all services
			if (drone != null)
				drone.getCommandManager().landing().doFor(2000);
			
			drone.stop();
			// Exit local program
			//System.exit(0);
		}
	}
	
	// Run this shiiiieeeeet
	public static void main(String[] args) {
		new SpaceXDrone();
	}
	
	private class powerLevel implements BatteryListener{

		@Override
		public void batteryLevelChanged(int arg0) {
			batteryPercentage = arg0;
			
			GeneralMotorCon.getInstance().setBatLvl(arg0);
			
			//SpaceXGUI.getInstance().appendToConsole("\n" + batteryPercentage + "Battery");
			
			
		}

		@Override
		public void voltageChanged(int arg0) {
			voltagePercentage = arg0;
			
		//	SpaceXGUI.getInstance().appendToConsole("\n" + voltagePercentage  + "Voltage");
			
			
		}
		
		
	}
}


