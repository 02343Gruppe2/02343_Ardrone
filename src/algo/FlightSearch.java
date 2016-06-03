package algo;

import java.util.ArrayList;

import core.PicAnal;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;

public class FlightSearch {
	boolean squareFound = false;
	boolean qrcodeFound = false;

	int speed = 10; //speed
	int lgwTime = 1; //long glasswall time
	int sgwTime = 1; //short glasswall time
	int lwbTime = 1; //long whiteboard time
	int swbTime = 1; //short whiteboard time
	int deafault = 500;
	
	IARDrone drone = null;
	CommandManager cmd;
	
	public FlightSearch(IARDrone drone){
		this.drone = drone;
		cmd = drone.getCommandManager();
		PicAnal obj = new PicAnal();
		Object[] res;
		ArrayList<String> square;
		ArrayList<String> qrcode;
		 
			
		for(int i = 0; i < 10; i++){
			cmd.spinLeft(10);
			res = obj.findQRCodes();
			square = (ArrayList<String>)res[1];
			qrcode = (ArrayList<String>)res[0];

				if(!qrcode.isEmpty()){
					qrcodeFound = true;
					squareFound = true;
				}else if(!square.isEmpty()){
					squareFound = true;
				}
			}
		
		
		while(squareFound == false && qrcodeFound == false){
			
			cmd.forward(5).doFor(2000);
			res = obj.findQRCodes();
			square = (ArrayList<String>)res[1];
			qrcode = (ArrayList<String>)res[0];
			
			if(!qrcode.isEmpty()){
				qrcodeFound = true;
				squareFound = true;
			}else if(!square.isEmpty())
				squareFound = true;			
		}
		
		if(qrcodeFound == true){

			res = obj.findQRCodes();
			qrcode = (ArrayList<String>)res[0];
						
			for(int j = 0; j < qrcode.size(); j++){
				
				if(qrcode.get(j).equals("W00.00")){
					
				}else if(qrcode.get(j).equals("W00.01")){
					
				}else if(qrcode.get(j).equals("W00.02")){
					
				}else if(qrcode.get(j).equals("W00.03")){
					
				}else if(qrcode.get(j).equals("W00.04")){
					
				}else if(qrcode.get(j).equals("W01.00")){
					
				}else if(qrcode.get(j).equals("W01.01")){
					
				}else if(qrcode.get(j).equals("W01.02")){
					
				}else if(qrcode.get(j).equals("W01.03")){
					
				}else if(qrcode.get(j).equals("W01.04")){
					
				}else if(qrcode.get(j).equals("W02.00")){
					
				}else if(qrcode.get(j).equals("W02.01")){
					
				}else if(qrcode.get(j).equals("W02.02")){
					
				}else if(qrcode.get(j).equals("W02.03")){
					
				}else if(qrcode.get(j).equals("W02.04")){
					
				}else if(qrcode.get(j).equals("W03.00")){
					
				}else if(qrcode.get(j).equals("W03.01")){
					
				}else if(qrcode.get(j).equals("W03.02")){
					
				}else if(qrcode.get(j).equals("W03.03")){
					
				}else if(qrcode.get(j).equals("W03.04")){
					
				}
			}
			

			cmd.forward(5).doFor(2000);
			res = obj.findQRCodes();
			qrcode = (ArrayList<String>)res[0];
			
			if(!qrcode.isEmpty()){
				
			}
			
		}
	}
}