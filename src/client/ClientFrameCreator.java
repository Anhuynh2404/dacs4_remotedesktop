package client;

import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import javax.swing.*;

class ClientFrameCreator extends Thread {
	String width = "", height = "";
	private JFrame frame = new JFrame();
	private JDesktopPane desktop = new JDesktopPane();
	private Socket cSocket = null;
	private JInternalFrame interFrame = new JInternalFrame("Server Screen", true, true, true);
	private JPanel cPanel = new JPanel();

	public ClientFrameCreator(Socket cSocket, String width, String height) {
		this.width = width;
		this.height = height;
		this.cSocket = cSocket;
		start();
	}

	public void drawGUI() {
		frame.add(desktop, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		interFrame.setLayout(new BorderLayout());
		interFrame.getContentPane().add(cPanel, BorderLayout.CENTER);
		interFrame.setSize(100, 100);
		desktop.add(interFrame);

		try {
			interFrame.setMaximum(true);
		} catch(PropertyVetoException ex) {
			ex.printStackTrace();
		}
		cPanel.setFocusable(true);
		interFrame.setVisible(true);
	}

	public void run() {
		InputStream is = null;
		drawGUI();
		try {
			is = cSocket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		new ScreenReceiver(is, cPanel);
		new EventSender(cSocket, cPanel, width, height);
	}
}
