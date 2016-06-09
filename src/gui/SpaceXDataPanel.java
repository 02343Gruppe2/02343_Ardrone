package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import algo.GeneralMotorCon;
import de.yadrone.base.navdata.BatteryListener;
import net.miginfocom.swing.MigLayout;

/**
 * DataPanel is used to give current informations regarding the GUI; image information, lines etc.
 * 
 * @author Kristin Hansen
 *
 */
public class SpaceXDataPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JLabel imageNumberLabel;
	private JLabel imageDimension;
	private JLabel batteryWatch;
	private long imageNumber = 0;
	private int imageHeight;
	private int imageWidth;
	private int batteryPercentage;
	
	
	
	/**
	 * Constructor for data panel.
	 */
	public SpaceXDataPanel() {
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(240, 480));
		setBackground(Color.decode("#333333"));
		
		Border b = BorderFactory.createLineBorder(Color.GRAY);
		setBorder(BorderFactory.createTitledBorder(b, "NavData", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Arial", 1, 14), Color.ORANGE));
		
		imageNumberLabel = new JLabel("Number of images: " + String.valueOf(imageNumber));
		imageNumberLabel.setFont(new Font("Arial", 0, 11));
		imageNumberLabel.setForeground(Color.WHITE);
		
		imageDimension = new JLabel("Image dimension: " + imageWidth + "x" + imageHeight);
		imageDimension.setFont(new Font("Arial", 0, 11));
		imageDimension.setForeground(Color.WHITE);
		
		batteryWatch = new JLabel("Battery Percetage: " + GeneralMotorCon.getInstance().getBatLvl() + "%");
		batteryWatch.setFont(new Font("Arial", 0, 11));
		batteryWatch.setForeground(Color.WHITE);
		
		JLabel imageData = new JLabel("Image data");
		imageData.setFont(new Font("Arial", 1, 12));
		imageData.setForeground(Color.WHITE);
		
		
		add(imageData, "wrap");
		add(imageNumberLabel, "wrap");
		add(imageDimension, "wrap");
		
		
		add(batteryWatch, "wrap");
		
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
	 * Updates the data about current image dimensions (height).
	 * 
	 * @param imageHeight The new height.
	 */
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
		
		imageDimension.setText("Image dimension: " + this.imageWidth + "x" + this.imageHeight);
	}
}

