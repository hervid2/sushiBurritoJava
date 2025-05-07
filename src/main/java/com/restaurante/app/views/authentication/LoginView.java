package main.java.com.restaurante.app.views.authentication;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class LoginView {

    private JFrame frame;
    private final JPanel panelWithImage = new JPanel();
    private final JLabel lblBackground = new JLabel("");
    private JTextField textFieldUsuario;
    private JPasswordField textFieldContrasena;

    // Clase interna para el label redondo
    class RoundLabel extends JLabel {
        public RoundLabel(ImageIcon icon) {
            super(icon);
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Shape circle = new Ellipse2D.Double(0, 0, getWidth()-1, getHeight()-1);
            g2.setClip(circle);
            super.paintComponent(g2);
            
            // Borde circular blanco
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.WHITE);
            g2.draw(circle);
            
            g2.dispose();
        }
    }

    public LoginView() {
        // Configuración inicial de FlatLaf (Look and Feel)
        try {
            FlatLightLaf.setup();
            
            // Personalización de propiedades de FlatLaf:
            UIManager.put("TextComponent.arc", 20); // Radio de esquinas redondeadas para campos de texto
            UIManager.put("Button.arc", 15); // Radio de esquinas redondeadas para botones
            UIManager.put("Component.focusWidth", 1); // Grosor del borde cuando el componente tiene foco
            UIManager.put("Panel.background", new Color(255, 250, 205)); // Color de fondo para los paneles
        } catch (Exception ex) {
            System.err.println("Error al inicializar FlatLaf: " + ex.getMessage());
        }
        
        initialize();
    }

    private void initialize() {
        // Configuración de la ventana principal
        frame = new JFrame();
        frame.setBounds(100, 100, 913, 663);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setTitle("Sushi Burrito - Inicio de Sesión");
        
        // ----------------------------
        // Panel izquierdo con imagen
        // ----------------------------
        panelWithImage.setBounds(0, 0, 435, 624);
        frame.getContentPane().add(panelWithImage);
        panelWithImage.setLayout(null);
        
        // Título sobre la imagen (agregado primero para que quede sobre el fondo)
        JLabel lblTitulo = new JLabel("Sistema de Gestión Sushi Burrito");
        lblTitulo.setFont(new Font("Yu Gothic Medium", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(31, 139, 363, 70);
        panelWithImage.add(lblTitulo);
        
        // Logo redondo (agregado segundo)
        ImageIcon originalIcon = new ImageIcon(LoginView.class.getResource("/main/resources/images/icons/logo.jpg"));
        Image img = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        RoundLabel lblLogo = new RoundLabel(new ImageIcon(img));
        lblLogo.setBounds(142, 284, 150, 150); // Centrado horizontalmente
        panelWithImage.add(lblLogo);
        
        // Imagen de fondo (agregada al final)
        lblBackground.setIcon(new ImageIcon(LoginView.class.getResource("/main/resources/images/ui/imagenLogin.jpg")));
        lblBackground.setBounds(0, 0, 435, 624);
        panelWithImage.add(lblBackground);
        
        // ----------------------------
        // Panel derecho con formulario
        // ----------------------------
        JPanel panelFormLogin = new JPanel();
        panelFormLogin.setBackground(new Color(255, 250, 205));
        panelFormLogin.setBounds(435, 0, 462, 624);
        panelFormLogin.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        frame.getContentPane().add(panelFormLogin);
        panelFormLogin.setLayout(null);
        
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
        lblPromptLogin1.setBounds(50, 170, 362, 20);
        lblPromptLogin1.setHorizontalAlignment(SwingConstants.CENTER);
        panelFormLogin.add(lblPromptLogin1);
        
        // ----------------------------
        // Panel interno para los campos de formulario
        // ----------------------------
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
        textFieldUsuario.putClientProperty("JTextField.placeholderText", "Ingrese su usuario");
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
        textFieldContrasena.putClientProperty("JTextField.placeholderText", "Ingrese su contraseña");
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
                btnIngresar.setBackground(new Color(255, 140, 0)); // Naranja al pasar mouse
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnIngresar.setBackground(new Color(0, 128, 0)); // Verde al salir mouse
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
        panel.add(lblPromptLogin2);
    }

    public void setVisible(boolean b) {
        frame.setVisible(true);
    }
}