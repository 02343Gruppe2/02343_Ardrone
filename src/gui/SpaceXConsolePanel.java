package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

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
	
	/**
	 * Creates the console panel with automatic scrolling.<br>
	 * 0-* parameters can given be given with type String. Each string will be treated as a line that will be added upon instantiating the GUI (default text).<br><br>
	 * 
	 * <b>Example</b><br>
	 * Calling new {@link SpaceXConsolePanel} with paramters <code>("A line", "Another line", "A third line")</code> will result in 3 lines added upon instantiating the console, like:<br><br>
	 * 
	 * [TIMESTAMP] A line<br>
	 * [TIMESTAMP] Another line<br>
	 * [TIMESTAMP] A third line
	 * 
	 * @param strings
	 */
	public SpaceXConsolePanel(String...strings) {
		// Basic layout for SpaceXConsolePanel object
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(854, 240));
		setBackground(Color.decode("#333333"));
		
		Border b = BorderFactory.createLineBorder(Color.GRAY);
		setBorder(BorderFactory.createTitledBorder(b, "Console", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Arial", 1, 14), Color.ORANGE));
		
		txtArea = new JTextArea();
		txtArea.setBackground(Color.decode("#EEEEEE"));
		txtArea.setEditable(false);
		txtArea.setLineWrap(true);
		txtArea.setMargin(new Insets(5, 5, 5, 5));
		txtArea.setWrapStyleWord(true);
		
		// If any strings were given with constructor, append them to textarea
		if(strings.length > 0)
			for(String s : strings)
				txtArea.append(s + "\n");
		
		// Add textarea to a JScrollPane with a vertical scrollbar
		JScrollPane sp = new JScrollPane(txtArea);
		sp.setPreferredSize(new Dimension(1094, 180));
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		// This will position the caret (or scrollbar) dynamically at the bottom whenever something is appended to textarea
		new SmartScroller(sp);
		
		// Add everything to SpaceXConsolePanel object
		add(sp);
	}
	
	public void appendText(String...strings) {
		for(String s : strings)
			txtArea.append(s);
	}

	public void clearPanel() {
		// TODO Auto-generated method stub
		
	}
}
