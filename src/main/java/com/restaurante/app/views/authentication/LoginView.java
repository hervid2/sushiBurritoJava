package main.java.com.restaurante.app.views.authentication;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.controllers.UsuarioController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class LoginView extends BaseAuthView {
    private JTextField textFieldUsuario;
    private JPasswordField textFieldContrasena;
    private JCheckBox showPasswordCheck;

    static {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 20);
            UIManager.put("TextComponent.arc", 20);
            UIManager.put("TextComponent.borderColor", new Color(200, 200, 200));
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.innerFocusWidth", 1);
        } catch (Exception ex) {
            System.err.println("Error al configurar FlatLaf: " + ex.getMessage());
        }
    }

    @Override
    protected String getBackgroundImagePath() {
        return "/main/resources/images/ui/imagenLogin.jpg";
    }

    @Override
    protected void initializeSpecificComponents(JPanel panelFormLogin) {
        JLabel lblTituloLogin = new JLabel("Bienvenido a Sushi Burrito");
        lblTituloLogin.setBounds(50, 80, 362, 41);
        lblTituloLogin.setFont(new Font("Yu Gothic Medium", Font.BOLD, 25));
        lblTituloLogin.setHorizontalAlignment(SwingConstants.CENTER);
        panelFormLogin.add(lblTituloLogin);

        JLabel lblPromptLogin = new JLabel("Inicia sesión para continuar");
        lblPromptLogin.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        lblPromptLogin.setBounds(50, 140, 362, 20);
        lblPromptLogin.setHorizontalAlignment(SwingConstants.CENTER);
        panelFormLogin.add(lblPromptLogin);

        JLabel lblPromptLogin1 = new JLabel("Si no tienes una cuenta, por favor contacta al administrador.");
        lblPromptLogin1.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 13));
        lblPromptLogin1.setBounds(50, 170, 372, 28);
        lblPromptLogin1.setHorizontalAlignment(SwingConstants.CENTER);
        panelFormLogin.add(lblPromptLogin1);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 250, 205));
        panel.setBounds(50, 220, 362, 320);
        panel.setLayout(null);
        panelFormLogin.add(panel);

        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
        lblUsuario.setBounds(30, 20, 300, 25);
        panel.add(lblUsuario);

        textFieldUsuario = new JTextField();
        textFieldUsuario.setToolTipText("Ingresa tu nombre de usuario");
        textFieldUsuario.putClientProperty("JTextField.placeholderText", "Ingresa tu usuario");
        textFieldUsuario.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        textFieldUsuario.setBounds(30, 50, 300, 40);
        panel.add(textFieldUsuario);

        JLabel lblContrasena = new JLabel("Contraseña");
        lblContrasena.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
        lblContrasena.setBounds(30, 110, 300, 25);
        panel.add(lblContrasena);

        textFieldContrasena = new JPasswordField();
        textFieldContrasena.setToolTipText("Ingresa tu contraseña");
        textFieldContrasena.putClientProperty("JTextField.placeholderText", "Ingresa tu contraseña");
        textFieldContrasena.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        textFieldContrasena.setBounds(30, 140, 300, 40);
        panel.add(textFieldContrasena);

        showPasswordCheck = new JCheckBox("Mostrar contraseña");
        showPasswordCheck.setBounds(30, 185, 300, 20);
        showPasswordCheck.setBackground(new Color(255, 250, 205));
        showPasswordCheck.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 13));
        showPasswordCheck.addActionListener(e -> textFieldContrasena.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '●'));
        panel.add(showPasswordCheck);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.setBackground(new Color(0, 128, 0));
        btnIngresar.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setBounds(30, 210, 300, 45);
        btnIngresar.setFocusPainted(false);
        btnIngresar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnIngresar.setBackground(new Color(255, 140, 0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnIngresar.setBackground(new Color(0, 128, 0));
            }
        });
        btnIngresar.addActionListener(e -> {
            String correo = textFieldUsuario.getText();
            String contrasena = new String(textFieldContrasena.getPassword());

            if (correo.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor completa ambos campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!Pattern.matches("^[\\w-\\.+]+@([\\w-]+\\.)+[\\w-]{2,4}$", correo)) {
                JOptionPane.showMessageDialog(frame, "Por favor ingresa un correo electrónico válido.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                UsuarioController controller = new UsuarioController();
                controller.login(correo, contrasena, frame);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error de conexión a base de datos: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        panel.add(btnIngresar);

        JLabel lblPromptLogin2 = new JLabel("¿Olvidaste tu contraseña?");
        lblPromptLogin2.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 13));
        lblPromptLogin2.setForeground(new Color(0, 100, 200));
        lblPromptLogin2.setBounds(30, 270, 300, 20);
        lblPromptLogin2.setHorizontalAlignment(SwingConstants.CENTER);
        lblPromptLogin2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblPromptLogin2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                ResetPasswordView resetView = new ResetPasswordView();
                resetView.setVisible(true);
            }
        });
        panel.add(lblPromptLogin2);

        SwingUtilities.invokeLater(() -> {
            SwingUtilities.updateComponentTreeUI(textFieldUsuario);
            SwingUtilities.updateComponentTreeUI(textFieldContrasena);
            SwingUtilities.updateComponentTreeUI(btnIngresar);
            SwingUtilities.updateComponentTreeUI(panel);
        });
    }
}
