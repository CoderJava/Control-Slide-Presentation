package com.ysn;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame implements ActionListener {

	private JPanel contentPane;
	public static JTextArea taKeterangan;
	private JButton btnAction;
	private JLabel lblStatus;
	private PopupMenu popupMenu;
	private TrayIcon trayIcon;
	private MenuItem menuItemExit;
	private MenuItem menuItemOpenWindow;

	private Thread thread;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("Codepolitan - Aplikasi Kontrol Slide Presentasi Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 258);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		taKeterangan = new JTextArea();
		taKeterangan.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(taKeterangan);
		scrollPane.setBounds(10, 11, 424, 160);
		contentPane.add(scrollPane);
		
		btnAction = new JButton("Start/Stop");
		btnAction.setBounds(10, 182, 89, 23);
		contentPane.add(btnAction);
		
		lblStatus = new JLabel("Status: Off");
		lblStatus.setHorizontalAlignment(SwingConstants.LEFT);
		lblStatus.setBounds(360, 182, 64, 23);					
		contentPane.add(lblStatus);
		
		popupMenu = new PopupMenu();		
		menuItemExit = new MenuItem("Exit");
		menuItemOpenWindow = new MenuItem("Open Window");
		popupMenu.add(menuItemExit);
		popupMenu.add(menuItemOpenWindow);
		Image iconTray = Toolkit.getDefaultToolkit().getImage("D:/icon_tray.png");		
		trayIcon = new TrayIcon(iconTray, "Codepolitan - Aplikasi Kontrol Slide Presentasi Server", popupMenu);
		trayIcon.setImageAutoSize(true);
		
		thread = new Thread(new WaitThread());
		
		setEventHandler();
	}
	
	public void setEventHandler() {
		//	menuItemExit
		menuItemExit.addActionListener(this);
		
		//	menuItemOpenWindow
		menuItemOpenWindow.addActionListener(this);
		
		//	btnAction
		btnAction.addActionListener(this);
		
		//	window
		this.addWindowListener(new WindowListener() {

			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				try {
					SystemTray.getSystemTray().add(trayIcon);
					setVisible(false);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}

			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		if(ae.getSource() == btnAction) {
			if(lblStatus.getText().equalsIgnoreCase("Status: On")) {
				lblStatus.setText("Status: Off");
				thread.stop();
			} else {
				lblStatus.setText("Status: On");
				thread.start();
			}
		} else if(ae.getSource() == menuItemExit) {
			int questionToQuit = JOptionPane.showConfirmDialog(null, "Are you sure to exit this app?", "Confirm", JOptionPane.YES_NO_OPTION);
			if(questionToQuit == JOptionPane.YES_OPTION) {
				System.exit(1);
			}
		} else if(ae.getSource() == menuItemOpenWindow) {
			setVisible(true);
			setState(Frame.NORMAL);
			SystemTray.getSystemTray().remove(trayIcon);
		}
	}
}
