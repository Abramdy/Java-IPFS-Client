package fr.rhaz.ipfs.daemon;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Window extends JFrame{

	private static final long serialVersionUID = -2188421821266426888L;
	public static Window window;
	private static TrayIcon icon;
	private static ImageIcon logo;
	private static String name = "IPFS";

	public Window(){
		window = this;
		logo = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));
		this.setTitle(name);
		this.setSize(900, 600);
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
		this.setVisible(true);
		this.setContentPane(new GUI());
	}
	
	public class GUI extends JPanel {

		private static final long serialVersionUID = 9027965393979292390L;

		public GUI() {
			add(new JLabel("IPFS Running..."));
		}
	}
}