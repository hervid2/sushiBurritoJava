package main.java.com.restaurante.app.views.authentication;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public abstract class BaseAuthView {
    protected JFrame frame;
    protected JPanel panelWithImage;
    protected JLabel lblBackground;
    
    protected abstract String getBackgroundImagePath();

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

    protected BaseAuthView() {
        setupLookAndFeel();
        initializeCommonComponents();
    }

    private void setupLookAndFeel() {
        try {
            FlatLightLaf.setup();
            
            // Personalización de propiedades de FlatLaf:
            UIManager.put("TextComponent.arc", 20);
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Panel.background", new Color(255, 250, 205));
        } catch (Exception ex) {
            System.err.println("Error al inicializar FlatLaf: " + ex.getMessage());
        }
    }

    private void initializeCommonComponents() {
        frame = new JFrame();
        frame.setBounds(100, 100, 913, 663);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setTitle("Sushi Burrito - Inicio de Sesión");
        
        // Panel izquierdo con imagen
        panelWithImage = new JPanel();
        panelWithImage.setBounds(0, 0, 435, 624);
        frame.getContentPane().add(panelWithImage);
        panelWithImage.setLayout(null);
        
        // Título sobre la imagen
        JLabel lblTitulo = new JLabel("Sistema de Gestión Sushi Burrito");
        lblTitulo.setFont(new Font("Yu Gothic Medium", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(31, 139, 363, 70);
        panelWithImage.add(lblTitulo);
        
        // Logo redondo
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/main/resources/images/icons/logo.jpg"));
        Image img = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        RoundLabel lblLogo = new RoundLabel(new ImageIcon(img));
        lblLogo.setBounds(142, 284, 150, 150);
        panelWithImage.add(lblLogo);
        
        // Imagen de fondo
        lblBackground = new JLabel("");
        lblBackground.setIcon(new ImageIcon(getClass().getResource(getBackgroundImagePath())));
        lblBackground.setBounds(0, 0, 435, 624);
        panelWithImage.add(lblBackground);
        
        // Panel derecho con formulario
        JPanel panelFormLogin = new JPanel();
        panelFormLogin.setBackground(new Color(255, 250, 205));
        panelFormLogin.setBounds(435, 0, 462, 624);
        panelFormLogin.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        frame.getContentPane().add(panelFormLogin);
        panelFormLogin.setLayout(null);
        
        initializeSpecificComponents(panelFormLogin);
    }

    protected abstract void initializeSpecificComponents(JPanel panelFormLogin);

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
    
    public void dispose() {
        frame.dispose();
    }
}