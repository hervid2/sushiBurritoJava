package main.java.com.restaurante.app.views.mesero;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CancelarPedidoView extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel pedidosModel;

    public CancelarPedidoView() {
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

        setTitle("Cancelar Pedido - Sushi Burrito");
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 250, 205));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Cancelar Pedido", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 28));
        titleLabel.setBorder(new EmptyBorder(10, 0, 30, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabla de pedidos
        pedidosModel = new DefaultTableModel(new String[]{"ID Pedido", "Mesa", "Productos", "Estado"}, 0);
        pedidosTable = new JTable(pedidosModel);
        JScrollPane scrollPane = new JScrollPane(pedidosTable);
        scrollPane.setPreferredSize(new Dimension(800, 250));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Botón Cancelar Pedido centrado
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        botonesPanel.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar Pedido");
        btnCancelar.setBackground(new Color(220, 20, 60)); // Rojo carmesí
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCancelar.setPreferredSize(new Dimension(200, 40));

        botonesPanel.add(btnCancelar);
        mainPanel.add(botonesPanel, BorderLayout.SOUTH);

        // Acción de cancelar pedido
        btnCancelar.addActionListener(e -> {
            int selectedRow = pedidosTable.getSelectedRow();
            if (selectedRow != -1) {
                String estado = (String) pedidosModel.getValueAt(selectedRow, 3);
                if (estado.equalsIgnoreCase("Pendiente")) {
                    JOptionPane.showMessageDialog(this, "Pedido cancelado exitosamente.");
                    // Opción 1: Remover directamente de la tabla (no archivar)
                    pedidosModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(this, "No se puede cancelar un pedido que ya está en preparación o entregado.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un pedido para cancelarlo.");
            }
        });

        // Botón Volver al Panel del Mesero
        JButton btnVolver = new JButton("← Volver al Panel del Mesero");
        btnVolver.setBackground(new Color(255, 140, 0));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVolver.setPreferredSize(new Dimension(250, 40));

        btnVolver.addActionListener(e -> {
            this.dispose();
            new WaiterPanelView().setVisible(true);
        });

        JPanel volverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volverPanel.setOpaque(false);
        volverPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        volverPanel.add(btnVolver);
        mainPanel.add(volverPanel, BorderLayout.NORTH);

        // Datos de prueba
        pedidosModel.addRow(new Object[]{"001", "Mesa 1", "1 Ramen, 1 Té Verde", "Pendiente"});
        pedidosModel.addRow(new Object[]{"002", "Mesa 4", "2 Sushi Roll, 1 Agua", "En preparación"});
        pedidosModel.addRow(new Object[]{"003", "Mesa 2", "1 Yakimeshi, 1 Brownie", "Entregado"});
    }
}
