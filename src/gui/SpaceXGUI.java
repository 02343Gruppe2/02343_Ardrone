package gui;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class SpaceXGUI extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static SpaceXGUI instance = null;
	private SpaceXButtonPanel bPanel;
	private SpaceXVideoPanel vPanel;
	private SpaceXConsolePanel cPanel;
	private SpaceXDataPanel dPanel;
	
	private SpaceXGUI() {
		setLayout(new MigLayout());
		bPanel = new SpaceXButtonPanel();
		vPanel = new SpaceXVideoPanel();
		cPanel = new SpaceXConsolePanel();
		dPanel = new SpaceXDataPanel();
		
		add(vPanel);
		add(dPanel, "wrap");
		add(cPanel);
		add(bPanel);
	}
	
	public static SpaceXGUI getInstance() {
		if(instance == null)
			instance = new SpaceXGUI();
		
		return instance;
	}
}
