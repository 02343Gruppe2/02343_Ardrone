package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
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
	private static SpaceXVideoPanel vPanel;
	private static SpaceXConsolePanel cPanel;
	private static SpaceXDataPanel dPanel;
	private static SpaceXLinesPanel lPanel;
	
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
		setMinimumSize(new Dimension((640+240), (480+240)));
		setPreferredSize(new Dimension((640+240), (480+240)));
		setBackground(Color.decode("#333333"));
		
		bPanel = new SpaceXButtonPanel();
		vPanel = new SpaceXVideoPanel();
		cPanel = new SpaceXConsolePanel(strings);
		dPanel = new SpaceXDataPanel();
		lPanel = new SpaceXLinesPanel();
		
		JPanel layerPanel = new JPanel();
		layerPanel.setLayout(new MigLayout());
		layerPanel.setPreferredSize(new Dimension(640,480));
		
		JLayeredPane lpPanel = new JLayeredPane();
		lpPanel.setPreferredSize(new Dimension(640,480));
		
		lpPanel.add(vPanel, new Integer(0), 0);
		lpPanel.add(lPanel, new Integer(1), 0);
		
		layerPanel.add(lpPanel);
		
		/*
		lPanel.setSize(lPanel.getSize());
		lPanel.setBounds(0,0,lPanel.getWidth(),lPanel.getHeight());
		lPanel.setLocation(0,0);
		vPanel.setSize(lPanel.getSize());
		vPanel.setBounds(0,0,lPanel.getWidth(),lPanel.getHeight());
		vPanel.setLocation(0,0);
		*/
		
		add(layerPanel); // NOTICE Switch layerPanel with vPanel to get video (but no lines)
		add(dPanel, "wrap");
		add(cPanel);
		add(bPanel);
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
					f.setPreferredSize(new Dimension((640+240), (480+180)));
		
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
		dPanel.setImageWidth(newImage.getWidth());
		dPanel.setImageHeight(newImage.getHeight());
		dPanel.incrementImageNumber();
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
