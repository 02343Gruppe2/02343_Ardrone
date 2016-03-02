package gui;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class SpaceXVideoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public SpaceXVideoPanel() {
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(640, 480));
		setBorder(BorderFactory.createTitledBorder("Spycam"));
		
		// TODO Need to load image from drone and render here...
	}
	
	public void newImage(Image i) {
		// TODO Maybe send image via this method and call repaint
	}
}
