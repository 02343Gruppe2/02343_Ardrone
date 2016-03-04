package gui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
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
		setPreferredSize(new Dimension(240, 180));
		setBorder(BorderFactory.createTitledBorder("Control panel"));
		
		panLeft = new JButton("<html><b>&larr;</b></html>");
		panRight = new JButton("<html><b>&rarr;</b></html>");
		panUp = new JButton("<html><b>&uarr;</b></html>");
		panDown = new JButton("<html><b>&darr;</b></html>");
		navHover = new JButton("Hover");
		navLand = new JButton("Land");
		navKill = new JButton("Kill");
		
		camNext = new JButton("Next");
		camHori = new JButton("Hori");
		camVert = new JButton("Vert");
		
		/* LAYOUT:
		 *  _____
		 * |__U__|
		 * |L|D|R|
		 * |N|H|V|
		 *  ¯ ¯ ¯
		 *  U: Up
		 *  L: Left
		 *  R: Right
		 *  D: Down
		 *  N: Next camera
		 *  H: Horizontal
		 *  V: Vertical
		 */
		
		// Navigation buttons
		add(panUp, "wrap, span, center");
		add(panLeft, "center");
		add(panDown, "center");
		add(panRight, "wrap, center");
		add(navHover, "center");
		add(navLand, "center");
		add(navKill, "wrap, center");
		
		// Camera buttons
		add(camNext, "center");
		add(camHori, "center");
		add(camVert, "wrap, center");
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
