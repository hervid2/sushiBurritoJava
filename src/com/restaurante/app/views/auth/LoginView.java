package com.restaurante.app.views.auth;

import javax.swing.JFrame;
import java.awt.*;
import javax.swing.JPanel;

public class LoginView extends JFrame {
	private final JPanel panel = new JPanel();
	public LoginView() {
		getContentPane().setLayout(null);
		panel.setBounds(0, 0, 739, 612);
		getContentPane().add(panel);
		panel.setLayout(null);
	}
}
