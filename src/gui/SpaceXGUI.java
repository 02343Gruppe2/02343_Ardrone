package gui;

public class SpaceXGUI {
	private static SpaceXGUI instance = null;
	
	private SpaceXGUI() {
		// Do shiiiieeeet
	}
	
	public static SpaceXGUI getInstance() {
		if(instance == null)
			instance = new SpaceXGUI();
		
		return instance;
	}
}
