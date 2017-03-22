package com.ysn;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class WaitThread implements Runnable {

	public WaitThread() {
		
	}
	
	public void run() {
		// TODO Auto-generated method stub
		waitForConnection();
	}
	
	//	waiting for connection from devices
	private void waitForConnection() {
		LocalDevice localDevice = null;
		StreamConnectionNotifier notifier;
		StreamConnection connection = null;
		try {
			localDevice = LocalDevice.getLocalDevice();
			localDevice.setDiscoverable(DiscoveryAgent.GIAC);
			UUID uuid = new UUID("0f2b61c18be240e6ab90e735818da0a7", false);
			String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteNotifier";
			notifier = (StreamConnectionNotifier) Connector.open(url);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		//	waiting for connection
		while(true) {
			try {
				System.out.println("Waiting for connection . . .");
				MainFrame.taKeterangan.append("Waiting for connection . . .\n");
				connection = notifier.acceptAndOpen();
				Thread processThread = new Thread(new ProcessConnectionThread(connection));
				processThread.start();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

}
