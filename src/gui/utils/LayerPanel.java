package gui.utils;

import javax.swing.JPanel;

public class LayerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JPanel[] comps;
	
	public LayerPanel(JPanel[] comps) {
		this.comps = comps;
		
		System.out.println(this.comps.toString());
	}
}
