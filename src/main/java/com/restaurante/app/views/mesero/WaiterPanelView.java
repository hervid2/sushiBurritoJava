package main.java.com.restaurante.app.views.mesero;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;

public class WaiterPanelView extends JFrame {

    // Image paths for the buttons
    private static final String[] IMAGE_PATHS = {
            "/main/resources/images/ui/orden.png",
            "/main/resources/images/ui/GenerarFactura.png",
            "/main/resources/images/ui/EditarPedido.png",
            "/main/resources/images/ui/CerrarPedido.png",
            "/main/resources/images/ui/CancelarPedido.png"
    };

    public WaiterPanelView() {
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

        setTitle("Panel de Mesero Sushi Burrito");
        setSize(1200, 750); // Increased frame size for more options
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with rounded border
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
        mainPanel.setBackground(new Color(255, 250, 205)); // Light yellow/cream background
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Top panel with logout button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);

        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setBackground(new Color(255, 140, 0)); // Orange button
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(150, 40));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Action for the logout button
        logoutButton.addActionListener(e -> {
            this.dispose();
            // TODO: Add logic to open the login window if needed
        });

        topPanel.add(logoutButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Hover effect for the logout button
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(new Color(0, 128, 0)); // Green on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(new Color(255, 140, 0)); // Orange on exit
            }

            @Override
            public void mousePressed(MouseEvent e) {
                logoutButton.setBackground(new Color(0, 128, 0)); // Green on press
            }
        });

        // Button panel for waiter options
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 20, 0)); // 5 columns for 5 options
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        String[] buttonTitles = {"Generar Comanda", "Generar Factura", "Editar Pedidos", "Cerrar Pedidos", "Cancelar Orden"};
        Color buttonColor = new Color(0, 128, 0); // Green button color

        for (int i = 0; i < buttonTitles.length; i++) {
            // Container panel for each option
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
            optionPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

            // Load image with rounded borders
            ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(IMAGE_PATHS[i])));
            Image scaledImage = originalIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH); // Adjusted image size
            
            // Label for the image with rounded borders
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
            imageLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // Title
            JLabel titleLabel = new JLabel(buttonTitles[i], JLabel.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

            // Panel to center content
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setOpaque(false);
            contentPanel.add(imageLabel, BorderLayout.CENTER);
            contentPanel.add(titleLabel, BorderLayout.SOUTH);

            optionPanel.add(contentPanel, BorderLayout.CENTER);

            // Configure actions based on the panel
            if (i == 0) { // Generar Comanda
                optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        generateOrder();
                    }
                });
            } else if (i == 1) { // Generar Factura
                optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        generateInvoice();
                    }
                });
            } else if (i == 2) { // Editar Pedidos
                optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        editOrder();
                    }
                });
            } else if (i == 3) { // Cerrar Pedidos
                optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        closeOrder();
                    }
                });
            } else { // Cancelar Orden
                optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        cancelOrder();
                    }
                });
            }

            buttonPanel.add(optionPanel);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Bottom panel with welcome message and copyright
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Bienvenido al panel de mesero.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 25));
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel copyrightLabel = new JLabel("© 2025 Sushi Burrito. Todos los derechos reservados.", SwingConstants.CENTER);
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        bottomPanel.add(welcomeLabel, BorderLayout.CENTER);
        bottomPanel.add(copyrightLabel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- Functional methods (placeholders) ---

    // Method to handle "Generar Comanda"
    private void generateOrder() {
        EventQueue.invokeLater(() -> {
        	this.dispose(); // Cierra la ventana actual
            new GenerarComandaView().setVisible(true); 
        });
    }

    // Method to handle "Generar Factura"
    private void generateInvoice() {
        EventQueue.invokeLater(() -> { 
        	this.dispose(); 
        	new GenerarFacturaView().setVisible(true); 
        });
    }

    // Method to handle "Editar Pedidos"
    private void editOrder() {
        EventQueue.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Funcionalidad: Editar Pedidos (Próximamente)", "Información", JOptionPane.INFORMATION_MESSAGE);
            // TODO: Implement the logic to edit existing orders
        });
    }

    // Method to handle "Cerrar Pedidos"
    private void closeOrder() {
        EventQueue.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Funcionalidad: Cerrar Pedidos (Próximamente)", "Información", JOptionPane.INFORMATION_MESSAGE);
            // TODO: Implement the logic to close orders (mark as paid/delivered)
        });
    }

    // Method to handle "Cancelar Orden"
    private void cancelOrder() {
        EventQueue.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Funcionalidad: Cancelar Orden (Próximamente)", "Información", JOptionPane.INFORMATION_MESSAGE);
            // TODO: Implement the logic to cancel an order
        });
    }

    // --- Helper class for mouse events and animations ---
    private abstract class PanelMouseAdapter extends MouseAdapter {
        private final float ZOOM_FACTOR = 1.05f; // Slightly smaller zoom for more options
        private Timer timer;
        private float currentScale = 1.0f;
        private final JLabel titleLabel;
        private final JComponent component; // Reference to the parent component

        public PanelMouseAdapter(JLabel titleLabel, JComponent component) {
            this.titleLabel = titleLabel;
            this.component = component; // Store reference to the parent component
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
                float delta = targetScale > currentScale ? 0.01f : -0.01f; // Slower animation
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
   

