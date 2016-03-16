package core;

public class FlightAlgo {
	
	public void assignment1 () {
		switch(hula_hoop()){
		case ((hula_hoop[0]+hula_hoop)>0):
			//flyv ned
			break;
		case ((hula_hoop[0]+hula_hoop)<0):
			//flyv op
			break;
		case ((hula_hoop[2]+hula_hoop[3])>0):
			//fly venstre
			break;
		case ((hula_hoop[2]+hula_hoop[3])<0):
			//flyv højre
			break;
		default: break;
		}
	}
	
	public void assignment2() {
		
	}
	
	public void assignment3() {
		
	}

	int [] hula_hoop (int high, int low, int right, int left) {
		int [] tmp = new int[4];
		
		tmp[0] = high;
		tmp[1] = low;
		tmp[2] = right;
		tmp[3] = left;
		
		return tmp;
	}
	
}

