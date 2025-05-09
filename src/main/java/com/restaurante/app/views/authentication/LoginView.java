package main.java.com.restaurante.app.views.authentication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginView extends BaseAuthView {
    private JTextField textFieldUsuario;
    private JPasswordField textFieldContrasena;

    @Override
    protected String getBackgroundImagePath() {
        return "/main/resources/images/ui/imagenLogin.jpg";
    }
    
    /**
     * @wbp.parser.entryPoint
     */
    @Override
    protected void initializeSpecificComponents(JPanel panelFormLogin) {
        // Título del formulario
        JLabel lblTituloLogin = new JLabel("Bienvenido a Sushi Burrito");
        lblTituloLogin.setBounds(50, 80, 362, 41);
        lblTituloLogin.setFont(new Font("Yu Gothic Medium", Font.BOLD, 25));
        lblTituloLogin.setHorizontalAlignment(SwingConstants.CENTER);
        panelFormLogin.add(lblTituloLogin);
        
        // Subtítulos informativos
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
        
        // Panel interno para los campos de formulario
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 250, 205));
        panel.setBounds(50, 220, 362, 320);
        panelFormLogin.add(panel);
        panel.setLayout(null);
        
        // Etiqueta y campo de usuario
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
        lblUsuario.setBounds(30, 20, 300, 25);
        panel.add(lblUsuario);
        
        textFieldUsuario = new JTextField();
        textFieldUsuario.setToolTipText("Ingresa tu nombre de usuario");
        textFieldUsuario.putClientProperty("JTextField.placeholderText", "Ingresa tu usuario");
        textFieldUsuario.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        textFieldUsuario.setBounds(30, 50, 300, 40);
        textFieldUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(textFieldUsuario);
        
        // Etiqueta y campo de contraseña
        JLabel lblContrasena = new JLabel("Contraseña");
        lblContrasena.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
        lblContrasena.setBounds(30, 110, 300, 25);
        panel.add(lblContrasena);
        
        textFieldContrasena = new JPasswordField();
        textFieldContrasena.setToolTipText("Ingresa tu contraseña");
        textFieldContrasena.putClientProperty("JTextField.placeholderText", "Ingresa tu contraseña");
        textFieldContrasena.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        textFieldContrasena.setBounds(30, 140, 300, 40);
        textFieldContrasena.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(textFieldContrasena);
        
        // Botón de ingreso
        JButton btnIngresar = new JButton("Ingresar");
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
        btnIngresar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Lógica de autenticación
            }
        });
        btnIngresar.setBackground(new Color(0, 128, 0));
        btnIngresar.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setBounds(30, 200, 300, 45);
        btnIngresar.setFocusPainted(false);
        panel.add(btnIngresar);
        
        // Enlace para recuperación de contraseña
        JLabel lblPromptLogin2 = new JLabel("¿Olvidaste tu contraseña?");
        lblPromptLogin2.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 13));
        lblPromptLogin2.setForeground(new Color(0, 100, 200));
        lblPromptLogin2.setBounds(30, 260, 300, 20);
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
    }
}