package gui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

/**
 * @author Kristin Hansen
 *
 */
public class SpaceXButtonPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	JButton panLeft;
	JButton panRight;
	JButton panUp;
	JButton panDown;
	JButton panForward;
	JButton panBackward;
	JButton navHover;
	JButton navLand;
	JButton navKill;
	JButton camNext;
	JButton camVert;
	JButton camHori;
	
	/**
	 * Constructor for SX's control panel, containing all the buttons.
	 */
	public SpaceXButtonPanel() {
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(240, 240));
		setBorder(BorderFactory.createTitledBorder("Control panel"));
		
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
		JLabel con = new JLabel("Control");
		JLabel cam = new JLabel("Camera");
		
		JSeparator sep1 = new JSeparator();
		JSeparator sep2 = new JSeparator();
		sep1.setPreferredSize(new Dimension(240, 1));
		sep2.setPreferredSize(new Dimension(240, 1));
		
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
		add(navLand, "center");
		add(navKill, "wrap, right");
		
		// Eye candy...
		//add(sep2, "wrap, span");
		
		// Camera buttons
		add(cam, "wrap, span, left");
		add(camNext, "left");
		add(camHori, "center");
		add(camVert, "wrap, right");
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
