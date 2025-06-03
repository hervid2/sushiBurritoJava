package main.java.com.restaurante.app.views.authentication;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.controllers.UsuarioController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SetNewPasswordView extends BaseAuthView {
    private JTextField textFieldNewPassword;
    private JTextField textFieldNewPasswordConfirm;
    private final String correo;

    public SetNewPasswordView(String correo) {
    	super();
        this.correo = correo;
    }

    @Override
    protected String getBackgroundImagePath() {
        return "/main/resources/images/ui/imagenLoginForgotPassword.jpg";
    }

    @Override
    protected void initializeSpecificComponents(JPanel panelFormLogin) {
        JLabel lblTituloLogin = new JLabel("Reestablecer Contraseña");
        lblTituloLogin.setBounds(71, 105, 327, 41);
        lblTituloLogin.setFont(new Font("Yu Gothic Medium", Font.BOLD, 25));
        lblTituloLogin.setHorizontalAlignment(SwingConstants.CENTER);
        panelFormLogin.add(lblTituloLogin);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 250, 205));
        panel.setBounds(50, 207, 386, 317);
        panel.setLayout(null);
        panelFormLogin.add(panel);

        JLabel lblNewPassword = new JLabel("Nueva Contraseña");
        lblNewPassword.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
        lblNewPassword.setBounds(20, 38, 173, 25);
        panel.add(lblNewPassword);

        textFieldNewPassword = new JPasswordField();
        textFieldNewPassword.setToolTipText("Ingresa una nueva contraseña");
        textFieldNewPassword.putClientProperty("JTextField.placeholderText", "Nueva Contraseña");
        textFieldNewPassword.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        textFieldNewPassword.setBounds(20, 62, 320, 40);
        panel.add(textFieldNewPassword);

        JLabel lblNewPasswordConfirm = new JLabel("Confirmar Contraseña");
        lblNewPasswordConfirm.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
        lblNewPasswordConfirm.setBounds(20, 125, 186, 25);
        panel.add(lblNewPasswordConfirm);

        textFieldNewPasswordConfirm = new JPasswordField();
        textFieldNewPasswordConfirm.setToolTipText("Ingresa de nuevo la contraseña ingresada anteriormente");
        textFieldNewPasswordConfirm.putClientProperty("JTextField.placeholderText", "Confirmar Contraseña");
        textFieldNewPasswordConfirm.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        textFieldNewPasswordConfirm.setBounds(20, 149, 320, 40);
        panel.add(textFieldNewPasswordConfirm);

        JButton btnReestablecerContrasena = new JButton("Reestablecer Contraseña");
        btnReestablecerContrasena.setBackground(new Color(0, 128, 0));
        btnReestablecerContrasena.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
        btnReestablecerContrasena.setForeground(Color.WHITE);
        btnReestablecerContrasena.setBounds(24, 211, 316, 45);
        btnReestablecerContrasena.setFocusPainted(false);
        
        btnReestablecerContrasena.addActionListener(e -> {
            String nueva = textFieldNewPassword.getText();
            String confirmar = textFieldNewPasswordConfirm.getText();
            if (nueva.isEmpty() || confirmar.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor completa ambos campos.");
                return;
            }
            if (!nueva.equals(confirmar)) {
                JOptionPane.showMessageDialog(frame, "Las contraseñas no coinciden.");
                return;
            }
            try {
                UsuarioController controller = new UsuarioController();
                if (controller.reestablecerContrasena(correo, nueva)) { 
                    JOptionPane.showMessageDialog(frame, "Contraseña actualizada correctamente.");
                } else {
                    JOptionPane.showMessageDialog(frame, "No se pudo actualizar la contraseña. Verifica el correo.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error al actualizar la contraseña: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        panel.add(btnReestablecerContrasena);

        JLabel lblPromptLogin2 = new JLabel("Volver al inicio de sesión");
        lblPromptLogin2.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 13));
        lblPromptLogin2.setForeground(new Color(0, 100, 200));
        lblPromptLogin2.setBounds(40, 267, 300, 20);
        lblPromptLogin2.setHorizontalAlignment(SwingConstants.CENTER);
        lblPromptLogin2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblPromptLogin2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                new LoginView().setVisible(true);
            }
        });
        panel.add(lblPromptLogin2);
    }
}
