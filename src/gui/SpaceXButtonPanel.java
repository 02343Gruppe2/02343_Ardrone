package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

/**
 * ButtonPanel contains all buttons for navigation and controls.
 * 
 * @author Kristin Hansen
 *
 */
public class SpaceXButtonPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JButton panLeft;
	private JButton panRight;
	private JButton panUp;
	private JButton panDown;
	private JButton panForward;
	private JButton panBackward;
	private JButton navHover;
	private JButton navLand;
	private JButton navKill;
	private JButton camNext;
	private JButton camVert;
	private JButton camHori;
	
	/**
	 * Constructor for SX's control panel, containing all the buttons.
	 */
	public SpaceXButtonPanel() {
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(240, 240));
		setBackground(Color.decode("#333333"));
		
		Border b = BorderFactory.createLineBorder(Color.GRAY);
		setBorder(BorderFactory.createTitledBorder(b, "Control panel", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Arial", 1, 14), Color.WHITE));
		
		panLeft = new JButton("<html><b>&larr;</b></html>");
		panRight = new JButton("<html><b>&rarr;</b></html>");
		panUp = new JButton("U");
		panDown = new JButton("D");
		panForward = new JButton("<html><b>&uarr;</b></html>");
		panBackward = new JButton("<html><b>&darr;</b></html>");
		navHover = new JButton("Hover");
		navLand = new JButton("Land");
		navKill = new JButton("Kill");
		
		camNext = new JButton("Next");
		camHori = new JButton("Hori");
		camVert = new JButton("Vert");
		
		JLabel nav = new JLabel("Navigation");
		nav.setFont(new Font("Arial", 1, 12));
		nav.setForeground(Color.WHITE);
		
		JLabel con = new JLabel("Control");
		con.setFont(new Font("Arial", 1, 12));
		con.setForeground(Color.WHITE);
		
		JLabel cam = new JLabel("Camera");
		cam.setFont(new Font("Arial", 1, 12));
		cam.setForeground(Color.WHITE);
		
		// Navigation buttons
		add(nav, "wrap, span, left");
		add(panUp, "right");
		add(panForward, "center");
		add(panDown, "wrap, left");
		add(panLeft, "right");
		add(panBackward, "center");
		add(panRight, "wrap, left");
		
		// Eye candy...
		//add(sep1, "wrap, span");
		
		// Critical control
		add(con, "wrap, span, left");
		add(navHover, "left");
		add(navLand, "left");
		add(navKill, "wrap, left");
		
		// Eye candy...
		//add(sep2, "wrap, span");
		
		// Camera buttons
		add(cam, "wrap, span, left");
		add(camNext, "left");
		add(camHori, "left");
		add(camVert, "wrap, left");
	}

	public JButton getPanLeft() {
		return panLeft;
	}

	public JButton getPanRight() {
		return panRight;
	}

	public JButton getPanUp() {
		return panUp;
	}

	public JButton getPanDown() {
		return panDown;
	}

	public JButton getNavHover() {
		return navHover;
	}

	public JButton getNavLand() {
		return navLand;
	}

	public JButton getNavKill() {
		return navKill;
	}

	public JButton getCamNext() {
		return camNext;
	}

	public JButton getCamVert() {
		return camVert;
	}

	public JButton getCamHori() {
		return camHori;
	}
}
