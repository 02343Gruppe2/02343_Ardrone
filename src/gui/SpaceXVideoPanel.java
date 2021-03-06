package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

/**
 * VideoPanel containing method to update image, {@link #imageUpdated(BufferedImage)} for usage.<br>
 * Dimensions have been set as 640x480, 
 * 
 * @author Kristin Hansen
 *
 */

public class SpaceXVideoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private BufferedImage frontImage = null;
	private BufferedImage bottomImage = null;
	private BufferedImage frontImageProcessed = null;
	private BufferedImage bottomImageProcessed = null;
	private Dimension dim = new Dimension(854,480);
	
	/**
	 * Constructor for GUI's video panel (Spycam). Use {@link #imageUpdated(BufferedImage)} to update image.
	 */
	public SpaceXVideoPanel() {
		setLayout(new MigLayout());
		setPreferredSize(dim);
		setBackground(Color.decode("#333333"));
		
		// Add a placeholder image...
		try {
			File img = new File("materials/bufferImage0.png");
			BufferedImage in = ImageIO.read(img);
			frontImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2d = frontImage.createGraphics();
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
	public void imageUpdated(BufferedImage newImage, Boolean isFront) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if(isFront) {
					frontImage = newImage;
				} else {
					bottomImage = newImage;
				}
				repaint();
			}
		});
    }
	
	public BufferedImage getImg(Boolean isFront) {
		if(isFront) {
			return frontImage;
		} else {
			return bottomImage;
		}
	}
	
	public void setImg(BufferedImage image,Boolean isFront) {
		if(isFront) {
			frontImageProcessed = image;
		} else {
			bottomImageProcessed = image;
		}
		repaint();
	}
	
	/**
	 * Edit the default dimension (640x480) with this method. <br>
	 * Will throw {@link IllegalArgumentException} if dimensions given are illegal (both width and height has to be greater than 0) 
	 * 
	 * @param w new width of dimension
	 * @param h new height of dimension
	 */
	public void setDimensions(int w, int h) {
		if(w > 0 && h > 0)
			this.dim.setSize(w, h);
		else
			throw new IllegalArgumentException("Width and height has to be greater than 0");
	}
	
	@Override
	public synchronized void paintComponent(Graphics g) {
		int width = 427; //picture width/3
		int height = 240; //picture height/3
		super.paintComponent(g);
        if (frontImageProcessed != null)
			g.drawImage(frontImageProcessed.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, width, height, null);
        if(frontImage != null)
        	g.drawImage(frontImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), width, 0, width, height, null);
        if (bottomImageProcessed != null)
			g.drawImage(bottomImageProcessed.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, height, width, height, null);
        if(bottomImage != null)
        	g.drawImage(bottomImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), width, height, width, height, null);
    }
}


