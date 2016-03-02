package gui.test;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JFrame;

import gui.SpaceXGUI;

public class GUITest {
	public GUITest() {
		createAndShowGUI();
	}
	
	public static void main(String[] args) {
		new GUITest();
	}
	
	public void createAndShowGUI() {
		// Create window
		JFrame f = new JFrame("Space X");
		
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setBackground(Color.decode("#333333"));
		f.setResizable(false);

        // Create the content pane
        JComponent c = SpaceXGUI.getInstance();
        
        c.setOpaque(false);
        f.setContentPane(c);

        // Draw the window
        f.pack();
        f.setVisible(true);
	}
}
