package fr.rhaz.ipfs.client;

import java.io.IOException;

import fr.rhaz.events.EventRunnable;
import fr.rhaz.ipfs.daemon.Daemon;
import fr.rhaz.ipfs.daemon.DaemonEvent;
import fr.rhaz.ipfs.daemon.DaemonEvent.DaemonEventType;

public class Client {
	public static Daemon daemon;
	public static Client i;
	private Window window;

	public static void main(String[] args){
		i = new Client();
	}

	public Client() {
		daemon = new Daemon();
		window = new Window();
		
		daemon.getEventManager().register(new EventRunnable<DaemonEvent>() {

			@Override
			public void execute(DaemonEvent e) {
				window.lblYouCanClose.setText("Please wait...");
				if(e.getType().equals(DaemonEventType.DAEMON_STARTED))
					window.lblIpfsIsRunning.setText("IPFS is starting...");
				if(e.getType().equals(DaemonEventType.DAEMON_STOPPED)) {
					window.lblIpfsIsRunning.setText("IPFS is stopped");
					window.lblYouCanClose.setText("You can close this window");
				}
				if(e.getType().equals(DaemonEventType.INIT_DONE))
					window.lblIpfsIsRunning.setText("IPFS is starting...");
				if(e.getType().equals(DaemonEventType.INIT_STARTED))
					window.lblIpfsIsRunning.setText("IPFS is starting...");
				if(e.getType().equals(DaemonEventType.ATTACHED)) {
					window.lblIpfsIsRunning.setText("IPFS is running");
					window.lblYouCanClose.setText("You can close this window");
				}
			}
		});
		
		daemon.run(() -> init());
		
	}
	
	public static void init() {
		try {
			daemon.binaries();
		} catch (IOException e) {
			e.printStackTrace();
		}
		daemon.start();
		daemon.attach();
	}
}
