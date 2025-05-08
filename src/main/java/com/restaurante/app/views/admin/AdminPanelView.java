package main.java.com.restaurante.app.views.admin;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class AdminPanelView extends JFrame {
    
    public AdminPanelView() {
        setupUI();
    }

    private void setupUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Configuración de estilos FlatLaf
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 20);
            UIManager.put("TextComponent.arc", 20);
        } catch (Exception ex) {
            System.err.println("Error al configurar FlatLaf: " + ex.getMessage());
        }

        setTitle("Panel Administrativo Sushi Burrito");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal con borde redondeado
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        mainPanel.setBackground(new Color(255, 250, 205)); // Color de fondo
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Panel superior con botón de cerrar sesión
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        
        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setBackground(new Color(220, 80, 80)); // Rojo para cerrar sesión
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        topPanel.add(logoutButton);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Panel de botones de gestión
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        String[] buttonTitles = {"Gestión Usuarios", "Gestión Carta", "Estadísticas"};
        for (String title : buttonTitles) {
            JButton button = new JButton(title);
            button.setBackground(new Color(70, 130, 180)); // Azul acero
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 16));
            button.setPreferredSize(new Dimension(200, 80));
            button.setFocusPainted(false);
            buttonPanel.add(button);
        }
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Panel inferior con mensaje de bienvenida y copyright
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Bienvenido al panel de administración.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel copyrightLabel = new JLabel("© 2025 Sushi Burrito. Todos los derechos reservados.", SwingConstants.CENTER);
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        bottomPanel.add(welcomeLabel, BorderLayout.CENTER);
        bottomPanel.add(copyrightLabel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
}