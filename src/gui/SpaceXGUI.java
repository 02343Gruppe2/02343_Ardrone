package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import utils.FormattedTimeStamp;

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
	private static SpaceXVideoPanel horiVPanel;
	private static SpaceXConsolePanel cPanel;
	private static SpaceXDataPanel dPanel;
	private Dimension dim = new Dimension(1094, 720);
	/**
	 * Private constructor of SpaceXGUI. Only one instance of this must ever exist!<br>
	 * Use {@link #getInstance(String...)} to create or instantiate GUI.
	 *
	 * Note that the optional string parameter only works when GUI is instantiated.
	 * 
	 * @param strings Optional string array for instantiation.
	 */
	private SpaceXGUI(String...strings) {
		setLayout(new MigLayout());
		setMinimumSize(dim);
		setPreferredSize(dim);
		setBackground(Color.decode("#333333"));
		
		//bPanel = new SpaceXButtonPanel();
		horiVPanel = new SpaceXVideoPanel();
		cPanel = new SpaceXConsolePanel(strings);
		dPanel = new SpaceXDataPanel();
		
		add(horiVPanel); // NOTICE Switch layerPanel with vPanel to get video (but no lines)
		add(dPanel, "wrap");
		add(cPanel);
		//add(bPanel);
	}
	
	/**
	 * Will return the current instance of the SpaceX Drone GUI.<br><br>
	 * 
	 * <b>IMPORTANT</b><br>
	 * Only the first time the instance is called should parameters be given.<br>
	 * They'll be used as default text for the console panel: String array type with size 0-*, each entry will be treated as a line.<br>
	 * Any parameters given the second time the instance is called in the same process will be ignored.<br><br>
	 * 
	 * <b>Example</b><br>
	 * Calling new {@link SpaceXGUI#getInstance(String...)} with paramters <code>("A line", "Another line", "A third line")</code> will result in 3 lines added upon instantiating the console, like:<br><br>
	 * 
	 * A line<br>
	 * Another line<br>
	 * A third line
	 * 
	 * @param strings Array of strings to be used as default text in console window.
	 * @return the current GUI instance.
	 * @see {@link FormattedTimeStamp#getTime()} if timestamps are needed.
	 */
	public static SpaceXGUI getInstance(String...strings) {
		if(instance == null) {
			instance = new SpaceXGUI(strings);
			
			SwingUtilities.invokeLater(new Runnable() {
		        public void run() {
					// Create window
					JFrame f = new JFrame("Space X");
					f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					f.setBackground(Color.decode("#333333"));
					f.setResizable(false);
					f.setPreferredSize(new Dimension((1094), (720)));
		
			        // Create the content pane
			        JComponent c = SpaceXGUI.getInstance();
			        c.setOpaque(false);
			        f.setContentPane(c);
		
			        // Draw the window
			        f.pack();
			        f.setLocationRelativeTo(null);
			        f.setVisible(true);
				}
            });
        }
		
		return instance;
	}
	
	// Available GUI commands
	
	/**
	 * Updates the image shown in the GUI.
	 * 
	 * @param newImage New image given as type BufferedImage
	 */
	public static void updateImage(BufferedImage newImage, Boolean isFront) {
		horiVPanel.imageUpdated(newImage, isFront);
		dPanel.setImageWidth(newImage.getWidth());
		dPanel.setImageHeight(newImage.getHeight());
		dPanel.incrementImageNumber();
		dPanel.updateBattery();
		dPanel.updateData();
	}
	
	public SpaceXVideoPanel getVPanel() {
		return horiVPanel;
	}
	
	public SpaceXButtonPanel getBPanel(){
		return bPanel;
	}
	
	/**
	 * Add a new line to the command window.
	 * 
	 * @param s Message to be appended.
	 */
	public void appendToConsole(String...strings) {
		cPanel.appendText(strings);
	}
	
	/**
	 * Clears the console.
	 */
	public void clearConsole() {
		cPanel.clearPanel();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}
