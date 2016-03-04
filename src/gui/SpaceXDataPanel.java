package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Kristin Hansen
 *
 */
public class SpaceXDataPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JLabel imageNumberLabel;
	private JLabel imageDimension;
	private long imageNumber = 0;
	private int imageHeight;
	private int imageWidth;

	/**
	 * Constructor for data panel.
	 */
	public SpaceXDataPanel() {
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(240, 480));
		setBorder(BorderFactory.createTitledBorder("NavData"));
		
		imageNumberLabel = new JLabel("Number of images: " + String.valueOf(imageNumber));
		imageNumberLabel.setFont(new Font("Arial", 0, 11));
		
		imageDimension = new JLabel("Image dimension: " + imageWidth + "x" + imageHeight);
		imageDimension.setFont(new Font("Arial", 0, 11));
		
		add(new JLabel("Image data"), "wrap");
		add(imageNumberLabel, "wrap");
		add(imageDimension, "wrap");
		add(new JLabel("Some data:"), "wrap");
		add(new JLabel("Some data:"), "wrap");
		add(new JLabel("Some data:"), "wrap");
		add(new JLabel("Some data:"), "wrap");
		
		// TODO
	}
	
	/**
	 * Will force the component to repaint and update all data values (repaint).
	 */
	public void updateData() {
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
	
	// GETTER/SETTERS
	/**
	 * Resets the image counter.
	 */
	public void resetImageNumber() {
		this.imageNumber = 0;
	}

	/**
	 * Increments the image counter with 1.
	 */
	public void incrementImageNumber() {
		this.imageNumber++;
		
		imageNumberLabel.setText("Number of images: " + String.valueOf(imageNumber));
	}
	
	public int getImageWidth() {
		return imageWidth;
	}
	
	/**
	 * Updates the data about current image dimensions (width).
	 * 
	 * @param imageWidth The new width.
	 */
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
		
		imageDimension.setText("Image dimension: " + this.imageWidth + "x" + this.imageHeight);
	}
	
	public int getImageHeight() {
		return imageHeight;
	}

	/**
	 * Updates the data about current iamge dimensions (height).
	 * 
	 * @param imageHeight The new height.
	 */
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
		
		imageDimension.setText("Image dimension: " + this.imageWidth + "x" + this.imageHeight);
	}
}