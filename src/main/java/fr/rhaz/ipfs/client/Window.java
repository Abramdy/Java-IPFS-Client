package fr.rhaz.ipfs.client;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JToggleButton;
import javax.swing.BoxLayout;
import javax.swing.JSeparator;
import java.awt.GridLayout;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.Color;

public class Window extends JFrame{

	private static final long serialVersionUID = -2188421821266426888L;
	public static Window window;
	private static TrayIcon icon;
	private static ImageIcon logo;
	private static String name = "IPFS";
	private final Action action = new SwingAction();
	public JLabel lblIpfsIsRunning;
	public JButton tglbtnNewToggleButton;
	public JLabel lblYouCanClose;

	public Window(){
		window = this;
		logo = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));
		this.setTitle("IPFS");
		this.setSize(300, 160);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setIconImage(logo.getImage());
		if(SystemTray.isSupported()){
			try {
				PopupMenu menu = new PopupMenu();
				ActionListener openaction = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						window.setVisible(true);
					}
				};
				MenuItem open = new MenuItem("Open");
				open.addActionListener(openaction);
				menu.add(open);
				MenuItem exit = new MenuItem("Exit");
				exit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						window.setVisible(false);
						SystemTray.getSystemTray().remove(icon);
						System.exit(0);
					}
				});
				menu.add(exit);
				icon = new TrayIcon(logo.getImage(), name, menu);
				icon.setImageAutoSize(true);
				icon.addActionListener(openaction);
				SystemTray.getSystemTray().add(icon);
				this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			} catch (AWTException e) {}
		}
		getContentPane().setLayout(null);
		
		lblIpfsIsRunning = new JLabel("IPFS is starting...");
		
		lblIpfsIsRunning.setBounds(0, 13, 282, 33);
		lblIpfsIsRunning.setFont(new Font("Lucida Console", Font.PLAIN, 18));
		lblIpfsIsRunning.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblIpfsIsRunning);
		
		tglbtnNewToggleButton = new JButton("Toggle");
		tglbtnNewToggleButton.setFont(new Font("Lucida Console", Font.PLAIN, 13));
		tglbtnNewToggleButton.setAction(action);
		tglbtnNewToggleButton.setBounds(41, 72, 200, 31);
		getContentPane().add(tglbtnNewToggleButton);
		
		lblYouCanClose = new JLabel("Please wait...");
		lblYouCanClose.setHorizontalAlignment(SwingConstants.CENTER);
		lblYouCanClose.setFont(new Font("Lucida Console", Font.PLAIN, 13));
		lblYouCanClose.setBounds(41, 43, 217, 16);
		getContentPane().add(lblYouCanClose);
		this.setVisible(true);
	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Toggle");
			putValue(SHORT_DESCRIPTION, "Toggle the IPFS daemon");
		}
		public void actionPerformed(ActionEvent e) {
			Thread thread = Client.daemon.getThread();
			if(thread != null && thread.isAlive()) {
				thread.interrupt();
			} else {
				Client.daemon.run(() -> Client.init());
			}
		}
	}
}