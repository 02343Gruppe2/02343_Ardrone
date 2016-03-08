package gui;

import gui.utils.CreateRandomColor;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * LinesPanel is used to draw lines on the panel itself.<br>
 * Intended use is to give an option to show e.g. automatic control (via algorithms) on the Spycam.<br>
 * 
 * @author Kristin Hansen
 */
public class SpaceXLinesPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Point[]> points = new ArrayList<Point[]>();
	
	/**
	 * Constructor for LinesPanel. Will create a default size of the LinesPanel (640,480).
	 */
	public SpaceXLinesPanel() {
		setPreferredSize(new Dimension(640,480));
		setOpaque(false);
	}
	
	/**
	 * Constructor for LinesPanel. Will create a LinesPanel with the given size from parameters.
	 * 
	 * @param width Will set the width of the LinesPanel.
	 * @param height Will set the height of the LinesPanel.
	 */
	public SpaceXLinesPanel(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		setOpaque(false);
	}
	
	/**
	 * Will add a new line to the LinesPanel and redraw the panel.
	 * 
	 * @param p1 Coordinate of first point in the line.
	 * @param p2 Coordinate of second point in the line.
	 */
	
	public void drawLine(Point p1, Point p2) {
		Point[] p = {p1, p2};
		
		points.add(p);
		
		this.repaint();
	}
	
	/**
	 * Will remove a specific line from the LinesPanel's list of lines.<br>
	 * First added line will have index 0 in the list.<br>
	 * 
	 * @param index Index of the line to be removed.
	 */
	public void removeLine(int index) throws ArrayIndexOutOfBoundsException {
		if((index+1) >= points.size())
			throw new ArrayIndexOutOfBoundsException();
			
		points.remove(index);
	}
	
	/**
	 * Will erase all lines from the LinesPanel.
	 */
	public void clearLines() {
		points.clear();
		
		this.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		
		for(int i = 0; i < points.size(); i++) {
			g2d.setColor(CreateRandomColor.getRandomColor());
			
			g.drawLine(points.get(i)[0].x, points.get(i)[0].y, points.get(i+1)[1].x, points.get(i+1)[1].y);
		}
	}
}