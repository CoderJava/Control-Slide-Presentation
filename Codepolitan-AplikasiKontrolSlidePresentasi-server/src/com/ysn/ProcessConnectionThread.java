package com.ysn;

import java.awt.Robot;
import java.io.InputStream;

import javax.microedition.io.StreamConnection;

import com.sun.glass.events.KeyEvent;

public class ProcessConnectionThread implements Runnable {
	
	private StreamConnection mConnection;
	private static final int EXIT_CMD = -1;
	private static final int KEY_LEFT = 1;
	private static final int KEY_RIGHT = 2;	
	
	public ProcessConnectionThread(StreamConnection connection) {
		mConnection = connection;		
	}	

	public void run() {
		// TODO Auto-generated method stub
		try {
			InputStream inputStream = mConnection.openInputStream();
			System.out.println("Waiting for input");
			MainFrame.taKeterangan.append("Waiting for input");			
			while(true) {
				int command = inputStream.read();
				if(command == EXIT_CMD) {
					System.out.println("Finish process");
					MainFrame.taKeterangan.append("Finish process");
					break;
				}
				processCommand(command);
				System.out.println("Command: " + command);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processCommand(int command) {
		try {
			Robot robot = new Robot();
			switch(command) {
				case KEY_RIGHT:
					robot.keyPress(KeyEvent.VK_RIGHT);
					robot.keyRelease(KeyEvent.VK_RIGHT);
					System.out.println("Command: Right");
					MainFrame.taKeterangan.append("Command: Right\n");					
					break;
				case KEY_LEFT:
					robot.keyPress(KeyEvent.VK_LEFT);
					robot.keyRelease(KeyEvent.VK_LEFT);
					System.out.println("Command: Left");
					MainFrame.taKeterangan.append("Command: Left\n");
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
