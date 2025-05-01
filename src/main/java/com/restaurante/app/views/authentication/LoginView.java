package main.java.com.restaurante.app.views.authentication;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;

@SuppressWarnings("serial")
public class LoginView extends JFrame {
    private ImageIcon originalBackgroundIcon;
    private JLabel backgroundLabel;
    private JLabel logoLabel;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnForgotPassword;
    private JPanel contentPanel;

    public LoginView() {
        initComponents();
        applyCustomStyles();
    }

    private void initComponents() {
        setTitle("Sushi Burrito - Inicio de Sesión");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 900, 700, 30, 30));
        
        // Configuración del fondo
        originalBackgroundIcon = new ImageIcon(
            getClass().getResource("/com/restaurante/app/resources/images/ui/imagenLogin.jpg")
        );
        
        backgroundLabel = new JLabel();
        backgroundLabel.setLayout(new BorderLayout());
        setContentPane(backgroundLabel);
        
        // Panel de contenido principal
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());
        backgroundLabel.add(contentPanel, BorderLayout.CENTER);
        
        // Panel para los componentes de login
        JPanel loginPanel = new JPanel();
        loginPanel.setOpaque(false);
        loginPanel.setLayout(new GridBagLayout());
        
        // Logo circular
        logoLabel = new JLabel();
        logoLabel.setPreferredSize(new Dimension(150, 150));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Componentes de formulario
        txtUsuario = new JTextField(20);
        txtPassword = new JPasswordField(20);
        btnLogin = new JButton("INGRESAR");
        btnForgotPassword = new JButton("¿Olvidaste tu contraseña?");
        
        // Configuración de constraints
        GridBagConstraints gbcLogo = new GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;
        gbcLogo.insets = new Insets(0, 0, 30, 0);
        
        GridBagConstraints gbcUsuario = new GridBagConstraints();
        gbcUsuario.gridx = 0;
        gbcUsuario.gridy = 1;
        gbcUsuario.insets = new Insets(10, 0, 10, 0);
        
        GridBagConstraints gbcPassword = new GridBagConstraints();
        gbcPassword.gridx = 0;
        gbcPassword.gridy = 2;
        gbcPassword.insets = new Insets(10, 0, 10, 0);
        
        GridBagConstraints gbcLogin = new GridBagConstraints();
        gbcLogin.gridx = 0;
        gbcLogin.gridy = 3;
        gbcLogin.insets = new Insets(20, 0, 10, 0);
        
        GridBagConstraints gbcForgot = new GridBagConstraints();
        gbcForgot.gridx = 0;
        gbcForgot.gridy = 4;
        gbcForgot.insets = new Insets(10, 0, 0, 0);
        
        // Agregar componentes al panel de login
        loginPanel.add(logoLabel, gbcLogo);
        loginPanel.add(new JLabel("USUARIO"), gbcUsuario);
        loginPanel.add(txtUsuario, gbcUsuario);
        loginPanel.add(new JLabel("CONTRASEÑA"), gbcPassword);
        loginPanel.add(txtPassword, gbcPassword);
        loginPanel.add(btnLogin, gbcLogin);
        loginPanel.add(btnForgotPassword, gbcForgot);
        
        // Agregar panel de login al contentPanel principal
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        contentPanel.add(loginPanel, gbcMain);
        
        // Escalado automático
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateBackground();
            }
        });
    }

    private void applyCustomStyles() {
        // Estilo para el logo circular
        styleLogoLabel(logoLabel);
        
        // Estilos para los componentes
        styleLabel((JLabel) ((JPanel) contentPanel.getComponent(0)).getComponent(1));
        styleLabel((JLabel) ((JPanel) contentPanel.getComponent(0)).getComponent(3));
        styleTextField(txtUsuario, "Ingrese su usuario");
        stylePasswordField(txtPassword, "Ingrese su contraseña");
        styleButton(btnLogin, new Color(255, 105, 120)); // Color rosa para el botón
        styleTransparentButton(btnForgotPassword);
        
        updateBackground();
    }

    private void updateBackground() {
        if (originalBackgroundIcon != null) {
            Image scaledImage = originalBackgroundIcon.getImage()
                .getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
            backgroundLabel.setIcon(new ImageIcon(scaledImage));
        }
    }

    private void styleLogoLabel(JLabel label) {
        // Cargar imagen del logo
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/com/restaurante/app/resources/images/ui/logo.png"));
        Image img = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(img));
        
        // Hacer el label circular
        label.setBorder(BorderFactory.createEmptyBorder());
        label.setOpaque(false);
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void styleTextField(JTextField textField, String placeholder) {
        try {
            // Versión corregida:
            Border lineBorder = BorderFactory.createLineBorder(new Color(255, 255, 255, 150));
            Border padding = BorderFactory.createEmptyBorder(10, 15, 10, 15);
            textField.setBorder(BorderFactory.createCompoundBorder(lineBorder, padding));
            
            textField.setFont(new Font("Arial", Font.PLAIN, 14));
            textField.setForeground(Color.WHITE);
            textField.setCaretColor(Color.WHITE);
            textField.setOpaque(false);
            textField.setText(placeholder);
            
            textField.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (textField.getText().equals(placeholder)) {
                        textField.setText("");
                    }
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (textField.getText().isEmpty()) {
                        textField.setText(placeholder);
                    }
                }
            });
        } catch (Exception e) {
            // Fallback seguro
            textField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            System.err.println("Error al aplicar estilos al campo de texto: " + e.getMessage());
        }
    }

    private void stylePasswordField(JPasswordField passwordField, String placeholder) {
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setOpaque(false);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 150)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setEchoChar((char) 0);
        passwordField.setText(placeholder);
        
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText(placeholder);
                }
            }
        });
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        
        // Bordes redondeados
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 25, 25);
                super.paint(g2, c);
                g2.dispose();
            }
        });
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private void styleTransparentButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(200, 200, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
            }
        });
    }
}