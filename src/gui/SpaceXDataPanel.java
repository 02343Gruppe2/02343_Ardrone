package gui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class SpaceXDataPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public SpaceXDataPanel() {
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(240, 480));
		setBorder(BorderFactory.createTitledBorder("NavData"));
		// TODO
	}
}
