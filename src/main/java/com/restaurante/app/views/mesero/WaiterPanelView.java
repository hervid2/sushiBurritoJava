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

    private static final String[] IMAGE_PATHS = {
            "/main/resources/images/ui/orden.png",
            "/main/resources/images/ui/GenerarFactura.png",
            "/main/resources/images/ui/EditarPedido.png",
            "/main/resources/images/ui/CerrarPedido.png",
            "/main/resources/images/ui/CancelarPedido.png",
            "/main/resources/images/ui/PedidosEnProgreso.png"
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
        setSize(1400, 750); // Aumentado el ancho del frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);

        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setBackground(new Color(255, 140, 0));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(150, 40));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        logoutButton.addActionListener(e -> this.dispose());

        logoutButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(new Color(0, 128, 0));
            }

            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(new Color(255, 140, 0));
            }

            public void mousePressed(MouseEvent e) {
                logoutButton.setBackground(new Color(0, 128, 0));
            }
        });

        topPanel.add(logoutButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 20, 0)); // Ahora 6 columnas
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        String[] buttonTitles = {
                "Generar Comanda",
                "Generar Factura",
                "Editar Pedidos",
                "Cerrar Pedidos",
                "Cancelar Orden",
                "Progreso pedidos"
        };

        Color buttonColor = new Color(0, 128, 0);

        for (int i = 0; i < buttonTitles.length; i++) {
            JPanel optionPanel = new JPanel(new BorderLayout()) {
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

            ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(IMAGE_PATHS[i])));
            Image scaledImage = originalIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);

            JLabel imageLabel = new JLabel() {
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

            JLabel titleLabel = new JLabel(buttonTitles[i], JLabel.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setOpaque(false);
            contentPanel.add(imageLabel, BorderLayout.CENTER);
            contentPanel.add(titleLabel, BorderLayout.SOUTH);

            optionPanel.add(contentPanel, BorderLayout.CENTER);

            switch (i) {
                case 0 -> optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    public void mouseClicked(MouseEvent e) { generateOrder(); }
                });
                case 1 -> optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    public void mouseClicked(MouseEvent e) { generateInvoice(); }
                });
                case 2 -> optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    public void mouseClicked(MouseEvent e) { editOrder(); }
                });
                case 3 -> optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    public void mouseClicked(MouseEvent e) { closeOrder(); }
                });
                case 4 -> optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    public void mouseClicked(MouseEvent e) { cancelOrder(); }
                });
                case 5 -> optionPanel.addMouseListener(new PanelMouseAdapter(titleLabel, optionPanel) {
                    public void mouseClicked(MouseEvent e) { viewActiveOrders(); }
                });
            }

            buttonPanel.add(optionPanel);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Bienvenido al panel de mesero.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 25));
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel copyrightLabel = new JLabel("© 2025 Sushi Burrito. Todos los derechos reservados.",
                SwingConstants.CENTER);
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        bottomPanel.add(welcomeLabel, BorderLayout.CENTER);
        bottomPanel.add(copyrightLabel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void generateOrder() {
        EventQueue.invokeLater(() -> {
            this.dispose();
            new GenerarComandaView().setVisible(true);
        });
    }

    private void generateInvoice() {
        EventQueue.invokeLater(() -> {
            this.dispose();
            new GenerarFacturaView().setVisible(true);
        });
    }

    private void editOrder() {
        EventQueue.invokeLater(() -> {
            this.dispose();
            new EditarPedidoView().setVisible(true);
        });
    }

    private void closeOrder() {
        EventQueue.invokeLater(() -> {
            this.dispose();
            new CerrarPedidosView().setVisible(true);
        });
    }

    private void cancelOrder() {
        EventQueue.invokeLater(() -> {
            this.dispose();
            new CancelarPedidoView().setVisible(true);
        });
    }

    private void viewActiveOrders() {
        EventQueue.invokeLater(() -> {
            this.dispose();
            new VerPedidosEnProgresoView().setVisible(true);
        });
    }

    private abstract class PanelMouseAdapter extends MouseAdapter {
        private final float ZOOM_FACTOR = 1.05f;
        private Timer timer;
        private float currentScale = 1.0f;
        private final JLabel titleLabel;
        private final JComponent component;

        public PanelMouseAdapter(JLabel titleLabel, JComponent component) {
            this.titleLabel = titleLabel;
            this.component = component;
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
                float delta = targetScale > currentScale ? 0.01f : -0.01f;
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
  

