package main.java.com.restaurante.app.views.authentication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ResetPasswordView extends BaseAuthView {
    private JTextField textFieldUsuario;
    
    @Override
    protected String getBackgroundImagePath() {
        return "/main/resources/images/ui/imagenLoginForgotPassword.jpg"; 
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    protected void initializeSpecificComponents(JPanel panelFormLogin) {
        // Título del formulario
        JLabel lblTituloLogin = new JLabel("Reestablecer Contraseña");
        lblTituloLogin.setBounds(71, 105, 327, 41);
        lblTituloLogin.setFont(new Font("Yu Gothic Medium", Font.BOLD, 25));
        lblTituloLogin.setHorizontalAlignment(SwingConstants.CENTER);
        panelFormLogin.add(lblTituloLogin);
        
        // Panel interno para los campos de formulario
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 250, 205));
        panel.setBounds(50, 257, 362, 249);
        panelFormLogin.add(panel);
        panel.setLayout(null);
        
        // Etiqueta y campo de usuario
        JLabel lblUsuario = new JLabel("Correo electrónico o nombre de usuario");
        lblUsuario.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
        lblUsuario.setBounds(20, 38, 320, 25);
        panel.add(lblUsuario);
        
        textFieldUsuario = new JTextField();
        textFieldUsuario.setToolTipText("Ingresa el correo o nombre de usuario asociado a tu perfil");
        textFieldUsuario.putClientProperty("JTextField.placeholderText", "Correo electrónico o nombre de usuario");
        textFieldUsuario.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        textFieldUsuario.setBounds(20, 74, 320, 40);
        textFieldUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(textFieldUsuario);
        
        // Botón de ingreso
        JButton btnIngresar = new JButton("Enviar");
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
        btnIngresar.setBounds(24, 142, 316, 45);
        btnIngresar.setFocusPainted(false);
        panel.add(btnIngresar);
        
        // Enlace para volver al login
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
        
        JList list = new JList();
        list.setBackground(new Color(255, 250, 205));
        list.setModel(new AbstractListModel() {
            String[] values = new String[] {"Por favor, ingresa tu correo electrónico ", "registrado o tu nombre de usuario para", "         reestablecer tu contraseña"};
            public int getSize() {
                return values.length;
            }
            public Object getElementAt(int index) {
                return values[index];
            }
        });
        list.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));
        list.setBounds(97, 157, 280, 89);
        panelFormLogin.add(list);
    }
}
