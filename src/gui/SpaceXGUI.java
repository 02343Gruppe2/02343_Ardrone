package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * GUI designed for SpaceX Drone 5 3/8
 * 
 * @author Kristin Hansen
 *
 */

public class SpaceXGUI extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static SpaceXGUI instance = null;
	private static SpaceXButtonPanel bPanel;
	private static SpaceXVideoPanel vPanel;
	private static SpaceXConsolePanel cPanel;
	private static SpaceXDataPanel dPanel;
	
	private SpaceXGUI() {
		setLayout(new MigLayout());
		setMinimumSize(new Dimension(100, 100));
		
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
		if(instance == null) {
			instance = new SpaceXGUI();
			
			// Create window
			JFrame f = new JFrame("Space X");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setBackground(Color.decode("#333333"));
			f.setResizable(false);
			f.setPreferredSize(new Dimension((640+240), (480+180)));

	        // Create the content pane
	        JComponent c = SpaceXGUI.getInstance();
	        c.setOpaque(false);
	        f.setContentPane(c);

	        // Draw the window
	        f.pack();
	        f.setVisible(true);
		}
		
		return instance;
	}
	
	// Available GUI commands
	/**
	 * Updates the image shown in the GUI.
	 * 
	 * @param path Path to new image
	 */
	public static void updateImage(String path) {
		BufferedImage newImage = null;
		
		try {
			File img = new File(path);
			BufferedImage in = ImageIO.read(img);
			
			newImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2d = newImage.createGraphics();
			g2d.drawImage(in, 0, 0, null);
			g2d.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		vPanel.imageUpdated(newImage);
		dPanel.incrementImageNumber();
		dPanel.setImageHeight(newImage.getHeight());
		dPanel.setImageWidth(newImage.getWidth());
	}
	
	/**
	 * Updates the image shown in the GUI.
	 * 
	 * @param newImage New image given as type BufferedImage
	 */
	public static void updateImage(BufferedImage newImage) {
		vPanel.imageUpdated(newImage);
		dPanel.incrementImageNumber();
	}
	
	public static void updateData() {
		dPanel.updateData();
	}
	
	public SpaceXButtonPanel getBPanel(){
		return bPanel;
	}
	
	/**
	 * Add a new line to the command window.
	 * 
	 * @param s Message to be appended.
	 */
	public void appendToConsole(String s) {
		cPanel.getTxtArea().append("[TIMESTAMP] " + s + "\n");
	}
	
	/**
	 * Clears the console.
	 */
	public void clearConsole() {
		cPanel.getTxtArea().removeAll();
	}
}
