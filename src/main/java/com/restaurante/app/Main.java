package main.java.com.restaurante.app;

import main.java.com.restaurante.app.views.authentication.LoginView;

/**
 * Launch the application.
 */
 
public class Main {

	public static void main(String[] args) {
		// vista del login que es el punto de arranque en la main
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
	}
}