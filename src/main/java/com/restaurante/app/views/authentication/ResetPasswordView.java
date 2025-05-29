package main.java.com.restaurante.app.views.authentication;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.controllers.UsuarioController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class ResetPasswordView extends BaseAuthView {
    private JTextField textFieldUsuario;

    static {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Component.arc", 20);
            UIManager.put("TextComponent.arc", 20);
            UIManager.put("TextComponent.borderColor", new Color(200, 200, 200));
            UIManager.put("Component.focusWidth", 1);
        } catch (Exception ex) {
            System.err.println("Error al configurar FlatLaf: " + ex.getMessage());
        }
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
        panel.setBounds(50, 257, 362, 249);
        panel.setLayout(null);
        panelFormLogin.add(panel);

        JLabel lblUsuario = new JLabel("Correo electrónico o nombre de usuario");
        lblUsuario.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
        lblUsuario.setBounds(20, 38, 320, 25);
        panel.add(lblUsuario);

        textFieldUsuario = new JTextField();
        textFieldUsuario.setToolTipText("Ingresa el correo o nombre de usuario asociado a tu perfil");
        textFieldUsuario.putClientProperty("JTextField.placeholderText", "Correo electrónico o nombre de usuario");
        textFieldUsuario.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        textFieldUsuario.setBounds(20, 74, 320, 40);
        panel.add(textFieldUsuario);

        JButton btnIngresar = new JButton("Enviar");
        btnIngresar.setBackground(new Color(0, 128, 0));
        btnIngresar.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setBounds(24, 142, 316, 45);
        btnIngresar.setFocusPainted(false);
        btnIngresar.addActionListener(e -> {
            String correo = textFieldUsuario.getText();

            if (correo == null || correo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor ingresa un correo o nombre de usuario.");
                return;
            }

            if (!isValidEmail(correo)) {
                JOptionPane.showMessageDialog(frame, "Formato de correo inválido.");
                return;
            }

            try {
                UsuarioController controller = new UsuarioController();
                if (controller.usuarioExistePorCorreo(correo)) {
                    dispose();
                    new SetNewPasswordView(correo).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(frame, "No se encontró ningún usuario con ese correo.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error al validar el usuario:\n" + ex.getMessage());
                ex.printStackTrace();
            }
        });
        panel.add(btnIngresar);

        JLabel lblPromptLogin2 = new JLabel("Volver al inicio de sesión");
        lblPromptLogin2.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 13)); 
        lblPromptLogin2.setForeground(new Color(0, 100, 200)); 
        lblPromptLogin2.setBounds(34, 198, 300, 20); 
        lblPromptLogin2.setHorizontalAlignment(SwingConstants.CENTER); 
        lblPromptLogin2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
        lblPromptLogin2.addMouseListener(new MouseAdapter() { 
            @Override 
            public void mouseClicked(MouseEvent e) { 
                frame.dispose(); 
                LoginView loginView = new LoginView(); 
                loginView.setVisible(true); 
            } 
        }); 
        panel.add(lblPromptLogin2); 

        JList<String> list = new JList<>(); 
        list.setBackground(new Color(255, 250, 205)); 
        list.setModel(new AbstractListModel<String>() { 
            String[] values = { 
                "Por favor, ingresa tu correo electrónico ", 
                "registrado o tu nombre de usuario para", 
                "         reestablecer tu contraseña" 
            }; 

            public int getSize() { return values.length; } 
            public String getElementAt(int index) { return values[index]; } 
        }); 
        list.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14)); 
        list.setBounds(97, 157, 280, 89); 
        panelFormLogin.add(list); 

        SwingUtilities.invokeLater(() -> { 
            SwingUtilities.updateComponentTreeUI(panelFormLogin); 
        }); 
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-\\.+]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(regex, email);
    }
} 
