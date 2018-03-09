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
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;

import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;

import java.awt.Font;
import javax.swing.JToggleButton;
import javax.swing.BoxLayout;
import javax.swing.JSeparator;
import java.awt.GridLayout;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.Color;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

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
	private final Action action_1 = new SwingAction_1();
	private final Action action_2 = new SwingAction_2();
	private final Action action_3 = new SwingAction_3();

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
		
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.setLocation(71, 97);
		addPopup(getContentPane(), popupMenu);
		
		JMenuItem mntmAdd = new JMenuItem("Add file...");
		mntmAdd.setAction(action_2);
		popupMenu.add(mntmAdd);
		
		JMenuItem mntmAddFolder = new JMenuItem("Add folder...");
		mntmAddFolder.setAction(action_3);
		popupMenu.add(mntmAddFolder);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAction(action_1);
		popupMenu.add(mntmExit);
		
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
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	private class SwingAction_1 extends AbstractAction {
		public SwingAction_1() {
			putValue(NAME, "Exit");
			putValue(SHORT_DESCRIPTION, "Exit");
		}
		public void actionPerformed(ActionEvent e) {
			window.setVisible(false);
			SystemTray.getSystemTray().remove(icon);
			System.exit(0);
		}
	}
	private class SwingAction_2 extends AbstractAction {
		public SwingAction_2() {
			putValue(NAME, "Add file...");
			putValue(SHORT_DESCRIPTION, "Add file to IPFS");
		}
		public void actionPerformed(ActionEvent e) {
			//Create a file chooser
			final JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			//In response to a button click:
			int returnVal = fc.showOpenDialog(window);
			if (returnVal != JFileChooser.APPROVE_OPTION) return;
			
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
			NamedStreamable.FileWrapper streamable = new NamedStreamable.FileWrapper(file);
			try {
				List<MerkleNode> nodes = Client.daemon.getIPFS().add(streamable);
				JTextArea textArea = new JTextArea(1, 1);
			      textArea.setText(nodes.get(0).hash.toString());
			      textArea.setEditable(false);
				JOptionPane.showMessageDialog(new JFrame(), textArea, "Done",
				        JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(new JFrame(), e1.getMessage(), "Error",
				        JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}
	private class SwingAction_3 extends AbstractAction {
		public SwingAction_3() {
			putValue(NAME, "Add folder...");
			putValue(SHORT_DESCRIPTION, "Add folder to IPFS");
		}
		public void actionPerformed(ActionEvent e) {
			//Create a file chooser
			final JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			//In response to a button click:
			int returnVal = fc.showOpenDialog(window);
			if (returnVal != JFileChooser.APPROVE_OPTION) return;
			
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
			NamedStreamable.FileWrapper streamable = new NamedStreamable.FileWrapper(file);
			List<NamedStreamable> list = streamable.getChildren();
			try {
				List<MerkleNode> nodes = Client.daemon.getIPFS().add(list, true);
				JTextArea textArea = new JTextArea(1, 1);
				List<String> hashs = nodes.stream().map(node -> node.hash.toString()).collect(Collectors.toList());
			      textArea.setText(hashs.get(hashs.size()-1));
			      textArea.setEditable(false);
				JOptionPane.showMessageDialog(new JFrame(), textArea, "Done",
				        JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(new JFrame(), e1.getMessage(), "Error",
				        JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}
}