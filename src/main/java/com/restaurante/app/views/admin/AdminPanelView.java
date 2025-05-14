package main.java.com.restaurante.app.views.admin;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.restaurante.app.views.admin.UsersManagement;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class AdminPanelView extends JFrame {
    
    // Ruta de las imágenes
    private static final String[] IMAGE_PATHS = {
        "/main/resources/images/ui/GestionUsuarios.jpeg",
        "/main/resources/images/ui/GestionCarta.jpg",
        "/main/resources/images/ui/Estadisticas.png"
    };
    
    public AdminPanelView() {
        setupUI();
    }

    private void setupUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 20);
            UIManager.put("TextComponent.arc", 20);
        } catch (Exception ex) {
            System.err.println("Error al configurar FlatLaf: " + ex.getMessage());
        }

        setTitle("Panel Administrativo Sushi Burrito");
        setSize(913, 663);
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
        mainPanel.setBackground(new Color(255, 250, 205));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Panel superior con botón de cerrar sesión
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        
        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setBackground(new Color(255, 140, 0));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(150, 40));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Acción para el botón de cerrar sesión
        logoutButton.addActionListener(e -> {
            this.dispose();
            // Aquí podrías abrir la ventana de login si es necesario
        });
        
        topPanel.add(logoutButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Panel de botones de gestión con imágenes
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        String[] buttonTitles = {"Gestión Usuarios", "Gestión Carta", "Estadísticas"};
        Color buttonColor = new Color(0, 128, 0);
        
        for (int i = 0; i < buttonTitles.length; i++) {
            // Panel contenedor para cada opción
            JPanel optionPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(buttonColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.dispose();
                }
            };
            optionPanel.setOpaque(false);
            optionPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Cargar imagen con bordes redondeados
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(IMAGE_PATHS[i]));
            Image scaledImage = originalIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            
            // Label para la imagen con bordes redondeados
            JLabel imageLabel = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imageLabel.setVerticalAlignment(JLabel.CENTER);
            imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Título
            JLabel titleLabel = new JLabel(buttonTitles[i], JLabel.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            
            // Panel para centrar contenido
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setOpaque(false);
            contentPanel.add(imageLabel, BorderLayout.CENTER);
            contentPanel.add(titleLabel, BorderLayout.SOUTH);
            
            optionPanel.add(contentPanel, BorderLayout.CENTER);
            
            // Configurar acciones según el panel
            if (i == 0) { // Gestión Usuarios
                optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        abrirGestionUsuarios();
                    }
                });
            } else if (i == 1) { // Gestión Carta
                optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Aquí iría la lógica para abrir Gestión Carta
                        JOptionPane.showMessageDialog(AdminPanelView.this, 
                            "Gestión Carta seleccionada", 
                            "Aviso", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            } else { // Estadísticas
                optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Aquí iría la lógica para abrir Estadísticas
                        JOptionPane.showMessageDialog(AdminPanelView.this, 
                            "Estadísticas seleccionadas", 
                            "Aviso", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
            
            buttonPanel.add(optionPanel);
        }
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Panel inferior con mensaje de bienvenida y copyright
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Bienvenido al panel de administración.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 25));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel copyrightLabel = new JLabel("© 2025 Sushi Burrito. Todos los derechos reservados.", SwingConstants.CENTER);
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        bottomPanel.add(welcomeLabel, BorderLayout.CENTER);
        bottomPanel.add(copyrightLabel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void abrirGestionUsuarios() {
        EventQueue.invokeLater(() -> {
            try {
                UsersManagement usersManagement = new UsersManagement();
                usersManagement.setVisible(true);
                usersManagement.setLocationRelativeTo(null);
                this.dispose(); // Cierra el panel actual
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error al abrir la gestión de usuarios: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    // Clase adaptadora para manejar los eventos del mouse
    private abstract class PanelMouseAdapter extends MouseAdapter {
        private final float ZOOM_FACTOR = 1.2f;
        private Timer timer;
        private float currentScale = 1.0f;
        private final JLabel titleLabel;
        private final JComponent component; // Referencia al componente padre
        
        public PanelMouseAdapter(JLabel titleLabel, JComponent component) {
            this.titleLabel = titleLabel;
            this.component = component; // Guardar referencia al componente padre
        }
        
        @Override
        public void mouseEntered(MouseEvent e) {
            startAnimation(ZOOM_FACTOR);
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            startAnimation(1.0f);
        }
        
        private void startAnimation(float targetScale) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            
            timer = new Timer(10, evt -> {
                float delta = targetScale > currentScale ? 0.05f : -0.05f;
                currentScale += delta;
                
                if ((delta > 0 && currentScale >= targetScale) || 
                    (delta < 0 && currentScale <= targetScale)) {
                    currentScale = targetScale;
                    timer.stop();
                }
                
                float fontSize = 16 * currentScale;
                titleLabel.setFont(titleLabel.getFont().deriveFont(fontSize));
                
                component.revalidate();
                component.repaint();
            });
            timer.start();
        }
    }
}