package gui;

import javax.swing.JPanel;

public class SpaceXGUI extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static SpaceXGUI instance = null;
	private SpaceXControlPanel cPanel;
	private SpaceXVideoPanel vPanel;
	
	private SpaceXGUI() {
		cPanel = new SpaceXControlPanel();
		vPanel = new SpaceXVideoPanel();
		
		add(cPanel);
		add(vPanel);
	}
	
	public static SpaceXGUI getInstance() {
		if(instance == null)
			instance = new SpaceXGUI();
		
		return instance;
	}
}
