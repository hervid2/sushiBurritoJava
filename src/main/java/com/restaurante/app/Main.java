package main.java.com.restaurante.app;
import main.java.com.restaurante.app.views.admin.AdminPanelView;
import main.java.com.restaurante.app.views.authentication.LoginView;
import main.java.com.restaurante.app.views.authentication.SetNewPasswordView;

/**
 * Launch the application.
 */
public class Main {

	public static void main(String[] args) {
		// SetNewPasswordView setNewPasswordView = new SetNewPasswordView();
		// setNewPasswordView.setVisible(true);
		
		// vista del login
      // LoginView loginView = new LoginView();
       // loginView.setVisible(true);
		
		AdminPanelView frame = new AdminPanelView();
        frame.setVisible(true);

	}

}
