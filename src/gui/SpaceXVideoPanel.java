package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class SpaceXVideoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private BufferedImage image = null;
	
	/**
	 * Constructor for GUI's video panel (Spycam). Use {@link #imageUpdated(BufferedImage)} to update image.
	 */
	public SpaceXVideoPanel() {
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(640, 480));
		
		// Add a placeholder image...
		try {
			File img = new File("materials/bufferImage0.png");
			BufferedImage in = ImageIO.read(img);
			
			image = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2d = image.createGraphics();
			g2d.drawImage(in, 0, 0, null);
			g2d.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates the Spycam image.
	 * 
	 * @param newImage New image to be painted.
	 */
	public void imageUpdated(BufferedImage newImage) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				image = newImage;
				
				repaint();
			}
		});
    }
	
	@Override
	public synchronized void paintComponent(Graphics g) {
		super.paintComponent(g);
		
        if (image != null)
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    }
}
