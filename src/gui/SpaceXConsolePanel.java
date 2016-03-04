package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Kristin Hansen
 *
 */
public class SpaceXConsolePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JTextArea txtArea;
	
	public SpaceXConsolePanel() {
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(640, 180));
		setBorder(BorderFactory.createTitledBorder("Console"));
		
		txtArea = new JTextArea("Initializing...");
		txtArea.setPreferredSize(new Dimension(230,480));
		txtArea.setLineWrap(true);
		txtArea.setWrapStyleWord(true);
		txtArea.setEditable(false);
		txtArea.setBackground(Color.decode("#EEEEEE"));
		
		add(txtArea);
	}
}
