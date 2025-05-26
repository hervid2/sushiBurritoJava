package main.java.com.restaurante.app.views.cocina;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class CocinaPanelView extends JFrame {

    private JTabbedPane tabbedPane;
    private JTable tablaEntrantes;
    private JTable tablaPreparacion;
    private DefaultTableModel modelEntrantes;
    private DefaultTableModel modelPreparacion;

    public CocinaPanelView() {
        setupUI();
    }

    private void setupUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 20);
            UIManager.put("TextComponent.arc", 20);
        } catch (Exception ex) {
            System.err.println("Error configurando FlatLaf: " + ex.getMessage());
        }

        setTitle("Panel de Cocina - Sushi Burrito");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 250, 205));
        setContentPane(mainPanel);

        // Header con título y logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Panel de Cocina", SwingConstants.CENTER);
        title.setFont(new Font("Yu Gothic UI", Font.BOLD, 28));
        topPanel.add(title, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setBackground(new Color(255, 140, 0));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(150, 40));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.addActionListener(e -> dispose());
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { logoutButton.setBackground(new Color(0,128,0)); }
            public void mouseExited(java.awt.event.MouseEvent e) { logoutButton.setBackground(new Color(255,140,0)); }
            public void mousePressed(java.awt.event.MouseEvent e) { logoutButton.setBackground(new Color(0,128,0)); }
        });
        topPanel.add(logoutButton, BorderLayout.EAST);
        // Espacio a la izquierda para centrar el título correctamente
        Dimension btnSize = logoutButton.getPreferredSize();
        topPanel.add(Box.createRigidArea(btnSize), BorderLayout.WEST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Configurar pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        // UI de pestañas redondeadas
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement,
                                          int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 15;
                // Draw only top rounded corners by extending height and masking bottom corners
                g2.setColor(isSelected ? new Color(200,200,200) : getBackground());
                // Fill a rounded rect slightly taller
                g2.fillRoundRect(x, y, w, h + arc/2, arc, arc);
                // Mask bottom rounded part to square
                g2.setColor(isSelected ? new Color(200,200,200) : getBackground());
                g2.fillRect(x, y + arc/2, w, h - arc/2);
                // Draw border only on top corners
                g2.setColor(Color.GRAY);
                g2.setStroke(new BasicStroke(1));
                // Top border arc
                g2.drawRoundRect(x, y, w, h + arc/2, arc, arc);
                // Mask bottom border line to straight
                g2.drawLine(x, y + h, x + w, y + h);
                g2.dispose();
            }
        });

        // Pestaña Pedidos Entrantes
        modelEntrantes = new DefaultTableModel(new Object[]{"ID Pedido","Mesa","Productos"}, 0);
        tablaEntrantes = new JTable(modelEntrantes);
        JScrollPane scrollEntrantes = new JScrollPane(tablaEntrantes);
        JPanel panelEntrantes = new JPanel(new BorderLayout());
        panelEntrantes.setOpaque(false);
        panelEntrantes.add(scrollEntrantes, BorderLayout.CENTER);
        JButton btnPasarPrep = new JButton("Pasar a Preparación");
        btnPasarPrep.setBackground(new Color(0, 128, 0));
        btnPasarPrep.setForeground(Color.WHITE);
        btnPasarPrep.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPasarPrep.setPreferredSize(new Dimension(220, 40));
        btnPasarPrep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int fila = tablaEntrantes.getSelectedRow();
                if (fila != -1) {
                    Vector row = (Vector)modelEntrantes.getDataVector().get(fila);
                    modelEntrantes.removeRow(fila);
                    modelPreparacion.addRow(row);
                } else {
                    JOptionPane.showMessageDialog(CocinaPanelView.this, "Selecciona un pedido entrante.");
                }
            }
        });
        JPanel botonEntrantes = new JPanel(new FlowLayout(FlowLayout.CENTER)); botonEntrantes.setOpaque(false);
        botonEntrantes.add(btnPasarPrep);
        panelEntrantes.add(botonEntrantes, BorderLayout.SOUTH);
        tabbedPane.addTab("Pedidos Entrantes", panelEntrantes);

        // Pestaña En Preparación
        modelPreparacion = new DefaultTableModel(new Object[]{"ID Pedido","Mesa","Productos"}, 0);
        tablaPreparacion = new JTable(modelPreparacion);
        JScrollPane scrollPreparacion = new JScrollPane(tablaPreparacion);
        JPanel panelPrep = new JPanel(new BorderLayout()); panelPrep.setOpaque(false);
        panelPrep.add(scrollPreparacion, BorderLayout.CENTER);
        JButton btnMarcarListo = new JButton("Marcar como Preparado");
        btnMarcarListo.setBackground(new Color(0, 128, 0));
        btnMarcarListo.setForeground(Color.WHITE);
        btnMarcarListo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnMarcarListo.setPreferredSize(new Dimension(220, 40));
        btnMarcarListo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int fila = tablaPreparacion.getSelectedRow();
                if (fila != -1) {
                    modelPreparacion.removeRow(fila);
                    JOptionPane.showMessageDialog(CocinaPanelView.this, "Pedido preparado y notificado.");
                } else {
                    JOptionPane.showMessageDialog(CocinaPanelView.this, "Selecciona un pedido en preparación.");
                }
            }
        });
        JPanel botonPrep = new JPanel(new FlowLayout(FlowLayout.CENTER)); botonPrep.setOpaque(false);
        botonPrep.add(btnMarcarListo);
        panelPrep.add(botonPrep, BorderLayout.SOUTH);
        tabbedPane.addTab("En Preparación", panelPrep);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        addSampleData();
    }

    private void addSampleData() {
        modelEntrantes.addRow(new Object[]{"101","Mesa 2","2 Sushi Roll"});
        modelEntrantes.addRow(new Object[]{"102","Mesa 5","1 Ramen, Gyozas"});
        modelPreparacion.addRow(new Object[]{"103","Mesa 3","Tempura"});
    }

    
}

