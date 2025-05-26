package main.java.com.restaurante.app.views.mesero;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VerPedidosEnProgresoView extends JFrame {

    public VerPedidosEnProgresoView() {
        setupUI();
    }

    private void setupUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 20);
            UIManager.put("TextComponent.arc", 20);
        } catch (Exception e) {
            System.err.println("Error al configurar FlatLaf: " + e.getMessage());
        }

        setTitle("Pedidos en Progreso - Sushi Burrito");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(255, 250, 205));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Pedidos en Progreso", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Yu Gothic Medium", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 128, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabla de pedidos
        String[] columnNames = {"# Pedido", "Mesa", "Mesero", "Estado", "Hora", "Total Estimado"};
        Object[][] sampleData = {
                {"001", "Mesa 3", "Carlos", "En preparación", "12:45", "$45.000"},
                {"002", "Mesa 7", "Ana", "Esperando cocina", "13:10", "$32.000"},
                {"003", "Mesa 1", "Luis", "Entregado", "13:25", "$50.000"}
        };

        JTable table = new JTable(new DefaultTableModel(sampleData, columnNames));
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionBackground(new Color(204, 255, 204));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botón volver
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton backButton = new JButton("Volver");
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.setBackground(new Color(255, 140, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setFocusPainted(false);

        backButton.addActionListener(e -> {
            this.dispose();
            new WaiterPanelView().setVisible(true);
        });

        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
}
