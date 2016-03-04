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
		
		txtArea = new JTextArea("[TIMESTAMP] Initializing...\n");
		txtArea.setPreferredSize(new Dimension(230,480));
		txtArea.setLineWrap(true);
		txtArea.setWrapStyleWord(true);
		txtArea.setEditable(false);
		txtArea.setBackground(Color.decode("#EEEEEE"));
		txtArea.setMargin(new Insets(5, 5, 5, 5));
		
		JScrollPane sp = new JScrollPane(txtArea);
		sp.setPreferredSize(new Dimension(640, 180));
		
		DefaultCaret c = (DefaultCaret) txtArea.getCaret();
		c.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		add(sp);
	}
	
	public JTextArea getTxtArea() {
		return this.txtArea;
	}
}
