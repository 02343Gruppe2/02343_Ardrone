package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

import gui.utils.FormattedTimeStamp;
import gui.utils.SmartScroller;
import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Kristin Hansen
 *
 */
public class SpaceXConsolePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JTextArea txtArea;
	
	public SpaceXConsolePanel() {
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(640, 180));
		setBorder(BorderFactory.createTitledBorder("Console"));
		
		txtArea = new JTextArea("[" + FormattedTimeStamp.getTime() + "] Initializing...");
		txtArea.setBackground(Color.decode("#EEEEEE"));
		txtArea.setEditable(false);
		txtArea.setLineWrap(true);
		txtArea.setMargin(new Insets(5, 5, 5, 5));
		txtArea.setWrapStyleWord(true);
		
		JScrollPane sp = new JScrollPane(txtArea);
		sp.setPreferredSize(new Dimension(640, 180));
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		// This will position the caret (or scrollbar) dynamically at the bottom
		new SmartScroller(sp);
		
		add(sp);
	}
	
	public JTextArea getTxtArea() {
		return this.txtArea;
	}
}
